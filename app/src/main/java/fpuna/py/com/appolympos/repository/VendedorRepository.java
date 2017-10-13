package fpuna.py.com.appolympos.repository;

import android.support.annotation.Nullable;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import fpuna.py.com.appolympos.entities.Vendedores;
import fpuna.py.com.appolympos.entities.VendedoresDao;
import fpuna.py.com.appolympos.utiles.Main;

/**
 * Created by Diego on 6/11/2017.
 */

public class VendedorRepository {

    public static VendedoresDao getDao() {
        return Main.getDaoSession().getVendedoresDao();
    }

    public static void clearAll() {
        getDao().deleteAll();
    }

    public static List<Vendedores> getAll() {
        return getDao().queryBuilder().orderDesc(VendedoresDao.Properties.Id).list();
    }

    public static QueryBuilder<Vendedores> query() {
        return getDao().queryBuilder();
    }

    public static long store(Vendedores vendedores) {
        long id = getDao().insertOrReplace(vendedores);
        return id;
    }

    public static Vendedores getSalesmanByUserName(String mUserName){
        return getDao().queryBuilder().where(VendedoresDao.Properties.Cedula.eq(mUserName)).unique();
    }

    public static void delete(Vendedores vendedores) {
        getDao().delete(vendedores);
    }

    @Nullable
    public static Vendedores getById(long id) {
        if (id <= 0) return null;
        List<Vendedores> result = query().
                where(VendedoresDao.Properties.Id.eq(id))
                .limit(1).list();
        if (result.size() <= 0) return null;
        return result.get(0);
    }
}
