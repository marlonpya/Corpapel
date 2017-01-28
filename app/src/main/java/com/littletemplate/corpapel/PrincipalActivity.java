package com.littletemplate.corpapel;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.littletemplate.corpapel.apis.FacebookApi;
import com.littletemplate.corpapel.app.BaseActivity;
import com.littletemplate.corpapel.fragment.EditarFragment;
import com.littletemplate.corpapel.fragment.MapaFragment;
import com.littletemplate.corpapel.fragment.SugerenciaFragment;

import java.util.ArrayList;
import java.util.List;

import com.littletemplate.corpapel.model.Usuario;
import com.littletemplate.corpapel.util.ConexionBroadcastReceiver;
import com.littletemplate.corpapel.util.Constante;
import com.littletemplate.corpapel.util.Util;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Optional;

public class PrincipalActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    private TextView etNombreNavegador;
    private ImageView ivImagenNavegador;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private boolean permisoLocation=false;
    private int tipo=-1;

    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }else{
            //Mostrar ubicacion en mapa si ya hay hay permiso
            if (tipo==0){
                Fragment fragment = new MapaFragment();
                mostrarFragment(fragment);
            }
            else if(tipo==1){
                startActivity(new Intent(PrincipalActivity.this, FiltroActivity.class));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        permisoLocation=true;
                    }
                } else {
                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_LONG).show();
                }
            }
            // other 'case' lines to check for other permissions this app might request.
            //You can add here other case statements according to your requirement.
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);
        etNombreNavegador = (TextView) view.findViewById(R.id.navNombre);
        ivImagenNavegador = (ImageView) view.findViewById(R.id.navImagen);
        if (getIntent().hasExtra(Constante.ID_DISTRITO)){
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_contenedor, new MapaFragment()).commit();
            navigationView.getMenu().getItem(1).setChecked(true);
        }
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_contenedor, new EditarFragment()).commit();
            navigationView.getMenu().getItem(0).setChecked(true);
            drawer.openDrawer(GravityCompat.START);
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        sesion();
    }

    @OnClick(R.id.btnToolbarCorpapel)
    public void visibleNavigator() {
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            drawer.openDrawer(GravityCompat.START);
    }

    public void OpenDrawer() {
        drawer.openDrawer(Gravity.LEFT);
    }

    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
            {
                super.onBackPressed();
                return;
            }
            else { Toast.makeText(getBaseContext(), "Vuelva a presionar para salir", Toast.LENGTH_SHORT).show(); }

            mBackPressed = System.currentTimeMillis();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
        if (id == R.id.item_editar) {
            fragment = new EditarFragment();
            mostrarFragment(fragment);
        } else if (id == R.id.item_buscar) {
            dialogoMapa();

        } else if (id == R.id.item_contacto) {
            fragment = new SugerenciaFragment();
            mostrarFragment(fragment);
        } else if (id == R.id.item_cerrar) {
            cerrarSesion();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void mostrarFragment(Fragment fragment){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fl_contenedor, fragment);
        transaction.commit();
    }

    private void dialogoMapa() {
        CharSequence[] charSequence = new CharSequence[]{getString(R.string.x_ubicacion_gps), getString(R.string.x_distrito)};
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.buscar_tienda).toUpperCase())
                .setSingleChoiceItems(charSequence, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(ConexionBroadcastReceiver.isConect()){
                            tipo=which;
                            checkLocationPermission();
                        }
                        else{
                            Toast.makeText(getBaseContext(), R.string.conexion_error, Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void cerrarSesion() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage(getString(R.string.cerrar_sesion))
                .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (FacebookApi.conectado()) {
                            FacebookApi.cerrarSesion();
                        }
                        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                            signOut();
                        }
                        Usuario.cerrarSesion();
                        startActivity(new Intent(PrincipalActivity.this, IniciarSesionActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                })
                .setNegativeButton(R.string.cancelar, null)
                .show();
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (permisoLocation){
            if (tipo==0){
                    Fragment fragment = new MapaFragment();
                    mostrarFragment(fragment);
            }
            else if (tipo==1){
                startActivity(new Intent(PrincipalActivity.this, FiltroActivity.class));
            }
            permisoLocation=false;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) { }

    private void sesion() {
        if (Usuario.getUsuario() != null) {
            etNombreNavegador.setText(Usuario.getUsuario().getNombres());
            Util.usarGlide(this, ivImagenNavegador, Usuario.getUsuario().getImagen());
        }
    }
}
