package fpuna.py.com.appolympos.utiles;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Diego on 5/30/2017.
 */

public class AppPreferences {

    private static SharedPreferences mAppPreferences;
    private static final String SHARED_PREFERENCE_APP = "SHARED_PREFERENCE_APP";
    public static final String KEY_PREFERENCE_LOGGED_IN = "KEY_LOGGED_IN";
    public static final String KEY_PREFERENCE_USUARIO = "KEY_USUARIO";
    public static final String KEY_PREFERENCE_LAST_SYNC = "KEY_LAST_SYNC";

    public static SharedPreferences getAppPreferences(Context context) {
        if (mAppPreferences == null) {
            mAppPreferences = context.getSharedPreferences(SHARED_PREFERENCE_APP, Context.MODE_PRIVATE);
        }
        return mAppPreferences;
    }

}
