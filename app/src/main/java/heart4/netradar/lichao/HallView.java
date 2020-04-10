package heart4.netradar.lichao;



import heart4.netradar.lichao.HelpDialog.CloseCallbackHelpDialog;
import heart4.netradar.lichao.RankDialog.CloseCallbackRankDialog;
import heart4.netradar.lichao.SettingDialog.CloseCallbackSettingDialog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carweibo.netradar.lichao.NvManager;


import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class HallView implements CloseCallbackSettingDialog, CloseCallbackHelpDialog, CloseCallbackRankDialog {

	

	private static int HALL_MODE_NORMAL=0;
	private static int HALL_MODE_SETTING=1;
	private static int HALL_MODE_HELP=2;
	private static int HALL_MODE_RANK=3;
	private static int HALL_WAITTING_NETWORK=4;
	
	
	private static int BUTTON_PRESSED_OFFSET=3;
	
	boolean threadFlag=true;
	boolean isDragRoom=false;
	boolean isRankPressed=false;
	boolean isSettingPressed=false;
	boolean isHelpPressed=false;
	boolean isLoginPressed=false;
	boolean isBackgroundMusic;
	boolean isPlayAudioEffect;
	
	int hallMode=HALL_MODE_NORMAL;
	
	ImagePosData background;//,room,room_pressed,quick_start;
	ImagePosData blackback;
	ImagePosData room_list;
	ImagePosData touxiang;
	ImagePosData rank;
	ImagePosData btn_setting,btn_rank,btn_help;
	ImagePosData score_not_enough;

	Bitmap[] rooms;
	
	int refresh_interval=30;
	int screenWidth,screenHeight;
	Rect backRect;
	
	//Handler main_handler;
	float rate_x,rate_y;
	Point room_list_border=new Point(35,167);
	int room_list_border_width=834,room_list_border_height=280;
	int room_offset=50;
	
	Point nickname_point=new Point(166,500);
	Point score_point=new Point(168,556);
	Point help_txt_point=new Point(106,188);
	Point progress_point=new Point();
	
	int help_txt_width=700,help_txt_height=320;
	

	
	int score=1600;
	int user_max_room=-1;
	int drag_distince=0;
	
	int current_click_room_index=-1;
	Heart4MainActivity main_activity;
	
	int txt_font=25;
	
	
	SettingDialog settingDialog;
	HelpDialog helpDialog;
	RankDialog rankDialog;
	HallViewCallBack callBack;
	ProgressView progressView;
	
	String nickname;
	String progress_str="正在连接服务器...";
	
	TextPaint txt_paint;
	
	public HallView(Heart4MainActivity main,HallViewCallBack CallBack) {
		
		
		if(main.nickname.equals("NOUSER")||main.nickname.equals("NOLOGIN"))
			nickname="游客";
		else
			nickname=main.nickname;
		main_activity=main;
		callBack=CallBack;
		
		screenWidth  = main.getWindowManager().getDefaultDisplay().getWidth();       // 屏幕宽（像素，如：480px）   
		screenHeight = main.getWindowManager().getDefaultDisplay().getHeight();
		backRect=new Rect(0,0,screenWidth,screenHeight);
	
		initImagePos();
		
		
		isBackgroundMusic=NvManager.getNVBoolean(main, NvManager.GAME_BACKGROUND_MUSIC, true);
		isPlayAudioEffect=NvManager.getNVBoolean(main, NvManager.GAME_PLAY_AUDIO_EFFECT, true);

		
	
	}

	private void initImagePos() {
		
		AssetManager assets=main_activity.getResources().getAssets();
		Bitmap background_img = null;
		Bitmap blackback_img=null;
		Bitmap class_img=null;
		Bitmap rank_img=null;
		Bitmap btn_img=null;
		Bitmap score_not_enough_img=null;
	
		try {
				background_img=BitmapFactory.decodeStream(assets.open("image/hall.png"));
				blackback_img=BitmapFactory.decodeStream(assets.open("image/blackback.png"));
				class_img=BitmapFactory.decodeStream(assets.open("image/class.png"));
				rank_img=BitmapFactory.decodeStream(assets.open("image/rank.png"));
				btn_img=BitmapFactory.decodeStream(assets.open("image/button.png"));
				score_not_enough_img=BitmapFactory.decodeStream(assets.open("image/score_not_enough.png"));
		} catch (IOException e) {
				
				Log.d("lichao","open assets error :"+e.toString());
			}
		rate_x=(float)screenWidth/(float)background_img.getWidth();
		rate_y=(float)screenHeight/(float)background_img.getHeight();
		room_list_border.x=(int) (room_list_border.x*rate_x);
		room_list_border.y=(int) (room_list_border.y*rate_y);
		room_list_border_width=(int) (room_list_border_width*rate_x);
		room_list_border_height=(int) (room_list_border_height*rate_y);
		room_offset=(int) (room_offset*rate_x);
		nickname_point.x=(int) (nickname_point.x*rate_x);
		nickname_point.y=(int) (nickname_point.y*rate_y);
		score_point.x=(int) (score_point.x*rate_x);
		score_point.y=(int) (score_point.y*rate_y);

		
		background=new ImagePosData(background_img,new Point(0,0),new Point(0,0),background_img.getWidth(),background_img.getHeight(),rate_x,rate_y);
		blackback=new ImagePosData(blackback_img,new Point(0,0),new Point(0,0),background_img.getWidth(),background_img.getHeight(),rate_x,rate_y);
		
		room_list=new ImagePosData(class_img,new Point(0,0),new Point(80,217), 201, 181, rate_x, rate_y);
		touxiang=new ImagePosData(main_activity.user_touxiang,new Point(0,0),new Point(54,476),86,86,rate_x,rate_y);
		rank=new ImagePosData(rank_img,new Point(0,0),new Point(617,499),156,59,rate_x,rate_y);
		btn_help=new ImagePosData(btn_img,new Point(145,1),new Point(85,78),144,57,rate_x,rate_y);
		btn_setting=new ImagePosData(btn_img,new Point(0,0),new Point(685,78),144,57,rate_x,rate_y);
		score_not_enough=new ImagePosData(score_not_enough_img,new Point(0,0),new Point(617,499),114,70,rate_x,rate_y);
		
		initUserMaxRoom();
		
	}

	private void initUserMaxRoom() {
		
		if(score>200000)
		{
			user_max_room=2;
		}
		else if(score>100000)
		{
			user_max_room=1;
		}
		else if(score>1500)
		{
			user_max_room=0;
		}
		else
			user_max_room=-1;
		
	}

/*

	public class DrawRunnable implements Runnable 
	{

		@Override
		public void run() {
			Log.d("lichao","thread start");
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
			Log.d("lichao","thread exit");
		}
		
	}*/
	
	
	protected void draw(Canvas canvas) {
	
		paintBackground(canvas);
		paintRooms(canvas);
		paintTouxiang(canvas);
		paintNickname(canvas);
		paintRank(canvas);
		paintSetting(canvas);
		paintHelp(canvas);
		
		if(hallMode==HALL_WAITTING_NETWORK)
		{
			if(progressView==null)
				progressView=new ProgressView(rate_x,rate_y,main_activity);
			progressView.setProgressText(progress_str);
			progressView.draw(canvas);
		
		}
		if(hallMode==HALL_MODE_SETTING)
		{
			paintBlackback(canvas);
			if(settingDialog==null)
				settingDialog=new SettingDialog(rate_x,rate_y,main_activity,this);
			settingDialog.draw(canvas);
		}
		if(hallMode==HALL_MODE_HELP)
		{
			paintBlackback(canvas);
			if(helpDialog==null)
				helpDialog=new HelpDialog(rate_x,rate_y,main_activity,this);
			helpDialog.draw(canvas);
		}
		if(hallMode==HALL_MODE_RANK)
		{
			paintBlackback(canvas);
			if(rankDialog==null)
				rankDialog=new RankDialog(rate_x,rate_y,main_activity,this);
			rankDialog.draw(canvas);
		}

	}
	
	

	private void paintBlackback(Canvas canvas) {
		
		Paint paint = new Paint();
		
		paint.setColor(0xbb222222); //Paint ARGB色值，A = 0x40 不透明。RGB222222 暗色
		canvas.drawBitmap(blackback.bmp_src, null,backRect, paint);
	}

	
	private void paintHelp(Canvas canvas) {
		
		Paint paint=new Paint();
		int offset=0;
		int offsetInBmp=0;
		if(isHelpPressed)
		{
			 
			offset=BUTTON_PRESSED_OFFSET;
			offsetInBmp=btn_setting.heightInBmp;
		}
	
		canvas.drawBitmap(btn_help.bmp_src, 
				new Rect(btn_help.posInBmp.x,
						btn_help.posInBmp.y+offsetInBmp,
						btn_help.posInBmp.x+btn_help.widthInBmp,
						btn_help.posInBmp.y+btn_help.heightInBmp+offsetInBmp),
				new Rect(btn_help.posInScreen.x,
						btn_help.posInScreen.y+offset,
						btn_help.posInScreen.x+btn_help.widthInScreen,
						btn_help.posInScreen.y+offset+btn_help.heightInScreen), paint);
	}

	private void paintSetting(Canvas canvas) {
		Paint paint=null;
		
		int offset=0;
		int offsetInBmp=0;
		if(isSettingPressed)
		{
			offset=BUTTON_PRESSED_OFFSET;
			offsetInBmp=btn_setting.heightInBmp;
		}
		else
		{
			
		}
		canvas.drawBitmap(btn_setting.bmp_src, 
				new Rect(btn_setting.posInBmp.x,
						btn_setting.posInBmp.y+offsetInBmp,
						btn_setting.posInBmp.x+btn_setting.widthInBmp,
						btn_setting.posInBmp.y+btn_setting.heightInBmp+offsetInBmp),
				new Rect(btn_setting.posInScreen.x,
						btn_setting.posInScreen.y+offset,
						btn_setting.posInScreen.x+btn_setting.widthInScreen,
						btn_setting.posInScreen.y+offset+btn_setting.heightInScreen), paint);

		
	}

/*	private void paintLogin(Canvas canvas) {
		
		Paint paint=new Paint();
		
		int offset=0;
		
		if(isLoginPressed)
		{
			offset=2;
			paint.setColor(0xbb222222);
		}
		else
		{
			 
		}
		canvas.drawBitmap(btn_login.bmp_src, 
				new Rect(btn_login.posInBmp.x,
						btn_login.posInBmp.y,
						btn_login.posInBmp.x+btn_login.widthInBmp,
						btn_login.posInBmp.y+btn_login.heightInBmp),
				new Rect(btn_login.posInScreen.x,
						btn_login.posInScreen.y+offset,
						btn_login.posInScreen.x+btn_login.widthInScreen,
						btn_login.posInScreen.y+offset+btn_login.heightInScreen), paint);

	}
*/
	
	private void paintRank(Canvas canvas) {
		Paint paint=null;
		
		int offset=0;
		int offsetInBmp=0;
		if(isRankPressed)
		{
			offsetInBmp=rank.heightInBmp;
			offset=BUTTON_PRESSED_OFFSET;
		}
		else
		{

			
		}
		canvas.drawBitmap(rank.bmp_src, 
				new Rect(rank.posInBmp.x,
						 rank.posInBmp.y+offsetInBmp,
						 rank.posInBmp.x+rank.widthInBmp,
						 rank.posInBmp.y+rank.heightInBmp+offsetInBmp),
				new Rect(rank.posInScreen.x,
						 rank.posInScreen.y+offset,
						 rank.posInScreen.x+rank.widthInScreen,
						 rank.posInScreen.y+offset+rank.heightInScreen), paint);
	}

	private void paintRooms(Canvas canvas) {
		
		
		
		for(int i=0;i<4;i++)
		{
			Rect src_rect=new Rect();
			Rect dst_rect=new Rect();
				
			int offsetY=0;
			if(i==current_click_room_index)
				offsetY=1;
			else
				offsetY=0;
					
			if((room_list.posInScreen.x+i*(room_list.widthInScreen+room_offset))<=room_list_border.x)
			{
				if((room_list.posInScreen.x+(i+1)*room_list.widthInScreen+i*room_offset)<=room_list_border.x)
				{
					//Log.d("lichao","第"+i+"个房间出去了");
					continue;
				}
				src_rect.set((int) (i*room_list.widthInBmp+(float)((room_list_border.x-(room_list.posInScreen.x+i*(room_list.widthInScreen+room_offset))))/rate_x),
						offsetY*room_list.heightInBmp,
						(i+1)*room_list.widthInBmp,
						room_list.heightInBmp+offsetY*room_list.heightInBmp);
				dst_rect.set(room_list_border.x,
						room_list.posInScreen.y,
						room_list.posInScreen.x+(i+1)*room_list.widthInScreen+i*room_offset,
						room_list.posInScreen.y+room_list.heightInScreen);
				
					
			}
			else if((room_list.posInScreen.x+(i+1)*room_list.widthInScreen+i*room_offset)>=(room_list_border.x+room_list_border_width))
			{
				if((room_list.posInScreen.x+i*(room_list.widthInScreen+room_offset))>=(room_list_border.x+room_list_border_width))
				{
					
					continue;
				}
				src_rect.set(i*room_list.widthInBmp,offsetY*room_list.heightInBmp,(int) (i*room_list.widthInBmp+(float)((room_list_border.x+room_list_border_width-(room_list.posInScreen.x+i*(room_list.widthInScreen+room_offset))))/rate_x),	room_list.heightInBmp+offsetY*room_list.heightInBmp);
				dst_rect.set(room_list.posInScreen.x+i*(room_list.widthInScreen+room_offset),
						room_list.posInScreen.y,
						room_list_border.x+room_list_border_width,
						room_list.posInScreen.y+room_list.heightInScreen);
				
				
			}
			else
			{
				src_rect.set(i*room_list.widthInBmp,offsetY*room_list.heightInBmp,(i+1)*room_list.widthInBmp,room_list.heightInBmp+offsetY*room_list.heightInBmp);
				dst_rect.set(room_list.posInScreen.x+i*(room_list.widthInScreen+room_offset),
						room_list.posInScreen.y,
						room_list.posInScreen.x+(i+1)*room_list.widthInScreen+i*room_offset,
						room_list.posInScreen.y+room_list.heightInScreen);
				
			}
			canvas.drawBitmap(room_list.bmp_src, src_rect,dst_rect, null);
			if(user_max_room<i&&i!=3)
				canvas.drawBitmap(score_not_enough.bmp_src, dst_rect.left,dst_rect.top,null);
		}
		
		
	}


	private void paintBackground(Canvas canvas) {
		
		canvas.drawBitmap(background.bmp_src, null,backRect, null);
	}

	boolean isQuickPressed=false;
	/*private void paintQuickStart(Canvas canvas) {
		Paint paint = new Paint();
		int offset=0;
		if(isQuickPressed)
		{
			paint.setColor(0xbb222222); //Paint ARGB色值，A = 0x40 不透明。RGB222222 暗色
			offset=2;
		}
		canvas.drawBitmap(quick_start, null,new Rect(quick_start_point.x,quick_start_point.y+offset,quick_start_point.x+quick_start_width,quick_start_point.y+quick_start_height+offset),
				  paint);
		
	}*/

	
	private void paintNickname(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(Color.parseColor("#ab170a"));
		paint.setTextSize(24*rate_x);
		paint.setAntiAlias(true);

		
		canvas.drawText(nickname, nickname_point.x,
				 nickname_point.y, paint);
		
		paint.setTextSize(20*rate_x);
		canvas.drawText("金币:"+String.valueOf(main_activity.user_coins), score_point.x,
				 score_point.y, paint);
		
	}

	private void paintTouxiang(Canvas canvas) {
		
		
		canvas.drawBitmap(touxiang.bmp_src, null,new Rect(touxiang.posInScreen.x,
											touxiang.posInScreen.y,
											touxiang.posInScreen.x+touxiang.widthInScreen,
											touxiang.posInScreen.y+touxiang.heightInScreen), null);
	}



	public boolean onTouch(MotionEvent event) {
		
		switch(hallMode)
			{
			case 1://setting
				settingDialog.onTouch(event);
				return false;
			case 2://help
				helpDialog.onTouch(event);
				return false;
			case 3://rank
				rankDialog.onTouch(event);
				return false;
			case 4://waitting network
				return false;
				
		}
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
		
		return false;
	}

	float start_x;

	public void onTouchMove(MotionEvent event) {
	
		switch(hallMode)
		{
			case 0://normal
			
				//current_click_room_index=-1;
				if(isDragRoom)
				{
					drag_distince=(int) (event.getX()-start_x);
				
					room_list.posInScreen.x=room_list.posInScreen.x+drag_distince;
					start_x= event.getX();
				}
				if(isSettingPressed)
				{
					if(!Tools.isInArea(event, btn_setting))
					{
						isSettingPressed=false;
					}
				}
				if(isHelpPressed)
				{
					if(!Tools.isInArea(event, btn_help))
					{
						isHelpPressed=false;
					}
				}
				if(isRankPressed)
				{
					if(!Tools.isInArea(event, rank))
					{
						isRankPressed=false;
					}
				}
				break;
		}
	}

	public void onTouchDown(MotionEvent event) {
		
		switch(hallMode)
		{
			case 0://normal
				if(isInRoomList(event))
				{
					isDragRoom=true;
					start_x= event.getX();
					current_click_room_index=getRoomNum(event.getX(),event.getY());
				
				}
				if(isInButton(event,rank))
				{
					isRankPressed=true;
				}
				if(isInButton(event,btn_setting))
				{
					isSettingPressed=true;
				}
				if(isInButton(event,btn_help))
				{
					isHelpPressed=true;
				}
				break;
		}
		
	}

	private boolean isInButton(MotionEvent event, ImagePosData data) {
		
		if(event.getX()>data.posInScreen.x&&event.getX()<(data.posInScreen.x+data.widthInScreen))
		{
			if(event.getY()>data.posInScreen.y&&event.getY()<(data.posInScreen.y+data.heightInScreen))
				return true;
		}
		return false;
	}

	private boolean isInRoomList(MotionEvent event) {
		
		if(event.getX()>room_list_border.x&&event.getX()<(room_list_border.x+room_list_border_width))
		{
			if(event.getY()>room_list_border.y&&event.getY()<(room_list_border.y+room_list_border_height))
				return true;
		}
		return false;
	}

	//boolean is
	public void onTouchUp(MotionEvent event) {
		switch(hallMode)
		{
	
		case 0://
			if(isDragRoom)
			{
				
				current_click_room_index=-1;
				if(room_list.posInScreen.x>(80*rate_x))
				{
					room_list.posInScreen.x=(int) (80*rate_x);
				}
				if((room_list.posInScreen.x+4*room_list.widthInScreen+3*room_offset)<(room_list_border.x+room_list_border_width))
				{
					
					room_list.posInScreen.x=(int) (room_list_border.x+room_list_border_width-4*room_list.widthInScreen-3*room_offset);
				}
				
				isDragRoom=false;
			}
			if(isSettingPressed)
			{
				isSettingPressed=false;
				if(settingDialog==null)
					settingDialog=new SettingDialog(rate_x,rate_y,main_activity,this);
				hallMode=HALL_MODE_SETTING;
			}
			if(isHelpPressed)
			{
				isHelpPressed=false;
				if(helpDialog==null)
					helpDialog=new HelpDialog(rate_x,rate_y,main_activity,this);
				hallMode=HALL_MODE_HELP;
			}
			if(isRankPressed)
			{
				isRankPressed=false;
			}
			break;
		
		}
		isQuickPressed=false;
		isRankPressed=false;
		isSettingPressed=false;
		isHelpPressed=false;
		isLoginPressed=false;
	
	
	}




	public boolean click(MotionEvent event) {

		
		if(hallMode==HALL_MODE_NORMAL)
		{
			if(isInButton(event,btn_setting))
			{
				
				if(settingDialog==null)
					settingDialog=new SettingDialog(rate_x,rate_y,main_activity,this);
				hallMode=HALL_MODE_SETTING;
				return true;
				
			}
			if(isInButton(event,btn_help))
			{
								
				if(helpDialog==null)
					helpDialog=new HelpDialog(rate_x,rate_y,main_activity,this);
				hallMode=HALL_MODE_HELP;
				return true;
			}
			if(isInButton(event,rank))
			{
				
				
				if(rankDialog==null)
					rankDialog=new RankDialog(rate_x,rate_y,main_activity,this);
				hallMode=HALL_MODE_RANK;
				return true;
			}
			int room=getRoomNum(event.getX(),event.getY());
			
			if(room!=-1)
			{
				current_click_room_index=-1;
				hallMode=HALL_WAITTING_NETWORK;
				callBack.onEnterRoom(room);
			}
		}
		
		if(hallMode==HALL_MODE_HELP)
		{
			helpDialog.onClick(event);
		}
		if(hallMode==HALL_MODE_SETTING)
		{
			settingDialog.onClick(event);
		}
		if(hallMode==HALL_MODE_RANK)
		{
			rankDialog.onClick(event);
		}
		return true;
	}

	private int getRoomNum(float x, float y) {
		if(x>=room_list_border.x&&x<=(room_list_border.x+room_list_border_width))
		{
			if(y>=room_list_border.y&&y<=(room_list_border.y+room_list_border_height))
			{
				for(int i=0;i<4;i++)
				{
					if(x>=(room_list.posInScreen.x+i*(room_list.widthInScreen+room_offset))&&x<=(room_list.posInScreen.x+(i+1)*room_list.widthInScreen)+i*room_offset)
						if(y>=room_list.posInScreen.y&&y<=(room_list.posInScreen.y+room_list.heightInScreen))
							return i;
				}
			}
		}
		return -1;
	}

	
	public void backPressed()
	{
		switch(hallMode)
		{
			case 0://normal
		//		exitHallView();
				break;
			case 1://setting
				hallMode=HALL_MODE_NORMAL;
				break;
			case 2://help
				hallMode=HALL_MODE_NORMAL;
				break;
			case 3://rank
				hallMode=HALL_MODE_NORMAL;
				break;
		}
	}

/*	private void exitHallView() {
		
		Message msg=new Message();
		msg.what=3;
		main_handler.sendMessage(msg);
	}*/
	
	@Override
	public void CloseSettingDialog() {
		hallMode=HALL_MODE_NORMAL;
		
	}

	@Override
	public void CloseHelpDialog() {
		hallMode=HALL_MODE_NORMAL;
		
	}

	@Override
	public void CloseRankDialog() {
		hallMode=HALL_MODE_NORMAL;
		
	} 
	public interface HallViewCallBack {
		
		public void onStartGame(int roomIndex,int deskIndex,int seatIndex,String data);
		public void onEnterRoom(int roomIndex);

	}
	public void handleMsg(Message msg) {
		switch(msg.what)
		{
		case 13://enter room
			onEnterRoom((String)msg.obj);
			break;
		}
		
	}

	int roomIndex,deskIndex,seatIndex;
	private void onEnterRoom(String data) {
		
		
		hallMode=HALL_MODE_NORMAL;
		try {
			JSONObject js=new JSONObject(data);
			roomIndex=js.getInt("roomIndex");
			deskIndex=js.getInt("deskIndex");
			seatIndex=js.getInt("seatIndex");
			JSONArray ja=new JSONArray(js.getString("playerlist"));
			callBack.onStartGame(roomIndex, deskIndex, seatIndex, ja.toString());
		
		} catch (JSONException e) {
			Log.d("lichao","hall view json error:"+e.toString());
		} 
	//	Log.d("lichao","recv:"+data);
	/*	PlayerInfo[] players=new PlayerInfo[4];
		
		for(int ii=0;ii<4;ii++)
		{
			players[ii]=new PlayerInfo();
		}
		
		try {
			JSONObject js=new JSONObject(data);
			roomIndex=js.getInt("roomIndex");
			deskIndex=js.getInt("deskIndex");
			seatIndex=js.getInt("seatIndex");
			
			players[seatIndex].index=seatIndex;
			if(!main_activity.isAnonymous)
				players[seatIndex].nickname=main_activity.nickname;
			else
				players[seatIndex].nickname="游客"+seatIndex++;
			players[seatIndex].score=main_activity.user_coins;
			players[seatIndex].touxiang=main_activity.user_touxiang;
			
			JSONArray ja=new JSONArray(js.getString("playerlist"));
			
			for(int i=0;i<ja.length();i++)
			{
				JSONObject js_player=ja.getJSONObject(i);
				
				int seatIndex_o=js_player.getInt("seatIndex");
				if(seatIndex_o!=seatIndex)
				{
					players[seatIndex_o].index=seatIndex_o;
					players[seatIndex_o].isAnonymous=js_player.getBoolean("isAnonymous");
					players[seatIndex_o].isDisconnect=js_player.getBoolean("isDisconnect");
					players[seatIndex_o].isStart=js_player.getBoolean("isReady");
					players[seatIndex_o].nickname=URLDecoder.decode(js_player.getString("nickname"),"UTF-8");
					players[seatIndex_o].touxiang_url=js_player.getString("touxiang");
					
				}
			}
			
			hallMode=HALL_MODE_NORMAL;
			callBack.onStartGame(roomIndex, deskIndex, seatIndex, players);
			
			
			
			
		} catch (JSONException e) {
			Log.d("lichao","hall view json error:"+e.toString());
		} catch (UnsupportedEncodingException e) {
			
			Log.d("lichao","hall view UnsupportedEncodingException:"+e.toString());
		}
		*/
	}

}
