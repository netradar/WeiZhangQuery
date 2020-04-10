package carweibo.netradar.lichao;

import heart4.netradar.lichao.Heart4MainActivity;
import net.youmi.android.diy.DiyManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class TimeLine extends Activity implements OnClickListener {
	
	String[] items={"加油站","停车场","超市","银行","餐饮"};
	
	BroadcastReceiver bdReceiver;
	private IntentFilter ifilter;
	
	TextView unread_weibo;
	TextView unread_vender;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.timeline_layout);
		unread_weibo=(TextView)findViewById(R.id.timeline_unread_weibo);
		unread_vender=(TextView)findViewById(R.id.timeline_unread_vender);
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
	    	    	
	    	    	
	    	    	refreshUnReadTip();
	    	    	
	    	    }

				
	    	  
	    	}; 
	    	ifilter= new IntentFilter(); 
	    	ifilter.addAction("netradar.bd");
	    	ifilter.addAction("netradar.bd.newlukuang");
	    	registerReceiver(bdReceiver,ifilter);
	}
	
	
	@Override
	protected void onResume() {
		refreshUnReadTip();
		super.onResume();
	}


	@Override
	protected void onDestroy() {
		unregisterReceiver(bdReceiver);
		super.onDestroy();
	}


	int unread_weibo_num=0,unread_audio_lukuang_num=0,unread_vender_num=0;
	protected void refreshUnReadTip() {
		
		unread_weibo_num=(int) getUnReadWeiboNum();
	//	unread_audio_lukuang_num=getUnReadLukuangNum();
		unread_vender_num=(int)getUnReadVenderNum();
		refreshUnreadTip();
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
	  private long getUnReadVenderNum() {
			
			long max_vender_id=NvManager.getNVLong(this, NvManager.MAX_VENDER_ID, -1);
			long readed_vender_id=NvManager.getNVLong(this, NvManager.READED_VENDER_ID, -1);
			
			if(max_vender_id==-1) return 0;
			
			if(readed_vender_id==-1) return  max_vender_id;
			
			if(max_vender_id>=readed_vender_id) return max_vender_id-readed_vender_id;
			return 0;
		}

	 private int getUnReadLukuangNum() {
	    	
			
			SharedPreferences pre= getSharedPreferences("carweibo_unread_tips", Context.MODE_PRIVATE);
			
			return pre.getInt("unreaded_lukuang", 0);
			
			

		}
	 private boolean isRunningLukuang()
	 {
		 SharedPreferences pre= getSharedPreferences("carweibo_unread_tips", Context.MODE_PRIVATE);
			
			return pre.getBoolean("is_running_lukuang", false);
	 }
	 private void refreshUnreadTip()
		{
		 
			if(unread_weibo_num==0)
				unread_weibo.setVisibility(View.GONE);
			else 
			{
				unread_weibo.setVisibility(View.VISIBLE);
				if(unread_weibo_num>99)
				{
					unread_weibo.setText("99+");
				}
				else
					unread_weibo.setText(String.valueOf(unread_weibo_num));
			}
			if(unread_vender_num==0)
				unread_vender.setVisibility(View.GONE);
			else 
			{
				unread_vender.setVisibility(View.VISIBLE);
				if(unread_vender_num>99)
				{
					unread_vender.setText("99+");
				}
				else
					unread_vender.setText(String.valueOf(unread_vender_num));
			}
			
	/*		if(unread_audio_lukuang_num==0)
	//			unread_audio_lukuang.setVisibility(View.GONE);
			else 
			{
	//			unread_audio_lukuang.setVisibility(View.VISIBLE);
				if(unread_audio_lukuang_num>99)
				{
					unread_audio_lukuang.setText("99+");
				}
				else
					unread_audio_lukuang.setText(String.valueOf(unread_audio_lukuang_num));
			}*/
		}
	public void onCarcircle(View v)
	{
		Intent i=new Intent();
		
		i.setClass(TimeLine.this, CarCircle.class);
		
		this.startActivity(i);
		this.getParent().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
		
	}
	public void onAudioLukuang(View v)
	{
		if(ScreenShoot.getSDDir(this)==null)
		{
			Toast.makeText(this, "没有检测到SD卡，无法使用该功能", Toast.LENGTH_SHORT).show();
			return;
		}
		
		Intent i=new Intent();
		
		i.setClass(TimeLine.this, AudioLukuang.class);
		
		this.startActivity(i);
		this.getParent().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
		
	}
	public void onNear(View v)
	{
		new AlertDialog.Builder(TimeLine.this).setTitle("选择").setSingleChoiceItems(items, 0,this).show();
			
	}
	public void onRecommend(View v)
	{
		DiyManager.showRecommendAppWall(this); 
		this.getParent().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
		
	}
	public void onShare(View v)
	{
		Intent i=new Intent();
		
		i.setClass(TimeLine.this, GetScore.class);
		
		this.startActivity(i);
		
	}
	
	public void onVenderList(View v)
	{
		if(NvManager.getNVBoolean(this, NvManager.IS_VENDER_FIRST_RUN, true))
		{
			Intent i=new Intent();
			i.setClass(TimeLine.this, InfoNoticeDialog.class);
			i.putExtra("notice", "		\n在“商户点评”里，大家可以把自已去过的吃喝玩乐、避暑纳凉、修车美容、购物理财、等等所有跟生活相关的商家、店铺的评价发布出来，表扬或吐槽都行。\n\n			让广大车友能吸取互相的经验，更好的选择消费去处，抵制不良商家！\n\n");//R.string.first_vender_info);
			i.putExtra("btn", "进入");
			startActivityForResult(i,1);
			return;
		}
		
		startVenderList();
	}

	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		
		arg0.cancel();
		Intent i=new Intent();
		i.setClass(TimeLine.this, PoiList.class);
		i.putExtra("title", items[arg1]);
		this.startActivity(i);
		this.getParent().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
		
		
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode)
		{
		case 1:
			NvManager.setNVBoolean(this, NvManager.IS_VENDER_FIRST_RUN, false);
			startVenderList();
			break;
		}
	}


	private void startVenderList() {
		
		NvManager.setNVLong(this, NvManager.READED_VENDER_ID, NvManager.getNVLong(this, NvManager.MAX_VENDER_ID, 0));
		Intent i=new Intent();
		i.setClass(TimeLine.this, VenderList.class);
		
		this.startActivity(i);
		this.getParent().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
		
	}
	public void onGame(View v)
	{
		Intent i=new Intent();
		i.setClass(TimeLine.this, Heart4MainActivity.class);
		String nickname= UserManager.getCurUser(this.getApplicationContext());
		i.putExtra("nickname",nickname);
		
		if(nickname.equals("NOLOGIN")||nickname.equals("NOUSER"))
		{
			
		}
		else
			i.putExtra("touxiang", UserManager.getSingleUserInfo(this.getApplicationContext(), UserManager.getCurUser(this.getApplicationContext())).touxiang_url);
		this.startActivity(i);
	}
	
}
