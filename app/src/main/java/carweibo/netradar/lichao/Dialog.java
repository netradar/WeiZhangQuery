package carweibo.netradar.lichao;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Dialog extends Activity {

	String car_num;

	String text;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog);
		RelativeLayout layout=(RelativeLayout)findViewById(R.id.confirm_dialog);
		layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		Intent i=this.getIntent();
		
		TextView tv=(TextView)findViewById(R.id.hint_textview);
		TextView ok=(TextView)findViewById(R.id.dialog_ok);
		TextView cancel=(TextView)findViewById(R.id.dialog_cancel);
		
		if(this.getIntent().getBooleanExtra("isLeft", false))
		{
			tv.setGravity(Gravity.LEFT);
		}
		else
			tv.setGravity(Gravity.CENTER_HORIZONTAL);
		tv.setText(i.getStringExtra("text"));
		ok.setText(i.getStringExtra("ok"));
		cancel.setText(i.getStringExtra("cancel"));
	}
	

	public void onOkDelete(View v){

    	this.setResult(0);
    	this.finish();
    	
	}
	public void onCancelDelete(View v)
	{
		this.setResult(1);
		this.finish();
	}
	@Override
	public boolean onTouchEvent(MotionEvent event){
		this.setResult(1);
		finish();
		return true;
	}


	@Override
	public void onBackPressed() {
		this.setResult(1);
		finish();
		super.onBackPressed();
	}
	
}
