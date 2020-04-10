package carweibo.netradar.lichao;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Notice extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.notice_dialog);
		TextView tv=(TextView)findViewById(R.id.notice);
		
		tv.setText(this.getIntent().getStringExtra("notice"));
	}
	
	public void onCancelNotice(View v)
	{
		this.finish();
	}
}
