package heart4.netradar.lichao;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.Layout.Alignment;
import android.util.Log;
import android.view.MotionEvent;

public class HelpDialog {
	
	boolean isHelpTxtDrag=false;

	ImagePosData help_dialog;
	ImagePosData help_txt;
	ImagePosData help_close;
	
	
	Point help_dialog_point=new Point(19,41);
	Point help_txt_point=new Point(50,155);
	Point help_txt_draw_point=new Point(50,155);
	Point help_close_point=new Point(755,64);
	
	int help_dialog_width=860,help_dialog_height=530;
	int help_txt_width=800,help_txt_height=380;
	int help_close_width=94,help_close_height=94;
	
	Rect help_dialog_rect;
	Rect help_txt_rect;
	Rect help_close_rect;
	
	
	CloseCallbackHelpDialog close_callback;
	
	StaticLayout help_txt_layout;
	String help_string;
	int top_help_txt_y,bottom_help_txt_y;
	
	float start_y;
	
	public HelpDialog(float rate_x,float rate_y,Heart4MainActivity main,CloseCallbackHelpDialog closeCallback) {
		
		Bitmap help_dialog_img=null;
		
	
		AssetManager assets=main.getResources().getAssets();
		try 
		{
			help_dialog_img=BitmapFactory.decodeStream(assets.open("image/helpdialog.png"));
			InputStream is=assets.open("txt/help.txt");
			byte[] buffer=new byte[is.available()];
			is.read(buffer);
			is.close();
			help_string=new String(buffer,"UTF-8");
		} 
		catch (IOException e) 
		{			
		}
		
		help_dialog=new ImagePosData(help_dialog_img,new Point(0,0),help_dialog_point,help_dialog_width,help_dialog_height,rate_x,rate_y);
		help_txt=new ImagePosData(null,null,help_txt_point,help_txt_width,help_txt_height,rate_x,rate_y);
		help_close=new ImagePosData(null,null,help_close_point,help_close_width,help_close_height,rate_x,rate_y);
		
		help_dialog_rect=getRect(help_dialog);
		help_txt_rect=getRect(help_txt);
		help_close_rect=getRect(help_close);
		
				
		close_callback=closeCallback;
		
		help_txt_draw_point.x=(int) (help_txt_draw_point.x*rate_x);
		help_txt_draw_point.y=(int) (help_txt_draw_point.y*rate_y);
		
		initHelpTxtCanvas(rate_x);
		
	}
	private void initHelpTxtCanvas(float rate_x) {
		TextPaint paint = new TextPaint();
		paint.setColor(Color.parseColor("#333333"));
		paint.setTextSize(24*rate_x);
		paint.setAntiAlias(true);
		
		help_txt_layout = new StaticLayout(help_string,paint,help_txt_rect.width(),Alignment.ALIGN_NORMAL,1.2F,0,false);

		
		
		
		top_help_txt_y=help_txt.posInScreen.y-(help_txt_layout.getHeight()-help_txt_rect.height());
		bottom_help_txt_y=help_txt.posInScreen.y;
	}

	public void draw(Canvas canvas)
	{
		drawDialog(canvas);
		paintHelpText(canvas);
	}
	private void paintHelpText(Canvas canvas) {
		
		canvas.save();
		//Log.d("lichao","help rect is "+help_txt_rect.left+" "+help_txt_rect.right+ "iii  "+help_txt_rect.toString());
		canvas.clipRect(help_txt_rect);
		
		canvas.translate(help_txt_draw_point.x, help_txt_draw_point.y);
		help_txt_layout.draw(canvas);
		canvas.restore();
		
	}
	public void onClick(MotionEvent event)
	{
		if(Tools.isInArea(event,help_close))
		{
			close_callback.CloseHelpDialog();
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
		
		if(isHelpTxtDrag)
		{
			help_txt_draw_point.y=(int) (help_txt_draw_point.y+event.getY()-start_y);
			start_y=event.getY();
		}
	}
	private void OnTouchDown(MotionEvent event) {
		if(Tools.isInArea(event,help_txt))
		{
			start_y=event.getY();
			isHelpTxtDrag=true;
		}
		
	}
	private void onTouchUp(MotionEvent event) {
		
		if(isHelpTxtDrag)
		{
			if(help_txt_draw_point.y<(top_help_txt_y-20))
				help_txt_draw_point.y=top_help_txt_y;
			if(help_txt_draw_point.y>(bottom_help_txt_y+20))
				help_txt_draw_point.y=bottom_help_txt_y;
		}
		isHelpTxtDrag=false;
	}
	private void drawDialog(Canvas canvas) {
		
		canvas.drawBitmap(help_dialog.bmp_src, 
				new Rect(help_dialog.posInBmp.x,
						help_dialog.posInBmp.y,
						help_dialog.posInBmp.x+help_dialog.widthInBmp,
						help_dialog.posInBmp.y+help_dialog.heightInBmp),
				new Rect(help_dialog.posInScreen.x,
						help_dialog.posInScreen.y,
						help_dialog.posInScreen.x+help_dialog.widthInScreen,
						help_dialog.posInScreen.y+help_dialog.heightInScreen), null);
		
	}

	private Rect getRect(ImagePosData data) {
		return new Rect(data.posInScreen.x,data.posInScreen.y,data.posInScreen.x+data.widthInScreen,data.posInScreen.y+data.heightInScreen);
		
	}
	
	public interface CloseCallbackHelpDialog {
		public void CloseHelpDialog();

	}
}
