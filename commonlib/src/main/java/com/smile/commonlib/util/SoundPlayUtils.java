package com.smile.commonlib.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.smile.commonlib.R;


/**
 * Created by xh_smile on 2019/8/5.
 */

public class SoundPlayUtils {
    // SoundPool对象
    private static SoundPool mSoundPlayer = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);

    private static int currentPlayStreamId;
    public static int TYPE_NORMAL_PASS;//正常通过
    public static int TYPE_KEY_PEOPLE;//重点人员，请注意盘查
    public static int TYPE_UNKNOWN_PEOPLE;//系统认识，请进一步核查
    public static int TYPE_BLE_CONNECT_SUCCESS;//蓝牙连接成功
    public static int TYPE_BLE_CONNECT_FAIL;//蓝牙连接失败
    public static int TYPE_BLE_DISCONNECT;//蓝牙已断开连接

    /**
     * 初始化
     *
     * @param context
     */
    public static void init(Context context) {
        // priority	优先级：都是短促音频无影响设置为1即可
        TYPE_NORMAL_PASS = mSoundPlayer.load(context, R.raw.normal_pass, 1);
        TYPE_KEY_PEOPLE = mSoundPlayer.load(context, R.raw.key_people, 1);
        TYPE_UNKNOWN_PEOPLE = mSoundPlayer.load(context, R.raw.unknown_people, 1);
        TYPE_BLE_CONNECT_SUCCESS = mSoundPlayer.load(context, R.raw.ble_connect_success, 1);
        TYPE_BLE_CONNECT_FAIL = mSoundPlayer.load(context, R.raw.ble_connect_fail, 1);
        TYPE_BLE_DISCONNECT = mSoundPlayer.load(context, R.raw.ble_disconnect, 1);
    }

    /**
     * 播放声音
     *
     * @param soundID
     */
    public static void play(int soundID) {
        try {
            if (mSoundPlayer == null) {
                return;
            }
            mSoundPlayer.stop(currentPlayStreamId);
            currentPlayStreamId = mSoundPlayer.play(soundID, 1, 1, 0, 0, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
