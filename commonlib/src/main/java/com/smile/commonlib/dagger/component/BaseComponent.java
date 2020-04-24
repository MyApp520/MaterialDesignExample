package com.smile.commonlib.dagger.component;

import android.content.Context;

import com.smile.commonlib.arouter.interceptor.LoginInterceptor;
import com.smile.commonlib.bean.UserBean;
import com.smile.commonlib.dagger.module.BaseModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by smile on 2019/3/18.
 */
@Singleton
@Component(modules = {BaseModule.class})
public interface BaseComponent {
    void inject(LoginInterceptor loginInterceptor);

    Context getContext();

    UserBean getUserBean();
}
