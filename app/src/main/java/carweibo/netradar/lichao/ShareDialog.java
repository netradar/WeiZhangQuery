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
import android.widget.TextView;
import android.widget.Toast;

public class ShareDialog extends Activity  {


	private String filePath;
	boolean type;
	ImageAndText imt;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.share_dialog);
		
		
		filePath=this.getIntent().getStringExtra("dir");
		imt=(ImageAndText) this.getIntent().getExtras().getSerializable("imt");

	}
	
	public void onCancelShare(View v)
	{
		this.finish();
	}
	
	public void onSinaWeibo(View v)
	{
		
		UserInfo user=UserManager.getBindedUser(this.getApplicationContext(),"Weibo");
		if(user==null)
		{
			Intent i=new Intent();
			i.setClass(ShareDialog.this, Dialog.class);
			i.putExtra("text", "提示\n\n您需要绑定一个新浪微博帐号才可以分享，现在绑定吗？");
			i.putExtra("ok","绑定");
			i.putExtra("cancel", "取消");
			startActivityForResult(i,1);
		}
		else
		{
			if(imt.grade==2)
				SendWeibo.sendSinaWeiboWithPic(this,user,filePath,imt.id,"Weibo",true);
			else
				SendWeibo.sendSinaWeiboWithPic(this,user,filePath,imt.id,"Weibo",false);
			this.finish();
		}
	}
	public void onTencentWeiBo(View v)
	{
		UserInfo user=UserManager.getBindedUser(this.getApplicationContext(),"QQ");
		if(user==null)
		{
			Intent i=new Intent();
			i.setClass(ShareDialog.this, Dialog.class);
			i.putExtra("text", "提示\n\n您需要绑定一个QQ帐号才可以分享，现在绑定吗？");
			i.putExtra("ok","绑定");
			i.putExtra("cancel", "取消");
			startActivityForResult(i,1);
		}
		else
		{
			//SendWeibo.sendTencentWeiboWithPic(this,user,filePath);
			if(imt.grade==2)
				SendWeibo.sendTencentWeiboWithPic(this,user,filePath,imt.id,"QQ",true);
			else
				SendWeibo.sendTencentWeiboWithPic(this,user,filePath,imt.id,"QQ",false);
			
			this.finish();
		}
	}
	public void onFriend(View v)
	{
		
		
		shareToWeixin();
		if(imt.grade==2)
			SendWeibo.sendTencentWeiboWithPic(this,null,null,imt.id,"WeiXin",true);
		else
			SendWeibo.sendTencentWeiboWithPic(this,null,null,imt.id,"WeiXin",false);
		
		this.finish();
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

		
		
		imgObj.setImagePath(filePath); 
		msg.mediaObject=imgObj;
		
		
		BitmapFactory.Options  option1=new BitmapFactory.Options();
		option1.inSampleSize=15;
		
		option1.inJustDecodeBounds=false;
		Bitmap bmp=BitmapFactory.decodeFile(filePath, option1);

	   
		msg.title="title";
		msg.description="分享了一个帖子";
	    msg.setThumbImage(bmp);

		
		
	
		req.transaction=String.valueOf(System.currentTimeMillis());
		
		req.scene=SendMessageToWX.Req.WXSceneSession;
		
		req.message=msg;
		api.sendReq(req);
		
	
		this.finish();
	}
	
	private void shareToWeixin()
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

		
		
		imgObj.setImagePath(filePath); 
		msg.mediaObject=imgObj;
		
		
		BitmapFactory.Options  option1=new BitmapFactory.Options();
		option1.inSampleSize=15;
		
		option1.inJustDecodeBounds=false;
		Bitmap bmp=BitmapFactory.decodeFile(filePath, option1);

	   
		msg.title="title";
		msg.description="分享了一个帖子";
	    msg.setThumbImage(bmp);

		
		
	
		req.transaction=String.valueOf(System.currentTimeMillis());
		
		req.scene=SendMessageToWX.Req.WXSceneTimeline;
		
		req.message=msg;
		api.sendReq(req);
		
	}
	
	
	public void onWeiBo(View v)
	{
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==0)
		{
			Intent i=new Intent();
			i.setClass(ShareDialog.this, Account.class);
			startActivity(i);
			this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
		}
	}
	
}
