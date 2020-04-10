package carweibo.netradar.lichao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class CarInfo extends Activity implements OnClickListener {

	String[] items;
	TextView num;
	TextView owner;
	TextView color;
	TextView brand;
	TextView type;
	TextView fdjh;
	TextView status;
	TextView valid;
	TextView destory;
	Cursor c;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.carinfo_layout);
		
		num=(TextView)findViewById(R.id.c_numC);
		owner=(TextView)findViewById(R.id.c_ownerC);
		color=(TextView)findViewById(R.id.c_colorC);
		brand=(TextView)findViewById(R.id.c_brandC);
		type=(TextView)findViewById(R.id.c_typeC);
		valid=(TextView)findViewById(R.id.c_validC);
		destory=(TextView)findViewById(R.id.c_destoryC);
		fdjh=(TextView)findViewById(R.id.c_fdjhC);
		
		DBUility dbUility=(DBUility)getApplicationContext();
    	
    	DBhelper db=dbUility.getDB();
    	
    	c=db.query(dbUility.CAR_TABLE);//, "num", this.getIntent().getStringExtra("data"));
		ArrayList<String> list=getCarList(c);
		if(list.size()<=0) 
			{
				this.finish();
				return;
			}
				
		items=new String[list.size()];
		for(int ii=0;ii<list.size();ii++)
		{
			items[ii]=list.get(ii);
		}
		updateCarInfo(0);
		
	}
	private void updateCarInfo(int index)
	{
		c.moveToPosition(index);
		
			
			try {
				JSONObject js=new JSONObject(c.getString(c.getColumnIndex("car")));
				JSONObject js1=js.getJSONObject("car");
				Log.d("lichao",js1.toString());
				
				/*num.setText(c.getString(c.getColumnIndex("num")));
				owner.setText(c.getString(c.getColumnIndex("owner")));
				fdjh.setText(c.getString(c.getColumnIndex("fdjh")));*/
				
				
				num.setText(js1.getString("Num"));
				owner.setText(js1.getString("Owner"));
				color.setText(js1.getString("Color"));
				brand.setText(js1.getString("Brand"));
				type.setText(js1.getString("NumCata"));
				fdjh.setText(c.getString(c.getColumnIndex("fdjh")));
				
			
				
				valid.setText(js1.getString("ValidDate"));
				destory.setText(js1.getString("DestroyDate"));
				
			} catch (JSONException e) {
				Log.d("lichao","CarInfo json error is: "+e.toString());
			}

		
	}
	public void onChangeCar(View v)
	{
		
		new AlertDialog.Builder(CarInfo.this).setTitle("选择车辆")
		.setSingleChoiceItems(items, 0,this).show();//显示对话框
	}
	public void onCancelCarDetail(View v){
		this.finish();
		this.overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
	}

	private ArrayList<String> getCarList(Cursor c)
	{

    	ArrayList<String> list=new ArrayList<String>();
    	while(c.moveToNext())
    	{
    		list.add(c.getString(c.getColumnIndex("num")));
		}
    	
		return list;
	}

	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		// TODO Auto-generated method stub
		updateCarInfo(arg1);
		arg0.dismiss();
	}
}
