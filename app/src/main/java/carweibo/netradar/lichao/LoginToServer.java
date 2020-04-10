package carweibo.netradar.lichao;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

public class LoginToServer {
	
	public String GetVenderCommentInfo(Context context, int vender_id,
			int readed_id, boolean is_bad_comment) {
		String url=((DBUility)context.getApplicationContext()).server+"/upload/getvendercomment";
		MultipartEntity postEntity = new MultipartEntity();
		
		
		ContentBody params=null;
		try {
			
			params=new StringBody(getGetVenderCommentReqParam(vender_id,readed_id,is_bad_comment));
			} catch (UnsupportedEncodingException e)
			{
				return "error_data";
			}
		
		postEntity.addPart("params", params);
		
		
		return getServerString(url,postEntity);
		
	}
	private String getGetVenderCommentReqParam(int vender_id, int readed_id,
			boolean is_bad_comment) {
		
		try {
			JSONObject js=new JSONObject();
						
			js.put("vender_id", vender_id);
			js.put("readed_id", readed_id);
			
			js.put("is_bad_comment", is_bad_comment);
			
			DesUility du=new DesUility("kajdkd");
             	
			return du.encrypt(js.toString());
			
		} catch (JSONException e) {
			
			
		} catch (Exception e) {

		}
		
		return null;
	}
	public String GetVenderList(Context context,int readed_id)
	{
		
		String url=((DBUility)context.getApplicationContext()).server+"/upload/getvender?readed_id="+readed_id;
	
		return getServerString(url,null);
		
		
	}

	
	public String sendAudioLukuang(Context context, String nickname,boolean isAnonymous,String file) {
		
		String url=((DBUility)context.getApplicationContext()).server+"/uploadlukuang";
		MultipartEntity postEntity = new MultipartEntity();
		
		
		ContentBody params=null;
		try {
			
			params=new StringBody(getUploadLukuang(nickname,isAnonymous,file));
			} catch (UnsupportedEncodingException e)
			{
				return "error_data";
			}
		
		postEntity.addPart("params", params);
		
		File upload_file = new File(file);
		
		if(upload_file.exists())
		{
			Log.d("lichao","login to server file upload ok");
			ContentBody cbFileData = new FileBody(upload_file, "image/jpg" );
			postEntity.addPart("uploadFile", cbFileData);
		
		}
		else
			Log.d("lichao","login to server file upload error");
		HttpPost httpPost=new HttpPost(url);  
      
        HttpClient client = null;
        int ret=-1;
        try {
        	
        	httpPost.setEntity(postEntity);
        	
        	client=new DefaultHttpClient();
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
			
			DefaultHttpRequestRetryHandler kk=new DefaultHttpRequestRetryHandler(0,false);
			((AbstractHttpClient)client).setHttpRequestRetryHandler(kk);
			HttpResponse response=client.execute(httpPost);
			
			ret=response.getStatusLine().getStatusCode();
			
			Log.d("lichao","Login ToServer upload audio ret is "+ret);
			if(ret!=200) 
			{
				 if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
				 switch(ret)
				 {
				 case 201:
					 return "user_not_exist";
				 case 202:
					 return "anonymous_not_permit";
				 case 203:
					 return "user_not_auth";
				 default:
					 return "error_data";
				 }
				
			}
			
			 HttpEntity entity=response.getEntity();  
			 String ret_str=EntityUtils.toString(entity, HTTP.UTF_8);
			 if(client!=null&&client.getConnectionManager()!=null)
					client.getConnectionManager().shutdown();
			return ret_str;
			
			} catch (UnsupportedEncodingException e) {
					
					Log.d("lichao",e.toString());
							
			} catch (ClientProtocolException e) {
					
					Log.d("lichao",e.toString());
					
			} catch (IOException e) {
				Log.d("lichao",e.toString());
					
			
			}finally{
				 if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
			}
			return "error_data";
			
			}
	private String getUploadLukuang(String nickname,boolean isAnonymous, String file) {
		try {
			JSONObject js=new JSONObject();
						
			js.put("nickname",URLEncoder.encode(nickname, "UTF-8"));
			js.put("isAnonymous", isAnonymous);
			
			DesUility du=new DesUility("kajdkd");
             	
			return du.encrypt(js.toString());
			
		} catch (JSONException e) {
			
			
		} catch (Exception e) {

		}
		
		return null;
		
	}
	public  String AuthUser(Context context,String nickname,boolean isAnonymous,int version)
	{
		
		
		String url=((DBUility)context.getApplicationContext()).server+"/auth";
		MultipartEntity postEntity = new MultipartEntity();
		
		
		ContentBody params=null;
		try {
			
			params=new StringBody(getAuthReqParam(nickname,isAnonymous,version));
			} catch (UnsupportedEncodingException e)
			{
				return "error_data";
			}
		
		postEntity.addPart("params", params);
		
		
		HttpPost httpPost=new HttpPost(url);  
      
        HttpClient client = null;
        int ret=-1;
        try {
        	
        	httpPost.setEntity(postEntity);
        	
        	client=new DefaultHttpClient();
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
			
			DefaultHttpRequestRetryHandler kk=new DefaultHttpRequestRetryHandler(0,false);
			((AbstractHttpClient)client).setHttpRequestRetryHandler(kk);
			HttpResponse response=client.execute(httpPost);
			
			ret=response.getStatusLine().getStatusCode();
			
			Log.d("lichao","Login ToServer auth ret is "+ret);
			if(ret!=200) 
			{
				 if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
				 if(ret==201) return "no_user";
				return "error_data";
			}
			
			 HttpEntity entity=response.getEntity();  
			 String ret_str=EntityUtils.toString(entity, HTTP.UTF_8);
			 if(client!=null&&client.getConnectionManager()!=null)
					client.getConnectionManager().shutdown();
			return ret_str;
			
			} catch (UnsupportedEncodingException e) {
					
					Log.d("lichao",e.toString());
							
			} catch (ClientProtocolException e) {
					
					Log.d("lichao",e.toString());
					
			} catch (IOException e) {
				Log.d("lichao",e.toString());
					
			
			}finally{
				 if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
			}
			return "error_data";
			
			}
	private String getAuthReqParam(String nickname,boolean isAnonymous,
			int version) {
		try {
			JSONObject js=new JSONObject();
						
			js.put("nickname",URLEncoder.encode(nickname, "UTF-8"));
			
			js.put("isAnonymous", isAnonymous);
			js.put("version", version);
			
			DesUility du=new DesUility("kajdkd");
             	
			return du.encrypt(js.toString());
			
		} catch (JSONException e) {
			
			
		} catch (Exception e) {

		}
		
		return null;
	}
	
