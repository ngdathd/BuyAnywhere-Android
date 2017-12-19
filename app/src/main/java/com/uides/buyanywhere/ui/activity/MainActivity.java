package com.uides.buyanywhere.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.auth.UserAuth;
import com.uides.buyanywhere.model.User;
import com.uides.buyanywhere.utils.BottomNavigationViewHelper;
import com.uides.buyanywhere.utils.SharedPreferencesOpenHelper;
import com.uides.buyanywhere.view_pager_adapter.MainPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by TranThanhTung on 15/09/2017.
 */

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, BottomNavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener, NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.tool_bar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.navigation_view)
    NavigationView navigationView;

    CircleImageView imageAvatar;
    TextView textName;
    TextView textEmail;

    private SearchView searchView;
    private MenuItem searchItem;
    private MainPagerAdapter mainPagerAdapter;
    private ActionBar actionBar;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tool_bar, menu);
        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(getString(R.string.input_key));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                drawerLayout.openDrawer(Gravity.START);
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

        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        imageAvatar = (CircleImageView) header.findViewById(R.id.img_avatar);
        textName = (TextView) header.findViewById(R.id.txt_name);
        textEmail = (TextView) header.findViewById(R.id.txt_email);

        User user = UserAuth.getAuthUser();
        if(user == null) {
            user = SharedPreferencesOpenHelper.getUser(this);
            UserAuth.setAuthUser(user);
        }
        updateDrawerHeader(user.getName(), user.getEmail(), user.getAvatarUrl());
    }

    public void updateDrawerHeader(String name,
                                   String email,
                                   String avatar) {

        Picasso.with(this)
                .load(avatar)
                .placeholder(R.drawable.avatar_placeholder)
                .into(imageAvatar);

        textName.setText(name);
        textEmail.setText(email);
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
        Intent intent = new Intent(this, SearchResultActivity.class);
        intent.putExtra(Constant.PRODUCT_NAME, query);
        startActivity(intent);
        return true;
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
                viewPager.setCurrentItem(MainPagerAdapter.PRODUCT_FRAGMENT_INDEX);
                navigationView.setCheckedItem(R.id.navigation_drawer_product);
                return true;
            }

            case R.id.navigation_location: {
                actionBar.setTitle(R.string.location_search);
                viewPager.setCurrentItem(MainPagerAdapter.FIND_BY_LOCATION_FRAGMENT_INDEX);
                return true;
            }

            case R.id.navigation_shopping_cart: {
                actionBar.setTitle(R.string.shopping_cart);
                viewPager.setCurrentItem(MainPagerAdapter.SHOPPING_CART_FRAGMENT_INDEX);
                navigationView.setCheckedItem(R.id.navigation_drawer_shopping_cart);
                return true;
            }

            case R.id.navigation_shop: {
                actionBar.setTitle(R.string.shop);
                viewPager.setCurrentItem(MainPagerAdapter.SHOP_FRAGMENT_INDEX);
                return true;
            }

            case R.id.navigation_profile: {
                actionBar.setTitle(R.string.profile);
                viewPager.setCurrentItem(MainPagerAdapter.PROFILE_FRAGMENT_INDEX);
                return true;
            }

            case R.id.navigation_drawer_product: {
                actionBar.setTitle(R.string.product);
                viewPager.setCurrentItem(MainPagerAdapter.PRODUCT_FRAGMENT_INDEX);
                drawerLayout.closeDrawer(Gravity.START);
                return true;
            }

            case R.id.navigation_drawer_location: {
                actionBar.setTitle(R.string.location_search);
                viewPager.setCurrentItem(MainPagerAdapter.FIND_BY_LOCATION_FRAGMENT_INDEX);
                drawerLayout.closeDrawer(Gravity.START);
                return true;
            }

            case R.id.navigation_drawer_order: {
                Intent intent = new Intent(this, OrderActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(Gravity.START);
                return false;
            }

            case R.id.navigation_drawer_shopping_cart: {
                actionBar.setTitle(R.string.shopping_cart);
                viewPager.setCurrentItem(MainPagerAdapter.SHOPPING_CART_FRAGMENT_INDEX);
                drawerLayout.closeDrawer(Gravity.START);
                return true;
            }

            case R.id.navigation_drawer_profile: {
                actionBar.setTitle(R.string.profile);
                viewPager.setCurrentItem(MainPagerAdapter.PROFILE_FRAGMENT_INDEX);
                drawerLayout.closeDrawer(Gravity.START);
                return true;
            }

            case R.id.navigation_drawer_shop: {
                actionBar.setTitle(R.string.shop);
                viewPager.setCurrentItem(MainPagerAdapter.SHOP_FRAGMENT_INDEX);
                drawerLayout.closeDrawer(Gravity.START);
                return true;
            }

            case R.id.navigation_drawer_logout: {
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(this, AuthActivity.class);
                intent.putExtra(Constant.KEY_AUTO_SIGN_IN, false);
                startActivity(intent);
                finish();
                return false;
            }

            default: {
                return true;
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case MainPagerAdapter.FIND_BY_LOCATION_FRAGMENT_INDEX: {
                bottomNavigationView.setSelectedItemId(R.id.navigation_location);
                navigationView.setCheckedItem(R.id.navigation_drawer_location);
            }
            break;

            case MainPagerAdapter.SHOPPING_CART_FRAGMENT_INDEX: {
                bottomNavigationView.setSelectedItemId(R.id.navigation_shopping_cart);
                navigationView.setCheckedItem(R.id.navigation_drawer_shopping_cart);
            }
            break;

            case MainPagerAdapter.SHOP_FRAGMENT_INDEX: {
                bottomNavigationView.setSelectedItemId(R.id.navigation_shop);
                navigationView.setCheckedItem(R.id.navigation_drawer_shop);
            }
            break;

            case MainPagerAdapter.PROFILE_FRAGMENT_INDEX: {
                bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
                navigationView.setCheckedItem(R.id.navigation_drawer_profile);
            }
            break;

            default: {
                bottomNavigationView.setSelectedItemId(R.id.navigation_product);
                navigationView.setCheckedItem(R.id.navigation_drawer_product);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
