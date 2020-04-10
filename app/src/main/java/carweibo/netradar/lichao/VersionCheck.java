package carweibo.netradar.lichao;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class VersionCheck {

	Context context;
	public VersionCheck(Context con)
	{
		context=con;
	}
	public void update(final boolean isAuto)
	{
		final Handler handler=new Handler(){
			 public void handleMessage(Message message) {
				 
				if(message.what==200)
				{
					
					setFirstRun();
				
					
					if(message.obj.equals("noNewVersion")&&!isAuto)
				
						Toast.makeText(context, "当前版本已经是最新版本～", Toast.LENGTH_LONG).show();
					
					else
					{
						Document doc=Jsoup.parse((String) message.obj);
						
						Elements e=doc.select("td");
						
						try {
							if(e.get(2).text().equals(getVersionName()))
							{
								if(!isAuto)
									Toast.makeText(context, "当前版本已经是最新版本～", Toast.LENGTH_LONG).show();
								if(isAuto)
								{
									String notice=e.get(0).text();
									String notice_id=e.get(1).text();
								
									
									
									if(notice.equals("noNotice"))
									{
										return;
									}
									else
									{
										if(isNoticeShow(notice_id))
										{
											return;
										}
										Intent i=new Intent();
										i.setClass(context, Notice.class);
										i.putExtra("notice", notice);
										((Activity) context).startActivity(i);
									}
								}
									return ;
							}
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							if(!isAuto)
								Toast.makeText(context, "当前版本已经是最新版本～", Toast.LENGTH_LONG).show();
							return;
						}
						
						Intent i=new Intent();
						
						
						i.putExtra("ver", e.get(2).text());
						i.putExtra("file", e.get(3).text());
						String func=new String();
						int j=e.size();
						
						for(int k=4;k<j;k++)
						{
							func=func+"\n"+e.get(k).text();
						}
						i.putExtra("func", func);
						
						i.setClass(context, NewVersionDialog.class);
						((Activity) context).startActivity(i);
					}
				}
				else
					if(!isAuto)
						Toast.makeText(context, "检测新版本失败，请检查网络配置～", Toast.LENGTH_LONG).show();
					Log.d("lichao","vercheck ret="+message.what);
            
              
           }
		};
		
		new Thread(){

			@Override
			public void run() {
		
				clearBufferPic();
				HttpClient client=new DefaultHttpClient();
				client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
				client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
			
				DefaultHttpRequestRetryHandler kk=new DefaultHttpRequestRetryHandler(0,false);
				((AbstractHttpClient)client).setHttpRequestRetryHandler(kk);
				HttpPost post;
			//	HttpGet get;
				try {
					post = new HttpPost(((DBUility)context.getApplicationContext()).server+"/update?version="+getVersionName()+"&firstrun="+isFirstRun());
			//		get = new HttpGet(((DBUility)context.getApplicationContext()).server+"/update?version="+getVersionName());
					
					HttpResponse response;
			//	Log.d("lichao","vercheck:"+((DBUility)context.getApplicationContext()).server+"/update?version="+getVersionName());
					response = client.execute(post);
					
					int ret=response.getStatusLine().getStatusCode();
					
					
					 HttpEntity entity=response.getEntity();  
					
					String ttt=EntityUtils.toString(entity,HTTP.UTF_8);
					// String ttt=entity.toString();;
					
					Message message = handler.obtainMessage(ret,ttt);
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
	
	 private String getVersionName() throws Exception
	 {
	
		 PackageManager packageManager = context.getPackageManager();
	           
		 PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(),0);
		 String version = packInfo.versionName;
		 return version;
	   }
	 
	 private boolean isNoticeShow(String notice_id)
	 {
		 SharedPreferences pre= context.getSharedPreferences("notice", Context.MODE_PRIVATE);
		 Editor editor = pre.edit();
		 String id=pre.getString("notice_id", "noid");
		
		
		 if(id.equals("noid"))
		 {
			 editor.putString("notice_id", notice_id);
			 editor.commit();
			 return false;
		 }
		 if(id.equals(notice_id))
			 return true;
		
		 editor.putString("notice_id", notice_id);
		 editor.commit();
		 return false;
	 }
	 private boolean isFirstRun()
	 {
		 SharedPreferences pre= context.getSharedPreferences("notice", Context.MODE_PRIVATE);
		 
		 return pre.getBoolean("firstRunFlag", true);
	 }
	 private void setFirstRun()
	 {
		 SharedPreferences pre= context.getSharedPreferences("notice", Context.MODE_PRIVATE);
		 Editor editor = pre.edit();
		 editor.putBoolean("firstRunFlag", false);
		 editor.commit();
	 }
	 private void clearBufferPic()
	 {
		 String sdDir=ScreenShoot.getSDDir(context);
		 
		 if(sdDir==null) return;
		 
		 String pic_dir=sdDir+"/weizhangquery/pic";
		 String weibo_pic_dir=sdDir+"/weizhangquery/pic/weibo_pic";
		 String cam_dir=sdDir+"/weizhangquery/cam";
		 
		 File file=new File(pic_dir);
		 File file_weibo=new File(weibo_pic_dir);
		 File file_cam=new File(cam_dir);
		
		 if(file.exists())
		 {
			 File[] files=file.listFiles();
			 for(int i=0;i<files.length;i++)
			 {
				 if(!files[i].isDirectory())
					 files[i].delete();
			 }
		 }
		 if(file_weibo.exists())
		 {
			 String date=getTime();
			 File[] files=file_weibo.listFiles();
			 for(int i=0;i<files.length;i++)
			 {
				 if(!files[i].getName().contains(date))
					 files[i].delete();
			 }
		 }
		 if(file_cam.exists())
		 {
			
			 File[] files=file_cam.listFiles();
			 for(int i=0;i<files.length;i++)
			 {
				 files[i].delete();
			 }
		 }
	 }
	 public String getTime()
	 {
				 
				 SimpleDateFormat   formatter1   =   new   SimpleDateFormat   ("yyyyMMdd");
				 Date   curDate   =   new   Date(System.currentTimeMillis());   
				 return formatter1.format(curDate);
	}
}