	public  String GetSingleWeiboById(Context context,String nickname,long weiboId,boolean isTop)
	{
		
		
		String url=((DBUility)context.getApplicationContext()).server+"/upload/getsingleweibo";
		MultipartEntity postEntity = new MultipartEntity();
		
		
		ContentBody params=null;
		try {
			
			params=new StringBody(getGetSingleWeiboReqParam(nickname,weiboId,isTop));
			} catch (UnsupportedEncodingException e)
			{
				return "error_data";
			}
		
		postEntity.addPart("params", params);
		
		
		HttpPost httpPost=new HttpPost(url);  
      
        HttpClient client = null;
        int ret=-1;
        try {
        	
        	httpPost.setEntity(postEntity);
        	
        	client=new DefaultHttpClient();
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
			//client.getParams().setParameter(HttpRequestParams , 20000);
			DefaultHttpRequestRetryHandler kk=new DefaultHttpRequestRetryHandler(0,false);
			((AbstractHttpClient)client).setHttpRequestRetryHandler(kk);
			HttpResponse response=client.execute(httpPost);
			
			ret=response.getStatusLine().getStatusCode();
			
			Log.d("lichao","Login ToServer ret is "+ret);
			if(ret!=200) 
			{
				 if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
				 if(ret==201) return "no_weibo";
				return "error_data";
			}
			
			 HttpEntity entity=response.getEntity();  
			 String ret_str=EntityUtils.toString(entity, HTTP.UTF_8);
			 if(client!=null&&client.getConnectionManager()!=null)
					client.getConnectionManager().shutdown();
			return ret_str;
			
			} catch (UnsupportedEncodingException e) {
					
					Log.d("lichao",e.toString());
							
			} catch (ClientProtocolException e) {
					
					Log.d("lichao",e.toString());
					
			} catch (IOException e) {
				Log.d("lichao",e.toString());
					
			
			}finally{
				 if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
			}
			return "error_data";
			
			}
	private String getGetSingleWeiboReqParam(String nickname, long weiboId,
			boolean isTop) {
		try {
			JSONObject js=new JSONObject();
						
			js.put("nickname",URLEncoder.encode(nickname, "UTF-8"));
			
			js.put("weibo_id", weiboId);
			js.put("isTop", isTop);
			
			DesUility du=new DesUility("kajdkd");
             	
			return du.encrypt(js.toString());
			
		} catch (JSONException e) {
			
			
		} catch (Exception e) {

		}
		
		return null;
	}
	public  String GetTimerInfo(Context context,String nickname)
	{
		
		
		String url=((DBUility)context.getApplicationContext()).server+"/upload/timergetinfo";
		MultipartEntity postEntity = new MultipartEntity();
		
		
		ContentBody params=null;
		try {
			
			params=new StringBody(getGetTimerInfoParam(nickname));
			} catch (UnsupportedEncodingException e)
			{
				return "error_data";
			}
		
		postEntity.addPart("params", params);
		
		
		HttpPost httpPost=new HttpPost(url);  
      
        HttpClient client = null;
        int ret=-1;
        try {
        	
        	httpPost.setEntity(postEntity);
        	
        	client=new DefaultHttpClient();
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
			
			DefaultHttpRequestRetryHandler kk=new DefaultHttpRequestRetryHandler(0,false);
			((AbstractHttpClient)client).setHttpRequestRetryHandler(kk);
			HttpResponse response=client.execute(httpPost);
			
			ret=response.getStatusLine().getStatusCode();
			if(ret!=200) 
			{
				 if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
				 
				return "error_data";
			}
			 HttpEntity entity=response.getEntity();  
			 String ret_str=EntityUtils.toString(entity, HTTP.UTF_8);
			 if(client!=null&&client.getConnectionManager()!=null)
					client.getConnectionManager().shutdown();
			return ret_str;
			
			} catch (UnsupportedEncodingException e) {
					
					Log.d("lichao",e.toString());
							
			} catch (ClientProtocolException e) {
					
					Log.d("lichao",e.toString());
					
			} catch (IOException e) {
				Log.d("lichao",e.toString());
					
			
			}finally{
				 if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
			}
			return "error_data";
			
			}
	
