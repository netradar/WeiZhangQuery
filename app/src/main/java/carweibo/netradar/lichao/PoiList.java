package carweibo.netradar.lichao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.Query;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class PoiList extends Activity implements AMapLocationListener, OnPoiSearchListener, OnItemClickListener {

	TextView cur_location,title,no_result;
	
	ProgressBar progressBar;
	String poi; 
    private LocationManagerProxy mAMapLocationManager; 
    
    ListView poi_list;
    List<HashMap<String, Object>> data;
    SimpleAdapter adapter;
    ImageView icon_poi;
    LinearLayout footView,getting_more_layout;
    Button get_more_btn;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.poi_list_layout);
		
		cur_location=(TextView)findViewById(R.id.poi_list_location);
		progressBar=(ProgressBar)findViewById(R.id.poi_list_progressBar);
		title=(TextView)findViewById(R.id.poi_list_title);
		icon_poi=(ImageView)findViewById(R.id.poi_item_icon);
		no_result=(TextView)findViewById(R.id.poi_list_no_result);
		footView=(LinearLayout)LayoutInflater.from(this).inflate(R.layout.foot, null);
		
	//	icon_poi.setBackgroundResource(R.drawable.qq_normal);
		poi=this.getIntent().getStringExtra("title");
		title.setText("附近"+poi);
		
		poi_list=(ListView)findViewById(R.id.poi_list_listview);
		poi_list.addFooterView(footView);
		//footView.setVisibility(View.GONE);
		getting_more_layout=(LinearLayout) footView.findViewById(R.id.getting_weibo);
		get_more_btn=(Button)footView.findViewById(R.id.moreweibo);
		
		data=new ArrayList<HashMap<String,Object>>();
		String[] from={"name","position","distinct"};
		int[] to={R.id.poi_item_name,R.id.poi_item_pos,R.id.poi_item_distinct};
		adapter=new SimpleAdapter(this, data,R.layout.poi_item_layout, from, to);
		
		poi_list.setAdapter(adapter);
        mAMapLocationManager = LocationManagerProxy.getInstance(this);   
       
        mAMapLocationManager.requestLocationUpdates(   
                LocationProviderProxy.AMapNetwork, 5000, 10, this);   
          
        
        poi_list.setOnItemClickListener(this);
	}
	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	int current_page=0;
	LatLonPoint lp=null;
	
	@Override
	public void onLocationChanged(AMapLocation location) {
		
		Bundle locBundle = location.getExtras();
		
		String desc=null;
		
		if (locBundle != null) {
		
			desc = locBundle.getString("desc");
			
		}
		
		if(desc!=null)
		{
			cur_location.setText(desc);
			
					
			startQueryNear(location);
		}
		else
		{
			cur_location.setText("无法定位当前位置！");
			startQueryNear(null);
		}
		mAMapLocationManager.destory();
		
	}
	
	Query query;
	PoiSearch poiSearch;
	private void startQueryNear( AMapLocation location) {
		
		
		
		if(location!=null)
			query= new PoiSearch.Query(poi, "", location.getCityCode());
		else
			query=new PoiSearch.Query(poi, "", "029");
		query.setPageSize(10);// 设置每页最多返回多少条poiitem
		query.setPageNum(current_page);// 设置查第一页
		
		poiSearch = new PoiSearch(this, query);
		poiSearch.setOnPoiSearchListener(this);
		
		if(location!=null)
		{
			lp=new LatLonPoint(location.getLatitude(),location.getLongitude());//设置搜索区域为以lp点为圆心，其周围1000米范围
			
			poiSearch.setBound(new SearchBound(lp, 1000*5));
		}
		poiSearch.searchPOIAsyn();//异步搜索
	}
	@Override
	public void onPoiItemDetailSearched(PoiItemDetail arg0, int arg1) {
		
		
	}
	@Override
	public void onPoiSearched(PoiResult result, int code) {
		
		progressBar.setVisibility(View.GONE);
		if(code==0)
		{
			if (result != null && result.getQuery() != null) 
			{
				List<PoiItem> poiItems = result.getPois();
				
				
				
				if(poiItems.size()<=0)
				{
					showNoResult();
					return;
				}
				for(PoiItem poi:poiItems)
				{
					HashMap<String,Object> map=new HashMap<String,Object>();
					map.put("name", poi.getTitle());
					map.put("position", poi.getSnippet());
					map.put("distinct", String.valueOf(poi.getDistance())+"米");
					map.put("latLon", poi.getLatLonPoint());
					
					data.add(map);
				}
				adapter.notifyDataSetChanged();
				
				if(current_page<(result.getPageCount()-1))
				{
					footView.setVisibility(View.VISIBLE);
					get_more_btn.setVisibility(View.VISIBLE);
					getting_more_layout.setVisibility(View.GONE);
				}
				else
				{
					footView.setVisibility(View.GONE);
					get_more_btn.setVisibility(View.GONE);
					getting_more_layout.setVisibility(View.GONE);
				}
				
			}
		}
		else
		{
			showNoResult();
		}
	}
	private void showNoResult() {
		
		poi_list.setVisibility(View.GONE);
		no_result.setVisibility(View.VISIBLE);
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		Intent i=new Intent();
		i.setClass(PoiList.this, PoiDetailMap.class);
		
		if(lp!=null)
		{
			i.putExtra("myLat", lp.getLatitude());
			i.putExtra("myLon", lp.getLongitude());
		}
		else
		{
			i.putExtra("myLat", 0);
			i.putExtra("myLon", 0);
		}
		
		LatLonPoint dest=(LatLonPoint) data.get(arg2).get("latLon");
		i.putExtra("destLat", dest.getLatitude());
		i.putExtra("destLon", dest.getLongitude());
		i.putExtra("title", (String)data.get(arg2).get("name"));
		
		
		this.startActivity(i);
		overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
		
	}

	public void onCancelPoiList(View v)
	{
		onBackPressed();
	}
	@Override
	public void onBackPressed() {
		this.finish();
		overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
		
	}
	
	public void onMoreWeibo(View v)
	{
		get_more_btn.setVisibility(View.GONE);
		getting_more_layout.setVisibility(View.VISIBLE);
		
		current_page++;
		query.setPageNum(current_page);
		poiSearch.searchPOIAsyn();
	}
}
