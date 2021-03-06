package pes.twochange.presentation.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import pes.twochange.R;
import pes.twochange.domain.model.Match;
import pes.twochange.presentation.adapter.RecyclerViewMatchAdapter;

public class MatchProductsListFragment extends ProductsListFragment implements View.OnClickListener {

    private OnFragmentInteractionListener activity;
    protected RecyclerViewMatchAdapter adapter;

    public MatchProductsListFragment() {
    }

    public static MatchProductsListFragment newInstance() {
        return new MatchProductsListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match_list, container, false);
        buildRecyclerView(view);
        FloatingActionButton addProduct = (FloatingActionButton) view.findViewById(R.id.match_fab);
        addProduct.setOnClickListener(this);
        return view;
    }

    @Override
    public void buildRecyclerView(@NonNull View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.products_recycler_view);
        adapter = new RecyclerViewMatchAdapter(getContext(), new HashMap<String, Match>(),
                activity);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        activity.loadProductList();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProductsListFragment.OnFragmentInteractionListener) {
            activity = (MatchProductsListFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    public void display(Map<String, Match> matchedProducts) {
        adapter = new RecyclerViewMatchAdapter(getContext(), matchedProducts, activity);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        activity.match();
    }

    public interface OnFragmentInteractionListener
            extends ProductsListFragment.OnFragmentInteractionListener {
        void match();
    }

}
