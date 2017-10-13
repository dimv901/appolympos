package fpuna.py.com.appolympos.repository;

import android.support.annotation.Nullable;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import fpuna.py.com.appolympos.entities.NoCompra;
import fpuna.py.com.appolympos.entities.NoCompraDao;
import fpuna.py.com.appolympos.utiles.Main;

/**
 * Created by Diego on 6/8/2017.
 */

public class NoCompraRepository {

    public static NoCompraDao getDao() {
        return Main.getDaoSession().getNoCompraDao();
    }

    public static void clearAll() {
        getDao().deleteAll();
    }

    public static List<NoCompra> getAll() {
        return getDao().queryBuilder().orderDesc(NoCompraDao.Properties.Id).list();
    }

    public static QueryBuilder<NoCompra> query() {
        return getDao().queryBuilder();
    }

    public static long store(NoCompra noCompra) {
        long id = getDao().insertOrReplace(noCompra);
        return id;
    }

    public static void delete(NoCompra noCompra) {
        getDao().delete(noCompra);
    }

    @Nullable
    public static NoCompra getById(long id) {
        if (id <= 0) return null;
        List<NoCompra> result = query().
                where(NoCompraDao.Properties.Id.eq(id))
                .limit(1).list();

        if (result.size() <= 0) return null;

        return result.get(0);
    }

    public static long count() {
        return getDao().queryBuilder().count();
    }
}
