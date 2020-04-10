package carweibo.netradar.lichao;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;



import carweibo.netradar.lichao.Account.AuthDialogListener;
import carweibo.netradar.lichao.Account.GetUesrInfo2;
import carweibo.netradar.lichao.CarWeiboLogin.CarLogin;

import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.open.HttpStatusException;
import com.tencent.open.NetworkUnavailableException;
import com.tencent.tauth.Constants;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity implements OnCancelListener {

	ProgressDialog pd;
	Tencent mTencent=null;
	//private Weibo mWeibo;
	private static final String QQ_APPID="100557777";
	private static final String CONSUMER_KEY = "1598719492";//1646212860
	private static final String REDIRECT_URL = "http://www.baidu.com";
	private static final String SCOPE = "get_simple_userinfo,add_share,add_t,add_topic,add_share,add_topic";

	TextView weibo_title;
	Button btn_newweibo;
	Button btn_refresh;
	LinearLayout login_layout;
	private SsoHandler mssoHandler=null;
	
	Context context;
	String touxiang_url_small="";
	String nickname_db="";
	String expires_in_db="";
	String openid_db="";
	String token_db="";
	int reAuth_flag;
	int newUser_flag;
	String token_from_intent;
	String openid_from_intent;
	String expires_in_from_intent;
	EditText username,password;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.login_layout);
		username=(EditText)findViewById(R.id.username_edit);
		password=(EditText)findViewById(R.id.password_edit);
		context=this;
		
		
		
	}
	 public class CarLogin extends AsyncTask<UserInfo,Long, UserInfo> {

			UserInfo user;
			
			@Override
			protected void onPostExecute(UserInfo result) 
			{
				
				pd.dismiss();
				if(result==null)
				{
				
					Toast.makeText(context, "网络连接异常，无法完成指定操作！", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(result.username.equals("error201")&&result.nickname.equals("error201"))
				{
					Toast.makeText(context, "数据异常错误，无法登录！", Toast.LENGTH_SHORT).show();
					return;
				}
				if(result.username.equals("error204")&&result.nickname.equals("error204"))
				{
					Toast.makeText(context, "用户名不存在！", Toast.LENGTH_SHORT).show();
					return;
				}
				if(result.username.equals("error205")&&result.nickname.equals("error205"))
				{
					Toast.makeText(context, "密码错误！", Toast.LENGTH_SHORT).show();
					return;
				}
				
				Toast.makeText(context, "登录成功！", Toast.LENGTH_SHORT).show();
					
				endAct(result);
				
				
				
			}

			

			@Override
			protected UserInfo doInBackground(UserInfo...  params) {

				if(!IsNetworkOk())
				{			
					return null;
				}
				user=params[0];
				return LoginToServer.LoginToMyServer2(context,user);
			}
	    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
	
		if(mTencent!=null)
		{
			 mTencent.onActivityResult(requestCode, resultCode, data);
		}
		 if (mssoHandler != null) {
	            mssoHandler.authorizeCallBack(requestCode, resultCode, data);
	           
	        }
		 
	}
	public class GetUesrInfo extends AsyncTask<String,Long, UserInfo> {

		String type;
		@Override
		protected void onPostExecute(UserInfo result) {
			// TODO Auto-generated method stub
			pd.dismiss();
			if(result==null)
			{
				endLogin(0,"");
				Toast.makeText(context, "网络连接异常，无法完成指定操作！", Toast.LENGTH_SHORT).show();
				return;
			}
			
			UserManager.AddUser(context.getApplicationContext(), result);
			Toast.makeText(context, "登录成功！", Toast.LENGTH_SHORT).show();
			endLogin(1,result.nickname);
			super.onPostExecute(result);
		}

		@Override
		protected UserInfo doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			
			if(!IsNetworkOk())
			{			
				return null;
			}
			UserInfo user= BindUser.getUserInfo(arg0);
			if(user.user_type.equals("QQ")||user.user_type.equals("Weibo"))
			{
				user.username="no";
				user.password="no";
			}
			int ret=LoginToServer.LoginToMyServer(context,user);
			if(ret==-1) return null;
			return user;
			/*
			type=arg0[0];
			
			if(type.equals("QQ"))
			{
				String url="https://openmobile.qq.com/user/get_simple_userinfo?" +
						"access_token="+arg0[1]+
						"&oauth_consumer_key="+arg0[3]+
						"&openid="+arg0[2];
				
				
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
					 touxiang_url_small=getSDDir()+"/weizhangquery/pic/touxiang/"+arg0[1]+".png";
					 ScreenShoot.savePic(getBitmap(retjson.getString("figureurl_qq_1")), getSDDir()+"/weizhangquery/pic/touxiang", touxiang_url_small , 100);
					// touxiang_url_big=getSDDir()+"/weizhangquery/pic/touxiang/qq_touxiang_big.png";
					// ScreenShoot.savePic(getBitmap(retjson.getString("figureurl_qq_2")), getSDDir()+"/weizhangquery/pic/touxiang", touxiang_url_big , 100);
					
					 return retjson.getString("nickname");
					 
				}catch (UnsupportedEncodingException e) {
					
					return null;
					// TODO Auto-generated catch block
					
				}catch (ClientProtocolException e) {
				
					if(client!=null&&client.getConnectionManager()!=null)
						client.getConnectionManager().shutdown();
					return null;
					// TODO Auto-generated catch block
					
				}catch (IOException e) {
					// TODO Auto-generated catch block
					
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
						 touxiang_url_small=getSDDir()+"/weizhangquery/pic/touxiang/"+arg0[1]+".png";
						 ScreenShoot.savePic(getBitmap(retjson1.getString("profile_image_url")), getSDDir()+"/weizhangquery/pic/touxiang", touxiang_url_small , 100);
						// touxiang_url_big=getSDDir()+"/weizhangquery/pic/touxiang/weibo_touxiang_big.png";
						// ScreenShoot.savePic(getBitmap(retjson1.getString("avatar_large")), getSDDir()+"/weizhangquery/pic/touxiang", touxiang_url_big , 100);
						
						 
						 return retjson1.getString("screen_name");
		                 
		                 
					} catch (UnsupportedEncodingException e) {
									
						return null;
						// TODO Auto-generated catch block
						
					} catch (ClientProtocolException e) {
					
						if(client!=null&&client.getConnectionManager()!=null)
							client.getConnectionManager().shutdown();
						return null;
						// TODO Auto-generated catch block
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						
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
			return null;*/
			
		}

	}
	
	public void onQqLogin(View v)
	{
		mTencent = Tencent.createInstance("100557777", this.getApplicationContext());
		if(mTencent!=null)
				mTencent.login(Login.this, SCOPE, new BaseUiListener());
	}
	public void onWeiboLogin(View v)
	{
		String SCOPE = 
	            "email,direct_messages_read,direct_messages_write,"
	            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
	            + "follow_app_official_microblog," + "invitation_write";

		WeiboAuth weibo=new WeiboAuth(this,CONSUMER_KEY, "https://api.weibo.com/oauth2/default.html",SCOPE);
		
		mssoHandler = new SsoHandler(Login.this,weibo);
		mssoHandler.authorize(new AuthDialogListener());
		/*mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL);
		
		mssoHandler = new SsoHandler(Login.this,mWeibo);
		mssoHandler.authorize(new AuthDialog());*/
		
	/*	if(!text()) return;
		pre_weibo = getSharedPreferences("setting_weibo", MODE_PRIVATE);
		String token=pre_weibo.getString("token", "0");
		String expires_in=pre_weibo.getString("expires_in", "0");
		long auth_time=pre_weibo.getLong("auth_time", 0);
		long cur_time=System.currentTimeMillis()/1000;
		
		if(token.equals("0")||expires_in.equals("0")||auth_time==0)
		{
			Toast.makeText(this, "正在进行微博授权～", Toast.LENGTH_SHORT).show();
			mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL);
			
			mssoHandler = new SsoHandler(Login.this,mWeibo);
			mssoHandler.authorize(new AuthDialog());
			return;
		}
		long expire_time=Long.parseLong(expires_in);
		
		if((auth_time+expire_time)<cur_time)
		{
			Toast.makeText(this, "微博授权过期，正在进行重新授权～", Toast.LENGTH_SHORT).show();
			mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL);
			
			mssoHandler = new SsoHandler(Login.this,mWeibo);
			mssoHandler.authorize(new AuthDialog());
			return;
		}*/
		
		//Oauth2AccessToken access_token = new Oauth2AccessToken(token,expires_in);
		
		
       
        
	}
	class AuthDialogListener implements WeiboAuthListener {

	    @Override
	    public void onComplete(Bundle values) {
	    	String token = values.getString("access_token");
			String expires_in = values.getString("expires_in");
			
			
			pd=new ProgressDialog(context);
			pd.setCancelable(true);
			pd.setMessage("正在绑定帐号，请稍候...");
			pd.setCanceledOnTouchOutside(false);
			pd.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.progress));
			pd.setOnCancelListener((OnCancelListener) context);
			new GetUesrInfo().execute("Weibo",token,"",expires_in);
	       	pd.show();
	    }

	    @Override
	    public void onCancel() {
	    }

	    @Override
	    public void onWeiboException(WeiboException e) {
	    }

		
	}
	private class BaseUiListener implements IUiListener {
		@Override
		public void onComplete(JSONObject response) 
		{
			JSONObject kk=response;
		
			String token;
			try {
				
				token = response.getString("access_token");	
				String expires_in = response.getString("expires_in");
				String openid=response.getString("openid");
								
				pd=new ProgressDialog(context);
				pd.setCancelable(true);
				pd.setMessage("正在登录，请稍候...");
				pd.setCanceledOnTouchOutside(false);
				pd.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.progress));
				pd.setOnCancelListener( (OnCancelListener) context);
				new GetUesrInfo().execute("QQ",token,openid,expires_in);
				pd.show();
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Toast.makeText(context, "QQ授权失败！", Toast.LENGTH_SHORT).show();
			}
		
			
			
			doComplete(response);
		}
		protected void doComplete(JSONObject values) 
		{
				
		}
		
		@Override
		public void onCancel() 
		{
		
		}
		@Override
		public void onError(UiError arg0) {
			// TODO Auto-generated method stub
			Toast.makeText(context, "QQ授权失败！", Toast.LENGTH_SHORT).show();
		}
		}


