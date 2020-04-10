package carweibo.netradar.lichao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import carweibo.netradar.lichao.MyListView.OnRefreshListener;




import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CarCircle extends Activity implements OnRefreshListener, OnItemClickListener, OnItemLongClickListener {

	
	String server_url;//=((DBUility)context.getApplicationContext()).server+"/weibo_pic/";
	long readed_new_id=-1;
	long readed_more_id=-1;
	private static int MAX_RECORD_RETURN=25;
	private static int GET_NEWWEIBO_TYPE_WITH_TOP=0;
	private static int GET_NEWWEIBO_TYPE_WITHOUT_TOP=1;

	BroadcastReceiver bdReceiver;
	private IntentFilter ifilter;
	
	RelativeLayout failure_hint_layout;
	RelativeLayout unsend_hint_layout;
	TextView failure_hint_text;
	TextView unsend_hint_text;
	
	MyListView weibo_listview;
	String nickname_cur;
	TextView nickname;
	Context context;
	List<ImageAndText> itemList;
	List<ImageAndText> top_weibo_list;
	ImageAndTextListAdapter adapter;
	float denisty;
	LinearLayout getting_layout;
	Button getmore_weibo;
	public class GetNewWeibo extends AsyncTask<String, Integer, String> {

		int type;
		@Override
		protected void onPostExecute(String result) {
			
			
			if(result.equals("error_data"))
			{
				Toast.makeText(context, "网络连接有问题，获取数据失败", Toast.LENGTH_SHORT).show();
				weibo_listview.onRefreshComplete();
				getting_layout.setVisibility(View.GONE);
				getmore_weibo.setVisibility(View.VISIBLE);	
				return;
			}
			refreshList(result,type);
			super.onPostExecute(result);
		}

		@Override
		protected String doInBackground(String... arg0) {
			int readed_id=Integer.parseInt(arg0[1]);
			type=Integer.parseInt(arg0[2]);
			return new LoginToServer().GetNewWeibo(context, arg0[0], readed_id,type);
			
		}

	}


	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.weibo_layout);
		failure_hint_layout=(RelativeLayout)findViewById(R.id.failure_hint_layout);
		unsend_hint_layout=(RelativeLayout)findViewById(R.id.unsend_hint_layout);
		failure_hint_text=(TextView)findViewById(R.id.failure_hint_text);
		unsend_hint_text=(TextView)findViewById(R.id.unsend_hint_text);
		getting_layout=(LinearLayout)findViewById(R.id.getting_weibo);
		getmore_weibo=(Button)findViewById(R.id.moreweibo);
	//	failure_hint_layout.getBackground().setAlpha(100);
	//	unsend_hint_layout.getBackground().setAlpha(100);
		
		weibo_listview=(MyListView)findViewById(R.id.weibo_listview);
		weibo_listview.setonRefreshListener(this);
		denisty=this.getResources().getDisplayMetrics().density;
		itemList=new ArrayList<ImageAndText>();
		
		
		weibo_listview.setOnItemClickListener(this);
		weibo_listview.setOnItemLongClickListener(this);
		
		
		denisty=this.getResources().getDisplayMetrics().density;
		top_weibo_list=new ArrayList<ImageAndText>();
		nickname=(TextView)findViewById(R.id.nickname);
		adapter=new ImageAndTextListAdapter(this,itemList,weibo_listview,getNickName());
		weibo_listview.setAdapter(adapter);
		
		
		
		context=this;
		server_url=((DBUility)context.getApplicationContext()).webapps+"/pic";
	//	getWeiboFromDB();
		
		
		bdReceiver=new BroadcastReceiver() {  
  		  
    	    @Override  
    	    public void onReceive(Context context, Intent intent) {  
    	        // TODO Auto-generated method stub   
    	    	
    	    	int type=intent.getIntExtra("type", -1);
    	    	
    	    	switch(type)
    	    	{
    	    	case 0://failure hint
    	    		showUnsendHint(intent.getStringExtra("hint"));
    	    		break;
    	    	default:
    	    		processUI(type,intent.getLongExtra("id", -1));
    	    		break;
    	    	}
    	    	
    	    }  
    	  
    	}; 
    	
    	ifilter= new IntentFilter(); 
    	ifilter.addAction("netradar.bd");
    	registerReceiver(bdReceiver,ifilter);
	}
	
	
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
		unregisterReceiver(bdReceiver);
	}


	protected void processUI(int type,long id) {
		
		if(id==-1) return;
		
		int i=0;
		for(ImageAndText imt:itemList)
		{
			if(imt.id==id)
			{
				break;
			}
			i++;
		}
		if(i==itemList.size()) return;
		switch(type)
		{
		case 1://good
			String tmp=itemList.get(i).good_num;
			
			int num=Integer.parseInt(tmp.substring(0, tmp.indexOf("个")));
			ImageAndText imt=new ImageAndText();
			imt=itemList.get(i);
			imt.good_num=String.valueOf(num+1)+"个赞";
			itemList.set(i, imt);
			adapter.notifyDataSetChanged();
			break;
			
    	case 2://retweet
    		String tmp1=itemList.get(i).retweet_num;
			
			int num1=Integer.parseInt(tmp1.substring(0, tmp1.indexOf("个")));
			ImageAndText imt1=new ImageAndText();
			imt1=itemList.get(i);
			imt1.retweet_num=String.valueOf(num1+1)+"个分享";
			itemList.set(i, imt1);
			adapter.notifyDataSetChanged();
			break;
    	case 3://comment
    		String tmp3=itemList.get(i).comment_num;
			
			int num3=Integer.parseInt(tmp3.substring(0, tmp3.indexOf("个")));
			ImageAndText imt3=new ImageAndText();
			imt3=itemList.get(i);
			imt3.comment_num=String.valueOf(num3+1)+"个评论";
			itemList.set(i, imt3);
			adapter.notifyDataSetChanged();
			break;
		}
		
	}


	public void refreshList(String result,int type)
	{
		
		try {
				
				JSONObject obj= new JSONObject(result);
				
				JSONArray weibo_list=obj.getJSONArray("weibo_list");
				int weibo_num=weibo_list.length();
				if(type==GET_NEWWEIBO_TYPE_WITH_TOP||type==GET_NEWWEIBO_TYPE_WITHOUT_TOP)
				{
					int top_num=0;
					if(type==GET_NEWWEIBO_TYPE_WITH_TOP)
						top_num=updateTopWeibo(weibo_list);
				//	Log.d("lichao","weibo list length is "+weibo_list.length());
					if((weibo_num-top_num)>=MAX_RECORD_RETURN)
					{
						itemList.clear();
						itemList.addAll(top_weibo_list);
						itemList.addAll(getNonTopWeibo(weibo_list));
					}
					else
					{
						int need_reserve=MAX_RECORD_RETURN-(weibo_num-top_weibo_list.size());
						
						List<ImageAndText> reserved=getReserveWeibo(need_reserve);
				//		Log.d("lichao","reseved list length is "+reserved.size());
				//		Log.d("lichao","top list length is "+top_weibo_list.size());
						
						itemList.clear();
						itemList.addAll(top_weibo_list);
						itemList.addAll(getNonTopWeibo(weibo_list));
						itemList.addAll(reserved);
					}
					if(itemList.size()>=1)
					{
						weibo_listview.showFootView(true);
						
						setReadedWeiboId();
					}
					weibo_listview.onRefreshComplete();
					
					showFailureHint("有"+weibo_num+"个新的帖子");
					
					
				}
				else
				{
					itemList.addAll(getNonTopWeibo(weibo_list));
					
					if(weibo_num<MAX_RECORD_RETURN)
						weibo_listview.showFootView(false);
					getting_layout.setVisibility(View.GONE);
					getmore_weibo.setVisibility(View.VISIBLE);	
				}
				
			} catch (JSONException e) {
				Log.d("lichao","refresh json error :"+e.toString());
				weibo_listview.onRefreshComplete();
			}
	//	saveWeiboToDB();
		adapter.notifyDataSetChanged();
		
		
	}

	/*private void saveWeiboToDB() {
		DBUility dbUility=(DBUility)getApplicationContext();
		DBhelper db=dbUility.getDB();
		
		db.deleteall(dbUility.WEIBO_TABLE);
		for(ImageAndText i:itemList)
		{
			ContentValues cv=new ContentValues();
			cv.put("weibo_id", i.id);
			cv.put("user_id", i.user_id);
			cv.put("nickname", i.nickname);
			cv.put("touxiang_url", i.touxiang_url);
			cv.put("time", i.time);
			cv.put("content", i.content);
			cv.put("comment_num", i.comment_num);
			cv.put("good_num", i.good_num);
			cv.put("user_score", i.user_score);
			
			JSONArray ja=new JSONArray();
			try {
				
				for(int j=0;j<i.pic_list_url.size();j++)
				{
					JSONObject jo=new JSONObject();
					jo.put("url", i.pic_list_url.get(j).url);
					jo.put("width", i.pic_list_url.get(j).width);
					jo.put("height", i.pic_list_url.get(j).height);
					ja.put(jo);
				}
				cv.put("pic_list_url", ja.toString());
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			//
			for(int j=0;j<i.pic_list_url.size();j++)
			{
				JSONObject jo = null;
				try {
					
					jo = new JSONObject(i.pic_list_url.get(j).toString());
					Log.d("lichao",i.pic_list_url.get(j).toString());
					Log.d("lichao","hkkkk"+jo.toString());
				} catch (JSONException e) {
					Log.d("lichao","json error");
				}
			
				
				//cv.put("pic_list_url", ja.toString());
			}
			cv.put("grade", i.grade);
			
			
			
			db.insert(dbUility.WEIBO_TABLE, cv);
		}
		
	}*/
	
	/*public void onViewWeibo(View v)	
	{
		WeiboViewCache view=(WeiboViewCache)v.getTag();
		
		
	}
	*/
