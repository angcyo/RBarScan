package com.angcyo.rbarscan;

import android.app.Application;

import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.HawkBuilder;

/**
 * Created by angcyo on 15-11-06-006.
 */
public class RApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Hawk.init(this).setEncryptionMethod(HawkBuilder.EncryptionMethod.NO_ENCRYPTION).build();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
