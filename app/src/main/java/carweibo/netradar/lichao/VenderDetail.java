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
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class VenderDetail extends Activity implements ImageCallback, OnScrollListener {


	private static int LOADIMG_TYPE_MULTI=2;
	Context context;
	VenderInfo vender;
	
	TextView vender_name,vender_addr,vender_comment,nav_bad_text,nav_good_text,nav_bad_text1,nav_good_text1;
	ListView vender_list_view;
	
	List<CommentInfo> good_comment_list,bad_comment_list;
	
	CommentAdapter good_adapter,bad_adapter,cur_adapter;
	
	LinearLayout content_header,nav_header,nav_layout,blank_foot,footView;
	float denisty;
	LinearLayout getting_info;
	int minListHeight;
	Button getmore_info;
	TextView foot_tip;
	int screenHeight,screenWidth;
	int stateHeight;
	
	boolean isCurListBad=false;
	
	ImageView badNav,goodNav,badNav1,goodNav1;
	
	ImageView image1,image2,image3;
	LinearLayout nav_self_layout;
	
	boolean isFirstClickBad=true;
	
	int bad_comment_num,good_comment_num;
	
	FOOTVIEWSTATUS bad_list_status,good_list_status;
	
	ArrayList<String> pic_url_list;
	
	AsyncImageLoader asyncImageLoader;
	
	TextView pic_text;
	LinearLayout multi_pic_layout;
	
	BroadcastReceiver bdReceiver;
	private IntentFilter ifilter;
	public class GetVenderComment extends AsyncTask<Integer, Integer, String> {

		boolean is_bad_comment;
		@Override
		protected void onPostExecute(String result) {
			
			super.onPostExecute(result);
			progressCommentReture(is_bad_comment,result);
		}

		@Override
		protected String doInBackground(Integer... arg0) {
			
			int vender_id=arg0[0];
			int readed_id=arg0[1];
			
			
			if(arg0[2]==0)
				is_bad_comment=false;
			else
				is_bad_comment=true;
			
			return new LoginToServer().GetVenderCommentInfo(context, vender_id, readed_id,is_bad_comment);
			
		}

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.vender_detail_layout);
		
		vender=(VenderInfo)this.getIntent().getExtras().getSerializable("vender");
		denisty=this.getResources().getDisplayMetrics().density;
		context=this;
		
		 bdReceiver=new BroadcastReceiver() {  
	  		  
	    	    @Override  
	    	    public void onReceive(Context context, Intent intent) {  
	    	          
	    	    	
	    	    	String bd_type=intent.getAction();
	    	    	
	    	    	if(bd_type.equals("netradar.bd.vender"))
	    	    	{
	    	    		int type=intent.getIntExtra("type", -1);
	    	    		
		    	    	if(type==1)
		    	    		getMoreComment(intent.getBooleanExtra("isBadComment", false));
		    	    		
	    	    		}
	    	      	    	
	    	    	
	    	    	
	    	    	
	    	    }

				
	    	  
	    	}; 
	    	ifilter= new IntentFilter(); 
	    	ifilter.addAction("netradar.bd.vender");
	    	
	    	registerReceiver(bdReceiver,ifilter);
		initView();
		initData();
	}
	

	@Override
	protected void onDestroy() {
		unregisterReceiver(bdReceiver);
		super.onDestroy();
	}


	public void progressCommentReture(boolean is_bad_comment, String result) {
		if(result.equals("error_data"))
		{
			Toast.makeText(this, "网络或数据异常，请稍候再试", Toast.LENGTH_SHORT).show();
			
			if(is_bad_comment&&isCurListBad)
				refreshFootView(FOOTVIEWSTATUS.PUSHTOGET);
			if((!is_bad_comment)&&(!isCurListBad))
				refreshFootView(FOOTVIEWSTATUS.PUSHTOGET);
			
			if(is_bad_comment)
			{
				bad_list_status=FOOTVIEWSTATUS.PUSHTOGET;
			}
			else
				good_list_status=FOOTVIEWSTATUS.PUSHTOGET;
			refreshBlankFootView(false);
			return;
		}
		
		JSONObject js;
		try {
			js = new JSONObject(result);
		
			JSONArray ja=js.getJSONArray("ret_comment_list");
			
		
			for(int i=0;i<ja.length();i++)
			{
				CommentInfo comment=new CommentInfo();
				comment.setNickname(URLDecoder.decode(ja.getJSONObject(i).getString("nickname"),"UTF-8"));
				
				comment.setTime(getTime(ja.getJSONObject(i).getString("time")));
				comment.setTouxiang(((DBUility)context.getApplicationContext()).webapps+"/pic/touxiang/"+ja.getJSONObject(i).getString("touxiang_url"));
				comment.setTo_id(-1);
			//	comment.setUser_id(ja.getJSONObject(i).getInt("user_id"));
				comment.setContent(URLDecoder.decode(ja.getJSONObject(i).getString("content"),"UTF-8"));
				comment.isVender=true;
				if(is_bad_comment)
				{
					bad_comment_list.add(comment);
					
					Log.d("lichao","bad comment list size is "+bad_comment_list.size());
				}
				else
					good_comment_list.add(comment);
				
				
			}
		} catch (JSONException e) {
		
		} catch (UnsupportedEncodingException e) {
			
		}
//		common_list.clear();
//		common_list.addAll(comment_list);
		if(is_bad_comment&&isCurListBad)
		{
			if(bad_comment_list.size()<bad_comment_num)
			{
				refreshFootView(FOOTVIEWSTATUS.PUSHTOGET);
			}
			else
				refreshFootView(FOOTVIEWSTATUS.NONE);
		}
		if((!is_bad_comment)&&(!isCurListBad))
		{
			if(good_comment_list.size()<good_comment_num)
			{
				refreshFootView(FOOTVIEWSTATUS.PUSHTOGET);
			}
			else
				refreshFootView(FOOTVIEWSTATUS.NONE);
		}
		if(is_bad_comment)
		{
			if(bad_comment_list.size()<bad_comment_num)
			{
				bad_list_status=FOOTVIEWSTATUS.PUSHTOGET;
			}
			else
				bad_list_status=FOOTVIEWSTATUS.NONE;
			
		}
		else
		{
			if(good_comment_list.size()<good_comment_num)
			{
				good_list_status=FOOTVIEWSTATUS.PUSHTOGET;
			}
			else
				good_list_status=FOOTVIEWSTATUS.NONE;
			
		}
		/*if(comment_list.size()<total_comment)
		{
			
			refreshFootView(FOOTVIEWSTATUS.PUSHTOGET);
		}
		else
		{
		
			refreshFootView(FOOTVIEWSTATUS.NONE);
		}*/
		cur_adapter.notifyDataSetChanged();
		
		refreshBlankFootView(false);
		
	}


	@Override
	protected void onResume() {
	
		super.onResume();
		refreshBlankFootView(true);
	}


	private void initData() {
		screenHeight=this.getIntent().getIntExtra("screenHeight", -1);
		screenWidth=this.getIntent().getIntExtra("screenWidth", -1);
		stateHeight=this.getIntent().getIntExtra("stateHeight", -1); 
		bad_comment_num=this.getIntent().getIntExtra("bad_comment_num", 0);
		good_comment_num=this.getIntent().getIntExtra("good_comment_num", 0);
		if(screenHeight==-1||stateHeight==-1)
			this.finish();
		
		denisty=this.getResources().getDisplayMetrics().density;
       
		minListHeight=(int) (screenHeight-stateHeight-125*denisty);
		
		/*nav_bad_text.setText("差评（"+bad_comment_num+"）");
		nav_good_text.setText("好评（"+good_comment_num+"）");*/
		
		good_comment_list=new ArrayList<CommentInfo>();
		bad_comment_list=new ArrayList<CommentInfo>();
		
		good_adapter=new CommentAdapter(this,good_comment_list,denisty,vender_list_view);
		bad_adapter=new CommentAdapter(this,bad_comment_list,denisty,vender_list_view);
		
		cur_adapter=good_adapter;
		vender_list_view.setAdapter(cur_adapter);
		
		vender_name.setText(vender.name);
		vender_addr.setText(vender.addr);
		vender_comment.setText(vender.comment);
		minListHeight=(int) (screenHeight-stateHeight-125*denisty);
		
		pic_url_list=new ArrayList<String>();
		String pic_list=this.getIntent().getStringExtra("pic_list");
		asyncImageLoader=new AsyncImageLoader(denisty);
		
	//	Log.d("lichao","pic_list is "+pic_list);
		if(pic_list==null||pic_list.length()==0)
		{
			pic_text.setVisibility(View.GONE);
			multi_pic_layout.setVisibility(View.GONE);
		}
		else
		{
			
			try {
				JSONArray ja_pic=new JSONArray(pic_list);
				
				for(int l=0;l<ja_pic.length();l++)
				{
					String url=((DBUility)this.getApplicationContext()).webapps+"/pic/weibo_pic/"+ja_pic.getJSONObject(l).getString("url");
					pic_url_list.add(url);
					
					if(l==0)
					{
						image1.setTag(url);
						Bitmap bmp=asyncImageLoader.loadDrawable(url,"0", this, LOADIMG_TYPE_MULTI);
						if(bmp!=null)
						{
							image1.setImageBitmap(bmp);
						}
						
					}
					if(l==1)
					{
						image2.setTag(url);
						Bitmap bmp=asyncImageLoader.loadDrawable(url,"0", this, LOADIMG_TYPE_MULTI);
						if(bmp!=null)
						{
							image2.setImageBitmap(bmp);
						}
					}
					if(l==2)
					{
						image3.setTag(url);
						Bitmap bmp=asyncImageLoader.loadDrawable(url,"0", this, LOADIMG_TYPE_MULTI);
						if(bmp!=null)
						{
							image3.setImageBitmap(bmp);
						}
					}
				}
			} catch (JSONException e)
			{
				pic_text.setVisibility(View.GONE);
				multi_pic_layout.setVisibility(View.GONE);
			}
			
			
		}
		
		new GetVenderComment().execute((int)vender.id,0,0);
		
		bad_list_status=FOOTVIEWSTATUS.NONE;
		good_list_status=FOOTVIEWSTATUS.REFRESHING;
		
		refreshFootView(FOOTVIEWSTATUS.REFRESHING);
	}

	private void initView() {
		
		vender_list_view=(ListView)findViewById(R.id.vender_detail_listview);
		
		
		nav_layout=(LinearLayout)findViewById(R.id.vender_detail_nav);
		
		content_header=(LinearLayout)LayoutInflater.from(this).inflate(R.layout.vender_detail_content_layout,null);
		nav_header=(LinearLayout)LayoutInflater.from(this).inflate(R.layout.vender_detail_nav_layout,null);
		
		vender_name=(TextView)content_header.findViewById(R.id.vender_detail_content_name_content);
		vender_addr=(TextView)content_header.findViewById(R.id.vender_detail_content_addr_content);
		vender_comment=(TextView)content_header.findViewById(R.id.vender_detail_content_comment_content);
	
		badNav=(ImageView)findViewById(R.id.vender_detail_bad_nav_f);
		goodNav=(ImageView)findViewById(R.id.vender_detail_good_nav_f);
		
		image1=(ImageView)content_header.findViewById(R.id.vender_detail_weibo_multi_image1);
		image2=(ImageView)content_header.findViewById(R.id.vender_detail_weibo_multi_image2);
		image3=(ImageView)content_header.findViewById(R.id.vender_detail_weibo_multi_image3);
		pic_text=(TextView)content_header.findViewById(R.id.vender_detail_content_pic_text);
		multi_pic_layout=(LinearLayout)content_header.findViewById(R.id.vender_detail_image_multi_layout);
		
		
		badNav1=(ImageView)nav_header.findViewById(R.id.vender_detail_nav_bad_comment_nav_f);
		goodNav1=(ImageView)nav_header.findViewById(R.id.vender_detail_nav_good_comment_nav_f);
		
		nav_bad_text=(TextView)nav_header.findViewById(R.id.vender_detail_nav_bad_comment);
		
		nav_good_text=(TextView)nav_header.findViewById(R.id.vender_detail_nav_good_comment);
		nav_bad_text1=(TextView)findViewById(R.id.vender_detail_bad_comment_f);
		
		nav_good_text1=(TextView)findViewById(R.id.vender_detail_good_comment_f);
		
		nav_bad_text.getPaint().setFakeBoldText(false);
		nav_good_text.getPaint().setFakeBoldText(true);
		nav_bad_text1.getPaint().setFakeBoldText(false);
		nav_good_text1.getPaint().setFakeBoldText(true);
	//	nav_self_layout=(LinearLayout)findViewById(R.id.vender_detail_nav);
		
		footView=(LinearLayout)LayoutInflater.from(this).inflate(R.layout.comment_detail_foot, null);
		blank_foot=(LinearLayout)LayoutInflater.from(this).inflate(R.layout.blank_foot,null);
		getting_info=(LinearLayout)footView.findViewById(R.id.getting_info);
		getmore_info=(Button)footView.findViewById(R.id.moreinfo);
		foot_tip=(TextView)footView.findViewById(R.id.comment_detail_foot_tip);
		
		vender_list_view.setOnScrollListener(this);
		
		badNav.setVisibility(View.GONE);
		badNav1.setVisibility(View.GONE);
		vender_list_view.addHeaderView(content_header);
		vender_list_view.addHeaderView(nav_header);
		vender_list_view.addFooterView(footView);
		vender_list_view.addFooterView(blank_foot);
		
	}
	
	public void onShareVender(View v)
	{
		if(!ProcessUserStatus.processUserStatus(this, 4)) return;
		
		String dir=ScreenShoot.getUploadPic(this,content_header);
		
		if(dir==null)
		{
			Toast.makeText(this, "T卡空间不足，不能进行分享～", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent i=new Intent();
		Bundle bd=new Bundle();
	
		i.putExtra("dir", dir);
		i.putExtra("type", 1);
		i.putExtras(bd);
		i.setClass(VenderDetail.this, ShareDialog1.class);
		
		startActivity(i);
	}
	
	public void onBadComment(View v)
	{
		if(!ProcessUserStatus.processUserStatus(this, 4)) return;
		
		Intent i=new Intent();
	
		
		i.setClass(VenderDetail.this, NewVenderComment.class);
		
		i.putExtra("title", "发表差评");
		i.putExtra("vender_id", vender.id);
		i.putExtra("is_bad_comment", true);

		this.startActivityForResult(i,2);
		this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
		
		
	}
	public void onGoodComment(View v)
	{
		if(!ProcessUserStatus.processUserStatus(this, 4)) return;
		
		Intent i=new Intent();
	
		
		i.setClass(VenderDetail.this, NewVenderComment.class);
		
		i.putExtra("title", "发表好评");
		i.putExtra("vender_id", vender.id);
		i.putExtra("is_bad_comment", false);
		this.startActivityForResult(i,2);
		this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==4)
		{
			if(resultCode==1)
			{
				UserManager.setCurUser(this.getApplicationContext(), data.getStringExtra("nickname"));
				
				Toast.makeText(this, "登录成功～", Toast.LENGTH_SHORT).show();
				
			}
		}
	}
	private int getListViewHeight(CommentAdapter adapter,boolean isFirst)
	{
		   int totalHeight = 0;    
		   for (int i = 0, len = adapter.getCount(); i < len; i++) 
	        { 
		
		        View listItem = ((CommentAdapter)adapter).getView1(i);  
		         listItem.measure(0, 0); 
		        totalHeight += listItem.getMeasuredHeight(); 
	        }    
   
	
		if(isFirst)
			return (int) (totalHeight+50*denisty);
		else
			return totalHeight+footView.getHeight();
		
	}
private int getDelta(int h) {
		
		
		if(h>minListHeight) return 0;
		return minListHeight-h;
		
		
	}
	private void refreshBlankFootView(boolean isFirst)
	{
		int delta=getDelta(getListViewHeight(cur_adapter,isFirst));
	
		blank_foot.setLayoutParams(new AbsListView.LayoutParams(blank_foot.getWidth(),delta));
	
	}
	private void refreshFootView(FOOTVIEWSTATUS status)
	{
		if(status==FOOTVIEWSTATUS.NONE)
		{
			//footView.setVisibility(View.GONE);
			footView.setVisibility(View.VISIBLE);
			getting_info.setVisibility(View.GONE);
			getmore_info.setVisibility(View.GONE);
			foot_tip.setVisibility(View.VISIBLE);
			
			foot_tip.setTextColor(Color.parseColor("#222000"));
			foot_tip.setText("已加载全部数据");
			
		}
	//	footView.setLayoutParams(new LayoutParams(footView.getWidth(),(int) (50*denisty)));
		if(status==FOOTVIEWSTATUS.REFRESHING)
		{
			footView.setVisibility(View.VISIBLE);
			getting_info.setVisibility(View.VISIBLE);
			getmore_info.setVisibility(View.GONE);
			foot_tip.setVisibility(View.GONE);
		}
		if(status==FOOTVIEWSTATUS.PUSHTOGET)
		{
			footView.setVisibility(View.VISIBLE);
			getting_info.setVisibility(View.GONE);
			getmore_info.setVisibility(View.VISIBLE);
			foot_tip.setVisibility(View.GONE);
		}
		if(status==FOOTVIEWSTATUS.SHOWTIP)
		{
			footView.setVisibility(View.VISIBLE);
			getting_info.setVisibility(View.GONE);
			getmore_info.setVisibility(View.GONE);
			foot_tip.setVisibility(View.VISIBLE);
			
			foot_tip.setTextColor(Color.parseColor("#999000"));
			foot_tip.setText("暂时没有评论");
		}
	}

	int topShow;
	public void onSelectBadList(View v)
	{
		nav_bad_text.getPaint().setFakeBoldText(true);
		nav_good_text.getPaint().setFakeBoldText(false);
		nav_bad_text1.getPaint().setFakeBoldText(true);
		nav_good_text1.getPaint().setFakeBoldText(false);
		
	//	topShow=vender_list_view.getFirstVisiblePosition();
		int top=vender_list_view.getChildAt(1).getTop();
		if(isScrollOutSide) top=0;
		vender_list_view.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
		
		isCurListBad=true;
		if(isFirstClickBad)
		{
			isFirstClickBad=false;
			cur_adapter=bad_adapter;
			getMoreComment(true);
			bad_list_status=FOOTVIEWSTATUS.REFRESHING;
			
		}
		else
		{
			cur_adapter=bad_adapter;
			
		}
		refreshFootView(bad_list_status);
		vender_list_view.setAdapter(cur_adapter);
		cur_adapter.notifyDataSetChanged();
		
		vender_list_view.setSelectionFromTop(1, top);
		
		badNav.setVisibility(View.VISIBLE);
		badNav1.setVisibility(View.VISIBLE);
		goodNav.setVisibility(View.GONE);
		goodNav1.setVisibility(View.GONE);
		
		refreshBlankFootView(false);
	}
	public void onSelectGoodList(View v)
	{
		nav_bad_text.getPaint().setFakeBoldText(false);
		nav_good_text.getPaint().setFakeBoldText(true);
		nav_bad_text1.getPaint().setFakeBoldText(false);
		nav_good_text1.getPaint().setFakeBoldText(true);
		
		topShow=vender_list_view.getFirstVisiblePosition();
		int top=vender_list_view.getChildAt(1).getTop();
		if(isScrollOutSide) top=0;
		vender_list_view.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
		
		isCurListBad=false;
		cur_adapter=good_adapter;
		vender_list_view.setAdapter(cur_adapter);
		cur_adapter.notifyDataSetChanged();
		vender_list_view.setSelectionFromTop(1, top);
		
		
		badNav.setVisibility(View.GONE);
		badNav1.setVisibility(View.GONE);
		goodNav.setVisibility(View.VISIBLE);
		goodNav1.setVisibility(View.VISIBLE);
		
		refreshFootView(good_list_status);
		refreshBlankFootView(false);
	}
	public void onGetMoreComment(View v)
	{
		
		refreshFootView(FOOTVIEWSTATUS.REFRESHING);
		getMoreComment(isCurListBad);
		
	}
	private void getMoreComment(boolean isBadComment)
	{
		if(isBadComment)
		{
			new GetVenderComment().execute((int)vender.id,bad_comment_list.size(),1);
			bad_list_status=FOOTVIEWSTATUS.REFRESHING;
		}
		else
		{
			new GetVenderComment().execute((int)vender.id,good_comment_list.size(),0);
			good_list_status=FOOTVIEWSTATUS.REFRESHING;
		}
	}
	private String getTime(String time)
	{
		return time.substring(0,4)+"年"+time.substring(4,6)+"月"+time.substring(6,8)+"日 "+time.substring(8,10)+":"+time.substring(10,12)+":"+time.substring(12,14);
	}


	@Override
	public void imageLoaded(Bitmap imageDrawable, String imageUrl, String id) {
		
	//	Log.d("lichao","image url is "+imageUrl+"  id is "+id);
		ImageView iv=(ImageView) content_header.findViewWithTag(imageUrl);
		if(iv!=null)
		{
			if(imageDrawable!=null)
				iv.setImageBitmap(imageDrawable);
		}
		
	}
	
	public void onViewVenderPic(View v)
	{
		String tag=(String)v.getTag();
		
		int index=0;
		
		
		ImageAndText imt=new ImageAndText();
		List<PicInfo> pic_info_list=new ArrayList<PicInfo>();
		
		for(int k=0;k<pic_url_list.size();k++)
		{
			PicInfo pic=new PicInfo();
			pic.url=pic_url_list.get(k);
			pic_info_list.add(pic);
			
			if(tag.equals(pic.url))
			{
				index=k;
			}
		}
		imt.pic_list_url=pic_info_list;
		
		Intent i=new Intent();
		Rect frame=new Rect();
		this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int screenWidth  = getWindowManager().getDefaultDisplay().getWidth();       // 屏幕宽（像素，如：480px）   
		int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		
		i.setClass(VenderDetail.this, ViewPic.class);
		Bundle bd=new Bundle();
	
		bd.putSerializable("data", imt);
		i.putExtra("index", index);
		i.putExtra("height", frame.top);
		i.putExtra("screenWidth",screenWidth);
		i.putExtra("screenHeight", screenHeight);
		i.putExtras(bd);
		startActivity(i);
	}

	boolean isScrollOutSide=false;
	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
		if(arg1!=0)
		{
			nav_layout.setVisibility(View.VISIBLE);
			isScrollOutSide=true;
		}
		else
		{
			nav_layout.setVisibility(View.GONE);
			isScrollOutSide=false;
		}
		
	}


	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	
	public void onCancelVenderDetail(View v)
	{
		this.finish();
		overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
	}


	@Override
	public void onBackPressed() {
		
		this.finish();
		overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
	}
	
}
