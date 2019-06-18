package org.smile.mde.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationQualityReport;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.example.commonlib.util.MyLog;
import com.example.commonlib.util.TimeUitls;
import com.example.commonlib.util.UIUtils;

import org.smile.mde.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AMapActivity extends Activity implements AMap.OnMapLoadedListener, GeocodeSearch.OnGeocodeSearchListener, AMap.OnCameraChangeListener {

    private final String TAG = AMapActivity.class.getSimpleName();
    @BindView(R.id.mapview)
    MapView mMapView;
    @BindView(R.id.imageView_location)
    ImageView imageViewLocation;
    @BindView(R.id.tv_latlng)
    TextView tvLatlng;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.ll_address)
    LinearLayout llAddress;
    @BindView(R.id.image_screen_center_marker)
    ImageView imageScreenCenterMarker;

    private Unbinder unbinder;
    private AMap aMap;
    private AMapLocationClient locationClient;
    private AMapLocationClientOption locationOption;
    private GeocodeSearch geocodeSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amap);
        unbinder = ButterKnife.bind(this);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        initView();
        initLocation();
    }

    private void initView() {
        //MyLocationStyle可理解为SDK自动触发的定位方式    AMapLocationClient手动触发定位的方式
        MyLocationStyle locationStyle = new MyLocationStyle();
        // 自定义定位蓝点图标
        locationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.gps_point));

        aMap = mMapView.getMap();
        aMap.moveCamera(CameraUpdateFactory.zoomTo(19.2f));
        aMap.setMyLocationStyle(locationStyle);
        //显示定位层并可触发(意思是: 如果设为true，那么地图图层加载出来后会自动定位，并将地图显示到定位位置)，默认false
        aMap.setMyLocationEnabled(false);
        //地图状态发生变化的监听
        aMap.setOnCameraChangeListener(this);

        //构造 GeocodeSearch 对象，并设置监听。
        geocodeSearch = new GeocodeSearch(this);
        geocodeSearch.setOnGeocodeSearchListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 初始化定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void initLocation() {
        //初始化client
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        mOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);//可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        return mOption;
    }

    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {

                StringBuffer sb = new StringBuffer();
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (location.getErrorCode() == 0) {
                    sb.append("定位成功" + "\n");
                    sb.append("定位类型: " + location.getLocationType() + "\n");
                    sb.append("经    度    : " + location.getLongitude() + "\n");
                    sb.append("纬    度    : " + location.getLatitude() + "\n");
                    sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
                    sb.append("提供者    : " + location.getProvider() + "\n");

                    sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
                    sb.append("角    度    : " + location.getBearing() + "\n");
                    // 获取当前提供定位服务的卫星个数
                    sb.append("星    数    : " + location.getSatellites() + "\n");
                    sb.append("国    家    : " + location.getCountry() + "\n");
                    sb.append("省            : " + location.getProvince() + "\n");
                    sb.append("市            : " + location.getCity() + "\n");
                    sb.append("城市编码 : " + location.getCityCode() + "\n");
                    sb.append("区            : " + location.getDistrict() + "\n");
                    sb.append("区域 码   : " + location.getAdCode() + "\n");
                    sb.append("地    址    : " + location.getAddress() + "\n");
                    sb.append("兴趣点    : " + location.getPoiName() + "\n");
                    //定位完成的时间
                    sb.append("定位时间: " + TimeUitls.getCurrentTime(2, null) + "\n");
                } else {
                    //定位失败
                    sb.append("定位失败" + "\n");
                    sb.append("错误码:" + location.getErrorCode() + "\n");
                    sb.append("错误信息:" + location.getErrorInfo() + "\n");
                    sb.append("错误描述:" + location.getLocationDetail() + "\n");
                }
                sb.append("***定位质量报告***").append("\n");
                sb.append("* WIFI开关：").append(location.getLocationQualityReport().isWifiAble() ? "开启" : "关闭").append("\n");
                sb.append("* GPS状态：").append(getGPSStatusString(location.getLocationQualityReport().getGPSStatus())).append("\n");
                sb.append("* GPS星数：").append(location.getLocationQualityReport().getGPSSatellites()).append("\n");
                sb.append("* 网络类型：" + location.getLocationQualityReport().getNetworkType()).append("\n");
                sb.append("* 网络耗时：" + location.getLocationQualityReport().getNetUseTime()).append("\n");
                sb.append("****************").append("\n");
                //定位之后的回调时间
                sb.append("回调时间: " + TimeUitls.getCurrentTime(2, null) + "\n");

                //解析定位结果，
                String result = sb.toString();
                Log.e(TAG, "解析定位结果 = " + result);
                tvLatlng.setText("经度: " + location.getLongitude() + ",  纬度: " + location.getLatitude());
                tvAddress.setText("地址: " + location.getAddress());
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                aMap.addMarker(getMarkerOption("点位", location.getLatitude(), location.getLongitude()));
            } else {
                Log.e(TAG, "定位失败，loc is null");
            }
        }
    };

    /**
     * 自定义图标
     *
     * @return
     */
    public MarkerOptions getMarkerOption(String str, double lat, double lgt) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.location_marker));
        markerOptions.position(new LatLng(lat, lgt));
        markerOptions.title(str);
        markerOptions.snippet("纬度:" + lat + "   经度:" + lgt);
        markerOptions.period(100);

        return markerOptions;
    }


    /**
     * 获取GPS状态的字符串
     *
     * @param statusCode GPS状态码
     * @return
     */
    private String getGPSStatusString(int statusCode) {
        String str = "";
        switch (statusCode) {
            case AMapLocationQualityReport.GPS_STATUS_OK:
                str = "GPS状态正常";
                break;
            case AMapLocationQualityReport.GPS_STATUS_NOGPSPROVIDER:
                str = "手机中没有GPS Provider，无法进行GPS定位";
                break;
            case AMapLocationQualityReport.GPS_STATUS_OFF:
                str = "GPS关闭，建议开启GPS，提高定位质量";
                break;
            case AMapLocationQualityReport.GPS_STATUS_MODE_SAVING:
                str = "选择的定位模式中不包含GPS定位，建议选择包含GPS定位的模式，提高定位质量";
                break;
            case AMapLocationQualityReport.GPS_STATUS_NOGPSPERMISSION:
                str = "没有GPS定位权限，建议开启gps定位权限";
                break;
        }
        return str;
    }

    /**
     * 开始定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void startLocation() {
        // 启动定位
        locationClient.startLocation();
    }

    /**
     * 停止定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void stopLocation() {
        // 停止定位
        locationClient.stopLocation();
    }

    /**
     * 销毁定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void destroyLocation() {
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    /**
     * 屏幕中心marker(ImageView) 跳动
     */
    private void startJumpAnimation() {
        if (imageScreenCenterMarker != null) {
            MyLog.e(TAG, TAG + "---来个动画");
            Animation animation = new TranslateAnimation(0, 0, -UIUtils.dp2px(getApplicationContext(), 68), 0);
            //越来越快
            animation.setInterpolator(new AccelerateInterpolator());
            //整个移动所需要的时间
            animation.setDuration(200);
            //设置动画
            imageScreenCenterMarker.setAnimation(animation);
            //开始动画
            imageScreenCenterMarker.startAnimation(animation);
        } else {
            Log.e(TAG, TAG + " imageScreenCenter is null");
        }
    }


    @OnClick(R.id.imageView_location)
    public void onViewClicked() {
//        startLocation();
//        GeocodeQuery query2 = new GeocodeQuery("深圳市罗湖区解放路4018号", "深圳市");
//        GeocodeQuery query2 = new GeocodeQuery("深圳市罗湖区清水河街道清水河村3栋深业进元大厦", "深圳市");
        GeocodeQuery query2 = new GeocodeQuery("深圳市罗湖区桂园街道深圳市公安局", "深圳市");
        geocodeSearch.getFromLocationNameAsyn(query2);
    }

    @Override
    public void onMapLoaded() {
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        //地图状态发生变化
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        startJumpAnimation();
        //地图状态发生变化结束
        Log.e(TAG, "地图状态发生变化结束: " + cameraPosition.toString());
        // 第一个参数表示一个Latlng(经纬度)，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
//        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(cameraPosition.target.latitude, cameraPosition.target.longitude), 200,GeocodeSearch.AMAP);
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(cameraPosition.target.latitude, cameraPosition.target.longitude), 10,GeocodeSearch.AMAP);
        geocodeSearch.getFromLocationAsyn(query);

        //address表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode都ok
