package com.uides.buyanywhere.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.uides.buyanywhere.R;
import com.uides.buyanywhere.utils.BottomNavigationViewHelper;
import com.uides.buyanywhere.view_pager_adapter.MainPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by TranThanhTung on 15/09/2017.
 */

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, BottomNavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.tool_bar)
    Toolbar toolbar;

    private SearchView searchView;
    private MainPagerAdapter mainPagerAdapter;
    private ActionBar actionBar;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tool_bar, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {

            }
            break;

            case R.id.action_search: {

            }
            break;

            case R.id.action_settings: {

            }
            break;

            default: {
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        inti();
    }

    private void inti() {
        initToolBar();
        initBottomNavigation();

        mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mainPagerAdapter);
        viewPager.addOnPageChangeListener(this);
    }

    private void initBottomNavigation() {
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.navigation_product);
    }

    private void initToolBar() {
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationIcon(R.drawable.ic_menu);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_product: {
                actionBar.setTitle(R.string.product);
                if(searchView != null) {
                    searchView.setVisibility(View.VISIBLE);
                }
                viewPager.setCurrentItem(MainPagerAdapter.PRODUCT_FRAGMENT_INDEX);
            }
            return true;

            case R.id.navigation_location: {
                actionBar.setTitle(R.string.location_search);
                if(searchView != null) {
                    searchView.setVisibility(View.VISIBLE);
                }
                viewPager.setCurrentItem(MainPagerAdapter.FIND_BY_LOCATION_FRAGMENT_INDEX);
            }
            return true;

            case R.id.navigation_shopping_cart: {
                actionBar.setTitle(R.string.shopping_cart);
                if(searchView != null) {
                    searchView.setVisibility(View.INVISIBLE);
                }
                viewPager.setCurrentItem(MainPagerAdapter.SHOPPING_CART_FRAGMENT_INDEX);
            }
            return true;

            case R.id.navigation_shop: {
                actionBar.setTitle(R.string.shop);
                if(searchView != null) {
                    searchView.setVisibility(View.INVISIBLE);
                }
                viewPager.setCurrentItem(MainPagerAdapter.SHOP_FRAGMENT_INDEX);
            }
            return true;

            case R.id.navigation_profile: {
                actionBar.setTitle(R.string.profile);
                if(searchView != null) {
                    searchView.setVisibility(View.INVISIBLE);
                }
                viewPager.setCurrentItem(MainPagerAdapter.PROFILE_FRAGMENT_INDEX);
            }
            return true;

            default: {
                return false;
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        bottomNavigationView.setSelectedItemId(MainPagerAdapter.getNavigationButtonID(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
