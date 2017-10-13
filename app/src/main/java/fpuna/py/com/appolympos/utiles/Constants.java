package fpuna.py.com.appolympos.utiles;

/**
 * Created by Diego on 5/30/2017.
 */

public class Constants {

    public static int RESPONSE_OK = 100;
    public static final String NO_SALE_TRANSACTION = "NO_COMPRA";
    public static final String ORDER_TRANSACTION = "ORDEN";

    //Transaction Status
    public static final int TRANSACTION_SEND = 1;
    public static final int TRANSACTION_PENDING = 2;
    public static final int TRANSACTION_ERROR = 3;

    // Operation URL
    public static final String LOGIN_URL = "http://192.168.43.242:8090/RestOlympos/webresources/entities.usuariosmoviles/login";
    public static final String UPDATE_PICTURE_URL = "http://192.168.43.242:8090/RestOlympos/webresources/entities.clientes/actualizarClienteFoto";
    public static final String UPDATE_LOCATION_URL = "http://192.168.43.242:8090/RestOlympos/webresources/entities.clientes/actualizarClienteUbicacion";
    public static final String NO_SALE_URL = "http://192.168.43.242:8090/RestOlympos/webresources/entities.visitas/registrarVisita";
    public static final String ORDER_URL = "http://192.168.43.242:8090/RestOlympos/webresources/entities.pedidoscabecera/registrarPedido";
    public static final String RESET_PIN_URL = "http://192.168.43.242:8090/RestOlympos/webresources/entities.usuariosmoviles/updatePin";

    // Sync Url
    public static final String SYNC_VENDEDOR_URL = "http://192.168.43.242:8090/RestOlympos/webresources/entities.vendedores/getDatosVendedor";
    public static final String SYNC_NO_COMPRA_URL = "http://192.168.43.242:8090/RestOlympos/webresources/entities.motivosnocompra/getMotivosNoCompra";
    public static final String SYNC_CLIENTE_URL = "http://192.168.43.242:8090/RestOlympos/webresources/entities.clientes/getClientes";
    public static final String SYNC_CIUDADES_URL = "http://192.168.43.242:8090/RestOlympos/webresources/entities.ciudades/getCiudades";
    public static final String SYNC_DEPARTAMENTOS_URL = "http://192.168.43.242:8090/RestOlympos/webresources/entities.departamentos/getDepartamentos";
    public static final String SYNC_CIRCUITOS_URL = "http://192.168.43.242:8090/RestOlympos/webresources/entities.circuitos/getCircuitos";
    public static final String SYNC_PRODUCTO_URL = "http://192.168.43.242:8090/RestOlympos/webresources/entities.productos/getProductos";

    // Date Format
    public static final String DEFAULT_DATETIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
    public static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
    public static final String DEFAULT_TIME_FORMAT = "HH:mm";
}
