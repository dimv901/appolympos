package fpuna.py.com.appolympos.entities;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig clientesDaoConfig;
    private final DaoConfig circuitosDaoConfig;
    private final DaoConfig departamentosDaoConfig;
    private final DaoConfig ciudadesDaoConfig;
    private final DaoConfig vendedoresDaoConfig;
    private final DaoConfig productosDaoConfig;
    private final DaoConfig noCompraDaoConfig;
    private final DaoConfig transactionsDaoConfig;

    private final ClientesDao clientesDao;
    private final CircuitosDao circuitosDao;
    private final DepartamentosDao departamentosDao;
    private final CiudadesDao ciudadesDao;
    private final VendedoresDao vendedoresDao;
    private final ProductosDao productosDao;
    private final NoCompraDao noCompraDao;
    private final TransactionsDao transactionsDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        clientesDaoConfig = daoConfigMap.get(ClientesDao.class).clone();
        clientesDaoConfig.initIdentityScope(type);

        circuitosDaoConfig = daoConfigMap.get(CircuitosDao.class).clone();
        circuitosDaoConfig.initIdentityScope(type);

        departamentosDaoConfig = daoConfigMap.get(DepartamentosDao.class).clone();
        departamentosDaoConfig.initIdentityScope(type);

        ciudadesDaoConfig = daoConfigMap.get(CiudadesDao.class).clone();
        ciudadesDaoConfig.initIdentityScope(type);

        vendedoresDaoConfig = daoConfigMap.get(VendedoresDao.class).clone();
        vendedoresDaoConfig.initIdentityScope(type);

        productosDaoConfig = daoConfigMap.get(ProductosDao.class).clone();
        productosDaoConfig.initIdentityScope(type);

        noCompraDaoConfig = daoConfigMap.get(NoCompraDao.class).clone();
        noCompraDaoConfig.initIdentityScope(type);

        transactionsDaoConfig = daoConfigMap.get(TransactionsDao.class).clone();
        transactionsDaoConfig.initIdentityScope(type);

        clientesDao = new ClientesDao(clientesDaoConfig, this);
        circuitosDao = new CircuitosDao(circuitosDaoConfig, this);
        departamentosDao = new DepartamentosDao(departamentosDaoConfig, this);
        ciudadesDao = new CiudadesDao(ciudadesDaoConfig, this);
        vendedoresDao = new VendedoresDao(vendedoresDaoConfig, this);
        productosDao = new ProductosDao(productosDaoConfig, this);
        noCompraDao = new NoCompraDao(noCompraDaoConfig, this);
        transactionsDao = new TransactionsDao(transactionsDaoConfig, this);

        registerDao(Clientes.class, clientesDao);
        registerDao(Circuitos.class, circuitosDao);
        registerDao(Departamentos.class, departamentosDao);
        registerDao(Ciudades.class, ciudadesDao);
        registerDao(Vendedores.class, vendedoresDao);
        registerDao(Productos.class, productosDao);
        registerDao(NoCompra.class, noCompraDao);
        registerDao(Transactions.class, transactionsDao);
    }
    
    public void clear() {
        clientesDaoConfig.getIdentityScope().clear();
        circuitosDaoConfig.getIdentityScope().clear();
        departamentosDaoConfig.getIdentityScope().clear();
        ciudadesDaoConfig.getIdentityScope().clear();
        vendedoresDaoConfig.getIdentityScope().clear();
        productosDaoConfig.getIdentityScope().clear();
        noCompraDaoConfig.getIdentityScope().clear();
        transactionsDaoConfig.getIdentityScope().clear();
    }

    public ClientesDao getClientesDao() {
        return clientesDao;
    }

    public CircuitosDao getCircuitosDao() {
        return circuitosDao;
    }

    public DepartamentosDao getDepartamentosDao() {
        return departamentosDao;
    }

    public CiudadesDao getCiudadesDao() {
        return ciudadesDao;
    }

    public VendedoresDao getVendedoresDao() {
        return vendedoresDao;
    }

    public ProductosDao getProductosDao() {
        return productosDao;
    }

    public NoCompraDao getNoCompraDao() {
        return noCompraDao;
    }

    public TransactionsDao getTransactionsDao() {
        return transactionsDao;
    }

}
