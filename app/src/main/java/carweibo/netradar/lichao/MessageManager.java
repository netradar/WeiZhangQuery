package carweibo.netradar.lichao;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carweibo.netradar.lichao.MessageInfo.MSG_STATUS;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class MessageManager {
	Context context;
	static DBUility dbUility;
	static DBhelper db;

	
	public static void AddMessage(Context context,
						MessageInfo	msg)
	{
		dbUility=(DBUility)context;
		db=dbUility.getDB();
	
		Cursor c=db.queryWhere(dbUility.PRIVATE_MSG_TABLE, "nickname='"+msg.getTo_nickname()+"' and from_user_id="+msg.getFrom_user_id());
		
		if(c.moveToFirst())
		{
			try
			{
				ContentValues cv=new ContentValues();
				cv.put("nickname",msg.getTo_nickname());
				
				cv.put("from_user_id", msg.getFrom_user_id());
				cv.put("from_nickname",msg.getFrom_nickname());
				cv.put("from_user_score", msg.getFrom_user_score());
			
				cv.put("from_touxiang_url", msg.getFrom_touxiang());
				if(!msg.isSend)
					cv.put("unread_num", c.getInt(c.getColumnIndex("unread_num"))+1);
				
				String msg_list=c.getString(c.getColumnIndex("msg_list"));
				JSONArray ja;
				if(msg_list==null||msg_list.length()==0)
					ja=new JSONArray();
				else
					ja=new JSONArray(msg_list);
				
				JSONObject js=new JSONObject();
				
				
				js.put("isSend", msg.isSend);
				js.put("msg_type", msg.getMsg_type());
				js.put("ref_weibo", msg.getRef_weibo());
				js.put("weibo_id", msg.getWeibo_id());
				js.put("is_top", msg.isTop());
				js.put("msg", msg.getMsg());
				js.put("time", msg.getTime());
				js.put("tag", msg.getMsg_tag());
				
				if(msg.getMsg_status()==MSG_STATUS.OK)
					js.put("status", 0);
				else
					js.put("status", 1);
				ja.put(js);
				
				cv.put("msg_list", ja.toString());
				
				db.update(dbUility.PRIVATE_MSG_TABLE, cv, "_id", String.valueOf(c.getInt(c.getColumnIndex("_id"))));
					
				
			} catch (JSONException e) {
				
			} finally{
				c.close();
			}
			return;
		}
		
		ContentValues cv=new ContentValues();
		cv.put("nickname",msg.getTo_nickname());
		
		cv.put("from_user_id", msg.getFrom_user_id());
		cv.put("from_nickname",msg.getFrom_nickname());
		cv.put("from_user_score", msg.getFrom_user_score());
		cv.put("from_touxiang_url", msg.getFrom_touxiang());
		if(!msg.isSend)
			cv.put("unread_num", 1);
		else
			cv.put("unread_num", 0);
		JSONArray ja=new JSONArray();
		JSONObject js=new JSONObject();
		
		try {
			js.put("isSend", msg.isSend);
			if(msg.getMsg_status()==MSG_STATUS.OK)
				js.put("status", 0);
			else
				js.put("status", 1);
			js.put("msg", msg.getMsg());
			js.put("time", msg.getTime());
			js.put("msg_type", msg.getMsg_type());
			js.put("ref_weibo", msg.getRef_weibo());
			js.put("weibo_id", msg.weibo_id);
			js.put("is_top", msg.isTop());
			js.put("tag", msg.getMsg_tag());
			ja.put(js);
			
			
		} catch (JSONException e) {
			return ;
		}
			
		cv.put("msg_list", ja.toString());
		
	
		
		db.insert(dbUility.PRIVATE_MSG_TABLE,cv); 	
    	
	}
	public static int getUnreadSum(Context context)
	{
		dbUility=(DBUility)context;
		db=dbUility.getDB();
		
		//Cursor c=db.query(dbUility.PRIVATE_MSG_TABLE);
		Cursor c;
		int sum=0;
		try {
			c = db.queryItem(dbUility.PRIVATE_MSG_TABLE, "nickname", URLEncoder.encode(UserManager.getCurUser(context),"UTF-8"));
		
			while(c.moveToNext())
			{
				sum=sum+c.getInt(c.getColumnIndex("unread_num"));
			
			}
			c.close();
		} catch (UnsupportedEncodingException e) {

		}
		
		
		return sum;
	}	
	public static List<MyMessageData> getMsgListByNickname(Context context,String nickname)
	{
		dbUility=(DBUility)context;
		db=dbUility.getDB();
		
		List<MyMessageData> list=new ArrayList<MyMessageData>();
		Cursor c = null;
		
		try {
			c = db.queryItem(dbUility.PRIVATE_MSG_TABLE, "nickname", URLEncoder.encode(nickname,"UTF-8"));
		
			if(c.getCount()==0)
			{
				c.close();
				return null;
			}
			while(c.moveToNext())
			{
				MyMessageData msg=new MyMessageData();
				msg.msg_id=c.getInt(c.getColumnIndex("_id"));
				msg.user_id=c.getInt(c.getColumnIndex("from_user_id"));
				msg.user_score=c.getInt(c.getColumnIndex("from_user_score"));
				msg.unread_num=c.getInt(c.getColumnIndex("unread_num"));
				
				msg.nickname=c.getString(c.getColumnIndex("from_nickname"));
				msg.touxiang_url=c.getString(c.getColumnIndex("from_touxiang_url"));
				msg.msgList=c.getString(c.getColumnIndex("msg_list"));
				list.add(msg);
			}
			c.close();
		} catch (UnsupportedEncodingException e) {

		
			return null;
		}
		
	
		return list;
	
	}
	public static String getSingleUserMsg(Context context,String nickname,int user_id)
	{
		dbUility=(DBUility)context;
		db=dbUility.getDB();
		
		Log.d("lichao",nickname+" id is "+user_id);
		String ret_str;
		Cursor c;
		
			try {
				c = db.queryWhere(dbUility.PRIVATE_MSG_TABLE, "nickname='"+URLEncoder.encode(nickname,"UTF-8")+"' and from_user_id="+user_id);
			
				if(!c.moveToFirst()) return null;
				
				ret_str=c.getString(c.getColumnIndex("msg_list"));
						
				//c.close();
				return ret_str;
			} catch (UnsupportedEncodingException e) {
			
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		
			
		
		
	
	}
	public static void deleteMsgById(Context context,String msg_id)
	{
		dbUility=(DBUility)context;
		db=dbUility.getDB();
		
		
		Cursor c;
		
			c = db.queryItem(dbUility.PRIVATE_MSG_TABLE, "_id",msg_id);
		
			if(!c.moveToFirst()) return;
			
			db.delete(dbUility.PRIVATE_MSG_TABLE, "_id", msg_id);
					
	}
	public static void clearMsgById(Context context,String msg_id)
	{
		dbUility=(DBUility)context;
		db=dbUility.getDB();
		
		
		Cursor c;
		
		c = db.queryItem(dbUility.PRIVATE_MSG_TABLE, "_id",msg_id);
	
		if(!c.moveToFirst()) return;
		
		ContentValues cv=new ContentValues();
		cv.put("unread_num",0);
		
		db.update(dbUility.PRIVATE_MSG_TABLE, cv, "_id", msg_id);
	}
	public static void deleteSingleMsg(Context context,String nickname,int from_user_id,int index)
	{
		dbUility=(DBUility)context;
		db=dbUility.getDB();
		
		
		Cursor c;
		
		try {
			c = db.queryWhere(dbUility.PRIVATE_MSG_TABLE, "nickname='"+URLEncoder.encode(nickname,"UTF-8")+"' and from_user_id="+from_user_id);
		
		
	
		if(!c.moveToFirst()) return;
		
		int msg_id=c.getInt(c.getColumnIndex("_id"));
		
		String msg_list=c.getString(c.getColumnIndex("msg_list"));
		
		if(msg_list==null||msg_list.length()==0) return;
		
		try {
			JSONArray ja=new JSONArray(msg_list);
			JSONArray ja_new=new JSONArray();
			for(int i=0;i<ja.length();i++)
			{
				if(i==index)
					continue;
				else
				{
					ja_new.put(ja.getJSONObject(i));
				}
			}
			ContentValues cv=new ContentValues();
			cv.put("msg_list",ja_new.toString());
			db.update(dbUility.PRIVATE_MSG_TABLE, cv, "_id", String.valueOf(msg_id));
			
		} catch (JSONException e) {
		
		}
		} catch (UnsupportedEncodingException e1) {
			
		}
	}
	public static void setSingleMsgStatusByTag(Context context,String nickname,int from_user_id,long tag,int status)
	{
		dbUility=(DBUility)context;
		db=dbUility.getDB();
		
		
		Cursor c;
		
		
			c = db.queryWhere(dbUility.PRIVATE_MSG_TABLE, "nickname='"+nickname+"' and from_user_id="+from_user_id);
		
		
	
		if(!c.moveToFirst()) return;
		
		int msg_id=c.getInt(c.getColumnIndex("_id"));
		
		String msg_list=c.getString(c.getColumnIndex("msg_list"));
		
		if(msg_list==null||msg_list.length()==0) return;
		
		try {
			JSONArray ja=new JSONArray(msg_list);
			JSONArray ja_new=new JSONArray();
			for(int i=0;i<ja.length();i++)
			{
				//Log.d("lichao","MessageManager tag is "+ja.getJSONObject(i).getLong("tag"));
				if(tag==ja.getJSONObject(i).getLong("tag"))
				{	
					JSONObject js=ja.getJSONObject(i);
				//	JSONObject js_new=new JSONObject();
					
					js.put("status", status);
					ja_new.put(js);
				}
				
				else
				{
					ja_new.put(ja.getJSONObject(i));
				}
			}
			ContentValues cv=new ContentValues();
			cv.put("msg_list",ja_new.toString());
			db.update(dbUility.PRIVATE_MSG_TABLE, cv, "_id", String.valueOf(msg_id));
			
		} catch (JSONException e) {
			Log.d("lichao","MessageManager json error: "+e.toString());
			
		
		}
		
	}
	/*public static void AddSingleMsg(Context context,String nickname,int from_user_id,String msg)
	{
		dbUility=(DBUility)context;
		db=dbUility.getDB();
		
		
		Cursor c;
		
		try {
			c = db.queryWhere(dbUility.PRIVATE_MSG_TABLE, "nickname='"+URLEncoder.encode(nickname,"UTF-8")+"' and from_user_id="+from_user_id);
			
		
	
			if(!c.moveToFirst()) return;
			
			int msg_id=c.getInt(c.getColumnIndex("_id"));
			
			String msg_list=c.getString(c.getColumnIndex("msg_list"));
			
			if(msg_list==null||msg_list.length()==0) return;
			
			try {
				JSONArray ja=new JSONArray(msg_list);
				JSONArray ja_new=new JSONArray();
				for(int i=0;i<ja.length();i++)
				{
					if(i==index)
						continue;
					else
					{
						ja_new.put(ja.getJSONObject(i));
					}
				}
				ContentValues cv=new ContentValues();
				cv.put("msg_list",ja_new.toString());
				db.update(dbUility.PRIVATE_MSG_TABLE, cv, "_id", String.valueOf(msg_id));
				
			} catch (JSONException e) {
			
			}
		} catch (UnsupportedEncodingException e1) {
			
		}
	}*/
}
