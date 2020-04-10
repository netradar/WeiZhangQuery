package carweibo.netradar.lichao;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

public class ImageAndText implements Serializable {


	private static final long serialVersionUID = 1L;
	private static int PIC_MIN_LENGTH=50;
	private static int PIC_MAX_LENGTH=160;
	long id;
	int user_id;
	int user_score;
	String nickname;
	String touxiang_url;
	String time;
	String content;
	String comment_num;
	String retweet_num;
	String good_num;
	int grade;
	
	List<PicInfo> pic_list_url;
	
	public ImageAndText()
	{
		
	}
	
	public ImageAndText(String server_url,JSONObject json,float denisty)
	{
		
		
		pic_list_url=new ArrayList<PicInfo>();
		try {
			id=json.getLong("id");
			
			touxiang_url=server_url+"/touxiang/"+json.getString("touxiang_url");
			time=getTime(json.getString("time"));
			user_id=json.getInt("user_id");
			try {
				nickname=URLDecoder.decode(json.getString("nickname"),"UTF-8");
				content=URLDecoder.decode(json.getString("content"),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			};
			
			retweet_num=json.getInt("retweet_num")+"个分享";
			comment_num=json.getInt("comment_num")+"个评论";
			good_num=json.getInt("good_num")+"个赞";
			grade=json.getInt("grade");
			user_score=json.getInt("user_score");
			String pic_list_string=json.getString("pic_list_url");
			
			if(pic_list_string.length()!=0)
			{
				JSONArray ja=new JSONArray(pic_list_string);
				for(int i=0;i<ja.length();i++)
				{
					PicInfo info=new PicInfo();
					info.url=server_url+"/weibo_pic/"+ja.getJSONObject(i).getString("url");
					String size=ja.getJSONObject(i).getString("size");
					
					info.width=Integer.parseInt(size.substring(0, size.indexOf("*")));
					info.height=Integer.parseInt(size.substring(size.indexOf("*")+1,size.length()));
					
					if(info.width/info.height<=2&&info.height/info.width<=2)
					{
						if(info.height<PIC_MIN_LENGTH*denisty||info.width<PIC_MIN_LENGTH*denisty)
						{
							if(info.height>=info.width)
							{
								
								info.height=(int) (info.height*((PIC_MIN_LENGTH*denisty)/(float)info.width));
								info.width=(int) (PIC_MIN_LENGTH*denisty);
							}
							else
							{
								info.width=(int) (info.width*((PIC_MIN_LENGTH*denisty)/(float)info.height));
								info.height=(int) (PIC_MIN_LENGTH*denisty);
							}
							
						}
					}
					if(info.width>=PIC_MAX_LENGTH*denisty)
						info.width=(int) (PIC_MAX_LENGTH*denisty);
					if(info.height>=PIC_MAX_LENGTH*denisty)
						info.height=(int) (PIC_MAX_LENGTH*denisty);
					
					pic_list_url.add(info);
				}
			}
			
			//JSONArray ja=new JSONArray("d");
			/*ObjectMapper om=new ObjectMapper();
			Log.d("lichao",pic_list_string);
			List<PicInfo2> info2=new ArrayList<PicInfo2>();
			info2=om.readValue(pic_list_string,  new TypeReference<List<PicInfo2>>(){});
			*/
			/*for(PicInfo2 pi:info2)
			{
				PicInfo info=new PicInfo();
				info.url=server_url+pi.url;
				Log.d("lichao","url is"+info.url);
				String size=pi.size;
				
				info.width=Integer.parseInt(size.substring(0, size.indexOf("*")-1));
				info.height=Integer.parseInt(size.substring(size.indexOf("*")+1),size.length());
				pic_list_url.add(info);
			}
			*/
			
			
			
		} catch (JSONException e) {
			pic_list_url=null;
			Log.d("lichao","image and text json error 1:"+e.toString());
		} 
	}

	private String getTime(String time)
	{
		return time.substring(0,4)+"年"+time.substring(4,6)+"月"+time.substring(6,8)+"日 "+time.substring(8,10)+":"+time.substring(10,12)+":"+time.substring(12,14);
	}
/*	private ThumbPicInfo getThumbPicInfo(int width,int height)
	{
		ThumbPicInfo pic=new ThumbPicInfo();
		
		float rate;
		if(height>=width)
		{
			rate=height/width;
			
		}
		else
		{
			rate=width/height;
		}
		if(rate>2)
		{
			pic.scale_type=ImageView.ScaleType.CENTER;
			
		}
		else
		{
			pic.scale_type=ImageView.ScaleType.FIT_CENTER;
		}
	
		if(width<denisty*PIC_MAX_LENGTH&&height<denisty*PIC_MAX_LENGTH)
		{
			if(width>denisty*PIC_MIN_LENGTH&&height>denisty*PIC_MIN_LENGTH)
			{
				pic.height=height;
				pic.width=width;
				pic.scale_type=ImageView.ScaleType.FIT_CENTER;
			}
			else if(height>=width)
			{
				if(rate<=2)
				{
					pic.width=(int) (denisty*PIC_MIN_LENGTH);
					pic.height=(int) (height*(denisty*PIC_MIN_LENGTH/(float)width));
					pic.scale_type=ImageView.ScaleType.FIT_CENTER;
				}
				else
				{
					pic.width=(int) (denisty*PIC_MIN_LENGTH);
					pic.height=(int) (denisty*PIC_MAX_LENGTH);
					pic.scale_type=ImageView.ScaleType.FIT_CENTER;
				}
			}
			else 
			{
				if(rate<=2)
				{
					pic.height=(int) (denisty*PIC_MIN_LENGTH);
					pic.width=(int) (width*(denisty*PIC_MIN_LENGTH/(float)height));
					pic.scale_type=ImageView.ScaleType.FIT_CENTER;
				}
				else
				{
					pic.height=(int) (denisty*PIC_MIN_LENGTH);
					pic.width=(int) (denisty*PIC_MAX_LENGTH);
					pic.scale_type=ImageView.ScaleType.CENTER;
				}
			}
		}
		else if(height>=width)
		{
			if(rate<=2)
			{
				pic.height=(int) (denisty*PIC_MAX_LENGTH);
				pic.width=(int) (width/((float)height/(denisty*PIC_MAX_LENGTH)));
				pic.scale_type=ImageView.ScaleType.FIT_CENTER;
			}
			else
			{
				pic.width=(int) (denisty*PIC_MIN_LENGTH);
				pic.height=(int) (denisty*PIC_MAX_LENGTH);
				pic.scale_type=ImageView.ScaleType.FIT_CENTER;
			}
			
		}
			if(rate>2)
			{
				
			}
			else
			{
				if(width<PIC_MIN_LENGTH*denisty)
				{
					pic.width=(int) (PIC_MIN_LENGTH*denisty);
					pic.height=(int) (height*(PIC_MIN_LENGTH*denisty/(float)width));
				}
				pic.scale_type=ImageView.ScaleType.FIT_CENTER;
				pic.height=(int) (PIC_MAX_LENGTH*denisty);
				
		///	}
		
		
		
		return null;
	}*/
   
}