	private String getGetTimerInfoParam(String nickname) {
		
		try {
			JSONObject js=new JSONObject();
						
		
			
			js.put("nickname", URLEncoder.encode(nickname, "UTF-8"));
			
			
			DesUility du=new DesUility("kajdkd");
             	
			return du.encrypt(js.toString());
			
		} catch (JSONException e) {
			
			
		} catch (Exception e) {

		}
		
		
		return null;
	}

	public  int sendPrivateMsg(Context context,int to_user_id, String from_nickname,
			String msg, String time) {
		
		String url=((DBUility)context.getApplicationContext()).server+"/upload/uploadmsg";
		MultipartEntity postEntity = new MultipartEntity();
		
		
		ContentBody params=null;
		try {
			
			params=new StringBody(getUploadMsgReqParam(to_user_id,from_nickname,msg));
		
		} catch (UnsupportedEncodingException e)
			{
				return -1;
			}
		
		postEntity.addPart("params", params);
		
		
		HttpPost httpPost=new HttpPost(url);  
      
        HttpClient client = null;
        int ret=-1;
        try {
        	
        	httpPost.setEntity(postEntity);
        	
        	client=new DefaultHttpClient();
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
			
			DefaultHttpRequestRetryHandler kk=new DefaultHttpRequestRetryHandler(0,false);
			((AbstractHttpClient)client).setHttpRequestRetryHandler(kk);
			HttpResponse response=client.execute(httpPost);
			
			ret=response.getStatusLine().getStatusCode();
			if(client!=null&&client.getConnectionManager()!=null)
				client.getConnectionManager().shutdown();
			if(ret==200) 
			{
				 
				 
				return 0;
			}
			else
				return -1;
	
			
			} catch (UnsupportedEncodingException e) {
					
					Log.d("lichao",e.toString());
							
			} catch (ClientProtocolException e) {
					
					Log.d("lichao",e.toString());
					
			} catch (IOException e) {
				Log.d("lichao",e.toString());
					
			
			}finally{
				 if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
			}
			return -1;
		
		
	}
	private String getUploadMsgReqParam(int to_user_id, String from_nickname,
			String msg) {
		
		try {
			JSONObject js=new JSONObject();
						
			js.put("to_user_id", to_user_id);
			
			js.put("from_nickname", URLEncoder.encode(from_nickname, "UTF-8"));
			js.put("msg", URLEncoder.encode(msg,"UTF-8"));
			
			DesUility du=new DesUility("kajdkd");
             	
			return du.encrypt(js.toString());
			
		} catch (JSONException e) {
			
			
		} catch (Exception e) {

		}
		
		return null;
	
	}
	public  String GetUserWeibo(Context context,int user_id,int readed_id,String nickname,boolean isTop)
	{
		
		
		String url=((DBUility)context.getApplicationContext()).server+"/upload/getuserweibo";
		MultipartEntity postEntity = new MultipartEntity();
		
		
		ContentBody params=null;
		try {
			
			params=new StringBody(getGetUserweiboReqParam(user_id,readed_id,nickname,isTop));
			} catch (UnsupportedEncodingException e)
			{
				return "error_data";
			}
		
		postEntity.addPart("params", params);
		
		
		HttpPost httpPost=new HttpPost(url);  
      
        HttpClient client = null;
        int ret=-1;
        try {
        	
        	httpPost.setEntity(postEntity);
        	
        	client=new DefaultHttpClient();
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
			
			DefaultHttpRequestRetryHandler kk=new DefaultHttpRequestRetryHandler(0,false);
			((AbstractHttpClient)client).setHttpRequestRetryHandler(kk);
			HttpResponse response=client.execute(httpPost);
			
			ret=response.getStatusLine().getStatusCode();
			if(ret!=200) 
			{
				 if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
				 
				return "error_data";
			}
			 HttpEntity entity=response.getEntity();  
			 String ret_str=EntityUtils.toString(entity, HTTP.UTF_8);
			 if(client!=null&&client.getConnectionManager()!=null)
					client.getConnectionManager().shutdown();
			return ret_str;
			
			} catch (UnsupportedEncodingException e) {
					
					Log.d("lichao",e.toString());
							
			} catch (ClientProtocolException e) {
					
					Log.d("lichao",e.toString());
					
			} catch (IOException e) {
				Log.d("lichao",e.toString());
					
			
			}finally{
				 if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
			}
			return "error_data";
			
			}
	
