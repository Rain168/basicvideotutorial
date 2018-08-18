package me.yangzhe.basicvideotutorial;

import android.app.Application;

public class APP extends Application {
    public static APP mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static APP getContext() {
        return mContext;
    }
}
