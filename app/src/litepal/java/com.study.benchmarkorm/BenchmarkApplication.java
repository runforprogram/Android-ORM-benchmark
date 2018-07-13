package com.study.benchmarkorm;


import android.app.Application;

import org.litepal.LitePal;


public class BenchmarkApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
    }

}
