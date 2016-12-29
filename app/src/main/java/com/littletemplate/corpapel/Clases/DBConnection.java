package com.littletemplate.corpapel.Clases;

import android.content.Context;
import android.os.StrictMode;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrador on 09/11/2016.
 */

public class DBConnection {
    static Connection conn;
    static String ip;
    static String puerto;
    static String baseDatos;
    static String user;
    static String password;


    static public void Conectar(Context context) {
            ip = "192.168.1.5";
            puerto ="3306";
            baseDatos = "corpapel";
            user = "corpapeluser";
            password ="123corpapelroot";

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();

                conn = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + puerto + "/" + baseDatos, user, password);
               // Toast.makeText(context,"lo logre",Toast.LENGTH_LONG).show();
                //Connection con = DriverManager.getConnection(url, user, password);
            } catch (InstantiationException e) {
                e.printStackTrace();
                Toast.makeText(context,"error: "+e,Toast.LENGTH_LONG).show();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                Toast.makeText(context,"error: "+e,Toast.LENGTH_LONG).show();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(context,"error: "+e,Toast.LENGTH_LONG).show();
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(context,"error: "+e,Toast.LENGTH_LONG).show();
            }

        }

    static public void Desconectar() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static public List<Tienda> GetTiendas(Context context) {
        List<Tienda> tiendas = new ArrayList<>();

        Conectar(context);

        Statement st = null;
        try {
            st = conn.createStatement();
            ResultSet result = st.executeQuery("select * from tienda");
            int cantidad = 0;
            if (result != null) {
                while (result.next()) {
                    cantidad = cantidad + 1;

                    Tienda tienda = new Tienda(result.getString("nombre"),result.getString("direccion"),result.getInt("horario_inicio"),result.getInt("horario_fin"),result.getDouble("latitud"),result.getDouble("longitud"));
                    //  String lastName = result.getString("nombre");
                    // System.out.println(lastName + "\n");
                    // Toast.makeText(context,"el primero se llama: "+result.getString("nombre"),Toast.LENGTH_LONG).show();
                    tiendas.add(tienda);


                }
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tiendas;
    }
}
