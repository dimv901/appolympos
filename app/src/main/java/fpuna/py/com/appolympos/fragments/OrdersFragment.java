package fpuna.py.com.appolympos.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import fpuna.py.com.appolympos.R;
import fpuna.py.com.appolympos.adapters.OrdersAdapter;
import fpuna.py.com.appolympos.entities.Transactions;
import fpuna.py.com.appolympos.loaders.OrdersLoader;
import fpuna.py.com.appolympos.utiles.DividerItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OrdersFragment.OnOrderSelectedListener} interface
 * to handle interaction events.
 * Use the {@link OrdersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrdersFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Transactions>> {

    private OnOrderSelectedListener mListener;
    private RecyclerView mRecyclerView;
    private View rootView;
    private OrdersAdapter mAdapter;


    public OrdersFragment() {
        // Required empty public constructor
    }

    public static OrdersFragment newInstance() {
        OrdersFragment fragment = new OrdersFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_orders, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView_orders);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new OrdersAdapter(new ArrayList<Transactions>(), getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
       /* mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mListener.onOrderSelectedListener(mAdapter.getItemAtPosition(position));
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));*/
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnOrderSelectedListener) {
            mListener = (OnOrderSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnOrderSelectedListener");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(1, null, this).forceLoad();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<List<Transactions>> onCreateLoader(int id, Bundle args) {
        return new OrdersLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<List<Transactions>> loader, List<Transactions> data) {
        mAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Transactions>> loader) {
        mAdapter.setData(new ArrayList<Transactions>());
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnOrderSelectedListener {
        void onOrderSelectedListener(Transactions transactions);
    }
}
