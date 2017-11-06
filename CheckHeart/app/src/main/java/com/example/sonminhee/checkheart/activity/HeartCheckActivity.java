package com.example.sonminhee.checkheart.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.sonminhee.checkheart.util.EcallDialog;
import com.example.sonminhee.checkheart.util.GraphView;
import com.example.sonminhee.checkheart.util.MyLocationListener;
import com.example.sonminhee.checkheart.R;

public class HeartCheckActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "HeartCheckActivity";

    Button btnAudioActivity;
    GraphView graphView;

    public static Context mContext;

    //areum
    private static final int RQ_ACCESS_FINE_LOCATION = 1;
    private static final int RQ_ACCESS_COARSE_LOCATION = 2;
    private static final int RQ_CALL_PHONE = 3;

    public EcallDialog mCustomDialog;
    public LocationManager locationManager;
    public Location location;
    public EditText txtLatitude;
    public EditText txtLongitude;
    public Intent intent_dial;
    public MyLocationListener locListenD;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_check);

        init();
        mContext = this;

        // TODO :: 유단비
        int[] points = {5, 3, 7, 8, 4, 3, 3, 6, 4, 1};
        graphView.setPoints(points, 1, 0, 10);
        graphView.drawForBeforeDrawView();

        checkAuthority();
        //setGPS(location);
    }

    private void init() {
        btn = (Button) findViewById(R.id.main_popup);
        btn.setOnClickListener(this);

        /** (GPS) 위치 받아오기 */
        txtLatitude = (EditText) findViewById(R.id.et_Latitude);
        txtLongitude = (EditText) findViewById(R.id.et_Longitude);

        btnAudioActivity = (Button) findViewById(R.id.btn_AudioActivity);
        btnAudioActivity.setOnClickListener(this);
        graphView = (GraphView) findViewById(R.id.GraphView);

    }

    // TODO :: 권한 체크 메소드 분리
    private void checkAuthority() {
        // OS가 M 이상일 경우 권한체크
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //ACCESS_FINE_LOCATION & ACCESS_COARSE_LOCATION권한 체크
           if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                   ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                   ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
               ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE}, 1000);
            }else{
               // 권한 존재
           }

        } else {
            // OS가 M 이전일 경우
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        /**
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
            // ACCESS_FINE_LOCATION & ACCESS_COARSE_LOCATION권한 획득
            setGPS(location);
        }
        if(grantResults[2] == PackageManager.PERMISSION_GRANTED){
            // CALL_PHONE 권한 획득
        }*/
    }

    @Override
    public void onClick(View view) {
        Log.i(TAG, "TEST ONCLICKED");
        switch (view.getId()) {
            case R.id.btn_AudioActivity:
                Log.i(TAG, "TEST :: ");
                Intent intent = new Intent(view.getContext(), AudioPlayerActivity.class);
                startActivity(intent);
                break;
            case R.id.main_popup:
                mCustomDialog = new EcallDialog(this,
                        "e-call서비스 호출",
                        "심박수에 이상이 있습니다. \n e-call 서비스를 호출하겠습니까? \n");
                mCustomDialog.show();
                break;
        }
    }

    /**
     * 전화하기 ecall service
     */
    // TODO :: EcallDialog는 Activity가 아니라 startActivity 사용불가 -> 분리 불가
    public void openDial() {
        intent_dial = new Intent(Intent.ACTION_CALL, Uri.parse("tel:01093493932"));
        startActivity(intent_dial);

        String SMSText = "Latitude : " + txtLatitude.getText() + "\nLongitude : " + txtLongitude.getText();
        /**
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage("01093493932", null, SMSText, null, null);
        mCustomDialog.dismiss();*/
    }

    /**
     * gps 값 받아오기
     */
    public void setGPS(Location l) {

        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                txtLatitude.setText("DISABLED GPS");
                txtLongitude.setText("DISABLED GPS");
            } else {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    txtLatitude.setText(Double.toString(location.getLatitude()));
                    txtLongitude.setText(Double.toString(location.getLongitude()));
                }else{
                    locListenD = new MyLocationListener();
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 0.5f, locListenD);
                    //txtLatitude.setText(Double.toString(l.getLatitude()));
                    //txtLongitude.setText(Double.toString(l.getLongitude()));
                }
            }

        } catch (SecurityException e) {
            // lets the user know there is a problem with the gps
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RQ_ACCESS_FINE_LOCATION);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, RQ_ACCESS_COARSE_LOCATION);
            Log.d("GPS :: ", "disable");
        }
    }

}
