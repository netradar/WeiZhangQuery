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
	String ad_msg="����������Ȧ��---��Υ�¡���̳�����̻����������ΰ���һ���򾡣���������ɡ����ص�ַ��http://s-93271.gotocdn.com:8080/pic/download/weizhangquery.apk";
	
//	String ad_msg="����λ�ð�׿�ֻ��������Ƽ����ö���--������Υ���ٲ顷����Υ�²���Ҫ���ܺţ��������߸����ѻ�������������ɡ����ص�ַ��http://s-93271.gotocdn.com:8080/pic/download/weizhangquery.apk";
	String ad_msg_weixin="����������Ȧ��---��Υ�¡���̳�����̻����������ΰ���һ���򾡣����������!����������ӵ���������������ˣ�http://s-93271.gotocdn.com:8080/pic/download/weizhangquery.apk";
	
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
			Toast.makeText(this, "ÿ���������һ������Ȧ�ĺ���Ŷ��", Toast.LENGTH_SHORT).show();
			 return;
		}
		/*String Akey="wx9031355f67fc0335";
		IWXAPI api;
		api=WXAPIFactory.createWXAPI(this, Akey,true);
		
		if (!api.openWXApp()) 
		{
			 Toast.makeText(this, "��û�а�װ΢��Ŷ��", Toast.LENGTH_LONG).show();
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
		msg.description="�������������Υ���ٲ顷";
		req.transaction=String.valueOf(System.currentTimeMillis());
		req.scene=SendMessageToWX.Req.WXSceneTimeline;
		req.message=msg;
		SendWeibo.sendWeiXin(this, api, req);*/
		String Akey="wx9031355f67fc0335";
		IWXAPI api;
		api=WXAPIFactory.createWXAPI(this, Akey,true);
		
		if (!api.openWXApp()) 
		{
			 Toast.makeText(this, "��û�а�װ΢��Ŷ��", Toast.LENGTH_LONG).show();
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
		msg.title="������Υ���ٲ顷������Ȧ";
		msg.description="��򵥲�Υ�·�ʽ�����ó��ܺţ����÷������ţ����������߻�������������ɡ�";
		req.transaction=String.valueOf(System.currentTimeMillis());
		req.scene=SendMessageToWX.Req.WXSceneTimeline;
		req.message=msg;
		SendWeibo.sendWeiXin(this, api, req);
		
	}
	public void onGetScoreSina(View v)
	{
		if(UserManager.isExistTodayWeibo(this, "Sina"))
		{
			Toast.makeText(this, "ÿ��ֻ������һ������΢���ĺ���Ŷ��", Toast.LENGTH_SHORT).show();
			 return;
		}
		UserInfo user=UserManager.getBindedUser(this.getApplicationContext(),"Weibo");
		if(user==null)
		{
			Intent i=new Intent();
			i.setClass(GetScore.this, Dialog.class);
			i.putExtra("text", "��ʾ\n\n����Ҫ��һ������΢���ʺŲſ��Է�������΢�������ڰ���");
			i.putExtra("ok","��");
			i.putExtra("cancel", "ȡ��");
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
			Toast.makeText(this, "ÿ��ֻ������һ����Ѷ΢���ĺ���Ŷ��", Toast.LENGTH_SHORT).show();
			 return;
		}
		UserInfo user=UserManager.getBindedUser(this.getApplicationContext(),"QQ");
		if(user==null)
		{
			Intent i=new Intent();
			i.setClass(GetScore.this, Dialog.class);
			i.putExtra("text", "��ʾ\n\n����Ҫ��һ��QQ�ʺŲſ��Է���Ѷ΢�������ڰ���");
			i.putExtra("ok","��");
			i.putExtra("cancel", "ȡ��");
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
			 Toast.makeText(this, "��û�а�װ΢��Ŷ��", Toast.LENGTH_LONG).show();
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
		msg.title="������Υ���ٲ顷������Ȧ";
		msg.description="��򵥲�Υ�·�ʽ�����ó��ܺţ����÷������ţ����������߻�������������ɡ�";
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
