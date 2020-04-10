package carweibo.netradar.lichao;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MyMessageViewCache {
	
	View baseView;
	
	ImageView touxiang_url;
	
	ImageView is_vip;
	
	TextView unread;
	TextView nickname;
	TextView msg;
	TextView time;
	
	
	
	public TextView getUnread() {
		if(unread==null)
		{
			unread=(TextView)baseView.findViewById(R.id.my_msg_item_unread_tip);
		}
		return unread;
	}


	public View getBaseView() {
		return baseView;
	}


	public ImageView getTouxiang_url() {
		if(touxiang_url==null)
		{
			touxiang_url=(ImageView)baseView.findViewById(R.id.my_msg_item_touxiang);
		}
		return touxiang_url;
	}

	public ImageView getIs_vip() {
		if(is_vip==null)
		{
			is_vip=(ImageView)baseView.findViewById(R.id.my_msg_item_vip_sign);
		}
		return is_vip;
	}

	public TextView getNickname() {
		if(nickname==null)
		{
			nickname=(TextView)baseView.findViewById(R.id.my_msg_item_nickname);
		}
		return nickname;
	}

	public TextView getMsg() {
		if(msg==null)
		{
			msg=(TextView)baseView.findViewById(R.id.my_msg_item_msg);
		}
		return msg;
	}

	public TextView getTime() {
		if(time==null)
		{
			time=(TextView)baseView.findViewById(R.id.my_msg_item_time);
		}
		return time;
	}

	public MyMessageViewCache(View baseView) {
		
		this.baseView = baseView;
	}
	
	
}
