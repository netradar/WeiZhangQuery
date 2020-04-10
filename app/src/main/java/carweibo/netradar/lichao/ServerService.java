package carweibo.netradar.lichao;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;


import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.ping.packet.Ping;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import carweibo.netradar.lichao.MessageInfo.MSG_STATUS;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Binder;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class ServerService extends Service implements Callback {

	String connect_sync_flag="";
	boolean isConnecting=false;
	private class ConnectToOpenfireTask extends TimerTask
	{

		@Override
		public void run() {
			
			synchronized (connect_sync_flag)
        	{
				if(!isConnecting)
				{
					isConnecting=true;
					if(connectionToOpenfire())
				
		        	{	
		        		stopConnectToOpenfireTimer();
		        		startPingOpenfireTimer();
		        	}
					isConnecting=false;
				}
        
        	connect_sync_flag.notify();
        	}
        	}
	}
	private class PingOpenfireTask extends TimerTask
	{

		@Override
		public void run() {
			
		//	Log.d("lichao"," ping to openfire");
		 
			if(!NetworkManager.IsNetworkOk(context))
			{
				sendConnectionStatus(false);
				return;
			}
        	Ping ping = new Ping();  
            ping.setType(Type.GET);  
            mPingID = ping.getPacketID();// 此id其实是随机生成，但是唯一的   
         
            if(xmppConnection.isConnected())
            	xmppConnection.sendPacket(ping);// 发送ping消息 
            
            
           // startTimer(timer_ping_timeout,new PingTimeoutTask(),TIMER_PING_TIME_OUT,true);
            startPingTimeoutTimer();
        	}
	}
		
	private class PingTimeoutTask extends TimerTask
	{

		@Override
		public void run() {
			
			sendConnectionStatus(false);
			
			stopPingOpenfireTimer();
			startConnectToOpenfireTimer();
        	timer_ping_timeout=null;
        	}
	}
	public class MyPacketListener implements PacketListener {

		@Override
		public void processPacket(Packet packet) {
			if (packet == null)  
                return;  

            if (packet.getPacketID().equals(mPingID)) 
            {
            //	Log.d("lichao","recv ping response packet");
            	sendConnectionStatus(true);
            	stopPingTimeoutTimer();
            }
			
		}

	}
	public class MyConnectionListener implements ConnectionListener {

		@Override
		public void connectionClosed() {
		
			Log.d("lichao","connectionClosed");
			sendConnectionStatus(false);

		}

		@Override
		public void connectionClosedOnError(Exception arg0) {
			Log.d("lichao","connectionClosedOnError "+arg0.toString());
			sendConnectionStatus(false);

		}

		@Override
		public void reconnectingIn(int arg0) {
			
			Log.d("lichao","reconnectingIn");
			sendConnectionStatus(false);

		}

		@Override
		public void reconnectionFailed(Exception arg0) {
			Log.d("lichao","reconnectionFailed");
			sendConnectionStatus(false);


		}

		@Override
		public void reconnectionSuccessful() {
			
			Log.d("lichao","reconnectionSuccessful");
			sendConnectionStatus(true);

		}

	}
	public class MyChatManagerListener implements ChatManagerListener {

		@Override
		public void chatCreated(Chat chat, boolean arg1) {
			chat.addMessageListener(new MessageListener(){  
	               

				

				@Override
				public void processMessage(Chat arg0,
						org.jivesoftware.smack.packet.Message msg) {

				
					Log.d("lichao","from is "+msg.getFrom());
					/*if(!msg.getFrom().contains("@"))
					{
						notice_intent.putExtra("isService", true);
						notice_intent.putExtra("notice", msg.getBody());
						sendNotify(3,"您有1条通知，点击查看",notice_intent);
						return;
					}*/
					//if(!myName.equals(msg.getFrom()))
					{
						Intent intent = new Intent("netradar.bd.newlukuang"); 
						intent.putExtra("type", "lukuang");
						intent.putExtra("body", msg.getBody());
						intent.putExtra("from", msg.getFrom());
		   			 
						AudioLukuangInfo info=getInfo(msg.getBody());
						if(info!=null)
						{
							AudioLukuangManager.AddLukuang(context.getApplicationContext(), info);
							
							if(!isRunningLukuang())
							{
								SharedPreferences pre= getSharedPreferences("carweibo_unread_tips", Context.MODE_PRIVATE);
							
								Editor editor = pre.edit();
								
								unreaded_lukuang_sum=getLukuangSum()+1;
								editor.putInt("unreaded_lukuang", unreaded_lukuang_sum);
								editor.commit();
								
				        	//	sendNotify(2,"您有"+unreaded_lukuang_sum+"条语音路况，点击收听",lukuang_intent);
							}
							sendBroadcast(intent); 
						}
					}
					
				}  }); 

		}

		protected AudioLukuangInfo getInfo(String body) {
					
			
			try {
			
				JSONObject js=new JSONObject(body);
				AudioLukuangInfo lukuang=new AudioLukuangInfo();
				
				lukuang.file_name=js.getString("filename");
				lukuang.msg_status=AudioLukuangInfo.MSG_STATUS.OK;
				lukuang.isLocal=false;
				lukuang.isUnRead=true;
				lukuang.period=js.getInt("period");
				String length="";
				
				for(int i=0;i<lukuang.period;i++)
				{
					length=length+"   ";
				}
				lukuang.lukuang_length=length;
				
				if(js.getString("nickname").equals("NOLOGIN")||js.getString("nickname").equals("NOUSER"))
				{
					lukuang.isAnoymous=true;
					lukuang.sender_nickname="NOUSER";
				}
				else
				{
					lukuang.isAnoymous=false;
					lukuang.sender_nickname=js.getString("nickname");
				}
					
				lukuang.isPlaying=false;
				lukuang.isDownloading=false;
				lukuang.isFinishDown=false;
				
				
				lukuang.send_time=js.getLong("send_time");
				return lukuang;
				
			} catch (JSONException e) {
				Log.d("lichao","ServerService json error "+e.toString());
				return null;
			}
			
		}

	}
	
	
	boolean connection_status=false;
	
	static int  TIMER_GET_TIMER_INFO=15*60;
	static int  TIMER_GET_NEW_WEIZHANG=24*60*60;
	static int  TIMER_CONNECT_TO_OPENFIRE=5;
	static int  TIMER_PING_OPENFIRE=10;
	static int  TIMER_PING_TIME_OUT=5;
	boolean is_connecting;
	
	boolean isStart=false;
	long weibo_id=-1;
	Handler handler;
	int msg_sum=0;
	int unreaded_lukuang_sum=0;
	LoginToServer login_server;
	static Context context;
	static Intent msg_intent,lukuang_intent,notice_intent,query_intent;
	
	
	XMPPConnection xmppConnection;
	String myName;
	boolean isLoginToOpenfire=false;
	
	
	
	MyConnectionListener conn_listener;
	
	Timer timer_connect_to_openfire,timer_ping_openfire,timer_ping_timeout;
	TimerTask task_ping_openfire,task_ping_timeout,task_connect_to_openfire;
	
	String mPingID=null;
	PacketListener mPongListener;
	
	
	public class LocalBinder extends Binder { 
		ServerService getService() { 
                return ServerService.this; 
        } 
	} 

	private final IBinder mBinder = new LocalBinder(); 

	
	@Override
	public void onCreate() {
		
		super.onCreate();
		handler=new Handler(this);
		login_server=new LoginToServer();
		context=this;
		
		conn_listener=new MyConnectionListener();
		mPongListener=new MyPacketListener();
		msg_intent=new Intent();
		msg_intent.setClass(ServerService.this, MyMessageList.class);
		msg_intent.putExtra("isService", true);
		
		lukuang_intent=new Intent();
		lukuang_intent.setClass(ServerService.this, AudioLukuang.class);
		lukuang_intent.putExtra("isService", true);
		
		notice_intent=new Intent();
		notice_intent.setClass(ServerService.this, Score.class);
		query_intent=new Intent();
		query_intent.setClass(ServerService.this, Query.class);
		
		
		 Timer timer = new Timer();  
		 timer_connect_to_openfire=null;//new Timer();  
		 timer_ping_openfire=null;//new Timer();
		 timer_ping_timeout=null;//new Timer();
		 TimerTask task_get_timer_info = new TimerTask(){   
		        public void run() {  
		        	
		        	synchronized (login_server)
		        	{
			        	String nickname=UserManager.getCurUser(context.getApplicationContext());
			        	
			        	//if(nickname.equals("NOUSER")||nickname.equals("NOLOGIN")) return;
			        	
			        	String ret_string=login_server.GetTimerInfo(context, nickname);
			        	
			        	int ret=updateDB(ret_string);
			        	if(ret==-1)
			        		return;
			        
			        	
			        	if(ret!=0&&msg_sum!=0)
			        	{
			        		clearNotify(1);
			        		sendNotify(1,"您有"+msg_sum+"条新私信，点击查看",msg_intent);
			        		}
			        	Message message = new Message();      
			            message.what = 0;      
			            handler.sendMessage(message);  
			            
			            login_server.notify();
		        	}
		            
		              }            
		    };  
		    TimerTask task_new_weizhang = new TimerTask(){   
		        public void run() {  
		        	List<String> car_list=getCarList();
		        	if(car_list==null||car_list.isEmpty())
		        		return;
		        	int weizhang_num=0;
					for(int i=0;i<car_list.size();i++)
					{
						weizhang_num=0;
						if(car_list.get(i).startsWith("A"))
							weizhang_num=getWeizhangNum(car_list.get(i).substring(1));
						else
							weizhang_num=getWeizhangNum(car_list.get(i).substring(1));
						
						
						weizhang_num=weizhang_num-getHistoryNum(car_list.get(i));
						if(weizhang_num<=0) break;
						
						sendNotify(3,"您的陕"+car_list.get(i)+"新增"+weizhang_num+"个违章，点击查看",query_intent);
						
					}
		            
		              }            
		    };  
		 /*   task_connect_to_openfire = new TimerTask()
		    {   
		        public void run()
		        {  
		        	synchronized (connect_sync_flag)
		        	{
		        	
		        	if(connectionToOpenfire())
		        		timer_connect_to_openfire.cancel();
		        		
		        
		        	connect_sync_flag.notify();
		        	}
		        	}
		    };  
		
		    task_ping_openfire = new TimerTask()
		    {   
		        public void run()
		        {  
		        	Log.d("lichao"," ping to openfire");
		        	Ping ping = new Ping();  
		            ping.setType(Type.GET);  
		            ping.setTo(PreferenceUtils.getPrefString(mService,  
		                    PreferenceConstants.Server, PreferenceConstants.GMAIL_SERVER));  
		        
		            mPingID = ping.getPacketID();// 此id其实是随机生成，但是唯一的   
		         
		            xmppConnection.sendPacket(ping);// 发送ping消息 
		            
		            timer_ping_timeout= new Timer();
		            timer_ping_timeout.schedule(task_ping_timeout,1000*TIMER_PING_TIME_OUT );
		       	}
		    };  
		    task_ping_timeout = new TimerTask()
		    {   
		        public void run()
		        {  
		        	Log.d("lichao"," ping error!!!!!!!!!!!!!");
		       	}
		    }; */
	//	//timer.schedule(task,0,1000*2);
		timer.schedule(task_get_timer_info,0,1000*TIMER_GET_TIMER_INFO);
	//	timer_connect_to_openfire.schedule(task_connect_to_openfire, 0,1000*TIMER_CONNECT_TO_OPENFIRE);

	//	startConnectToOpenfireTimer();
		}

	protected int getHistoryNum(String num) {
			
		DBUility dbUility=(DBUility)getApplicationContext();
    	
    	DBhelper db=dbUility.getDB();
		Cursor c=db.queryItem(dbUility.CAR_TABLE,"num", num);
		if(!c.moveToFirst()) 
		{
			
			return 0;
		}
		
		try {
			JSONObject js=new JSONObject(c.getString(c.getColumnIndex("car")));
			JSONArray ja=(JSONArray)js.getJSONArray("weiZhang");
			return ja.length();
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
	}

	protected int getWeizhangNum(String num) {
		Document doc;
		
		try {
			doc = Jsoup.connect("http://117.36.53.122:9082/wfjf/wfjf/wsjf.do?actiontype=wfcx&hpzl=02&hphm="+num).timeout(10000).get();
		} catch (IOException e) {
			
			return -1;
		}
		 String c=doc.toString();
		
		 if(c.contains("当前车辆没有未处理的违法记录"))
		 {
		
			 return 0;
		 }
		 if(c.contains("对不起没有查到对应条件的信息"))
		 {
			
			 return -1;
		 }
		 Elements elem = doc.select("form[id=form1]").select("tr[title]");
		 if(elem.isEmpty()) return 0;
		 
		 return elem.size();
	}

	private List<String> getCarList() {
		DBUility dbUility=(DBUility)getApplicationContext();
    	
    	DBhelper db=dbUility.getDB();
    	
    	
    	Cursor c=db.query(dbUility.CAR_TABLE);
		ArrayList<String> list=getCar(c);
		
		return list;
	}

	private ArrayList<String> getCar(Cursor c) {
		
    	ArrayList<String> list=new ArrayList<String>();
    	while(c.moveToNext())
    	{
    		list.add(c.getString(c.getColumnIndex("num")));
		}
    	
		return list;
		
		
	}

	private void startConnectToOpenfireTimer()
	{
		
		if(timer_connect_to_openfire==null)
		{
			timer_connect_to_openfire=new Timer();
			timer_connect_to_openfire.schedule(new ConnectToOpenfireTask(), 0,1000*TIMER_CONNECT_TO_OPENFIRE);

		}
		
		
	}
	
	private void stopConnectToOpenfireTimer()
	{
		timer_connect_to_openfire.cancel();
		timer_connect_to_openfire=null;
	}
	
	private void startPingOpenfireTimer()
	{
		
		if(timer_ping_openfire==null)
		{
			timer_ping_openfire=new Timer();
			timer_ping_openfire.schedule(new PingOpenfireTask(), 0,1000*TIMER_PING_OPENFIRE);

		}
		
		
	}
	
	private void stopPingOpenfireTimer()
	{
		timer_ping_openfire.cancel();
		timer_ping_openfire=null;
	}
	private void startPingTimeoutTimer()
	{
		
		if(timer_ping_timeout==null)
		{
			timer_ping_timeout=new Timer();
			timer_ping_timeout.schedule(new PingTimeoutTask(),1000*TIMER_PING_TIME_OUT);

		}
		
		
	}
	
	private void stopPingTimeoutTimer()
	{
		timer_ping_timeout.cancel();
		timer_ping_timeout=null;
	}
	@Override
	public IBinder onBind(Intent arg0) {
		
		
		return mBinder;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		
		super.onStart(intent, startId);


		
	//	connectionToOpenfire();
		
	}
	private boolean isRunningLukuang()
	 {
		 SharedPreferences pre= getSharedPreferences("carweibo_unread_tips", Context.MODE_PRIVATE);
			
		return pre.getBoolean("is_running_lukuang", false);
	 }
	public void connectToOpenFireNow()
	{
		new Thread(){

			@Override
			public void run() {
				synchronized (connect_sync_flag)
		    	{
					Log.d("lichao","connectToOpenFireNow");
			    	connectionToOpenfire();
			    	connect_sync_flag.notify();
		    	
		    	}
			}
			
		
		}.start();
		
	}
	public boolean isConnectedToOpenfire()
	{
		
		return connection_status;
		
	}
	
	//
	private boolean connectionToOpenfire() {
		Log.d("lichao","ServerService connectionToOpenfire");
		
	//	if(isConnecting) return false;		
	//	isConnecting=true;
		connection_status=false;
		sendConnectionStatus(false);
		if(!NetworkManager.IsNetworkOk(this))
		{
			Log.d("lichao","ServerService network is not ok");
			return false;
		}
		/*if(((DBUility)this.getApplication()).connection_openfire!=null)
		{
			if(((DBUility)this.getApplication()).connection_openfire.isConnected())
				return;
			sendConnectionStatus(false);
			//return;
		}*/
	
		Log.d("lichao","ServerService connectionToOpenfire ing++++++++++++");
		
		
		int port=5222;
		ConnectionConfiguration config = new ConnectionConfiguration(((DBUility)this.getApplication()).openfire, port,((DBUility)this.getApplication()).openfire_domain);
		config.setSASLAuthenticationEnabled(false);
		config.setDebuggerEnabled(false);
		
		
	//	if(null==((DBUility)this.getApplication()).connection_openfire)
		if(((DBUility)this.getApplication()).connection_openfire!=null)
			((DBUility)this.getApplication()).connection_openfire.disconnect();
		((DBUility)this.getApplication()).connection_openfire= new XMPPConnection(config);
		xmppConnection=((DBUility)this.getApplication()).connection_openfire;
		try
		{
			
			xmppConnection.disconnect();
			
			
			xmppConnection.connect();
			xmppConnection.addPacketListener(mPongListener, new PacketTypeFilter(  
	                IQ.class));// 
		//	xmppConnection.addConnectionListener(conn_listener);
			if(xmppConnection.isAuthenticated())
			{
				Log.d("lichao","ServerService isAuthenticated true");
			}
			else
			{
				
				xmppConnection.loginAnonymously();
				   
				myName=xmppConnection.getUser();
				
				((DBUility)this.getApplication()).chatManager = ((DBUility)this.getApplication()).connection_openfire.getChatManager();
				((DBUility)this.getApplication()).chatManager.addChatListener(new MyChatManagerListener());
		
			}
			connection_status=true;
			
			sendConnectionStatus(true);
	   
			
			
			Log.d("lichao","ServerService name is "+xmppConnection.getUser());
			
			
			return true;
		} catch (XMPPException e) {
			
			sendConnectionStatus(false);
			Log.d("lichao","ServerService xmpp connect error");
			return false;
		}
		
	}

	private void sendConnectionStatus(boolean isConnected)
	{
		Intent intent = new Intent("netradar.bd.newlukuang"); 
		intent.putExtra("type", "connect");
		intent.putExtra("isConnected", isConnected);
	 
	 	sendBroadcast(intent); 
	}
	private int getMsgSum()
	{
		SharedPreferences pre= getSharedPreferences("carweibo_unread_tips", Context.MODE_PRIVATE);
		
		return pre.getInt("msg_sum", 0);
	}
	private int getLukuangSum()
	{
		SharedPreferences pre= getSharedPreferences("carweibo_unread_tips", Context.MODE_PRIVATE);
		
		return pre.getInt("unreaded_lukuang", 0);
	}
	protected int updateDB(String ret_string) {
		
		int length=0;
		
		if(ret_string==null||ret_string.length()==0||ret_string.equals("error_data"))
			return -1;
		
		try {
			JSONObject json=new JSONObject(ret_string);
			
			UserManager.updateUserScore(this.getApplicationContext(), URLDecoder.decode(json.getString("msg_nickname"),"UTF-8"), json.getInt("user_score"));

			//Log.d("lichao",json.toString());
			JSONArray ja=json.getJSONArray("returnTimerInfo_list");
			length=ja.length();
			for(int i=0;i<length;i++)
			{
				MessageInfo msg=new MessageInfo();
				msg.setFrom_user_id(ja.getJSONObject(i).getInt("from_user_id"));
				msg.setFrom_nickname(ja.getJSONObject(i).getString("from_nickname"));
				msg.setFrom_touxiang(ja.getJSONObject(i).getString("from_touxiang_url"));
				msg.setFrom_user_score(ja.getJSONObject(i).getInt("user_score"));
				
				msg.setTo_nickname(json.getString("msg_nickname"));
				msg.setMsg(ja.getJSONObject(i).getString("msg"));
				msg.setTime(ja.getJSONObject(i).getString("time"));
				msg.setMsg_type(ja.getJSONObject(i).getInt("msg_type"));
				msg.setRef_weibo(ja.getJSONObject(i).getString("ref_weibo"));
				msg.setWeibo_id(ja.getJSONObject(i).getLong("weibo_id"));
				msg.setTop(ja.getJSONObject(i).getBoolean("top"));
				msg.setMsg_status(MSG_STATUS.OK);
				
				MessageManager.AddMessage(this.getApplicationContext(), msg);
			}
			
			msg_sum=getMsgSum()+length;
			SharedPreferences pre= getSharedPreferences("carweibo_unread_tips", Context.MODE_PRIVATE);
			Editor editor = pre.edit();
			
			editor.putLong("max_weiboid", json.getLong("last_weibo_id"));
			
			editor.putInt("msg_sum", msg_sum);
			editor.commit();
			
			NvManager.setNVLong(this, NvManager.MAX_VENDER_ID, json.getLong("last_vender_id"));
			
		} catch (JSONException e) {
			Log.d("lichao","service json error:"+e.toString());
			return -1;
		} catch (UnsupportedEncodingException e) {
			Log.d("lichao","service json error:"+e.toString());
		}
		
		return length;
	}

	@Override
	public void onDestroy() {
		
		super.onDestroy();
		
	}

	@Override
	public boolean handleMessage(Message arg0) {
		if(arg0.what==0)
		{
			 Intent intent = new Intent("netradar.bd"); 
			 intent.putExtra("type", 4);
			 
			 sendBroadcast(intent); 
		}
		return false;
	}
	
	 public void refreshNow() 
	 {
		 new Thread(){

			@Override
			public void run() {
				
				synchronized (login_server)
	        	{
		        	String nickname=UserManager.getCurUser(context.getApplicationContext());
		        	
		        	if(nickname.equals("NOUSER")||nickname.equals("NOLOGIN")) return;
		        	
		        	String ret_string=login_server.GetTimerInfo(context, nickname);
		        	
		        //	Log.d("lichao","ServerSevice ret_string "+ret_string);
		        	int ret=updateDB(ret_string);//==-1) 
		        	if(ret==-1)
		        	{
		        		Intent intent = new Intent("netradar.bd"); 
		   			 	intent.putExtra("type", 4);
		   			 
		   			 	sendBroadcast(intent); 
		        		return;
		        	}
		      
		        	if(ret!=0&&msg_sum!=0)
		        	{
		        		clearNotify(1);
		        		sendNotify(1,"您有"+msg_sum+"条新私信，点击查看",msg_intent);
		        		}
		        	Message message = new Message();      
		            message.what = 0;      
		            handler.sendMessage(message);  
		            
		            login_server.notify();
	        	}
			}
			 
			 
		 }.start();
	 }
	 public void clearNotification()
	 {
		 msg_sum=0;
		 clearNotify(1);
	 }
	 
		public static void sendNotify(int id,String str,Intent intent)
		{
			
			if(context==null) return;
		   	String ns = Context.NOTIFICATION_SERVICE;
		   	
		   	NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(ns);



		   	CharSequence tickerText = str;

		   	long when = System.currentTimeMillis();

		   	CharSequence contentTitle = "西安车友圈";

		   	CharSequence contentText = str;

		  // 	Intent notificationIntent = new Intent(this, MyClass.class);

		   	PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
		   	

		   	Notification notification = new Notification(R.drawable.icon_lanuch1, tickerText, when);
		   
		   	notification.defaults |= Notification.DEFAULT_LIGHTS;

		   	notification.ledARGB = 0xff00ff00;

		   	notification.ledOnMS = 500;

		   	notification.ledOffMS = 300;

		   	notification.flags |= Notification.FLAG_SHOW_LIGHTS|Notification.FLAG_AUTO_CANCEL;

		   	notification.setLatestEventInfo(context.getApplicationContext(), contentTitle, contentText ,contentIntent);
		 
		   	
		   	mNotificationManager.notify(id, notification);
		   	

		}
		public static void clearNotify(int id)
		{
			if(context==null) return;
		   	String ns = Context.NOTIFICATION_SERVICE;

		   	NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(ns);


		   	mNotificationManager.cancel(id);
			
		}
	
}
