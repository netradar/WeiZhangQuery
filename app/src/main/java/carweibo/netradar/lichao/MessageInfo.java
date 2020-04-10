package carweibo.netradar.lichao;

public class MessageInfo {
	public enum MSG_STATUS {
		SENDING,ERROR,OK

	};
	int msg_type;
	boolean isSend;
	boolean isReSend=false;
	long msg_tag;
	int from_user_id;
	int from_user_score;
	
	String from_nickname;
	String from_touxiang;
	
	String time;
	String msg;
	
	int to_user_id;
	int to_user_score;
	
	String to_nickname;
	String to_touxiang;
	
	MSG_STATUS msg_status;
	
	String ref_weibo;
	long weibo_id;
	boolean isTop;
	
	
	public boolean isTop() {
		return isTop;
	}
	public void setTop(boolean isTop) {
		this.isTop = isTop;
	}
	public long getWeibo_id() {
		return weibo_id;
	}
	public void setWeibo_id(long weibo_id) {
		this.weibo_id = weibo_id;
	}
	public boolean isReSend() {
		return isReSend;
	}
	public void setReSend(boolean isReSend) {
		this.isReSend = isReSend;
	}
	public int getMsg_type() {
		return msg_type;
	}
	public String getRef_weibo() {
		return ref_weibo;
	}
	public void setMsg_type(int msg_type) {
		this.msg_type = msg_type;
	}
	public void setRef_weibo(String ref_weibo) {
		this.ref_weibo = ref_weibo;
	}
	public long getMsg_tag() {
		return msg_tag;
	}
	public void setMsg_tag(long msg_tag) {
		this.msg_tag = msg_tag;
	}
	public MSG_STATUS getMsg_status() {
		return msg_status;
	}
	public void setMsg_status(MSG_STATUS msg_status) {
		this.msg_status = msg_status;
	}
	public boolean isSend() {
		return isSend;
	}
	public int getFrom_user_id() {
		return from_user_id;
	}
	public int getFrom_user_score() {
		return from_user_score;
	}
	public String getFrom_nickname() {
		return from_nickname;
	}
	public String getFrom_touxiang() {
		return from_touxiang;
	}
	public String getTime() {
		return time;
	}
	public String getMsg() {
		return msg;
	}
	public int getTo_user_id() {
		return to_user_id;
	}
	public int getTo_user_score() {
		return to_user_score;
	}
	public String getTo_nickname() {
		return to_nickname;
	}
	public String getTo_touxiang() {
		return to_touxiang;
	}
	public void setSend(boolean isSend) {
		this.isSend = isSend;
	}
	public void setFrom_user_id(int from_user_id) {
		this.from_user_id = from_user_id;
	}
	public void setFrom_user_score(int from_user_score) {
		this.from_user_score = from_user_score;
	}
	public void setFrom_nickname(String from_nickname) {
		this.from_nickname = from_nickname;
	}
	public void setFrom_touxiang(String from_touxiang) {
		this.from_touxiang = from_touxiang;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public void setTo_user_id(int to_user_id) {
		this.to_user_id = to_user_id;
	}
	public void setTo_user_score(int to_user_score) {
		this.to_user_score = to_user_score;
	}
	public void setTo_nickname(String to_nickname) {
		this.to_nickname = to_nickname;
	}
	public void setTo_touxiang(String to_touxiang) {
		this.to_touxiang = to_touxiang;
	}
	
	
	
}
