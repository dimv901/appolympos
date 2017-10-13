package fpuna.py.com.appolympos.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fpuna.py.com.appolympos.R;
import fpuna.py.com.appolympos.services.SyncService;
import fpuna.py.com.appolympos.utiles.AppPreferences;
import fpuna.py.com.appolympos.utiles.Constants;
import fpuna.py.com.appolympos.utiles.Utils;

public class SyncActivity extends AppCompatActivity {

    private AppCompatCheckBox mCheckBoxVendedor;
    private AppCompatCheckBox mCheckBoxCircuito;
    private AppCompatCheckBox mCheckBoxCliente;
    private AppCompatCheckBox mCheckBoxProducto;
    private AppCompatCheckBox mCheckBoxNoCompra;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mCheckBoxVendedor = (AppCompatCheckBox) findViewById(R.id.checkBoxVendedor);
        mCheckBoxCircuito = (AppCompatCheckBox) findViewById(R.id.checkBoxCircuito);
        mCheckBoxCliente = (AppCompatCheckBox) findViewById(R.id.checkBoxClientes);
        mCheckBoxProducto = (AppCompatCheckBox) findViewById(R.id.checkBoxProductos);
        mCheckBoxNoCompra = (AppCompatCheckBox) findViewById(R.id.checkBoxNoCompra);

        AppCompatButton mButtonPositive = (AppCompatButton) findViewById(R.id.sync_button_ok);
        AppCompatButton mButtonNegative = (AppCompatButton) findViewById(R.id.sync_button_cancel);

        mButtonPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSync();
            }
        });

        mButtonNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void startSync() {

        List<String> urls = new ArrayList<>();

        if (mCheckBoxVendedor.isChecked())
            urls.add(Constants.SYNC_VENDEDOR_URL);
        if (mCheckBoxCircuito.isChecked())
            urls.add(Constants.SYNC_CIRCUITOS_URL);
        if (mCheckBoxCliente.isChecked())
            urls.add(Constants.SYNC_CLIENTE_URL);
        if (mCheckBoxProducto.isChecked())
            urls.add(Constants.SYNC_PRODUCTO_URL);
        if (mCheckBoxNoCompra.isChecked())
            urls.add(Constants.SYNC_NO_COMPRA_URL);

        if (urls.size() <= 0) {
            Utils.getToast(this, getString(R.string.error_empty_selection_sync));
            return;
        }

        String[] urlActions = new String[urls.size()];
        urlActions = urls.toArray(urlActions);

        final JSONObject params = new JSONObject();
        try {
            params.put(SyncService.JSON_PARAM_USUARIO, AppPreferences.getAppPreferences(getApplicationContext()).getString(AppPreferences.KEY_PREFERENCE_USUARIO, null));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String[] paramsList = new String[urlActions.length];
        for (int i = 0; i < urlActions.length; i++) {
            paramsList[i] = params.toString();
        }

        Utils.getToast(this, getString(R.string.sync_in_progress));
        SyncService.startActionSync(this, urlActions, paramsList);
        finish();
    }

}
