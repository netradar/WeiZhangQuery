package carweibo.netradar.lichao;

import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;




import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Account extends Activity  implements OnCancelListener{

	public class GetUesrInfo2 extends AsyncTask<String, String, UserInfo> {

		String ad_msg="《西安车友圈》---查违章、论坛分享、商户点评、出游安排一网打尽，快来体验吧。下载地址：http://s-93271.gotocdn.com:8080/pic/download/weizhangquery.apk";
		
		@Override
		protected void onPostExecute(UserInfo result) {
			// TODO Auto-generated method stub
			
			pd.dismiss();
			if(result==null)
			{
				
				Toast.makeText(context, "网络连接或数据异常，无法完成指定操作！", Toast.LENGTH_SHORT).show();
				return;
			}
			
			UserManager.AddUser(context.getApplicationContext(), result);
			Toast.makeText(context, "绑定成功！", Toast.LENGTH_SHORT).show();
			if(result.user_type.equals("Weibo"))
				SendWeibo.sendSinaWeibo(null,result,ad_msg);
			if(result.user_type.equals("QQ"))
				SendWeibo.sendTencentWeibo(null,result,ad_msg);
			refreshView();
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
			if(ret==201) return null;
			
			return user;
		}

	}

	ProgressDialog pd;
	Context context;
	private static final String QQ_APPID="100557777";
	private static final String CONSUMER_KEY = "1598719492";//1646212860
	private static final String REDIRECT_URL = "http://www.baidu.com";
	private static final String SCOPE = "get_simple_userinfo,add_share,add_t,add_topic,add_share,add_topic";

	private SsoHandler mssoHandler=null;
	ListView account_listview;
	int loginUserId=-1;
	Tencent mTencent=null;
//	private Weibo mWeibo;
	TextView carweibo_nickname, qq_nickname,weibo_nickname,qq_bind,weibo_bind,carweibo_bind,qq_islogin,weibo_islogin,qq_score,weibo_score;
	TextView carweibo_cur_tip,qq_cur_tip,weibo_cur_tip;
	Button logout_btn;
	String is_login;
	String cur_qq,cur_weibo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.account_layout);
		context=this;
		
		qq_nickname=(TextView)findViewById(R.id.QQ_nickname);
		weibo_nickname=(TextView)findViewById(R.id.Weibo_nickname);
		carweibo_nickname=(TextView)findViewById(R.id.CarWeibo_nickname);
		
		carweibo_bind=(TextView)findViewById(R.id.CarWeibo_bind);
		qq_bind=(TextView)findViewById(R.id.QQ_bind);
		weibo_bind=(TextView)findViewById(R.id.Weibo_bind);

		carweibo_cur_tip=(TextView)findViewById(R.id.CarWeibo_cur_icon);
		qq_cur_tip=(TextView)findViewById(R.id.QQ_cur_icon);
		weibo_cur_tip=(TextView)findViewById(R.id.Weibo_cur_icon);
		logout_btn=(Button)findViewById(R.id.account_logout_btn);
		
		
	
		refreshView();
		
		
		/*UserInfo userinfo=UserManager.getUserInfo(this.getApplicationContext());
		TextView nickname=(TextView)findViewById(R.id.account_nickname);
	
		
		ImageView touxiang=(ImageView)findViewById(R.id.account_touxiang);
		
		nickname.setText(userinfo.nickname);
		Bitmap bm=getTouxiangBmp(userinfo.touxiang_url);
		touxiang.setImageBitmap(bm);*/
		
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		refreshView();
	}

	private void refreshView() {

		initView();
		List<UserInfo> userList=UserManager.getUserInfo(this.getApplicationContext());
		if(userList==null)
		{
			logout_btn.setText("登录");
			logout_btn.setBackgroundResource(R.drawable.btn_query);
			is_login="NOUSER";
			return;
		}
		String cur_user="";
		for(UserInfo a:userList)
		{
			if(a.user_type.equals("QQ"))
			{
				//qq_nickname.setText("（QQ帐号）"+a.nickname);
				cur_qq=a.nickname;
				if(UserManager.isUserValid(a))
				{
					qq_bind.setTextColor(Color.rgb(3, 3, 3));
					qq_bind.setText("已绑定");
				}
				else
					qq_bind.setText("授权过期");
				if(a.isLogin.equals("LOGIN"))
				{
					qq_cur_tip.setVisibility(View.VISIBLE);
					cur_user=a.nickname;
				}
			}
			if(a.user_type.equals("Weibo"))
			{
				//weibo_nickname.setText("（新浪微博）"+a.nickname);
				cur_weibo=a.nickname;
				if(UserManager.isUserValid(a))
				{
					weibo_bind.setTextColor(Color.rgb(3, 3, 3));
					weibo_bind.setText("已绑定");
				}
				else
					weibo_bind.setText("授权过期");
				
				if(a.isLogin.equals("LOGIN"))
				{
					weibo_cur_tip.setVisibility(View.VISIBLE);
					cur_user=a.nickname;
					
				}
			}
			if(a.user_type.equals("CarWeibo"))
			{
				carweibo_nickname.setText(a.nickname);
				carweibo_bind.setTextColor(Color.rgb(3, 3, 3));
				carweibo_bind.setText("已绑定");
				if(a.isLogin.equals("LOGIN"))
				{
					carweibo_cur_tip.setVisibility(View.VISIBLE);
					cur_user=a.nickname;
					
				}
				
			}
			
		}
		if(!cur_user.equals(""))
		{
			logout_btn.setText("退出（"+cur_user+"）");
			logout_btn.setBackgroundResource(R.drawable.btn_style_red);
			is_login="LOGIN";
		}
		else
		{
			logout_btn.setText("登录");
			logout_btn.setBackgroundResource(R.drawable.btn_query);
			is_login="NOLOGIN";
			
		}
	}

	private void initView() {
		
		cur_qq=null;
		cur_weibo=null;
		qq_nickname.setText("QQ帐号");
		weibo_nickname.setText("新浪微博");
		carweibo_nickname.setText("车友圈帐号");
		qq_bind.setText("未绑定");qq_bind.setTextColor(Color.rgb(153, 00, 0));
		weibo_bind.setText("未绑定");weibo_bind.setTextColor(Color.rgb(153, 00, 0));
		carweibo_bind.setText("未绑定");carweibo_bind.setTextColor(Color.rgb(153, 00, 0));
		
		carweibo_cur_tip.setVisibility(View.GONE);
		qq_cur_tip.setVisibility(View.GONE);
		weibo_cur_tip.setVisibility(View.GONE);
		
		
		
		//Log.d("lichao","color is "+ weibo_islogin.getTextColors().getDefaultColor());
	
	}

	public void onCarWeibo(View v)
	{
		if(carweibo_bind.getText().toString().equals("未绑定"))
		{
			Intent i=new Intent();
			i.setClass(Account.this, CarWeiboLogin.class);
			this.startActivityForResult(i,3);
			this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
			return;
		}
		Intent i=new Intent();
		i.setClass(Account.this, AccountInfo.class);
		i.putExtra("nickname", carweibo_nickname.getText().toString());
		
		this.startActivity(i);
		this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
	}
	public void onBindQQ(View v)
	{
		if(qq_bind.getText().toString().equals("未绑定")||qq_bind.getText().toString().equals("授权过期"))
		{
			mTencent = Tencent.createInstance("100557777", this.getApplicationContext());
			if(mTencent!=null)
				mTencent.login(Account.this, SCOPE, new BaseUiListener());
		}
		else
		{
			Intent i=new Intent();
			i.setClass(Account.this, AccountInfo.class);
			i.putExtra("nickname", cur_qq);
			
			this.startActivity(i);
			this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
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
				pd.setMessage("正在绑定帐号，请稍候...");
				pd.setCanceledOnTouchOutside(false);
				pd.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.progress));
				pd.setOnCancelListener( (OnCancelListener) context);
				new GetUesrInfo2().execute("QQ",token,openid,expires_in);
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


	public void onBindWeibo(View v)
	{
		if(weibo_bind.getText().toString().equals("未绑定")||weibo_bind.getText().toString().equals("授权过期"))
		{
			//mWeibo = Weibo.getInstance(CONSUMER_KEY, "https://api.weibo.com/oauth2/default.html");
			String SCOPE = 
		            "email,direct_messages_read,direct_messages_write,"
		            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
		            + "follow_app_official_microblog," + "invitation_write";

			WeiboAuth weibo=new WeiboAuth(this,CONSUMER_KEY, "https://api.weibo.com/oauth2/default.html",SCOPE);
			
			mssoHandler = new SsoHandler(Account.this,weibo);
			mssoHandler.authorize(new AuthDialogListener());
		}
		else
		{
			Intent i=new Intent();
			i.setClass(Account.this, AccountInfo.class);
			i.putExtra("nickname", cur_weibo);
			this.startActivity(i);
			this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
		}
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
			new GetUesrInfo2().execute("Weibo",token,"",expires_in);
	       	pd.show();
	    }

	    @Override
	    public void onCancel() {
	    }

	    @Override
	    public void onWeiboException(WeiboException e) {
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
			pd.setMessage("正在绑定帐号，请稍候...");
			pd.setCanceledOnTouchOutside(false);
			pd.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.progress));
			pd.setOnCancelListener((OnCancelListener) context);
			new GetUesrInfo2().execute("Weibo",token,"",expires_in);
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
			Log.d("lichao","onComplete error");
			
		}

		@Override
		public void onError(WeiboException arg0) {
			Log.d("lichao","onError error");
			
		}

		@Override
		public void onIOException(IOException arg0) {
			Log.d("lichao","onIOException error");
			
		}

	}*/
