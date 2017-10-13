package fpuna.py.com.appolympos.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fpuna.py.com.appolympos.R;
import fpuna.py.com.appolympos.entities.Productos;
import fpuna.py.com.appolympos.repository.ProductoRepository;

/**
 * Created by Diego on 7/13/2017.
 */

public class SearchProductsAdapter extends RecyclerView.Adapter<SearchProductsAdapter.SearchProductsViewHolder> implements Filterable {

    private List<Productos> mProductList;
    private Context mContext;
    private SearchProductFilter mFilter;

    public SearchProductsAdapter(List<Productos> productsList, Context context) {
        mContext = context;
        mProductList = productsList;
    }


    public class SearchProductsViewHolder extends RecyclerView.ViewHolder {

        TextView description;
        TextView code;

        public SearchProductsViewHolder(View itemView) {
            super(itemView);
            description = (TextView) itemView.findViewById(R.id.txt_header);
            code = (TextView) itemView.findViewById(R.id.txt_content);
        }
    }


    @Override
    public SearchProductsAdapter.SearchProductsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_product, parent, false);
        return new SearchProductsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SearchProductsAdapter.SearchProductsViewHolder holder, int position) {
        holder.description.setText(mProductList.get(position).getDescripcion());
        holder.code.setText(mProductList.get(position).getCodigoBarra());
    }

    @Override
    public int getItemCount() {
        return mProductList.size();
    }

    public void setData(List<Productos> data) {
        mProductList = new ArrayList<>();
        mProductList.addAll(data);
        notifyDataSetChanged();
    }

    public Productos getItemAtPosition(int position) {
        return mProductList.get(position);
    }


    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new SearchProductFilter();
        }
        return mFilter;
    }

    public class SearchProductFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toUpperCase();

            FilterResults results = new FilterResults();
            final List<Productos> list = ProductoRepository.getAll();

            int count = list.size();
            final ArrayList<Productos> nlist = new ArrayList<Productos>(count);

            String filterableString = constraint.toString().toUpperCase();

            for (Productos p : list) {
                if (p.getDescripcion().toUpperCase().contains(filterString) || p.getCodigoBarra().toUpperCase().contains(filterableString)) {
                    nlist.add(p);
                }
            }
            results.values = nlist;
            results.count = nlist.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mProductList = (List<Productos>) results.values;
            notifyDataSetChanged();
        }
    }
}
