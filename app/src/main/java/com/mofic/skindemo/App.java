package com.mofic.skindemo;

import android.app.Application;

import com.mofic.skindemo.skin.SkinManager;
import com.squareup.leakcanary.LeakCanary;

/**
 * @author lanweining
 * @Date 2018/4/9 上午9:16
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        SkinManager.getInstance().init(this);

    }
}
