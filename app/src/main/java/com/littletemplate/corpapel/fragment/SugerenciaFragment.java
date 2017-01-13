package com.littletemplate.corpapel.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.littletemplate.corpapel.R;
import com.littletemplate.corpapel.app.Configuracion;
import com.littletemplate.corpapel.model.Usuario;
import com.littletemplate.corpapel.util.ConexionBroadcastReceiver;
import com.littletemplate.corpapel.util.Constante;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SugerenciaFragment extends Fragment {
    public static final String TAG = SugerenciaFragment.class.getSimpleName();
    @BindView(R.id.etNombre) EditText etNombre;
    @BindView(R.id.etCorreo) EditText etCorreo;
    @BindView(R.id.etConsulta) EditText etConsulta;
    private ProgressDialog progressDialog;

    public SugerenciaFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sugerencia, container, false);
        ButterKnife.bind(this, view);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.cargando));

        etNombre.setText(Usuario.getUsuario().getNombres());
        etCorreo.setText(Usuario.getUsuario().getCorreo());


        return view;
    }

    @OnClick(R.id.btnEnviarSugerencia)
        public void enviarSugerencia(){
        if (validarDatos()){
            if (ConexionBroadcastReceiver.isConect()) {
                requestSugerencia();
            }
            else {
                //ConexionBroadcastReceiver.showSnack(layout, getActivity());
            }

        }
        else
        {
            Toast.makeText(getActivity(), R.string.ingresar_todos_datos, Toast.LENGTH_SHORT).show();
        }
    }

    private void requestSugerencia() {
        progressDialog.show();
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constante.URL_ENVIAR_SUGERENCIA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.d(TAG, jsonObject.toString());

                            progressDialog.hide();
                            Toast.makeText(getActivity(), R.string.sugerencia_enviada, Toast.LENGTH_SHORT).show();
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
                params.put("nombre", etNombre.getText().toString().trim());
                params.put("correo", etCorreo.getText().toString().trim());
                params.put("comentario", etConsulta.getText().toString().trim());
                return params;
            }
        };
        Configuracion.getInstancia().addRequestQueue(request, TAG);
    }

    public boolean validarDatos() {
        return (!TextUtils.isEmpty(etNombre.getText().toString().trim()) &&
                !TextUtils.isEmpty(etCorreo.getText().toString().trim()) &&
                !TextUtils.isEmpty(etConsulta.getText().toString().trim()));
    }


}
