package heart4.netradar.lichao;

import java.io.IOException;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;

public class PlayerButton {
	
	int gameMode=1;
	boolean isReady=false;
	
	boolean isPlayPressed=false;
	boolean isPassPressed=false;
	boolean isTipsPressed=false;
	boolean isReadyPressed=false;
	boolean isAlertPressed=false;
	
	
	ImagePosData pass;
	ImagePosData tips;
	ImagePosData play;
	ImagePosData ready;
	ImagePosData alert;
	
	Point pass_point=new Point(330,363);
	Point tips_point=new Point(479,363);
	Point play_point=new Point(628,363);
	Point ready_point=new Point(301,363);
	Point alert_point=new Point(529,363);
	
	int pass_width=106,pass_height=48;
	int tips_width=106,tips_height=48;
	int play_width=106,play_height=48;
	int ready_width=195,ready_height=52;
	int alert_width=195,alert_height=52;
	
	
	Rect pass_rect,pass_pressed_rect;
	Rect tips_rect,tips_pressed_rect;
	Rect play_rect,play_pressed_rect;
	Rect ready_rect,ready_pressed_rect;
	Rect alert_rect,alert_pressed_rect;
	
	
	Heart4MainActivity main_activity;
	float rate_x,rate_y;
	Paint bitmap_paint;
	
	ButtonPressed btnCallBack;
	public PlayerButton(float rate_x,float rate_y,Heart4MainActivity main,ButtonPressed callBack) 
	{
		
		btnCallBack=callBack;
		main_activity=main;
		this.rate_x=rate_x;
		this.rate_y=rate_y;
		initImagePos();
		
		bitmap_paint=new Paint();
		bitmap_paint.setAntiAlias(true);
		bitmap_paint.setFilterBitmap(true);

		
	}
	private void initImagePos() {
		
		Bitmap choice_img=null;
		Bitmap start_img=null;
		
		
		
		
		AssetManager assets=main_activity.getResources().getAssets();
		try 
		{
			choice_img=BitmapFactory.decodeStream(assets.open("image/userchoice.png"));
			start_img=BitmapFactory.decodeStream(assets.open("image/start.png"));	
			} 
		catch (IOException e) 
		{			
		}
		
		pass=new ImagePosData(choice_img,new Point(106,0),pass_point,pass_width,pass_height,rate_x,rate_y);
		tips=new ImagePosData(choice_img,new Point(212,0),tips_point,tips_width,tips_height,rate_x,rate_y);
		play=new ImagePosData(choice_img,new Point(0,0),play_point,play_width,play_height,rate_x,rate_y);
		
		ready=new ImagePosData(start_img,new Point(195,0),ready_point,ready_width,ready_height,rate_x,rate_y);
		alert=new ImagePosData(start_img,new Point(0,0),alert_point,alert_width,alert_height,rate_x,rate_y);
		
		pass_rect=Tools.getRect(pass);
		pass_pressed_rect=Tools.getRectOffsetY(pass, Tools.BUTTON_PRESSED_OFFSET);
		tips_rect=Tools.getRect(tips);
		tips_pressed_rect=Tools.getRectOffsetY(tips, Tools.BUTTON_PRESSED_OFFSET);
		play_rect=Tools.getRect(play);
		play_pressed_rect=Tools.getRectOffsetY(play, Tools.BUTTON_PRESSED_OFFSET);
		ready_rect=Tools.getRect(ready);
		ready_pressed_rect=Tools.getRectOffsetY(ready, Tools.BUTTON_PRESSED_OFFSET);
		alert_rect=Tools.getRect(alert);
		alert_pressed_rect=Tools.getRectOffsetY(alert, Tools.BUTTON_PRESSED_OFFSET);
	}
	
	
	public void draw(Canvas canvas, PlayerProcess players)
	{
		if((gameMode==6)&&(players.currenPendingPlayer==players.bottomSeatIndex))
		{
			paintPass(canvas);
			paintPlay(canvas);
			paintTips(canvas);
		}
		if(gameMode==1||gameMode==2)
		{
			if(!players.player_bottom.isStart)
				paintReady(canvas);
			paintAlert(canvas);
			
		}
		
	}
	
