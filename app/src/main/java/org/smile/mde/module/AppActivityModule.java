package org.smile.mde.module;

import com.example.commonlib.dagger.scope.ActivityScope;

import org.smile.mde.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by smile on 2019/3/20.
 * 注意：
 *      1、主模块（即app模块）所有可能使用dagger2注入实例的activity，都要这个类里面声明；
 *      2、其他子模块可能使用dagger2注入实例的activity，在子模块相应的module类声明后，需要将module类通过includes的方式包含到此类来；
 */
@Module
public abstract class AppActivityModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = {AppFragmentModule.class})
    abstract MainActivity provideMainActivity();
}
