package org.musalahuddin.myexpenseorganizer;

import android.app.Application;

public class MyApplication extends Application {

    private static MyApplication mSelf;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mSelf = this;
    }

    public static MyApplication getInstance() {
        return mSelf;
    }
}