package carweibo.netradar.lichao;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class GalleryViewCache {
	
	private View baseView;
    private TextView progress;
    private ZoomImageView imgView;
    private ImageView failureView;
    
    
    

	public GalleryViewCache(View baseView) {
    	
        this.baseView = baseView;
    }
    
	public ImageView getFailureView() {
		
		if(failureView==null)
		{
			failureView=(ImageView)baseView.findViewById(R.id.gallery_item_failuer);
		}
		return failureView;
	}

	public TextView getProgress() {
		if(progress==null)
		{
			progress=(TextView)baseView.findViewById(R.id.gallery_item_progress);
		}
		return progress;
	}
	public ZoomImageView getImgView() {
		
		if(imgView==null)
		{
			imgView=(ZoomImageView)baseView.findViewById(R.id.gallery_item_imageView);
		}
		return imgView;
	}
	
	public void setProgress(TextView progress) {
		this.progress = progress;
	}
	public void setImgView(ZoomImageView imgView) {
		this.imgView = imgView;
	}
    
    

}
