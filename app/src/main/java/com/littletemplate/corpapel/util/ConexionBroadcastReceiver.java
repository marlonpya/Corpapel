package com.littletemplate.corpapel.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.littletemplate.corpapel.R;
import com.littletemplate.corpapel.app.Configuracion;

/**
 * Created by ucweb02 on 27/12/2016.
 */

public class ConexionBroadcastReceiver extends BroadcastReceiver{
    private static ConexionListener conexionListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cl = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cl.getActiveNetworkInfo();
        boolean estado = info != null && info.isConnectedOrConnecting();
        if (conexionListener != null) conexionListener.onNetworkConnectionChanged(estado);
    }

    public static boolean isConect() {
        ConnectivityManager manager = (ConnectivityManager) Configuracion.getInstancia().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    private interface ConexionListener {
        void onNetworkConnectionChanged(boolean contect);
    }

    public static void showSnack(View view, Context context) {
        String message = context.getString(R.string.internet_error);
        int color = Color.WHITE;
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }
}
