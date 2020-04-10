package carweibo.netradar.lichao;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class NvManager {
	
	public static String IS_VENDER_FIRST_RUN="vender_first_run";
	public static String MAX_WEIBO_ID="max_weiboid";
	public static String MAX_VENDER_ID="max_venderid";
	public static String READED_VENDER_ID="readed_venderid";
	public static String GAME_BACKGROUND_MUSIC="game_background_music";
	public static String GAME_PLAY_AUDIO_EFFECT="game_play_audiio_effect";
	
	static public String getNVString(Context context,String nv,String def)
	{
		SharedPreferences pre=context.getSharedPreferences("carweibo", Context.MODE_PRIVATE);
		
		return pre.getString(nv,def);
	}
	static public void setNVString(Context context,String nv,String var)
	{
		SharedPreferences pre= context.getSharedPreferences("carweibo", Context.MODE_PRIVATE);
		Editor editor = pre.edit();
		
		editor.putString(nv, var);
	
		editor.commit();
	}
	
	static public Boolean getNVBoolean(Context context,String nv,Boolean def)
	{
		SharedPreferences pre=context.getSharedPreferences("carweibo", Context.MODE_PRIVATE);
		
		return pre.getBoolean(nv,def);
	}
	static public void setNVBoolean(Context context,String nv,Boolean var)
	{
		SharedPreferences pre= context.getSharedPreferences("carweibo", Context.MODE_PRIVATE);
		Editor editor = pre.edit();
		
		editor.putBoolean(nv, var);
	
		editor.commit();
	}

	static public int getNVInt(Context context,String nv,int def)
	{
		SharedPreferences pre=context.getSharedPreferences("carweibo", Context.MODE_PRIVATE);
		
		return pre.getInt(nv,def);
	}
	static public void setNVInt(Context context,String nv,int var)
	{
		SharedPreferences pre= context.getSharedPreferences("carweibo", Context.MODE_PRIVATE);
		Editor editor = pre.edit();
		
		editor.putInt(nv, var);
	
		editor.commit();
	}
	static public long getNVLong(Context context,String nv,long def)
	{
		SharedPreferences pre=context.getSharedPreferences("carweibo", Context.MODE_PRIVATE);
		
		return pre.getLong(nv,def);
	}
	static public void setNVLong(Context context,String nv,long var)
	{
		SharedPreferences pre= context.getSharedPreferences("carweibo", Context.MODE_PRIVATE);
		Editor editor = pre.edit();
		
		editor.putLong(nv, var);
	
		editor.commit();
	}
}
