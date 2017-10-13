package fpuna.py.com.appolympos.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.maps.android.clustering.ClusterManager;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import fpuna.py.com.appolympos.entities.Clientes;
import fpuna.py.com.appolympos.entities.ClientesDao;
import fpuna.py.com.appolympos.repository.ClienteRepository;
import fpuna.py.com.appolympos.utiles.MyMarkCluster;

/**
 * Created by Diego on 9/27/2017.
 */

public class ClusterMarkLoader extends AsyncTaskLoader<ClusterManager<MyMarkCluster>> {

    private Context mContext;
    private ClusterManager<MyMarkCluster> mClusterManager;

    public ClusterMarkLoader(Context context, GoogleMap map) {
        super(context);
        mClusterManager = new ClusterManager<>(context, map);
    }

    public ClusterMarkLoader(Context context) {
        super(context);
    }

    @Override
    public ClusterManager<MyMarkCluster> loadInBackground() {
        QueryBuilder queryBuilder = ClienteRepository.query();
        /*Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.SUNDAY:
                queryBuilder.where(ClientesDao.Properties.Domingo.eq(true));
                break;
            case Calendar.MONDAY:
                queryBuilder.where(ClientesDao.Properties.Lunes.eq(true));
                break;
            case Calendar.TUESDAY:
                queryBuilder.where(ClientesDao.Properties.Martes.eq(true));
                break;
            case Calendar.WEDNESDAY:
                queryBuilder.where(ClientesDao.Properties.Miercoles.eq(true));
                break;
            case Calendar.THURSDAY:
                queryBuilder.where(ClientesDao.Properties.Jueves.eq(true));
                break;
            case Calendar.FRIDAY:
                queryBuilder.where(ClientesDao.Properties.Viernes.eq(true));
                break;
            case Calendar.SATURDAY:
                queryBuilder.where(ClientesDao.Properties.Sabado.eq(true));
                break;
        }*/
        queryBuilder.orderDesc(ClientesDao.Properties.Id);

        List<Clientes> list = (List<Clientes>) queryBuilder.list();
        for (Clientes c : list) {
            if (c.getGeolocalizado()){
                mClusterManager.addItem(new MyMarkCluster(c.getLatitud(), c.getLatitud(), c.getNombreNegocio()));
                Log.d("LOADER MARKER", "CLIENTE CARGADO "+ c.getNombreNegocio());
            }

        }
        return mClusterManager;
    }
}
