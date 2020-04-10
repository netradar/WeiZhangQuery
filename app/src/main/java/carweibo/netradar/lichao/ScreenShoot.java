package carweibo.netradar.lichao;

import java.io.File;
import java.io.FileNotFoundException;  
import java.io.FileOutputStream;  
import java.io.IOException;  
  
import android.app.Activity;  
import android.content.Context;
import android.graphics.Bitmap;  
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;  
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;  
import android.view.View; 
import android.widget.ListView;
import android.widget.Toast;

public class ScreenShoot {
	 public static Bitmap takeScreenShot(Activity activity) {  
	        // View是你需要截图的View   
	        View view = activity.getWindow().getDecorView();  
	        view.setDrawingCacheEnabled(true);  
	        view.buildDrawingCache();  
	        Bitmap b1 = view.getDrawingCache();  
	  
	        // 获取状态栏高度   
	        Rect frame = new Rect();  
	        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);  
	        int statusBarHeight = frame.top;  
	        Log.i("TAG", "" + statusBarHeight);  
	  
	        // 获取屏幕长和高   
	        int width = activity.getWindowManager().getDefaultDisplay().getWidth();  
	        int height = activity.getWindowManager().getDefaultDisplay()  
	                .getHeight();  
	        // 去掉标题栏   
	        // Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);   
	        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height  
	                - statusBarHeight);  
	        view.destroyDrawingCache();  
	        return b;  
	    }  
	  
	    // 保存到sdcard   
	    public static  void savePic(Bitmap b, String path,String strFileName,int rate) {  
	    	
	    	 File dir = new File(path);  
	    	/// Log.d("lichao",strFileName);
             if(!dir.exists())  
             {  
            	 
                 dir.mkdirs();
                	
             }  
               
             File bitmapFile = new File(strFileName);  
             if(!bitmapFile.exists())  
             {  
                 try  
                 {  
                     bitmapFile.createNewFile();  
                 }  
                 catch (IOException e)  
                 {  
                     // TODO Auto-generated catch block   
                     Log.d("lichao",e.toString());
                 }  
             }  
	        FileOutputStream fos = null;  
	        try {  
	            fos = new FileOutputStream(bitmapFile);  
	            if (null != fos) {  
	                if(b.compress(Bitmap.CompressFormat.JPEG, rate, fos))
	                {
	                	//Log.d("lichao","success");  
	                }
	                else 
	                	Log.d("lichao","failed");
	                fos.flush();  
	                fos.close();  
	            }  
	            else
	            	Log.d("lichao","fos is null");
	        } catch (FileNotFoundException e) {  
	        	Log.d("lichao","fos is null2");
	            e.printStackTrace();  
	        } catch (IOException e) {  
	        	Log.d("lichao","fos is null3");
	            e.printStackTrace();  
	        }  
	    }  
	  
	    public static Bitmap getbBitmap(ListView listView) {

	        int h = 0;

	        Bitmap bitmap = null;

	        // 获取listView实际高度

	        for (int i = 0; i < 8; i++) {

	            h += listView.getChildAt(0).getHeight();

	        }

	       Log.d("lichao","i is "+listView.getChildCount());

	        // 创建对应大小的bitmap

	        bitmap = Bitmap.createBitmap(listView.getWidth(), h,

	                Bitmap.Config.ARGB_8888);

	        final Canvas canvas = new Canvas(bitmap);

	        listView.draw(canvas);

	        // 测试输出


	        return bitmap;

	    }

	    public static String getSDDir(Context context)
		{
			 if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
	    	 {  
	    		 File sdCard=Environment.getExternalStorageDirectory();
	    		 
	    		
	    		 StatFs statfs = new StatFs(sdCard.getPath()); 
	    		 long totalBlocks = statfs.getAvailableBlocks();
	    		 long blocSize = statfs.getBlockSize();
	    		 if((totalBlocks*blocSize)>2000000)
	    			 return sdCard.getPath();
	    		 else
	    		 {
	    			 
	    			// Toast.makeText(context, "SD卡空间不足，无法完成指定操作", Toast.LENGTH_SHORT).show();
	    			 return null;
	    		 }
	    				 
	    	 }
			 else
			 {
		/*		 	File path = Environment.getExternalStorageDirectory();  //获取数据目录   
				 	Log.d("lichao","ScreenShoot path is "+path.getPath());
			        StatFs stat = new StatFs(path.getPath());  
			        long blockSize = stat.getBlockSize();  
			        long availableBlocks = stat.getAvailableBlocks();  
			        if((availableBlocks*blockSize)>2000000)
			        	return path.getPath();
		    		 else
		    		 {
		    			 return null;
		    		 }*/
				 
				 return null;

			 }
			//Toast.makeText(context, "SD卡空间不足，无法完成指定操作", Toast.LENGTH_SHORT).show();
			
			
		}
	    
		static public  String getUploadPic(Context context,View v)
		{

			String SDDir=getSDDir(context);
			
			if(SDDir==null)
			{
				
				return null;
			}
	       
			 Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(),  
		                Bitmap.Config.ARGB_8888);  
			 
			 Canvas canvas = new Canvas(b);
			 canvas.drawColor(Color.parseColor("#efefef"));
			 v.draw(canvas);
			 ScreenShoot.savePic(b,SDDir+"/weizhangquery/cache",SDDir+"/weizhangquery/cache/"+"vender.png",100);
				
			return SDDir+"/weizhangquery/cache/"+"vender.png";
			
		}


}