/*
	private void refreshList() {
		List<UserInfo> user_list=UserManager.getUserInfo(this.getApplicationContext());
		if(user_list!=null)
		{
			SimpleAdapter adapter=getAdapter(user_list);
			adapter.setViewBinder(new ViewBinder() {

				@Override
				public boolean setViewValue(View view, Object data,
						String textRepresentation) {
					
					if(view instanceof ImageView && data instanceof Bitmap)
					{   
						ImageView iv = (ImageView) view;  
						iv.setImageBitmap((Bitmap) data);   
						return true;   
						}
					else  if(textRepresentation.contains("未登录"))
					{   
						TextView tv = (TextView) view;  
						tv.setTextColor(Color.rgb(5, 5, 5));
						return false;   
						}
					else if(textRepresentation.contains("已登录"))
					{   
						TextView tv = (TextView) view;  
						tv.setTextColor(Color.GREEN);
						return false;   
						}
						return false; 
					
				} }
				

				);
		
			
			account_listview.setAdapter(adapter);
		}
		
	}

	private SimpleAdapter getAdapter(List<UserInfo> l) {
	

		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		int i=0;
    	for(UserInfo a:l)
    	{
    		Map<String,Object> map=new HashMap<String,Object>();

				map.put("nickname",a.nickname);
				if(a.user_type.equals("QQ"))
					map.put("usertype","类型：QQ帐号");
				else if(a.user_type.equals("Weibo"))
					map.put("usertype", "类型：新浪微博帐号");
				else
					map.put("usertype", "类型：车友圈帐号");
	    		map.put("touxiang",getTouxiangBmp(a.touxiang_url));
	    		map.put("score", "积分："+a.user_score+"分");
	    		map.put("grade", "等级："+a.grade);
	    		if(a.isLogin.equals("LOGIN"))
	    		{
	    			map.put("islogin", "状态：已登录");
	    			
	    		}
				else 
					map.put("islogin", "状态：未登录");
				
	    		i++;
			list.add(map);
		}
		
		return new SimpleAdapter(this,list,R.layout.account_item_layout,
				new String[]{"nickname","usertype","touxiang","score","grade","islogin"},
				new int[]{R.id.accountlist_nickname,R.id.accountlist_usertype,R.id.accountlist_touxiang,
				R.id.accountlist_userscore,R.id.accountlist_grade,R.id.accountlist_islogin});
	
	}*/

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
		if(requestCode==1||requestCode==2)
		{
			switch(resultCode)
			{
			case 0:
				break;
			case 1:
				UserManager.setCurUser(this.getApplicationContext(), data.getStringExtra("nickname"));
				refreshView();
			
			}
		}
		if(requestCode==3)
		{
			switch(resultCode)
			{
			case 0:
				break;
			case 1:
				//UserManager.setCurUser(this.getApplicationContext(), data.getStringExtra("nickname"));
				refreshView();
				break;
			}
		}
		
		
	}

	/*public void onChangeAccount(View v)
	{
		Intent i=new Intent();
		i.setClass(Account.this, Login.class);
		i.putExtra("newUser_flag", 1);
		this.startActivityForResult(i, 1);
		overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
	}*/
	
	public void onLogout(View v)
	{
		if(is_login.equals("LOGIN"))
		{	
			UserManager.logoutCurUser(this.getApplicationContext());
			DBUility dbUility=(DBUility)getApplicationContext();
			DBhelper db=dbUility.getDB();
			
			db.deleteall(dbUility.WEIBO_TABLE);
			setLogoutFlag(true);
			Toast.makeText(this, "您已退出登录～", Toast.LENGTH_SHORT).show();
			refreshView();
		}
		else if(is_login.equals("NOLOGIN"))
		{
			Intent i=new Intent();
			i.setClass(Account.this, SelectUser.class);
			startActivityForResult(i,1);
			this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
			
		}
		else
		{
			Intent i=new Intent();
			i.setClass(Account.this, Login.class);
			startActivityForResult(i,2);
			this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
			
		}
		
		
		
		
		
		
		/*SharedPreferences pre_cur_user=getSharedPreferences("setting_cur_user", MODE_PRIVATE);
		Editor editor;
		editor=pre_cur_user.edit();
		editor.putString("cur_user", "nologin");
		boolean b = editor.commit();
		this.finish();*/
	}/*
	private String getNickName()
	{
		SharedPreferences pre_cur_user = getSharedPreferences("setting_cur_user", MODE_PRIVATE);
		
		String cur_user=pre_cur_user.getString("cur_user", "nologin");
		if(cur_user.equals("QQ"))
		{
			SharedPreferences pre_qq = getSharedPreferences("setting_qq", MODE_PRIVATE);
			
			return pre_qq.getString("nickname", "游客");
		}
		if(cur_user.equals("WeiBo"))
		{
			SharedPreferences pre_weibo = getSharedPreferences("setting_weibo", MODE_PRIVATE);
			
			return pre_weibo.getString("nickname", "游客");
		}
		if(cur_user.equals("CarWeibo"))
		{
			return "游客";
		}
		else
			return "游客";
		
	}*/
	
	public void onRegiste(View v)
	{
		Intent i=new Intent();
		i.setClass(Account.this, Register.class);
		this.startActivity(i);
		this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
	}
	private Bitmap getTouxiangBmp(String dir)
	{
		
		
		if(dir==null) return null;
		
		return BitmapFactory.decodeFile(dir);
	}
	
	
	

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		this.finish();
		this.overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
	}

	public void onCancelAccount(View v)
	{
		onBackPressed();
	}
	boolean IsNetworkOk(){
		NetworkInfo info=((ConnectivityManager)getSystemService(this.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		if(info==null||!info.isAvailable())
			return false;
		return true;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		// TODO Auto-generated method stub
		
	}
	private void setLogoutFlag(boolean flag)
	{
		SharedPreferences preference = getSharedPreferences("CarWeiboLogout", MODE_PRIVATE);
		Editor editor = preference.edit();
		editor.putBoolean("CarWeiboLogoutFlag", flag);
		editor.commit();
	}
	private boolean getLogoutFlag()
	{
		SharedPreferences preference = getSharedPreferences("CarWeiboLogout", MODE_PRIVATE);
		return preference.getBoolean("CarWeiboLogoutFlag", false);
	}

}
