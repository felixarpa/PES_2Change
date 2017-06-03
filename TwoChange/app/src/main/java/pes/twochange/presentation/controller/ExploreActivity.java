package pes.twochange.presentation.controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

import pes.twochange.R;
import pes.twochange.domain.model.Product;
import pes.twochange.domain.themes.AdTheme;
import pes.twochange.presentation.fragment.SearchProductsListFragment;

public class ExploreActivity extends BaseActivity
        implements SearchProductsListFragment.OnFragmentInteractionListener {

    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        toolbar.setTitle(R.string.explore);

        fragment = SearchProductsListFragment.newInstance();

        replaceFragment(R.id.explore_frame, fragment, "main_list");

    }

    @Override
    protected int currentMenuItemIndex() {
        return EXPLORE_ACTIVITY;
    }

    private ArrayList<Product> productsList = new ArrayList<>();

    @Override
    public void loadProductList() {
        AdTheme.getInstance().getAllProducts(
                new AdTheme.ProductListResponse() {
                    @Override
                    public void listResponse(ArrayList<Product> productItems) {
                        productsList = productItems;
                        if (fragment != null && fragment instanceof SearchProductsListFragment) {
                            ((SearchProductsListFragment) fragment).display(productsList);
                        }
                    }
                },
                new AdTheme.ErrorResponse() {
                    @Override
                    public void error(String error) {

                    }
                }
        );
    }

    @Override
    public void searchProducts(String query) {
        if (productsList != null && productsList.size() > 0) {
            ArrayList<Product> searchResultList = new ArrayList<>();
            String upperCaseQuery = query.toUpperCase();
            for (Product product : productsList) {
                String upperCaseTitle = product.getName().toUpperCase();
                String upperCaseDescription = product.getDescription().toUpperCase();
                if (upperCaseTitle.contains(upperCaseQuery) ||
                        upperCaseDescription.contains(upperCaseQuery)) {
                    searchResultList.add(product);
                }
            }
            if (fragment != null && fragment instanceof SearchProductsListFragment) {
                ((SearchProductsListFragment) fragment).display(searchResultList);
            }
        }
    }

    private void categoryFilter(String category) {
        if (productsList != null && productsList.size() > 0) {
            ArrayList<Product> categoryResultList = new ArrayList<>();
            for (Product product : productsList) {
                String prodCategory = product.getCategory();
                if (category.equals(prodCategory)) {
                    categoryResultList.add(product);
                }
            }
            if (fragment != null && fragment instanceof SearchProductsListFragment) {
                ((SearchProductsListFragment) fragment).display(categoryResultList);
            }
        }
    }

    private void rateFilter(int min, int max) {
        if (productsList != null && productsList.size() > 0) {
            ArrayList<Product> rateResultList = new ArrayList<>();
            for (Product product : productsList) {
                int prodRate = product.getRating();
                if ((prodRate >= min) && (prodRate <= max)) {
                    rateResultList.add(product);
                }
            }
            if (fragment != null && fragment instanceof SearchProductsListFragment) {
                ((SearchProductsListFragment) fragment).display(rateResultList);
            }
        }
    }

    @Override
    public void onRecyclerViewItemClickListener(int position) {

    }

}
