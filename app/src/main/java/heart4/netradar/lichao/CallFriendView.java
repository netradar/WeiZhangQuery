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

public class CallFriendView {
	
	private static int BUTTON_PRESSED_OFFSET=3;

	boolean is_caohua_btn_pressed=true;
	boolean is_fangpian_btn_pressed=false;
	boolean is_heitao_btn_pressed=false;
	boolean is_hongtao_btn_pressed=false;
	boolean is_btn4_pressed=true;
	boolean is_btn5_pressed=false;
	boolean is_btn6_pressed=false;
	boolean is_btn7_pressed=false;
	boolean is_btn8_pressed=false;
	boolean is_btn9_pressed=false;
	boolean is_btn10_pressed=false;
	boolean is_ok_btn_pressed=false;
	boolean is_close_btn_pressed=false;
	
	ImagePosData dialog;
	ImagePosData close_btn;
	ImagePosData caohua_btn;
	ImagePosData fangpian_btn;
	ImagePosData heitao_btn;
	ImagePosData hongtao_btn;
	ImagePosData btn4;
	ImagePosData btn5;
	ImagePosData btn6;
	ImagePosData btn7;
	ImagePosData btn8;
	ImagePosData btn9;
	ImagePosData btn10;
	ImagePosData ok_btn;
	ImagePosData select_card;
	
	Point dialog_point=new Point(115,6);
	Point close_btn_point=new Point(679,27);
	Point heitao_btn_point=new Point(288,220);
	Point hongtao_btn_point=new Point(367,220);
	Point caohua_btn_point=new Point(446,220);
	Point fangpian_btn_point=new Point(525,220);
	
	Point btn4_point=new Point(287,320);
	Point btn5_point=new Point(366,320);
	Point btn6_point=new Point(445,320);
	Point btn7_point=new Point(524,320);
	Point btn8_point=new Point(287,386);
	Point btn9_point=new Point(366,386);
	Point btn10_point=new Point(445,386);
	Point ok_btn_point=new Point(253,474);
	Point select_card_point=new Point(656,354);
	Point my_card_point=new Point(289,108);
	Point time_left_point=new Point(520,524);
	
	int dialog_width=661,dialog_height=592;
	int close_btn_width=71,close_btn_height=71;
	int card_btn_width=64,card_btn_height=64;
	int btn_num_width=61,btn_num_height=25;
	int ok_btn_width=390,ok_btn_height=71;
	int select_card_height=93;
	int my_card_width=442,my_card_height=88;
	
	
	Rect dialog_rect;
	Rect close_btn_rect;
	Rect caohua_btn_rect;
	Rect fangpian_btn_rect;
	Rect heitao_btn_rect;
	Rect hongtao_btn_rect;
	Rect btn4_rect;
	Rect btn5_rect;
	Rect btn6_rect;
	Rect btn7_rect;
	Rect btn8_rect;
	Rect btn9_rect;
	Rect btn10_rect;
	Rect ok_btn_rect;
	Rect select_card_rect;
	
	Heart4MainActivity main_activity;
	float rate_x,rate_y;
	Paint bitmap_paint;
	TextPaint text_paint;
	
	CallFirendCallBack callBack;
	
	List<CardInfo> myCardList;
	Bitmap card_img;
	
	int selected_huase=0,selected_num=4;
	
	int time_left;
	
	
	public CallFriendView(float rate_x,float rate_y,Heart4MainActivity main,CallFirendCallBack CallBack, PlayerProcess players) 
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
		
		myCardList=new ArrayList<CardInfo>();
		initMyCardList(players.player_bottom.card);
		
