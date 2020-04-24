package org.smile.mde.ui.activity.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.smile.commonlib.base.BaseActivity;
import com.smile.commonlib.util.Bluetooth.MdeBluetoothUtil;
import com.smile.commonlib.util.ShowToast;

import org.smile.mde.R;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * （一）经典蓝牙：
 * 1、使用BluetoothSocket建立连接；
 * 2、主要应用在蓝牙电话接听，蓝牙耳机，蓝牙音箱等场合；
 * <p>
 * （二）低功耗蓝牙：
 * 1、使用BluetoothGatt建立连接；
 * 2、主要应用在可穿戴设备，IoT智能设备，健身设备，蓝牙鼠标键盘等电池供电场合；
 * 3、BluetoothGattServer作为周边来提供数据，BluetoothGattServerCallback返回周边的状态，更通俗的说，当中央有请求时，系统调用该抽象类的相应方法传递数据给周边。
 * 4、BluetoothGatt作为中央来使用和处理数据，BluetoothGattCallback返回中央的状态和周边提供数据，即周边反馈的数据通过该抽象类的相应方法传递到中央。
 * <p>
 * （三）双模蓝牙：
 * 1、 即同时支持低功耗蓝牙和经典蓝牙；
 * <p>
 * <p>
 * 知识点：
 * 1、Android BLE 使用的蓝牙协议是 GATT 协议；
 * 2、Service是服务，Characteristic是特征值。一个蓝牙协议里面有多个Service，一个Service里面又包括多个Characteristic；
 * 3、每个Service或者Characteristic都有一个 128 bit 的UUID来作为标识区分。
 * 4、Service可以理解为一个功能集合，而Characteristic比较重要，蓝牙设备正是通过Characteristic来进行设备间的交互的（如读、写、订阅等操作）。
 * <p>
 * <p>
 * <p>
 * 操作步骤：
 * 0、Android 6.0 以上设备，使用蓝牙，需要申请定位权限，最好是GPS也打开；
 * 1、检测蓝牙是否开启，开启蓝牙；
 * 2、搜索蓝牙设备；
 * 第一种方式：startDiscovery搜索一段时间(默认是12秒左右)后会自动停止搜索；
 * 第二种方式：startLeScan(leScanCallback)此方法会一直搜索，需要手动调用stopLeScan(leScanCallback)后才会停止搜索
 * 第三种方式：BluetoothLeScanner的方式会一直搜索，需要手动调用stopScan后才会停止搜索；
 * 注意：
 * 当两种蓝牙设备被某设备（包括当前的设备）配对/绑定后，可能不会再被扫描到。
 * 实际应用：
 * startDiscovery和startLeScan同时启动搜索，保证经典蓝牙和低功耗蓝牙都能搜索到。
 * <p>
 * 3、蓝牙配对(也叫绑定)，蓝牙配对成功后才可以进行连接；
 * 4、低功耗蓝牙：建立蓝牙BluetoothGatt连接通道，通道建立成功后，会得到一个BluetoothGatt对象；
 * 5、通道建立成功后，通过BluetoothGatt对象搜索到对应的蓝牙通道服务；
 */
public class BluetoothActivity extends BaseActivity {

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

    //服务和特征值
    private UUID write_UUID_service;
    private UUID write_UUID_chara;//write_UUID_service和write_UUID_chara是硬件工程师告诉我们的，如果他们搞不清楚，我们也可以自己获取
    private UUID read_UUID_service;
    private UUID read_UUID_chara;
    private UUID notify_UUID_service;
    private UUID notify_UUID_chara;
    private UUID indicate_UUID_service;
    private UUID indicate_UUID_chara;

    private Handler mHandler;
    private Map<String, String> mBluetoothDeviceMap = new HashMap<>();
    private List<BluetoothDevice> mBluetoothDeviceList = new ArrayList<>();
    private BaseQuickAdapter<BluetoothDevice, BaseViewHolder> mBleDeviceListAdapter;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothA2dp mBluetoothA2dp;

    private BluetoothServerSocket mBluetoothServerSocket;//经典蓝牙连接时使用
    private BluetoothSocket mBluetoothSocket;//经典蓝牙连接时使用

