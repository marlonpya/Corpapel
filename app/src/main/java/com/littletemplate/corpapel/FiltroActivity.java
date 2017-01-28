package com.littletemplate.corpapel;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.littletemplate.corpapel.app.BaseActivity;
import com.littletemplate.corpapel.model.Departamento;
import com.littletemplate.corpapel.model.Distrito;
import com.littletemplate.corpapel.model.Provincia;
import com.littletemplate.corpapel.util.ConexionBroadcastReceiver;
import com.littletemplate.corpapel.util.Constante;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.realm.Realm;
import io.realm.RealmResults;

public class FiltroActivity extends BaseActivity {
    @BindView(R.id.etDepartamento) TextView etDepartamento;
    @BindView(R.id.etProvincia) TextView etProvincia;
    @BindView(R.id.etDistrito) TextView etDistrito;
    @BindView(R.id.btnDepartamento) LinearLayout btnDepartamento;
    @BindView(R.id.btnProvincia) LinearLayout btnProvincia;
    @BindView(R.id.btnDistrito) LinearLayout btnDistrito;
    @BindView(R.id.btnBuscar) Button btnBuscar;

    private int idDep=-1, idPro=-1, idDis=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);
    }

    @OnClick(R.id.btnDepartamento)
    public void dialogoDepartamentos(){
        List<String> items = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<Departamento> departamentos = realm.where(Departamento.class).findAll();
        for (int i = 0; i < departamentos.size(); i++){
            items.add(departamentos.get(i).getNomDepartamento());
        }
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.seleccione_departamento).toUpperCase())
                .setSingleChoiceItems(items.toArray(new String[items.size()]), -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        idDep = departamentos.get(which).getIdServer();
                        etDepartamento.setText(departamentos.get(which).getNomDepartamento());
                    }
                })
                .show();
    }

    @OnClick(R.id.btnProvincia)
    public void dialogoProvincia(){
        if (idDep!=-1) {
            List<String> items = new ArrayList<>();
            Realm realm = Realm.getDefaultInstance();
            final RealmResults<Provincia> provincias = realm.where(Provincia.class).equalTo("fkDepartamento", idDep).findAll();
            for (int i = 0; i < provincias.size(); i++) {
                items.add(provincias.get(i).getNomProvincia());
            }

            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.seleccione_provincia).toUpperCase())
                    .setSingleChoiceItems(items.toArray(new String[items.size()]), -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            idPro = provincias.get(which).getIdServer();
                            etProvincia.setText(provincias.get(which).getNomProvincia());
                        }
                    })
                    .show();
        }

    }

    @OnClick(R.id.btnDistrito)
    public void dialogoDistrito(){
        if (idPro!=-1) {
            List<String> items = new ArrayList<>();
            Realm realm = Realm.getDefaultInstance();
            final RealmResults<Distrito> distritos = realm.where(Distrito.class).equalTo("fkProvincia", idPro).findAll();
            for (int i = 0; i < distritos.size(); i++) {
                items.add(distritos.get(i).getNomDistrito());
            }
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.seleccione_distrito).toUpperCase())
                    .setSingleChoiceItems(items.toArray(new String[items.size()]), -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            idDis = distritos.get(which).getIdServer();
                            etDistrito.setText(distritos.get(which).getNomDistrito());
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    @OnTextChanged(R.id.etDepartamento)
    void onDepartamentoTextChange() {
        etProvincia.setText("");
        idPro=-1;
    }

    @OnTextChanged(R.id.etProvincia)
    void onProvinciaTextChange() {
        etDistrito.setText("");
        idDis=-1;
    }

    @OnClick(R.id.btnBuscar)
    public void buscarTiendas() {
        if (idDis!=-1) {
            if (ConexionBroadcastReceiver.isConect()) {
                startActivity(new Intent(FiltroActivity.this, ListaTiendasActivity.class).putExtra(Constante.ID_DISTRITO, idDis));
            } else
                Toast.makeText(this, R.string.conexion_error, Toast.LENGTH_SHORT).show();
        }
        else{

            Toast.makeText(this, R.string.ingresar_todos_datos, Toast.LENGTH_SHORT).show();
        }
    }

}
