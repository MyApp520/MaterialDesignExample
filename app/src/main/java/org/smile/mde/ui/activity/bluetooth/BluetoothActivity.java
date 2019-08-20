package org.smile.mde.ui.activity.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.commonlib.base.BaseActivity;
import com.example.commonlib.util.ShowToast;

import org.smile.mde.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 知识点：
 * 1、Android BLE 使用的蓝牙协议是 GATT 协议；
 * 2、Service是服务，Characteristic是特征值。一个蓝牙协议里面有多个Service，一个Service里面又包括多个Characteristic；
 * 3、每个Service或者Characteristic都有一个 128 bit 的UUID来作为标识区分。
 * 4、Service可以理解为一个功能集合，而Characteristic比较重要，蓝牙设备正是通过Characteristic来进行设备间的交互的（如读、写、订阅等操作）。
 *
 *
 *
 * 操作步骤：
 * 0、Android 6.0 以上设备，使用蓝牙，需要申请定位权限，最好是GPS也打开；
 * 1、检测蓝牙是否开启，开启蓝牙；
 * 2、搜索蓝牙设备；
 * 3、建立BluetoothGatt蓝牙通道连接，通道建立成功后，会得到一个BluetoothGatt对象；
 * 4、通道建立成功后，通过BluetoothGatt对象搜索通道对应的蓝牙服务；
 */
public class BluetoothActivity extends BaseActivity {

    private final String TAG = BluetoothActivity.class.getSimpleName();

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.imageView_back)
    ImageView imageViewBack;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.ble_recycler_view)
    RecyclerView bleRecyclerView;

    private final String BLUETOOTH_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private final int ACTION_REQUEST_ENABLE = 0x01;
    private Handler mHandler;
    private List<BluetoothDevice> bluetoothDeviceList = new ArrayList<>();
    private BaseQuickAdapter<BluetoothDevice, BaseViewHolder> bleDeviceListAdapter;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;// 第一种方式：startDiscovery搜索一段时间(默认是12秒左右)后会自动停止搜索；
    private BluetoothLeScanner mBluetoothLeScanner;// 第二种方式：BluetoothLeScanner的方式会一直搜索，需要手动调用stopScan后才会停止搜索；
    private BluetoothServerSocket bluetoothServerSocket;
    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;
    private BluetoothGatt bluetoothGatt;

    @Override
    protected int bindLayout() {
        return R.layout.activity_bluetooth;
    }

    @Override
    protected void initView() {
        textView.setText("开始搜索");
        initRecyclerView();
        initHandler();
        initBluetooth();
    }

    @Override
    protected void initData() {
        // mBluetoothAdapter.startDiscovery()的方式搜索蓝牙，注册搜索蓝牙设备的回调结果广播接收器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);//搜索开始
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);//搜索结束
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);//搜到一个蓝牙设备
        registerReceiver(bleDeviceScanResultReceiver, intentFilter);
    }

    @Override
    protected boolean isUseDagger() {
        return false;
    }

    private void initRecyclerView() {
        bleDeviceListAdapter = new BaseQuickAdapter<BluetoothDevice, BaseViewHolder>(R.layout.item_ble_device, bluetoothDeviceList) {
            @Override
            protected void convert(BaseViewHolder helper, BluetoothDevice item) {
                helper.setText(R.id.tv_ble_device_name, item.getName());
                helper.setText(R.id.tv_ble_connect_state, "bondState = " + item.getBondState() + ",  type = " + item.getType());
                helper.setText(R.id.tv_ble_address, item.getAddress());
            }
        };
        bleDeviceListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (mBluetoothAdapter.isDiscovering()) {
                    ShowToast.showToast(getApplicationContext(), "正在搜索蓝牙设备，请稍后再操作");
                    return;
                }

                // 检测蓝牙地址是否合规 mBluetoothAdapter.checkBluetoothAddress(address);
                // bluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);
                bluetoothDevice = bluetoothDeviceList.get(position);
                createBluetoothGatt();
            }
        });
        bleRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        bleRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        bleRecyclerView.setAdapter(bleDeviceListAdapter);
    }

    private void initBluetooth() {
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        Log.e(TAG, "initBluetooth: 蓝牙管理者 mBluetoothManager = " + mBluetoothManager);
        if (mBluetoothManager == null) {
            return;
        }
        // 得到蓝牙适配器：
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        Log.e(TAG, "initBluetooth: 是否支持蓝牙 mBluetoothAdapter = " + mBluetoothAdapter);
        if (mBluetoothAdapter == null) {
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            // 打开蓝牙提示框界面，提示用户打开蓝牙
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, ACTION_REQUEST_ENABLE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        }
    }

    /**
     * 获取蓝牙中已经绑定了的设备
     */
    private void getHasBoundDevices() {
        if (mBluetoothAdapter == null) {
            return;
        }
        Set<BluetoothDevice> boundDevices = mBluetoothAdapter.getBondedDevices();
        if (boundDevices == null || boundDevices.isEmpty()) {
            return;
        }
        bluetoothDeviceList.addAll(boundDevices);
    }

    /**
     * 建立蓝牙通道连接
     */
    private void createBluetoothGatt() {
        try {
            stopScanBleDevice();
            releaseBluetoothGatt();
            imageViewBack.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "建立蓝牙通道连接: 11111111111111 bluetoothGatt = " + bluetoothGatt);
                    // 打开Gatt链接，连接可能需要等待。这里必须提醒下，已经连接上设备后才会回调到bluetoothGattCallback。
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        bluetoothGatt = bluetoothDevice.connectGatt(getApplicationContext(), false, bluetoothGattCallback, BluetoothDevice.TRANSPORT_LE);
                    } else {
                        bluetoothGatt = bluetoothDevice.connectGatt(getApplicationContext(), false, bluetoothGattCallback);
                    }
                    Log.e(TAG, "建立蓝牙通道连接: 22222222222222 bluetoothGatt = " + bluetoothGatt);
                }
            }, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止搜索蓝牙设备
     */
    private void stopScanBleDevice() {
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    /**
     * 释放bluetoothGatt连接，节省资源，视情况调用
     */
    private void releaseBluetoothGatt() {
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            bluetoothGatt = null;
        }
    }

    @SuppressLint("HandlerLeak")
    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {

                }
            }
        };
    }

    @OnClick({R.id.imageView_back, R.id.textView})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imageView_back:
                finish();
                break;
            case R.id.textView:
                if (mBluetoothAdapter == null) {
                    return;
                }
                if (mBluetoothAdapter.isDiscovering()) {
                    ShowToast.showToast(getApplicationContext(), "正在搜索，请稍等");
                    return;
                }
                // ToDo 所有扫描方法都会一定程度影响主线程的流畅性，因此可以考虑把扫描操作放置子线程执行
                // 第一种方式：startDiscovery搜索一段时间(一般是10秒左右)后会自动停止搜索；
                mBluetoothAdapter.startDiscovery();

