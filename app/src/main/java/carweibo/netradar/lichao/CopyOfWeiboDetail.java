package carweibo.netradar.lichao;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carweibo.netradar.lichao.AsyncImageLoader.ImageCallback;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class CopyOfWeiboDetail extends Activity implements ImageCallback {

	
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
	
	TextView retweet,comment,good;
	ImageView retweet_nav,comment_nav,good_nav;
	
	CommentListView comment_listview;
	ListView retweet_listview,good_listview;
	List<Map<String,String>> comment_list,retweet_list,good_list;
	LinearLayout footView;
	
	CommentAdapter comment_adapter;
	FrameLayout comment_frame_layout;
	MyScrollView scroll_view;
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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.weibo_detail_layout);
		
		imt=(ImageAndText) this.getIntent().getExtras().getSerializable("data");
		screenHeight=this.getIntent().getIntExtra("screenHeight", 800);
		screenWidth=this.getIntent().getIntExtra("screenWidth", 500);
		stateHeight=this.getIntent().getIntExtra("stateHeight", 30);
		denisty=this.getResources().getDisplayMetrics().density;
        
		asyncimageloader=new AsyncImageLoader(denisty);
		parent=(View)findViewById(R.layout.weibo_detail_layout);
		context=this;
		initView();
		
		initData(imt);
		new GetCommentInfo().execute("comment_list",new String().valueOf(imt.id),new String().valueOf(-1),"notop");
		
	}

	
	public void progressCommentReture(String result) {
		
		if(result.equals("error_data"))
		{
			return;
		}
		
		try {
			JSONObject js=new JSONObject(result);
			JSONArray ja=js.getJSONArray("ret_comment_list");
			for(int i=0;i<ja.length();i++)
			{
				Map<String,String> map=new HashMap<String,String>();
				map.put("nickname",URLDecoder.decode(ja.getJSONObject(i).getString("nickname"),"UTF-8"));
				map.put("content", URLDecoder.decode(ja.getJSONObject(i).getString("content"),"UTF-8"));
				map.put("time", getTime(ja.getJSONObject(i).getString("time")));
				comment_list.add(map);
				
			}
			comment_adapter.notifyDataSetChanged();
		//	adjustViewHeight(comment_listview);
		} catch (JSONException e) {
			Log.d("lichao","json error in progress comment ret");
		} catch (UnsupportedEncodingException e) {
			
			Log.d("lichao","UnsupportedEncodingException");
		}
	}

	private String getTime(String time)
	{
		return time.substring(0,4)+"年"+time.substring(4,6)+"月"+time.substring(6,8)+"日 "+time.substring(8,10)+":"+time.substring(10,12)+":"+time.substring(12,14);
	}
	private void adjustViewHeight(ListView listView) {
		
        if (comment_adapter == null) {  
             
            return;  
        }  
  
        int totalHeight = 0;  
        for (int i = 0, len = comment_adapter.getCount(); i < len; i++) {  
            View listItem = comment_adapter.getView(i, null, listView);  
            listItem.measure(0, 0);  
            totalHeight += listItem.getMeasuredHeight(); 
        }  
  
        ViewGroup.LayoutParams params = listView.getLayoutParams();  
        params.height = totalHeight + (listView.getDividerHeight() * (comment_adapter.getCount() - 1));  
        
        listView.setLayoutParams(params);  
		
	}


	private void initData(ImageAndText imt) {
		
		nickname.setText(imt.nickname);
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
		
		/*comment_adapter=new CommentAdapter(this,comment_list,R.layout.comment_item_layout,
				new String[]{"nickname","content","touxiang","time","isvip"},
				new int[]{R.id.comment_item_nickname,R.id.comment_item_content,R.id.comment_item_touxiang,R.id.comment_item_time,R.id.comment_item_vip_sign},0,null);
	*/
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
		
		
	}

	private void initView() {
	
		touxiang=(ImageView)findViewById(R.id.weibo_detail_touxiang);
		vip_sign=(ImageView)findViewById(R.id.weibo_detail_vip_sign);
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
	
		/*		comment_num=(TextView)findViewById(R.id.weibo_d);
		good_num=(TextView)findViewById(R.id.weibo_detail_touxiang);*/
		
		
		
	}

	@Override
	public void imageLoaded(Bitmap imageDrawable, String imageUrl, String id) {
		parent=((ViewGroup)this.findViewById(android.R.id.content)).getChildAt(0);
		ImageView iv=(ImageView) parent.findViewWithTag(id+imageUrl);
		if(iv!=null)
		{
			if(imageDrawable!=null)
				iv.setImageBitmap(imageDrawable);
		}
		
	}
	
	public void onViewPic(View v)
	{
		String tag=(String)v.getTag();
		int pos=Integer.parseInt(tag.substring(0,tag.indexOf("http")));
		
	
		
		
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
		
		i.setClass(CopyOfWeiboDetail.this, ViewPic.class);
		Bundle bd=new Bundle();
	
		bd.putSerializable("data", imt);
		i.putExtra("index", k);
		i.putExtra("height", frame.top);
		i.putExtra("screenWidth",screenWidth);
		i.putExtra("screenHeight", screenHeight);
		i.putExtras(bd);
		startActivity(i);
	}

	public void onClickRetweet(View v)
	{
		retweet.setTextColor(Color.parseColor("#111000"));
		comment.setTextColor(Color.parseColor("#929292"));
		good.setTextColor(Color.parseColor("#929292"));
		
		retweet_nav.setVisibility(View.VISIBLE);
		comment_nav.setVisibility(View.GONE);
		good_nav.setVisibility(View.GONE);
	}
	
	public void onClickComment(View v)
	{
		retweet.setTextColor(Color.parseColor("#929292"));
		comment.setTextColor(Color.parseColor("#111000"));
		good.setTextColor(Color.parseColor("#929292"));	
		
		retweet_nav.setVisibility(View.GONE);
		comment_nav.setVisibility(View.VISIBLE);
		good_nav.setVisibility(View.GONE);
	}
	
	public void onClickGood(View v)
	{
		retweet.setTextColor(Color.parseColor("#929292"));
		comment.setTextColor(Color.parseColor("#929292"));
		good.setTextColor(Color.parseColor("#111000"));	
		
		retweet_nav.setVisibility(View.GONE);
		comment_nav.setVisibility(View.GONE);
		good_nav.setVisibility(View.VISIBLE);
		
		
	}
	public void onDeleteWeibo(View v)
	{
		Intent i=new Intent();
		
		
		i.putExtra("text", "提示\n\n"+"确定删除帖子吗？");
		i.putExtra("ok", "确定");
		i.putExtra("cancel", "取消");
		i.setClass(CopyOfWeiboDetail.this, Dialog.class);
		this.startActivityForResult(i,1);
	}
	public void onComment(View v)
	{
		Intent i=new Intent();
	
		
		i.setClass(CopyOfWeiboDetail.this, NewComment.class);
		Bundle bd=new Bundle();
	
		bd.putSerializable("data", imt);
		i.putExtra("title", "发表评论");
		i.putExtras(bd);
		startActivity(i);
		this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
		
	}
	
	public void onShareWeibo(View v)
	{
		String dir=getUploadPic((View) v.getParent().getParent());
		
		
		Intent i=new Intent();
		Bundle bd=new Bundle();
		bd.putSerializable("imt", imt);
		i.putExtra("dir", dir);
		i.putExtras(bd);
		i.setClass(CopyOfWeiboDetail.this, ShareDialog.class);
		
		startActivity(i);
	}
	public void onGood(View v)
	{
		if(imt.grade==2)
			new SendWeibo().sendGood(this, UserManager.getCurUser(this.getApplicationContext()), imt.id, true);
		else
			new SendWeibo().sendGood(this,UserManager.getCurUser(this.getApplicationContext()), imt.id, false);
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
		if(requestCode==1)
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


	




	
}
