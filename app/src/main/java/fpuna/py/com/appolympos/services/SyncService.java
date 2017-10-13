package fpuna.py.com.appolympos.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import fpuna.py.com.appolympos.R;
import fpuna.py.com.appolympos.entities.Circuitos;
import fpuna.py.com.appolympos.entities.Ciudades;
import fpuna.py.com.appolympos.entities.Clientes;
import fpuna.py.com.appolympos.entities.Departamentos;
import fpuna.py.com.appolympos.entities.NoCompra;
import fpuna.py.com.appolympos.entities.Productos;
import fpuna.py.com.appolympos.entities.Vendedores;
import fpuna.py.com.appolympos.mask.ClientMask;
import fpuna.py.com.appolympos.mask.ProductMask;
import fpuna.py.com.appolympos.network.NetworkQueue;
import fpuna.py.com.appolympos.repository.CircuitoRepository;
import fpuna.py.com.appolympos.repository.CiudadRepository;
import fpuna.py.com.appolympos.repository.ClienteRepository;
import fpuna.py.com.appolympos.repository.DepartamentoRepository;
import fpuna.py.com.appolympos.repository.NoCompraRepository;
import fpuna.py.com.appolympos.repository.ProductoRepository;
import fpuna.py.com.appolympos.repository.VendedorRepository;
import fpuna.py.com.appolympos.utiles.Constants;
import fpuna.py.com.appolympos.utiles.Utils;
import okhttp3.internal.Util;

public class SyncService extends IntentService {

    private static final String TAG_CLASS = SyncService.class.getName();

    private static final String ACTION_SYNC = "fpuna.py.com.appolympos.service.SYNC";
    private static final String EXTRA_PARAM_URL = "fpuna.py.com.appolympos.service.extra.PARAM_URL";
    private static final String EXTRA_PARAM_JSON = "fpuna.py.com.appolympos.service.extra.PARAM_JSON";
    private static final String EXTRA_PARAM_SERVICES_AMOUNT = "fpuna.py.com.appolympos.service.PARAM_SERVICES_AMOUNT";

    public static final String PARAM_STATUS = "status";
    public static final String PARAM_MESSAGE = "message";
    public static final String PARAM_ITEM = "item";
    public static final String PARAM_LIST = "list";

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

    private NetworkQueue mMyQueue;

    public static final String JSON_PARAM_USUARIO = "usuario";

    // PROGRESS
    private static final int VENDEDOR_PROGRESS_ID = 1;
    private static final int NO_COMPRA_PROGRESS_ID = 2;
    private static final int CLIENTE_PROGRESS_ID = 3;
    private static final int CIUDAD_PROGRESS_ID = 4;
    private static final int DEPARTAMENTO_PROGRESS_ID = 5;
    private static final int CIRCUITO_PROGRESS_ID = 6;
    private static final int PRODUCTO_PROGRESS_ID = 7;


    public SyncService() {
        super("SyncService");
    }

