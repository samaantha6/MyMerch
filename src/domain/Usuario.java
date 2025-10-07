package domain;

public class Usuario {

    public static final String[] DOMINIOS_VALIDOS = {"@gmail.com", "@merch.com"};

    private int id; // Se asigna automáticamente en la BD
    private String nombre;
    private String apellidos;
    private String correo;
    private String telefono;
    private String contrasena;

    public Usuario(String nombre, String apellidos, String correo, String telefono, String contrasena) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.correo = correo;
        this.telefono = telefono;
        this.contrasena = contrasena;
    }

    public Usuario(int id, String nombre, String apellidos, String correo, String telefono, String contrasena) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.correo = correo;
        this.telefono = telefono;
        this.contrasena = contrasena;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public static boolean dominioValido(String correo) {
        for (String dominio : DOMINIOS_VALIDOS) {
            if (correo.endsWith(dominio)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("Usuario{id=%d, nombre='%s', apellidos='%s', correo='%s', telefono='%s'}",
                id, nombre, apellidos, correo, telefono);
    }
}
