package domain;

public class Producto {
    private int id;
    private String nombre;
    private String descripcion;
    private double precio;
    private String imagen; // ruta de la imagen
    private int stock;

    public Producto(int id, String nombre, String descripcion, double precio, String imagen, int stock) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagen = imagen;
        this.stock = stock;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}
