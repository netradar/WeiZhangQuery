package carweibo.netradar.lichao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

public class Feedback extends Activity {

	TextView feedback;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.feedback_layout);
		feedback=(TextView)findViewById(R.id.feedback);
	}

	public void onCancelFeedback(View v)
	{
		this.finish();
		this.overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		this.finish();
		this.overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
	}

	public void onSendFeedback(View v)
	{
		if(feedback.length()==0)
		{
			Toast.makeText(this, "还没写问题呐～", Toast.LENGTH_SHORT).show();
			return;
		}
		sendNotify(0,"正在发送您的反馈意见...");
		final Context context=this;
		new Thread(){
			
			

			@Override
			public void run() {
				
				 List<NameValuePair> list=new ArrayList<NameValuePair>();
				 
		         list.add(new BasicNameValuePair("mac", getLocalMacAddress()));
		         list.add(new BasicNameValuePair("feedback", feedback.getText().toString()));
						
					
		            
		            
							
		
				HttpClient client=new DefaultHttpClient();
				client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
				client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
			
				DefaultHttpRequestRetryHandler kk=new DefaultHttpRequestRetryHandler(0,false);
				((AbstractHttpClient)client).setHttpRequestRetryHandler(kk);
				HttpPost post;
				try {
					post = new HttpPost(((DBUility)context.getApplicationContext()).server+"/feedback");
					post.setEntity(new UrlEncodedFormEntity(list,"UTF-8"));
					HttpResponse response;
				
					response = client.execute(post);
					
					int ret=response.getStatusLine().getStatusCode();
					if(ret==200)
						{
						sendNotify(0,"发送成功！感谢您的反馈！");
						clearNotify(0);
						}
					else
					{
						sendNotify(0,"发送失败！");
						clearNotify(0);
					}
					
					} catch (ClientProtocolException e) {
						
							sendNotify(0,"发送失败！请检查网络配置～");
							clearNotify(0);
				
				} catch (IOException e) {
					
						sendNotify(0,"发送失败！请检查网络配置～");
						clearNotify(0);
				
				}catch (Exception e) {
					
						sendNotify(0,"发送失败！请检查网络配置～");
						clearNotify(0);
				
				}finally {
					if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
				}
				
				 
				
			}
			
			
		}.start();
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
		boolean isOpen=imm.isActive(); 
		if(isOpen==true)
		{
			imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
		
		this.finish();
	}
	
	public void sendNotify(int id,String str)
	{
	   	String ns = Context.NOTIFICATION_SERVICE;

	   	NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);



	   	CharSequence tickerText = str;

	   	long when = System.currentTimeMillis();

	   	Notification notification = new Notification(R.drawable.icon_small1, tickerText, when);
	   	notification.setLatestEventInfo(getApplicationContext(), null, null,null);
	 
	   	
	   	mNotificationManager.notify(id, notification);

	}
	public void clearNotify(int id)
	{
	   	String ns = Context.NOTIFICATION_SERVICE;

	   	NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);


	   	mNotificationManager.cancel(id);
		
	}
	 public String getLocalMacAddress() {   
	        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);   
	        WifiInfo info = wifi.getConnectionInfo();   
	        return info.getMacAddress();   
	    } 
}
