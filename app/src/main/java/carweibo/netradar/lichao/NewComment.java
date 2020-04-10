package carweibo.netradar.lichao;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NewComment extends Activity implements TextWatcher {

	EditText comment;
	SendWeibo sendweibo;
	ImageAndText imt;
	long weibo_id;
	int to_user_id;
	int grade;
	TextView words_count;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.newcomment_layout);
		TextView title=(TextView)findViewById(R.id.newcomment_title);
		comment=(EditText)findViewById(R.id.comment_content);
		words_count=(TextView)findViewById(R.id.newcomment_words_count);
		title.setText(this.getIntent().getStringExtra("title"));
		
		weibo_id=this.getIntent().getLongExtra("weibo_id", -1);
		grade=this.getIntent().getIntExtra("grade", 0);
		to_user_id=this.getIntent().getIntExtra("to_user_id", -1);
		if(weibo_id==-1) this.finish();
		
		comment.addTextChangedListener(this);
		
		
		sendweibo=new SendWeibo();
		
	}
	
	public void onSendComment(View v)
	{
		if(comment.getText().toString().length()==0)
		{
			Toast.makeText(this, "您没有输入任何内容哦～", Toast.LENGTH_SHORT).show();
			return;
		}
		if(grade==2)
			sendweibo.sendComment(this, UserManager.getCurUser(this.getApplicationContext()), to_user_id, comment.getText().toString(), weibo_id, true);
		else
			sendweibo.sendComment(this, UserManager.getCurUser(this.getApplicationContext()), to_user_id, comment.getText().toString(), weibo_id, false);
	
		onBackPressed();
	}

	public void onCancelNewComment(View v)
	{
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
		boolean isOpen=imm.isActive(); 
		if(isOpen==true)
		{
			imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
		onBackPressed();
		
	}


	@Override
	public void onBackPressed() {
		this.setResult(1);
		super.onBackPressed();
		this.finish();
		this.overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
		
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		words_count.setText(comment.getText().toString().length()+"/200");
		
	}
	
}
