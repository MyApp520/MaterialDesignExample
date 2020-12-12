package org.smile.mde.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsProvider;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.smile.commonlib.base.BaseActivity;
import com.smile.commonlib.util.JsonUtil;
import com.smile.commonlib.util.MyLog;

import org.smile.mde.R;
import org.smile.mde.baidumap.location.BDLocationService;
import org.smile.mde.bean.LocationResponse;
import org.smile.mde.bean.LoginRespose;
import org.smile.mde.constant.AppConstant;
import org.smile.mde.ui.activity.BaiDuMapTrackActivity;
import org.smile.mde.ui.activity.ConstraintLayoutActivity;
import org.smile.mde.ui.activity.TestCropperActivity;
import org.smile.mde.ui.activity.ToolBarActivity;
import org.smile.mde.ui.activity.TwoRecyclerViewActivity;
import org.smile.mde.ui.activity.audio_video.PlayVoiceActivity;
import org.smile.mde.ui.activity.bluetooth.BluetoothActivity;
import org.smile.mde.ui.activity.customize.RotateCircleActivity;
import org.smile.mde.ui.activity.design.CoordinatorLayoutActivity;
import org.smile.mde.ui.activity.mpandroidchart.MPAndroidChartActivity;
import org.smile.mde.view.SecondPTZCircleControlView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class MainActivity extends BaseActivity implements SecondPTZCircleControlView.OnRingClickListener {

    @BindView(R.id.btn_two_recycler_view)
    Button btnTwoRecyclerView;
    @BindView(R.id.btn_toolbar)
    Button btnToolbar;
    @BindView(R.id.ptz_control_view)
    SecondPTZCircleControlView secondPTZCircleControlView;
    @BindView(R.id.imageview)
    ImageView mImageView;

    private final int READ_REQUEST_CODE = 0x01;

    private BDLocationService bdLocationService;

    @Override
    protected int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        secondPTZCircleControlView.setOnRingClickListener(this);
    }

    @Override
    protected void initData() {
        DocumentsProvider documentsProvider = new DocumentsProvider() {
            @Override
            public Cursor queryRoots(String[] projection) throws FileNotFoundException {
                return null;
            }

            @Override
            public Cursor queryDocument(String documentId, String[] projection) throws FileNotFoundException {
                return null;
            }

            @Override
            public Cursor queryChildDocuments(String parentDocumentId, String[] projection, String sortOrder) throws FileNotFoundException {
                return null;
            }

            @Override
            public ParcelFileDescriptor openDocument(String documentId, String mode, @Nullable CancellationSignal signal) throws FileNotFoundException {
                return null;
            }

            @Override
            public boolean onCreate() {
                return false;
            }
        };
    }

    @Override
    protected boolean isUseDagger() {
        return false;
    }

    @OnClick({R.id.btn_face_rect, R.id.btn_two_recycler_view, R.id.btn_toolbar, R.id.btn_CoordinatorLayout, R.id.btn_ConstraintLayout,
            R.id.btn_mpandroidchart, R.id.btn_customize_view, R.id.btn_audio_video, R.id.btn_bluetooth, R.id.btn_cropper})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_face_rect:
//                startActivity(new Intent(this, FaceRectActivity.class));
//                startActivity(new Intent(this, AMapActivity.class));//高德地图
                startActivity(new Intent(this, BaiDuMapTrackActivity.class));//百度地图绘制运动轨迹
//                OkSocketHelper.getInstance().connectSocket();
                break;
            case R.id.btn_two_recycler_view:
                startActivity(new Intent(this, TwoRecyclerViewActivity.class));
                break;
            case R.id.btn_toolbar:
                startActivity(new Intent(this, ToolBarActivity.class));
                break;
            case R.id.btn_CoordinatorLayout:
                startActivity(new Intent(this, CoordinatorLayoutActivity.class));
                break;
            case R.id.btn_ConstraintLayout:
                startActivity(new Intent(this, ConstraintLayoutActivity.class));
                break;
            case R.id.btn_mpandroidchart:
                startActivity(new Intent(this, MPAndroidChartActivity.class));
                break;
            case R.id.btn_customize_view:
