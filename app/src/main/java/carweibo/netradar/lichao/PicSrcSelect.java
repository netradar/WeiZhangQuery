package carweibo.netradar.lichao;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

public class PicSrcSelect extends Activity {

	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择

    private static final int SELECT_TYPE_TOUXIANG = 3;// 选头像
    private static final int SELECT_TYPE_WEIBO = 4;// 选上传照片

    int select_type;
	Uri uri;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		this.finish();
		return super.onTouchEvent(event);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.pic_src_select);
		select_type=this.getIntent().getIntExtra("select_type",0);
		
		LinearLayout layout = (LinearLayout)findViewById(R.id.exit_layout2);
		layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		
	}
	
	public void onFromCamera(View v)
	{
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if(ScreenShoot.getSDDir(this)==null)
		{
			Toast.makeText(this, "SD卡空间不足，无法完成指定操作", Toast.LENGTH_SHORT).show();
			
			return;
		}
		String strImgPath =ScreenShoot.getSDDir(this) + "/weizhangquery/cam/";
		String fileName = new SimpleDateFormat("yyyyMMddHHmmss")
        .format(new Date()) + ".jpg";
		
		File out = new File(strImgPath);
        if (!out.exists()) {
            out.mkdirs();
        }
        out = new File(strImgPath, fileName);
        strImgPath = strImgPath + fileName;
        uri = Uri.fromFile(out);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
    
        

		startActivityForResult(intent,PHOTO_REQUEST_TAKEPHOTO);
		

		
	}
	
	public void onFromAlbum(View v)
	{
		Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
	    
	    intent.setType("image/*");


	    startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
		
	}
	public void onCancelSel(View v)
	{
		this.finish();
		
		setResult(0,null);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode==0) return;
		if(requestCode==PHOTO_REQUEST_TAKEPHOTO)
		{
			Intent i=new Intent();
			
			Bundle bun=new Bundle();
			bun.putString("camURI", uri.toString());
			
			i.putExtra("data", bun);
			setResult(requestCode,i);
		}
		else if(requestCode==PHOTO_REQUEST_GALLERY)
			setResult(requestCode,data);
		
		 
		 

		 this.finish();
		super.onActivityResult(requestCode, resultCode, data);
	}
	

}
