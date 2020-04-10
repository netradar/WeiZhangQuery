package heart4.netradar.lichao;

import android.graphics.Rect;
import android.view.MotionEvent;

public class Tools {
	
	
	public static int CMD_LOGIN=0;
	public static int CMD_START=1;
	public static int CMD_QUIT=2;
	public static int CMD_EXIT=3;
	public static int CMD_ALERT=4;
	public static int CMD_READY=5;
	public static int CMD_PLAY=6;
	public static int CMD_MSG=7;
	public static int CMD_CALL=8;
	public static int CMD_CONNECT_STATUS=9;
	public static int CMD_CONNECT_FAIL=10;
	public static int CMD_DISCONNECTED=11;
	public static int CMD_CONNECT_FAILED=12;
	public static int CMD_ENTER_ROOM=13;
	public static int CMD_CONNECT_SUCCESS = 14;
	public static int CMD_NEW_PLAYER=15;
	public static int CMD_PLAYER_EXIT=16;
	public static int CMD_PLAYER_DISCONNECT=17;
	public static int CMD_RE_ENTER_ROOM=18;
	
	public static int USER_CHOICE_BTN_PLAY=0;
	public static int USER_CHOICE_BTN_TIPS=1;
	public static int USER_CHOICE_BTN_PASS=2;
	public static int USER_CHOICE_BTN_READY=3;
	public static int USER_CHOICE_BTN_ALERT=4;
	
	public static int BUTTON_PRESSED_OFFSET=3;
	
	
	static public boolean isInArea(MotionEvent event, ImagePosData data)
	{
		if(event.getX()>data.posInScreen.x&&event.getX()<(data.posInScreen.x+data.widthInScreen))
		{
			if(event.getY()>data.posInScreen.y&&event.getY()<(data.posInScreen.y+data.heightInScreen))
				return true;
		}
		return false;
	}
	static public boolean isInRect(MotionEvent event, Rect rect)
	{
		if(event.getX()>rect.left&&event.getX()<rect.right)
		{
			if(event.getY()>rect.top&&event.getY()<rect.bottom)
				return true;
		}
		return false;
	}
	static public Rect getRect(ImagePosData data) {
		return new Rect(data.posInScreen.x,data.posInScreen.y,data.posInScreen.x+data.widthInScreen,data.posInScreen.y+data.heightInScreen);
		
	}
	static public Rect getRectOffsetY(ImagePosData data,int offset) {
		return new Rect(data.posInScreen.x,data.posInScreen.y+offset,data.posInScreen.x+data.widthInScreen,data.posInScreen.y+offset+data.heightInScreen);
		
	}
	static public Rect getRectOffsetYInBmp(ImagePosData data,int offset) {
		return new Rect(data.posInBmp.x,data.posInBmp.y+offset,data.posInBmp.x+data.widthInBmp,data.posInBmp.y+offset+data.heightInBmp);
		
	}
}