//                startActivity(new Intent(this, CustomizeChartActivity.class));
                startActivity(new Intent(this, RotateCircleActivity.class));
                break;
            case R.id.btn_audio_video:
                startActivity(new Intent(this, PlayVoiceActivity.class));
                break;
            case R.id.btn_bluetooth:
                startActivity(new Intent(this, BluetoothActivity.class));
                break;
            case R.id.btn_cropper:
                startActivity(new Intent(this, TestCropperActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    public void onRingClick(int clickPosition) {
        String info = "";
        switch (clickPosition) {
            case 0:
                info = "北方";
                break;
            case 1:
                info = "东北";
                break;
            case 2:
                info = "东方";
                break;
            case 3:
                info = "东南";
                break;
            case 4:
                info = "南方";
                break;
            case 5:
                info = "西南";
                break;
            case 6:
                info = "西方";
                break;
            case 7:
                info = "西北";
                break;
        }
        Toast.makeText(getApplicationContext(), "点击的是 " + info, Toast.LENGTH_SHORT).show();
    }

    private void intiBaiDuMapLocation() {
        bdLocationService = new BDLocationService(getApplicationContext());
        bdLocationService.registerListener(new BDAbstractLocationListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onReceiveLocation(BDLocation location) {
                if (location == null) {
                    Log.e(TAG, "onReceiveLocation: " + "获取位置信息失败，定位出错");
                    return;
                }
                if (location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType() == BDLocation.TypeOffLineLocation
                        || location.getLocType() == BDLocation.TypeNetWorkLocation) {
                    StringBuffer sb = new StringBuffer(256);
                    sb.append("time : ");
                    /**
                     * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                     * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                     */
                    sb.append(location.getTime());
                    sb.append("\nlocType : ");// 定位类型
                    sb.append(location.getLocType());
                    sb.append("\nlocType description : ");// *****对应的定位类型说明*****
                    sb.append(location.getLocationDescribe());
                    sb.append("\nlatitude : ");// 纬度
                    sb.append(location.getLatitude());
                    sb.append("\nlontitude : ");// 经度
                    sb.append(location.getLongitude());
                    sb.append("\naddr : ");// 地址信息
                    sb.append(location.getAddrStr());

                    MyLog.e(TAG, "定位成功了：" + sb.toString());
                    if (location.getAddress() != null) {
                        Log.e(TAG, "onReceiveLocation: " + location.getAddress().province + location.getAddress().city
                                + location.getAddress().district + location.getAddress().street);
                    }
                } else if (BDLocation.TypeServerCheckKeyError == location.getLocType()) {
                    MyLog.e(TAG, "获取位置信息失败，没有定位权限：locType = " + location.getLocType());
                } else {
                    MyLog.e(TAG, "获取位置信息失败，请检查网络：locType = " + location.getLocType());
                }
            }

            @Override
            public void onLocDiagnosticMessage(int locType, int diagnosticType, String diagnosticMessage) {
                super.onLocDiagnosticMessage(locType, diagnosticType, diagnosticMessage);
                Log.e(TAG, "定位message locType = " + locType + ", diagnosticType = " + diagnosticType + ", diagnosticMessage = " + diagnosticMessage);
            }
        });
        bdLocationService.startBDLocation();
    }

    private OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build();

    private void login() {
        //FormBody普通表单数据时使用(数据是个普通表单)
        //MultipartBody数据里有文件时使用(注意：普通表单数据也可以使用)
        FormBody.Builder formBodyBuilder = new FormBody.Builder()
                .add("MOBIL_NO", AppConstant.MOBIL_NO)
                .add("USERNAME", AppConstant.USERNAME)
                .add("PASSWORD", Base64.encodeToString(AppConstant.PASSWORD.getBytes(), 0))
                .add("C_VER", AppConstant.C_VER)
                .add("P_VER", AppConstant.P_VER)
                .add("DEVICE_NUMBER", getImei())
                .add("encode_type", AppConstant.encode_type);

        Request request = new Request.Builder()
                .url("http://xh-video.3322.org:99/mobile/login.php")
                .post(formBodyBuilder.build())
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "login onFailure: call = " + call);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.toString());
                String content = response.body().string();
                Log.e(TAG, "login onResponse: content = " + content);
                final LoginRespose loginRespose = (LoginRespose) JsonUtil.fromJson(content, LoginRespose.class);
                Log.e(TAG, "loginRespose.getStatus(): " + loginRespose.getStatus());
                AppConstant.session_id = loginRespose.getSession_id();
                Log.e(TAG, "loginRespose.getSession_id(): " + AppConstant.session_id);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ("YES".equals(loginRespose.getStatus())) {
                            //location
                            AppConstant.session_id = loginRespose.getSession_id();
                            Toast.makeText(getApplicationContext(), "login success!", Toast.LENGTH_SHORT).show();
                            outsideLocation();
                        } else {
                            Toast.makeText(getApplicationContext(), loginRespose.getStatus(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void location() {
        setLocationState();
        HttpUrl.Builder httpUrlBuilder = HttpUrl.parse("http://xh-video.3322.org:99/pda/attendance/sign/data.php").newBuilder()
                .addQueryParameter("action", AppConstant.action)
                .addQueryParameter("type", AppConstant.type)
                .addQueryParameter("state", AppConstant.state)
                .addQueryParameter("dutyid", AppConstant.dutyid)
                .addQueryParameter("coor_id", AppConstant.coor_id)
                .addQueryParameter("field_type", AppConstant.field_type);
        Request request = new Request.Builder()
                .addHeader("Cookie", "PHPSESSID=" + AppConstant.session_id + ";" + "P" + AppConstant.session_id + ";" + "C_VER=" + AppConstant.C_VER)
                .url(httpUrlBuilder.build())
                .get()
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "location onFailure: call = " + call);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.toString());
                final String content = response.body().string();
                Log.e(TAG, "location onResponse: content = " + content);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (content != null) {
                            LocationResponse locationResponse = (LocationResponse) JsonUtil.fromJson(content, LocationResponse.class);
                            if (locationResponse != null && 1 == locationResponse.getStatus()) {
                                Toast.makeText(getApplicationContext(), "成功了，啦啦啦", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "未知原因，登录OA查看一下吧", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "未知原因，登录OA查看一下吧", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void outsideLocation() {
        HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(getOutsideLocationUrl()).newBuilder();
        Request request = new Request.Builder()
                .addHeader("Cookie", "PHPSESSID=" + AppConstant.session_id + ";" + "P" + AppConstant.session_id + ";" + "C_VER=" + AppConstant.C_VER)
                .url(httpUrlBuilder.build())
                .get()
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "location onFailure: call = " + call);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.toString());
                final String content = response.body().string();
                Log.e(TAG, "location onResponse: content = " + content);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (content != null) {
                            LocationResponse locationResponse = (LocationResponse) JsonUtil.fromJson(content, LocationResponse.class);
                            if (locationResponse != null && 1 == locationResponse.getStatus()) {
                                Toast.makeText(getApplicationContext(), "成功了，啦啦啦", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "未知原因，登录OA查看一下吧", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "未知原因，登录OA查看一下吧", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    private String getImei() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String imei = "";
        if (telephonyManager != null) {
            if (Build.VERSION.SDK_INT < 26) {
                imei = telephonyManager.getDeviceId();
            } else {
                imei = telephonyManager.getImei();
            }
        }
        if (imei == null) {
            imei = "";
        }
        return imei;
    }

    private void setLocationState() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour < 12) {
            AppConstant.state = "1";
        } else {
            AppConstant.state = "2";
        }
    }

    private String getOutsideLocationUrl() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour < 12) {
            return AppConstant.MORNING_ATTEND_URL;
        } else {
            return AppConstant.AFTERNOON_ATTEND_URL;
        }
    }
}
