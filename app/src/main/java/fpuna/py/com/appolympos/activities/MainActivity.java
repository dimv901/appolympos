package fpuna.py.com.appolympos.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Fade;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fpuna.py.com.appolympos.R;
import fpuna.py.com.appolympos.entities.Transactions;
import fpuna.py.com.appolympos.fragments.ClientsFragment;
import fpuna.py.com.appolympos.fragments.NoSaleFragment;
import fpuna.py.com.appolympos.fragments.OrdersFragment;
import fpuna.py.com.appolympos.fragments.RouteFragment;
import fpuna.py.com.appolympos.repository.TransactionRepository;
import fpuna.py.com.appolympos.services.SendTransactionService;
import fpuna.py.com.appolympos.services.SyncService;
import fpuna.py.com.appolympos.utiles.AppPreferences;
import fpuna.py.com.appolympos.utiles.Constants;
import fpuna.py.com.appolympos.utiles.NotificationSuscriber;
import fpuna.py.com.appolympos.utiles.Utils;
import fpuna.py.com.appolympos.utiles.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NoSaleFragment.OnNoSaleSelectedListener, OrdersFragment.OnOrderSelectedListener {

    private CoordinatorLayout mCoordinator;
    private View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupWindowAnimations();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCoordinator = (CoordinatorLayout) findViewById(R.id.main_coordinator_view);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(mViewPager);

        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        setupIconsTab(mTabLayout);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerView = LayoutInflater.from(this).inflate(R.layout.nav_header_main, navigationView, false);
        navigationView.addHeaderView(headerView);
        syncIfNeeded();
        //NotificationSuscriber.getInstance(getApplicationContext());
        new NotificationSuscriber(getApplicationContext());
        Utils.setSalesmanInfo(headerView, getApplicationContext());
    }

    private void setupWindowAnimations() {
        Fade fade = new Fade();
        fade.setDuration(1000);
        getWindow().setEnterTransition(fade);

        Slide slide = new Slide();
        slide.setDuration(1000);
        getWindow().setReturnTransition(slide);
    }

    private void syncIfNeeded() {
        long now = Utils.trimDate(new Date()).getTime();
        long lastSync = AppPreferences.getAppPreferences(getApplicationContext()).getLong(AppPreferences.KEY_PREFERENCE_LAST_SYNC, 0);
        if (lastSync < now) {
            startSync();
        }
    }

    private void startSync() {
        List<String> urls = new ArrayList<>();
        urls.add(Constants.SYNC_VENDEDOR_URL);
        urls.add(Constants.SYNC_CIRCUITOS_URL);
        urls.add(Constants.SYNC_CLIENTE_URL);
        urls.add(Constants.SYNC_PRODUCTO_URL);
        urls.add(Constants.SYNC_NO_COMPRA_URL);

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
        AppPreferences.getAppPreferences(getApplicationContext()).edit().putLong(AppPreferences.KEY_PREFERENCE_LAST_SYNC, new Date().getTime()).apply();
    }

    @Override
    protected void onResume() {
        Utils.setSalesmanInfo(headerView, getApplicationContext());
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_sync) {
            // Handle the camera action
            startActivity(new Intent(this, SyncActivity.class));
        } else if (id == R.id.nav_exit) {
            logout();
        } else if (id == R.id.nav_reset) {
            startActivity(new Intent(this, ChangePasswordActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(RouteFragment.newInstance());
        adapter.addFrag(ClientsFragment.newInstance());
        adapter.addFrag(OrdersFragment.newInstance());
        adapter.addFrag(NoSaleFragment.newInstance());
        viewPager.setAdapter(adapter);
    }

    private void setupIconsTab(TabLayout tabLayout) {
        final int[] icons = new int[]{
                R.mipmap.ic_map_white_24dp,
                R.mipmap.ic_account_box_white_24dp,
                R.mipmap.ic_shopping_cart_white_24dp,
                R.mipmap.ic_remove_shopping_cart_white_24dp,
        };

        tabLayout.getTabAt(0).setIcon(icons[0]);
        tabLayout.getTabAt(1).setIcon(icons[1]);
        tabLayout.getTabAt(2).setIcon(icons[2]);
        tabLayout.getTabAt(3).setIcon(icons[3]);
    }


    private void logout() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(R.string.dialog_exit_message).setTitle(R.string.dialog_confirm_title);
        builder.setIcon(R.mipmap.ic_info_black_24dp);
        builder.setPositiveButton(R.string.label_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Utils.endSession(getApplicationContext(), null);
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

    @Override
    public void onNoSaleSelectedListener(Transactions transactions) {
        showDialogOptions(transactions);
    }

    @Override
    public void onOrderSelectedListener(Transactions transactions) {
        showDialogOptions(transactions);
    }

    private void showDialogOptions(final Transactions t) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.label_choose_option)
                .setItems(R.array.transactions_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which) {
                            case 0:
                                info(t);
                                break;
                            case 1:
                                send(t);
                                break;
                            case 2:
                                delete(t);
                                break;
                        }

                    }
                }).setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void send(Transactions transaction) {
        String mUrl = "";
        if (transaction.getStatus() == Constants.TRANSACTION_SEND) {
            Utils.getSnackBar(mCoordinator, getString(R.string.error_already_send));
        } else {
            if (transaction.getType().equals(Constants.ORDER_TRANSACTION)) {
                mUrl = Constants.ORDER_URL;
            } else if (transaction.getType().equals(Constants.NO_SALE_TRANSACTION)) {
                mUrl = Constants.NO_SALE_URL;
            }
            SendTransactionService.startActionSend(getApplicationContext(), transaction.getHttpDetail(), mUrl, transaction.getId());
            Utils.getSnackBar(mCoordinator, getString(R.string.label_sending_transaction));
        }
    }

    private void info(Transactions t) {
        StringBuilder sb = new StringBuilder();
        String message = "";
        switch (t.getType()) {
            case Constants.ORDER_TRANSACTION:
                sb.append(getApplicationContext().getString(R.string.label_transaction_client)).append(" ").append(t.getClientName()).append("\n");
                sb.append(getApplicationContext().getString(R.string.label_transaction_type)).append(" ").append(getApplicationContext().getString(R.string.label_transaction_orden)).append("\n");
                sb.append(getApplicationContext().getString(R.string.label_transaction_date)).append(" ").append(Utils.formatDate(t.getCreatedAt(), Constants.DEFAULT_DATE_FORMAT)).append("\n");
                sb.append(getApplicationContext().getString(R.string.label_transaction_amount)).append(" ").append(Utils.formatNumber(String.valueOf(t.getAmount()))).append(" ").append("GS.").append("\n");
                break;
            case Constants.NO_SALE_TRANSACTION:
                sb.append(getApplicationContext().getString(R.string.label_transaction_client)).append(" ").append(t.getClientName()).append("\n");
                sb.append(getApplicationContext().getString(R.string.label_transaction_type)).append(" ").append(getApplicationContext().getString(R.string.transaction_type_no_sale)).append("\n");
                sb.append(getApplicationContext().getString(R.string.label_transaction_date)).append(" ").append(Utils.formatDate(t.getCreatedAt(), Constants.DEFAULT_DATE_FORMAT)).append("\n");
                break;
        }
        switch (t.getStatus()) {
            case Constants.TRANSACTION_ERROR:
                sb.append(getString(R.string.label_transaction_status)).append(" ").append(getString(R.string.label_error)).append("\n");
                sb.append(getString(R.string.label_transaction_message)).append(" ").append(t.getObservation()).append("\n");
                break;
            case Constants.TRANSACTION_SEND:
                sb.append(getString(R.string.label_transaction_status)).append(" ").append(getString(R.string.label_sending)).append("\n");
                sb.append(getString(R.string.label_transaction_date_send)).append(" ").append(Utils.formatDate(t.getUpdatedAt(), Constants.DEFAULT_DATETIME_FORMAT)).append("\n");
                sb.append(getString(R.string.label_transaction_message)).append(" ").append(t.getObservation()).append("\n");
                break;
            case Constants.TRANSACTION_PENDING:
                sb.append(getString(R.string.label_transaction_status)).append(" ").append(getString(R.string.label_pending)).append("\n");
                sb.append(getString(R.string.label_transaction_message)).append(" ").append(t.getObservation()).append("\n");
                break;
        }
        message = sb.toString();
        if (TextUtils.isEmpty(message)) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle(R.string.label_information);
        builder.setIcon(R.mipmap.ic_info_black_24dp);
        builder.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    private void delete(final Transactions t) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_delete_message).setTitle(R.string.dialog_confirm_title);
        builder.setIcon(R.mipmap.ic_info_black_24dp);
        builder.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setPositiveButton(R.string.label_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (t.getStatus().equals(Constants.TRANSACTION_SEND)) {
                    Utils.getSnackBar(mCoordinator, getString(R.string.error_transaction_send_delete));
                } else {
                    TransactionRepository.delete(t);
                    Utils.getSnackBar(mCoordinator, getString(R.string.message_delete_transaction));
                }

            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }
}
