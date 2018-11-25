package com.example.elvis.reportviolation.Activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.elvis.reportviolation.R;

import java.util.ArrayList;
import java.util.List;

public class ReporterMapActivity extends AppCompatActivity {
    public LocationClient mLocationClient;

    private TextView positionText;
    private MapView mapView;
    private BaiduMap baiduMap;
    private boolean isFirstLocate = true;
    private String locationData;
    private double locationLatitude;
    private double locationLongitude;
    private TextView mTextMessage;
    private String theReporterID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMap();

    }

    public void setMap(){
        mLocationClient= new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        setContentView(R.layout.activity_reporter_map);

        Intent getIntent = getIntent();
        theReporterID = getIntent.getStringExtra("useID");
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //set up map
        mapView = (MapView) findViewById(R.id.reportMap);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(ReporterMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(ReporterMapActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(ReporterMapActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissionList.isEmpty()){
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(ReporterMapActivity.this,permissions,1);
        }else{
            requestLocation();
        }
    }

    //set two cases button value.
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_dashboard:
                    Intent intentSendLocation = new Intent(ReporterMapActivity.this,ReportActivity.class);
                    intentSendLocation.putExtra("locationLatitude",locationLatitude);
                    intentSendLocation.putExtra("locationLongitude",locationLongitude);
                    intentSendLocation.putExtra("location_data",locationData);
                    startActivity(intentSendLocation);
                    return true;
            }
            return false;
        }

    };

    //move the map to location
    private void navigateTo(BDLocation location){
        LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(ll,16);
        baiduMap.animateMapStatus(update);
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);


    }
    private  void requestLocation(){
        initLocation();
        mLocationClient.start();
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }


    @Override
    protected  void onResume(){
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected  void onPause(){
        super.onPause();
        mapView.onPause();
    }

    protected  void  onDestory(){
        super.onDestroy();
        mLocationClient.stop();
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
    }



    public void onRequestPermissionResult(int requestcode,String[] permissions,int[] grantResults){
        switch (requestcode){
            case 1:
                if (grantResults.length > 0 ){
                    for (int result : grantResults){
                        if (result !=PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this,"必须同意所有权限",Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                }else {
                    Toast.makeText(this,"未知error",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    public void reportMapBackToUserInfor(View view) {
        finish();
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public  void onReceiveLocation(BDLocation location) {
            StringBuilder currentPosition = new StringBuilder();
            currentPosition.append("").append(location.getCountry()).append(location.getProvince()).append(location.getCity()).append(location.getDistrict()).append(location.getStreet());
            locationLongitude = location.getLongitude();
            locationLatitude = location.getLatitude();
            locationData = currentPosition.toString();
            navigateTo(location);
        }
    }

}
