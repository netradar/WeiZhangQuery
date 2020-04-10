package carweibo.netradar.lichao;

import java.util.List;

import net.youmi.android.diy.DiyManager;
import net.youmi.android.offers.OffersManager;

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

public class GetScore extends Activity {
	String ad_msg="《西安车友圈》---查违章、论坛分享、商户点评、出游安排一网打尽，快来体验吧。下载地址：http://s-93271.gotocdn.com:8080/pic/download/weizhangquery.apk";
	
//	String ad_msg="给各位用安卓手机的亲们推荐个好东东--《西安违章速查》，查违章不需要车架号，还能在线跟车友互动，快来体验吧。下载地址：http://s-93271.gotocdn.com:8080/pic/download/weizhangquery.apk";
	String ad_msg_weixin="《西安车友圈》---查违章、论坛分享、商户点评、出游安排一网打尽，快来体验吧!复制这个链接到浏览器就能下载了：http://s-93271.gotocdn.com:8080/pic/download/weizhangquery.apk";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.getscore_dialog);
	}

	public void onGetScoreWeixin(View v)
	{
		if(UserManager.isExistTodayWeibo(this, "WeiXin"))
		{
			Toast.makeText(this, "每天最多邀请一次朋友圈的好友哦～", Toast.LENGTH_SHORT).show();
			 return;
		}
		/*String Akey="wx9031355f67fc0335";
		IWXAPI api;
		api=WXAPIFactory.createWXAPI(this, Akey,true);
		
		if (!api.openWXApp()) 
		{
			 Toast.makeText(this, "您没有安装微信哦～", Toast.LENGTH_LONG).show();
			 return;
		}
		if(api.registerApp(Akey))
			Log.d("lichao","regist weixin success");
		
		SendMessageToWX.Req req=new SendMessageToWX.Req();
		WXImageObject imgObj = new WXImageObject(); 
		WXTextObject txtObj=new WXTextObject();
		WXMediaMessage msg=new WXMediaMessage();
	    
	    WXWebpageObject webpage = new WXWebpageObject(); 
	    webpage.webpageUrl = "http://www.baidu.com"; 

		txtObj.text=ad_msg_weixin;
		
		msg.mediaObject=txtObj;
		msg.title="title";
		msg.description="分享软件《西安违章速查》";
		req.transaction=String.valueOf(System.currentTimeMillis());
		req.scene=SendMessageToWX.Req.WXSceneTimeline;
		req.message=msg;
		SendWeibo.sendWeiXin(this, api, req);*/
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
		
		SendMessageToWX.Req req=new SendMessageToWX.Req();
		 
		WXWebpageObject webpage = new WXWebpageObject();                        
		webpage.webpageUrl =((DBUility)this.getApplicationContext()).webapps+"/first"; 
		WXTextObject txtObj=new WXTextObject();
		WXMediaMessage msg=new WXMediaMessage(webpage);
	/*    
	    WXWebpageObject webpage = new WXWebpageObject(); 
	    webpage.webpageUrl = "http://www.baidu.com"; */

		txtObj.text=ad_msg_weixin;
		
	//	msg.mediaObject=txtObj;
		msg.title="《西安违章速查》－车友圈";
		msg.description="最简单查违章方式，不用车架号，不用发动机号，跟车友在线互动，快来体验吧。";
		req.transaction=String.valueOf(System.currentTimeMillis());
		req.scene=SendMessageToWX.Req.WXSceneTimeline;
		req.message=msg;
		SendWeibo.sendWeiXin(this, api, req);
		
	}
	public void onGetScoreSina(View v)
	{
		if(UserManager.isExistTodayWeibo(this, "Sina"))
		{
			Toast.makeText(this, "每天只能邀请一次新浪微博的好友哦～", Toast.LENGTH_SHORT).show();
			 return;
		}
		UserInfo user=UserManager.getBindedUser(this.getApplicationContext(),"Weibo");
		if(user==null)
		{
			Intent i=new Intent();
			i.setClass(GetScore.this, Dialog.class);
			i.putExtra("text", "提示\n\n您需要绑定一个新浪微博帐号才可以发送新浪微博，现在绑定吗？");
			i.putExtra("ok","绑定");
			i.putExtra("cancel", "取消");
			startActivityForResult(i,1);
		}
		else
		{
			SendWeibo.sendSinaWeibo(this,user,ad_msg);
			this.finish();
		}
	}
	public void onGetScoreTencent(View v)
	{
		if(UserManager.isExistTodayWeibo(this, "Tencent"))
		{
			Toast.makeText(this, "每天只能邀请一次腾讯微博的好友哦～", Toast.LENGTH_SHORT).show();
			 return;
		}
		UserInfo user=UserManager.getBindedUser(this.getApplicationContext(),"QQ");
		if(user==null)
		{
			Intent i=new Intent();
			i.setClass(GetScore.this, Dialog.class);
			i.putExtra("text", "提示\n\n您需要绑定一个QQ帐号才可以发腾讯微博，现在绑定吗？");
			i.putExtra("ok","绑定");
			i.putExtra("cancel", "取消");
			startActivityForResult(i,1);
		}
		else
		{
			SendWeibo.sendTencentWeibo(this,user, ad_msg);
			this.finish();
		}
	}
	
	public void onGetScoreFriend(View v)
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
		
		SendMessageToWX.Req req=new SendMessageToWX.Req();
		 
		WXWebpageObject webpage = new WXWebpageObject();                        
		webpage.webpageUrl =((DBUility)this.getApplicationContext()).webapps+"/first"; 
		WXTextObject txtObj=new WXTextObject();
		WXMediaMessage msg=new WXMediaMessage(webpage);
	/*    
	    WXWebpageObject webpage = new WXWebpageObject(); 
	    webpage.webpageUrl = "http://www.baidu.com"; */

		txtObj.text=ad_msg_weixin;
		
	//	msg.mediaObject=txtObj;
		msg.title="《西安违章速查》－车友圈";
		msg.description="最简单查违章方式，不用车架号，不用发动机号，跟车友在线互动，快来体验吧。";
		req.transaction=String.valueOf(System.currentTimeMillis());
		req.scene=SendMessageToWX.Req.WXSceneSession;
		req.message=msg;
		SendWeibo.sendWeiXin(this, api, req);
	}
	public void onGetScoreApp(View v)
	{
		DiyManager.showRecommendAppWall(this);
		//OffersManager.getInstance(this).showOffersWall();
		

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode==0)
		{
			Intent i=new Intent();
			i.setClass(GetScore.this, Account.class);
			startActivity(i);
			this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
		}
	}
	
	
}
