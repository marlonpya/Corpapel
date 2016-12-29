package com.littletemplate.corpapel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
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
import com.littletemplate.corpapel.app.BaseActivity;
import com.littletemplate.corpapel.app.Configuracion;
import com.littletemplate.corpapel.util.ConexionBroadcastReceiver;
import com.littletemplate.corpapel.util.Constante;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

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

        ArrayAdapter<CharSequence> adapterDEPARTAMENTO = ArrayAdapter.createFromResource(
                this, R.array.DEPARTAMENTO_array, R.layout.spinner_item);
        adapterDEPARTAMENTO.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spDepartamento.setAdapter(adapterDEPARTAMENTO);

        ArrayAdapter<CharSequence> adapterPROVINCIA = ArrayAdapter.createFromResource(
                this, R.array.PROVINCIA_array, R.layout.spinner_item);
        adapterPROVINCIA.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spProvincia.setAdapter(adapterPROVINCIA);

        ArrayAdapter<CharSequence> adapterDISTRITO = ArrayAdapter.createFromResource(
                this, R.array.DISTRITO_array, R.layout.spinner_item);
        adapterDISTRITO.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spDistrito.setAdapter(adapterDISTRITO);
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
                            if (jsonObject.getBoolean("status")) {
                                Toast.makeText(RegistroActivity.this, R.string.registro_ok, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegistroActivity.this, IniciarSesionActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            } else {
                                Toast.makeText(RegistroActivity.this, R.string.registro_error, Toast.LENGTH_SHORT).show();
                            }
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

    private boolean validarDatos() {
        return  (!TextUtils.isEmpty(etNombres.getText().toString().trim()) &&
                !TextUtils.isEmpty(etPassword.getText().toString().trim()) &&
                !TextUtils.isEmpty(etNombresEmpresa.getText().toString().trim()) &&
                !TextUtils.isEmpty(etDireccion.getText().toString().trim()) &&
                !TextUtils.isEmpty(spDepartamento.getSelectedItem().toString().trim()) &&
                !TextUtils.isEmpty(spProvincia.getSelectedItem().toString().trim()) &&
                !TextUtils.isEmpty(spDistrito.getSelectedItem().toString().trim()) &&
                !TextUtils.isEmpty(etCorreo.getText().toString().trim()) &&
                !TextUtils.isEmpty(etTelefono.getText().toString().trim())) ||
                spDepartamento.getSelectedItem().toString().trim().equals("DEPARTAMENTO") ||
                spProvincia.getSelectedItem().toString().trim().equals("PROVINCIA") ||
                spDistrito.getSelectedItem().toString().trim().equals("DISTRITO");
    }
}
