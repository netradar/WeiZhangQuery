package carweibo.netradar.lichao;



import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Appstart extends Activity{

	ImageView iv;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.appstart);
		iv=(ImageView)findViewById(R.id.welcome_car);
		Animation animation = (AnimationSet) AnimationUtils.loadAnimation(this, R.anim.welcome_anim);
		iv.setAnimation(animation);
        Intent i=new Intent();
        i.setClass(Appstart.this, ServerService.class);
        startService(i);
	new Handler().postDelayed(new Runnable(){
		@Override
		public void run(){
			iv.setVisibility(8);
			Intent intent = new Intent (Appstart.this,MainTabActivity.class);			
			startActivity(intent);			
			Appstart.this.finish();
		}
	}, 1500);
   }
}