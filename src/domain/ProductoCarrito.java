package domain;

public class ProductoCarrito {
    private int id;
    private String nombre;
    private double precio;
    private int cantidad;

    public ProductoCarrito(String nombre, double precio, int cantidad) {
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    public ProductoCarrito(int id, String nombre, double precio, int cantidad) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getNombre() { return nombre; }

    public void setPrecio(double precio) { this.precio = precio; }
    public double getPrecio() { return precio; }
    
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    
    public double getSubtotal() { return precio * cantidad; }
}
