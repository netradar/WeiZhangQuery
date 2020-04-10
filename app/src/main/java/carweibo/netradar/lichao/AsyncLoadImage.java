package carweibo.netradar.lichao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;


public class AsyncLoadImage {

	OnRefreshProgress refreshProgress;
	public Bitmap LoadImage(String url) throws IOException
	{
		InputStream i = null;
		long total=0;
		try {
			/*m = new URL(url);
			i = (InputStream) m.getContent();
			*/
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
	    	 refreshProgress.refresh(sum,total);
	    	 
    	 }
    	 byte[] bytes = baos.toByteArray();
    	 baos.close();
    	 
    	 InputStream i2=new ByteArrayInputStream(bytes);
    			 
		
		
		Bitmap bm=BitmapFactory.decodeStream(i2, null, null);
	
		i2.close();
		return bm;
		
	/*	Log.d("lichao","before createstream");
		Drawable d = Drawable.creat
		Log.d("lichao","after createstream");
								return d;*/
					
	}
	public void setOnRefreshProgress(OnRefreshProgress callBack)
	{
		refreshProgress=callBack;
	}

	public interface OnRefreshProgress {

		public void refresh(long progress,long len);
	}
	

}
