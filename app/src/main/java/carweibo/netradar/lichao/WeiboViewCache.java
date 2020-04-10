package carweibo.netradar.lichao;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeiboViewCache {

	 private View baseView;
	    private TextView user;
	    private TextView content;
	    private TextView time;
	    private TextView comment_num;
	    private TextView retweet_num;
	    private TextView good_num;
	    private TextView delete;
	    private ImageView touxiang;
	    private ImageView pic_single;
	    private ImageView top_weibo_sign;
	    
	    private ImageView weibo_vip_icon;
	    
	    private LinearLayout pic_multi_layout;
	    private LinearLayout pic_single_layout;
	    
	    private List<ImageView> pic_multi;
	    
	    private List<LinearLayout> multi_layout;
	    
	   private LinearLayout share_layout;
	   private LinearLayout good_layout; 
	    

	    public WeiboViewCache(View baseView) {
	    	
	        this.baseView = baseView;
	    }
	    
	    

	    public LinearLayout getShare_layout() {
	    	if (share_layout == null) {
	    		 
	    		share_layout = (LinearLayout) baseView.findViewById(R.id.bottom_retweet);
	        }
			return share_layout;
		}



		public LinearLayout getGood_layout() {
			
			if (good_layout == null) {
	    		 
				good_layout = (LinearLayout) baseView.findViewById(R.id.bottom_good);
	        }
			
			return good_layout;
		}




		public LinearLayout getPic_single_layout() {
	    	if (pic_single_layout == null) {
	    		 
	    		pic_single_layout = (LinearLayout) baseView.findViewById(R.id.single_image_layout);
	        }
			return pic_single_layout;
		}



	
		public TextView getDelete() {
	    	if (delete == null) {
	    		delete = (TextView) baseView.findViewById(R.id.weibo_delete);
	        }
	       
			return delete;
		}



		public TextView getUser() {
	    	if (user == null) {
	            user = (TextView) baseView.findViewById(R.id.weibo_nickname);
	        }
	        return user;
		}



		public TextView getContent() {
			 if (content == null) {
		            content = (TextView) baseView.findViewById(R.id.weibo_content);
		        }
		        return content;
		}



		public TextView getTime() {
			 if (time == null) {
				 time = (TextView) baseView.findViewById(R.id.weibo_time);
		        }
		    
			return time;
		}



		public TextView getComment_num() {
			if (comment_num == null) {
				comment_num = (TextView) baseView.findViewById(R.id.weibo_item_comment_num);
		        }
			return comment_num;
		}

		public TextView getRetweet_num() {
			if (retweet_num == null) {
				retweet_num = (TextView) baseView.findViewById(R.id.weibo_bottom_share_text);
		        }
			return retweet_num;
		}

		public TextView getGood_num() {
			if (good_num == null) {
				good_num = (TextView) baseView.findViewById(R.id.weibo_item_good_num);
		        }
			return good_num;
		}



		public ImageView getTouxiang() {
			if (touxiang == null) {
				// Log.d("lichao","new touxiang");
				touxiang = (ImageView) baseView.findViewById(R.id.weibo_touxiang);
		        }
			return touxiang;
		}



		public ImageView getPic_single() {
			if (pic_single == null) {
				
				pic_single = (ImageView) baseView.findViewById(R.id.weibo_image_single);
		        }
			return pic_single;
		}



		public ImageView getTop_weibo_sign() {
			if (top_weibo_sign == null) {
				top_weibo_sign = (ImageView) baseView.findViewById(R.id.weibo_top_sign);
		        }
			return top_weibo_sign;
		}



		public ImageView getWeibo_vip_icon() {
			if (weibo_vip_icon == null) {
				weibo_vip_icon = (ImageView) baseView.findViewById(R.id.weibo_vip_sign);
		        }
			return weibo_vip_icon;
		}



		public LinearLayout getPic_multi_layout() {
			if (pic_multi_layout == null) {
				pic_multi_layout = (LinearLayout) baseView.findViewById(R.id.weibo_image_multi);
		        }
			return pic_multi_layout;
		}



		public List<ImageView> getPic_multi() {
			if (pic_multi == null) {
				pic_multi=new ArrayList<ImageView>();
				
				pic_multi.add((ImageView) baseView.findViewById(R.id.weibo_multi_image1));
				pic_multi.add((ImageView) baseView.findViewById(R.id.weibo_multi_image2));
				pic_multi.add((ImageView) baseView.findViewById(R.id.weibo_multi_image3));
		        }
			return pic_multi;
		}

		public List<LinearLayout> getMulti_layout() {
			if (multi_layout == null) {
				multi_layout=new ArrayList<LinearLayout>();
				
				multi_layout.add((LinearLayout) baseView.findViewById(R.id.multi_image_layout1));
				multi_layout.add((LinearLayout) baseView.findViewById(R.id.multi_image_layout2));
				multi_layout.add((LinearLayout) baseView.findViewById(R.id.multi_image_layout3));
		        }
			return multi_layout;
		}




}
