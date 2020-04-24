package org.smile.mde.ui.activity.audio_video;

import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.smile.commonlib.base.BaseActivity;
import com.smile.commonlib.util.ShowToast;
import com.smile.commonlib.util.SoundPlayUtils;

import org.smile.mde.R;

import butterknife.BindView;
import butterknife.OnClick;

public class PlayVoiceActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.image_back)
    ImageView imageBack;
    @BindView(R.id.btn_play_voice)
    Button btnPlayVoice;

    private MediaPlayer mXhMediaPlayer;
    private int playCount;

    @Override
    protected int bindLayout() {
        return R.layout.activity_play_voice;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        SoundPlayUtils.init(getApplicationContext());
    }

    @Override
    protected boolean isUseDagger() {
        return false;
    }

    @OnClick({R.id.image_back, R.id.btn_play_voice, R.id.btn_sound})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.btn_play_voice:
                SoundPlayUtils.play(SoundPlayUtils.TYPE_UNKNOWN_PEOPLE);
                break;
            case R.id.btn_sound:
                SoundPlayUtils.play(SoundPlayUtils.TYPE_BLE_CONNECT_SUCCESS);
                btnPlayVoice.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SoundPlayUtils.play(SoundPlayUtils.TYPE_KEY_PEOPLE);
                    }
                }, 2500);
                break;
        }
    }

    private void playVoice() {
        releaseMediaPlayer();
        mXhMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.normal_pass);
        if (mXhMediaPlayer == null) {
            ShowToast.showToast(getApplicationContext(), "语音文件在哪");
            return;
        }
        try {
            mXhMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // 开始播放音频
                    mp.start();
                }
            });
            mXhMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playCount++;
                    //播放完成触发此事件
                    Log.e(TAG, "onCompletion: 播放完成了 = " + mp + ", playCount = " + playCount);
                    releaseMediaPlayer();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放音频资源
     */
    private void releaseMediaPlayer() {
        Log.e(TAG, "releaseMediaPlayer: mXhMediaPlayer =" + mXhMediaPlayer);
        if (mXhMediaPlayer == null) {
            return;
        }
        try {
            mXhMediaPlayer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mXhMediaPlayer.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mXhMediaPlayer.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mXhMediaPlayer = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }
}
