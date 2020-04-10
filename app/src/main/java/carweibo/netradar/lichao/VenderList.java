package carweibo.netradar.lichao;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class VenderList extends Activity implements OnItemClickListener {

	Context context;
	
	ListView vender_listView;
	
	SimpleAdapter adapter;
	
	ArrayList<HashMap<String,String>> data;
	
	ArrayList<VenderInfo> vender_list;
	
	int[] to={R.id.vender_item_name,R.id.vender_item_nickname,R.id.vender_item_time,R.id.vender_item_good_comment,R.id.vender_item_bad_comment};
	String from[]={"name","nickname","time","good_comment","bad_comment"};
	
	ProgressBar progress;
	
	BroadcastReceiver bdReceiver;
	private IntentFilter ifilter;
	private LinearLayout footView;
	
	public class GetVenderList extends AsyncTask<Integer, Integer, String> {
		

		@Override
		protected void onPostExecute(String result) {
			
			processVenderResult(result);
			super.onPostExecute(result);
		}

		@Override
		protected String doInBackground(Integer... arg0) {
			
			return new LoginToServer().GetVenderList(context,arg0[0]);
		
		}

	}
	
	Button getmore_btn;
	
	LinearLayout getting_layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.vender_list_layout);
		vender_listView=(ListView)findViewById(R.id.vender_list_listview);
		progress=(ProgressBar)findViewById(R.id.vender_list_progressBar);
		footView=(LinearLayout)LayoutInflater.from(this).inflate(R.layout.foot, null);
		getmore_btn=(Button)footView.findViewById(R.id.moreweibo);
		getting_layout=(LinearLayout)footView.findViewById(R.id.getting_weibo);
		
		
		vender_listView.addFooterView(footView);
		getting_layout.setVisibility(View.VISIBLE);
		getmore_btn.setVisibility(View.GONE);
		
		data=new ArrayList<HashMap<String,String>>();
		
		vender_list=new ArrayList<VenderInfo>();
		
		adapter=new SimpleAdapter(this, data, R.layout.vender_item_layout, from, to);
		
		vender_listView.setAdapter(adapter);
		
		vender_listView.setOnItemClickListener(this);
		context=this;
		
		 bdReceiver=new BroadcastReceiver() {  
	  		  
	    	    @Override  
	    	    public void onReceive(Context context, Intent intent) {  
	    	          
	    	    	
	    	    	String bd_type=intent.getAction();
	    	    	
	    	    	if(bd_type.equals("netradar.bd.vender"))
	    	    	{
	    	    		int type=intent.getIntExtra("type", -1);
	    	    		
		    	    	if(type==1)
		    	    		refreshNewComment(intent.getLongExtra("vender_id", -1),intent.getBooleanExtra("isBadComment", false));
	    	    	}
	    	      	    	
	    	    	
	    	    	
	    	    	
	    	    }

				
	    	  
	    	}; 
	    	ifilter= new IntentFilter(); 
	    	ifilter.addAction("netradar.bd.vender");
	    	
	    	registerReceiver(bdReceiver,ifilter);
		
		refreshVender(-1);
		progress.setVisibility(View.VISIBLE);
	}

	
	protected void refreshNewComment(long vender_id, boolean isBadComment) {
	
		int index=0;
	
		for(VenderInfo info:vender_list)
		{
			if(info.id==vender_id)
			{
				if(isBadComment)
				{
					info.bad_comment_num=info.bad_comment_num+1;
					HashMap<String,String> map=data.get(index);
					map.put("bad_comment", "差评："+info.bad_comment_num);
					data.set(index, map);
				}
				else
				{
					
					info.good_comment_num=info.good_comment_num+1;
					HashMap<String,String> map=data.get(index);
					map.put("good_comment", "好评："+info.good_comment_num);
					data.set(index, map);
				}
				adapter.notifyDataSetChanged();
				break;
			}
			index++;
		}
	}


	@Override
	protected void onDestroy() {
		unregisterReceiver(bdReceiver);
		super.onDestroy();
	}


	public void processVenderResult(String result)
	{
		progress.setVisibility(View.GONE);
		getting_layout.setVisibility(View.GONE);
		if(result.equals("error_data")||result.startsWith("ErrorCode"))
		{
			Toast.makeText(this, "网络或数据异常，请稍候再试～", Toast.LENGTH_SHORT).show();
			return;
		}
		
		try {
			JSONObject js=new JSONObject(result);
			
			JSONArray ja=js.getJSONArray("vender_list");
	
			for(int i=0;i<ja.length();i++)
			{
				VenderInfo info=new VenderInfo();
				info.id=ja.getJSONObject(i).getLong("id");
				info.nickname=URLDecoder.decode(ja.getJSONObject(i).getString("nickname"),"UTF-8");
				info.name=URLDecoder.decode(ja.getJSONObject(i).getString("name"),"UTF-8");
				info.addr=URLDecoder.decode(ja.getJSONObject(i).getString("addr"),"UTF-8");
				info.time=ja.getJSONObject(i).getString("time");
				info.comment=URLDecoder.decode(ja.getJSONObject(i).getString("comment"),"UTF-8");
			//	info.comment_list=URLDecoder.decode(ja.getJSONObject(i).getString("comment_list"),"UTF-8");
				info.pic_list=ja.getJSONObject(i).getString("pic_list");
				
				info.bad_comment_num=ja.getJSONObject(i).getInt("bad_comment_num");
				info.good_comment_num=ja.getJSONObject(i).getInt("good_comment_num");
				addToListViewData(info);
				vender_list.add(info);
			}
			
			if(ja.length()==25)
			{
				
				getmore_btn.setVisibility(View.VISIBLE);
			}
			else
			{
				getmore_btn.setVisibility(View.GONE);
			}
			adapter.notifyDataSetChanged();
			
		} catch (JSONException e) {
			Toast.makeText(this, "网络或数据异常，请稍候再试～", Toast.LENGTH_SHORT).show();
			return;
		} catch (UnsupportedEncodingException e) {
			Toast.makeText(this, "网络或数据异常，请稍候再试～", Toast.LENGTH_SHORT).show();
			return;
		}
		
	}


	private void addToListViewData(VenderInfo info) {
	
		HashMap<String,String> map=new HashMap<String,String>();
		map.put("name",info.name);
		map.put("nickname", "来自："+info.nickname);
		
		map.put("time", "时间："+getTime(info.time));
		map.put("good_comment", "好评："+info.good_comment_num);
		map.put("bad_comment", "差评："+info.bad_comment_num);
		
		data.add(map);
		
	}


	@Override
	protected void onResume() {
		
		super.onResume();
		
	}


	public void onAddVender(View v)
	{
		if(!ProcessUserStatus.processUserStatus(this,1))
			return;
		Intent i=new Intent();
		i.setClass(VenderList.this, NewVender.class);
		this.startActivity(i);
		overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
		
	}
	public void onCancelVenderList(View v)
	{
		this.finish();
		overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
	}

	@Override
	public void onBackPressed() {
		onCancelVenderList(null);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode)
		{
		case 1:
			if(resultCode==1)
			{
				UserManager.setCurUser(this.getApplicationContext(), data.getStringExtra("nickname"));
				
				Toast.makeText(this, "登录成功～", Toast.LENGTH_SHORT).show();
				
			}
			break;
			
		}
	}
	
	private void refreshVender(int readed_id)
	{
		new GetVenderList().execute(readed_id);
	}
	public String getTime(String time){
		 SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("MM月dd日   HH:mm");
	//	 Log.d("lichao","time is "+time);
		 
		// return formatter.format(time);
		 return time.substring(4,6)+"月"+time.substring(6,8)+"日 "+time.substring(8,10)+":"+time.substring(10,12);
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		Intent i=new Intent();
		i.setClass(VenderList.this, VenderDetail.class);
		
		Rect frame=new Rect();
		this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int screenWidth  = getWindowManager().getDefaultDisplay().getWidth();       // 屏幕宽（像素，如：480px）   
		int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
	
		Bundle bd=new Bundle();
		
		bd.putSerializable("vender", vender_list.get(arg2));
		i.putExtra("stateHeight", frame.top);
		i.putExtra("screenWidth", screenWidth);
		i.putExtra("screenHeight", screenHeight);
		i.putExtra("bad_comment_num", vender_list.get(arg2).bad_comment_num);
		i.putExtra("good_comment_num", vender_list.get(arg2).good_comment_num);
		i.putExtra("pic_list", vender_list.get(arg2).pic_list);
		i.putExtras(bd);
		startActivity(i);
		overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
		
	}
	
	public void onMoreWeibo(View v)
	{
		getting_layout.setVisibility(View.VISIBLE);
		getmore_btn.setVisibility(View.GONE);
		
		refreshVender((int) vender_list.get(vender_list.size()-1).id);
	}
}
