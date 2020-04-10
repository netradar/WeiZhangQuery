package carweibo.netradar.lichao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class DraftList extends Activity implements OnItemClickListener, OnItemLongClickListener {

	ListView listview;
	
	List<HashMap<String, String>> list_data;
	SimpleAdapter adapter;
	String nickname;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.draft_layout);
		
		nickname=this.getIntent().getStringExtra("nickname");
		
		
		
		String[] from={"content","time"};
		int[]    to={R.id.draft_item_content,R.id.draft_item_time};
		
		listview=(ListView)findViewById(R.id.draft_listview);
		list_data=new ArrayList<HashMap<String,String>>();
		
		List<DraftInfo> list=DraftManager.getDraftListByNickname(this.getApplicationContext(), nickname);
		
		if(list!=null)
		{
			for(DraftInfo info:list)
			{
				HashMap<String,String> map=new HashMap<String,String>();
				map.put("content", info.content);
				map.put("time", info.time);
				map.put("pic_list", info.pic_list);
				map.put("id", String.valueOf(info.id));
				list_data.add(map);
			}
		}
		adapter=new SimpleAdapter(this, list_data, R.layout.draft_item_layout, from, to);
		
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(this);
		listview.setOnItemLongClickListener(this);
		
		
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	/*	list_data.clear();
		List<DraftInfo> list=DraftManager.getDraftListByNickname(this.getApplicationContext(), nickname);
		
		if(list!=null)
		{
			for(DraftInfo info:list)
			{
				HashMap<String,String> map=new HashMap<String,String>();
				map.put("content", info.content);
				map.put("time", info.time);
				map.put("pic_list", info.pic_list);
				map.put("id", String.valueOf(info.id));
				list_data.add(map);
			}
		}
		adapter.notifyDataSetChanged();*/
	}


	@Override
	public void onBackPressed() {
		
		super.onBackPressed();
		this.finish();
		this.overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
	}


	public void onCancelDraftList(View v)
	{
		onBackPressed();
	}


	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
	
		return false;
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent i=new Intent();
		i.setClass(DraftList.this, NewWeibo.class);
		
		i.putExtra("isDraft", true);
		i.putExtra("content", list_data.get(arg2).get("content"));
		i.putExtra("pic_list", list_data.get(arg2).get("pic_list"));
		i.putExtra("draft_id", list_data.get(arg2).get("id"));
		
		startActivity(i);
		
		list_data.remove(arg2);
		adapter.notifyDataSetChanged();
		this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
	}
	
	public void onDeleteAllDraft(View v)
	{
		if(list_data.size()==0) return;
		
		Intent i=new Intent();
		i.setClass(DraftList.this, Dialog.class);
		i.putExtra("text", "提示\n\n您确定要删除所有草稿内容吗？");
		i.putExtra("ok","确定");
		i.putExtra("cancel", "取消");
		startActivityForResult(i,2);
		//list_data.clear();
		
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode==2)
		{
			if(resultCode==0)
			{
				list_data.clear();
				adapter.notifyDataSetChanged();
				DraftManager.deleteAllDraftByNickname(this.getApplicationContext(), UserManager.getCurUser(this.getApplicationContext()));
			}
		}
	}
	
}
