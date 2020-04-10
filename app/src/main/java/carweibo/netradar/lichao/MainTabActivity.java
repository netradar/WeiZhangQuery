package carweibo.netradar.lichao;


import net.youmi.android.AdManager;
import net.youmi.android.diy.DiyManager;

import android.os.Bundle;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost;


@SuppressWarnings("deprecation")
public class MainTabActivity extends FragmentActivity implements  OnClickListener {

	private TabHost mTabHost;
	RadioButton rb1;
	RadioButton rb2;
	RadioButton rb3;
	RadioButton rb4;
	int reAuth_flag=0;
	int rb1_id;
	int rb2_id;
	int rb3_id;
	int rb4_id;
	BroadcastReceiver bdReceiver;
	private IntentFilter ifilter;
	TextView unread_weibo_textview,unread_msg_textview;
	int unread_weibo_num=0,unread_msg_num=0,unread_vender_num=0,unread_audio_lukuang_num=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabmain_layout);
       // RadioGroup gr=(RadioGroup)findViewById(R.id.main_radio);
        unread_weibo_textview=(TextView)findViewById(R.id.unread_weibo);
        unread_msg_textview=(TextView)findViewById(R.id.unread_msg);
     
        rb1=(RadioButton)findViewById(R.id.tab_weibo);
        rb2=(RadioButton)findViewById(R.id.tab_query);
        rb3=(RadioButton)findViewById(R.id.tab_lukuang);
        rb4=(RadioButton)findViewById(R.id.tab_setting);
        
        rb1.setOnClickListener(this);
        rb2.setOnClickListener(this);
        rb3.setOnClickListener(this);
        rb4.setOnClickListener(this);
        
        rb1.setChecked(true);
        rb1_id=rb1.getId();
        rb2_id=rb2.getId();
        rb3_id=rb3.getId();
        rb4_id=rb4.getId();
        
        VersionCheck();

        mTabHost = getTabHost();   
        mTabHost.addTab(mTabHost.newTabSpec("weibo").setIndicator("微博").setContent(new Intent(this,TimeLine.class)));

        mTabHost.addTab(mTabHost.newTabSpec("query").setIndicator("违章").setContent(new Intent(this,Query.class)));
     
        mTabHost.addTab(mTabHost.newTabSpec("lukuang").setIndicator("实时路况").setContent(new Intent(this,LuKuang.class)));

        mTabHost.addTab(mTabHost.newTabSpec("more").setIndicator("设置").setContent(new Intent(this,More.class)));
         
        mTabHost.setCurrentTabByTag("weibo");
      //  gr.check(R.id.tab_query);
      //  gr.setOnCheckedChangeListener(this);
        AdManager.getInstance(this).init("60de7214f824777a","f43be6d5eb0fc46d", false); 
        DiyManager.initAdObjects(this);
        
        bdReceiver=new BroadcastReceiver() {  
  		  
    	    @Override  
    	    public void onReceive(Context context, Intent intent) {  
    	          
    	    	
    	    	String bd_type=intent.getAction();
    	    	
    	    	if(bd_type.equals("netradar.bd"))
    	    	{
    	    		int type=intent.getIntExtra("type", -1);
	    	    	
	    	    	if(type!=4) return;
    	    	}
    	    	else if(bd_type.equals("netradar.bd.newlukuang"))
    	    	{
    	    		if(intent.getStringExtra("type").equals("lukuang"))
    	    		{
    	    			
    	    		}
    	    		else
    	    			return;
    	    	}
    	    	
    	    	
    	    	refreshMainUnReadTip();
    	    	
    	    }

			
    	  
    	}; 
    	ifilter= new IntentFilter(); 
    	ifilter.addAction("netradar.bd");
    	ifilter.addAction("netradar.bd.newlukuang");
    	registerReceiver(bdReceiver,ifilter);
    }
    
    
    @Override
	protected void onResume() {
    	refreshMainUnReadTip();
		super.onResume();
	}


	private void refreshMainUnReadTip() {
		
    	unread_weibo_num=(int) ((int) getUnReadWeiboNum()+getUnReadVenderNum());
   // 	unread_vender_num=(int) getUnReadVenderNum();
    //	unread_audio_lukuang_num=getUnReadLukuangNum();
    	unread_msg_num=MessageManager.getUnreadSum(this.getApplicationContext());
    	refreshUnreadTip();
	}  
  
	 private long getUnReadVenderNum() {
			
			long max_vender_id=NvManager.getNVLong(this, NvManager.MAX_VENDER_ID, -1);
			long readed_vender_id=NvManager.getNVLong(this, NvManager.READED_VENDER_ID, -1);
			
			if(max_vender_id==-1) return 0;
			
			if(readed_vender_id==-1) return  max_vender_id;
			
			if(max_vender_id>=readed_vender_id) return max_vender_id-readed_vender_id;
			return 0;
		}

	private long getUnReadWeiboNum() {
    	
		
		SharedPreferences pre= getSharedPreferences("carweibo_unread_tips", Context.MODE_PRIVATE);
		
		long readed_weibo_id=pre.getLong("readed_weiboid", -1);
		long max_weibo_id=pre.getLong("max_weiboid", -1);
		
		if(max_weibo_id==-1) return 0;
		
		if(readed_weibo_id==-1) return max_weibo_id;
		
		if(max_weibo_id>=readed_weibo_id) return max_weibo_id-readed_weibo_id;
		
		return 0;
		

	}
    private int getUnReadLukuangNum() {
    	
		
		SharedPreferences pre= getSharedPreferences("carweibo_unread_tips", Context.MODE_PRIVATE);
		
		return pre.getInt("unreaded_lukuang", 0);
		
		

	}
    private void VersionCheck()
    {
    	new VersionCheck(this).update(true);
    }

    

	
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
		unregisterReceiver(bdReceiver);
		
		SharedPreferences pre= getSharedPreferences("carweibo_my_message", Context.MODE_PRIVATE);
		Editor editor = pre.edit();
		
		editor.putBoolean("my_msg_run",false);
		editor.commit();
	}
	@Override
	public void onClick(View arg0) {
		
		if(arg0.getId()==rb1_id)
		{
			rb2.setChecked(false);
			rb3.setChecked(false);
			rb4.setChecked(false);
			mTabHost.setCurrentTab(0);
		
		}
		if(arg0.getId()==rb2_id)
		{
			rb1.setChecked(false);
			rb3.setChecked(false);
			rb4.setChecked(false);
			mTabHost.setCurrentTab(1);
			/*if(mTabHost.getCurrentTab()==1)
			{
				
			}
			
			String cur_user=UserManager.getCurUser(this.getApplicationContext());
			//String isLogin=UserManager.isCurUserLogin(this.getApplicationContext());
			//String isUserValid=UserManager.isUserValid(this.getApplicationContext());
			processUserStatus(cur_user);*/
			
		}
		if(arg0.getId()==rb3_id)
		{
			rb1.setChecked(false);
			rb2.setChecked(false);
			rb4.setChecked(false);
			mTabHost.setCurrentTab(2);
		}
		if(arg0.getId()==rb4_id)
		{
			rb1.setChecked(false);
			rb2.setChecked(false);
			rb3.setChecked(false);
			mTabHost.setCurrentTab(3);
		}
		
	}
	
	private void processUserStatus(String cur_user)
	{
		
		if(cur_user.equals("NOUSER"))
		{
			newUserLogin();
			return;
		}
		if(cur_user.equals("NOLOGIN"))
		{
		
			selectUser();
			return;
		}
		UserInfo userinfo=UserManager.getSingleUserInfo(this.getApplicationContext(), cur_user);
		if(userinfo==null) {newUserLogin();return;}
		
		normalLogin();
		
		/*Log.d("lichao",userinfo.toString());
		String usertype=userinfo.user_type;
		if(usertype.equals("QQ")||usertype.equals("Weibo"))
		{
			if(isUserExpires(userinfo))
			{
				userReAuth(usertype,userinfo);
			}
			else
			{
				normalLogin();
			}
			
			
		}*/
	}
	private void selectUser()
	{
		rb2.setChecked(false);
		Intent i=new Intent();
		i.setClass(MainTabActivity.this, SelectUser.class);
		startActivityForResult(i,2);
		this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
		Toast.makeText(this, "您还没登录，请选择一个用户登录～", Toast.LENGTH_SHORT).show();
		
	}
	private void normalLogin()
	{
		rb1.setChecked(false);
		rb3.setChecked(false);
		rb4.setChecked(false);
		mTabHost.setCurrentTab(1);
	}
	private void newUserLogin()
	{
		rb2.setChecked(false);
		Toast.makeText(this, "请先登录～", Toast.LENGTH_SHORT).show();
		Intent i=new Intent();
		i.setClass(mTabHost.getCurrentTabView().getContext(), Login.class);
		
		this.startActivityForResult(i, 1);
		overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);//全新登录，数据库没有用户数据
	}
	/*
	private void userReAuth(String usertype,UserInfo userinfo)
	{
		if(usertype.equals("QQ"))
		{
			rb2.setChecked(false);
			Toast.makeText(this, "QQ授权过期，需要您重新授权～", Toast.LENGTH_SHORT).show();
			
			showLogin(LOGIN_REQUEST_CODE_REAUTH_QQ,userinfo);//QQ用户授权过期，重新授权
		}
		else
		{
			rb2.setChecked(false);
			Toast.makeText(this, "微博授权过期，需要您重新授权～", Toast.LENGTH_SHORT).show();
			
			showLogin(LOGIN_REQUEST_CODE_REAUTH_WEIBO,null);//微博用户授权过期，重新授权
		}
	}
	private boolean isUserExpires(UserInfo userinfo)
	{
		long expire_time=Long.parseLong(userinfo.expires_in);
		long auth=Long.parseLong(userinfo.auth_time);
		long cur_time=System.currentTimeMillis()/1000;
		
		if((auth+expire_time)<cur_time)
		{
				
			return true;
		}
		return true;
	}*/
	
 /*
	private boolean isLogin()
	{
		reAuth_flag=0;
		SharedPreferences pre_cur_user = getSharedPreferences("setting_cur_user", MODE_PRIVATE);
		
		String cur_user=pre_cur_user.getString("cur_user", "nologin");
		if(cur_user.equals("nologin"))
		{
			return false;
		}
		if(cur_user.equals("QQ"))
		{
			if(isQqValid())
				return true;
			else
			{
				Toast.makeText(this, "QQ授权过期，正在进行重新授权～", Toast.LENGTH_SHORT).show();
				reAuth_flag=1;
				return false;
			}
		}
		if(cur_user.equals("WeiBo"))
		{
			if(isWeiboValid())
				return true;
			else
			{
				Toast.makeText(this, "新浪微博授权过期，正在进行重新授权～", Toast.LENGTH_SHORT).show();
				reAuth_flag=2;
				return false;
			}
		}
		return false;
	}*/


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
			switch(resultCode)
		
			{
			case 0://Login or Select activity cancel
				break;
			case 1://Login or Select activity success return
				UserManager.setCurUser(this.getApplicationContext(), data.getStringExtra("nickname"));
				rb1.setChecked(false);
				rb2.setChecked(true);
				rb3.setChecked(false);
				rb4.setChecked(false);
				mTabHost.setCurrentTab(1);
				
				break;
			}
		
	}
	
	private void refreshUnreadTip()
	{
		if(unread_weibo_num==0)
			unread_weibo_textview.setVisibility(View.GONE);
		else 
		{
			unread_weibo_textview.setVisibility(View.VISIBLE);
			if(unread_weibo_num>99)
			{
				unread_weibo_textview.setText("99+");
			}
			else
				unread_weibo_textview.setText(String.valueOf(unread_weibo_num));
		}
		
		if(unread_msg_num==0)
			unread_msg_textview.setVisibility(View.GONE);
		else 
		{
			unread_msg_textview.setVisibility(View.VISIBLE);
			if(unread_msg_num>99)
			{
				unread_msg_textview.setText("99+");
			}
			else
				unread_msg_textview.setText(String.valueOf(unread_msg_num));
		}
	}
	/*
	private boolean isQqValid()
	{
		SharedPreferences pre_qq = getSharedPreferences("setting_qq", MODE_PRIVATE);
		String token=pre_qq.getString("token", "0");
		String expires_in=pre_qq.getString("expires_in", "0");
		String openid=pre_qq.getString("openid", "0");
		long auth_time=pre_qq.getLong("auth_time", 0);
		long cur_time=System.currentTimeMillis()/1000;
	
		if(token.equals("0")||expires_in.equals("0")||openid.equals("0")||auth_time==0)
		{
			//Toast.makeText(this, "QQ授权过期，正在进行重新授权～", Toast.LENGTH_SHORT).show();
			 return false;
		}
		
		long expire_time=Long.parseLong(expires_in);
		if((auth_time+expire_time)<cur_time)
		{
			//Toast.makeText(this, "QQ授权过期，正在进行重新授权～", Toast.LENGTH_SHORT).show();
			
			return false;
		}
		return true;
	}
	private boolean isWeiboValid()
	{
		SharedPreferences pre_weibo = getSharedPreferences("setting_weibo", MODE_PRIVATE);
		String token=pre_weibo.getString("token", "0");
		String expires_in=pre_weibo.getString("expires_in", "0");
		long auth_time=pre_weibo.getLong("auth_time", 0);
		long cur_time=System.currentTimeMillis()/1000;
		
		if(token.equals("0")||expires_in.equals("0")||auth_time==0)
		{
			
			return false;
		}
		long expire_time=Long.parseLong(expires_in);
		
		if((auth_time+expire_time)<cur_time)
		{
			//Toast.makeText(this, "微博授权过期，正在进行重新授权～", Toast.LENGTH_SHORT).show();
			
			return false;
		}
		return true;
	}
	*/
	
}
