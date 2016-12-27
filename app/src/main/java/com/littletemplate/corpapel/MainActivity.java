package com.littletemplate.corpapel;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.littletemplate.corpapel.app.BaseActivity;
import com.littletemplate.corpapel.fragment.ProductosFragment;

import java.util.ArrayList;
import java.util.List;

import Clases.Tienda;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, BusquedaFragment.OnFragmentInteractionListener, TiendaFragment.OnFragmentInteractionListener, ProductosFragment.OnFragmentInteractionListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    DrawerLayout drawer;
   /* private static final int PLACE_PICKER_REQUEST = 1;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));*/

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private List<Tienda> tiendas = new ArrayList<>();
    private GoogleMap mMap;

    private Marker markerBolognesi;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            //You can add here other case statements according to your requirement.
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Button button = (Button) findViewById(R.id.buttonburger);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //tiendas    = DBConnection.GetTiendas(this);

        //mMap.getUiSettings().setScrollGesturesEnabled(false);

    }

    public void OpenDrawer() {
        drawer.openDrawer(Gravity.LEFT);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
                //mMap.getUiSettings().setScrollGesturesEnabled(true);
            } else {
                super.onBackPressed();
            }
        }
    }

    public void SetFragmentTienda(Bundle bundle) {
        Fragment fragment = null;
        fragment = new TiendaFragment();
        fragment.setArguments(bundle);
        // getSupportFragmentManager().beginTransaction().replace(R.id.Contenedor,fragment).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.Contenedor, fragment).addToBackStack(null).commit();
        return;
    }

    public void SetFragmentProductos() {
        Fragment fragment = null;
        fragment = new ProductosFragment();
        // getSupportFragmentManager().beginTransaction().replace(R.id.Contenedor,fragment).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.Contenedor, fragment).addToBackStack(null).commit();
        return;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;
        Boolean FragmentoSeleccionado = false;

        if (id == R.id.nav_camera) {
            // Handle the camera action
            startActivity(new Intent(MainActivity.this, EditActivity.class));
        } else if (id == R.id.nav_gallery) {

            AddMarkers();
            //fragment = new BusquedaFragment();
            //getSupportFragmentManager().beginTransaction().replace(R.id.Contenedor,fragment).commit();
            // FragmentoSeleccionado=true;
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

            LoginManager.getInstance().logOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }


        if (FragmentoSeleccionado) {
            // getSupportFragmentManager().beginTransaction().replace(R.id.Contenedor,fragment).commit();

            getSupportFragmentManager().beginTransaction().replace(R.id.Contenedor, fragment).addToBackStack(null).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //   mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        // Add a marker in Sydney and move the camera

        LatLng latitud_longitud = new LatLng(-12.0553011, -77.0802424);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latitud_longitud, 15));
        mMap.getUiSettings().setScrollGesturesEnabled(true);

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public void AddMarkers() {/*
       // Connection conn;
        try {

         //   Class.forName("com.mysql.jdbc.Driver");
         //   conn = DriverManager.getConnection("jdbc:mysql://192.168.1.5:3306/corpapel", "root", "");
            //Statement st = conn.createStatement();
           // ResultSet rs = st.executeQuery("select * from table_name");
           // ResultSetMetaData rsmd = rs.getMetaData();
            Toast toast = Toast.makeText(this, "conexion completa",Toast.LENGTH_SHORT);
            toast.show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(this, "error",Toast.LENGTH_SHORT);
            toast.show();
        } catch (SQLException e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(this, "error",Toast.LENGTH_SHORT);
            toast.show();
        }


*/


        //  Toast.makeText(getApplicationContext(),
        //          "cantidad: " + ConexionDB.getTiendas(),
        //           Toast.LENGTH_SHORT).show();

        mMap.setOnMarkerClickListener(this);


        for (int i = 0; i < tiendas.size(); i++) {

            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(tiendas.get(i).latitud, tiendas.get(i).longitud))
                    .title(tiendas.get(i).nombre));

            marker.setSnippet(String.valueOf(i));
            // marker.showInfoWindow();


        }


       /* markerBolognesi= mMap.addMarker(new MarkerOptions()
                .position(new LatLng(-12.060067, -77.041664))
                .title("Plaza Bolognesi"));

        markerBolognesi.showInfoWindow();*/


    }


    @Override
    public boolean onMarkerClick(Marker marker) {


      /*  for(int i=0; i<tiendas.size();i++)
        {
            if(marker.getTitle()==tiendas.get(i).nombre)
            {*/

        // String aux;


        Bundle bundle = new Bundle();
        for (int i = 0; i < tiendas.size(); i++) {

            // aux =  tiendas.get(i).nombre+" ";


            if (i == Integer.parseInt(marker.getSnippet())) {
                //Toast.makeText(this, tiendas.get(i).nombre, Toast.LENGTH_LONG).show();
                bundle.putString("nombre", tiendas.get(i).nombre);
                bundle.putString("direccion", tiendas.get(i).direccion);
                // Toast.makeText(this,marker.getTitle()+"="+tiendas.get(i).nombre, Toast.LENGTH_LONG).show();
                break;
            }
        }
        SetFragmentTienda(bundle);

        double lat = marker.getPosition().latitude - 0.011525;
        double lng = marker.getPosition().longitude;// returns LatLng object
        LatLng latlng = new LatLng(lat, lng);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        return true;
      /*      }

        }
*/
        /*if (marker.equals(markerBolognesi))
        {
            SetFragmentTienda();
            double lat = marker.getPosition().latitude-0.011525; // returns LatLng object
            double lng =  marker.getPosition().longitude;// returns LatLng object
            LatLng latlng = new LatLng(lat,lng);
            // CameraPosition pos = new CameraPosition(latlng,0,0,0);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));// setCenter takes a LatLng object
            // mMap.getUiSettings().setScrollGesturesEnabled(false);
            return true;
        }
        return false;*/

        //  return false;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Tú estás aquí");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }
}
