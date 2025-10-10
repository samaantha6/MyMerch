package db;

import java.io.File;
import java.sql.*;
import java.util.logging.*;
import domain.*;

public class BaseDatosConfig {

    private static final Logger logger = Logger.getLogger(BaseDatosConfig.class.getName());

    public static Connection initBD(String nombreBD) {
        Connection con = null;

        try {
            File carpeta = new File("resources/db");
            if (!carpeta.exists()) {
                carpeta.mkdirs();
            }

            // Conectar
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:" + nombreBD);
            logger.info("Conexión con la base de datos establecida correctamente.");

        } catch (ClassNotFoundException e) {
            logger.severe("Driver SQLite no encontrado: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException ex) {
            logger.warning("Error conectando con la base de datos: " + ex.getMessage());
            ex.printStackTrace();
        }

        return con;
    }

    public static void closeBD(Connection con) {
        if (con != null) {
            try {
                con.close();
                logger.info("Conexión cerrada correctamente.");
            } catch (SQLException ex) {
                logger.warning("Error cerrando conexión con la BBDD: " + ex.getMessage());
            }
        }
    }

    public static void crearTablas(Connection con) throws SQLException {
        if (con == null) {
            logger.warning("No hay conexión a la base de datos.");
            return;
        }

        Statement stmt = con.createStatement();

        // Tabla Usuarios
        stmt.execute(
            "CREATE TABLE IF NOT EXISTS Usuarios (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nombre TEXT NOT NULL, " +
            "apellidos TEXT NOT NULL, " +
            "correo TEXT NOT NULL UNIQUE, " +
            "telefono TEXT, " +
            "contrasena TEXT NOT NULL" +
            ");"
        );

        // Tabla Productos
        stmt.execute(
            "CREATE TABLE IF NOT EXISTS Productos (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nombre TEXT NOT NULL, " +
            "descripcion TEXT, " +
            "precio REAL NOT NULL, " +
            "imagen TEXT" +
            ");"
        );

        // Tabla Pedidos (adaptada para dirección)
        stmt.execute(
            "CREATE TABLE IF NOT EXISTS Pedidos (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "id_usuario INTEGER NOT NULL, " +
            "fecha TEXT NOT NULL, " +
            "estado TEXT DEFAULT 'Pendiente', " +
            "direccion TEXT DEFAULT '', " +
            "pais TEXT DEFAULT '', " +
            "cp TEXT DEFAULT '', " +
            "provincia TEXT DEFAULT '', " +
            "FOREIGN KEY (id_usuario) REFERENCES Usuarios(id)" +
            ");"
        );

        // Tabla DetallePedido
        stmt.execute(
            "CREATE TABLE IF NOT EXISTS DetallePedido (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "id_pedido INTEGER NOT NULL, " +
            "id_producto INTEGER NOT NULL, " +
            "cantidad INTEGER NOT NULL, " +
            "FOREIGN KEY (id_pedido) REFERENCES Pedidos(id), " +
            "FOREIGN KEY (id_producto) REFERENCES Productos(id)" +
            ");"
        );

        logger.info("Tablas inicializadas correctamente.");
        stmt.close();
    }

    public static boolean insertarUsuario(Usuario u) {
        Connection con = initBD("resources/db/MyMerch.db");
        if (con != null) {
            try {
                String sql = "INSERT INTO Usuarios(nombre, apellidos, correo, telefono, contrasena) VALUES(?,?,?,?,?)";
                PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                pst.setString(1, u.getNombre());
                pst.setString(2, u.getApellidos());
                pst.setString(3, u.getCorreo());
                pst.setString(4, u.getTelefono());
                pst.setString(5, u.getContrasena());

                int rows = pst.executeUpdate();
                if (rows == 0) return false;

                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    u.setId(idGenerado);
                }

                rs.close();
                pst.close();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                closeBD(con);
            }
        }
        return false;
    }

}
