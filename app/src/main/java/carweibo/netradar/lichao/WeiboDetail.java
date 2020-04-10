package carweibo.netradar.lichao;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carweibo.netradar.lichao.AsyncImageLoader.ImageCallback;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WeiboDetail extends Activity implements ImageCallback, OnScrollListener, OnLongClickListener, OnItemLongClickListener {

	public enum FOOTVIEWSTATUS {
		NONE, REFRESHING, PUSHTOGET,SHOWTIP

	};
	/*FOOTVIEWSTATUS retweet_mode=FOOTVIEWSTATUS.REFRESHING;*/
	FOOTVIEWSTATUS comment_mode=FOOTVIEWSTATUS.REFRESHING;
/*	FOOTVIEWSTATUS good_mode=FOOTVIEWSTATUS.REFRESHING;*/
	
	private static int LOADIMG_TYPE_TOUXIANG=0;
	private static int LOADIMG_TYPE_SINGLE=1;
	private static int LOADIMG_TYPE_MULTI=2;
	ImageView touxiang;
	ImageView vip_sign;
	
	TextView nickname,time,delete,content;
	
	LinearLayout single_pic_layout,multi_pic_layout;
	ImageView single_pic;
	ImageView multi_pic1,multi_pic2,multi_pic3;
	
	TextView comment_num,good_num;
	Context context;
	AsyncImageLoader asyncimageloader;
	
	View parent;
	List<ImageView> multi_pic_list;
	int multi_pic_id[]={R.id.weibo_detail_weibo_multi_image1,R.id.weibo_detail_weibo_multi_image2,R.id.weibo_detail_weibo_multi_image3};
	ImageAndText imt;
	
	TextView comment;
	//ImageView retweet_nav,comment_nav,good_nav;
	

	TextView comment_f;
	//ImageView retweet_nav_f,comment_nav_f,good_nav_f;
	
	CommentListView comment_listview;
//	ListView retweet_listview,good_listview;
	List<CommentInfo> comment_list;
	LinearLayout footView,bottom_layout;
	
	CommentAdapter comment_adapter;
//	RetweetAdapter retweet_adapter;
//	GoodAdapter good_adapter;
	
	FrameLayout comment_frame_layout;
//	MyScrollView scroll_view;
//	boolean isClickRetweet=false,isClickComment=false,isClickGood=false;
	
//	int currentSelectedItem=1;
	int total_comment;
	BroadcastReceiver bdReceiver;
	private IntentFilter ifilter;
	public class GetCommentInfo extends AsyncTask<String, Integer, String> {

		String type;
		boolean isTop;
		int readed_id;
		long weibo_id;
		
		@Override
		protected void onPostExecute(String result) {
			
			super.onPostExecute(result);
			
			progressCommentReture(result);
			
		}

		@Override
		protected String doInBackground(String... arg0) {
			type=arg0[0];
			weibo_id=Long.parseLong(arg0[1]);
			readed_id=Integer.parseInt(arg0[2]);
			if(arg0[3].equals("top"))
				isTop=true;
			else
				isTop=false;
			return new LoginToServer().GetCommentInfo(context, weibo_id, readed_id, type,isTop);
			
		}

	}

	int screenHeight,screenWidth;
	int stateHeight;
	float denisty;
	int minListHeight;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.test_weibo_detail_layout);
		
		imt=(ImageAndText) this.getIntent().getExtras().getSerializable("data");
		screenHeight=this.getIntent().getIntExtra("screenHeight", -1);
		screenWidth=this.getIntent().getIntExtra("screenWidth", -1);
		stateHeight=this.getIntent().getIntExtra("stateHeight", -1); 
		
		if(screenHeight==-1||stateHeight==-1)
			this.finish();
		
		denisty=this.getResources().getDisplayMetrics().density;
       
		minListHeight=(int) (screenHeight-stateHeight-125*denisty);
		asyncimageloader=new AsyncImageLoader(denisty);
		/*parent=(View)findViewById(R.layout.weibo_detail_layout);*/
		context=this;
		initView();
		
		initData(imt);
		
		String isTop;
		if(imt.grade==2)
			isTop="top";
		else
			isTop="notop";
		new GetCommentInfo().execute("comment_list",String.valueOf(imt.id),String.valueOf(0),isTop);
		
		bdReceiver=new BroadcastReceiver() {  
	  		  
    	    @Override  
    	    public void onReceive(Context context, Intent intent) {  
    	        // TODO Auto-generated method stub   
    	    	
    	    	int type=intent.getIntExtra("type", -1);
    	    	if(type!=3) return;
    	    	total_comment++;
    	    	comment.setText("评论("+total_comment+")");
    			comment_f.setText("评论("+total_comment+")");
    			onGetMoreComment(null);
    	    }  
    	  
    	}; 
    	
    	ifilter= new IntentFilter(); 
    	ifilter.addAction("netradar.bd");
	}

	
	@Override
	protected void onResume() {
	
		super.onResume();
		
		refreshBlankFootView(true);
		
	}

	@Override
	protected void onStart() {
	
		super.onStart();
		
		
		registerReceiver(bdReceiver,ifilter);
	}
	@Override
	protected void onStop() {
	
		super.onStop();
		
		
		unregisterReceiver(bdReceiver);
	}
	

	public void progressCommentReture(String result) {
		
		if(result.equals("error_data"))
		{
			Toast.makeText(this, "网络或数据异常，请稍候再试", Toast.LENGTH_SHORT).show();
			
			refreshFootView(FOOTVIEWSTATUS.PUSHTOGET);
			refreshBlankFootView(false);
			return;
		}
		
		try {
			JSONObject js=new JSONObject(result);
			total_comment=js.getInt("ret_comment_num");
			if(total_comment==0)
			{
				refreshFootView(FOOTVIEWSTATUS.SHOWTIP);
				refreshBlankFootView(false);
				return;
			}
			JSONArray ja=js.getJSONArray("ret_comment_list");
			
			comment.setText("评论("+total_comment+")");
			comment_f.setText("评论("+total_comment+")");
		//	Log.d("lichao",ja.toString());
			for(int i=0;i<ja.length();i++)
			{
				CommentInfo comment=new CommentInfo();
				comment.setNickname(URLDecoder.decode(ja.getJSONObject(i).getString("nickname"),"UTF-8"));
				comment.setTo_id(ja.getJSONObject(i).getInt("to_id"));
				comment.setTo_nickname(URLDecoder.decode(ja.getJSONObject(i).getString("to_nickname"),"UTF-8"));
				
				comment.setTime(getTime(ja.getJSONObject(i).getString("time")));
				comment.setTouxiang(((DBUility)context.getApplicationContext()).webapps+"/pic"+"/touxiang/"+ja.getJSONObject(i).getString("touxiang_url"));
				
				comment.setUser_id(ja.getJSONObject(i).getInt("user_id"));
				comment.setContent(URLDecoder.decode(ja.getJSONObject(i).getString("content"),"UTF-8"));
				
				comment_list.add(comment);
				
			}
//			common_list.clear();
//			common_list.addAll(comment_list);
			if(comment_list.size()<total_comment)
			{
				comment_mode=FOOTVIEWSTATUS.PUSHTOGET;
				refreshFootView(comment_mode);
			}
			else
			{
				comment_mode=FOOTVIEWSTATUS.NONE;
				refreshFootView(comment_mode);
			}
			comment_adapter.notifyDataSetChanged();
			
		//	adjustViewHeight(comment_listview);
		} catch (JSONException e) {
			Log.d("lichao","json error in progress comment ret");
		} catch (UnsupportedEncodingException e) {
			
			Log.d("lichao","UnsupportedEncodingException");
		}
	//	refreshFootView(FOOTVIEWSTATUS.PUSHTOGET);
		
		refreshBlankFootView(false);
	}

	private String getTime(String time)
	{
		return time.substring(0,4)+"年"+time.substring(4,6)+"月"+time.substring(6,8)+"日 "+time.substring(8,10)+":"+time.substring(10,12)+":"+time.substring(12,14);
	}
	


	private void initData(ImageAndText imt) {
		
		content_header.setOnLongClickListener(this);
		weibo_listview.setOnItemLongClickListener(this);
	//	common_list=new ArrayList<Map<String,String>>();
		comment_list=new ArrayList<CommentInfo>();
	//	retweet_list=new ArrayList<Map<String,String>>();
	//	good_list=new ArrayList<Map<String,String>>();
		
	//	common_list.addAll(comment_list);
		comment_adapter=new CommentAdapter(this,comment_list,denisty,weibo_listview);
		
	
	/*	retweet_adapter=new RetweetAdapter(this,retweet_list,R.layout.retweet_item_layout,
				new String[]{"nickname","content","touxiang","time","isvip"},
				new int[]{R.id.retweet_item_nickname,R.id.retweet_item_content,R.id.retweet_item_touxiang,R.id.retweet_item_time,R.id.retweet_item_vip_sign},
				denisty,weibo_listview);
		good_adapter=new GoodAdapter(this,good_list,R.layout.good_item_layout,
				new String[]{"nickname","touxiang","isvip"},
				new int[]{R.id.good_item_nickname,R.id.good_item_touxiang,R.id.good_item_vip_sign},
				denisty,weibo_listview);*/
		
		weibo_listview.setAdapter(comment_adapter);
		
		comment_mode=FOOTVIEWSTATUS.REFRESHING;
		refreshFootView(comment_mode);
		
		
		
		nickname.setText(imt.nickname);
		touxiang.setTag(imt.id+imt.touxiang_url);
		Bitmap touxiang_bmp=asyncimageloader.loadDrawable(imt.touxiang_url, String.valueOf(imt.id), this, LOADIMG_TYPE_TOUXIANG);
		if(touxiang_bmp==null)
		{
			touxiang.setImageResource(R.drawable.switchuser);
		}
		else
			touxiang.setImageBitmap(touxiang_bmp);
		time.setText(imt.time);
		content.setText(imt.content);
		total_comment=Integer.parseInt(imt.comment_num.substring(0, imt.comment_num.indexOf("个")));
		comment.setText("评论("+total_comment+")");
		comment_f.setText("评论("+total_comment+")");
		
		if(imt.pic_list_url.size()==0)
		{
			single_pic_layout.setVisibility(View.GONE);
			multi_pic_layout.setVisibility(View.GONE);
		}
		else if(imt.pic_list_url.size()==1)
		{
			single_pic_layout.setVisibility(View.VISIBLE);
			multi_pic_layout.setVisibility(View.GONE);
			single_pic_layout.setLayoutParams(new FrameLayout.LayoutParams(imt.pic_list_url.get(0).width,imt.pic_list_url.get(0).height));
			
			single_pic.setTag(imt.id+imt.pic_list_url.get(0).url);
			Bitmap single_bmp=asyncimageloader.loadDrawable(imt.pic_list_url.get(0).url, String.valueOf(imt.id), this, LOADIMG_TYPE_SINGLE);
			
			if(single_bmp==null)
			{
				single_pic.setImageBitmap(null);
			}
			else
				single_pic.setImageBitmap(single_bmp);
			
		}
		else 
		{
			single_pic_layout.setVisibility(View.GONE);
			multi_pic_layout.setVisibility(View.VISIBLE);
			
			int i=0;
			for(PicInfo p:imt.pic_list_url)
			{
				multi_pic_list.get(i).setTag(imt.id+imt.pic_list_url.get(i).url);
				Bitmap pic_multi_bmp=asyncimageloader.loadDrawable(imt.pic_list_url.get(i).url, String.valueOf(imt.id),this,LOADIMG_TYPE_MULTI);
				if(pic_multi_bmp==null)
           		{
					multi_pic_list.get(i).setImageBitmap(null);
           		}
           		else
           			multi_pic_list.get(i).setImageBitmap(pic_multi_bmp);
				i++;
			}
           		
           	
		}
		
		if(imt.user_score>5000)
			vip_sign.setVisibility(View.VISIBLE);
		else
			vip_sign.setVisibility(View.GONE);
		/*nickname.setText(imt.nickname);
		time.setText(imt.time);
		content.setText(imt.content);
		
		
		
		touxiang.setTag(imt.id+imt.touxiang_url);
		Bitmap touxiang_bmp=asyncimageloader.loadDrawable(imt.touxiang_url, new String().valueOf(imt.id), this, LOADIMG_TYPE_TOUXIANG);
		if(touxiang_bmp==null)
		{
			touxiang.setImageResource(R.drawable.switchuser);
		}
		else
			touxiang.setImageBitmap(touxiang_bmp);
		
		if(imt.pic_list_url.size()==0)
		{
			single_pic_layout.setVisibility(View.GONE);
			multi_pic_layout.setVisibility(View.GONE);
		}
		else if(imt.pic_list_url.size()==1)
		{
			single_pic_layout.setVisibility(View.VISIBLE);
			multi_pic_layout.setVisibility(View.GONE);
			single_pic_layout.setLayoutParams(new FrameLayout.LayoutParams(imt.pic_list_url.get(0).width,imt.pic_list_url.get(0).height));
			
			single_pic.setTag(imt.id+imt.pic_list_url.get(0).url);
			Bitmap single_bmp=asyncimageloader.loadDrawable(imt.pic_list_url.get(0).url, new String().valueOf(imt.id), this, LOADIMG_TYPE_SINGLE);
			
			if(single_bmp==null)
			{
				single_pic.setImageBitmap(null);
			}
			else
				single_pic.setImageBitmap(single_bmp);
			
		}
		else 
		{
			single_pic_layout.setVisibility(View.GONE);
			multi_pic_layout.setVisibility(View.VISIBLE);
			
			int i=0;
			for(PicInfo p:imt.pic_list_url)
			{
				multi_pic_list.get(i).setTag(imt.id+imt.pic_list_url.get(i).url);
				Bitmap pic_multi_bmp=asyncimageloader.loadDrawable(imt.pic_list_url.get(i).url, new String().valueOf(imt.id),this,LOADIMG_TYPE_MULTI);
				if(pic_multi_bmp==null)
           		{
					multi_pic_list.get(i).setImageBitmap(null);
           		}
           		else
           			multi_pic_list.get(i).setImageBitmap(pic_multi_bmp);
				i++;
			}
           		
           	
		}
		
		comment_list=new ArrayList<Map<String,String>>();
		
		//MergeAdapter adap=new MergeAdapter();
		//View view = this.getLayoutInflater().inflate(R.layout.head,  null);
        
		//adap.addView(view);
		
		comment_adapter=new CommentAdapter(this,comment_list,R.layout.comment_item_layout,
				new String[]{"nickname","content","touxiang","time","isvip"},
				new int[]{R.id.comment_item_nickname,R.id.comment_item_content,R.id.comment_item_touxiang,R.id.comment_item_time,R.id.comment_item_vip_sign});
	
		//adap.addAdapter(comment_adapter);
		comment_listview.setAdapter(comment_adapter);
		
		comment_frame_layout.setLayoutParams(new LinearLayout.LayoutParams(screenWidth,(int) (screenHeight-stateHeight-125*denisty)));

		scroll_view.setListView(comment_listview);
		comment_listview.setOnScrollListener(new OnScrollListener() {
			   
		    @Override
		    public void onScrollStateChanged(AbsListView view, int scrollState) {
		     // 当不滚动时
		     if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
		      //判断是否滚动到底部
		      if (view.getLastVisiblePosition() == view.getCount() - 1) {
		      comment_listview.isBottom=true; 
		      }
		      else
		    	  comment_listview.isBottom=false;
		     }
		    }
		   
		    @Override
		    public void onScroll(AbsListView view, int firstVisibleItem,
		      int visibleItemCount, int totalItemCount) {
		     // TODO Auto-generated method stub
		    
		    }
		   });

		comment_listview.scrollView=scroll_view;
		
		*/
	}

	CommentListView weibo_listview;
	LinearLayout nav_layout;
	RelativeLayout title_layout;
	LinearLayout content_header, nav_header,blank_foot;
	
	LinearLayout getting_info;
	Button getmore_info;
	TextView foot_tip;
	
	private void initView() {
		
		
		weibo_listview=(CommentListView)findViewById(R.id.comment_detail_comment_listview);
		nav_layout=(LinearLayout)findViewById(R.id.weibo_detail_nav);
		content_header=(LinearLayout)LayoutInflater.from(this).inflate(R.layout.weibo_detail_content_layout,null);
		nav_header=(LinearLayout)LayoutInflater.from(this).inflate(R.layout.weibo_detail_nav_layout,null);
		blank_foot=(LinearLayout)LayoutInflater.from(this).inflate(R.layout.blank_foot,null);
		bottom_layout=(LinearLayout)findViewById(R.id.weibo_detail_bottom_toolbar);
		footView=(LinearLayout)LayoutInflater.from(this).inflate(R.layout.comment_detail_foot, null);
		getting_info=(LinearLayout)footView.findViewById(R.id.getting_info);
		getmore_info=(Button)footView.findViewById(R.id.moreinfo);
		foot_tip=(TextView)footView.findViewById(R.id.comment_detail_foot_tip);
		
		weibo_listview.addHeaderView(content_header);
		weibo_listview.addHeaderView(nav_header);
		weibo_listview.addFooterView(footView);
		weibo_listview.addFooterView(blank_foot);
		
		
		title_layout=(RelativeLayout)findViewById(R.id.weibo_detail_title_layout);
		
		weibo_listview.setOnScrollListener(this);
		
		
		touxiang=(ImageView)content_header.findViewById(R.id.weibo_detail_touxiang);
		
		vip_sign=(ImageView)content_header.findViewById(R.id.weibo_detail_vip_sign);
		nickname=(TextView)content_header.findViewById(R.id.weibo_detail_nickname);
		time=(TextView)content_header.findViewById(R.id.weibo_detail_time);
		delete=(TextView)content_header.findViewById(R.id.weibo_detail_delete);
		content=(TextView)content_header.findViewById(R.id.weibo_detail_content);
		
		
		single_pic_layout=(LinearLayout)content_header.findViewById(R.id.weibo_detail_single_image_layout);
		multi_pic_layout=(LinearLayout)content_header.findViewById(R.id.weibo_detail_image_multi_layout);
		single_pic=(ImageView)content_header.findViewById(R.id.weibo_detail_image_single);
		
		
		
		if(imt.nickname.equals(UserManager.getCurUser(this.getApplicationContext())))
		{
			delete.setVisibility(View.VISIBLE);
		
		}
		else
			delete.setVisibility(View.GONE);
		
		multi_pic_list=new ArrayList<ImageView>();
		for(int i=0;i<3;i++)
		{
			multi_pic_list.add((ImageView)content_header.findViewById(multi_pic_id[i]));
		}
		comment=(TextView)nav_header.findViewById(R.id.weibo_detail_comment);
		comment_f=(TextView)findViewById(R.id.weibo_detail_comment_f);
	/*	retweet=(TextView)nav_header.findViewById(R.id.weibo_detail_retweet);
		
		good=(TextView)nav_header.findViewById(R.id.weibo_detail_good);
		retweet_nav=(ImageView)nav_header.findViewById(R.id.weibo_detail_retweet_nav);
		comment_nav=(ImageView)nav_header.findViewById(R.id.weibo_detail_comment_nav);
		good_nav=(ImageView)nav_header.findViewById(R.id.weibo_detail_good_nav);
	*/	
	//	retweet_f=(TextView)findViewById(R.id.weibo_detail_retweet_f);
	//	
	//	good_f=(TextView)findViewById(R.id.weibo_detail_good_f);
	//	retweet_nav_f=(ImageView)findViewById(R.id.weibo_detail_retweet_nav_f);
	//	comment_nav_f=(ImageView)findViewById(R.id.weibo_detail_comment_nav_f);
	//	good_nav_f=(ImageView)findViewById(R.id.weibo_detail_good_nav_f);
		
		/*vip_sign=(ImageView)findViewById(R.id.weibo_detail_vip_sign);
		nickname=(TextView)findViewById(R.id.weibo_detail_nickname);
		time=(TextView)findViewById(R.id.weibo_detail_time);
		delete=(TextView)findViewById(R.id.weibo_detail_delete);
		content=(TextView)findViewById(R.id.weibo_detail_content);
		single_pic_layout=(LinearLayout)findViewById(R.id.weibo_detail_single_image_layout);
		multi_pic_layout=(LinearLayout)findViewById(R.id.weibo_detail_image_multi_layout);
		single_pic=(ImageView)findViewById(R.id.weibo_detail_image_single);
		retweet=(TextView)findViewById(R.id.weibo_detail_retweet);
		comment=(TextView)findViewById(R.id.weibo_detail_comment);
		good=(TextView)findViewById(R.id.weibo_detail_good);
		retweet_nav=(ImageView)findViewById(R.id.weibo_detail_retweet_nav);
		comment_nav=(ImageView)findViewById(R.id.weibo_detail_comment_nav);
		good_nav=(ImageView)findViewById(R.id.weibo_detail_good_nav);
		retweet_listview=(ListView)findViewById(R.id.comment_detail_retweet_listview);
		comment_listview=(CommentListView)findViewById(R.id.comment_detail_comment_listview);
		good_listview=(ListView)findViewById(R.id.comment_detail_good_listview);
		footView=(LinearLayout)LayoutInflater.from(this).inflate(R.layout.comment_detail_foot, null);
		
		comment_listview.addFooterView(footView);
		comment_frame_layout=(FrameLayout)findViewById(R.id.weibo_detail_comment_frameLayout);
		scroll_view=(MyScrollView)findViewById(R.id.weibo_detail_scrollview);
		
		if(imt.nickname.equals(UserManager.getCurUser(this.getApplicationContext())))
		{
			delete.setVisibility(View.VISIBLE);
		
		}
		else
			delete.setVisibility(View.GONE);
		multi_pic_list=new ArrayList<ImageView>();
		for(int i=0;i<3;i++)
		{
			multi_pic_list.add((ImageView)findViewById(multi_pic_id[i]));
		}
	
				comment_num=(TextView)findViewById(R.id.weibo_d);
		good_num=(TextView)findViewById(R.id.weibo_detail_touxiang);
		*/
		
		
	}

	@Override
	public void imageLoaded(Bitmap imageDrawable, String imageUrl, String id) {
		//parent=((ViewGroup)this.findViewById(android.R.id.content)).getChildAt(0);
		ImageView iv=(ImageView) content_header.findViewWithTag(id+imageUrl);
		if(iv!=null)
		{
			if(imageDrawable!=null)
				iv.setImageBitmap(imageDrawable);
		}
		
	}
	
	public void onViewPic(View v)
	{
		String tag=(String)v.getTag();
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
		
		i.setClass(WeiboDetail.this, ViewPic.class);
		Bundle bd=new Bundle();
	
		bd.putSerializable("data", imt);
		i.putExtra("index", k);
		i.putExtra("height", frame.top);
		i.putExtra("screenWidth",screenWidth);
		i.putExtra("screenHeight", screenHeight);
		i.putExtras(bd);
		startActivity(i);
	}

	boolean changeTab=false;
	/*public void onClickRetweet(View v)
	{
		
		retweet_f.setTextColor(Color.parseColor("#111000"));
		comment_f.setTextColor(Color.parseColor("#929292"));
		good_f.setTextColor(Color.parseColor("#929292"));
		
		retweet_nav_f.setVisibility(View.VISIBLE);
		comment_nav_f.setVisibility(View.GONE);
		good_nav_f.setVisibility(View.GONE);
		
		
		retweet.setTextColor(Color.parseColor("#111000"));
		comment.setTextColor(Color.parseColor("#929292"));
		good.setTextColor(Color.parseColor("#929292"));
		
		retweet_nav.setVisibility(View.VISIBLE);
		comment_nav.setVisibility(View.GONE);
		good_nav.setVisibility(View.GONE);
		
		common_list.clear();
		common_list.addAll(retweet_list);
		
		comment_adapter.notifyDataSetChanged();
		
		refreshFootView(retweet_mode);
		refreshBlankFootView(false);
		
		if(!isClickRetweet)
		{
			
			//retweet_mode=FOOTVIEWSTATUS.REFRESHING;
			getRetweetInfo();
		}
		
	
	}*/
	
	
	


	private int getDelta(int h) {
		
		
		if(h>minListHeight) return 0;
		return minListHeight-h;
		
		
	}


	/*public void onClickComment(View v)
	{
		retweet_f.setTextColor(Color.parseColor("#929292"));
		comment_f.setTextColor(Color.parseColor("#111000"));
		good_f.setTextColor(Color.parseColor("#929292"));	
		
		retweet_nav_f.setVisibility(View.GONE);
		comment_nav_f.setVisibility(View.VISIBLE);
		good_nav_f.setVisibility(View.GONE);
		
		
		retweet.setTextColor(Color.parseColor("#929292"));
		comment.setTextColor(Color.parseColor("#111000"));
		good.setTextColor(Color.parseColor("#929292"));	
		
		retweet_nav.setVisibility(View.GONE);
		comment_nav.setVisibility(View.VISIBLE);
		good_nav.setVisibility(View.GONE);
		
		common_list.clear();
	//	comment_adapter.notifyDataSetChanged();
		common_list.addAll(comment_list);
		
		
		comment_adapter.notifyDataSetChanged();
		refreshFootView(comment_mode);
		refreshBlankFootView(false);	
	//	weibo_listview.setAdapter(comment_adapter);
		
	//	int delta=getDelta(getListViewHeight(comment_adapter));
		
		//	Log.d("lichao","listview_height is "+listview_height);
			
	//		blank_foot.setLayoutParams(new AbsListView.LayoutParams(blank_foot.getWidth(),delta));
		
	}
	
	public void onClickGood(View v)
	{
		retweet_f.setTextColor(Color.parseColor("#929292"));
		comment_f.setTextColor(Color.parseColor("#929292"));
		good_f.setTextColor(Color.parseColor("#111000"));	
		
		retweet_nav_f.setVisibility(View.GONE);
		comment_nav_f.setVisibility(View.GONE);
		good_nav_f.setVisibility(View.VISIBLE);
		
		
		retweet.setTextColor(Color.parseColor("#929292"));
		comment.setTextColor(Color.parseColor("#929292"));
		good.setTextColor(Color.parseColor("#111000"));	
		
		retweet_nav.setVisibility(View.GONE);
		comment_nav.setVisibility(View.GONE);
		good_nav.setVisibility(View.VISIBLE);
		
		common_list.clear();
	
		common_list.addAll(good_list);
			
			
		comment_adapter.notifyDataSetChanged();
		refreshBlankFootView(false);	
		
	}*/
	public void onDeleteWeibo(View v)
	{
		Intent i=new Intent();
		
		
		i.putExtra("text", "提示\n\n"+"确定删除帖子吗？");
		i.putExtra("ok", "确定");
		i.putExtra("cancel", "取消");
		i.setClass(WeiboDetail.this, Dialog.class);
		this.startActivityForResult(i,1);
	}
	public void onComment(View v)
	{
		if(!ProcessUserStatus.processUserStatus(this, 4)) return;
		Intent i=new Intent();
	
		
		i.setClass(WeiboDetail.this, NewComment.class);
		/*Bundle bd=new Bundle();
	
		bd.putSerializable("data", imt);*/
		i.putExtra("title", "发表评论");
		i.putExtra("weibo_id", imt.id);
		i.putExtra("grade", imt.grade);
		i.putExtra("to_user_id", -1);
	//	i.putExtras(bd);
		this.startActivityForResult(i,2);
		this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
		
	}
	
	public void onShareWeibo(View v)
	{
		if(!ProcessUserStatus.processUserStatus(this, 4)) return;
		
		String dir=getUploadPic(content_header);
		Intent i=new Intent();
		Bundle bd=new Bundle();
		bd.putSerializable("imt", imt);
		i.putExtra("dir", dir);
		i.putExtras(bd);
		i.setClass(WeiboDetail.this, ShareDialog.class);
		
		startActivity(i);
	}
	public void onGood(View v)
	{
		if(!ProcessUserStatus.processUserStatus(this, 4)) return;
		if(imt.grade==2)
			new SendWeibo().sendGood(this, UserManager.getCurUser(this.getApplicationContext()), imt.id, true);
		else
			new SendWeibo().sendGood(this,UserManager.getCurUser(this.getApplicationContext()), imt.id, false);
	}
	public void onGetMoreComment(View v)
	{
		comment_mode=FOOTVIEWSTATUS.REFRESHING;
		refreshFootView(comment_mode);
		String isTop;
		if(imt.grade==2)
			isTop="top";
		else
			isTop="notop";
		new GetCommentInfo().execute("comment_list",String.valueOf(imt.id),new String().valueOf(comment_list.size()),isTop);
		
	}
	
	public void onCommentToComment(View v)
	{
		if(!ProcessUserStatus.processUserStatus(this, 4)) return;
		int pos=(Integer)v.getTag();
		
		Intent i=new Intent();
	
		
		i.setClass(WeiboDetail.this, NewComment.class);
		/*Bundle bd=new Bundle();
	
		bd.putSerializable("data", imt);*/
		i.putExtra("title", "回复评论");
		i.putExtra("weibo_id", imt.id);
		i.putExtra("grade", imt.grade);
		i.putExtra("to_user_id",comment_list.get(pos).getUser_id());
	//	i.putExtras(bd);
		this.startActivityForResult(i,2);
		this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
		
	}
	public void onCancelWeiboDetail(View v)
	{
		this.setResult(1);
		this.finish();
		this.overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
		
	}

	@Override
	public void onBackPressed() {
		onCancelWeiboDetail(null);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==1)//delete weibo
		{
			if(resultCode==0)
			{
				if(imt.grade==2)
					new SendWeibo().deleteWeibo(this, UserManager.getCurUser(this.getApplicationContext()),imt.id,true);
				else
					new SendWeibo().deleteWeibo(this, UserManager.getCurUser(this.getApplicationContext()),imt.id,false);
			
				
				this.setResult(0);
				this.finish();
				this.overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
				
			}
		}
		if(requestCode==2)//comment 
		{
			if(resultCode==0)
			{
						
			}
		}
		if(requestCode==3)
		{
			if(resultCode==0)
			{
				
				Intent i=new Intent();
				i.setClass(WeiboDetail.this, Login.class);
				
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



	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

	
		if(arg1!=0)
		{
			nav_layout.setVisibility(View.VISIBLE);
		}
		else
			nav_layout.setVisibility(View.GONE);
		
		
	}


	

	@Override
	public void onScrollStateChanged(AbsListView arg0, int scrollState) {
		
		
	}

	private int getListViewHeight(CommentAdapter adapter,boolean isFirst)
	{
		   int totalHeight = 0;    
		   for (int i = 0, len = adapter.getCount(); i < len; i++) 
	        { 
		
		        View listItem = adapter.getView1(i);  
		         listItem.measure(0, 0); 
		        totalHeight += listItem.getMeasuredHeight(); 
	        }    
		   
	//	if(footView.getVisibility()==View.GONE)
	//		return totalHeight;
		   
	
		if(isFirst)
			return (int) (totalHeight+50*denisty);
		else
			return totalHeight+footView.getHeight();
		
	}

	private void refreshBlankFootView(boolean isFirst)
	{
		int delta=getDelta(getListViewHeight(comment_adapter,isFirst));
	
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

	public void onUserWeibo(View v)
	{
		startUserWeiboList(imt.user_id,imt.nickname);
	}
	public void onUserWeibo1(View v)
	{
		String tmp=(String)v.getTag();
		int pos=Integer.parseInt(tmp.substring(0,tmp.indexOf("http")));
		
		startUserWeiboList(comment_list.get(pos).getUser_id(),comment_list.get(pos).getNickname());
	}


	private void startUserWeiboList(int user_id,String nickname) {
		Intent i=new Intent();
		i.setClass(WeiboDetail.this, UserWeiboList.class);
		
		i.putExtra("isId", true);
		i.putExtra("nickname", nickname);
		
		i.putExtra("user_id",user_id);
		
		startActivity(i);
		this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
		
		
	}
	@Override
	public boolean onLongClick(View v) {
		
		String[] option={"复制帖子内容"};
		
		new AlertDialog.Builder(WeiboDetail.this).setTitle("选择操作").setItems(option, new OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
				
				coptToBuffer(false,-1);	
				
			}}).show();//显示对话框
		return false;
	}


	protected void coptToBuffer(boolean isComment,int index) {
		
		ClipboardManager cmb = (ClipboardManager)this.getSystemService(Context.CLIPBOARD_SERVICE);  
		if(!isComment)
			cmb.setText(content.getText().toString());
		else
			cmb.setText(comment_list.get(index).content);
		Toast.makeText(this, "已复制到剪帖板", Toast.LENGTH_SHORT).show();
		
	}


	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2,
			long arg3) {
		if(arg2==0||arg2==1) return false;
		String[] option={"复制评论内容"};
		
		new AlertDialog.Builder(WeiboDetail.this).setTitle("选择操作").setItems(option, new OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
				
				coptToBuffer(true,arg2-2);	
				
			}}).show();//显示对话框
		return false;
	}


}
