package com.littletemplate.corpapel.model;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by ucweb02 on 27/12/2016.
 */

public class Producto extends RealmObject{
    public static final String TAG = Producto.class.getSimpleName();
    public static final String ID = "id";


    @PrimaryKey
    private long id;
    private int idServer;
    private int fkTienda;

    @Required
    private String nombre;
    private String codigo;
    private String precio_normal;
    private String precio_descuento;
    private String imagen;


    public static int getUltimoId() {
        Realm realm = Realm.getDefaultInstance();
        Number number = realm.where(Producto.class).max(ID);
        return number == null ? 0 : number.intValue() + 1;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIdServer() {
        return idServer;
    }

    public void setIdServer(int idServer) {
        this.idServer = idServer;
    }

    public int getFkTienda() {
        return fkTienda;
    }

    public void setFkTienda(int fkTienda) {
        this.fkTienda = fkTienda;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getPrecio_normal() {
        return precio_normal;
    }

    public void setPrecio_normal(String precio_normal) {
        this.precio_normal = precio_normal;
    }

    public String getPrecio_descuento() {
        return precio_descuento;
    }

    public void setPrecio_descuento(String precio_descuento) {
        this.precio_descuento = precio_descuento;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
