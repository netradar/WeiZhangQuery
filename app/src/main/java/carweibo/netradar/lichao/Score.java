package carweibo.netradar.lichao;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Score extends Activity {

	String score="1.����Ȧ֧���û�����������������ͣ�������ʾ���ö���ʾ�������û��Ļ��ִﵽһ���ķ����󣨸���������50���ö�������100��ʱ�Ϳ��Բ�����\n\n"+
			"2.ÿ����һ����������������1�֡�\n\n" +
			"3.�ڲ˵������ࡱ->��������Ѽ��복��Ȧ����������ѣ�ÿ�λ�������3�֡�\n\n" +
			"4.ÿ�ɹ�����һ����������������50���֡�\n\n" +
			"5.ÿ�ɹ�����һ���ö�����������100���֡�\n\n" +
			"6.�ö������ö���Чʱ��Ϊһ�죬�ڶ����ȡ���ö���" 
			
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
			title.setText("֪ͨ");
		}
		else
		{
			tv.setText(score);
			title.setText("����˵��");
			
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