	private String getGetUserweiboReqParam(int user_id, int readed_id,
			String nickname, boolean isTop) {
		try {
			JSONObject js=new JSONObject();
						
			js.put("user_id", user_id);
			js.put("readed_id", readed_id);
			js.put("nickname", URLEncoder.encode(nickname, "UTF-8"));
			js.put("isTop", isTop);
			
			DesUility du=new DesUility("kajdkd");
             	
			return du.encrypt(js.toString());
			
		} catch (JSONException e) {
			
			
		} catch (Exception e) {

		}
		
		return null;
	
	}

	public  String GetCommentInfo(Context context,long weibo_id,int readed_id,String type,boolean isTop)
	{
		
		
		String url=((DBUility)context.getApplicationContext()).server+"/upload/getcomment";
		MultipartEntity postEntity = new MultipartEntity();
		
		
		ContentBody params=null;
		try {
			
			params=new StringBody(getGetCommentReqParam(weibo_id,readed_id,type,isTop));
			} catch (UnsupportedEncodingException e)
			{
				return "error_data";
			}
		
		postEntity.addPart("params", params);
		
		
		HttpPost httpPost=new HttpPost(url);  
      
        HttpClient client = null;
        int ret=-1;
        try {
        	
        	httpPost.setEntity(postEntity);
        	
        	client=new DefaultHttpClient();
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
			
			DefaultHttpRequestRetryHandler kk=new DefaultHttpRequestRetryHandler(0,false);
			((AbstractHttpClient)client).setHttpRequestRetryHandler(kk);
			HttpResponse response=client.execute(httpPost);
			
			ret=response.getStatusLine().getStatusCode();
			if(ret!=200) 
			{
				 if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
				return "error_data";
			}
			 HttpEntity entity=response.getEntity();  
			 String ret_str=EntityUtils.toString(entity, HTTP.UTF_8);
			 if(client!=null&&client.getConnectionManager()!=null)
					client.getConnectionManager().shutdown();
			return ret_str;
			
			} catch (UnsupportedEncodingException e) {
					
					Log.d("lichao",e.toString());
							
			} catch (ClientProtocolException e) {
					
					Log.d("lichao",e.toString());
					
			} catch (IOException e) {
				Log.d("lichao",e.toString());
					
			
			}finally{
				 if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
			}
			return "error_data";
			
			}
	
	private String getGetCommentReqParam(long weibo_id, int readed_id,String type,boolean isTop) {
		
		try {
			JSONObject js=new JSONObject();
						
			js.put("weibo_id", weibo_id);
			js.put("readed_id", readed_id);
			js.put("type", type);
			js.put("isTop", isTop);
			
			DesUility du=new DesUility("kajdkd");
             	
			return du.encrypt(js.toString());
			
		} catch (JSONException e) {
			
			
		} catch (Exception e) {

		}
		
		return null;
	}

