package com.example.commonlib.util.Bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import com.example.commonlib.util.HexUtil;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by smile on 2019/8/21.
 */

public class MdeBluetoothUtil {

    private static final String TAG = MdeBluetoothUtil.class.getSimpleName();

    /**
     * 蓝牙配对（配对成功与失败通过广播返回）经典蓝牙和低功耗蓝牙配对方式一样，连接方式不一样
     * 经典蓝牙必须先配对，再连接；
     * 低功耗蓝牙可以直接连接，因为它会在连接的时候自动完成配对
     *
     * @param device
     */
    public static boolean createBluetoothBond(BluetoothDevice device) {
        if (device == null) {
            Log.e(TAG, "bond device null");
            return false;
        }
        boolean bondResult = false;
        //判断设备是否配对，没有配对在配，配对了就不需要配了
        if (BluetoothDevice.BOND_NONE == device.getBondState()) {
            Log.d(TAG, "准备进行蓝牙配对 to bond:" + device.getName());
            try {
                Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
                bondResult = (Boolean) createBondMethod.invoke(device);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e(TAG, "蓝牙配对出现异常了 to bond fail!");
            }
        } else if (BluetoothDevice.BOND_BONDED == device.getBondState()) {
            bondResult = true;
        }
        Log.e(TAG, "蓝牙配对结果 = " + bondResult);
        return bondResult;
    }

    /**
     * @param mBluetoothGatt
     * @param write_UUID_service
     * @param write_UUID_chara
     * @param hexString          这里必须是16进制字符串，不是普通的字符串
     */
    public static void writeData(BluetoothGatt mBluetoothGatt, UUID write_UUID_service, UUID write_UUID_chara, String hexString) {
        writeData(mBluetoothGatt, write_UUID_service, write_UUID_chara, HexUtil.hexStringToBytes(hexString));
    }

    public static void writeData(BluetoothGatt mBluetoothGatt, UUID write_UUID_service, UUID write_UUID_chara, byte[] data) {
        if (mBluetoothGatt == null || data == null || data.length < 1) {
            return;
        }
        BluetoothGattService service = mBluetoothGatt.getService(write_UUID_service);
        BluetoothGattCharacteristic charaWrite = service.getCharacteristic(write_UUID_chara);
        if (data.length > 20) {//数据大于个字节 分批次写入
            Log.e(TAG, "writeData: length=" + data.length);
            int num = 0;
            if (data.length % 20 != 0) {
                num = data.length / 20 + 1;
            } else {
                num = data.length / 20;
            }
            for (int i = 0; i < num; i++) {
                byte[] tempArr;
                if (i == num - 1) {
                    tempArr = new byte[data.length - i * 20];
                    System.arraycopy(data, i * 20, tempArr, 0, data.length - i * 20);
                } else {
                    tempArr = new byte[20];
                    System.arraycopy(data, i * 20, tempArr, 0, 20);
                }
                charaWrite.setValue(tempArr);
                mBluetoothGatt.writeCharacteristic(charaWrite);
            }
        } else {
            charaWrite.setValue(data);
            mBluetoothGatt.writeCharacteristic(charaWrite);
        }
    }
}
