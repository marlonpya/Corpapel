package com.littletemplate.corpapel.model;

import android.util.Log;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
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
    private String apellidos;
    private String direccion;
    private String departamento;
    private String provincia;
    private String distrito;
    private String correo;
    private String movil;
    private boolean sesion;

    public static int getUltimoId() {
        Realm realm = Realm.getDefaultInstance();
        Number number = realm.where(Usuario.class).max(ID);
        return number == null ? 0 : number.intValue() + 1;
    }

    public static void crearSesion(Usuario usuario) {
        Realm realm = Realm.getDefaultInstance();
        Usuario usuario1 = realm.where(Usuario.class).equalTo(ID, ID_SESION).findFirst();
        realm.beginTransaction();
        if (usuario1 == null) {
            Usuario usuario2 = realm.createObject(Usuario.class, ID_SESION);
            usuario2.setNombres(usuario.getNombres());
            usuario2.setApellidos(usuario.getApellidos());
            usuario2.setDireccion(usuario.getDireccion());
            usuario2.setDepartamento(usuario.getDepartamento());
            usuario2.setProvincia(usuario.getProvincia());
            usuario2.setDistrito(usuario.getDistrito());
            usuario2.setCorreo(usuario.getCorreo());
            usuario2.setMovil(usuario.getMovil());
            usuario2.setSesion(true);
            realm.copyToRealm(usuario2);
            Log.d(TAG, usuario2.toString());
        } else {
            usuario1.setId(ID_SESION);
            usuario1.setNombres(usuario.getNombres());
            usuario1.setApellidos(usuario.getApellidos());
            usuario1.setDireccion(usuario.getDireccion());
            usuario1.setDepartamento(usuario.getDepartamento());
            usuario1.setProvincia(usuario.getProvincia());
            usuario1.setDistrito(usuario.getDistrito());
            usuario1.setCorreo(usuario.getCorreo());
            usuario1.setMovil(usuario.getMovil());
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
        usuario.setApellidos("");
        usuario.setDireccion("");
        usuario.setDepartamento("");
        usuario.setProvincia("");
        usuario.setDistrito("");
        usuario.setCorreo("");
        usuario.setMovil("");
        usuario.setSesion(false);
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

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
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

    public boolean isSesion() {
        return sesion;
    }

    public void setSesion(boolean sesion) {
        this.sesion = sesion;
    }
}