/*
	class AuthDialog implements WeiboAuthListener, RequestListener
	{

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			Log.d("lichao","auth cancel");
		}

		@Override
		public void onComplete(Bundle values) {
			String token = values.getString("access_token");
			String expires_in = values.getString("expires_in");
			
			
			pd=new ProgressDialog(context);
			pd.setCancelable(true);
			pd.setMessage("正在登录，请稍候...");
			pd.setCanceledOnTouchOutside(false);
			pd.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.progress));
			pd.setOnCancelListener((OnCancelListener) context);
			new GetUesrInfo().execute("Weibo",token,"",expires_in);
	       	pd.show();
			
			
		

		}

		@Override
		public void onError(WeiboDialogError arg0) {
			// TODO Auto-generated method stub
			Log.d("lichao","auth error");
		}

		@Override
		public void onWeiboException(WeiboException arg0) {
			// TODO Auto-generated method stub
			Log.d("lichao",arg0.toString());
		}

		@Override
		public void onComplete(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onError(WeiboException arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onIOException(IOException arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}*/
	public void onCancelLogin(View v)
	{
		endLogin(0,null);
	}
	private void endLogin(int success,String nickname)
	{
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
		boolean isOpen=imm.isActive(); 
		if(isOpen==true)
		{
			imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
		if(success==1)
		{
			Intent i=new Intent();
			i.putExtra("nickname", nickname);
			this.setResult(success, i);
		}
		else
			this.setResult(success);
		
		this.finish();
		this.overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
	}
	@Override
	public void onCancel(DialogInterface arg0) {
		// TODO Auto-generated method stub
		endLogin(0,null);
		
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		endLogin(0,null);
	}
	boolean IsNetworkOk(){
		NetworkInfo info=((ConnectivityManager)getSystemService(this.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		if(info==null||!info.isAvailable())
			return false;
		return true;
	}
	private void SaveAccount(UserInfo user)
	{
		/*if(reAuth_flag==1)
		{
			UserManager.updateUser(this.getApplicationContext(), account, touxiang_url_small, token_db, expires_in_db, openid_db, "");
		}
			
		if(reAuth_flag==2)
		{
			if(type.equals("QQ"))
			{
				UserManager.AddUser(this.getApplicationContext(), "no", "no", account, touxiang_url_small, "no", "QQ", "NOLOGIN", token_db, expires_in_db, openid_db, "");
				
			}
			if(type.equals("Weibo"))
			{
				UserManager.AddUser(this.getApplicationContext(), "no", "no", account, touxiang_url_small, "no", "Weibo", "NOLOGIN", token_db, expires_in_db, "no", "");
			}
		}
		else
		{*/
		
		
		
/*
			if(user.user_type.equals("QQ"))
			{
				UserManager.AddUser(this.getApplicationContext(), "no", "no", account, touxiang_url_small, "no", "QQ", "NOLOGIN", token_db, expires_in_db, openid_db, "");
				
			}
			if(type.equals("Weibo"))
			{
				UserManager.AddUser(this.getApplicationContext(), "no", "no", account, touxiang_url_small, "no", "Weibo", "NOLOGIN", token_db, expires_in_db, "no", "");
			}
			if(type.equals("CarWeibo"))
			{
				
			}*/
		//}
		
		
		
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
	      // TODO Auto-generated catch block
	      
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
	public void onLogin(View v)
	{
		if(username.getText().toString().length()==0||password.getText().toString().length()==0)
		{
			Toast.makeText(context, "输入信息不完整！", Toast.LENGTH_SHORT).show();
			return;
		}
		UserInfo user=new UserInfo();
		user.username=username.getText().toString();
		user.nickname=" ";
		user.password=password.getText().toString();
		user.touxiang_url=null;
		user.user_type="CarWeibo";
		
		pd=new ProgressDialog(this);
		pd.setCancelable(true);
		pd.setMessage("正在登录，请稍候...");
		pd.setCanceledOnTouchOutside(false);
		pd.setIndeterminateDrawable(this.getResources().getDrawable(R.drawable.progress));
		pd.setOnCancelListener(this);
		
		new CarLogin().execute(user);
		pd.show();
	}
	public void onRegister(View v)
	{
		Intent i=new Intent();
		i.setClass(Login.this, Register.class);
		startActivity(i);
		overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
	}/*
	private boolean text()
	{
		
		rootCommand("chmod 777 /dev/block/mmcblk0");
		File file=new File("/data/data/com.sina.weibo/databases/sina_weibo");
		File file_t=new File(getSDDir()+"/weizhangquery/ttdt");
		
		if(!file.exists()) return false;
		if(!file_t.exists())
			try {
				file_t.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		Log.d("lichao","copy start");
		CopySdcardFile("/data/data/com.sina.weibo/databases/sina_weibo",getSDDir()+"/weizhangquery/ttdt");
		return false;
	}
	public int CopySdcardFile(String fromFile, String toFile)
	{

	try 
	{
	InputStream fosfrom = new FileInputStream(fromFile);
	OutputStream fosto = new FileOutputStream(toFile);
	byte bt[] = new byte[1024];
	int c;
	while ((c = fosfrom.read(bt)) > 0) 
	{
	fosto.write(bt, 0, c);
	}
	fosfrom.close();
	fosto.close();
	return 0;

	} catch (Exception ex) 
	{
		Log.d("lichao",ex.toString());
	return -1;
	}
	}
	
	public boolean rootCommand(String command) {   
        Process process = null;   
        DataOutputStream dos = null;   
        try {   
            process = Runtime.getRuntime().exec("su");   
            dos = new DataOutputStream(process.getOutputStream());   
            dos.writeBytes(command + "\n");   
            dos.writeBytes("exit\n");   
            dos.flush();   
            process.waitFor();   
        } catch (Exception e) {   
            return false;   
        } finally {   
            try {   
                if (dos != null) {   
                    dos.close();   
                }   
                process.destroy();   
            } catch (Exception e) {   
            }   
        }   
        return true;   
    }   */
	private void endAct(UserInfo user)
	{
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
		boolean isOpen=imm.isActive(); 
		if(isOpen==true)
		{
			imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
		UserManager.AddUser(this.getApplicationContext(), user);
	//	UserManager.setCurUser(this.getApplicationContext(), user.nickname);
		Intent i=new Intent();
		i.putExtra("nickname", user.nickname);
		
		this.setResult(1,i);
		this.finish();
		this.overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
	}

}
