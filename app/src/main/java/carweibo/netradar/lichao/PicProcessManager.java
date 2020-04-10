package carweibo.netradar.lichao;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;


public class PicProcessManager {
	
	private static int MAX_THUMB_PIC_WIDTH=200;
	private static int MAX_THUMB_PIC_HEIGHT=200;
	private static int MAX_UPLOAD_PIC_WIDTH=720;
	private static int MAX_UPLOAD_PIC_HEIGHT=1080;
	private static class PicInfo{
		float rate;
		int dstWidth;
		int dstHeight;
	}
	
	static public UploadThumbInfo adjustAndSave(Context context,String fileName)
	{
	BitmapFactory.Options  option=new BitmapFactory.Options();
	BitmapFactory.Options  option1=new BitmapFactory.Options();
	option.inJustDecodeBounds=true;
	Bitmap bm=BitmapFactory.decodeFile(fileName, option);
	
	
	PicInfo pic=getPicInfo(option,0);
	PicInfo pic_s=getPicInfo(option,1);
		
	if(pic!=null)//(option.outHeight>MAX_UPLOAD_PIC_HEIGHT)||(option.outWidth>MAX_UPLOAD_PIC_WIDTH))
	{	
		
		option1.inSampleSize=(int) pic.rate;
		option1.inJustDecodeBounds=false;
		bm=BitmapFactory.decodeFile(fileName, option1);
		
		Bitmap bm_upload=Bitmap.createScaledBitmap(bm, pic.dstWidth, pic.dstHeight, true);
		Bitmap bm_thumb=null;
		if(pic_s!=null)
			bm_thumb=Bitmap.createScaledBitmap(bm, pic_s.dstWidth, pic_s.dstHeight, true);
		
		if(ScreenShoot.getSDDir(context.getApplicationContext())==null)
		{
			Toast.makeText(context, "SD卡空间不足，无法完成指定操作", Toast.LENGTH_SHORT).show();
			return null;
		}
		
		String url=ScreenShoot.getSDDir(context.getApplicationContext())+"/weizhangquery/pic/"+System.currentTimeMillis()+".png";
		String url_s=url;
	
		ScreenShoot.savePic(bm_upload, ScreenShoot.getSDDir(context.getApplicationContext())+"/weizhangquery/pic/",url, 70);
		
		if(bm_thumb!=null)
		{
			url_s=url.substring(0,url.length()-4)+"_s.png";
			ScreenShoot.savePic(bm_thumb, ScreenShoot.getSDDir(context.getApplicationContext())+"/weizhangquery/pic/",url_s, 70);
			
		}
		UploadThumbInfo info=new UploadThumbInfo();
		if(pic_s==null)
		{
			pic_s=pic;
	
		}
		
		info.thumb_height=pic_s.dstHeight;
		info.thumb_width=pic_s.dstWidth;
		info.thumb_url=url_s;
		info.upload_url=url;
		
		
		return info;
		
		
		
			
		//return  Bitmap.createScaledBitmap(bm, dstWidth+1, dstHeight+1, true);
		//return  Bitmap.createBitmap(source, x, y, width, height)
		
	}
	else if(pic_s!=null)
	{
		option1.inSampleSize=(int) pic_s.rate;
		option1.inJustDecodeBounds=false;
		bm=BitmapFactory.decodeFile(fileName, option1);
		
		Bitmap bm_thumb=Bitmap.createScaledBitmap(bm, pic_s.dstWidth, pic_s.dstHeight, true);
			
		if(ScreenShoot.getSDDir(context.getApplicationContext())==null) return null;
		
		String url_s=ScreenShoot.getSDDir(context.getApplicationContext())+"/weizhangquery/pic/"+System.currentTimeMillis()+"_s.png";
		ScreenShoot.savePic(bm_thumb, ScreenShoot.getSDDir(context.getApplicationContext())+"/weizhangquery/pic/",url_s, 70);
		
		UploadThumbInfo info=new UploadThumbInfo();
				
		info.thumb_height=pic_s.dstHeight;
		info.thumb_width=pic_s.dstWidth;
		info.thumb_url=url_s;
		info.upload_url=fileName;
		
		return info;
	}
	else
	{
		UploadThumbInfo info=new UploadThumbInfo();
		
		info.thumb_height=option.outHeight;
		info.thumb_width=option.outWidth;
		info.thumb_url=fileName;
		info.upload_url=fileName;
		
		return info;
	}
	
	

	
}

