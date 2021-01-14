package com.example.anzu;

import android.app.Application;

public class MyApplication extends Application {
    private String uid;
    private String LogoPath;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLogoPath() {
        return LogoPath;
    }

    public void setLogoPath(String logoPath) {
        LogoPath = logoPath;
    }
    //    @Override
//    public void onCreate() {
//        super.onCreate();
//    }
}
