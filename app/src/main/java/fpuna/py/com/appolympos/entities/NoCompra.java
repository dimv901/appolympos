package fpuna.py.com.appolympos.entities;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "NO_COMPRA".
 */
public class NoCompra {

    private Long id;
    private String descripcion;

    public NoCompra() {
    }

    public NoCompra(Long id) {
        this.id = id;
    }

    public NoCompra(Long id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
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

}
