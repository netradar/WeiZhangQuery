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
		Toast.makeText(context, "����û��¼����ѡ��һ���û���¼��", Toast.LENGTH_SHORT).show();
		
	}
	
	private static void newUserLogin(Context context,int requestCode)
	{
		Intent i=new Intent();
		i.setClass(context, Login.class);
		
		((Activity) context).startActivityForResult(i, requestCode);
		((Activity) context).overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);//ȫ�µ�¼�����ݿ�û���û�����

		Toast.makeText(context, "����û��¼��ѡ��һ�ֵ�¼��ʽ�ɡ�", Toast.LENGTH_SHORT).show();
	}
	
}
