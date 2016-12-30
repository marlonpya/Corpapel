package com.littletemplate.corpapel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.text.LocaleDisplayNames;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
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
import com.littletemplate.corpapel.model.Usuario;
import com.littletemplate.corpapel.util.ConexionBroadcastReceiver;
import com.littletemplate.corpapel.util.Constante;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;


public class IniciarSesionActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {
    public static final String TAG = IniciarSesionActivity.class.getSimpleName();
    @BindView(R.id.activity_login) RelativeLayout layout;
    @BindView(R.id.etCorreoIngresar) EditText etCorreo;
    @BindView(R.id.etPasswordIngresar) EditText etPassword;
    @BindView(R.id.btnIngresarFB) LoginButton loginButton;

    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Usuario.getUsuario() != null) {
            if (Usuario.getUsuario().isSesion())
                startActivity(new Intent(this, PrincipalActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        if (FacebookApi.conectado())
            startActivity(new Intent(this, PrincipalActivity.class)
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
        callbackManager = CallbackManager.Factory.create();
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
                requestIniciarSesion(etCorreo.getText().toString().trim(), etPassword.getText().toString().trim());
            } else {
                ConexionBroadcastReceiver.showSnack(layout, this);
            }
        } else {
            Toast.makeText(this, R.string.ingresar_todos_datos, Toast.LENGTH_SHORT).show();
        }
    }

    private void requestIniciarSesion(final String correo, final String password) {
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
                            int codigo = jsonObject.getInt("codigo");
                            progressDialog.hide();
                            mostrarToastYCrearUsuario(codigo, jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, e.toString(), e);
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
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("correo", correo);
                params.put("password",password);
                Log.d(TAG, params.toString());
                return params;
            }
        };
        Configuracion.getInstancia().addRequestQueue(request, TAG);
    }

    private void mostrarToastYCrearUsuario(int codigo, JSONObject jsonObject) throws JSONException {
        String texto = "No se recibieron los datos necesarios";
        switch (codigo) {
            case -3 : texto = "Correo Incorrecto"; break;
            case -4 : texto = "Password Incorrecto"; break;
            case 200 : texto = "Sesión iniciada satisfactoriamente"; break;
        }
        Toast.makeText(getApplicationContext(), texto, Toast.LENGTH_SHORT).show();
        if (codigo == 200) {
            JSONObject jData = jsonObject.getJSONObject("data");
            JSONObject jUsuario = jData.getJSONObject("usuario");
            Usuario usuario = new Usuario();
            usuario.setNombres(!jUsuario.getString("USU_NOMBRE").equals("null")                 ? jUsuario.getString("USU_NOMBRE") : "");
            usuario.setNombre_empresa(!jUsuario.getString("USU_NOMBRE_EMPRESA").equals("null")  ? jUsuario.getString("USU_NOMBRE_EMPRESA") : "");
            usuario.setDireccion(!jUsuario.getString("USU_DIRECCION").equals("null")            ? jUsuario.getString("USU_DIRECCION") : "");
            usuario.setDepartamento(!jUsuario.getString("USU_DEPARTAMENTO").equals("null")      ? jUsuario.getString("USU_DEPARTAMENTO") : "");
            usuario.setProvincia(!jUsuario.getString("USU_PROVINCIA").equals("null")            ? jUsuario.getString("USU_PROVINCIA") : "");
            usuario.setDistrito(!jUsuario.getString("USU_DISTRITO").equals("null")              ? jUsuario.getString("USU_DISTRITO") : "");
            usuario.setCorreo(!jUsuario.getString("USU_CORREO").equals("null")                  ? jUsuario.getString("USU_CORREO") : "");
            usuario.setMovil(!jUsuario.getString("USU_TELEFONO").equals("null")                 ? jUsuario.getString("USU_TELEFONO") : "");
            usuario.setImagen(!jUsuario.getString("USU_IMAGEN").equals("null")                  ? jUsuario.getString("USU_IMAGEN") : "");
            Usuario.crearSesion(usuario);
            Log.d(TAG, usuario.toString());
            startActivity(new Intent(this, PrincipalActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
    }

    @OnClick(R.id.buttonregistro)
    public void iARegistro() { startActivity(new Intent(this, RegistroActivity.class)); }

    @OnClick(R.id.btnIngresarFB)
    public void iniciarSesionFACEBOOK() {
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        obteneDatosFB(object);
                        Log.d(TAG, object.toString());
                    }
                });
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() { }

            @Override
            public void onError(FacebookException error) { Log.e(TAG, error.toString(), error);}
        });
    }

    private void obteneDatosFB(JSONObject object) {
        String id = "";
        String foto = "";
        String nombre = "";
        String apellido = "";
        String correo = "";
        try {
            id = object.getString("id");
            try {
                URL url = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                foto = url.toString();
                Log.d(TAG, url.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            if (object.has("first_name")) nombre = object.getString("first_name");
            if (object.has("last_name")) apellido = object.getString("last_name");
            if (object.has("email")) correo = object.getString("email");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String[] array = new String[] {foto, nombre, apellido, correo};
        Log.d(TAG, Arrays.toString(array));
        if (!correo.isEmpty()) requestIniciarSesionFB(correo, "facebook", nombre+" "+apellido);
        else {
            Toast.makeText(this, R.string.permiso_correo_error, Toast.LENGTH_LONG).show();
            FacebookApi.cerrarSesion();
        }
    }

    private void requestIniciarSesionFB(final String correo, final String red_social, final String nombre) {
        progressDialog.show();
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constante.URL_INICIAR_SESION_REDSOCIAL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int codigo = jsonObject.getInt("codigo");
                            if (codigo == 200) {
                                JSONObject jData = jsonObject.getJSONObject("data");
                                JSONObject jUsuario = jData.getJSONObject("usuario");
                                Usuario usuario = new Usuario();
                                usuario.setNombres(!jUsuario.getString("USU_NOMBRE").equals("null")               ? jUsuario.getString("USU_NOMBRE") : "");
                                usuario.setNombre_empresa(!jUsuario.getString("USU_NOMBRE_EMPRESA").equals("null")? jUsuario.getString("USU_NOMBRE_EMPRESA") : "");
                                usuario.setDireccion(!jUsuario.getString("USU_DIRECCION").equals("null")          ? jUsuario.getString("USU_DIRECCION") : "");
                                usuario.setDepartamento(!jUsuario.getString("USU_DEPARTAMENTO").equals("null")    ? jUsuario.getString("USU_DEPARTAMENTO") : "");
                                usuario.setProvincia(!jUsuario.getString("USU_PROVINCIA").equals("null")          ? jUsuario.getString("USU_PROVINCIA") : "");
                                usuario.setDistrito(!jUsuario.getString("USU_DISTRITO").equals("null")            ? jUsuario.getString("USU_DISTRITO") : "");
                                usuario.setCorreo(!jUsuario.getString("USU_CORREO").equals("null")                ? jUsuario.getString("USU_CORREO") : "");
                                usuario.setMovil(!jUsuario.getString("USU_TELEFONO").equals("null")               ? jUsuario.getString("USU_TELEFONO") : "");
                                usuario.setImagen(!jUsuario.getString("USU_IMAGEN").equals("null")                ? jUsuario.getString("USU_IMAGEN") : "");
                                Usuario.crearSesion(usuario);
                                Log.d(TAG, usuario.toString());
                                progressDialog.hide();
                                startActivity(new Intent(IniciarSesionActivity.this, PrincipalActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            } else if (codigo == -4) {
                                startActivity(new Intent(IniciarSesionActivity.this, RegistroActivity.class)
                                .putExtra(Constante.S_REGISTRO_NOMBRE, nombre)
                                .putExtra(Constante.S_REGISTRO_CORREO, correo));
                                progressDialog.hide();
                            } else {
                                progressDialog.hide();
                                Toast.makeText(IniciarSesionActivity.this, R.string.conexion_error, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, e.toString(), e);
                            progressDialog.hide();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e(error.toString());
                        progressDialog.hide();
                        progressDialog.hide();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("correo", correo);
                params.put("red_social", red_social);
                return params;
            }
        };
        Configuracion.getInstancia().addRequestQueue(request, TAG);
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
            Intent mainLobby = new Intent(IniciarSesionActivity.this, PrincipalActivity.class);
        } else {
            // Signed out, show unauthenticated UI.
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) { }

    /*public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            this.moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/

    private boolean validarIniciarSesion() {
        return !TextUtils.isEmpty(etCorreo.getText().toString().trim()) ||
                !TextUtils.isEmpty(etPassword.getText().toString().trim());
    }
}
