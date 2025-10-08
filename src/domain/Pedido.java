package domain;

public class Pedido {

    private int id;
    private String detalles;
    private String fecha;
    private String direccion;
    private String estado;

    public Pedido(int id, String detalles, String fecha, String direccion, String estado) {
        this.id = id;
        this.detalles = detalles;
        this.fecha = fecha;
        this.direccion = direccion;
        this.estado = estado;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Pedido [id=" + id + ", detalles=" + detalles + ", fecha=" + fecha + ", direccion=" + direccion
                + ", estado=" + estado + "]";
    }
}
