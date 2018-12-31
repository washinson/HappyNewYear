package com.washinson.happynewyear;

import android.app.Application;

import com.vk.sdk.VKSdk;

public class VKApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        VKSdk.initialize(this);
    }
}