    private BluetoothGattServer mBluetoothGattServer;//低功耗蓝牙连接时使用，作为周边来提供数据
    private BluetoothGatt mBluetoothGatt;//低功耗蓝牙连接时使用，作为中央来使用和处理数据

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
        intentFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);//蓝牙正在请求配对
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//配对状态发生改变
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);//
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);//
        registerReceiver(bluetoothBroadcastReceiver, intentFilter);
    }

    @Override
    protected boolean isUseDagger() {
        return false;
    }

    private void initRecyclerView() {
        mBleDeviceListAdapter = new BaseQuickAdapter<BluetoothDevice, BaseViewHolder>(R.layout.item_ble_device, mBluetoothDeviceList) {
            @Override
            protected void convert(BaseViewHolder helper, BluetoothDevice item) {
                String getBondState = "未知";
                switch (item.getBondState()) {
                    case BluetoothDevice.BOND_NONE:
                        getBondState = "没有配对";
                        break;
                    case BluetoothDevice.BOND_BONDING:
                        getBondState = "正在配对";
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        getBondState = "配对成功";
                        break;
                }
                String bluetoothType = "未知";
                switch (item.getType()) {
                    // 该设备配备的蓝牙是什么类型的
                    case BluetoothDevice.DEVICE_TYPE_CLASSIC:
                        bluetoothType = "经典蓝牙";
                        break;
                    case BluetoothDevice.DEVICE_TYPE_LE:
                        bluetoothType = "低功耗蓝牙";
                        break;
                    case BluetoothDevice.DEVICE_TYPE_DUAL:
                        bluetoothType = "双模蓝牙";
                        break;
                }
                //设备类型：手机、电脑、可穿戴设备、血压器、温度计等等
                helper.setText(R.id.tv_ble_device_name, item.getName() + " --- 设备类型 = " + item.getBluetoothClass().getDeviceClass());
                helper.setText(R.id.tv_ble_connect_state, "bondState = " + item.getBondState() + " - " + getBondState + ",  type = " + item.getType());
                helper.setText(R.id.tv_ble_address, item.getAddress() + " --- " + bluetoothType);
            }
        };
        mBleDeviceListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (mBluetoothAdapter.isDiscovering()) {
                    ShowToast.showToast(getApplicationContext(), "正在搜索蓝牙设备，请稍后再操作");
                    return;
                }

                stopScanBleDevice();
                // 检测蓝牙地址是否合规 mBluetoothAdapter.checkBluetoothAddress(address);
                // bluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);
                mBluetoothDevice = mBluetoothDeviceList.get(position);
                switch (mBluetoothDevice.getType()) {
                    // 该设备配备的蓝牙是什么类型的
                    case BluetoothDevice.DEVICE_TYPE_CLASSIC:
                        // 经典蓝牙 BR / EDR设备
                        createBluetoothSocket();
                        break;
                    case BluetoothDevice.DEVICE_TYPE_LE:
                        // 低功耗蓝牙
                        createBluetoothGatt();
                        break;
                    case BluetoothDevice.DEVICE_TYPE_DUAL:
                        // 双模蓝牙
                        createBluetoothGatt();
                        break;
                }
            }
        });
        bleRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        bleRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        bleRecyclerView.setAdapter(mBleDeviceListAdapter);
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
     * 获取蓝牙中已经配对(绑定)了的设备
     */
    private void getHasBoundDevices() {
        if (mBluetoothAdapter == null) {
            return;
        }
        Set<BluetoothDevice> boundDevices = mBluetoothAdapter.getBondedDevices();
        if (boundDevices == null || boundDevices.isEmpty()) {
            return;
        }
        for (BluetoothDevice boundDevice : boundDevices) {
            if (boundDevice == null || mBluetoothDeviceMap.containsKey(boundDevice.getAddress())) {
                continue;
            }
            mBluetoothDeviceList.add(boundDevice);
            mBluetoothDeviceMap.put(boundDevice.getAddress(), boundDevice.getAddress());
        }
    }

    /**
     * 建立经典蓝牙连接通道
     */
    private void createBluetoothSocket() {
        if (mBluetoothSocket != null) {
            try {
                mBluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mBluetoothSocket = null;
        }
        if (bluetoothSocketThread != null) {
            try {
                bluetoothSocketThread.interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            Method method = mBluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
            mBluetoothSocket = (BluetoothSocket) method.invoke(mBluetoothDevice, 1);
            Log.e(TAG, "请求建立经典蓝牙连接通道: mBluetoothSocket = " + mBluetoothSocket);
            if (BluetoothDevice.BOND_BONDED == mBluetoothDevice.getBondState()) {
                bluetoothSocketThread.start();
            } else {
                // 配对(绑定)
                MdeBluetoothUtil.createBluetoothBond(mBluetoothDevice);
            }
        } catch (IllegalThreadStateException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 建立低功耗蓝牙连接通道
     */
    private void createBluetoothGatt() {
        try {
            stopScanBleDevice();
            releaseBluetoothGatt();
            imageViewBack.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "请求建立低功耗蓝牙连接通道: 11111111111111 mBluetoothGatt = " + mBluetoothGatt);
                    // 打开Gatt链接，连接可能需要等待。这里必须提醒下，已经连接上设备后才会回调到bluetoothGattCallback。
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        mBluetoothGatt = mBluetoothDevice.connectGatt(getApplicationContext(), false, bluetoothGattCallback, BluetoothDevice.TRANSPORT_LE);
                    } else {
                        mBluetoothGatt = mBluetoothDevice.connectGatt(getApplicationContext(), false, bluetoothGattCallback);
                    }
                    Log.e(TAG, "请求建立低功耗蓝牙连接通道: 22222222222222 mBluetoothGatt = " + mBluetoothGatt);
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
        if (mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.stopLeScan(leScanCallback);
        }
    }

    /**
     * 释放bluetoothGatt连接，节省资源，视情况调用
     */
    private void releaseBluetoothGatt() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }

    @SuppressLint("HandlerLeak")
    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 30:
                        boolean isConnected = mBluetoothSocket.isConnected();
                        Log.e(TAG, "run333: 蓝牙bluetoothSocket连接状态 isConnected = " + isConnected);
                        if (!isConnected) {
                            mBluetoothAdapter.getProfileProxy(getApplicationContext(), serviceListener, BluetoothProfile.A2DP);
                        }
                        break;
                    case 31:
                        boolean isConnected2 = mBluetoothSocket.isConnected();
                        Log.e(TAG, "run333: 蓝牙bluetoothSocket连接状态 isConnected2 = " + isConnected2);
                        int connectState = mBluetoothA2dp.getConnectionState(mBluetoothDevice);
                        Log.e(TAG, "handleMessage: mBluetoothA2dp connectState = " + connectState);
                        break;
                }
            }
        };
    }

    /**
     * 初始化蓝牙服务和特征的UUID
     */
    private void initServiceAndChara(List<BluetoothGattService> bluetoothGattServices) {
        for (BluetoothGattService bluetoothGattService : bluetoothGattServices) {
            Log.e(TAG, "bluetoothGattService getUuid = " + bluetoothGattService.getUuid() + ", type = " + bluetoothGattService.getType());
            // 获取服务对应的特征
            List<BluetoothGattCharacteristic> characteristics = bluetoothGattService.getCharacteristics();
            for (BluetoothGattCharacteristic characteristic : characteristics) {
                Log.e(TAG, "characteristic getUuid = " + characteristic.getUuid());
                Log.e(TAG, "characteristic getPermissions = " + characteristic.getPermissions());
                Log.e(TAG, "characteristic getProperties = " + characteristic.getProperties());

                int charaProp = characteristic.getProperties();
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                    read_UUID_chara = characteristic.getUuid();
                    read_UUID_service = bluetoothGattService.getUuid();
                    Log.e(TAG, "read_chara=" + read_UUID_chara + "----read_service=" + read_UUID_service);
                }
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                    write_UUID_chara = characteristic.getUuid();
                    write_UUID_service = bluetoothGattService.getUuid();
                    Log.e(TAG, "write_chara=" + write_UUID_chara + "----write_service=" + write_UUID_service);
                }
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
                    write_UUID_chara = characteristic.getUuid();
                    write_UUID_service = bluetoothGattService.getUuid();
                    Log.e(TAG, "write_chara=" + write_UUID_chara + "----write_service=" + write_UUID_service);

                }
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                    notify_UUID_chara = characteristic.getUuid();
                    notify_UUID_service = bluetoothGattService.getUuid();
                    Log.e(TAG, "notify_chara=" + notify_UUID_chara + "----notify_service=" + notify_UUID_service);
                }
                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
                    indicate_UUID_chara = characteristic.getUuid();
                    indicate_UUID_service = bluetoothGattService.getUuid();
                    Log.e(TAG, "indicate_chara=" + indicate_UUID_chara + "----indicate_service=" + indicate_UUID_service);
                }
            }
        }

        Log.e(TAG, "read_chara=" + read_UUID_chara + "----read_service=" + read_UUID_service);
        Log.e(TAG, "write_chara=" + write_UUID_chara + "----write_service=" + write_UUID_service);
        Log.e(TAG, "notify_chara=" + notify_UUID_chara + "----notify_service=" + notify_UUID_service);
        Log.e(TAG, "indicate_chara=" + indicate_UUID_chara + "----indicate_service=" + indicate_UUID_service);
    }

    @OnClick({R.id.imageView_back, R.id.tv_title, R.id.textView})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imageView_back:
                finish();
                break;
            case R.id.tv_title:
                MdeBluetoothUtil.writeData(mBluetoothGatt, write_UUID_service, write_UUID_chara, "mde".getBytes());
                break;
            case R.id.textView:
                if (mBluetoothAdapter == null) {
                    return;
                }
                if (mBluetoothAdapter.isDiscovering()) {
                    ShowToast.showToast(getApplicationContext(), "正在搜索，请稍等");
                    return;
                }
                // 搜索前先清空数据
                mBluetoothDeviceList.clear();
                mBluetoothDeviceMap.clear();
                mBleDeviceListAdapter.setNewData(mBluetoothDeviceList);

                // 获取已经配对(绑定)过的设备
                getHasBoundDevices();
                // ToDo 所有扫描方法都会一定程度影响主线程的流畅性，因此可以考虑把扫描操作放置子线程执行
                // 第一种方式：startDiscovery搜索一段时间(一般是12秒左右)后会自动停止搜索；
                mBluetoothAdapter.startDiscovery();
                // 第二种方式：此方法也会一直搜索，需要手动调用stopLeScan(leScanCallback)后才会停止搜索
                mBluetoothAdapter.startLeScan(leScanCallback);

