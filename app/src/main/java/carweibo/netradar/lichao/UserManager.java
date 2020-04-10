package carweibo.netradar.lichao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import android.database.Cursor;
import android.util.Log;

public class UserManager {
	Context context;
	static DBUility dbUility;
	static DBhelper db;

	
	public static void AddUser(Context context,
						UserInfo user)
	{
		dbUility=(DBUility)context;
		db=dbUility.getDB();
	
		if(isUserExisted(context,user.nickname))
			deleteUser(context,user.nickname);
		
		ContentValues cv=new ContentValues();
		cv.put("nickname", user.nickname);
		cv.put("username", user.username);
		cv.put("touxiang_url", user.touxiang_url);
		cv.put("user_type", user.user_type);
		cv.put("token", user.token);
		cv.put("expires_in", user.expires_in);
		cv.put("openid", user.openid);
		
		cv.put("register_time", getTime());
		cv.put("isLogin", "NOLOGIN");
		cv.put("auth_time", String.valueOf(System.currentTimeMillis()/1000));
		cv.put("user_score", 10);
		db.insert(dbUility.USER_TABLE,cv); 	
    	
	}
	public static void updateUser(Context context,
			String username,
			String nickname
			)
{
		dbUility=(DBUility)context;
		db=dbUility.getDB();
		
				
		ContentValues cv=new ContentValues();
		cv.put("nickname", nickname);
		
		
		cv.put("auth_time", new String().valueOf(System.currentTimeMillis()/1000));
		
		
		db.update(dbUility.USER_TABLE, cv, "username", username);

}
	public static String getCurUser(Context context)
	{
		dbUility=(DBUility)context;
		db=dbUility.getDB();
		Cursor c=db.query(dbUility.USER_TABLE);
		
		if(c.getCount()==0)
		{
			//Log.d("lichao",dbUility.USER_TABLE);
			return "NOUSER";
		}
		while(c.moveToNext())
		{
			if(c.getString(c.getColumnIndex("isLogin")).equals("LOGIN"))
				return c.getString(c.getColumnIndex("nickname"));
		}
		return "NOLOGIN";
		
	}
/*	public static String isCurUserLogin(Context context)
	{
		dbUility=(DBUility)context;
		db=dbUility.getDB();
		Cursor c=db.query(dbUility.USER_TABLE);
		
		if(c.getCount()==0)
		{
			return "nouser";
		}
		c.moveToFirst();
		return c.getString(c.getColumnIndex("isLogin"));
	}*/
	public static boolean isUserValid(UserInfo user)
	{
		
		String expires_in=user.expires_in;
		String auth_time=user.auth_time;
		
		long expire_time=Long.parseLong(expires_in);
		long auth=Long.parseLong(auth_time);
		long cur_time=System.currentTimeMillis()/1000;
		
		if((auth+expire_time)<cur_time)
		{
			//Toast.makeText(this, "微博授权过期，正在进行重新授权～", Toast.LENGTH_SHORT).show();
			
			return false;
		}
		return true;
	}
	public static void logoutCurUser(Context context)
	{
		dbUility=(DBUility)context;
		db=dbUility.getDB();
		ContentValues cv=new ContentValues();
		cv.put("isLogin", "NOLOGIN");
		db.update(dbUility.USER_TABLE, cv, null, null);
		
		Intent intent = new Intent("netradar.bd"); 
		 intent.putExtra("type", 4);
		 
		 context.sendBroadcast(intent); 
		
	}
	public static List<UserInfo> getUserInfo(Context context)
	{
		dbUility=(DBUility)context;
		db=dbUility.getDB();
		
		Cursor c=db.query(dbUility.USER_TABLE);
		if(c.getCount()==0) return null;
		
		List<UserInfo> list=new ArrayList<UserInfo>();
		while(c.moveToNext())
		{
			UserInfo userinfo=new UserInfo();
			
			userinfo.nickname=c.getString(c.getColumnIndex("nickname"));
			userinfo.user_type=c.getString(c.getColumnIndex("user_type"));
			userinfo.touxiang_url=c.getString(c.getColumnIndex("touxiang_url"));
			userinfo.token=c.getString(c.getColumnIndex("token"));
			userinfo.expires_in=c.getString(c.getColumnIndex("expires_in"));
			userinfo.openid=c.getString(c.getColumnIndex("openid"));
			userinfo.isLogin=c.getString(c.getColumnIndex("isLogin"));
			userinfo.expires_in=c.getString(c.getColumnIndex("expires_in"));
			userinfo.auth_time=c.getString(c.getColumnIndex("auth_time"));
			userinfo.username=c.getString(c.getColumnIndex("username"));
		
			list.add(userinfo);
		}
		return list;
		
	}
	public static boolean isUserExisted(Context context,String nickname)
	{
		dbUility=(DBUility)context;
		db=dbUility.getDB();
		Cursor c=db.queryItem(dbUility.USER_TABLE, "nickname", nickname);
		
		if(c.getCount()==0) return false;
		else return true;
	}
	
