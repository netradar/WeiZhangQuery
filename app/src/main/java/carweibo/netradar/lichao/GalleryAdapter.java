package carweibo.netradar.lichao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import carweibo.netradar.lichao.AsyncImageLoader.ImageCallback;
import carweibo.netradar.lichao.AsyncImageLoader2.ImageCallback2;

import carweibo.netradar.lichao.AsyncImageLoader2.TextCallback2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

public class GalleryAdapter extends BaseAdapter implements TextCallback2, ImageCallback2 {

	Bitmap[] bmpCache;

	private List<GalleryItemInfo> list;
	LayoutInflater inflater;
	AsyncImageLoader2	asyncImageLoader2;
	Gallery gallery;
	int state_height,screenWidth,screenHeight;
	float denisty;
	public GalleryAdapter(Context context,Gallery gallery,List<GalleryItemInfo> list,int state_height,int screenWidth,int screenHeight) 
	{
		bmpCache=new Bitmap[3];
		for(int i=0;i<3;i++)
			bmpCache[i]=null;
		
		  this.list=list;
		  this.inflater=LayoutInflater.from(context);
		  this.gallery=gallery;
		  this.state_height=state_height;
		  this.screenHeight=screenHeight;
		  this.screenWidth=screenWidth;
		  
		  denisty=context.getResources().getDisplayMetrics().density;
	        
		  asyncImageLoader2=new AsyncImageLoader2();
		/*  imageCache = new HashMap<String, SoftReference<Bitmap>>();
	     threadCache =new HashMap<String,String>();*/
	        
	}

	@Override
	public int getCount() {
		
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		
		GalleryViewCache cacheView;
		if(convertView==null)
		{
			convertView=inflater.inflate(R.layout.gallery_item_layout, null);
			cacheView=new GalleryViewCache(convertView);
			convertView.setTag(cacheView);
		}else
		{
			cacheView=(GalleryViewCache) convertView.getTag();
		}
		
		cacheView.getProgress().setTag(String.valueOf(position));
		cacheView.getFailureView().setTag(String.valueOf(position)+"failure");
		cacheView.getImgView().setTag(position+list.get(position).url);
		//Bitmap bmp=asyncImageLoader.loadDrawable(list.get(position),new String().valueOf(position), this, 0);
		//Bitmap bmp=loadBitmapFromUrl(list.get(position),cacheView.getImgView(),cacheView.getProgress());
		
		if(list.get(position).loadFailure)
		{
			//cacheView.getImgView().setScaleType(ImageView.ScaleType.CENTER);
			cacheView.getImgView().setImageBitmap(null);
			cacheView.getFailureView().setVisibility(View.VISIBLE);
			cacheView.getProgress().setVisibility(View.GONE);
			//cacheView.getProgress().setText("图片下载失败！");
		}
		
		else
		{
			cacheView.getFailureView().setVisibility(View.GONE);
			Bitmap bmp=asyncImageLoader2.loadDrawable(list.get(position).url,String.valueOf(position), this,this);
			//cacheView.getImgView().setScaleType(ImageView.ScaleType.MATRIX);
			if(bmp==null)
			{
				cacheView.getImgView().setImageBitmap(null);
				cacheView.getProgress().setVisibility(View.VISIBLE);
				cacheView.getProgress().setText(list.get(position).progress);
				
			}
			else
			{
				bmpCache[position]=bmp;
				cacheView.getProgress().setVisibility(View.GONE);
				cacheView.getImgView().setInfo(denisty, state_height, screenWidth, screenHeight);
				cacheView.getImgView().setImageBitmap(bmp);
			}
		}
		return convertView;

	}

	@Override
	public void imageLoaded(Bitmap imageDrawable, String imageUrl, String id) {

		ZoomImageView iv=(ZoomImageView)gallery.findViewWithTag(id+imageUrl);
		
		if(iv!=null)
		{
			iv.setInfo(denisty, state_height, screenWidth, screenHeight);
		
			iv.setImageBitmap(imageDrawable);
			
			bmpCache[Integer.parseInt(id)]=imageDrawable;
			
			
			
		}
		TextView tv=(TextView)gallery.findViewWithTag(id);
		ImageView iv2=(ImageView)gallery.findViewWithTag(id+"failure");
		
		if(tv==null) return;
		if(imageDrawable==null)
		{
			
			list.get(Integer.parseInt(id)).setLoadFailure(true);
			if(iv2!=null)
				iv2.setVisibility(View.VISIBLE);
		//	iv.setImageBitmap(null);
			//tv.setText("图片下载失败！");
		}
		else
		{
			if(iv2!=null)
				iv2.setVisibility(View.GONE);
		}
		
			tv.setVisibility(View.GONE);
		
		
		
	}
	
