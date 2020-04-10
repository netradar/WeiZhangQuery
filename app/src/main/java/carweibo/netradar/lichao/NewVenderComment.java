package carweibo.netradar.lichao;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NewVenderComment extends Activity implements TextWatcher {

	EditText comment;
	
	VenderInfo vender;
	long vender_id;
	
	TextView words_count;
	
	boolean is_bad_comment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.newcomment_layout);
		TextView title=(TextView)findViewById(R.id.newcomment_title);
		comment=(EditText)findViewById(R.id.comment_content);
		words_count=(TextView)findViewById(R.id.newcomment_words_count);
		title.setText(this.getIntent().getStringExtra("title"));
		
		vender_id=this.getIntent().getLongExtra("vender_id", -1);
		is_bad_comment=this.getIntent().getBooleanExtra("is_bad_comment", false);
		
		if(vender_id==-1) this.finish();
		
		comment.addTextChangedListener(this);
		
		
	}
	
	public void onSendComment(View v)
	{
		if(comment.getText().toString().length()==0)
		{
			Toast.makeText(this, "您没有输入任何内容哦～", Toast.LENGTH_SHORT).show();
			return;
		}
		
		new SendWeibo().sendVenderComment(this, UserManager.getCurUser(this.getApplicationContext()), comment.getText().toString(), vender_id,is_bad_comment);
	
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