	public  String GetNewWeibo(Context context,String nickname,int readed_id,int type)
	{
		
		
		String url=((DBUility)context.getApplicationContext()).server+"/upload/getnewweibo";
		MultipartEntity postEntity = new MultipartEntity();
		
		
		ContentBody params=null;
		try {
			
			params=new StringBody(getGetNewWeiboReqParam(nickname,readed_id,type));
			} catch (UnsupportedEncodingException e)
			{
				return "error_data";
			}
		
		postEntity.addPart("params", params);
		
		
		HttpPost httpPost=new HttpPost(url);  
      
        HttpClient client = null;
        int ret=-1;
        try {
        	
        	httpPost.setEntity(postEntity);
        	
        	client=new DefaultHttpClient();
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
			//client.getParams().setParameter(HttpRequestParams , 20000);
			DefaultHttpRequestRetryHandler kk=new DefaultHttpRequestRetryHandler(0,false);
			((AbstractHttpClient)client).setHttpRequestRetryHandler(kk);
			HttpResponse response=client.execute(httpPost);
			
			ret=response.getStatusLine().getStatusCode();
			
			//Log.d("lichao","ret is "+ret);
			if(ret!=200) 
			{
				 if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
				return "error_data";
			}
			 HttpEntity entity=response.getEntity();  
			 String ret_str=EntityUtils.toString(entity, HTTP.UTF_8);
			 if(client!=null&&client.getConnectionManager()!=null)
					client.getConnectionManager().shutdown();
			return ret_str;
			
			} catch (UnsupportedEncodingException e) {
					
					Log.d("lichao",e.toString());
							
			} catch (ClientProtocolException e) {
					
					Log.d("lichao",e.toString());
					
			} catch (IOException e) {
				Log.d("lichao",e.toString());
					
			
			}finally{
				 if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
			}
			return "error_data";
			
			}
	
	
	public static int AddScore(Context context,String nickname,String type)
	{
		String url=((DBUility)context.getApplicationContext()).server+"/addscore";
		
		MultipartEntity postEntity = new MultipartEntity();
		
		
		ContentBody params=null;
		try {
			
			params=new StringBody(getAddScoreReqParam(nickname,type));
			} catch (UnsupportedEncodingException e)
			{
				return -1;
			}
		
		postEntity.addPart("params", params);
		
		
		int ret=-1;
		
		HttpPost httpPost=new HttpPost(url);  
      
        HttpClient client = null;
        try {
			httpPost.setEntity(postEntity);
	
			
		    
		    
			client=new DefaultHttpClient();
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
			//client.getParams().setParameter(HttpRequestParams , 20000);
			DefaultHttpRequestRetryHandler kk=new DefaultHttpRequestRetryHandler(0,false);
			((AbstractHttpClient)client).setHttpRequestRetryHandler(kk);
			HttpResponse response=client.execute(httpPost);
			
			ret=response.getStatusLine().getStatusCode();
			
			
				 if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
			
			
			return ret;
			
			} catch (UnsupportedEncodingException e) {
					
					Log.d("lichao",e.toString());
							
			} catch (ClientProtocolException e) {
					
					Log.d("lichao",e.toString());
					
			} catch (IOException e) {
				Log.d("lichao",e.toString());
					
			
			}finally{
				 if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
			}
			return -1;
			
			}
	
