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
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import fpuna.py.com.appolympos.R;
import fpuna.py.com.appolympos.adapters.ClientsAdapter;
import fpuna.py.com.appolympos.entities.Clientes;
import fpuna.py.com.appolympos.loaders.ClientsLoader;
import fpuna.py.com.appolympos.utiles.DividerItemDecoration;


public class ClientsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Clientes>> {

    private RecyclerView mRecyclerView;
    private SearchView mSearchview;
    private ClientsAdapter mAdapter;
    private View rootView;


    public ClientsFragment() {

    }

    public static ClientsFragment newInstance() {
        ClientsFragment fragment = new ClientsFragment();
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
        rootView = inflater.inflate(R.layout.fragment_clients, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView_cliente);
        mSearchview = (SearchView) rootView.findViewById(R.id.search_client);
        mSearchview.setFocusable(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new ClientsAdapter(new ArrayList<Clientes>(), getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        mSearchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(1, null, this).forceLoad();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public Loader<List<Clientes>> onCreateLoader(int id, Bundle args) {
        return new ClientsLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<List<Clientes>> loader, List<Clientes> data) {
        mAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Clientes>> loader) {
        mAdapter.setData(new ArrayList<Clientes>());
    }
}
