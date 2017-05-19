package pes.twochange.presentation.controller;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;

import java.util.ArrayList;

import pes.twochange.R;
import pes.twochange.domain.model.Ad;
import pes.twochange.domain.themes.AdTheme;
import pes.twochange.presentation.Config;
import pes.twochange.presentation.fragment.AdListFragment;

public class AdListsActivity extends BaseActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener,
        AdListFragment.OnFragmentInteractionListener, AdTheme.ErrorResponse, AdTheme.ListResponse {

    private FragmentManager fragmentManager;
    private String username;
    private AdListFragment fragment;

    private final String[] TAGS = {
            "wanted", "offered", "single", "create"
    };

    private final int WANTED = 0;
    private final int OFFERED = 1;
    private final int SINGLE = 2;
    private final int CREATE = 3;

    private int currentFragment;

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int navigationItem = currentFragment;
        switch (item.getItemId()) {
            case R.id.navigation_wanted:
                navigationItem = WANTED;
                break;

            case R.id.navigation_offered:
                navigationItem = OFFERED;
                break;
        }

        if (currentFragment == navigationItem) {
            return false;
        } else {
            displayFragment(navigationItem, TAGS[navigationItem], -1);
            return true;
        }
    }

    @Override
    protected int currentMenuItemIndex() {
        return AD_ACTIVITY;
    }

    // newFragment = the number of the new fragment
    // itemList = the list is showing
    // itemPosition = the position of the item is showing, if it is displaying a list -1
    private void displayFragment(int newFragment, String itemList, int itemPosition) {
        // if the new fragment is SINGLE or CREATE it is just added in the top of the stack,
        // if it is WANTED of OFFERED the whole stack is removed and then the new fragment is added
        if (newFragment == WANTED || newFragment == OFFERED) {
            int firstFragmentId = fragmentManager.getBackStackEntryAt(0).getId();
            fragmentManager.popBackStack(firstFragmentId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        switch (newFragment) {
            case WANTED:
            case OFFERED:
                fragment = AdListFragment.newInstance();
                break;

            case SINGLE:
                // TODO single item fragment
                break;

            case CREATE:
                // TODO create item fragment
                break;

            default:
                // an error, just restart the activity flow
                displayFragment(WANTED, itemList, -1);
                return;
        }

        currentFragment = newFragment;

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.content, fragment, TAGS[newFragment]);
        transaction.commit();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_lists);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        currentFragment = WANTED;
        fragmentManager = getSupportFragmentManager();
        fragment = AdListFragment.newInstance();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.content, fragment);
        transaction.commit();

    }

    private ArrayList<Ad> wantedProducts;
    private ArrayList<Ad> offeredProducts;

    @Override
    public void onRecyclerViewItemClickListener(int position) {
        displayFragment(SINGLE, TAGS[currentFragment], position);
    }

    @Override
    public void onRecyclerViewItemLongClickListener(int position) {
        Ad item = null;
        if (currentFragment == WANTED) {
            item = wantedProducts.get(position);
        } else {
            item = offeredProducts.get(position);
        }
        // for the dialog listener
        final String finalItemKey = item.getId();
        new AlertDialog.Builder(getApplicationContext())
                .setTitle("Delete item?")
                .setMessage("Are you sure you want to remove " + item.getTitle() + "?")
                .setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AdTheme.getInstance().remove(username, TAGS[currentFragment], finalItemKey);
                            }
                        }
                )
                .setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }
                )
                .show();
    }

    @Override
    public void getProductList(final AdListFragment response) {
        final String title = TAGS[currentFragment];
        if (currentFragment == WANTED) {
            if (wantedProducts != null) {
                response.response(title, wantedProducts);
            } else {
                AdTheme.getInstance().getWantedList(
                        username,
                        this,
                        this
                );
            }
        } else {
            if (offeredProducts != null) {
                response.response(title, offeredProducts);
            } else {
                AdTheme.getInstance().getOfferedList(
                        username,
                        this,
                        this
                );
            }
        }
    }

    @Override
    public void error(String error) {

    }

    @Override
    public void listResponse(ArrayList<Ad> ads) {
        if (fragment != null) {
            fragment.response(TAGS[currentFragment], ads);
        }
    }
}