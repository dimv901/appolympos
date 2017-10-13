package fpuna.py.com.appolympos;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;


public class MyDaoGenerator {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "fpuna.py.com.appolympos.entities");

        //Clientes Table
        Entity clientes = schema.addEntity("Clientes");
        clientes.addLongProperty("id").primaryKey();
        clientes.addStringProperty("nombreTitular");
        clientes.addStringProperty("cedulaTitular");
        clientes.addStringProperty("contactoTitular");
        clientes.addStringProperty("emailTitular");
        clientes.addStringProperty("razonSocial");
        clientes.addStringProperty("nombreNegocio");
        clientes.addStringProperty("ruc");
        clientes.addStringProperty("emailNegocio");
        clientes.addStringProperty("telefonoNegocio");
        clientes.addStringProperty("barrio");
        clientes.addStringProperty("callePrincipal");
        clientes.addStringProperty("calleSecundaria");
        clientes.addIntProperty("numeroCasa");
        clientes.addStringProperty("direccion");
        clientes.addStringProperty("referencia");
        clientes.addBooleanProperty("geolocalizado");
        clientes.addDoubleProperty("latitud");
        clientes.addDoubleProperty("longitud");
        clientes.addByteArrayProperty("foto");
        clientes.addBooleanProperty("tieneFoto");
        clientes.addBooleanProperty("lunes");
        clientes.addBooleanProperty("martes");
        clientes.addBooleanProperty("miercoles");
        clientes.addBooleanProperty("jueves");
        clientes.addBooleanProperty("viernes");
        clientes.addBooleanProperty("sabado");
        clientes.addBooleanProperty("domingo");
        clientes.addLongProperty("idCircuito");
        clientes.addLongProperty("idCiudad");
        clientes.addLongProperty("idDepartamento");
        clientes.addLongProperty("idFormaPago");
        clientes.addStringProperty("horaVisita");
        clientes.addBooleanProperty("visitado");

        //Circuitos Table
        Entity circuitos = schema.addEntity("Circuitos");
        circuitos.addLongProperty("id").primaryKey();
        circuitos.addStringProperty("codigo");
        circuitos.addStringProperty("descripcion");

        //Departamentos Table
        Entity departamentos = schema.addEntity("Departamentos");
        departamentos.addLongProperty("id").primaryKey();
        departamentos.addStringProperty("descripcion");

        //Ciudades Table
        Entity ciudades = schema.addEntity("Ciudades");
        ciudades.addLongProperty("id").primaryKey();
        ciudades.addStringProperty("descripcion");
        ciudades.addLongProperty("idDepartamento");

        //Vendedores Table
        Entity vendedores = schema.addEntity("Vendedores");
        vendedores.addLongProperty("id").primaryKey();
        vendedores.addStringProperty("nombre");
        vendedores.addStringProperty("apellido");
        vendedores.addStringProperty("cedula");
        vendedores.addBooleanProperty("activo");
        vendedores.addBooleanProperty("android");
        vendedores.addStringProperty("telefonoContacto");
        vendedores.addLongProperty("idCircuito");

        //Productos Table
        Entity productos = schema.addEntity("Productos");
        productos.addLongProperty("id").primaryKey();
        productos.addStringProperty("descripcion");
        productos.addStringProperty("codigoBarra");
        productos.addDoubleProperty("precioVenta");
        productos.addDoubleProperty("cantidad");
        productos.addBooleanProperty("tieneFoto");
        productos.addStringProperty("foto");


        //No Compra Table
        Entity noCompra = schema.addEntity("NoCompra");
        noCompra.addLongProperty("id").primaryKey();
        noCompra.addStringProperty("descripcion");

        //Transaction Table
        Entity transactions = schema.addEntity("Transactions");
        transactions.addIdProperty();
        transactions.addStringProperty("type");
        transactions.addStringProperty("ClientId");
        transactions.addStringProperty("ClientName");
        transactions.addIntProperty("amount");
        transactions.addStringProperty("httpDetail");
        transactions.addIntProperty("status");
        transactions.addIntProperty("attempts");
        transactions.addDateProperty("createdAt");
        transactions.addDateProperty("updatedAt");
        transactions.addStringProperty("observation");
        transactions.addIntProperty("queued");
        transactions.addIntProperty("tries");

        /*//Order Table
        Entity orders = schema.addEntity("Orders");
        orders.addIdProperty();
        orders.addStringProperty("ClientId");
        orders.addIntProperty("amount");
        orders.addDateProperty("createdAt");


        Entity ordersItem = schema.addEntity("OrdersItem");
        ordersItem.addIdProperty();
        ordersItem.addStringProperty("ClientId");
        ordersItem.addIntProperty("amount");
        ordersItem.addDateProperty("createdAt");
        */


        System.out.println("GENERATING TABLES FOR OLYMPOS DB...");
        new DaoGenerator().generateAll(schema, "../app/src/main/java");
    }
}
