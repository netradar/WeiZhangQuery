package carweibo.netradar.lichao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carweibo.netradar.lichao.AudioLukuangInfo.MSG_STATUS;
import carweibo.netradar.lichao.ServerService.LocalBinder;





import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Handler.Callback;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class AudioLukuang extends Activity implements OnTouchListener, OnCancelListener, Runnable, Callback {

	boolean resumePlay=false,resumePlayNow=false;
	public class MyPhoneStateListener extends PhoneStateListener {

		/* (non-Javadoc)
		 * @see android.telephony.PhoneStateListener#onCallStateChanged(int, java.lang.String)
		 */
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch(state) {  
            case TelephonyManager.CALL_STATE_IDLE: //空闲   
            	
            	if(resumePlay)
            	{
            		isPaused=false;
            		mp.start();
            		resumePlay=false;
            	}
            	if(resumePlayNow)
            	{
            		mp_now.start();
            		resumePlayNow=false;
            	}
                break;  
            case TelephonyManager.CALL_STATE_RINGING: //来电   
            	if(mp.isPlaying())
            	{
            		mp.pause();
            		isPaused=true;
            		resumePlay=true;
            	}
            	if(mp_now.isPlaying())
            	{
            		mp_now.pause();
            		resumePlayNow=true;
            	}
                break;  
            case TelephonyManager.CALL_STATE_OFFHOOK: //摘机（正在通话中）  
            	if(mp.isPlaying())
            	{
            		mp.pause();
            		isPaused=true;
            		resumePlay=true;
            	}
            	if(mp_now.isPlaying())
            	{
            		mp_now.pause();
            		resumePlayNow=true;
            	}
                break;  
            }  

			super.onCallStateChanged(state, incomingNumber);
		}
		

	}

	public class PlayNowHandler implements Callback {

		@Override
		public boolean handleMessage(android.os.Message msg) {
			
			adapter.notifyDataSetChanged();
			
			if(msg.what==0)
			playNowAmr((AudioLukuangInfo)msg.obj);
			
			return false;
		}

	}

	String isPlayCompleted="COMPLETE";

	public class MyCompletePlay implements OnCompletionListener {

		@Override
		public void onCompletion(MediaPlayer mp) {
		
			mp.reset();
			current_playing_lukuang.isUnRead=false;
			current_playing_lukuang.isPlaying=false;
			AudioLukuangManager.updateLukuangList(context.getApplicationContext(), current_playing_lukuang.send_time, true, false);
			
			adapter.notifyDataSetChanged();
			playAmr();
		}

	}

	public class PlayRunnable implements Runnable {

		@Override
		public void run() {
			while(!play_thread_exit)
			{
				AudioLukuangInfo info=null;
				synchronized (play_buffer)
				{
					
					if(play_buffer.isEmpty()||play_buffer.size()==0)
						try {
							
							play_buffer.wait();
							continue;
						} catch (InterruptedException e) {
							
						}
					else
					{
						
						
						info=play_buffer.get(0);
						
						play_buffer.remove(0);
						
						play_buffer.notify();
						
						info.isPlaying=true;
						android.os.Message message = handler_play.obtainMessage(0,info);
						   
						handler_play.sendMessage(message);
						
					//	playAmr(info.file_name);
						
						
						Log.d("lichao","before isPlayCompleted wait");
						synchronized(isPlayCompleted)
						{
							Log.d("lichao","before com wait");
							try {
								isPlayCompleted.wait();
							} catch (InterruptedException e) {
								Log.d("lichao","InterruptedException error");
							}
							isPlayCompleted.notify();
							Log.d("lichao","after com wait");
						}
						info.isPlaying=false;
						info.isUnRead=false;
						
						android.os.Message message1 = handler_play.obtainMessage(1,info);
						   
						handler_play.sendMessage(message1);
					}
				}
				

			}
		}

	}

	public class PlayHandler implements Callback {

		@Override
		public boolean handleMessage(android.os.Message msg) {
			
			adapter.notifyDataSetChanged();
			return false;
		}

	}

	public class DownloadHandler implements Callback {

		@Override
		public boolean handleMessage(android.os.Message msg) {
			
			AudioLukuangInfo info=(AudioLukuangInfo) msg.obj;
			
			if(info.isFinishDown)
			{
				if(isListening_flag)
				{
					//synchronized (play_buffer)
					{
					
						play_buffer.add(info);
						playAmr();
					///	play_buffer.notify();
					}
				}
				AudioLukuangManager.updateLukuangList(context.getApplicationContext(), info.send_time, true, true);
			}
			else
			{
				adapter.notifyDataSetChanged();
			}
			return false;
		}

	}

	public class DownLoadRunnable implements Runnable {

		@Override
		public void run() {
			
			
			while(!download_thread_exit)
			{
				
				AudioLukuangInfo info=null;
				synchronized (download_buffer)
				{
					
					if(download_buffer.isEmpty()||download_buffer.size()==0)
					{
						
						try {
					
							
							download_buffer.wait();
							
							continue;
						} catch (InterruptedException e) {
							
						}
					}
					else
					{
						
						
						info=download_buffer.get(0);
						
						download_buffer.remove(0);
						
						download_buffer.notify();
						info.isFinishDown=downloadAMR(info.file_name);
						if(!info.isFinishDown)
							info.msg_status=MSG_STATUS.ERROR;
						else
							info.msg_status=MSG_STATUS.OK;
						android.os.Message message = handler_download.obtainMessage(0,info);
						   
						handler_download.sendMessage(message);
					}
				}
				
				
				
				
			}

			Log.d("lichao","audio download thread exit");
		}

	}

	int DIALOG_STATUS_RECORDING=0;
	int DIALOG_STATUS_RELEASE_TO_CANCEL=1;
	int DIALOG_STATUS_TOO_SHORT=2;
	int DIALOG_STATUS_LOADING=3;
	int DIALOG_STATUS_RESET=4;
	
	LinearLayout voice_rcd_hint_loading, voice_rcd_hint_rcding,
	voice_rcd_hint_tooshort,voice_rcd_hint_release_to_cancel;
	
	ImageView volume;
	
	LinearLayout speak_btn;
	ImageView speak_mic;
	TextView speak_text;
