package fpuna.py.com.appolympos.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fpuna.py.com.appolympos.R;
import fpuna.py.com.appolympos.adapters.ClientsAdapter;
import fpuna.py.com.appolympos.adapters.SearchProductsAdapter;
import fpuna.py.com.appolympos.adapters.TakeOrderAdapter;
import fpuna.py.com.appolympos.dialogs.ProgressDialogFragment;
import fpuna.py.com.appolympos.entities.Clientes;
import fpuna.py.com.appolympos.entities.Productos;
import fpuna.py.com.appolympos.entities.Transactions;
import fpuna.py.com.appolympos.entities.Vendedores;
import fpuna.py.com.appolympos.loaders.ProductsLoader;
import fpuna.py.com.appolympos.mask.OrderMask;
import fpuna.py.com.appolympos.network.MyRequest;
import fpuna.py.com.appolympos.network.NetworkQueue;
import fpuna.py.com.appolympos.repository.ClienteRepository;
import fpuna.py.com.appolympos.repository.ProductoRepository;
import fpuna.py.com.appolympos.repository.TransactionRepository;
import fpuna.py.com.appolympos.repository.VendedorRepository;
import fpuna.py.com.appolympos.utiles.AppLocationProvider;
import fpuna.py.com.appolympos.utiles.AppPreferences;
import fpuna.py.com.appolympos.utiles.Constants;
import fpuna.py.com.appolympos.utiles.RecyclerItemClickListener;
import fpuna.py.com.appolympos.utiles.Utils;

public class TakeOrderActivity extends AppCompatActivity implements TakeOrderAdapter.OnTotalOrderChangeListener, LoaderManager.LoaderCallbacks<List<Productos>> {
    public static final String TAG_CLASS = TakeOrderActivity.class.getName();

    private Clientes mCliente;
    private Vendedores mVendedor;
    private TakeOrderAdapter mOrderAdapter;
    private SearchProductsAdapter mSearchProductAdapter;
    private CoordinatorLayout mCoordinator;
    private OrderTask mOrderTask;

    private NetworkQueue mQueue;
    private AppLocationProvider mAppLocationProvider;

    private RecyclerView mRecyclerView;
    private TextInputEditText inputClientName;
    private TextInputEditText inputClientRUC;
    private TextView orderAmount;
    private FloatingActionButton fbAddProduct;
    private Button btAccept;
    private Button btCancel;

