package fpuna.py.com.appolympos.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fpuna.py.com.appolympos.R;
import fpuna.py.com.appolympos.entities.Transactions;
import fpuna.py.com.appolympos.repository.TransactionRepository;
import fpuna.py.com.appolympos.services.SendTransactionService;
import fpuna.py.com.appolympos.utiles.Constants;
import fpuna.py.com.appolympos.utiles.Utils;

/**
 * Created by Diego on 7/9/2017.
 */

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrdersAdapterViewHolder> {
    private List<Transactions> mOrderTransactionsList = new ArrayList<>();
    private Context mContext;

    public OrdersAdapter(List<Transactions> orderTransactionsList, Context context) {
        mOrderTransactionsList = orderTransactionsList;
        mContext = context;
    }

    public class OrdersAdapterViewHolder extends RecyclerView.ViewHolder {
        // ImageView mOrderStatus;
        ImageView mOrderMenu;
        TextView mOrderClient;
        TextView mOrderDate;
        TextView mOrderAmount;
        TextView mOrderStatus;

        public OrdersAdapterViewHolder(View itemView) {
            super(itemView);
            //   mOrderStatus = (ImageView) itemView.findViewById(R.id.item_order_icon_status);
            mOrderClient = (TextView) itemView.findViewById(R.id.item_order_client);
            mOrderDate = (TextView) itemView.findViewById(R.id.item_order_date);
            mOrderAmount = (TextView) itemView.findViewById(R.id.item_order_amount);
            mOrderMenu = (ImageView) itemView.findViewById(R.id.item_order_menu);
            mOrderStatus = (TextView) itemView.findViewById(R.id.item_order_status);
        }
    }

    @Override
    public OrdersAdapter.OrdersAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrdersAdapter.OrdersAdapterViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final OrdersAdapter.OrdersAdapterViewHolder holder, final int position) {
        Transactions order = mOrderTransactionsList.get(position);
        holder.mOrderClient.setText(order.getClientName().toUpperCase());
        holder.mOrderDate.setText(Utils.formatDate(order.getCreatedAt(), Constants.DEFAULT_DATE_FORMAT));
        holder.mOrderAmount.setText(new StringBuilder().append(Utils.formatNumber(String.valueOf(order.getAmount()))).append(" Gs.").toString());
        switch (order.getStatus()) {
            case Constants.TRANSACTION_SEND:
                holder.mOrderStatus.setText("ENVIADO");
                holder.mOrderStatus.setTextColor(mContext.getResources().getColor(R.color.colorSuccess));
                break;
            case Constants.TRANSACTION_PENDING:
                holder.mOrderStatus.setText("PENDIENTE");
                holder.mOrderStatus.setTextColor(mContext.getResources().getColor(R.color.colorPending));
                break;
            case Constants.TRANSACTION_ERROR:
                holder.mOrderStatus.setText("ERROR");
                holder.mOrderStatus.setTextColor(mContext.getResources().getColor(R.color.colorError));
                break;
        }

        holder.mOrderMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(mContext, holder.mOrderMenu);
                //inflating menu from xml resource
                popup.inflate(R.menu.main);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_send:
                                //handle menu1 click
                                send(getItemAtPosition(position));
                                break;
                            case R.id.action_info:
                                //handle menu2 click
                                info(getItemAtPosition(position));
                                break;
                            case R.id.action_delete:
                                //handle menu3 click
                                delete(getItemAtPosition(position));
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
            }
        });
    }

    private void info(Transactions t) {
        StringBuilder sb = new StringBuilder();
        String message = "";
        switch (t.getType()) {
            case Constants.ORDER_TRANSACTION:
                sb.append(mContext.getString(R.string.label_transaction_client)).append(" ").append(t.getClientName()).append("\n");
                sb.append(mContext.getString(R.string.label_transaction_type)).append(" ").append(mContext.getString(R.string.label_transaction_orden)).append("\n");
                sb.append(mContext.getString(R.string.label_transaction_date)).append(" ").append(Utils.formatDate(t.getCreatedAt(), Constants.DEFAULT_DATE_FORMAT)).append("\n");
                sb.append(mContext.getString(R.string.label_transaction_amount)).append(" ").append(Utils.formatNumber(String.valueOf(t.getAmount()))).append(" ").append("GS.").append("\n");
                break;
            case Constants.NO_SALE_TRANSACTION:
                sb.append(mContext.getString(R.string.label_transaction_client)).append(" ").append(t.getClientName()).append("\n");
                sb.append(mContext.getString(R.string.label_transaction_type)).append(" ").append(mContext.getString(R.string.transaction_type_no_sale)).append("\n");
                sb.append(mContext.getString(R.string.label_transaction_date)).append(" ").append(Utils.formatDate(t.getCreatedAt(), Constants.DEFAULT_DATE_FORMAT)).append("\n");
                break;
        }
        switch (t.getStatus()) {
            case Constants.TRANSACTION_ERROR:
                sb.append(mContext.getString(R.string.label_transaction_status)).append(" ").append(mContext.getString(R.string.label_error)).append("\n");
                sb.append(mContext.getString(R.string.label_transaction_message)).append(" ").append(t.getObservation()).append("\n");
                break;
            case Constants.TRANSACTION_SEND:
                sb.append(mContext.getString(R.string.label_transaction_status)).append(" ").append(mContext.getString(R.string.label_sending)).append("\n");
                sb.append(mContext.getString(R.string.label_transaction_date_send)).append(" ").append(Utils.formatDate(t.getUpdatedAt(), Constants.DEFAULT_DATETIME_FORMAT)).append("\n");
                sb.append(mContext.getString(R.string.label_transaction_message)).append(" ").append(t.getObservation()).append("\n");
                break;
            case Constants.TRANSACTION_PENDING:
                sb.append(mContext.getString(R.string.label_transaction_status)).append(" ").append(mContext.getString(R.string.label_pending)).append("\n");
                sb.append(mContext.getString(R.string.label_transaction_message)).append(" ").append(t.getObservation()).append("\n");
                break;
        }
        message = sb.toString();
        if (TextUtils.isEmpty(message)) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
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
                    Utils.getToast(mContext, mContext.getString(R.string.error_transaction_send_delete));
                } else {
                    TransactionRepository.delete(t);
                    Utils.getToast(mContext, mContext.getString(R.string.message_delete_transaction));
                }

            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    private void send(Transactions transaction) {
        String mUrl = "";
        if (transaction.getStatus() == Constants.TRANSACTION_SEND) {
            Utils.getToast(mContext, mContext.getString(R.string.error_already_send));
        } else {
            if (transaction.getType().equals(Constants.ORDER_TRANSACTION)) {
                mUrl = Constants.ORDER_URL;
            } else if (transaction.getType().equals(Constants.NO_SALE_TRANSACTION)) {
                mUrl = Constants.NO_SALE_URL;
            }
            SendTransactionService.startActionSend(mContext, transaction.getHttpDetail(), mUrl, transaction.getId());
            Utils.getToast(mContext, mContext.getString(R.string.label_sending_transaction));
        }
    }


    @Override
    public int getItemCount() {
        return mOrderTransactionsList.size();
    }

    public void setData(List<Transactions> items) {
        mOrderTransactionsList = new ArrayList<>();
        mOrderTransactionsList.addAll(items);
        notifyDataSetChanged();
    }

    public Transactions getItemAtPosition(int position) {
        return mOrderTransactionsList.get(position);
    }
}
