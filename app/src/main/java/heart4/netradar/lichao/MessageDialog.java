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
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.Layout.Alignment;
import android.view.MotionEvent;

public class MessageDialog {
	
	
	
	
	ImagePosData dialog;
	ImagePosData close_btn;
	ImagePosData[] msg=new ImagePosData[5];
	
	Point dialog_point=new Point(147,32);
	Point close_btn_point=new Point(657,55);
	Point[] msg_point={new Point(173,152),new Point(173,230),new Point(173,304),new Point(173,378),new Point(173,451)};
	
	
	int dialog_width=621,dialog_height=544;
	int close_btn_width=93,close_btn_height=93;
	int msg_width=574,msg_height=74;
	
	Rect dialog_rect;
	Rect close_btn_rect;
	
	Rect[] msg_rect=new Rect[5];
	
	Heart4MainActivity main_activity;
	CallbackMessageDialog callBack;
	
	float rate_x,rate_y;
	
	Paint bitmap_paint;
	Paint item_paint;
	
	int current_pressed=-1;
	Bitmap blackback_img;
	public MessageDialog(float rate_x,float rate_y,Heart4MainActivity main,CallbackMessageDialog CallBack) {
		
		callBack=CallBack;
		main_activity=main;
		this.rate_x=rate_x;
		this.rate_y=rate_y;
		
		
		
		
		bitmap_paint=new Paint();
		bitmap_paint.setAntiAlias(true);
		bitmap_paint.setFilterBitmap(true);
		
		item_paint=new Paint();
		item_paint.setAntiAlias(true);
		item_paint.setFilterBitmap(true);
		item_paint.setAlpha(20);
		
		initPos();
		
	}
	
	private void initPos() {
		
		Bitmap dialog_img=null;
		
		AssetManager assets=main_activity.getResources().getAssets();
		try 
		{
			dialog_img=BitmapFactory.decodeStream(assets.open("image/messagedialog.png"));
			blackback_img=BitmapFactory.decodeStream(assets.open("image/blackback.png"));
		} 
		catch (IOException e) 
		{			
		}
		
		dialog=new ImagePosData(dialog_img,new Point(0,0),dialog_point,dialog_width,dialog_height,rate_x,rate_y);
		close_btn=new ImagePosData(null,new Point(168,0),close_btn_point,close_btn_width,close_btn_height,rate_x,rate_y);
	
		for(int i=0;i<5;i++)
		{
			msg[i]=new ImagePosData(null,new Point(0,0),msg_point[i],msg_width,msg_height,rate_x,rate_y);
			msg_rect[i]=Tools.getRect(msg[i]);
		}
		dialog_rect=Tools.getRect(dialog);
		close_btn_rect=Tools.getRect(close_btn);
		
		
	}
	public void draw(Canvas canvas)
	{
		paintDialog(canvas);
		paintItem(canvas);
	}


	private void paintItem(Canvas canvas) {
		
		for(int i=0;i<5;i++)
		{
			if(current_pressed==i)
			{
				canvas.drawBitmap(blackback_img, null,msg_rect[i], item_paint);
			}
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
			case MotionEvent.ACTION_MOVE:
				OnTouchMove(event);
				break;
			case MotionEvent.ACTION_UP:
				OnTouchUp(event);
				break;
			
		
		}
	}
	
	

	private void OnTouchMove(MotionEvent event) {
		for(int i=0;i<5;i++)
		{
			if(!Tools.isInArea(event,msg[i])&&current_pressed==i)
			{
				current_pressed=-1;
				return;
			}
		}
		
		
	}
	private void OnTouchUp(MotionEvent event) {
		
		
		for(int i=0;i<5;i++)
		{
			if(Tools.isInArea(event,msg[i])&&current_pressed==i)
			{
				callBack.onSendMessageDialog(i);
				return;
			}
		}
		current_pressed=-1;
		
	}
	private void OnTouchDown(MotionEvent event) {
		
		if(Tools.isInArea(event,close_btn))
		{
			callBack.onCloseMessageDialog();
			return;
		}
		
		for(int i=0;i<5;i++)
		{
			if(Tools.isInArea(event,msg[i]))
			{
				current_pressed=i;
				return;
			}
		}
		
	}
	
	public interface CallbackMessageDialog {
		
		public void onCloseMessageDialog();
		public void onSendMessageDialog(int index);
	}
	
}
