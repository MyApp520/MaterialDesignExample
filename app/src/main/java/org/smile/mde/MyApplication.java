package org.smile.mde;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.Context;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.baidu.mapapi.SDKInitializer;
import com.smile.commonlib.base.BaseApplication;
import com.smile.commonlib.bean.UserBean;
import com.smile.commonlib.util.AppDebugUtil;

import org.smile.mde.dagger.component.DaggerAppComponent;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasBroadcastReceiverInjector;
import dagger.android.HasContentProviderInjector;
import dagger.android.HasServiceInjector;

/**
 * Created by smile on 2019/3/12.
 */

public class MyApplication extends BaseApplication implements HasActivityInjector, HasServiceInjector, HasBroadcastReceiverInjector, HasContentProviderInjector {

    private final String TAG = getClass().getSimpleName();

    @Inject
    DispatchingAndroidInjector<Activity> activityInjector;
    @Inject
    DispatchingAndroidInjector<BroadcastReceiver> broadcastReceiverInjector;
    @Inject
    DispatchingAndroidInjector<Service> serviceInjector;
    @Inject
    DispatchingAndroidInjector<ContentProvider> contentProviderInjector;

    @Inject
    UserBean userBean;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppDebugUtil.syncIsDebug(getApplicationContext());
        DaggerAppComponent.builder().baseComponent(getBaseComponent()).build().inject(this);
        SDKInitializer.initialize(getApplicationContext());
        Log.e(TAG, "MyApplication onCreate: userBean = " + userBean);
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityInjector;
    }

    @Override
    public AndroidInjector<BroadcastReceiver> broadcastReceiverInjector() {
        return broadcastReceiverInjector;
    }

    @Override
    public AndroidInjector<Service> serviceInjector() {
        return serviceInjector;
    }

    @Override
    public AndroidInjector<ContentProvider> contentProviderInjector() {
        return contentProviderInjector;
    }
}
