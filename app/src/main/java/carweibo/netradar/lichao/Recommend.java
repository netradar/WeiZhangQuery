package carweibo.netradar.lichao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

public class Recommend extends Activity {


	ListView lv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.recommend_layout);
		lv=(ListView)findViewById(R.id.ad_listview);
	/*	DiyManager.showRecommendAppWall(this); */
/*		list = DiyManager.getAdList(this);
		

		if(list==null) 
		{
			Toast.makeText(this, "网络状况有问题，无法获到推荐信息～", Toast.LENGTH_LONG).show();
			return;
		}
			
		for(AdObject a:list)
		{
			Log.d("lichao",a.getAppName());
		}
		
		SimpleAdapter adapter=getAdapter(list);
		
		adapter.setViewBinder(new ViewBinder() { 
				public boolean setViewValue(View view, Object data,String textRepresentation)
				{  
					if(view instanceof ImageView && data instanceof Bitmap)
					{   
						ImageView iv = (ImageView) view;  
						iv.setImageBitmap((Bitmap) data);   
						return true;   
						}
					else   
						return false;   
					}});
		lv.setAdapter(adapter);
*/
	}
/*	
	private SimpleAdapter getAdapter(List<AdObject> l)
	{
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
    	for(AdObject a:l)
    	{
    		Map<String,Object> map=new HashMap<String,Object>();

				map.put("name",a.getAppName());
	    		map.put("description", a.getAdText());
	    		map.put("icon",a.getIcon());

    	 

			list.add(map);
		}
		
		return new SimpleAdapter(this,list,R.layout.ad_item_layout,new String[]{"name","description","icon"},new int[]{R.id.ad_name,R.id.ad_description,R.id.adicon});
	
		
	}

	public void onDownload(View v)
	{
		
		int i;
		for(i=0;i<lv.getCount();i++)
			{
				if(v==lv.getChildAt(i).findViewById(R.id.download))
					{
					Log.d("lichao","id is "+i);
					break;
					}
			}
		
		String appName=((TextView)(lv.getChildAt(i).findViewById(R.id.ad_name))).getText().toString();
		DiyManager.downloadAd(this, list.get(nameToId(appName)).getAdId());
		
		Log.d("lichao",list.get((nameToId(appName))).getPackageName());
	}

	public void onClickAd(View v)
	{
		String appName=((TextView)(v.findViewById(R.id.ad_name))).getText().toString();
	}

	private int nameToId(String appName)
	{
		int i=0;
		for(AdObject a:list)
		{
			
			if(a.getAppName().equals(appName))
				{
					
					return i;
				}
			i++;
		}
		return -1;
	}*/
}
