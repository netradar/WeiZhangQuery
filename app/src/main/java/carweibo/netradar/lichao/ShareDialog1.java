package carweibo.netradar.lichao;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class ShareDialog1 extends Activity {

	static String share_weizhang_msg="晒一下我的违章！软件下载地址：http://s-93271.gotocdn.com:8080/pic/download/weizhangquery.apk";
	static String share_vender_msg="点评了一个商家，请围观！软件下载地址：http://s-93271.gotocdn.com:8080/pic/download/weizhangquery.apk";
	
	String[] msg={share_weizhang_msg,share_vender_msg};
	String dir;
	
	int type=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.share_dialog1);
		dir=this.getIntent().getStringExtra("dir");
		type=this.getIntent().getIntExtra("type", 0);
	}
	

	public void onSinaWeibo(View v)
	{
		UserInfo user=UserManager.getBindedUser(this.getApplicationContext(),"Weibo");
		if(user==null)
		{
			Intent i=new Intent();
			i.setClass(ShareDialog1.this, Dialog.class);
			i.putExtra("text", "提示\n\n您需要绑定一个新浪微博帐号才可以分享，现在绑定吗？");
			i.putExtra("ok","绑定");
			i.putExtra("cancel", "取消");
			startActivityForResult(i,1);
		}
		else
		{
			
			SendWeibo.sendSinaWeiboWithPic1(this,user,dir,msg[type]);
			this.finish();
		}
	}
	
	public void onFriend1(View v)
	{
		String Akey="wx9031355f67fc0335";
		IWXAPI api;
		api=WXAPIFactory.createWXAPI(this, Akey,true);
		
		if (!api.openWXApp()) 
		{
			 Toast.makeText(this, "您没有安装微信哦～", Toast.LENGTH_LONG).show();
			 return;
		}
		if(api.registerApp(Akey))
			Log.d("lichao","regist weixin success");
		else
			return;
		SendMessageToWX.Req req=new SendMessageToWX.Req();
		 WXWebpageObject webpage = new WXWebpageObject();                        
		 webpage.webpageUrl =((DBUility)this.getApplicationContext()).webapps+"/first"; 
		WXImageObject imgObj = new WXImageObject(); 
		WXTextObject txtObj=new WXTextObject();
		WXMediaMessage msg=new WXMediaMessage(webpage);

		
		
		imgObj.setImagePath(dir); 
		msg.mediaObject=imgObj;
		
		
		BitmapFactory.Options  option1=new BitmapFactory.Options();
		option1.inSampleSize=15;
		
		option1.inJustDecodeBounds=false;
		Bitmap bmp=BitmapFactory.decodeFile(dir, option1);

	   
		msg.title="title";
		msg.description="晒我的违章";
	    msg.setThumbImage(bmp);

		
		
	
		req.transaction=String.valueOf(System.currentTimeMillis());
		
		req.scene=SendMessageToWX.Req.WXSceneSession;
		
		req.message=msg;
		api.sendReq(req);
		
	
		this.finish();
	}
	public void onFriend(View v)
	{

		String Akey="wx9031355f67fc0335";
		IWXAPI api;
		api=WXAPIFactory.createWXAPI(this, Akey,true);
		
		if (!api.openWXApp()) 
		{
			 Toast.makeText(this, "您没有安装微信哦～", Toast.LENGTH_LONG).show();
			 return;
		}
		if(api.registerApp(Akey))
			Log.d("lichao","regist weixin success");
		else
			return;
		SendMessageToWX.Req req=new SendMessageToWX.Req();
		WXImageObject imgObj = new WXImageObject(); 
		WXTextObject txtObj=new WXTextObject();
		WXMediaMessage msg=new WXMediaMessage();

		
		
		imgObj.setImagePath(dir); 
		msg.mediaObject=imgObj;
		
		
		BitmapFactory.Options  option1=new BitmapFactory.Options();
		option1.inSampleSize=15;
		
		option1.inJustDecodeBounds=false;
		Bitmap bmp=BitmapFactory.decodeFile(dir, option1);

	   
		msg.title="title";
		msg.description="晒我的违章";
	    msg.setThumbImage(bmp);

		
		
	
		req.transaction=String.valueOf(System.currentTimeMillis());
		
		req.scene=SendMessageToWX.Req.WXSceneTimeline;
		
		req.message=msg;
		api.sendReq(req);
		
	}

	public void onCarWeiBo(View v)
	{
		if(!processUserStatus(UserManager.getCurUser(this.getApplicationContext()))) return;
		
		Intent i=new Intent(Intent.ACTION_SEND);
		i.setClass(ShareDialog1.this, NewWeibo.class);
		i.setType("carweibo");
		i.putExtra("dir", dir);
		
		startActivity(i);
		overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==0)
		{
			Intent i=new Intent();
			i.setClass(ShareDialog1.this, Account.class);
			startActivity(i);
			this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
		}
		if(requestCode==3)
		{
			if(resultCode==0)
			{
				
				Intent i=new Intent();
				i.setClass(ShareDialog1.this, Login.class);
				
				this.startActivityForResult(i, 4);
				overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);//全新登录，数据库没有用户数据

				
			}
		}
		if(requestCode==4)
		{
			if(resultCode==1)
			{
				UserManager.setCurUser(this.getApplicationContext(), data.getStringExtra("nickname"));
				
				Toast.makeText(this, "登录成功～", Toast.LENGTH_SHORT).show();
				
			}
		}
	}
	private boolean processUserStatus(String cur_user)
	{
		if(cur_user==null)
		{
			newUserLogin();
			return false;
		}
		
		if(cur_user.equals("NOUSER"))
		{
			newUserLogin();
			return false;
		}
		if(cur_user.equals("NOLOGIN"))
		{
		
			selectUser();
			return false;
		}
		
		return true;
		
		
	
	}
	private void selectUser()
	{
		
		Intent i=new Intent();
		i.setClass(ShareDialog1.this, SelectUser.class);
		startActivityForResult(i,4);
		overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
		Toast.makeText(this, "您还没登录，请选择一个用户登录～", Toast.LENGTH_SHORT).show();
		
	}
	
	private void newUserLogin()
	{
		Intent i=new Intent();
		
		
		i.putExtra("text", "提示\n\n"+"您没有论坛帐号，现在注册一个吗？");
		i.putExtra("ok", "注册");
		i.putExtra("cancel", "取消");
		i.setClass(ShareDialog1.this, Dialog.class);
		startActivityForResult(i,3);
	}
	public void onCancelShareDetail1(View v)
	{
		this.finish();
	}
	
}
