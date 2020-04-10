package heart4.netradar.lichao;

import carweibo.netradar.lichao.R;
import carweibo.netradar.lichao.ScreenShoot;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class Heart4MainActivity extends Activity {

	

	int currentViewIndex=0;
	HallView hall_view=null;
	MySurfaceView surface_view=null;
	GameView game_view=null;
	String nickname;
	String touxiang;
	int user_coins;
	String notice;
	
	Bitmap user_touxiang;
	Bitmap no_touxiang;
	
	GestureDetector gd;
	
	
	boolean isAnonymous=false;
	BroadcastReceiver bdReceiver;
	
	int roomIndex,deskIndex,seatIndex;
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		Log.d("lichao","heart4 onCreate");
		super.onCreate(savedInstanceState);
		nickname=this.getIntent().getStringExtra("nickname");
		touxiang=this.getIntent().getStringExtra("touxiang");
		String sdDir=ScreenShoot.getSDDir(this);
		
		no_touxiang=BitmapFactory.decodeResource(getResources(), R.drawable.switchuser);
		if(sdDir==null||nickname.equals("NOUSER")||nickname.equals("NOLOGIN"))
		{
			user_touxiang=no_touxiang;
			nickname="user"+System.currentTimeMillis();
			isAnonymous=true;
		}
		else
		{
			user_touxiang=BitmapFactory.decodeFile(touxiang);
			if(user_touxiang==null)
			{
				user_touxiang=no_touxiang;
				
			}
		}
			
		surface_view=new MySurfaceView(this,this);
		
//		hall_view=new HallView(this,this);
//		game_view=new GameView(this,this);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setContentView(surface_view);
		/*
		
		
		  bdReceiver=new BroadcastReceiver() {  
    		  
	    	    @Override  
	    	    public void onReceive(Context context, Intent intent) {  
	    	        
	    	    	processServerData(intent);
	    	    	
	    	    }

				
	    	  
	    	}; 
	    	ifilter= new IntentFilter(); 
	    	ifilter.addAction("heart4.netradar.lichao");
	    	registerReceiver(bdReceiver,ifilter);*/
	    	
	    
		
	//	startGameView(1);
		
		
	}

	
	@Override
	protected void onDestroy() {
	//	unregisterReceiver(bdReceiver);
	
		super.onDestroy();
	}


	protected void processServerData(Intent intent) {
		
		
	}

	@Override
	protected void onResume() {
		if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
			  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			 }
		super.onResume();
	}
	/*Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
				case 0:
				//	setContentView(start_view);
					break;
				case 1:
					currentViewIndex=1;
					//startHallView();
				//	startGameView(msg.arg1);
					break;
				case 2:
					currentViewIndex=2;
					startGameView(msg.arg1);
					break;
				case 3:
					exitGame();
					break;
				case 4:
					currentViewIndex=1;
				
					startHallView();
					
					break;
					
					
			}
		}

	};*/

	@Override
	public void onBackPressed() {
		
		if(surface_view.backPressed()) return;
		surface_view.network.exit();
		this.finish();
	}

	protected void startGameView(int index) {
/*		if(game_view==null)
			game_view=new GameView(this,this);
		
		game_view.room_index=index;
		setContentView(game_view);
*/		
	}

	protected void startHallView() {
/*		if(hall_view==null)
			hall_view=new HallView(this,this);
		setContentView(hall_view);
*/		
	}

	protected void exitGame() {
		this.finish();
		
	}

	public void showToast(String string) {

		Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
		
	}
/*
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		
		return false;
	}
*/

	
}
