package heart4.netradar.lichao;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import android.graphics.Bitmap;
import android.util.Log;

import carweibo.netradar.lichao.AsyncImageLoader2;
import carweibo.netradar.lichao.AsyncImageLoader2.ImageCallback2;
import carweibo.netradar.lichao.AsyncImageLoader2.TextCallback2;
import carweibo.netradar.lichao.DBUility;

public class PlayerProcess {
	
	int currenPendingPlayer=-2;
	int hostSeatIndex;
	int hostPos;
	int bottomSeatIndex=-1;
	PlayerInfo player_bottom;
	PlayerInfo player_top;
	PlayerInfo player_left;
	PlayerInfo player_right;
	
	AsyncImageLoader2 asyncImageLoader;
	String server_url;
	
	public PlayerProcess(Heart4MainActivity main) 
	{
		player_bottom=new PlayerInfo();
		player_top=new PlayerInfo();
		player_left=new PlayerInfo();
		player_right=new PlayerInfo();
		
		asyncImageLoader=new AsyncImageLoader2();
		server_url=((DBUility)main.getApplicationContext()).webapps+"/pic";
	}
	public void addPlayer(int seatIndex,JSONObject player,ImageCallback2 imageCallBack,TextCallback2 textCallBack)
	{
		PlayerInfo player_tmp;
		int posIndex=getPos(seatIndex);
		
		switch(posIndex)
		{
		case 1:
			player_tmp=player_left;
			break;
		case 2:
			player_tmp=player_top;
			break;
		case 3:
			player_tmp=player_right;
			break;
		default:
			return;
		}
		
		try 
		{
			if(player.getBoolean("isSeated"))
			{
				player_tmp.isAnonymous=player.getBoolean("isAnonymous");
				player_tmp.isDisconnect=player.getBoolean("isDisconnect");
				player_tmp.isStart=player.getBoolean("isReady");
				
				player_tmp.nickname=URLDecoder.decode(player.getString("nickname"),"UTF-8");
				
				player_tmp.touxiang_url=player.getString("touxiang");
				
				Bitmap bmp=asyncImageLoader.loadDrawable(server_url+"/touxiang/"+player_tmp.touxiang_url, String.valueOf(player_tmp.index),imageCallBack,textCallBack);
				player_tmp.touxiang=bmp;
				player_tmp.index=seatIndex;
			}
		} catch (UnsupportedEncodingException e) {
			
		} catch (JSONException e) {
			
		}
	
	}
	public void updatePlayer(int seat, JSONObject player) {
		
		PlayerInfo player_tmp;
		int posIndex=getPos(seat);
		
		switch(posIndex)
		{
		case 1:
			player_tmp=player_left;
			break;
		case 2:
			player_tmp=player_top;
			break;
		case 3:
			player_tmp=player_right;
			break;
		default:
			return;
		}
		
		try 
		{
			if(!player.getBoolean("isSeated"))
				player_tmp.reset();
			else
			{
				player_tmp.isAnonymous=player.getBoolean("isAnonymous");
				player_tmp.isDisconnect=player.getBoolean("isDisconnect");
				player_tmp.isStart=player.getBoolean("isReady");
			}

		} catch (JSONException e) {
			
		}
	}
	public void deletePlayer(int seatIndex)
	{
		int posIndex=getPos(seatIndex);
		switch(posIndex)
		{
		case 1:
			player_left.reset();
			break;
		case 2:
			player_top.reset();
			break;
		case 3:
			player_right.reset();
		}
	}
	public void playerDisconnect(int seatIndex,boolean isDisconnected)
	{
		int posIndex=getPos(seatIndex);
		switch(posIndex)
		{
		case 1:
			player_left.isDisconnect=isDisconnected;
			break;
		case 2:
			player_top.isDisconnect=isDisconnected;
			break;
		case 3:
			player_right.isDisconnect=isDisconnected;
		}
	}
	public void playerSetHost(int seatIndex)
	{
		int posIndex=seatIndex;
	//	hostSeatIndex=posIndex;
		switch(posIndex)
		{
		case 0:
			player_bottom.isHost=true;
			break;
		case 1:
			player_left.isHost=true;
			break;
		case 2:
			player_top.isHost=true;
			break;
		case 3:
			player_right.isHost=true;
		}
	}
	public void playerTimeLeft(int seatIndex,int time)
	{
		int posIndex=getPos(seatIndex);
		switch(posIndex)
		{
		case 0:
			player_bottom.time_left=time;
			break;
		case 1:
			player_left.time_left=time;
			break;
		case 2:
			player_top.time_left=time;
			break;
		case 3:
			player_right.time_left=time;
		}
	}
	public void playerSetReady(int seatIndex)
	{
		int posIndex=getPos(seatIndex);
		switch(posIndex)
		{
		case 0:
			player_bottom.isStart=true;
			break;
		case 1:
			player_left.isStart=true;
			break;
		case 2:
			player_top.isStart=true;
			break;
		case 3:
			player_right.isStart=true;
		}
	}
	public void playerIsNocard(int seatIndex)
	{
		int posIndex=getPos(seatIndex);
		switch(posIndex)
		{
		case 0:
			player_bottom.isNocard=true;
			break;
		case 1:
			player_left.isNocard=true;
			break;
		case 2:
			player_top.isNocard=true;
			break;
		case 3:
			player_right.isNocard=true;
		}
	}
	public void playerCardLeft(int seatIndex,int card_left)
	{
		int posIndex=getPos(seatIndex);
		switch(posIndex)
		{
		case 1:
			player_left.card_left=card_left;
			break;
		case 2:
			player_top.card_left=card_left;
			break;
		case 3:
			player_right.card_left=card_left;
		}
	}

