package carweibo.netradar.lichao;

import java.io.File;

import carweibo.netradar.lichao.Login.GetUesrInfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class Register extends Activity implements OnCancelListener {

	ProgressDialog pd;
	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    ImageView touxiang;
    boolean isCamera;
    File file_camera;
    EditText nickname,username,pwd,repwd;
    String touxiang_dir=null;
    Context context;
    
    public class RegisterUser extends AsyncTask<UserInfo,Long, Integer> {

		UserInfo user;
		
		@Override
		protected void onPostExecute(Integer result) 
		{
			
			pd.dismiss();
			if(result==100)
			{
			
				Toast.makeText(context, "网络连接异常，无法完成指定操作！", Toast.LENGTH_SHORT).show();
				return;
			}
			switch(result)
			{
			case 200:
				Toast.makeText(context, "注册成功！", Toast.LENGTH_SHORT).show();
				//UserManager.AddUser(context.getApplicationContext(), user);
				onBackPressed();
				break;
			case 201:
				Toast.makeText(context, "数据异常，注册失败！", Toast.LENGTH_SHORT).show();

				break;
			case 202:
				Toast.makeText(context, "用户名被占用，换一个用户名吧～", Toast.LENGTH_SHORT).show();
				break;
			case 203:
				Toast.makeText(context, "昵称被占用，换一个昵称吧～", Toast.LENGTH_SHORT).show();
				
				break;
			case 204:
				Toast.makeText(context, "每个手机只能注册5次，您已经超出了～", Toast.LENGTH_SHORT).show();
				break;
			}
			
			
		}

		

		@Override
		protected Integer doInBackground(UserInfo...  params) {

			if(!IsNetworkOk())
			{			
				return 100;
			}
			user=params[0];
			return LoginToServer.RegisterUser(context,user);
		}
    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.register_layout);
		touxiang=(ImageView)findViewById(R.id.registe_touxiang);
		nickname=(EditText)findViewById(R.id.registe_nickname_edit);
		username=(EditText)findViewById(R.id.registe_username_edit);
		pwd=(EditText)findViewById(R.id.registe_password_edit);
		repwd=(EditText)findViewById(R.id.registe_password2_edit);
		context=this;
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==100)
		{
			switch(resultCode)
			{
			case PHOTO_REQUEST_TAKEPHOTO:
				isCamera=true;
				Bundle bun=data.getBundleExtra("data");
	           	String uri=bun.getString("camURI");
	           	
	           	file_camera=new File(uri.substring(6));
				cropImage(Uri.fromFile(file_camera));
				break;
			case PHOTO_REQUEST_GALLERY:
				isCamera=false;
				cropImage(data.getData());
				break;
			
			}
		}
		if(requestCode==PHOTO_REQUEST_CUT)
		{
			if (data != null) 
				touxiang_dir=saveAndShow(data);

		}
				
	}

	public void onSubmmit(View v)
	{
		if(!inputCorrect()) return;
		
		UserInfo user=new UserInfo();
		user.username=username.getText().toString();
		user.nickname=nickname.getText().toString();
		user.password=pwd.getText().toString();
		user.touxiang_url=touxiang_dir;
		user.user_type="CarWeibo";
		user.imei=getIMEI();
		
		pd=new ProgressDialog(this);
		pd.setCancelable(true);
		pd.setMessage("正在注册，请稍候...");
		pd.setCanceledOnTouchOutside(false);
		pd.setIndeterminateDrawable(this.getResources().getDrawable(R.drawable.progress));
		pd.setOnCancelListener( this);
		new RegisterUser().execute(user);
		pd.show();
		
		
		
	}

	 private String getIMEI()
	 {
		 TelephonyManager telephonyManager= (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		 String imei=telephonyManager.getDeviceId();
		 if(imei==null) return "noImei";
		 return imei;
		 
	 }
	private boolean inputCorrect()
	{
		
		if(nickname.getText().toString().length()==0||
				username.getText().toString().length()==0||
				pwd.getText().toString().length()==0||
				repwd.getText().toString().length()==0)
		{
			Toast.makeText(this, "输入不完整～", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(nickname.getText().toString().contains("车")&&
				nickname.getText().toString().contains("友")&&
				nickname.getText().toString().contains("圈"))
		{
			Toast.makeText(this, "昵称不合法，换个昵称～", Toast.LENGTH_SHORT).show();
			return false;
		}
	
		if(!pwd.getText().toString().equals(repwd.getText().toString()))
		{
			Toast.makeText(this, "密码不匹配～", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		this.finish();
		this.overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
		super.onBackPressed();
	}

	public void onCancelRegister(View v)
	{
		onBackPressed();
	}
	public void onChangeTouxiang(View v)
	{
		Intent i=new Intent();

		//i.putExtra("isTouxiang", true);
		i.setClass(Register.this,PicSrcSelect.class);
		this.startActivityForResult(i, 100);
	}
	private void cropImage(Uri uri)
	{
		Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
       
        intent.putExtra("crop", "true");

       
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

       
        intent.putExtra("outputX", 50);
        intent.putExtra("outputY", 50);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, PHOTO_REQUEST_CUT);

	}
	private String saveAndShow(Intent data)
	{
		
		Bundle bundle = data.getExtras();
        if (bundle != null) 
        {
        	
            Bitmap photo = bundle.getParcelable("data");
          //  Drawable drawable = new BitmapDrawable(photo);
            
            
            if(isCamera)
            {
            	if(file_camera!=null)
            	{
            		file_camera.delete();
            	}
            }
            
            String sd_dir=ScreenShoot.getSDDir(this);
	        if(sd_dir==null)
	        {
	        	Toast.makeText(context, "SD卡空间不足，头像信息无法存储！", Toast.LENGTH_SHORT).show();
    			
	        	return null;
	        }
	        touxiang.setImageBitmap(photo);
	       	 ScreenShoot.savePic(photo, sd_dir+"/weizhangquery/pic/touxiang", 
	       			sd_dir+"/weizhangquery/pic/touxiang/carweibo_touxiang_small.png"
				 , 100);
				
	       	 return sd_dir+"/weizhangquery/pic/touxiang/carweibo_touxiang_small.png";
	    }
	        
        
        return null;
		
	}
	boolean IsNetworkOk(){
		NetworkInfo info=((ConnectivityManager)getSystemService(this.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		if(info==null||!info.isAvailable())
			return false;
		return true;
	}


	@Override
	public void onCancel(DialogInterface dialog) {
		
		
	}
}
