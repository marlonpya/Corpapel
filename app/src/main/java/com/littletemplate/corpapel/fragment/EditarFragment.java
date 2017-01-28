package com.littletemplate.corpapel.fragment;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.littletemplate.corpapel.R;
import com.littletemplate.corpapel.apis.FacebookApi;
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
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;
import io.realm.Realm;
import io.realm.RealmResults;

public class EditarFragment extends Fragment {
    public static final String TAG = EditarFragment.class.getSimpleName();
    @BindView(R.id.activity_register) RelativeLayout layout;
    @BindView(R.id.etNombresEditar) EditText etNombre;
    @BindView(R.id.etPasswordEditar) EditText etPassword;
    @BindView(R.id.etNombresEmpresaEditar) EditText etNombreEmpresa;
    @BindView(R.id.etDireccionEditar) EditText etDireccion;
    @BindView(R.id.etDepartamento) TextView etDepartamento;
    @BindView(R.id.etProvincia) TextView etProvincia;
    @BindView(R.id.etDistrito) TextView etDistrito;
    @BindView(R.id.btnDepartamento) LinearLayout btnDepartamento;
    @BindView(R.id.btnProvincia) LinearLayout btnProvincia;
    @BindView(R.id.btnDistrito) LinearLayout btnDistrito;

