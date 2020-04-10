package carweibo.netradar.lichao;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewCache {
	private View baseView;
	
	private TextView num;
	private TextView owner;
	private ImageView car_icon;
	private ImageView submmit_icon;
	
	public ViewCache(View v)
	{
		this.baseView=v;
	}

	public TextView getNum() {
		if(num==null)
		{
			num=(TextView)baseView.findViewById(R.id.car_num);
		}
		return num;
	}
	public TextView getOwner() {
		if(owner==null)
		{
			owner=(TextView)baseView.findViewById(R.id.car_owner1);
		}
		return owner;
	}
	public ImageView getCar_icon() {
		if(car_icon==null)
		{
			car_icon=(ImageView)baseView.findViewById(R.id.caricon);
		}
		return car_icon;
	}
	public ImageView getSubmmit_icon() {
		if(submmit_icon==null)
		{
			submmit_icon=(ImageView)baseView.findViewById(R.id.submiticon);
		}
		return submmit_icon;
	}

	
	
	

}