    public static void startActionSync(Context context, String[] urlActions, String[] actionParams) {
        Intent intent = new Intent(context, SyncService.class);
        intent.setAction(ACTION_SYNC);
        intent.putExtra(EXTRA_PARAM_URL, urlActions);
        intent.putExtra(EXTRA_PARAM_JSON, actionParams);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            switch (action) {
                case ACTION_SYNC:
                    mNotificationManager = (NotificationManager)
                            getSystemService(Context.NOTIFICATION_SERVICE);

                    mBuilder = new NotificationCompat.Builder(getApplicationContext());

                    final String[] urlActions = intent.getStringArrayExtra(EXTRA_PARAM_URL);
                    final String[] actionParams = intent.getStringArrayExtra(EXTRA_PARAM_JSON);
                    handleActionSync(urlActions, actionParams);
                    break;
            }
        }
    }

    private void handleActionSync(String[] urlActions, String[] actionParams) {
        Log.d(TAG_CLASS, "Starting synchronization");
        mMyQueue = new NetworkQueue(getApplicationContext());

        Log.d(TAG_CLASS, "urlActions.length " + urlActions.length);
        for (int i = 0; i < urlActions.length; i++) {
            if ((actionParams[i] != null && !actionParams[i].isEmpty()) &&
                    (urlActions[i] != null && !urlActions[i].isEmpty())) {


                final String url = urlActions[i];
                JsonObjectRequest momoJsonObjectRequest = null;
                try {
                    momoJsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlActions[i], new JSONObject(actionParams[i]),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    if (response != null)
                                        handleActionByUrl(response, url);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    String message = NetworkQueue.handleError(error, getApplicationContext());
                                    Log.w(TAG_CLASS, "error_sync(url: " + url + ", resp: " + message + ")");

                                    int id = 0;
                                    String title = "";
                                    String text = "";

                                    switch (url) {
                                        case Constants.SYNC_VENDEDOR_URL:
                                            id = VENDEDOR_PROGRESS_ID;
                                            title = getString(R.string.sync_vendedor_title);
                                            text = getString(R.string.sync_vendedor_failed);
                                            break;
                                        case Constants.SYNC_CIRCUITOS_URL:
                                            id = CIRCUITO_PROGRESS_ID;
                                            title = getString(R.string.sync_circuito_title);
                                            text = getString(R.string.sync_circuito_failed);
                                            break;
                                        case Constants.SYNC_CLIENTE_URL:
                                            id = CLIENTE_PROGRESS_ID;
                                            title = getString(R.string.sync_clientes_title);
                                            text = getString(R.string.sync_clientes_failed);
                                            break;
                                        case Constants.SYNC_PRODUCTO_URL:
                                            id = PRODUCTO_PROGRESS_ID;
                                            title = getString(R.string.sync_productos_title);
                                            text = getString(R.string.sync_productos_failed);
                                            break;
                                        case Constants.SYNC_NO_COMPRA_URL:
                                            id = NO_COMPRA_PROGRESS_ID;
                                            title = getString(R.string.sync_no_compra_title);
                                            text = getString(R.string.sync_no_compra_failed);
                                            break;
                                        case Constants.SYNC_CIUDADES_URL:
                                            id = CIUDAD_PROGRESS_ID;
                                            title = getString(R.string.sync_ciudad_title);
                                            text = getString(R.string.sync_ciudad_failed);
                                            break;
                                        case Constants.SYNC_DEPARTAMENTOS_URL:
                                            id = DEPARTAMENTO_PROGRESS_ID;
                                            title = getString(R.string.sync_departamento_title);
                                            text = getString(R.string.sync_departamento_failed);
                                            break;
                                        default:
                                            title = getString(R.string.sync_error);
                                            text = message;
                                            break;
                                    }

                                    mBuilder.setContentTitle(title)
                                            .setContentText(text)
                                            .setSmallIcon(android.R.drawable.stat_notify_sync)
                                            .setProgress(0, 0, false);
                                    mNotificationManager.notify(id, mBuilder.build());
                                }
                            });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                momoJsonObjectRequest.setTag(SyncService.TAG_CLASS);
                momoJsonObjectRequest.setRetryPolicy(NetworkQueue.getDefaultRetryPolicy());

                Log.d(TAG_CLASS, "Queueing synchronization: " + url);
                Log.d(TAG_CLASS, "Request params: " + actionParams[i]);
                mMyQueue.addToRequestQueue(momoJsonObjectRequest, getApplicationContext());
            }
        }
    }

    private void handleActionByUrl(JSONObject response, String action) {

        String message = null;
        int status = -1;
        JSONObject item = null;
        JSONArray list = null;

        if (response == null) {
            Utils.getToast(getApplicationContext(), getString(R.string.volley_default_error));
            return;
        }

        Log.i(TAG_CLASS, " | Response: " + response.toString());

        try {
            if (response.has("status")) status = response.getInt("status");
            if (response.has("message")) message = response.getString("message");
            if (status != Constants.RESPONSE_OK) {
                Utils.getToast(getApplicationContext(), (message == null) ? getString(R.string.volley_default_error) : message);
                return;
            }

            if (response.has("item") && !response.isNull("item"))
                item = response.getJSONObject("item");

            if (response.has("list") && !response.isNull("list"))
                list = response.getJSONArray("list");

            switch (action) {
                case Constants.SYNC_VENDEDOR_URL:
                    onSyncResponse(response, VENDEDOR_PROGRESS_ID,
                            getString(R.string.sync_vendedor_title),
                            getString(R.string.sync_in_progress),
                            getString(R.string.sync_vendedor_notify_end),
                            getString(R.string.sync_vendedor_failed),
                            VendedorRepository.class,
                            Vendedores.class,
                            item,
                            null);
                    break;
                case Constants.SYNC_CIRCUITOS_URL:
                    onSyncResponse(response, CIRCUITO_PROGRESS_ID,
                            getString(R.string.sync_circuito_title),
                            getString(R.string.sync_in_progress),
                            getString(R.string.sync_circuito_notify_end),
                            getString(R.string.sync_circuito_failed),
                            CircuitoRepository.class,
                            Circuitos.class,
                            null,
                            list);
                    break;
                case Constants.SYNC_CLIENTE_URL:
                    onSyncResponseClient(response, CLIENTE_PROGRESS_ID,
                            getString(R.string.sync_clientes_title),
                            getString(R.string.sync_in_progress),
                            getString(R.string.sync_clientes_notify_end),
                            getString(R.string.sync_clientes_failed),
                            ClienteRepository.class,
                            ClientMask.class,
                            null,
                            list);
                    break;
                case Constants.SYNC_PRODUCTO_URL:
                    onSyncResponseProducts(response, PRODUCTO_PROGRESS_ID,
                            getString(R.string.sync_productos_title),
                            getString(R.string.sync_in_progress),
                            getString(R.string.sync_productos_notify_end),
                            getString(R.string.sync_productos_failed),
                            ProductoRepository.class,
                            Productos.class,
                            null,
                            list);
                    break;
                case Constants.SYNC_CIUDADES_URL:
                    onSyncResponse(response, CIUDAD_PROGRESS_ID,
                            getString(R.string.sync_ciudad_title),
                            getString(R.string.sync_in_progress),
                            getString(R.string.sync_ciudad_notify_end),
                            getString(R.string.sync_ciudad_failed),
                            CiudadRepository.class,
                            Ciudades.class,
                            null,
                            list);
                    break;
                case Constants.SYNC_DEPARTAMENTOS_URL:
                    onSyncResponse(response, DEPARTAMENTO_PROGRESS_ID,
                            getString(R.string.sync_departamento_title),
                            getString(R.string.sync_in_progress),
                            getString(R.string.sync_departamento_notify_end),
                            getString(R.string.sync_departamento_failed),
                            DepartamentoRepository.class,
                            Departamentos.class,
                            null,
                            list);
                    break;
                case Constants.SYNC_NO_COMPRA_URL:
                    onSyncResponse(response, NO_COMPRA_PROGRESS_ID,
                            getString(R.string.sync_no_compra_title),
                            getString(R.string.sync_in_progress),
                            getString(R.string.sync_no_compra_notify_end),
                            getString(R.string.sync_no_compra_failed),
                            NoCompraRepository.class,
                            NoCompra.class,
                            null,
                            list);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onSyncResponse(JSONObject response, int notId, String notTitle, String notInitText,
                                String notCompleteText, String notIncompleteText, Class<?> repository,
                                Class<?> itemClass, JSONObject item, JSONArray list) {

        try {


            Method clearAll = repository.getMethod("clearAll");
            Method store = repository.getMethod("store", itemClass);

            if (list != null && list.length() > 0) {

                clearAll.invoke(null, null);

                mBuilder.setContentTitle(notTitle)
                        .setContentText(notInitText)
                        .setSmallIcon(android.R.drawable.stat_notify_sync);

                int processed = 0;
                for (int i = 0; i < list.length(); i++) {
                    mBuilder.setProgress(list.length(), i, false);
                    mNotificationManager.notify(notId, mBuilder.build());
                    try {
                        JSONObject jsonObject = (JSONObject) list.get(i);
                        long rowId = (long) store.invoke(null, new Gson().fromJson(String.valueOf(jsonObject), itemClass));
                        if (rowId > 0) {
                            processed++;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (processed < list.length()) {
                    mBuilder.setContentText(notIncompleteText)
                            .setProgress(0, 0, false);
                } else {
                    mBuilder.setContentText(notCompleteText)
                            .setProgress(0, 0, false);
                }

                mNotificationManager.notify(notId, mBuilder.build());
            }

            if (item != null) {
                clearAll.invoke(null, (Object[]) null);
                mBuilder.setContentTitle(notTitle)
                        .setContentText(notInitText)
                        .setSmallIcon(android.R.drawable.stat_notify_sync);

                int processed = 0;
                mBuilder.setProgress(1, 0, false);
                mNotificationManager.notify(notId, mBuilder.build());
                try {
                    long rowId = (long) store.invoke(null, new Gson().fromJson(String.valueOf(item), itemClass));
                    if (rowId > 0) {
                        processed++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (processed < 1) {
                    mBuilder.setContentText(notIncompleteText)
                            .setProgress(0, 0, false);
                } else {
                    mBuilder.setContentText(notCompleteText)
                            .setProgress(0, 0, false);
                }
                mNotificationManager.notify(notId, mBuilder.build());
            }
        } catch (NoSuchMethodException | InvocationTargetException |
                IllegalAccessException e) {
            e.printStackTrace();
            Utils.getToast(getApplicationContext(), getString(R.string.volley_parse_error));
        }

    }


    private void onSyncResponseClient(JSONObject response, int notId, String notTitle, String notInitText,
                                      String notCompleteText, String notIncompleteText, Class<?> repository,
                                      Class<?> itemClass, JSONObject item, JSONArray list) {

        try {


            Method clearAll = repository.getMethod("clearAll");
            Method store = repository.getMethod("store", Clientes.class);

            if (list != null && list.length() > 0) {

                clearAll.invoke(null, null);

                mBuilder.setContentTitle(notTitle)
                        .setContentText(notInitText)
                        .setSmallIcon(android.R.drawable.stat_notify_sync);

                int processed = 0;
                for (int i = 0; i < list.length(); i++) {
                    mBuilder.setProgress(list.length(), i, false);
                    mNotificationManager.notify(notId, mBuilder.build());
                    try {
                        JSONObject jsonObject = (JSONObject) list.get(i);
                        ClientMask c = new Gson().fromJson(String.valueOf(jsonObject), ClientMask.class);
                        if (c != null) {
                            Clientes cl = new Clientes();
                            cl.setId(c.getId());
                            cl.setNombreTitular(c.getNombreTitular());
                            cl.setCedulaTitular(c.getCedulaTitular());
                            cl.setContactoTitular(c.getContactoTitular());
                            cl.setRazonSocial(c.getRazonSocial());
                            cl.setNombreNegocio(c.getNombreNegocio());
                            cl.setRuc(c.getRuc());
                            cl.setEmailNegocio(c.getEmailNegocio());
                            cl.setTelefonoNegocio(c.getTelefonoNegocio());
                            cl.setBarrio(c.getBarrio());
                            cl.setCallePrincipal(c.getCallePrincipal());
                            cl.setCalleSecundaria(c.getCalleSecundaria());
                            cl.setNumeroCasa(c.getNumeroCasa());
                            cl.setDireccion(c.getDireccion());
                            cl.setReferencia(c.getReferencia());
                            cl.setGeolocalizado(c.getGeolocalizado());
                            cl.setLatitud(c.getLatitud());
                            cl.setLongitud(c.getLongitud());
                            if (c.getTieneFoto()) {
                                cl.setFoto(c.getFoto().getBytes());
                            }
                            cl.setTieneFoto(c.getTieneFoto());
                            cl.setLunes(c.getLunes());
                            cl.setMartes(c.getMartes());
                            cl.setMiercoles(c.getMiercoles());
                            cl.setJueves(c.getJueves());
                            cl.setViernes(c.getViernes());
                            cl.setSabado(c.getSabado());
                            cl.setDomingo(c.getDomingo());
                            cl.setIdCircuito(Long.getLong(String.valueOf(c.getIdCircuito())));
                            cl.setIdCiudad(Long.getLong(String.valueOf(c.getIdCiudad())));
                            cl.setIdDepartamento(Long.getLong(String.valueOf(c.getIdDepartamento())));
                            cl.setHoraVisita(c.getHoraVisita());
                            cl.setVisitado(false);
                            long rowId = (long) store.invoke(null, cl);
                            if (rowId > 0) {
                                processed++;
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (processed < list.length()) {
                    mBuilder.setContentText(notIncompleteText)
                            .setProgress(0, 0, false);
                } else {
                    mBuilder.setContentText(notCompleteText)
                            .setProgress(0, 0, false);
                }

                mNotificationManager.notify(notId, mBuilder.build());
            }

            if (item != null) {
                clearAll.invoke(null, (Object[]) null);
                mBuilder.setContentTitle(notTitle)
                        .setContentText(notInitText)
                        .setSmallIcon(android.R.drawable.stat_notify_sync);

                int processed = 0;
                mBuilder.setProgress(1, 0, false);
                mNotificationManager.notify(notId, mBuilder.build());
                try {
                    long rowId = (long) store.invoke(null, new Gson().fromJson(String.valueOf(item), itemClass));
                    if (rowId > 0) {
                        processed++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (processed < 1) {
                    mBuilder.setContentText(notIncompleteText)
                            .setProgress(0, 0, false);
                } else {
                    mBuilder.setContentText(notCompleteText)
                            .setProgress(0, 0, false);
                }
                mNotificationManager.notify(notId, mBuilder.build());
               // Utils.callMarkersOptionLoader(getApplicationContext());
               // Utils.callClienteLoader(getApplicationContext());
            }
        } catch (NoSuchMethodException | InvocationTargetException |
                IllegalAccessException e) {
            e.printStackTrace();
            Utils.getToast(getApplicationContext(), getString(R.string.volley_parse_error));
        }

    }

    private void onSyncResponseProducts(JSONObject response, int notId, String notTitle, String notInitText,
                                        String notCompleteText, String notIncompleteText, Class<?> repository,
                                        Class<?> itemClass, JSONObject item, JSONArray list) {

        try {


            Method clearAll = repository.getMethod("clearAll");
            Method store = repository.getMethod("store", Productos.class);

            if (list != null && list.length() > 0) {

                clearAll.invoke(null, null);

                mBuilder.setContentTitle(notTitle)
                        .setContentText(notInitText)
                        .setSmallIcon(android.R.drawable.stat_notify_sync);

                int processed = 0;
                for (int i = 0; i < list.length(); i++) {
                    mBuilder.setProgress(list.length(), i, false);
                    mNotificationManager.notify(notId, mBuilder.build());
                    try {
                        JSONObject jsonObject = (JSONObject) list.get(i);
                        ProductMask p = new Gson().fromJson(String.valueOf(jsonObject), ProductMask.class);
                        if (p != null) {
                            Productos pr = new Productos();
                            pr.setId(p.getId());
                            pr.setCodigoBarra(p.getCodigoBarra());
                            pr.setDescripcion(p.getDescripcion());
                            pr.setPrecioVenta(p.getPrecioVenta());
                            pr.setCantidad(p.getCantidad());
                            pr.setTieneFoto(p.getTieneFoto());
                            pr.setFoto(p.getFoto());
                            long rowId = (long) store.invoke(null, pr);
                            if (rowId > 0) {
                                processed++;
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (processed < list.length()) {
                    mBuilder.setContentText(notIncompleteText)
                            .setProgress(0, 0, false);
                } else {
                    mBuilder.setContentText(notCompleteText)
                            .setProgress(0, 0, false);
                }

                mNotificationManager.notify(notId, mBuilder.build());
            }

            if (item != null) {
                clearAll.invoke(null, (Object[]) null);
                mBuilder.setContentTitle(notTitle)
                        .setContentText(notInitText)
                        .setSmallIcon(android.R.drawable.stat_notify_sync);

                int processed = 0;
                mBuilder.setProgress(1, 0, false);
                mNotificationManager.notify(notId, mBuilder.build());
                try {
                    long rowId = (long) store.invoke(null, new Gson().fromJson(String.valueOf(item), itemClass));
                    if (rowId > 0) {
                        processed++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (processed < 1) {
                    mBuilder.setContentText(notIncompleteText)
                            .setProgress(0, 0, false);
                } else {
                    mBuilder.setContentText(notCompleteText)
                            .setProgress(0, 0, false);
                }
                mNotificationManager.notify(notId, mBuilder.build());
            }
        } catch (NoSuchMethodException | InvocationTargetException |
                IllegalAccessException e) {
            e.printStackTrace();
            Utils.getToast(getApplicationContext(), getString(R.string.volley_parse_error));
        }

    }
}