	public static String GetUserGrade(Context context,String nickname)
	{
		String url=((DBUility)context.getApplicationContext()).server+"/getinfo";
		
		MultipartEntity postEntity = new MultipartEntity();
		
		
		ContentBody nicknameBody=null;
		try {
			
				nicknameBody=new StringBody(URLEncoder.encode( nickname, "UTF-8"));
			} catch (UnsupportedEncodingException e)
			{
				return null;
			}
		
		postEntity.addPart("nickname1", nicknameBody);
		
		
		int ret=-1;
		
		HttpPost httpPost=new HttpPost(url);  
      
        HttpClient client = null;
        try {
			httpPost.setEntity(postEntity);
	
			
		    
		    
			client=new DefaultHttpClient();
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
			//client.getParams().setParameter(HttpRequestParams , 20000);
			DefaultHttpRequestRetryHandler kk=new DefaultHttpRequestRetryHandler(0,false);
			((AbstractHttpClient)client).setHttpRequestRetryHandler(kk);
			HttpResponse response=client.execute(httpPost);
			
			ret=response.getStatusLine().getStatusCode();
			if(ret!=200) 
			{
				 if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
				return null;
			}
			 HttpEntity entity=response.getEntity();  
			 String ret_str=EntityUtils.toString(entity, HTTP.UTF_8);
			 if(client!=null&&client.getConnectionManager()!=null)
					client.getConnectionManager().shutdown();
			return ret_str;
			
			} catch (UnsupportedEncodingException e) {
					
					Log.d("lichao",e.toString());
							
			} catch (ClientProtocolException e) {
					
					Log.d("lichao",e.toString());
					
			} catch (IOException e) {
				Log.d("lichao",e.toString());
					
			
			}finally{
				 if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
			}
			return null;
			
			}
	
	
	public static int ChangeUser(Context context,String type,String touxiang_dir,String nickname,String username)
	{
		String url=((DBUility)context.getApplicationContext()).server+"/change";
		
		MultipartEntity postEntity = new MultipartEntity();
		
		ContentBody usernameBody = null;
		ContentBody typeBody = null;
		ContentBody nicknameBody=null;
		try {
			usernameBody = new StringBody(username);
			typeBody = new StringBody(type);
			if(nickname!=null)
				nicknameBody=new StringBody(URLEncoder.encode( nickname, "UTF-8"));
			} catch (UnsupportedEncodingException e)
			{
				return -1;
			}
		
		
		if(touxiang_dir!=null)
		{
			File file = new File(touxiang_dir);
		
			if(!file.exists())
			{
				//Log.d("lichao","loginToServer file not exist!");
				return -1;
			}
			ContentBody cbFileData = new FileBody( file, "image/jpg" );
			postEntity.addPart("touxiang", cbFileData);
		}
		
		postEntity.addPart("username", usernameBody);
		postEntity.addPart("type", typeBody);
		if(nicknameBody!=null)
			postEntity.addPart("nickname1", nicknameBody);
		int ret=-1;
		
		HttpPost httpPost=new HttpPost(url);  
      
        HttpClient client = null;
        try {
			httpPost.setEntity(postEntity);
	
			
		    
		    
			client=new DefaultHttpClient();
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
			//client.getParams().setParameter(HttpRequestParams , 20000);
			DefaultHttpRequestRetryHandler kk=new DefaultHttpRequestRetryHandler(0,false);
			((AbstractHttpClient)client).setHttpRequestRetryHandler(kk);
			HttpResponse response=client.execute(httpPost);
			
			ret=response.getStatusLine().getStatusCode();
			 
			} catch (UnsupportedEncodingException e) {
					ret=-1;
					Log.d("lichao",e.toString());
							
			} catch (ClientProtocolException e) {
					ret=-1;
					Log.d("lichao",e.toString());
					
			} catch (IOException e) {
				Log.d("lichao",e.toString());
					ret=-1;
			
			}finally{
				 if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
			}
			return ret;
			
			}
	
	public static int RegisterUser(Context context,UserInfo user)
	{
		String url=((DBUility)context.getApplicationContext()).server+"/register";
		String param=getLoginRequestParam(user);
		if(param==null) return -1;
		MultipartEntity postEntity = new MultipartEntity();
		
		ContentBody cbContent = null;
		try {
			cbContent = new StringBody(param);
		
			} catch (UnsupportedEncodingException e)
			{
				return -1;
			}
		
		
		
		if(user.touxiang_url==null) 
		{
			//postEntity.addPart("touxiang", null);
		}
		else
		{
			File file = new File(user.touxiang_url);
			ContentBody cbFileData = new FileBody( file, "image/jpg" );
			postEntity.addPart("touxiang", cbFileData);
		}
		
		
		postEntity.addPart("params", cbContent);
		
		int ret=-1;
		
		HttpPost httpPost=new HttpPost(url);  
      
        HttpClient client = null;
        try {
			httpPost.setEntity(postEntity);
	
			
		    
		    
			client=new DefaultHttpClient();
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
			//client.getParams().setParameter(HttpRequestParams , 20000);
			DefaultHttpRequestRetryHandler kk=new DefaultHttpRequestRetryHandler(0,false);
			((AbstractHttpClient)client).setHttpRequestRetryHandler(kk);
			HttpResponse response=client.execute(httpPost);
			
			ret=response.getStatusLine().getStatusCode();
			 
			} catch (UnsupportedEncodingException e) {
					ret=-1;
					Log.d("lichao",e.toString());
							
			} catch (ClientProtocolException e) {
					ret=-1;
					Log.d("lichao",e.toString());
					
			} catch (IOException e) {
				Log.d("lichao",e.toString());
					ret=-1;
			
			}finally{
				 if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
			}
			return ret;
			
			}
	
