package org.smile.mde.ui.activity.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.smile.commonlib.base.BaseActivity;

import org.smile.mde.R;

import java.util.UUID;

/**
 * 手机端作为外设，也就是作为周边来提供数据
 */
public class BluetoothServerActivity extends BaseActivity {

    private final String BLUETOOTH_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private final int ACTION_REQUEST_ENABLE = 0x01;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothDevice mBluetoothDevice;
    private BluetoothGattService mBluetoothGattService;
    private BluetoothGattCharacteristic mBluetoothGattCharacteristic;
    private BluetoothGattDescriptor mBluetoothGattDescriptor;

    private BluetoothServerSocket mBluetoothServerSocket;//经典蓝牙连接时使用
    private BluetoothSocket mBluetoothSocket;//经典蓝牙连接时使用

    private BluetoothGattServer mBluetoothGattServer;//低功耗蓝牙连接时使用，作为周边来提供数据
    private BluetoothGatt mBluetoothGatt;//低功耗蓝牙连接时使用，作为中央来使用和处理数据
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;//手机作为外设：设置广播与服务主要涉及的类BluetoothLeAdvertiser与BluetoothGattServer

    @Override
    protected int bindLayout() {
        return R.layout.activity_bluetooth_server;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        initBluetooth();
    }

    @Override
    protected boolean isUseDagger() {
        return false;
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
        // 低功耗蓝牙
        mBluetoothGattServer = mBluetoothManager.openGattServer(getApplicationContext(), bluetoothGattServerCallback);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 设置广播，主要操作是BluetoothLeAdvertiser
            mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        }
        if (mBluetoothLeAdvertiser == null) {
            Toast.makeText(getApplicationContext(), "低功耗蓝牙不支持作为周边设备，无法向中心提供数据", Toast.LENGTH_SHORT).show();
        }
//        // 经典蓝牙
//        mBluetoothServerSocket = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord();
    }

    /**
     * 用BluetoothGattServer添加一个服务；该服务有一个读特征，该特征有一个描述；一个写特征；
     * 注意：可以添加多个服务，每个服务也可以添加多个特征，每个特征可以添加多个描述；
     */
    private void addBluetoothGattService(UUID UUID_SERVER, UUID UUID_CHARREAD, UUID UUID_DESCRIPTOR, UUID UUID_CHARWRITE) {
        BluetoothGattService service = new BluetoothGattService(UUID_SERVER, BluetoothGattService.SERVICE_TYPE_PRIMARY);

        BluetoothGattCharacteristic characteristicRead = new BluetoothGattCharacteristic(UUID_CHARREAD,
                BluetoothGattCharacteristic.PROPERTY_READ, BluetoothGattCharacteristic.PERMISSION_READ);
        BluetoothGattDescriptor descriptor = new BluetoothGattDescriptor(UUID_DESCRIPTOR, BluetoothGattCharacteristic.PERMISSION_WRITE);
        characteristicRead.addDescriptor(descriptor);
        service.addCharacteristic(characteristicRead);

        BluetoothGattCharacteristic characteristicWrite = new BluetoothGattCharacteristic(UUID_CHARWRITE,
                BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_WRITE);
        service.addCharacteristic(characteristicWrite);
        Log.d(TAG, "用BluetoothGattServer添加一个服务 = " + mBluetoothGattServer.addService(service));
    }

    private BluetoothGattServerCallback bluetoothGattServerCallback = new BluetoothGattServerCallback() {
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            super.onConnectionStateChange(device, status, newState);
            Log.e(TAG, "onConnectionStateChange: status = " + status + ", newState = " + newState);
        }

        @Override
        public void onServiceAdded(int status, BluetoothGattService service) {
            super.onServiceAdded(status, service);
            Log.e(TAG, "onServiceAdded: status = " + status);
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
            Log.e(TAG, "onCharacteristicReadRequest: requestId = " + requestId);
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
            Log.e(TAG, "onCharacteristicWriteRequest: requestId = " + requestId);
        }

        @Override
        public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
            super.onDescriptorReadRequest(device, requestId, offset, descriptor);
            Log.e(TAG, "onDescriptorReadRequest: requestId = " + requestId);
        }

        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);
            Log.e(TAG, "onDescriptorWriteRequest: requestId = " + requestId);
        }

        @Override
        public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
            super.onExecuteWrite(device, requestId, execute);
        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            super.onNotificationSent(device, status);
            Log.e(TAG, "onNotificationSent: status " + status);
        }

        @Override
        public void onMtuChanged(BluetoothDevice device, int mtu) {
            super.onMtuChanged(device, mtu);
        }

        @Override
        public void onPhyUpdate(BluetoothDevice device, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(device, txPhy, rxPhy, status);
            Log.e(TAG, "onPhyUpdate: status " + status);
        }

        @Override
        public void onPhyRead(BluetoothDevice device, int txPhy, int rxPhy, int status) {
            super.onPhyRead(device, txPhy, rxPhy, status);
            Log.e(TAG, "onPhyRead: status " + status);
        }
    };
}
