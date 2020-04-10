package carweibo.netradar.lichao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MySimpleAdapter extends SimpleAdapter {

	Context con;
	int size;
	public MySimpleAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		
		super(context, data, resource, from, to);
		con=context;
		size=data.size();
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		View rowView=convertView;
		ViewCache viewCache;
		if(rowView==null)
		{
			LayoutInflater inflater = ((Activity) con).getLayoutInflater();
            rowView = inflater.inflate(R.layout.car_item_layout, null);
            viewCache = new ViewCache(rowView);
            rowView.setTag(viewCache);
		}
		else
			viewCache=(ViewCache) rowView.getTag();
		
		HashMap<String,String> map=(HashMap<String,String>)getItem(position);
		
		TextView tv=(TextView)viewCache.getOwner();
		tv.setText(map.get("owner"));
		TextView tv1=(TextView)viewCache.getNum();
		tv1.setText(map.get("num"));
		
		if(size==1)
			rowView.setBackgroundResource(R.drawable.prefrence_single_item);
		else if(position==0)
			rowView.setBackgroundResource(R.drawable.preference_first_item);
		else if(position==(size-1))
			rowView.setBackgroundResource(R.drawable.preference_last_item);
		else
			rowView.setBackgroundResource(R.drawable.preference_item);
			
		return rowView;
			
		//return super.getView(position, convertView, parent);
	}

	

	

}