    @BindView(R.id.etTelefonoEditar) EditText etTelefono;
    private ProgressDialog progressDialog;
    private TextView etNombreNavegador;
    public EditarFragment() { }
    private int idDep=-1, idPro=-1, idDis;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar, container, false);
        ButterKnife.bind(this, view);

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.cargando));

        if (Usuario.getUsuario() != null) {
            Usuario usuario = Usuario.getUsuario();
            etNombre.setText(usuario.getNombres());
            etNombreEmpresa.setText(usuario.getNombre_empresa());
            etDireccion.setText(usuario.getDireccion());
            etDepartamento.setText(usuario.getDepartamento());
            etProvincia.setText(usuario.getProvincia());
            etDistrito.setText(usuario.getDistrito());

            etTelefono.setText(usuario.getMovil());
            etPassword.setText(usuario.getPassword());
            if (FacebookApi.conectado() || !TextUtils.isEmpty(usuario.getId_google()) ){
                etPassword.setVisibility(View.GONE);
            }
        }

        if (Departamento.getUltimoId() == 0){
            if (ConexionBroadcastReceiver.isConect()){
                requestDepartamento();
                requestProvincia();
                requestDistrito();
            }
            else {
                ConexionBroadcastReceiver.showSnack(layout, getActivity());
            }
        }
        else{
            //sesion();
        }
        return view;
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


    private void actualizarHeader(String nombre){
        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);
        etNombreNavegador = (TextView) view.findViewById(R.id.navNombre);
        etNombreNavegador.setText(nombre);
    }

    @OnClick(R.id.btnActualizarDatos)
    public void actualizarDatos() {
        if (validarDatos()){
            if (ConexionBroadcastReceiver.isConect()) {
                confirmarActualizacion();
            }
            else {
                ConexionBroadcastReceiver.showSnack(layout, getActivity());
            }
        }
        else
        {
            Toast.makeText(getActivity(), R.string.ingresar_todos_datos, Toast.LENGTH_SHORT).show();
        }

    }

    private void requestEditarUsuario() {
        progressDialog.show();
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constante.URL_EDITAR_USUARIO,
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
                params.put("nombre_apellidos", etNombre.getText().toString().trim());
                params.put("nombre_empresa", etNombreEmpresa.getText().toString().trim());
                params.put("direccion", etDireccion.getText().toString().trim());
                params.put("departamento", etDepartamento.getText().toString().trim());
                params.put("provincia", etProvincia.getText().toString().trim());
                params.put("distrito", etDistrito.getText().toString().trim());
                Usuario usuario = Usuario.getUsuario();
                params.put("correo", usuario.getCorreo());
                params.put("telefono", etTelefono.getText().toString().trim());
                params.put("password", etPassword.getText().toString().trim());
                return params;
            }
        };
        Configuracion.getInstancia().addRequestQueue(request, TAG);
    }

    private void requestEditarUsuarioFB() {
        progressDialog.show();
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constante.URL_EDITAR_USUARIO_FB,
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
                params.put("nombre_apellidos", etNombre.getText().toString().trim());
                params.put("nombre_empresa", etNombreEmpresa.getText().toString().trim());
                params.put("direccion", etDireccion.getText().toString().trim());
                params.put("departamento", etDepartamento.getText().toString().trim());
                params.put("provincia", etProvincia.getText().toString().trim());
                params.put("distrito", etDistrito.getText().toString().trim());
                Usuario usuario = Usuario.getUsuario();
                params.put("id_fb", usuario.getId_facebook());
                params.put("telefono", etTelefono.getText().toString().trim());
                return params;
            }
        };
        Configuracion.getInstancia().addRequestQueue(request, TAG);
    }

    private void requestEditarUsuarioGoogle() {
        progressDialog.show();
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constante.URL_EDITAR_USUARIO_GOOGLE,
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
                params.put("nombre_apellidos", etNombre.getText().toString().trim());
                params.put("nombre_empresa", etNombreEmpresa.getText().toString().trim());
                params.put("direccion", etDireccion.getText().toString().trim());
                params.put("departamento", etDepartamento.getText().toString().trim());
                params.put("provincia", etProvincia.getText().toString().trim());
                params.put("distrito", etDistrito.getText().toString().trim());
                Usuario usuario = Usuario.getUsuario();
                params.put("id_google", usuario.getId_google());
                params.put("correo", usuario.getCorreo());
                params.put("telefono", etTelefono.getText().toString().trim());
                return params;
            }
        };
        Configuracion.getInstancia().addRequestQueue(request, TAG);
    }


    private void confirmarActualizacion() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.app_name)
                .setMessage(getString(R.string.confirm_edit))
                .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (!TextUtils.isEmpty(Usuario.getUsuario().getId_facebook())){
                            requestEditarUsuarioFB();
                        }
                        else if (!TextUtils.isEmpty(Usuario.getUsuario().getId_google())){
                            requestEditarUsuarioGoogle();
                        }
                        else {
                            requestEditarUsuario();
                        }
                    }
                })
                .setCancelable(false)
                .setNegativeButton(R.string.cancelar, null)
                .show();
    }

    private void mensajeSistema(int codigo) {
        String texto = "No se recibieron los datos necesarios";
        switch (codigo) {
            case -3 : texto = "El correo ingresado se encuentra en uso"; break;
            case -4 : texto = "Ocurrió un error al momento de editar el usuario"; break;
            case 200 : texto = "Usuario editado satisfactoriamente"; break;
        }
        DialogInterface.OnClickListener click = null;
        if (codigo == 200) click = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                //update usuario
                actualizarSesion();
                actualizarHeader(Usuario.getUsuario().getNombres());
            }
        };
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.app_name)
                .setMessage(texto)
                .setCancelable(false)
                .setPositiveButton(R.string.aceptar, click)
                .show();
    }

    private void actualizarSesion(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Usuario usuario = Usuario.getUsuario();
        usuario.setNombres(etNombre.getText().toString().trim());
        usuario.setNombre_empresa(etNombreEmpresa.getText().toString().trim());
        usuario.setDireccion(etDireccion.getText().toString().trim());
        usuario.setDepartamento(etDepartamento.getText().toString().trim());
        usuario.setProvincia(etProvincia.getText().toString().trim());
        usuario.setDistrito(etDistrito.getText().toString().trim());
        usuario.setMovil(etTelefono.getText().toString().trim());
        realm.commitTransaction();
        realm.close();
    }

    private boolean validarDatos() {
        return  (!TextUtils.isEmpty(etNombre.getText().toString().trim()) &&
                !TextUtils.isEmpty(etNombreEmpresa.getText().toString().trim()) &&
                !TextUtils.isEmpty(etDireccion.getText().toString().trim()) &&
                !TextUtils.isEmpty(etDepartamento.getText().toString().trim()) &&
                !TextUtils.isEmpty(etDistrito.getText().toString().trim()) &&
                !TextUtils.isEmpty(etProvincia.getText().toString().trim()) &&
                !TextUtils.isEmpty(etTelefono.getText()));
    }

    @OnClick(R.id.btnDepartamento)
    public void dialogoDepartamentos(){
        List<String> items = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<Departamento> departamentos = realm.where(Departamento.class).findAll();
        for (int i = 0; i < departamentos.size(); i++){
            items.add(departamentos.get(i).getNomDepartamento());
        }
        new AlertDialog.Builder(getActivity())
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
        if (!TextUtils.isEmpty(etDepartamento.getText().toString().trim())) {
            Realm realm = Realm.getDefaultInstance();
            final Departamento departamento = realm.where(Departamento.class).equalTo("nomDepartamento", etDepartamento.getText().toString().trim()).findFirst();
            idDep=departamento.getIdServer();
            if (idDep!=-1) {
                List<String> items = new ArrayList<>();
                final RealmResults<Provincia> provincias = realm.where(Provincia.class).equalTo("fkDepartamento", idDep).findAll();
                for (int i = 0; i < provincias.size(); i++) {
                    items.add(provincias.get(i).getNomProvincia());
                }
                new AlertDialog.Builder(getContext())
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
    }

    @OnClick(R.id.btnDistrito)
    public void dialogoDistrito(){
        if (!TextUtils.isEmpty(etProvincia.getText().toString().trim())) {
            Realm realm = Realm.getDefaultInstance();
            final Provincia provincia = realm.where(Provincia.class).equalTo("nomProvincia", etProvincia.getText().toString().trim()).findFirst();
            idPro = provincia.getIdServer();
            if (idPro != -1) {
                List<String> items = new ArrayList<>();
                final RealmResults<Distrito> distritos = realm.where(Distrito.class).equalTo("fkProvincia", idPro).findAll();
                for (int i = 0; i < distritos.size(); i++) {
                    items.add(distritos.get(i).getNomDistrito());
                }
                new AlertDialog.Builder(getContext())
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
