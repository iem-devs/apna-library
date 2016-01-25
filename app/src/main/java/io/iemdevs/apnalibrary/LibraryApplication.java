package io.iemdevs.apnalibrary;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Application Class of the app
 * Will be used to intialize stuff such as
 * Facebook's Stetho, Fabric Crashlytics, etc
 */
public class LibraryApplication extends Application {

    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
