package heart4.netradar.lichao;

import java.io.IOException;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.TextPaint;

public class Player {
	
	ImagePosData player_top_host;
	ImagePosData player_top_disconnect;
	ImagePosData player_top_clock;
	ImagePosData player_top_ready;
	ImagePosData player_top_card_left;
	ImagePosData player_top_pass;
	ImagePosData player_top_txt;
	ImagePosData player_top_touxiang;
	
	
	ImagePosData player_left_host;
	ImagePosData player_left_disconnect;
	ImagePosData player_left_clock;
	ImagePosData player_left_ready;
	ImagePosData player_left_card_left;
	ImagePosData player_left_pass;
	ImagePosData player_left_txt;
	ImagePosData player_left_touxiang;
	
	ImagePosData player_right_host;
	ImagePosData player_right_disconnect;
	ImagePosData player_right_clock;
	ImagePosData player_right_ready;
	ImagePosData player_right_card_left;
	ImagePosData player_right_pass;
	ImagePosData player_right_txt;
	ImagePosData player_right_touxiang;
	
	ImagePosData player_bottom_host;
	ImagePosData player_bottom_disconnect;
	ImagePosData player_bottom_clock;
	ImagePosData player_bottom_ready;
	ImagePosData player_bottom_pass;
	ImagePosData player_bottom_txt;
	ImagePosData player_bottom_touxiang;
	
	
	Point player_top_host_point=new Point(408,12);
	Point player_top_disconnect_point=new Point(406,56);
	Point player_top_clock_point=new Point(363,81);
	Point player_top_ready_point=new Point(455,99);
	Point player_top_pass_point=new Point(422,89);
	Point player_top_txt_point=new Point(439,40);
	Point player_top_touxiang_point=new Point(365,11);
	Point player_top_card_left_point=new Point(319,13);//图片坐标
	
	Point player_top_msg_point=new Point(439,81);
	Point player_top_nickname_point=new Point(441,12);
	Point player_top_card_left_num_point=new Point(328,50);//文字坐标
	Point player_top_time_left_point=new Point(382,128);
	
	
	Point player_left_host_point=new Point(54,202);
	Point player_left_disconnect_point=new Point(53,252);
	Point player_left_clock_point=new Point(99,197);
	Point player_left_ready_point=new Point(106,262);
	Point player_left_pass_point=new Point(129,217);
	Point player_left_txt_point=new Point(90,203);
	Point player_left_touxiang_point=new Point(10,204);
	Point player_left_card_left_point=new Point(10,272);
	
	Point player_left_msg_point=new Point(90,241);
	Point player_left_nickname_point=new Point(10,199);
	Point player_left_card_left_num_point=new Point(19,308);//文字坐标
	Point player_left_time_left_point=new Point(120,245);
	

	Point player_right_host_point=new Point(802,202);
	Point player_right_disconnect_point=new Point(802,252);
	Point player_right_clock_point=new Point(744,197);
	Point player_right_ready_point=new Point(651,262);
	Point player_right_pass_point=new Point(695,217);
	Point player_right_txt_point=new Point(803,203);
	Point player_right_touxiang_point=new Point(814,204);
	Point player_right_card_left_point=new Point(839,272);
	
	Point player_right_msg_point=new Point();
	Point player_right_nickname_point=new Point(882,199);
	Point player_right_card_left_num_point=new Point(847,308);//文字坐标
	Point player_right_time_left_point=new Point(763,245);
	
	
	Point player_bottom_host_point=new Point(51,495);
	Point player_bottom_disconnect_point=new Point(50,545);
	Point player_bottom_clock_point=new Point(100,499);
	Point player_bottom_ready_point=new Point(106,495);
	Point player_bottom_pass_point=new Point(495,409);
	Point player_bottom_txt_point=new Point(86,490);
	Point player_bottom_touxiang_point=new Point(10,499);
	
	Point player_bottom_msg_point=new Point(86,533);
	Point player_bottom_nickname_point=new Point(10,493);
	Point player_bottom_score_point=new Point(10,596);
	Point player_bottom_time_left_point=new Point(119,546);
	
