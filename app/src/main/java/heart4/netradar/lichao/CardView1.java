package heart4.netradar.lichao;

import java.io.IOException;
import java.util.ArrayList;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;

public class CardView1 {
	
	
	
	ImagePosData my_card;
	//ImagePosData my_card_select;
	ImagePosData my_card_play;
	ImagePosData my_card_no_card;
	ImagePosData left_card_play;
	ImagePosData top_card_play;
	ImagePosData right_card_play;
	
	Point my_card_point=new Point(197,465);
	//Point my_card_select_point=new Point(197,483);
	Point my_card_play_point=new Point(324,345);
	Point my_card_no_card_point=new Point(276,501);
	Point left_card_play_point=new Point(112,213);
	Point top_card_play_point=new Point(365,87);
	Point right_card_play_point=new Point(768,213);
	
	int my_card_width=665,my_card_height=123;
	int my_card_no_card_width=501,my_card_no_card_height=54;
	int card_play_width=404,card_play_height=105;
	int card_width=103,card_height=128;
	int card_played_width,card_played_height;
	
	int select_offset=28;
	
	Rect my_card_rect;
	Rect my_card_select_rect;
	Rect my_card_play_rect;
	Rect my_card_no_card_rect;
	Rect left_card_play_rect;
	Rect top_card_play_rect;
	Rect right_card_play_rect;
	
	
	Heart4MainActivity main_activity;
	float rate_x,rate_y;
	ArrayList<CardInfo> cardInfoList;
	ArrayList<CardInfo> selectedCardInfoList;
	
	ArrayList<CardInfo> myPlayedCardInfoList;
	ArrayList<CardInfo> leftPlayedCardInfoList;
	ArrayList<CardInfo> topPlayedCardInfoList;
	ArrayList<CardInfo> rightPlayedCardInfoList;
	
	Paint bitmap_paint;
	int playedCardGap=36;
	
	public CardView1(float rate_x,float rate_y,Heart4MainActivity main,int[] card_list) 
	{
		
		main_activity=main;
		this.rate_x=rate_x;
		this.rate_y=rate_y;
		initImagePos();
		cardInfoList=new ArrayList<CardInfo>();
		selectedCardInfoList=new ArrayList<CardInfo>();
		
		myPlayedCardInfoList=new ArrayList<CardInfo>();
		leftPlayedCardInfoList=new ArrayList<CardInfo>();
		topPlayedCardInfoList=new ArrayList<CardInfo>();
		rightPlayedCardInfoList=new ArrayList<CardInfo>();
		
		
		initCard(card_list);
		
		bitmap_paint=new Paint();
		bitmap_paint.setAntiAlias(true);
		bitmap_paint.setFilterBitmap(true);

		
		
	}
	
	int[] seqBigToSmall={3,2,1,0,12,11,10,9,8,7,6,5,4};
	
	private void initCard(int[] card_list) {
		
		int gap=(my_card_rect.width()-card_width)/(card_list.length-1);
		int length=card_list.length;
		
		
		for(int i=0;i<seqBigToSmall.length;i++)
		{
			for(int j=0;j<length;j++)
			{
				if((card_list[j]%13)==seqBigToSmall[i])
				{
					CardInfo card=new CardInfo();
					card.num=(card_list[j]%13);
					card.huase=card_list[j]/13;
					
					card.pos=new Point(my_card_rect.left+gap*cardInfoList.size(),my_card_rect.top);
					
					if(cardInfoList.size()==(length-1))
					{
						card.rect=new Rect(card.pos.x,card.pos.y,card.pos.x+card_width,card.pos.y+card_height);
						card.rect_paint=new Rect(card.pos.x,card.pos.y,card.pos.x+card_width,card.pos.y+card_height);
					}
					else
					{
						card.rect=new Rect(card.pos.x,card.pos.y,card.pos.x+gap,card.pos.y+card_height);
						card.rect_paint=new Rect(card.pos.x,card.pos.y,card.pos.x+card_width,card.pos.y+card_height);
					}
					cardInfoList.add(card);
					
				}
				
			}
		}
		
		/*for(CardInfo info:cardInfoList)
		{
			Log.d("lichao","num is "+info.num);
		}*/
		
		
	}
	
