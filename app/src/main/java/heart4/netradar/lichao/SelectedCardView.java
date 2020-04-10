package heart4.netradar.lichao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.Log;
import android.view.MotionEvent;

public class SelectedCardView {
	
	private static int BUTTON_PRESSED_OFFSET=3;

	
	boolean is_ok_btn_pressed=false;
	
	
	ImagePosData dialog;
	ImagePosData ok_btn;
	ImagePosData select_card;
	
	Point dialog_point=new Point(176,104);
	Point ok_btn_point=new Point(250,385);
	Point select_card_point=new Point(396,210);
	
	
	int dialog_width=543,dialog_height=416;
	
	int ok_btn_width=390,ok_btn_height=71;
	int select_card_height=141;
	
	
	
	Rect dialog_rect;
	
	Rect ok_btn_rect;
	Rect select_card_rect;
	
	Heart4MainActivity main_activity;
	float rate_x,rate_y;
	Paint bitmap_paint;
	
	
	SelectedCardCallBack callBack;
	int selected_huase,selected_num;
	Bitmap card_img;
	
	public SelectedCardView(float rate_x,float rate_y,Heart4MainActivity main,SelectedCardCallBack CallBack,int selectedCard) 
	{
	//	Log.d("lichao","CallFriendView");
		callBack=CallBack;
		
		main_activity=main;
		this.rate_x=rate_x;
		this.rate_y=rate_y;
		initImagePos();
		
		bitmap_paint=new Paint();
		bitmap_paint.setAntiAlias(true);
		bitmap_paint.setFilterBitmap(true);
		
		selected_huase=(selectedCard-1)/13;
		selected_num=selectedCard%13;
	}
	
	
	private void initImagePos() {
		
		AssetManager assets=main_activity.getResources().getAssets();
		Bitmap dialog_img = null;
	
		Bitmap ok_btn_img=null;
				
		
		
		try {
			card_img=BitmapFactory.decodeStream(assets.open("image/poker.png"));
			dialog_img=BitmapFactory.decodeStream(assets.open("image/calledcard.png"));
			ok_btn_img=BitmapFactory.decodeStream(assets.open("image/ok_big.png"));
			
			
		} catch (IOException e) {
			
			Log.d("lichao","img init error:"+e.toString());
		}
		
		dialog=new ImagePosData(dialog_img,new Point(0,0),dialog_point,dialog_width,dialog_height,rate_x,rate_y);
		ok_btn=new ImagePosData(ok_btn_img,new Point(0,0),ok_btn_point,ok_btn_width,ok_btn_height,rate_x,rate_y);
	//	select_card=new ImagePosData(null,new Point(0,0),select_card_point,select_card_width,select_card_height,rate_x,rate_y);
	
		select_card_point.x=(int) (select_card_point.x*rate_x);
		select_card_point.y=(int) (select_card_point.y*rate_y);
		
		select_card_height=(int) (select_card_height*rate_y);
		
		
		dialog_rect=Tools.getRect(dialog);
		ok_btn_rect=Tools.getRect(ok_btn);
		
		select_card_rect=new Rect(select_card_point.x,select_card_point.y,select_card_point.x+(int)((float)103/(float)128*select_card_height),select_card_point.y+select_card_height);
		
	}

	public void draw(Canvas canvas)
	{
		paintDialog(canvas);
		paintOKBtn(canvas);
		paintSelectCard(canvas);
		
	}

	private void paintSelectCard(Canvas canvas) {
		
		Rect tmp=getRect(selected_huase,selected_num);
		canvas.drawBitmap(card_img, tmp,select_card_rect,bitmap_paint);
	}

	
	private Rect getRect(int huase, int num) {
		
		int real_num=num-3;
		if(real_num<0) real_num=real_num+13;
		
		return new Rect(real_num*103,huase*128,(real_num+1)*103,(huase+1)*128);
	}
	private void paintOKBtn(Canvas canvas) {
		
		//Log.d("lichao","paintOKBtn  is_ok_btn_pressed="+is_ok_btn_pressed);
		if(is_ok_btn_pressed)
		{
			//Log.d("lichao","ok pint pressed");
			canvas.drawBitmap(ok_btn.bmp_src,Tools.getRectOffsetYInBmp(ok_btn, ok_btn.heightInBmp),new Rect(ok_btn_rect.left,ok_btn_rect.top+BUTTON_PRESSED_OFFSET,ok_btn_rect.right,ok_btn_rect.bottom+BUTTON_PRESSED_OFFSET), bitmap_paint);
		}
		else
		{
			canvas.drawBitmap(ok_btn.bmp_src,Tools.getRectOffsetYInBmp(ok_btn, 0),ok_btn_rect, bitmap_paint);

		}
	}

	
	private void paintDialog(Canvas canvas) {
		
	//	canvas.save();
	//	canvas.clipRect(dialog_rect);
		canvas.drawBitmap(dialog.bmp_src, null,dialog_rect, bitmap_paint);
	//	canvas.restore();
	}
	public interface SelectedCardCallBack {
		
		public void onSelectedCardClose();

	}
	public void touch(MotionEvent event) {
		switch(event.getAction() & MotionEvent.ACTION_MASK)
		{
		
		case MotionEvent.ACTION_DOWN:
			onTouchDown(event);
			break;
		case MotionEvent.ACTION_UP:
			onTouchUp(event);
			break;
		case MotionEvent.ACTION_MOVE:
			onTouchMove(event);
			break;
		}
	
	}

	private void onTouchUp(MotionEvent event) {
		
		if(Tools.isInArea(event, ok_btn)&&is_ok_btn_pressed)
		{
			
			is_ok_btn_pressed=false;
			onOkSelectedCard();
			return;
		}
		/*if(Tools.isInArea(event, close_btn)&&is_close_btn_pressed)
		{
			is_close_btn_pressed=false;
			
			return;
		}*/
	}
	
	public void onOkSelectedCard()
	{
		callBack.onSelectedCardClose();
	}

	private void onTouchMove(MotionEvent event) {
		
		if(!Tools.isInArea(event, ok_btn)&&is_ok_btn_pressed)
		{
			is_ok_btn_pressed=false;
			return;
		}
	
	}

	private void onTouchDown(MotionEvent event)
	{
		
	
		if(Tools.isInArea(event, ok_btn))
		{
			
			is_ok_btn_pressed=true;
			Log.d("lichao","ok pressed is_ok_btn_pressed="+is_ok_btn_pressed);
			return;
		}
	
		
	}

	
}