	int player_host_width=45,player_host_height=24;
	int player_disconnect_width=45,player_disconnect_height=25;
	int player_clock_width=59,player_clock_height=80;
	int player_ready_width=43,player_ready_height=49;
	int player_pass_width=87,player_pass_height=45;
	int player_txt_width,player_txt_height;
	int player_touxiang_width=67,player_touxiang_height=67;
	int player_card_left_width=38,player_card_left_height=58;
	
	
	Rect player_top_host_rect;
	Rect player_top_disconnect_rect;
	Rect player_top_clock_rect;
	Rect player_top_ready_rect;
	Rect player_top_pass_rect;
	Rect player_top_txt_rect;
	Rect player_top_touxiang_rect;
	Rect player_top_card_left_rect;
	Rect player_top_nickname_rect;
	
	Rect player_left_host_rect;
	Rect player_left_disconnect_rect;
	Rect player_left_clock_rect;
	Rect player_left_ready_rect;
	Rect player_left_pass_rect;
	Rect player_left_txt_rect;
	Rect player_left_touxiang_rect;
	Rect player_left_card_left_rect;
	Rect player_left_nickname_rect;
	
	Rect player_right_host_rect;
	Rect player_right_disconnect_rect;
	Rect player_right_clock_rect;
	Rect player_right_ready_rect;
	Rect player_right_pass_rect;
	Rect player_right_txt_rect;
	Rect player_right_touxiang_rect;
	Rect player_right_card_left_rect;
	Rect player_right_nickname_rect;
	
	Rect player_bottom_host_rect;
	Rect player_bottom_disconnect_rect;
	Rect player_bottom_clock_rect;
	Rect player_bottom_ready_rect;
	Rect player_bottom_pass_rect;
	Rect player_bottom_txt_rect;
	Rect player_bottom_touxiang_rect;
	Rect player_bottom_nickname_rect;
	
	
	Heart4MainActivity main_activity;
	float rate_x,rate_y;
	
	Paint bitmap_paint;
	/*Bitmap player_top_touxiang,play_left_touxiang,play_right_touxiang,play_bottom_touxiang;
	String player_top_nickname,play_left_nickname,play_right_nickname,play_bottom_nickname;
*/	
	int myScore;
	
	public Player(float rate_x,float rate_y,Heart4MainActivity main) 
	{
		
		main_activity=main;
		this.rate_x=rate_x;
		this.rate_y=rate_y;
		initImagePos();
		initTxtCanvas(rate_y);

		
	}


	TextPaint paint_big,paint_small;
	int font_width_big,font_height_big;
	public int time_left;
	private void initTxtCanvas(float rate_x) {
		
		paint_big = new TextPaint();
		paint_small=new TextPaint();
		paint_big.setColor(Color.parseColor("#333333"));
		paint_big.setTextSize(26*rate_x);
		paint_big.setAntiAlias(true);
		
		font_width_big=(int) (paint_big.getFontMetrics().bottom-paint_big.getFontMetrics().top);
		paint_small.setColor(Color.parseColor("#ffffff"));
		paint_small.setTextSize(22*rate_x);
		paint_small.setAntiAlias(true);
	}
	