	private void initImagePos() {
		
		Bitmap card_img=null;
		Bitmap no_card_img=null;
		
		
		
		
		AssetManager assets=main_activity.getResources().getAssets();
		try 
		{
			card_img=BitmapFactory.decodeStream(assets.open("image/poker.png"));
			no_card_img=BitmapFactory.decodeStream(assets.open("image/nocard.png"));	
			} 
		catch (IOException e) 
		{			
		}
		
		my_card=new ImagePosData(card_img,new Point(0,0),my_card_point,my_card_width,my_card_height,rate_x,rate_y);
	//	my_card_select=new ImagePosData(card_img,new Point(0,0),my_card_select_point,my_card_width,my_card_height,rate_x,rate_y);
		my_card_play=new ImagePosData(card_img,new Point(0,0),my_card_play_point,card_play_width,card_play_height,rate_x,rate_y);
		my_card_no_card=new ImagePosData(no_card_img,new Point(0,0),my_card_no_card_point,my_card_no_card_width,my_card_no_card_height,rate_x,rate_y);
		left_card_play=new ImagePosData(card_img,new Point(0,0),left_card_play_point,card_play_width,card_play_height,rate_x,rate_y);
		top_card_play=new ImagePosData(card_img,new Point(0,0),top_card_play_point,card_play_width,card_play_height,rate_x,rate_y);
		right_card_play=new ImagePosData(card_img,new Point(0,0),right_card_play_point,card_play_width,card_play_height,rate_x,rate_y);
		
		my_card_rect=Tools.getRect(my_card);
	//	my_card_select_rect=Tools.getRect(my_card_select);
		my_card_play_rect=Tools.getRect(my_card_play);
		my_card_no_card_rect=Tools.getRect(my_card_no_card);
		left_card_play_rect=Tools.getRect(left_card_play);
		top_card_play_rect=Tools.getRect(top_card_play);
		right_card_play_rect=Tools.getRect(right_card_play);
		
		
		
		//float card_height_rate=(float)card_height/(float)my_card_height;
		float card_rate=(float)card_width/(float)card_height;
		
		card_height=my_card_rect.height();
		card_width=(int) (card_height*card_rate);
		
		card_played_height=my_card_play_rect.height();
		
		card_played_width=(int) (card_played_height*card_rate);
		playedCardGap=(int) (playedCardGap*card_rate);		
				
		select_offset=(int) (select_offset*rate_y);
		
		card_play_width=(int) (card_play_width*rate_x);
	//	playedCardGap=(card_play_width-card_width)/12;
		
	}
	public void drawCard(Canvas canvas, PlayerInfo player_bottom,
			PlayerInfo player_left, PlayerInfo player_top,
			PlayerInfo player_right) {
		
		paintBottomCard(canvas);
		
		
		
		paintPlayCard(canvas);
		
	//	paintNoCard(canvas);
		
		paintPlayedCard(canvas,myPlayedCardInfoList);
		paintPlayedCard(canvas,leftPlayedCardInfoList);
		paintPlayedCard(canvas,topPlayedCardInfoList);
		paintPlayedCard(canvas,rightPlayedCardInfoList);
	}
	

