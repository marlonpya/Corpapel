package com.littletemplate.corpapel.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.littletemplate.corpapel.R;

import butterknife.ButterKnife;

public class SugerenciaFragment extends Fragment {

    public SugerenciaFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sugerencia, container, false);
        ButterKnife.bind(this, view);


        return view;
    }

}
