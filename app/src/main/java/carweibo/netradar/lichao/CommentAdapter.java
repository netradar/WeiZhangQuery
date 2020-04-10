package carweibo.netradar.lichao;

import java.util.List;
import carweibo.netradar.lichao.AsyncImageLoader.ImageCallback;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentAdapter extends ArrayAdapter<CommentInfo> implements ImageCallback {

	private static int LOADIMG_TYPE_TOUXIANG=0;
	View localView;
	Context con;
	int size;
	AsyncImageLoader asyncimageloader;
	public CommentAdapter(Context context, List<CommentInfo> data,
			float denisty,View v) {
		super(context, 0, data);
		
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
		{
			viewCache=(CommentViewCache) rowView.getTag();
			if(viewCache==null)
			{
				LayoutInflater inflater = ((Activity) con).getLayoutInflater();
	            rowView = inflater.inflate(R.layout.comment_item_layout, null);
	            viewCache = new CommentViewCache(rowView);
	            rowView.setTag(viewCache);
			}
		}
		
		CommentInfo item=(CommentInfo)getItem(position);
		viewCache.getComment_btn().setTag(position);
		viewCache.getNickname().setText(item.getNickname());
		
		viewCache.getContent().setText(item.getContent());
		
		viewCache.getTime().setText(item.getTime());
		
		viewCache.getTouxiang_url().setTag(position+item.getTouxiang());
		
		if(item.getTouxiang().startsWith("http"))
		{
			Bitmap bmp=asyncimageloader.loadDrawable(item.getTouxiang(), String.valueOf(position), this, LOADIMG_TYPE_TOUXIANG);
			
			if(bmp!=null)
			{
				viewCache.getTouxiang_url().setImageBitmap(bmp);
			}
			else
			{
				viewCache.getTouxiang_url().setImageResource(R.drawable.switchuser);
			}
		}
		else
		{
			Bitmap bmp=getTouxiangFromSD(item.getTouxiang());
			
			if(bmp!=null)
			{
				viewCache.getTouxiang_url().setImageBitmap(bmp);
			}
			else
				viewCache.getTouxiang_url().setImageResource(R.drawable.switchuser);
			
		}
		
		if(item.getUser_score()>5000)
		{
			viewCache.getIs_vip().setVisibility(View.VISIBLE);
		}
		else
			viewCache.getIs_vip().setVisibility(View.GONE);
		
		if(UserManager.getCurUser(con.getApplicationContext()).equals(item.nickname)||item.isVender)
		{
			viewCache.getComment_btn().setVisibility(View.GONE);
		}
		else
		{
			viewCache.getComment_btn().setVisibility(View.VISIBLE);
		}
		//Log.d("lichao","row view height is "+rowView.getHeight());
		return rowView;
	}
	
	public View getView1(int position) {
		
		View rowView;
		LayoutInflater inflater = ((Activity) con).getLayoutInflater();
        rowView = inflater.inflate(R.layout.comment_item_layout, null);
            
	
		CommentInfo item=(CommentInfo)getItem(position);
		((TextView) rowView.findViewById(R.id.comment_item_nickname)).setText(item.getNickname());
		((TextView) rowView.findViewById(R.id.comment_item_content)).setText(item.getTouxiang());
		((TextView) rowView.findViewById(R.id.comment_item_time)).setText(item.getTime());
		
	//	viewCache.getTouxiang_url().setTag(position+item.get("touxiang"));
		
	//	Bitmap bmp=asyncimageloader.loadDrawable(item.get("touxiang"), new String().valueOf(position), this, LOADIMG_TYPE_TOUXIANG);
		
		
		((ImageView) rowView.findViewById(R.id.comment_item_touxiang)).setImageResource(R.drawable.switchuser);
		
		
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

	private Bitmap getTouxiangFromSD(String string) {
		
		
		return BitmapFactory.decodeFile(ScreenShoot.getSDDir(con)+"/weizhangquery/pic/touxiang/"+string);
	}
}