	public static PicInfo getPicInfo(Options option, int i)
	{
		int dstWidth=option.outWidth;
		int dstHeight=option.outHeight;
		float rate=1;
		int max_width;
		int max_height;
		
		PicInfo ret_pic=new PicInfo();
		
		if(i==0)
		{
			max_width=MAX_UPLOAD_PIC_WIDTH;
			max_height=MAX_UPLOAD_PIC_HEIGHT;
		}
		else if(i==1)
		{
			max_width=MAX_THUMB_PIC_WIDTH;
			max_height=MAX_THUMB_PIC_HEIGHT;
		}
		else 
			return null;
		
		if((option.outHeight>max_height)&&(option.outWidth>max_width))
		{
			
		
			if((option.outHeight>=option.outWidth))
			{
				rate=((float)(option.outWidth)/(float)max_width);
				dstWidth=max_width;
				dstHeight=(int) (((float)(option.outHeight))/rate);
				
				if(dstHeight>4*max_height)
				{
					rate=rate*2;
					dstWidth=max_width/2;
					dstHeight=(int) (dstHeight/2);
				}
			}
			else
			{
				rate=((float)(option.outHeight)/(float)max_height);
				
				dstHeight=max_height;
				dstWidth=(int) (((float)(option.outWidth))/rate);
				
				if(dstWidth>4*max_width)
				{
					rate=rate*2;
					dstHeight=max_height/2;
					dstWidth=(int) (dstWidth/2);
				}
				
			}
			
			ret_pic.dstWidth=dstWidth;
			ret_pic.dstHeight=dstHeight;
			ret_pic.rate=rate;
			
			return ret_pic;
		}
		else
		{
			if((option.outHeight>=option.outWidth))
			{
				if(dstHeight>4*max_height)
				{
					rate=rate*2;
					dstWidth=option.outWidth/2;
					dstHeight=(int) (option.outHeight/rate);
					
					ret_pic.dstWidth=dstWidth;
					ret_pic.dstHeight=dstHeight;
					ret_pic.rate=rate;
					
					return ret_pic;
				}
			}
			else
			{
				if(dstWidth>4*max_width)
				{
					rate=rate*2;
					dstHeight=option.outHeight/2;
					dstWidth=(int) (option.outWidth/rate);
					
					ret_pic.dstWidth=dstWidth;
					ret_pic.dstHeight=dstHeight;
					ret_pic.rate=rate;
					
					return ret_pic;
				}
				
			}
			
			
		}
		return null;
	}
	
	public static Bitmap getSmallPic(Context context,String fileName)
	{
		float density=context.getResources().getDisplayMetrics().density;
		BitmapFactory.Options  option=new BitmapFactory.Options();
		BitmapFactory.Options  option1=new BitmapFactory.Options();
		option.inJustDecodeBounds=true;
		Bitmap bm=BitmapFactory.decodeFile(fileName, option);
		Bitmap ret_bmp;
		int rate=1;

		
		if((option.outHeight>70*density)&&(option.outWidth>70*density))
		{
			if((option.outHeight>=option.outWidth))
			{
				rate=(int) (option.outWidth/(70*density));
			}
			else
				rate=(int) (option.outHeight/(70*density));
			
			
			
			option1.inSampleSize=rate;
			option1.inJustDecodeBounds=false;
			bm=BitmapFactory.decodeFile(fileName, option1);
			
			if(bm==null) return null;
			
			int dstWidth=bm.getWidth();
			int dstHeight=bm.getHeight();
			int x = 0,y = 0;
			if(dstHeight>=dstWidth)
			{
				x=0;
				y=(int) (dstHeight/2-dstWidth/2);
				ret_bmp= Bitmap.createBitmap(bm, x, y, dstWidth, dstWidth);
			}
			else
			{
				x=(int) (dstWidth/2-dstHeight/2);
				y=0;
				ret_bmp= Bitmap.createBitmap(bm, x, y, dstHeight, dstHeight);
			}
			
			
			
			
			
			
				
			//return  Bitmap.createScaledBitmap(bm, dstWidth+1, dstHeight+1, true);
			//return  Bitmap.createBitmap(source, x, y, width, height)
			
		}
		else
		{
			int dstWidth;
			int dstHeight;
			int x = 0,y = 0;
			if((option.outHeight>=option.outWidth))
			{
				dstWidth=(int) (70*density);
				dstHeight=(int) (((float) ((70*density)/option.outWidth))*option.outHeight);
				x=0;
				y=dstHeight/2-dstWidth/2;
			}
			else
			{
				dstHeight=(int) (70*density);
				dstWidth=(int) (((float) ((70*density)/option.outHeight))*option.outWidth);
				y=0;
				x=dstWidth/2-dstHeight/2;
			}
			
			
			
			option1.inSampleSize=1;
			option1.inJustDecodeBounds=false;
			bm=BitmapFactory.decodeFile(fileName, option1);
			
			if(bm==null) return null;	
			ret_bmp= Bitmap.createScaledBitmap(bm,dstWidth, dstHeight,true);
			ret_bmp=Bitmap.createBitmap(ret_bmp,x,y,(int)(70*density),(int)(70*density));
		}
		/*if(ScreenShoot.getSDDir(this)!=null)
		{
			String save_dir=fileName.substring(0, fileName.length()-4)+"_s.png";
			ScreenShoot.savePic(ret_bmp, ScreenShoot.getSDDir(this)+"/weizhangquery/pic/", save_dir, 70);
		}
		else
			return null;*/
		
		return ret_bmp;
		
	}
	
}
