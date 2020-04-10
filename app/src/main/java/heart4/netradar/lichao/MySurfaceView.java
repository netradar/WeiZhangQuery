package heart4.netradar.lichao;

import heart4.netradar.lichao.GameView.GameViewCallBack;
import heart4.netradar.lichao.HallView.HallViewCallBack;
import heart4.netradar.lichao.LoadingView.CallBackLoadingView;
import heart4.netradar.lichao.StartView.StartCallBack;
import android.util.Log;
import android.view.View.OnTouchListener;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback, OnTouchListener, HallViewCallBack, GameViewCallBack, GestureDetector.OnGestureListener, Callback, CallBackLoadingView{


	private static int SURFACE_VIEW_START=0;
	private static int SURFACE_VIEW_HALL=1;
	private static int SURFACE_VIEW_DESK=2;
	boolean threadFlag=true;
	boolean isLoaded=false;
	Canvas canvas;
	SurfaceHolder holder;
	Heart4MainActivity main_activity;
	Handler main_handler;

	int refresh_interval=50;
	int surfaceMode=SURFACE_VIEW_START;
	
	LoadingView loadingView=null;
	StartView 	startView=null;
	HallView  	hallView=null;
	GameView	gameView=null;
	
	GestureDetector gd;
	Handler handler_network;
	GameNetworkProcess network=null;
	
	public MySurfaceView(Context context,Heart4MainActivity main) {
		super(context);
		holder = getHolder();
		
		main_activity=main;
		
				
		this.getHolder().addCallback(this);
		this.setOnTouchListener(this);
		this.setClickable(true);
		gd= new GestureDetector(this);
		handler_network=new Handler(this);	
		
	}

	public class DrawRunnable implements Runnable 
	{

		@Override
		public void run() {
			


			long before_draw,after_draw;
			while (threadFlag) {
				try {
					canvas = holder.lockCanvas();
					synchronized (this) {
						before_draw=System.currentTimeMillis();
						if(canvas!=null)
							Draw(canvas);
						after_draw=System.currentTimeMillis();
					}
					
				} finally {
					if(canvas!=null)
						holder.unlockCanvasAndPost(canvas);
				}
				if((after_draw-before_draw)<refresh_interval)
				{
					try {
						Thread.sleep(refresh_interval-(after_draw-before_draw));
						
					
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		
		}
		
	}
	Thread GameThread;
	/*= new Thread() {
		@Override
		public void run() {

			long before_draw,after_draw;
			while (threadFlag) {
				try {
					canvas = holder.lockCanvas();
					synchronized (this) {
						before_draw=System.currentTimeMillis();
						if(canvas!=null)
							Draw(canvas);
						after_draw=System.currentTimeMillis();
					}
					
				} finally {
					if(canvas!=null)
						holder.unlockCanvasAndPost(canvas);
				}
				if((after_draw-before_draw)<refresh_interval)
				{
					try {
						Thread.sleep(refresh_interval-(after_draw-before_draw));
						
					
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

	};*/
	
	protected void Draw(Canvas canvas) {
		
		
		if(surfaceMode==SURFACE_VIEW_START)
		{
			if(loadingView==null)
			{
				
				loadingView=new LoadingView(main_activity,this);
			}
			loadingView.draw(canvas);
			return;
		}
		if(surfaceMode==SURFACE_VIEW_HALL)
		{
			if(hallView==null)
				hallView=new HallView(main_activity,this);
			hallView.draw(canvas);
			return;
		}
		if(surfaceMode==SURFACE_VIEW_DESK)
		{
			if(gameView!=null)
				gameView.draw(canvas);
			return;
		}
		
	}

	
	
	
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		
		
	}

	

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		threadFlag = true;
		GameThread=new Thread(new DrawRunnable());
		GameThread.start();
		if(network==null)
		//for(int i=0;i<50;i++)
			network=new GameNetworkProcess(handler_network,main_activity.nickname);
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		threadFlag = false;
		
		
	}
	





	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gd.onTouchEvent(event);
		return super.onTouchEvent(event);
	}




	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
	//	if(gd.onTouchEvent(event)) return false;
		
		
		if(surfaceMode==SURFACE_VIEW_START)
		{
			if(loadingView==null)
				loadingView=new LoadingView(main_activity,this);
			loadingView.touch(event);
			return false;
		}
		if(surfaceMode==SURFACE_VIEW_HALL)
		{
			if(hallView!=null)
				hallView.onTouch(event);
			return false;
		}
		if(surfaceMode==SURFACE_VIEW_DESK)
		{
			if(gameView==null)
				gameView=new GameView(main_activity,this);
			gameView.onTouch(event);
			return false;
		}
		

	
		return false;

	}





	/*@Override
	public void onStartGame() {
		
		
		if(gameView==null)
			gameView=new GameView(main_activity,this);
		surfaceMode=SURFACE_VIEW_DESK;
		
	}*/




	@Override
	public boolean onDown(MotionEvent event) {
	
		return false;
	}




	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}




	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}




	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public boolean onSingleTapUp(MotionEvent event) {
	
	//	Log.d("lichao","onSingleTapUp");
		if(surfaceMode==SURFACE_VIEW_START)
		{
			/*if(loadingView==null)
				return true;*/
			loadingView.click(event);
			return true;
		}
		if(surfaceMode==SURFACE_VIEW_HALL)
		{
			if(hallView==null)
				hallView=new HallView(main_activity,this);
			hallView.click(event);
			return true;
		}
		if(surfaceMode==SURFACE_VIEW_DESK)
		{
			if(gameView==null)
				gameView=new GameView(main_activity,this);
			gameView.click(event);
			return true;
		}
		return true;
	}





	@Override
	public boolean handleMessage(Message msg) {
		
		
		if(msg.what==Tools.CMD_CONNECT_SUCCESS)
		{
			network.login();
			return false;
		}
		if(msg.what==Tools.CMD_CONNECT_FAIL)
		{
			network.processDisconnect(false);
			return false;
		}
		
		
		if(surfaceMode==SURFACE_VIEW_START)
		{
			
			if(loadingView!=null)
				loadingView.handleMsg(msg);
			return false;
			
		}
		if(surfaceMode==SURFACE_VIEW_HALL)
		{
			
			if(hallView!=null)
				hallView.handleMsg(msg);
			return false;
			
		}
		if(surfaceMode==SURFACE_VIEW_DESK)
		{
			
			if(gameView!=null)
				gameView.handleMsg(msg);
			return false;
			
		}
		return false;
	}




	@Override
	public void onStartHall(int score, String notice) {
		
		main_activity.user_coins=score;
		main_activity.notice=notice;
		
		if(hallView==null)
			hallView=new HallView(main_activity,this);
		surfaceMode=SURFACE_VIEW_HALL;
		
		
	}




	@Override
	public void onEnterRoom(int roomIndex) {
		
		network.enterRoom(roomIndex);
	}




	@Override
	public void onStartGame(int roomIndex,int deskIndex,int seatIndex,String data) {
		
		main_activity.roomIndex=roomIndex;
		main_activity.deskIndex=deskIndex;
		main_activity.seatIndex=seatIndex;
		
		if(gameView!=null)
			gameView.destroy();
		gameView=new GameView(main_activity,this);
		gameView.initPlayers(seatIndex,data);
		
		surfaceMode=SURFACE_VIEW_DESK;
	}




	@Override
	public void onReconnect() {
		
		network=new GameNetworkProcess(handler_network,main_activity.nickname);
	}




	public boolean backPressed() {
		if(surfaceMode==SURFACE_VIEW_START)
		{
			
			return false;
			
		}
		if(surfaceMode==SURFACE_VIEW_HALL)
		{
			//hallView.backPressed();
			return false;
			
		}
		if(surfaceMode==SURFACE_VIEW_DESK)
		{
			
			gameView.backPressed();
			surfaceMode=SURFACE_VIEW_HALL;
			return true;
			
		}
		return false;
		
	}




	@Override
	public void playerExitGame(boolean isForceExit) {
		
		network.playerExitGame(isForceExit,main_activity.roomIndex,main_activity.deskIndex,main_activity.seatIndex);
		
	}




	@Override
	public void changeDesk() {
	
		network.changeDesk(main_activity.roomIndex,main_activity.deskIndex,main_activity.seatIndex);
		
	}




	@Override
	public void ready() {
		network.ready(main_activity.roomIndex,main_activity.deskIndex,main_activity.seatIndex);
		
	}




	@Override
	public void reConnectInGame() {
		
		network=new GameNetworkProcess(handler_network,main_activity.nickname);
	}




	@Override
	public void enterRoomInGame(int roomIndex) {
		network.reEnterRoom(main_activity.roomIndex,main_activity.deskIndex,main_activity.seatIndex);
		
	}




	@Override
	public void backToHallView() {
	
		if(hallView==null)
			hallView=new HallView(main_activity,this);
		surfaceMode=SURFACE_VIEW_HALL;
	}




	@Override
	public void callFriend(int selectedCard) {
		
		network.callFriend(main_activity.roomIndex,main_activity.deskIndex,main_activity.seatIndex,selectedCard);
		
	}




	@Override
	public void dropCard(PlayedCardInfo card) {
		
		network.dropCard(main_activity.roomIndex,main_activity.deskIndex,main_activity.seatIndex,card);
		
		
	}

}
