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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import fpuna.py.com.appolympos.R;
import fpuna.py.com.appolympos.adapters.NoSaleAdapter;
import fpuna.py.com.appolympos.entities.Transactions;
import fpuna.py.com.appolympos.loaders.NoSaleLoader;
import fpuna.py.com.appolympos.utiles.DividerItemDecoration;
import fpuna.py.com.appolympos.utiles.Utils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NoSaleFragment.OnNoSaleSelectedListener} interface
 * to handle interaction events.
 * Use the {@link NoSaleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoSaleFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Transactions>> {

    private OnNoSaleSelectedListener mListener;
    private RecyclerView mRecyclerView;
    private View rootView;
    private NoSaleAdapter mAdapter;

    public NoSaleFragment() {
        // Required empty public constructor
    }

    public static NoSaleFragment newInstance() {
        NoSaleFragment fragment = new NoSaleFragment();
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
        rootView = inflater.inflate(R.layout.fragment_no_sale, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView_no_sale);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new NoSaleAdapter(new ArrayList<Transactions>(), getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        /*mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mListener.onNoSaleSelectedListener(mAdapter.getItemAtPosition(position));
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
        if (context instanceof OnNoSaleSelectedListener) {
            mListener = (OnNoSaleSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNoSaleSelectedListener");
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
        return new NoSaleLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<List<Transactions>> loader, List<Transactions> data) {
        mAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Transactions>> data) {
        mAdapter.setData(new ArrayList<Transactions>());
    }

    public interface OnNoSaleSelectedListener {
        void onNoSaleSelectedListener(Transactions transactions);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = -1;
        try {
            position = mAdapter.getPosition();
        } catch (Exception e) {
            return super.onContextItemSelected(item);
        }
        Utils.getToast(getContext(), "SEEE FRAGMENT " + position);
        switch (item.getItemId()) {
            case 1:
                // do your stuff
                break;
            case 2:
                // do your stuff
                break;
            case 3:
                // do your stuff
                break;
        }
        return super.onContextItemSelected(item);
    }
}
