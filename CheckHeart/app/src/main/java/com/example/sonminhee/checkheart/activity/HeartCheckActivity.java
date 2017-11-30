package com.example.sonminhee.checkheart.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sonminhee.checkheart.R;
import com.example.sonminhee.checkheart.util.EcallDialog;
import com.example.sonminhee.checkheart.util.GraphView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class HeartCheckActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "HeartCheckActivity";
    private static final int RQ_ACCESS_FINE_LOCATION = 1;
    private static final int RQ_ACCESS_COARSE_LOCATION = 2;
    private static final int RQ_CALL_PHONE = 3;
    static final int REQUEST_ENABLE_BT = 10;


    Button btnAudioActivity;
    GraphView graphView;
    TextView txtHeartbeat;
    ImageView imgStatus;

    Handler bluetoothIn = null;

    public static Context mContext;


    public EcallDialog mCustomDialog;
    public Intent intent_dial;


    int mPariedDeviceCount = 0;
    Set<BluetoothDevice> mDevices;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mRemoteDevice;
    //BluetoothSocket mSocket = null;


    private BluetoothSocket btSocket = null;
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private ConnectedThread mConnectedThread;
    final int handlerState = 0;
    private StringBuilder recDataString = new StringBuilder();
    public static int dialog = 1;

    int[] points;

    /**
     * Normal 70~120mmHg
     * Sleepy
     * Excited
     * Fatal ~70, 120~
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_check);

        init();
        mContext = this;

        // TODO :: 유단비
        points = new int[10];
        graphView.setPoints(points, 1, 75, 15);
        graphView.drawForBeforeDrawView();

        checkAuthority();

        selectDevice();


        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                Log.i("TEST ", "TESTRTETE");
                if (msg.what == handlerState) {
                    String readMessage = (String) msg.obj;
                    recDataString.append(readMessage);
                    txtHeartbeat.setText("현재 심박수 : " + (int) readMessage.charAt(0));

                    if (points.length < 10) {
                        points[points.length] = (int) readMessage.charAt(0);
                    } else {
                        System.arraycopy(points, 1, points, 0, points.length - 1);
                        points[points.length - 1] = (int) readMessage.charAt(0);
                    }
                    graphView.setPoints(points, 1, 75, 15);
                    graphView.draw();
                    graphView.invalidate();

                }
            }
        };

        txtHeartbeat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                if (charSequence.length() >= 11 && Integer.parseInt(charSequence.subSequence(9, 11).toString()) < 70 && dialog == 1) {
                    dialog = -1;
                    Log.i("TEST", "TESTRTETE :: status fatal :: ");
                    imgStatus.setImageResource(R.drawable.ic_status_fatal);


                    mCustomDialog = new EcallDialog(HeartCheckActivity.this,
                            "e-call서비스 호출",
                            "심박수에 이상이 있습니다. \n e-call 서비스를 호출하겠습니까? \n");

                    mCustomDialog.show();


                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    /**
     * BluetoothDevice : 페어링 된 기기 목록을 얻어옴
     *
     * @param name
     * @return
     */
    BluetoothDevice getDeviceFromBondedList(String name) {
        BluetoothDevice selectedDevice = null;
        for (BluetoothDevice deivce : mDevices) {
            if (name.equals(deivce.getName())) {
                selectedDevice = deivce;
                break;
            }
        }
        return selectedDevice;
    }

    /**
     * 원격 장치와 연결하는 과정을 나타냄
     *
     * @param selectedDeviceName
     */
    void connectToSelectedDevice(String selectedDeviceName) {
        mRemoteDevice = getDeviceFromBondedList(selectedDeviceName);
        UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        try {
            btSocket = createBluetoothSocket(mRemoteDevice);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }
        try {
            btSocket = createBluetoothSocket(mRemoteDevice);
        } catch (IOException e) {
        }
        // Establish the Bluetooth socket connection.
        try {
            btSocket.connect();

        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                //insert code to deal with this
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }


    private void init() {
        imgStatus = (ImageView) findViewById(R.id.imgStatus);
        txtHeartbeat = (TextView) findViewById(R.id.main_heartRate);

        btnAudioActivity = (Button) findViewById(R.id.btn_AudioActivity);
        btnAudioActivity.setOnClickListener(this);
        graphView = (GraphView) findViewById(R.id.GraphView);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            finish();

        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {

            }
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    selectDevice();
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(getApplicationContext(), "블루투수를 사용할 수 없어 프로그램을 종료합니다", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     *
     */
    // TODO :: 권한 체크 메소드 분리
    private void checkAuthority() {
        // OS가 M 이상일 경우 권한체크
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //ACCESS_FINE_LOCATION & ACCESS_COARSE_LOCATION권한 체크
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE}, 1000);
            } else {
                // 권한 존재
            }

        } else {
            // OS가 M 이전일 경우
        }
    }


    /**
     * 블루투스 디바이스는 연결해서 사용하기 전에 먼저 페어링 되어야만 한다
     */
    void selectDevice() {
        mDevices = mBluetoothAdapter.getBondedDevices();
        mPariedDeviceCount = mDevices.size();

        if (mPariedDeviceCount == 0) {
            Toast.makeText(getApplicationContext(), "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();
            finish();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("블루투스 장치 선택");

        List<String> listItems = new ArrayList<String>();
        for (BluetoothDevice device : mDevices) {
            listItems.add(device.getName());
        }

        listItems.add("취소");  // 취소 항목 추가.

        final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);
        listItems.toArray(new CharSequence[listItems.size()]);

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                // TODO Auto-generated method stub
                if (item == mPariedDeviceCount) {
                    Toast.makeText(getApplicationContext(), "연결할 장치를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    connectToSelectedDevice(items[item].toString());
                }
            }

        });

        builder.setCancelable(false);
        AlertDialog alert = builder.create();
        alert.show();
    }


    /**
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
        }
        if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {
        }
    }

    @Override
    protected void onDestroy() {
        try {
        } catch (Exception e) {
        }

        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_AudioActivity:
                Intent intent = new Intent(view.getContext(), AudioPlayerActivity.class);
                startActivity(intent);
                break;
        }
    }


    /**
     * 전화하기 ecall service
     */
    // TODO :: EcallDialog는 Activity가 아니라 startActivity 사용불가 -> 분리 불가
    public void openDial() {
        intent_dial = new Intent(Intent.ACTION_CALL, Uri.parse("tel:01044444444"));
        startActivity(intent_dial);
        setAfterFatal();
    }


    public void setAfterFatal() {
        this.dialog = 1;
        imgStatus.setImageResource(R.drawable.ic_status_normal);
    }

    /**
     * connect Thread
     */
    private class ConnectedThread extends Thread {

        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {

                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        public void write(String input) {
            byte[] msgBuffer = input.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }

}
