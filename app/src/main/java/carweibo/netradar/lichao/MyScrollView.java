package carweibo.netradar.lichao;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {
	
	boolean isBottom=false;
	boolean isTop;
	CommentListView list;
	public MyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		

		
	}
	
	public void setListView(CommentListView v)
	{
		this.list=v;
	}

	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		// TODO Auto-generated method stub
		super.onScrollChanged(l, t, oldl, oldt);
		
		if(getScrollY() + getHeight() >=  computeVerticalScrollRange())   
		{   
		    isBottom=true;
	}   
		else  
		{   
		    isBottom=false;
		   
		} 
	}
	int startY;
	int originY;
	int nowY;
	MotionEvent ev;
	boolean isList=false;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		return false;
	/*	
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
		//	Log.d("lichao","scroll touch");
			startY=(int) event.getY();
			ev=event;
		//	return true;
		//	originY=startY;
		//	return super.onTouchEvent(event);
			
		}
		
		
		if((event.getAction()& MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE)
		{
			nowY=(int) event.getY();
		}
		//Log.d("lichao","nowY is "+nowY+" startY is "+startY+" pos is "+list.getFirstVisiblePosition()+" top is "+list.getChildAt(0).getTop());
		
		if(nowY>=startY&&getScrollY()==0)
		{
			startY=nowY;
			return true;
		}
		if(startY>nowY&&list.isBottom)
		{
			startY=nowY;
			return true;
		}
		if((nowY<startY&&isBottom))
		 {
		//	Log.d("lichao","list touch");
			 list.onTouchEvent1(event);
			 startY=nowY;
			 return true;
		 }
		else if((nowY>=startY&&!(list.getFirstVisiblePosition()==0&&list.getChildAt(0).getTop()==0)))
		{
			//Log.d("lichao","list touch1");
			 list.onTouchEvent1(event);
			 startY=nowY;
			 isList=true;
			 return true;
		}
		else
		{
			//Log.d("lichao","scroll touch");
			if(isList)
			{ev=MotionEvent.obtain(0, 0,  MotionEvent.ACTION_DOWN, event.getX(), event.getY(),0);
				super.onTouchEvent(ev);isList=false;}
			startY=nowY;
			super.onTouchEvent(event);
			return true;
		}
		//return true;
		if((event.getAction()& MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE)
		{
				
			nowY=(int) event.getY();
		
			if(startY>nowY)
			{
				if(isBottom==false)
				{
					//this.smoothScrollBy(0, startY-nowY);
					startY=nowY;
					return super.onTouchEvent(event);
					
				}
				else
				{
					
					list.smoothScrollBy(startY-nowY,0);
				
				}
			}
			else
			{
				if(!isBottom)
				{
					//this.smoothScrollBy(0, startY-nowY);
					startY=nowY;
					return super.onTouchEvent(event);
				}
				else
				{
					
					if((list.getFirstVisiblePosition()==0&&list.getChildAt(0).getTop()==0))
					{
						MotionEvent new_ev=event;
					//	new_ev.setLocation(event.getX(), originY+startY-nowY);
						this.smoothScrollBy(0, startY-nowY);
						startY=nowY;
						
						//return super.onTouchEvent(new_ev);
					}
					else
					{
						list.smoothScrollBy(startY-nowY,5);
					//	list.onTouchEvent(event);
					}
				}
			}
			
			startY=nowY;
			
		}
		else
			return super.onTouchEvent(event);
		//return	super.onTouchEvent(event);
		
		//return 
*/	}




}
