package com.example.elvis.reportviolation.Activity;
/**
 * Elvis Gu, May 2018
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.elvis.reportviolation.R;
import com.example.elvis.reportviolation.bean.Bean;

import java.util.ArrayList;
import java.util.List;

public class PoliceMap extends AppCompatActivity {

    private MapView mapView;
    private BaiduMap baiduMap;
    private List<Bean> mapBeans;
    private LatLng setMiddle;
    private  String[] plateNumCases;
    private  ArrayList<Integer> casesNumber;
    private  double[] casesLon;
    private  double[] casesLat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_police_map);
        //get the intent data: 去重车牌 举报次数 估计的loc
        Intent intent = getIntent();
        plateNumCases = intent.getStringArrayExtra("plateNumViolation");
        casesNumber = intent.getIntegerArrayListExtra("casesNumber");
        casesLat = intent.getDoubleArrayExtra("gpsCasesLat");
        casesLon = intent.getDoubleArrayExtra("gpsCasesLon");
        mapView = (MapView) findViewById(R.id.police_map_view);
        baiduMap = mapView.getMap();

        //set the middle point of the Police map which is the middle of China
        LatLng ll = new LatLng(33.6586300000,106.2685730000);
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(ll,5.5f);
        baiduMap.animateMapStatus(update);


        initData();

        //after clicking the marker, the informationClickWindow will show up
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                // TODO Auto-generated method stub
                final Bean beans = (Bean)marker.getExtraInfo().get("BEAN");
                View markView = View.inflate(getApplicationContext(), R.layout.item_maker, null);
                TextView tv_id = (TextView)markView.findViewById(R.id.mark_id);
                TextView tv_number = (TextView)markView.findViewById(R.id.mark_platenumber);
                tv_id.setText(beans.getId()  + "");
                tv_number.setText("车  牌 号：" + beans.getNumber());
                InfoWindow.OnInfoWindowClickListener listener = null;

                //click the informationClickWindow to jump to deal with violation activity.
                listener = new InfoWindow.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick() {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent(PoliceMap.this,DealReport.class);
                        intent.putExtra("bean", beans.getNumber());
                        startActivity(intent);
                        baiduMap.hideInfoWindow();
                    }
                };
                LatLng ll = marker.getPosition();
                InfoWindow mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(markView), ll, -47, listener);
                baiduMap.showInfoWindow(mInfoWindow);
                return true;
            }
        });

    }

    private void initData() {
        // TODO Auto-generated method stub
        //设置中心点
        //添加marker
        mapBeans = new ArrayList<Bean>();
        for(int i = 0; i<casesNumber.size(); i++){
            mapBeans.add(new Bean(casesNumber.get(i), plateNumCases[i], casesLat[i], casesLon[i]));
        }
        for(Bean bean : mapBeans){
            LatLng latLng = new LatLng(bean.getLatitude(), bean.getLongitude());
            Bundle bundle = new Bundle();
            bundle.putSerializable("BEAN", bean);
            View view = View.inflate(getApplicationContext(), R.layout.item_bean, null);
            TextView tView = (TextView)view.findViewById(R.id.item_bean);
            tView.setText(bean.getId() + "");
            //transform View to Bitmap
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromView(view);
            OverlayOptions options = new MarkerOptions().position(latLng).icon(descriptor).extraInfo(bundle).zIndex(9).draggable(true);
            baiduMap.addOverlay(options);
        }
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
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
    }



    public void policeMapBackToUserInfor(View view) {
        Intent intent = new Intent(PoliceMap.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}