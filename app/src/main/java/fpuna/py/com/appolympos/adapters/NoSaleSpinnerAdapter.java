package fpuna.py.com.appolympos.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fpuna.py.com.appolympos.R;
import fpuna.py.com.appolympos.entities.NoCompra;

/**
 * Created by Diego on 7/2/2017.
 */

public class NoSaleSpinnerAdapter  extends ArrayAdapter<NoCompra> {

    private List<NoCompra> mReasonList = new ArrayList<>();
    private Context mContext;
    private int layoutResource;

    public NoSaleSpinnerAdapter(Context context, int resource, List<NoCompra> data) {
        super(context, resource);
        mReasonList = data;
        mContext = context;
        layoutResource = resource;
    }

    @Nullable
    @Override
    public NoCompra getItem(int position) {
        return mReasonList.get(position);
    }

    @Override
    public int getPosition(NoCompra item) {
        return mReasonList.indexOf(item);
    }

    @Override
    public int getCount() {
        return mReasonList.size();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public long getItemId(int position) {
        return mReasonList.get(position).getId();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(layoutResource, parent, false);
            // Configure the view holder
            viewHolder.mReasonDescription = (TextView) convertView.findViewById(R.id.item_reason_description);
            convertView.setTag(viewHolder);
        } else {
            // Fill data from the recycled view holder
            viewHolder = (ViewHolder) convertView.getTag();
        }
        NoCompra reasons = mReasonList.get(position);
        viewHolder.mReasonDescription.setText(reasons.getDescripcion().toUpperCase());
        return convertView;
    }


    private static class ViewHolder {
        TextView mReasonDescription;

    }
}
