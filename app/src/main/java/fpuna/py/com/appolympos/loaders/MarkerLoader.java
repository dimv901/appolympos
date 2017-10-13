package fpuna.py.com.appolympos.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import fpuna.py.com.appolympos.entities.Clientes;
import fpuna.py.com.appolympos.entities.ClientesDao;
import fpuna.py.com.appolympos.receivers.MarkersObserver;
import fpuna.py.com.appolympos.repository.ClienteRepository;

/**
 * Created by Diego on 9/27/2017.
 */

public class MarkerLoader extends AsyncTaskLoader<List<MarkerOptions>> {
    private MarkersObserver mObserver;
    private Context mContext;
    private List<MarkerOptions> mData;

    public MarkerLoader(Context context) {
        super(context);
        mData = new ArrayList<>();
    }

    @Override
    public List<MarkerOptions> loadInBackground() {
        QueryBuilder queryBuilder = ClienteRepository.query();
        Calendar calendar = Calendar.getInstance();
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
        }
        queryBuilder.orderDesc(ClientesDao.Properties.Id);


        List<Clientes> list = (List<Clientes>) queryBuilder.list();
        for (Clientes c : list) {
            if (c.getGeolocalizado()) {
                MarkerOptions options = new MarkerOptions();
                if (c.getVisitado()) {
                    options.position(new LatLng(c.getLatitud(), c.getLongitud()));
                    options.title(c.getNombreNegocio());
                    options.snippet("VISITADO");
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    mData.add(options);
                } else {
                    options.position(new LatLng(c.getLatitud(), c.getLongitud()));
                    options.title(c.getNombreNegocio());
                    options.snippet("PENDIENTE");
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    mData.add(options);
                }

            }
        }
        return mData;
    }

    @Override
    public void deliverResult(List<MarkerOptions> data) {
        if (isReset()) {
            // The Loader has been reset; ignore the result and invalidate the data.
            releaseResources(data);
            return;
        }

        // Hold a reference to the old data so it doesn't get garbage collected.
        // We must protect it until the new data has been delivered.
        List<MarkerOptions> oldData = mData;
        mData = data;

        if (isStarted()) {
            // If the Loader is in a started state, deliver the results to the
            // client. The superclass method does this for us.
            super.deliverResult(data);
        }

        // Invalidate the old data as we don't need it any more.
        if (oldData != null && oldData != data) {
            releaseResources(oldData);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mData != null) {
            // Deliver any previously loaded data immediately.
            deliverResult(mData);
        }

        // Begin monitoring the underlying data source.
        if (mObserver == null) {
            mObserver = new MarkersObserver(this);
        }

        if (takeContentChanged() || mData == null) {
            // When the observer detects a change, it should call onContentChanged()
            // on the Loader, which will cause the next call to takeContentChanged()
            // to return true. If this is ever the case (or if the current data is
            // null), we force a new load.
            forceLoad();
        }
    }


    @Override
    protected void onStopLoading() {
        // The Loader is in a stopped state, so we should attempt to cancel the
        // current load (if there is one).
        cancelLoad();

        // Note that we leave the observer as is. Loaders in a stopped state
        // should still monitor the data source for changes so that the Loader
        // will know to force a new load if it is ever started again.
    }

    @Override
    protected void onReset() {
        // Ensure the loader has been stopped.
        onStopLoading();

        // At this point we can release the resources associated with 'mData'.
        if (mData != null) {
            releaseResources(mData);
            mData = null;
        }

        // The Loader is being reset, so we should stop monitoring for changes.
        if (mObserver != null) {
            getContext().unregisterReceiver(mObserver);
            mObserver = null;
        }
    }

    @Override
    public void onCanceled(List<MarkerOptions> data) {
        // Attempt to cancel the current asynchronous load.
        super.onCanceled(data);

        // The load has been canceled, so we should release the resources
        // associated with 'data'.
        releaseResources(data);
    }

    private void releaseResources(List<MarkerOptions> data) {
        // For a simple List, there is nothing to do. For something like a Cursor, we
        // would close it in this method. All resources associated with the Loader
        // should be released here.
    }
}
