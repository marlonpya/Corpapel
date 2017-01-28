package com.littletemplate.corpapel;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.littletemplate.corpapel.apis.FacebookApi;
import com.littletemplate.corpapel.app.BaseActivity;
import com.littletemplate.corpapel.app.Configuracion;
import com.littletemplate.corpapel.model.Departamento;
import com.littletemplate.corpapel.model.Distrito;
import com.littletemplate.corpapel.model.Provincia;
import com.littletemplate.corpapel.model.Usuario;
import com.littletemplate.corpapel.util.ConexionBroadcastReceiver;
import com.littletemplate.corpapel.util.Constante;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;
import io.realm.Realm;
import io.realm.RealmResults;

public class RegistroActivity extends BaseActivity {
    public static final String TAG = RegistroActivity.class.getSimpleName();
    @BindView(R.id.activity_register) RelativeLayout layout;
    @BindView(R.id.etNombresRegistro) EditText etNombres;
    @BindView(R.id.etPasswordRegistro) EditText etPassword;
    @BindView(R.id.etNombresEmpresaRegistro) EditText etNombresEmpresa;
    @BindView(R.id.etDireccionRegistro) EditText etDireccion;
    @BindView(R.id.etDepartamento) TextView etDepartamento;
    @BindView(R.id.etProvincia) TextView etProvincia;
    @BindView(R.id.etDistrito) TextView etDistrito;
    @BindView(R.id.btnDepartamento) LinearLayout btnDepartamento;
    @BindView(R.id.btnProvincia) LinearLayout btnProvincia;
    @BindView(R.id.btnDistrito) LinearLayout btnDistrito;
    @BindView(R.id.etCorreoRegistro) EditText etCorreo;
    @BindView(R.id.etTelefonoRegistro) EditText etTelefono;
    private ProgressDialog progressDialog;
    private int idDep=-1, idPro=-1, idDis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        iniciarProgresDialog();
        if (Departamento.getUltimoId() == 0){
            if (ConexionBroadcastReceiver.isConect()){
                requestDepartamento();
                requestProvincia();
                requestDistrito();
            }
            else {
                ConexionBroadcastReceiver.showSnack(layout, this);
            }
        }
        else{
            //llenarDepartamento();
        }

