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
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import io.realm.Realm;
import io.realm.RealmResults;

public class RegistroActivity extends BaseActivity {
    public static final String TAG = RegistroActivity.class.getSimpleName();
    @BindView(R.id.activity_register) RelativeLayout layout;
    @BindView(R.id.etNombresRegistro) EditText etNombres;
    @BindView(R.id.etPasswordRegistro) EditText etPassword;
    @BindView(R.id.etNombresEmpresaRegistro) EditText etNombresEmpresa;
    @BindView(R.id.etDireccionRegistro) EditText etDireccion;
    @BindView(R.id.spinner_departamento_registro) Spinner spDepartamento;
    @BindView(R.id.spinner_provincia_registro) Spinner spProvincia;
    @BindView(R.id.spinner_distrito_registro) Spinner spDistrito;
    @BindView(R.id.etCorreoRegistro) EditText etCorreo;
    @BindView(R.id.etTelefonoRegistro) EditText etTelefono;
    private ProgressDialog progressDialog;

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
            llenarDepartamento();
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
                            //int codigo = jsonObject.getInt("codigo");
                            Log.d(TAG, jsonObject.toString());
                            //mensajeSistema(codigo);
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
                            //int codigo = jsonObject.getInt("codigo");
                            Log.d(TAG, jsonObject.toString());
                            //mensajeSistema(codigo);
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

    private void llenarDepartamento(){
        Departamento departamento = new Departamento();
        List<String> array = departamento.listarDepartamento();
        ArrayAdapter spDepartamentoAdapter = new ArrayAdapter(this,
                R.layout.spinner_item,array);
        spDepartamentoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spDepartamento.setAdapter(spDepartamentoAdapter);
    }

    @OnItemSelected(R.id.spinner_departamento_registro)
    public void spinnerItemSelectedDep(Spinner spinner, int pos) {
        llenarProvincia(spDepartamento.getSelectedItem().toString());
    }

    @OnItemSelected(R.id.spinner_provincia_registro)
    public void spinnerItemSelectedProv(Spinner spinner, int pos) {
        llenarDistrito(spProvincia.getSelectedItem().toString());
    }

    private void llenarDistrito(String nomProv) {
        Realm realm = Realm.getDefaultInstance();
        spDistrito.setAdapter(null);
        int provId=0;
        RealmResults<Provincia> provincias = realm.where(Provincia.class).findAll();
        for (int i = 0; i < provincias.size(); i++) {
            if (provincias.get(i).getNomProvincia().equals(nomProv)){
                provId=provincias.get(i).getIdServer();
            }
        }
        RealmResults<Distrito> distritos = realm.where(Distrito.class).findAll();
        final List<String> nombre_distritos = new ArrayList<>();
        for (int i = 0; i < distritos.size(); i++){
            if (distritos.get(i).getFkProvincia()==provId){
                nombre_distritos.add(distritos.get(i).getNomDistrito());
            }
        }
        ArrayAdapter spDistritoAdapter = new ArrayAdapter(this,
                R.layout.spinner_item,nombre_distritos);
        spDistritoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spDistrito.setAdapter(spDistritoAdapter);
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
                            //int codigo = jsonObject.getInt("codigo");
                            Log.d(TAG, jsonObject.toString());
                            llenarDepartamento();
                            progressDialog.hide();
                            //mensajeSistema(codigo);
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

    private void llenarProvincia(String nomDep) {
        Realm realm = Realm.getDefaultInstance();
        spProvincia.setAdapter(null);
        int depId=0;
        RealmResults<Departamento> departamentos = realm.where(Departamento.class).findAll();
        for (int i = 0; i < departamentos.size(); i++) {
            if (departamentos.get(i).getNomDepartamento().equals(nomDep)){
                depId=departamentos.get(i).getIdServer();
            }
        }
        RealmResults<Provincia> provincias = realm.where(Provincia.class).findAll();
        final List<String> nombre_provincias = new ArrayList<>();
        for (int i = 0; i < provincias.size(); i++){
            if (provincias.get(i).getFkDepartamento()==depId){
                nombre_provincias.add(provincias.get(i).getNomProvincia());
            }
        }
        ArrayAdapter spProvinciaAdapter = new ArrayAdapter(this,
                R.layout.spinner_item,nombre_provincias);
        spProvinciaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spProvincia.setAdapter(spProvinciaAdapter);
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
                params.put("departamento", spDepartamento.getSelectedItem().toString().trim());
                params.put("provincia", spProvincia.getSelectedItem().toString().trim());
                params.put("distrito", spDistrito.getSelectedItem().toString().trim());
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
                !TextUtils.isEmpty(spDepartamento.getSelectedItem().toString().trim()) &&
                !TextUtils.isEmpty(spProvincia.getSelectedItem().toString().trim()) &&
                !TextUtils.isEmpty(spDistrito.getSelectedItem().toString().trim()) &&
                !TextUtils.isEmpty(etCorreo.getText().toString().trim()) &&
                !TextUtils.isEmpty(etTelefono.getText().toString().trim())) /*||
                spDepartamento.getSelectedItem().toString().trim().equals("DEPARTAMENTO") ||
                spProvincia.getSelectedItem().toString().trim().equals("PROVINCIA") ||
                spDistrito.getSelectedItem().toString().trim().equals("DISTRITO")*/;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (FacebookApi.conectado()) FacebookApi.cerrarSesion();
    }
}
