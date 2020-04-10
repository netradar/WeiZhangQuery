package carweibo.netradar.lichao;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

public class CommentInfo {
	
	int user_id;
	int to_id;
	int user_score;
	
	String nickname;
	String to_nickname;
	
	SpannableStringBuilder content;
	String time;
	String touxiang;
	
	boolean isVender=false;
	public int getUser_id() {
		return user_id;
	}
	public int getTo_id() {
		return to_id;
	}
	public int getUser_score() {
		return user_score;
	}
	public String getNickname() {
		return nickname;
	}
	public String getTo_nickname() {
		return to_nickname;
	}
	public SpannableStringBuilder getContent() {
		return content;
	}
	public String getTime() {
		return time;
	}
	public String getTouxiang() {
		return touxiang;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public void setTo_id(int to_id) {
		this.to_id = to_id;
	}
	public void setUser_score(int user_score) {
		this.user_score = user_score;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public void setTo_nickname(String to_nickname) {
		this.to_nickname = to_nickname;
	}
	public void setContent(String content_r) {
		if(to_id==-1)
			this.content = new SpannableStringBuilder(content_r);
		else
		{
			SpannableStringBuilder builder=new SpannableStringBuilder("»Ø¸´"+to_nickname+"£º"+content_r);
			ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.parseColor("#336699")); 
			builder.setSpan(redSpan, 2, 2+to_nickname.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);  
			this.content = builder;
			
		}
	}
	public void setTime(String time) {
		this.time = time;
	}
	public void setTouxiang(String touxiang) {
		this.touxiang = touxiang;
	}
	
	
}
