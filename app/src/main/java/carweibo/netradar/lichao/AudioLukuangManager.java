package carweibo.netradar.lichao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class AudioLukuangManager {
	Context context;
	static DBUility dbUility;
	static DBhelper db;

	
	public static void AddLukuang(Context context,
						AudioLukuangInfo	info)
	{
		dbUility=(DBUility)context;
		db=dbUility.getDB();
	
		Cursor c=db.query(dbUility.AUDIO_LUKUANG_TABLE);// "nickname='"+msg.getTo_nickname()+"' and from_user_id="+msg.getFrom_user_id());
		
		if(c.moveToFirst())
		{
			try
			{
				ContentValues cv=new ContentValues();
				
				String lukuang_list=c.getString(c.getColumnIndex("lukuang_list"));
				JSONArray ja;
				if(lukuang_list==null||lukuang_list.length()==0)
					ja=new JSONArray();
				else
					ja=new JSONArray(lukuang_list);
				
				
				Gson gson=new Gson();
				ja.put(new JSONObject(gson.toJson(info)));
				
				cv.put("lukuang_list", ja.toString());
				
				db.update(dbUility.AUDIO_LUKUANG_TABLE, cv, "_id", String.valueOf(c.getInt(c.getColumnIndex("_id"))));
					
				
			} catch (JSONException e) {
				
			} finally{
				c.close();
			}
			return;
		}
		
		ContentValues cv=new ContentValues();
		
		JSONArray ja=new JSONArray();
		
		
		try {
			Gson gson=new Gson();
			ja.put(new JSONObject(gson.toJson(info)));
			
			
			
		} catch (JSONException e) {
			return ;
		}
			
		cv.put("lukuang_list", ja.toString());
		
	
		
		db.insert(dbUility.AUDIO_LUKUANG_TABLE,cv); 	
    	
	}
	
	public static String getLukuangList(Context context)
	{
		dbUility=(DBUility)context;
		db=dbUility.getDB();
		String ret=null;
		
		Cursor c = null;
		
		c = db.query(dbUility.AUDIO_LUKUANG_TABLE);//, "nickname", URLEncoder.encode(nickname,"UTF-8"));

		if(c.getCount()==0)
		{
			c.close();
			return null;
		}
		if(c.moveToNext())
		{
			ret=c.getString(c.getColumnIndex("lukuang_list"));
			
		}
		c.close();
		return ret;
		
	
		
	
	}
	
	public static void updateLukuangList(Context context,long time,boolean isFinishDownload,boolean isUnRead)
	{
		dbUility=(DBUility)context;
		db=dbUility.getDB();
		
		ContentValues cv=new ContentValues();
		Cursor c = null;
		
		c = db.query(dbUility.AUDIO_LUKUANG_TABLE);//, "nickname", URLEncoder.encode(nickname,"UTF-8"));

		if(c.getCount()==0)
		{
			c.close();
			return;
		}
		if(c.moveToNext())
		{
			String lukuang_str=c.getString(c.getColumnIndex("lukuang_list"));
			
			try {
				JSONArray ja=new JSONArray(lukuang_str);
				JSONArray ja_new=new JSONArray();
				if(ja.length()!=0)
				{
					for(int i=0;i<ja.length();i++)
					{
						JSONObject js=ja.getJSONObject(i);
						if(js.getLong("send_time")==time)
						{
							js.put("isFinishDown", isFinishDownload);
							js.put("isUnRead", isUnRead);
							js.put("isPlaying", false);
							
						}
						ja_new.put(js);
					}
					
					cv.put("lukuang_list", ja.toString());
				}
			} catch (JSONException e) {
				
			}
			db.update(dbUility.AUDIO_LUKUANG_TABLE, cv, "_id", String.valueOf(c.getInt(c.getColumnIndex("_id"))));
			
		}
		c.close();
		
		
	
		
	
	}

	public static void deleteLukuang(Context context)
	{
		dbUility=(DBUility)context;
		db=dbUility.getDB();
		
		
		Cursor c;
		
		c = db.query(dbUility.AUDIO_LUKUANG_TABLE);//, "_id",msg_id);
		
		if(!c.moveToFirst()) return;
			
		db.delete(dbUility.AUDIO_LUKUANG_TABLE, "_id", String.valueOf(c.getInt(c.getColumnIndex("_id"))));
		c.close();			
	}
	
}
