package com.example.elvis.reportviolation.Activity;
/**
 * Elvis Gu, May 2018
 */

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.example.elvis.reportviolation.R;
import com.example.elvis.reportviolation.bean.ViolationCase;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * deal with the police map data
 * */
public class TransferData extends AppCompatActivity {
    private List<ViolationCase> violationCases;
    private List<LatLng> gpsCases;
    List<Integer> casesNum;
    private static final  double EARTH_RADIUS = 6378137;//赤道半径
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                        violationCases = (List<ViolationCase>) msg.obj;
                    break;
            }

        }
    };
    private Handler handler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    casesNum = (List<Integer>) msg.obj;
                    break;
            }

        }
    };
    private Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2:
                    gpsCases =(List<LatLng>) msg.obj;
                    Intent intent = new Intent(TransferData.this,PoliceMap.class);
                    intent.putExtra("plateNumViolation",reporterViolationtoStringArray(violationCases));
                    intent.putIntegerArrayListExtra("casesNumber",casesNumtoIntArray(casesNum));
                    intent.putExtra("gpsCasesLon",gpsCasesToLon(gpsCases));
                    intent.putExtra("gpsCasesLat",gpsCasesToLat(gpsCases));
//                    Toast.makeText(TransferData.this,"casesNum1double[1]：是" +gpsCasesToLat(gpsCases)[1]+"     casesNum1：是  " +gpsCases.get(1).latitude,Toast.LENGTH_LONG).show();
                    startActivity(intent);
//                    finish();
                    break;
            }

        }
    };
    private static double rad(double d){
        return d * Math.PI / 180.0;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_send_app_user);
        loadData();

    }



    /*
     * Use Bmob api(findobjects function) to search and delete the same plate number.
     * Then put all nonredundant plate numbers in List list and store the report number of each violation in List countReport.
     * And use message to pass these two lists out.
     */
    public void loadData() {
        BmobQuery<ViolationCase> query = new BmobQuery<ViolationCase>();
        query.addWhereEqualTo("isDealt", false);
        query.findObjects(new FindListener<ViolationCase>() {

            @Override
            public void done(List<ViolationCase> list, BmobException e) {
                if(e==null){
                    Toast.makeText(TransferData.this,"查询成功：共" +list.size() + "条数据。",Toast.LENGTH_LONG).show();
                    List<Integer> countReport = new ArrayList<>();
                    List<LatLng> reportMiddleLoc = new ArrayList<>();

                    List<ViolationCase> delTrueList = list;

                    for(int i = 0; i < list.size() ; i++){
                        countReport.add(1);
                        Log.i("gpsll","当i= "+i+" 时list.get(i).getLLlocation().getLatitude()："+list.get(i).getLLlocation().getLatitude());
                        reportMiddleLoc.add(new LatLng(list.get(i).getLLlocation().getLatitude(),list.get(i).getLLlocation().getLongitude()));
                        Log.i("gpsll","这里当index="+i+"时，设置reportMiddleLoc是 "+list.get(i).getLLlocation().getLatitude()+"  "+list.get(i).getLLlocation().getLongitude());
                        int k = 1;
                        double sumlat = list.get(i).getLLlocation().getLatitude();
                        double sumlng = list.get(i).getLLlocation().getLongitude();
                        Log.i("gpsll","sumlng在这里当index="+i+"时是"+sumlng);
                        for(int j = list.size() - 1; j > i; j-- ){
                            if  (list.get(j).getPlateNumber().equals(list.get(i).getPlateNumber()))  {
                                k = k+1;
                                countReport.set(i,k);
                                sumlat = sumlat + list.get(j).getLLlocation().getLatitude();
                                sumlng = sumlng + list.get(j).getLLlocation().getLongitude();
                                Log.i("gpsll","list.remove(j) 成功"+list.get(j).getObjectId());
                                list.remove(j);
                                Log.i("gpsll","list.remove(j) 成功");
                                Log.i("gpsll","这里当index="+i+"和K= "+k+"时，设置数值");
                            }
                        }
                        reportMiddleLoc.set(i,new LatLng(sumlat/countReport.get(i),sumlng/countReport.get(i)));



                    }
                    Log.i("gpsll","去重后list"+list.size());
                    Log.i("gpsll",list.get(0).getPlateNumber()+ "  "+reportMiddleLoc.get(0).longitude);

                    Message message = handler.obtainMessage();
                    Message message1 = handler.obtainMessage();
                    Message message2 = handler.obtainMessage();
                    message.what = 0;
                    message1.what = 1;
                    message2.what = 2;
                    //以消息为载体
                    message.obj = list;//这里的list就是查询出list
                    message1.obj = countReport;
                    message2.obj = reportMiddleLoc;
                    //向handler发送消息
                    handler.sendMessage(message);
                    handler1.sendMessage(message1);
                    handler2.sendMessage(message2);
                    }else{
                    Log.i("233333","失败了："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });

}





    private String[] reporterViolationtoStringArray(List<ViolationCase> listRV){

        List<String> list = new ArrayList<>();
//        Toast.makeText(TransferData.this,"listRV" +listRV.size() + "条数据。",Toast.LENGTH_LONG).show();
        for(int i = 0; i < listRV.size(); i++) {
            list.add(listRV.get(i).getPlateNumber());
        }
//        Toast.makeText(TransferData.this,"list" +list.size() + "条数据。",Toast.LENGTH_LONG).show();
        String[] toBeStored = list.toArray(new String[list.size()]);
        return toBeStored;
    }

    private ArrayList<Integer> casesNumtoIntArray(List<Integer> listIn){
        ArrayList<Integer> list = new ArrayList<>();
        for(int i = 0; i < listIn.size(); i++) {
            list.add(listIn.get(i));
        }
        return list;
    }

    private double[] gpsCasesToLon(List<LatLng> listGpsCases){
        double[] toBedoubleGpsLon = new double[listGpsCases.size()];
        for(int i = 0; i < listGpsCases.size(); i++) {
           toBedoubleGpsLon[i] = listGpsCases.get(i).longitude;
        }
        return toBedoubleGpsLon;
    }

    private double[] gpsCasesToLat(List<LatLng> listGpsCases){
        double[] toBedoubleGpsLat = new double[listGpsCases.size()];
        for(int i = 0; i < listGpsCases.size(); i++) {
            toBedoubleGpsLat[i] = listGpsCases.get(i).latitude;
        }
        return toBedoubleGpsLat;
    }

    public static double GetDistance(double lon1,double lat1,double lon2, double lat2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lon1) - rad(lon2);
        double s = 2 *Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2)+Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
        s = s * EARTH_RADIUS;
        return s;//meter
    }

}

