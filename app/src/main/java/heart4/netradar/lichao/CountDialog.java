package heart4.netradar.lichao;

import java.io.IOException;

import carweibo.netradar.lichao.NvManager;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.TextPaint;
import android.view.MotionEvent;

public class CountDialog {
	
	

	
	ImagePosData dialog;
	ImagePosData close_btn;
	
	
	Point dialog_point=new Point(128,91);
	Point close_btn_point=new Point(695,112);
	Point[] player_nickname_point={new Point(181,241),new Point(181,311),new Point(181,381),new Point(181,451)};

	Point[] player_score_point={new Point(606,241),new Point(606,311),new Point(606,381),new Point(606,451)};

	
	
	int dialog_width=661,dialog_height=413;
	int close_btn_width=72,close_btn_height=72;
	
	
	Rect dialog_rect;
	
	
	Heart4MainActivity main_activity;
	CallbackCountDialog callBack;
	
	float rate_x,rate_y;
	String[] nicknameList;
	int[] scoreList;
	
	TextPaint text_paint;
	Paint bitmap_paint;
	public CountDialog(float rate_x,float rate_y,Heart4MainActivity main,CallbackCountDialog CallBack) {
		
		callBack=CallBack;
		main_activity=main;
		this.rate_x=rate_x;
		this.rate_y=rate_y;
	
		initPos();
		
		bitmap_paint=new Paint();
		bitmap_paint.setAntiAlias(true);
		bitmap_paint.setFilterBitmap(true);
		
		text_paint = new TextPaint();
		text_paint.setColor(Color.parseColor("#333333"));
		text_paint.setTextSize(35*rate_x);
		text_paint.setAntiAlias(true);
		
		
		
	}
	public void setData(String[] nickname_list,int[] score_list)
	{
		nicknameList=new String[4];
		scoreList=new int[4];
		for(int i=0;i<4;i++)
		{
			nicknameList[i]=nickname_list[i];
			scoreList[i]=score_list[i];
		}
	}
	private void initPos() {
		
		Bitmap dialog_img=null;
		AssetManager assets=main_activity.getResources().getAssets();
		try 
		{
			dialog_img=BitmapFactory.decodeStream(assets.open("image/countdialog.png"));
		} 
		catch (IOException e) 
		{			
		}
		
		dialog=new ImagePosData(dialog_img,new Point(0,0),dialog_point,dialog_width,dialog_height,rate_x,rate_y);
		close_btn=new ImagePosData(null,new Point(0,0),close_btn_point,close_btn_width,close_btn_height,rate_x,rate_y);
		
		
		dialog_rect=Tools.getRect(dialog);
		
		for(int i=0;i<4;i++)
		{
			player_nickname_point[i].x=(int) (player_nickname_point[i].x*rate_x);
			player_nickname_point[i].y=(int) (player_nickname_point[i].y*rate_y);
		
			player_score_point[i].x=(int) (player_score_point[i].x*rate_x);
			player_score_point[i].y=(int) (player_score_point[i].y*rate_y);
			
		}
		
	}
	public void draw(Canvas canvas)
	{
		paintDialog(canvas);
		paintData(canvas);
		
		
	}
	private void paintData(Canvas canvas) {
		for(int i=0;i<4;i++)
		{
			canvas.drawText(nicknameList[i], player_nickname_point[i].x
					, player_nickname_point[i].y, text_paint);
			
			canvas.drawText(String.valueOf(scoreList[i]), player_score_point[i].x
					, player_score_point[i].y, text_paint);
		}
		
	}
	private void paintDialog(Canvas canvas) {
		
		canvas.drawBitmap(dialog.bmp_src, null,dialog_rect, bitmap_paint);
	}
	
	
	public void onTouch(MotionEvent event)
	{
		switch(event.getAction() & MotionEvent.ACTION_MASK)
		{
			
			case MotionEvent.ACTION_DOWN:
				OnTouchDown(event);
				break;
			
		
		}
	}
	
	

	private void OnTouchDown(MotionEvent event) {
		
		if(Tools.isInArea(event,close_btn))
		{
			callBack.CloseCountDialog();
		}
		
	}
	
	public interface CallbackCountDialog {
		public void CloseCountDialog();
	}
	
}
