package com.example.elvis.reportviolation.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.example.elvis.reportviolation.R;

public class HeatMap extends AppCompatActivity {

    private MapView mMapView = null;
    private BaiduMap mMap;
    private ImageView backUserMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_heat_map);
        // 获取地图控件引用
        mMapView = (MapView) findViewById(R.id.id_bmapHeatView);
        mMap = mMapView.getMap();

        mMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(false);

        //开启热力图
        mMap.setBaiduHeatMapEnabled(true);

        backUserMap = (ImageView) findViewById(R.id.heat_map_back_user_info);
        backUserMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
