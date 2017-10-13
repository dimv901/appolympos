package fpuna.py.com.appolympos.repository;

import android.support.annotation.Nullable;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import fpuna.py.com.appolympos.entities.Ciudades;
import fpuna.py.com.appolympos.entities.CiudadesDao;
import fpuna.py.com.appolympos.utiles.Main;

/**
 * Created by Diego on 6/11/2017.
 */

public class CiudadRepository {

    public static CiudadesDao getDao() {
        return Main.getDaoSession().getCiudadesDao();
    }

    public static void clearAll() {
        getDao().deleteAll();
    }

    public static List<Ciudades> getAll() {
        return getDao().queryBuilder().orderDesc(CiudadesDao.Properties.Id).list();
    }

    public static QueryBuilder<Ciudades> query() {
        return getDao().queryBuilder();
    }

    public static long store(Ciudades ciudades) {
        long id = getDao().insertOrReplace(ciudades);
        return id;
    }

    public static void delete(Ciudades ciudades) {
        getDao().delete(ciudades);
    }

    @Nullable
    public static Ciudades getById(long id) {
        if (id <= 0) return null;
        List<Ciudades> result = query().
                where(CiudadesDao.Properties.Id.eq(id))
                .limit(1).list();
        if (result.size() <= 0) return null;

        return result.get(0);
    }
}
