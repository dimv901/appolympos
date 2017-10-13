package fpuna.py.com.appolympos.entities;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "VENDEDORES".
 */
public class Vendedores {

    private Long id;
    private String nombre;
    private String apellido;
    private String cedula;
    private Boolean activo;
    private Boolean android;
    private String telefonoContacto;
    private Long idCircuito;

    public Vendedores() {
    }

    public Vendedores(Long id) {
        this.id = id;
    }

    public Vendedores(Long id, String nombre, String apellido, String cedula, Boolean activo, Boolean android, String telefonoContacto, Long idCircuito) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.cedula = cedula;
        this.activo = activo;
        this.android = android;
        this.telefonoContacto = telefonoContacto;
        this.idCircuito = idCircuito;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Boolean getAndroid() {
        return android;
    }

    public void setAndroid(Boolean android) {
        this.android = android;
    }

    public String getTelefonoContacto() {
        return telefonoContacto;
    }

    public void setTelefonoContacto(String telefonoContacto) {
        this.telefonoContacto = telefonoContacto;
    }

    public Long getIdCircuito() {
        return idCircuito;
    }

    public void setIdCircuito(Long idCircuito) {
        this.idCircuito = idCircuito;
    }

}