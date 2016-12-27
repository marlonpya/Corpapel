package com.littletemplate.corpapel.app;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by ucweb02 on 27/12/2016.
 */

public class Configuracion extends Application {
    private static final String TAG = Configuracion.class.getSimpleName();
    private RequestQueue requestQueue;
    public static Configuracion mInstancia;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstancia = this;

        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .modules(Realm.getDefaultModule())
                .name("corpapel.db")
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

    public static synchronized Configuracion getInstancia() {
        return mInstancia;
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) requestQueue = Volley.newRequestQueue(getApplicationContext());
        return requestQueue;
    }

    public <T> void addRequestQueue(Request<T> request, String tag) {
        request.setTag(tag.isEmpty() ? TAG : tag);
        getRequestQueue().add(request);
    }

    public <T> void addRequestQueue(Request<T> request) {
        request.setTag(TAG);
        getRequestQueue().add(request);
    }
}
