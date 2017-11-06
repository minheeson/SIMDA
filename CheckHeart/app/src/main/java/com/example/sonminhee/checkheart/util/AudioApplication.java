package com.example.sonminhee.checkheart.util;

import android.app.Application;

/**
 * Created by sonminhee on 2017. 10. 9..
 */

public class AudioApplication extends Application {
    private static AudioApplication mInstance;
    private IAudioService mInterface;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mInterface = new IAudioService(getApplicationContext());
    }

    public static AudioApplication getInstance() {
        return mInstance;
    }

    public IAudioService getServiceInterface() {
        return mInterface;
    }

}
