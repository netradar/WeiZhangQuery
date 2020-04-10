package carweibo.netradar.lichao;

import org.json.JSONObject;

import carweibo.netradar.lichao.Register.RegisterUser;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CarWeiboLogin extends Activity implements OnCancelListener {

	Context context;
	ProgressDialog pd;
	boolean cancel=false;
	
    public class CarLogin extends AsyncTask<UserInfo,Long, UserInfo> {

		UserInfo user;
		
		@Override
		protected void onPostExecute(UserInfo result) 
		{
			if(cancel) return;
			pd.dismiss();
			if(result==null)
			{
			
				Toast.makeText(context, "网络连接异常，无法完成指定操作！", Toast.LENGTH_SHORT).show();
				return;
			}
			if(result.username.equals("error201")&&result.nickname.equals("error201"))
			{
				Toast.makeText(context, "数据异常错误，无法绑定！", Toast.LENGTH_SHORT).show();
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
			
				Toast.makeText(context, "绑定成功！", Toast.LENGTH_SHORT).show();
				
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
    EditText password;
    TextView username;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.carweibo_login_layout);
		username=(TextView)findViewById(R.id.carweibo_username_edit);
		password=(EditText)findViewById(R.id.carweibo_password_edit);
		username.setText(this.getIntent().getStringExtra("username"));
		context=this;
	}
	public void onCarWeiboLogin(View v)
	{
		if(!inputCorrect()) return;
		
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
		cancel=false;
		new CarLogin().execute(user);
		pd.show();
		
	}
	public void onRegister(View v)
	{
		Intent i=new Intent();
		i.setClass(CarWeiboLogin.this, Register.class);
		startActivity(i);
		overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
	}
	private boolean inputCorrect() {
		if(username.getText().toString().length()==0||
				password.getText().toString().length()==0
			)
		{
			Toast.makeText(this, "输入不完整～", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	boolean IsNetworkOk(){
		NetworkInfo info=((ConnectivityManager)getSystemService(this.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		if(info==null||!info.isAvailable())
			return false;
		return true;
	}
	@Override
	public void onCancel(DialogInterface dialog) {
		cancel=true;
		
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		this.setResult(-1);
		this.finish();
		this.overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
		super.onBackPressed();
	}

	public void onCancelCarWeiboLogin(View v)
	{
		onBackPressed();
	}
	
	private void endAct(UserInfo user)
	{
		UserManager.AddUser(this.getApplicationContext(), user);
	//	UserManager.setCurUser(this.getApplicationContext(), user.nickname);
		Intent i=new Intent();
		i.putExtra("nickname", user.nickname);
		
		this.setResult(1,i);
		this.finish();
		this.overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
	}
}
