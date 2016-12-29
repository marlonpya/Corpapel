package com.littletemplate.corpapel.Clases;

/**
 * Created by Administrador on 10/11/2016.
 */

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.littletemplate.corpapel.R;

import java.util.List;


public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ProductViewHolder>{

  public  List<Producto> productos;

   public RVAdapter(List<Producto> productos){
        this.productos = productos;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        ProductViewHolder pvh = new ProductViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        holder.productoTitulo.setText(productos.get(position).nombre);
        holder.productoCodigo.setText(productos.get(position).codigo);
        //  holder.productPhoto.setImageResource(productos.get(position).photoId);
        holder.productoPrecio.setText("S/. "+productos.get(position).precio);
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView productoTitulo;
        TextView productoCodigo;
        TextView productoPrecio;
        // ImageView productPhoto;

        ProductViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            productoTitulo = (TextView)itemView.findViewById(R.id.producto_titulo);
            productoCodigo = (TextView)itemView.findViewById(R.id.producto_codigo);
            productoPrecio = (TextView)itemView.findViewById(R.id.producto_precio);
            // productPhoto = (ImageView)itemView.findViewById(R.id.producto_imagen);
        }
    }

}