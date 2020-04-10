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
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.Layout.Alignment;
import android.view.MotionEvent;

public class HostView {
	
	boolean isFinished=false;

	
	ImagePosData start;
	ImagePosData end;
	
	
	Point start_point;//=new Point(147,32);
//	Point[] end_point={new Point(408,12),new Point(54,202),new Point(802,202),new Point(51,495)};//=new Point(657,55);
	Point[] end_point={new Point(51,495),new Point(54,202),new Point(408,12),new Point(802,202)};//=new Point(657,55);
	
	
	int start_width=45*3,start_height=24*3;
	int end_width=45,end_height=24;
	
	Rect start_rect;
	Rect end_rect;
	
	
	Heart4MainActivity main_activity;
	CallbackHostView callBack;
	
	float rate_x,rate_y;
	
	Paint bitmap_paint;
	
	Bitmap host_img;
	int anim_time=800;
	int start_left_time=600;
	int refresh_interval=50;
	int cur_frame=0;
	int frame_count=anim_time/refresh_interval;
	int first_frame_num=start_left_time/refresh_interval;
	
	Rect[] cur_rect=new Rect[frame_count];
	
	public HostView(float rate_x,float rate_y,Heart4MainActivity main,CallbackHostView CallBack) {
		
		callBack=CallBack;
		main_activity=main;
		this.rate_x=rate_x;
		this.rate_y=rate_y;
		bitmap_paint=new Paint();
		bitmap_paint.setAntiAlias(true);
		bitmap_paint.setFilterBitmap(true);
		
		start_point=new Point((900-start_width)/2,(600-start_height)/2);
		
		initPos();
		
	}
	
	public void setEndPlayer(int index)
	{
		isFinished=false;
		start=new ImagePosData(null,new Point(0,0),start_point,start_width,start_height,rate_x,rate_y);
		end=new ImagePosData(null,new Point(0,0),end_point[index],end_width,end_height,rate_x,rate_y);
	
		start_rect=Tools.getRect(start);
		end_rect=Tools.getRect(end);
		
		for(int i=0;i<frame_count;i++)
			cur_rect[i]=new Rect(start_rect.left-((start_rect.left-end_rect.left)/frame_count)*i,
					start_rect.top-((start_rect.top-end_rect.top)/frame_count)*i,
					start_rect.right-((start_rect.right-end_rect.right)/frame_count)*i,
					start_rect.bottom-((start_rect.bottom-end_rect.bottom)/frame_count)*i
					);
		
		
		
	}
	private void initPos() {
		
	
		
		AssetManager assets=main_activity.getResources().getAssets();
		try 
		{
			host_img=BitmapFactory.decodeStream(assets.open("image/host.png"));
		} 
		catch (IOException e) 
		{			
		}
		
	}
	public void draw(Canvas canvas)
	{
		if(!isFinished)
			paintBmp(canvas);

	}

	private void paintBmp(Canvas canvas) {
		
		canvas.drawBitmap(host_img, null,cur_rect[cur_frame], bitmap_paint);
		if(first_frame_num>0)
			first_frame_num--;
		else
			cur_frame++;
		if(cur_frame>=frame_count)
		{
			isFinished=true;
			callBack.onFinishHostView();
		}
	}
	public interface CallbackHostView {
		
		public void onFinishHostView();
		
	}
	
}
