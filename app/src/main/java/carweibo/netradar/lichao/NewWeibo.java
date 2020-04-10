package carweibo.netradar.lichao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NewWeibo extends Activity implements TextWatcher {
	
	private static int MAX_THUMB_PIC_WIDTH=200;
	private static int MAX_THUMB_PIC_HEIGHT=200;
	private static int MAX_UPLOAD_PIC_WIDTH=720;
	private static int MAX_UPLOAD_PIC_HEIGHT=1080;
	
	private class PicInfo{
		float rate;
		int dstWidth;
		int dstHeight;
	}
	/*private class UploadThumbInfo{
		String upload_url;
		String thumb_url;
		int thumb_width;
		int thumb_height;
	}*/
	private static int MAX_PIC_UPLOAD=3;
	int cur_select=0;


	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    
	int[] img_btn_list={R.id.addImage1,R.id.addImage2,R.id.addImage3,R.id.addImage4,R.id.addImage5,R.id.addImage6,R.id.addImage7,R.id.addImage8,R.id.addImage9};
	int[] layout_list_id={R.id.addImage_layout1,R.id.addImage_layout2,R.id.addImage_layout3,R.id.addImage_layout4,R.id.addImage_layout5,R.id.addImage_layout6,R.id.addImage_layout7,R.id.addImage_layout8,R.id.addImage_layout9};
	int[] delete_list_id={R.id.deleteImage1,R.id.deleteImage2,R.id.deleteImage3,R.id.deleteImage4,R.id.deleteImage5,R.id.deleteImage6,R.id.deleteImage7,R.id.deleteImage8,R.id.deleteImage9};
	LinearLayout[] layout_list;
	ImageView[] deleteView_list;
	ImageView[] imageView_list;;
	
	ArrayList<ImageInfo> bmpFile_list;
	LinearLayout add_pic_layout2,add_pic_layout3;
	EditText weibo_content;
	TextView words_count;
	ImageView same_tencent,same_sina;
	boolean isTencentSelect=false,isSinaSelect=false;
	int cur_select_type=0;
	ImageView radio_image1,radio_image2,radio_image3;
	
	boolean isDraft=false;
	String content;
	int draft_id=-1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.newweibo_layout);
		imageView_list=new ImageView[9];
		deleteView_list=new ImageView[9];
		layout_list=new LinearLayout[9];
		bmpFile_list=new ArrayList<ImageInfo>();
		
		add_pic_layout2=(LinearLayout)findViewById(R.id.add_pic_layout2);
		add_pic_layout3=(LinearLayout)findViewById(R.id.add_pic_layout3);
		same_tencent=(ImageView)findViewById(R.id.tencent_imageview);
		same_sina=(ImageView)findViewById(R.id.sina_imageview);
		radio_image1=(ImageView)findViewById(R.id.normal_weibo);
		radio_image2=(ImageView)findViewById(R.id.midum_weibo);
		radio_image3=(ImageView)findViewById(R.id.high_weibo);
		weibo_content=(EditText)findViewById(R.id.weibo_content);
		words_count=(TextView)findViewById(R.id.newweibo_words_count);
		
		weibo_content.addTextChangedListener(this);
		initImageView();
		refreshImageView();
		
		isDraft=this.getIntent().getBooleanExtra("isDraft", false);
		
		if(isDraft)
		{
			loadWeiboFromDraft();
			String tmp_draft_id=this.getIntent().getStringExtra("draft_id");
			
			draft_id=Integer.parseInt(tmp_draft_id);
		}
		
	}
	
	@Override
	protected void onResume() {
		
		super.onResume();
		Intent i=getIntent();
		if(i==null) return;
		String action=i.getAction();
		String type=i.getType();
		
		if(i==null||action==null||type==null) return;
		String cur_nickname=UserManager.getCurUser(this.getApplicationContext());
		
		if(cur_nickname.equals("NOUSER")||cur_nickname.equals("NOLOGIN"))
		{
			if(cur_nickname.equals("NOLOGIN"))
			{
				Intent intent=new Intent();
				intent.setClass(NewWeibo.this, SelectUser.class);
				startActivityForResult(intent,3);
				this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
				Toast.makeText(this, "您还没登录，请选择一个用户登录～", Toast.LENGTH_SHORT).show();
			}
			if(cur_nickname.equals("NOUSER"))
			{
				Toast.makeText(this, "请先登录～", Toast.LENGTH_SHORT).show();
				Intent intent=new Intent();
				intent.setClass(NewWeibo.this, Login.class);
				
				this.startActivityForResult(intent, 3);
				this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);//全新登录，数据库没有用户数据
			
			}
			return;
		}
		
		if(action.equals(Intent.ACTION_SEND)&&type.startsWith("image/"))
		{
			Uri imgUri = (Uri)i.getExtras().getParcelable(Intent.EXTRA_STREAM);              
			//Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM); 
			
			
			String file=getRealPath(imgUri);
			
			UploadThumbInfo info=adjustAndSave(file);
           	
			if(info!=null)
				addToList(info);
			
			refreshImageView();
			
		}
		if(action.equals(Intent.ACTION_SEND)&&type.equals("carweibo"))
		{
			String dir = i.getStringExtra("dir");              
			//Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM); 
			
			
			
			UploadThumbInfo info=adjustAndSave(dir);
           	
			if(info!=null)
				addToList(info);
			
			refreshImageView();
			
		}
		if(action.equals(Intent.ACTION_SEND)&&type.startsWith("text/"))
		{
			String extra = i.getStringExtra(Intent.EXTRA_TEXT);       
	        
			if(extra!=null)		
			weibo_content.setText(extra);
			
		}
		if(action.equals(Intent.ACTION_SEND_MULTIPLE)&&type.startsWith("image/"))
		{
			ArrayList<Uri> imageUris = i.getParcelableArrayListExtra(Intent.EXTRA_STREAM);  
             
	        for(int j=0;j<imageUris.size()&&j<3;j++)
	        {
				String file=getRealPath(imageUris.get(j));
				
				UploadThumbInfo info=adjustAndSave(file);
	           	
				if(info!=null)
					addToList(info);
	        }
			refreshImageView();
			
		}
		
	}

	private void loadWeiboFromDraft() {
		
		weibo_content.setText(this.getIntent().getStringExtra("content"));
		
		String picList=this.getIntent().getStringExtra("pic_list");
		
		try {
			JSONArray ja=new JSONArray(picList);
			
			for(int i=0;i<ja.length();i++)
			{
				JSONObject json= ja.getJSONObject(i);
				
				ImageInfo img=new ImageInfo();
				Log.d("lichao",json.toString());
				img.bmp=getSmallPic(json.getString("sfile"));
				
				img.file_url=json.getString("file");
				img.sfile_url=json.getString("sfile");
				img.size=json.getString("size");
				
				if(img.bmp==null||img.sfile_url==null||img.size==null)
					return;
				bmpFile_list.add(img);
			}
			refreshImageView();
			
		} catch (JSONException e) {
			Log.d("lichao","NewWeibo json error "+e.toString());
		}
		
	}

	private void initImageView()
	{
		
		
		for(int i=0;i<9;i++)
		{
			//ImageInfo img=new ImageInfo();
			layout_list[i]=(LinearLayout)findViewById(layout_list_id[i]);
			imageView_list[i]=(ImageView)findViewById(img_btn_list[i]);
			deleteView_list[i]=(ImageView)findViewById(delete_list_id[i]);
			
		}
		radio_image1.setImageResource(R.drawable.icon_green);
		cur_select_type=0;
		isTencentSelect=isTencentBind();
		isSinaSelect=isSinaBind();
		
		if(isTencentSelect)
			same_tencent.setImageResource(R.drawable.icon_tencent_weibo);
		else
			same_tencent.setImageResource(R.drawable.icon_tencent_weibo_black);
		
		if(isSinaSelect)
			same_sina.setImageResource(R.drawable.more_weibo);
		else
			same_sina.setImageResource(R.drawable.more_weibo_black);
	}

	private void refreshImageView() 
	{
		resetView();
		int sum=bmpFile_list.size();
		int i=0;
		while(i<sum)
		{
			layout_list[i].setBackgroundResource(R.drawable.add_pic_backgrounds);
			imageView_list[i].setVisibility(View.VISIBLE);
			
			imageView_list[i].setImageBitmap(bmpFile_list.get(i).bmp);
			deleteView_list[i].setVisibility(View.VISIBLE);
			i++;
		}
		if(i<MAX_PIC_UPLOAD)
		imageView_list[i].setVisibility(View.VISIBLE);
		
		if(i<4) 
		{
			add_pic_layout2.setVisibility(View.GONE);
			add_pic_layout3.setVisibility(View.GONE);
		}
		if(i>=4&&i<8)
		{
			add_pic_layout3.setVisibility(View.GONE);
		}
		
	}

	private void resetView() {
		for(int i=0;i<9;i++)
		{
			layout_list[i].setBackgroundDrawable(null);
			imageView_list[i].setVisibility(View.GONE);
			imageView_list[i].setImageResource(R.drawable.pic_add_btn);
			deleteView_list[i].setVisibility(View.GONE);
		}
		add_pic_layout2.setVisibility(View.VISIBLE);
		add_pic_layout3.setVisibility(View.VISIBLE);
		
	}

	public void onAddPic(View v)
	{
		int j=0;
		int sum=bmpFile_list.size();
		for(j=0;j<9;j++)
		{
			if(v==imageView_list[j])
			{
				if(j<sum)
					return;
				else
				{
					
				
					Intent i=new Intent();
					i.setClass(NewWeibo.this, PicSrcSelect.class);
					
					this.startActivityForResult(i, 1);
				}
			}
		}
		
		
	}

	public void onDeletePic(View v)
	{
		int j;
		for(j=0;j<9;j++)
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
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode)
		{
		case 0:
			if(resultCode==0)
			{
				Intent i=new Intent();
				i.setClass(NewWeibo.this, Score.class);
				
				this.startActivity(i);
				this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
			}
			break;
		case 1:
			switch(resultCode)
			{
				
				case PHOTO_REQUEST_TAKEPHOTO:
					
					Bundle bun=data.getBundleExtra("data");
		           	String uri=bun.getString("camURI");
		           	
		           	UploadThumbInfo info=adjustAndSave(uri.substring(6));
		           	
					if(info!=null)
						addToList(info);
					break;
				case PHOTO_REQUEST_GALLERY:
					String real_url=getRealPath(data.getData());
					UploadThumbInfo info1=adjustAndSave(real_url);
					
					if(info1!=null)
						addToList(info1);
					
					
					break;
			}
			refreshImageView();
			break;
		case 2:
			switch(resultCode)
			{
				
				case 0:
					Intent i=new Intent();
					i.setClass(NewWeibo.this, Account.class);
					startActivity(i);
					this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);					
					break;
				case 1:
					
					
					
					break;
			}
			break;
		case 3:
			switch(resultCode)
			{
			
			case 0://Login or Select activity cancel
				this.finish();
				break;
			case 1://Login or Select activity success return
				UserManager.setCurUser(this.getApplicationContext(), data.getStringExtra("nickname"));
				break;
			}
		}
	}
	private void addToList(UploadThumbInfo info)
	{
		ImageInfo img=new ImageInfo();
		img.bmp=getSmallPic(info.thumb_url);
		
		img.file_url=info.upload_url;
		img.sfile_url=info.thumb_url;
		img.size=info.thumb_width+"*"+info.thumb_height;
		
		if(img.bmp==null||img.sfile_url==null||img.size==null)
			return;
		bmpFile_list.add(img);
		
	}

	private String getThumbPic(String filePath)
	{
		float density=this.getResources().getDisplayMetrics().density;
		BitmapFactory.Options  option=new BitmapFactory.Options();
		BitmapFactory.Options  option1=new BitmapFactory.Options();
		option.inJustDecodeBounds=true;
		Bitmap bm=BitmapFactory.decodeFile(filePath, option);
		
		float rate=1;

		
		if((option.outHeight>MAX_THUMB_PIC_WIDTH*density)||(option.outWidth>MAX_THUMB_PIC_WIDTH*density))
		{
			if((option.outHeight>=option.outWidth))
			{
				rate=(int) (option.outHeight/(MAX_THUMB_PIC_WIDTH*density));
				
				option1.inSampleSize=(int) rate;
				option1.inJustDecodeBounds=false;
				bm=BitmapFactory.decodeFile(filePath, option1);
				
				bm=Bitmap.createScaledBitmap(bm,(int)(option.outWidth/rate), (int)(MAX_THUMB_PIC_WIDTH*density), true);
			}
			else
			{
				rate=(int) (option.outWidth/(MAX_THUMB_PIC_WIDTH*density));
				
				option1.inSampleSize=(int) rate;
				option1.inJustDecodeBounds=false;
				bm=BitmapFactory.decodeFile(filePath, option1);
				
				bm=Bitmap.createScaledBitmap(bm,(int)(MAX_THUMB_PIC_WIDTH*density), (int)(option.outHeight/rate),true);
			}
			
			
		
			
			if(ScreenShoot.getSDDir(this)==null) return null;
			
			String url=filePath.substring(0, filePath.length()-4)+"_s.png";
				
			ScreenShoot.savePic(bm, ScreenShoot.getSDDir(this)+"/weizhangquery/pic/",url, 100);
			 Log.d("lichao","kkk "+url);
			return url;
			
			
			
				
			//return  Bitmap.createScaledBitmap(bm, dstWidth+1, dstHeight+1, true);
			//return  Bitmap.createBitmap(source, x, y, width, height)
			
		}
		else
		{
			return filePath;
		}
		
	}

	private String getBmpSize(String filePath) {
		Bitmap bm=BitmapFactory.decodeFile(filePath);
		if(bm!=null)
			return bm.getWidth()+"*"+bm.getHeight();
		return null;
	}

	private UploadThumbInfo adjustAndSave(String fileName) {
		
		
		BitmapFactory.Options  option=new BitmapFactory.Options();
		BitmapFactory.Options  option1=new BitmapFactory.Options();
		option.inJustDecodeBounds=true;
		Bitmap bm=BitmapFactory.decodeFile(fileName, option);
		
		
		PicInfo pic=getPicInfo(option,0);
		PicInfo pic_s=getPicInfo(option,1);
			
		if(pic!=null)//(option.outHeight>MAX_UPLOAD_PIC_HEIGHT)||(option.outWidth>MAX_UPLOAD_PIC_WIDTH))
		{	
			
			option1.inSampleSize=(int) pic.rate;
			option1.inJustDecodeBounds=false;
			bm=BitmapFactory.decodeFile(fileName, option1);
			
			Bitmap bm_upload=Bitmap.createScaledBitmap(bm, pic.dstWidth, pic.dstHeight, true);
			Bitmap bm_thumb=null;
			if(pic_s!=null)
				bm_thumb=Bitmap.createScaledBitmap(bm, pic_s.dstWidth, pic_s.dstHeight, true);
			
			if(ScreenShoot.getSDDir(this)==null)
			{
				Toast.makeText(this, "SD卡空间不足，无法完成指定操作", Toast.LENGTH_SHORT).show();
				return null;
			}
			
			String url=ScreenShoot.getSDDir(this)+"/weizhangquery/pic/"+System.currentTimeMillis()+".png";
			String url_s=url;
		
			ScreenShoot.savePic(bm_upload, ScreenShoot.getSDDir(this)+"/weizhangquery/pic/",url, 70);
			
			if(bm_thumb!=null)
			{
				url_s=url.substring(0,url.length()-4)+"_s.png";
				ScreenShoot.savePic(bm_thumb, ScreenShoot.getSDDir(this)+"/weizhangquery/pic/",url_s, 70);
				
			}
			UploadThumbInfo info=new UploadThumbInfo();
			if(pic_s==null)
			{
				pic_s=pic;
		
			}
			
			info.thumb_height=pic_s.dstHeight;
			info.thumb_width=pic_s.dstWidth;
			info.thumb_url=url_s;
			info.upload_url=url;
			
			
			return info;
			
			
			
				
			//return  Bitmap.createScaledBitmap(bm, dstWidth+1, dstHeight+1, true);
			//return  Bitmap.createBitmap(source, x, y, width, height)
			
		}
		else if(pic_s!=null)
		{
			option1.inSampleSize=(int) pic_s.rate;
			option1.inJustDecodeBounds=false;
			bm=BitmapFactory.decodeFile(fileName, option1);
			
			Bitmap bm_thumb=Bitmap.createScaledBitmap(bm, pic_s.dstWidth, pic_s.dstHeight, true);
				
			if(ScreenShoot.getSDDir(this)==null) return null;
			
			String url_s=ScreenShoot.getSDDir(this)+"/weizhangquery/pic/"+System.currentTimeMillis()+"_s.png";
			ScreenShoot.savePic(bm_thumb, ScreenShoot.getSDDir(this)+"/weizhangquery/pic/",url_s, 70);
			
			UploadThumbInfo info=new UploadThumbInfo();
					
			info.thumb_height=pic_s.dstHeight;
			info.thumb_width=pic_s.dstWidth;
			info.thumb_url=url_s;
			info.upload_url=fileName;
			
			return info;
		}
		else
		{
			UploadThumbInfo info=new UploadThumbInfo();
			
			info.thumb_height=option.outHeight;
			info.thumb_width=option.outWidth;
			info.thumb_url=fileName;
			info.upload_url=fileName;
			
			return info;
		}
		
		
		
	}

	private PicInfo getPicInfo(Options option, int i)
	{
		int dstWidth=option.outWidth;
		int dstHeight=option.outHeight;
		float rate=1;
		int max_width;
		int max_height;
		
		PicInfo ret_pic=new PicInfo();
		
		if(i==0)
		{
			max_width=MAX_UPLOAD_PIC_WIDTH;
			max_height=MAX_UPLOAD_PIC_HEIGHT;
		}
		else if(i==1)
		{
			max_width=MAX_THUMB_PIC_WIDTH;
			max_height=MAX_THUMB_PIC_HEIGHT;
		}
		else 
			return null;
		
		if((option.outHeight>max_height)&&(option.outWidth>max_width))
		{
			
		
			if((option.outHeight>=option.outWidth))
			{
				rate=((float)(option.outWidth)/(float)max_width);
				dstWidth=max_width;
				dstHeight=(int) (((float)(option.outHeight))/rate);
				
				if(dstHeight>4*max_height)
				{
					rate=rate*2;
					dstWidth=max_width/2;
					dstHeight=(int) (dstHeight/2);
				}
			}
			else
			{
				rate=((float)(option.outHeight)/(float)max_height);
				
				dstHeight=max_height;
				dstWidth=(int) (((float)(option.outWidth))/rate);
				
				if(dstWidth>4*max_width)
				{
					rate=rate*2;
					dstHeight=max_height/2;
					dstWidth=(int) (dstWidth/2);
				}
				
			}
			
			ret_pic.dstWidth=dstWidth;
			ret_pic.dstHeight=dstHeight;
			ret_pic.rate=rate;
			
			return ret_pic;
		}
		else
		{
			if((option.outHeight>=option.outWidth))
			{
				if(dstHeight>4*max_height)
				{
					rate=rate*2;
					dstWidth=option.outWidth/2;
					dstHeight=(int) (option.outHeight/rate);
					
					ret_pic.dstWidth=dstWidth;
					ret_pic.dstHeight=dstHeight;
					ret_pic.rate=rate;
					
					return ret_pic;
				}
			}
			else
			{
				if(dstWidth>4*max_width)
				{
					rate=rate*2;
					dstHeight=option.outHeight/2;
					dstWidth=(int) (option.outWidth/rate);
					
					ret_pic.dstWidth=dstWidth;
					ret_pic.dstHeight=dstHeight;
					ret_pic.rate=rate;
					
					return ret_pic;
				}
				
			}
			
			
		}
		return null;
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
	
	private Bitmap getSmallPic(String fileName)
	{
		float density=this.getResources().getDisplayMetrics().density;
		BitmapFactory.Options  option=new BitmapFactory.Options();
		BitmapFactory.Options  option1=new BitmapFactory.Options();
		option.inJustDecodeBounds=true;
		Bitmap bm=BitmapFactory.decodeFile(fileName, option);
		Bitmap ret_bmp;
		int rate=1;

		
		if((option.outHeight>70*density)&&(option.outWidth>70*density))
		{
			if((option.outHeight>=option.outWidth))
			{
				rate=(int) (option.outWidth/(70*density));
			}
			else
				rate=(int) (option.outHeight/(70*density));
			
			
			
			option1.inSampleSize=rate;
			option1.inJustDecodeBounds=false;
			bm=BitmapFactory.decodeFile(fileName, option1);
			
			if(bm==null) return null;
			
			int dstWidth=bm.getWidth();
			int dstHeight=bm.getHeight();
			int x = 0,y = 0;
			if(dstHeight>=dstWidth)
			{
				x=0;
				y=(int) (dstHeight/2-dstWidth/2);
				ret_bmp= Bitmap.createBitmap(bm, x, y, dstWidth, dstWidth);
			}
			else
			{
				x=(int) (dstWidth/2-dstHeight/2);
				y=0;
				ret_bmp= Bitmap.createBitmap(bm, x, y, dstHeight, dstHeight);
			}
			
			
			
			
			
			
				
			//return  Bitmap.createScaledBitmap(bm, dstWidth+1, dstHeight+1, true);
			//return  Bitmap.createBitmap(source, x, y, width, height)
			
		}
		else
		{
			int dstWidth;
			int dstHeight;
			int x = 0,y = 0;
			if((option.outHeight>=option.outWidth))
			{
				dstWidth=(int) (70*density);
				dstHeight=(int) (((float) ((70*density)/option.outWidth))*option.outHeight);
				x=0;
				y=dstHeight/2-dstWidth/2;
			}
			else
			{
				dstHeight=(int) (70*density);
				dstWidth=(int) (((float) ((70*density)/option.outHeight))*option.outWidth);
				y=0;
				x=dstWidth/2-dstHeight/2;
			}
			
			
			
			option1.inSampleSize=1;
			option1.inJustDecodeBounds=false;
			bm=BitmapFactory.decodeFile(fileName, option1);
			
			if(bm==null) return null;	
			ret_bmp= Bitmap.createScaledBitmap(bm,dstWidth, dstHeight,true);
			ret_bmp=Bitmap.createBitmap(ret_bmp,x,y,(int)(70*density),(int)(70*density));
		}
		/*if(ScreenShoot.getSDDir(this)!=null)
		{
			String save_dir=fileName.substring(0, fileName.length()-4)+"_s.png";
			ScreenShoot.savePic(ret_bmp, ScreenShoot.getSDDir(this)+"/weizhangquery/pic/", save_dir, 70);
		}
		else
			return null;*/
		
		return ret_bmp;
		
	}
	
	public void onSameToTencent(View v)
	{
		if(!isTencentBind())
		{
			Intent i=new Intent();
			i.setClass(NewWeibo.this, Dialog.class);
			i.putExtra("text", "提示\n\n您还没有绑定QQ帐号，无法分享到腾讯微博，现在去绑定吗？");
			i.putExtra("ok","绑定");
			i.putExtra("cancel", "取消");
			startActivityForResult(i,2);
			return;
		}
		isTencentSelect=!isTencentSelect;
		if(isTencentSelect)
			same_tencent.setImageResource(R.drawable.icon_tencent_weibo);
		else
			same_tencent.setImageResource(R.drawable.icon_tencent_weibo_black);
	}
	public void onSameToSina(View v)
	{
		if(!isSinaBind())
		{
			Intent i=new Intent();
			i.setClass(NewWeibo.this, Dialog.class);
			i.putExtra("text", "提示\n\n您还没有绑定新浪微博，无法完成分享博，现在去绑定吗？");
			i.putExtra("ok","绑定");
			i.putExtra("cancel", "取消");
			startActivityForResult(i,2);
			return;
		}
		isSinaSelect=!isSinaSelect;
		if(isSinaSelect)
			same_sina.setImageResource(R.drawable.more_weibo);
		else
			same_sina.setImageResource(R.drawable.more_weibo_black);
	}
	public void onNormalWeibo(View v)
	{
		radio_image1.setImageResource(R.drawable.icon_green);
		radio_image2.setImageResource(R.drawable.icon_grey);
		radio_image3.setImageResource(R.drawable.icon_grey);
		cur_select_type=0;
	}
	public void onMidumWeibo(View v)
	{
		int score=UserManager.GetUserScore(this.getApplicationContext(), UserManager.getCurUser(this.getApplicationContext()));
		
		if(score<50)
		{
			Intent i=new Intent();
			i.setClass(NewWeibo.this, Dialog.class);
			
			i.putExtra("text", "积分不足\n\n"+"高亮帖:积分>50\n\n您当前积分为:"+score);
			i.putExtra("ok", "积分说明");
			i.putExtra("cancel", "取消");
			
			this.startActivityForResult(i,0);
			return;
		}
		radio_image1.setImageResource(R.drawable.icon_grey);
		radio_image2.setImageResource(R.drawable.icon_green);
		radio_image3.setImageResource(R.drawable.icon_grey);
		cur_select_type=1;
		
	}
	public void onHighWeibo(View v)
	{
		int score=UserManager.GetUserScore(this.getApplicationContext(), UserManager.getCurUser(this.getApplicationContext()));
		
		if(score<100)
		{
			Intent i=new Intent();
			i.setClass(NewWeibo.this, Dialog.class);
			i.putExtra("text", "积分不足\n\n"+"置顶帖:积分>100\n\n您当前积分为:"+score);
			i.putExtra("ok", "积分说明");
			i.putExtra("cancel", "取消");
			
			this.startActivityForResult(i,0);
			return;
		}
		radio_image1.setImageResource(R.drawable.icon_grey);
		radio_image2.setImageResource(R.drawable.icon_grey);
		radio_image3.setImageResource(R.drawable.icon_green);
		cur_select_type=2;
	}
	
	private boolean isTencentBind()
	{
		UserInfo user=UserManager.getBindedUser(this.getApplicationContext(), "QQ");
		if(user!=null)
		{
			if(UserManager.isUserValid(user))
				return true;
		}
		return false;
	
	}
	private boolean isSinaBind()
	{
		UserInfo user=UserManager.getBindedUser(this.getApplicationContext(), "Weibo");
		if(user!=null)
		{
			if(UserManager.isUserValid(user))
				return true;
		}
		return false;
	}
	public void onSendNewWeibo(View v)
	{
		if(!isInputCorrect())
			return;
		
		String content=new String();
		if(weibo_content.length()==0)
		{
			content="分享图片";
		}
		else
		{
			content=weibo_content.getText().toString();
		}
	
			SendWeibo.sendCarWeibo(this, UserManager.getCurUser(this.getApplicationContext()), content, bmpFile_list, cur_select_type,0,draft_id);
		
		
		onBackPressed();
		/*if(isSinaSelect)
		{
			UserInfo user=UserManager.getBindedUser(this.getApplicationContext(), "Weibo");
			if(user!=null)
				new SendWeibo().sendSinaWeibo(null, user, content);
		}
		if(isTencentSelect)
		{
			UserInfo user=UserManager.getBindedUser(this.getApplicationContext(), "QQ");
			if(user!=null)
				new SendWeibo().sendTencentWeibo(null, user, content);
		}*/
	}

	private boolean isInputCorrect() {
		
		if(weibo_content.length()==0&&bmpFile_list.size()==0)
		{
			Toast.makeText(this, "您没有输入任何内容哦～", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		
		return true;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		this.finish();
		this.overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
		
	}
	public void onCancelNewWeibo(View v)
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
	public void afterTextChanged(Editable arg0) {

		words_count.setText(weibo_content.getText().toString().length()+"/300");
		
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}
}
