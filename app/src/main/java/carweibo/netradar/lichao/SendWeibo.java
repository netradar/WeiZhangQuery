package carweibo.netradar.lichao;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;



public class SendWeibo {
	static Context context;
	static String ad_msg="分享一个来自《西安车友圈》的帖子，见图！你也来车友圈看看吧？软件下载地址：http://s-93271.gotocdn.com:8080/pic/download/weizhangquery.apk";
	
	public void sendVenderComment(final Context conn,final String nickname,final String content,final long vender_id,final boolean isBadComment)
	{
		context=conn;
	
		final Handler handler=new Handler()
		{
			 public void handleMessage(Message message)
			 {
				 
				 switch(message.what)
				 {
				 case 200:
					 sendNotify(0,"评论发送成功！");
					 clearNotify(0);
					 break;
				
				 default:
					 sendNotify(0,"评论失败：网络或者数据异常！");
					 clearNotify(0);
					 break;
				     
				 }
				 
				
			
				 if(message.what==200)
				 {
					  Intent intent = new Intent("netradar.bd.vender"); 
					  intent.putExtra("type", 1);
					//  Log.d("lichao","vender id is "+vender_id);
	                  intent.putExtra("vender_id", vender_id);
	                  intent.putExtra("isBadComment", isBadComment);
	                  //send Broadcast 
	                  conn.sendBroadcast(intent); 
				 }
			 }
		};
		
		new Thread(){

			@Override
			public void run() {
				
				int ret=-1;
				String url=((DBUility)context.getApplicationContext()).server+"/upload/newvendercomment";
				String param=getNewVenderCommentRequestParam(nickname,content,vender_id,isBadComment);
				if(param==null) 
				{
					Message message = handler.obtainMessage(-1);
					handler.sendMessage(message);
					return;
				}
				MultipartEntity postEntity = new MultipartEntity();
				
				ContentBody cbContent = null;
				try {
					cbContent = new StringBody(param);
				
					} catch (UnsupportedEncodingException e)
					{
						Message message = handler.obtainMessage(-1);
						handler.sendMessage(message);
						return;
					}
				
				sendNotify(0,"评论发送中...");
				 clearNotify(0);
				
				
				postEntity.addPart("params", cbContent);
				
				
				HttpPost httpPost=new HttpPost(url);  
		      
		        HttpClient client = null;
		        try {
					httpPost.setEntity(postEntity);
						    
					client=new DefaultHttpClient();
					client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
					client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
					//client.getParams().setParameter(HttpRequestParams , 20000);
					DefaultHttpRequestRetryHandler kk=new DefaultHttpRequestRetryHandler(0,false);
					((AbstractHttpClient)client).setHttpRequestRetryHandler(kk);
					HttpResponse response=client.execute(httpPost);
					
					ret=response.getStatusLine().getStatusCode();
					
					if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
					
					Message message = handler.obtainMessage(ret);
					handler.sendMessage(message);
					return;
					
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
					
		        Message message = handler.obtainMessage(ret);
				handler.sendMessage(message);
					
						
			}

			private String getNewVenderCommentRequestParam(String nickname,String content, long vender_id,boolean isBadComment) {
				try {
					JSONObject js=new JSONObject();
					js.put("nickname", URLEncoder.encode( nickname, "UTF-8"));
				
					js.put("comment",URLEncoder.encode( content, "UTF-8"));
					js.put("vender_id", vender_id);
					js.put("is_bad_comment", isBadComment);
			
					DesUility du=new DesUility("kajdkd");
		             	
					return du.encrypt(js.toString());
					
				} catch (JSONException e) {
					
					
				} catch (Exception e) {

				}
				
				return null;
				
			}
			
			
		}.start();
	}
	public static void sendVender(final Context conn, final String curUser,
			final String name, final String addr, final String comment,
			final ArrayList<ImageInfo> bmpFile_list, final int draft_id) {
		
		context=conn;
		final Handler handler=new Handler()
		{
			 public void handleMessage(Message message)
			 {
				 String hint = null;
				 switch(message.what)
				 {
				 case 200:
					 sendNotify(0,"上传商户信息成功！");
							
					 clearNotify(0);
					 
					 if(draft_id!=-1)
						 DraftManager.deleteDraftVender(conn.getApplicationContext(), draft_id);
					 break;
				/* case 201:
				
					 break;
				 case 202:
			
					 hint="发送失败：用户不存在！";
				
					 break;
				 case 203:
			
					 hint="发送失败：该用户已被禁止发帖！";
					 if(draft_id==-1)
						 saveVenderToDB(conn,name,addr,comment,bmpFile_list);
					 break;
				 case 204:
						
					 break;
				 case 206:
					 break;*/
				 default:
					 sendNotify(0,"网络或者数据异常，评论失败！");
						
					 clearNotify(0);
					 hint="网络或者数据异常，发送失败，已存入草稿箱！";
					 if(draft_id==-1)
						 saveVenderToDB(conn,name,addr,comment,bmpFile_list);
					 break;
				     
				 }
				 
				 if(message.what!=200)
				 {
					  Intent intent = new Intent("netradar.bd.vender"); 
					  intent.putExtra("type", 1);
	                  intent.putExtra("hint",hint); 
	                  conn.sendBroadcast(intent); 
				 }
			
			 }

			private void saveVenderToDB(Context conn, String name,
					String addr, String comment, ArrayList<ImageInfo> bmpFile_list) {
				
				JSONArray ja=new JSONArray();
				
				for(ImageInfo info:bmpFile_list)
				{
					JSONObject json=new JSONObject();
					try {
						json.put("sfile",info.sfile_url);
						json.put("file", info.file_url);
						json.put("size", info.size);
						ja.put(json);
					} catch (JSONException e) {
						Log.d("lichao","SendWeibo json error:"+e.toString());
					}
				}
				
				DraftManager.addDraftVender(conn.getApplicationContext(), null, name, addr,comment,bmpFile_list, ja.toString());
				
			}
		};
		
		new Thread(){

			@Override
			public void run() {
				
				int ret=-1;
				String url=((DBUility)context.getApplicationContext()).server+"/upload/newvender";
				String param=getNewVenderRequestParam(curUser,name,addr,comment);
				if(param==null) 
				{
					Message message = handler.obtainMessage(-1);
					handler.sendMessage(message);
					return;
				}
				MultipartEntity postEntity = new MultipartEntity();
				
				ContentBody cbContent = null;
				try {
					cbContent = new StringBody(param);
				
					} catch (UnsupportedEncodingException e)
					{
						Message message = handler.obtainMessage(-1);
						handler.sendMessage(message);
						return;
					}
				
				sendNotify(0,"信息上传中...");
				 clearNotify(0);
				if(bmpFile_list!=null)
				{
				//	Log.d("lichao",fileList.toString());
					for(ImageInfo img:bmpFile_list)
					{
						File file = new File(img.file_url);
						File file_s=new File(img.sfile_url);
						if(file.exists()&&file_s.exists())
						{
							ContentBody cbFileData = new FileBody( file, "image/jpg" );
							postEntity.addPart("filedata", cbFileData);
							cbFileData = new FileBody( file_s, "image/jpg" );
							postEntity.addPart("filedata_s", cbFileData);
							try {
								postEntity.addPart("sizelist", new StringBody(img.size));
							} catch (UnsupportedEncodingException e) {
								
								//postEntity.addPart("sizelist", new StringBody("100*100"));
							}
						}
					}
				}
				
				
				postEntity.addPart("params", cbContent);
				
				
				HttpPost httpPost=new HttpPost(url);  
		      
		        HttpClient client = null;
		        try {
					httpPost.setEntity(postEntity);
						    
					client=new DefaultHttpClient();
					client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
					client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 15000);
					//client.getParams().setParameter(HttpRequestParams , 20000);
					DefaultHttpRequestRetryHandler kk=new DefaultHttpRequestRetryHandler(0,false);
					((AbstractHttpClient)client).setHttpRequestRetryHandler(kk);
					HttpResponse response=client.execute(httpPost);
					
					ret=response.getStatusLine().getStatusCode();
					
					if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
					
					Message message = handler.obtainMessage(ret);
					handler.sendMessage(message);
					return;
					
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
					
		        Message message = handler.obtainMessage(ret);
				handler.sendMessage(message);
					
						
			}

			private String getNewVenderRequestParam(String nickname,String name, String addr,
					String comment) {
				
				try {
					JSONObject js=new JSONObject();
					js.put("nickname", URLEncoder.encode( nickname, "UTF-8"));
					js.put("name", URLEncoder.encode( name, "UTF-8"));
					js.put("addr",URLEncoder.encode( addr, "UTF-8"));
					js.put("comment",URLEncoder.encode( comment, "UTF-8"));
					
					DesUility du=new DesUility("kajdkd");
		             	
					return du.encrypt(js.toString());
					
				} catch (JSONException e) {
					
					
				} catch (Exception e) {

				}
				Log.d("lichao","getNewVenderRequestParam error");
				return null;
			}

			
			
			
		}.start();
	}
	public void deleteWeibo(final Context conn,final String nickname,final long weibo_id,final boolean isTop)
	{
		context=conn;
		final Handler handler=new Handler()
		{
			 public void handleMessage(Message message)
			 {
				 String hint = null;
				 switch(message.what)
				 {
				 case 200:
					 sendNotify(0,"删除成功！");
					 clearNotify(0);
					 break;
						
				 default:
				//	 sendNotify(0,"发送失败：网络或者数据异常，发送失败！");
					 hint="删除帖子失败：网络或者数据异常！";
					 break;
				     
				 }
				 
				 if(message.what!=200)
				 {
					  Intent intent = new Intent("netradar.bd"); 
	                  intent.putExtra("hint",hint); 
	                  intent.putExtra("type", 0);
	                  conn.sendBroadcast(intent); 
				 }
			
			 }
		};
		
		new Thread(){

			@Override
			public void run() {
				
				int ret=-1;
				String url=((DBUility)context.getApplicationContext()).server+"/upload/delete";
				String param=getDeleteRequestParam(nickname,weibo_id,isTop);
				if(param==null) 
				{
					Message message = handler.obtainMessage(-1);
					handler.sendMessage(message);
					return;
				}
				MultipartEntity postEntity = new MultipartEntity();
				
				ContentBody cbContent = null;
				try {
					cbContent = new StringBody(param);
				
					} catch (UnsupportedEncodingException e)
					{
						Message message = handler.obtainMessage(-1);
						handler.sendMessage(message);
						return;
					}
				
				sendNotify(0,"正在删除...");
				 clearNotify(0);
				
				
				postEntity.addPart("params", cbContent);
				
				
				HttpPost httpPost=new HttpPost(url);  
		      
		        HttpClient client = null;
		        try {
					httpPost.setEntity(postEntity);
						    
					client=new DefaultHttpClient();
					client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
					client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
					//client.getParams().setParameter(HttpRequestParams , 20000);
					DefaultHttpRequestRetryHandler kk=new DefaultHttpRequestRetryHandler(0,false);
					((AbstractHttpClient)client).setHttpRequestRetryHandler(kk);
					HttpResponse response=client.execute(httpPost);
					
					ret=response.getStatusLine().getStatusCode();
					
					if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
					
					Message message = handler.obtainMessage(ret);
					handler.sendMessage(message);
					
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
					
		        Message message = handler.obtainMessage(ret);
				handler.sendMessage(message);
					
						
			}

			private String getDeleteRequestParam(String nickname, long weibo_id,boolean isTop) {
				try {
					JSONObject js=new JSONObject();
					js.put("nickname", URLEncoder.encode( nickname, "UTF-8"));
					js.put("weibo_id", weibo_id);
					js.put("isTop", isTop);
			
					DesUility du=new DesUility("kajdkd");
		             	
					return du.encrypt(js.toString());
					
				} catch (JSONException e) {
					
					
				} catch (Exception e) {

				}
				
				return null;
				
			}
			
			
		}.start();
	}
	
	public void sendGood(final Context conn,final String nickname,final long weibo_id,final boolean isTop )
	{
		context=conn;
		final Handler handler=new Handler()
		{
			 public void handleMessage(Message message)
			 {
				 switch(message.what)
				 {
				 case 200:
				
					 sendNotify(0,"点赞成功！");
					 clearNotify(0);
					 break;
				
				case 202:
				//	 sendNotify(0,"发送失败：用户不存在！");
					 
					 sendNotify(0,"点赞失败：用户不存在！");
					 clearNotify(0);
					 break;
				 case 203:
				//	 sendNotify(0,"发送失败：该用已被禁止发帖！");
					 
					 sendNotify(0,"点赞失败：帖子已被删除！");
					 clearNotify(0);
					 break;
				 case 201:
					 Toast.makeText(context, "您已经赞过这个帖子了哦～", Toast.LENGTH_SHORT).show();
					 break;
				 default:
					 Toast.makeText(context, "点赞失败：网络或者数据异常！", Toast.LENGTH_SHORT).show();
					
					 break;
				     
				 }
				 if(message.what==200)
				 {
					  Intent intent = new Intent("netradar.bd"); 
					  intent.putExtra("type", 1);
	                  intent.putExtra("id", weibo_id);
	                  //send Broadcast 
	                  conn.sendBroadcast(intent); 
				 }
				
			 }
		};
		
		new Thread(){

			@Override
			public void run() {
				
				int ret=-1;
				String url=((DBUility)context.getApplicationContext()).server+"/upload/newgood";
				String param=getNewGoodRequestParam(nickname,weibo_id,isTop);
				if(param==null) 
				{
					Message message = handler.obtainMessage(-1);
					handler.sendMessage(message);
					return;
				}
				MultipartEntity postEntity = new MultipartEntity();
				
				ContentBody cbContent = null;
				try {
					cbContent = new StringBody(param);
				
					} catch (UnsupportedEncodingException e)
					{
						Message message = handler.obtainMessage(-1);
						handler.sendMessage(message);
						return;
					}
				
				sendNotify(0,"正在点赞...");
				 clearNotify(0);
				
				
				postEntity.addPart("params", cbContent);
				
				
				HttpPost httpPost=new HttpPost(url);  
		      
		        HttpClient client = null;
		        try {
					httpPost.setEntity(postEntity);
						    
					client=new DefaultHttpClient();
					client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
					client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
					//client.getParams().setParameter(HttpRequestParams , 20000);
					DefaultHttpRequestRetryHandler kk=new DefaultHttpRequestRetryHandler(0,false);
					((AbstractHttpClient)client).setHttpRequestRetryHandler(kk);
					HttpResponse response=client.execute(httpPost);
					
					ret=response.getStatusLine().getStatusCode();
					
					if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
					
					Message message = handler.obtainMessage(ret);
					handler.sendMessage(message);
					return;
					
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
					
		        Message message = handler.obtainMessage(ret);
				handler.sendMessage(message);
					
						
			}

			private String getNewGoodRequestParam(String nickname, long weibo_id,boolean isTop) {
				try {
					JSONObject js=new JSONObject();
					js.put("nickname", URLEncoder.encode( nickname, "UTF-8"));
					js.put("weibo_id", weibo_id);
					js.put("isTop", isTop);
			
					DesUility du=new DesUility("kajdkd");
		             	
					return du.encrypt(js.toString());
					
				} catch (JSONException e) {
					
					
				} catch (Exception e) {

				}
				
				return null;
				
			}
			
			
		}.start();
	}
	public void sendComment(final Context conn,final String nickname,final int to_user_id,final String content,final long weibo_id,final boolean isTop )
	{
		context=conn;
	
		final Handler handler=new Handler()
		{
			 public void handleMessage(Message message)
			 {
				 String hint = null;
				 switch(message.what)
				 {
				 case 200:
					 sendNotify(0,"评论发送成功！");
					 clearNotify(0);
					 break;
				case 201:
				//	 sendNotify(0,"发送失败：用户不存在！");
					 hint="评论失败：该用户不存在！";
					 break;
				 case 202:
				//	 sendNotify(0,"发送失败：该用已被禁止发帖！");
					 hint="评论发送失败：帖子已被删除！";
					 break;
				
				 default:
				//	 sendNotify(0,"发送失败：网络或者数据异常，发送失败！");
					 hint="评论发送失败：网络或者数据异常！";
					 break;
				     
				 }
				 
				 if(message.what!=200)
				 {
					  Intent intent = new Intent("netradar.bd"); 
					  intent.putExtra("type", 0);
	                  intent.putExtra("hint",hint); 
	                 
	                  conn.sendBroadcast(intent); 
				 }
			
				 if(message.what==200)
				 {
					  Intent intent = new Intent("netradar.bd"); 
					  intent.putExtra("type", 3);
	                  intent.putExtra("id", weibo_id);
	                  //send Broadcast 
	                  conn.sendBroadcast(intent); 
				 }
			 }
		};
		
		new Thread(){

			@Override
			public void run() {
				
				int ret=-1;
				String url=((DBUility)context.getApplicationContext()).server+"/upload/newcomment";
				String param=getNewCommentRequestParam(nickname,to_user_id,content,weibo_id,isTop);
				if(param==null) 
				{
					Message message = handler.obtainMessage(-1);
					handler.sendMessage(message);
					return;
				}
				MultipartEntity postEntity = new MultipartEntity();
				
				ContentBody cbContent = null;
				try {
					cbContent = new StringBody(param);
				
					} catch (UnsupportedEncodingException e)
					{
						Message message = handler.obtainMessage(-1);
						handler.sendMessage(message);
						return;
					}
				
				sendNotify(0,"评论发送中...");
				 clearNotify(0);
				
				
				postEntity.addPart("params", cbContent);
				
				
				HttpPost httpPost=new HttpPost(url);  
		      
		        HttpClient client = null;
		        try {
					httpPost.setEntity(postEntity);
						    
					client=new DefaultHttpClient();
					client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
					client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
					//client.getParams().setParameter(HttpRequestParams , 20000);
					DefaultHttpRequestRetryHandler kk=new DefaultHttpRequestRetryHandler(0,false);
					((AbstractHttpClient)client).setHttpRequestRetryHandler(kk);
					HttpResponse response=client.execute(httpPost);
					
					ret=response.getStatusLine().getStatusCode();
					
					if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
					
					Message message = handler.obtainMessage(ret);
					handler.sendMessage(message);
					return;
					
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
					
		        Message message = handler.obtainMessage(ret);
				handler.sendMessage(message);
					
						
			}

			private String getNewCommentRequestParam(String nickname,int to_user_id,String content, long weibo_id,boolean isTop) {
				try {
					JSONObject js=new JSONObject();
					js.put("nickname", URLEncoder.encode( nickname, "UTF-8"));
					js.put("to_user_id", to_user_id);
					js.put("content",URLEncoder.encode( content, "UTF-8"));
					js.put("weibo_id", weibo_id);
					js.put("isTop", isTop);
			
					DesUility du=new DesUility("kajdkd");
		             	
					return du.encrypt(js.toString());
					
				} catch (JSONException e) {
					
					
				} catch (Exception e) {

				}
				
				return null;
				
			}
			
			
		}.start();
	}
	
	public static void sendCarWeibo(final Context conn,final String nickname,final String content,final ArrayList<ImageInfo> fileList,final int grade,final int readed_id,final int draft_id)
	{
		context=conn;
		final Handler handler=new Handler()
		{
			 public void handleMessage(Message message)
			 {
				 String hint = null;
				 switch(message.what)
				 {
				 case 200:
					 sendNotify(0,"发帖成功！");
					 if(grade==0)
						 UserManager.AddUserScore(context.getApplicationContext(), nickname,1);
					 if(grade==1)
						 UserManager.AddUserScore(context.getApplicationContext(), nickname,-50);
					 if(grade==2)
						 UserManager.AddUserScore(context.getApplicationContext(), nickname,-100);
						
					 clearNotify(0);
					 
					 if(draft_id!=-1)
						 DraftManager.deleteDraft(conn.getApplicationContext(), draft_id);
					 break;
				 case 201:
				//	 sendNotify(0,"发送失败：帖子内容重复！");
					 hint="发送失败：帖子内容重复！";
					 if(draft_id==-1)
					 saveWeiboToDB(conn,nickname,content,fileList,grade);
					 break;
				 case 202:
				//	 sendNotify(0,"发送失败：用户不存在！");
					 hint="发送失败：用户不存在！";
					 if(draft_id==-1)
					 saveWeiboToDB(conn,nickname,content,fileList,grade);
					 break;
				 case 203:
				//	 sendNotify(0,"发送失败：该用已被禁止发帖！");
					 hint="发送失败：该用户已被禁止发帖！";
					 if(draft_id==-1)
					 saveWeiboToDB(conn,nickname,content,fileList,grade);
					 break;
				 case 204:
				//	 sendNotify(0,"发帖成功！（积分不足转为普通帖子）");
					 hint="发帖成功！（积分不足转为普通帖子）";
					 UserManager.AddUserScore(context.getApplicationContext(), nickname,1);
					 if(draft_id!=-1)
						 DraftManager.deleteDraft(conn.getApplicationContext(), draft_id);
						
					 break;
				 case 206:
				//	 sendNotify(0,"发帖成功！（今日置顶帖已满，转为普通帖）");
					 hint="发帖成功！（今日置顶帖已满，转为普通帖）";
					 UserManager.AddUserScore(context.getApplicationContext(), nickname,1);
					 if(draft_id!=-1)
						 DraftManager.deleteDraft(conn.getApplicationContext(), draft_id);
						
					 break;
				 default:
				//	 sendNotify(0,"发送失败：网络或者数据异常，发送失败！");
					 hint="网络或者数据异常，发送失败，已存入草稿箱！";
					 if(draft_id==-1)
						 saveWeiboToDB(conn,nickname,content,fileList,grade);
					 break;
				     
				 }
				 
				 if(message.what!=200)
				 {
					  Intent intent = new Intent("netradar.bd"); 
					  intent.putExtra("type", 0);
	                  intent.putExtra("hint",hint); 
	                  //send Broadcast 
	                  conn.sendBroadcast(intent); 
				 }
			
			 }

			private void saveWeiboToDB(Context conn, String nickname,
					String content, ArrayList<ImageInfo> fileList, int grade) {
				
				JSONArray ja=new JSONArray();
				
				for(ImageInfo info:fileList)
				{
					JSONObject json=new JSONObject();
					try {
						json.put("sfile",info.sfile_url);
						json.put("file", info.file_url);
						json.put("size", info.size);
						ja.put(json);
					} catch (JSONException e) {
						Log.d("lichao","SendWeibo json error:"+e.toString());
					}
				}
				
				DraftManager.addDraft(conn.getApplicationContext(), null, nickname, content, grade, ja.toString());
				
			}
		};
		
		new Thread(){

			@Override
			public void run() {
				
				int ret=-1;
				String url=((DBUility)context.getApplicationContext()).server+"/upload/newweibo";
				String param=getNewWeiboRequestParam(nickname,content,grade,readed_id);
				if(param==null) 
				{
					Message message = handler.obtainMessage(-1);
					handler.sendMessage(message);
					return;
				}
				MultipartEntity postEntity = new MultipartEntity();
				
				ContentBody cbContent = null;
				try {
					cbContent = new StringBody(param);
				
					} catch (UnsupportedEncodingException e)
					{
						Message message = handler.obtainMessage(-1);
						handler.sendMessage(message);
						return;
					}
				
				sendNotify(0,"帖子发送中...");
				 clearNotify(0);
				if(fileList!=null)
				{
				//	Log.d("lichao",fileList.toString());
					for(ImageInfo img:fileList)
					{
						File file = new File(img.file_url);
						File file_s=new File(img.sfile_url);
						if(file.exists()&&file_s.exists())
						{
							ContentBody cbFileData = new FileBody( file, "image/jpg" );
							postEntity.addPart("filedata", cbFileData);
							cbFileData = new FileBody( file_s, "image/jpg" );
							postEntity.addPart("filedata_s", cbFileData);
							try {
								postEntity.addPart("sizelist", new StringBody(img.size));
							} catch (UnsupportedEncodingException e) {
								
								//postEntity.addPart("sizelist", new StringBody("100*100"));
							}
						}
					}
				}
				
				
				postEntity.addPart("params", cbContent);
				
				
				HttpPost httpPost=new HttpPost(url);  
		      
		        HttpClient client = null;
		        try {
					httpPost.setEntity(postEntity);
						    
					client=new DefaultHttpClient();
					client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
					client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 15000);
					//client.getParams().setParameter(HttpRequestParams , 20000);
					DefaultHttpRequestRetryHandler kk=new DefaultHttpRequestRetryHandler(0,false);
					((AbstractHttpClient)client).setHttpRequestRetryHandler(kk);
					HttpResponse response=client.execute(httpPost);
					
					ret=response.getStatusLine().getStatusCode();
					
					if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
					
					Message message = handler.obtainMessage(ret);
					handler.sendMessage(message);
					return;
					
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
					
		        Message message = handler.obtainMessage(ret);
				handler.sendMessage(message);
					
						
			}

			private String getNewWeiboRequestParam(String nickname, String content, int grade,int readed_id) {
				try {
					JSONObject js=new JSONObject();
					js.put("nickname", URLEncoder.encode( nickname, "UTF-8"));
					js.put("content",URLEncoder.encode( content, "UTF-8"));
					js.put("grade", grade);
					js.put("readed_id", readed_id);
			
					DesUility du=new DesUility("kajdkd");
		             	
					return du.encrypt(js.toString());
					
				} catch (JSONException e) {
					
					
				} catch (Exception e) {

				}
				Log.d("lichao","getNewWeiboRequestParam error");
				return null;
				
			}
			
			
		}.start();
	}
	public static void sendWeiXin(Context conn,final IWXAPI api,final SendMessageToWX.Req req)
	{
		context=conn;
		final Handler handler=new Handler()
		{
			 public void handleMessage(Message message)
			 {
				 switch(message.what)
				 {
				 case 200:
					// sendNotify(0,"朋友圈分享成功，积分＋3！");
					 if(context!=null)
					 UserManager.AddUserScore(context.getApplicationContext(), UserManager.getCurUser(context.getApplicationContext()),3);
						
					 break;
				default:
				     //sendNotify(0,"朋友圈分享失败！");
				     
				 }
				 clearNotify(0);
				 
			
			 }
		};
		
		new Thread(){

			@Override
			public void run() {
				
				if(api.sendReq(req))
				{
					if(context!=null)
					{
						LoginToServer.AddScore(context, UserManager.getCurUser(context.getApplicationContext()), "AD");
						if(req.scene==SendMessageToWX.Req.WXSceneTimeline)
						UserManager.setLastShareTime(context, "WeiXin");
					}
					Message message = handler.obtainMessage(200);
					handler.sendMessage(message);
				}
				else
				{
					Message message = handler.obtainMessage(0);
					handler.sendMessage(message);
				}
					
						
			}
			
			
		}.start();
	}
	
	public static void sendSinaWeiboWithPic(Context conn,final UserInfo user,final String pic,final long weibo_id,final String type,final boolean isTop)
	{
		context=conn;
		final Handler handler=new Handler()
		{
			 public void handleMessage(Message message)
			 {
				 switch(message.what)
				 {
				 case 200:
					 sendNotify(0,"新浪微博分享发送成功！");
					 break;
				default:
				     sendNotify(0,"网络或者数据异常，发送失败！");
				     
				 }
				 clearNotify(0);
				 if(message.what==200)
				 {
					  Intent intent = new Intent("netradar.bd"); 
					  intent.putExtra("type", 2);
	                  intent.putExtra("id", weibo_id);
	                  //send Broadcast 
	                  context.sendBroadcast(intent); 
				 }
			
			 }
			 
		};
		
		new Thread(){

			@Override
			public void run() {
				
				sendNotify(0,"新浪微博分享中...");
				 clearNotify(0);
				HttpClient client=new DefaultHttpClient();
				client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
				client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 15000);
			
				DefaultHttpRequestRetryHandler kk=new DefaultHttpRequestRetryHandler(0,false);
				((AbstractHttpClient)client).setHttpRequestRetryHandler(kk);
				HttpPost post;
				try {
					post = new HttpPost("https://upload.api.weibo.com/2/statuses/upload.json");
				
					
					MultipartEntity postEntity = new MultipartEntity();
					
					ContentBody source = null;
					ContentBody access_token = null;
					ContentBody status = null;
					FileBody file=null;
					try {
						source = new StringBody("1598719492");
						access_token=new StringBody(user.token);
						status=new StringBody(URLEncoder.encode( ad_msg, "UTF-8"));
					
						} catch (UnsupportedEncodingException e)
						{
							
							Message message = handler.obtainMessage(-1);
							handler.sendMessage(message);
							return;
						}
					postEntity.addPart("source",source);
					postEntity.addPart("access_token",access_token);
					postEntity.addPart("status",status);
					
					File uploadPic=new File(pic);
					
					if(uploadPic.exists())
					{
						
						file=new FileBody(uploadPic,"image/png");
						postEntity.addPart("pic",file);
					}
					
					/*List<NameValuePair>list=new ArrayList<NameValuePair>();  
					
					list.add(new BasicNameValuePair("source","1598719492"));
					list.add(new BasicNameValuePair("access_token",user.token));
					list.add(new BasicNameValuePair("status","分享一个来自《西安违章速查》车友圈的帖子，见图！软件下载地址："));
					Log.d("lichao","pic is "+pic);
					list.add(new BasicNameValuePair("pic",pic));*/
					post.setEntity(postEntity);
					
					HttpResponse response;
				
					response = client.execute(post);
					
					int ret=response.getStatusLine().getStatusCode();
					
					
					if(ret!=200)
					{
						Message message = handler.obtainMessage(ret);
						handler.sendMessage(message);
						return;
					}
					sendRetweetInfo(weibo_id,UserManager.getCurUser(context.getApplicationContext()),type,isTop);
					Message message = handler.obtainMessage(ret);
					handler.sendMessage(message);
					
				} catch (ClientProtocolException e) {
					
					Message message = handler.obtainMessage(0);
					handler.sendMessage(message);
				} catch (IOException e) {
					
					Message message = handler.obtainMessage(0);
					handler.sendMessage(message);
				}catch (Exception e) {
					
					Message message = handler.obtainMessage(0);
					handler.sendMessage(message);
				}finally{
					if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
				}
				
				 
				
			}
			
			
		}.start();
	}
	public static void sendSinaWeiboWithPic1(Context conn,final UserInfo user,final String pic,final String text_send)
	{
		context=conn;
		final Handler handler=new Handler()
		{
			 public void handleMessage(Message message)
			 {
				 switch(message.what)
				 {
				 case 200:
					 sendNotify(0,"新浪微博分享发送成功！");
					 break;
				default:
				     sendNotify(0,"网络或者数据异常，发送失败！");
				     
				 }
				 clearNotify(0);
			
			 }
			 
		};
		
		new Thread(){

			@Override
			public void run() {
				
				sendNotify(0,"新浪微博分享中...");
				 clearNotify(0);
				HttpClient client=new DefaultHttpClient();
				client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
				client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 15000);
			
				DefaultHttpRequestRetryHandler kk=new DefaultHttpRequestRetryHandler(0,false);
				((AbstractHttpClient)client).setHttpRequestRetryHandler(kk);
				HttpPost post;
				try {
					post = new HttpPost("https://upload.api.weibo.com/2/statuses/upload.json");
				
					
					MultipartEntity postEntity = new MultipartEntity();
					
					ContentBody source = null;
					ContentBody access_token = null;
					ContentBody status = null;
					FileBody file=null;
					try {
						source = new StringBody("1598719492");
						access_token=new StringBody(user.token);
						status=new StringBody(URLEncoder.encode(text_send, "UTF-8"));
					
						} catch (UnsupportedEncodingException e)
						{
							
							Message message = handler.obtainMessage(-1);
							handler.sendMessage(message);
							return;
						}
					postEntity.addPart("source",source);
					postEntity.addPart("access_token",access_token);
					postEntity.addPart("status",status);
					
					File uploadPic=new File(pic);
					
					if(uploadPic.exists())
					{
						
						file=new FileBody(uploadPic,"image/png");
						postEntity.addPart("pic",file);
					}
					
				
					post.setEntity(postEntity);
					
					HttpResponse response;
				
					response = client.execute(post);
					
					int ret=response.getStatusLine().getStatusCode();
					
					
					if(ret!=200)
					{
						Message message = handler.obtainMessage(ret);
						handler.sendMessage(message);
						return;
					}
					Message message = handler.obtainMessage(ret);
					handler.sendMessage(message);
					
				} catch (ClientProtocolException e) {
					
					Message message = handler.obtainMessage(0);
					handler.sendMessage(message);
				} catch (IOException e) {
					
					Message message = handler.obtainMessage(0);
					handler.sendMessage(message);
				}catch (Exception e) {
					
					Message message = handler.obtainMessage(0);
					handler.sendMessage(message);
				}finally{
					if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
				}
				
				 
				
			}
			
			
		}.start();
	}
	
	public static void sendSinaWeibo(Context conn,final UserInfo user,final String data)
	{
		context=conn;
		final Handler handler=new Handler()
		{
			 public void handleMessage(Message message)
			 {
				 switch(message.what)
				 {
				 case 200:
					 sendNotify(0,"新浪微博发送成功，积分＋3！");
					 if(context!=null)
					 UserManager.AddUserScore(context.getApplicationContext(), UserManager.getCurUser(context.getApplicationContext()),3);
					 break;
				 case 400:
					 sendNotify(0,"新浪微博授权过期，需重新授权！");
					 break;
				default:
				     sendNotify(0,"网络或者数据异常，发送失败！");
				     
				 }
				 clearNotify(0);
				 
			
			 }
		};
		
		new Thread(){

			@Override
			public void run() {
				
				sendNotify(0,"新浪微博发送中...");
				 clearNotify(0);
				HttpClient client=new DefaultHttpClient();
				client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
				client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 15000);
			
				DefaultHttpRequestRetryHandler kk=new DefaultHttpRequestRetryHandler(0,false);
				((AbstractHttpClient)client).setHttpRequestRetryHandler(kk);
				HttpPost post;
				try {
					post = new HttpPost("https://api.weibo.com/2/statuses/update.json");
				
					List<NameValuePair>list=new ArrayList<NameValuePair>();  
					
					list.add(new BasicNameValuePair("source","1598719492"));
					list.add(new BasicNameValuePair("access_token",user.token));
					list.add(new BasicNameValuePair("status",data));
					post.setEntity(new UrlEncodedFormEntity(list,"UTF-8"));
					
					HttpResponse response;
				
					response = client.execute(post);
					
					int ret=response.getStatusLine().getStatusCode();
					
					
					if(ret==200&&context!=null)
					{
						LoginToServer.AddScore(context, UserManager.getCurUser(context.getApplicationContext()), "AD");
						UserManager.setLastShareTime(context, "Sina");
					}
					Message message = handler.obtainMessage(ret);
					handler.sendMessage(message);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					Message message = handler.obtainMessage(0);
					handler.sendMessage(message);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Message message = handler.obtainMessage(0);
					handler.sendMessage(message);
				}catch (Exception e) {
					// TODO Auto-generated catch block
					Message message = handler.obtainMessage(0);
					handler.sendMessage(message);
				}finally{
					if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
				}
				
				 
				
			}
			
			
		}.start();
	}
	

	public static void sendTencentWeiboWithPic(Context conn,final UserInfo user,final String pic,final long weibo_id,final String type,final boolean isTop)
	{
		context=conn;
		final Handler handler=new Handler()
		{
			 public void handleMessage(Message message)
			 {
				 switch(message.what)
				 {
				 case 200:
					 sendNotify(0,"腾讯微博分享成功！");
					 break;
				 default:
				     sendNotify(0,"网络或数据异常，分享失败！");
				     
				 }
				 clearNotify(0);
				 if(message.what==200)
				 {
					  Intent intent = new Intent("netradar.bd"); 
					  intent.putExtra("type", 2);
	                  intent.putExtra("id", weibo_id);
	                  //send Broadcast 
	                  context.sendBroadcast(intent); 
				 }
			
			 }
		};
		
		new Thread(){

			@Override
			public void run() {
				
				if(pic==null)
				{
					sendRetweetInfo(weibo_id,UserManager.getCurUser(context.getApplicationContext()),type,isTop);
					
				  Intent intent = new Intent("netradar.bd"); 
				  intent.putExtra("type", 2);
                  intent.putExtra("id", weibo_id);
                  //send Broadcast 
                  context.sendBroadcast(intent); 
					 
					return;
				}
				
				sendNotify(0,"腾讯微博分享中...");
				 clearNotify(0);
				HttpClient client=new DefaultHttpClient();
				client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
				client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 15000);
			
				DefaultHttpRequestRetryHandler kk=new DefaultHttpRequestRetryHandler(0,false);
				((AbstractHttpClient)client).setHttpRequestRetryHandler(kk);
				HttpPost post;
				try {
					post = new HttpPost("https://graph.qq.com/t/add_pic_t");
				
					
					MultipartEntity postEntity = new MultipartEntity();
					
					
					ContentBody oauth_consumer_key = null;
					ContentBody access_token = null;
					ContentBody openid=null;
					ContentBody content = null;
					FileBody file=null;
					try {
						oauth_consumer_key = new StringBody("100557777");
						access_token=new StringBody(user.token);
						openid=new StringBody(user.openid);
						content=new StringBody(ad_msg,Charset.forName("UTF-8"));
					
						} catch (UnsupportedEncodingException e)
						{
							
							Message message = handler.obtainMessage(-1);
							handler.sendMessage(message);
							return;
						}
					postEntity.addPart("oauth_consumer_key",oauth_consumer_key);
					postEntity.addPart("access_token",access_token);
					postEntity.addPart("openid",openid);
					postEntity.addPart("content",content);
					
					File uploadPic=new File(pic);
					
					if(uploadPic.exists())
					{
						
						file=new FileBody(uploadPic,"image/png");
						postEntity.addPart("pic",file);
					}
					
					
					
					post.setEntity(postEntity);
					
					HttpResponse response;
				
					response = client.execute(post);
					
					HttpEntity entity=response.getEntity();  
					JSONObject retjson=new JSONObject();
					 retjson=new JSONObject(EntityUtils.toString(entity, HTTP.UTF_8));
					 Message message;
					if(retjson.getString("ret").equals("0"))
					{
						sendRetweetInfo(weibo_id,UserManager.getCurUser(context.getApplicationContext()),type,isTop);
						message = handler.obtainMessage(200);
					}
					else
						message = handler.obtainMessage(0);
					handler.sendMessage(message);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					Message message = handler.obtainMessage(0);
					handler.sendMessage(message);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Message message = handler.obtainMessage(0);
					handler.sendMessage(message);
				}catch (Exception e) {
					// TODO Auto-generated catch block
					Message message = handler.obtainMessage(0);
					handler.sendMessage(message);
				}finally{
					if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
				}
				
				 
				
			}
			
			
		}.start();
	}
	
	
	public static void sendTencentWeibo(Context conn,final UserInfo user,final String data)
	{
		context=conn;
		final Handler handler=new Handler()
		{
			 public void handleMessage(Message message)
			 {
				 switch(message.what)
				 {
				 case 200:
					 sendNotify(0,"腾讯微博发送成功，积分＋3！");
					 if(context!=null)
						 UserManager.AddUserScore(context.getApplicationContext(),  UserManager.getCurUser(context.getApplicationContext()),3);
						
					 break;
				 case 0:
				     sendNotify(0,"网络或数据异常，发送失败！");
				     
				 }
				 clearNotify(0);
				 
			
			 }
		};
		
		new Thread(){

			@Override
			public void run() {
				
				sendNotify(0,"腾讯微博发送中...");
				 clearNotify(0);
				HttpClient client=new DefaultHttpClient();
				client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
				client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 15000);
			
				DefaultHttpRequestRetryHandler kk=new DefaultHttpRequestRetryHandler(0,false);
				((AbstractHttpClient)client).setHttpRequestRetryHandler(kk);
				HttpPost post;
				try {
					post = new HttpPost("https://graph.qq.com/t/add_t");
				
					List<NameValuePair>list=new ArrayList<NameValuePair>();  
					
					list.add(new BasicNameValuePair("access_token",user.token));
					list.add(new BasicNameValuePair("oauth_consumer_key","100557777"));
					list.add(new BasicNameValuePair("openid",user.openid));
					list.add(new BasicNameValuePair("content",data));
					post.setEntity(new UrlEncodedFormEntity(list,"UTF-8"));
					
					HttpResponse response;
				
					response = client.execute(post);
					
					HttpEntity entity=response.getEntity();  
					JSONObject retjson=new JSONObject();
					 retjson=new JSONObject(EntityUtils.toString(entity, HTTP.UTF_8));
					 Message message;
					if(retjson.getString("ret").equals("0"))
					{
						if(context!=null)
						{
							LoginToServer.AddScore(context, UserManager.getCurUser(context.getApplicationContext()), "AD");
							UserManager.setLastShareTime(context, "Tencent");
						}
						
						message = handler.obtainMessage(200);
					}
					else
						message = handler.obtainMessage(0,retjson.getString("msg"));
					handler.sendMessage(message);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					Message message = handler.obtainMessage(0);
					handler.sendMessage(message);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Message message = handler.obtainMessage(0);
					handler.sendMessage(message);
				}catch (Exception e) {
					// TODO Auto-generated catch block
					Message message = handler.obtainMessage(0);
					handler.sendMessage(message);
				}finally{
					if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
				}
				
				 
				
			}
			
			
		}.start();
	}
	
	private static int sendRetweetInfo(long weibo_id,String nickname,String type,boolean isTop)
	{
		int ret=-1;
		String url=((DBUility)context.getApplicationContext()).server+"/upload/retweetinfo";
		String param=getRetweetRequestParam(nickname,weibo_id,type,isTop);
		if(param==null) 
		{
			
			return -1;
		}
		MultipartEntity postEntity = new MultipartEntity();
		
		ContentBody cbContent = null;
		try {
			cbContent = new StringBody(param);
		
			} catch (UnsupportedEncodingException e)
			{
				
				return -1;
			}
		
	
		
		postEntity.addPart("params", cbContent);
		
		
		HttpPost httpPost=new HttpPost(url);  
      
        HttpClient client = null;
        try {
			httpPost.setEntity(postEntity);
				    
			client=new DefaultHttpClient();
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
			DefaultHttpRequestRetryHandler kk=new DefaultHttpRequestRetryHandler(0,false);
			((AbstractHttpClient)client).setHttpRequestRetryHandler(kk);
			HttpResponse response=client.execute(httpPost);
			
			ret=response.getStatusLine().getStatusCode();
			
			if(client!=null&&client.getConnectionManager()!=null)
				client.getConnectionManager().shutdown();
			
			if(ret==200) return 0;
			
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
			
       return -1;
			
				
	}
		
	private static String getRetweetRequestParam(String nickname, long weibo_id,
			String type,boolean isTop) {
		
		try {
			JSONObject js=new JSONObject();
			js.put("nickname", URLEncoder.encode( nickname, "UTF-8"));
			js.put("type",type);
			js.put("isTop", isTop);
			js.put("weibo_id", weibo_id);
	
			DesUility du=new DesUility("kajdkd");
             	
			return du.encrypt(js.toString());
			
		} catch (JSONException e) {
			
			
		} catch (Exception e) {

		}
		
		
		return null;
	}
	
	public static void sendNotify(int id,String str)
	{
		
		if(context==null) return;
	   	String ns = Context.NOTIFICATION_SERVICE;

	   	NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(ns);



	   	CharSequence tickerText = str;

	   	long when = System.currentTimeMillis();

	   	Notification notification = new Notification(R.drawable.icon_small1, tickerText, when);
	   	notification.setLatestEventInfo(context.getApplicationContext(), null, null,null);
	 
	   	
	   	mNotificationManager.notify(id, notification);

	}
	public static void clearNotify(int id)
	{
		if(context==null) return;
	   	String ns = Context.NOTIFICATION_SERVICE;

	   	NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(ns);


	   	mNotificationManager.cancel(id);
		
	}

	

}
