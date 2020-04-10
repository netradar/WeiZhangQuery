package carweibo.netradar.lichao;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carweibo.netradar.lichao.Account.GetUesrInfo2;
import carweibo.netradar.lichao.MessageInfo.MSG_STATUS;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PrivateMsgChat extends Activity implements TextWatcher, Runnable, Callback, OnTouchListener, OnItemLongClickListener {

	
	ListView message_listview;
	LinkedList<MessageInfo> message_list;
	LinkedList<MessageInfo> message_list_total;
/*	ArrayList<MessageInfo> message_list;
	ArrayList<MessageInfo> message_list_total;*/
	MessageAdapter adapter;
	EditText msg_edittext;
	Button sendBtn;
	float denisty;
	
	String nickname;
	int user_id;
	String touxiang_url;
	
	UserInfo user;
	Bitmap sender_bmp;
	Thread send_thread;
	Handler handler;
	List<MessageInfo> msgBuffer;
	LoginToServer loginToServer;
	TextView title_nickname;
	private LinearLayout headView;
	
	
	BroadcastReceiver bdReceiver;
	private IntentFilter ifilter;
	
	ProgressDialog pd;
	Context context;
	public class GetSingleWeibo extends AsyncTask<Long, Integer, String> {

		
		@Override
		protected void onPostExecute(String result) {
			
			pd.dismiss();
			if(isCancel)
			{
				
				isCancel=false;
				
				return;
			}
			if(result.equals("error_data"))
			{
				
				Toast.makeText(context, "网络连接有问题，获取数据失败", Toast.LENGTH_SHORT).show();
				
				return;
			}
			if(result.equals("no_weibo"))
			{
				
				Toast.makeText(context, "原帖已被删除", Toast.LENGTH_SHORT).show();
				
				return;
			}
			processWeiboRet(result);
			super.onPostExecute(result);
		}

		@Override
		protected String doInBackground(Long... arg0) {
			
			long weibo_id=arg0[0];
			boolean isTop;
			if(arg0[1]==0)
				isTop=true;
			else
				isTop=false;
			
			return new LoginToServer().GetSingleWeiboById(context,UserManager.getCurUser(context.getApplicationContext()),weibo_id,isTop);
			
		}

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.message_layout);
		context=this;
		initView();
		initData();
	
	}
	public void processWeiboRet(String result) {

		Log.d("lichao","privatemsgchat result is "+result);
		try {
			JSONObject json=new JSONObject(result);
			
			ImageAndText imt=new ImageAndText(((DBUility)this.getApplicationContext()).webapps+"/pic",json.getJSONObject("single_weibo"),this.getResources().getDisplayMetrics().density);
		
			Intent i=new Intent();
			Rect frame=new Rect();
			this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
			int screenWidth  = getWindowManager().getDefaultDisplay().getWidth();       // 屏幕宽（像素，如：480px）   
			int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
			
			
			Bundle bd=new Bundle();
			i.setClass(PrivateMsgChat.this, WeiboDetail.class);
			bd.putSerializable("data", imt);
			i.putExtras(bd);
			i.putExtra("stateHeight", frame.top);
			i.putExtra("screenHeight",screenHeight);
			i.putExtra("screenWidth",screenWidth);
			
			this.startActivityForResult(i, 2);
			this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
			
			

		} catch (JSONException e) {
			
		}
		
		
	}
	private void initData() {
		message_list=new LinkedList<MessageInfo>();
		message_list_total=new LinkedList<MessageInfo>();
		denisty=this.getResources().getDisplayMetrics().density;
		user=UserManager.getSingleUserInfo(this.getApplicationContext(), UserManager.getCurUser(this.getApplicationContext()));
		
		sender_bmp=BitmapFactory.decodeFile(user.touxiang_url);
		adapter=new MessageAdapter(this,message_list,denisty,message_listview);
		adapter.setTouxiang(sender_bmp,null);
		
		nickname=this.getIntent().getStringExtra("nickname");
		user_id=this.getIntent().getIntExtra("user_id", -1);
		touxiang_url=this.getIntent().getStringExtra("touxiang_url");
		message_listview.setAdapter(adapter);
		
		message_listview.setOnItemLongClickListener(this);
		
		msgBuffer=new ArrayList<MessageInfo>();
		handler=new Handler(this);
		send_thread=new Thread(this);
		send_thread.start();
		
		title_nickname.setText(nickname);
		
		loginToServer=new LoginToServer();
		pd = new ProgressDialog(this);
		getChatRecordFromDB();
		
		bdReceiver=new BroadcastReceiver() {  
	   		  
    	    @Override  
    	    public void onReceive(Context context, Intent intent) {  
    	        // TODO Auto-generated method stub   
    	    	
    	    	int type=intent.getIntExtra("type", -1);
    	    	
    	    	if(type!=4) return;
    	    	
    	    	processBD();
    	    	
    	    }



			
    	  
    	}; 
    	ifilter= new IntentFilter(); 
    	ifilter.addAction("netradar.bd1");
		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}

	
	protected void processBD() {
		this.finish();
		
	}
	@Override
	protected void onStart() {
	
		super.onStart();
		registerReceiver(bdReceiver,ifilter);
	}
	@Override
	protected void onStop() {
		super.onStop();
		
	}
	
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
		unregisterReceiver(bdReceiver);
	}
	private void getChatRecordFromDB() {
		
		String msg_list=MessageManager.getSingleUserMsg(this.getApplicationContext(), UserManager.getCurUser(this.getApplicationContext()), user_id);
		
		if(msg_list!=null&&msg_list.length()!=0)
		{
		
		try {
			JSONArray ja=new JSONArray(msg_list);
			
			for(int i=0;i<ja.length();i++)
			{
				MessageInfo msg=new MessageInfo();
				msg.setFrom_touxiang(touxiang_url);
				if(ja.getJSONObject(i).getInt("status")==0)
					msg.setMsg_status(MSG_STATUS.OK);
				else
					msg.setMsg_status(MSG_STATUS.ERROR);
				msg.setTime(getTime(ja.getJSONObject(i).getString("time")));
				msg.setMsg_tag(ja.getJSONObject(i).getLong("tag"));
				//int type=ja.getJSONObject(i).getInt("msg_type");
				
				
				String ref_weibo=URLDecoder.decode(ja.getJSONObject(i).getString("ref_weibo"),"UTF-8");
				String tmp= URLDecoder.decode(ja.getJSONObject(i).getString("msg"),"UTF-8");
				msg.setMsg_type(ja.getJSONObject(i).getInt("msg_type"));
				msg.setWeibo_id(ja.getJSONObject(i).getLong("weibo_id"));
				msg.setTop(ja.getJSONObject(i).getBoolean("is_top"));
				if(ref_weibo.length()>10)
					ref_weibo=ref_weibo.substring(0,9)+"...";
				msg.setRef_weibo(ref_weibo);
				if(ref_weibo.length()>10)
					ref_weibo=ref_weibo.substring(0,9);
				switch(msg.getMsg_type())
				{
				case 0:
					break;
				case 1:
					
					break;
				case 2:
					tmp= "我赞了你的帖子《"+ref_weibo+"...》";
					break;
				case 3:
				
					tmp= "我转发了你的帖子《"+ref_weibo+"...》到“新浪微博”";
					break;
				case 4:
					
					tmp= "我转发了你的帖子《"+ref_weibo+"...》到“腾讯微博”";
					break;
				case 5:
					
					tmp= "我转发了你的帖子《"+ref_weibo+"...》到“微信朋友圈”";
					break;
				}
				msg.setMsg(tmp);
				
				msg.setSend(ja.getJSONObject(i).getBoolean("isSend"));
				message_list_total.add(msg);
			}
			
			
			
		} catch (JSONException e) {
			Log.d("lichao","PrivateMsgChat json error1:"+e.toString());
		} catch (UnsupportedEncodingException e) {
			Log.d("lichao","PrivateMsgChat json error1:"+e.toString());
		}
		}
		if(message_list_total.size()>10)
		{
			for(int i=0;i<10;i++)
			{
				message_list.addFirst(message_list_total.get(message_list_total.size()-1-i));
				
			}
			getting_layout.setVisibility(View.GONE);
			getmore_weibo.setVisibility(View.VISIBLE);	
		}
		else
		{
			for(MessageInfo info:message_list_total)
			{
				message_list.add(info);
				
			}
			getting_layout.setVisibility(View.GONE);
			getmore_weibo.setVisibility(View.GONE);	
		}
		adapter.notifyDataSetChanged();
		
		message_listview.scrollTo(0, message_list.size());
	}
	LinearLayout getting_layout;
	Button getmore_weibo;
	private void initView()
	{
		
		message_listview=(ListView)findViewById(R.id.message_listview);
		msg_edittext=(EditText)findViewById(R.id.message_bottom_edittext);
		sendBtn=(Button)findViewById(R.id.message_bottom_send_btn);
		title_nickname=(TextView)findViewById(R.id.message_nickname);
		headView=(LinearLayout)LayoutInflater.from(this).inflate(R.layout.foot, null);
		sendBtn.setEnabled(false);
		getting_layout=(LinearLayout)headView.findViewById(R.id.getting_weibo);
		getmore_weibo=(Button)headView.findViewById(R.id.moreweibo);
		getmore_weibo.setText("点击获取历史消息");
		message_listview.addHeaderView(headView);
		msg_edittext.addTextChangedListener(this);
		
		msg_edittext.setOnTouchListener(this);
	
	}
	boolean test=false;
	public void onSendMsg(View v)
	{
		MessageInfo msg=new MessageInfo();
		msg.setSend(true);
		msg.setFrom_nickname(nickname);
		msg.setMsg(msg_edittext.getText().toString());
		msg.setTime(getTime());
		msg.setFrom_touxiang(touxiang_url);
		msg.setMsg_status(MessageInfo.MSG_STATUS.SENDING);
		msg.setFrom_user_id(user_id);
		msg.setTo_nickname(UserManager.getCurUser(this.getApplicationContext()));
		msg.setMsg_tag(System.currentTimeMillis());
		message_list.add(msg);
		message_list_total.add(msg);
		
		
		sendMessage(msg);
		
		adapter.notifyDataSetChanged();
		message_listview.scrollTo(0, message_list.size());
		msg_edittext.setText("");
	}
	boolean thread_exit=false;
	public String getTime(String time){
		 return time.substring(4,6)+"月"+time.substring(6,8)+"日 "+time.substring(8,10)+":"+time.substring(10,12);
	}
	private void sendMessage(MessageInfo msg) {
		
		
		synchronized (msgBuffer)
		{
			msgBuffer.add(msg);
		//	if(send_thread.getState()==Thread.State.WAITING)
			{
				msgBuffer.notify();
			}
		}
		
	}
	@Override
	public void afterTextChanged(Editable arg0) {
		
		
	}
	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		
		
	}
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if(msg_edittext.getText().toString().length()!=0)
		{
			sendBtn.setEnabled(true);
		}
		else
			sendBtn.setEnabled(false);
		
	}

	public String getTime(){
		 SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("MM月dd日   HH:mm");
		 
	//	 SimpleDateFormat   formatter1   =   new   SimpleDateFormat   ("YYMMddHHmmss");
		 Date   curDate   =   new   Date(System.currentTimeMillis());   
		 return formatter.format(curDate);
	}
	public String getTime1(){
	//	 SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("MM月dd日   HH:mm");
		 
		 SimpleDateFormat   formatter1   =   new   SimpleDateFormat   ("yyyyMMddHHmmss");
		 Date   curDate   =   new   Date(System.currentTimeMillis());   
		 return formatter1.format(curDate);
	}
	@Override
	public void run() {
		
		MessageInfo msg=null;
		while(!thread_exit)
		{
			
			synchronized (msgBuffer)
			{
				
				if(msgBuffer.isEmpty()||msgBuffer.size()==0)
					try {
						
						msgBuffer.wait();
						continue;
					} catch (InterruptedException e) {
						
					}
				else
				{
					
					
					msg=msgBuffer.get(0);
					
					msgBuffer.remove(0);
					
					msgBuffer.notify();
				}
			}
			
			/*if(msgBuffer.isEmpty()||msgBuffer.size()==0) continue;
			else*/
			{
				int ret=loginToServer.sendPrivateMsg(this,msg.getFrom_user_id(),msg.getTo_nickname(),msg.getMsg(),msg.getTime());
				/*try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				 Message message = handler.obtainMessage(ret,msg);
	           
	             handler.sendMessage(message);
			}
             /*synchronized (this)
             {
	             if(msgBuffer.isEmpty())
					try {
						wait();
					} catch (InterruptedException e) {
						Log.d("lichao","thread wait error");
					}
             }*/
			/*}
			 synchronized (this)
			 {
				try {
					wait();
				} catch (InterruptedException e) {
					Log.d("lichao","thread wait error");
				}
			 }*/
		}
		
	//	Log.d("lichao","thread wait exit");
		
	}
	@Override
	public boolean handleMessage(Message message) {
		MessageInfo msg=(MessageInfo)message.obj;
		if(message.what==0)
			msg.setMsg_status(MSG_STATUS.OK);
		else
			msg.setMsg_status(MSG_STATUS.ERROR);
		try {
			msg.setFrom_nickname(URLEncoder.encode(msg.getFrom_nickname(),"UTF-8"));
			msg.setTo_nickname(URLEncoder.encode(msg.getTo_nickname(),"UTF-8"));
			msg.setFrom_touxiang(msg.getFrom_touxiang().substring(msg.getFrom_touxiang().lastIndexOf("/")+1, msg.getFrom_touxiang().length()));
			msg.setMsg_type(0);
			msg.setTime(getTime1());
			msg.setRef_weibo("");
			msg.setMsg(URLEncoder.encode(msg.getMsg(),"UTF-8"));
			if(msg.isReSend&&msg.getMsg_status()==MSG_STATUS.OK)
			{
				MessageManager.setSingleMsgStatusByTag(this.getApplicationContext(), msg.getTo_nickname(), msg.getFrom_user_id(), msg.getMsg_tag(), 0);
			}
			else
				MessageManager.AddMessage(this.getApplicationContext(), msg);
			msg.setMsg(URLDecoder.decode(msg.getMsg(),"UTF-8"));
			msg.setTime(getTime());
		} catch (UnsupportedEncodingException e) {
		
		}
		
		
		refreshMsgStatus(message.what,((MessageInfo)message.obj).getMsg_tag());
		return false;
	}
	private void refreshMsgStatus(int what, long msg_tag) {
		
		for(MessageInfo msg:message_list)
		{
			if(msg.getMsg_tag()==msg_tag)
			{
				if(what==0)
					msg.setMsg_status(MSG_STATUS.OK);
				else
					msg.setMsg_status(MSG_STATUS.ERROR);
			}
		}
		
		adapter.notifyDataSetChanged();
	}
	@Override
	public void onBackPressed() {
		
		super.onBackPressed();
		thread_exit=true;
		//if(send_thread.getState()==Thread.State.WAITING)
		{
			 synchronized (msgBuffer)
			 {
				 msgBuffer.notify();
			 }
		}
		
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
		
		boolean isOpen=imm.isActive(); 
		if(isOpen==true)
		{
			imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
		this.finish();
		this.overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
		
	}
	
	public void onCancelMessage(View v)
	{
		onBackPressed();
	}
	
	private void refreshHeadView(int status)
	{
		switch(status)
		{
		
		}
	}
	public void onMoreWeibo(View v)
	{
		int top=message_listview.getChildAt(1).getTop();
		int total=message_list_total.size();
		int cur_num=message_list.size();
		
		int add_num=0;
		message_listview.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
		if(total-cur_num>10)
		{
			for(int i=0;i<10;i++)
				message_list.addFirst(message_list_total.get(total-cur_num-i-1));
				
			add_num=10;
			
			getting_layout.setVisibility(View.GONE);
			getmore_weibo.setVisibility(View.VISIBLE);	
		}
		else
		{
			for(int i=0;i<total-cur_num;i++)
			message_list.addFirst(message_list_total.get(total-cur_num-i-1));
			
			add_num=total-cur_num;
			getting_layout.setVisibility(View.GONE);
			getmore_weibo.setVisibility(View.GONE);	
		}
		
		adapter.notifyDataSetChanged();
	//	message_listview.setSelection(add_num);
		message_listview.setSelectionFromTop(add_num+1,top);
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		message_listview.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		return false;
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2,
			long arg3) {
		String[] option;

		if(message_list.get(arg2-1).msg_status==MSG_STATUS.ERROR)
		{
			option=new String[2];
			option[1]="重新发送";
			option[0]="复制帖子内容";
		}
		else
		{
			option=new String[1];
		
			option[0]="复制帖子内容";
		}
		
		new AlertDialog.Builder(this).setTitle("选择操作").setItems(option,new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				if(which==1)
				{
					reSendMsg(arg2-1);
				}
				if(which==0)
				{
					copyToBuffer(arg2-1);
				}
				
			}}).show();
			
			
		
		
		  
	
		return false;
	}
	protected void reSendMsg(int i) {
		
		MessageInfo msg=message_list.get(i);
		msg.setSend(true);
		msg.setFrom_nickname(nickname);
		msg.setReSend(true);
		
		msg.setFrom_touxiang(touxiang_url);
		msg.setMsg_status(MessageInfo.MSG_STATUS.SENDING);
		msg.setFrom_user_id(user_id);
		msg.setTo_nickname(UserManager.getCurUser(this.getApplicationContext()));
		
		sendMessage(msg);
		
		adapter.notifyDataSetChanged();
	}
	protected void copyToBuffer(int i) {
		ClipboardManager cmb = (ClipboardManager)this.getSystemService(Context.CLIPBOARD_SERVICE);  
		cmb.setText(message_list.get(i).msg.trim());  
		
		Toast.makeText(this, "信息已复制到剪帖板", Toast.LENGTH_SHORT).show();
		
	}
	boolean isCancel=false;
	public void onViewDetail(View v)
	{
		int position=(Integer) v.getTag();
		
		pd=new ProgressDialog(this);
		pd.setCancelable(true);
		pd.setMessage("正在获取数据，请稍候...");
		pd.setCanceledOnTouchOutside(false);
		pd.setIndeterminateDrawable(this.getResources().getDrawable(R.drawable.progress));
		pd.setOnCancelListener(new OnCancelListener(){

			@Override
			public void onCancel(DialogInterface arg0) {
				
				isCancel=true;
			}});
		
       	pd.show();
       	
       	if(message_list.get(position).isTop())
       		new GetSingleWeibo().execute(message_list.get(position).weibo_id,0L);
       	else
       		new GetSingleWeibo().execute(message_list.get(position).weibo_id,1L);
	}
	/*@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
	//	Log.d("lichao","onTouch");
		my_layout.isInput=true;
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
		boolean isOpen=imm.isActive(); 
		//msg_edittext.setVisibility(View.GONE);
		msg_edittext.requestFocus();
		//title_nickname.requestFocus();
		//if(isOpen==false)
		{
			
			imm.showSoftInput(msg_edittext,InputMethodManager.SHOW_FORCED);
		}
		return true;
	}*/


	
}
