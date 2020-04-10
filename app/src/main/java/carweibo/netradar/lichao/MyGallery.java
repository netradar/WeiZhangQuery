package carweibo.netradar.lichao;




import android.content.Context;
import android.graphics.Matrix;

import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.Gallery;


public class MyGallery extends Gallery implements  OnTouchListener  {

	ZoomImageView.MODE mode;
	
	public enum MODE_A {
		NONE, SCROLL,DRAG_SLOW,ZOOM

	};
	GestureDetector gesture;
	boolean imageDB=false;
	boolean isFling;
	MODE_A mode_a=MODE_A.NONE;
	ZoomImageView ziv;
	public MyGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnTouchListener(this);
		//this.setOnItemSelectedListener(this);
		gesture=new GestureDetector(new MySimpleGesture());
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gesture.onTouchEvent(event);
		
		//return false;
		return super.onTouchEvent(event);
	}



	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
	//	Log.d("lichao","scroll");
		
		if(ziv.isBmpNull)
		{
			isFling=true;
			super.onScroll(e1, e2, distanceX, distanceY);
			return false;
		}
		Matrix mx=new Matrix();
		
		mx.set(ziv.afterMatrix);
		float v[]=new float[9];
		mx.getValues(v);
		
		isFling=false;
		if(ziv.mode!=ZoomImageView.MODE.DRAG)
			return false;
		isFling=true;
		
		if((v[2]>=-1||((v[2]+v[0]*ziv.picSize.x)<=ziv.screenWidth+1))&&ziv.canScroll)
		{
			mode_a=MODE_A.SCROLL;
			super.onScroll(e1, e2, distanceX, distanceY);
		}
		
			return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
	//	Log.d("lichao","fling");
		/*if(ziv.mode!=ZoomImageView.MODE.DRAG)
			return false;
			Matrix mx=new Matrix();
		
		mx.set(ziv.afterMatrix);
		float v[]=new float[9];
		mx.getValues(v);
		//Log.d("lichao","filing");
		if(v[2]>=-10||((v[2]+v[0]*ziv.picSize.x)<=ziv.screenWidth+10))
			return super.onFling(e1, e2, velocityX, velocityY);*/
		/*//return false;
		if(ziv.mode!=ZoomImageView.MODE.NONE)
			return true;
		Matrix mx=new Matrix();
		
		mx.set(ziv.afterMatrix);
		float v[]=new float[9];
		mx.getValues(v);
		if(!(v[2]>=-10||((v[2]+v[0]*ziv.picSize.x)<=ziv.screenWidth+10)))
			return true;
		int kEvent;
	      if (e1.getX()+10< e2.getX()) {
	          // Check if scrolling left
	          kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
	          onKeyDown(kEvent, null);
	      } 
	      if(e1.getX()>e2.getX()+10)
	      {
	         // Otherwise scrolling right
	         kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
	         onKeyDown(kEvent, null);
	      }*/
		if(!isFling||!ziv.canScroll) return false;
		
		Matrix mx=new Matrix();
		
		mx.set(ziv.afterMatrix);
		float v[]=new float[9];
		mx.getValues(v);
		if(!(v[2]>=-10||((v[2]+v[0]*ziv.picSize.x)<=ziv.screenWidth+10)))
			return true;
		int kEvent;
	//	if(velocityX<10) return true;
		
	      if ((e2.getX()- e1.getX())>40) {
	          
	          kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
	          onKeyDown(kEvent, null);
	      } 
	      if((e1.getX()-e2.getX())>40)
	      {
	         
	         kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
	         onKeyDown(kEvent, null);
	      }
	      return false;

	}

	/*@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
	
		 ziv=(ZoomImageView)arg1.findViewById(R.id.gallery_item_imageView);
		 imageDB=false;
		 ziv.setImageMatrix(ziv.org);
		 
	}*/

	public void processItemSelect(View arg1)
	{
		ziv=(ZoomImageView)arg1.findViewById(R.id.gallery_item_imageView);
		 imageDB=false;
		 ziv.setImageMatrix(ziv.org);
	}
	/*@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
		
	}
*/
	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		
		if(ziv.isBmpNull) return false;
		
		switch(event.getAction() & MotionEvent.ACTION_MASK)
		{
			case MotionEvent.ACTION_UP:
				
				mode_a=MODE_A.NONE;
				ziv.onTouchUp(event);
			break;
			
			case MotionEvent.ACTION_DOWN:
				ziv.OnTouchDown(event);
				break;
			case MotionEvent.ACTION_MOVE:
				if(mode_a!=MODE_A.SCROLL)
				ziv.OnMove(event);
				return false;
			case MotionEvent.ACTION_POINTER_DOWN:
				ziv.OnPointerDown(event);
				break;
			case MotionEvent.ACTION_POINTER_UP:
				
				ziv.OnPointUp(event);
		
		}
		return false;
	}

	private class MySimpleGesture extends SimpleOnGestureListener {
		
		public boolean onDoubleTap(MotionEvent e) {
			ziv.mode=ZoomImageView.MODE.DBTAP;
			if(imageDB)
				ziv.setImageMatrix(ziv.org);
			else
				ziv.setImageMatrix(ziv.org_min);
			imageDB=!imageDB;
			return true;
		}
	}
	
	
}