	public static void deleteUser(Context context,String nickname)
	{
		dbUility=(DBUility)context;
		db=dbUility.getDB();
		db.delete(dbUility.USER_TABLE, "nickname", nickname);
	}
	public static UserInfo getSingleUserInfo(Context context,String nickname)
	{
		dbUility=(DBUility)context;
		db=dbUility.getDB();
		Cursor c=db.queryItem(dbUility.USER_TABLE, "nickname", nickname);
		
		if(c.getCount()==0) return null;
		c.moveToFirst();
		UserInfo userinfo=new UserInfo();
		
		userinfo.username=c.getString(c.getColumnIndex("username"));
		userinfo.nickname=c.getString(c.getColumnIndex("nickname"));
		userinfo.user_type=c.getString(c.getColumnIndex("user_type"));
		userinfo.touxiang_url=c.getString(c.getColumnIndex("touxiang_url"));
		userinfo.token=c.getString(c.getColumnIndex("token"));
		userinfo.expires_in=c.getString(c.getColumnIndex("expires_in"));
		userinfo.openid=c.getString(c.getColumnIndex("openid"));
		userinfo.isLogin=c.getString(c.getColumnIndex("isLogin"));
		userinfo.auth_time=c.getString(c.getColumnIndex("auth_time"));
		
		return userinfo;
	}
	public static void setCurUser(Context context,String nickname)
	{
		dbUility=(DBUility)context;
		db=dbUility.getDB();
		
		
		
		
		Cursor c=db.query(dbUility.USER_TABLE);
		if(c.getCount()==0) return;
		
		ContentValues cv=new ContentValues();
		cv.put("isLogin", "NOLOGIN");
		db.update(dbUility.USER_TABLE, cv, null, null);
		cv.put("isLogin", "LOGIN");
		db.update(dbUility.USER_TABLE, cv, "nickname", nickname);
		
		Intent intent = new Intent("netradar.bd"); 
		 intent.putExtra("type", 4);
		 
		 context.sendBroadcast(intent); 
	}
	public static void updateUserScore(Context context,String nickname,int score)
	{
		dbUility=(DBUility)context;
		db=dbUility.getDB();
		
		Cursor c=db.queryItem(dbUility.USER_TABLE, "nickname", nickname);
		
		if(c.getCount()==0) return;
		c.moveToFirst();
		
		ContentValues cv=new ContentValues();
		cv.put("user_score", score);
		
		db.update(dbUility.USER_TABLE, cv, "nickname", nickname);
		
	}
	public static void AddUserScore(Context context,String nickname,int score)
	{
		dbUility=(DBUility)context;
		db=dbUility.getDB();
		
		Cursor c=db.queryItem(dbUility.USER_TABLE, "nickname", nickname);
		
		if(c.getCount()==0) return;
		c.moveToFirst();
		int old_score=c.getInt(c.getColumnIndex("user_score"));
		old_score=old_score+score;
		ContentValues cv=new ContentValues();
		cv.put("user_score", old_score);
		
		db.update(dbUility.USER_TABLE, cv, "nickname", nickname);
		
	}
	public static int GetUserScore(Context context,String nickname)
	{
		dbUility=(DBUility)context;
		db=dbUility.getDB();
		
		Cursor c=db.queryItem(dbUility.USER_TABLE, "nickname", nickname);
		
		if(c.getCount()==0) return 0;
		c.moveToFirst();
		int old_score=c.getInt(c.getColumnIndex("user_score"));
		c.close();
		return old_score;
		
	}
	private static String getTime()
	{
		 SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yyyy年MM月dd日");
		 Date   curDate   =   new   Date(System.currentTimeMillis());//获取当前时间    
		 return formatter.format(curDate);
	}
	public static UserInfo getBindedUser(Context context,String type)
	{
		List<UserInfo> userList=getUserInfo(context);
		if(userList==null)
		{
			return null;
		}
		
		for(UserInfo a:userList)
		{
			
			if(a.user_type.equals(type))
			{
				return a;
			}
			
			
		}
		return null;
	}
	public static boolean isExistTodayWeibo(Context context,String type)
	{
		
		SharedPreferences last_share_time =context.getSharedPreferences("last_share_time", 0);
		
		String date=last_share_time.getString(type, "");
		SimpleDateFormat   format=new   SimpleDateFormat("yyyyMMdd");
		 Date   curDate   =   new   Date(System.currentTimeMillis());
		
		String now=format.format(curDate);
		
		if(now.equals(date)) return true;
		return false;
	}
	public static void setLastShareTime(Context context,String type)
	{
		SharedPreferences last_share_time=context.getSharedPreferences("last_share_time", 0);
		Editor editor;
		editor=last_share_time.edit();
		SimpleDateFormat   format=new   SimpleDateFormat("yyyyMMdd");
		 Date   curDate   =   new   Date(System.currentTimeMillis());
		
		editor.putString(type, format.format(curDate));
		boolean b = editor.commit();
		
	}
}
