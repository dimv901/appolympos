package fpuna.py.com.appolympos.utiles;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import fpuna.py.com.appolympos.entities.DaoMaster;
import fpuna.py.com.appolympos.entities.DaoSession;

/**
 * Created by Diego on 6/8/2017.
 */

public class Main extends Application {
    private static Main INSTANCE = null;
    public DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        setupDatabase();
    }


    private void setupDatabase() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "olymposdb", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public static Main getInstance() {
        return INSTANCE;
    }

    public static DaoSession getDaoSession() {
        return getInstance().daoSession;
    }
}

