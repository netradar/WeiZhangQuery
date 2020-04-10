package carweibo.netradar.lichao;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.params.CoreConnectionPNames;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class VerifyDialog extends Activity {
	
	ImageView code_image;
	EditText code_edit;
	TextView ok_btn;
	//ProgressBar pb;
	TextView title;
	
	Bundle bd;
	String jsessionid;
	
	Boolean isGetOk=false;
	public class GetCodeImage extends AsyncTask<Integer, Integer, Bitmap> {

		
		@Override
		protected void onPostExecute(Bitmap result) {
			
			
		//	pb.setVisibility(View.GONE);
			if(result!=null)
			{
				title.setText("请输入验证码");
				isGetOk=true;
				code_image.setImageBitmap(result);
			}
			else
				title.setText("验证码获取失败！");
			
			super.onPostExecute(result);
		}

		@Override
		protected Bitmap doInBackground(Integer... arg0) {
			
			isGetOk=false;
			if(!IsNetworkOk())
			{
				
				
				return null;
			}
			
			String verify_code_url="http://117.36.53.122:9081/wfcx/imageServlet";///wfcx/query.do?actiontype=vioSurveil&hpzl=02&hphm=AEQ115&tj=SYR&tj_val=%E9%83%AD%E7%BB%AA%E6%96%8C";
		
			Bitmap bm=getBitmap(verify_code_url);
			if(bm==null) return null;
			
			return bm;
		}

	}
	private Bitmap getBitmap(String biturl)
	  {
	    Bitmap bitmap=null;
	    
	    try {
	      URL url=new URL(biturl);
	      
	      URLConnection conn=url.openConnection();
	           
	     
	      conn.setConnectTimeout(5000);
	      conn.setReadTimeout(5000);
	      conn.setRequestProperty("Cookie", "JSESSIONID=0000mDQ71elDALuvKAX8ovBIqMt:-1");
	    	

				
	      InputStream in =conn.getInputStream();
	      String cookie=conn.getHeaderField("Set-cookie");
    	
	      if(cookie!=null)
	      {
	    	//  Log.d("lichao","verifydialog jsession id is: "+cookie.substring(0,cookie.indexOf(";")));
	    	  jsessionid=cookie.substring(0,cookie.indexOf(";"));
	    	  
	    	  Log.d("lichao","verify jsessionid 1 is "+jsessionid);
	      }
	      else
	      {
	    	  Log.d("lichao","cookie is null");
	    	  return null;
	      }
	      bitmap=BitmapFactory.decodeStream(new BufferedInputStream(in));
	    
	      in.close();
	    } catch (Exception e) {
	     
	      
	    }
	    return bitmap;
	  }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.get_verify_dialog);
		code_image=(ImageView)findViewById(R.id.verify_code_image);
		code_edit=(EditText)findViewById(R.id.verity_code_edittext);
		ok_btn=(TextView)findViewById(R.id.verify_ok_btn);
		title=(TextView)findViewById(R.id.verify_hint_textview);
	//	pb=(ProgressBar)findViewById(R.id.verify_progressbar);
		
		title.setText("正在获取验证码...");
	//	pb.setVisibility(View.VISIBLE);
		new GetCodeImage().execute(0);
		
	}

	boolean IsNetworkOk(){
		NetworkInfo info=((ConnectivityManager)getSystemService(this.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		if(info==null||!info.isAvailable())
			return false;
		return true;
	}
	
	public void onOtherImage(View v)
	{
		title.setText("正在获取验证码...");
//	pb.setVisibility(View.VISIBLE);
		new GetCodeImage().execute(0);
	}
	public void onSubmit(View v)
	{
		if(code_edit.getText().toString().length()==0)
		{
			Toast.makeText(this, "验证码不能为空～", Toast.LENGTH_SHORT).show();
			return;
		}
		if(!isGetOk)
		{
			Toast.makeText(this, "验证码输入不正确", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent i=new Intent();
		i.putExtra("verify_code",code_edit.getText().toString());
		i.putExtra("type", this.getIntent().getStringExtra("type"));
		 Log.d("lihcao","verify jsessionid is "+jsessionid);
		i.putExtra("jsession_id", jsessionid);
		i.putExtra("carinfo", this.getIntent().getBundleExtra("carinfo"));
		this.setResult(0, i);
	
		this.finish();
	}
	@Override
	public void onBackPressed() {
		this.setResult(1, null);
		
		this.finish();
		super.onBackPressed();
	}
	
	
}
