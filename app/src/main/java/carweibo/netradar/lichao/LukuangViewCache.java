package carweibo.netradar.lichao;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LukuangViewCache {
	
	View baseView;
	
	
	LinearLayout audio_layout;
	

	TextView sender_info;
	TextView is_unread;

	ImageView speaker_img,status_img_error;
	
	TextView status_period;
	ProgressBar sending_img;
	
	TextView audio_length;
	
	public LukuangViewCache(View rowView) {
		baseView=rowView;
	}



	
	public TextView getAudio_length() {
		if(audio_length==null)
		{
			audio_length=(TextView)baseView.findViewById(R.id.lukuang_item_length);
		}
		
		return audio_length;
	}



	/**
	 * @return the audio_layout
	 */
	public LinearLayout getAudio_layout() {
		if(audio_layout==null)
		{
			audio_layout=(LinearLayout)baseView.findViewById(R.id.lukuang_item_voice);
		}
		return audio_layout;
	}



	/**
	 * @return the sender_info
	 */
	public TextView getSender_info() {
		if(sender_info==null)
		{
			sender_info=(TextView)baseView.findViewById(R.id.lukuang_item_sender);
			
		}
		return sender_info;
	}



	/**
	 * @return the is_unread
	 */
	public TextView getIs_unread() {
		if(is_unread==null)
		{
			is_unread=(TextView)baseView.findViewById(R.id.lukuang_item_is_unread);
		}
		return is_unread;
	}



	/**
	 * @return the speaker_img
	 */
	public ImageView getSpeaker_img() {
		if(speaker_img==null)
		{
			speaker_img=(ImageView)baseView.findViewById(R.id.lukuang_item_speak_img);
		}
		return speaker_img;
	}



	/**
	 * @return the status_img_error
	 */
	public ImageView getStatus_img_error() {
		if(status_img_error==null)
		{
			status_img_error=(ImageView)baseView.findViewById(R.id.lukuang_item_status_error_img);
		}
		return status_img_error;
	}



	/**
	 * @return the status_period
	 */
	public TextView getStatus_period() {
		if(status_period==null)
		{
			status_period=(TextView)baseView.findViewById(R.id.lukuang_item_status_period);
		}
		return status_period;
	}



	/**
	 * @return the sending_img
	 */
	public ProgressBar getSending_img() {
		if(sending_img==null)
		{
			sending_img=(ProgressBar)baseView.findViewById(R.id.lukuang_item_status_sending);
		}
		return sending_img;
	}



	
}
