package Clases;

/**
 * Created by Administrador on 10/11/2016.
 */

public class Tienda {
    public String nombre;
    public String direccion;
    public int horaInicio;
    public int horaFin;
    public double latitud;
    public double longitud;


    public Tienda (String nombre, String direccion, int horaInicio, int horaFin, double latitud, double longitud) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.latitud = latitud;
        this.longitud = longitud;
    }
}
