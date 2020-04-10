package carweibo.netradar.lichao;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentViewCache {
	
	View baseView;
	
	ImageView touxiang_url;
	ImageView is_vip;
	ImageView comment_btn;
	
	TextView nickname;
	TextView content;
	TextView time;
	
	
	
	public View getBaseView() {
		return baseView;
	}

	
	public ImageView getComment_btn() {
		
		if(comment_btn==null)
		{
			comment_btn=(ImageView)baseView.findViewById(R.id.comment_item_comment);
		}
		return comment_btn;
	}


	public ImageView getTouxiang_url() {
		if(touxiang_url==null)
		{
			touxiang_url=(ImageView)baseView.findViewById(R.id.comment_item_touxiang);
		}
		return touxiang_url;
	}

	public ImageView getIs_vip() {
		if(is_vip==null)
		{
			is_vip=(ImageView)baseView.findViewById(R.id.comment_item_vip_sign);
		}
		return is_vip;
	}

	public TextView getNickname() {
		if(nickname==null)
		{
			nickname=(TextView)baseView.findViewById(R.id.comment_item_nickname);
		}
		return nickname;
	}

	public TextView getContent() {
		if(content==null)
		{
			content=(TextView)baseView.findViewById(R.id.comment_item_content);
		}
		return content;
	}

	public TextView getTime() {
		if(time==null)
		{
			time=(TextView)baseView.findViewById(R.id.comment_item_time);
		}
		return time;
	}

	public CommentViewCache(View baseView) {
		
		this.baseView = baseView;
	}
	
	
}
