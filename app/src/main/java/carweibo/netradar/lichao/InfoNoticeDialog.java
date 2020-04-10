package carweibo.netradar.lichao;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class InfoNoticeDialog extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.info_notice);
		TextView info=(TextView)findViewById(R.id.info_notice);
		TextView btn=(TextView)findViewById(R.id.info_notice_ok);
		
		info.setText(this.getIntent().getStringExtra("notice"));
		btn.setText(this.getIntent().getStringExtra("btn"));
		
	}
	
	public void onOkInfo(View v)
	{
		this.setResult(0);
		this.finish();
	}

	@Override
	public void onBackPressed() {
		
		onOkInfo(null);
	}
	
}
