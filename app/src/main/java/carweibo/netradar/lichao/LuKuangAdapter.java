package carweibo.netradar.lichao;

import java.util.List;

import carweibo.netradar.lichao.AudioLukuangInfo.MSG_STATUS;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class LuKuangAdapter extends ArrayAdapter<AudioLukuangInfo> {

	Context con;
	
	public LuKuangAdapter(Context context,	List<AudioLukuangInfo> data) {
		super(context, 0, data);
		con=context;
		
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View rowView=convertView;
		LukuangViewCache viewCache;
		
		if(rowView==null)
		{
			
			LayoutInflater inflater = ((Activity) con).getLayoutInflater();
            rowView = inflater.inflate(R.layout.lukuang_item_layout, null);
            viewCache = new LukuangViewCache(rowView);
            rowView.setTag(viewCache);
		}
		else
		{
			viewCache=(LukuangViewCache) rowView.getTag();
			
		}
		AudioLukuangInfo lukuang=(AudioLukuangInfo)getItem(position);
		
		viewCache.getAudio_layout().setTag(position);
		viewCache.getSender_info().setText(lukuang.send_time_s);
		if(lukuang.isPlaying)
		{
			viewCache.getSpeaker_img().setImageResource(R.drawable.chatfrom_voice_playing);
			
		}
		else
			viewCache.getSpeaker_img().setImageResource(R.drawable.chatfrom_group_voice_playing);
		if(lukuang.isLocal)
		{
			viewCache.getIs_unread().setVisibility(View.GONE);
			viewCache.getAudio_layout().setBackgroundResource(R.drawable.lukuang_send_sel);
			
			switch(lukuang.msg_status)
			{
			case SENDING:
				viewCache.getSending_img().setVisibility(View.VISIBLE);
				viewCache.getStatus_img_error().setVisibility(View.GONE);
				viewCache.getStatus_period().setVisibility(View.GONE);
				break;
			case ERROR:
				viewCache.getSending_img().setVisibility(View.GONE);
				viewCache.getStatus_img_error().setVisibility(View.VISIBLE);
				viewCache.getStatus_period().setVisibility(View.GONE);
				break;
			case OK:
				viewCache.getSending_img().setVisibility(View.GONE);
				viewCache.getStatus_img_error().setVisibility(View.GONE);
				viewCache.getStatus_period().setVisibility(View.VISIBLE);
				viewCache.getStatus_period().setText(lukuang.period+"秒");
				break;
			}
			
		}
		else
		{
			viewCache.getIs_unread().setVisibility(View.VISIBLE);
			viewCache.getSending_img().setVisibility(View.GONE);
			viewCache.getStatus_img_error().setVisibility(View.GONE);
			viewCache.getStatus_period().setVisibility(View.VISIBLE);
			
			if(lukuang.isFinishDown)
			{
				if(!lukuang.isPlaying)
				{
					if(lukuang.isUnRead)
					{
						viewCache.getIs_unread().setText("未听");
						viewCache.getIs_unread().setTextColor(Color.RED);
						viewCache.getAudio_layout().setBackgroundResource(R.drawable.lukuang_unread_sel);
					}
					else
					{
						viewCache.getIs_unread().setText("已听");
						viewCache.getIs_unread().setTextColor(Color.parseColor("#229900"));
						viewCache.getAudio_layout().setBackgroundResource(R.drawable.lukuang_readed_sel);
			
					}
				}
				else
				{
					viewCache.getIs_unread().setText("正在播放...");
					viewCache.getIs_unread().setTextColor(Color.parseColor("#229900"));
					viewCache.getAudio_layout().setBackgroundResource(R.drawable.lukuang_unread_sel);
					
					
				}
			}
			else
			{
				if(lukuang.isDownloading)
				{
					viewCache.getIs_unread().setText("加载中...");
					viewCache.getIs_unread().setTextColor(Color.parseColor("#229900"));
					viewCache.getAudio_layout().setBackgroundResource(R.drawable.lukuang_unread_sel);
			
				}
				else
				{
					viewCache.getIs_unread().setText("未加载!");
					viewCache.getIs_unread().setTextColor(Color.parseColor("#000666"));
					viewCache.getAudio_layout().setBackgroundResource(R.drawable.lukuang_unread_sel);
			
				}
			}
			viewCache.getStatus_period().setText(lukuang.period+"秒");
		}
				
		viewCache.getAudio_length().setText(lukuang.lukuang_length);
		return rowView;
	}

	
	

}
