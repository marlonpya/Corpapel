package com.littletemplate.corpapel;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.littletemplate.corpapel.adapter.TiendaAdapter;
import com.littletemplate.corpapel.app.BaseActivity;
import com.littletemplate.corpapel.app.Configuracion;
import com.littletemplate.corpapel.model.Distrito;
import com.littletemplate.corpapel.model.Tienda;
import com.littletemplate.corpapel.util.ConexionBroadcastReceiver;
import com.littletemplate.corpapel.util.Constante;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;

public class ListaTiendasActivity extends BaseActivity {

    public static final String TAG = ListaTiendasActivity.class.getSimpleName();
    public static final String FK_DISTRITO = "idDistrito";

    private int idDistrito = -1;
    private ProgressDialog progressDialog;
    @BindView(R.id.lviTiendas) RealmRecyclerView realmRecyclerView;
    @BindView(R.id.txtDistrito) TextView txtDistrito;
    private RealmResults<Tienda> tiendas;
    private TiendaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_tiendas);
        iniciarProgresDialog();
        idDistrito = getIntent().getIntExtra(Constante.ID_DISTRITO, -1);
        Realm realm = Realm.getDefaultInstance();
        Distrito distrito = realm.where(Distrito.class).equalTo("idServer", idDistrito).findFirst();
        txtDistrito.setText(distrito.getNomDistrito());
        if (ConexionBroadcastReceiver.isConect()){
            vaciarTiendaRealm();
            requestTienda();
        }
        else{
            Toast.makeText(this, R.string.conexion_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void iniciarRRV() {
        Realm realm = Realm.getDefaultInstance();
        tiendas = realm.where(Tienda.class).equalTo(FK_DISTRITO, idDistrito).findAll();
        adapter = new TiendaAdapter(this, tiendas);
        realmRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void iniciarProgresDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.cargando));
        progressDialog.setCancelable(false);
    }

    private void vaciarTiendaRealm() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Tienda> tienda_lista = realm.where(Tienda.class).findAll();
        realm.beginTransaction();
        tienda_lista.deleteAllFromRealm();
        realm.commitTransaction();
    }

    private void requestTienda() {
        progressDialog.show();
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constante.URL_LISTAR_TIENDA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jArray = jsonObject.getJSONArray("tienda");
                            Realm realm = Realm.getDefaultInstance();
                            for (int i = 0; i < jArray.length(); i++) {
                                if (!jArray.getJSONObject(i).getString("TIE_LATITUD").isEmpty() ||
                                        !jArray.getJSONObject(i).getString("TIE_LATITUD").equals("null") ||
                                        !jArray.getJSONObject(i).getString("TIE_LONGITUD").isEmpty() ||
                                        !jArray.getJSONObject(i).getString("TIE_LONGITUD").equals("null")){

                                    realm.beginTransaction();
                                    Tienda tienda = realm.createObject(Tienda.class, Tienda.getUltimoId());
                                    tienda.setIdServer(jArray.getJSONObject(i).getInt("TIE_ID"));
                                    tienda.setNombre(jArray.getJSONObject(i).getString("TIE_NOMBRE"));
                                    tienda.setDireccion(jArray.getJSONObject(i).getString("TIE_DIRECCION"));
                                    tienda.setTelefono(jArray.getJSONObject(i).getString("TIE_TELEFONO"));
                                    tienda.setHorario_inicio(jArray.getJSONObject(i).getString("TIE_HORARIO_INICIO"));
                                    tienda.setHorario_fin(jArray.getJSONObject(i).getString("TIE_HORARIO_FIN"));
                                    tienda.setLatitud(Double.parseDouble(jArray.getJSONObject(i).getString("TIE_LATITUD")));
                                    tienda.setLongitud(Double.parseDouble(jArray.getJSONObject(i).getString("TIE_LONGITUD")));
                                    tienda.setIdDepartamento(Integer.parseInt(jArray.getJSONObject(i).getString("ID_DEPARTAMENTO")));
                                    tienda.setIdProvincia(Integer.parseInt(jArray.getJSONObject(i).getString("ID_PROVINCIA")));
                                    tienda.setIdDistrito(Integer.parseInt(jArray.getJSONObject(i).getString("ID_DISTRITO")));
                                    realm.copyToRealm(tienda);
                                    realm.commitTransaction();
                                }
                            }
                            realm.close();
                            iniciarRRV();
                            progressDialog.hide();
                            Log.d(TAG, jsonObject.toString());
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
}
