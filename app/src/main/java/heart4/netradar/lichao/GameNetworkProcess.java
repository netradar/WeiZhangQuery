package heart4.netradar.lichao;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class GameNetworkProcess {
	
	boolean isConnected=false;
	boolean threadFlag;
	boolean exitGame=false;
	boolean sendFlag;
	
	String	IP_addr="192.168.1.104";
//	String	IP_addr="192.168.43.147";
	int 	port=999;
	SocketChannel client;
	
	InputStream is;
	ObjectOutputStream oos;

	Selector selector;
	
	Thread sendThread;
	Thread recvThread;
	Thread listenThread;
	
	List<String> sendBuffer;
	ByteBuffer recvBuffer;
	SocketChannel myChannel;
	
	Handler main_handler;
	String nickname;
	boolean isAnonymous;
	
	int reConnectTimes=0;
	
	Timer timer_connect_timeout;
	Timer timer_ping;
	Timer timer_ping_timeout;
	
	 SocketChannel channel;
	 
	 List<String> sendList=new ArrayList<String>();
	 
	 private class PingTask extends TimerTask
		{

			@Override
			public void run() {
				
			//	Log.d("lichao","send ping req");
			
					startPingTimeoutTimer();
					try {
						sendBuffer("ping-req");
					} catch (ClosedChannelException e) {
						
						e.printStackTrace();
					}
					
				
	        	}
		}
	 private class PingTimeoutTask extends TimerTask
		{

			@Override
			public void run() {
				Log.d("lichao","ping timeout");
				stopPingTimer();
				close();
				sendMsg(Tools.CMD_DISCONNECTED,"");
				
	        	}
		}
	private class ConnectTimeoutTask extends TimerTask
	{

		@Override
		public void run() {
			
			Log.d("lichao","connect timeout");
				close();
				sendMsg(Tools.CMD_CONNECT_FAIL,"");
				
			
        	}
	}
	public class SendRunnable implements Runnable {

		@Override
		public void run() {
		
			while(sendFlag)
			{
				
				synchronized(sendList)
				{
					if(sendList.isEmpty())
					{
						try {
							sendList.wait();
						} catch (InterruptedException e) {
							
						}
						continue;
					}
					send();
					sendList.notify();
				}
				
			}
				
			
		}
		
	}
	public class ListenRunnable implements Runnable {

		boolean isDisconnected=false;
		@Override
		public void run() 
		{
		//	Log.d("lichao","thread start");
			
			try {
				sendMsg(Tools.CMD_CONNECT_STATUS,"正在连接服务器（第"+reConnectTimes+"次）...");
				isConnected=false;
				channel = SocketChannel.open();
			
		        channel.configureBlocking(false);   
		       
		        selector = Selector.open();   
		    
		        channel.connect(new InetSocketAddress(IP_addr,port));   
		  
		        channel.register(selector, SelectionKey.OP_CONNECT);   

				while(threadFlag&!exitGame)
				{
			//		Log.d("lichao","select start   "); 
					
					selector.select();   
			//		Log.d("lichao","select end   "); 
		            // 获得selector中选中的项的迭代器   
		            Iterator<SelectionKey> ite = selector.selectedKeys().iterator();   
		            while (ite.hasNext()) {   
		            	
		                SelectionKey key = (SelectionKey) ite.next();   
		                // 删除已选的key,以防重复处理   
		                ite.remove();   
		                // 连接事件发生   
		                if (key.isConnectable()) {   
		                	
		                	
		                	timer_connect_timeout.cancel();
		                	myChannel = (SocketChannel) key   
		                            .channel();   
		                    // 如果正在连接，则完成连接   
		                    if(myChannel.isConnectionPending()){   
		                    	myChannel.finishConnect();   
		                           
		                    }   
		                    isConnected=true;
		                    // 设置成非阻塞   
		                    myChannel.configureBlocking(false);   
		  
		                    //在这里可以给服务端发送信息哦   
		                     //在和服务端连接成功之后，为了可以接收到服务端的信息，需要给通道设置读的权限。   
		                    myChannel.register(selector, SelectionKey.OP_READ);   
		                    reConnectTimes=0;
		                    sendMsg(Tools.CMD_CONNECT_STATUS,"连接成功，获取用户信息...");
		                //    startPingTimer();
		                    sendMsg(Tools.CMD_CONNECT_SUCCESS,"");
		                //    login();
		                       
		                    // 获得了可读的事件   
		                } 
		                else if (key.isReadable()) {   
		                //	Log.d("lichao","isReadable   "); 
		                	 SocketChannel channel2 = (SocketChannel) key.channel(); 

						      
						       ByteBuffer headBuffer = ByteBuffer.allocate(2);      
						       int byteRead = channel2.read(headBuffer);
						       int msgLength=byteArrayToInt(headBuffer.array());
						       if(byteRead>0)
						       {
						    	   ByteBuffer buf =ByteBuffer.allocate(msgLength);// (ByteBuffer) key.attachment(); 
							       buf.clear();
						    	   int bytesRead = channel2.read(buf); 
							       byte[] bytes=buf.array();
							       if (bytesRead<0) { 

							    	   close();
							    	
							    	   isDisconnected=true;
							    	   break;
							           

							       } else if (bytesRead > 0) { 
							    	   processRecv(new String(bytes),bytesRead); 
							    	   
							       } 
						       }
						       else
						       {
						    	   close();
							       isDisconnected=true;
						    	   break;
						       }
						       
						       
						       
						      
		                }  
		              /*  else if(key.isWritable())
		                {
		                	Log.d("lichao","writeable!"); 
		                	send();
		                	myChannel.register(selector, SelectionKey.OP_READ);
		                }*/
		            }
					
				}
			} catch (IOException e1) {
				Log.d("lichao","ListenRunnable  4 "+e1.toString()); 
				//sendMsg(Tools.CMD_CONNECT_FAIL,"");
				 isDisconnected=false;
				
			} 
			
			Log.d("lichao","network thread exit");
			
			if(!exitGame)
			{
				if(!isConnected)
				{
					Log.d("lichao","send fail");
					sendMsg(Tools.CMD_CONNECT_FAIL,"");
				}
				else
				{
					stopPingTimer();
					stopPingTimeoutTimer();
					sendMsg(Tools.CMD_DISCONNECTED,"");
				}
			}
		}
	}
	
	/*public class RecvRunnable implements Runnable {

		@Override
		public void run() 
		{
			while(true)
			{
				try {
					Log.d("lichao","reading......");
					byte[] bytes=new byte[100];
					int length=is.read(bytes);

					processRecv(bytes,length);
				} catch (OptionalDataException e) {
					Log.d("lichao","RecvRunnable  error 1 "+e.toString()); 
					e.printStackTrace();
				} catch (IOException e) {
					Log.d("lichao","RecvRunnable  error 3 "+e.toString()); 
					e.printStackTrace();
				}
				
			}
		}
	}*/
/*	public class SendRunnable implements Runnable {

		@Override
		public void run() 
		{
			while(true)
			{
				synchronized (sendBuffer)
				{
					String buffer;
					
					if(sendBuffer.size()==0||sendBuffer==null)
					{
						try {
							Log.d("lichao","sendbuffer waiting");
							sendBuffer.wait();
							Log.d("lichao","sendbuffer waiting over");
						} catch (InterruptedException e) {
							
						}
						continue;
					}
					for(int i=0;i<sendBuffer.size();i++)
					{	buffer=sendBuffer.get(i);
						try {
							oos.writeObject(buffer);
						} catch (IOException e) {
							
							Log.d("lichao","sendRunnable  error :"+e.toString()); 
						}
					}
					sendBuffer.notify();
				}

			}
		}
	}*/

	public GameNetworkProcess(Handler handler_network, String nickname) {
		
		threadFlag=true;
		sendFlag=true;
		main_handler=handler_network;
		if(nickname.equals("NOUSER")||nickname.equals("NOLOGIN"))
			isAnonymous=true;
		else
			isAnonymous=false;
		try {
			this.nickname=URLEncoder.encode(nickname, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			
		}
		initSocket();
			
	}
	
	private void startPingTimeoutTimer()
	{
	//	Log.d("lichao","startPingTimeoutTimer");
	
		/*timer_ping_timeout=new Timer();
		timer_ping_timeout.schedule(new PingTimeoutTask(),3*1000);*/
	}
	private void stopPingTimeoutTimer()
	{
	//	Log.d("lichao","stopPingTimeoutTimer");
		if(timer_ping_timeout!=null)
		{
			
			timer_ping_timeout.cancel();
			timer_ping_timeout=null;
		}
		else
			Log.d("lichao","stopPingTimeoutTimer failed!!!!!!!!!!!!1");
	}

	public void startPingTimer() {

	//	Log.d("lichao","startPingTimer");
		if(timer_ping!=null) timer_ping.cancel();
		timer_ping=new Timer();
		timer_ping.schedule(new PingTask(), 0,1*1000);
		
	}
	private void stopPingTimer()
	{
	//	Log.d("lichao","stopPingTimer");
		if(timer_ping!=null)
		{
			timer_ping.cancel();
			timer_ping=null;
		}
	}

	public void processRecv(String string,int length) {
		
		int cmd;
		String recv_str=string.substring(0,length);
		
		try {
			
			if(recv_str.equals("ping-ack"))
			{
			//	Log.d("lichao","recv ping ack");
				stopPingTimeoutTimer();
				return;
			}
			Log.d("lichao","recv str is :"+recv_str);
			JSONObject js=new JSONObject(recv_str);
			cmd=js.getInt("cmd");
		
					
		} catch (JSONException e) {
			return;
		}
		
		sendMsg(cmd,recv_str);
		
	}
	
	private void sendMsg(int cmd,String str)
	{
		Message message = new Message();      
        message.what = cmd;  
        message.obj=str;
        main_handler.sendMessage(message); 
	}

	public void processDisconnect(boolean reset) {
		
		threadFlag=true;
		
		initSocket();
	}

	
	Socket socket;
	private boolean initSocket() {
		
		 
		reConnectTimes++;
		if(reConnectTimes>2)
		{
			close();
			sendMsg(Tools.CMD_CONNECT_FAILED,"");
			return false;
			
		}
		
		startConnectTimeoutTimer();
		
		listenThread=new Thread(new ListenRunnable());
		sendThread=new Thread(new SendRunnable());
		
		listenThread.start();
		sendThread.start();
		return false;
	    
		
		
		
	}
	
	private void sendBuffer1(String buffer)
	{
		int length=buffer.length();
	//	Log.d("lichao","length is "+length);
		if(myChannel==null||!myChannel.isConnected())
		{
			if(myChannel!=null)
				try {
					myChannel.close();
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			close();
			sendMsg(Tools.CMD_DISCONNECTED,"");
			return;
		}
		
		
		 try {
			 	ByteBuffer buf_length= ByteBuffer.wrap(intToBytes(length));
			 	ByteBuffer buf_body= ByteBuffer.wrap(buffer.getBytes());
			 	
			 	ByteBuffer buf=ByteBuffer.allocate(length+2);
			 	buf.clear();
			 //	buf.put(intToBytes(length));
			 	for(int i=0;i<2;i++)
			 	{
			 		buf.put(buf_length.get(i));
			 	}
			 	for(int j=0;j<length;j++)
			 	{
			 		buf.put(buf_body.get(j));
			 	}
			 	
			 	
			// 	Log.d("lichao","buf is "+new String(buf.array()));
			 	buf.flip();
			 	int remain_length=buf.remaining();
				int writed_length=0;
				int reSendTimes=0;
				
				while(remain_length!=writed_length&&reSendTimes<10)
				{
					writed_length=writed_length+myChannel.write(buf);
					reSendTimes++;
				}
				if(reSendTimes==10)
				{
					close();
					sendMsg(Tools.CMD_DISCONNECTED,"");
					return;
					
				}
			/*	remain_length=buf_body.remaining();
				writed_length=0;
				reSendTimes=0;
				
				while(remain_length!=writed_length&&reSendTimes<10)
				{
					writed_length=writed_length+myChannel.write(buf_body);
					reSendTimes++;
				}
				if(reSendTimes==10)
				{
					close();
					sendMsg(Tools.CMD_DISCONNECTED,"");
					
				}*/
		
			
		} catch (IOException e) {
			Log.d("lichao","send buffer error :"+e.toString());
			close();
			sendMsg(Tools.CMD_DISCONNECTED,"");
		}   
          if(length!=8)
		 Log.d("lichao","end sendbuffer");
	}
	public void sendBuffer(String buffer) throws ClosedChannelException
	{
	//	Log.d("lichao"," sendbuffer add");
		synchronized(sendList)
		{
			sendList.add(buffer);
			sendList.notify();
		}
	//	 myChannel.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE);
		
	/*	
		int length=buffer.length();
		Log.d("lichao","length is "+length);
		if(myChannel==null||!myChannel.isConnected())
		{
			if(myChannel!=null)
				try {
					myChannel.close();
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			close();
			sendMsg(Tools.CMD_DISCONNECTED,"");
			return;
		}
		
		
		 try {
			 	ByteBuffer buf_length= ByteBuffer.wrap(intToBytes(length));
			 	ByteBuffer buf_body= ByteBuffer.wrap(buffer.getBytes());
			 	
			 	
			 	int remain_length=buf_length.remaining();
				int writed_length=0;
				int reSendTimes=0;
				
				while(remain_length!=writed_length&&reSendTimes<10)
				{
					writed_length=writed_length+myChannel.write(buf_length);
					reSendTimes++;
				}
				if(reSendTimes==10)
				{
					close();
					sendMsg(Tools.CMD_DISCONNECTED,"");
					return;
					
				}
				remain_length=buf_body.remaining();
				writed_length=0;
				reSendTimes=0;
				
				while(remain_length!=writed_length&&reSendTimes<10)
				{
					writed_length=writed_length+myChannel.write(buf_body);
					reSendTimes++;
				}
				if(reSendTimes==10)
				{
					close();
					sendMsg(Tools.CMD_DISCONNECTED,"");
					
				}
		
			
		} catch (IOException e) {
			Log.d("lichao","send buffer error :"+e.toString());
			close();
			sendMsg(Tools.CMD_DISCONNECTED,"");
		}   
          
		 Log.d("lichao","end sendbuffer");*/
	}

	private void send()
	{
		Iterator<String> stringIter = sendList.iterator();
	//	Log.d("lichao","send ");
		while(stringIter.hasNext())
		{
			String sendStr=stringIter.next();
	//		Log.d("lichao","sendStr is :"+sendStr);
			sendBuffer1(sendStr);
			stringIter.remove();
			
		}
		
	
		
	}
	public void close() {
		Log.d("lichao","channel closed");
		if(timer_ping!=null)
			timer_ping.cancel();
		
		try {
			if(myChannel!=null)
				myChannel.close();
			if(channel!=null)
				channel.close();
		} catch (IOException e) {
			
			Log.d("lichao","close Socket error "+e.toString()); 
		}
		threadFlag=false;
		sendFlag=false;
		synchronized(sendList)
		{
			sendList.notify();
		}
		selector.wakeup();
	}
	public void exit()
	{
		exitGame=true;
		close();
	}
	public void login()
	{
	//	Log.d("lichao","login"); 
		try 
		{
			JSONObject js=new JSONObject();
			js.put("cmd", Tools.CMD_LOGIN);
			js.put("isAnonymous", isAnonymous);
			
			js.put("nickname", nickname);
			sendBuffer(js.toString());
		} catch (JSONException e) {
			
		} catch (ClosedChannelException e) {
			
			Log.d("lichao","login channel error:"+e.toString());
		}
		
	}
	public void enterRoom(int roomIndex)
	{
		
		try 
		{
			JSONObject js=new JSONObject();
			js.put("cmd", Tools.CMD_ENTER_ROOM);
			js.put("roomIndex", roomIndex);
			js.put("isAnonymous", isAnonymous);
			js.put("nickname", nickname);
			sendBuffer(js.toString());
		} catch (JSONException e) {
			
		} catch (ClosedChannelException e) {
		
			e.printStackTrace();
		}
		
	}
	public void playerExitGame(boolean isForceExit,int roomIndex,int deskIndex,int seatIndex) 
	{

		try 
		{
			JSONObject js=new JSONObject();
			js.put("cmd", Tools.CMD_PLAYER_EXIT);
			js.put("isForce", isForceExit);
			js.put("roomIndex", roomIndex);
			js.put("deskIndex", deskIndex);
			js.put("seatIndex", seatIndex);
			sendBuffer(js.toString());
		} catch (JSONException e) {
			
		} catch (ClosedChannelException e) {
		
			
		}
		
	}
	public void changeDesk(int roomIndex, int deskIndex, int seatIndex) {
		
		try 
		{
			JSONObject js=new JSONObject();
			js.put("cmd", Tools.CMD_ALERT);
			js.put("roomIndex", roomIndex);
			js.put("deskIndex", deskIndex);
			js.put("seatIndex", seatIndex);
			sendBuffer(js.toString());
			
		} catch (JSONException e) {
			
		} catch (ClosedChannelException e) {
		
			
		}
	}
	public void ready(int roomIndex, int deskIndex, int seatIndex) {
		try 
		{
			JSONObject js=new JSONObject();
			js.put("cmd", Tools.CMD_READY);
			js.put("roomIndex", roomIndex);
			js.put("deskIndex", deskIndex);
			js.put("seatIndex", seatIndex);
			sendBuffer(js.toString());
		} catch (JSONException e) {
			
		} catch (ClosedChannelException e) {
		
			
		}
		
	}
	private void startConnectTimeoutTimer()
	{
		
		//if(timer_connect_timeout==null)
		{
			timer_connect_timeout=new Timer();
			timer_connect_timeout.schedule(new ConnectTimeoutTask(),5000);

		}
		
		
	}
	
	public static byte[] intToBytes(int n){  
	    byte[] b = new byte[2];  
	    for(int i = 0;i < 2;i++){  
	        b[i] = (byte)(n >> (8 - i * 8));   
	    }  
	    return b;  
	} 
	public static int byteArrayToInt(byte[] b) {
        int mask = 0xff;
        int temp = 0;
        int n = 0;
        for(int i=0; i<b.length; i++){
        n <<= 8;
            temp = b[i] & mask;
            n |= temp;
        }
        return n;
}

	public void reEnterRoom(int roomIndex, int deskIndex, int seatIndex) {
		
		try 
		{
			JSONObject js=new JSONObject();
			js.put("cmd", Tools.CMD_RE_ENTER_ROOM);
			js.put("nickname", nickname);
			js.put("roomIndex", roomIndex);
			js.put("deskIndex", deskIndex);
			js.put("seatIndex", seatIndex);
			sendBuffer(js.toString());
		} catch (JSONException e) {
			
		} catch (ClosedChannelException e) {
		
		
		}
	}

	public void callFriend(int roomIndex, int deskIndex, int seatIndex,
			int selectedCard) {
		
		try 
		{
			JSONObject js=new JSONObject();
			js.put("cmd", Tools.CMD_CALL);
			
			js.put("roomIndex", roomIndex);
			js.put("deskIndex", deskIndex);
			js.put("seatIndex", seatIndex);
			js.put("selectedCard",selectedCard);
			sendBuffer(js.toString());
		} catch (JSONException e) {
			
		} catch (ClosedChannelException e) {
		
		
		}
	}

	public void dropCard(int roomIndex, int deskIndex, int seatIndex,
			PlayedCardInfo card) {
		
		Gson gs=new Gson();
		try 
		{
			JSONObject js=new JSONObject();
			js.put("cmd", Tools.CMD_PLAY);
			
			js.put("roomIndex", roomIndex);
			js.put("deskIndex", deskIndex);
			js.put("seatIndex", seatIndex);
			js.put("dropCard",gs.toJson(card));
			sendBuffer(js.toString());
		} catch (JSONException e) {
			
		} catch (ClosedChannelException e) {
		
		
		}
	}

	

	

	
}
