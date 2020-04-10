package carweibo.netradar.lichao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBhelper extends SQLiteOpenHelper {
	
	//private  String DB_NAME=null;// = "weizhang";  
    private  String CAR_TABLE="car_table";// = "carinfo";  //ver2 add
   
    private  String USER_TABLE="user_table";//ver3 add
    private  String WEIBO_TABLE="weibo_table";//ver3 add
    private  String DRAFT_TABLE="draft_table";//ver3 add
    private  String PRIVATE_MSG_TABLE="PRIVATE_MSG_TABLE";//ver3 add
    private  String AUDIO_LUKUANG_TABLE="audio_lukuang_table";//ver4 add
    
    private  String CREATE_CAR_TBL = null; 
    private  String CREATE_USER_TBL = null; 
    private  String CREATE_WEIBO_TBL = null; 
    private  String CREATE_DRAFT_TBL = null; 
    private  String CREATE_PRIVATE_MSG_TBL = null; 
    private  String CREATE_AUDIO_LUKUANG_TBL = null; 
      
    private SQLiteDatabase db;  

	DBhelper(Context c,String dbname){
		super(c,dbname,null,4);
		
		CREATE_CAR_TBL="create table " + CAR_TABLE+
				" (_id integer primary key autoincrement,num text,owner text,type text,querymethod text,fdjh text,time text,car text);" ;
		
		CREATE_USER_TBL="create table " + USER_TABLE+
				" (_id integer primary key autoincrement," +
				"nickname text," +
				"user_score inteter," +
				"username text," +
				"touxiang_url text," +
				"user_type text," +
				"register_time text," +
				"isLogin text," +
				"token text," +
				"expires_in text," +
				"openid text," +
				"auth_time text);" ;
		

		CREATE_WEIBO_TBL="create table " + WEIBO_TABLE+
				" (_id integer primary key autoincrement," +
				"weibo_id long," +
				"user_id long," +
				"nickname text," +
				"touxiang_url text," +
				"time text," +
				"content text," +
				"comment_num text," +
				"user_score integer," +
				"good_num text," +
				"pic_list_url text," +
				"grade integer);" ;
		
		CREATE_DRAFT_TBL="create table " + DRAFT_TABLE+
				" (_id integer primary key autoincrement," +
				
				"nickname text," +
				
				"time text," +
				"content text," +
				"type integer,"+
				"pic_list text);";
		
		CREATE_PRIVATE_MSG_TBL="create table " + PRIVATE_MSG_TABLE+
				" (_id integer primary key autoincrement," +
				"nickname text," +
				"from_user_id long," +
				"from_user_score int," +
				"from_nickname text," +
				"from_touxiang_url text," +
				"unread_num integer," +
				"msg_list mediumtext);" ;
		CREATE_AUDIO_LUKUANG_TBL="create table " + AUDIO_LUKUANG_TABLE+
				" (_id integer primary key autoincrement," +
				
				"lukuang_list mediumtext);" ;
		
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
	
		Log.d("lichao","db create");
		SQLiteDatabase db1=arg0;
		db1.execSQL(CREATE_CAR_TBL);
		db1.execSQL(CREATE_USER_TBL);
		db1.execSQL(CREATE_WEIBO_TBL);
		db1.execSQL(CREATE_DRAFT_TBL);
		db1.execSQL(CREATE_PRIVATE_MSG_TBL);
		db1.execSQL(CREATE_AUDIO_LUKUANG_TBL);
		
	
	}
	public void insert(String tbl,ContentValues cv){
		
		SQLiteDatabase db1=getWritableDatabase();
		
		Long l=db1.insert(tbl, null, cv);
		
		
		

				
	}
	public void update(String tbl,ContentValues cv,String key,String value){
		
		SQLiteDatabase db1=getWritableDatabase();
		
		if(key==null)
			db1.update(tbl, cv,null,null);
		else
			db1.update(tbl, cv, key+"=?", new String[]{value});
		
		

				
	}
	public void delete(String tbl,String key,String value){
		db=getWritableDatabase();
		db.delete(tbl, key+"=?", new String[] { value });

				
	}
	public Cursor queryItem(String tbl,String key,String value){
		db=getWritableDatabase();
		String t="'"+value+"'";

		if(db!=null)
		{	
			return db.query(tbl, null,key+"="+t,null,null,null,null);
		}
			return null;	
	}
	public Cursor queryWhere(String tbl,String where){
		db=getWritableDatabase();
		

		if(db!=null)
		{	
			return db.query(tbl, null,where,null,null,null,null);
		}
			return null;	
	}
	public void deleteall(String tbl)
	{
		SQLiteDatabase db1=getWritableDatabase();
		db1.delete(tbl, null, null);
	}
	public Cursor query(String tbl)
	{
		SQLiteDatabase db1=getWritableDatabase();
		if(db1!=null)
		{
			Cursor c=db1.query(tbl, null, null, null, null, null, null);
			return c;
		}
		return null;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db2, int oldversion, int newversion) {

		if(oldversion==2&&newversion==3)
		{
			db2.execSQL("alter table "+CAR_TABLE+" rename to temp_"+CAR_TABLE);
			
			db2.execSQL(CREATE_CAR_TBL);
			db2.execSQL(CREATE_USER_TBL);
			db2.execSQL(CREATE_WEIBO_TBL);
			db2.execSQL(CREATE_DRAFT_TBL);
			db2.execSQL(CREATE_PRIVATE_MSG_TBL);
			
			db2.execSQL("insert into "+CAR_TABLE+" select * from temp_"+CAR_TABLE);
			
			db2.execSQL("drop table temp_"+CAR_TABLE);
		}
		if(oldversion==2&&newversion==4)
		{
			db2.execSQL("alter table "+CAR_TABLE+" rename to temp_"+CAR_TABLE);
			
			db2.execSQL(CREATE_CAR_TBL);
			db2.execSQL(CREATE_USER_TBL);
			db2.execSQL(CREATE_WEIBO_TBL);
			db2.execSQL(CREATE_DRAFT_TBL);
			db2.execSQL(CREATE_PRIVATE_MSG_TBL);
			db2.execSQL(CREATE_AUDIO_LUKUANG_TBL);
			
			db2.execSQL("insert into "+CAR_TABLE+" select * from temp_"+CAR_TABLE);
			
			db2.execSQL("drop table temp_"+CAR_TABLE);
		}
		if(oldversion==3&&newversion==4)
		{
			db2.execSQL("alter table "+CAR_TABLE+" rename to temp_"+CAR_TABLE);
			db2.execSQL("alter table "+USER_TABLE+" rename to temp_"+USER_TABLE);
			db2.execSQL("alter table "+WEIBO_TABLE+" rename to temp_"+WEIBO_TABLE);
			db2.execSQL("alter table "+DRAFT_TABLE+" rename to temp_"+DRAFT_TABLE);
			db2.execSQL("alter table "+PRIVATE_MSG_TABLE+" rename to temp_"+PRIVATE_MSG_TABLE);
			
			
			db2.execSQL(CREATE_CAR_TBL);
			db2.execSQL(CREATE_USER_TBL);
			db2.execSQL(CREATE_WEIBO_TBL);
			db2.execSQL(CREATE_DRAFT_TBL);
			db2.execSQL(CREATE_PRIVATE_MSG_TBL);
			db2.execSQL(CREATE_AUDIO_LUKUANG_TBL);
			
			db2.execSQL("insert into "+CAR_TABLE+" select * from temp_"+CAR_TABLE);
			db2.execSQL("insert into "+USER_TABLE+" select * from temp_"+USER_TABLE);
			db2.execSQL("insert into "+WEIBO_TABLE+" select * from temp_"+WEIBO_TABLE);
			db2.execSQL("insert into "+DRAFT_TABLE+" select * from temp_"+DRAFT_TABLE);
			db2.execSQL("insert into "+PRIVATE_MSG_TABLE+" select * from temp_"+PRIVATE_MSG_TABLE);
			
			db2.execSQL("drop table temp_"+CAR_TABLE);
			db2.execSQL("drop table temp_"+USER_TABLE);
			db2.execSQL("drop table temp_"+WEIBO_TABLE);
			db2.execSQL("drop table temp_"+DRAFT_TABLE);
			db2.execSQL("drop table temp_"+PRIVATE_MSG_TABLE);
		}
		
		

	}
	
	public void backupData(String tbl)
	{
		
/*		String CREATE_BOOK = "create table book(bookId integer primarykey,bookName text)";

		String CREATE_NEW_BOOK = "create table book(bookId integer primarykey,bookName text,bookContent text)";

		String CREATE_TEMP_BOOK = "alter table book rename to _temp_book";

		String INSERT_DATA = "insert into book select *,' ' from _temp_book";

		String DROP_BOOK = "drop table _temp_book";*/


	}

}
