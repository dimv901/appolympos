package fpuna.py.com.appolympos.mask;

/**
 * Created by Diego on 7/11/2017.
 */

public class ProductMask {

    private Long id;
    private String descripcion;
    private String codigoBarra;
    private Double precioVenta;
    private Double cantidad;
    private Boolean tieneFoto;
    private String foto;

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

    public String getCodigoBarra() {
        return codigoBarra;
    }

    public void setCodigoBarra(String codigoBarra) {
        this.codigoBarra = codigoBarra;
    }

    public Double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(Double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public Double getCantidad() {
        return cantidad;
    }

    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad;
    }

    public Boolean getTieneFoto() {
        return tieneFoto;
    }

    public void setTieneFoto(Boolean tieneFoto) {
        this.tieneFoto = tieneFoto;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

}
