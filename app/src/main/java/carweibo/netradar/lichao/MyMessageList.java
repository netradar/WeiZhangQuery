package carweibo.netradar.lichao;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carweibo.netradar.lichao.ServerService.LocalBinder;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

public class MyMessageList extends Activity implements OnItemClickListener, OnItemLongClickListener {

	ListView msg_listview;
	List<HashMap<String, String>> msg_list;
	MyMessageAdapter adapter;
	float denisty;
	String webapps;
	BroadcastReceiver bdReceiver;
	private IntentFilter ifilter;
	
	boolean isService;
	
	ProgressBar progress_bar;
	Button refresh_btn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.my_msglist_layout);
		SharedPreferences pre= getSharedPreferences("carweibo_unread_tips", Context.MODE_PRIVATE);
		Editor editor = pre.edit();
		
		
		editor.putInt("msg_sum", 0);
		editor.commit();
		isService=this.getIntent().getBooleanExtra("isService", false);
		if(isService)
		{	
			Intent intent = new Intent("netradar.bd1"); 
			 intent.putExtra("type", 4);
			 
			 sendBroadcast(intent); 
			
			if(getActRunFlag())
			{
				
				this.finish();
			}
			
		//	return;
		}
		clearNotify(1);
		
		setActRunFlag(true);
		
		
		 bdReceiver=new BroadcastReceiver() {  
	   		  
	    	    @Override  
	    	    public void onReceive(Context context, Intent intent) {  
	    	        // TODO Auto-generated method stub   
	    	    	
	    	    	int type=intent.getIntExtra("type", -1);
	    	    	
	    	    	if(type!=4) return;
	    	    	
	    	    	refreshList();
	    	    	progress_bar.setVisibility(View.GONE);
	    	    	refresh_btn.setEnabled(true);
	    	    	
	    	    }



				
	    	  
	    	}; 
	    	ifilter= new IntentFilter(); 
	    	ifilter.addAction("netradar.bd");
	    	initView();
			initData();
	}

	ServiceConnection conn;
	@Override
	protected void onStart() {
		
		
		super.onStart();
		conn = new ServiceConnection() 
		{

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				
				LocalBinder binder=(LocalBinder)service;
				bindService = binder.getService();
				
		}

			@Override
			public void onServiceDisconnected(ComponentName name) {}
			
		};
		
		Intent i=new Intent();
		i.setClass(MyMessageList.this, ServerService.class);
		bindService(i, conn, Context.BIND_AUTO_CREATE);
		
		
		registerReceiver(bdReceiver,ifilter);
		
	}


	@Override
	protected void onStop() {
	
		super.onStop();
	//	setActRunFlag(false);
		unbindService(conn);
		unregisterReceiver(bdReceiver);
	}

	

	@Override
	protected void onDestroy() {
	//	Log.d("lichao","onDestroy");
	//	setActRunFlag(false);
		super.onDestroy();
	}


	@Override
	public void onBackPressed() {
		Log.d("lichao","onBackPressed");
		setActRunFlag(false);
		super.onBackPressed();
		this.finish();
		this.overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
		
	}
	public void onCancelMyMsgList(View v)
	{
		onBackPressed();
	}

	private void initView() 
	{
		msg_listview=(ListView)findViewById(R.id.my_msglist_listview);
		progress_bar=(ProgressBar)findViewById(R.id.my_msg_progressBar);
		refresh_btn=(Button)findViewById(R.id.my_msg_refresh_btn);
	}
	private void initData()
	{
		
		progress_bar.setVisibility(View.GONE);
		denisty=this.getResources().getDisplayMetrics().density;
		msg_list=new ArrayList<HashMap<String,String>>();
		adapter=new MyMessageAdapter(this,msg_list,R.layout.my_msglist_layout,new String[]{"nickname","unread_num","time","msg"},new int[]{R.id.my_msg_item_nickname,R.id.my_msg_item_unread_tip,R.id.my_msg_item_time,R.id.my_msg_item_msg},denisty,msg_listview);
		msg_listview.setAdapter(adapter);
		webapps=((DBUility)this.getApplicationContext()).webapps+"/pic";
		msg_listview.setOnItemClickListener(this);
		msg_listview.setOnItemLongClickListener(this);
		refreshList();
		
	}

	private void refreshList() {
		
		msg_list.clear();
		List<MyMessageData> list=new ArrayList<MyMessageData>();
		String nickname=UserManager.getCurUser(this.getApplicationContext());
		if(nickname.equals("NOUSER")||nickname.equals("NOLOGIN"))
		{
			return;
		}
		list=MessageManager.getMsgListByNickname(this.getApplicationContext(),nickname);
	
		if(list!=null)
		{
			for(MyMessageData data:list)
			{
				HashMap<String,String> map=new HashMap<String,String>();
				try {
					map.put("nickname",URLDecoder.decode(data.getNickname(),"UTF-8"));
				
				map.put("touxiang_url",webapps+"/touxiang/"+data.getTouxiang_url());
				map.put("touxiang_name",data.getTouxiang_url());
				
				map.put("msg_id", String.valueOf(data.getMsg_id()));
				map.put("user_id",String.valueOf(data.getUser_id()));
				map.put("user_score",String.valueOf(data.getUser_score()));
				map.put("msg_list", data.getMsgList());
				map.put("msg", getLastMsg(data.getMsgList()));
				map.put("time", getLastTime(data.getMsgList()));
				
				int unread_num=data.getUnread_num();
				if(unread_num>99)
					map.put("unread_num", "99+");
				else
					map.put("unread_num", String.valueOf(unread_num));
				msg_list.add(map);
				} catch (UnsupportedEncodingException e) {

				}
			}
			adapter.notifyDataSetChanged();
		}
	}

	private String getLastTime(String msgList) {
		try {
			JSONArray ja=new JSONArray(msgList);
			return getTime(ja.getJSONObject(ja.length()-1).getString("time"));
		} catch (JSONException e) {
			return "";
		}
		
	}
	public String getTime(String time){
		 return time.substring(4,6)+"月"+time.substring(6,8)+"日 "+time.substring(8,10)+":"+time.substring(10,12);
	}
	private String getLastMsg(String msgList) {

		
		try {
			JSONArray ja=new JSONArray(msgList);
			//Log.d("lichao",ja.getJSONObject(ja.length()-1).toString());
			int type=ja.getJSONObject(ja.length()-1).getInt("msg_type");
			String ref_weibo=URLDecoder.decode(ja.getJSONObject(ja.length()-1).getString("ref_weibo"),"UTF-8");
			String tmp= URLDecoder.decode(ja.getJSONObject(ja.length()-1).getString("msg"),"UTF-8");
			
			if(ref_weibo.length()>10)
				ref_weibo=ref_weibo.substring(0,9);
			switch(type)
			{
			case 0:
				return tmp;
			case 1:
				return "我在帖子《"+ref_weibo+"...》给你有新评论:"+tmp;
			case 2:
				return "我赞了你的帖子《"+ref_weibo+"...》";
			
			case 3:
			
				return "我转发了你的帖子《"+ref_weibo+"》到新浪微博";
				
			case 4:
				
				return "我转发了你的帖子《"+ref_weibo+"》到腾讯微博";
				
			case 5:
				
				return "我转发了你的帖子《"+ref_weibo+"》到微信朋友圈";
				
			}
		} catch (JSONException e) {
			return "";
		} catch (UnsupportedEncodingException e) {
			return "";
		}
		return "";
	}



	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	
		SharedPreferences pre= getSharedPreferences("carweibo_unread_tips", Context.MODE_PRIVATE);
		Editor editor = pre.edit();
		
		
		editor.putInt("msg_sum", 0);
		editor.commit();
		Intent i=new Intent();
		
		i.setClass(MyMessageList.this, PrivateMsgChat.class);
		
		i.putExtra("nickname", msg_list.get(arg2).get("nickname"));
		i.putExtra("user_id",Integer.parseInt(msg_list.get(arg2).get("user_id")));
		i.putExtra("touxiang_url", msg_list.get(arg2).get("touxiang_url"));
		
		MessageManager.clearMsgById(this.getApplicationContext(), msg_list.get(arg2).get("msg_id"));
		Intent intent = new Intent("netradar.bd"); 
		intent.putExtra("type", 4);
		 
		clearNotify(1);
		startActivityForResult(i,2);
		this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);

		this.sendBroadcast(intent);
	}

	int delete_pos;
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		delete_pos=arg2;
		Intent i=new Intent();
		
		
		String nickname=msg_list.get(arg2).get("nickname");
		
		i.putExtra("text", "提示\n\n"+"确定删除与\""+nickname+"\"的私信记录吗？");
		i.putExtra("ok", "确定");
		i.putExtra("cancel", "取消");
		i.setClass(MyMessageList.this, Dialog.class);
		this.startActivityForResult(i,1);
		
		return false;

	}

	ServerService bindService;
	public void onRefreshNow(View v)
	{
		refresh_btn.setEnabled(false);
		
		progress_bar.setVisibility(View.VISIBLE);
		bindService.refreshNow();
	//	 

	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==1&&resultCode==0)
		{
			
			MessageManager.deleteMsgById(this.getApplicationContext(), msg_list.get(delete_pos).get("msg_id"));
			msg_list.remove(delete_pos);
			adapter.notifyDataSetChanged();
		}
	}

	public void clearNotify(int id)
	{
		
	   	String ns = Context.NOTIFICATION_SERVICE;
	   	
	   	NotificationManager mNotificationManager = (NotificationManager)getApplicationContext().getSystemService(ns);


	   	mNotificationManager.cancel(id);
		
	}
	private boolean getActRunFlag()
	{
		SharedPreferences pre= getSharedPreferences("carweibo_my_message", Context.MODE_PRIVATE);
		
		return pre.getBoolean("my_msg_run", false);
		
		
	}
	private void setActRunFlag(boolean isRun)
	{
		SharedPreferences pre= getSharedPreferences("carweibo_my_message", Context.MODE_PRIVATE);
		Editor editor = pre.edit();
		
		editor.putBoolean("my_msg_run",isRun);
		editor.commit();
	}
	
}
