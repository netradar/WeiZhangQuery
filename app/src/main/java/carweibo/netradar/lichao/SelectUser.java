package carweibo.netradar.lichao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

public class SelectUser extends Activity {

	ListView user_listview;
	String selected_nickname=null;
	String selected_type;
	ImageView last_imageview=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.select_user_layout);
		user_listview=(ListView)findViewById(R.id.selectuser_listview);
	
		refreshList();
	}
	private void refreshList() {
		List<UserInfo> user_list=UserManager.getUserInfo(this.getApplicationContext());
		if(user_list!=null)
		{
			SimpleAdapter adapter=getAdapter(user_list);
			adapter.setViewBinder(new ViewBinder() {

				@Override
				public boolean setViewValue(View view, Object data,
						String textRepresentation) {
					if(view instanceof ImageView && data instanceof Bitmap)
					{   
						ImageView iv = (ImageView) view;  
						iv.setImageBitmap((Bitmap) data);  
						
						return true;   
						}
					else   
						return false; 
					
				} }
				

				);
		user_listview.setAdapter(adapter);
		}
		
	}
	public void onCancelSelectUser(View v)
	{
		Log.d("lichao","oncancel sele");
		endSelect(0,"");
	}
	public void onAddAccount(View v)
	{
		Intent i=new Intent();
		i.setClass(SelectUser.this, Login.class);
		i.putExtra("newUser_flag", 1);
		this.startActivityForResult(i, 1);
		this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
	}
	public void onSelectLogin(View v)
	{
		if(selected_nickname==null)
		{
			return;
		}
		UserInfo user=UserManager.getSingleUserInfo(this.getApplicationContext(), selected_nickname);
		/*if(user.user_type.equals("CarWeibo"))
		{
			Intent i=new Intent();
			i.setClass(SelectUser.this, CarWeiboLogin.class);
			i.putExtra("username", user.username);
			this.startActivityForResult(i, 1);
			this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
			return;
			
		}*/
		endSelect(1,selected_nickname);
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		endSelect(0,"");
	}
	private void endSelect(int success,  String nickname) {
		
		
		Intent i=new Intent();
		i.putExtra("nickname", nickname);
		this.setResult(success, i);
		this.finish();
		this.overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
		
	}
	private SimpleAdapter getAdapter(List<UserInfo> l) {
		
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
    	for(UserInfo a:l)
    	{
    		Map<String,Object> map=new HashMap<String,Object>();

				map.put("nickname",a.nickname);
				if(a.user_type.equals("QQ"))
					map.put("usertype","QQ’ ∫≈");
				else if(a.user_type.equals("Weibo"))
					map.put("usertype", "–¬¿ÀŒ¢≤©’ ∫≈");
				else
					map.put("usertype", "≥µ”—»¶’ ∫≈");
				if(a.touxiang_url!=null)
					map.put("touxiang",getTouxiangBmp(a.touxiang_url));
				else
					map.put("touxiang",BitmapFactory.decodeResource(this.getResources(), R.drawable.switchuser));

    	 

			list.add(map);
		}
		
		return new SimpleAdapter(this,list,R.layout.select_user_item_layout,new String[]{"nickname","usertype","touxiang"},new int[]{R.id.selectuser_nickname,R.id.selectuser_usertype,R.id.selectuser_touxiang});
	
	}
	private Bitmap getTouxiangBmp(String dir)
	{
		
		if(dir==null) return null;
		
		return BitmapFactory.decodeFile(dir);
	}
	
	public void onItemClick(View v) {
		
		TextView tx=(TextView)v.findViewById(R.id.selectuser_nickname);
		TextView type=(TextView)v.findViewById(R.id.selectuser_usertype);
		selected_nickname=tx.getText().toString();
		selected_type=type.getText().toString();
		if(last_imageview!=null) last_imageview.setImageResource(R.drawable.icon_grey);
		ImageView iv=(ImageView)v.findViewById(R.id.selected_icon);
		last_imageview=iv;
		iv.setImageResource(R.drawable.icon_green);
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch(resultCode)
		{
		case 0:
			endSelect(1,data.getStringExtra("nickname"));
			break;
		case -1:
			
			break;
		}
	}

	
}
