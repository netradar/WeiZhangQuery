package carweibo.netradar.lichao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import carweibo.netradar.lichao.AsyncImageLoader.ImageCallback;
import carweibo.netradar.lichao.MessageInfo.MSG_STATUS;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MessageAdapter extends ArrayAdapter<MessageInfo> implements ImageCallback {

	private static int LOADIMG_TYPE_TOUXIANG=0;
	View localView;
	Context con;
	int size;
	AsyncImageLoader asyncimageloader;
	Bitmap to_bmp=null,from_bmp=null;
	public MessageAdapter(Context context, List<MessageInfo> data,
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
		MessageViewCache viewCache;
		
		if(rowView==null)
		{
			
			LayoutInflater inflater = ((Activity) con).getLayoutInflater();
            rowView = inflater.inflate(R.layout.message_item_layout, null);
            viewCache = new MessageViewCache(rowView);
            rowView.setTag(viewCache);
		}
		else
		{
			viewCache=(MessageViewCache) rowView.getTag();
			/*if(viewCache==null)
			{
				LayoutInflater inflater = ((Activity) con).getLayoutInflater();
	            rowView = inflater.inflate(R.layout.message_item_layout, null);
	            viewCache = new MessageViewCache(rowView);
	            rowView.setTag(viewCache);
			}*/
		}
		
		MessageInfo item=(MessageInfo)getItem(position);
		
		viewCache.getFrom_sending().setVisibility(View.GONE);
		viewCache.getTo_sending().setVisibility(View.GONE);
		viewCache.getFrom_failure().setVisibility(View.GONE);
		viewCache.getTo_failure().setVisibility(View.GONE);
		
		if(item.isSend)
		{
			viewCache.getFrom_touxiang_url().setVisibility(View.GONE);
			viewCache.getFrom_layout().setVisibility(View.GONE);
			
			viewCache.getTo_touxiang_url().setVisibility(View.VISIBLE);
			viewCache.getTo_layout().setVisibility(View.VISIBLE);
			if(item.getMsg_status()==MSG_STATUS.SENDING)
			{
				viewCache.getTo_sending().setVisibility(View.VISIBLE);
				viewCache.getTo_failure().setVisibility(View.GONE);
			}
			if(item.getMsg_status()==MSG_STATUS.ERROR)
			{
				viewCache.getTo_sending().setVisibility(View.GONE);
				viewCache.getTo_failure().setVisibility(View.VISIBLE);
			}
			if(item.getMsg_status()==MSG_STATUS.OK)
			{
				viewCache.getTo_sending().setVisibility(View.GONE);
				viewCache.getTo_failure().setVisibility(View.GONE);
			}
			if(to_bmp!=null)
				viewCache.getTo_touxiang_url().setImageBitmap(to_bmp);
			else
				viewCache.getTo_touxiang_url().setImageResource(R.drawable.switchuser);
			viewCache.getTo_msg().setText(item.getMsg());
			viewCache.getTo_time().setText(item.getTime());
			
			
		}
		else
		{
			viewCache.getFrom_touxiang_url().setVisibility(View.VISIBLE);
			viewCache.getFrom_layout().setVisibility(View.VISIBLE);
			
			viewCache.getTo_touxiang_url().setVisibility(View.GONE);
			viewCache.getTo_layout().setVisibility(View.GONE);
			
			viewCache.getView_detail().setTag(position);
			if(item.getMsg_type()==0)
				viewCache.getView_detail().setVisibility(View.GONE);
			else
				viewCache.getView_detail().setVisibility(View.VISIBLE);
			if(from_bmp!=null)
				viewCache.getFrom_touxiang_url().setImageBitmap(from_bmp);
			else
			{
				viewCache.getFrom_touxiang_url().setImageResource(R.drawable.switchuser);
				viewCache.getFrom_touxiang_url().setTag(position+item.getFrom_touxiang());
				from_bmp=asyncimageloader.loadDrawable(item.getFrom_touxiang(), String.valueOf(position),this, LOADIMG_TYPE_TOUXIANG);
				if(from_bmp!=null)
					viewCache.getFrom_touxiang_url().setImageBitmap(from_bmp);
				else
					viewCache.getFrom_touxiang_url().setImageResource(R.drawable.switchuser);
				
			}
			
			viewCache.getFrom_msg().setText(item.getMsg());
			viewCache.getFrom_time().setText(item.getTime());
			switch(item.getMsg_type())
			{
			case 0:
				viewCache.getType_tip().setVisibility(View.GONE);
				break;
			case 1:
				viewCache.getType_tip().setVisibility(View.VISIBLE);
				viewCache.getType_tip().setText("我在帖子《"+item.getRef_weibo()+"...》给你有新的评论:");
				break;
			default:
				viewCache.getType_tip().setVisibility(View.GONE);	
/*			case 2:
				viewCache.getType_tip().setVisibility(View.GONE);
				viewCache.getFrom_msg().setText("我“赞”了你的帖子《"+item.getRef_weibo()+"》");
				break;
			case 3:
				viewCache.getType_tip().setVisibility(View.GONE);
				viewCache.getFrom_msg().setText("我转发了你的帖子《"+item.getRef_weibo()+"》到新浪微博");
				break;
			case 4:
				viewCache.getType_tip().setVisibility(View.GONE);
				viewCache.getFrom_msg().setText("我转发了你的帖子《"+item.getRef_weibo()+"》到腾讯微博");
				break;
			case 5:
				viewCache.getType_tip().setVisibility(View.GONE);
				viewCache.getFrom_msg().setText("我转发了你的帖子《"+item.getRef_weibo()+"》到微信朋友圈");
				
				break;*/
			}
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
			{
				from_bmp=imageDrawable;
				iv.setImageBitmap(imageDrawable);
			}
			else
				iv.setImageResource(R.drawable.switchuser);
		}
	}

	public void setTouxiang(Bitmap sender_bmp,Bitmap from_bmp) {
		to_bmp=sender_bmp;
		this.from_bmp=from_bmp;
	}

	
}
