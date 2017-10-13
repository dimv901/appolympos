package fpuna.py.com.appolympos.repository;

import android.support.annotation.Nullable;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import fpuna.py.com.appolympos.entities.Productos;
import fpuna.py.com.appolympos.entities.ProductosDao;
import fpuna.py.com.appolympos.utiles.Main;

/**
 * Created by Diego on 6/11/2017.
 */

public class ProductoRepository {

    public static ProductosDao getDao() {
        return Main.getDaoSession().getProductosDao();
    }

    public static void clearAll() {
        getDao().deleteAll();
    }

    public static List<Productos> getAll() {
        return getDao().queryBuilder().orderDesc(ProductosDao.Properties.Id).list();
    }

    public static QueryBuilder<Productos> query() {
        return getDao().queryBuilder();
    }

    public static long store(Productos productos) {
        long id = getDao().insertOrReplace(productos);
        return id;
    }

    public static long count() {
        return getDao().queryBuilder().count();
    }

    public static void delete(Productos productos) {
        getDao().delete(productos);
    }

    @Nullable
    public static Productos getById(long id) {
        if (id <= 0) return null;
        List<Productos> result = query().
                where(ProductosDao.Properties.Id.eq(id))
                .limit(1).list();

        if (result.size() <= 0) return null;

        return result.get(0);
    }
}
