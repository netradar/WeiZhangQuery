package carweibo.netradar.lichao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import carweibo.netradar.lichao.AsyncImageLoader.ImageCallback;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MyMessageAdapter extends SimpleAdapter implements ImageCallback {

	private static int LOADIMG_TYPE_TOUXIANG=0;
	Context con;
	HashMap<String,String> mapItem;
	AsyncImageLoader	asyncImageLoader;
	ListView listView;
	 float density;
	public MyMessageAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to,float denisty,ListView listview) {
		super(context, data, resource, from, to);
		this.density=denisty;
		con=context;
		listView=listview;
		 asyncImageLoader=new AsyncImageLoader(density);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View rowView=convertView;
		MyMessageViewCache viewCache;
		
		if(rowView==null)
		{
			
			LayoutInflater inflater = ((Activity) con).getLayoutInflater();
            rowView = inflater.inflate(R.layout.my_msg_item_layout, null);
            viewCache = new MyMessageViewCache(rowView);
            rowView.setTag(viewCache);
		}
		else
		{
			viewCache=(MyMessageViewCache) rowView.getTag();
			if(viewCache==null)
			{
				LayoutInflater inflater = ((Activity) con).getLayoutInflater();
	            rowView = inflater.inflate(R.layout.my_msg_item_layout, null);
	            viewCache = new MyMessageViewCache(rowView);
	            rowView.setTag(viewCache);
			}
		}
		mapItem= (HashMap<String, String>) getItem(position);
		
		viewCache.getNickname().setText(mapItem.get("nickname"));
		viewCache.getMsg().setText(mapItem.get("msg"));
		viewCache.getTime().setText(mapItem.get("time"));
		
		if(mapItem.get("unread_num").equals("0"))
			viewCache.getUnread().setVisibility(View.GONE);
		else
		{
			viewCache.getUnread().setVisibility(View.VISIBLE);
			viewCache.getUnread().setText(mapItem.get("unread_num"));
			
		}
		
		/*Bitmap bmp=getTouxiangFromSD(mapItem.get("touxiang_name"));
		if(bmp!=null)
			viewCache.getTouxiang_url().setImageBitmap(bmp);
		else
			viewCache.getTouxiang_url().setImageResource(R.drawable.switchuser);*/
		
	
			viewCache.getTouxiang_url().setTag(position+mapItem.get("touxiang_url"));
			
			Bitmap bmp=asyncImageLoader.loadDrawable(mapItem.get("touxiang_url"), String.valueOf(position), this, LOADIMG_TYPE_TOUXIANG);
		
			if(bmp!=null)
			{
				viewCache.getTouxiang_url().setImageBitmap(bmp);
			}
			else
			{
				viewCache.getTouxiang_url().setImageResource(R.drawable.switchuser);
			}
		
	
		
		return rowView;
	}



	@Override
	public void imageLoaded(Bitmap imageDrawable, String imageUrl, String id) {
		ImageView imageViewByTag = (ImageView) listView.findViewWithTag(id+imageUrl);
        if (imageViewByTag != null&&imageDrawable!=null) 
        {
        		
        	imageViewByTag.setImageBitmap(imageDrawable);
                         
        }
        if(imageDrawable!=null)
        {
        	String sdDir=ScreenShoot.getSDDir(con);
        	if(sdDir==null) return;
        	String fileName=imageUrl.substring(imageUrl.lastIndexOf("/")+1,imageUrl.length());
        	
        	ScreenShoot.savePic(imageDrawable,sdDir+"/weizhangquery/pic/touxiang", sdDir+"/weizhangquery/pic/touxiang/"+fileName , 100);
        }

		
	}
	

}