	private void initImagePos() {
		
		Bitmap host_img=null;
		Bitmap disconnect_img=null;
		Bitmap clock_img=null;
		Bitmap pass_img=null;
		Bitmap ready_img=null;
		Bitmap card_left_img=null;
		
		
		
		AssetManager assets=main_activity.getResources().getAssets();
		try 
		{
			host_img=BitmapFactory.decodeStream(assets.open("image/host.png"));
			disconnect_img=BitmapFactory.decodeStream(assets.open("image/disconnect.png"));	
			clock_img=BitmapFactory.decodeStream(assets.open("image/clock.png"));
			pass_img=BitmapFactory.decodeStream(assets.open("image/pass.png"));
			ready_img=BitmapFactory.decodeStream(assets.open("image/status_ready.png"));
			card_left_img=BitmapFactory.decodeStream(assets.open("image/poker_back_small.png"));
		} 
		catch (IOException e) 
		{			
		}
		
		 player_top_host=new ImagePosData(host_img,new Point(0,0),player_top_host_point,player_host_width,player_host_height,rate_x,rate_y);
		 player_top_disconnect=new ImagePosData(disconnect_img,new Point(0,0),player_top_disconnect_point,player_disconnect_width,player_disconnect_height,rate_x,rate_y);
		 player_top_clock=new ImagePosData(clock_img,new Point(0,0),player_top_clock_point,player_clock_width,player_clock_height,rate_x,rate_y);
		 player_top_ready=new ImagePosData(ready_img,new Point(0,0),player_top_ready_point,player_ready_width,player_ready_height,rate_x,rate_y);
		 player_top_card_left=new ImagePosData(card_left_img,new Point(0,0),player_top_card_left_point,player_card_left_width,player_card_left_height,rate_x,rate_y);
		 player_top_pass=new ImagePosData(pass_img,new Point(0,0),player_top_pass_point,player_pass_width,player_pass_height,rate_x,rate_y);
		// player_top_txt=
		 player_top_touxiang=new ImagePosData(null,new Point(0,0),player_top_touxiang_point,player_touxiang_width,player_touxiang_height,rate_x,rate_y);
		
		
		 player_left_host=new ImagePosData(host_img,new Point(0,0),player_left_host_point,player_host_width,player_host_height,rate_x,rate_y);
		 player_left_disconnect=new ImagePosData(disconnect_img,new Point(0,0),player_left_disconnect_point,player_disconnect_width,player_disconnect_height,rate_x,rate_y);
		 player_left_clock=new ImagePosData(clock_img,new Point(0,0),player_left_clock_point,player_clock_width,player_clock_height,rate_x,rate_y);
		 player_left_ready=new ImagePosData(ready_img,new Point(0,0),player_left_ready_point,player_ready_width,player_ready_height,rate_x,rate_y);
		 player_left_card_left=new ImagePosData(card_left_img,new Point(0,0),player_left_card_left_point,player_card_left_width,player_card_left_height,rate_x,rate_y);
		 player_left_pass=new ImagePosData(pass_img,new Point(0,0),player_left_pass_point,player_pass_width,player_pass_height,rate_x,rate_y);
		// player_left_txt=
		 player_left_touxiang=new ImagePosData(null,new Point(0,0),player_left_touxiang_point,player_touxiang_width,player_touxiang_height,rate_x,rate_y);
		
		 player_right_host=new ImagePosData(host_img,new Point(0,0),player_right_host_point,player_host_width,player_host_height,rate_x,rate_y);
		 player_right_disconnect=new ImagePosData(disconnect_img,new Point(0,0),player_right_disconnect_point,player_disconnect_width,player_disconnect_height,rate_x,rate_y);
		 player_right_clock=new ImagePosData(clock_img,new Point(0,0),player_right_clock_point,player_clock_width,player_clock_height,rate_x,rate_y);
		 player_right_ready=new ImagePosData(ready_img,new Point(0,0),player_right_ready_point,player_ready_width,player_ready_height,rate_x,rate_y);
		 player_right_card_left=new ImagePosData(card_left_img,new Point(0,0),player_right_card_left_point,player_card_left_width,player_card_left_height,rate_x,rate_y);
		 player_right_pass=new ImagePosData(pass_img,new Point(0,0),player_right_pass_point,player_pass_width,player_pass_height,rate_x,rate_y);
	//	 player_right_txt=
		 player_right_touxiang=new ImagePosData(null,new Point(0,0),player_right_touxiang_point,player_touxiang_width,player_touxiang_height,rate_x,rate_y);
		
		 player_bottom_host=new ImagePosData(host_img,new Point(0,0),player_bottom_host_point,player_host_width,player_host_height,rate_x,rate_y);
		 player_bottom_disconnect=new ImagePosData(disconnect_img,new Point(0,0),player_bottom_disconnect_point,player_disconnect_width,player_disconnect_height,rate_x,rate_y);
		 player_bottom_clock=new ImagePosData(clock_img,new Point(0,0),player_bottom_clock_point,player_clock_width,player_clock_height,rate_x,rate_y);
		 player_bottom_ready=new ImagePosData(ready_img,new Point(0,0),player_bottom_ready_point,player_ready_width,player_ready_height,rate_x,rate_y);
		 player_bottom_pass=new ImagePosData(pass_img,new Point(0,0),player_bottom_pass_point,player_pass_width,player_pass_height,rate_x,rate_y);
		// player_bottom_txt=
		 player_bottom_touxiang=new ImagePosData(null,new Point(0,0),player_bottom_touxiang_point,player_touxiang_width,player_touxiang_height,rate_x,rate_y);
		
		 
		 player_top_host_rect=Tools.getRect(player_top_host);
		 player_top_disconnect_rect=Tools.getRect(player_top_disconnect);
		 player_top_clock_rect=Tools.getRect(player_top_clock);
		 player_top_ready_rect=Tools.getRect(player_top_ready);
		 player_top_pass_rect=Tools.getRect(player_top_pass);
		// player_top_txt_rect=Tools.getRect(player_top_txt);
		 player_top_touxiang_rect=Tools.getRect(player_top_touxiang);
		 player_top_card_left_rect=Tools.getRect(player_top_card_left);
		
		
		 player_left_host_rect=Tools.getRect(player_left_host);
		 player_left_disconnect_rect=Tools.getRect(player_left_disconnect);
		 player_left_clock_rect=Tools.getRect(player_left_clock);
		 player_left_ready_rect=Tools.getRect(player_left_ready);
		 player_left_pass_rect=Tools.getRect(player_left_pass);
	//	 player_left_txt_rect=Tools.getRect(player_left_txt);
		 player_left_touxiang_rect=Tools.getRect(player_left_touxiang);
		 player_left_card_left_rect=Tools.getRect(player_left_card_left);
		 
		
		 player_right_host_rect=Tools.getRect(player_right_host);
		 player_right_disconnect_rect=Tools.getRect(player_right_disconnect);
		 player_right_clock_rect=Tools.getRect(player_right_clock);
		 player_right_ready_rect=Tools.getRect(player_right_ready);
		 player_right_pass_rect=Tools.getRect(player_right_pass);
	//	 player_right_txt_rect=Tools.getRect(player_right_txt);
		 player_right_touxiang_rect=Tools.getRect(player_right_touxiang);
		 player_right_card_left_rect=Tools.getRect(player_right_card_left);
		
		
		 player_bottom_host_rect=Tools.getRect(player_bottom_host);
		 player_bottom_disconnect_rect=Tools.getRect(player_bottom_disconnect);
		 player_bottom_clock_rect=Tools.getRect(player_bottom_clock);
		 player_bottom_ready_rect=Tools.getRect(player_bottom_ready);
		 player_bottom_pass_rect=Tools.getRect(player_bottom_pass);
	//	 player_bottom_txt_rect=Tools.getRect(player_bottom_txt);
		 player_bottom_touxiang_rect=Tools.getRect(player_bottom_touxiang);
		 
		 
		 
		 
		 player_top_msg_point.x=(int) (player_top_msg_point.x*rate_x);
		 player_top_msg_point.y=(int) (player_top_msg_point.y*rate_y);
		 player_top_nickname_point.x=(int) (player_top_nickname_point.x*rate_x);
		 player_top_nickname_point.y=(int) (player_top_nickname_point.y*rate_y);
		 player_top_card_left_num_point.x=(int) (player_top_card_left_num_point.x*rate_x);
		 player_top_card_left_num_point.y=(int) (player_top_card_left_num_point.y*rate_y);
		 player_top_time_left_point.x=(int) (player_top_time_left_point.x*rate_x);
		 player_top_time_left_point.y=(int) (player_top_time_left_point.y*rate_y);
		 
		 player_left_msg_point.x=(int) (player_left_msg_point.x*rate_x);
		 player_left_msg_point.y=(int) (player_left_msg_point.y*rate_y);
		 player_left_nickname_point.x=(int) (player_left_nickname_point.x*rate_x);
		 player_left_nickname_point.y=(int) (player_left_nickname_point.y*rate_y);
		 player_left_card_left_num_point.x=(int) (player_left_card_left_num_point.x*rate_x);
		 player_left_card_left_num_point.y=(int) (player_left_card_left_num_point.y*rate_y);
		 player_left_time_left_point.x=(int) (player_left_time_left_point.x*rate_x);
		 player_left_time_left_point.y=(int) (player_left_time_left_point.y*rate_y);
		 
		 
		 player_right_msg_point.x=(int) (player_right_msg_point.x*rate_x);
		 player_right_msg_point.y=(int) (player_right_msg_point.y*rate_y);
		 player_right_nickname_point.x=(int) (player_right_nickname_point.x*rate_x);
		 player_right_nickname_point.y=(int) (player_right_nickname_point.y*rate_y);
		 player_right_card_left_num_point.x=(int) (player_right_card_left_num_point.x*rate_x);
		 player_right_card_left_num_point.y=(int) (player_right_card_left_num_point.y*rate_y);
		 player_right_time_left_point.x=(int) (player_right_time_left_point.x*rate_x);
		 player_right_time_left_point.y=(int) (player_right_time_left_point.y*rate_y);
		 
		 
		 player_bottom_msg_point.x=(int) (player_bottom_msg_point.x*rate_x);
		 player_bottom_msg_point.y=(int) (player_bottom_msg_point.y*rate_y);
		 player_bottom_nickname_point.x=(int) (player_bottom_nickname_point.x*rate_x);
		 player_bottom_nickname_point.y=(int) (player_bottom_nickname_point.y*rate_y);
		 player_bottom_score_point.x=(int) (player_bottom_score_point.x*rate_x);
		 player_bottom_score_point.y=(int) (player_bottom_score_point.y*rate_y);
		 player_bottom_time_left_point.x=(int) (player_bottom_time_left_point.x*rate_x);
		 player_bottom_time_left_point.y=(int) (player_bottom_time_left_point.y*rate_y);
		 
	}
/*	public void drawPlayer(Canvas canvas, PlayerInfo player_bottom, PlayerInfo player_left, PlayerInfo player_top, PlayerInfo player_right)
	{
		paintBottom(canvas,player_bottom);
		paintOthers(canvas,player_left,player_top,player_right);
	}
*/
	public void drawPlayer(Canvas canvas, PlayerProcess players) {
		
		paintBottom(canvas,players.player_bottom,players.currenPendingPlayer);
		paintOthers(canvas,players.player_left,players.player_top,players.player_right,players.currenPendingPlayer);
		
		
	}  
	
