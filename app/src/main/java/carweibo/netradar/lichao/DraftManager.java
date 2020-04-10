package carweibo.netradar.lichao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class DraftManager {
	Context context;
	static DBUility dbUility;
	static DBhelper db;

	
	public static void addDraft(Context context,String time,String nickname,String content,int type,String pic_list)
	{
		dbUility=(DBUility)context;
		db=dbUility.getDB();
	
		ContentValues cv=new ContentValues();
		cv.put("nickname", nickname);
		cv.put("time", getTime());
		cv.put("content", content);
		cv.put("type", type);
		cv.put("pic_list", pic_list);
		
		db.insert(dbUility.DRAFT_TABLE,cv); 	
    	
	}
	private static String getTime(){
		 SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("MM‘¬dd»’   HH:mm");
		 
		 Date   curDate   =   new   Date(System.currentTimeMillis());   
		 return formatter.format(curDate);
	}
	public static List<DraftInfo> getDraftListByNickname(Context context,String nickname)
	{
		dbUility=(DBUility)context;
		db=dbUility.getDB();
		
		List<DraftInfo> list=new ArrayList<DraftInfo>();
			
		Cursor c = null;
		c = db.queryItem(dbUility.DRAFT_TABLE, "nickname", nickname);
		if(c.getCount()==0)
		{
			c.close();
			return null;
		}
		while(c.moveToNext())
		{
			DraftInfo msg=new DraftInfo();
			msg.id=c.getInt(c.getColumnIndex("_id"));
			msg.time=c.getString(c.getColumnIndex("time"));
			msg.nickname=c.getString(c.getColumnIndex("nickname"));
			msg.type=c.getInt(c.getColumnIndex("type"));
			
			msg.content=c.getString(c.getColumnIndex("content"));
			msg.pic_list=c.getString(c.getColumnIndex("pic_list"));
			
			list.add(msg);
		}
		c.close();
	
		return list;
		
		
		

	}
	public static void deleteDraft(Context context,int id)
	{
		dbUility=(DBUility)context;
		db=dbUility.getDB();
		
				
		ContentValues cv=new ContentValues();
		db.delete(dbUility.DRAFT_TABLE, "_id",String.valueOf(id));

	}
	public static void deleteAllDraftByNickname(Context context,String nickname)
	{
		dbUility=(DBUility)context;
		db=dbUility.getDB();
		
				
		ContentValues cv=new ContentValues();
		db.delete(dbUility.DRAFT_TABLE, "nickname",nickname);

	}
	public static void addDraftVender(Context context,
			Object object, String name, String addr, String comment,
			ArrayList<ImageInfo> bmpFile_list, String string) {
		
		
	}
	public static void deleteDraftVender(Context applicationContext,
			int draft_id) {
		
		
	}

}
