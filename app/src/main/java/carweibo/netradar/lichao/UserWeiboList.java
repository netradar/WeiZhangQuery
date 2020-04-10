package carweibo.netradar.lichao;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carweibo.netradar.lichao.AsyncImageLoader.ImageCallback;
import carweibo.netradar.lichao.WeiboDetail.FOOTVIEWSTATUS;
import carweibo.netradar.lichao.WeiboDetail.GetCommentInfo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserWeiboList extends Activity implements OnItemClickListener, ImageCallback {

	private static int LOADIMG_TYPE_TOUXIANG=0;
	Context context;
	public enum FOOTVIEWSTATUS {
		NONE, REFRESHING, PUSHTOGET,SHOWTIP

	};
	public class GetUserWeiboList extends AsyncTask<String, Integer, String> {

		int user_id;
		int readed_id;
		boolean isTop;
		String nickname;
		
		@Override
		protected void onPostExecute(String result) {
			
			processUserWeiboReturn(result,readed_id);
			super.onPostExecute(result);
		}

		@Override
		protected String doInBackground(String... arg0) {
			nickname=arg0[0];
			user_id=Integer.parseInt(arg0[1]);
			readed_id=Integer.parseInt(arg0[2]);
			if(arg0[3].equals("true"))
				isTop=true;
			else
				isTop=false;
			
			return new LoginToServer().GetUserWeibo(context, user_id, readed_id, nickname, isTop);
		
		}

	}

	ListView weibo_listview;
	RelativeLayout userinfo_layout_header;
	LinearLayout nav_layout_header;
	LinearLayout foot_layout;
	ImageView touxiang_imageview,vip_imageview;
	TextView nickname_textview,time_textview,weibonum_textview,replynum_textview,grade_textview;
	Button send_msg_btn;
	
	List<ImageAndText> weibo_list;
	boolean isId;
	String nickname;
	int user_id;
	ImageAndTextListAdapter adapter;
	
	String server_url;
	float denisty;
	LinearLayout getting_info;
	Button getmore_info;
	TextView foot_tip;
	BroadcastReceiver bdReceiver;
	private IntentFilter ifilter;
	AsyncImageLoader asyncimageloader;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.user_weibo_layout);
		
		context=this;
			
		initView();
		initData();
		bdReceiver=new BroadcastReceiver() {  
	  		  
    	    @Override  
    	    public void onReceive(Context context, Intent intent) {  
    	        // TODO Auto-generated method stub   
    	    	
    	    	int type=intent.getIntExtra("type", -1);
    	    	
    	    	switch(type)
    	    	{
    	    	case 0://failure hint
    	    		
    	    		break;
    	    	default:
    	    		processUI(type,intent.getLongExtra("id", -1));
    	    		break;
    	    	}
    	    	
    	    }  
    	  
    	}; 
    	
    	ifilter= new IntentFilter(); 
    	ifilter.addAction("netradar.bd");
	}
	
	
	@Override
	protected void onStart() {
		
		super.onStart();
		registerReceiver(bdReceiver,ifilter);
		Log.d("lichao","onStart");
	}


	@Override
	protected void onStop() {
		
		super.onStop();
		unregisterReceiver(bdReceiver);
		Log.d("lichao","onStop");
	}


	protected void processUI(int type, long id) {
		if(id==-1) return;
		
		int i=0;
		for(ImageAndText imt:weibo_list)
		{
			if(imt.id==id)
			{
				break;
			}
			i++;
		}
		if(i==weibo_list.size()) return;
		switch(type)
		{
		case 1://good
			String tmp=weibo_list.get(i).good_num;
			
			int num=Integer.parseInt(tmp.substring(0, tmp.indexOf("个")));
			ImageAndText imt=new ImageAndText();
			imt=weibo_list.get(i);
			imt.good_num=String.valueOf(num+1)+"个赞";
			weibo_list.set(i, imt);
			adapter.notifyDataSetChanged();
			break;
			
    	case 2://retweet
    		String tmp1=weibo_list.get(i).retweet_num;
			
			int num1=Integer.parseInt(tmp1.substring(0, tmp1.indexOf("个")));
			ImageAndText imt1=new ImageAndText();
			imt1=weibo_list.get(i);
			imt1.retweet_num=String.valueOf(num1+1)+"个分享";
			weibo_list.set(i, imt1);
			adapter.notifyDataSetChanged();
			break;
    	case 3://comment
    		String tmp3=weibo_list.get(i).comment_num;
			
			int num3=Integer.parseInt(tmp3.substring(0, tmp3.indexOf("个")));
			ImageAndText imt3=new ImageAndText();
			imt3=weibo_list.get(i);
			imt3.comment_num=String.valueOf(num3+1)+"个评论";
			weibo_list.set(i, imt3);
			adapter.notifyDataSetChanged();
			break;
		}
		
	}

	JSONObject userinfo=null;
	public void processUserWeiboReturn(String result,int readed_id) {
		
		if(result.equals("error_data"))
		{
			Toast.makeText(this, "网络或数据异常，请稍候再试", Toast.LENGTH_SHORT).show();
			refreshFootView(FOOTVIEWSTATUS.PUSHTOGET);
			return;
		}
		try 
		{
			JSONObject js=new JSONObject(result);
			if(readed_id==-1)
			{
				userinfo=js.getJSONObject("userDetailInfo");
				if(userinfo!=null&&userinfo.length()!=0)
				{
					
					String time=userinfo.getString("time");
					nickname=URLDecoder.decode(userinfo.getString("nickname"),"UTF-8");
					time_textview.setText(time.substring(0,4)+"年"+time.substring(4,6)+"月"+time.substring(6,8)+"日");
					weibonum_textview.setText(userinfo.getInt("weibo_num")+"条");
					replynum_textview.setText(userinfo.getInt("reply_num")+"条");
					if(userinfo.getInt("user_score")>5000)
					{
						grade_textview.setText("VIP会员");
					//	vip_imageview.setVisibility(View.VISIBLE);
					
					}
					else
					{
						grade_textview.setText("普通用户");
					//	vip_imageview.setVisibility(View.GONE);
					}
					
				
					/*touxiang_imageview.setTag(-1+((DBUility)this.getApplicationContext()).webapps+"/pic/touxiang/"+userinfo.getString("touxiang_url"));
					Bitmap touxiang_bmp=asyncimageloader.loadDrawable(((DBUility)this.getApplicationContext()).webapps+"/pic/touxiang/"+userinfo.getString("touxiang_url"), "-1", this, LOADIMG_TYPE_TOUXIANG);
					if(touxiang_bmp==null)
					{
						touxiang_imageview.setImageResource(R.drawable.switchuser);
					}
					else
						touxiang_imageview.setImageBitmap(touxiang_bmp);*/
				}
				
			}
			if(userinfo!=null)
			{
				refreshWeiboList(js.getJSONArray("weibo_list"),userinfo);
				if(!UserManager.getCurUser(this.getApplicationContext()).equals(nickname))
					send_msg_btn.setVisibility(View.VISIBLE);
			}
			else
				refreshFootView(FOOTVIEWSTATUS.PUSHTOGET);
			
		} catch (JSONException e) {
			Toast.makeText(this, "网络或数据异常，请稍候再试", Toast.LENGTH_SHORT).show();
			refreshFootView(FOOTVIEWSTATUS.PUSHTOGET);
		} catch (UnsupportedEncodingException e) {
			Toast.makeText(this, "网络或数据异常，请稍候再试", Toast.LENGTH_SHORT).show();
			refreshFootView(FOOTVIEWSTATUS.PUSHTOGET);
		}
	}

	private void refreshWeiboList(JSONArray jsonArray, JSONObject userinfo) {
		
		
		for(int i=0;i<jsonArray.length();i++)
		{
			try {
				JSONObject json=jsonArray.getJSONObject(i);
				json.put("nickname", userinfo.getString("nickname"));
				json.put("touxiang_url", userinfo.getString("touxiang_url"));
				json.put("user_score", userinfo.getInt("user_score"));
				weibo_list.add(new ImageAndText(server_url,json,denisty));
			} catch (JSONException e) {
				
			}
			
		}
		adapter.notifyDataSetChanged();
		if(jsonArray.length()>=25)
		{
			refreshFootView(FOOTVIEWSTATUS.PUSHTOGET);
		}
		else
			refreshFootView(FOOTVIEWSTATUS.NONE);
	}

	private void initView() {
		weibo_listview=(ListView)findViewById(R.id.user_weibo_listview);
		userinfo_layout_header=(RelativeLayout)LayoutInflater.from(this).inflate(R.layout.user_weibo_userinfo_layout,null);
		nav_layout_header=(LinearLayout)LayoutInflater.from(this).inflate(R.layout.user_weibo_userinfo_nav_layout,null);
		foot_layout=(LinearLayout)LayoutInflater.from(this).inflate(R.layout.comment_detail_foot,null);
		
	//	touxiang_imageview=(ImageView)userinfo_layout_header.findViewById(R.id.user_weibo_userinfo_touxiang);
	//	vip_imageview=(ImageView)userinfo_layout_header.findViewById(R.id.user_weibo_userinfo_vip_sign);
		nickname_textview=(TextView)userinfo_layout_header.findViewById(R.id.user_weibo_userinfo_nickname);
		time_textview=(TextView)userinfo_layout_header.findViewById(R.id.user_weibo_userinfo_time);
		weibonum_textview=(TextView)userinfo_layout_header.findViewById(R.id.user_weibo_userinfo_weibonum);
		replynum_textview=(TextView)userinfo_layout_header.findViewById(R.id.user_weibo_userinfo_replynum);
		grade_textview=(TextView)userinfo_layout_header.findViewById(R.id.user_weibo_userinfo_grade);
		send_msg_btn=(Button)this.findViewById(R.id.user_weibo_msg_btn);
		
		weibo_listview.addHeaderView(userinfo_layout_header);
		weibo_listview.addHeaderView(nav_layout_header);
		weibo_listview.addFooterView(foot_layout);
		
		weibo_listview.setOnItemClickListener(this);
		
		getting_info=(LinearLayout)foot_layout.findViewById(R.id.getting_info);
		getmore_info=(Button)foot_layout.findViewById(R.id.moreinfo);
		foot_tip=(TextView)foot_layout.findViewById(R.id.comment_detail_foot_tip);
		
	
	}
	private void initData() {
		
		isId=this.getIntent().getBooleanExtra("isId", true);
		nickname=this.getIntent().getStringExtra("nickname");
		user_id=this.getIntent().getIntExtra("user_id", -1);
		
		if(isId&&user_id==-1) this.finish();
		
		nickname_textview.setText(nickname);
		
		weibo_list=new ArrayList<ImageAndText>();
		adapter=new ImageAndTextListAdapter(this,weibo_list,weibo_listview,getNickName());
		weibo_listview.setAdapter(adapter);
		

		denisty=this.getResources().getDisplayMetrics().density;
		
		server_url=((DBUility)context.getApplicationContext()).webapps+"/pic";
		
		if(isId)
			new GetUserWeiboList().execute("",String.valueOf(user_id),"-1","true");
		else
			new GetUserWeiboList().execute(nickname,"-1","-1","false");
		refreshFootView(FOOTVIEWSTATUS.REFRESHING);
		
		asyncimageloader=new AsyncImageLoader(denisty);
		
		send_msg_btn.setVisibility(View.GONE);
	}
	int delete_pos;
	public void onDeleteWeibo(View v)
	{
		delete_pos=(Integer) v.getTag();
		Intent i=new Intent();
		
		
		i.putExtra("text", "提示\n\n"+"确定删除帖子吗？");
		i.putExtra("ok", "确定");
		i.putExtra("cancel", "取消");
		i.setClass(UserWeiboList.this, Dialog.class);
		this.startActivityForResult(i,1);
		
	}

	public void onShareWeibo(View v)
	{
		if(UserManager.getCurUser(this.getApplicationContext()).equals("NOLOGIN")||
				UserManager.getCurUser(this.getApplicationContext()).equals("NOUSER"))
		{
			Toast.makeText(this, "您还没有登录，不能进行此操作～", Toast.LENGTH_SHORT).show();
			return;
		}
		int pos=(Integer) v.getTag();
		
		
		String dir=getUploadPic((View) v.getParent().getParent());
		
		
		Intent i=new Intent();
		Bundle bd=new Bundle();
		bd.putSerializable("imt", adapter.getItem(pos));
		i.putExtra("dir", dir);
		i.putExtras( bd);
		i.setClass(UserWeiboList.this, ShareDialog.class);
		
		startActivity(i);
	}
	public void onGood(View v)
	{
		if(UserManager.getCurUser(this.getApplicationContext()).equals("NOLOGIN")||
				UserManager.getCurUser(this.getApplicationContext()).equals("NOUSER"))
		{
			Toast.makeText(this, "您还没有登录，不能进行此操作～", Toast.LENGTH_SHORT).show();
			return;
		}
		int pos=(Integer)v.getTag();
		ImageAndText imt=adapter.getItem(pos);
		
		if(imt.grade==2)
			new SendWeibo().sendGood(this, getNickName(), imt.id, true);
		else
			new SendWeibo().sendGood(this, getNickName(), imt.id, false);
		
		
	}
	private String getNickName()
	{
		return UserManager.getCurUser(this.getApplicationContext());
		
	}

	@Override
	public void onBackPressed() {

		this.finish();
		this.overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
		
	}
	
	public void onCancelUserWeibo(View v)
	{
		onBackPressed();
	}
	private String getUploadPic(View v)
	{

		String SDDir=ScreenShoot.getSDDir(this);
		
		if(SDDir==null)
		{
			Toast.makeText(this, "T卡空间不足，不能进行分享～", Toast.LENGTH_SHORT).show();
			return null;
		}
       
		 Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(),  
	                Bitmap.Config.ARGB_8888);  
		 
		 Canvas canvas = new Canvas(b);
		 v.draw(canvas);
		 ScreenShoot.savePic(b,SDDir+"/weizhangquery/cache",SDDir+"/weizhangquery/cache/"+"tmp.png",100);
			
		return SDDir+"/weizhangquery/cache/"+"tmp.png";
		
	}
	
	public void onUserWeibo(View v)
	{
		
	}

	int detail_pos;
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(arg2==0||arg2==1) return;
		detail_pos=arg2-2;
		ImageAndText imt=adapter.getItem(detail_pos);
		
		
		Intent i=new Intent();
		Rect frame=new Rect();
		this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int screenWidth  = getWindowManager().getDefaultDisplay().getWidth();       // 屏幕宽（像素，如：480px）   
		int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		
		
		Bundle bd=new Bundle();
		i.setClass(UserWeiboList.this, WeiboDetail.class);
		bd.putSerializable("data", imt);
		i.putExtras(bd);
		i.putExtra("stateHeight", frame.top);
		i.putExtra("screenHeight",screenHeight);
		i.putExtra("screenWidth",screenWidth);
		
		this.startActivityForResult(i, 2);
		this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
		
		
		
	}

	public void onGetMoreComment(View v)
	{
		
		refreshFootView(FOOTVIEWSTATUS.REFRESHING);
		long readed_id=0;
		String isTop;
		Log.d("lichao","size is "+weibo_list.size());
		if(weibo_list.size()==0)
		{
			readed_id=-1;
			isTop="true";
		}
		else
		{
			readed_id=weibo_list.get(weibo_list.size()-1).id;
			if(weibo_list.get(weibo_list.size()-1).grade==2)
				isTop="true";
			else
				isTop="false";
		}
		
		
		if(isId)
			new GetUserWeiboList().execute("",String.valueOf(user_id),String.valueOf(readed_id),isTop);
		else
			new GetUserWeiboList().execute(nickname,"-1",String.valueOf(readed_id),isTop);
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==1)
		{
			if(resultCode==0)
			{
				if(weibo_list.get(delete_pos).grade==2)
					new SendWeibo().deleteWeibo(this, getNickName(),weibo_list.get(delete_pos).id,true);
				else
					new SendWeibo().deleteWeibo(this, getNickName(),weibo_list.get(delete_pos).id,false);
			
				weibo_list.remove(delete_pos);
				adapter.notifyDataSetChanged();
			}
		}
		if(requestCode==2)
		{
			if(resultCode==0)
			{
				
					weibo_list.remove(detail_pos);
					adapter.notifyDataSetChanged();
				
			}
		}
	}
	
	private  void refreshFootView(FOOTVIEWSTATUS status)
	{
		if(status==FOOTVIEWSTATUS.NONE)
		{
			//footView.setVisibility(View.GONE);
			foot_layout.setVisibility(View.GONE);
			getting_info.setVisibility(View.GONE);
			getmore_info.setVisibility(View.GONE);
			foot_tip.setVisibility(View.VISIBLE);
			
			foot_tip.setTextColor(Color.parseColor("#222000"));
			foot_tip.setText("已加载全部数据");
			
		}
	//	footView.setLayoutParams(new LayoutParams(footView.getWidth(),(int) (50*denisty)));
		if(status==FOOTVIEWSTATUS.REFRESHING)
		{
			foot_layout.setVisibility(View.VISIBLE);
			getting_info.setVisibility(View.VISIBLE);
			getmore_info.setVisibility(View.GONE);
			foot_tip.setVisibility(View.GONE);
		}
		if(status==FOOTVIEWSTATUS.PUSHTOGET)
		{
			foot_layout.setVisibility(View.VISIBLE);
			getting_info.setVisibility(View.GONE);
			getmore_info.setVisibility(View.VISIBLE);
			foot_tip.setVisibility(View.GONE);
		}
		if(status==FOOTVIEWSTATUS.SHOWTIP)
		{
			foot_layout.setVisibility(View.VISIBLE);
			getting_info.setVisibility(View.GONE);
			getmore_info.setVisibility(View.GONE);
			foot_tip.setVisibility(View.VISIBLE);
			
			foot_tip.setTextColor(Color.parseColor("#999000"));
			foot_tip.setText("该用户暂时没有帖子");
		}
	}

	public void onSendPrivateMsg(View v)
	{
		if(userinfo==null) return;
		
		if(UserManager.getCurUser(this.getApplicationContext()).equals("NOLOGIN")||
				UserManager.getCurUser(this.getApplicationContext()).equals("NOUSER"))
		{
			Toast.makeText(this, "您还没有登录，不能给好友私信～", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent i=new Intent();
		i.setClass(UserWeiboList.this, PrivateMsgChat.class);
	
		try {
			i.putExtra("user_id", userinfo.getInt("user_id"));
			i.putExtra("nickname",nickname );
			
			i.putExtra("touxiang_url", ((DBUility)this.getApplicationContext()).webapps+"/pic/touxiang/"+userinfo.getString("touxiang_url"));
			this.startActivity(i);
			this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
			
			
		} catch (JSONException e) {
			
		}
		
	}
	
	public void onViewPic(View v)
	{
		String tag=(String)v.getTag();
		int pos=Integer.parseInt(tag.substring(0,tag.indexOf("http")));
		
	
		ImageAndText imt=adapter.getItem(pos);
		
		tag=tag.substring(tag.indexOf("http"),tag.length());
		
		if(imt.pic_list_url.size()==0) return;
		
		int k=0;
		for(k=0;k<imt.pic_list_url.size();k++)
		{
			if(imt.pic_list_url.get(k).url.equals(tag))
				break;
		}
		
	
		
		Intent i=new Intent();
		Rect frame=new Rect();
		this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int screenWidth  = getWindowManager().getDefaultDisplay().getWidth();       // 屏幕宽（像素，如：480px）   
		int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		
		i.setClass(UserWeiboList.this, ViewPic.class);
		Bundle bd=new Bundle();
	
		bd.putSerializable("data", imt);
		i.putExtra("index", k);
		i.putExtra("height", frame.top);
		i.putExtra("screenWidth",screenWidth);
		i.putExtra("screenHeight", screenHeight);
		i.putExtras(bd);
		startActivity(i);
	}

	@Override
	public void imageLoaded(Bitmap imageDrawable, String imageUrl, String id) {
		ImageView iv=(ImageView) userinfo_layout_header.findViewWithTag(id+imageUrl);
		if(iv!=null)
		{
			if(imageDrawable!=null)
				iv.setImageBitmap(imageDrawable);
		}
		
	}
}
