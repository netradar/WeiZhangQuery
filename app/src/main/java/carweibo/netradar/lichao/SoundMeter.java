package carweibo.netradar.lichao;

import java.io.IOException;

import android.media.MediaRecorder;
import android.util.Log;

public  class SoundMeter {
	

	private MediaRecorder recorder = null;
	long send_time;
	
	String file_name;
	

	public SoundMeter() 
	{
		
		
	}

	private void initRecorder()
	{
		recorder=new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);  
		recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);  
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);  
	}
		
	

	public void start(String dir,long file) {
		
		String name=dir+"/"+file+".amr";
		file_name=name;
		send_time=file;
		if (recorder == null) 
		{
			initRecorder();
			recorder.setOutputFile(name);
			try {
				recorder.prepare();
				recorder.start();
				
			
			} catch (IllegalStateException e) {
				Log.d("lichao","SoundMeter IllegalStateException error:"+e.toString());
			} catch (IOException e) {
				Log.d("lichao","SoundMeter IOException error:"+e.toString());
			}
		}
		
		
		
		

		
	}

	public void stop() {
		if (recorder != null) {
			recorder.stop();
			recorder.release();
			recorder=null;
		}
	}



	public double getAmplitude() {
		if (recorder != null)
			return (recorder.getMaxAmplitude() / 2700.0);
		else
			return 0;

	}

}
