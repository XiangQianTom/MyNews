package com.si.mynews.di.module;

import android.app.Activity;

import com.si.mynews.di.ActivityScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by si on 16/8/7.
 */

@Module
public class ActivityModule {
    private Activity mActivity;

    public ActivityModule(Activity activity) {
        this.mActivity = activity;
    }

    @Provides
    @ActivityScope
    public Activity provideActivity() {
        return mActivity;
    }
}
