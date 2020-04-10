package carweibo.netradar.lichao;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class About extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.about_layout);
		TextView ver=(TextView)findViewById(R.id.version);
		
		try {
			ver.setText("version"+getVersionName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onCancelAbout(View v)
	{
		this.finish();
		this.overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
	}
	 @Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		this.finish();
		this.overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
	}

	private String getVersionName() throws Exception
	 {
	
		 PackageManager packageManager = getPackageManager();
	           
		 PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);
		 String version = packInfo.versionName;
		 return version;
	   }
}