	public static int LoginToMyServer(Context context,UserInfo user)
	{
		String url=((DBUility)context.getApplicationContext()).server+"/login";
		String param=getLoginRequestParam(user);
		if(param==null) return -1;
		MultipartEntity postEntity = new MultipartEntity();
		
		ContentBody cbContent = null;
		try {
			cbContent = new StringBody(param);
		
			} catch (UnsupportedEncodingException e)
			{
				return -1;
			}
		
		
		
		if(user.touxiang_url!=null&&!user.touxiang_url.equals("NOTOUXIANG"))
		{
			File file = new File(user.touxiang_url);
			ContentBody cbFileData = new FileBody( file, "image/jpg" );
		
			postEntity.addPart("touxiang", cbFileData);
		}
		
			postEntity.addPart("params", cbContent);
		
		int ret=-1;
		
		HttpPost httpPost=new HttpPost(url);  
       /* List<NameValuePair>list=new ArrayList<NameValuePair>();  
        try {
        	DesUility du=new DesUility("kajdkd");
        	
			list.add(new BasicNameValuePair("data", du.encrypt(param)));
		
		} catch (Exception e1) {
			
			return -1;
		}  */
        HttpClient client = null;
        try {
			httpPost.setEntity(postEntity);
	
			
		    
		    
			client=new DefaultHttpClient();
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
			//client.getParams().setParameter(HttpRequestParams , 20000);
			DefaultHttpRequestRetryHandler kk=new DefaultHttpRequestRetryHandler(0,false);
			((AbstractHttpClient)client).setHttpRequestRetryHandler(kk);
			HttpResponse response=client.execute(httpPost);
			
			ret=response.getStatusLine().getStatusCode();
			 
			} catch (UnsupportedEncodingException e) {
					ret=-1;
					Log.d("lichao",e.toString());
							
			} catch (ClientProtocolException e) {
					ret=-1;
					Log.d("lichao",e.toString());
					
			} catch (IOException e) {
				Log.d("lichao",e.toString());
					ret=-1;
			
			}finally{
				 if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
			}
			return ret;
			
			}
	public static UserInfo LoginToMyServer2(Context context,UserInfo user)
	{
		String url=((DBUility)context.getApplicationContext()).server+"/login";
		String param=getLoginRequestParam(user);
		if(param==null) return null;
		MultipartEntity postEntity = new MultipartEntity();
		
		ContentBody cbContent = null;
		try {
			cbContent = new StringBody(param);
		
			} catch (UnsupportedEncodingException e)
			{
				return null;
			}
		
		
		
		postEntity.addPart("params", cbContent);
		
		int ret=-1;
		
		HttpPost httpPost=new HttpPost(url);  
       /* List<NameValuePair>list=new ArrayList<NameValuePair>();  
        try {
        	DesUility du=new DesUility("kajdkd");
        	
			list.add(new BasicNameValuePair("data", du.encrypt(param)));
		
		} catch (Exception e1) {
			
			return -1;
		}  */
        HttpClient client = null;
        try {
			httpPost.setEntity(postEntity);
	
			
		    
		    
			client=new DefaultHttpClient();
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
			//client.getParams().setParameter(HttpRequestParams , 20000);
			DefaultHttpRequestRetryHandler kk=new DefaultHttpRequestRetryHandler(0,false);
			((AbstractHttpClient)client).setHttpRequestRetryHandler(kk);
			HttpResponse response=client.execute(httpPost);
			
			ret=response.getStatusLine().getStatusCode();
			
			if(ret==200) 
			{
				 HttpEntity entity=response.getEntity();  
			
				 
				 JSONObject retjson=new JSONObject();
				 
				 retjson=new JSONObject(EntityUtils.toString(entity, HTTP.UTF_8));
				 
				 
				 String sd_dir=ScreenShoot.getSDDir(context);
				 UserInfo user_ret=new UserInfo();
				 user_ret.username=user.username;
				 user_ret.nickname=URLDecoder.decode(retjson.getString("nickname"), "UTF-8");;
				 
				 
				 user_ret.user_type="CarWeibo";
				 
				 if(sd_dir!=null)
				 {
					 user_ret.touxiang_url=sd_dir+"/weizhangquery/pic/touxiang/carweibo_touxiang_small.png";
					 String ser_url=((DBUility)context.getApplicationContext()).webapps+"/pic/touxiang/"+retjson.getString("touxiang_url");
				 	
					 Bitmap bm=getBitmap(ser_url);
					 if(bm!=null)
						  ScreenShoot.savePic(getBitmap(ser_url), sd_dir+"/weizhangquery/pic/touxiang", user_ret.touxiang_url , 100);
				 }
				 else
					 user_ret.touxiang_url="NOTOUXIANG";
				 
				 if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
                 return user_ret;
			}
			UserInfo user_ret=new UserInfo();
			switch(ret)
			{
				
				case 201:
					user_ret.username="error201";user_ret.nickname="error201";
					break;
				case 204:
					user_ret.username="error204";user_ret.nickname="error204";
					break;
				case 205:
					user_ret.username="error205";user_ret.nickname="error205";
					break;
			}
			if(client!=null&&client.getConnectionManager()!=null)
				client.getConnectionManager().shutdown();
			return user_ret;
			 
			} catch (UnsupportedEncodingException e) {
			
							
			} catch (ClientProtocolException e) {
					
					
			} catch (IOException e) {
				
			
			}catch (ParseException e) {
				
			} catch (JSONException e) {
				
			} finally{
				 if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
			}
			return null;
			
			}

