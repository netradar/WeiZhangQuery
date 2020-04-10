package heart4.netradar.lichao;

import java.io.IOException;

import carweibo.netradar.lichao.Appstart;
import carweibo.netradar.lichao.MainTabActivity;
import carweibo.netradar.lichao.NvManager;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Paint.FontMetrics;
import android.os.Handler;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.Layout.Alignment;
import android.view.MotionEvent;

public class ProgressView {
	
	
	int anim_time=1000;
	
	ImagePosData background;
	ImagePosData anim;
	
	Point background_point=new Point();
	Point txt_point=new Point();
	Point anim_point=new Point();
	
	int start_width=45*3,start_height=24*3;
	int end_width=45,end_height=24;
	
	Rect background_rect;
	Rect anim_rect;
	
	
	Heart4MainActivity main_activity;
	
	float rate_x,rate_y;
	
	Paint bitmap_paint;
	TextPaint text_paint;
	
	String progress_str;
	int screenWidth,screenHeight;
	Bitmap background_img;
	Bitmap anim_img;
	
	int rotate_per_frame=360/20;
	int rotate_cur=0;
	
	public ProgressView(float rate_x,float rate_y,Heart4MainActivity main) {
		
		
		main_activity=main;
		this.rate_x=rate_x;
		this.rate_y=rate_y;
		bitmap_paint=new Paint();
		bitmap_paint.setAntiAlias(true);
		bitmap_paint.setFilterBitmap(true);
		
		text_paint = new TextPaint();
		text_paint.setColor(Color.parseColor("#ffffff"));
		text_paint.setTextSize(20*rate_x);
		text_paint.setAntiAlias(true);
		screenWidth  = main.getWindowManager().getDefaultDisplay().getWidth();       // ÆÁÄ»¿í£¨ÏñËØ£¬Èç£º480px£©   
		screenHeight = main.getWindowManager().getDefaultDisplay().getHeight();
	
		initPos();
		
	}
	public void setProgressText(String str)
	{
		progress_str=str;
		int txt_width=(int) text_paint.measureText(progress_str);
		int txt_height=getTxtHeight(progress_str);
		int background_height=(int) (100*rate_y);
		
		int background_width=(int) (65*rate_x+txt_width+20*2);
		
		int anim_width=(int) (65*rate_x);
		int anim_height=(int) (65*rate_y);
		
		background_point.x=(screenWidth-background_width)/2;
		background_point.y=(screenHeight-background_height)/2;
		
		anim_point.x=background_point.x+20;
		anim_point.y=background_point.y+(background_height-anim_height)/2;
		
		background_rect=new Rect(background_point.x,background_point.y,background_point.x+background_width,background_point.y+background_height);
		anim_rect=new Rect(anim_point.x,anim_point.y,anim_point.x+anim_width,anim_point.y+anim_height);
		
		txt_point.x=background_point.x+20;
		txt_point.y=background_rect.bottom-(background_height-txt_height)/2;
	}
	private void initPos() {
		
		
		
		AssetManager assets=main_activity.getResources().getAssets();
		try 
		{
			background_img=BitmapFactory.decodeStream(assets.open("image/progress_back.png"));
			anim_img=BitmapFactory.decodeStream(assets.open("image/loading.png"));
		} 
		catch (IOException e) 
		{			
		}
		
		
	}
	private int getTxtHeight(String str) {
		
		 FontMetrics fm = text_paint.getFontMetrics(); 
			
		 return (int) (fm.bottom-fm.ascent); 


	}
	public void draw(Canvas canvas)
	{
		
		paintBackground(canvas);
	//	paintAnim(canvas);
		paintText(canvas);

	}
	private void paintText(Canvas canvas) {
		canvas.drawText(progress_str, txt_point.x,txt_point.y, text_paint);
		
	}
	private void paintAnim(Canvas canvas) {
		
		Matrix matrix = new Matrix(); 
		matrix.postTranslate(anim_rect.left,anim_rect.top); 
		matrix.postRotate(rotate_cur, anim_rect.left+anim_rect.width()/2 ,anim_rect.top+anim_rect.width()/2);
		rotate_cur=rotate_cur+rotate_per_frame;
		if(rotate_cur>360) rotate_cur=0;
		
		canvas.drawBitmap(anim_img, matrix, bitmap_paint);   
		//canvas.drawBitmap(anim_img, null,anim_rect, bitmap_paint);
	}
	private void paintBackground(Canvas canvas) {
		
		canvas.drawBitmap(background_img, null,background_rect, bitmap_paint);
	}

	
}