	public void refreshPlayedCard(int[] card_list,int player_index)
	{
		
		if(player_index==1)
		{
			synchronized (leftPlayedCardInfoList)
			{
				if(card_list==null)
				{
					leftPlayedCardInfoList.clear();
					return;
				}
				else
				{
					for(int i=0;i<card_list.length;i++)
					{
						CardInfo info=new CardInfo();
						
						info.num=(card_list[i]%13);
						info.huase=card_list[i]/13;
						
						info.rect_paint=new Rect(left_card_play_rect.left+playedCardGap*i,
								left_card_play_rect.top,
								left_card_play_rect.left+playedCardGap*i+card_played_width,
								left_card_play_rect.top+card_played_height);
						
						leftPlayedCardInfoList.add(info);
						
					}
				}
				leftPlayedCardInfoList.notify();
			}
			
		}
		if(player_index==2)
		{
			//list=topPlayedCardInfoList;
			synchronized (topPlayedCardInfoList)
			{
				if(card_list==null)
				{
					topPlayedCardInfoList.clear();
					return;
				}
				else
				{
					int length=card_list.length;
					int firstLeft=top_card_play_rect.left;//+(top_card_play_rect.width()-(length-1)*playedCardGap-card_played_width)/2;
					for(int i=0;i<card_list.length;i++)
					{
						CardInfo info=new CardInfo();
						
						info.num=(card_list[i]%13);
						info.huase=card_list[i]/13;
						
						info.rect_paint=new Rect(firstLeft+playedCardGap*i,
								top_card_play_rect.top,
								firstLeft+playedCardGap*i+card_played_width,
								top_card_play_rect.top+card_played_height);
						
						topPlayedCardInfoList.add(info);
						
					}
				}
				topPlayedCardInfoList.notify();
			}
		}
		if(player_index==3)
		{
			//list=rightPlayedCardInfoList;
			synchronized (rightPlayedCardInfoList)
			{
				if(card_list==null)
				{
					rightPlayedCardInfoList.clear();
					return;
				}
				else
				{
					int length=card_list.length;
					int firstLeft=right_card_play_rect.left-((length-1)*playedCardGap+card_played_width);
					for(int i=0;i<card_list.length;i++)
					{
						CardInfo info=new CardInfo();
						
						info.num=(card_list[i]%13);
						info.huase=card_list[i]/13;
						
						info.rect_paint=new Rect(firstLeft+playedCardGap*i,
								right_card_play_rect.top,
								firstLeft+playedCardGap*i+card_played_width,
								right_card_play_rect.top+card_played_height);
						
						rightPlayedCardInfoList.add(info);
						
					}
				}
				rightPlayedCardInfoList.notify();
			}
		}
		
		
	}

	private void paintPlayedCard(Canvas canvas, ArrayList<CardInfo> PlayedCardInfoList) {
		
		synchronized (PlayedCardInfoList)
    	{
			for(CardInfo info:PlayedCardInfoList)
			{
				Rect tmp=getRect(info.huase,info.num);
				canvas.drawBitmap(my_card.bmp_src, tmp,info.rect_paint,bitmap_paint);
				
			}
		
			PlayedCardInfoList.notify();
    	}
	}

	private void paintNoCard(Canvas canvas) {
		
		canvas.drawBitmap(my_card_no_card.bmp_src, null,my_card_no_card_rect,bitmap_paint);
		
	}

	private void paintPlayCard(Canvas canvas) {
		
		
	}

	private void paintBottomCard(Canvas canvas) {
		
		synchronized (cardInfoList)
    	{
		
			for(CardInfo info:cardInfoList)
			{
				canvas.drawBitmap(my_card.bmp_src, getRect(info.huase,info.num),info.rect_paint,bitmap_paint);
			}
			cardInfoList.notify();
    	}
		
	}

	private Rect getRect(int huase, int num) {
		
		int real_num=num-3;
		if(real_num<0) real_num=real_num+13;
		
		return new Rect(real_num*103,huase*128,(real_num+1)*103,(huase+1)*128);
	}
	private void changeRect(boolean isSelected,int index) {
		
		if(isSelected)
		{
			//Log.d("lichao","index "+index+" is pressed true");
			cardInfoList.get(index).rect=new Rect(cardInfoList.get(index).rect.left,cardInfoList.get(index).rect.top-select_offset,cardInfoList.get(index).rect.right,cardInfoList.get(index).rect.bottom-select_offset);
			cardInfoList.get(index).rect_paint=new Rect(cardInfoList.get(index).rect_paint.left,cardInfoList.get(index).rect_paint.top-select_offset,cardInfoList.get(index).rect_paint.right,cardInfoList.get(index).rect_paint.bottom-select_offset);
			
		}
		else
		{
		//	Log.d("lichao","index "+index+" is pressed false");
			cardInfoList.get(index).rect=new Rect(cardInfoList.get(index).rect.left,cardInfoList.get(index).rect.top+select_offset,cardInfoList.get(index).rect.right,cardInfoList.get(index).rect.bottom+select_offset);
			cardInfoList.get(index).rect_paint=new Rect(cardInfoList.get(index).rect_paint.left,cardInfoList.get(index).rect_paint.top+select_offset,cardInfoList.get(index).rect_paint.right,cardInfoList.get(index).rect_paint.bottom+select_offset);

		}
			
	}