	private void paintOthers(Canvas canvas, PlayerInfo player_left,
			PlayerInfo player_top, PlayerInfo player_right, int currenPendingPlayer) {
		
		if(player_left.index!=-1)
		{
			if(player_left.touxiang==null)
				canvas.drawBitmap(main_activity.no_touxiang, null, player_left_touxiang_rect,null);
			else
				canvas.drawBitmap(player_left.touxiang, null, player_left_touxiang_rect,null);
			
			canvas.drawText(player_left.nickname, player_left_nickname_point.x, player_left_nickname_point.y, paint_big);
			
	
			if(player_left.isDisconnect)
				canvas.drawBitmap(player_left_disconnect.bmp_src, null, player_left_disconnect_rect,null);
			if(player_left.isHost)
				canvas.drawBitmap(player_left_host.bmp_src, null, player_left_host_rect,null);
			if(player_left.isNocard)
				canvas.drawBitmap(player_left_pass.bmp_src, null, player_left_pass_rect,null);
/*			if(player_left.isTurn)
				canvas.drawBitmap(player_left_clock.bmp_src, null, player_left_clock_rect,null);
*/			
			if(player_left.isStart)
				canvas.drawBitmap(player_left_ready.bmp_src, null, player_left_ready_rect,null);
			if(player_left.card_left!=-1)
			{
				canvas.drawBitmap(player_left_card_left.bmp_src, null, player_left_card_left_rect,null);
				
				canvas.drawText(String.valueOf(player_left.card_left), player_left_card_left_num_point.x, player_left_card_left_num_point.y, paint_small);
			}
			if(player_left.index==currenPendingPlayer)
			{
				canvas.drawBitmap(player_left_clock.bmp_src, null, player_left_clock_rect,null);
				canvas.drawText(String.valueOf(time_left), player_left_time_left_point.x, player_left_time_left_point.y, paint_small);
			}
			}
		
		if(player_top.index!=-1)
		{
			if(player_top.touxiang==null)
				canvas.drawBitmap(main_activity.no_touxiang, null, player_top_touxiang_rect,null);
			else
				canvas.drawBitmap(player_top.touxiang, null, player_top_touxiang_rect,null);
		
			canvas.drawText(player_top.nickname, player_top_nickname_point.x, player_top_nickname_point.y+getFontHeightBig(), paint_big);
			if(player_top.isDisconnect)
				canvas.drawBitmap(player_top_disconnect.bmp_src, null, player_top_disconnect_rect,null);
			if(player_top.isHost)
				canvas.drawBitmap(player_top_host.bmp_src, null, player_top_host_rect,null);
			if(player_top.isNocard)
				canvas.drawBitmap(player_top_pass.bmp_src, null, player_top_pass_rect,null);
			if(player_top.isTurn)
				canvas.drawBitmap(player_top_clock.bmp_src, null, player_top_clock_rect,null);
			if(player_top.isStart)
				canvas.drawBitmap(player_left_ready.bmp_src, null, player_top_ready_rect,null);
			if(player_top.card_left!=-1)
			{
				canvas.drawBitmap(player_top_card_left.bmp_src, null, player_top_card_left_rect,null);
				canvas.drawText(String.valueOf(player_top.card_left), player_top_card_left_num_point.x, player_top_card_left_num_point.y, paint_small);
			}
			if(player_top.index==currenPendingPlayer)
			{
				canvas.drawBitmap(player_top_clock.bmp_src, null, player_top_clock_rect,null);
				canvas.drawText(String.valueOf(time_left), player_top_time_left_point.x, player_top_time_left_point.y, paint_small);
			}
			
		}
		if(player_right.index!=-1)
		{
			if(player_right.touxiang==null)
				canvas.drawBitmap(main_activity.no_touxiang, null, player_right_touxiang_rect,null);
			else
				canvas.drawBitmap(player_right.touxiang, null, player_right_touxiang_rect,null);
			canvas.drawText(player_right.nickname,player_right_nickname_point.x-paint_big.measureText(player_right.nickname), player_right_nickname_point.y, paint_big);
			if(player_right.isDisconnect)
				canvas.drawBitmap(player_right_disconnect.bmp_src, null, player_right_disconnect_rect,null);
			if(player_right.isHost)
				canvas.drawBitmap(player_right_host.bmp_src, null, player_right_host_rect,null);
			if(player_right.isNocard)
				canvas.drawBitmap(player_right_pass.bmp_src, null, player_right_pass_rect,null);
			if(player_right.isTurn)
				canvas.drawBitmap(player_right_clock.bmp_src, null, player_right_clock_rect,null);
/*			if(player_right.card_left!=-1)
				canvas.drawBitmap(player_right_card_left.bmp_src, null, player_right_card_left_rect,null);
*/			if(player_right.isStart)
				canvas.drawBitmap(player_left_ready.bmp_src, null, player_right_ready_rect,null);
//			canvas.drawText(String.valueOf(player_right.card_left), player_right_card_left_num_point.x, player_right_card_left_num_point.y, paint_small);
			
			if(player_right.card_left!=-1)
			{
				canvas.drawBitmap(player_right_card_left.bmp_src, null, player_right_card_left_rect,null);
				canvas.drawText(String.valueOf(player_right.card_left), player_right_card_left_num_point.x, player_right_card_left_num_point.y, paint_small);
			}
			if(player_right.index==currenPendingPlayer)
			{
				canvas.drawBitmap(player_right_clock.bmp_src, null, player_right_clock_rect,null);
				canvas.drawText(String.valueOf(time_left), player_right_time_left_point.x, player_right_time_left_point.y, paint_small);
			}
			
		}
	}

