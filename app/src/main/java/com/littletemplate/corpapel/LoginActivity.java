package com.littletemplate.corpapel;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.littletemplate.corpapel.apis.FacebookApi;
import com.littletemplate.corpapel.app.BaseActivity;

import java.util.Arrays;

import butterknife.OnClick;


public class LoginActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {
    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    private boolean isMainLobbyStarted = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (FacebookApi.conectado())
            startActivity(new Intent(this, MainActivity.class)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        /*SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setScopes(gso.getScopeArray());
        findViewById(R.id.sign_in_button).setOnClickListener(this);*/

        //DBConnection.GetTiendas(this);
    }

    @OnClick(R.id.buttonlogin)
    public void iniciarSesion() { startActivity(new Intent(this, MainActivity.class)); }

    @OnClick(R.id.buttonregistro)
    public void iARegistro() { startActivity(new Intent(this, RegisterActivity.class)); }

    @OnClick(R.id.login_button)
    public void iniciarSesionFACEBOOK() {
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                isMainLobbyStarted=false;
                Intent mainLobby = new Intent(LoginActivity.this, MainActivity.class);
                if(!isMainLobbyStarted) {
                    startActivity(mainLobby);
                    isMainLobbyStarted = true;
                }
            }

            @Override
            public void onCancel() { }

            @Override
            public void onError(FacebookException error) {}
        });
    }

    @OnClick(R.id.sign_in_button)
    public void iniciarSesionGOOGLE() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, 9001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 9001) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            isMainLobbyStarted=false;
            Intent mainLobby = new Intent(LoginActivity.this, MainActivity.class);
            if(!isMainLobbyStarted) {
                startActivity(mainLobby);
                isMainLobbyStarted = true;
            }
        } else {
            // Signed out, show unauthenticated UI.
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) { }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            this.moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
