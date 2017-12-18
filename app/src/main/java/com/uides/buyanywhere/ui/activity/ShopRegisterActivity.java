package com.uides.buyanywhere.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.auth.UserAuth;
import com.uides.buyanywhere.custom_view.ClearableEditText;
import com.uides.buyanywhere.custom_view.dialog.LoadingDialog;
import com.uides.buyanywhere.model.google_map.Location;
import com.uides.buyanywhere.model.Shop;
import com.uides.buyanywhere.network.Network;
import com.uides.buyanywhere.service.user.ShopRegisterService;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by TranThanhTung on 24/11/2017.
 */

public class ShopRegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ShopRegisterActivity";
    private static final int MAP_REQUEST_CODE = 0;

    @BindView(R.id.txt_input_shop_name)
    ClearableEditText textShopName;
    @BindView(R.id.txt_input_phone)
    ClearableEditText textPhone;
    @BindView(R.id.txt_input_address)
    TextInputLayout textInputAddress;
    @BindView(R.id.txt_address)
    EditText textAddress;
    @BindView(R.id.tool_bar)
    Toolbar toolbar;

    private CompositeDisposable compositeDisposable;
    private LoadingDialog loadingDialog;
    private Location location;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actitvity_shop_register);
        ButterKnife.bind(this);
        initToolBar();
        initViews();
    }

    private void initToolBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.shop_register);
        }

        textAddress.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
            }
            return true;

            default: {
                return false;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }

    private void initViews() {
        findViewById(R.id.btn_register_shop).setOnClickListener(this);
        loadingDialog = new LoadingDialog(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register_shop: {
                if (!textShopName.validate() || !textPhone.validate()) {
                    return;
                }

                String address = textAddress.getText().toString();
                if(address.isEmpty()) {
                    textInputAddress.setError(getString(R.string.pick_address_required));
                    return;
                }

                Shop shop = new Shop();
                shop.setName(textShopName.getText());
                shop.setPhone(textPhone.getText());
                shop.setAddress(address);
                shop.setOwnerId(UserAuth.getAuthUser().getId());
                shop.setLat(location.getLat());
                shop.setLon(location.getLng());

                registerShop(shop);
            }
            break;

            case R.id.txt_address:{
                Intent intent = new Intent(this, MapsActivity.class);
                startActivityForResult(intent, MAP_REQUEST_CODE);
            }
            break;

            default: {
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MAP_REQUEST_CODE:{
                if(resultCode == RESULT_OK) {
                    String address = data.getStringExtra(Constant.ADDRESS);
                    textAddress.setText(address);
                    textInputAddress.setError(null);

                    location = (Location) data.getSerializableExtra(Constant.LOCATION);
                }
            }
            break;
        }
    }

    private void registerShop(Shop shop) {
        compositeDisposable = new CompositeDisposable();
        loadingDialog.show();
        ShopRegisterService shopRegisterService = Network.getInstance().createService(ShopRegisterService.class);
        compositeDisposable.add(shopRegisterService.registerShop(UserAuth.getAuthUser().getAccessToken(), shop)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onRegisterShopSuccess, this::onRegisterShopFailed));
    }

    private void onRegisterShopSuccess(Shop shop) {
        loadingDialog.hide();
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(Constant.SHOP_ID, shop.getId());
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void onRegisterShopFailed(Throwable e) {
        Log.i(TAG, "onRegisterShopFailed: " + e);
        loadingDialog.hide();
        Toast.makeText(this, R.string.register_failed, Toast.LENGTH_SHORT).show();
    }
}
