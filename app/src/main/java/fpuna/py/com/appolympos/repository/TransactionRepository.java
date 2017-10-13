package fpuna.py.com.appolympos.repository;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import fpuna.py.com.appolympos.entities.Transactions;
import fpuna.py.com.appolympos.entities.TransactionsDao;
import fpuna.py.com.appolympos.utiles.Constants;
import fpuna.py.com.appolympos.utiles.Main;
import fpuna.py.com.appolympos.utiles.Utils;

/**
 * Created by Diego on 7/2/2017.
 */

public class TransactionRepository {

    public static TransactionsDao getDao() {
        return Main.getDaoSession().getTransactionsDao();
    }

    public static void clearAll() {
        getDao().deleteAll();
    }

    public static List<Transactions> getAll() {
        return getDao().queryBuilder().orderDesc(TransactionsDao.Properties.Id).list();
    }

    public static QueryBuilder<Transactions> query() {
        return getDao().queryBuilder();
    }

    public static long store(Transactions transaction) {
        long id = getDao().insertOrReplace(transaction);
        Utils.callNoSaleLoader(Main.getInstance());
        Utils.callOrderLoader(Main.getInstance());
        return id;
    }

    public static void delete(Transactions transaction) {
        getDao().delete(transaction);
        Utils.callNoSaleLoader(Main.getInstance());
        Utils.callOrderLoader(Main.getInstance());
    }


    public static Transactions getById(long id) {
        if (id <= 0) return null;

        List<Transactions> result = query().
                where(TransactionsDao.Properties.Id.eq(id))
                .limit(1).list();

        if (result.size() <= 0) return null;

        return result.get(0);
    }


    public static List<Transactions> getAllPendingTransactions() {
        return getDao().queryBuilder().where(TransactionsDao.Properties.Status.eq(Constants.TRANSACTION_PENDING)).list();
    }


}
