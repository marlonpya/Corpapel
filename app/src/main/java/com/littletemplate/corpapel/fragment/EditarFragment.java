package com.littletemplate.corpapel.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.littletemplate.corpapel.R;
import com.littletemplate.corpapel.model.Usuario;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditarFragment extends Fragment {
    @BindView(R.id.etNombresEditar) EditText etNombre;
    @BindView(R.id.etPasswordEditar) EditText etPassword;
    @BindView(R.id.etNombresEmpresaEditar) EditText etNombreEmpresa;
    @BindView(R.id.etDireccionEditar) EditText etDireccion;
    @BindView(R.id.spinner_departamento_Editar) Spinner spDepartamento;
    @BindView(R.id.spinner_provincia_Editar) Spinner spProvincia;
    @BindView(R.id.spinner_distrito_Editar) Spinner spDistrito;
    @BindView(R.id.etTelefonoEditar) EditText etTelefono;

    public EditarFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar, container, false);
        ButterKnife.bind(this, view);

        if (Usuario.getUsuario() != null) {
            Usuario usuario = Usuario.getUsuario();
            etNombre.setText(usuario.getNombres());
            etNombreEmpresa.setText(usuario.getNombre_empresa());
            etDireccion.setText(usuario.getDireccion());
            spDepartamento.setPrompt(usuario.getDepartamento());
            spProvincia.setPrompt(usuario.getProvincia());
            spDistrito.setPrompt(usuario.getDistrito());
            etTelefono.setText(usuario.getMovil());
        }
        return view;
    }

    @OnClick(R.id.btnActualizarDatos)
    public void actualizarDatos() {

    }

}
