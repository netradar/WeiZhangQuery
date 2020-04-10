package heart4.netradar.lichao;

import android.graphics.Bitmap;
import android.graphics.Point;


public class ImagePosData {
	
	
	public ImagePosData(Bitmap bmp_src,Point posInBmp,Point posInBackground,int widthInBmp,int heightInBmp,
			float rate_x,float rate_y) {
	
		this.bmp_src	 	=	bmp_src;
		this.posInBackground=	posInBackground;
		this.posInBmp 		=	posInBmp;
		this.widthInBmp 	=	widthInBmp;
		this.heightInBmp 	= 	heightInBmp;
		
		posInScreen=new Point((int)(posInBackground.x*rate_x),(int)(posInBackground.y*rate_y));
		widthInScreen=(int) (widthInBmp*rate_x);
		heightInScreen=(int) (heightInBmp*rate_y);
		
	}
	Bitmap 	bmp_src;//图片来源

	Point 	posInBmp;//在图片源中的哪个位置
	
	Point	posInBackground;//在背景中的位置
	int 	widthInBmp;//在图片源中的宽度
	int 	heightInBmp;//在图片源中的高度
	
	Point 	posInScreen;//显示的位置
	int 	widthInScreen;//显示的宽度
	int 	heightInScreen;//显示的高度
	
	
	/*int alpha;
	int alpha_per_second;*/

}
