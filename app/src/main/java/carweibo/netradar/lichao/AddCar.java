package carweibo.netradar.lichao;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class AddCar extends Activity implements OnCheckedChangeListener {

	RadioGroup gr;
	RadioButton rb0;
	RadioButton rb1;
	EditText carNum;
	EditText carOwner;
	TextView car_owner;
	TextView change;
	boolean isOwner=true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.new_car_layout);
		gr=(RadioGroup)findViewById(R.id.car_catagory);
		gr.setOnCheckedChangeListener(this);
		rb0=(RadioButton)findViewById(R.id.radio0);
		rb1=(RadioButton)findViewById(R.id.radio1);
		carNum=(EditText)findViewById(R.id.carnum_edit);
		carOwner=(EditText)findViewById(R.id.carOwner_edit);
		car_owner=(TextView)findViewById(R.id.carowner);
		change=(TextView)findViewById(R.id.change_query_method);
	
	}
	
	public void onAddQuery(View v)
	{
		String num=carNum.getText().toString();
		String owner=carOwner.getText().toString();
		
		if(num.length()==0)
		{
			Toast.makeText(this, "亲，你没输入车牌号哦～", Toast.LENGTH_LONG).show();
			return;
		}
		if(!num.startsWith("A")&&!num.startsWith("a")&&!num.startsWith("V")&&!num.startsWith("v"))
		{
			Toast.makeText(this, "亲，车牌号要以A(a)或V(v)开头哦～", Toast.LENGTH_LONG).show();
			return;
		}
		if(owner.length()==0)
		{
			if(isOwner)
				Toast.makeText(this, "亲，车主姓名没输入哦～", Toast.LENGTH_LONG).show();
			else
				Toast.makeText(this, "亲，发动机号码没输入哦～", Toast.LENGTH_LONG).show();
			return;
		}
		
		if(isExist("陕"+num.toUpperCase()))
		{
    		Toast.makeText(this, "同样的车牌已经存在了～", Toast.LENGTH_LONG).show();
    		return;
    	}
			
    		
		
		Intent i=new Intent();
		Bundle bd=new Bundle();
		
		bd.putString("num","陕"+num.toUpperCase());
		bd.putString("owner_fdjh", owner);
		if(gr.getCheckedRadioButtonId()==R.id.radio0)
		{
			bd.putString("type", "02");
		}
		else
			bd.putString("type","01");
		if(isOwner)
			bd.putString("querymethod", "owner");
		else
			bd.putString("querymethod", "fdjh");
		
		i.putExtra("data", bd);
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
		boolean isOpen=imm.isActive(); 
		if(isOpen==true)
		{
			imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
		this.setResult(0, i);
		this.finish();
	}

	public void onCancelAdd(View v)
	{
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
		boolean isOpen=imm.isActive(); 
		if(isOpen==true)
		{
			imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
		this.setResult(1);
		this.finish();
		
		
		this.overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
	
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		this.setResult(1);
		this.finish();
		this.overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
		super.onBackPressed();
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		// TODO Auto-generated method stub

		Drawable green= getResources().getDrawable(R.drawable.icon_green);
		Drawable grey= getResources().getDrawable(R.drawable.icon_grey);
		
		green.setBounds(0, 0, green.getMinimumWidth(), green.getMinimumHeight());

		grey.setBounds(0, 0, grey.getMinimumWidth(), grey.getMinimumHeight());
		

		if(arg1==R.id.radio0)
		{
			rb0.setCompoundDrawables(green, null, null, null);
			rb1.setCompoundDrawables(grey, null, null, null);
		}
		else
			{
			rb0.setCompoundDrawables(grey, null, null, null);
			rb1.setCompoundDrawables(green, null, null, null);
			}
	}
	
	public boolean isExist(String num){
		
		DBUility dbUility=(DBUility)getApplicationContext();
    	
    	DBhelper db=dbUility.getDB();
    	
    	Cursor c=db.queryItem(dbUility.CAR_TABLE, "num", num);
    	
    	if(c.moveToFirst())
    	{
    		return true;
		
    	}
    	
    	return false;
	}
	
	public void onChangeQueryMethod(View v)
	{
		if(isOwner)
		{
			car_owner.setText("发动机号：");
			carOwner.setHint("可在行驶证上查询");
			change.setText("发动机号记不住？\n点这里用车主姓名查询吧");
			isOwner=false;
		}
		else
		{
			car_owner.setText("所有人：");
			carOwner.setHint("车主或单位中文名称");
			change.setText("所有人记不住？\n点这里用发动机号来查询吧");
			isOwner=true;
		}
	}

}
