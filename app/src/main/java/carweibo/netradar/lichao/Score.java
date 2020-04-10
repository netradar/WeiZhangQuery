package carweibo.netradar.lichao;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Score extends Activity {

	String score="1.车友圈支持用户发表特殊的帖子类型（高亮显示，置顶显示），当用户的积分达到一定的分数后（高亮帖大于50，置顶帖大于100）时就可以操作。\n\n"+
			"2.每发表一个新帖，积分增加1分。\n\n" +
			"3.在菜单“更多”->“邀请好友加入车友圈”中邀请好友，每次积分增加3分。\n\n" +
			"4.每成功发表一个高亮帖，需消耗50积分。\n\n" +
			"5.每成功发表一个置顶帖，需消耗100积分。\n\n" +
			"6.置顶帖的置顶有效时间为一天，第二天会取消置顶。" 
			
			;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.score_layout);
		TextView tv=(TextView)findViewById(R.id.score_text);
		TextView title=(TextView)findViewById(R.id.score_title);
		
		if(this.getIntent().getBooleanExtra("isService", false))
		{
			tv.setText(this.getIntent().getStringExtra("notice"));
			title.setText("通知");
		}
		else
		{
			tv.setText(score);
			title.setText("积分说明");
			
		}
		
	}
	@Override
	public void onBackPressed() {
		
		super.onBackPressed();
		this.finish();
		this.overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
		
	}

	public void onCancelScore(View v)
	{
		onBackPressed();
	}
	
}
