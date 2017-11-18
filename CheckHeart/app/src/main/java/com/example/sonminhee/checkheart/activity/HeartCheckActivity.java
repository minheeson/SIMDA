package com.example.sonminhee.checkheart.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sonminhee.checkheart.util.BluetoothSerialClient;
import com.example.sonminhee.checkheart.util.EcallDialog;
import com.example.sonminhee.checkheart.util.GraphView;
import com.example.sonminhee.checkheart.util.MyLocationListener;
import com.example.sonminhee.checkheart.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class HeartCheckActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "HeartCheckActivity";
    // private static final String TAB_BT = "Bluetooth";
    //private static final int REQUEST_ENABLE_BT = 100;

    Button btnAudioActivity;
    GraphView graphView;

    Handler bluetoothIn = null;

    public static Context mContext;

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
    Button btn; // TODO :: 이거 무슨 버튼이람?
    Button btnTemp;

    // TODO :: refactor var
    private LinkedList<BluetoothDevice> mBluetoothDevices = new LinkedList<BluetoothDevice>();
    private ArrayAdapter<String> mDeviceArrayAdapter;

    private EditText mEditTextInput;
    private TextView mTextView;
    private Button mButtonSend;
    private ProgressDialog mLoadingDialog;
    private AlertDialog mDeviceListDialog;
    private Menu mMenu;
    private BluetoothSerialClient mClient;
    // -- bluetooth

    static final int REQUEST_ENABLE_BT = 10;
    int mPariedDeviceCount = 0;
    Set<BluetoothDevice> mDevices;
    // 폰의 블루투스 모듈을 사용하기 위한 오브젝트.
    BluetoothAdapter mBluetoothAdapter;
    /**
     * BluetoothDevice 로 기기의 장치정보를 알아낼 수 있는 자세한 메소드 및 상태값을 알아낼 수 있다.
     * 연결하고자 하는 다른 블루투스 기기의 이름, 주소, 연결 상태 등의 정보를 조회할 수 있는 클래스.
     * 현재 기기가 아닌 다른 블루투스 기기와의 연결 및 정보를 알아낼 때 사용.
     */
    BluetoothDevice mRemoteDevie;
    // 스마트폰과 페어링 된 디바이스간 통신 채널에 대응 하는 BluetoothSocket
    BluetoothSocket mSocket = null;
    OutputStream mOutputStream = null;
    InputStream mInputStream = null;
    String mStrDelimiter = "\n";
    char mCharDelimiter = '\n';


    Thread mWorkerThread = null;
    byte[] readBuffer;
    int readBufferPosition;

    TextView txtHeartbeat;

    private BluetoothSocket btSocket = null;
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private ConnectedThread mConnectedThread;
    final int handlerState = 0;
    private StringBuilder recDataString = new StringBuilder();


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

        selectDevice();


        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                Log.i("TEST ", "TESTRTETE");
                if (msg.what == handlerState) {
                    String readMessage = (String) msg.obj;
                    recDataString.append(readMessage);
                    Log.i("TEST ", "TESTRTETE ++ " + recDataString);
                    //Log.i("TEST ", "TESTRTETE :: INT :: " + (int)recDataString.charAt(0));
                    int[] points2 = {5, 4,5,3, 7, 8, 5, 6, 7, 8};
                    graphView.setPoints(points2, 1, 0, 10);
                    graphView.drawForBeforeDrawView();
                    txtHeartbeat.setText("현재 심박수 : " + (int) readMessage.charAt(0));

                }
            }
        };
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
        // BluetoothDevice 원격 블루투스 기기를 나타냄.
        Log.i(TAG, "TEST NAME :: " + selectedDeviceName);
        mRemoteDevie = getDeviceFromBondedList(selectedDeviceName);
        // java.util.UUID.fromString : 자바에서 중복되지 않는 Unique 키 생성.
        UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        try {
            btSocket = createBluetoothSocket(mRemoteDevie);
            Log.i("TEST ", "TESTRTETE :: btSocket :: ");
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }
        try {
            btSocket = createBluetoothSocket(mRemoteDevie);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try {
            btSocket.connect();
            Log.i("TEST ", "TESTRTETE :: thread.connect() :: ");

        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                //insert code to deal with this
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        Log.i("TEST ", "TESTRTETE :: thread.start() :: ");
        mConnectedThread.start();

        mConnectedThread.write("x");


    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }


    private void init() {
        btn = (Button) findViewById(R.id.main_popup);
        btn.setOnClickListener(this);


        txtHeartbeat = (TextView) findViewById(R.id.main_heartRate);

        /** (GPS) 위치 받아오기 */
//        txtLatitude = (EditText) findViewById(R.id.et_Latitude);
//        txtLongitude = (EditText) findViewById(R.id.et_Longitude);

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
        // startActivityForResult 를 여러번 사용할 땐 이런 식으로 switch 문을 사용하여 어떤 요청인지 구분하여 사용함.
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

//    // TODO :: add bluetooth
//    private void initBluetooth() {
//        mClient = BluetoothSerialClient.getInstance();
//
//        if (mClient == null) {
//            Toast.makeText(getApplicationContext(), "Cannot use the Bluetooth device.", Toast.LENGTH_SHORT).show();
//            finish();
//        }
//    }


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

    void checkBluetooth() {
        /**
         * getDefaultAdapter() : 만일 폰에 블루투스 모듈이 없으면 null 을 리턴한다.
         이경우 Toast를 사용해 에러메시지를 표시하고 앱을 종료한다.
         */
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {  // 블루투스 미지원
            Toast.makeText(getApplicationContext(), "기기가 블루투스를 지원하지 않습니다.", Toast.LENGTH_LONG).show();
            finish();  // 앱종료
        } else { // 블루투스 지원
            /** isEnable() : 블루투스 모듈이 활성화 되었는지 확인.
             *               true : 지원 ,  false : 미지원
             */
            if (!mBluetoothAdapter.isEnabled()) { // 블루투스 지원하며 비활성 상태인 경우.
                Toast.makeText(getApplicationContext(), "현재 블루투스가 비활성 상태입니다.", Toast.LENGTH_LONG).show();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                // REQUEST_ENABLE_BT : 블루투스 활성 상태의 변경 결과를 App 으로 알려줄 때 식별자로 사용(0이상)
                /**
                 startActivityForResult 함수 호출후 다이얼로그가 나타남
                 "예" 를 선택하면 시스템의 블루투스 장치를 활성화 시키고
                 "아니오" 를 선택하면 비활성화 상태를 유지 한다.
                 선택 결과는 onActivityResult 콜백 함수에서 확인할 수 있다.
                 */
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else // 블루투스 지원하며 활성 상태인 경우.
                selectDevice();
        }
    }

    /**
     * 블루투스 디바이스는 연결해서 사용하기 전에 먼저 페어링 되어야만 한다
     */
    void selectDevice() {
        mDevices = mBluetoothAdapter.getBondedDevices();
        mPariedDeviceCount = mDevices.size();

        if (mPariedDeviceCount == 0) { // 페어링된 장치가 없는 경우.
            Toast.makeText(getApplicationContext(), "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();
            finish(); // App 종료.
        }
        // 페어링된 장치가 있는 경우.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("블루투스 장치 선택");

        // 각 디바이스는 이름과(서로 다른) 주소를 가진다. 페어링 된 디바이스들을 표시한다.
        List<String> listItems = new ArrayList<String>();
        for (BluetoothDevice device : mDevices) {
            // device.getName() : 단말기의 Bluetooth Adapter 이름을 반환.
            listItems.add(device.getName());
            Log.i(TAG, "DEVICE NAME :: " + device.getName());
        }

        listItems.add("취소");  // 취소 항목 추가.


        // CharSequence : 변경 가능한 문자열.
        // toArray : List형태로 넘어온것 배열로 바꿔서 처리하기 위한 toArray() 함수.
        final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);
        // toArray 함수를 이용해서 size만큼 배열이 생성 되었다.
        listItems.toArray(new CharSequence[listItems.size()]);

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                // TODO Auto-generated method stub
                if (item == mPariedDeviceCount) { // 연결할 장치를 선택하지 않고 '취소' 를 누른 경우.
                    Toast.makeText(getApplicationContext(), "연결할 장치를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
                    finish();
                } else { // 연결할 장치를 선택한 경우, 선택한 장치와 연결을 시도함.
                    connectToSelectedDevice(items[item].toString());
                }
            }

        });

        builder.setCancelable(false);  // 뒤로 가기 버튼 사용 금지.
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
            // ACCESS_FINE_LOCATION & ACCESS_COARSE_LOCATION권한 획득
            setGPS(location);
        }
        if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {
            // CALL_PHONE 권한 획득
        }
    }

    @Override
    protected void onDestroy() {
        try {
            mWorkerThread.interrupt(); // 데이터 수신 쓰레드 종료
            mInputStream.close();
            mSocket.close();
        } catch (Exception e) {
        }

        super.onDestroy();
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
                } else {
                    locListenD = new MyLocationListener();
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 0.5f, locListenD);
                }
            }

        } catch (SecurityException e) {
            // lets the user know there is a problem with the gps
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RQ_ACCESS_FINE_LOCATION);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, RQ_ACCESS_COARSE_LOCATION);
            Log.d("GPS :: ", "disable");
        }


    }


    /**
     * connect Thread
     */
    private class ConnectedThread extends Thread {

        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection


                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
                Log.i("TEST", "TESTRTETE ::  Thread :: " + tmpIn);
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

                    Log.i("TEST", "TESTRTETE ::  run() " + mmInStream.available());


                    bytes = mmInStream.read(buffer);
                    Log.i("TEST", "TESTRTETE ::  bytes = " + bytes);//read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler


                    Log.i("TEST", "TESTRTETE ::  readMSG ::  " + readMessage + "");
                    Log.i("TEST", "TESTRTETE ::  readMSG ::  " + (int) readMessage.charAt(0) + "");
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    Log.i("TEST", "TESTRTETE :: error :: ");
                    break;
                }
            }
        }

        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }

}
