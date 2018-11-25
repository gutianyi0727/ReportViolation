package com.example.elvis.reportviolation.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.bumptech.glide.Glide;
import com.example.elvis.reportviolation.R;
import com.example.elvis.reportviolation.Service.RecognizeService;
import com.example.elvis.reportviolation.bean.MyUser;
import com.example.elvis.reportviolation.bean.ViolationCase;
import com.example.elvis.reportviolation.util.FileUtil;
import com.example.elvis.reportviolation.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ReportActivity extends BaseActivity {

    private static final int REQUEST_CODE_LICENSE_PLATE = 122;

    private boolean hasGotToken = false;

    private ImageButton backToMain;

    private MyUser mMyUser;
    private ImageView pic;
    private TextView reporter_name;

    private String reporterID;
    private double Lat;
    private double Lon;
    private String number;
    private String detailedLoc;

    private AlertDialog.Builder alertDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_violation);
        //print out location which is from last activity's intent
        Intent intent = getIntent();
        TextView locationText =(TextView)findViewById(R.id.violation_case_location);
        detailedLoc = intent.getStringExtra("location_data");
        locationText.setText(detailedLoc);

        mMyUser = BmobUser.getCurrentUser(MyUser.class);
        reporterID = mMyUser.getObjectId();
        Lat = intent.getDoubleExtra("locationLatitude",0.000000000000);
        Lon = intent.getDoubleExtra("locationLongitude",0.000000000000);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.twoChoose);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        alertDialog = new AlertDialog.Builder(this);

        initAccessTokenWithAkSk();

        backToMain = (ImageButton) findViewById(R.id.back_main_user_info);
        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pic = (ImageView) findViewById(R.id.image_user_avatar);
        reporter_name = (TextView) findViewById(R.id.reporter_name);
        reporter_name.setText(mMyUser.getName());
        Glide.with(this).load(mMyUser.getAvatar().getUrl()).bitmapTransform(new CropCircleTransformation(this)).crossFade(1000).into(pic);
    }


    //The following code is from Baidu OCR Demo
    private boolean checkTokenStatus() {
        if (!hasGotToken) {
            Toast.makeText(getApplicationContext(), "token还未成功获取", Toast.LENGTH_LONG).show();
        }
        return hasGotToken;
    }

    private void initAccessToken() {
        OCR.getInstance().initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken accessToken) {
                String token = accessToken.getAccessToken();
                hasGotToken = true;
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                alertText("licence方式获取token失败", error.getMessage());
            }
        }, getApplicationContext());
    }

    private void initAccessTokenWithAkSk() {
        OCR.getInstance().initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                String token = result.getAccessToken();
                hasGotToken = true;
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                alertText("AK，SK方式获取token失败", error.getMessage());
            }
        }, getApplicationContext(),"TSHVuO4sEjGcYz8G69O1T6wI", "64bNwkKwPvGlO1SsmgrFPYAPzplOpeIi");
    }

    private void alertText(final String title, final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("sure", null)
                        .show();
            }
        });
    }


    //ensure the phone's permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initAccessToken();
        } else {
            Toast.makeText(getApplicationContext(), "需要android.permission.READ_PHONE_STATE", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Identify successful and deal with callbacks.
        if (requestCode == REQUEST_CODE_LICENSE_PLATE && resultCode == Activity.RESULT_OK) {
            RecognizeService.recLicensePlate(FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new RecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result){
                            try {
                                JSONObject jObject = new JSONObject(result);
                                number  = (String)jObject.getJSONObject("words_result").get("number");
                                TextView lblTitle=(TextView)findViewById(R.id.case_plate_number);
                                lblTitle.setText(number);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }
    }
    //End of the code
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放内存资源
        OCR.getInstance().release();
    }



    //cases click the BottomNavigation burron
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                //click button to take photo
                case R.id.navigation_photo:
                    Intent intent = new Intent(ReportActivity.this, CameraActivity.class);
                    intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                            FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                    intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                            CameraActivity.CONTENT_TYPE_GENERAL);
                    startActivityForResult(intent, REQUEST_CODE_LICENSE_PLATE);
                    return true;

                //click button to send the report
                case R.id.navigation_report:
                    BmobGeoPoint point = new BmobGeoPoint(Lon,Lat);
                    ViolationCase testReport = new ViolationCase();
                    testReport.setLLlocation(point);
                    testReport.setDelatedLoc(detailedLoc);
                    testReport.setPlateNumber(number);
                    testReport.setReporter(BmobUser.getCurrentUser(MyUser.class));
                    testReport.setDealt(false);
                    EditText ET=(EditText) findViewById(R.id.violation_case_type);
                    testReport.setReportTitleandType(ET.getText().toString());
                    testReport.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e==null){
                                ToastUtil.showText(ReportActivity.this,"success Send");
                            }else{
                                ToastUtil.showText(ReportActivity.this,"创建数据失败：" + e.getMessage());
                            }
                        }
                    });
                    return true;
            }
            return false;
        }

    };
}
