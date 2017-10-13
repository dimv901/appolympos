package fpuna.py.com.appolympos.repository;

import android.support.annotation.Nullable;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import fpuna.py.com.appolympos.entities.Departamentos;
import fpuna.py.com.appolympos.entities.DepartamentosDao;
import fpuna.py.com.appolympos.utiles.Main;

/**
 * Created by Diego on 6/11/2017.
 */

public class DepartamentoRepository {

    public static DepartamentosDao getDao() {
        return Main.getDaoSession().getDepartamentosDao();
    }

    public static void clearAll() {
        getDao().deleteAll();
    }

    public static List<Departamentos> getAll() {
        return getDao().queryBuilder().orderDesc(DepartamentosDao.Properties.Id).list();
    }

    public static QueryBuilder<Departamentos> query() {
        return getDao().queryBuilder();
    }

    public static long store(Departamentos departamentos) {
        long id = getDao().insertOrReplace(departamentos);
        return id;
    }

    public static void delete(Departamentos departamentos) {
        getDao().delete(departamentos);
    }

    @Nullable
    public static Departamentos getById(long id) {
        if (id <= 0) return null;
        List<Departamentos> result = query().
                where(DepartamentosDao.Properties.Id.eq(id))
                .limit(1).list();
        if (result.size() <= 0) return null;
        return result.get(0);
    }
}
