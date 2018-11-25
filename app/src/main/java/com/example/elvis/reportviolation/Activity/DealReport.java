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
import android.view.View;
import android.widget.Toast;

import com.example.elvis.reportviolation.R;
import com.example.elvis.reportviolation.bean.ViolationCase;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import github.ishaan.buttonprogressbar.ButtonProgressBar;

public class DealReport extends AppCompatActivity {

    private List<ViolationCase> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_report);
        Intent intent = getIntent();
        searchSameReport(intent.getStringExtra("bean"));

        final ButtonProgressBar bar = (ButtonProgressBar) findViewById(R.id.bpb_main);
        bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.startLoader();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        for(int i=0;i<list.size();i++){
                            ViolationCase dealReport = new ViolationCase();
                            dealReport.setDealt(true);
                            final int finalI = i;
                            dealReport.update(list.get(i).getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if(e==null){
                                        Toast.makeText(DealReport.this,"dealt with violation successfully",Toast.LENGTH_LONG).show();
                                        Log.i("bmobUpdate",list.get(finalI).getObjectId()+" 更新成功");
                                    }else{
                                        Log.i("bmobUpdate",list.get(finalI).getObjectId()+" 更新失败："+e.getMessage()+","+e.getErrorCode());
                                    }
                                }
                            });
                        }

                        bar.stopLoader();
                    }
                }, 3000);
            }
        });
    }


    public void searchSameReport(String plateNum) {
        BmobQuery<ViolationCase> query = new BmobQuery<ViolationCase>();
        query.addWhereEqualTo("plateNumber", plateNum);//return 10 reports
        query.findObjects(new FindListener<ViolationCase>() {
            @Override
            public void done(List<ViolationCase> list, BmobException e) {
                if (e == null) {
                    Message message = handler.obtainMessage();
                    message.what = 0;
                    //以消息为载体
                    message.obj = list;//searched list
                    //向handler发送消息
                    handler.sendMessage(message);

                } else {
                    Log.e("bmob", "" + e);
                }
            }
        });

    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    list = (List<ViolationCase>) msg.obj;
                    break;
            }

        }
    };


    public void policeDealBackToTrans(View view) {
        Intent intentReturn = new Intent(DealReport.this,TransferData.class);
        startActivity(intentReturn);
        finish();
    }
}