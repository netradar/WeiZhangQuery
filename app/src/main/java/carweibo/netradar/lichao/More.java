package carweibo.netradar.lichao;

import java.util.ArrayList;

import net.youmi.android.diy.DiyManager;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class More extends Activity implements OnClickListener {

	Cursor c;
	String[] items;
	TextView unread_msg_view;
	BroadcastReceiver bdReceiver;
	private IntentFilter ifilter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.more_layout);
		unread_msg_view=(TextView)findViewById(R.id.more_msg_unread_tip);
		 bdReceiver=new BroadcastReceiver() {  
   		  
	    	    @Override  
	    	    public void onReceive(Context context, Intent intent) {  
	    	        // TODO Auto-generated method stub   
	    	    	
	    	    	int type=intent.getIntExtra("type", -1);
	    	    	
	    	    	if(type!=4) return;
	    	    	
	    	    	refreshUnreadTip();
	    	    	
	    	    }



				
	    	  
	    	}; 
	    	ifilter= new IntentFilter(); 
	    	ifilter.addAction("netradar.bd");
	}

	protected void refreshUnreadTip() {
		int unread_msg_num=MessageManager.getUnreadSum(this.getApplicationContext());
		if(unread_msg_num==0)
			unread_msg_view.setVisibility(View.GONE);
		else 
		{
			unread_msg_view.setVisibility(View.VISIBLE);
			if(unread_msg_num>99)
			{
				unread_msg_view.setText("99+");
			}
			else
				unread_msg_view.setText(String.valueOf(unread_msg_num));
		}
		
	}

	@Override
	protected void onStart() {
		
		super.onStart();
		registerReceiver(bdReceiver,ifilter);

	}

	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		refreshUnreadTip();
	}

	@Override
	protected void onStop() {
		unregisterReceiver(bdReceiver);
		super.onStop();
	}

	public void onMyAccount(View v)
	{
		/*if(getNickName().equals("游客"))
		{
			Toast.makeText(this, "您还没登录～", Toast.LENGTH_LONG).show();
			return;
		}*/
		Intent i=new Intent();
		i.setClass(More.this, Account.class);
		startActivity(i);
		this.getParent().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
	}
	/*public void onCarInfo(View v)
	{
		Intent i=new Intent();
		i.putExtra("data", "");
		i.setClass(More.this, CarInfo.class);
		
		startActivity(i);
		this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
	}
	public void onHistoryQuery(View v)
	{
		DBUility dbUility=(DBUility)getApplicationContext();
    	
    	DBhelper db=dbUility.getDB();
    	
    	c=db.query(dbUility.CAR_TABLE);//, "num", this.getIntent().getStringExtra("data"));
		ArrayList<String> list=getCarList(c);
		if(list.size()<=0) 
			{
			Toast.makeText(this, "车库还空着呢，先添加车辆吧～", Toast.LENGTH_LONG).show();
				return;
			}
		

		if(list.size()>1)
		{
			items=new String[list.size()];
			for(int ii=0;ii<list.size();ii++)
			{
				items[ii]=list.get(ii);
			}
			new AlertDialog.Builder(More.this).setTitle("选择车辆").setSingleChoiceItems(items, 0,this).show();//显示对话框
		}
		else
		{
			Log.d("lichao","car is "+list.get(0));
			startDetailAct1(list.get(0));
		}
		
	}
	*/
	public void updateVer(View v)
	{
		new VersionCheck(this).update(false);
	}
	public void onFeedback(View v)
	{
		Intent i=new Intent();
		i.setClass(More.this, Feedback.class);
		
		startActivity(i);
		this.getParent().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);

	}
	public void onAbout(View v)
	{
		Intent i=new Intent();
		i.setClass(More.this, About.class);
		
		startActivity(i);
		this.getParent().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);

	}
	private void startDetailAct1(String num)
	{
		DBUility dbUility=(DBUility)getApplicationContext();
    	
    	DBhelper db=dbUility.getDB();
		Cursor c=db.queryItem(dbUility.CAR_TABLE,"num", num);
		if(!c.moveToFirst()) 
		{
			Toast.makeText(this, "数据获取失败，抱歉～", Toast.LENGTH_LONG).show();
			return;
		}
		
		try {
			JSONObject js=new JSONObject(c.getString(c.getColumnIndex("car")));
			startDetailAct(num,js);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void startDetailAct(String num,JSONObject js)
	{
		
		
		Intent i=new Intent();
		DBUility dbUility=(DBUility)getApplicationContext();
    	
    	DBhelper db=dbUility.getDB();
    	
    	Cursor c=db.queryItem(dbUility.CAR_TABLE, "num", num);
    	
    
    		if(!c.moveToFirst()) 
    		{
    			Toast.makeText(this, "数据获取失败，抱歉～", Toast.LENGTH_LONG).show();
    			return;
    		}	
    	
		i.putExtra("data", js.toString());
		i.putExtra("num", num);
		i.putExtra("time", c.getString(c.getColumnIndex("time")));
		i.setClass(More.this, Detail.class);
		
			startActivity(i);
			this.getParent().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
			
	}
	


	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		// TODO Auto-generated method stub
		arg0.dismiss();
		startDetailAct1(items[arg1]);
	}
	
	public void onShareTo(View v)
	{
		/*Intent i=new Intent();
		
		i.putExtra("type", "share");
		
		i.setClass(More.this, ShareDialog.class);
		
		startActivity(i);*/
		String cur_user=UserManager.getCurUser(this.getApplicationContext());
		if(cur_user.equals("NOUSER")||cur_user.equals("NOLOGIN"))
		{
			Intent i=new Intent();
			i.setClass(More.this, Dialog.class);
			i.putExtra("text", "提示\n\n您还没有登录车友圈，现在登录吗？");
			i.putExtra("ok","确定");
			i.putExtra("cancel", "取消");
			startActivityForResult(i,1);
		}
		else
		{
			Intent i=new Intent();
			i.setClass(More.this, GetScore.class);
			i.putExtra("nickname", cur_user);
			startActivity(i);
		}
		
	}
	public void onExit(View v)
	{
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode)
		{
		case 1://获取积分对话框返回
			if(resultCode==0)//确认
			{
				String cur_user=UserManager.getCurUser(this.getApplicationContext());
				if(cur_user.equals("NOUSER"))
				{
					Intent i=new Intent();
					i.setClass(More.this, Login.class);
					startActivityForResult(i,2);
					this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
			
				}
				else
				{
					Intent i=new Intent();
					i.setClass(More.this, SelectUser.class);
					startActivityForResult(i,2);
					this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
				}
			}
			break;
		case 2://登录返回
			if(resultCode==1)
			{
				UserManager.setCurUser(this.getApplicationContext(), data.getStringExtra("nickname"));
				onShareTo(null);
			}
			break;
		}
	}
	
	public void onMyMessage(View v)
	{
		String cur_nickname=UserManager.getCurUser(this.getApplicationContext());
		if(cur_nickname.equals("NOUSER")||cur_nickname.equals("NOLOGIN"))
		{
			Toast.makeText(this, "您还没登录～", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent i=new Intent();
		i.setClass(More.this, MyMessageList.class);
		
		
		startActivity(i);
		this.getParent().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
		
	}
	public void onMyWeibo(View v)
	{
		String cur_nickname=UserManager.getCurUser(this.getApplicationContext());
		
		if(cur_nickname.equals("NOUSER")||cur_nickname.equals("NOLOGIN"))
		{
			Toast.makeText(this, "您还没登录～", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent i=new Intent();
		i.setClass(More.this, UserWeiboList.class);
		
		i.putExtra("isId", false);
		i.putExtra("nickname", cur_nickname);
		
		i.putExtra("user_id", -1);
		
		startActivity(i);
		this.getParent().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);

	}
	
	public void onDraft(View v)
	{
		String cur_nickname=UserManager.getCurUser(this.getApplicationContext());
		
		if(cur_nickname.equals("NOUSER")||cur_nickname.equals("NOLOGIN"))
		{
			Toast.makeText(this, "您还没登录～", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent i=new Intent();
		i.setClass(More.this, DraftList.class);
		i.putExtra("nickname", cur_nickname);
		
		startActivity(i);
		this.getParent().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);

	}
}
