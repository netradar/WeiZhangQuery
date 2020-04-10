package carweibo.netradar.lichao;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import carweibo.netradar.lichao.Register.RegisterUser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences.Editor;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class AccountInfo extends Activity implements OnCancelListener {

	Context context;
	  ProgressDialog pd;
	  public class GetUserGrade extends AsyncTask<String, Integer, String> {

		  
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			updateGrade(result);
		}

		@Override
		protected String doInBackground(String... params) {
			
			return LoginToServer.GetUserGrade(context,params[0]);
		}

	}


	  
	public class ChangeUserInfo extends AsyncTask<String, Integer, Integer> {

		String type;
		String user;
		String nickname;
		
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pd.dismiss();
			if(result==-1)
			{
				
				Toast.makeText(context, "网络连接异常，无法完成指定操作！", Toast.LENGTH_SHORT).show();
				return;
			}
			switch(result)
			{
				case 200:
					Toast.makeText(context, "修改成功！", Toast.LENGTH_SHORT).show();
					updateAndShow(type,user,nickname);
					//onBackPressed();
					break;
				case 201:
				case 202:
					Toast.makeText(context, "数据异常，修改用户信息失败！", Toast.LENGTH_SHORT).show();
					
					break;
				case 203:
					Toast.makeText(context, "昵称被占用了，换个昵称吧～", Toast.LENGTH_SHORT).show();
					
					break;
				
			}
			
		}

		@Override
		protected Integer doInBackground(String... params) {
			type=params[0];
			nickname=params[2];
			user=params[3];
			return	LoginToServer.ChangeUser(context,params[0],params[1],params[2],params[3]);
			
		}

	}

	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    boolean isCamera;
    File file_camera;
  
    String touxiang_dir=null;
  
    
	ImageView touxiang,touxiang_sub,nickname_sub;
	View touxiang_layout,nickname_layout;
	TextView nickname,score,grade,weibo_num,user_type,user_name;
	String score_string="";
	String grade_string="";
	String weibo_string="";
	String reply_string="";
	Button unbind_btn;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.account_info_layout);
		
		touxiang_sub=(ImageView)findViewById(R.id.touxiang_sub_icon);
		nickname_sub=(ImageView)findViewById(R.id.nickname_sub_icon);
		touxiang_layout=(View)findViewById(R.id.account_info_touxiang_layout);
		nickname_layout=(View)findViewById(R.id.account_info_nickname_layout);
		
		touxiang=(ImageView)findViewById(R.id.account_info_touxiang);
		nickname=(TextView)findViewById(R.id.account_info_nickname);
		score=(TextView)findViewById(R.id.account_info_score);
		grade=(TextView)findViewById(R.id.account_info_grade);
		weibo_num=(TextView)findViewById(R.id.account_info_weibo);
		user_type=(TextView)findViewById(R.id.account_info_type);
		user_name=(TextView)findViewById(R.id.account_info_account);
		
		unbind_btn=(Button)findViewById(R.id.account_info_unbind_btn);
		context=this;
		UserInfo user=UserManager.getSingleUserInfo(this.getApplicationContext(), this.getIntent().getStringExtra("nickname"));
		
		refreshView(user);
		
		new GetUserGrade().execute(user.nickname);
	}
	
	public void updateGrade(String result) {
		
		if(result==null)
		{
			Log.d("lichao","resut"+result);
			score.setText("获取失败");
			grade.setText("获取失败");
			weibo_num.setText("获取失败");
			return;
		}
		try {
			
			JSONObject json=new JSONObject(result);
			
			score.setText(json.getString("user_score")+"分");
			grade.setText(json.getString("user_grade")+"级");
			weibo_num.setText(json.getString("user_weibonum")+"个帖子 "+json.getString("user_replynum")+"个回复");
			
			
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		
	}

	public void updateAndShow(String type,String user,String nick) 
	{
		if(type.equals("touxiang"))
		{
			String sd_dir=ScreenShoot.getSDDir(this);
	        if(sd_dir==null)	return;
	       	
	       	String url=sd_dir+"/weizhangquery/pic/touxiang/carweibo_touxiang_small_t.png";
	       	Bitmap bm=BitmapFactory.decodeFile(url);
	        ScreenShoot.savePic(bm, sd_dir+"/weizhangquery/pic/touxiang", 
	       			sd_dir+"/weizhangquery/pic/touxiang/carweibo_touxiang_small.png"
				 , 100);
			File file=new File(url);
			if(file.exists()) file.delete();
			touxiang.setImageBitmap(bm);
		}
		if(type.equals("nickname"))
		{
			nickname.setText(nick);
			UserManager.updateUser(this.getApplicationContext(),user,nick);
		}
	}

	private void refreshView(UserInfo user) {
		Bitmap bm=getTouxiangBmp(user.touxiang_url);
		if(bm!=null)
			touxiang.setImageBitmap(bm);
		
		if(user.user_type.equals("QQ")||user.user_type.equals("Weibo"))
		{
			touxiang_sub.setVisibility(View.GONE);
			nickname_sub.setVisibility(View.GONE);
			touxiang_layout.setClickable(false);
			nickname_layout.setClickable(false);
			if(user.user_type.equals("QQ"))
				user_type.setText("QQ帐号");
			if(user.user_type.equals("Weibo"))
				user_type.setText("新浪微博");
			
			
			user_name.setText("");
			
			unbind_btn.setVisibility(View.VISIBLE);
		}
		else
		{
			touxiang_sub.setVisibility(View.VISIBLE);
			nickname_sub.setVisibility(View.VISIBLE);
			touxiang_layout.setClickable(true);
			nickname_layout.setClickable(true);
			if(user.user_type.equals("CarWeibo"))
				user_type.setText("车友圈帐号");
			user_name.setText(user.username);
			unbind_btn.setText("删除帐号");
		}
		
		nickname.setText(user.nickname);
		
		if(score_string.equals(""))
		{
			score.setText("获取...");
			grade.setText("获取...");
			weibo_num.setText("获取...");

			
		}
		else
		{
			score.setText(score_string);
			grade.setText(grade_string);
			weibo_num.setText(weibo_string);
		
		}
		
		
		
	}

	private Bitmap getTouxiangBmp(String dir) 
	{
		
		
		if(dir==null) return null;
		
		return BitmapFactory.decodeFile(dir);
		
	}


	public void onUnbind(View v)
	{
		Button btn=(Button)v;
		if(btn.getText().toString().equals("删除帐号"))
		{
			Intent i=new Intent();
			i.setClass(AccountInfo.this, Dialog.class);
			i.putExtra("text", "提示\n\n确认删除车友圈帐号吗？");
			i.putExtra("ok","确定");
			i.putExtra("cancel", "取消");
			startActivityForResult(i,1);
			return;
		}
		Intent i=new Intent();
		i.setClass(AccountInfo.this, Dialog.class);
		i.putExtra("text", "提示\n\n解除绑定后将不能完成分享了，确认解除绑定吗？");
		i.putExtra("ok","确定");
		i.putExtra("cancel", "取消");
		startActivityForResult(i,1);
		
	}
	private void endAct()
	{
		onBackPressed();
	
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		this.finish();
		this.overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
	}
	
	public void onCancelAccountInfo(View v)
	{
		onBackPressed();
	}
	public void onChangeTouxiang(View v)
	{
		Intent i=new Intent();

		i.setClass(AccountInfo.this,PicSrcSelect.class);
		this.startActivityForResult(i, 100);
	}
	public void onChangeNickname(View v)
	{
		/*pd=new ProgressDialog(this);
		pd.setCancelable(true);
		pd.setMessage("正在修改，请稍候...");
		pd.setCanceledOnTouchOutside(false);
		pd.setIndeterminateDrawable(this.getResources().getDrawable(R.drawable.progress));
		pd.setOnCancelListener( this);
		new ChangeUserInfo().execute("nickname",null,null,user_name.getText().toString());
		pd.show();*/
		Intent i=new Intent();
		i.putExtra("nickname", nickname.getText().toString());
		i.setClass(AccountInfo.this,ChangeNickname.class);
		this.startActivityForResult(i, 101);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode==1&&resultCode==0)
		{
			DBUility dbUility=(DBUility)getApplicationContext();
			DBhelper db=dbUility.getDB();
			
			db.deleteall(dbUility.WEIBO_TABLE);
			setLogoutFlag(true);
			UserManager.deleteUser(this.getApplicationContext(), nickname.getText().toString());
			endAct();
		}
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
				touxiang_dir=savePic(data);
			if(touxiang_dir!=null)
			{
				pd=new ProgressDialog(this);
				pd.setCancelable(true);
				pd.setMessage("正在上传头像，请稍候...");
				pd.setCanceledOnTouchOutside(false);
				pd.setIndeterminateDrawable(this.getResources().getDrawable(R.drawable.progress));
				pd.setOnCancelListener( this);
				new ChangeUserInfo().execute("touxiang",touxiang_dir,null,user_name.getText().toString());
				pd.show();
			}
		}
		if(requestCode==101)
		{
			if(resultCode==1)
			{
				pd=new ProgressDialog(this);
				pd.setCancelable(true);
				pd.setMessage("正在更改昵称，请稍候...");
				pd.setCanceledOnTouchOutside(false);
				pd.setIndeterminateDrawable(this.getResources().getDrawable(R.drawable.progress));
				pd.setOnCancelListener( this);
				new ChangeUserInfo().execute("nickname",null,data.getStringExtra("nickname"),user_name.getText().toString());
				pd.show();
			}
		}
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
	private String savePic(Intent data)
	{
		
		Bundle bundle = data.getExtras();
        if (bundle != null) 
        {
        	
            Bitmap photo = bundle.getParcelable("data");
          //  Drawable drawable = new BitmapDrawable(photo);
          //  touxiang.setImageBitmap(photo);
            
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
	       	 ScreenShoot.savePic(photo, sd_dir+"/weizhangquery/pic/touxiang", 
	       			sd_dir+"/weizhangquery/pic/touxiang/carweibo_touxiang_small_t.png"
				 , 100);
				
	       	 return sd_dir+"/weizhangquery/pic/touxiang/carweibo_touxiang_small_t.png";
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
	public void onCancel(DialogInterface arg0) {
		// TODO Auto-generated method stub
		
	}

	private void setLogoutFlag(boolean flag)
	{
		SharedPreferences preference = getSharedPreferences("CarWeiboLogout", MODE_PRIVATE);
		Editor editor = preference.edit();
		editor.putBoolean("CarWeiboLogoutFlag", flag);
		editor.commit();
	}
}
