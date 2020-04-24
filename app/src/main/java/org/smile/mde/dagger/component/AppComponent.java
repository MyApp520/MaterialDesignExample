package org.smile.mde.dagger.component;

import com.smile.commonlib.dagger.component.BaseComponent;
import com.smile.commonlib.dagger.scope.AppScope;

import org.smile.mde.MyApplication;
import org.smile.mde.module.AppActivityModule;

import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * Created by smile on 2019/3/20.
 */

@AppScope
@Component(modules = {
        AndroidInjectionModule.class,
        AndroidSupportInjectionModule.class,
        AppActivityModule.class}, dependencies = {BaseComponent.class})
public interface AppComponent {
    void inject(MyApplication myApplication);
}
