package fpuna.py.com.appolympos.repository;

import android.support.annotation.Nullable;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import fpuna.py.com.appolympos.entities.Clientes;
import fpuna.py.com.appolympos.entities.ClientesDao;
import fpuna.py.com.appolympos.utiles.Main;
import fpuna.py.com.appolympos.utiles.Utils;
import okhttp3.internal.Util;

/**
 * Created by Diego on 6/11/2017.
 */

public class ClienteRepository {
    public static ClientesDao getDao() {
        return Main.getDaoSession().getClientesDao();
    }

    public static void clearAll() {
        getDao().deleteAll();
    }

    public static List<Clientes> getAll() {
        return getDao().queryBuilder().orderDesc(ClientesDao.Properties.Id).list();
    }

    public static QueryBuilder<Clientes> query() {
        return getDao().queryBuilder();
    }

    public static long store(Clientes clientes) {
        long id = getDao().insertOrReplace(clientes);
        Utils.callClienteLoader(Main.getInstance());
        Utils.callMarkersOptionLoader(Main.getInstance());
        return id;
    }

    public static void delete(Clientes clientes) {
        getDao().delete(clientes);
    }

    @Nullable
    public static Clientes getById(long id) {
        if (id <= 0) return null;
        List<Clientes> result = query().
                where(ClientesDao.Properties.Id.eq(id))
                .limit(1).list();
        if (result.size() <= 0) return null;

        return result.get(0);
    }
}
