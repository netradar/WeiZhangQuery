package carweibo.netradar.lichao;

import java.io.Serializable;

public class VenderInfo implements Serializable
{
	
	private static final long serialVersionUID = 1L;
	long id;
	String nickname;
	long user_id;
	String name;
	String addr;
	String comment;
	String pic_list;
	String time;
	String timeD;
	int bad_comment_num;
	int good_comment_num;
	
}
