package carweibo.netradar.lichao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ChangeNickname extends Activity {

	EditText nickname;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.change_nickname_layout);
		nickname=(EditText)findViewById(R.id.change_nickname_nickname);
		
		nickname.setText(this.getIntent().getStringExtra("nickname"));
	}

	public void onChangeNickname(View v)
	{
		if(nickname.getText().toString().length()==0)
		{

			Toast.makeText(this, "êÇ³Æ²»ÄÜÎª¿Õ", Toast.LENGTH_SHORT).show();
			return;
		}
		
		Intent i=new Intent();
		i.putExtra("nickname", nickname.getText().toString());
		this.setResult(1,i);
		this.finish();
		this.overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
	}

	@Override
	public void onBackPressed() {
		
		
		this.setResult(0);
		this.finish();
		this.overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
		super.onBackPressed();
	}
	public void onCancelChangeNickname(View v)
	{
		onBackPressed();
	}
	
}
