package heart4.netradar.lichao;

import heart4.netradar.lichao.AlertDialog.CallbackAlertDialog;
import heart4.netradar.lichao.CallFriendView.CallFirendCallBack;
import heart4.netradar.lichao.CountDialog.CallbackCountDialog;
import heart4.netradar.lichao.HostView.CallbackHostView;
import heart4.netradar.lichao.MessageDialog.CallbackMessageDialog;
import heart4.netradar.lichao.PlayerButton.ButtonPressed;
import heart4.netradar.lichao.SecondTimerTask.TimeoutCallback;
import heart4.netradar.lichao.SelectedCardView.SelectedCardCallBack;
import heart4.netradar.lichao.SettingDialog.CloseCallbackSettingDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;


import carweibo.netradar.lichao.AsyncImageLoader2;
import carweibo.netradar.lichao.AsyncImageLoader2.ImageCallback2;
import carweibo.netradar.lichao.AsyncImageLoader2.TextCallback2;
import carweibo.netradar.lichao.DBUility;

import android.text.TextPaint;
import android.util.Log;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class GameView implements ButtonPressed, CallFirendCallBack, CallbackCountDialog, CloseCallbackSettingDialog, CallbackAlertDialog, CallbackMessageDialog, CallbackHostView, ImageCallback2, TextCallback2, TimeoutCallback, SelectedCardCallBack {
	
	
	private static String escHint="您正在游戏中，如果选择退出，将会被扣除一定金币，确认退出吗？";

	private static int GAME_MODE_WAITTING_PLAYER_ENTER=1;
	private static int GAME_MODE_WAITTING_PLAYER_START_1=2;
	private static int GAME_MODE_WAITTING_PLAYER_START_2=3;
	private static int GAME_MODE_WAITTING_HOST_SELECT_PARTNER1=4;
	private static int GAME_MODE_WAITTING_PLAYER_DROP=5;
	private static int GAME_MODE_PLAYING=6;
	private static int GAME_MODE_OVER=7;
	private static int GAME_MODE_USER_EXIT=8;
	private static int GAME_MODE_SETTING=9;
	private static int GAME_MODE_MESSAGE=10;
	private static int GAME_MODE_HOST_VIEW=11;
	private static int GAME_MODE_ALERT=12;
	private static int GAME_MODE_CHANGE_DESK=13;
	private static int GAME_MODE_EXIT_ALERT=14;
	private static int GAME_MODE_SELECTED_CARD=15;
	private static int GAME_MODE_WAITTING_HOST_SELECT_PARTNER2=16;
	
	
	private static int BUTTON_PRESSED_OFFSET=3;
	
	boolean isSettingBtnPressed=false;
	boolean isMessageBtnPressed=false;
	boolean isDialogPopup=false;
	
	int gameMode=1;
	int room_index;
	boolean threadFlag=true;
	Canvas canvas;
	SurfaceHolder holder;
	ImagePosData background;
	ImagePosData blackback;
	ImagePosData setting_btn;
	ImagePosData message_btn;
	ImagePosData clock;
	ImagePosData play_mode;
	ImagePosData card_call;
//	ImagePosData ok_btn;
	
	
	Point background_point=new Point(0,0);
	Point blackback_point=new Point(0,0);
	Point setting_btn_point=new Point(733,21);
	Point message_btn_point=new Point(733,87);
	Point clock_point=new Point(448,226);
	Point play_mode_point=new Point(30,55);
	Point card_call_point=new Point(100,29);
	Point time_left=new Point(468,257);
	
	int setting_btn_width=117,setting_btn_height=49;
	int message_btn_width=117,message_btn_height=49;
	int clock_width=59,clock_height=80;
	int play_mode_width=75,play_mode_height=40;
	int card_call_width=42,card_call_height=53;
	int time_left_width=18,time_left_height=18;
	
	
	Rect background_rect;
	Rect blackback_rect;
	Rect setting_btn_rect;
	Rect message_btn_rect;
	Rect clock_rect;
	Rect play_mode_rect;
	Rect card_call_rect;
	
	
	
	
	Rect backRect;
	
	
	
	int screenWidth,screenHeight;
	
	
	Handler main_handler;
	Heart4MainActivity main_activity;
	float rate_x,rate_y;
	GestureDetector gd;
	
	//PlayerInfo player_bottom,player_top,player_left,player_right;
	PlayerProcess players;
	
	Player playerView;
	CardView cardView;
	PlayerButton playerButton;
	CallFriendView callFriendView;
	CountDialog countDialog;
	SettingDialog settingDialog;
	AlertDialog alertDialog;
	MessageDialog messageDialog;
	ProgressView progressView;
	HostView hostView;
	SelectedCardView selectedCardView;
	
	Paint bitmap_paint;
	TextPaint text_paint;
	GameViewCallBack callBack;
	
//	int cur_host=1;
	int timer_txt;
	
	AsyncImageLoader2 asyncImageLoader;
	String server_url;

	String alert_str;
	//SecondTimerTask secondTimerTask;
	Timer secondTimer;
	int player_time_left;
	int selectedCard=-1;
	
	public class GetDeskPlayerInfo extends AsyncTask<String, Void, String> {

		
		@Override
		protected void onPostExecute(String result) {
			refreshPlayerInfo(result);
		}

		@Override
		protected String doInBackground(String... params) {
			
			return null;
		}

	}
	
	public GameView(Heart4MainActivity main,GameViewCallBack CallBack) {
		
		players=new PlayerProcess(main);
		
		
		server_url=((DBUility)main.getApplicationContext()).webapps+"/pic";
		callBack=CallBack;
		
		screenWidth  = main.getWindowManager().getDefaultDisplay().getWidth();       // 屏幕宽（像素，如：480px）   
		screenHeight = main.getWindowManager().getDefaultDisplay().getHeight();
		backRect=new Rect(0,0,screenWidth,screenHeight);
		
		main_activity=main;
		initImagePos();
	//	initPlayers();
		playerView=new Player(rate_x,rate_y,main);
		
		playerButton=new PlayerButton(rate_x,rate_y,main,this);
	//	callFriendView=new CallFriendView(rate_x,rate_y,main,this,testCard);
		
		
		
		bitmap_paint=new Paint();
		bitmap_paint.setAntiAlias(true);
		bitmap_paint.setFilterBitmap(true);
		
		text_paint=new TextPaint();
		text_paint.setColor(Color.parseColor("#ffffff"));
		text_paint.setTextSize(22*rate_x);
		text_paint.setAntiAlias(true);
		
		secondTimer=new Timer();
		
		secondTimer.schedule(new SecondTimerTask(this), 0,1000);
	//	updatePlayedCards();
		
	//	gameMode=GAME_MODE_HOST_VIEW;
	}
	
	public void refreshPlayerInfo(String result) {
		
		
	}

	int[] testCard={3,2,19,43,28,48,21,39,42,23,51,9,18};
	int[] testCard1={28,48,21,39,42,23,51,9,18};
	int[] testCard2={18};
	/*private void initPlayers() {
		
		player_bottom=new PlayerInfo();
		player_bottom.touxiang=main_activity.user_touxiang;
		player_bottom.score=main_activity.user_coins;
		player_bottom.nickname=main_activity.nickname;
		player_bottom.isDisconnect=true;
		player_bottom.isHost=false;
		player_bottom.isNocard=true;
		player_bottom.isTurn=true;
		player_bottom.card_left=12;
		player_bottom.time_left=20;
		
		player_bottom.card=testCard;
		
		player_left=new PlayerInfo();player_left.nickname="王静";
		player_top=new PlayerInfo();player_top.nickname="胡锦涛";
		player_right=new PlayerInfo();player_right.nickname="奥巴马";
		
	}*/

	private void updatePlayedCards()
	{
		cardView.refreshPlayedCard(testCard, 1);
		cardView.refreshPlayedCard(testCard2, 2);
		cardView.refreshPlayedCard(testCard1, 3);
	}
	private void initImagePos() {
		AssetManager assets=main_activity.getResources().getAssets();
		Bitmap background_img = null;
		Bitmap blackback_img=null;
		Bitmap button_img=null;
		Bitmap clock_img=null;
		Bitmap play_mode_img=null;
		Bitmap card_call_img=null;
		
		
		
		try {
			background_img=BitmapFactory.decodeStream(assets.open("image/desk.png"));
			blackback_img=BitmapFactory.decodeStream(assets.open("image/blackback.png"));
			button_img=BitmapFactory.decodeStream(assets.open("image/game_btn.png"));
			clock_img=BitmapFactory.decodeStream(assets.open("image/clock.png"));
			play_mode_img=BitmapFactory.decodeStream(assets.open("image/game_mode.png"));
			card_call_img=BitmapFactory.decodeStream(assets.open("image/poker.png"));
			
			
		} catch (IOException e) {
			
			Log.d("lichao","img init error:"+e.toString());
		}
		rate_x=(float)screenWidth/(float)background_img.getWidth();
		rate_y=(float)screenHeight/(float)background_img.getHeight();
		
		background=new ImagePosData(background_img,new Point(0,0),background_point,background_img.getWidth(),background_img.getHeight(),rate_x,rate_y);
		blackback=new ImagePosData(blackback_img,new Point(0,0),background_point,background_img.getWidth(),background_img.getHeight(),rate_x,rate_y);
		setting_btn=new ImagePosData(button_img,new Point(0,0),setting_btn_point,setting_btn_width,setting_btn_height,rate_x,rate_y);
		message_btn=new ImagePosData(button_img,new Point(117,0),message_btn_point,message_btn_width,message_btn_height,rate_x,rate_y);
		clock=new ImagePosData(clock_img,new Point(0,0),clock_point,clock_width,clock_height,rate_x,rate_y);
		play_mode=new ImagePosData(play_mode_img,new Point(0,0),play_mode_point,play_mode_width,play_mode_height,rate_x,rate_y);
		card_call=new ImagePosData(card_call_img,new Point(0,0),card_call_point,card_call_width,card_call_height,rate_x,rate_y);
	
		time_left.x=(int) (time_left.x*rate_x);
		time_left.y=(int) (time_left.y*rate_y);
		
		background_rect=Tools.getRect(background);
		blackback_rect=Tools.getRect(blackback);
		setting_btn_rect=Tools.getRect(setting_btn);
		message_btn_rect=Tools.getRect(message_btn);
		clock_rect=Tools.getRect(clock);
		play_mode_rect=Tools.getRect(play_mode);
		card_call_rect=Tools.getRect(card_call);
		
	}

/*	public class DrawRunnable implements Runnable  {
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
						
					//	Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			Log.d("lichao","gameview exit");
		}

	};*/
	/*@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
		
	}*/
	
	
	protected void draw(Canvas canvas) {
		Paint paint=new Paint();
		
		canvas.drawBitmap(background.bmp_src, null,backRect, paint);
		
		paintButton(canvas);
		
	//	playerView.drawPlayer(canvas,player_bottom,player_left,player_top,player_right);
		if(playerView!=null)
		{
			playerView.time_left=player_time_left;
			playerView.drawPlayer(canvas,players);
		}
		
		if(gameMode==GAME_MODE_SELECTED_CARD)
		{
			if(selectedCardView==null)
				selectedCardView=new SelectedCardView(rate_x,rate_y,main_activity,this,selectedCard);
			
			selectedCardView.draw(canvas);
			return;
		}
		if(gameMode==GAME_MODE_EXIT_ALERT)
		{
			if(alertDialog==null)
				alertDialog=new AlertDialog(rate_x,rate_y,escHint,main_activity,this);
			alertDialog.setString(alert_str);
			alertDialog.draw(canvas);
			return;
		}
		if(gameMode==GAME_MODE_WAITTING_PLAYER_START_1)
		{
			paintClock(canvas);
			playerButton.draw(canvas,players);
			return;
		}
		if(gameMode==GAME_MODE_CHANGE_DESK)
		{
			if(progressView==null)
			{
				progressView=new ProgressView(rate_x,rate_y,main_activity);
				progressView.setProgressText("正在进入其它游戏桌，请稍候...");
			}
			progressView.draw(canvas);
			return;
		}
		
		if(gameMode==GAME_MODE_WAITTING_PLAYER_ENTER)
		{
			playerButton.draw(canvas,players);
		}
	//	Log.d("lichao","game mode is :"+gameMode);
		if(gameMode==GAME_MODE_ALERT)
		{
			
			if(alertDialog==null)
				alertDialog=new AlertDialog(rate_x,rate_y,escHint,main_activity,this);
			alertDialog.setString(alert_str);
			alertDialog.draw(canvas);
			return;
		}
		if(gameMode==GAME_MODE_HOST_VIEW)
		{
			
			if(hostView==null)
				hostView=new HostView(rate_x,rate_y,main_activity,this);
			hostView.setEndPlayer(players.hostPos);
			hostView.draw(canvas);
			
			return;
		}
		if(gameMode==GAME_MODE_MESSAGE)
		{
			paintBlackback(canvas);
			if(messageDialog==null)
				messageDialog=new MessageDialog(rate_x,rate_y,main_activity,this);
			messageDialog.draw(canvas);
			return;
		}
		if(gameMode==GAME_MODE_USER_EXIT)
		{
			paintBlackback(canvas);
			if(alertDialog==null)
				alertDialog=new AlertDialog(rate_x,rate_y,escHint,main_activity,this);
			alertDialog.setString(escHint);
			alertDialog.draw(canvas);
			return;
		}
		if(gameMode==GAME_MODE_SETTING)
		{
			paintBlackback(canvas);
			if(settingDialog==null)
				settingDialog=new SettingDialog(rate_x,rate_y,main_activity,this);
			settingDialog.draw(canvas);
			return;
		}
		if(gameMode==GAME_MODE_OVER)
		{
			paintBlackback(canvas);
			if(countDialog==null)
				countDialog=new CountDialog(rate_x,rate_y,main_activity,this);
			countDialog.setData(new String[]{players.player_bottom.nickname,
											players.player_left.nickname,
											players.player_top.nickname,
											players.player_right.nickname}, 
											new int[]{3000,3000,-3000,-3000});
			countDialog.draw(canvas);
			return;
			
		}
		
		if(gameMode==GAME_MODE_WAITTING_HOST_SELECT_PARTNER1)
		{
			if(players.bottomSeatIndex==players.hostSeatIndex)
			{
				paintBlackback(canvas);
				if(callFriendView==null)
					callFriendView=new CallFriendView(rate_x,rate_y,main_activity,this,players);
				callFriendView.time_left=player_time_left;
				callFriendView.draw(canvas);
			}
			else
			{
				players.setTimeLeft(players.hostSeatIndex,player_time_left);
			}
			return;
		}
		
		if(gameMode==GAME_MODE_WAITTING_PLAYER_ENTER)
		{
	//		cardView.drawCard(canvas,player_bottom,player_left,player_top,player_right);
			
			
		}
		if(gameMode==GAME_MODE_PLAYING)
		{
			playerButton.draw(canvas,players);
			paintCardView(canvas);
			paintCalledCard(canvas);
		}
		
	}
	private void paintCalledCard(Canvas canvas) {
	
		if(selectedCard!=-1)
		{
			canvas.drawBitmap(card_call.bmp_src, getRect((selectedCard-1)/13,selectedCard%13), card_call_rect, bitmap_paint);
		}
	
	}

	private void paintCardView(Canvas canvas)
	{
		if(cardView==null)
			cardView=new CardView(rate_x,rate_y,main_activity,players.player_bottom.card);
		
		cardView.drawCard(canvas,players);
	}
	private void paintBlackback(Canvas canvas) {
		
		Paint paint = new Paint();
		
		paint.setColor(0xbb222222); //Paint ARGB色值，A = 0x40 不透明。RGB222222 暗色
		canvas.drawBitmap(blackback.bmp_src, null,backRect, paint);
	}
	private void paintClock(Canvas canvas) {
		
		canvas.drawBitmap(clock.bmp_src, null, clock_rect, bitmap_paint);
		canvas.drawText(String.valueOf(timer_txt), time_left.x, time_left.y, text_paint);
	}
	private void paintButton(Canvas canvas) {
		
		int offset=0;
		int offsetInBmp=0;
		if(isSettingBtnPressed)
		{
			offset=BUTTON_PRESSED_OFFSET;
			offsetInBmp=setting_btn.heightInBmp;
		}
		canvas.drawBitmap(setting_btn.bmp_src, 
				new Rect(setting_btn.posInBmp.x,
						setting_btn.posInBmp.y+offsetInBmp,
						setting_btn.posInBmp.x+setting_btn.widthInBmp,
						setting_btn.posInBmp.y+setting_btn.heightInBmp+offsetInBmp),
				new Rect(setting_btn.posInScreen.x,
						setting_btn.posInScreen.y+offset,
						setting_btn.posInScreen.x+setting_btn.widthInScreen,
						setting_btn.posInScreen.y+offset+setting_btn.heightInScreen), bitmap_paint);
		if(isMessageBtnPressed)
		{
			offset=BUTTON_PRESSED_OFFSET;
			offsetInBmp=message_btn.heightInBmp;
		}
		else
		{
			offset=0;
			offsetInBmp=0;
		}
		canvas.drawBitmap(message_btn.bmp_src, 
				new Rect(message_btn.posInBmp.x,
						message_btn.posInBmp.y+offsetInBmp,
						message_btn.posInBmp.x+message_btn.widthInBmp,
						message_btn.posInBmp.y+message_btn.heightInBmp+offsetInBmp),
				new Rect(message_btn.posInScreen.x,
						message_btn.posInScreen.y+offset,
						message_btn.posInScreen.x+message_btn.widthInScreen,
						message_btn.posInScreen.y+offset+message_btn.heightInScreen), bitmap_paint);

	}
/*
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		threadFlag = true;
		new Thread(new DrawRunnable()).start();
		
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		threadFlag = false;
		
	}*/
	
	public boolean onTouch(MotionEvent event) {
		
	//	Log.d("lichao","game view touch");
		
		if(gameMode==GAME_MODE_SELECTED_CARD)
		{
			if(selectedCardView!=null)
				selectedCardView.touch(event);
			return false;
			
		}
		if(gameMode==GAME_MODE_EXIT_ALERT)
		{
			if(alertDialog!=null)
				alertDialog.onTouch(event);
			return false;
			
		}
		if(gameMode==GAME_MODE_ALERT)
		{
			
			if(alertDialog!=null)
				alertDialog.onTouch(event);
			return false;
		}
		if(gameMode==GAME_MODE_WAITTING_PLAYER_ENTER)
		{
			if(playerButton!=null)
				playerButton.touch(event,players);
			backgroundTouch(event);
			return false;
			
		}
		if(gameMode==GAME_MODE_WAITTING_PLAYER_START_1)
		{
			if(playerButton!=null)
				playerButton.touch(event,players);
			backgroundTouch(event);
			return false;
			
		}
		if(gameMode==GAME_MODE_HOST_VIEW)
		{
			return false;
		}
		if(gameMode==GAME_MODE_MESSAGE)
		{
			if(messageDialog!=null)
				messageDialog.onTouch(event);
			return false;
		}
		if(gameMode==GAME_MODE_USER_EXIT)
		{
			
			alertDialog.onTouch(event);
			return false;
		}
		if(gameMode==GAME_MODE_WAITTING_HOST_SELECT_PARTNER1)
		{
			callFriendView.touch(event);
			return false;
		}
		if(gameMode==GAME_MODE_OVER)
		{
			countDialog.onTouch(event);
			return false;
		}
		if(gameMode==GAME_MODE_SETTING)
		{
			
			settingDialog.onTouch(event);
			return false;
		}
		if(gameMode==GAME_MODE_PLAYING)
		{
			if(playerButton!=null)
				playerButton.touch(event,players);
			if(cardView!=null)
				cardView.touch(event);
			backgroundTouch(event);
			return false;
			
		}
		//if(cardView.touch(event)) return false;
		
		
		
		
		return false;
	}
	

	private void backgroundTouch(MotionEvent event) {
		switch(event.getAction() & MotionEvent.ACTION_MASK)
		{
			case MotionEvent.ACTION_UP:
				onTouchUp(event);
				break;
			
			case MotionEvent.ACTION_DOWN:
				onTouchDown(event);
				break;
			case MotionEvent.ACTION_MOVE:
				onTouchMove(event);
				break;
			
		
		}
	
}

	public void onTouchMove(MotionEvent event) {
		
		if(!Tools.isInArea(event, setting_btn)&&isSettingBtnPressed)
		{
			isSettingBtnPressed=false;
			return;
		}
		if(!Tools.isInArea(event, message_btn)&&isMessageBtnPressed)
		{
			isMessageBtnPressed=false;
			return;
		}
	}

	public void onTouchDown(MotionEvent event) {
		
		if(Tools.isInArea(event, setting_btn))
		{
			isSettingBtnPressed=true;
			return;
		}
		if(Tools.isInArea(event, message_btn))
		{
			isMessageBtnPressed=true;
			return;
		}
	}

	public void onTouchUp(MotionEvent event) {
		if(Tools.isInArea(event, setting_btn)&&isSettingBtnPressed)
		{
			isSettingBtnPressed=false;
			showSettingDialog();
			return;
		}
		if(Tools.isInArea(event, message_btn)&&isMessageBtnPressed)
		{
			isMessageBtnPressed=false;
			showMessageDialog();
			return;
		}
		
		isSettingBtnPressed=false;
		isMessageBtnPressed=false;
	}

	private void showMessageDialog() {
		// 
		if(messageDialog==null)
			messageDialog=new MessageDialog(rate_x,rate_y,main_activity,this);
		gameMode=GAME_MODE_MESSAGE;
	}

	private void showSettingDialog() {
		if(settingDialog==null)
			settingDialog=new SettingDialog(rate_x,rate_y,main_activity,this);
		gameMode=GAME_MODE_SETTING;
	}

	public void backPressed()
	{
		Log.d("lichao","backPressed");
	//	if(gameMode==GAME_MODE_PLAYING)
		{
			//gameMode=GAME_MODE_PLAYING;
			callBack.playerExitGame(false);
			return ;
		}
	//	return true;
	//	Log.d("lichao","playerExitGame");
		
	//	exitGameView();
	}
	/*private void exitGameView() {
		threadFlag = false;
		Message msg=new Message();
		msg.what=4;
		main_handler.sendMessage(msg);
	}*/

	public boolean click(MotionEvent arg0) {
		if(gameMode==GAME_MODE_SETTING)
		{
			if(settingDialog!=null)
				settingDialog.onClick(arg0);
			return false;
		}
		if(gameMode==GAME_MODE_WAITTING_HOST_SELECT_PARTNER1)
		{
			callFriendView.click(arg0);
			return false;
		}
		isSettingBtnPressed=false;
		isMessageBtnPressed=false;
		return true;
	}

	@Override
	public void onBtnPressed(int btnIndex) {
		
		if(btnIndex==Tools.USER_CHOICE_BTN_PLAY)
		{
			onPlayBtnPressed();
		}
		if(btnIndex==Tools.USER_CHOICE_BTN_READY)
		{
			onReadyBtnPressed();
		}
		if(btnIndex==Tools.USER_CHOICE_BTN_ALERT)
		{
			onAlertBtnPressed();
		}
		
	}

	private void onAlertBtnPressed() {
		
		gameModeChange(GAME_MODE_CHANGE_DESK);
		playerButton.isReady=false;
		callBack.changeDesk();
		
	}

	private void onReadyBtnPressed() {
	
		players.player_bottom.isStart=true;
		playerButton.isReady=true;
		callBack.ready();
		
	}

	private void onPlayBtnPressed() {
		ArrayList<Integer> list=cardView.getSelectedCards();
		if(list==null)
		{
			main_activity.showToast("您没有选择要出的牌～");
			return;
		}
		
		if(!isCardOk(list))
		{
			main_activity.showToast("您所选择的牌不合法～");
			return;
		}
		processDropCard(true,list);
		
	}

	private boolean isCardOk(ArrayList<Integer> list) {
		
		return true;
	}

	@Override
	public void onCallFriendClose(int selectedCard) {
		
		this.selectedCard=selectedCard;
		
		gameModeChange(GAME_MODE_PLAYING);
		callFriendView=null;
		callBack.callFriend(selectedCard);
		players.clearReady();
		setPendingPlayer(players.hostSeatIndex);
	}
	

	private void setPendingPlayer(int seatIndex) {
		
		players.setPendingPlayer(seatIndex);
		player_time_left=30;
	}


	public interface GameViewCallBack {

		public void playerExitGame(boolean isForceExit);
		public void changeDesk();
		public void ready();
		public void reConnectInGame();
		public void enterRoomInGame(int roomIndex);
		public void backToHallView();
		public void callFriend(int selectedCard);
		public void dropCard(PlayedCardInfo card);
	}

	@Override
	public void CloseCountDialog() {
		
		gameMode=GAME_MODE_PLAYING;
	}

	@Override
	public void CloseSettingDialog() {
		
		gameMode=GAME_MODE_PLAYING;
	}

	@Override
	public void OkAlertDialog() {
		
		if(gameMode==GAME_MODE_ALERT)
		{
			callBack.reConnectInGame();
			gameMode=GAME_MODE_PLAYING;
			return;
		}
		
		if(gameMode==GAME_MODE_EXIT_ALERT)
		{
			callBack.backToHallView();
			return;
		}
	}

	@Override
	public void CancelAlertDialog() {
		

		if(gameMode==GAME_MODE_ALERT)
		{
			
			gameMode=GAME_MODE_PLAYING;
			return;
		}
		
		if(gameMode==GAME_MODE_EXIT_ALERT)
		{
			callBack.backToHallView();
			return;
		}
		
	}

	@Override
	public void onCloseMessageDialog() {
		gameMode=GAME_MODE_PLAYING;
		
	}

	@Override
	public void onSendMessageDialog(int index) {
		gameMode=GAME_MODE_PLAYING;
		
	}

	@Override
	public void onFinishHostView() {
	//	player_bottom.isHost=true;
		players.playerSetHost(players.hostPos);
		
		hostView=null;
	//	if(players.hostSeatIndex==players.bottomSeatIndex)
		{
			gameModeChange(GAME_MODE_WAITTING_HOST_SELECT_PARTNER1);
			player_time_left=30;
		}
	/*	else
			gameModeChange(GAME_MODE_WAITTING_HOST_SELECT_PARTNER2);*/
	//		gameMode=GAME_MODE_PLAYING;
		
	}

	public void setData(int seatIndex, String data) {
		
		players.reset();
		
		players.player_bottom.index=seatIndex;
		if(!main_activity.isAnonymous)
			players.player_bottom.nickname=main_activity.nickname;
		else
			players.player_bottom.nickname="游客"+seatIndex+1;
		players.player_bottom.score=main_activity.user_coins;
		players.player_bottom.touxiang=main_activity.user_touxiang;
		players.bottomSeatIndex=seatIndex;
		try {
			JSONArray ja=new JSONArray(data);
			
			for(int i=0;i<ja.length();i++)
			{
				JSONObject js=ja.getJSONObject(i);
				int seat=js.getInt("seatIndex");
				if(seat!=seatIndex)
				{
					players.addPlayer(seat,js,this,this);
				}
			}
			
		} catch (JSONException e) {
			
		}
		
		/*player_bottom=data[index];
		
		index++;
		player_left=data[index%4];
		index++;
		player_top=data[index%4];
		index++;
		player_right=data[index%4];
		
		if(seatIndex==3)
			gameMode=GAME_MODE_WAITTING_PLAYER_START_1;
		else
			gameMode=GAME_MODE_WAITTING_PLAYER_ENTER;
		
		if(player_left.index!=-1)
		{
			Bitmap bmp=asyncImageLoader.loadDrawable(server_url+"/touxiang/"+player_left.touxiang_url, String.valueOf(player_left.index), this, this);
			player_left.touxiang=bmp;
		}
		if(player_top.index!=-1)
		{
			Bitmap bmp=asyncImageLoader.loadDrawable(server_url+"/touxiang/"+player_top.touxiang_url, String.valueOf(player_top.index), this, this);
			player_top.touxiang=bmp;
		}
		if(player_right.index!=-1)
		{
			Bitmap bmp=asyncImageLoader.loadDrawable(server_url+"/touxiang/"+player_right.touxiang_url, String.valueOf(player_right.index), this, this);
			player_right.touxiang=bmp;
		}*/
		
	}
	
	public void handleMsg(Message msg)
	{
		Log.d("lichao","handleMsg gameview "+msg.what);
		switch(msg.what)
		{
		case 0://login
			processLogin((String) msg.obj);
			break;
		case 11://disconnect from server
			alert_str="与服务器连接被断开！重新连接吗？";
			processDisconnect();
		
			break;
		case 12:
			
			alert_str="连接服务器失败！重新连接吗？";
			processDisconnect();
			break;
		
		case 13:// change desk
			processEnterRoom((String)msg.obj);
			break;
		case 15://new player enter
			processNewPlayer((String)msg.obj);
			break;
		case 16://player exit
			processPlayerExit((String)msg.obj);
			break;
		case 5://ready
			processReady((String)msg.obj);
			break;
		case 18://re enter room
			processReEnterRoom((String)msg.obj);
			break;
		case 1://start game
			processStartGame((String)msg.obj);
			break;
		case 8://call friend
			processCallFriend((String)msg.obj);
			break;
		case 6://drop card
			processDropCard((String)msg.obj);
		}
	}

	private void processDropCard(String obj) {
		
		Log.d("lichao","processDropCard :"+obj);
		
		try {
			JSONObject js=new JSONObject(obj);
			Gson gs=new Gson();
			int seatIndex=js.getInt("seatIndex");
			PlayedCardInfo card=gs.fromJson(js.getString("dropCard"), PlayedCardInfo.class);
			
			if(js.getInt("seatIndex")!=players.bottomSeatIndex)
			{
				selectedCard=js.getInt("selectedCard");
				//updateData(main_activity.seatIndex,js.getString("playerlist"));
				players.updateNetworkStatus(new JSONArray(js.getString("playerstatus")));
				gameModeChange(GAME_MODE_SELECTED_CARD);
				players.clearReady();
				setPendingPlayer(players.hostSeatIndex);
			}
		} catch (JSONException e) {
			Log.d("lichao","processCallFriend json error :"+e.toString());
		}
	}

	private void processCallFriend(String obj) {
		
		Log.d("lichao","processCallFriend :"+obj);
		
		try {
			JSONObject js=new JSONObject(obj);
			if(js.getInt("seatIndex")!=players.bottomSeatIndex)
			{
				selectedCard=js.getInt("selectedCard");
				//updateData(main_activity.seatIndex,js.getString("playerlist"));
				players.updateNetworkStatus(new JSONArray(js.getString("playerstatus")));
				gameModeChange(GAME_MODE_SELECTED_CARD);
				players.clearReady();
				setPendingPlayer(players.hostSeatIndex);
			}
		} catch (JSONException e) {
			Log.d("lichao","processCallFriend json error :"+e.toString());
		}
	}

	private void processStartGame(String obj) {

		try {
				JSONObject js=new JSONObject(obj);
				//players.playerSetHost(js.getInt("hostSeatIndex"));
				players.setHostSeatIndex(js.getInt("hostSeatIndex"));
				players.initCards(new JSONArray(js.getString("cardlist")));
				players.updateNetworkStatus(new JSONArray(js.getString("playerstatus")));
				players.clearReady();
				gameModeChange(GAME_MODE_HOST_VIEW);
			} catch (JSONException e) {
				Log.d("lichao","processStartGame json error :"+e.toString());
			}
		//	processStartTimer();
	}

	private void processReEnterRoom(String obj) {
		
		
		try {
			JSONObject js=new JSONObject(obj);
			if(js.getBoolean("isInGame"))
			{
				JSONArray ja=new JSONArray(js.getString("playerlist"));
				setData(main_activity.seatIndex,ja.toString());
			}
			else
			{
				callBack.backToHallView();
			}
		
		} catch (JSONException e) {
			Log.d("lichao","hall view json error:"+e.toString());
		} 	
		processStartTimer();
	}

	private void processLogin(String obj) {
		
		callBack.enterRoomInGame(main_activity.roomIndex);
		
	}

	private void processReady(String obj) {
		

		
		try 
		{
			JSONObject js=new JSONObject(obj);

			int seatIndex=js.getInt("seatIndex");
			players.playerSetReady(seatIndex);
			players.updateNetworkStatus(new JSONArray(js.getString("playerstatus")));
			
			//Log.d("lichao","playerlist is :"+js.getString("playerlist"));
		
		} catch (JSONException e) {
			Log.d("lichao","hall view processReady error:"+e.toString());
		} 
		
		processStartTimer();
	}

	private void processStartTimer() {
		
		if(players.getPlayerNum()==4&&players.getReadyNum()==3)
		{
			gameModeChange(GAME_MODE_WAITTING_PLAYER_START_1);
			timer_txt=5;
		}
		else
		{
			gameModeChange(GAME_MODE_WAITTING_PLAYER_ENTER);
		}
		
	}

	private void updateData(int seatIndex, String string) {
		try {
			JSONArray ja=new JSONArray(string);
			for(int i=0;i<ja.length();i++)
			{
				JSONObject js=ja.getJSONObject(i);
				int seat=js.getInt("seatIndex");
				if(seat!=seatIndex)
				{
					players.updatePlayer(seat,js);
				}
			}
		} catch (JSONException e) {
			
		}
		
		
	}

	private void processEnterRoom(String obj) {
		
		Log.d("lichao","processEnterRoom");
		try {
			JSONObject js=new JSONObject(obj);
			main_activity.roomIndex=js.getInt("roomIndex");
			main_activity.deskIndex=js.getInt("deskIndex");
			main_activity.seatIndex=js.getInt("seatIndex");
			JSONArray ja=new JSONArray(js.getString("playerlist"));
			setData(main_activity.seatIndex,ja.toString());
		
		} catch (JSONException e) {
			Log.d("lichao","hall view json error:"+e.toString());
		} 
		Log.d("lichao","deskindex is "+main_activity.deskIndex);
		gameModeChange(GAME_MODE_WAITTING_PLAYER_ENTER);
		
		processStartTimer();
	}

	private void processPlayerExit(String obj) {
		
		
		try
		{
			JSONObject js=new JSONObject(obj);
			int seatIndex=js.getInt("seatIndex");
			players.deletePlayer(seatIndex);
			players.updateNetworkStatus(new JSONArray(js.getString("playerstatus")));
		//	updateData(main_activity.seatIndex,new JSONObject(obj).getString("playerlist").toString());
		} catch (JSONException e) {
			Log.d("lichao","hall view processPlayerExit json error:"+e.toString());
		}
		gameModeChange(GAME_MODE_WAITTING_PLAYER_ENTER);
	}

	private void processNewPlayer(String obj) {
		
		try {
			JSONObject js=new JSONObject(obj);
			JSONObject js_player=js.getJSONObject("player");
	
			players.addPlayer(js_player.getInt("seatIndex"), js_player, this,this);
			//updateData(main_activity.seatIndex,js.getString("playerlist"));
			players.updateNetworkStatus(new JSONArray(js.getString("playerstatus")));

		} catch (JSONException e) {
			
		}
		processStartTimer();
	}

	private void processDisconnect() {
	
		gameModeChange(GAME_MODE_ALERT);
	}
	
	private void gameModeChange(int newGameMode)
	{
		gameMode=newGameMode;
		
		if(playerButton!=null)
			playerButton.setGameMode(gameMode);
	}

	@Override
	public void imageLoaded(Bitmap imageDrawable, String imageUrl, String id) {
		
		
		if(imageDrawable!=null)
		{
			
			int seatIndex=Integer.valueOf(id);
			
			players.setTouxiang(seatIndex,imageDrawable);
		/*	if(seatIndex==player_left.index)
			{
				player_left.touxiang=imageDrawable;
			}
			if(seatIndex==player_top.index)
			{
				player_top.touxiang=imageDrawable;
			}
			if(seatIndex==player_right.index)
			{
				player_right.touxiang=imageDrawable;
			}*/
		}
		
	}

	@Override
	public void textLoaded(String text, String id) {
	
		
	}

	public void destroy() {
		
		if(secondTimer!=null)
			secondTimer.cancel();
		secondTimer=null;
	}

	@Override
	public void onSecondTimeout() {
		
		if(gameMode==GAME_MODE_WAITTING_PLAYER_START_1)
		{
			if(timer_txt>0)
				timer_txt--;
			else
				processReadyTimeout();
				
		}
	
		if(gameMode==GAME_MODE_WAITTING_HOST_SELECT_PARTNER1)
		{
			if(player_time_left>0)
				player_time_left--;
			else
			{
				if(players.bottomSeatIndex==players.hostSeatIndex)
					processFinishCallFriend();
			}
		}
		if(gameMode==GAME_MODE_PLAYING||gameMode==GAME_MODE_SELECTED_CARD)
		{
			if(player_time_left>0)
				player_time_left--;
			else
				if(players.currenPendingPlayer==players.bottomSeatIndex)
				{
					processDropCard(true,null);
				}
		}
	}

	private void processDropCard(boolean isNoCard,ArrayList<Integer> list) {
		
		players.currenPendingPlayer=(players.currenPendingPlayer+1)%4;
		player_time_left=0;
		
		if(isNoCard)
		{
			players.player_bottom.isNocard=true;
		}
		else
		{
			players.player_bottom.isNocard=false;
		}
		callBack.dropCard(new PlayedCardInfo(isNoCard,list));
	}

	private void processFinishCallFriend() {
		callFriendView.onOkCallFriend();
	//	gameModeChange(GAME_MODE_PLAYING);
	//	
	}

	private void processReadyTimeout() {
		
		if(!players.player_bottom.isStart)
		{
			alert_str="您长时间没有开始游戏，被请离游戏桌";
			gameModeChange(GAME_MODE_EXIT_ALERT);
		}
		
	}

	public void initPlayers(int seatIndex, String data) {
		setData(seatIndex,data);
		processStartTimer();
	}

	@Override
	public void onSelectedCardClose() {
		players.clearReady();
		gameModeChange(GAME_MODE_PLAYING);
		selectedCardView=null;
	}
	private Rect getRect(int huase, int num) {
		
		int real_num=num-3;
		if(real_num<0) real_num=real_num+13;
		
		return new Rect(real_num*103,huase*128,(real_num+1)*103,(huase+1)*128);
	}
}
