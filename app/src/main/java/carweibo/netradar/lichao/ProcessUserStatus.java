package carweibo.netradar.lichao;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ProcessUserStatus {

	public static boolean processUserStatus(Context context,int requestCode)
	{
		String cur_user=UserManager.getCurUser(context.getApplicationContext());
		if(cur_user==null)
		{
			newUserLogin(context,requestCode);
			return false;
		}
		
		if(cur_user.equals("NOUSER"))
		{
			newUserLogin(context,requestCode);
			return false;
		}
		if(cur_user.equals("NOLOGIN"))
		{
		
			selectUser(context,requestCode);
			return false;
		}
		
		return true;
		
		
	
	}
	private static void selectUser(Context context,int requestCode)
	{
		
		Intent i=new Intent();
		i.setClass(context, SelectUser.class);
		((Activity) context).startActivityForResult(i,requestCode);
		((Activity) context).overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
		Toast.makeText(context, "您还没登录，请选择一个用户登录～", Toast.LENGTH_SHORT).show();
		
	}
	
	private static void newUserLogin(Context context,int requestCode)
	{
		Intent i=new Intent();
		i.setClass(context, Login.class);
		
		((Activity) context).startActivityForResult(i, requestCode);
		((Activity) context).overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);//全新登录，数据库没有用户数据

		Toast.makeText(context, "您还没登录，选择一种登录方式吧～", Toast.LENGTH_SHORT).show();
	}
	
}
