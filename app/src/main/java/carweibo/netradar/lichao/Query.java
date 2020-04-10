package carweibo.netradar.lichao;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;








import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Query extends Activity implements OnItemLongClickListener, OnCancelListener, OnClickListener {
	
	ListView carList;
	ProgressDialog pd;
	TextView hint_view;
	QueryInfo thread;
	boolean cancel_flag=false;
	String delete_num=null;
	Context context;
	LinearLayout carManagerLayout;
	String[] items;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.query_layout);
		carList=(ListView)findViewById(R.id.carinfo_listview);
		carList.setOnItemLongClickListener(this);
		hint_view=(TextView)findViewById(R.id.shuoming1);
		carManagerLayout=(LinearLayout)findViewById(R.id.query_car_manager_layout);
		context=this;
		RefreshList();
	}
	

	public void onNewCar(View v){
		
		Intent i=new Intent();
		i.setClass(Query.this, AddCar.class);
		
		this.startActivityForResult(i,0);
		
		this.getParent().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
		
		
	}

	Bundle cache_bd=null;
	String src=null;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode==0)//增加
		{
			if(resultCode==0)
			{
				Bundle bd=data.getBundleExtra("data");
				Intent i=new Intent();
				i.setClass(Query.this, VerifyDialog.class);
				i.putExtra("carinfo", bd);
				i.putExtra("type", "add");
				cache_bd=bd;
				src="add";
				
				this.startActivityForResult(i, 4);
				//startQuery(bd.getString("num"),bd.getString("owner_fdjh"),bd.getString("type"),bd.getString("querymethod"),"add");
				
			}
			
		}
		if(requestCode==1)//删除
		{
			if(resultCode==0)
			{
				DBUility dbUility=(DBUility)getApplicationContext();
		    	
		    	DBhelper db=dbUility.getDB();
		    	
		    	db.delete(dbUility.CAR_TABLE,"num", delete_num);
				RefreshList();
			}
		}
		if(requestCode==2)//重新添加
		{
			if(resultCode==0)
			{
				onNewCar(null);
			}
		}
		if(requestCode==3)//版本升级
		{
			if(resultCode==0)
			{
				new VersionCheck(this).update(true);
				
			}
			else
				this.getParent().finish();
		}
		if(requestCode==4)//验证码
		{
			if(resultCode==0)
			{
				//Log.d("lichao","Query verify code is "+data.getStringExtra("verify_code"));
				Bundle bd;
				bd=data.getBundleExtra("carinfo");
				startQuery(bd.getString("num"),bd.getString("owner_fdjh"),bd.getString("type"),bd.getString("querymethod"),data.getStringExtra("type"),data.getStringExtra("verify_code"),data.getStringExtra("jsession_id"));
				
			}
			
		}
		if(requestCode==5)//重新获取验证码
		{
			if(resultCode==0)
			{
			//	Bundle bd=data.getBundleExtra("data");
				Intent i=new Intent();
				i.setClass(Query.this, VerifyDialog.class);
				i.putExtra("carinfo", cache_bd);
				i.putExtra("type", src);
				this.startActivityForResult(i, 4);
			}
			
		}
	}
	
	private class QueryInfo extends AsyncTask<String,Void,JSONObject> {

		String num;
		String owner_fdjh;
		String type;
		String querymethod;
		String src;
		String code;
		String jsession_id;
		int ret;
		

		@Override
		protected void onPostExecute(JSONObject result) {
			// TODO Auto-generated method stub
			
			pd.dismiss();
			
			postProcessQuery(src,ret,num,owner_fdjh,type,querymethod,result);
		
			super.onPostExecute(result);
		}

		@Override
		protected JSONObject doInBackground(String... params) {
			// TODO Auto-generated method stub
			
		//	Log.d("lichao","params 4 is "+params[4]+" 5 is "+params[6]);
			num=params[0];
			owner_fdjh=params[1];
			type=params[2];
			querymethod=params[3];
			src=params[4];
			code=params[5];
			jsession_id=params[6];
				if(!IsNetworkOk())
				{
					
					ret=206;
					return null;
				}
			String url = null;
			if(querymethod.equals("owner"))
			try 
			{
				url="http://117.36.53.122:9081/wfcx/query.do?actiontype=vioSurveil&hpzl="+type+
					"&hphm="+num.substring(1)+
					"&tj=SYR&tj_val="+URLEncoder.encode(owner_fdjh,"UTF-8")+
					"&jdccode="+code;
				
			} catch (UnsupportedEncodingException e1) 
			{
				ret=208;
				return null;
			}
			else
				url="http://117.36.53.122:9081/wfcx/query.do?actiontype=vioSurveil&hpzl="+type+
				"&hphm="+num.substring(1)+
				"&tj=FDJH&tj_val="+owner_fdjh+
				"&jdccode="+code;
			
			//Log.d("lichao","query url is: "+url);
			Document doc=null;	
			try {
				doc = Jsoup.connect(url).cookie("JSESSIONID",jsession_id.substring(jsession_id.indexOf("=")+1)).timeout(6000).get();
				
				
				
				if(doc==null)
				{
					ret=207;
					return null;
				}
				
			} catch (IOException e) {
				ret=207;
				return null;
			}
		//	Log.d("lichao","doc is "+doc.toString());
			CarInfo1 car=PullCarInfo(doc);
			
				if(car.Num=="wrong")
		        {
		        	ret=201;
		        	return null;
		        }
		        else if(car.Owner=="wrong")
		        {
		        	ret=203;
		        	return null;
		        	
		        }
		        else if(car.Owner=="wrongf")
		        {
		        	ret=202;
		        	return null;
		        	
		        }
		        else if(car.Owner=="wrong_code")
		        {
		        	ret=204;
		        	return null;
		        	
		        }
		        else if(car.Owner=="error_jj")
		        {
		        	ret=205;
		        	return null;
					
		        }
				
			
			List<WeiZhangInfo> weizhang_list=PullWeiZhangInfo(num.substring(1),doc);
			
			JSONObject ret_json=new JSONObject();
			
			Gson gson=new Gson();
			
		//	Log.d("lichao","gson is "+gson.toJson(car));
		//	Log.d("lichao","gson list  is "+gson.toJson(weizhang_list));
			
			
		//	JSONArray weizhang_ja=new JSONArray(weizhang_list);
			
			
			try {
				
			//	JSONObject car_js=new JSONObject();
				
				
				ret_json.put("car", new JSONObject(gson.toJson(car)));
				ret_json.put("weiZhang", new JSONArray(gson.toJson(weizhang_list)));
			} catch (JSONException e) {
				ret=208;
				
				return null;
			}
			
			ret=200;
			return ret_json;
			
			
			
			
				
		
			
				
			
		/*	HttpPost httpPost=new HttpPost(url);  
            httpPost.setHeader("Cookie", jsession_id);
            HttpClient client = null;
            try {
				
				
			    
			    
				client=new DefaultHttpClient();
				client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
				client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
				//client.getParams().setParameter(HttpRequestParams , 20000);
				DefaultHttpRequestRetryHandler kk=new DefaultHttpRequestRetryHandler(0,false);
				((AbstractHttpClient)client).setHttpRequestRetryHandler(kk);
			
				HttpResponse response=client.execute(httpPost);
				
				 ret=response.getStatusLine().getStatusCode();
				 
				 Log.d("lichao","query ret is "+ret);
				 if(ret!=200) return null;
				 
				 HttpEntity entity=response.getEntity();  
				 
				 
				// Log.d("lichao","query entity is: "+EntityUtils.toString(entity, HTTP.UTF_8));
				 
				 if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
                 return null;
			} catch (UnsupportedEncodingException e) {
				ret=208;
				
				return null;
				// TODO Auto-generated catch block
				
			} catch (ClientProtocolException e) {
				ret=207;
				if(client!=null&&client.getConnectionManager()!=null)
					client.getConnectionManager().shutdown();
				return null;
				// TODO Auto-generated catch block
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				ret=207;
				if(client!=null&&client.getConnectionManager()!=null)
					client.getConnectionManager().shutdown();
				
				return null;
			}
			//ret=0;
			//return new JSONObject("{\"car\":{\"brand\":\"波罗牌\",\"color\":\"白\",\"destroyDate\":\"2099-12-31\",\"model\":\"SVW7164CSi\",\"num\":\"陕ALM282\",\"numCata\":\"小型汽车\",\"owner\":\"李超\",\"status\":\" 违法未处理\",\"validDate\":\"2014-10-31\"},\"weiZhang\":[{\"addr\":\"西二环白家口盘道至龙海铁路线\",\"money\":null,\"score\":null,\"src\":\"西安市公安局交通警察支队莲湖大队执勤四中队（红庙坡中队）\",\"status\":\"未交款\",\"time\":\"2013-01-26 09:18:32.0\",\"type\":\"8363超速50％以下\"},{\"addr\":\"东十一道巷口\",\"money\":null,\"score\":null,\"src\":\"西安市公安局交通警察支队碑林大队\",\"status\":\"未交款\",\"time\":\"2013-02-03 10:25:32.0\",\"type\":\"8001【电子警察】机动车违反标志标线指示\"},{\"addr\":\"西二环白家口盘道至龙海铁路线\",\"money\":null,\"score\":null,\"src\":\"西安市公安局交通警察支队莲湖大队执勤四中队（红庙坡中队）\",\"status\":\"未交款\",\"time\":\"2013-01-26 09:18:32.0\",\"type\":\"8363超速50％以下\"},{\"addr\":\"东十一道巷口\",\"money\":null,\"score\":null,\"src\":\"西安市公安局交通警察支队碑林大队\",\"status\":\"未交款\",\"time\":\"2013-02-03 10:25:32.0\",\"type\":\"8001【电子警察】机动车违反标志标线指示\"},{\"addr\":\"西二环白家口盘道至龙海铁路线\",\"money\":null,\"score\":null,\"src\":\"西安市公安局交通警察支队莲湖大队执勤四中队（红庙坡中队）\",\"status\":\"未交款\",\"time\":\"2013-01-26 09:18:32.0\",\"type\":\"8363超速50％以下\"},{\"addr\":\"东十一道巷口\",\"money\":null,\"score\":null,\"src\":\"西安市公安局交通警察支队碑林大队\",\"status\":\"未交款\",\"time\":\"2013-02-03 10:25:32.0\",\"type\":\"8001【电子警察】机动车违反标志标线指示\"},{\"addr\":\"西二环白家口盘道至龙海铁路线\",\"money\":null,\"score\":null,\"src\":\"西安市公安局交通警察支队莲湖大队执勤四中队（红庙坡中队）\",\"status\":\"未交款\",\"time\":\"2013-01-26 09:18:32.0\",\"type\":\"8363超速50％以下\"},{\"addr\":\"东十一道巷口\",\"money\":null,\"score\":null,\"src\":\"西安市公安局交通警察支队碑林大队\",\"status\":\"未交款\",\"time\":\"2013-02-03 10:25:32.0\",\"type\":\"8001【电子警察】机动车违反标志标线指示\"},{\"addr\":\"西二环白家口盘道至龙海铁路线\",\"money\":null,\"score\":null,\"src\":\"西安市公安局交通警察支队莲湖大队执勤四中队（红庙坡中队）\",\"status\":\"未交款\",\"time\":\"2013-01-26 09:18:32.0\",\"type\":\"8363超速50％以下\"},{\"addr\":\"东十一道巷口\",\"money\":null,\"score\":null,\"src\":\"西安市公安局交通警察支队碑林大队\",\"status\":\"未交款\",\"time\":\"2013-02-03 10:25:32.0\",\"type\":\"8001【电子警察】机动车违反标志标线指示\"},{\"addr\":\"西二环白家口盘道至龙海铁路线\",\"money\":null,\"score\":null,\"src\":\"西安市公安局交通警察支队莲湖大队执勤四中队（红庙坡中队）\",\"status\":\"未交款\",\"time\":\"2013-01-26 09:18:32.0\",\"type\":\"8363超速50％以下\"},{\"addr\":\"东十一道巷口\",\"money\":null,\"score\":null,\"src\":\"西安市公安局交通警察支队碑林大队\",\"status\":\"未交款\",\"time\":\"2013-02-03 10:25:32.0\",\"type\":\"8001【电子警察】机动车违反标志标线指示\"},{\"addr\":\"长安中路-兴善寺十字\",\"money\":null,\"score\":null,\"src\":\"西安市公安局交通警察支队雁塔大队\",\"status\":\"未交款\",\"time\":\"2013-07-18 10:07:36.0\",\"type\":\"1019机动车违规使用专用车道\"}]}");
			*/
			
		}
		public  CarInfo1 PullCarInfo(Document doc)
		{
			CarInfo1 car=new CarInfo1();
			List<String> list=new ArrayList<String>();
			
			//Log.d("lichao","doc is "+doc.toString());
			if(doc.toString().contains("号牌号码和号牌种类输入错误")) 
			{
				car.Num="wrong";
				return car;
			}
			if(doc.toString().contains("机动车所有人匹配错误")) 
			{
			car.Owner="wrong";
			return car;
			}
			if(doc.toString().contains("发动机号匹配错误")) 
			{
			car.Owner="wrongf";
			return car;
			}
			if(doc.toString().contains("验证码出错")) 
			{
			car.Owner="wrong_code";
			return car;
			}
			
			 Elements elem = doc.select("form[name]").select("table").select("table[style]");
			 
			 if(elem.isEmpty()||elem==null)
			 {
				 car.Owner="error_jj";
				 return car;
			 }
			    Elements elem1=elem.get(0).select("tbody").select("td");
			 
			    
			  
			    for(Element e:elem1)
			    {
			    	if(!e.hasAttr("class"))
			    		{
			    		
			    	
			    		list.add(e.text().substring(1));
			    		}
			    }
			if(list.size()<9) return null;
			car.NumCata=list.get(0);
			car.Num=list.get(1);
			car.Brand=list.get(2);
			car.Model=list.get(3);
			car.Color=list.get(5);
			car.ValidDate=list.get(6);
			car.DestroyDate=list.get(7);
			car.Status=list.get(8);
			car.Owner=list.get(9);
			return car;
		}
		public  List<WeiZhangInfo> PullWeiZhangInfo(String num,Document doc)
		{
			List<WeiZhangInfo> list=new ArrayList<WeiZhangInfo>();
		
			
			
			 Elements elem = doc.select("form[name]").select("table").select("table[style]");
			 
				
			    Elements elem1=elem.get(1).select("tr[style=cursor:hand]");
			    List<HashMap<String, String>> money=null;

			   // System.out.println(elem1.toString());
			    for(Element e:elem1)
			    {
			    	
			    	WeiZhangInfo tmp=new WeiZhangInfo();
			    	tmp.Time=e.select("td").get(2).text();
			    	tmp.Addr=e.select("td").get(3).text();
			    	String tmps=e.select("td").get(4).text();
			    	
			    	byte[] bb=tmps.getBytes();
			    	
			    	int i=0;
			    	String code="";
			    	while(i<tmps.length()&&bb[i]>='0'&&bb[i]<='9')
			    	{
			    		
			    		code=code+tmps.substring(i,i+1);
			    		i++;
			    	}
			    	if(i<tmps.length())
			    		tmp.Type=tmps.substring(i);
			    	else
			    		tmp.Type="未知";
			    /*	Log.d("lichao","tmps is "+tmps);
			    	byte[] bb=tmps.getBytes();
			    	String code=tmps.substring(0,4);
			    	
			    	if(bb[4]>='0'&&bb[4]<='9')
			    	{
			    		code=code+bb[4];
			    		tmp.Type=tmps.substring(5);
			    	}
			    	else
			    	{
			    		tmp.Type=tmps.substring(4);
			    	}*/
			    	
			    	HashMap<String,String> map=getMoneyScoreFromDB(code);
			    	
			    	if(tmp.Type.equals("未知")&&map!=null)
				    	tmp.Type=map.get("type");
			    	tmp.Src=e.select("td").get(5).text();
			    	tmp.Status=e.select("td").get(6).text();
			    	
			    	if(map!=null)
			    	{
			    		
			    		tmp.Money=(String)map.get("money");
			    	   	tmp.Score=map.get("score");
			    	}
			    	else
			    	{
			    		
			    		if(money==null)
			    		{
			    			if(num.startsWith("A"))
			    				money=getMoneyScoreFromUrl(num.substring(1));
			    			else
			    				money=getMoneyScoreFromUrl(num);
			    		}
			    		
			    		if(money!=null)
			    		{
			    			
			    			for(HashMap<String,String> m:money)
			    			{
			    				if(code.equals(m.get("code")))
			    				{
			    					
			    					tmp.Money=m.get("money");
			    					tmp.Score=m.get("score");
			    				
			    			//	inertCodeToDB(code,m.get("money"),m.get("score"),tmp.Type);
			    				break;
			    				}
			    			//		
			    			}
			    		}
			    		else
			    		{
			    			
			    			tmp.Money="无法获取";
			    			tmp.Score="无法获取";
			    		}
			    	}
			    /*	if(timeMoney!=null)
			    	{
				    	for(HashMap<String,String> hm:timeMoney){
				    		if(hm.get("time").equals(tmp.Time)){
				    			tmp.Money=(String)hm.get("money");
				    			tmp.Score=(String)hm.get("score");
				    		}
				    	}
			    	}
			    	else
			    	{
			    		tmp.Money="暂时无法获取";
		    			tmp.Score="暂时无法获取";
			    	}*/
			    	
			    	
			    	list.add(tmp);
			    	
			    	
			    }
			    
			    
			   Log.d("lichao","您总共有"+list.size()+"个违章!");
			 /*  for(WeiZhangInfo i:list){
			   Log.d("lichao",i.Time);
			   Log.d("lichao",i.Addr);
			   Log.d("lichao",i.Type);
			   Log.d("lichao",i.Src);
			   Log.d("lichao",i.Status);
			   Log.d("lichao",i.Money);
			   Log.d("lichao",i.Score);}*/
			return list;
		}
		private HashMap<String, String> getMoneyScoreFromDB(String code) {
			
			HashMap<String,String> map=new HashMap<String,String>();
			
			CodeInfo info=((DBUility)context.getApplicationContext()).code_map.get(Integer.valueOf(code));
		
			if(info!=null)
			{
				map.put("money", info.money+"元");
				map.put("score", info.score+"分");
				map.put("type", info.type);
				return map;
			}
			return null;
		}

		private  List<HashMap<String, String>> getMoneyScoreFromUrl(String num)
		{
			
		    List<HashMap<String,String>> timeMoney=null;
			Document doc;
			try {
				doc = Jsoup.connect("http://117.36.53.122:9082/wfjf/wfjf/wsjf.do?actiontype=wfcx&hpzl=02&hphm="+num).timeout(10000).get();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			 String c=doc.toString();
			
			 if(c.contains("当前车辆没有未处理的违法记录"))
			 {
				// System.out.println(num+"没有违章记录");
				 return null;
			 }
			 if(c.contains("对不起没有查到对应条件的信息"))
			 {
				 //System.out.println(num+"没有此车辆");
				 return null;
			 }
			timeMoney=new ArrayList<HashMap<String,String>>();
			 Elements elem = doc.select("form[id=form1]").select("tr[title]");
			
			 if(elem.isEmpty()) return null;
			 for(Element e:elem){
				 HashMap<String,String> map=new HashMap<String,String>();
				 
				 Elements e1=e.select("td");
				 map.put("code", e1.get(4).text());
				 map.put("score", e1.get(5).text());
				 map.put("money", e1.get(6).text());
				 
				 
				 
				 timeMoney.add(map);
				 
			 }
			
			return timeMoney;
					
		}
		private Bitmap getBitmap(String biturl)
		  {
		    Bitmap bitmap=null;
		    
		    try {
		      URL url=new URL(biturl);
		      URLConnection conn=url.openConnection();
		      conn.setConnectTimeout(5000);
		      conn.setReadTimeout(5000);
		      InputStream in =conn.getInputStream();
		      bitmap=BitmapFactory.decodeStream(new BufferedInputStream(in));
		    
		      
		    } catch (Exception e) {
		     
		      
		    }
		    return bitmap;
		  }
		private String getRequestParam(String num, String owner_fdjh, String type,String querymethod) {
			// TODO Auto-generated method stub
			
			
			try {
				JSONObject js=new JSONObject();
				js.put("num", num);
				js.put("querymethod", querymethod);
				String t_owner=URLEncoder.encode(owner_fdjh, "UTF-8");
				
				js.put("owner_fdjh", t_owner);
				js.put("type", type);
				js.put("version", getVersionName());
				js.put("mac", getLocalMacAddress());
				js.put("imei", getIMEI());
				js.put("grade", "100");
				if(src.equals("add"))
					js.put("exist", "yes");
				else
					js.put("exist", "no");
				return js.toString();
			} catch (JSONException e) {
				Log.d("lichao","get error"+e.toString());
				
			} catch (UnsupportedEncodingException e) {
				Log.d("lichao","get error"+e.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.d("lichao","get error"+e.toString());
			}
			Log.d("lichao","get error");
			return null;
		}
		
	}
	
	private void AddCarToDB(String num,String owner,String type,String fdjh,String querymethod,JSONObject js)
	{
		JSONObject info;
		
		ContentValues cv=new ContentValues();
		cv.put("num", num);
		cv.put("owner", owner);
		cv.put("type", type);
		cv.put("time", getTime());
		cv.put("car", js.toString());
		cv.put("fdjh", fdjh);
		cv.put("querymethod", querymethod);

		DBUility dbUility=(DBUility)getApplicationContext();
		DBhelper db=dbUility.getDB();
		
		db.insert(dbUility.CAR_TABLE,cv);
				
		RefreshList();
	}


	private void RefreshCarDB(String num,String owner,String type,String fdjh,String querymethod,JSONObject js)
	{
		JSONObject info;
		
		ContentValues cv=new ContentValues();
		cv.put("num", num);
		cv.put("owner", owner);
		cv.put("type", type);
		cv.put("time", getTime());
		cv.put("car", js.toString());
		cv.put("fdjh", fdjh);
		cv.put("querymethod", querymethod);

		DBUility dbUility=(DBUility)getApplicationContext();
		DBhelper db=dbUility.getDB();
		
		db.update(dbUility.CAR_TABLE,cv,"num",num);
		
		RefreshList();
	}
    void RefreshList()
    {
    	DBUility dbUility=(DBUility)getApplicationContext();
    	
    	DBhelper db=dbUility.getDB();
    	
    	Cursor c=db.query(dbUility.CAR_TABLE);
    	
    	carList.setAdapter(GetCarInfoAdapter(c));
    	
    	if(c.getCount()==0)
    	{
    		hint_view.setText("车库还空着，点击右上角“添加”按钮增加一辆车吧");
    		carManagerLayout.setVisibility(View.GONE);
    	}
    	else
    	{
    		hint_view.setText("车辆列表：");
    		carManagerLayout.setVisibility(View.VISIBLE);
    	}
    		
    	
    }
    
    private MySimpleAdapter  GetCarInfoAdapter(Cursor c)
    {
    	
    	List<Map<String,String>> list=new ArrayList<Map<String,String>>();
    	while(c.moveToNext())
    	{
    		Map<String,String> map=new HashMap<String,String>();

				map.put("num", c.getString(c.getColumnIndex("num")));
	    		map.put("owner", c.getString(c.getColumnIndex("owner")));

    	 

			list.add(map);
		}
    	return new MySimpleAdapter(this,list,R.layout.car_item_layout,
				new String[]{"num","owner"},
				new int[]{R.id.car_num,R.id.car_owner1});
		
		//return null;
    	
    }


	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		
		
		
		Intent i=new Intent();
	
		ViewCache vc=(ViewCache) arg1.getTag();
		delete_num=vc.getNum().getText().toString();
		
		i.putExtra("text", "提示\n\n"+"确定删除\""+delete_num+"\"吗？");
		i.putExtra("ok", "确定");
		i.putExtra("cancel", "取消");
		i.setClass(Query.this, Dialog.class);
		this.startActivityForResult(i,1);
		
		return false;
	}

	
	public void onClickItem(View v)
	{
		ViewCache vc=(ViewCache) v.getTag();
		String num=vc.getNum().getText().toString();
		DBUility dbUility=(DBUility)getApplicationContext();
    	
    	DBhelper db=dbUility.getDB();
		Cursor c=db.queryItem(dbUility.CAR_TABLE,"num", num);
		if(!c.moveToFirst()) return;
	
		String querymethod=c.getString(c.getColumnIndex("querymethod"));
		
		Bundle bd=new Bundle();
		
		bd.putString("num", num);
		if(querymethod.equals("owner"))
			bd.putString("owner_fdjh", vc.getOwner().getText().toString());
		else
			bd.putString("owner_fdjh", c.getString(c.getColumnIndex("fdjh")));
		bd.putString("type", c.getString(c.getColumnIndex("type")));
		bd.putString("querymethod", querymethod);
		
		Intent i=new Intent();
		i.setClass(Query.this, VerifyDialog.class);
		i.putExtra("carinfo", bd);
		i.putExtra("type", "click");
		
		cache_bd=bd;
		src="click";
		this.startActivityForResult(i, 4);
		
	/*	if(querymethod.equals("owner"))
			startQuery(num,vc.getOwner().getText().toString(),c.getString(c.getColumnIndex("type")),querymethod,"click");
		else
			startQuery(num,c.getString(c.getColumnIndex("fdjh")),c.getString(c.getColumnIndex("type")),querymethod,"click");
		*/
		
		
	//pd=ProgressDialog.show(this, "", "正在请求数据，请稍候...", false);
		

	}
	public void startQuery(String num,String owner_fdjh,String type,String querymethod,String src, String code, String jsession_id)
	{


		pd=new ProgressDialog(this);
		pd.setCancelable(true);
		pd.setMessage("正在努力获取数据，请稍候～");
		pd.setCanceledOnTouchOutside(false);
		pd.setIndeterminateDrawable(this.getResources().getDrawable(R.drawable.progress));
		pd.setOnCancelListener(this);
		thread=new QueryInfo();
		thread.execute(num,owner_fdjh,type,querymethod,src,code,jsession_id);
		pd.show();
	
	}

	private void startDetailAct(String num,JSONObject js)
	{
		//if(pd.isShowing())
		//	pd.dismiss();
		
		Intent i=new Intent();
		DBUility dbUility=(DBUility)getApplicationContext();
    	
    	DBhelper db=dbUility.getDB();
    	
    	Cursor c=db.queryItem(dbUility.CAR_TABLE, "num", num);
    	
    
		if(!c.moveToFirst()) 
		{
			Toast.makeText(this, "数据获取失败，抱歉～", Toast.LENGTH_LONG).show();
			return;
		}	
    	
		i.putExtra("data", js.toString());
		i.putExtra("num", num);
		i.putExtra("time", c.getString(c.getColumnIndex("time")));
		i.setClass(Query.this, Detail.class);
		
		startActivity(i);
		this.getParent().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
			
	}


	private void postProcessQuery(String src,int ret,String num,String owner_fdjh,String type,String querymethod,JSONObject js)
	{
		
		switch(ret)
		{
		case 200:
			if(src.equals("add")) 
			{
				if(querymethod.equals("owner"))
					AddCarToDB(num,owner_fdjh,type,"--",querymethod,js);
					
				else
				{
					String owner = null;
					try {
						
						JSONObject js1 = js.getJSONObject("car");
						Log.d("lichao",js1.toString());
						owner=js1.getString("owner");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						return;
					}
					AddCarToDB(num,owner,type,owner_fdjh,querymethod,js);
					
				}
				
			}
			else
			{
				if(querymethod.equals("owner"))
					RefreshCarDB(num,owner_fdjh,type,"--",querymethod,js);
					
				else
				{
					String owner = null;
					try {
						
						JSONObject js1 = js.getJSONObject("car");
						
						owner=js1.getString("owner");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						return;
					}
					RefreshCarDB(num,owner,type,owner_fdjh,querymethod,js);
					
				}
			}
			Toast.makeText(this, "查询成功！", Toast.LENGTH_LONG).show();
			startDetailAct(num,js);
			break;
		case 201:
			if(src.equals("add"))
			{
				Intent i=new Intent();
			
				
				i.putExtra("text", "提示\n\n"+"车牌号或者车辆类型有误！");
				i.putExtra("ok", "重新添加");
				i.putExtra("cancel", "取消");
				i.setClass(Query.this, Dialog.class);
				this.startActivityForResult(i,2);
			}
			else
			{
				Toast.makeText(this, "获取数据出了点问题，请重试～", Toast.LENGTH_LONG).show();
			}
			break;
		case 202:
			if(src.equals("add"))
			{
				Intent i=new Intent();
			
				
				i.putExtra("text", "提示\n\n"+"车辆与发动机号码不匹配！");
				i.putExtra("ok", "重新添加");
				i.putExtra("cancel", "取消");
				i.setClass(Query.this, Dialog.class);
				this.startActivityForResult(i,2);
			}
			else
			{
				Toast.makeText(this, "获取数据出了点问题，请重试～", Toast.LENGTH_LONG).show();
			}
			break;
		/*case 202:
			if(src.equals("add"))
			{
				Intent i=new Intent();
			
				
				i.putExtra("text", "提示\n\n"+"官网服务器异常，请过段时间重试一下吧～");
				i.putExtra("ok", "重新添加");
				i.putExtra("cancel", "取消");
				i.setClass(Query.this, Dialog.class);
				this.startActivityForResult(i,2);
					}
			else
			{
				startDetailAct1(num);
			}
			
			break;*/
		case 203:
			if(src.equals("add"))
			{
				Intent i=new Intent();
			
				
				i.putExtra("text", "提示\n\n"+"车牌号与所有人不匹配！");
				i.putExtra("ok", "重新添加");
				i.putExtra("cancel", "取消");
				i.setClass(Query.this, Dialog.class);
				this.startActivityForResult(i,2);
			}
			else
			{
				Toast.makeText(this, "获取数据出了点问题，请重试～", Toast.LENGTH_LONG).show();
			}
			break;
		case 204:
			//onVersionUpdate();
			//if(src.equals("add"))
			{
				Intent i=new Intent();
				/*Bundle bd=new Bundle();
				
				bd.putString("num", num);
				bd.putString("owner_fdjh", owner_fdjh);
				bd.putString("type", type);
				bd.putString("querymethod", querymethod);*/
				
				i.putExtra("text", "提示\n\n"+"验证码错误");
				i.putExtra("ok", "重新获取");
				i.putExtra("cancel", "取消");
				i.setClass(Query.this, Dialog.class);
				this.startActivityForResult(i,5);
			}
			break;
		case 205:
			
		
			Toast.makeText(this, "交警官网挂了，稍候再试～", Toast.LENGTH_LONG).show();
		
			break;
		case 206:
			if(src.equals("add"))
			{
				Toast.makeText(this, "网络连接有问题，请检查网络配置～", Toast.LENGTH_LONG).show();
			}
			else
			{
				Toast.makeText(this, "网络连接有问题，将显示上次查询结果～", Toast.LENGTH_LONG).show();
				startDetailAct1(num);
			}
			
			break;
		case 207:
			if(src.equals("add"))
			{
				Toast.makeText(this, "无法连接服务器，请检查网络配置或者稍后再试～", Toast.LENGTH_LONG).show();
			}
			else
			{
				Toast.makeText(this, "网络连接有问题，将显示上次查询结果～", Toast.LENGTH_LONG).show();
				startDetailAct1(num);
			}
			
			break;
		case 208:
			if(src.equals("add"))
			{
				Toast.makeText(this, "请检查一下车牌号和所有人信息是否正确，再重试～", Toast.LENGTH_LONG).show();
			}
			else
			{
				Toast.makeText(this, "网络连接有问题，将显示上次查询结果～", Toast.LENGTH_LONG).show();
				startDetailAct1(num);
			}
			
			break;
		default:
			Toast.makeText(this, "获取数据出现问题，请重试～", Toast.LENGTH_LONG).show();
		}
	}
	private void startDetailAct1(String num)
	{
		
		DBUility dbUility=(DBUility)getApplicationContext();
    	
    	DBhelper db=dbUility.getDB();
		Cursor c=db.queryItem(dbUility.CAR_TABLE,"num", num);
		if(!c.moveToFirst()) 
		{
			Toast.makeText(this, "数据获取失败，抱歉～", Toast.LENGTH_LONG).show();
			return;
		}
		
		try {
			JSONObject js=new JSONObject(c.getString(c.getColumnIndex("car")));
			
			startDetailAct(num,js);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void onVersionUpdate()
	{
		Intent i=new Intent();
		
				
		i.putExtra("text", "提示\n\n"+"您当前的版本太老，必须进行升级才能继续使用，现在升级吗？");
		i.putExtra("ok", "升级");
		i.putExtra("cancel", "退出");
		i.setClass(Query.this, Dialog.class);
		this.startActivityForResult(i,3);
	}
	boolean IsNetworkOk(){
		NetworkInfo info=((ConnectivityManager)getSystemService(this.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		if(info==null||!info.isAvailable())
			return false;
		return true;
	}
	
	 private String getVersionName() throws Exception
	 {
	
		 PackageManager packageManager = getPackageManager();
	           
		 PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);
		 String version = packInfo.versionName;
		 return version;
	   }
	 public String getLocalMacAddress() {   
	        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);   
	       
	        WifiInfo info = wifi.getConnectionInfo();   
	        String mac=info.getMacAddress();
	        
	        if(mac==null) return "noMac";
	        return mac;   
	    } 
	 private String getIMEI()
	 {
		 TelephonyManager telephonyManager= (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		 String imei=telephonyManager.getDeviceId();
		 if(imei==null) return "noImei";
		 return imei;
		 
	 }
	@Override
	public void onCancel(DialogInterface arg0) {
		// TODO Auto-generated method stub
		thread.cancel(true);
		cancel_flag=true;
	}
	public String getTime(){
		 SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yyyy年MM月dd日   HH:mm:ss     ");
		 Date   curDate   =   new   Date(System.currentTimeMillis());//获取当前时间    
		 return formatter.format(curDate);
	}
	
	public void onCarInfo(View v)
	{
		Intent i=new Intent();
		i.putExtra("data", "");
		i.setClass(Query.this, CarInfo.class);
		
		startActivity(i);
		this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
	}
	public void onHistoryQuery(View v)
	{
		DBUility dbUility=(DBUility)getApplicationContext();
    	
    	DBhelper db=dbUility.getDB();
    	
    	Cursor c=db.query(dbUility.CAR_TABLE);//, "num", this.getIntent().getStringExtra("data"));
		ArrayList<String> list=getCarList(c);
		if(list.size()<=0) 
			{
			Toast.makeText(this, "车库还空着呢，先添加车辆吧～", Toast.LENGTH_LONG).show();
				return;
			}
		

		if(list.size()>1)
		{
			items=new String[list.size()];
			for(int ii=0;ii<list.size();ii++)
			{
				items[ii]=list.get(ii);
			}
			new AlertDialog.Builder(Query.this).setTitle("选择车辆").setSingleChoiceItems(items, 0,this).show();//显示对话框
		//	new AlertDialog.Builder(this).setTitle("选择操作").setItems(option,new DialogInterface.OnClickListener() {

		}
		else
		{
			Log.d("lichao","query car is "+list.get(0));
			startDetailAct1(list.get(0));
		}
		
	}
	private ArrayList<String> getCarList(Cursor c)
	{

    	ArrayList<String> list=new ArrayList<String>();
    	while(c.moveToNext())
    	{
    		list.add(c.getString(c.getColumnIndex("num")));
		}
    	
		return list;
	}


	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		arg0.dismiss();
		startDetailAct1(items[arg1]);
		
	}
}
