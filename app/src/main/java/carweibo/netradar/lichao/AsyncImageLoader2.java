package carweibo.netradar.lichao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class AsyncImageLoader2 {

	
	 private HashMap<String, SoftReference<Bitmap>> imageCache;
	 private HashMap<String,String> threadCache;


	
	  long sum=0;
	     public AsyncImageLoader2() {
	    	 imageCache = new HashMap<String, SoftReference<Bitmap>>();
	    	 threadCache =new HashMap<String,String>();
	    	 
	    	
	     }
	  
	     public Bitmap loadDrawable(final String imageUrl,final String id, final ImageCallback2 imageCallback,final TextCallback2 textCallback) {
	    	 
	    	 if (imageCache.containsKey(imageUrl)) {
	        	 
	             SoftReference<Bitmap> softReference = imageCache.get(imageUrl);
	             Bitmap drawable = softReference.get();
	             if (drawable != null) {
	            
	            	return drawable;
	             }
	             else
	             {
	            	 Bitmap b=ReadFromSD(imageUrl);
		             if(b!=null)
		            	 return b;
	             }
	         }
	    	 else
	    	 {
	    		 
	             Bitmap b=ReadFromSD(imageUrl);
	             if(b!=null)
	            	 return b;

	    	 }
	    	 
	    	// Log.d("lichao","id imageurl is "+id+imageUrl);
	    	 if(threadCache.containsKey(id+imageUrl))
	    	 {
	    		
	    			 return null;

	    		 
	    	 }
	         final Handler handler = new Handler() {
	        	 
	             public void handleMessage(Message message) 
	             {
	            	 if(message.what==0)
	            	 {	
	            		 imageCallback.imageLoaded((Bitmap) message.obj, imageUrl,id);
	            		 threadCache.remove(id+imageUrl);
	            	//	 Log.d("lichao","thread out");
	            	 }
	            	 if(message.what==1)
	            	 {
	            		 textCallback.textLoaded(String.valueOf(message.obj), id);
	            	 }
	            	 
	            	
	             }
	         };
	     
	        
	        
	         new Thread() {
	             @Override
	             public void run() {
						
						 threadCache.put(id+imageUrl,null);
						 
						 InputStream i = null;
							long total=0;
							try {
								
								HttpPost post=new HttpPost(imageUrl);
								DefaultHttpClient client=new DefaultHttpClient();
								client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
								client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 15000);
								
								HttpResponse response=client.execute(post);
								if(response.getStatusLine().getStatusCode()==200)
								{  
									HttpEntity entity=response.getEntity();  
									i=(InputStream)entity.getContent();
							        total=entity.getContentLength();
							 	}  
								
							} catch (MalformedURLException e1) {
								
							} catch (IOException e) {
								
							}

		         		 ByteArrayOutputStream baos =new ByteArrayOutputStream();
		             	// byte[] b =new byte[4096];
		             	byte[] b =new byte[1024];
		             	 int len =0;
		             	 long sum=0;
		             	 byte[] bytes = null;
		             	 int progress;
		             	 
		             	 if(i==null)//||total>500000)
		             	 {
		             		 Message message = handler.obtainMessage(0, null);
			                 
			                 handler.sendMessage(message);
			                 return;
		             	 }
		             	
		             	 
		             	try { 
			             	 while ((len = i.read(b, 0,1024)) !=-1)
			             	 {
			         	    	 baos.write(b,0,len);
			         	    	 
			         	    	 baos.flush();
			         	    	 sum+=len;
			         	    	 progress=(int) ((float)sum/total*100);
			         	    	 
			         	    	// Log.d("lichao","pro is "+progress);//(int)((float)(float)sum/(float)total)*100);
			         	    	 Message message = handler.obtainMessage(1, progress);
				                 
				                 handler.sendMessage(message);
			         	    	 
			             	 }
			             	 	bytes = baos.toByteArray();
			             	 
								baos.close();
						} catch (IOException e) {
							
						}
		             	 if(bytes==null)
		             	 {
		             		 Message message = handler.obtainMessage(0, null);
			                 
			                 handler.sendMessage(message);
			                 return;
		             	 }
		             	 InputStream i2=new ByteArrayInputStream(bytes);
		             			 
		             	Bitmap bm=BitmapFactory.decodeStream(i2, null, null);
		         	
		         		/*if(bm.getWidth()>2000||bm.getHeight()>2000)
		         			bm=null;*/

		         		try {
							i2.close();
						} catch (IOException e) {
							
						}
					
		         	
		         		
						
	                 Message message = handler.obtainMessage(0, bm);
	                 
	                 handler.sendMessage(message);
	                 if(bm!=null)
	                 {
	
	                	 SaveToSD(imageUrl,bm);
	                	
	                	 imageCache.put(imageUrl, new SoftReference<Bitmap>(bm));
	                 }
	                
	                 
	                 
	             }

			

	         }.start();
	         
	         return null;
	     }
	 
	     public interface ImageCallback2 {
	         public void imageLoaded(Bitmap imageDrawable, String imageUrl,String id);
	     }
	     public interface TextCallback2 {
	         public void textLoaded(String text, String id);
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
	     private Bitmap ReadFromSD(String imageUrl)
	     {
	    	 /** 
              * 加上一个对本地缓存的查找 
              */  
	    	 String sdCard=ScreenShoot.getSDDir(null);
	    	 
	    	 if(sdCard==null) return null;
             String bitmapName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);  
               
             
             return BitmapFactory.decodeFile(sdCard+"/weizhangquery/pic/weibo_pic/" + bitmapName);  
	         
         
	    	 
	     }
	     
		private void SaveToSD(String imageUrl,Bitmap bitmap)
	    {
			
			String sdCard=ScreenShoot.getSDDir(null);
	    	 
	    	 if(sdCard==null) return;
             String bitmapName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1); 
             
             ScreenShoot.savePic(bitmap, sdCard+"/weizhangquery/pic/weibo_pic", sdCard+"/weizhangquery/pic/weibo_pic/" + bitmapName, 100);
	    }

	/*     private Bitmap ReadFromSD(String imageUrl)
	     {
	    	 *//** 
              * 加上一个对本地缓存的查找 
              *//*  
	    	 if(sdCard==null) return null;
             String bitmapName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);  
             File cacheDir = new File(sdCard.getPath()+"/carweibo/weibo_pic/");  
             if(cacheDir.exists()) 
             {
	             File[] cacheFiles = cacheDir.listFiles();  
	             int i = 0;  
	             for(; i<cacheFiles.length; i++)  
	             {  
	                 if(bitmapName.equals(cacheFiles[i].getName()))  
	                 {  
	                     break;  
	                 }  
	             }  
	               
	             if(i < cacheFiles.length)  
	             {  
	                 return BitmapFactory.decodeFile(sdCard.getPath()+"/carweibo/weibo_pic/" + bitmapName);  
	             }  
             }
             return null;
	    	 
	     }*/
	     
	/*	private void SaveToSD(String imageUrl,Bitmap bitmap)
	     {
			Log.d("lichao",imageUrl);
			if(sdCard==null||isSdCardFull==true) return;
	    	 File dir = new File(sdCard.getPath()+"/carweibo/weibo_pic");  
             if(!dir.exists())  
             {  
            	 
                 dir.mkdirs();
                	
             }  
               
             File bitmapFile = new File(sdCard.getPath()+"/carweibo/weibo_pic/" +   
                     imageUrl.substring(imageUrl.lastIndexOf("/") + 1));  
             if(!bitmapFile.exists())  
             {  
                 try  
                 {  
                     bitmapFile.createNewFile();  
                 }  
                 catch (IOException e)  
                 {  
                     // TODO Auto-generated catch block   
                     e.printStackTrace();  
                 }  
             }  
             FileOutputStream fos;  
             try  
             {  
                 fos = new FileOutputStream(bitmapFile);  
                 bitmap.compress(Bitmap.CompressFormat.JPEG,   
                         100, fos);  
                 fos.close();  
             }  
             catch (FileNotFoundException e)  
             {  
                 // TODO Auto-generated catch block   
                 e.printStackTrace();  
             }  
             catch (IOException e)  
             {  
                 // TODO Auto-generated catch block   
                 e.printStackTrace();  
             }  

	    	 
	     }*/


}
