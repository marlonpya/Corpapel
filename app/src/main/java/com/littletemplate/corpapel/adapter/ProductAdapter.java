package com.littletemplate.corpapel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.littletemplate.corpapel.R;
import com.littletemplate.corpapel.model.Producto;
import com.littletemplate.corpapel.util.Util;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;

/**
 * Created by ucweb02 on 10/01/2017.
 */

public class ProductAdapter extends RealmBasedRecyclerViewAdapter<Producto, ProductAdapter.ViewHolder> {

    public ProductAdapter(
            Context context,
            RealmResults<Producto> realmResults) {
        super(context, realmResults, true, true);
    }

    @Override
    public ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(inflater.inflate(R.layout.product_item, viewGroup, false));
    }

    @Override
    public void onBindRealmViewHolder(ViewHolder viewHolder, int i) {
        final Producto producto = realmResults.get(i);
        Util.usarGlide(getContext(), viewHolder.imagen, producto.getImagen());
        viewHolder.titulo.setText(producto.getNombre());
        viewHolder.codigo.setText(producto.getCodigo());
        viewHolder.precio_normal.setText(producto.getPrecio_normal());
        viewHolder.precio_descuento.setText(producto.getPrecio_descuento());
    }

    public class ViewHolder extends RealmViewHolder {
        @BindView(R.id.producto_imagen) ImageView imagen;
        @BindView(R.id.producto_titulo) TextView titulo;
        @BindView(R.id.producto_codigo) TextView codigo;
        @BindView(R.id.producto_precio) TextView precio_normal;
        @BindView(R.id.producto_precioDescuento) TextView precio_descuento;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}