//                // 第三种方式：BluetoothLeScanner的方式会一直搜索，需要手动调用stopScan(scanCallback)后才会停止搜索；
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    mBluetoothLeScanner.startScan(scanCallback);
//                }
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
    @SuppressLint("NewApi")
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
    private BroadcastReceiver bluetoothBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 搜索到一个蓝牙设备
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);//bluetoothDevice信号强度
                Log.e(TAG, "onReceive: 搜索到一个蓝牙设备 bluetoothDevice = " + bluetoothDevice + ",  rssi = " + rssi);
                if (bluetoothDevice != null && !mBluetoothDeviceMap.containsKey(bluetoothDevice.getAddress())) {
                    mBluetoothDeviceList.add(bluetoothDevice);
                    mBluetoothDeviceMap.put(bluetoothDevice.getAddress(), bluetoothDevice.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                // 搜索开始
                Log.e(TAG, "onReceive: 开始搜索蓝牙设备");
                textView.setText("正在搜索");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                // 搜索结束
                Log.e(TAG, "onReceive: 搜索蓝牙设备的工作完成了 = " + mBluetoothAdapter.isDiscovering() + ", 结果个数 = " + mBluetoothDeviceList.size());
                stopScanBleDevice();
                mBleDeviceListAdapter.setNewData(mBluetoothDeviceList);
                textView.setText("开始搜索");
            } else if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
                Log.e(TAG, "onReceive: 请求配对");
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                switch (mBluetoothDevice.getBondState()) {
                    case BluetoothDevice.BOND_NONE:
                        Log.e(TAG, "onReceive 没有配对");
                        break;
                    case BluetoothDevice.BOND_BONDING:
                        Log.e(TAG, "onReceive 配对中");
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        Log.e(TAG, "onReceive 配对成功 mBluetoothDevice.getType() = " + mBluetoothDevice.getType());
                        if (mBluetoothDevice.getType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) {
                            // 启动连接经典蓝牙的线程
                            bluetoothSocketThread.start();
                        }
                        break;
                }
                mBleDeviceListAdapter.notifyDataSetChanged();
            } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                Log.e(TAG, "onReceive: BluetoothDevice.ACTION_ACL_CONNECTED");
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                Log.e(TAG, "onReceive: BluetoothDevice.ACTION_ACL_DISCONNECTED");
            } else {
                Log.e(TAG, "onReceive:蓝牙广播接收器 action = " + action);
            }
        }
    };

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.e(TAG, "onLeScan: device = " + device + ",  rssi = " + rssi);
            if (device != null && !mBluetoothDeviceMap.containsKey(device.getAddress())) {
                mBluetoothDeviceList.add(device);
                mBluetoothDeviceMap.put(device.getAddress(), device.getAddress());
            }
        }
    };

    private Thread bluetoothSocketThread = new Thread(new Runnable() {
        @Override
        public void run() {
            Log.e(TAG, "bluetoothSocketThread: mBluetoothSocket = " + mBluetoothSocket);
            if (mBluetoothSocket == null) {
                return;
            }
            try {
                mBluetoothSocket.connect();
            } catch (IOException e) {
                Log.e(TAG, "run: 反射调用方式连接蓝牙失败 ");
                e.printStackTrace();
                try {
                    mBluetoothSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                try {
                    mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(UUID.randomUUID());
                    mBluetoothSocket.connect();
                    Log.e(TAG, "run111: 蓝牙bluetoothSocket连接状态 = " + mBluetoothSocket.isConnected());
                } catch (Exception e1) {
                    Log.e(TAG, "run: createRfcommSocketToServiceRecord 方式也连接失败");
                    e1.printStackTrace();
                    try {
                        mBluetoothSocket.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
            }
            Log.e(TAG, "run222: 蓝牙bluetoothSocket连接状态 = " + mBluetoothSocket.isConnected());
            mHandler.sendEmptyMessageDelayed(30, 30 * 1000);
        }
    });

    private BluetoothProfile.ServiceListener serviceListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            Log.e(TAG, "onServiceConnected: profile = " + profile);
            int connectState = proxy.getConnectionState(mBluetoothDevice);
            Log.e(TAG, "onServiceConnected: proxy connectState = " + connectState);
            if (BluetoothProfile.STATE_CONNECTED != connectState) {
                mBluetoothA2dp = (BluetoothA2dp) proxy;
                try {
                    mBluetoothA2dp.getClass().getMethod("connect", BluetoothDevice.class).invoke(mBluetoothA2dp, mBluetoothDevice);
                    mHandler.sendEmptyMessageDelayed(31, 30 * 1000);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            } else {
                mHandler.removeCallbacksAndMessages(null);
            }
            for (BluetoothDevice bluetoothDevice : proxy.getConnectedDevices()) {
                Log.e(TAG, "onServiceConnected: name = " + bluetoothDevice.getName() + ",  address = " + bluetoothDevice.getAddress());
            }
            Log.e(TAG, "onServiceConnected: isConnected = " + mBluetoothSocket.isConnected());
        }

        @Override
        public void onServiceDisconnected(int profile) {
            Log.e(TAG, "onServiceDisconnected: profile = " + profile);
        }
    };

    /**
     * BluetoothGattCallback 为所有蓝牙数据回调的处理者，也是整个蓝牙操作当中最为核心的一部分
     * BluetoothGattCallback是个抽象类，里面有很多方法，但并非所有都需要在开发当中用到，需要哪个方法，就重写哪个方法，不需要的，可以直接去掉。
     * 已经连接上设备后才会回调到bluetoothGattCallback
     */
    private BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            // 建立蓝牙BluetoothGatt连接通道后，最先触发的回调，监听通道状态的改变。
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
            // 发现设备（到这里才是真正建立了数据通信连接，之后才可以进行数据读、写、订阅等操作）当发现设备服务时，会回调到此处。
            Log.e(TAG, "onServicesDiscovered:发现设备服务 status = " + status);
            List<BluetoothGattService> bluetoothGattServices = gatt.getServices();
            // 初始化蓝牙服务和特征的UUID
            initServiceAndChara(bluetoothGattServices);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                boolean result = mBluetoothGatt.requestMtu(512);//有些硬件设备不支持此设置，所以不一定能设置成功
                Log.e(TAG, "onServicesDiscovered: 设置长度512：" + result);
            }
            //订阅通知
            mBluetoothGatt.setCharacteristicNotification(mBluetoothGatt.getService(notify_UUID_service).getCharacteristic(notify_UUID_chara), true);
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
            //当特征（值）发生变法时回调到此处（接收到所连接的蓝牙设备返回的数据）。
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
        unregisterReceiver(bluetoothBroadcastReceiver);
        stopScanBleDevice();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }
}
