package fpuna.py.com.appolympos.entities;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "FORMAS_PAGO".
 */
public class FormasPago {

    private Long id;
    private String descripcion;
    private java.util.Date fechaCreacion;
    private java.util.Date fechaActualizacion;

    public FormasPago() {
    }

    public FormasPago(Long id) {
        this.id = id;
    }

    public FormasPago(Long id, String descripcion, java.util.Date fechaCreacion, java.util.Date fechaActualizacion) {
        this.id = id;
        this.descripcion = descripcion;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public java.util.Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(java.util.Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public java.util.Date getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(java.util.Date fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

}