	@Override
	public void textLoaded(String text, String id) {
		
		TextView iv=(TextView)gallery.findViewWithTag(id);
	//	Log.d("lichao","progress is "+text);
		if(iv!=null)
		{
			
			iv.setText(text+"%");
			
		}
		list.get(Integer.parseInt(id)).setProgress(text);
	}
/*
	 private HashMap<String, SoftReference<Bitmap>> imageCache;
	 private HashMap<String,String> threadCache;
	private Bitmap loadBitmapFromUrl(final String url,final ZoomImageView imgView,final TextView textView)
	{
		if (imageCache.containsKey(url)) {
       	 
            SoftReference<Bitmap> softReference = imageCache.get(url);
            Bitmap drawable = softReference.get();
            if (drawable != null) {
           
           	return drawable;
            }
        }
	   	 else
	   	 {
	   		 *//** 
	             * 加上一个对本地缓存的查找 
	             *//*  
	         //   Bitmap b=ReadFromSD(imageUrl);
	          //  if(b!=null)
	          // 	 return b;
	
	   	 }
   
   	 if(threadCache.containsKey(url))
   	 {
   		 if(threadCache.get(image).equals(id+imageUrl))
   		 {
   			// Log.d("lichao","thread is existed!");
   			 return null;
   		 //}
   		 
   	 }
        final Handler handler = new Handler() {
            public void handleMessage(Message message) {
           //	if(((String)(image.getTag())).equals(id+imageUrl))
           //	{
           		//imageCallback.imageLoaded((Bitmap) message.obj, imageUrl,id);
           		imgView.setImageBitmap((Bitmap) message.obj);
           //	}
           	else
           	{
           		Log.d("lichao","update is over");
           	}
                threadCache.remove(url);
                title_test--;
                title.setText(new String().valueOf(title_test));
           //     
               
            }
        };
        title_test++;
        title.setText(new String().valueOf(title_test));
       
       
        new Thread() {
            @Override
            public void run() {
                Bitmap bitmap = null;
                threadCache.put(url,null);
                
				
				try {
					bitmap = loadImage(url,textView);
				} catch (IOException e) {
					
				}
				if(bitmap==null)
				{
					 Message message = handler.obtainMessage(0, bitmap);
	                 
	                 handler.sendMessage(message);
	              //   Log.d("lichao","get touxiang failure1 "+imageUrl);
	                 return;
				}
				
				Log.d("lichao","bitmap is over");
                Message message = handler.obtainMessage(0, bitmap);
                
                handler.sendMessage(message);
                if(bitmap!=null)
                {
               	 //SaveToSD(imageUrl,bitmap);
               	
               	 imageCache.put(url, new SoftReference<Bitmap>(bitmap));
                }
                
                
            }
        }.start();
		return null;
	}
    
        private Bitmap loadImage(String url,TextView progress) throws IOException
        {
        	InputStream i = null;
    		long total=0;
    		try {
    			m = new URL(url);
    			i = (InputStream) m.getContent();
    			
    			HttpPost post=new HttpPost(url);
    		//	Log.d("lichao","before execute"+System.currentTimeMillis());
    			HttpResponse response=new DefaultHttpClient().execute(post);
    	//		Log.d("lichao","after execute"+System.currentTimeMillis());
    			if(response.getStatusLine().getStatusCode()==200)
    			{  
    		//		Log.d("lichao","before getentity"+System.currentTimeMillis());
    		        HttpEntity entity=response.getEntity();  
    		//        Log.d("lichao","after getentity"+System.currentTimeMillis());
    		        i=(InputStream)entity.getContent();
    		        total=entity.getContentLength();
    		  //      Log.d("lichao","after inputstram"+System.currentTimeMillis());
    			}  
    			else 
    				return null;
    		} catch (MalformedURLException e1) {
    			e1.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	
    		 ByteArrayOutputStream baos =new ByteArrayOutputStream();
        	 byte[] b =new byte[1024];
        	 int len =0;
        	 long sum=0;
        	 
        	 while ((len = i.read(b, 0,1024)) !=-1)
        	 {
    	    	 baos.write(b,0,len);
    	    	 
    	    	 baos.flush();
    	    	 sum+=len;
    	    //	 refreshProgress.refresh(sum,total);
    	    //	 progress.setText(new String().valueOf(sum/total));
    	    	 
        	 }
        	 byte[] bytes = baos.toByteArray();
        	 baos.close();
        	 
        	 InputStream i2=new ByteArrayInputStream(bytes);
        			 
    		
    		
    		Bitmap bm=BitmapFactory.decodeStream(i2, null, null);
    	
    		i2.close();
    		return bm;
    		
    		Log.d("lichao","before createstream");
    		Drawable d = Drawable.creat
    		Log.d("lichao","after createstream");
    								return d;
			
        	
        }*/

	
}
