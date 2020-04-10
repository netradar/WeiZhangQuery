package carweibo.netradar.lichao;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MessageViewCache {
	
	View baseView;
	
	ImageView from_touxiang_url,to_touxiang_url;

	
	
	LinearLayout to_layout;
	LinearLayout from_layout;

	TextView from_msg,to_msg;
	TextView from_time,to_time;

	TextView type_tip;
	
	ProgressBar from_sending,to_sending;
	ImageView from_failure,to_failure;
	
	Button view_detail;
	
	public View getBaseView() {
		return baseView;
	}

	
	public Button getView_detail() {
		if(view_detail==null)
		{
			view_detail=(Button)baseView.findViewById(R.id.message_item_view_detail);
		}
		return view_detail;
	}


	public TextView getType_tip() {
		if(type_tip==null)
		{
			type_tip=(TextView)baseView.findViewById(R.id.message_item_from_type_tip);
		}
		return type_tip;
	}


	public LinearLayout getFrom_layout() {
		
		if(from_layout==null)
		{
			from_layout=(LinearLayout)baseView.findViewById(R.id.message_item_from_layout);
		}
		return from_layout;
	}

	public LinearLayout getTo_layout() {
		
		if(to_layout==null)
		{
			to_layout=(LinearLayout)baseView.findViewById(R.id.message_item_to_layout);
		}
		return to_layout;
	}

	public MessageViewCache(View baseView) {
    	
        this.baseView = baseView;
    }

	public ImageView getFrom_touxiang_url() {
		if(from_touxiang_url==null)
		{
			from_touxiang_url=(ImageView)baseView.findViewById(R.id.messagefrom_item_touxiang);
		}
		return from_touxiang_url;
	}
	public ImageView getTo_touxiang_url() {
		if(to_touxiang_url==null)
		{
			to_touxiang_url=(ImageView)baseView.findViewById(R.id.messageto_item_touxiang);
		}
		return to_touxiang_url;
	}

	public TextView getFrom_msg() {
		if(from_msg==null)
		{
			from_msg=(TextView)baseView.findViewById(R.id.message_item_from_msg);
		}
		return from_msg;
	}
	public TextView getTo_msg() {
		if(to_msg==null)
		{
			to_msg=(TextView)baseView.findViewById(R.id.message_item_to_msg);
		}
		return to_msg;
	}

	public TextView getFrom_time() {
		if(from_time==null)
		{
			from_time=(TextView)baseView.findViewById(R.id.message_item_from_time);
		}
		return from_time;
	}

	public TextView getTo_time() {
		if(to_time==null)
		{
			to_time=(TextView)baseView.findViewById(R.id.message_item_to_time);
		}
		return to_time;
	}

	public ProgressBar getFrom_sending() {
		if(from_sending==null)
		{
			from_sending=(ProgressBar)baseView.findViewById(R.id.message_item_from_sending);
		}
		return from_sending;
	}
	public ProgressBar getTo_sending() {
		if(to_sending==null)
		{
			to_sending=(ProgressBar)baseView.findViewById(R.id.message_item_to_sending);
		}
		return to_sending;
	}

	public ImageView getFrom_failure() {
		if(from_failure==null)
		{
			from_failure=(ImageView)baseView.findViewById(R.id.message_item_from_fail);
		}
		return from_failure;
	}

	public ImageView getTo_failure() {
		if(to_failure==null)
		{
			to_failure=(ImageView)baseView.findViewById(R.id.message_item_to_fail);
		}
		return to_failure;
	}

	
	
	
	
}