/*	private void getWeiboFromDB() {
		DBUility dbUility=(DBUility)getApplicationContext();
		DBhelper db=dbUility.getDB();
		
		Cursor c=db.query(dbUility.WEIBO_TABLE);
		
		while(c.moveToNext())
		{
			ImageAndText im=new ImageAndText();
			
			im.id=c.getLong(c.getColumnIndex("weibo_id"));
			im.user_id=c.getInt(c.getColumnIndex("user_id"));
			im.nickname=c.getString(c.getColumnIndex("nickname"));
			im.touxiang_url=c.getString(c.getColumnIndex("touxiang_url"));
			im.time=c.getString(c.getColumnIndex("time"));
			im.content=c.getString(c.getColumnIndex("content"));
			im.comment_num=c.getString(c.getColumnIndex("comment_num"));
			im.good_num=c.getString(c.getColumnIndex("good_num"));
			im.user_score=c.getInt(c.getColumnIndex("user_score"));
			
			String pic=c.getString(c.getColumnIndex("pic_list_url"));
			
			
			JSONArray ja;
			im.pic_list_url=new ArrayList<PicInfo>();
			try {
				ja = new JSONArray(pic);
				for(int i=0;i<ja.length();i++)
				{
					PicInfo info=new PicInfo();
					info.url=ja.getJSONObject(i).getString("url");
					info.width=ja.getJSONObject(i).getInt("width");
					info.height=ja.getJSONObject(i).getInt("height");
					im.pic_list_url.add(info);
				}
			} catch (JSONException e) {
				
			}
			
			im.grade=c.getInt(c.getColumnIndex("grade"));
			
			itemList.add(im);
		}
		
	
		
	}	
*/
	private boolean isTopWeiboOK()
	{
		/*if(top_weibo_list.size()<100) return false;
		
		String timeD=top_weibo_list.get(0).time.substring(0,8);
		
		if(timeD.equals(getTimeD())) return true;*/
		
		return false;
			
	}
	private int updateTopWeibo(JSONArray result) 
	{
		top_weibo_list.clear();
		
		int i=0;
		try {
				for(i=0;i<result.length();i++)
				{
					JSONObject json=result.getJSONObject(i);
					if(json.getInt("grade")==2)
						top_weibo_list.add(new ImageAndText(server_url,json,denisty));
					
				}
			} catch (JSONException e) {
					
				}
		return top_weibo_list.size();
	}
	private List<ImageAndText> getNonTopWeibo(JSONArray result) 
	{
		
		List<ImageAndText> ret_list=new ArrayList<ImageAndText>();
		try {
				for(int i=0;i<result.length();i++)
				{
					JSONObject json=result.getJSONObject(i);
					if(json.getInt("grade")!=2)
						ret_list.add(new ImageAndText(server_url,json,denisty));
					
				}
			} catch (JSONException e) {
					
				}

		return ret_list;
		
	}

	private void setReadedWeiboId() {
	
		int i=0;
		for(ImageAndText imt:itemList)
		{
			if(imt.grade==2)
			{
				i++;
				continue;
			}
			else
				break;
			
		}
		SharedPreferences pre= getSharedPreferences("carweibo_unread_tips", Context.MODE_PRIVATE);
		 Editor editor = pre.edit();
		
		 editor.putLong("readed_weiboid", itemList.get(i).id);
		 editor.commit();
		 Intent intent = new Intent("netradar.bd"); 
		 intent.putExtra("type", 4);
		 
		 sendBroadcast(intent); 

	}


	private List<ImageAndText> getReserveWeibo(int num) 
	{
		
		List<ImageAndText> ret_list=new ArrayList<ImageAndText>();
		int size=itemList.size();
		
		for(int i=0;i<num&&i<size;i++)
		{
			if(itemList.get(i)!=null&&itemList.get(i).grade!=2)
				ret_list.add(itemList.get(i));
		}
		return ret_list;
		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		nickname_cur=getNickName();
		if(nickname_cur.equals("NOLOGIN")||nickname_cur.equals("NOUSER"))
			nickname.setText("游客");
		else
			nickname.setText(nickname_cur);
		adapter.current_nickname=nickname_cur;
		
		if(getLogoutFlag())
		{
			itemList.clear();
			top_weibo_list.clear();
			readed_new_id=-1;
			adapter.notifyDataSetChanged();
			setLogoutFlag(false);
		}
		
		
		if(itemList.size()>=1) weibo_listview.showFootView(true);
		
		else
		{
			weibo_listview.showFootView(false);
			onRefreshWeibo(null);
		}
		
		
	
	//	showUnsendHint(null);
	}

	public void onClearUnSendHint(View v)
	{
		unsend_hint_layout.setVisibility(View.GONE);
	}
	public void onMoreWeibo(View v)
	{
		long id=itemList.get(itemList.size()-1).id;
		
		new String();
		new GetNewWeibo().execute(nickname_cur,String.valueOf(id),"2");
		
		getting_layout.setVisibility(View.VISIBLE);
		getmore_weibo.setVisibility(View.GONE);
	}

	public void onRefreshWeibo(View v)
	{
		
		weibo_listview.showHeadView();
		if(itemList.size()>=1) 
			weibo_listview.setSelection(0);
		onRefresh();
	}
	public void onNewWeibo(View v)
	{
		if(!ProcessUserStatus.processUserStatus(this, 4)) return;
		
		Intent i=new Intent();
		i.setClass(CarCircle.this, NewWeibo.class);
		startActivity(i);
		overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
		
	}
	
	private String getNickName()
	{
		return UserManager.getCurUser(this.getApplicationContext());
		
	}

	

	@Override
	public void onRefresh() {
	
		//Log.d("lichao","read id is "+readed_new_id);
		if(itemList.size()>=1) 
		{
			for(ImageAndText imt:itemList)
				if(imt.grade!=2)
				{
					readed_new_id=imt.id;
					break;
				}
		}
		else
			readed_new_id=-1;
		if(isTopWeiboOK())
			new GetNewWeibo().execute(nickname_cur,String.valueOf(readed_new_id),"1");
		else
			new GetNewWeibo().execute(nickname_cur,String.valueOf(readed_new_id),"0");
		
		
		
	}
	
	private void showFailureHint(String hint)
	{
		failure_hint_text.setText(hint);
		failure_hint_layout.setVisibility(View.VISIBLE);
		
		new Handler().postDelayed(new Runnable(){
			@Override
			public void run(){
				failure_hint_layout.setVisibility(View.GONE);
				
			}
		}, 3000);
	}
	private void showUnsendHint(String hint)
	{
		unsend_hint_text.setText(hint);
		//if(getUnSendFlag())
			unsend_hint_layout.setVisibility(View.VISIBLE);
	/*	else
			unsend_hint_layout.setVisibility(View.GONE);*/
		
	}
	public static String getTimeD(){
		 SimpleDateFormat   format=new   SimpleDateFormat("yyyyMMdd");
		 Date   curDate   =   new   Date(System.currentTimeMillis()); 
		 return format.format(curDate);
	}
	private void setLogoutFlag(boolean flag)
	{
		SharedPreferences preference = getSharedPreferences("CarWeiboLogout", MODE_PRIVATE);
		Editor editor = preference.edit();
		editor.putBoolean("CarWeiboLogoutFlag", flag);
		editor.commit();
	}
	private boolean getLogoutFlag()
	{
		SharedPreferences preference = getSharedPreferences("CarWeiboLogout", MODE_PRIVATE);
		return preference.getBoolean("CarWeiboLogoutFlag", true);
	}

	int detail_pos;

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		
		detail_pos=arg2-1;
		ImageAndText imt=adapter.getItem(detail_pos);
		
		
		Intent i=new Intent();
		Rect frame=new Rect();
		this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int screenWidth  = getWindowManager().getDefaultDisplay().getWidth();       // 屏幕宽（像素，如：480px）   
		int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		
		
		Bundle bd=new Bundle();
		i.setClass(CarCircle.this, WeiboDetail.class);
		bd.putSerializable("data", imt);
		i.putExtras(bd);
		i.putExtra("stateHeight", frame.top);
		i.putExtra("screenHeight",screenHeight);
		i.putExtra("screenWidth",screenWidth);
		//i.putExtra("to_user_id", imt.user_id);
		this.startActivityForResult(i, 2);
		overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
		
		
	}

	public void onUserWeibo(View v)
	{
		int pos;
		if(v instanceof ImageView)
		{
			String tmp=(String)v.getTag();
			pos=Integer.parseInt(tmp.substring(0,tmp.indexOf("http")));
		}
		else
			pos=(Integer)v.getTag();
	
		Intent i=new Intent();
		i.setClass(CarCircle.this, UserWeiboList.class);
		
		i.putExtra("isId", true);
		i.putExtra("nickname", itemList.get(pos).nickname);
		
		i.putExtra("user_id", itemList.get(pos).user_id);
		
		startActivity(i);
		overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
		
	}
	public void onViewPic(View v)
	{
		
		String tag=(String)v.getTag();
		int pos=Integer.parseInt(tag.substring(0,tag.indexOf("http")));
		
	
		ImageAndText imt=adapter.getItem(pos);
		
		tag=tag.substring(tag.indexOf("http"),tag.length());
		
		if(imt.pic_list_url.size()==0) return;
		
		int k=0;
		for(k=0;k<imt.pic_list_url.size();k++)
		{
			if(imt.pic_list_url.get(k).url.equals(tag))
				break;
		}
		
	
		
		Intent i=new Intent();
		Rect frame=new Rect();
		this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int screenWidth  = getWindowManager().getDefaultDisplay().getWidth();       // 屏幕宽（像素，如：480px）   
		int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		
		i.setClass(CarCircle.this, ViewPic.class);
		Bundle bd=new Bundle();
	
		bd.putSerializable("data", imt);
		i.putExtra("index", k);
		i.putExtra("height", frame.top);
		i.putExtra("screenWidth",screenWidth);
		i.putExtra("screenHeight", screenHeight);
		i.putExtras(bd);
		startActivity(i);
				
	}
	
	int delete_pos;
	
	public void onDeleteWeibo(View v)
	{
		delete_pos=(Integer) v.getTag();
		Intent i=new Intent();
		
		
		i.putExtra("text", "提示\n\n"+"确定删除帖子吗？");
		i.putExtra("ok", "确定");
		i.putExtra("cancel", "取消");
		i.setClass(CarCircle.this, Dialog.class);
		this.startActivityForResult(i,1);
		
	}

	public void onShareWeibo(View v)
	{
		if(!ProcessUserStatus.processUserStatus(this, 4)) return;
		int pos=(Integer) v.getTag();
		
		
		String dir=getUploadPic((View) v.getParent().getParent());
		
		if(dir==null) return;
		Intent i=new Intent();
		Bundle bd=new Bundle();
		bd.putSerializable("imt", adapter.getItem(pos));
		i.putExtra("dir", dir);
		i.putExtras( bd);
		i.setClass(CarCircle.this, ShareDialog.class);
		
		startActivity(i);
	}
	public void onGood(View v)
	{
		if(!ProcessUserStatus.processUserStatus(this, 4)) return;
		int pos=(Integer)v.getTag();
		ImageAndText imt=adapter.getItem(pos);
		
		if(imt.grade==2)
			new SendWeibo().sendGood(this, getNickName(), imt.id, true);
		else
			new SendWeibo().sendGood(this, getNickName(), imt.id, false);
		
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode==1)
		{
			if(resultCode==0)
			{
				if(itemList.get(delete_pos).grade==2)
					new SendWeibo().deleteWeibo(this, getNickName(),itemList.get(delete_pos).id,true);
				else
					new SendWeibo().deleteWeibo(this, getNickName(),itemList.get(delete_pos).id,false);
			
				itemList.remove(delete_pos);
				adapter.notifyDataSetChanged();
			}
		}
		if(requestCode==2)
		{
			if(resultCode==0)
			{
				
					itemList.remove(detail_pos);
					adapter.notifyDataSetChanged();
				
			}
		}
		if(requestCode==3)
		{
			if(resultCode==0)
			{
				
				Intent i=new Intent();
				i.setClass(CarCircle.this, Login.class);
				
				this.startActivityForResult(i, 4);
				overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);//全新登录，数据库没有用户数据

				
			}
		}
		if(requestCode==4)
		{
			if(resultCode==1)
			{
				UserManager.setCurUser(this.getApplicationContext(), data.getStringExtra("nickname"));
				
				Toast.makeText(this, "登录成功～", Toast.LENGTH_SHORT).show();
				
			}
		}
	}
	
	private String getUploadPic(View v)
	{

		String SDDir=ScreenShoot.getSDDir(this);
		
		if(SDDir==null)
		{
			Toast.makeText(this, "T卡空间不足，不能进行分享～", Toast.LENGTH_SHORT).show();
			return null;
		}
       
		 Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(),  
	                Bitmap.Config.ARGB_8888);  
		 
		 Canvas canvas = new Canvas(b);
		 v.draw(canvas);
		 ScreenShoot.savePic(b,SDDir+"/weizhangquery/cache",SDDir+"/weizhangquery/cache/"+"tmp.png",100);
			
		return SDDir+"/weizhangquery/cache/"+"tmp.png";
		
	}


	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2,
			long arg3) {
		
		String[] option;//={"复制帖子"};
		//Log.d("lichao","nickname is "+nickname);
		//Log.d("lichao","item nickname is "+itemList.get(arg2-1).nickname);
		if(nickname.getText().toString().equals(itemList.get(arg2-1).nickname))
		{
			option=new String[2];
			option[1]="删除帖子";
			option[0]="复制帖子内容";
		}
		else
		{
			option=new String[1];
		
			option[0]="复制帖子内容";
		}
		
		new AlertDialog.Builder(CarCircle.this).setTitle("选择操作").setItems(option, new OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
				
				if(arg1==1)
				{
					delete_pos=arg2-1;
					Intent i=new Intent();
					
					
					i.putExtra("text", "提示\n\n"+"确定删除帖子吗？");
					i.putExtra("ok", "确定");
					i.putExtra("cancel", "取消");
					i.setClass(CarCircle.this, Dialog.class);
					startActivityForResult(i,1);
				}
				
				if(arg1==0)
				copyToBuffer(arg2-1);
				
			}}).show();//显示对话框
		
		  
		
	
		return false;
	}


	protected void copyToBuffer(int i) {
		ClipboardManager cmb = (ClipboardManager)this.getSystemService(Context.CLIPBOARD_SERVICE);  
		cmb.setText(itemList.get(i).content.trim());
		Toast.makeText(this, "帖子内容已复制到剪帖板", Toast.LENGTH_SHORT).show();
		
	}


	@Override
	public void onBackPressed() {
		this.finish();
		overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
		super.onBackPressed();
	}
	
	
	
}
