package carweibo.netradar.lichao;





import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.view.View.OnTouchListener;

public class ZoomImageView extends ImageView  {

	Bitmap bmp;
	float density;
	int state_height;
	Context context;
	int screenWidth,screenHeight;
	Point screen_mid;
	Point mid,picSize;
	int picX,picY;
	boolean isEage=true;
	public enum MODE {
		NONE, DRAG, ZOOM,DRAG_SLOW,DBTAP

	};
	MODE mode=MODE.NONE;
	Matrix org,org_min;
	
	int MAX_W,MIN_W,MAX_H,MIN_H;
	int cur_x,cur_y,start_x,start_y;
	Matrix startMatrix=new Matrix();
	Matrix afterMatrix=new Matrix();
	float beforeDis,afterDis;
	float scale;
	boolean enterSlowFlag=false;
	boolean isBmpNull=false;
	public ZoomImageView(Context context) {
		super(context);
		
	}
    public ZoomImageView(Context context, AttributeSet attrs, int defStyle) { 
        super(context, attrs, defStyle); 
       
    } 
 
    public ZoomImageView(Context context, AttributeSet attrs) { 
    	
        super(context, attrs); 
       // this.context=context;
       //Log.d("lichao","my imageview");
        screen_mid=new Point();
        mid=new Point();
        picSize=new Point();
        
        
    }
    
    public void setInfo(float denisty,int state_height,int screenWidth,int screenHeight)
    {
    	this.density=denisty;
    	this.state_height=state_height;
    	this.screenWidth=screenWidth;
    	this.screenHeight=screenHeight;
    }
    