	private void paintAlert(Canvas canvas) {
		
		if(isAlertPressed)
		{
			
			canvas.drawBitmap(alert.bmp_src, new Rect(alert.posInBmp.x,alert.posInBmp.y+alert.heightInBmp,alert.posInBmp.x+alert.widthInBmp,alert.posInBmp.y+alert.heightInBmp+alert.heightInBmp),alert_pressed_rect,bitmap_paint);
			
		}
		else
			canvas.drawBitmap(alert.bmp_src, new Rect(alert.posInBmp.x,alert.posInBmp.y,alert.posInBmp.x+alert.widthInBmp,alert.posInBmp.y+alert.heightInBmp),alert_rect,bitmap_paint);
	
	}
	private void paintReady(Canvas canvas) {
	
		if(isReadyPressed)
		{
			
			canvas.drawBitmap(ready.bmp_src, new Rect(ready.posInBmp.x,ready.posInBmp.y+ready.heightInBmp,ready.posInBmp.x+ready.widthInBmp,ready.posInBmp.y+ready.heightInBmp+ready.heightInBmp),ready_pressed_rect,bitmap_paint);
			
		}
		else
			canvas.drawBitmap(ready.bmp_src, new Rect(ready.posInBmp.x,ready.posInBmp.y,ready.posInBmp.x+ready.widthInBmp,ready.posInBmp.y+ready.heightInBmp),ready_rect,bitmap_paint);
	
	}
	private void paintTips(Canvas canvas) {

		if(isTipsPressed)
		{
			
			canvas.drawBitmap(tips.bmp_src, new Rect(tips.posInBmp.x,tips.posInBmp.y+tips.heightInBmp,tips.posInBmp.x+tips.widthInBmp,tips.posInBmp.y+tips.heightInBmp+tips.heightInBmp),tips_pressed_rect,bitmap_paint);
			
		}
		else
			canvas.drawBitmap(tips.bmp_src, new Rect(tips.posInBmp.x,tips.posInBmp.y,tips.posInBmp.x+tips.widthInBmp,tips.posInBmp.y+tips.heightInBmp),tips_rect,bitmap_paint);

		
	}
	private void paintPlay(Canvas canvas) {
		
		
		
		if(isPlayPressed)
		{
			
			canvas.drawBitmap(play.bmp_src, new Rect(play.posInBmp.x,play.posInBmp.y+play.heightInBmp,play.posInBmp.x+play.widthInBmp,play.posInBmp.y+play.heightInBmp+play.heightInBmp),play_pressed_rect,bitmap_paint);
			
		}
		else
			canvas.drawBitmap(play.bmp_src, new Rect(play.posInBmp.x,play.posInBmp.y,play.posInBmp.x+play.widthInBmp,play.posInBmp.y+play.heightInBmp),play_rect,bitmap_paint);
		
		
		
	}
	private void paintPass(Canvas canvas) {
		if(isPassPressed)
		{
			
			canvas.drawBitmap(pass.bmp_src, new Rect(pass.posInBmp.x,pass.posInBmp.y+pass.heightInBmp,pass.posInBmp.x+pass.widthInBmp,pass.posInBmp.y+pass.heightInBmp+pass.heightInBmp),pass_pressed_rect,bitmap_paint);
			
		}
		else
			canvas.drawBitmap(pass.bmp_src, new Rect(pass.posInBmp.x,pass.posInBmp.y,pass.posInBmp.x+pass.widthInBmp,pass.posInBmp.y+pass.heightInBmp),pass_rect,bitmap_paint);
		
		
	}
	public boolean touch(MotionEvent event,PlayerProcess players)
	{
		
		switch(event.getAction() & MotionEvent.ACTION_MASK)
		{
		
		case MotionEvent.ACTION_DOWN:
			return onTouchDown(event,players);
			
		case MotionEvent.ACTION_UP:
			return onTouchUp(event);
			
		case MotionEvent.ACTION_MOVE:
			return onTouchMove(event);
			
		}
	
		return false;
	}
	
	private boolean onTouchMove(MotionEvent event) {
		
		if(Tools.isInArea(event, play))
		{
			
			return true;
		}
		if(Tools.isInArea(event, tips))
		{
			return true;
		}
		if(Tools.isInArea(event, pass))
		{
			return true;
		}
		if(Tools.isInArea(event, ready))
		{
			return true;
		}
		if(Tools.isInArea(event, alert))
		{
			return true;
		}
		isPlayPressed=false;
		isPassPressed=false;
		isTipsPressed=false;
		isReadyPressed=false;
		isAlertPressed=false;
		
		return false;
	}
	private boolean onTouchUp(MotionEvent event) {
		
		if(isPlayPressed&&Tools.isInArea(event, play))
		{
			isPlayPressed=false;
			btnCallBack.onBtnPressed(Tools.USER_CHOICE_BTN_PLAY);
			return true;
		}
		if(isTipsPressed&&Tools.isInArea(event, tips))
		{
			isTipsPressed=false;
			btnCallBack.onBtnPressed(Tools.USER_CHOICE_BTN_TIPS);
			return true;
		}
		if(isPassPressed&&Tools.isInArea(event, pass))
		{
			isPassPressed=false;
			btnCallBack.onBtnPressed(Tools.USER_CHOICE_BTN_PASS);
			return true;
		}
		if(isReadyPressed&&Tools.isInArea(event, ready))
		{
			isReadyPressed=false;
			btnCallBack.onBtnPressed(Tools.USER_CHOICE_BTN_READY);
			return true;
		}
		if(isAlertPressed&&Tools.isInArea(event, alert))
		{
			isAlertPressed=false;
			btnCallBack.onBtnPressed(Tools.USER_CHOICE_BTN_ALERT);
			return true;
		}
		isPlayPressed=false;
		isPassPressed=false;
		isTipsPressed=false;
		isReadyPressed=false;
		isAlertPressed=false;
		
		return false;
	
		
	}
	public interface ButtonPressed {

		public void onBtnPressed(int btnIndex);
	}
	
	private boolean onTouchDown(MotionEvent event, PlayerProcess players) {
	
		if(gameMode==6&&(players.currenPendingPlayer==players.bottomSeatIndex))
		{
			if(Tools.isInArea(event, play))
			{
				isPlayPressed=true;
				return true;
			}
			if(Tools.isInArea(event, tips))
			{
				isTipsPressed=true;
				return true;
			}
			
			if(Tools.isInArea(event, pass))
			{
				isPassPressed=true;
				return true;
			}
		}
		if(gameMode==1||gameMode==2)
		{
			if(!isReady&&Tools.isInArea(event, ready))
			{
				isReadyPressed=true;
				return true;
			}
			if(Tools.isInArea(event, alert))
			{
				isAlertPressed=true;
				return true;
			}
		}
		return false;
	}
	public void click()
	{
		
	}
	public void setGameMode(int gameMode) {
		this.gameMode=gameMode;
		
	}

}
