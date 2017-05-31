package pes.twochange.presentation.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import pes.twochange.R;
import pes.twochange.presentation.adapter.RecyclerViewImagesAdapter;
import pes.twochange.presentation.view.OnRecyclerViewItemClickListener;

public class NewProductFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener activity;

    public NewProductFragment() {
        // Required empty public constructor
    }
    public static NewProductFragment newInstance() {
        return new NewProductFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_product, container, false);

        ImageView closeButton = (ImageView) view.findViewById(R.id.close_button);
        EditText nameTextView = (EditText) view.findViewById(R.id.products_name);
        EditText descriptionTextView = (EditText) view.findViewById(R.id.products_description);
        Spinner categorySpinner = (Spinner) view.findViewById(R.id.category_spinner);
        Spinner stateSpinner = (Spinner) view.findViewById(R.id.state_spinner);
        Spinner yearSpinner = (Spinner) view.findViewById(R.id.year_spinner);
        RecyclerView imagesList = (RecyclerView) view.findViewById(R.id.images_list);
        ImageView addImageButton = (ImageView) view.findViewById(R.id.add_image_button);
        TextView postButton = (TextView) view.findViewById(R.id.post_product_button);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.ad_category, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.product_states, android.R.layout.simple_spinner_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, getYearsArray());
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        RecyclerViewImagesAdapter adapter = new RecyclerViewImagesAdapter(getActivity());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false);
        imagesList.setLayoutManager(layoutManager);
        imagesList.setAdapter(adapter);

        closeButton.setOnClickListener(this);
        addImageButton.setOnClickListener(this);
        postButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            activity = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    private ArrayList<String> getYearsArray() {
        ArrayList<String> res = new ArrayList<>();
        for (int i = 1970; i < 2017; i++) {
            res.add(String.format(Locale.FRANCE, "%d", i));
        }
        return res;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_button:
                activity.close();
                break;

            case R.id.add_image_button:
                activity.addImage();
                break;

            case R.id.post_product_button:
                // TODO get whole data to build Product
//                activity.postProduct(...);
        }
    }

    public interface OnFragmentInteractionListener extends OnRecyclerViewItemClickListener {
        void addImage();
        void close();
        void postProduct(String name, String description, String category);
    }
}
