package main;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.SwingUtilities;

import db.BaseDatosConfig;
import gui.VentanaInicioSesion;

public class Main {

	public static void main(String[] args) {
	    Connection con = BaseDatosConfig.initBD("resources/db/MyMerch.db");
	    File dbFile = new File("resources/db/MyMerch.db");

	    try {
	        BaseDatosConfig.crearTablas(con);
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    BaseDatosConfig.closeBD(con);
	 
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new VentanaInicioSesion(); 
            }
        });
    }
}
