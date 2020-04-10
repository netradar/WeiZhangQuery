package carweibo.netradar.lichao;

public class AudioLukuangInfo {
	public enum MSG_STATUS {
		SENDING,ERROR,OK

	};
	
	boolean isUnRead;
	boolean isLocal;
	boolean isReSend=false;
	boolean isAnoymous;
	boolean isPlaying;
	boolean isFinishDown=false;
	boolean isDownloading=false;
	boolean isSended=false;
	
	long msg_tag;
	String sender_nickname;
	String lukuang_length;
	
	long send_time;
	String send_time_s;
	int period;
	
	String file_name;
	
	MSG_STATUS msg_status;
	
	
	
	
}
