package carweibo.netradar.lichao;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;

public class BindUser {
	
	public static UserInfo getUserInfo(String... arg0)
	{
		String type=arg0[0];
		
		UserInfo userinfo=new UserInfo();
		
		
		if(type.equals("QQ"))
		{
			String url="https://openmobile.qq.com/user/get_simple_userinfo?" +
					"access_token="+arg0[1]+
					"&oauth_consumer_key=100557777"+
					"&openid="+arg0[2];
			
			userinfo.user_type=arg0[0];
			userinfo.token=arg0[1];
			userinfo.openid=arg0[2];
			userinfo.expires_in=arg0[3];
			userinfo.username="";
			
			
			HttpClient client = null;
			try {
				client=new DefaultHttpClient();
				client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
				client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
				//client.getParams().setParameter(HttpRequestParams , 20000);
				DefaultHttpRequestRetryHandler kk=new DefaultHttpRequestRetryHandler(0,false);
				((AbstractHttpClient)client).setHttpRequestRetryHandler(kk);
				HttpGet httpget_id=new HttpGet(url);  
				HttpResponse response=client.execute(httpget_id);
				int ret;
				 ret=response.getStatusLine().getStatusCode();
				 if(ret!=200) return null;
				 
				 HttpEntity entity=response.getEntity();  
				
				 
				 JSONObject retjson=new JSONObject();
				 retjson=new JSONObject(EntityUtils.toString(entity, HTTP.UTF_8));
				 if(getSDDir()!=null)
				 { 
					 userinfo.touxiang_url=getSDDir()+"/weizhangquery/pic/touxiang/"+arg0[1]+".png";
					 ScreenShoot.savePic(getBitmap(retjson.getString("figureurl_qq_1")), getSDDir()+"/weizhangquery/pic/touxiang", userinfo.touxiang_url , 100);
				 }
				 else
				 {
					 userinfo.touxiang_url="NOTOUXIANG";
				 }
				 userinfo.nickname=retjson.getString("nickname");
				 return userinfo;
				 
			}catch (UnsupportedEncodingException e) {
				
				return null;
				
				
			}catch (ClientProtocolException e) {
			
				if(client!=null&&client.getConnectionManager()!=null)
					client.getConnectionManager().shutdown();
				return null;
				
				
			}catch (IOException e) {
				if(client!=null&&client.getConnectionManager()!=null)
					client.getConnectionManager().shutdown();
				
				return null;
			}catch (JSONException e1) {
				return null;	
			}
		
		}
		if(type.equals("Weibo"))
		{
			
			
				String url="https://api.weibo.com/2/account/get_uid.json?access_token="+arg0[1];
				userinfo.user_type=arg0[0];
				userinfo.token=arg0[1];
				userinfo.openid=arg0[2];
				userinfo.expires_in=arg0[3];
				userinfo.username="";
				
				HttpClient client = null;
				try {
					client=new DefaultHttpClient();
					client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
					client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
					//client.getParams().setParameter(HttpRequestParams , 20000);
					DefaultHttpRequestRetryHandler kk=new DefaultHttpRequestRetryHandler(0,false);
					((AbstractHttpClient)client).setHttpRequestRetryHandler(kk);
					HttpGet httpget_id=new HttpGet(url);  
					HttpResponse response=client.execute(httpget_id);
					int ret;
					 ret=response.getStatusLine().getStatusCode();
					 if(ret!=200) return null;
					 
					 HttpEntity entity=response.getEntity();  
					
					 
					 JSONObject retjson=new JSONObject();
					 retjson=new JSONObject(EntityUtils.toString(entity, HTTP.UTF_8));
					
	                 String uid= retjson.getString("uid");
	                 
	                 String url_info="https://api.weibo.com/2/users/show.json?uid="+uid+"&access_token="+arg0[1];
	                
	                 HttpGet httpget_info=new HttpGet(url_info);  
	                 HttpResponse response_info=client.execute(httpget_info);
	                 ret=response_info.getStatusLine().getStatusCode();
					 if(ret!=200) return null;
					 
					 HttpEntity entity1=response_info.getEntity();  
					
					 
					
					 JSONObject retjson1=new JSONObject(EntityUtils.toString(entity1, HTTP.UTF_8));
					 
					 if(client!=null&&client.getConnectionManager()!=null)
							client.getConnectionManager().shutdown();
					 if(getSDDir()!=null)
					 {
						 userinfo.touxiang_url=getSDDir()+"/weizhangquery/pic/touxiang/"+arg0[1]+".png";
					
					 	ScreenShoot.savePic(getBitmap(retjson1.getString("profile_image_url")), getSDDir()+"/weizhangquery/pic/touxiang", userinfo.touxiang_url , 100);
					 }
					 else
						 userinfo.touxiang_url="NOTOUXIANG";
					 
					 userinfo.nickname=retjson1.getString("screen_name");
					 return userinfo;
	                 
	                 
				} catch (UnsupportedEncodingException e) {
								
					return null;
					
					
				} catch (ClientProtocolException e) {
				
					if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
					return null;
					
				} catch (IOException e) {
					
					
					if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
					
					return null;
				}catch (JSONException e1) {
					return null;	
				}
		
		}
		if(type.equals("carweibo"))
		{
			return null;
		}
		return null;
		
	}
	public static Bitmap getBitmap(String biturl)
	  {
	    Bitmap bitmap=null;
	    
	    try {
	      URL url=new URL(biturl);
	      URLConnection conn=url.openConnection();
	      InputStream in =conn.getInputStream();
	      bitmap=BitmapFactory.decodeStream(new BufferedInputStream(in));

	    } catch (Exception e) {
	     
	      
	    }
	    return bitmap;
	  }
	private static String getSDDir()
	{
		 if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
    	 {  
    		 File sdCard=Environment.getExternalStorageDirectory();
    		 
    		
    		 StatFs statfs = new StatFs(sdCard.getPath()); 
    		 long totalBlocks = statfs.getAvailableBlocks();
    		 long blocSize = statfs.getBlockSize();
    		 if((totalBlocks*blocSize)>1000000)
    			 return sdCard.getPath();
    		 else
    			 return null;
    		 
    				 
    	 }
		return null;
	}
}
