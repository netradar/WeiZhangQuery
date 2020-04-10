package heart4.netradar.lichao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;

import android.text.TextPaint;

import android.view.MotionEvent;

public class RankDialog {
	
	
	boolean isListDrag=false;

	ArrayList<HashMap<String,String>> rankList;

	ImagePosData rank_dialog=null;
	ImagePosData rank_item=null;
	ImagePosData rank_close=null;
	ImagePosData rank_index=null;
	ImagePosData rank_nickname=null;
	ImagePosData rank_score=null;
	ImagePosData rank_show_area=null;
	
	Point rank_dialog_point=new Point(128,0);
	Point rank_item_point=new Point(195,94);
	Point rank_close_point=new Point(690,15);
	Point rank_index_point_left_bottom=new Point(9,38);
	Point rank_index_point_left_top=new Point(9,0);
	Point rank_nickname_point_left_bottom=new Point(79,38);
	Point rank_nickname_point_left_top=new Point(79,0);
	Point rank_score_point_left_bottom=new Point(334,38);
	Point rank_score_point_left_top=new Point(334,0);
	Point rank_show_area_point=new Point(147,88);
	
	int rank_dialog_width=661,rank_dialog_height=600;
	int rank_item_width=526,rank_item_height=46;
	int rank_close_width=70,rank_close_height=70;
	int rank_index_width=50,rank_index_height=46;
	int rank_nickname_width=234,rank_nickname_height=46;
	int rank_score_width=181,rank_score_height=46;
	int rank_show_area_width=661,rank_show_area_height=473;
	
	Rect rank_dialog_rect;
	Rect rank_item_rect;
	Rect rank_close_rect;
	Rect rank_index_rect;
	Rect rank_nickname_rect;
	Rect rank_score_rect;
	Rect rank_show_area_rect;
	
	CloseCallbackRankDialog close_callback;
	
	String nickname;
	float offset_Y=0;
	public class GetRankList extends AsyncTask<String, Integer, String> {

		
		@Override
		protected void onPostExecute(String result) {
			refreshData(result);
			super.onPostExecute(result);
		}

		@Override
		protected String doInBackground(String... arg0) {
			offset_Y=0;
			return null;
		}

	}
	public RankDialog(float rate_x,float rate_y,Heart4MainActivity main,CloseCallbackRankDialog closeCallback) 
	{
		Bitmap rank_dialog_img=null;
		Bitmap rank_item_img=null;
		
		AssetManager assets=main.getResources().getAssets();
		try 
		{
			rank_dialog_img=BitmapFactory.decodeStream(assets.open("image/rankdialog.png"));
			rank_item_img=BitmapFactory.decodeStream(assets.open("image/rankitem.png"));	
		} 
		catch (IOException e) 
		{			
		}
		
		rank_dialog=new ImagePosData(rank_dialog_img,new Point(0,0),rank_dialog_point,rank_dialog_width,rank_dialog_height,rate_x,rate_y);
		rank_item=new ImagePosData(rank_item_img,new Point(0,0),rank_item_point,rank_item_width,rank_item_height,rate_x,rate_y);
		rank_close=new ImagePosData(null,null,rank_close_point,rank_close_width,rank_close_height,rate_x,rate_y);
		rank_index=new ImagePosData(null,null,rank_index_point_left_top,rank_index_width,rank_index_height,rate_x,rate_y);
		rank_nickname=new ImagePosData(null,null,rank_nickname_point_left_top,rank_nickname_width,rank_nickname_height,rate_x,rate_y);
		rank_score=new ImagePosData(null,null,rank_score_point_left_top,rank_score_width,rank_score_height,rate_x,rate_y);
		rank_show_area=new ImagePosData(null,null,rank_show_area_point,rank_show_area_width,rank_show_area_height,rate_x,rate_y);
		
		rank_index_point_left_bottom.x=(int) (rank_index_point_left_bottom.x*rate_x);
		rank_index_point_left_bottom.y=(int) (rank_index_point_left_bottom.y*rate_y);
		rank_nickname_point_left_bottom.x=(int) (rank_nickname_point_left_bottom.x*rate_x);
		rank_nickname_point_left_bottom.y=(int) (rank_nickname_point_left_bottom.y*rate_y);
		rank_score_point_left_bottom.x=(int) (rank_score_point_left_bottom.x*rate_x);
		rank_score_point_left_bottom.y=(int) (rank_score_point_left_bottom.y*rate_y);
		
		rank_dialog_rect=Tools.getRect(rank_dialog);
		rank_item_rect=Tools.getRect(rank_item);
		rank_close_rect=Tools.getRect(rank_close);
		rank_index_rect=Tools.getRect(rank_index);
		rank_nickname_rect=Tools.getRect(rank_nickname);
		rank_score_rect=Tools.getRect(rank_score);
		rank_show_area_rect=Tools.getRect(rank_show_area);
		
		close_callback=closeCallback;
		
		initTxtCanvas(rate_y);
		rankList=new ArrayList<HashMap<String,String>>();
		new GetRankList().execute(nickname);
	}
	
	
	TextPaint paint;
	private void initTxtCanvas(float rate_x) {
		
		paint = new TextPaint();
		paint.setColor(Color.parseColor("#333333"));
		paint.setTextSize(30*rate_x);
		paint.setAntiAlias(true);
		
		
		
		
	}

