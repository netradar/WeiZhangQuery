package heart4.netradar.lichao;

import java.io.IOException;

import carweibo.netradar.lichao.NvManager;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;

public class SettingDialog {
	
	


	boolean isBackgroundMusic;
	boolean isPlayAudioEffect;
	
	ImagePosData setting_dialog;
	ImagePosData setting_back_music;
	ImagePosData setting_play_effect;
	ImagePosData setting_close;
	
	Point setting_dialog_point=new Point(128,91);
	Point setting_back_music_point=new Point(651,223);
	Point setting_play_effect_point=new Point(651,369);
	Point setting_close_point=new Point(695,112);
	
	int setting_dialog_width=661,setting_dialog_height=413;
	int setting_back_music_width=66,setting_back_music_height=66;
	int setting_play_effect_width=66,setting_play_effect_height=66;
	int setting_close_width=72,setting_close_height=72;
	
	
	Rect setting_dialog_rect;
	Rect setting_back_music_rect;
	Rect setting_play_effect_rect;
	Rect setting_close_btn_rect;

	
	
	Heart4MainActivity mainActivity;
	CloseCallbackSettingDialog close_callback;
	public SettingDialog(float rate_x,float rate_y,Heart4MainActivity main,CloseCallbackSettingDialog closeCallback) {
		
		Bitmap setting_dialog_img=null;
		Bitmap setting_back_music_img=null;
		Bitmap setting_play_effect_img=null;
	
		AssetManager assets=main.getResources().getAssets();
		try 
		{
			setting_dialog_img=BitmapFactory.decodeStream(assets.open("image/settingdialog.png"));
			setting_back_music_img=BitmapFactory.decodeStream(assets.open("image/checkbox.png"));
			setting_play_effect_img=BitmapFactory.decodeStream(assets.open("image/checkbox.png"));
		} 
		catch (IOException e) 
		{			
		}
		
		setting_dialog=new ImagePosData(setting_dialog_img,new Point(0,0),setting_dialog_point,setting_dialog_width,setting_dialog_height,rate_x,rate_y);
		setting_back_music=new ImagePosData(setting_back_music_img,new Point(0,0),setting_back_music_point,setting_back_music_width,setting_back_music_height,rate_x,rate_y);
		setting_play_effect=new ImagePosData(setting_play_effect_img,new Point(0,0),setting_play_effect_point,setting_play_effect_width,setting_play_effect_height,rate_x,rate_y);
		setting_close=new ImagePosData(null,null,setting_close_point,setting_close_width,setting_close_height,rate_x,rate_y);
		
		
		setting_dialog_rect=getRect(setting_dialog);
		setting_back_music_rect=getRect(setting_back_music);
		setting_play_effect_rect=getRect(setting_play_effect);
		setting_close_btn_rect=getRect(setting_close);
		
		
		isBackgroundMusic=NvManager.getNVBoolean(main, NvManager.GAME_BACKGROUND_MUSIC, true);
		isPlayAudioEffect=NvManager.getNVBoolean(main, NvManager.GAME_PLAY_AUDIO_EFFECT, true);

		mainActivity=main;
		close_callback=closeCallback;
		
	}
	public void draw(Canvas canvas)
	{
		drawDialog(canvas);
		paintSettingCheckBox(canvas);
	}
	private void paintSettingCheckBox(Canvas canvas) {
		if(!isBackgroundMusic)
		{
			canvas.drawBitmap(setting_back_music.bmp_src, 
					new Rect(setting_back_music.posInBmp.x,
							setting_back_music.posInBmp.y,
							setting_back_music.posInBmp.x+setting_back_music.widthInBmp,
							setting_back_music.posInBmp.y+setting_back_music.heightInBmp),
					new Rect(setting_back_music.posInScreen.x,
							setting_back_music.posInScreen.y,
							setting_back_music.posInScreen.x+setting_back_music.widthInScreen,
							setting_back_music.posInScreen.y+setting_back_music.heightInScreen), null);
		}
		else
		{
			canvas.drawBitmap(setting_back_music.bmp_src, 
					new Rect(setting_back_music.posInBmp.x,
							setting_back_music.posInBmp.y+setting_back_music.heightInBmp,
							setting_back_music.posInBmp.x+setting_back_music.widthInBmp,
							setting_back_music.posInBmp.y+2*setting_back_music.heightInBmp),
					new Rect(setting_back_music.posInScreen.x,
							setting_back_music.posInScreen.y,
							setting_back_music.posInScreen.x+setting_back_music.widthInScreen,
							setting_back_music.posInScreen.y+setting_back_music.heightInScreen), null);

		}
		if(!isPlayAudioEffect)
		{
			canvas.drawBitmap(setting_play_effect.bmp_src, 
					new Rect(setting_play_effect.posInBmp.x,
							setting_play_effect.posInBmp.y,
							setting_play_effect.posInBmp.x+setting_play_effect.widthInBmp,
							setting_play_effect.posInBmp.y+setting_play_effect.heightInBmp),
					new Rect(setting_play_effect.posInScreen.x,
							setting_play_effect.posInScreen.y,
							setting_play_effect.posInScreen.x+setting_play_effect.widthInScreen,
							setting_play_effect.posInScreen.y+setting_play_effect.heightInScreen), null);
		}
		else
		{
			canvas.drawBitmap(setting_play_effect.bmp_src, 
					new Rect(setting_play_effect.posInBmp.x,
							setting_play_effect.posInBmp.y+setting_play_effect.heightInBmp,
							setting_play_effect.posInBmp.x+setting_play_effect.widthInBmp,
							setting_play_effect.posInBmp.y+2*setting_play_effect.heightInBmp),
					new Rect(setting_play_effect.posInScreen.x,
							setting_play_effect.posInScreen.y,
							setting_play_effect.posInScreen.x+setting_play_effect.widthInScreen,
							setting_play_effect.posInScreen.y+setting_play_effect.heightInScreen), null);

		}
		
		
	}
	private void drawDialog(Canvas canvas) {
		
		canvas.drawBitmap(setting_dialog.bmp_src, null,setting_dialog_rect, null);
	}
	
	public void onClick(MotionEvent event)
	{
		if(Tools.isInArea(event,setting_close))
		{
			close_callback.CloseSettingDialog();
		}
	}
	public void onTouch(MotionEvent event)
	{
		switch(event.getAction() & MotionEvent.ACTION_MASK)
		{
			case MotionEvent.ACTION_UP:
				onTouchUp(event);
				break;
			
			case MotionEvent.ACTION_DOWN:
				OnTouchDown(event);
				break;
			case MotionEvent.ACTION_MOVE:
				OnMove(event);
				break;
			
		
		}
	}
	
	

	private void OnMove(MotionEvent event) {
		
		
	}
	private void OnTouchDown(MotionEvent event) {
		if(Tools.isInArea(event,setting_back_music))
		{
			isBackgroundMusic=!isBackgroundMusic;
		}
		if(Tools.isInArea(event,setting_play_effect))
		{
			isPlayAudioEffect=!isPlayAudioEffect;
		}
		
	}
	private void onTouchUp(MotionEvent event) {
		
		
	}
	private Rect getRect(ImagePosData data) {
		return new Rect(data.posInScreen.x,data.posInScreen.y,data.posInScreen.x+data.widthInScreen,data.posInScreen.y+data.heightInScreen);
		
	}
	
	public interface CloseCallbackSettingDialog {
		public void CloseSettingDialog();
	}
	
}