	@Override
	public void setImageBitmap(Bitmap bm) {
		
		super.setImageBitmap(bm);
		this.bmp=bm;
		
		if(bmp!=null)
		{
			
			initImage(bmp);
			isBmpNull=false;
		}
		else
			isBmpNull=true;
	}
	
	
	
	
	private void initImage(Bitmap bm) {
		
		 

		screen_mid.x=screenWidth/2;
		screen_mid.y=screenHeight/2;
		org=new Matrix();
		org_min=new Matrix();
		
		float values[]=new float[9];
		//org.setValues(values);
	//	Log.d("lichao","screenWidth is "+screenWidth);
	//	Log.d("lichao","bm width is "+bm.getWidth());
		
		
		org.set(getImageMatrix());
		
		org.getValues(values);
	//	for(int i=0;i<9;i++)
	//	Log.d("lichao","\nvalues is "+values[i]);
		
		float scale1=(float)screenWidth/(float)bm.getWidth();
						
		float scale2=(float)screenHeight/(float)bm.getHeight();
		float scale;
		if(scale1<=scale2)  scale=scale1;
		else scale=scale2;
		
		float shitH=(float)((float)screenHeight-(float)((float)bm.getHeight()*scale1))/(float)2.0f;
		float shitW=(float)((float)screenWidth-(float)((float)bm.getWidth()*scale2))/(float)2.0f;
		
		if(values[0]==1.0f)
			org.postScale(scale,scale);
		if(scale1<=scale2)
		{
			/*values[0]=scale1;
			values[1]=0.0f;
			values[2]=0.0f;
			values[3]=0.0f;
			values[4]=scale1;
			values[5]=shitH;
			values[6]=0.0f;
			values[7]=0.0f;
			values[8]=1.0f;*/
			if(values[0]==1.0f)
			org.postTranslate(0, shitH);
		}
		else
		{
		/*	values[0]=scale2;
			values[1]=0.0f;
			values[2]=shitW;
			values[3]=0.0f;
			values[4]=scale2;
			values[5]=0.0f;
			values[6]=0.0f;
			values[7]=0.0f;
			values[8]=1.0f;*/
			if(values[0]==1.0f)
			org.postTranslate(shitW, 0);
		
		}
		
	//	org.setValues(values);
		
		org_min.set(org);
		if(scale>0.9f&&scale<1.1f)
			org_min.postScale(0.5f, 0.5f, screen_mid.x, screen_mid.y);
		else
			org_min.postScale(1/scale, 1/scale, screen_mid.x, screen_mid.y);
		setImageMatrix(org);
		//Log.d("lichao","bm size is "+bm.getWidth()+"  "+bm.getHeight());
	//	iv.setImageBitmap(bm);
	
		picSize.set((int)(bm.getWidth()),(int)(bm.getHeight()));
		MAX_W=screenWidth*2;
		MAX_H=screenHeight*2;
		MIN_W=screenWidth/2;
		MIN_H=screenHeight/2;
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
	//	if(bmp==null) return true;
		/*switch(event.getAction() & MotionEvent.ACTION_MASK)
		{
			case MotionEvent.ACTION_UP:
				onTouchUp(event);
			break;
			
			case MotionEvent.ACTION_DOWN:
				OnTouchDown(event);
				break;
			case MotionEvent.ACTION_MOVE:
				OnMove(event);
				return true;
			case MotionEvent.ACTION_POINTER_DOWN:
				OnPointerDown(event);
				break;
			case MotionEvent.ACTION_POINTER_UP:
				
				OnPointUp(event);
		
		}*/
		return false;
		//return super.onTouchEvent(event);
	}
	public void OnPointUp(MotionEvent event) {
		
		
	}
	public void OnPointerDown(MotionEvent event) {
		mode=MODE.ZOOM;
		
		if(event.getPointerCount()==2){
			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			

			mid.set((int)(event.getX(0)+event.getX(1))/2, (int)(event.getY(0)+event.getY(1))/2);
			
			Matrix tmx=new Matrix();
			tmx.set(getImageMatrix());
			float v[]=new float[9];
			tmx.getValues(v);
			
		//	mid.set((int)(v[2]+v[0]*picSize.x/2),(int)(v[5]+v[0]*picSize.y/2));
			
			

			
			beforeDis= FloatMath.sqrt(x * x + y * y);
		}
		
		
	}
	boolean canScroll=true;
	boolean canScroll_flag;
	public void OnMove(MotionEvent event) 
	{
		if(canScroll_flag&&mode==MODE.DRAG)
		{
			cur_x=(int)event.getX();
			cur_y=(int)event.getY();
			
			
			
			float fx,fy;
			fx=cur_x-start_x;
			fy=cur_y-start_y;
			if(Math.abs(fy)<16) return;
			
			
			else if((Math.abs(fy)/Math.abs(fx))>0.6f)
			{
				Log.d("lichao","canscroll is false");
				canScroll=false;
			}
			else
			{
				Log.d("lichao","canscroll is true");
				canScroll=true;
			}
			canScroll_flag=false;
		}
		
		if(enterSlowFlag&&mode!=MODE.ZOOM)
		{
			cur_x=(int)event.getX();
			cur_y=(int)event.getY();
			
			
			
			float fx,fy;
			fx=cur_x-start_x;
			fy=cur_y-start_y;
			
			if(Math.abs(fx)<8&&Math.abs(fy)<8) return;
			
		//	Log.d("lichao","data is "+fx+" "+fy+" ");
			if(Math.abs(fy)>Math.abs(fx))
			{
			//	Log.d("lichao","slow flag ok");
				mode=MODE.DRAG_SLOW;
			}
			enterSlowFlag=false;
		}
		
		if(mode==MODE.DRAG_SLOW)
		{
			cur_x=(int)event.getX();
			cur_y=(int)event.getY();
			
			
			
			float fx,fy;
			fx=cur_x-start_x;
			fy=cur_y-start_y;
			
			
			afterMatrix.set(startMatrix);
				

			afterMatrix.postTranslate(0, fy/3);
			setImageMatrix(afterMatrix);

			return;
		}
		
		if(mode==MODE.DRAG)
		{
			
			cur_x=(int)event.getX();
			cur_y=(int)event.getY();
			
			
			
			float fx,fy;
			fx=cur_x-start_x;
			fy=cur_y-start_y;
			
			
			afterMatrix.set(startMatrix);
				

			afterMatrix.postTranslate(fx, fy);
			
			float v[]=new float[9];
			afterMatrix.getValues(v);
			
			float fxd=fx,fyd=fy;
			if((cur_x>start_x&&v[2]>=0)||(cur_x<start_x&&(v[2]+v[0]*picSize.x)<=screenWidth))
			{
				
				fxd=0;
					
			}
			if((cur_y>start_y&&v[5]>=0)||(cur_y<start_y&&(v[5]+v[0]*picSize.y)<=screenHeight))
			{
				
				fyd=0;
				
					
			}
			/*if(v[5]>=0)
			{
				fyd=fy/2;
			}*/
			
			afterMatrix.set(startMatrix);
			afterMatrix.postTranslate(fxd, fyd);
			

			setImageMatrix(afterMatrix);
			startMatrix.set(afterMatrix);
			start_x=cur_x;
			start_y=cur_y;
			
			

			
			
			
			
		}
		if(mode==MODE.ZOOM&&event.getPointerCount()==2)
		{
			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			

	
			
			
			afterDis= FloatMath.sqrt(x * x + y * y);
			
			Matrix mx=new Matrix();
			mx.set(getImageMatrix());
		
			
			scale=afterDis/beforeDis;
			afterMatrix.set(startMatrix);
			afterMatrix.postScale(scale, scale, mid.x, mid.y);
			float value[]=new float[9];
			afterMatrix.getValues(value);
			
			
			if(scale>1&&(picSize.x*value[0]>=MAX_W&&picSize.y*value[0]>=MAX_H)||
					(scale<1&&(picSize.x*value[0]<=MIN_W&&picSize.y*value[0]<=MIN_H)))
			{
				afterMatrix.set(startMatrix);
				afterMatrix.postScale(1, 1,mid.x,mid.y);
				
			}
			else
			{
				if(scale<1)
				{
					if(value[2]>=0&&(value[2]+picSize.x*value[0])>screenWidth)
					{
						value[2]=0;
						afterMatrix.setValues(value);
					}
					if(value[2]<0&&(value[2]+picSize.x*value[0])<screenWidth)
					{
						value[2]=value[2]+screenWidth-(value[2]+picSize.x*value[0]);
						afterMatrix.setValues(value);
					}
					if(value[5]>=0&&(value[5]+picSize.y*value[0])>screenHeight)
					{
						value[5]=0;
						afterMatrix.setValues(value);
					}
					if(value[5]<0&&(value[5]+picSize.y*value[0])<screenHeight)
					{
						value[5]=value[5]+screenHeight-(value[5]+picSize.y*value[0]);
						afterMatrix.setValues(value);
					}
					
					if((value[2]>=0&&(value[2]+picSize.x*value[0])<=screenWidth)||(value[5]>=0&&(value[5]+picSize.y*value[0])<=screenHeight))
					{
						afterMatrix.set(startMatrix);
						afterMatrix.postScale(scale, scale,screen_mid.x,screen_mid.y);
					}
				}
				if(scale>1)
				{
					if((value[2]>=0&&(value[2]+picSize.x*value[0])<=screenWidth)||(value[5]>=0&&(value[5]+picSize.y*value[0])<=screenHeight))
					{
						afterMatrix.set(startMatrix);
						afterMatrix.postScale(scale, scale,screen_mid.x,screen_mid.y);
					}
				}
			}

		
			setImageMatrix(afterMatrix);
			startMatrix.set(afterMatrix);
			beforeDis=afterDis;

			
			
		}
		
	}
	public void OnTouchDown(MotionEvent event) {
		
		
		mode=MODE.DRAG;
		cur_x=start_x=(int)event.getX();
		cur_y=start_y=(int)event.getY();
		
		picX=getLeft();
		picY=getTop();
		startMatrix.set(getImageMatrix());

	//	Matrix mx=new Matrix();
	//	mx.set(iv.getImageMatrix());
		float value[]=new float[9];
		startMatrix.getValues(value);
		enterSlowFlag=false;
		canScroll_flag=false;
		if(value[2]>=0&&value[5]>=0)
		{
			//mode=MODE.DRAG_SLOW;
			enterSlowFlag=true;
			Log.d("lichao","slow flag");
		}
		
		if(value[2]>=0&&value[5]<0)
		{
			canScroll_flag=true;
		}
		
	}
	public void onTouchUp(MotionEvent event) {
	
		if(mode==MODE.DRAG_SLOW)
		{
			setImageMatrix(startMatrix);
			
		}
		
		mode=MODE.NONE;
		return; 
	}


    
	
}
