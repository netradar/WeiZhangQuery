package heart4.netradar.lichao;

import android.graphics.Bitmap;

public class PlayerInfo {
	
	int index=-1;
	
	Bitmap touxiang=null;
	String nickname;
	int score;
	String touxiang_url;
	int[] card=new int[13]; 
	
	boolean isStart=false;
	boolean isTurn=false;
	boolean isHost=false;
	boolean isDisconnect=false;
	boolean isNocard=false;
	boolean isAnonymous=false;
	int card_left=-1;
	int time_left=-1;
	
	
	public PlayerInfo()
	{
		
	}


	public void reset() {
		
		index=-1;
		touxiang=null;
		nickname="";
		score=-1;
		touxiang_url=null;
		isStart=false;
		isTurn=false;
		isHost=false;
		isDisconnect=false;
		isNocard=false;
		isAnonymous=false;
		card_left=-1;
		time_left=-1;
		
	}
	

}
