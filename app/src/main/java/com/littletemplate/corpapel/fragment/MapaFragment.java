package com.littletemplate.corpapel.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.littletemplate.corpapel.R;

import butterknife.ButterKnife;

public class MapaFragment extends Fragment {
    public static final String TAG = MapaFragment.class.getSimpleName();
    private GoogleMap mGoogleMap;

    public MapaFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mapa, container, false);
        ButterKnife.bind(this, view);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                LatLng latLng = new LatLng(-12.0813796, -77.062214);
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        });
        return view;
    }

}
