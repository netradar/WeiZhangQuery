package carweibo.netradar.lichao;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class AsyncImageLoader {

	private static int PIC_MIN_LENGTH=50;
	private static int PIC_THUMB_LENGTH=80;
	 private HashMap<String, SoftReference<Bitmap>> imageCache;
	 private HashMap<String,String> threadCache;
	 private float denisty;
	 static int scale=1;
		private static int LOADIMG_TYPE_TOUXIANG=0;
		private static int LOADIMG_TYPE_SINGLE=1;
		private static int LOADIMG_TYPE_MULTI=2;
	
	  long sum=0;
	 
	     public AsyncImageLoader(float density) {
	    	 imageCache = new HashMap<String, SoftReference<Bitmap>>();
	    	 threadCache =new HashMap<String,String>();
	    	 
	    	 this.denisty=density;
	    	 
	    	
/*	    	// scale=1;
	    			 

	    	 if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
	    	 {  
	    		 sdCard=Environment.getExternalStorageDirectory();
	    		 
	    		 Log.d("lichao",sdCard.getPath());
	    		 StatFs statfs = new StatFs(sdCard.getPath()); 
	    		 long totalBlocks = statfs.getAvailableBlocks();
	    		 long blocSize = statfs.getBlockSize();
	    		 if((totalBlocks*blocSize)>1000000)
	    			 isSdCardFull=false;
	    		 else
	    			 isSdCardFull=true;
	    		 
	    				 
	    	 }*/
	     }
	  
	     public Bitmap loadDrawable(final String imageUrl,final String id, final ImageCallback imageCallback,final int type) {
	    	 
	    	 if (imageCache.containsKey(imageUrl)) 
	    	 {
	    		// Log.d("lichao","AsynImageLoader containsKey!");
	             SoftReference<Bitmap> softReference = imageCache.get(imageUrl);
	             Bitmap drawable = softReference.get();
	             if (drawable != null) 
	             {
	            	
	            	return drawable;
	             }
	             else
	             {
	            	 Bitmap b=ReadFromSD(imageUrl,type);
		             if(b!=null)
		             {
		            	 
		            	 return b;
		             }
	             }
	         }
	    	 else
	    	 {
	    		 /** 
	              * 加上一个对本地缓存的查找 
	              */  
	    		 
	             Bitmap b=ReadFromSD(imageUrl,type);
	             if(b!=null)
	             {
	            	 
	            	 return b;
	             }
	            
	    	 }
	    	
	         
	    	 if(threadCache.containsKey(id+imageUrl))
	    	 {
	    		 
	    		 /*if(threadCache.get(image).equals(id+imageUrl))
	    		 {*/
	    			// Log.d("lichao","thread is existed!");
	    			 return null;
	    		 //}
	    		 
	    	 }
	         final Handler handler = new Handler() {
	             public void handleMessage(Message message) {
	            //	if(((String)(image.getTag())).equals(id+imageUrl))
	            //	{
	            		imageCallback.imageLoaded((Bitmap) message.obj, imageUrl,id);
	            /*//	}
	            	else
	            	{
	            		Log.d("lichao","update is over");
	            	}*/
	                 threadCache.remove(id+imageUrl);
	               /*  title_test--;
	                 title.setText(new String().valueOf(title_test));*/
	            //     
	                
	             }
	         };
	      /*   title_test++;
	         title.setText(new String().valueOf(title_test));*/
	        
	        
	         new Thread() {
	             @Override
	             public void run() {
	                 Bitmap bitmap = null;
	                 threadCache.put(id+imageUrl,null);
	                 
	              //   Log.d("lichao","thread is start!");
					bitmap = getBitmap(imageUrl);
					if(bitmap==null)
					{
						 Message message = handler.obtainMessage(0, bitmap);
		                 
		                 handler.sendMessage(message);
		              //   Log.d("lichao","get touxiang failure1 "+imageUrl);
		                 return;
					}
					
					bitmap=progressBmp(bitmap,type);
					
					
	                 Message message = handler.obtainMessage(0, bitmap);
	                 
	                 handler.sendMessage(message);
	                 if(bitmap!=null)
	                 {
	                	// Log.d("lichao","get touxiang ok "+imageUrl);
	                //	
	                	 
	                	 SaveToSD(imageUrl,bitmap,type);
	                	
	                	 imageCache.put(imageUrl, new SoftReference<Bitmap>(bitmap));
	                 }
	                 else
	                	 Log.d("lichao","get touxiang failure "+imageUrl);
	                 
	                 
	             }

				private Bitmap progressBmp(Bitmap bitmap,int type)
				{
					Bitmap ret_bmp=bitmap;
					int width=bitmap.getWidth();
					int height=bitmap.getHeight();
					
					if(width/height<=2&&height/width<=2)
					{
						if(height<PIC_MIN_LENGTH*denisty||width<PIC_MIN_LENGTH*denisty)
						{
							if(height>=width)
							{
								
								height=(int) (height*((PIC_MIN_LENGTH*denisty)/(float)width));
								width=(int) (PIC_MIN_LENGTH*denisty);
							}
							else
							{
								width=(int) (width*((PIC_MIN_LENGTH*denisty)/(float)height));
								height=(int) (PIC_MIN_LENGTH*denisty);
							}
							ret_bmp=Bitmap.createScaledBitmap(bitmap, width, height, true);
						}
					}
					if(type==LOADIMG_TYPE_TOUXIANG||type==LOADIMG_TYPE_SINGLE)	
						return ret_bmp;
					else
						return getSmallPic(ret_bmp);
				}

				private Bitmap getSmallPic(Bitmap bmp)
				{
					
					Bitmap bm=bmp;
					int width=bm.getWidth();
					int height=bm.getHeight();
					
					
					if((height>PIC_THUMB_LENGTH*denisty)&&(width>PIC_THUMB_LENGTH*denisty))
					{
						int dstWidth;
						int dstHeight;
						if((height>=width))
						{
							dstWidth=(int) (PIC_THUMB_LENGTH*denisty);
							dstHeight=(int) ((float)height/((float)width/(PIC_THUMB_LENGTH*denisty)));
							//rate=width/(70*denisty);
							
						}
						else
						{
							dstHeight=(int) (PIC_THUMB_LENGTH*denisty);
							dstWidth=(int) ((float)width/((float)height/(PIC_THUMB_LENGTH*denisty)));
						}
						
						bm=Bitmap.createScaledBitmap(bm, dstWidth, dstHeight, true);
						
						dstWidth=bm.getWidth();
						dstHeight=bm.getHeight();
						int x = 0,y = 0;
						if(dstHeight>=dstWidth)
						{
							x=0;
							y=(int) (dstHeight/2-dstWidth/2);
							bm= Bitmap.createBitmap(bm, x, y, dstWidth, dstWidth);
						}
						else
						{
							x=(int) (dstWidth/2-dstHeight/2);
							y=0;
							bm= Bitmap.createBitmap(bm, x, y, dstHeight, dstHeight);
						}
						
						
						
						
						
						
							
						//return  Bitmap.createScaledBitmap(bm, dstWidth+1, dstHeight+1, true);
						//return  Bitmap.createBitmap(source, x, y, width, height)
						
					}
					else
					{
						int dstWidth;
						int dstHeight;
						int x = 0,y = 0;
						if((height>=width))
						{
							dstWidth=(int) (PIC_THUMB_LENGTH*denisty);
							dstHeight=(int) (((float) ((PIC_THUMB_LENGTH*denisty)/(float)width))*(float)height);
							x=0;
							y=dstHeight/2-dstWidth/2;
						}
						else
						{
							dstHeight=(int) (PIC_THUMB_LENGTH*denisty);
							dstWidth=(int) (((float) ((PIC_THUMB_LENGTH*denisty)/(float)height))*(float)width);
							y=0;
							x=dstWidth/2-dstHeight/2;
						}
						
						
						
						
						bm=Bitmap.createScaledBitmap(bm, dstWidth, dstHeight, true);
						
							
						
						bm=Bitmap.createBitmap(bm,x,y,(int)(PIC_THUMB_LENGTH*denisty),(int)(PIC_THUMB_LENGTH*denisty));
					}
					/*if(ScreenShoot.getSDDir(this)!=null)
					{
						String save_dir=fileName.substring(0, fileName.length()-4)+"_s.png";
						ScreenShoot.savePic(ret_bmp, ScreenShoot.getSDDir(this)+"/weizhangquery/pic/", save_dir, 70);
					}
					else
						return null;*/
					
					return bm;
					
				}
	         }.start();
	         
	         return null;
	     }
	  /*
		public static Bitmap loadImageFromUrl(String url) throws IOException {
			URL m;
			InputStream i = null;
			InputStream ii=null;
			long len=0;
			try {
				m = new URL(url);
				i = (InputStream) m.getContent();
				
				HttpPost post=new HttpPost(url);
				HttpResponse response=new DefaultHttpClient().execute(post);
				if(response.getStatusLine().getStatusCode()==200)
				{  
			        HttpEntity entity=response.getEntity();  
			        i=(InputStream)entity.getContent();
			        len=entity.getContentLength();
			        
			        
			        		        
			        
				}  
				else 
					return null;
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			BitmapFactory.Options  option=new BitmapFactory.Options();
			BitmapFactory.Options  option1=new BitmapFactory.Options();
			
			int rate=1;
			
			byte[] bt=getBytes(i);
			i=new ByteArrayInputStream(bt);
			InputStream i2=new ByteArrayInputStream(bt);
			bt=null;
			
			
			option.inJustDecodeBounds=true;
			
			BitmapFactory.decodeStream(i, null, option);
			i.close();
		
			 int be = option.outHeight / scale;
		    
		     if (be <= 0) 
		      be = 1; 
		     option1.inSampleSize=be;
			
	
			Bitmap b= BitmapFactory.decodeStream(i2, null, option1);
			i2.close();
			
			return b;
			
			Log.d("lichao","before createstream");
			Drawable d = Drawable.creat
			Log.d("lichao","after createstream");
									return d;
						
		}*/
		
		private Bitmap getBitmap(String biturl)
		  {
		    Bitmap bitmap=null;
		    
		    try {
		      URL url=new URL(biturl);
		      URLConnection conn=url.openConnection();
		      conn.setConnectTimeout(5000);
		      conn.setReadTimeout(5000);
		      InputStream in =conn.getInputStream();
		      bitmap=BitmapFactory.decodeStream(new BufferedInputStream(in));
		    
		      
		    } catch (Exception e) {
		     
		      
		    }
		    return bitmap;
		  }
	  
	     public interface ImageCallback {
	         public void imageLoaded(Bitmap imageDrawable, String imageUrl,String id);
	     }
	     
	     private static byte[] getBytes(InputStream is) throws IOException {

	    	 ByteArrayOutputStream baos =new ByteArrayOutputStream();
	    	 byte[] b =new byte[1024];
	    	 int len =0;

	    	 while ((len = is.read(b, 0,1024)) !=-1)
	    	 {
	    	 baos.write(b,0,len);
	    	 
	    	 baos.flush();
	    	 }
	    	 byte[] bytes = baos.toByteArray();
	    	 baos.close();
	    	 return bytes;
	    	 }
	     
	     private Bitmap ReadFromSD(String imageUrl,int type)
	     {
	    	 /** 
              * 加上一个对本地缓存的查找 
              */  
	    	 String sdCard=ScreenShoot.getSDDir(null);
	    	 
	    	 if(sdCard==null) return null;
             String bitmapName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);  
               
         //    Log.d("lichao","AsyncImageLoader bitmapName is "+bitmapName);
             String dir;
             if(type==LOADIMG_TYPE_TOUXIANG)
            	 dir=sdCard+"/weizhangquery/pic/touxiang/";
             else
            	 dir=sdCard+"/weizhangquery/pic/weibo_pic/";
             return BitmapFactory.decodeFile(dir + bitmapName);  
	         
         
	    	 
	     }
	     
		private void SaveToSD(String imageUrl,Bitmap bitmap,int type)
	    {
			
			String sdCard=ScreenShoot.getSDDir(null);
	    	 
	    	 if(sdCard==null) return;
             String bitmapName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1); 
             
             String dir;
             if(type==LOADIMG_TYPE_TOUXIANG)
            	 dir=sdCard+"/weizhangquery/pic/touxiang";
             else
            	 dir=sdCard+"/weizhangquery/pic/weibo_pic";
             ScreenShoot.savePic(bitmap, dir,dir+"/" + bitmapName, 100);
	    }


}
