package com.littletemplate.corpapel.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.littletemplate.corpapel.R;
import com.littletemplate.corpapel.model.Producto;
import com.littletemplate.corpapel.model.Tienda;
import com.littletemplate.corpapel.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;

/**
 * Created by ucweb02 on 10/01/2017.
 */

public class ProductAdapter extends RealmBasedRecyclerViewAdapter<Producto, ProductAdapter.ViewHolder> {


    public static final String TAG = ProductAdapter.class.getSimpleName();

    public ProductAdapter(
            Context context,
            RealmResults<Producto> realmResults) {
        super(context, realmResults, true, true);
    }

    public static final String ID_TIENDA = "idServer";

    @Override
    public ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(inflater.inflate(R.layout.product_item, viewGroup, false));
    }

    @Override
    public void onBindRealmViewHolder(ViewHolder viewHolder, int i) {
        final Producto producto = realmResults.get(i);
        if (!producto.getImagen().equals("null")){
            Util.usarGlide(getContext(), viewHolder.imagen, producto.getImagen());
        }
        viewHolder.titulo.setText(producto.getNombre());
        viewHolder.codigo.setText(producto.getCodigo());
        if (!producto.getPrecio_descuento().equals("null") && !producto.getPrecio_descuento().isEmpty()){
            viewHolder.precio_actual.setText("S/ " + String.format("%.2f",Double.parseDouble(producto.getPrecio_descuento())));
            viewHolder.precio_anterior.setText("S/ " + String.format("%.2f",Double.parseDouble(producto.getPrecio_normal())));
            viewHolder.precio_anterior.setPaintFlags(viewHolder.precio_anterior.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else {
            viewHolder.precio_actual.setText("S/ " + String.format("%.2f",Double.parseDouble(producto.getPrecio_normal())));
            viewHolder.precio_anterior.setText("");
        }

        viewHolder.seleccionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View vw = LayoutInflater.from(getContext()).inflate(R.layout.dialogo_enviar_mensaje, null);
                final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setView(vw);
                TextView txtTienda = (TextView)vw.findViewById(R.id.txtTienda);
                TextView txtTelefono = (TextView)vw.findViewById(R.id.txtTelefono);

                Realm realm = Realm.getDefaultInstance();
                final Tienda tienda;
                tienda = realm.where(Tienda.class).equalTo(ID_TIENDA, producto.getFkTienda()).findFirst();
                txtTienda.setText(tienda.getNombre());
                txtTelefono.setText(tienda.getTelefono());

                Button btnLlamar = (Button)vw.findViewById(R.id.btnLlamar);
                btnLlamar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:"+tienda.getTelefono()));
                        try{
                            getContext().startActivity(intent);
                        }catch (Exception e){
                            Log.d(TAG,e.getMessage());
                            Toast.makeText(getContext(),"Este número no existe",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                Button btnEnviarEmail = (Button)vw.findViewById(R.id.btnEnviarEmail);
                btnEnviarEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri uri = Uri.parse("smsto:"+tienda.getTelefono());
                        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                        getContext().startActivity(intent);
                    }
                });
                dialog.create();
                dialog.show();
            }
        });
    }

    public class ViewHolder extends RealmViewHolder {
        @BindView(R.id.producto_imagen) ImageView imagen;
        @BindView(R.id.producto_titulo) TextView titulo;
        @BindView(R.id.producto_codigo) TextView codigo;
        @BindView(R.id.precio_actual) TextView precio_actual;
        @BindView(R.id.precio_anterior) TextView precio_anterior;
        @BindView(R.id.btnSeleccionar) LinearLayout seleccionar;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}