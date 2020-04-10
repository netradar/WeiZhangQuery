package carweibo.netradar.lichao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import carweibo.netradar.lichao.AsyncLoadImage.OnRefreshProgress;



import android.app.Activity;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ViewPic extends Activity implements OnDoubleTapListener, OnItemSelectedListener {//, android.view.GestureDetector.OnGestureListener
	ImageView iv;
	AsyncLoadImage imageLoad;
	TextView progress;
	int cur_x,cur_y,start_x,start_y;
	int pic_height,pic_width;
	int picX,picY;
	float beforeDis,afterDis;
	private enum MODE {
		NONE, DRAG, ZOOM,DRAG_SLOW,DBTAP

	};
	MODE mode=MODE.NONE;
	float scale;
	float fouseRateX,fouseRateY;
	int dragsum=0;
	Matrix startMatrix=new Matrix();
	Matrix afterMatrix=new Matrix();
	Point mid=new Point();
	Point screen_mid=new Point();

	Point picSize=new Point();
	Point startPicSize=new Point();
	float totalScale;
	float orignalScale;
	int MAX_W,MAX_H;
	int MIN_W,MIN_H;
	int screenWidth,screenHeight;
	Matrix org;
	Matrix org_min;
	int state_height;
	GestureDetector gestureScanner;
	
	boolean doubleTapFlag=true;
	MyGallery gallery;
	GalleryAdapter ga;

	TextView current_pic_index;
	int total_pic;
	List<GalleryItemInfo> url_list;
	private void onTouchUp(MotionEvent event) {
		
		if(mode==MODE.DRAG_SLOW)
		{
			iv.setImageMatrix(startMatrix);
			
		}
		
		mode=MODE.NONE;
		return;
	}

	private void OnPointUp(MotionEvent event) {
		
		
		
		mode=MODE.NONE;
		Matrix mx=new Matrix();
		mx.set(iv.getImageMatrix());
		
		float value[]=new float[9];
		mx.getValues(value);
		
		if((value[2]>0&&value[5]>0)||((value[2]+picSize.x*value[0])<screenWidth&&(value[5]+picSize.y*value[0])<screenHeight))
			
		{
			//iv.setImageMatrix(org);
		}
		
	}

	public class GetPicFromUrl extends AsyncTask<String, Integer, Bitmap> implements OnRefreshProgress{

		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			imageLoad.setOnRefreshProgress(this);
	
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			//iv.setImageBitmap(result);
			Init(result);
			progress.setVisibility(View.GONE);
			pic_height=result.getHeight();
			pic_width=result.getWidth();
						
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			float per=values[0]*100/values[1];
			
			progress.setText(per+"%");
			
		}

		@Override
		protected Bitmap doInBackground(String... url) {
			// TODO Auto-generated method stub
			Bitmap bm=null;
			try {
				bm=imageLoad.LoadImage(url[0]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return bm;
			
		}

		@Override
		public void refresh(long progress, long len) {
			publishProgress((int)progress/1024,(int)len/1024);
			
		}

		
		
	}

	@Override
	protected void onCreate(Bundle bd) {
		// TODO Auto-generated method stub
		
	super.onCreate(bd);
	/*		this.setContentView(R.layout.view_pic);
		state_height=this.getIntent().getIntExtra("height", 0);
		iv=(MyImageView)this.findViewById(R.id.imageView1);
	
		progress=(TextView)this.findViewById(R.id.progress);
		String imageUrl=this.getIntent().getStringExtra("url");
		imageLoad= new AsyncLoadImage();
		imageUrl=imageUrl.substring(imageUrl.indexOf("http"),imageUrl.length());
		
		imageUrl=imageUrl.substring(0,imageUrl.length()-6)+".png";
		
		
		new GetPicFromUrl().execute(imageUrl);
		gestureScanner= new GestureDetector(this);
		gestureScanner.setOnDoubleTapListener(this);*/
				
		this.setContentView(R.layout.view_pic_gallery);
		state_height=this.getIntent().getIntExtra("height", 0);
		gallery=(MyGallery)this.findViewById(R.id.view_pic_gallery);
		current_pic_index=(TextView)this.findViewById(R.id.current_pic_index);
		//iv=(MyImageView)this.findViewById(R.id.imageView1);
	
	//	gallery.setOnItemSelectedListener(this);
	//	progress=(TextView)this.findViewById(R.id.progress);
	//	String imageUrl=this.getIntent().getStringExtra("url");
		imageLoad= new AsyncLoadImage();
		
		ImageAndText imt=(ImageAndText) this.getIntent().getExtras().getSerializable("data");
		
		/*imageUrl=imageUrl.substring(imageUrl.indexOf("http"),imageUrl.length());
		
		imageUrl=imageUrl.substring(0,imageUrl.length()-6)+".png";*/
		
		url_list=new ArrayList<GalleryItemInfo>();
		
		for(int i=0;i<imt.pic_list_url.size();i++)
		{
			GalleryItemInfo ginfo=new GalleryItemInfo();
			ginfo.url=imt.pic_list_url.get(i).url.substring(imt.pic_list_url.get(i).url.indexOf("http"),imt.pic_list_url.get(i).url.length());
			ginfo.url=ginfo.url.substring(0,ginfo.url.length()-6)+".png";
			ginfo.progress="0%";
			
			url_list.add(ginfo);
		}
		total_pic=url_list.size();
		screenWidth=this.getIntent().getIntExtra("screenWidth", 0);
		screenHeight=this.getIntent().getIntExtra("screenHeight", 0);
		int index=this.getIntent().getIntExtra("index", 0);
		ga=new GalleryAdapter(this,gallery,url_list,state_height,screenWidth,screenHeight);
		gallery.setAdapter(ga);
		gallery.setSelection(index);
		gallery.setOnItemSelectedListener(this);
	}
	
	private void Init(Bitmap bm)
	{

		screenWidth  = getWindowManager().getDefaultDisplay().getWidth();       // 屏幕宽（像素，如：480px）   
		screenHeight = getWindowManager().getDefaultDisplay().getHeight()-state_height;      // 屏幕高（像素，如：800p）   
		 

		screen_mid.x=screenWidth/2;
		screen_mid.y=screenHeight/2;
	/*	//Bitmap tempbm=BitmapFactory.decodeFile("/storage/sdcard2/Photo/IMG_20130504_075452.jpg");
		BitmapFactory.Options  option=new BitmapFactory.Options();
		option.inJustDecodeBounds=true;
		BitmapFactory.decodeFile("/storage/sdcard0/DCIM/Camera/IMG_20130517_183723_053.jpg",option);
		//Bitmap tempbm=BitmapFactory.decodeResource(null, R.drawable.test);
		int bw=bm.getWidth();
		int bh=bm.getHeight();
		
		int rate=bw/bmpWidth;
		
		option.inJustDecodeBounds=false;
		option.inSampleSize=rate;
		Bitmap tempbm=BitmapFactory.decodeFile("/storage/sdcard0/DCIM/Camera/IMG_20130517_183723_053.jpg",option);
	*/	
		
		
		org=new Matrix();
		org_min=new Matrix();
		org.set(iv.getImageMatrix());
		
		float va[]=new float[9];
		org.getValues(va);
		
		float scale1=(float)screenWidth/(float)bm.getWidth();
						
		float scale2=(float)screenHeight/(float)bm.getHeight();
		float scale;
		if(scale1<=scale2)  scale=scale1;
		else scale=scale2;
		
		float shitH=(float)((float)screenHeight-(float)((float)bm.getHeight()*scale1))/(float)2.0f;
		float shitW=(float)((float)screenWidth-(float)((float)bm.getWidth()*scale2))/(float)2.0f;
		
		org.postScale(scale,scale);
		if(scale1<=scale2)
		{
			org.postTranslate(0, shitH);
		
			}
		else
		{
			org.postTranslate(shitW, 0);
		
		}
		
		org_min.set(org);
		org_min.postScale(1/scale, 1/scale, screen_mid.x, screen_mid.y);
		
		iv.setImageMatrix(org);
		//Log.d("lichao","bm size is "+bm.getWidth()+"  "+bm.getHeight());
		iv.setImageBitmap(bm);
	
		picSize.set((int)(bm.getWidth()),(int)(bm.getHeight()));
		MAX_W=screenWidth*2;
		MAX_H=screenHeight*2;
		MIN_W=screenWidth/2;
		MIN_H=screenHeight/2;
	

	}
	//float fxLast=0;
	private void OnTouchDown(MotionEvent event)
	{
		mode=MODE.DRAG;
		cur_x=start_x=(int)event.getX();
		cur_y=start_y=(int)event.getY();
		
		picX=iv.getLeft();
		picY=iv.getTop();
		startMatrix.set(iv.getImageMatrix());

	//	Matrix mx=new Matrix();
	//	mx.set(iv.getImageMatrix());
		float value[]=new float[9];
		startMatrix.getValues(value);
		
		if(value[5]>=0)
		{
			mode=MODE.DRAG_SLOW;
		}
		
	//	fxLast=0-value[2];
		
		//iv.setImageMatrix(afterMatrix);
	}
	boolean leftOver=false;
	float fxMove;
	private void OnMove(MotionEvent event)
	{
		if(mode==MODE.DRAG_SLOW)
		{
			cur_x=(int)event.getX();
			cur_y=(int)event.getY();
			
			
			
			float fx,fy;
			fx=cur_x-start_x;
			fy=cur_y-start_y;
			
			
			afterMatrix.set(startMatrix);
				

			afterMatrix.postTranslate(0, fy/3);
			iv.setImageMatrix(afterMatrix);
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
			

			iv.setImageMatrix(afterMatrix);
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
			mx.set(iv.getImageMatrix());
		
			
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
					
					if(value[2]>=0&&(value[2]+picSize.x*value[0])<=screenWidth&&value[5]>=0&&(value[5]+picSize.y*value[0])<=screenHeight)
					{
						afterMatrix.set(startMatrix);
						afterMatrix.postScale(scale, scale,screen_mid.x,screen_mid.y);
					}
				}
				if(scale>1)
				{
					if(value[2]>=0&&(value[2]+picSize.x*value[0])<=screenWidth&&value[5]>=0&&(value[5]+picSize.y*value[0])<=screenHeight)
					{
						afterMatrix.set(startMatrix);
						afterMatrix.postScale(scale, scale,screen_mid.x,screen_mid.y);
					}
				}
			}

		
			iv.setImageMatrix(afterMatrix);
			startMatrix.set(afterMatrix);
			beforeDis=afterDis;

			
			
		}
		
	}
	private void OnPointerDown(MotionEvent event)
	{
		mode=MODE.ZOOM;
	
		if(event.getPointerCount()==2){
			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			

			mid.set((int)(event.getX(0)+event.getX(1))/2, (int)(event.getY(0)+event.getY(1))/2);
			
			Matrix tmx=new Matrix();
			tmx.set(iv.getImageMatrix());
			float v[]=new float[9];
			tmx.getValues(v);
			
		//	mid.set((int)(v[2]+v[0]*picSize.x/2),(int)(v[5]+v[0]*picSize.y/2));
			
			

			
			beforeDis= FloatMath.sqrt(x * x + y * y);
		}
		
	}

	@Override
	public boolean onDoubleTap(MotionEvent arg0) {
		// TODO Auto-generated method stub
	
		mode=MODE.DBTAP;
		if(doubleTapFlag)
		{
			iv.setImageMatrix(org_min);
			doubleTapFlag=false;
		}
		else
		{
			iv.setImageMatrix(org);
			doubleTapFlag=true;
		}
		
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent arg0) {
		
		
		return true;
		
		
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent arg0) {
		// TODO Auto-generated method stub
		this.finish();
		return false;
	}

	
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		current_pic_index.setText((arg2+1)+"/"+total_pic);
		
		gallery.processItemSelect(arg1);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
		
	}

	public void onSavePic(View v)
	{
		int cur_sel=gallery.getSelectedItemPosition();
		
		if(ga.bmpCache[cur_sel]!=null)
		{
			String sdDir=ScreenShoot.getSDDir(this);
			
			if(sdDir==null)
			{
				Toast.makeText(this, "T卡空间不足，无法进行存储～", Toast.LENGTH_SHORT).show();
				return;
			}
			
			String picName=getPicName(cur_sel);
			
			Log.d("lichao","View Pic picnam is "+picName);
			
			ScreenShoot.savePic(ga.bmpCache[cur_sel], sdDir+"/weizhangquery/save", sdDir+"/weizhangquery/save/"+picName+".jpg", 100);
		
			Toast.makeText(this, "图片已保存到"+sdDir+"/weizhangquery/save/"+picName+".jpg", Toast.LENGTH_SHORT).show();
		}
		
	}

	private String getPicName(int cur_sel) {
		
		String url=url_list.get(cur_sel).url;
		
		return url.substring(url.lastIndexOf("/")+1,url.length()-4);
	}
	




}