//        GeocodeQuery query2 = new GeocodeQuery("深圳市罗湖区清水河街道清水河村3栋深业进元大厦", "深圳市");
//        geocodeSearch.getFromLocationNameAsyn(query2);
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        //逆地理编码(坐标转地址)
        Log.e(TAG, "onRegeocodeSearched: 逆地理编码(坐标转地址) = " + regeocodeResult);
        if (regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null) {
            RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
            Log.e(TAG, "onRegeocodeSearched: " + regeocodeAddress.getCity() + ", " + regeocodeAddress.getDistrict() + ", " + regeocodeAddress.getBuilding());
            Log.e(TAG, "onRegeocodeSearched: getFormatAddress = " + regeocodeAddress.getFormatAddress());
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        //地理编码(地址转坐标)
        Log.e(TAG, "onGeocodeSearched: 地理编码(地址转坐标) = " + geocodeResult);
        if (geocodeResult != null) {
            LatLonPoint latLonPoint = geocodeResult.getGeocodeAddressList().get(0).getLatLonPoint();
            Log.e(TAG, "onGeocodeSearched:地理编码 latlng = " + latLonPoint.getLatitude() + ", " + latLonPoint.getLongitude());
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude())));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyLocation();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        imageScreenCenterMarker.clearAnimation();
        unbinder.unbind();
    }
}