//                // 第二种方式：BluetoothLeScanner的方式会一直搜索，需要手动调用stopScan后才会停止搜索；
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    mBluetoothLeScanner.startScan(scanCallback);
//                }

//                // 第三种方式
//                mBluetoothAdapter.startLeScan(BluetoothAdapter.LeScanCallback callback)
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: requestCode = " + requestCode + ",  resultCode = " + resultCode + ",  data = " + data);
        if (ACTION_REQUEST_ENABLE == requestCode && mBluetoothAdapter.isEnabled()) {
            Log.e(TAG, "onActivityResult: 已开启蓝牙");
        }
    }

    /**
     * mBluetoothLeScanner.startScan(scanCallback)的方式搜索蓝牙的回调结果
     * BluetoothLeScanner的方式会一直搜索，需要手动调用stopScan后才会停止搜索；
     */
    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            Log.e(TAG, "onScanResult: callbackType = " + callbackType + ", scanResult = " + result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.e(TAG, "onBatchScanResults: results = " + results);
            if (results != null) {
                Log.e(TAG, "onBatchScanResults: results = " + results.size());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.e(TAG, "onScanFailed: errorCode = " + errorCode);
        }
    };

    /**
     * 注册搜索蓝牙设备结果的回调广播
     * mBluetoothAdapter.startDiscovery()的方式搜索蓝牙，当扫描到蓝牙设备后，会发出广播，只要在需要的地方注册接收广播；
     * startDiscovery搜索一段时间(一般是10秒左右)后会自动停止搜索；
     */
    private BroadcastReceiver bleDeviceScanResultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 搜索到一个蓝牙设备
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);//bluetoothDevice信号强度
                Log.e(TAG, "onReceive: 搜索到一个蓝牙设备 bluetoothDevice = " + bluetoothDevice + ",  rssi = " + rssi);
                if (bluetoothDevice != null) {
                    bluetoothDeviceList.add(bluetoothDevice);
                    bleDeviceListAdapter.setNewData(bluetoothDeviceList);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                // 搜索开始
                Log.e(TAG, "onReceive: 开始搜索蓝牙设备");
                bluetoothDeviceList.clear();
                textView.setText("正在搜索");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                // 搜索结束
                Log.e(TAG, "onReceive: 搜索蓝牙设备的工作完成了 = " + mBluetoothAdapter.isDiscovering() + ", 结果个数 = " + bluetoothDeviceList.size());
                textView.setText("开始搜索");
            }
        }
    };

    private Thread bluetoothSocketThread = new Thread(new Runnable() {
        @Override
        public void run() {
            Log.e(TAG, "bluetoothSocketThread: bluetoothSocket = " + bluetoothSocket);
            if (bluetoothSocket == null) {
                return;
            }
            try {
                bluetoothSocket.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e(TAG, "run: 蓝牙bluetoothSocket连接状态 = " + bluetoothSocket.isConnected());
        }
    });

    /**
     * BluetoothGattCallback 为所有蓝牙数据回调的处理者，也是整个蓝牙操作当中最为核心的一部分
     * BluetoothGattCallback是个抽象类，里面有很多方法，但并非所有都需要在开发当中用到，需要哪个方法，就重写哪个方法，不需要的，可以直接去掉。
     * 已经连接上设备后才会回调到bluetoothGattCallback
     */
    private BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            // 建立蓝牙通道连接后，最先触发的回调， 蓝牙通道状态发生改变。
            Log.e(TAG, "onConnectionStateChange: status = " + status + ",  newState = " + newState);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "onConnectionStateChange 成功建立蓝牙通道 status = " + status + ",  newState = " + newState);
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    //到这一步，仅仅表示和某个设备建立了蓝牙通道，这个时候需要去调用BluetoothGatt去查找这个设备支持的服务。
                    Log.e(TAG, "onConnectionStateChange 蓝牙连接成功，开始搜索对应的蓝牙服务 gatt.discoverServices() = " + gatt.discoverServices());
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.e(TAG, "onConnectionStateChange 蓝牙连接断开，再次连接蓝牙设备 gatt.connect() = " + gatt.connect());
                }
            } else if (133 == status || 129 == status) {
                Log.e(TAG, "onConnectionStateChange 建立蓝牙通道失败，重新请求建立蓝牙通道连接 status = " + status);
                createBluetoothGatt();
            } else {
                Log.e(TAG, "onConnectionStateChange 建立蓝牙通道失败 status = " + status);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            //当发现设备服务时，会回调到此处。
            Log.e(TAG, "onServicesDiscovered:发现设备服务 status = " + status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            //读取特征后回调到此处。
            Log.e(TAG, "onCharacteristicRead: status = " + status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            //写入特征后回调到此处。
            Log.e(TAG, "onCharacteristicWrite: status = " + status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            //当特征（值）发生变法时回调到此处。
            Log.e(TAG, "onCharacteristicChanged: ");
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            //读取描述符后回调到此处。
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            //写入描述符后回调到此处
            Log.e(TAG, "onDescriptorWrite: ");
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
            //暂时没有用过。
            Log.e(TAG, "onReliableWriteCompleted: status = " + status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            //Rssi表示设备与中心的信号强度，发生变化时回调到此处。
            Log.e(TAG, "onReadRemoteRssi: rssi = " + rssi + ", status = " + status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            //暂时没有用过。
            Log.e(TAG, "onMtuChanged: mtu = " + mtu + ",  status = " + status);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseBluetoothGatt();
        unregisterReceiver(bleDeviceScanResultReceiver);
        stopScanBleDevice();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }
}