        if (getIntent().hasExtra(Constante.S_REGISTRO_CORREO) && getIntent().hasExtra(Constante.S_REGISTRO_NOMBRE)) {
            etCorreo.setText(getIntent().getStringExtra(Constante.S_REGISTRO_CORREO));
            etNombres.setText(getIntent().getStringExtra(Constante.S_REGISTRO_NOMBRE));
        }
    }

    private void requestProvincia() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constante.URL_LISTAR_PROVINCIA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jArray = jsonObject.getJSONArray("provincia");
                            Realm realm = Realm.getDefaultInstance();
                            for (int i = 0; i < jArray.length(); i++) {
                                realm.beginTransaction();
                                Provincia provincia = realm.createObject(Provincia.class, Provincia.getUltimoId());
                                provincia.setIdServer(jArray.getJSONObject(i).getInt("idProvincia"));
                                provincia.setFkDepartamento(jArray.getJSONObject(i).getInt("idDepartamento"));
                                provincia.setNomProvincia(jArray.getJSONObject(i).getString("nom_provincia"));
                                realm.copyToRealm(provincia);
                                realm.commitTransaction();
                            }
                            realm.close();
                            Log.d(TAG, jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, e.toString(), e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        VolleyLog.e(error.toString());
                    }
                }

        );
        Configuracion.getInstancia().addRequestQueue(request, TAG);
    }

    private void iniciarProgresDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.enviando_peticion));
        progressDialog.setCancelable(false);
    }

    @OnClick(R.id.btnRegistrarse)
    public void registrarUsuario() {
        if (validarDatos()) {
            if (ConexionBroadcastReceiver.isConect()) {
                requestRegistrarse();
            } else {
                ConexionBroadcastReceiver.showSnack(layout, this);
            }
        } else {
            Toast.makeText(this, R.string.ingresar_todos_datos, Toast.LENGTH_SHORT).show();
        }
    }

    private void requestDepartamento() {
        progressDialog.show();
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constante.URL_LISTAR_DEPARTAMENTO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jArray = jsonObject.getJSONArray("departamento");
                            Realm realm = Realm.getDefaultInstance();
                            for (int i = 0; i < jArray.length(); i++) {
                                realm.beginTransaction();
                                Departamento departamento = realm.createObject(Departamento.class, Departamento.getUltimoId());
                                departamento.setIdServer(jArray.getJSONObject(i).getInt("idDepartamento"));
                                departamento.setNomDepartamento(jArray.getJSONObject(i).getString("nom_departamento"));
                                realm.copyToRealm(departamento);
                                realm.commitTransaction();
                            }
                            realm.close();
                            Log.d(TAG, jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, e.toString(), e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        VolleyLog.e(error.toString());
                    }
                }
        );
        Configuracion.getInstancia().addRequestQueue(request, TAG);
    }

    private void requestDistrito() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constante.URL_LISTAR_DISTRITO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jArray = jsonObject.getJSONArray("distrito");
                            Realm realm = Realm.getDefaultInstance();
                            for (int i = 0; i < jArray.length(); i++) {
                                realm.beginTransaction();
                                Distrito distrito = realm.createObject(Distrito.class, Distrito.getUltimoId());
                                distrito.setIdServer(jArray.getJSONObject(i).getInt("idDistrito"));
                                distrito.setFkProvincia(jArray.getJSONObject(i).getInt("idProvincia"));
                                distrito.setNomDistrito(jArray.getJSONObject(i).getString("nom_distrito"));
                                realm.copyToRealm(distrito);
                                realm.commitTransaction();
                            }
                            realm.close();
                            Log.d(TAG, jsonObject.toString());
                            progressDialog.hide();
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

        );
        Configuracion.getInstancia().addRequestQueue(request, TAG);
    }

    private void requestRegistrarse() {
        progressDialog.show();
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constante.URL_REGISTRAR_USUARIO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int codigo = jsonObject.getInt("codigo");
                            Log.d(TAG, jsonObject.toString());
                            progressDialog.hide();
                            mensajeSistema(codigo);
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
                params.put("nombre_apellidos", etNombres.getText().toString().trim());
                params.put("nombre_empresa", etNombresEmpresa.getText().toString().trim());
                params.put("direccion", etDireccion.getText().toString().trim());
                params.put("departamento", etDepartamento.getText().toString().trim());
                params.put("provincia", etProvincia.getText().toString().trim());
                params.put("distrito", etDistrito.getText().toString().trim());
                params.put("correo", etCorreo.getText().toString().trim());
                params.put("telefono", etTelefono.getText().toString().trim());
                params.put("password", etPassword.getText().toString().trim());
                return params;
            }
        };
        Configuracion.getInstancia().addRequestQueue(request, TAG);
    }

    private void mensajeSistema(int codigo) {
        String texto = "No se recibieron los datos necesarios";
        switch (codigo) {
            case -3 : texto = "El correo ingresado se encuentra en uso"; break;
            case -4 : texto = "Ocurrió un error al momento de registrar usuario"; break;
            case 200 : texto = "Usuario registrado satisfactoriamente"; break;
        }
        DialogInterface.OnClickListener click = null;
        if (codigo == 200) click = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                startActivity(new Intent(RegistroActivity.this, IniciarSesionActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        };
        new AlertDialog.Builder(RegistroActivity.this)
                .setTitle(R.string.app_name)
                .setMessage(texto)
                .setCancelable(false)
                .setPositiveButton(R.string.aceptar, click)
                .show();
    }

    private boolean validarDatos() {
        return  (!TextUtils.isEmpty(etNombres.getText().toString().trim()) &&
                !TextUtils.isEmpty(etPassword.getText().toString().trim()) &&
                !TextUtils.isEmpty(etNombresEmpresa.getText().toString().trim()) &&
                !TextUtils.isEmpty(etDireccion.getText().toString().trim()) &&
                !TextUtils.isEmpty(etDepartamento.getText().toString().trim()) &&
                !TextUtils.isEmpty(etProvincia.getText().toString().trim()) &&
                !TextUtils.isEmpty(etDistrito.getText().toString().trim()) &&
                !TextUtils.isEmpty(etCorreo.getText().toString().trim()) &&
                !TextUtils.isEmpty(etTelefono.getText().toString().trim()));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (FacebookApi.conectado()) FacebookApi.cerrarSesion();
    }

    @OnClick(R.id.btnDepartamento)
    public void dialogoDepartamentos(){
        List<String> items = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<Departamento> departamentos = realm.where(Departamento.class).findAll();
        for (int i = 0; i < departamentos.size(); i++){
            items.add(departamentos.get(i).getNomDepartamento());
        }
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.seleccione_departamento).toUpperCase())
                .setSingleChoiceItems(items.toArray(new String[items.size()]), -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        idDep = departamentos.get(which).getIdServer();
                        etDepartamento.setText(departamentos.get(which).getNomDepartamento());
                    }
                })
                .show();
    }

    @OnClick(R.id.btnProvincia)
    public void dialogoProvincia(){
        if (idDep!=-1) {
            List<String> items = new ArrayList<>();
            Realm realm = Realm.getDefaultInstance();
            final RealmResults<Provincia> provincias = realm.where(Provincia.class).equalTo("fkDepartamento", idDep).findAll();
            for (int i = 0; i < provincias.size(); i++) {
                items.add(provincias.get(i).getNomProvincia());
            }

            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.seleccione_provincia).toUpperCase())
                    .setSingleChoiceItems(items.toArray(new String[items.size()]), -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            idPro = provincias.get(which).getIdServer();
                            etProvincia.setText(provincias.get(which).getNomProvincia());
                        }
                    })
                    .show();
        }
    }

    @OnClick(R.id.btnDistrito)
    public void dialogoDistrito(){
        if (idPro!=-1) {
            List<String> items = new ArrayList<>();
            Realm realm = Realm.getDefaultInstance();
            final RealmResults<Distrito> distritos = realm.where(Distrito.class).equalTo("fkProvincia", idPro).findAll();
            for (int i = 0; i < distritos.size(); i++) {
                items.add(distritos.get(i).getNomDistrito());
            }
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.seleccione_distrito).toUpperCase())
                    .setSingleChoiceItems(items.toArray(new String[items.size()]), -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            idDis = distritos.get(which).getIdServer();
                            etDistrito.setText(distritos.get(which).getNomDistrito());
                        }
                    })
                    .show();
        }
    }

    @OnTextChanged(R.id.etDepartamento)
    void onDepartamentoTextChange() {
        etProvincia.setText("");
        idPro=-1;
    }

    @OnTextChanged(R.id.etProvincia)
    void onProvinciaTextChange() {
        etDistrito.setText("");
    }

}
