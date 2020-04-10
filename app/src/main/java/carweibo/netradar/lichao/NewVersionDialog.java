package carweibo.netradar.lichao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NewVersionDialog extends Activity {

	Intent i;
	TextView tv1;
	TextView tv2;
	TextView tv3;
	TextView btn_ok;
	ProgressBar pb;
	String file;
	ColorStateList tv1_color;
	final int DOWN_UPDATE=1;
	final int DOWN_OVER=2;
	final int DOWN_ERROR=3;
	int progress=0;
	boolean interceptFlag=false;
	RelativeLayout progress_layout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.newversion_dialog);
		tv1=(TextView)findViewById(R.id.verhint);
		tv2=(TextView)findViewById(R.id.textnewversionfunc);
		tv3=(TextView)findViewById(R.id.progresshint);
		pb=(ProgressBar)findViewById(R.id.progressBarDownLoad);
		btn_ok=(TextView)findViewById(R.id.dialog_ok_update);
		progress_layout=(RelativeLayout)findViewById(R.id.progressRlayout);
		tv1_color=tv1.getTextColors();
		i=new Intent();
		i=this.getIntent();
		
		tv1.setText("发现新版本：V"+i.getStringExtra("ver"));
		tv2.setText(i.getStringExtra("func"));
		file=i.getStringExtra("file");
		
	}
	
	public void onCancelNewVersion(View v)
	{
		interceptFlag=true;
		this.finish();
	}
	public void onOkNewVersion(View v)
	{
		tv1.setText("新版本下载中...");
		tv1.setTextColor(tv1_color);
		tv2.setVisibility(View.GONE);
		progress_layout.setVisibility(View.VISIBLE);
		//pb.setBackgroundResource(R.drawable.mm_progress_bg);
		//pb.setProgressDrawable(this.getResources().getDrawable(R.drawable.mm_progress));
		//pb.setProgressDrawable(this.getResources().getDrawable(R.drawable.progressbar));
		pb.setMax(100);
		pb.setProgress(0);
		tv3.setText("0/100");
		((TextView)v).setTextColor(Color.GRAY);
		//v.setClickable(false);
		v.setEnabled(false);
		
		downLoadApk(this,file);
		
	}
	
	private void downLoadApk(final Context con,final String file)
	{
		final Handler handler=new Handler(){
			 public void handleMessage(Message message) {
				 
				
					
					switch (message.what) {
					case DOWN_UPDATE:
						pb.setProgress(progress);
						tv3.setText(progress+"/100");
						break;
					case DOWN_OVER:
						pb.setProgress(100);
						tv1.setText("版本下载成功！");
						tv3.setText("100/100");
						installApk((String) message.obj);
						((Activity)con).finish();
						break;
					case DOWN_ERROR:
						tv1.setText("版本下载失败！");
						tv1.setTextColor(Color.RED);
						btn_ok.setText("重新下载");
						btn_ok.setTextColor(Color.WHITE);
						btn_ok.setEnabled(true);
					default:
						break;
					}

				
				
             
          }
		};
		
		
		new Thread(){

			HttpURLConnection conn=null;
			@Override
			public void run() {
				try{
				URL url = new URL(((DBUility)con.getApplicationContext()).webapps+"/pic/download/"+file);
				//URL url = new URL("http://192.168.1.105:8080/first/newversion/"+file);
				
				conn = (HttpURLConnection)url.openConnection();
				conn.setConnectTimeout(5000);
				conn.setReadTimeout(5000);
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();
				
				String sd=getSDDir();
				if(sd==null)
				{
				}
				File savedir = new File(sd+"/weizhangquery/download");
				if(!savedir.exists()){
					savedir.mkdirs();
				}
				String apkFile = file;
				File ApkFile = new File(savedir+"/"+apkFile);
				if(!ApkFile.exists())
				{
					ApkFile.createNewFile();
				}
				FileOutputStream fos = new FileOutputStream(ApkFile);
				
				int count = 0;
				byte buf[] = new byte[1024];
				
				do{   		   		
		    		int numread = is.read(buf);
		    		count += numread;
		    	    progress =(int)(((float)count / length) * 100);
		    	    //更新进度
		    	    handler.sendEmptyMessage(DOWN_UPDATE);
		    		if(numread <= 0){	
		    			Message message = handler.obtainMessage(DOWN_OVER,savedir+"/"+apkFile );
						
		    			handler.sendMessage(message);
		    			break;
		    		}
		    		fos.write(buf,0,numread);
		    	}while(!interceptFlag);//点击取消就停止下载.
				
				fos.close();
				is.close();
				if(conn!=null)
					conn.disconnect();
			} catch (MalformedURLException e) {
				handler.sendEmptyMessage(DOWN_ERROR);
				if(conn!=null)
					conn.disconnect();
				return;
			} catch(IOException e){
				handler.sendEmptyMessage(DOWN_ERROR);
				if(conn!=null)
					conn.disconnect();
				return;
			}

				 
				
			}
			
			
		}.start();
	}

private static String getSDDir()
{
	 if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
	 {  
		 File sdCard=Environment.getExternalStorageDirectory();
		 
		
		 StatFs statfs = new StatFs(sdCard.getPath()); 
		 long totalBlocks = statfs.getAvailableBlocks();
		 long blocSize = statfs.getBlockSize();
		 if((totalBlocks*blocSize)>1000000)
			 return sdCard.getPath();
		 else
			 return null;
		 
				 
	 }
	return null;
}
private void installApk(String saveFileName){
	File apkfile = new File(saveFileName);
    if (!apkfile.exists()) {
        return;
    }    
    Intent i = new Intent(Intent.ACTION_VIEW);
    i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive"); 
    this.startActivity(i);

}

}