		text_paint=new TextPaint();
		text_paint.setColor(Color.parseColor("#333333"));
		text_paint.setTextSize(22*rate_x);
		text_paint.setAntiAlias(true);
		
	}
	int[] seqBigToSmall={3,2,1,0,12,11,10,9,8,7,6,5,4};
	
	private void initMyCardList(int[] card_list) {
		my_card_point.x=(int) (my_card_point.x*rate_x);
		my_card_point.y=(int) (my_card_point.y*rate_y);
		my_card_width=(int) (my_card_width*rate_x);
		my_card_height=(int) (my_card_height*rate_y);
		
		int card_width,card_height;
		
		card_height=my_card_height;
		card_width=(int) (((float)103/(float)128)*card_height);
		
		int gap=(my_card_width-card_width)/(card_list.length-1);
		int length=card_list.length;
		
		
		for(int i=0;i<seqBigToSmall.length;i++)
		{
			for(int j=0;j<length;j++)
			{
				if((card_list[j]%13)==seqBigToSmall[i])
				{
					CardInfo card=new CardInfo();
					card.num=(card_list[j]%13);
					card.huase=(card_list[j]-1)/13;
					
					card.pos=new Point(my_card_point.x+gap*myCardList.size(),my_card_point.y);
					
				
					
					card.rect_paint=new Rect(card.pos.x,card.pos.y,card.pos.x+card_width,card.pos.y+card_height);
				
					myCardList.add(card);
					
				}
				
			}
		}
		
		
	}

	private void initImagePos() {
		
		AssetManager assets=main_activity.getResources().getAssets();
		Bitmap dialog_img = null;
		Bitmap huase_img=null;
		Bitmap radio_img=null;
		Bitmap ok_btn_img=null;
				
		
		
		try {
			card_img=BitmapFactory.decodeStream(assets.open("image/poker.png"));
			dialog_img=BitmapFactory.decodeStream(assets.open("image/selectpartner.png"));
			huase_img=BitmapFactory.decodeStream(assets.open("image/huase.png"));
			radio_img=BitmapFactory.decodeStream(assets.open("image/radiobutton.png"));
			ok_btn_img=BitmapFactory.decodeStream(assets.open("image/ok_big.png"));
			
			
		} catch (IOException e) {
			
			Log.d("lichao","img init error:"+e.toString());
		}
		
		dialog=new ImagePosData(dialog_img,new Point(0,0),dialog_point,dialog_width,dialog_height,rate_x,rate_y);
		close_btn=new ImagePosData(null,new Point(0,0),close_btn_point,close_btn_width,close_btn_height,rate_x,rate_y);
		caohua_btn=new ImagePosData(huase_img,new Point(0,0),caohua_btn_point,card_btn_width,card_btn_height,rate_x,rate_y);
		fangpian_btn=new ImagePosData(huase_img,new Point(64,0),fangpian_btn_point,card_btn_height,card_btn_height,rate_x,rate_y);
		heitao_btn=new ImagePosData(huase_img,new Point(128,0),heitao_btn_point,card_btn_height,card_btn_height,rate_x,rate_y);
		hongtao_btn=new ImagePosData(huase_img,new Point(192,0),hongtao_btn_point,card_btn_height,card_btn_height,rate_x,rate_y);
		btn4=new ImagePosData(radio_img,new Point(0,0),btn4_point,btn_num_width,btn_num_height,rate_x,rate_y);
		btn5=new ImagePosData(radio_img,new Point(0,0),btn5_point,btn_num_width,btn_num_height,rate_x,rate_y);
		btn6=new ImagePosData(radio_img,new Point(0,0),btn6_point,btn_num_width,btn_num_height,rate_x,rate_y);
		btn7=new ImagePosData(radio_img,new Point(0,0),btn7_point,btn_num_width,btn_num_height,rate_x,rate_y);
		btn8=new ImagePosData(radio_img,new Point(0,0),btn8_point,btn_num_width,btn_num_height,rate_x,rate_y);
		btn9=new ImagePosData(radio_img,new Point(0,0),btn9_point,btn_num_width,btn_num_height,rate_x,rate_y);
		btn10=new ImagePosData(radio_img,new Point(0,0),btn10_point,btn_num_width,btn_num_height,rate_x,rate_y);
		ok_btn=new ImagePosData(ok_btn_img,new Point(0,0),ok_btn_point,ok_btn_width,ok_btn_height,rate_x,rate_y);
	//	select_card=new ImagePosData(null,new Point(0,0),select_card_point,select_card_width,select_card_height,rate_x,rate_y);
	
		select_card_point.x=(int) (select_card_point.x*rate_x);
		select_card_point.y=(int) (select_card_point.y*rate_y);
		
		select_card_height=(int) (select_card_height*rate_y);
		time_left_point.x=(int) (time_left_point.x*rate_x);
		time_left_point.y=(int) (time_left_point.y*rate_y);
		
		
		dialog_rect=Tools.getRect(dialog);
		close_btn_rect=Tools.getRect(close_btn);
		caohua_btn_rect=Tools.getRect(caohua_btn);
		fangpian_btn_rect=Tools.getRect(fangpian_btn);
		heitao_btn_rect=Tools.getRect(heitao_btn);
		hongtao_btn_rect=Tools.getRect(hongtao_btn);
		btn4_rect=Tools.getRect(btn4);
		btn5_rect=Tools.getRect(btn5);
		btn6_rect=Tools.getRect(btn6);
		btn7_rect=Tools.getRect(btn7);
		btn8_rect=Tools.getRect(btn8);
		btn9_rect=Tools.getRect(btn9);
		btn10_rect=Tools.getRect(btn10);
		ok_btn_rect=Tools.getRect(ok_btn);
		
		select_card_rect=new Rect(select_card_point.x,select_card_point.y,select_card_point.x+(int)((float)103/(float)128*select_card_height),select_card_point.y+select_card_height);
		
		rect_test1=Tools.getRectOffsetYInBmp(caohua_btn, caohua_btn.heightInBmp);
		rect_test2=Tools.getRectOffsetYInBmp(caohua_btn, 0);
	}

	Rect rect_test1,rect_test2;
	public void draw(Canvas canvas)
	{
	//	Log.d("lichao","time is "+System.currentTimeMillis());
		paintDialog(canvas);
	//	synchronized (heitao_btn)
		{
			paintHuase(canvas);
	//		heitao_btn.notify();
		}
		paintNum(canvas);
		paintOKBtn(canvas);
		paintMyCard(canvas);
		paintSelectCard(canvas);
		paintTimeLeft(canvas);
	//	Log.d("lichao","time after is "+System.currentTimeMillis());
		
	}
	private void paintTimeLeft(Canvas canvas) {

		canvas.drawText(time_left+"Ãë", time_left_point.x,time_left_point.y, text_paint);
		
	}

	private void paintSelectCard(Canvas canvas) {
		
		Rect tmp=getRect(selected_huase,selected_num);
		canvas.drawBitmap(card_img, tmp,select_card_rect,bitmap_paint);
	}

	private void paintMyCard(Canvas canvas) {
		for(CardInfo info:myCardList)
		{
			Rect tmp=getRect(info.huase,info.num);
			canvas.drawBitmap(card_img, tmp,info.rect_paint,bitmap_paint);
			
		}
		
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

	private void paintNum(Canvas canvas) {
		
		if(!is_btn4_pressed)
		{
			canvas.drawBitmap(btn4.bmp_src,Tools.getRectOffsetYInBmp(btn4, btn4.heightInBmp),btn4_rect, bitmap_paint);
		}
		else
		{
			canvas.drawBitmap(btn4.bmp_src,Tools.getRectOffsetYInBmp(btn4, 0),btn4_rect, bitmap_paint);

		}
		if(!is_btn5_pressed)
		{
			canvas.drawBitmap(btn5.bmp_src,Tools.getRectOffsetYInBmp(btn5, btn5.heightInBmp),btn5_rect, bitmap_paint);
		}
		else
		{
			canvas.drawBitmap(btn5.bmp_src,Tools.getRectOffsetYInBmp(btn5, 0),btn5_rect, bitmap_paint);

		}
		if(!is_btn6_pressed)
		{
			canvas.drawBitmap(btn6.bmp_src,Tools.getRectOffsetYInBmp(btn6, btn6.heightInBmp),btn6_rect, bitmap_paint);
		}
		else
		{
			canvas.drawBitmap(btn6.bmp_src,Tools.getRectOffsetYInBmp(btn6, 0),btn6_rect, bitmap_paint);

		}
		if(!is_btn7_pressed)
		{
			canvas.drawBitmap(btn7.bmp_src,Tools.getRectOffsetYInBmp(btn7, btn7.heightInBmp),btn7_rect, bitmap_paint);
		}
		else
		{
			canvas.drawBitmap(btn7.bmp_src,Tools.getRectOffsetYInBmp(btn7, 0),btn7_rect, bitmap_paint);

		}
		if(!is_btn8_pressed)
		{
			canvas.drawBitmap(btn8.bmp_src,Tools.getRectOffsetYInBmp(btn8, btn8.heightInBmp),btn8_rect, bitmap_paint);
		}
		else
		{
			canvas.drawBitmap(btn8.bmp_src,Tools.getRectOffsetYInBmp(btn8, 0),btn8_rect, bitmap_paint);

		}
		if(!is_btn9_pressed)
		{
			canvas.drawBitmap(btn9.bmp_src,Tools.getRectOffsetYInBmp(btn9, btn9.heightInBmp),btn9_rect, bitmap_paint);
		}
		else
		{
			canvas.drawBitmap(btn9.bmp_src,Tools.getRectOffsetYInBmp(btn9, 0),btn9_rect, bitmap_paint);

		}
		if(!is_btn10_pressed)
		{
			canvas.drawBitmap(btn10.bmp_src,Tools.getRectOffsetYInBmp(btn10, btn10.heightInBmp),btn10_rect, bitmap_paint);
		}
		else
		{
			canvas.drawBitmap(btn10.bmp_src,Tools.getRectOffsetYInBmp(btn10, 0),btn10_rect, bitmap_paint);

		}
	}

	private void paintHuase(Canvas canvas) {
		
		if(is_caohua_btn_pressed)
		{
	//		canvas.drawBitmap(caohua_btn.bmp_src,Tools.getRectOffsetYInBmp(caohua_btn, caohua_btn.heightInBmp),caohua_btn_rect, bitmap_paint);
			canvas.save();
			canvas.clipRect(caohua_btn_rect);
			canvas.drawBitmap(caohua_btn.bmp_src,rect_test1,caohua_btn_rect, bitmap_paint);
			canvas.restore();
		}
		else
		{
			canvas.save();
			canvas.clipRect(caohua_btn_rect);
		//	canvas.drawBitmap(caohua_btn.bmp_src,Tools.getRectOffsetYInBmp(caohua_btn, 0),caohua_btn_rect, bitmap_paint);
			canvas.drawBitmap(caohua_btn.bmp_src,rect_test2,caohua_btn_rect, bitmap_paint);
			canvas.restore();
		}
		if(is_fangpian_btn_pressed)
		{
			canvas.drawBitmap(fangpian_btn.bmp_src,Tools.getRectOffsetYInBmp(fangpian_btn, fangpian_btn.heightInBmp),fangpian_btn_rect, bitmap_paint);

		}
		else
		{
			canvas.drawBitmap(fangpian_btn.bmp_src,Tools.getRectOffsetYInBmp(fangpian_btn, 0),fangpian_btn_rect, bitmap_paint);

		}
		if(is_heitao_btn_pressed)
		{
			
			canvas.drawBitmap(heitao_btn.bmp_src,Tools.getRectOffsetYInBmp(heitao_btn, heitao_btn.heightInBmp),heitao_btn_rect, bitmap_paint);

		}
		else
		{
			//Log.d("lichao","heitao not pressed");
			canvas.drawBitmap(heitao_btn.bmp_src,Tools.getRectOffsetYInBmp(heitao_btn, 0),heitao_btn_rect, bitmap_paint);

		}
		if(is_hongtao_btn_pressed)
		{
			canvas.drawBitmap(hongtao_btn.bmp_src,Tools.getRectOffsetYInBmp(hongtao_btn, hongtao_btn.heightInBmp),hongtao_btn_rect, bitmap_paint);

		}
		else
		{
			canvas.drawBitmap(hongtao_btn.bmp_src,Tools.getRectOffsetYInBmp(hongtao_btn, 0),hongtao_btn_rect, bitmap_paint);

		}
	}

	private void paintDialog(Canvas canvas) {
		
	//	canvas.save();
	//	canvas.clipRect(dialog_rect);
		canvas.drawBitmap(dialog.bmp_src, null,dialog_rect, bitmap_paint);
	//	canvas.restore();
	}
	public interface CallFirendCallBack {
		
		public void onCallFriendClose(int selectedCard);

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
			onOkCallFriend();
			return;
		}
		/*if(Tools.isInArea(event, close_btn)&&is_close_btn_pressed)
		{
			is_close_btn_pressed=false;
			
			return;
		}*/
	}
	
	public void onOkCallFriend()
	{
		callBack.onCallFriendClose(selected_huase*13+selected_num);
	}

	private void onTouchMove(MotionEvent event) {
		
/*		if(!Tools.isInArea(event, caohua_btn)&&is_caohua_btn_pressed)
		{
			is_caohua_btn_pressed=false;
			return;
		}
		if(!Tools.isInArea(event, fangpian_btn)&&is_fangpian_btn_pressed)
		{
			is_fangpian_btn_pressed=true;
			return;
		}
		if(!Tools.isInArea(event, heitao_btn)&&is_heitao_btn_pressed)
		{
			is_heitao_btn_pressed=true;
			return;
		}
		if(!Tools.isInArea(event, hongtao_btn)&&is_hongtao_btn_pressed)
		{
			is_hongtao_btn_pressed=true;
			return;
		}
		if(!Tools.isInArea(event, btn4)&&is_btn4_pressed)
		{
			is_btn4_pressed=true;
			return;
		}
		if(!Tools.isInArea(event, btn5)&&is_btn5_pressed)
		{
			is_btn5_pressed=true;
			return;
		}
		if(!Tools.isInArea(event, btn6)&&is_btn6_pressed)
		{
			is_btn6_pressed=true;
			return;
		}
		if(!Tools.isInArea(event, btn7)&&is_btn7_pressed)
		{
			is_btn7_pressed=true;
			return;
		}
		if(!Tools.isInArea(event, btn8)&&is_btn8_pressed)
		{
			is_btn8_pressed=true;
			return;
		}
		if(!Tools.isInArea(event, btn9)&&is_btn9_pressed)
		{
			is_btn9_pressed=true;
			return;
		}
		if(!Tools.isInArea(event, btn10)&&is_btn10_pressed)
		{
			is_btn10_pressed=true;
			return;
		}*/
		if(!Tools.isInArea(event, ok_btn)&&is_ok_btn_pressed)
		{
			is_ok_btn_pressed=false;
			return;
		}
		/*if(!Tools.isInArea(event, close_btn)&&is_close_btn_pressed)
		{
			is_close_btn_pressed=false;
			return;
		}*/
	}

	private void onTouchDown(MotionEvent event) {
		
		
	//	synchronized (heitao_btn)
		{
		if(Tools.isInArea(event, caohua_btn))
		{
			initHuaseButton();
			is_caohua_btn_pressed=true;
			selected_huase=2;
			return;
		}
		if(Tools.isInArea(event, fangpian_btn))
		{
			initHuaseButton();
			is_fangpian_btn_pressed=true;
			selected_huase=3;
			return;
		}
		if(Tools.isInArea(event, heitao_btn))
		{
			initHuaseButton();
			is_heitao_btn_pressed=true;
			selected_huase=0;
			return;
		}
		if(Tools.isInArea(event, hongtao_btn))
		{
			initHuaseButton();
			is_hongtao_btn_pressed=true;
			selected_huase=1;
			return;
		}
		}
		if(Tools.isInArea(event, btn4))
		{
			initNumButton();
			is_btn4_pressed=true;
			selected_num=4;
			return;
		}
		if(Tools.isInArea(event, btn5))
		{
			initNumButton();
			is_btn5_pressed=true;
			selected_num=5;
			return;
		}
		if(Tools.isInArea(event, btn6))
		{
			initNumButton();
			is_btn6_pressed=true;
			selected_num=6;
			return;
		}
		if(Tools.isInArea(event, btn7))
		{
			initNumButton();
			is_btn7_pressed=true;
			selected_num=7;
			return;
		}
		if(Tools.isInArea(event, btn8))
		{
			initNumButton();
			is_btn8_pressed=true;
			selected_num=8;
			return;
		}
		if(Tools.isInArea(event, btn9))
		{
			initNumButton();
			is_btn9_pressed=true;
			selected_num=9;
			return;
		}
		if(Tools.isInArea(event, btn10))
		{
			initNumButton();
			is_btn10_pressed=true;
			selected_num=10;
			return;
		}
		if(Tools.isInArea(event, ok_btn))
		{
			
			is_ok_btn_pressed=true;
			Log.d("lichao","ok pressed is_ok_btn_pressed="+is_ok_btn_pressed);
			return;
		}
		/*if(Tools.isInArea(event, close_btn))
		{
			is_close_btn_pressed=true;
			return;
		}*/
		
	}

	private void initNumButton() {
		is_btn4_pressed=false;
		is_btn5_pressed=false;
		is_btn6_pressed=false;
		is_btn7_pressed=false;
		is_btn8_pressed=false;
		is_btn9_pressed=false;
		is_btn10_pressed=false;
		
	}

	private void initHuaseButton() {
		is_caohua_btn_pressed=false;
		is_fangpian_btn_pressed=false;
		is_heitao_btn_pressed=false;
		is_hongtao_btn_pressed=false;
		
	}

	/*private void initAllButton() {
		
		
		is_ok_btn_pressed=false;
		is_close_btn_pressed=false;
		
	}
*/
	public void click(MotionEvent arg0) {
		
		
	}
	
	
}