	public void touch(MotionEvent event) {
		
		if((event.getAction() & MotionEvent.ACTION_MASK)==MotionEvent.ACTION_DOWN)
		{
			if(Tools.isInArea(event, my_card))
			{
				int i=0;
				synchronized (cardInfoList)
		    	{
					for(CardInfo info:cardInfoList)
					{
					
						if(Tools.isInRect(event, info.rect))
						{
							info.isSelected=!info.isSelected;
							changeRect(info.isSelected,i);
						//	updateSelectCardlist();
							break;
						}
						i++;
					}
					cardInfoList.notify();
		    	}
			}
		
		}
	}

	
	private CardInfo copyOfCardInfo(CardInfo info) {
		
		CardInfo ret=new CardInfo();
		
		ret.huase=info.huase;
		ret.num=info.num;
		ret.pos=new Point();
		ret.rect_paint=new Rect();
		return ret;
		
	}

	public boolean updateMyPlayedCard() {
		
		
		
		synchronized (myPlayedCardInfoList)
    	{
		
			myPlayedCardInfoList.clear();
			
			for(CardInfo info:cardInfoList)
			{
			
				if(info.isSelected)
				{
					
					myPlayedCardInfoList.add(copyOfCardInfo(info));
				}
				
			}
			
			if(myPlayedCardInfoList.size()==0) return false;
			
			int cardWidth=playedCardGap*(myPlayedCardInfoList.size()-1)+card_played_width;
			
			Point firstCardPoint=new Point();
			firstCardPoint.x=my_card_play_rect.left+(my_card_play_rect.width()-cardWidth)/2;
			firstCardPoint.y=my_card_play_rect.top;
			
			int i=0;
			for(CardInfo info:myPlayedCardInfoList)
			{
				info.pos.x=firstCardPoint.x+i*playedCardGap;
				info.pos.y=firstCardPoint.y;
				
				info.rect_paint.left=info.pos.x;
				info.rect_paint.top=info.pos.y;
				info.rect_paint.right=info.pos.x+card_played_width;
				info.rect_paint.bottom=info.pos.y+card_played_height;
						
				i++;
			}
			myPlayedCardInfoList.notify();
    	}
		
		
		updateMyCardList();
		return true;
		
	}

	private void updateMyCardList() {
		synchronized (cardInfoList)
    	{
		
			for(int i=0;i<cardInfoList.size();i++)
			{
				if(cardInfoList.get(i).isSelected)
				{
					cardInfoList.remove(i);
					i--;
				}
			}
			
			if(cardInfoList.size()==1)
			{
				int firstCardLeft=my_card_rect.left+(my_card_rect.width()-card_width)/2;
				cardInfoList.get(0).pos.x=firstCardLeft;
				cardInfoList.get(0).rect.left=firstCardLeft;
				
				cardInfoList.get(0).rect.right=firstCardLeft+card_width;
				cardInfoList.get(0).rect_paint.left=firstCardLeft;
				cardInfoList.get(0).rect_paint.right=firstCardLeft+card_width;
			}
			else
			{
				int gap_new=(my_card_rect.width()-card_width)/(cardInfoList.size()-1);
				
				if(gap_new>card_width) gap_new=card_width;
				
				int width_new=gap_new*(cardInfoList.size()-1)+card_width;
				
				int firstCardLeft=my_card_rect.left+(my_card_rect.width()-width_new)/2;
				
				int k=0;
				for(CardInfo info:cardInfoList)
				{
					info.pos.x=firstCardLeft+gap_new*k;
					info.rect.left=info.pos.x;
					info.rect.top=info.pos.y;
					info.rect.right=info.pos.x+gap_new;
					info.rect_paint.left=info.rect.left;
					info.rect_paint.right=info.rect_paint.left+card_width;
					k++;
				}
			}
			cardInfoList.notify();
    	}
		
	}

	
	

}