//	Button listen_btn;
	//ImageView listen_img;
	TextView listen_text;
	
	Handler handler_send;
	Handler handler_download;
	Handler handler_play;
	Handler handler_play_now;
	
	long startRecordTime;
	
	MediaPlayer mp,mp_now;
	
//	ProgressDialog pd;
	
	boolean isAuthCancel=false;
	Context context;
	Timer timer_volume;
	TimerTask timer_task_volume;
	Handler handler_volume;
	SoundMeter recorder;
	
	Thread sender_thread;
	Thread download_thread;
	Thread play_thread;
	
	
	List<AudioLukuangInfo> send_buffer;
	List<AudioLukuangInfo> play_buffer;
	List<AudioLukuangInfo> download_buffer;
	
	boolean send_thread_exit=false;
	boolean download_thread_exit=false;
	boolean play_thread_exit=false;
	
	boolean isConnectedToOpenfire;
	LoginToServer loginToServer;
	
	
	BroadcastReceiver bdReceiver;
	private IntentFilter ifilter;
	
	ListView lukuangListView;
	LuKuangAdapter adapter;
	List<AudioLukuangInfo> lukuang_list;
	protected ServerService bindService;
	private ServiceConnection service_conn;
	
	ProgressBar connecting_progress;
	
	MyCompletePlay myCompletePlay;
	
	public class AuthUserTask extends AsyncTask<String, Integer, String> {

		String nickname;
		boolean isAnonymous;
		@Override
		protected void onPostExecute(String result) {
			
	//		pd.dismiss();
			super.onPostExecute(result);
			
			if(isAuthCancel) return;
	//		pd.dismiss();
			
			processAuthResult(nickname,isAnonymous,result);
		}

		@Override
		protected String doInBackground(String... arg0) {
			
			isAuthCancel=false;
			nickname=arg0[0];
			int version=Integer.valueOf(arg0[1]);
			isAnonymous=Boolean.valueOf(arg0[2]);
			
		//	Log.d("lichao","nickname is "+nickname+" version is "+version+" isa is "+isAnonymous);
			return new LoginToServer().AuthUser(context, nickname, isAnonymous, version);
		}

	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		{
			SharedPreferences pre= getSharedPreferences("carweibo_unread_tips", Context.MODE_PRIVATE);
			Editor editor = pre.edit();
			
			
			editor.putInt("unreaded_lukuang", 0);
			editor.putBoolean("is_running_lukuang", true);
			editor.commit();
		}
		this.setContentView(R.layout.audio_lukuang_layout);
		initViews();
		initData();
		context=this;
		
		  bdReceiver=new BroadcastReceiver() {  
    		  
	    	    @Override  
	    	    public void onReceive(Context context, Intent intent) {  
	    	        
	    	    	processNewLukuang(intent);
	    	    	
	    	    }

				
	    	  
	    	}; 
	    	ifilter= new IntentFilter(); 
	    	ifilter.addAction("netradar.bd.newlukuang");
	    	registerReceiver(bdReceiver,ifilter);
	}

	
	public void playNowAmr(AudioLukuangInfo info) {

		Log.d("lichao","playNowAmr");
		String sdDir=ScreenShoot.getSDDir(this);
		if(!info.isFinishDown)
		{
			Toast.makeText(this, "未成功加载该条路况，无法播放", Toast.LENGTH_SHORT).show();
			return;
		}
		info.isPlaying=true;
		adapter.notifyDataSetChanged();
		if(info==current_playing_now_lukuang)
		{
			try {
				mp_now.setDataSource(sdDir+"/weizhangquery/recording/"+info.file_name);
				mp_now.prepare();
				   
		        mp_now.start();
		        mp_now.setOnCompletionListener(new OnCompletionListener(){

					@Override
					public void onCompletion(MediaPlayer arg0) {
						
						mp_now.reset();
						current_playing_now_lukuang.isPlaying=false;
						current_playing_now_lukuang.isUnRead=false;
						AudioLukuangManager.updateLukuangList(context.getApplicationContext(), current_playing_now_lukuang.send_time, true, false);
						
						adapter.notifyDataSetChanged();
						if(isListening_flag)
							playAmr();
					}});
			} catch (IllegalArgumentException e) {
				mp_now.reset();
				current_playing_now_lukuang.isPlaying=false;
				
				adapter.notifyDataSetChanged();
			} catch (IllegalStateException e) {
				current_playing_now_lukuang.isPlaying=false;
				
				adapter.notifyDataSetChanged();
				mp_now.reset();
			} catch (IOException e) {
				current_playing_now_lukuang.isPlaying=false;
				
				adapter.notifyDataSetChanged();
				mp_now.reset();
		}
		}
		
	}


	AudioLukuangInfo current_playing_lukuang=null;
	boolean isPaused=false;
	public void playAmr() {
		
		String sdDir=ScreenShoot.getSDDir(this);
		
		if(sdDir==null) return;
		if(isRecording) return;
		if(mp_now.isPlaying()) return;
		AudioLukuangInfo info=null;
	
		if(isPaused)
		{
			mp.start();
			isPaused=false;
			return;
		}
		if(play_buffer.isEmpty()||play_buffer.size()==0)
		{
			mp.reset();
			return;
		}
		if(mp.isPlaying())
		{
			return;
		
		}
			
		
		
		{
			
			
			info=play_buffer.get(0);
			
			play_buffer.remove(0);
		}
			try 
			{
				current_playing_lukuang=info;
				//Log.d("lichao","playAmr "+current_playing_lukuang.file_name);
				mp.setDataSource(sdDir+"/weizhangquery/recording/"+current_playing_lukuang.file_name);
				mp.prepare();
		   
		        mp.start();
		        current_playing_lukuang.isPlaying=true;
		        adapter.notifyDataSetChanged();
	
			} catch (IllegalArgumentException e) {
				Log.d("lichao","AudioLukuang IllegalArgumentException error:"+e.toString());
				mp.reset();
				//isPlayCompleted.notify();
			} catch (IllegalStateException e) {
				Log.d("lichao","AudioLukuang IllegalStateException error:"+e.toString());
				
				mp.reset();
				//isPlayCompleted.notify();
			} catch (IOException e) {
				Log.d("lichao","AudioLukuang IOException error:"+e.toString());
				mp.reset();
			//	isPlayCompleted.notify();
			}
			
			
			mp.setOnCompletionListener(myCompletePlay);
		
	}



	public boolean downloadAMR(String file_name) 
	{
		URL url;
		HttpURLConnection conn = null;
		try {
			url = new URL(((DBUility)this.getApplicationContext()).webapps+"/pic/recording/"+file_name);
	
		
			conn = (HttpURLConnection)url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			conn.connect();
		
			InputStream is = conn.getInputStream();
			
			String sd=ScreenShoot.getSDDir(this);
			if(sd==null)
			{
				Log.d("lichao","sd is null");
				return false;
			}
			File savedir = new File(sd+"/weizhangquery/recording");
			if(!savedir.exists()){
				savedir.mkdirs();
			}
			
			File AmrFile = new File(sd+"/weizhangquery/recording/"+file_name);
			if(!AmrFile.exists())
			{
				AmrFile.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(AmrFile);
			
			
			byte buf[] = new byte[1024];
			
			int numread;
			while((numread=is.read(buf))>0){   		   		
	    		
	    	 		fos.write(buf,0,numread);
	    	}
			
			fos.close();
			is.close();
			if(conn!=null)
				conn.disconnect();
		
			return true;
		} catch (MalformedURLException e) {
			
			Log.d("lichao","audiolukuang MalformedURLException error:"+e.toString());
			
		} catch (IOException e) {
			Log.d("lichao","audiolukuang IOException error:"+e.toString());
			
		}
		if(conn!=null)
			conn.disconnect();
		return false;
	} 
		 
		





	protected void processNewLukuang(Intent intent) {
		
		if(intent==null)
		{
			
			return;
		}
		String type=intent.getStringExtra("type");
		
		if(type==null)
		{
			
			return;
		}
		
		if(type.equals("connect"))
		{
			connecting_progress.setVisibility(View.GONE);
			isReconnecting=false;
			boolean isConn=intent.getBooleanExtra("isConnected", false);
			
			
			{
				//isListening_flag=false;
				changeAudioLukuangStatus(isConn,isListening_flag);
			}
		}
		if(type.equals("lukuang"))
		{
			String body=intent.getStringExtra("body");
		
			
			try {
				JSONObject js=new JSONObject(body);
				AudioLukuangInfo lukuang=new AudioLukuangInfo();
				
				lukuang.file_name=js.getString("filename");
				lukuang.msg_status=MSG_STATUS.OK;
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
				
				lukuang.send_time=js.getLong("send_time");
				lukuang.send_time_s=getTime(lukuang.send_time);
				
				
				
				lukuang_list.add(lukuang);
				adapter.notifyDataSetChanged();
				
				
				if(isListening_flag)
				{
					
					synchronized (download_buffer)
					{
						lukuang.isDownloading=true;
						download_buffer.add(lukuang);
						download_buffer.notify();
					}
				}
				
			} catch (JSONException e) {
				return;
			}
			
		}
		
	}



	@Override
	protected void onDestroy() {
	
		Log.d("lichao","audio lukuang destroy");
		unregisterReceiver(bdReceiver);
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		Log.d("lichao","audio lukuang onPause");
		super.onPause();
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {

	//	Log.d("lichao","audio lukuang onResume");
		isListening_flag=true;
		changeAudioLukuangStatus(isConnectedToOpenfire,isListening_flag);
		clearNotify(2);
		super.onResume();
	}


	public void clearNotify(int id)
	{
		
	   	String ns = Context.NOTIFICATION_SERVICE;
	   	
	   	NotificationManager mNotificationManager = (NotificationManager)getApplicationContext().getSystemService(ns);


	   	mNotificationManager.cancel(id);
		
	}
	

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		
		super.onStart();
		sender_thread=new Thread(this);
		download_thread=new Thread(new DownLoadRunnable());
		send_thread_exit=false;
		download_thread_exit=false;
		sender_thread.start();
		download_thread.start();
		Log.d("lichao","audio lukuang onStart");
		service_conn = new ServiceConnection() 
		{

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				
				LocalBinder binder=(LocalBinder)service;
				bindService = binder.getService();
				Log.d("lichao","audio lukuang bindservice ");
		}

			@Override
			public void onServiceDisconnected(ComponentName name) {}
			
		};
		
		Intent i=new Intent();
		i.setClass(AudioLukuang.this, ServerService.class);
		//Log.d("lichao","audio lukuang bind service:"+this.getApplicationContext().bindService(i, service_conn, Context.BIND_AUTO_CREATE));
		this.getApplicationContext().bindService(i, service_conn, Context.BIND_AUTO_CREATE);
	}



	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		Log.d("lichao","audio lukuang onStop");
		exitLukuang();
		this.getApplicationContext().unbindService(service_conn);
		super.onStop();
	}



	public void processAuthResult(String nickname, boolean isAnonymous, String result) {
		
		
		if(result.equals("error_data"))
		{
			Toast.makeText(this, "网络或数据传输错误，请稍候重试！", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(result.equals("no_user"))
		{
			Toast.makeText(this, "您的帐号出现异常，无法使用该服务！", Toast.LENGTH_LONG).show();
			return;
		}
		
		try {
			JSONObject json=new JSONObject(result);
			boolean isVerOK=json.getBoolean("verOK");
			
			boolean isLuKuangPermit=json.getBoolean("luKuangPermit");
			if(!isVerOK)
			{
				Toast.makeText(this, "您的软件版本过低，无法使用语音路况功能，尽快升级版本吧～", Toast.LENGTH_LONG).show();
				return;
			}
			if(!isLuKuangPermit)
			{
				if(isAnonymous)
				{
					Toast.makeText(this, "没有登录～", Toast.LENGTH_LONG).show();
					return;
				}
				else
				{
					Toast.makeText(this, "您的帐号被禁止收听语音路况！", Toast.LENGTH_LONG).show();
					return;
				}
				
			}
			else
			{
				isListening_flag=true;
				changeAudioLukuangStatus(this.isConnectedToOpenFire(),isListening_flag);
			}
		} catch (JSONException e) {
			Toast.makeText(this, "网络或数据传输错误，请稍候重试！", Toast.LENGTH_SHORT).show();
			return;
		}
		
	}

	private void initData() {
		
		isConnectedToOpenfire=false;
		mp=new MediaPlayer();
		mp_now= new MediaPlayer();
		
		initRecorder();
		timer_volume=new Timer();
		timer_task_volume=new TimerTask()
		{
			 public void run() 
			 {  
				 updateVolumn();
			 }
		        	
		};
		
		handler_volume=new Handler();
		handler_play_now=new Handler(new PlayNowHandler());
		
		
	//	play_thread=new Thread(new PlayRunnable());
		
		handler_send=new Handler(this);
		handler_download=new Handler(new DownloadHandler());
	//	handler_play=new Handler(new PlayHandler());
		
		send_buffer=new ArrayList<AudioLukuangInfo>();
		download_buffer=new ArrayList<AudioLukuangInfo>();
		play_buffer=new ArrayList<AudioLukuangInfo>();
	
		
		
		
	//	play_thread.start();
		
		myCompletePlay=new MyCompletePlay();
		
		
		loginToServer=new LoginToServer();
		
		lukuang_list=new ArrayList<AudioLukuangInfo>();
		
		adapter=new LuKuangAdapter(this,lukuang_list);
		
		lukuangListView.setAdapter(adapter);
		
		getLukuangFromDB();
	/*	AssetManager asset=this.getAssets();
		AssetFileDescriptor afd;
		
		try {
			afd = asset.openFd("test.mp3");
			mp.setDataSource(afd.getFileDescriptor());
		} catch (IllegalArgumentException e) {
			Log.d("lichao","AudioLukuang IllegalArgumentException error:"+e.toString());
		} catch (IllegalStateException e) {
			Log.d("lichao","AudioLukuang IllegalStateException error:"+e.toString());
		} catch (IOException e) {
			Log.d("lichao","AudioLukuang IOException error:"+e.toString());
		}
		*/
		
		 TelephonyManager tpm = (TelephonyManager) this  
	                .getSystemService(Context.TELEPHONY_SERVICE);  
	       
	        tpm.listen(new MyPhoneStateListener(),  
	                PhoneStateListener.LISTEN_CALL_STATE); 
		
	}

	private void getLukuangFromDB() {
		
		String lukuang_str=AudioLukuangManager.getLukuangList(this.getApplicationContext());
		
		if(lukuang_str==null||lukuang_str.length()==0) return;
		try {
			JSONArray ja=new JSONArray(lukuang_str);
			
			
			for(int i=0;i<ja.length();i++)
			{
				AudioLukuangInfo lukuang=new AudioLukuangInfo();
				
				lukuang.file_name=ja.getJSONObject(i).getString("file_name");
				lukuang.msg_status=MSG_STATUS.OK;
				lukuang.isLocal=ja.getJSONObject(i).getBoolean("isLocal");
				lukuang.isUnRead=ja.getJSONObject(i).getBoolean("isUnRead");
				lukuang.period=ja.getJSONObject(i).getInt("period");
				
				lukuang.lukuang_length=ja.getJSONObject(i).getString("lukuang_length");
				
				
				{
					lukuang.isAnoymous=ja.getJSONObject(i).getBoolean("isAnoymous");;
					lukuang.sender_nickname=ja.getJSONObject(i).getString("sender_nickname");
				}
					
				lukuang.isPlaying=false;
				lukuang.isFinishDown=ja.getJSONObject(i).getBoolean("isFinishDown");
				lukuang.send_time=ja.getJSONObject(i).getLong("send_time");
				lukuang.send_time_s=getTime(lukuang.send_time);
				
				
				
				lukuang_list.add(lukuang);
			}
			adapter.notifyDataSetChanged();
			
			int size=lukuang_list.size();
			int jj;
			if(size>=5)
			{
				for(jj=(size-5);jj<size;jj++)
				{
					if(lukuang_list.get(jj).isUnRead)
					{
						synchronized (download_buffer)
						{
							lukuang_list.get(jj).isDownloading=true;
							download_buffer.add(lukuang_list.get(jj));
							download_buffer.notify();
						}
					}
				}
			}
			else
			{
				for(jj=0;jj<size;jj++)
				{
					Log.d("lichao","is is "+lukuang_list.get(jj).isUnRead);
					if(lukuang_list.get(jj).isUnRead)
					{
						synchronized (download_buffer)
						{
							lukuang_list.get(jj).isDownloading=true;
							download_buffer.add(lukuang_list.get(jj));
							download_buffer.notify();
						}
					}
				}
			}
			
		} catch (JSONException e) {
			Log.d("lichao","AudioLukuang JSON error "+e.toString());
		}
		
		
	}


	protected void updateVolumn()
	{
		double amr=recorder.getAmplitude();
		
		switch((int)amr)
		{
		case 0:
		case 1:
			volume.setImageResource(R.drawable.amp1);
			break;
		case 2:
		case 3:
			volume.setImageResource(R.drawable.amp2);
			
			break;
		case 4:
		case 5:
			volume.setImageResource(R.drawable.amp3);
			break;
		case 6:
		case 7:
			volume.setImageResource(R.drawable.amp4);
			break;
		case 8:
		case 9:
			volume.setImageResource(R.drawable.amp5);
			break;
		case 10:
		case 11:
			volume.setImageResource(R.drawable.amp6);
			break;
		default:
			volume.setImageResource(R.drawable.amp7);
			break;
		
		}
		
	}

	private void initRecorder() {
		
		recorder=new SoundMeter();
	}

	private void initViews() {
		speak_btn=(LinearLayout)findViewById(R.id.audio_lukuang_bottom);
		speak_mic=(ImageView)findViewById(R.id.audio_lukuang_speak_img);
		speak_text=(TextView)findViewById(R.id.audio_lukuang_speak_text);
//		listen_btn=(Button)findViewById(R.id.audio_lukuang_listen_btn);
//		listen_img=(ImageView)findViewById(R.id.audio_lukuang_listen_img);
		listen_text=(TextView)findViewById(R.id.audio_lukuang_listen_text);
		lukuangListView=(ListView)findViewById(R.id.audio_lukuang_listview);
		connecting_progress=(ProgressBar)findViewById(R.id.audio_lukuang_progressBar);
		
		
		voice_rcd_hint_loading=(LinearLayout)findViewById(R.id.voice_rcd_hint_loading);
		voice_rcd_hint_rcding=(LinearLayout)findViewById(R.id.voice_rcd_hint_rcding);
		voice_rcd_hint_tooshort=(LinearLayout)findViewById(R.id.voice_rcd_hint_tooshort);
		voice_rcd_hint_release_to_cancel=(LinearLayout)findViewById(R.id.voice_rcd_hint_release_to_cancel);
		
		volume=(ImageView)findViewById(R.id.voice_rcd_hint_volume);
		
		speak_btn.setOnTouchListener(this);
		changeDialogStatus(DIALOG_STATUS_RESET);
		
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		
		
		return false;
	}

	boolean isRecording=false;
	boolean isMoveOutside=false;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		int[] location = new int[2];
		speak_btn.getLocationInWindow(location);
		int width=speak_btn.getWidth();
		int height=speak_btn.getHeight();
		int btn_rc_Y = location[1];
		int btn_rc_X = location[0];
		
		int raw_X=(int) event.getRawX();
		int raw_Y=(int) event.getRawY();
	//	Log.d("lichao","y is "+event.getRawX()()+" btn_rc_Y is "+btn_rc_Y+" heigth is "+height);
	//	Log.d("lichao","x is "+event.getX()+" btn_rc_X is "+btn_rc_X+" width is "+width);
		switch(event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			if(raw_X>btn_rc_X&&raw_X<(btn_rc_X+width)&&raw_Y>btn_rc_Y&&raw_Y<(btn_rc_Y+height))	
			{
				if(!onStartRecord()) return true;
				isRecording=true;
				changeDialogStatus(DIALOG_STATUS_RECORDING);
				
				return true;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if(isRecording)
			{
				if(!(raw_X>btn_rc_X&&raw_X<(btn_rc_X+width)&&raw_Y>btn_rc_Y&&raw_Y<(btn_rc_Y+height)))	
				{
					changeDialogStatus(DIALOG_STATUS_RELEASE_TO_CANCEL);
					isMoveOutside=true;
					
				}
				else
				{
					if(isMoveOutside)
					{
						changeDialogStatus(DIALOG_STATUS_RECORDING);
						isMoveOutside=false;
					}
				}
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			
			if(isRecording)
			{
				if(!(raw_X>btn_rc_X&&raw_X<(btn_rc_X+width)&&raw_Y>btn_rc_Y&&raw_Y<(btn_rc_Y+height)))	
				{
					onCancelRecord();
					changeDialogStatus(DIALOG_STATUS_RESET);
					
				}
				else
				{
					changeDialogStatus(DIALOG_STATUS_RESET);
					onStopRecord();
					
				}
				
				isRecording=false;
				
				if(isListening_flag)
				{
					playAmr();
				}
				return true;
			}
			break;
		}
		
		return super.onTouchEvent(event);
	}

	
	private void onStopRecord() 
	{
		changeDialogStatus(DIALOG_STATUS_RESET);
		handler_volume.removeCallbacks(volumeRunnable);
		recorder.stop();
		XMPPConnection connection=((DBUility)this.getApplication()).connection_openfire;
		
		int period=(int) (System.currentTimeMillis()-startRecordTime);
		if(period<1000)
		{
			
			Toast.makeText(this, "录音时间太短", Toast.LENGTH_SHORT).show();
			
		}
		else
		{
			
			
			
			/*if(connection.isConnected())
			{*/
				sendAudioToServer(period);
				
			/*}
			else
			{
				Log.d("lichao","AudioLuKuang openfire connection error");
			}*/
		}
	}

	
	private void sendAudioToServer(int period) {
		
		AudioLukuangInfo lukuang=new AudioLukuangInfo();
		
		lukuang.file_name=recorder.file_name;
		lukuang.msg_status=MSG_STATUS.SENDING;
		lukuang.isLocal=true;
		lukuang.isUnRead=true;
		lukuang.period=period/1000;
		String length="";
		
		for(int i=0;i<lukuang.period;i++)
		{
			length=length+"   ";
		}
		lukuang.lukuang_length=length;
		String cur_user=UserManager.getCurUser(this.getApplicationContext());
		
		if(cur_user.equals("NOLOGIN")||cur_user.equals("NOUSER"))
		{
			lukuang.isAnoymous=true;
			lukuang.sender_nickname="NOUSER";
		}
		else
		{
			lukuang.isAnoymous=false;
			lukuang.sender_nickname=cur_user;
		}
			
		lukuang.isPlaying=false;
		
		lukuang.send_time=recorder.send_time;;
		lukuang.send_time_s=getTime(recorder.send_time);
		
		synchronized (send_buffer)
		{
			send_buffer.add(lukuang);
			send_buffer.notify();
		}
		
		lukuang_list.add(lukuang);
		
		adapter.notifyDataSetChanged();
		
	}

	private String getTime(long file_name) {
		 SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("HH时mm分");
		 Date   curDate   =   new   Date(file_name);   
		 return formatter.format(curDate);
		
	}

	private void onCancelRecord() 
	{
		recorder.stop();
		
	}

	private Runnable volumeRunnable=new Runnable(){

		@Override
		public void run() {
			
			updateVolumn();
			handler_volume.postDelayed(volumeRunnable, 300);
			
		}};
		
	
	private boolean onStartRecord() 
	{
		
		
			
		if(ScreenShoot.getSDDir(this)==null)
		{
			Toast.makeText(this, "没有检测到SD卡，无法使用上传语音功能", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(mp.isPlaying())
		{
			mp.pause();
			isPaused=true;
		}
		if(mp_now.isPlaying())
		{
			mp_now.reset();
			current_playing_now_lukuang.isPlaying=false;
			adapter.notifyDataSetChanged();
		}
		AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_SAME,AudioManager.FLAG_PLAY_SOUND);
	
		String dir=ScreenShoot.getSDDir(this)+"/weizhangquery/recording";
		
		File file=new File(dir);
		if(!file.exists())
			file.mkdirs();
		
		changeDialogStatus(DIALOG_STATUS_RECORDING);
		startRecordTime=System.currentTimeMillis();
		recorder.start(dir,startRecordTime);
		handler_volume.postDelayed(volumeRunnable, 300);
		return true;
		
	}
	
	/*protected void startRecord() 
	{
		if(!isRecording) return;
		startRecordTime=System.currentTimeMillis();
		String dir=ScreenShoot.getSDDir(this)+"/weizhangquery/recording";
		
		File file=new File(dir);
		if(!file.exists())
			file.mkdirs();
		
		changeDialogStatus(DIALOG_STATUS_RECORDING);
		recorder.start(dir+"/"+startRecordTime+".amr");
		
		
		//handler_volume.postDelayed(volumeRunnable, 300);
	}*/

	private void changeDialogStatus(int status)
	{
		
		
		if(status==DIALOG_STATUS_RECORDING)
		{
			speak_btn.setBackgroundResource(R.drawable.popupsdk_button_g_press);
			voice_rcd_hint_loading.setVisibility(View.GONE);
			voice_rcd_hint_rcding.setVisibility(View.VISIBLE);
			voice_rcd_hint_tooshort.setVisibility(View.GONE);
			voice_rcd_hint_release_to_cancel.setVisibility(View.GONE);
		}
		if(status==DIALOG_STATUS_RELEASE_TO_CANCEL)
		{
			speak_btn.setBackgroundResource(R.drawable.popupsdk_button_g_press);
			voice_rcd_hint_loading.setVisibility(View.GONE);
			voice_rcd_hint_rcding.setVisibility(View.GONE);
			voice_rcd_hint_tooshort.setVisibility(View.GONE);
			voice_rcd_hint_release_to_cancel.setVisibility(View.VISIBLE);
		}
		if(status==DIALOG_STATUS_TOO_SHORT)
		{
			speak_btn.setBackgroundResource(R.drawable.popupsdk_button_d);
			voice_rcd_hint_loading.setVisibility(View.GONE);
			voice_rcd_hint_rcding.setVisibility(View.GONE);
			voice_rcd_hint_tooshort.setVisibility(View.VISIBLE);
			voice_rcd_hint_release_to_cancel.setVisibility(View.GONE);
		}
		if(status==DIALOG_STATUS_LOADING)
		{
			speak_btn.setBackgroundResource(R.drawable.popupsdk_button_d);
			voice_rcd_hint_loading.setVisibility(View.VISIBLE);
			voice_rcd_hint_rcding.setVisibility(View.GONE);
			voice_rcd_hint_tooshort.setVisibility(View.GONE);
			voice_rcd_hint_release_to_cancel.setVisibility(View.GONE);
		}
		if(status==DIALOG_STATUS_RESET)
		{
			speak_btn.setBackgroundResource(R.drawable.popupsdk_button_d);
			voice_rcd_hint_loading.setVisibility(View.GONE);
			voice_rcd_hint_rcding.setVisibility(View.GONE);
			voice_rcd_hint_tooshort.setVisibility(View.GONE);
			voice_rcd_hint_release_to_cancel.setVisibility(View.GONE);
			volume.setImageResource(R.drawable.amp1);
		}
		
	}

	boolean isListening_flag=false;
	boolean isReconnecting=false;
	public void onStartListen(View v)
	{
		if(isReconnecting)
		{
			isReconnecting=false;
			connecting_progress.setVisibility(View.GONE);
			listen_text.setText("状态：未连接服务器！");
	//		listen_btn.setText("立即连接");
			return;
		}
		if(!isConnectedToOpenFire())
		{
			isReconnecting=true;
			connecting_progress.setVisibility(View.VISIBLE);
			listen_text.setText("状态：正在连接服务器...");
	//		listen_btn.setText("取消");
			bindService.connectToOpenFireNow();
			
			return;
		}
		if(!isListening_flag)
		{
			if(ScreenShoot.getSDDir(this)==null)
			{
				Toast.makeText(this, "没有检测到SD卡，无法使用语音路况功能", Toast.LENGTH_SHORT).show();
				return;
			}
			authUser();
		}
		else
		{
			if(mp.isPlaying())
			{
				current_playing_lukuang.isPlaying=false;
				current_playing_lukuang.isUnRead=true;
				adapter.notifyDataSetChanged();
			}
			isListening_flag=false;
			mp.stop();
			mp.reset();
			play_buffer.clear();
			changeAudioLukuangStatus(this.isConnectedToOpenFire(),isListening_flag);
			
			
		}
		 
		 
		 
		
	}
	
	private void authUser() {
		String nickname=UserManager.getCurUser(this.getApplicationContext());
		String isAnonymous="false";
		if(nickname.equals("NOLOGIN")||nickname.equals("NOUSER"))
		{
			isAnonymous="true";
		}
		PackageManager packageManager = getPackageManager();
        
		 PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(),0);
		} catch (NameNotFoundException e) {
			return;
		}
		 String version = packInfo.versionName.replace(".", "");
		 
		 new AuthUserTask().execute(nickname,version,isAnonymous);
		/*pd=new ProgressDialog(this);
		pd.setCancelable(true);
		pd.setMessage("正在启动。。。");
		pd.setCanceledOnTouchOutside(false);
		pd.setIndeterminateDrawable(this.getResources().getDrawable(R.drawable.progress));
		pd.setOnCancelListener(this);
		
		pd.show();*/
		
	}

	private void changeListenStatus(boolean isListening)
	{
		if(!isListening)
		{
			isListening=true;
	//		listen_btn.setBackgroundResource(R.drawable.btn_style_zero_pressed);
	//		listen_btn.setText("停止收听");
	//		listen_img.setBackgroundResource(R.drawable.talk_room_mic_speaking);
			listen_text.setTextColor(Color.GREEN);
			listen_text.setText("收听中...");
		}
		else
		{
			isListening=false;
	//		listen_btn.setBackgroundResource(R.drawable.btn_style_five_normal);
	//		listen_btn.setText("开始收听");
		//	listen_img.setBackgroundResource(R.drawable.talk_room_mic_idle);
			listen_text.setTextColor(Color.parseColor("000666"));
			listen_text.setText("暂停收听");
		}
	}

	@Override
	public void onCancel(DialogInterface arg0) {
		isAuthCancel=true;
		
	}

	@Override
	public void run() {
		AudioLukuangInfo info=null;
		while(!send_thread_exit)
		{
			
			synchronized (send_buffer)
			{
				
				if(send_buffer.isEmpty()||send_buffer.size()==0)
					try {
						
						send_buffer.wait();
						continue;
					} catch (InterruptedException e) {
						
					}
				else
				{
					
					
					info=send_buffer.get(0);
					
					send_buffer.remove(0);
					
					send_buffer.notify();
				}
			}
			
		
			{
			//	Log.d("lichao","before connect openfire");
			//	bindService.connectToOpenFireNow();
			//	Log.d("lichao","after connect openfire");
				int ret=0;
				if(!((DBUility)context.getApplicationContext()).connection_openfire.isConnected())
				{
					ret=-1;
				}
				else
				{
					String result=loginToServer.sendAudioLukuang(context,info.sender_nickname,info.isAnoymous,info.file_name);
					
					if(result.equals("error_data"))
					{
						ret=-1;
					}
					if(result.equals("user_not_exist"))
					{
						ret=-2;
					}
					if(result.equals("anonymous_not_permit"))
					{
						ret=-3;
					}
					if(result.equals("user_not_auth"))
					{
						ret=-4;
					}
					
					sendBroadcast(info,result);
				
				}
				 android.os.Message message = handler_send.obtainMessage(ret,info);
	           
	             handler_send.sendMessage(message);
			}
 
		
	}
		Log.d("lichao","audio send thread exit");
	}

	@Override
	public boolean handleMessage(android.os.Message arg0) {
		
		switch(arg0.what)
		{
		case 0:
			processUploadSuccess(((AudioLukuangInfo)arg0.obj).send_time,true);
			return false;
			
		case -1:
			Toast.makeText(this, "网络或数据错误，请检查网络或稍候重试", Toast.LENGTH_SHORT).show();
			break;
		case -2:
			Toast.makeText(this, "用户不存在！", Toast.LENGTH_SHORT).show();
			break;
		case -3:
			Toast.makeText(this, "您没有登录！", Toast.LENGTH_SHORT).show();
			break;
		case -4:
			Toast.makeText(this, "您的帐号权限不够，无法使用此项功能！", Toast.LENGTH_SHORT).show();
			break;
		}
		processUploadSuccess(((AudioLukuangInfo)arg0.obj).send_time,false);
		return false;
	}

	private void processUploadSuccess(long send_time,boolean isSuccess) {
		
		
		for(AudioLukuangInfo lukuang:lukuang_list)
		{
			
			if(lukuang.send_time==send_time)
			{
				if(isSuccess)
				{
					lukuang.msg_status=MSG_STATUS.OK;
					
					lukuang_list.remove(lukuang);
				}
				else
					lukuang.msg_status=MSG_STATUS.ERROR;
				adapter.notifyDataSetChanged();
				
				break;
			}
			
		}
	}

	AudioLukuangInfo current_playing_now_lukuang=null;

	
	public void onListenSel(View v)
	{
		final String sdDir=ScreenShoot.getSDDir(this);
		
		if(sdDir==null) return;
		if(isRecording) return;
		int pos=(Integer) v.getTag();
		final AudioLukuangInfo info=adapter.getItem(pos);
		
		if(info.isLocal){ return;}
		if(isListening_flag&&mp.isPlaying())
		{
			mp.pause();
			isPaused=true;
			if(current_playing_lukuang!=null)
			{
				current_playing_lukuang.isPlaying=false;
				adapter.notifyDataSetChanged();
			}
		}
		if(mp_now.isPlaying())
		{
			mp_now.reset();
			current_playing_now_lukuang.isPlaying=false;
			adapter.notifyDataSetChanged();
		}
		
		
		current_playing_now_lukuang=info;
		
		if(info.isFinishDown)
		{
			
			playNowAmr(info);
		}
		else
		{
			new Thread(){

				
				
				@Override
				public void run() {
					info.isDownloading=true;
					android.os.Message message = handler_play_now.obtainMessage(1,info);
					   
					handler_play_now.sendMessage(message);
					info.isFinishDown=downloadAMR(info.file_name);
					if(!info.isFinishDown)
						info.msg_status=MSG_STATUS.ERROR;
					else
					{
						info.msg_status=MSG_STATUS.OK;
						AudioLukuangManager.updateLukuangList(context.getApplicationContext(), current_playing_now_lukuang.send_time, true, true);
						
					}
					info.isDownloading=false;
					message = handler_play_now.obtainMessage(0,info);
					   
					handler_play_now.sendMessage(message);
					
					
					
				}
				
			}.start();
		
			}
		
		
	}

	private int sendBroadcast(AudioLukuangInfo info, String result) {
		
		XMPPConnection connection=((DBUility)this.getApplication()).connection_openfire;
		
		if(!connection.isConnected())
		{
			return -1;
		}
		 Message m = new Message();
		 
		 JSONObject json=new JSONObject();
		 
		 try
		 {
			JSONObject ret=new JSONObject(result);
			json.put("nickname", info.sender_nickname);
			json.put("send_time", info.send_time);
			json.put("period", info.period);
			json.put("filename", ret.getString("file_name"));
		} catch (JSONException e) {
			
			Log.d("lichao","AudioLuKuang json error:"+e.toString());
			return -1;
		}
		 
		 m.setBody(json.toString());
		 m.setTo("all@broadcast."+((DBUility)this.getApplication()).openfire_domain);
		 connection.sendPacket(m);
		return 0;
		
	}
	
	private boolean isConnectedToOpenFire()
	{
		XMPPConnection connection=((DBUility)this.getApplication()).connection_openfire;
		
		if(!connection.isConnected())
		{
			return false;
		}
		return true;
	}
	
	private void changeAudioLukuangStatus(boolean isConnected,boolean isListenning)
	{
		if(!isConnected)
		{
			isConnectedToOpenfire=false;
	//		listen_btn.setBackgroundResource(R.drawable.popupsdk_button_d);
	//		listen_btn.setTextColor(Color.parseColor("#000333"));
	//		listen_btn.setText("立即连接");
	//		listen_img.setVisibility(View.GONE);
			listen_text.setTextColor(Color.RED);
			listen_text.setText("状态：正在连接服务器...");
			speak_btn.setVisibility(View.GONE);
			connecting_progress.setVisibility(View.VISIBLE);
			return;
		}
		speak_btn.setVisibility(View.VISIBLE);
		if(isConnected&&!isListenning)
		{
			isListening_flag=false;
	//		listen_btn.setBackgroundResource(R.drawable.btn_style_five_normal);
	//		listen_btn.setTextColor(Color.WHITE);
	//		listen_btn.setText("开始收听");
	//		listen_img.setBackgroundResource(R.drawable.talk_room_mic_idle);
	//		listen_img.setVisibility(View.VISIBLE);
			listen_text.setTextColor(Color.parseColor("#000666"));
			listen_text.setText("状态：暂停收听");
			
		}
		else
		{
			isConnectedToOpenfire=true;
			isListening_flag=true;
	//		listen_btn.setBackgroundResource(R.drawable.btn_style_zero_pressed);
	//		listen_btn.setTextColor(Color.WHITE);
	//		listen_btn.setText("停止收听");
	//		listen_img.setVisibility(View.VISIBLE);
	//		listen_img.setBackgroundResource(R.drawable.talk_room_mic_speaking);
			listen_text.setTextColor(Color.parseColor("#229900"));
			listen_text.setText("状态：服务器连接正常！");
			connecting_progress.setVisibility(View.GONE);
		}
		
	}
	public void onCancelAudiolukuang(View v)
	{
		if(isListening_flag)
		{
			confirmExit();
			return;
		}
		this.finish();
		overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
	//	exitLukuang();
	}
	
	
	@Override
	public void onBackPressed() {
		if(isListening_flag)
		{
			confirmExit();
			return;
		}
		this.finish();
		overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
	//	exitLukuang();
	}


	private void  confirmExit()
	{
		Intent i=new Intent();
		
		
		
		i.putExtra("text", "提示\n\n"+"确定退出吗？");
		i.putExtra("ok", "确定");
		i.putExtra("cancel", "取消");
		i.setClass(AudioLukuang.this, Dialog.class);
		this.startActivityForResult(i,1);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==1&&resultCode==0)
		{
		//	exitLukuang();
			this.finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}


	private void exitLukuang()
	{
		if(isListening_flag&&mp.isPlaying())
		{
			current_playing_lukuang.isPlaying=false;
			AudioLukuangManager.updateLukuangList(this.getApplicationContext(), current_playing_lukuang.send_time, true, current_playing_lukuang.isUnRead);
			adapter.notifyDataSetChanged();
		}
		if(mp_now.isPlaying())
		{
			current_playing_now_lukuang.isPlaying=false;
			AudioLukuangManager.updateLukuangList(this.getApplicationContext(), current_playing_now_lukuang.send_time, true, current_playing_now_lukuang.isUnRead);
			adapter.notifyDataSetChanged();
		}
		send_thread_exit=true;
		download_thread_exit=true;
		isListening_flag=false;
		mp.reset();
		mp_now.reset();
		
		SharedPreferences pre= getSharedPreferences("carweibo_unread_tips", Context.MODE_PRIVATE);
		Editor editor = pre.edit();
		editor.putBoolean("is_running_lukuang", false);
		editor.commit();
		
		synchronized(send_buffer)
		{
			send_buffer.notify();
		}
		synchronized(download_buffer)
		{
			download_buffer.notify();
		}
		
	}
	
	public void onClearAll(View v)
	{
		lukuang_list.clear();
		adapter.notifyDataSetChanged();
		
		AudioLukuangManager.deleteLukuang(this.getApplicationContext());
	}
}
