package fpuna.py.com.appolympos.repository;

import android.support.annotation.Nullable;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import fpuna.py.com.appolympos.entities.Circuitos;
import fpuna.py.com.appolympos.entities.CircuitosDao;
import fpuna.py.com.appolympos.utiles.Main;

/**
 * Created by Diego on 6/11/2017.
 */

public class CircuitoRepository {

    public static CircuitosDao getDao() {
        return Main.getDaoSession().getCircuitosDao();
    }

    public static void clearAll() {
        getDao().deleteAll();
    }

    public static List<Circuitos> getAll() {
        return getDao().queryBuilder().orderDesc(CircuitosDao.Properties.Id).list();
    }

    public static QueryBuilder<Circuitos> query() {
        return getDao().queryBuilder();
    }

    public static long store(Circuitos circuitos) {
        long id = getDao().insertOrReplace(circuitos);
        return id;
    }

    public static void delete(Circuitos circuitos) {
        getDao().delete(circuitos);
    }

    @Nullable
    public static Circuitos getById(long id) {
        if (id <= 0) return null;
        List<Circuitos> result = query().
                where(CircuitosDao.Properties.Id.eq(id))
                .limit(1).list();
        if (result.size() <= 0) return null;

        return result.get(0);
    }
}
