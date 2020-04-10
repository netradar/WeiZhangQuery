package heart4.netradar.lichao;



import heart4.netradar.lichao.AlertDialog.CallbackAlertDialog;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Message;
import android.text.TextPaint;
import android.util.Log;
import android.view.MotionEvent;

public class LoadingView implements CallbackAlertDialog   {

	
	boolean isShowDisconnect=false;
	
	private int star_interval=500;//
	private int click_interval=1000;
	int refresh_interval=50;

	ImagePosData background;//,room,room_pressed,quick_start;
	ImagePosData click_to_start;
	ImagePosData star;

	Point click_to_start_point=new Point(265,367);
	Point star_point=new Point(403,101);
	Point progress_point=new Point(259,317);
	
	int star_width=67,star_height=66;
	int click_to_start_width=255,click_to_start_height=28;
	
	Rect background_rect;
	Rect star_rect;
	Rect click_to_start_rect;
	
	boolean isLoaded=false;

	String progress_string="正在连接...";
	
	
	int screenWidth,screenHeight;
	
	
	float rate_x,rate_y;

	
	int star_alpha_per_frame=255/(star_interval/refresh_interval);
	int click_alpha_per_frame=255/(click_interval/refresh_interval);

	int star_alpha=0,click_alpha=100;
	
	Heart4MainActivity main_activity;
	
	TextPaint text_paint;
	Paint bitmap_paint;
	
	String nickname;
	int score;
	String notice;
	
	CallBackLoadingView callBack;
	
	AlertDialog alertDialog;
	
	String alert_str;
	
	public LoadingView(Heart4MainActivity main,CallBackLoadingView CallBack) {
		
		callBack=CallBack;
	
		main_activity=main;
		
		screenWidth  = main.getWindowManager().getDefaultDisplay().getWidth();       // 屏幕宽（像素，如：480px）   
		screenHeight = main.getWindowManager().getDefaultDisplay().getHeight();
		initImagePos();
		
		bitmap_paint=new Paint();
		bitmap_paint.setAntiAlias(true);
		bitmap_paint.setFilterBitmap(true);
		
		text_paint = new TextPaint();
		text_paint.setColor(Color.parseColor("#000000"));
		text_paint.setTextSize(20*rate_x);
		text_paint.setAntiAlias(true);
	}

	private void initImagePos() {
		
		AssetManager assets=main_activity.getResources().getAssets();
		Bitmap background_img = null;
		Bitmap star_img=null;
		Bitmap click_to_start_img=null;
		
		try {
			background_img=BitmapFactory.decodeStream(assets.open("image/red4_start.png"));
			star_img=BitmapFactory.decodeStream(assets.open("image/star.png"));
			click_to_start_img=BitmapFactory.decodeStream(assets.open("image/start_remind_txt.png"));
		} catch (IOException e) {
				
				Log.d("lichao","open assets error :"+e.toString());
			}
		rate_x=(float)screenWidth/(float)background_img.getWidth();
		rate_y=(float)screenHeight/(float)background_img.getHeight();
	
		
		background=new ImagePosData(background_img,new Point(0,0),new Point(0,0),background_img.getWidth(),background_img.getHeight(),rate_x,rate_y);
		star=new ImagePosData(star_img,new Point(0,0),star_point,star_width,star_height,rate_x,rate_y);
		click_to_start=new ImagePosData(click_to_start_img,new Point(0,0),click_to_start_point,click_to_start_width,click_to_start_height,rate_x,rate_y);
		
		progress_point.x=(int) (progress_point.x*rate_x);
		progress_point.y=(int) (progress_point.y*rate_y);
		
		background_rect=Tools.getRect(background);
		star_rect=Tools.getRect(star);
		click_to_start_rect=Tools.getRect(click_to_start);
	}


	
	
	protected void draw(Canvas canvas) {
	
		paintBackground(canvas);
		paintStar(canvas);
		paintProgress(canvas);
		
		if(isShowDisconnect)
		{
			if(alertDialog==null)
				alertDialog=new AlertDialog((float)screenWidth/(float)900,(float)screenHeight/(float)600,alert_str,main_activity,this);
			alertDialog.setString(alert_str);
			alertDialog.draw(canvas);
		}
		
		if(isLoaded)
			paintClickToStart(canvas);
	
	}
	private void paintProgress(Canvas canvas)
	{
		canvas.drawText(progress_string, progress_point.x, progress_point.y, text_paint);
	
	}
	private void paintClickToStart(Canvas canvas)
	{
		Paint paint=new Paint();
		paint.setAlpha(click_alpha);
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		
		click_alpha=click_alpha+click_alpha_per_frame;
		if(click_alpha>=255)
		{
			click_alpha=255;
			click_alpha_per_frame=-1*click_alpha_per_frame;
		}
		if(click_alpha<=0)
		{
			click_alpha=0;
			click_alpha_per_frame=-1*click_alpha_per_frame;
		}
		canvas.drawBitmap(click_to_start.bmp_src, null, click_to_start_rect,paint);
	
	}

	
	

	private void paintStar(Canvas canvas) {
		Paint paint=new Paint();
		paint.setAlpha(star_alpha);
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		
		star_alpha=star_alpha+star_alpha_per_frame;
		if(star_alpha>=255)
		{
			star_alpha=255;
			star_alpha_per_frame=-1*star_alpha_per_frame;
		}
		if(star_alpha<=0)
		{
			star_alpha=0;
			star_alpha_per_frame=-1*star_alpha_per_frame;
		}
		canvas.drawBitmap(star.bmp_src, null, star_rect,paint);
		
	}

	private void paintBackground(Canvas canvas) {
		
		canvas.drawBitmap(background.bmp_src, null,background_rect, null);
	}
	public void touch(MotionEvent event)
	{
		if(isShowDisconnect)
		{
			if(alertDialog==null)
				return;
			alertDialog.onTouch(event);
			return;
		}
	/*	if(isLoaded)
			callBack.onStartHall(score,notice);*/
	}
	public void handleMsg(Message msg)
	{
		
		switch(msg.what)
		{
		case 0://login
			processLogin((String) msg.obj);
			isLoaded=true;
			progress_string="";
			//Log.d("lichao","loading recv "+msg.obj);
			break;
		case 9://connect status
			progress_string=(String) msg.obj;
			break;
		case 12:
			progress_string="连接服务器失败";
			alert_str="连接服务器失败！重新连接吗？";
			isLoaded=false;
			processDisconnect();
			break;
		case 11:
			progress_string="连接服务器失败";
			alert_str="与服务器连接被断开！重新连接吗？";
			isLoaded=false;
			processDisconnect();
			
			break;
			
		}
	}
	private void processDisconnect() {
		
		isShowDisconnect=true;
	}

	private void processLogin(String obj) {
		
		try {
			JSONObject js=new JSONObject(obj);
			score=js.getInt("score");
			notice=URLDecoder.decode(js.getString("notice"),"UTF-8");
			if(score==-1) score=2000;
		//	callBack.onStartHall(score,notice);
			
		} catch (JSONException e) {
			
		} catch (UnsupportedEncodingException e) {
			
		}
		
		
	}
	public interface CallBackLoadingView {
		
		public void onStartHall(int score,String notice);
		public void onReconnect();

	}
	@Override
	public void OkAlertDialog() {
		isShowDisconnect=false;
		callBack.onReconnect();
		
	}

	@Override
	public void CancelAlertDialog() {
		
		isShowDisconnect=false;
	}

	public void click(MotionEvent event) {
		if(isLoaded)
			callBack.onStartHall(score,notice);
		
	}

	
}
