package com.littletemplate.corpapel.model;

import android.util.Log;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;

/**
 * Created by ucweb02 on 27/12/2016.
 */

public class Usuario extends RealmObject {
    public static final String TAG = Usuario.class.getSimpleName();
    public static final String ID = "id";
    public static final int ID_SESION = 1;

    @PrimaryKey
    private long id;
    @Required
    private String nombres;
    private String nombre_empresa;
    private String direccion;
    private String departamento;
    private String provincia;
    private String distrito;
    private String correo;
    private String movil;
    private String imagen;
    private String password;
    private String id_facebook;
    private String id_google;
    private boolean sesion;

    public static void crearSesion(Usuario usuario) {
        Realm realm = Realm.getDefaultInstance();
        Usuario usuario1 = realm.where(Usuario.class).equalTo(ID, ID_SESION).findFirst();
        realm.beginTransaction();
        if (usuario1 == null) {
            Usuario usuario2 = realm.createObject(Usuario.class, ID_SESION);
            usuario2.setNombres(usuario.getNombres());
            usuario2.setNombre_empresa(usuario.getNombre_empresa());
            usuario2.setDireccion(usuario.getDireccion());
            usuario2.setDepartamento(usuario.getDepartamento());
            usuario2.setProvincia(usuario.getProvincia());
            usuario2.setDistrito(usuario.getDistrito());
            usuario2.setCorreo(usuario.getCorreo());
            usuario2.setMovil(usuario.getMovil());
            usuario2.setImagen(usuario.getImagen());
            usuario2.setPassword(usuario.getPassword());
            usuario2.setId_facebook(usuario.getId_facebook());
            usuario2.setId_google(usuario.getId_google());
            usuario2.setSesion(true);
            realm.copyToRealm(usuario2);
            Log.d(TAG, usuario2.toString());
        } else {
            usuario1.setNombres(usuario.getNombres());
            usuario1.setNombre_empresa(usuario.getNombre_empresa());
            usuario1.setDireccion(usuario.getDireccion());
            usuario1.setDepartamento(usuario.getDepartamento());
            usuario1.setProvincia(usuario.getProvincia());
            usuario1.setDistrito(usuario.getDistrito());
            usuario1.setCorreo(usuario.getCorreo());
            usuario1.setMovil(usuario.getMovil());
            usuario1.setImagen(usuario.getImagen());
            usuario1.setPassword(usuario.getPassword());
            usuario1.setId_facebook(usuario.getId_facebook());
            usuario1.setId_google(usuario.getId_google());
            usuario1.setSesion(true);
            Log.d(TAG, usuario1.toString());
        }
        realm.commitTransaction();
        realm.close();
    }

    public static void cerrarSesion() {
        Realm realm = Realm.getDefaultInstance();
        Usuario usuario = realm.where(Usuario.class).equalTo(ID, ID_SESION).findFirst();
        realm.beginTransaction();
        usuario.setNombres("");
        usuario.setNombre_empresa("");
        usuario.setDireccion("");
        usuario.setDepartamento("");
        usuario.setProvincia("");
        usuario.setDistrito("");
        usuario.setCorreo("");
        usuario.setMovil("");
        usuario.setImagen("");
        usuario.setPassword("");
        usuario.setId_facebook("");
        usuario.setId_google("");
        usuario.setSesion(false);
        //modificacion AMD
        realm.commitTransaction();
        realm.close();
    }

    public static Usuario getUsuario() {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(Usuario.class).equalTo(ID, ID_SESION).findFirst();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getNombre_empresa() {
        return nombre_empresa;
    }

    public void setNombre_empresa(String nombre_empresa) {
        this.nombre_empresa = nombre_empresa;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getMovil() {
        return movil;
    }

    public void setMovil(String movil) {
        this.movil = movil;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSesion() {
        return sesion;
    }

    public void setSesion(boolean sesion) {
        this.sesion = sesion;
    }

    public String getId_facebook() {
        return id_facebook;
    }

    public void setId_facebook(String id_facebook) {
        this.id_facebook = id_facebook;
    }

    public String getId_google() {
        return id_google;
    }

    public void setId_google(String id_google) {
        this.id_google = id_google;
    }
}
