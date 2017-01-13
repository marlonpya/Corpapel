package com.littletemplate.corpapel.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.text.Line;
import com.google.android.gms.vision.text.Text;
import com.littletemplate.corpapel.ProductActivity;
import com.littletemplate.corpapel.R;
import com.littletemplate.corpapel.app.Configuracion;
import com.littletemplate.corpapel.model.Tienda;
import com.littletemplate.corpapel.util.ConexionBroadcastReceiver;
import com.littletemplate.corpapel.util.Constante;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class MapaFragment extends Fragment {
    public static final String TAG = MapaFragment.class.getSimpleName();
    private GoogleMap mGoogleMap;
    private LocationManager locManager;
    private MyLocationListener myLocationListener;
    public MapaFragment() { }
    private Location ubicacionActual = new Location("");
    private ProgressDialog progressDialog;
    private boolean fijarMapa= true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mapa, container, false);
        ButterKnife.bind(this, view);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.actualizando));
        locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        myLocationListener = new MyLocationListener();
        comenzarLocacion();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;

                if (ConexionBroadcastReceiver.isConect()) requestTienda();
                mGoogleMap.setMyLocationEnabled(true);

                mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        final Tienda tienda = ((Tienda)marker.getTag());
                        //Calcular distancia
                        Location ubicacionTienda = new Location("");
                        ubicacionTienda.setLatitude(tienda.getLatitud());
                        ubicacionTienda.setLongitude(tienda.getLongitud());
                        float distanciaKm = ubicacionActual.distanceTo(ubicacionTienda)/1000;
                        //----
                        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialogo_detalle_local, null);
                        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                        dialog.setView(view);
                        TextView tvNombre = (TextView)view.findViewById(R.id.txt_nombre);
                        TextView tvDireccion = (TextView)view.findViewById(R.id.txt_direccion);
                        TextView tvHorario = (TextView)view.findViewById(R.id.txt_horario);
                        TextView tvDistancia = (TextView)view.findViewById(R.id.txt_distancia);
                        LinearLayout btnCatalogo = (LinearLayout)view.findViewById(R.id.btnCatalogo);
                        tvNombre.setText(tienda.getNombre());
                        tvDireccion.setText(tienda.getDireccion());
                        tvHorario.setText("HORARIO DE ATENCIÓN: " + tienda.getHorario_inicio() + " a " + tienda.getHorario_fin());
                        tvDistancia.setText(String.format(Locale.US,"%.2f", distanciaKm) + " km");

                        dialog.create();
                        dialog.show();

                        btnCatalogo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //startActivity(new Intent(getActivity().getApplicationContext(), ProductActivity.class).putExtra(Constante.ID_TIENDA, tienda.getIdServer()));
                            }
                        });
                        return false;
                    }
                });
            }
        });
        return view;
    }

    private void comenzarLocacion() {
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, myLocationListener );
        locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, myLocationListener);
        locManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, myLocationListener);

    }

    private void detenerLocacion(){
        locManager.removeUpdates(myLocationListener);
    }

    private void agregarMakers() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Tienda> tienda = realm.where(Tienda.class).findAll();

        for (int i = 0; i < tienda.size(); i++) {
            LatLng latLngTda = new LatLng(tienda.get(i).getLatitud(), tienda.get(i).getLongitud());

            mGoogleMap.addMarker(new MarkerOptions()
                    .position(latLngTda)
                    .title(tienda.get(i).getNombre())).setTag(tienda.get(i));

        }
    }

    private void fijarMapa() {
        LatLng latLng = new LatLng(ubicacionActual.getLatitude(), ubicacionActual.getLongitude());
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        fijarMapa=false;
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
                                realm.beginTransaction();
                                Tienda tienda = realm.createObject(Tienda.class, Tienda.getUltimoId());
                                tienda.setIdServer(jArray.getJSONObject(i).getInt("TIE_ID"));
                                tienda.setNombre(jArray.getJSONObject(i).getString("TIE_NOMBRE"));
                                tienda.setDireccion(jArray.getJSONObject(i).getString("TIE_DIRECCION"));
                                tienda.setHorario_inicio(jArray.getJSONObject(i).getString("TIE_HORARIO_INICIO"));
                                tienda.setHorario_fin(jArray.getJSONObject(i).getString("TIE_HORARIO_FIN"));
                                tienda.setLatitud(Double.parseDouble(jArray.getJSONObject(i).getString("TIE_LATITUD")));
                                tienda.setLongitud(Double.parseDouble(jArray.getJSONObject(i).getString("TIE_LONGITUD")));
                                realm.copyToRealm(tienda);
                                realm.commitTransaction();
                            }
                            realm.close();
                            agregarMakers();
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
    private class MyLocationListener implements LocationListener{

        @Override
        public void onLocationChanged(Location location) {
            progressDialog.show();
            ubicacionActual.setLatitude(location.getLatitude());
            ubicacionActual.setLongitude(location.getLongitude());
            if (fijarMapa!=false)
            {
                fijarMapa();
            }
            progressDialog.hide();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }

        @Override
        public void onProviderEnabled(String provider) { }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        comenzarLocacion();
    }

    @Override
    public void onResume() {
        super.onResume();
        comenzarLocacion();
    }

    @Override
    public void onPause() {
        super.onPause();
        detenerLocacion();
    }
}