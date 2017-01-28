package com.littletemplate.corpapel.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.littletemplate.corpapel.PrincipalActivity;
import com.littletemplate.corpapel.R;
import com.littletemplate.corpapel.fragment.MapaFragment;
import com.littletemplate.corpapel.model.Distrito;
import com.littletemplate.corpapel.model.Tienda;
import com.littletemplate.corpapel.util.Constante;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;

/**
 * Created by ucweb02 on 25/01/2017.
 */

public class TiendaAdapter extends RealmBasedRecyclerViewAdapter<Tienda, TiendaAdapter.ViewHolder>{
    public TiendaAdapter(
            Context context,
            RealmResults<Tienda> realmResults) {
        super(context, realmResults, true, true);
    }
    @Override
    public ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(inflater.inflate(R.layout.tienda_item, viewGroup, false));
    }

    @Override
    public void onBindRealmViewHolder(TiendaAdapter.ViewHolder viewHolder, int i) {
        final Tienda tienda = realmResults.get(i);
        viewHolder.nombre.setText(tienda.getNombre().toUpperCase());
        viewHolder.direccion.setText(tienda.getDireccion());
        viewHolder.telefono.setText("Teléfono: " + tienda.getTelefono());
        viewHolder.horario.setText("HORARIO DE ATENCIÓN: " + tienda.getHorario_inicio()+" a " + tienda.getHorario_fin());

        viewHolder.seleccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContext().startActivity(new Intent(getContext(), PrincipalActivity.class)
                        .putExtra(Constante.ID_DISTRITO, tienda.getIdDistrito())
                        .putExtra(Constante.ID_TIENDA, tienda.getIdServer()).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });
    }

    public class ViewHolder extends RealmViewHolder {
        @BindView(R.id.txt_nombre) TextView nombre;
        @BindView(R.id.txt_direccion) TextView direccion;
        @BindView(R.id.txt_telefono) TextView telefono;
        @BindView(R.id.txt_horario) TextView horario;
        @BindView(R.id.btnSeleccion) LinearLayout seleccion;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
