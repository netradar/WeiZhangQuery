package carweibo.netradar.lichao;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;


import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

public class LuKuang extends Activity implements LocationSource, AMapLocationListener {
	private MapView mapView;
	private AMap aMap;
	private OnLocationChangedListener mListener;   
    private LocationManagerProxy mAMapLocationManager;  
    
    private RelativeLayout search;
    boolean isSearch=true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.lukuang_layout);
		search=(RelativeLayout)findViewById(R.id.searchHeadLayout);
		//search.getBackground().setAlpha(50);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);
		if (aMap == null) {
			aMap = mapView.getMap();
		}
		
		CameraUpdate cu=CameraUpdateFactory.newLatLngZoom(new LatLng(34.259416,108.94713), 13);
		aMap.setTrafficEnabled(true);
		
		aMap.moveCamera(cu);
		
		setUpMap();
		
		
	}
	
    private void setUpMap() {   
        // �Զ���ϵͳ��λС����   
        MyLocationStyle myLocationStyle = new MyLocationStyle();   
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory   
                .fromResource(R.drawable.location_marker));   
        myLocationStyle.strokeColor(Color.GREEN);   
        myLocationStyle.radiusFillColor(Color.TRANSPARENT);
        myLocationStyle.strokeWidth(0);   
        aMap.setMyLocationStyle(myLocationStyle);   
        // ���ö�λ��Դ   
        aMap.setLocationSource(this);   
        // ����Ϊtrue��ʾϵͳ��λ��ť��ʾ����Ӧ�����false��ʾ���أ�Ĭ����false   
        aMap.setMyLocationEnabled(true);   
    }  
    
    public void onSearch(View v)
    {
    	
    	if(isSearch)
    		search.setVisibility(View.VISIBLE);
    	else
    		search.setVisibility(View.GONE);
    	isSearch=!isSearch;
    }
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		super.onDestroy();
	//	mapView.onDestroy();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
//		mapView.onPause();
		this.deactivate();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	//	mapView.onResume();
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	//	mapView.onSaveInstanceState(outState);
	}

	@Override
	public void activate(OnLocationChangedListener listener) {
		// TODO Auto-generated method stub
		 mListener = listener;   
	        if (mAMapLocationManager == null) {   
	            mAMapLocationManager = LocationManagerProxy.getInstance(this);   
	            /*  
	             * mAMapLocManager.setGpsEnable(false);//  
	             * 1.0.2�汾��������������true��ʾ��϶�λ�а���gps��λ��false��ʾ�����綨λ��Ĭ����true  
	             */  
	            // Location SDK��λ����GPS�������϶�λ��ʽ��ʱ�������5000���룬������Ч   
	            mAMapLocationManager.requestLocationUpdates(   
	                    LocationProviderProxy.AMapNetwork, 5000, 10, this);   
	        }   

	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub
		 mListener = null;   
	        if (mAMapLocationManager != null) {   
	            mAMapLocationManager.removeUpdates(this);   
	            mAMapLocationManager.destory();   
	        }   
	        mAMapLocationManager = null;
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
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationChanged(AMapLocation aLocation) {
		// TODO Auto-generated method stub
		 if (mListener != null) {   
	            // ����λ��Ϣ��ʾ�ڵ�ͼ��   
	            mListener.onLocationChanged(aLocation);   
	        }   
	}



	
	
}
