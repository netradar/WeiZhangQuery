package carweibo.netradar.lichao;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NewVender extends Activity implements TextWatcher {

	private static final int PIC_NUM=3;
	TextView vender_name,vender_addr,vender_comment,vender_words_count;
	
	ArrayList<ImageInfo> bmpFile_list;
	ImageView[]	imageView_list;
	LinearLayout[] layout_list;
	ImageView[] deleteView_list;
	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    
	int[] img_btn_list={R.id.new_vender_addImage1,R.id.new_vender_addImage2,R.id.new_vender_addImage3};
	int[] layout_list_id={R.id.new_vender_addImage_layout1,R.id.new_vender_addImage_layout2,R.id.new_vender_addImage_layout3};
	int[] delete_list_id={R.id.new_vender_deleteImage1,R.id.new_vender_deleteImage2,R.id.new_vender_deleteImage3};
	
	int draft_id=-1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.new_vender_layout);
		
		vender_name=(TextView)findViewById(R.id.vender_name);
		vender_addr=(TextView)findViewById(R.id.vender_addr);
		vender_comment=(TextView)findViewById(R.id.vender_comment);
		vender_words_count=(TextView)findViewById(R.id.new_vender_words_count);
		
		vender_comment.addTextChangedListener(this);
		
		imageView_list=new ImageView[PIC_NUM];
		deleteView_list=new ImageView[PIC_NUM];
		layout_list=new LinearLayout[PIC_NUM];
		bmpFile_list=new ArrayList<ImageInfo>();
		
		draft_id=this.getIntent().getIntExtra("draft_id", -1);
		
		for(int i=0;i<PIC_NUM;i++)
		{
		
			layout_list[i]=(LinearLayout)findViewById(layout_list_id[i]);
			imageView_list[i]=(ImageView)findViewById(img_btn_list[i]);
			deleteView_list[i]=(ImageView)findViewById(delete_list_id[i]);
			
		}
		refreshImageView();
	}

	@Override
	public void onBackPressed() {
		onCancelNewVender(null);
	}

	public void onCancelNewVender(View v)
	{
		this.finish();
		overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
	}
	public void onSendNewVender(View v)
	{
		String name=vender_name.getText().toString();
		String addr=vender_addr.getText().toString();
		String comment=vender_comment.getText().toString();
		
		if(name.length()==0)
		{
			Toast.makeText(this, "商户名称不能为空～", Toast.LENGTH_SHORT).show();
			return;
		}
		if(comment.length()==0)
		{
			Toast.makeText(this, "您的评价没有填～", Toast.LENGTH_SHORT).show();
			return;
		}
		if(addr.length()==0)
		{
			addr="无";
		}
		
		SendWeibo.sendVender(this, UserManager.getCurUser(this.getApplicationContext()), name,addr,comment, bmpFile_list,draft_id);
		
		this.finish();
		overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
	}
	public void onAddPic(View v)
	{
		int j=0;
		int sum=bmpFile_list.size();
		for(j=0;j<PIC_NUM;j++)
		{
			if(v==imageView_list[j])
			{
				if(j<sum)
					return;
				else
				{
					
				
					Intent i=new Intent();
					i.setClass(NewVender.this, PicSrcSelect.class);
					
					this.startActivityForResult(i, 1);
				}
			}
		}
	}
	public void onDeletePic(View v)
	{
		int j;
		for(j=0;j<PIC_NUM;j++)
		{
			if(v==deleteView_list[j])
			{
				bmpFile_list.remove(j);
				refreshImageView();
				break;
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode)
		{
		
			case 1:
			switch(resultCode)
			{
				
				case PHOTO_REQUEST_TAKEPHOTO:
					
					Bundle bun=data.getBundleExtra("data");
		           	String uri=bun.getString("camURI");
		           	
		           	UploadThumbInfo info=PicProcessManager.adjustAndSave(this,uri.substring(6));
		           	
					if(info!=null)
						addToList(info);
					break;
				case PHOTO_REQUEST_GALLERY:
					String real_url=getRealPath(data.getData());
					UploadThumbInfo info1=PicProcessManager.adjustAndSave(this,real_url);
					
					if(info1!=null)
						addToList(info1);
					
					
					break;
			}
			refreshImageView();
			break;
		}
	}

	private void refreshImageView() {
		resetView();
		int sum=bmpFile_list.size();
		int i=0;
		while(i<sum)
		{
			//layout_list[i].setBackgroundResource(R.drawable.add_pic_backgrounds);
			imageView_list[i].setVisibility(View.VISIBLE);
			
			imageView_list[i].setImageBitmap(bmpFile_list.get(i).bmp);
			deleteView_list[i].setVisibility(View.VISIBLE);
			i++;
		}
		if(i<PIC_NUM)
		imageView_list[i].setVisibility(View.VISIBLE);
		
		
		
	}

	private void resetView() {
		for(int i=0;i<PIC_NUM;i++)
		{
			layout_list[i].setBackgroundDrawable(null);
			imageView_list[i].setVisibility(View.GONE);
			imageView_list[i].setImageResource(R.drawable.pic_add_btn);
			deleteView_list[i].setVisibility(View.GONE);
		}
		
		
	}

	private void addToList(UploadThumbInfo info) 
	{
		ImageInfo img=new ImageInfo();
		img.bmp=PicProcessManager.getSmallPic(this,info.thumb_url);
		
		img.file_url=info.upload_url;
		img.sfile_url=info.thumb_url;
		img.size=info.thumb_width+"*"+info.thumb_height;
		
		if(img.bmp==null||img.sfile_url==null||img.size==null)
			return;
		bmpFile_list.add(img);
		
	}
	private String getRealPath(Uri uri)
	{
		if(uri.getScheme().toString().equals("file"))
		{
			return uri.toString().substring(7,uri.toString().length());
		}
		String[] proj = { MediaStore.Images.Media.DATA };  

	 

		 Cursor actualimagecursor = managedQuery(uri,proj,null,null,null);  
 

		 int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);  

		    

		actualimagecursor.moveToFirst();  

	   

		 String img_path = actualimagecursor.getString(actual_image_column_index);  

		 return img_path;
		 
	}

	@Override
	public void afterTextChanged(Editable s) {
		vender_words_count.setText(vender_comment.getText().toString().length()+"/100");
		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}
}
