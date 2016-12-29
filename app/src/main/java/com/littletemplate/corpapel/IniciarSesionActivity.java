package com.littletemplate.corpapel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
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
import com.littletemplate.corpapel.app.Configuracion;
import com.littletemplate.corpapel.util.ConexionBroadcastReceiver;
import com.littletemplate.corpapel.util.Constante;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;


public class IniciarSesionActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {
    public static final String TAG = IniciarSesionActivity.class.getSimpleName();
    @BindView(R.id.activity_login) RelativeLayout layout;
    @BindView(R.id.etNombreIngresar) EditText etNombre;
    @BindView(R.id.etPasswordIngresar) EditText etPassword;

    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    private boolean isMainLobbyStarted = false;
    private ProgressDialog progressDialog;

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
        iniciarProgresDialog();
    }

    private void iniciarProgresDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.enviando_peticion));
        progressDialog.setCancelable(false);
    }

    @OnClick(R.id.btnIniciarSesion)
    public void iniciarSesion() {
        if (validarIniciarSesion()) {
            if (ConexionBroadcastReceiver.isConect()) {
                requestIniciarSesion();
            } else {
                ConexionBroadcastReceiver.showSnack(layout, this);
            }
        } else {
            Toast.makeText(this, R.string.ingresar_todos_datos, Toast.LENGTH_SHORT).show();
        }
    }

    private void requestIniciarSesion() {
        progressDialog.show();
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constante.URL_INICIAR_SESION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            progressDialog.hide();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.hide();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        VolleyLog.e(error.toString());
                        progressDialog.hide();
                    }
                }
        );
        Configuracion.getInstancia().addRequestQueue(request, TAG);
    }

    @OnClick(R.id.buttonregistro)
    public void iARegistro() { startActivity(new Intent(this, RegistroActivity.class)); }

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
                Intent mainLobby = new Intent(IniciarSesionActivity.this, MainActivity.class);
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
            Intent mainLobby = new Intent(IniciarSesionActivity.this, MainActivity.class);
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


    private boolean validarIniciarSesion() {
        return !TextUtils.isEmpty(etNombre.getText().toString().trim()) ||
                !TextUtils.isEmpty(etPassword.getText().toString().trim());
    }
}
