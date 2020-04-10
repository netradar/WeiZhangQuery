package carweibo.netradar.lichao;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class PoiDetailMap extends Activity implements InfoWindowAdapter, OnMapLoadedListener {

	private MapView mapView;
	private AMap aMap;
	TextView title;
	LatLng my_point,dest_point;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.poi_detail_map_layout);
		title=(TextView)findViewById(R.id.poi_detail_map_title);
		
		title.setText(this.getIntent().getStringExtra("title"));
		
		mapView = (MapView) findViewById(R.id.poi_detail_map);
		mapView.onCreate(savedInstanceState);
		if (aMap == null) {
			aMap = mapView.getMap();
		}
		
		Double myLat,myLon,destLat,destLon;
		
		myLat=this.getIntent().getDoubleExtra("myLat", 0);
		myLon=this.getIntent().getDoubleExtra("myLon", 0);
		destLat=this.getIntent().getDoubleExtra("destLat", 0);
		destLon=this.getIntent().getDoubleExtra("destLon", 0);
		
		if(myLat==0) 
			my_point=null;
		else
		{
			my_point=new LatLng(myLat,myLon);
		}
		
		if(destLat==0&&destLon==0) 
			dest_point=null;
		else
		{
			dest_point=new LatLng(destLat,destLon);
			
		}
		
	//	CameraUpdate cu=CameraUpdateFactory.newLatLngZoom(dest_point, 19);
	//	aMap.setTrafficEnabled(false);
		
	//	aMap.moveCamera(cu);
		initMap();
	}
	private void initMap() 
	{
		aMap.setInfoWindowAdapter(this);
		aMap.setOnMapLoadedListener(this);
		if(my_point!=null)
		{
			
			
			Marker aMarker=aMap.addMarker(new MarkerOptions().title("Œ“µƒŒª÷√").position(my_point).icon(BitmapDescriptorFactory.fromResource(R.drawable.my_location)));
			aMarker.showInfoWindow();
		}
	
		if(dest_point!=null)
		{
			Marker aMarker1=aMap.addMarker(new MarkerOptions().title(title.getText().toString()).position(dest_point).icon(BitmapDescriptorFactory.fromResource(R.drawable.bubble_wrongcheck)));
			aMarker1.showInfoWindow();
		}
		
	
	}
	
	public void onCancelPoiDetail(View v)
	{
		exitPoiDetail();
	}
	private void exitPoiDetail() {

		this.finish();
		overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
		
	}
	@Override
	public void onBackPressed() {
		exitPoiDetail();
		super.onBackPressed();
	}
	@Override
	public View getInfoContents(Marker marker) {
		View infoWindow = getLayoutInflater().inflate(
				R.layout.custom_info_window, null);

		TextView title=(TextView) infoWindow.findViewById(R.id.custom_info_window_title);
		title.setText(marker.getTitle());
		return infoWindow;
	}
	@Override
	public View getInfoWindow(Marker marker) {
		
		View infoWindow = getLayoutInflater().inflate(
				R.layout.custom_info_window, null);

		TextView title=(TextView) infoWindow.findViewById(R.id.custom_info_window_title);
		title.setText(marker.getTitle());
		return infoWindow;

	}
	@Override
	public void onMapLoaded() {
		LatLngBounds bounds;
		if(my_point==null)
			bounds=new LatLngBounds.Builder().include(dest_point).build();
		else
			bounds=new LatLngBounds.Builder().include(my_point).include(dest_point).build();
		aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
		
	}

	
}