    private Transactions mTransaction;
    private long mTransactionId = 0;
    private Double orderTotalAmount = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCoordinator = (CoordinatorLayout) findViewById(R.id.take_order_coordinator);
        inputClientName = (TextInputEditText) findViewById(R.id.take_order_client_name);
        inputClientName.setFocusable(false);
        inputClientRUC = (TextInputEditText) findViewById(R.id.take_order_client_ruc);
        inputClientRUC.setFocusable(false);
        orderAmount = (TextView) findViewById(R.id.take_order_amount);


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_take_order_products);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mOrderAdapter = new TakeOrderAdapter(new ArrayList<OrderMask>(), this);
        mRecyclerView.setAdapter(mOrderAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showDeleteProductDialog(mSearchProductAdapter.getItemAtPosition(position), position);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && fbAddProduct.isShown()) {
                    fbAddProduct.hide();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fbAddProduct.show();
                }

                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        mSearchProductAdapter = new SearchProductsAdapter(new ArrayList<Productos>(), this);

        fbAddProduct = (FloatingActionButton) findViewById(R.id.fab_button_add);
        fbAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProductSearchDialog();
            }
        });

        btAccept = (Button) findViewById(R.id.take_order_button_ok);
        btAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });
        btCancel = (Button) findViewById(R.id.take_order_button_cancel);
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOrderAdapter.getItemCount() > 0) {
                    showExitDialog();
                } else {
                    finish();
                }
            }
        });
        setData();
        getClienteData();
        getVendedorData();

        mQueue = new NetworkQueue(getApplicationContext());
        mAppLocationProvider = new AppLocationProvider(this);
    }


    private void showDeleteProductDialog(final Productos p, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TakeOrderActivity.this);
        builder.setMessage(R.string.dialog_delete_product_message).setTitle(R.string.dialog_info_title);
        builder.setIcon(R.mipmap.ic_info_black_24dp);
        builder.setPositiveButton(R.string.label_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mOrderAdapter.remove(position);
                Utils.getSnackBar(mCoordinator, getString(R.string.message_delete_product));
            }
        });
        builder.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }


    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TakeOrderActivity.this);
        builder.setMessage(R.string.dialog_exit_order).setTitle(R.string.dialog_info_title);
        builder.setIcon(R.mipmap.ic_info_black_24dp);
        builder.setPositiveButton(R.string.label_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    private void showProductSearchDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(TakeOrderActivity.this);
        View v = LayoutInflater.from(TakeOrderActivity.this).inflate(R.layout.search_product_dialog, null);
        SearchView inputSearch = (SearchView) v.findViewById(R.id.search_product_input);
        RecyclerView recyclerViewProducts = (RecyclerView) v.findViewById(R.id.recycler_search_product);
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerViewProducts.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        recyclerViewProducts.setItemAnimator(new DefaultItemAnimator());
        recyclerViewProducts.setHasFixedSize(true);
        recyclerViewProducts.setAdapter(mSearchProductAdapter);

        inputSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mSearchProductAdapter.getFilter().filter(newText);
                return false;
            }
        });
        builder.setView(v);
        builder.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        final Dialog mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        recyclerViewProducts.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mDialog.dismiss();
                showProductQuantityDialog(mSearchProductAdapter.getItemAtPosition(position));
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
    }

    private void showProductQuantityDialog(final Productos p) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(TakeOrderActivity.this);
        View v = LayoutInflater.from(TakeOrderActivity.this).inflate(R.layout.product_quantity_dialog, null);
        ImageView imageProduct = (ImageView) v.findViewById(R.id.product_quantity_image);
        TextView mTextProductDescription = (TextView) v.findViewById(R.id.product_quantity_description);
        mTextProductDescription.setText(new StringBuilder().append("PRODUCTO:").append(" ").append(p.getDescripcion()).toString().toUpperCase());
        TextView mTextProductCode = (TextView) v.findViewById(R.id.product_quantity_code);
        mTextProductCode.setText(new StringBuilder().append("CODIGO:").append(" ").append(p.getCodigoBarra()).toString().toUpperCase());
        TextView mTextProductStock = (TextView) v.findViewById(R.id.product_quantity_stock);
        final TextInputEditText mInputQuantity = (TextInputEditText) v.findViewById(R.id.product_input_quantity);
        Utils.addTextChangeListener(mInputQuantity);
        if (p.getCantidad() == 0) {
            mTextProductStock.setText(new StringBuilder().append("El producto ya no cuenta con stock, por favor sincronice sus Productos").toString().toUpperCase());
            mTextProductStock.setTextColor(Color.RED);
            mInputQuantity.setVisibility(View.GONE);
        } else {
            mTextProductStock.setText(new StringBuilder().append("STOCK:").append(" ").append(p.getCantidad()).toString().toUpperCase());
        }

        if (p.getTieneFoto()) {
            try {
                byte[] imgbytes = Base64.decode(p.getFoto(), Base64.DEFAULT);
                imageProduct.setImageBitmap(BitmapFactory.decodeByteArray(imgbytes, 0, imgbytes.length));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        builder.setView(v);
        builder.setPositiveButton(getString(R.string.label_accept), null);
        builder.setNegativeButton(getString(R.string.label_cancel), null);
        builder.setView(v);
        final AlertDialog mDialog = builder.create();
        mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button accept = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (p.getCantidad() > 0) {
                            mInputQuantity.setError(null);
                            String mQuantity = mInputQuantity.getText().toString().trim().replace(".", "");
                            if (TextUtils.isEmpty(mQuantity)) {
                                mInputQuantity.setError(getString(R.string.error_field_required));
                                return;
                            }

                            Double q = Double.parseDouble(mQuantity);
                            if (q <= 0.0) {
                                mInputQuantity.setError(getString(R.string.error_invalid_quantity));
                                return;
                            }
                            mDialog.dismiss();
                            addProduct(p, Double.parseDouble(mQuantity));
                        } else {
                            mDialog.dismiss();
                        }
                    }
                });
                Button negative = mDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });
            }
        });
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
    }

    private void addProduct(Productos p, Double quantity) {
        OrderMask item = new OrderMask();
        item.setIdProducto(p.getId());
        item.setCantidad(quantity);
        item.setPrecio(p.getPrecioVenta());
        item.setImporte(quantity * p.getPrecioVenta());
        mOrderAdapter.addItem(item);
    }

    private void setData() {
        long productCount = ProductoRepository.count();
        if (productCount > 0) {
            getSupportLoaderManager().initLoader(1, null, this).forceLoad();
        } else {
            Utils.getToast(this, getString(R.string.error_need_sync_products));
            startActivity(new Intent(getApplicationContext(), SyncActivity.class));
            finish();
            return;
        }
    }

    private void getClienteData() {
        if (!getIntent().hasExtra(ClientsAdapter.CLIENT_ID)) {
            Utils.getToast(getApplicationContext(), getString(R.string.error_client_id_not_found));
            finish();
            return;
        }

        if (getIntent().getExtras().containsKey(ClientsAdapter.CLIENT_ID)) {
            long clientId = getIntent().getExtras().getLong(ClientsAdapter.CLIENT_ID);
            mCliente = ClienteRepository.getById(clientId);
            if (mCliente == null) {
                Utils.getToast(getApplicationContext(), getString(R.string.error_client_id_not_found));
                finish();
                return;
            }
            inputClientName.setText(mCliente.getNombreNegocio());
            inputClientRUC.setText(mCliente.getRuc());
        } else {
            Utils.getToast(getApplicationContext(), getString(R.string.error_client_id_not_found));
            finish();
            return;
        }
    }

    private void getVendedorData() {
        String mUserName = AppPreferences.getAppPreferences(this).getString(AppPreferences.KEY_PREFERENCE_USUARIO, null);
        mVendedor = VendedorRepository.getSalesmanByUserName(mUserName);
        if (mVendedor == null) {
            Utils.getToast(this, getString(R.string.error_need_sync_salesman));
            startActivity(new Intent(getApplicationContext(), SyncActivity.class));
            finish();
            return;
        }
    }

    @Override
    public void onTotalOrderChangeListener(Double total) {
        setOrderTotalAmount(total);
        int currentTotal = getOrderTotalAmount().intValue();
        orderAmount.setText(Utils.formatNumber(String.valueOf(currentTotal)));
    }

    @Override
    public Loader<List<Productos>> onCreateLoader(int id, Bundle args) {
        return new ProductsLoader(getApplicationContext());
    }

    @Override
    public void onLoadFinished(Loader<List<Productos>> loader, List<Productos> data) {
        mSearchProductAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Productos>> loader) {
        mSearchProductAdapter.setData(new ArrayList<Productos>());
    }

    private void validate() {
        if (mOrderAdapter.getItemCount() == 0) {
            Utils.getSnackBar(mCoordinator, getString(R.string.error_empty_product_list));
            return;
        }
        double latitude = 0;
        double longitude = 0;
        Location mLocation = mAppLocationProvider.getLocation();
        if (mLocation != null) {
            latitude = mLocation.getLatitude();
            longitude = mLocation.getLongitude();
        }
        String salesmanId = String.valueOf(mVendedor.getId());
        String clientId = String.valueOf(mCliente.getId());
        String clientDesc = String.valueOf(mCliente.getNombreNegocio());
        List<OrderMask> items = mOrderAdapter.getAllItems();
        String amount = String.valueOf(getOrderTotalAmount().intValue());
        mOrderTask = new OrderTask(clientId, clientDesc, salesmanId, amount, items, String.valueOf(latitude), String.valueOf(longitude));
        mOrderTask.confirm();
    }

    private class OrderTask extends MyRequest {
        public static final String REQUEST_TAG = "OrderTask";
        private String mClientId;
        private String mSalesmanId;
        private String mAmount;
        private List<OrderMask> mProducts;
        private String mLatitude;
        private String mLongitude;
        private String mClientDesc;

        public OrderTask(String mClientId, String mClientDesc, String mSalesmanId, String mAmount, List<OrderMask> mProducts, String mLatitude, String mLongitude) {
            this.mClientId = mClientId;
            this.mSalesmanId = mSalesmanId;
            this.mAmount = mAmount;
            this.mProducts = mProducts;
            this.mLatitude = mLatitude;
            this.mLongitude = mLongitude;
            this.mClientDesc = mClientDesc;
        }

        @Override
        protected void confirm() {
            final AlertDialog.Builder builder = new AlertDialog.Builder(TakeOrderActivity.this);
            builder.setIcon(R.mipmap.ic_info_black_24dp);
            builder.setTitle(R.string.dialog_confirmation_title);
            builder.setMessage(R.string.dialog_confirmation_message);
            builder.setPositiveButton(R.string.label_accept, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    execute();
                }
            });

            builder.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mOrderTask = null;
                }
            });

            AlertDialog confirmDialog = builder.create();
            confirmDialog.setCanceledOnTouchOutside(false);
            confirmDialog.show();
        }

        @Override
        protected void execute() {
            mProgressDialog = ProgressDialogFragment.newInstance(getApplicationContext());
            mProgressDialog.show(getSupportFragmentManager(), ProgressDialogFragment.TAG_CLASS);

            if (mJsonObjectRequest != null)
                mQueue.getRequestQueue(getApplicationContext()).cancelAll(TakeOrderActivity.OrderTask.REQUEST_TAG);


            Gson mGsonObject = new Gson();
            if (mTransaction == null) {
                mTransaction = new Transactions();
                mTransaction.setCreatedAt(Utils.trimDate(new Date()));
                mTransaction.setClientId(mClientId);
                mTransaction.setClientName(mClientDesc);
                mTransaction.setAmount(Integer.parseInt(mAmount));
                mTransaction.setType(Constants.ORDER_TRANSACTION);
                mTransaction.setStatus(Constants.TRANSACTION_PENDING);
                mTransaction.setObservation(getApplication().getString(R.string.message_pending_transaction));
                mTransaction.setHttpDetail(String.valueOf(getParams()));
                mTransactionId = TransactionRepository.store(mTransaction);
                if (mTransactionId <= 0) {
                    Log.e(TAG_CLASS, "Error storing transaction data: " + mGsonObject.toJson(getParams()));
                    Utils.getSnackBar(mCoordinator, getString(R.string.error_save_transaction));
                    mProgressDialog.dismiss();
                    return;
                }
            } else {
                Log.v(TAG_CLASS, "Transaction exist, just updating on database");
                mTransaction.setCreatedAt(Utils.trimDate(new Date()));
                mTransaction.setClientId(mClientId);
                mTransaction.setClientName(mClientDesc);
                mTransaction.setAmount(Integer.parseInt(mAmount));
                mTransaction.setType(Constants.ORDER_TRANSACTION);
                mTransaction.setStatus(Constants.TRANSACTION_PENDING);
                mTransaction.setObservation(getApplication().getString(R.string.message_pending_transaction));
                mTransaction.setHttpDetail(String.valueOf(getParams()));
                TransactionRepository.store(mTransaction);
            }

            mCliente.setVisitado(true);
            ClienteRepository.store(mCliente);

            mJsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    Constants.ORDER_URL,
                    getParams(),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            mProgressDialog.dismiss();
                            handleResponse(response);
                            mJsonObjectRequest.cancel();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mProgressDialog.dismiss();
                            String message = NetworkQueue.handleError(error, getApplicationContext());
                            Utils.getSnackBar(mCoordinator, message);
                        }
                    });
            mJsonObjectRequest.setTag(TakeOrderActivity.OrderTask.REQUEST_TAG);
            mQueue.addToRequestQueue(mJsonObjectRequest, getApplicationContext());
        }

        @Override
        protected JSONObject getParams() {
            mJsonParams = new JSONObject();
            JSONArray array = new JSONArray();

            try {
                mJsonParams.put("idCliente", mClientId);
                mJsonParams.put("idVendedor", mSalesmanId);
                mJsonParams.put("importe", mAmount);
                mJsonParams.put("latitud", mLatitude);
                mJsonParams.put("longitud", mLongitude);

                for (OrderMask item : mProducts) {
                    JSONObject p = new JSONObject();
                    p.put("idProducto", item.getIdProducto());
                    p.put("cantidad", item.getCantidad());
                    p.put("precio", item.getPrecio());
                    p.put("importe", item.getImporte());
                    array.put(p);
                }
                mJsonParams.put("detalle", array);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return mJsonParams;
        }


        @Override
        protected void handleResponse(JSONObject response) {
            String message = null;
            int status = -1;

            if (response == null) {
                Utils.updateTransaction(mTransaction, Constants.TRANSACTION_ERROR, getString(R.string.volley_default_error));
                Utils.getSnackBar(mCoordinator, getString(R.string.volley_default_error));
                return;
            }

            Log.i(TAG_CLASS, REQUEST_TAG + " | Response: " + response.toString());

            try {
                if (response.has("status")) status = response.getInt("status");
                if (response.has("message")) message = response.getString("message");

                if (status != Constants.RESPONSE_OK) {
                    Utils.updateTransaction(mTransaction, Constants.TRANSACTION_ERROR, (message == null) ? getString(R.string.volley_default_error) : message);
                    Utils.getSnackBar(mCoordinator, (message == null) ? getString(R.string.volley_default_error) : message);
                    return;
                }
                Utils.updateTransaction(mTransaction, Constants.TRANSACTION_SEND, message);
                Utils.callMarkersOptionLoader(getApplicationContext());
                successDialog(message);
            } catch (Exception e) {
                e.printStackTrace();
                Utils.getSnackBar(mCoordinator, getString(R.string.volley_default_error));
            }
        }
    }

    private void successDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TakeOrderActivity.this);
        builder.setIcon(R.mipmap.ic_done_black_24dp);
        builder.setTitle(R.string.dialog_info_title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.label_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                Utils.callMarkersOptionLoader(getApplicationContext());
            }
        });
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    public Double getOrderTotalAmount() {
        return orderTotalAmount;
    }

    public void setOrderTotalAmount(Double orderTotalAmount) {
        this.orderTotalAmount = orderTotalAmount;
    }
}
