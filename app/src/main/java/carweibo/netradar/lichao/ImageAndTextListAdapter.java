package carweibo.netradar.lichao;

import java.util.List;



import carweibo.netradar.lichao.AsyncImageLoader.ImageCallback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ImageAndTextListAdapter extends ArrayAdapter<ImageAndText> implements ImageCallback {

	private static int LOADIMG_TYPE_TOUXIANG=0;
	private static int LOADIMG_TYPE_SINGLE=1;
	private static int LOADIMG_TYPE_MULTI=2;
		
	    private ListView listView;
	    AsyncImageLoader	asyncImageLoader;
	    Context act;
	    float density;
	    int scale;
	    int color;
	    String current_nickname;
	    public ImageAndTextListAdapter(Context activity, List<ImageAndText> imageAndTexts, ListView listView,String cur_nickname) {
	        super(activity, 0, imageAndTexts);
	        this.listView = listView;
	        act=activity;
	        color=Color.argb(50, 237, 237, 237);
	        density=activity.getResources().getDisplayMetrics().density;
	        current_nickname=cur_nickname;
	        asyncImageLoader=new AsyncImageLoader(density);
	        
	    }

	    public View getView(int position, View convertView, ViewGroup parent) {
	        Activity activity = (Activity) getContext();

	        // Inflate the views from XML
	        View rowView = convertView;
	        WeiboViewCache viewCache;
	        if (rowView == null) {
	            LayoutInflater inflater = activity.getLayoutInflater();
	            rowView = inflater.inflate(R.layout.weibo_item_layout, null);
	            viewCache = new WeiboViewCache(rowView);
	            rowView.setTag(viewCache);
	          //  Log.d("lichao","get view new row");
	        } else {
	            viewCache = (WeiboViewCache) rowView.getTag();
	          
	        }
	        ImageAndText imageAndText = getItem(position);

	        // Set the text on the TextView
	       
	        viewCache.getUser().setText(imageAndText.nickname);
	        viewCache.getUser().setTag(position);
	     
	        if(imageAndText.grade==1)
	        {
	        	viewCache.getContent().setTextColor(Color.parseColor("#006600"));
	        	
	        }
	        else
	        {
	        	
	        	viewCache.getContent().setTextColor(Color.BLACK);
	        }
	        viewCache.getContent().setText(imageAndText.content);
	       
	        viewCache.getTime().setText(imageAndText.time);
	        
	        /*if(imageAndText.comment_num!=0)
	        	viewCache.getComment_num().setText(new String().valueOf(imageAndText.comment_num));
	        else*/
	        	viewCache.getComment_num().setText(imageAndText.comment_num);
	 	   
	      /*  if(imageAndText.good_num!=0)
	        	viewCache.getGood_num() .setText(new String().valueOf(imageAndText.good_num));
	        else */
	        	viewCache.getGood_num() .setText(imageAndText.good_num);
	        	viewCache.getRetweet_num().setText(imageAndText.retweet_num);
	        if(imageAndText.grade==2)
	        	viewCache.getTop_weibo_sign().setVisibility(View.VISIBLE);
	        else
	        	viewCache.getTop_weibo_sign().setVisibility(View.GONE);
	        
	        if(imageAndText.user_score>5000)
	        	viewCache.getWeibo_vip_icon().setVisibility(View.VISIBLE);
	        else
	        	viewCache.getWeibo_vip_icon().setVisibility(View.GONE);
	        
	        if(current_nickname.equals(imageAndText.nickname))
	        {
	        	viewCache.getDelete().setVisibility(View.VISIBLE);
	        }
	        else
	        	viewCache.getDelete().setVisibility(View.GONE);
	        viewCache.getDelete().setTag(position);
	      
	        
	        viewCache.getShare_layout().setTag(position);
	        viewCache.getGood_layout().setTag(position);
	        // Load the image and set it on the ImageView
	    //    String imageUrl = imageAndText.getStrByKey("pic");
	        if(imageAndText.pic_list_url.size()==0)
	        {
	        	viewCache.getPic_single_layout().setVisibility(View.GONE);
	        	viewCache.getPic_multi_layout().setVisibility(View.GONE);
	        }
	        else if(imageAndText.pic_list_url.size()==1)
	        {
	        	viewCache.getPic_single_layout().setVisibility(View.VISIBLE);
	        	viewCache.getPic_single_layout().setLayoutParams(new FrameLayout.LayoutParams(imageAndText.pic_list_url.get(0).width,imageAndText.pic_list_url.get(0).height));
	        	
	        	viewCache.getPic_multi_layout().setVisibility(View.GONE);

	        	viewCache.getPic_single().setTag(position+imageAndText.pic_list_url.get(0).url);
	        	 Bitmap pic_single_bmp= asyncImageLoader.loadDrawable(imageAndText.pic_list_url.get(0).url, new String().valueOf(position),this,LOADIMG_TYPE_SINGLE);
	    	     
	 	        if(pic_single_bmp==null)
	 	        {
	 	        	viewCache.getPic_single().setImageBitmap(null);
	 	        //	viewCache.getPic_single().setImageResource(R.drawable.chat_pic_loading);
	 	        	//viewCache.getTouxiang().setImageDrawable(activity.getResources().getDrawable(R.drawable.switchuser));
	 	        }
	 	        else
	 	        	viewCache.getPic_single().setImageBitmap(pic_single_bmp);
	 	        
	        	
	        }
	        else
	        {
	        	viewCache.getPic_single_layout().setVisibility(View.GONE);
	           	viewCache.getPic_multi_layout().setVisibility(View.VISIBLE);
	           	viewCache.getMulti_layout().get(0).setVisibility(View.GONE);
	           	viewCache.getMulti_layout().get(1).setVisibility(View.GONE);
	           	viewCache.getMulti_layout().get(2).setVisibility(View.GONE);
	           	for(int i=0;i<imageAndText.pic_list_url.size();i++)
	           	{
	           		viewCache.getMulti_layout().get(i).setVisibility(View.VISIBLE);
	           		viewCache.getPic_multi().get(i).setTag(position+imageAndText.pic_list_url.get(i).url);
	           		Bitmap pic_multi_bmp=asyncImageLoader.loadDrawable(imageAndText.pic_list_url.get(i).url, new String().valueOf(position),this,LOADIMG_TYPE_MULTI);
	           		if(pic_multi_bmp==null)
	           		{
	           			viewCache.getPic_multi().get(i).setImageBitmap(null);
	           		}
	           		else
	           			viewCache.getPic_multi().get(i).setImageBitmap(pic_multi_bmp);
	           	}
	           	
	           	
	        }
	        viewCache.getTouxiang().setTag(position+imageAndText.touxiang_url);
		       
	       // Log.d("lichao","touxiang url is "+imageAndText.touxiang_url);
	        Bitmap touxiang_bmp= asyncImageLoader.loadDrawable(imageAndText.touxiang_url, String.valueOf(position),this,LOADIMG_TYPE_TOUXIANG);
	     
	        if(touxiang_bmp==null)
	        {
	        	viewCache.getTouxiang().setImageResource(R.drawable.switchuser);
	        	//viewCache.getTouxiang().setImageDrawable(activity.getResources().getDrawable(R.drawable.switchuser));
	        }
	        else
	        	viewCache.getTouxiang().setImageBitmap(touxiang_bmp);
	        /*  if(imageUrl==null) 
	        	{
	        	
	        	imageView.setVisibility(2);
	        	return rowView;
	        	}
	        int height=Integer.parseInt(imageAndText.getStrByKey("height"));
	        int width=Integer.parseInt(imageAndText.getStrByKey("width"));;
	        if(height>100)*/
	      /*  {
	        	int rate;
	        	rate=height/scale;
	        	height=height/rate;
	        	width=width/rate;
	        	
	        }
	        
	        Log.d("lichao","h="+height+"w="+width);*/
	       // imageView.setLayoutParams(new LayoutParams(width,height));
	        
	        
	        /*
	        String id=imageAndText.getStrByKey("id");
	        
	        imageView.setTag(id+imageUrl);
	        final Bitmap cachedImage = asyncImageLoader.loadDrawable(imageUrl, id,this);
	        */
	       
	        /*
			if (cachedImage == null) {
				Bitmap bm=Bitmap.createBitmap(width, height, Config.ARGB_4444);
				bm.eraseColor(color);
						
				imageView.setImageBitmap(bm);
			}else{
				
				imageView.setImageBitmap(cachedImage);
			}
		  
		        imageView.setOnClickListener(new View.OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						
						Intent it=new Intent(act, ViewPic.class);
						
						ImageView v=(ImageView)arg0;
						
						String pic=(String)v.getTag();
						
						Rect frame=new Rect();
						((Activity) act).getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
							

						it.putExtra("height", frame.top);
					
						it.putExtra("url", pic);
								
						act.startActivity(it);
						
					}

					private Object getWindow() {
						// TODO Auto-generated method stub
						return null;
					}
		        	
		        	
		        });*/

	      

	        return rowView;
	    }

		@Override
		public void imageLoaded(Bitmap imageDrawable, String imageUrl,String id) {
			// TODO Auto-generated method stub
			//if(imageDrawable==null) return;
			ImageView imageViewByTag = (ImageView) listView.findViewWithTag(id+imageUrl);
	                if (imageViewByTag != null) {
	                	//Rect rc=imageDrawable.getBounds();
	                	//imageViewByTag.setScaleType(ImageView.ScaleType.FIT_START);
	                	/*if(imageDrawable==null)
	                		imageViewByTag.setImageResource(R.drawable.preview_image_failure);
	                	else*/
	                		imageViewByTag.setImageBitmap(imageDrawable);
	                                 
	                }
	     }
		
		

}
