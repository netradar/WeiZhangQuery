package carweibo.netradar.lichao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import carweibo.netradar.lichao.AsyncImageLoader.ImageCallback;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

public class GoodAdapter extends SimpleAdapter implements ImageCallback {

	private static int LOADIMG_TYPE_TOUXIANG=0;
	View localView;
	Context con;
	int size;
	AsyncImageLoader asyncimageloader;
	public GoodAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to,float denisty,View v) {
		super(context, data, resource, from, to);
		
		con=context;
		size=data.size();
		localView=v;
		asyncimageloader=new AsyncImageLoader(denisty);
		
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView=convertView;
		CommentViewCache viewCache;
		if(rowView==null)
		{
			LayoutInflater inflater = ((Activity) con).getLayoutInflater();
            rowView = inflater.inflate(R.layout.comment_item_layout, null);
            viewCache = new CommentViewCache(rowView);
            rowView.setTag(viewCache);
		}
		else
			viewCache=(CommentViewCache) rowView.getTag();
		@SuppressWarnings("unchecked")
		Map<String,String> item=(HashMap<String,String>)getItem(position);
		viewCache.getNickname().setText(item.get("nickname"));
		viewCache.getContent().setText(item.get("content"));
		viewCache.getTime().setText(item.get("time"));
		
		viewCache.getTouxiang_url().setTag(position+item.get("touxiang"));
		
		Bitmap bmp=asyncimageloader.loadDrawable(item.get("touxiang"), new String().valueOf(position), this, LOADIMG_TYPE_TOUXIANG);
		
		if(bmp!=null)
		{
			viewCache.getTouxiang_url().setImageBitmap(bmp);
		}
		else
		{
			viewCache.getTouxiang_url().setImageResource(R.drawable.switchuser);
		}
		
		//Log.d("lichao","row view height is "+rowView.getHeight());
		return rowView;
	}
	@Override
	public void imageLoaded(Bitmap imageDrawable, String imageUrl, String id) {
		
		ImageView iv=(ImageView) localView.findViewWithTag(id+imageUrl);
		if(iv!=null)
		{
			if(imageDrawable!=null)
				iv.setImageBitmap(imageDrawable);
			else
				iv.setImageResource(R.drawable.switchuser);
		}
	}

	
}
