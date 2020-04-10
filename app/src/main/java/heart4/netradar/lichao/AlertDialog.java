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

public class AlertDialog {
	
	
	private static int BUTTON_PRESSED_OFFSET=3;
	
	boolean isOkBtnPressed=false;
	boolean isCancelBtnPressed=false;

	
	ImagePosData dialog;
	ImagePosData ok_btn;
	ImagePosData cancel_btn;
	ImagePosData txt;
	
	Point dialog_point=new Point(128,91);
	Point ok_btn_point=new Point(536,392);
	Point cancel_btn_point=new Point(221,392);
	Point txt_point=new Point(196,200);
	
	
	int dialog_width=661,dialog_height=413;
	int btn_width=168,btn_height=70;
	int txt_width=528,txt_height=185;
	
	Rect dialog_rect;
	Rect ok_btn_rect;
	Rect cancel_btn_rect;
	Rect txt_rect;
	
	Heart4MainActivity main_activity;
	CallbackAlertDialog callBack;
	
	float rate_x,rate_y;
	String[] nicknameList;
	int[] scoreList;
	
	TextPaint text_paint;
	Paint bitmap_paint;
	String hint;
	
	StaticLayout txt_layout;
	
	public AlertDialog(float rate_x,float rate_y,String Hint,Heart4MainActivity main,CallbackAlertDialog CallBack) {
		
		callBack=CallBack;
		main_activity=main;
		this.rate_x=rate_x;
		this.rate_y=rate_y;
		hint=Hint;
		
		
		
		bitmap_paint=new Paint();
		bitmap_paint.setAntiAlias(true);
		bitmap_paint.setFilterBitmap(true);
		
		text_paint = new TextPaint();
		text_paint.setColor(Color.parseColor("#000000"));
		text_paint.setTextSize(35*rate_x);
		text_paint.setAntiAlias(true);
		
		initPos();
		
	}
	public void setString(String str)
	{
		hint=str;
		txt_layout=new StaticLayout(hint,text_paint,txt_rect.width(),Alignment.ALIGN_NORMAL,1.2F,0,false);

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
		Bitmap btn_img=null;
		AssetManager assets=main_activity.getResources().getAssets();
		try 
		{
			dialog_img=BitmapFactory.decodeStream(assets.open("image/alertdialog.png"));
			btn_img=BitmapFactory.decodeStream(assets.open("image/okcancel_btn.png"));
		} 
		catch (IOException e) 
		{			
		}
		
		dialog=new ImagePosData(dialog_img,new Point(0,0),dialog_point,dialog_width,dialog_height,rate_x,rate_y);
		ok_btn=new ImagePosData(btn_img,new Point(168,0),ok_btn_point,btn_width,btn_height,rate_x,rate_y);
		cancel_btn=new ImagePosData(btn_img,new Point(0,0),cancel_btn_point,btn_width,btn_height,rate_x,rate_y);
		txt=new ImagePosData(null,new Point(0,0),txt_point,txt_width,txt_height,rate_x,rate_y);
		
		dialog_rect=Tools.getRect(dialog);
		ok_btn_rect=Tools.getRect(ok_btn);
		cancel_btn_rect=Tools.getRect(cancel_btn);
		txt_rect=Tools.getRect(txt);
		
		txt_point.x=(int) (txt_point.x*rate_x);
		txt_point.y=(int) (txt_point.y*rate_y);
		
		txt_layout=new StaticLayout(hint,text_paint,txt_rect.width(),Alignment.ALIGN_NORMAL,1.2F,0,false);

	}
	public void draw(Canvas canvas)
	{
		paintDialog(canvas);
		paintButton(canvas);
		paintHint(canvas);
		
		
	}

	private void paintButton(Canvas canvas) {
		
		if(isOkBtnPressed)
		{
			//Log.d("lichao","ok pint pressed");
			canvas.drawBitmap(ok_btn.bmp_src,Tools.getRectOffsetYInBmp(ok_btn, ok_btn.heightInBmp),new Rect(ok_btn_rect.left,ok_btn_rect.top+BUTTON_PRESSED_OFFSET,ok_btn_rect.right,ok_btn_rect.bottom+BUTTON_PRESSED_OFFSET), bitmap_paint);
		}
		else
		{
			canvas.drawBitmap(ok_btn.bmp_src,Tools.getRectOffsetYInBmp(ok_btn, 0),ok_btn_rect, bitmap_paint);

		}
		if(isCancelBtnPressed)
		{
			//Log.d("lichao","ok pint pressed");
			canvas.drawBitmap(cancel_btn.bmp_src,Tools.getRectOffsetYInBmp(cancel_btn, cancel_btn.heightInBmp),new Rect(cancel_btn_rect.left,cancel_btn_rect.top+BUTTON_PRESSED_OFFSET,cancel_btn_rect.right,cancel_btn_rect.bottom+BUTTON_PRESSED_OFFSET), bitmap_paint);
		}
		else
		{
			canvas.drawBitmap(cancel_btn.bmp_src,Tools.getRectOffsetYInBmp(cancel_btn, 0),cancel_btn_rect, bitmap_paint);

		}
	}
	private void paintHint(Canvas canvas) {
		
		canvas.save();
		canvas.clipRect(txt_rect);
		canvas.translate(txt_point.x, txt_point.y);
		txt_layout.draw(canvas);
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
		if(!Tools.isInArea(event,ok_btn)&&isOkBtnPressed)
		{
			isOkBtnPressed=false;
			return;
		}
		if(!Tools.isInArea(event,cancel_btn)&&isCancelBtnPressed)
		{
			isCancelBtnPressed=false;
			return;
		}
		
	}
	private void OnTouchUp(MotionEvent event) {
		
		if(Tools.isInArea(event,ok_btn)&&isOkBtnPressed)
		{
			isOkBtnPressed=false;
			callBack.OkAlertDialog();
			return;
		}
		if(Tools.isInArea(event,cancel_btn)&&isCancelBtnPressed)
		{
			isCancelBtnPressed=false;
			callBack.CancelAlertDialog();
			return;
		}
	}
	private void OnTouchDown(MotionEvent event) {
		
		if(Tools.isInArea(event,ok_btn))
		{
			isOkBtnPressed=true;
			return;
		}
		if(Tools.isInArea(event,cancel_btn))
		{
			isCancelBtnPressed=true;
			return;
		}
		
	}
	
	public interface CallbackAlertDialog {
		public void OkAlertDialog();
		public void CancelAlertDialog();
	}
	
}
