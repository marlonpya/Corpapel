package com.littletemplate.corpapel.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.littletemplate.corpapel.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditarFragment extends Fragment {

    public EditarFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.btnActualizarDatos)
    public void actualizarDatos() {

    }

}