	private int getPos(int seatIndex) {
		
		int offset=seatIndex-bottomSeatIndex;
		if(offset<0) offset=offset+4;
		
		return offset;
		
	}
	public void setTouxiang(int seatIndex, Bitmap imageDrawable) {
		
		int posIndex=getPos(seatIndex);
		switch(posIndex)
		{
		case 1:
			player_left.touxiang=imageDrawable;
			break;
		case 2:
			player_top.touxiang=imageDrawable;
			break;
		case 3:
			player_right.touxiang=imageDrawable;
		}
	}
	public void reset() {
		
		currenPendingPlayer=-1;
		
		bottomSeatIndex=-1;
		player_bottom.reset();
		player_left.reset();
		player_top.reset();
		player_right.reset();
	}
	public void updateNetworkStatus(JSONArray jsonArray) {
		
		for(int i=0;i<jsonArray.length();i++)
		{
			try {
				JSONObject js=jsonArray.getJSONObject(i);
				int seat=js.getInt("seatIndex");
				boolean isSeated=js.getBoolean("isSeated");
				if(seat!=bottomSeatIndex)
				{
					if(!isSeated)
					{
						deletePlayer(seat);
					}
					else
						playerDisconnect(i,js.getBoolean("isDisconnected"));
				}
			} catch (JSONException e) {
				
			}
			
		}
		
	}
	public int getPlayerNum() {
		
		int ret=1;
		if(player_left.index!=-1) ret++;
		if(player_top.index!=-1) ret++;
		if(player_right.index!=-1) ret++;
		
		return ret;
	}
	public int getReadyNum() {
		
		int ret=0;
		if(player_bottom.isStart) ret++;
		if(player_left.isStart) ret++;
		if(player_top.isStart) ret++;
		if(player_right.isStart) ret++;
		
		return ret;
	}
	
	public int getUnReadyPlayer()
	{
		
		if(player_bottom.isStart) return player_bottom.index;
		if(player_left.isStart) return player_left.index;
		if(player_top.isStart) return player_top.index;
		if(player_right.isStart) return player_right.index;
		
		return -1;
		
	}
	public void initCards(JSONArray jsonArray) {
	
		Gson gs=new Gson();
		for(int i=0;i<4;i++)
		{
			try {
				JSONObject js=jsonArray.getJSONObject(i);
				PlayerCards playCards;
				
				playCards=gs.fromJson(js.toString(),PlayerCards.class);
				if(playCards.seatIndex==bottomSeatIndex)
				{
					for(int j=0;j<13;j++)
					player_bottom.card[j]=playCards.cards[j];
				}
			} catch (JSONException e) {
				
			}
			
		}
		player_left.card_left=13;
		player_top.card_left=13;
		player_right.card_left=13;
		
	}
	public void setHostSeatIndex(int seatIndex) {
		
		hostPos=getPos(seatIndex);
		hostSeatIndex=seatIndex;
	//	Log.d("lichao","seatIndex is:"+seatIndex+"  hostSeatIndex is :"+hostSeatIndex);
	}
	public void clearReady() {
		player_bottom.isStart=false;
		player_left.isStart=false;
		player_top.isStart=false;
		player_right.isStart=false;
		
	}
	public void setTimeLeft(int seatIndex, int callFriend_time_left) {
		
		int posIndex=getPos(seatIndex);
		switch(posIndex)
		{
		case 1:
			player_left.time_left=callFriend_time_left;
			break;
		case 2:
			player_top.time_left=callFriend_time_left;
			break;
		case 3:
			player_right.time_left=callFriend_time_left;
		}
	}
	public void setPendingPlayer(int seatIndex) {
		
		currenPendingPlayer=seatIndex;
		
	}
	
}
