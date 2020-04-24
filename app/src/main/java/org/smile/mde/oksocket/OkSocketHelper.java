package org.smile.mde.oksocket;

import android.util.Log;

import com.smile.commonlib.util.AppDebugUtil;
import com.xuhao.didi.core.iocore.interfaces.IPulseSendable;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;
import com.xuhao.didi.socket.client.sdk.client.connection.DefaultReconnectManager;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;
import com.xuhao.didi.socket.server.impl.OkServerOptions;

import org.json.JSONException;
import org.json.JSONObject;
import org.smile.mde.oksocket.bean.LoginSendData;
import org.smile.mde.oksocket.bean.PulseData;
import org.smile.mde.oksocket.constant.SocketServerConstant;
import org.smile.mde.oksocket.protocol.FirstReaderProtocol;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class OkSocketHelper {

    private final String TAG = OkSocketHelper.class.getSimpleName();

    private static OkSocketHelper mInstance;

    private IConnectionManager mIConnectionManager;

    private Disposable mSendHeartBagDisposable;

    private OkSocketHelper() {
    }

    public static OkSocketHelper getInstance() {
        if (mInstance == null) {
            synchronized (OkSocketHelper.class) {
                if (mInstance == null) {
                    OkServerOptions.setIsDebug(AppDebugUtil.isDebug());
                    OkSocketOptions.setIsDebug(AppDebugUtil.isDebug());
                    mInstance = new OkSocketHelper();
                }
            }
        }
        return mInstance;
    }

    /**
     * 建立Socket连接
     */
    public void connectSocket() {
        // 连接参数设置(IP,端口号),这也是一个连接的唯一标识,不同连接,该参数中的两个值至少有其一不一样
        // ("190.15.240.22", 19000)  ("192.168.3.101", 8080)
        ConnectionInfo info = new ConnectionInfo("190.15.240.22", 19000);

        // 获得当前连接通道的参数配置对象
        OkSocketOptions okSocketOptions = OkSocketOptions.getDefault();
        // 基于当前参数配置对象构建一个参配建造者类
        // 连接通道OkSocketOptions的参数都是私有的，如果需要修改，就必须通过OkSocketOptions.Builder来改
        OkSocketOptions.Builder builder = new OkSocketOptions.Builder(okSocketOptions);
        builder.setReaderProtocol(new FirstReaderProtocol());
        builder.setReconnectionManager(new DefaultReconnectManager());
        builder.setConnectTimeoutSecond(10);
        builder.setMaxReadDataMB(15);
        builder.setWritePackageBytes(1024);


        // 调用OkSocket,开启这次连接的通道,拿到通道Manager
        mIConnectionManager = OkSocket.open(info);
        // 建造一个新的参配对象并且付给通道
        mIConnectionManager.option(builder.build());
        // 注册Socket行为监听器, SocketActionAdapter是回调的Simple类, 其他回调方法请参阅类文档
        mIConnectionManager.registerReceiver(mSocketActionAdapter);
        // 调用通道进行连接
        mIConnectionManager.connect();
    }

    public void disConnectSocket() {
        stopSendHeartBag();
        if (mIConnectionManager != null) {
            mIConnectionManager.disconnect();
        }
    }

    private void loginAuth() {
//        mIConnectionManager.send(new TestSendData());
        LoginSendData loginSendData = new LoginSendData(SocketServerConstant.RequestCode.REQUEST_TYPE_LOGIN_AUTH, "101461"
                , "mobileVideo:login:app:25ec08e3408f4217b528332fd1c403db"
                , "test_imei_234820xcfklx", "etrkhDsQnULXbTaFuA9");
        mIConnectionManager.send(loginSendData);
    }

    /**
     * 给服务端发送心跳包
     */
    private void sendHeartBagToServer() {
        mSendHeartBagDisposable = Observable.interval(0, 6, TimeUnit.SECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.e(TAG, "给服务端发送心跳包 accept: along = " + aLong);
                        PulseData pulseData = new PulseData(SocketServerConstant.RequestCode.REQUEST_TYPE_HEARTBEAT_BAG);
                        mIConnectionManager.send(pulseData);
                    }
                });
    }

    /**
     * 停止发送心跳包
     */
    private void stopSendHeartBag() {
        if (mSendHeartBagDisposable != null && !mSendHeartBagDisposable.isDisposed()) {
            mSendHeartBagDisposable.dispose();
        }
        mSendHeartBagDisposable = null;
    }

    /**
     * Socket行为监听器
     */
    private SocketActionAdapter mSocketActionAdapter = new SocketActionAdapter() {

        @Override
        public void onSocketIOThreadStart(String s) {
            Log.e(TAG, "onSocketIOThreadStart: s = " + s);
        }

        @Override
        public void onSocketIOThreadShutdown(String s, Exception e) {
            Log.e(TAG, "onSocketIOThreadShutdown: s = " + s);
            e.printStackTrace();
        }

        @Override
        public void onSocketReadResponse(ConnectionInfo connectionInfo, String s, OriginalData data) {
            Log.e(TAG, "onSocketReadResponse 读取成功: info = " + connectionInfo);
            Log.e(TAG, "onSocketReadResponse: s = " + s);
            Log.e(TAG, "onSocketReadResponse: getHeadBytes = " + data.getHeadBytes().length);
            Log.e(TAG, "onSocketReadResponse: getBodyBytes = " + data.getBodyBytes().length);
            String strHeader = new String(data.getHeadBytes(), Charset.forName("utf-8"));
            String strBody = new String(data.getBodyBytes(), Charset.forName("utf-8"));
            Log.e(TAG, "onSocketReadResponse: strHeader = " + strHeader + ", strBody = " + strBody);

            String responseJsonContent = new String(data.getBodyBytes(), Charset.forName("utf-8"));
            Log.e(TAG, "接收到的数据: responseJsonContent = " + responseJsonContent);
            try {
                JSONObject jsonObject = new JSONObject(responseJsonContent);
                // 根据code值判断推送的数据类型
                String code = jsonObject.getString("code");
                if (SocketServerConstant.ResponseCode.RESPONSE_TYPE_LOGIN_AUTH_SUCCESS.equals(code)) {
                    // 登录成功，主动给服务端发送心跳包
                    sendHeartBagToServer();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSocketWriteResponse(ConnectionInfo connectionInfo, String s, ISendable iSendable) {
            Log.e(TAG, "onSocketWriteResponse: 数据发出去了 s = " + s);
            String str = new String(iSendable.parse(), Charset.forName("utf-8"));
            Log.e(TAG, "onSocketWriteResponse: 给socket服务端发的数据 = " + str);
            Log.e(TAG, "onSocketWriteResponse: 给socket服务端发的数据大小 = " + str.getBytes(Charset.forName("utf-8")).length);
        }

        @Override
        public void onPulseSend(ConnectionInfo connectionInfo, IPulseSendable iPulseSendable) {
            Log.e(TAG, "onPulseSend: 心跳 iPulseSendable = " + iPulseSendable);
        }

        @Override
        public void onSocketDisconnection(ConnectionInfo connectionInfo, String s, Exception e) {
            Log.e(TAG, "onSocketDisconnection: 断开连接 s = " + s);
            e.printStackTrace();
            stopSendHeartBag();
        }

        @Override
        public void onSocketConnectionSuccess(ConnectionInfo connectionInfo, String s) {
            Log.e(TAG, "onSocketConnectionSuccess: 连接成功了 s = " + s);
            loginAuth();
        }

        @Override
        public void onSocketConnectionFailed(ConnectionInfo connectionInfo, String s, Exception e) {
            Log.e(TAG, "onSocketConnectionFailed: 连接失败 s = " + s);
            e.printStackTrace();
            stopSendHeartBag();
        }
    };
}