	public void refreshData(String result) {
		
		HashMap<String,String> map=new HashMap<String,String>();
		map.put("李超","123456");
		rankList.add(map);
		
	}

	void draw(Canvas canvas)
	{
		paintRankDialog(canvas);
		if(rankList.size()>=0)
			paintItem(canvas);
	}
	private void paintItem(Canvas canvas) {
		
		Bitmap b = Bitmap.createBitmap(rank_item.widthInScreen, rank_item.heightInScreen,  
                Bitmap.Config.ARGB_4444);
		
		for(int i=0;i<10;i++)
		{
			
			Canvas can=new Canvas(b);
			
						
			can.drawBitmap(rank_item.bmp_src, null,
					new Rect(0,0,
							b.getWidth(),
							b.getHeight()), null);
			can.save();
			can.clipRect(rank_index_rect);
			can.drawText(String.valueOf(i+1), rank_index_point_left_bottom.x, rank_index_point_left_bottom.y, paint);
			can.restore();
			
			can.save();
			can.clipRect(rank_nickname_rect);
			can.drawText("李超李超李超李超李超李超李超李超李超李超", rank_nickname_point_left_bottom.x, rank_nickname_point_left_bottom.y, paint);
			can.restore();
			
			can.save();
			can.clipRect(rank_score_rect);
			can.drawText("123456", rank_score_point_left_bottom.x, rank_score_point_left_bottom.y, paint);
			can.restore();
			
			canvas.save();
			canvas.clipRect(rank_show_area_rect);
			canvas.drawBitmap(b, null, 
					new Rect(rank_item.posInScreen.x,
							rank_item.posInScreen.y+i*(rank_item.heightInScreen+20)+(int)offset_Y,
							rank_item.posInScreen.x+rank_item.widthInScreen,
							rank_item.posInScreen.y+(i+1)*rank_item.heightInScreen+i*20+(int)offset_Y),null);
			canvas.restore();
		}
		
		
	}

	void paintRankDialog(Canvas canvas)
	{
		canvas.drawBitmap(rank_dialog.bmp_src, null,rank_dialog_rect, null);
	}
	
	public void onClick(MotionEvent event)
	{
		if(Tools.isInArea(event,rank_close))
		{
			close_callback.CloseRankDialog();
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

	float start_Y;
	private void OnMove(MotionEvent event) {
		
		if(isListDrag)
		{
			offset_Y=offset_Y+event.getY()-start_Y;
			start_Y=event.getY();
		}
	}

	private void OnTouchDown(MotionEvent event) {
		
		if(Tools.isInArea(event, rank_show_area))
		{
			isListDrag=true;
			start_Y=event.getY();
		}
	}

	private void onTouchUp(MotionEvent event) {
		
		isListDrag=true;
	}

	public interface CloseCallbackRankDialog {
		public void CloseRankDialog();

	}
}
