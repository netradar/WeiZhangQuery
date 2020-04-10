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
	Bitmap 	bmp_src;//ͼƬ��Դ

	Point 	posInBmp;//��ͼƬԴ�е��ĸ�λ��
	
	Point	posInBackground;//�ڱ����е�λ��
	int 	widthInBmp;//��ͼƬԴ�еĿ��
	int 	heightInBmp;//��ͼƬԴ�еĸ߶�
	
	Point 	posInScreen;//��ʾ��λ��
	int 	widthInScreen;//��ʾ�Ŀ��
	int 	heightInScreen;//��ʾ�ĸ߶�
	
	
	/*int alpha;
	int alpha_per_second;*/

}
