package carweibo.netradar.lichao;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import android.widget.TextView;


public class Detail extends Activity implements OnClickListener {

	ListView lv;
	int total_weizhang=0;
	int total_money=0;
	int total_score=0;
	String num=null;
	JSONObject js1;
	String qtime;
	SimpleAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.detail_layout);
		lv=(ListView)findViewById(R.id.detail_listview);
		
		TextView tv=(TextView)findViewById(R.id.detail_num);
		TextView sum_tv=(TextView)findViewById(R.id.detail_car);
		num=this.getIntent().getStringExtra("num");
		tv.setText(num);
	
		qtime=this.getIntent().getStringExtra("time");
		
		try {
			js1=new JSONObject(this.getIntent().getStringExtra("data"));
		
			adapter=getAdapter(js1);
			lv.setAdapter(adapter);
			
			if(total_money==-1)
			{
				
				sum_tv.setText("违章统计："+total_weizhang+"个违章  罚款和扣分情况未知\n刷新时间："+qtime);
			}
			else
			{
				
				sum_tv.setText("违章统计："+total_weizhang+"个违章  罚款"+total_money+"元  扣"+total_score+"分\n刷新时间："+this.getIntent().getStringExtra("time"));
			}
			if(total_weizhang==0)
			{
				RelativeLayout rl=(RelativeLayout)findViewById(R.id.noweizhang_layout);
				rl.setVisibility(0);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private SimpleAdapter getAdapter(JSONObject js)
	{
		List<Map<String,String>> list=new ArrayList<Map<String,String>>();
		//Log.d("lichao","Detail js is "+js.toString());
		try {
			JSONArray ja=(JSONArray)js.getJSONArray("weiZhang");
			JSONObject j;
			int i=0;
		
			for(i=0;i<ja.length();i++)
			{
				Map<String,String> map=new HashMap<String,String>();
				j=ja.getJSONObject(i);
				map.put("time", j.getString("Time"));
				map.put("index", String.valueOf(i+1));
				map.put("addr", j.getString("Addr"));
				map.put("type", j.getString("Type"));
				map.put("src", j.getString("Src"));
				
				map.put("money", j.getString("Money"));
				map.put("score", j.getString("Score"));
				total_weizhang++;
				
				//Log.d("lichao",j.toString());
				if(!j.getString("Money").contains("无法获取"))
				{
					total_money=total_money+Integer.valueOf(j.getString("Money").substring(0, j.getString("Money").indexOf("元")));
				
					total_score=total_score+Integer.valueOf(j.getString("Score").substring(0, j.getString("Score").indexOf("分")));
				}
				
				list.add(map);
			}
			
		} catch (JSONException e) {
			Log.d("lichao","Detail jsont error is: "+e.toString());
			return null;
		}
		
		return new SimpleAdapter(this,list,R.layout.detai_item_layout,
				new String[]{"time","addr","type","money","score","src","index"},
				new int[]{R.id.w_timeC,R.id.w_addrC,R.id.w_typeC,R.id.w_moneyC,R.id.w_scoreC,R.id.w_srcC,R.id.w_index});
	}
	public void onCancelDetail(View v)
	{
		this.finish();
		this.overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		this.finish();
		this.overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
		super.onBackPressed();
	}
	
	/**
	 * @param v
	 */
	public void onShareDetail(View v)
	{
		if(getSDDir()==null)
		{
			Toast.makeText(this, "未检测到SD卡，无法完成指定操作", Toast.LENGTH_SHORT).show();
			return;
		}
		shootDetailWeizhangPic();
		
		Intent i=new Intent();
		i.setClass(Detail.this, ShareDialog1.class);
		i.putExtra("dir", getSDDir()+"/weizhangquery/tmp/"+"tmp_detail.jpg");
		i.putExtra("type", 0);
		startActivity(i);
		
	//	RelativeLayout rl=(RelativeLayout)findViewById(R.id.sharetofriend);
		
	//	rl.setVisibility(View.VISIBLE);
	/*	RelativeLayout rl=(RelativeLayout)findViewById(R.id.detailtitlelayout);*/
			//LayoutInflater inflater = this.getLayoutInflater();
        //View rowView = inflater.inflate(R.layout.detai_item_layout, null);
	/*	if(getStr().equals("noWeiZhang"))
		{
			ScreenShoot.savePic(ScreenShoot.takeScreenShot(this),getSDDir()+"/weizhangquery/pic",getSDDir()+"/weizhangquery/pic/"+"tmp.jpg",30);
			
			Intent i=new Intent();
			
			i.putExtra("type", "weizhang");
			i.putExtra("dir", getSDDir()+"/weizhangquery/pic/"+"tmp.jpg");
			i.setClass(Detail.this, ShareDialog.class);
			
			startActivity(i);
			return;
			
		}
		
		LayoutInflater inflater = this.getLayoutInflater();
		View rowView=inflater.inflate( R.layout.text_layout, null);
		TextView tv=(TextView)rowView.findViewById(R.id.text);
		tv.setWidth(getWindowManager().getDefaultDisplay().getWidth()-8);
	

		tv.setText(getStr());
		
		int spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);  
    
      
        rowView.measure(spec, spec);  
        rowView.layout(0, 0, rowView.getMeasuredWidth(), rowView.getMeasuredHeight()); 
        
       
        Bitmap b = Bitmap.createBitmap(rowView.getWidth(), rowView.getHeight(),  
                Bitmap.Config.ARGB_4444);  

        ByteArrayOutputStream   baos = new ByteArrayOutputStream();  

        b.compress(Bitmap.CompressFormat.JPEG, 30, baos);
		final Canvas canvas = new Canvas(b);
		
		canvas.translate(-rowView.getScrollX(), -rowView.getScrollY());  
		rowView.draw(canvas);
		rowView.setDrawingCacheEnabled(true); 
		
        Bitmap cacheBmp = rowView.getDrawingCache();  
       // Bitmap viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_4444, true);  Log.d("lichao","i= 1");
        rowView.destroyDrawingCache();  

        
    

	//	canvas.skew(0, -vd.getHeight());
		
		View vv=lv.getChildAt(0);
		
		TextView tt=(TextView)vv.findViewById(R.id.w_addrC);
		
		tt.setHeight(100);
		tt.setWidth(200);
		tt.setText("hkaldjfkadjfkadsjfa;kdfjaskdfjsadkfj");
       
		 Bitmap b = Bitmap.createBitmap(vv.getWidth(), vv.getHeight(),  
	                Bitmap.Config.ARGB_8888);  
		 
		 final Canvas canvas = new Canvas(b);
		 vv.draw(canvas);
		ScreenShoot.savePic(b,getSDDir()+"/weizhangquery/pic",getSDDir()+"/weizhangquery/pic/"+"tmp.jpg",30);
		
		Intent i=new Intent();
		
		i.putExtra("type", "weizhang");
		i.putExtra("dir", getSDDir()+"/weizhangquery/pic/"+"tmp.jpg");
		i.setClass(Detail.this, ShareDialog.class);
		
		startActivity(i);*/
		//this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
	
		/*Intent i=new Intent();
		
		i.putExtra("data", num);
		i.setClass(Detail.this, CarInfo.class);
		
		startActivity(i);
		this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
	*/}

	private void shootDetailWeizhangPic() {
		View summory=(View)findViewById(R.id.summory);
		Paint paint = new Paint();  
		int itemHeiht=0;
		
		List<Bitmap> bmps=new ArrayList<Bitmap>();
		 for (int i = 0; i < lv.getCount(); i++) {  
			  
             View childView      = adapter.getView(i, null, lv);              
         //    Drawable defaultDrawable = getResources().getDrawable(R.drawable.ic_launcher);                          
             childView.measure(MeasureSpec.makeMeasureSpec(lv.getWidth(), MeasureSpec.EXACTLY),   
                     MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));  
             itemHeiht = itemHeiht+childView.getMeasuredHeight();  
             childView.layout(0, 0, childView.getMeasuredWidth(), childView.getMeasuredHeight());  
             childView.setDrawingCacheEnabled(true);  
             childView.buildDrawingCache();  
             bmps.add(childView.getDrawingCache());  
               
         }  

		 Bitmap b = Bitmap.createBitmap(summory.getWidth(), summory.getHeight()+itemHeiht,  
	                Bitmap.Config.ARGB_4444);  
			Canvas canvas = new Canvas(b);
	
			canvas.drawColor(Color.parseColor("#efefef"));
		summory.setDrawingCacheEnabled(true);
		
		canvas.drawBitmap(summory.getDrawingCache(),0,0,paint);
		
		
		int startY=summory.getHeight();
		for(int j=0;j<lv.getCount();j++)
		{
			canvas.drawBitmap(bmps.get(j),0,startY,paint);
			startY=startY+bmps.get(j).getHeight();
		}
		ScreenShoot.savePic(b,getSDDir()+"/weizhangquery/tmp",getSDDir()+"/weizhangquery/tmp/"+"tmp_detail.jpg",50);
		
		
	}

	private static String getSDDir()
	{
		 if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
    	 {  
    		 File sdCard=Environment.getExternalStorageDirectory();
    		 
    		
    		 StatFs statfs = new StatFs(sdCard.getPath()); 
    		 long totalBlocks = statfs.getAvailableBlocks();
    		 long blocSize = statfs.getBlockSize();
    		 if((totalBlocks*blocSize)>1000000)
    			 return sdCard.getPath();
    		 else
    			 return null;
    		 
    				 
    	 }
		return null;
	}
	
	public void onSelCar(View v)
	{
		final String[] items = {"java", ".net", "php"};
		new AlertDialog.Builder(Detail.this).setTitle("选择语言")
		.setSingleChoiceItems(items, 1,this).show();//显示对话框

	}

	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		
		
	}
	private String getStr()
	{
		List<Map<String,String>> list=getList(js1);
		if(list==null) return null;
		int i=1;
		String str=new String();
		
		str=str+"\n查询工具：《西安违章速查》 Android版\n";
		str=str+"查询车辆："+num+"\n"+"查询时间："+qtime+"\n查询明细：\n";
		
			
		for(Map<String,String> m:list)
		{
			str=str+"\n"+new String(String.valueOf(i))+"：\n";
			str=str+"时间："+m.get("time")+"\n";
			str=str+"地点："+m.get("addr")+"\n";
			str=str+"行为："+m.get("type")+"\n";
			
			str=str+"罚款："+m.get("money")+"\n";
			str=str+"扣分："+m.get("score")+"\n";
			str=str+"来源："+m.get("src")+"\n";
					
			i++;
		}
		if(i==1) return "noWeiZhang";
		return str;
	}
	private List<Map<String,String>> getList(JSONObject js)
	{
		List<Map<String,String>> list=new ArrayList<Map<String,String>>();
		
		try {
			JSONArray ja=(JSONArray)js.getJSONArray("weiZhang");
			JSONObject j;
			int i=0;
		
			for(i=0;i<ja.length();i++)
			{
				Map<String,String> map=new HashMap<String,String>();
				j=ja.getJSONObject(i);
				map.put("time", j.getString("time"));
			
				map.put("addr", j.getString("addr"));
				map.put("type", j.getString("type"));
				map.put("src", j.getString("src"));
				
				map.put("money", j.getString("money"));
				map.put("score", j.getString("score"));
		
				
				list.add(map);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return null;
		}
		
		return list;
	}

}
