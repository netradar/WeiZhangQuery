package heart4.netradar.lichao;

import java.io.IOException;

import android.text.TextPaint;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.view.MotionEvent;

public class StartView {
	
	private int star_interval=500;//
	private int click_interval=1000;
	int refresh_interval=50;

	ImagePosData background;
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

	String progress_string="";
	
	
	
	int screenWidth,screenHeight;
	
	
	Handler main_handler;
	float rate_x,rate_y;
	
	
	
	int star_alpha_per_frame=255;///(star_interval/refresh_interval);
	int click_alpha_per_frame=255;///(click_interval/refresh_interval);

	int star_alpha=0,click_alpha=100;
	

	
	StartCallBack callBack;
	
	Heart4MainActivity main_activity;
	
	TextPaint text_paint;
	Paint bitmap_paint;
	
	public StartView(Heart4MainActivity main,StartCallBack CallBack) {
		main_activity=main;
	
		callBack=CallBack;
		
		
		screenWidth  = main.getWindowManager().getDefaultDisplay().getWidth();       // ÆÁÄ»¿í£¨ÏñËØ£¬Èç£º480px£©   
		screenHeight = main.getWindowManager().getDefaultDisplay().getHeight();
		
	
		initPos();

		
		bitmap_paint=new Paint();
		bitmap_paint.setAntiAlias(true);
		bitmap_paint.setFilterBitmap(true);
		
		text_paint = new TextPaint();
		text_paint.setColor(Color.parseColor("#f20219"));
		text_paint.setTextSize(35*rate_x);
		text_paint.setAntiAlias(true);
	
	}

	private void initPos() {
		
		AssetManager assets=main_activity.getResources().getAssets();
		
		Bitmap background_img=null;
		Bitmap star_img=null;
		Bitmap click_to_start_img=null;
		try {
			background_img=BitmapFactory.decodeStream(assets.open("image/desk.png"));
	//		star_img=BitmapFactory.decodeStream(assets.open("image/star.png"));
	//		click_to_start_img=BitmapFactory.decodeStream(assets.open("image/start_remind_txt.png"));
		} catch (IOException e) {
			
			
		}
		rate_x=(float)screenWidth/(float)background_img.getWidth();
		rate_y=(float)screenHeight/(float)background_img.getHeight();
		
		
		background=new ImagePosData(background_img,new Point(0,0),new Point(0,0),background_img.getWidth(),background_img.getHeight(),rate_x,rate_y);
	//	star=new ImagePosData(star_img,new Point(0,0),star_point,star_width,star_height,rate_x,rate_y);
	//	click_to_start=new ImagePosData(click_to_start_img,new Point(0,0),click_to_start_point,click_to_start_width,click_to_start_height,rate_x,rate_y);
		
		
//		progress_point.x=(int) (progress_point.x*rate_x);
//		progress_point.y=(int) (progress_point.y*rate_y);
		
		background_rect=Tools.getRect(background);
//		star_rect=Tools.getRect(star);
//		click_to_start_rect=Tools.getRect(click_to_start);
	
		
	}
	protected void draw(Canvas canvas) {
		
		canvas.drawBitmap(background.bmp_src, null, background_rect, bitmap_paint);
	//	paintStar(canvas);
	
	//	paintProgress(canvas);
	/*	if(isLoaded)
			paintClickToStart(canvas);*/
	}
	private void paintClickToStart(Canvas canvas)
	{
		Paint paint=new Paint();
		paint.setAlpha(star_alpha);
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

	private void paintProgress(Canvas canvas)
	{
		canvas.drawText(progress_string, progress_point.x, progress_point.y, text_paint);
	
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
	
	/*public boolean onTouch(MotionEvent event) {
		
		return true;
	}*/
	
	public void click(MotionEvent event)
	{
		
		if(!isLoaded)
			return;
		isLoaded=true;
		callBack.onTouchStart();
	}
	
	public interface StartCallBack {
		
		public void onTouchStart();

	}

/*	public void handleMessage(Message msg)
	{
		switch(msg.what)
		{
		case 0:
			isLoaded=true;
			break;
		case 9:
			progress_string=(String) msg.obj;
			break;
		}
		
	}*/

}