	private void paintBottom(Canvas canvas,PlayerInfo player, int currenPendingPlayer) {
		canvas.drawBitmap(player.touxiang, null, player_bottom_touxiang_rect,null);
		
		canvas.drawText(player.nickname, player_bottom_nickname_point.x, player_bottom_nickname_point.y, paint_big);
		canvas.drawText("金币:"+player.score, player_bottom_score_point.x, player_bottom_score_point.y, paint_big);
		
		if(player.isDisconnect)
			canvas.drawBitmap(player_bottom_disconnect.bmp_src, null, player_bottom_disconnect_rect,null);
		if(player.isHost)
			canvas.drawBitmap(player_bottom_host.bmp_src, null, player_bottom_host_rect,null);
		if(player.isNocard)
			canvas.drawBitmap(player_bottom_pass.bmp_src, null, player_bottom_pass_rect,null);
/*		if(player.isTurn)
			canvas.drawBitmap(player_bottom_clock.bmp_src, null, player_bottom_clock_rect,null);
*/		if(player.isStart)
			canvas.drawBitmap(player_bottom_ready.bmp_src, null, player_bottom_ready_rect,null);
		if(player.index==currenPendingPlayer)
		{
			canvas.drawBitmap(player_bottom_clock.bmp_src, null, player_bottom_clock_rect,null);
			
			canvas.drawText(String.valueOf(time_left), player_bottom_time_left_point.x, player_bottom_time_left_point.y, paint_small);
		}
		
	}

	public int getFontHeightBig()

	{ 

    

	    FontMetrics fm = paint_big.getFontMetrics(); 
	
	    return (int) (fm.bottom-fm.ascent); 

	}

	


}