	private static String getLoginRequestParam(UserInfo user) {
		try {
			JSONObject js=new JSONObject();
			js.put("username", URLEncoder.encode( user.username, "UTF-8"));
			js.put("password", user.password);
			
			js.put("nickname",URLEncoder.encode( user.nickname, "UTF-8"));
			
			js.put("usertype", user.user_type);
			js.put("imei", user.imei);
			
			
			DesUility du=new DesUility("kajdkd");
             	
			return du.encrypt(js.toString());
			
		} catch (JSONException e) {
			
			
		} catch (Exception e) {

		}
	//	Log.d("lichao","get error");
		return null;
		
	}
	
	private static String getGetNewWeiboReqParam(String nickname,int readed_id,int type) {
		try {
			JSONObject js=new JSONObject();
						
			js.put("nickname",URLEncoder.encode(nickname, "UTF-8"));
			
			js.put("readed_id", readed_id);
			js.put("type", type);
			
			DesUility du=new DesUility("kajdkd");
             	
			return du.encrypt(js.toString());
			
		} catch (JSONException e) {
			
			
		} catch (Exception e) {

		}
		
		return null;
		
	}
	private static String getAddScoreReqParam(String nickname,String type) {
		try {
			JSONObject js=new JSONObject();
						
			js.put("nickname",URLEncoder.encode(nickname, "UTF-8"));
			
			js.put("type", type);
			
			js.put("time", System.currentTimeMillis()/1000);
			
			DesUility du=new DesUility("kajdkd");
             	
			return du.encrypt(js.toString());
			
		} catch (JSONException e) {
			
			
		} catch (Exception e) {

		}
		//Log.d("lichao","get error");
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
	
	private String getServerString(String url,MultipartEntity postEntity)
	{
		HttpPost httpPost=new HttpPost(url);  
	      
        HttpClient client = null;
        int ret=-1;
        try {
        	
        	if(postEntity!=null)
        	{
        		httpPost.setEntity(postEntity);
        	}
        	
        	client=new DefaultHttpClient();
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
			
			DefaultHttpRequestRetryHandler kk=new DefaultHttpRequestRetryHandler(0,false);
			((AbstractHttpClient)client).setHttpRequestRetryHandler(kk);
			HttpResponse response=client.execute(httpPost);
			
			ret=response.getStatusLine().getStatusCode();
			
			Log.d("lichao","Login ToServer getServerString ret is "+ret);
			if(ret!=200) 
			{
				 if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
				return "ErrorCode"+ret;
				
			}
			
			 HttpEntity entity=response.getEntity();  
			 String ret_str=EntityUtils.toString(entity, HTTP.UTF_8);
			 if(client!=null&&client.getConnectionManager()!=null)
					client.getConnectionManager().shutdown();
			return ret_str;
			
			} catch (UnsupportedEncodingException e) {
					
					Log.d("lichao","LoginToServer getServerString:"+e.toString());
							
			} catch (ClientProtocolException e) {
					
					Log.d("lichao","LoginToServer getServerString:"+e.toString());
					
			} catch (IOException e) {
				Log.d("lichao","LoginToServer getServerString:"+e.toString());
					
			
			}finally{
				 if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
			}
			return "error_data";
	
	}


	


}
