package fpuna.py.com.appolympos.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fpuna.py.com.appolympos.R;
import fpuna.py.com.appolympos.entities.Productos;
import fpuna.py.com.appolympos.mask.OrderMask;
import fpuna.py.com.appolympos.repository.ProductoRepository;
import fpuna.py.com.appolympos.utiles.Utils;

/**
 * Created by Diego on 7/11/2017.
 */

public class TakeOrderAdapter extends RecyclerView.Adapter<TakeOrderAdapter.TakeOrderViewHolder> {

    private List<OrderMask> mOrderMaskList = new ArrayList<>();
    private Context mContext;
    private OnTotalOrderChangeListener mListener;

    public TakeOrderAdapter(List<OrderMask> orderMaskList, Context context) {
        mOrderMaskList = orderMaskList;
        mContext = context;
        if (context instanceof OnTotalOrderChangeListener) {
            mListener = (OnTotalOrderChangeListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTotalOrderChangeListener");
        }
        estimateTotal();
    }

    public class TakeOrderViewHolder extends RecyclerView.ViewHolder {
        private ImageView mProductIcon;
        private TextView mProductDescription;
        private TextView mPrice;
        private TextView mQuantity;
        private TextView mAmount;

        public TakeOrderViewHolder(View itemView) {
            super(itemView);
            mProductIcon = (ImageView) itemView.findViewById(R.id.item_product_order_icon);
            mProductDescription = (TextView) itemView.findViewById(R.id.item_product_order_description);
            mQuantity = (TextView) itemView.findViewById(R.id.item_product_quantity);
            mPrice = (TextView) itemView.findViewById(R.id.item_product_price);
            mAmount = (TextView) itemView.findViewById(R.id.item_product_amount);
        }
    }


    @Override
    public TakeOrderAdapter.TakeOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_order, parent, false);
        return new TakeOrderAdapter.TakeOrderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TakeOrderAdapter.TakeOrderViewHolder holder, int position) {
        StringBuilder sb = new StringBuilder();
        OrderMask om = mOrderMaskList.get(position);
        Productos p = ProductoRepository.getById(om.getIdProducto());
        if (p != null) {
            holder.mProductDescription.setText(p.getDescripcion().toUpperCase());
            if (p.getTieneFoto()) {
                try {
                    byte[] imgbytes = Base64.decode(p.getFoto(), Base64.DEFAULT);
                    holder.mProductIcon.setImageBitmap(BitmapFactory.decodeByteArray(imgbytes, 0, imgbytes.length));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            holder.mQuantity.setText(sb.append("Cantidad:").append(" ").append(om.getCantidad()).toString());
            int price = p.getPrecioVenta().intValue();
            sb = new StringBuilder();
            holder.mPrice.setText(sb.append("Precio:").append(" ").append(Utils.formatNumber(String.valueOf(price))).append(" Gs.").toString());
            sb = new StringBuilder();
            int amount = om.getImporte().intValue();
            holder.mAmount.setText(sb.append("Importe:").append(" ").append(Utils.formatNumber(String.valueOf(amount))).append(" Gs.").toString());
        }

    }

    @Override
    public int getItemCount() {
        return mOrderMaskList.size();
    }

    public synchronized void removeItem(Productos productos) {
        for (OrderMask m : mOrderMaskList) {
            if (m.getIdProducto() == productos.getId()) {
                mOrderMaskList.remove(m);
            }
        }
        estimateTotal();
        notifyDataSetChanged();
    }

    public void remove(int position) {
        mOrderMaskList.remove(position);
        estimateTotal();
        notifyDataSetChanged();
    }


    public void addItem(OrderMask item) {
        if (alreadyExist(item)) {
            Productos p = ProductoRepository.getById(item.getIdProducto());
            showAlreadyExistsProductMessage(p);
        } else {
            mOrderMaskList.add(item);
            estimateTotal();
            notifyDataSetChanged();
        }
    }

    private void estimateTotal() {
        double total = 0;
        for (OrderMask om : mOrderMaskList) {
            total = total + om.getImporte();
        }
        mListener.onTotalOrderChangeListener(total);
    }

    public List<OrderMask> getAllItems() {
        return mOrderMaskList;
    }

    public boolean alreadyExist(OrderMask item) {
        for (OrderMask om : mOrderMaskList) {
            if (om.getIdProducto() == item.getIdProducto())
                return true;
        }
        return false;
    }

    private void showAlreadyExistsProductMessage(Productos item) {
        StringBuilder sb = new StringBuilder();
        sb.append("El producto")
                .append(" ")
                .append(item.getDescripcion().toUpperCase())
                .append(" ")
                .append("ya existe en la lista, por favor eliminelo y vuelva a agregarlo.")
                .append(" ")
                .append("\n");

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setIcon(R.mipmap.ic_info_black_24dp);
        builder.setTitle(R.string.dialog_info_title);
        builder.setMessage(sb.toString());
        builder.setPositiveButton(R.string.label_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    public interface OnTotalOrderChangeListener {
        void onTotalOrderChangeListener(Double total);
    }
}
