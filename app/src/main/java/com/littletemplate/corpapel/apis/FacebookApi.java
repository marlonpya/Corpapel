package com.littletemplate.corpapel.apis;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

/**
 * Created by ucweb02 on 27/12/2016.
 */

public class FacebookApi {

    public static boolean conectado() {
        return AccessToken.getCurrentAccessToken() != null;
    }

    public static void cerrarSesion() {
        LoginManager.getInstance().logOut();
    }
}
