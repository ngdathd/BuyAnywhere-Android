package com.uides.buyanywhere.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.model.google_map.Address;
import com.uides.buyanywhere.model.google_map.Location;
import com.uides.buyanywhere.model.google_map.GoogleAddressResponse;
import com.uides.buyanywhere.network.GoogleMapClient;
import com.uides.buyanywhere.network.SingleShotLocationProvider;
import com.uides.buyanywhere.recyclerview_adapter.RecyclerViewAdapter;
import com.uides.buyanywhere.service.google_map.GetMapAddressService;
import com.uides.buyanywhere.service.google_map.GetMapLatLonService;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        SearchView.OnQueryTextListener,
        GoogleMap.OnMapClickListener,
        View.OnClickListener,
        RecyclerViewAdapter.OnItemClickListener {
    private static final String TAG = "MapsActivity";
    private static final int REQUEST_ACCESS_LOCATION_REQUEST_CODE = 0;

    @BindView(R.id.tool_bar)
    Toolbar toolbar;
    @BindView(R.id.map_view)
    MapView mapView;
    @BindView(R.id.card_address)
    CardView cardAddress;
    @BindView(R.id.txt_address)
    TextView textAddress;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.fab_my_location)
    FloatingActionButton fabMyLocation;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    GoogleMap googleMap;
    Marker shopMarker;

    private GetMapAddressService getMapAddressService;
    private GetMapLatLonService getMapLatLonService;
    private CompositeDisposable compositeDisposable;

    private Disposable activeAddressDisposable;
    private Disposable activeLatLngQueryDisposable;
    private boolean isMarkerAtMyLocation = false;

    private AddressAdapter addressAdapter;
    private Location location;
    private String address;
    private SearchView searchView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tool_bar, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (addressAdapter.getItemCount() != 0) {
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                } else {
                    recyclerView.setVisibility(View.INVISIBLE);
                }
            }
        });

        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(getString(R.string.input_address));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
            }
            break;

            default: {
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra(Constant.ADDRESS, address);
        intent.putExtra(Constant.LOCATION, location);
        setResult(RESULT_OK, intent);
        super.finish();
    }

    public void showAddress(boolean isShow, String address) {
        cardAddress.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
        if (address == null) {
            textAddress.setText(R.string.locating);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            textAddress.setText(address);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        location = (Location) intent.getSerializableExtra(Constant.LOCATION);
        address = intent.getStringExtra(Constant.ADDRESS);

        initServices();

        initToolBar();

        initListAddressResult();

        initMap(savedInstanceState);
    }

    private void initListAddressResult() {
        addressAdapter = new AddressAdapter(this);
        addressAdapter.setOnItemClickListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, linearLayoutManager.getOrientation()));
        recyclerView.setAdapter(addressAdapter);
    }

    private void initServices() {
        compositeDisposable = new CompositeDisposable();
        GoogleMapClient googleMapClient = GoogleMapClient.getInstance();
        getMapAddressService = googleMapClient.createService(GetMapAddressService.class);
        getMapLatLonService = googleMapClient.createService(GetMapLatLonService.class);
    }

    private void initMap(Bundle savedInstanceState) {
        fabMyLocation.setOnClickListener(this);

        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        MapsInitializer.initialize(this);
        mapView.getMapAsync(this);
    }

    private void initToolBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.pick_address);
        }
    }

    public void showShopMarkerAt(LatLng latLng, String address) {
        if (shopMarker == null) {
            shopMarker = googleMap.addMarker(new MarkerOptions()
                    .position(latLng));
        } else {
            shopMarker.setPosition(latLng);
        }

        if (address == null || address.isEmpty()) {
            fetchAddress(latLng.latitude, latLng.longitude);
        } else {
            showAddress(true, address);
            this.address = address;
            this.location.setLat(latLng.latitude);
            this.location.setLng(latLng.longitude);
        }
    }

    public void fetchAddress(double lat, double lon) {
        if (activeAddressDisposable != null && !activeAddressDisposable.isDisposed()) {
            activeAddressDisposable.dispose();
        }

        showAddress(true, null);
        activeAddressDisposable = getMapAddressService.getAddress(lat + "," + lon)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> onFetchAddressSuccess(lat, lon, success), this::onFetchAddressFailed);

        compositeDisposable.add(activeAddressDisposable);
    }

    private void onFetchAddressSuccess(double lat, double lon, GoogleAddressResponse googleAddressResponse) {
        List<com.uides.buyanywhere.model.google_map.Address> addresses = googleAddressResponse.getResults();
        if (addresses.size() == 0) {
            showAddress(false, null);
            this.address = "";
            this.location.setLat(null);
            this.location.setLng(null);
        } else {
            this.address = addresses.get(0).getFormatted_address();
            this.location.setLat(lat);
            this.location.setLng(lon);
            showAddress(true, this.address);
        }
    }

    private void onFetchAddressFailed(Throwable e) {
        showAddress(false, null);
        Log.i(TAG, "onFetchAddressFailed: " + e);
        Toast.makeText(this, R.string.unexpected_error_message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.setOnMapClickListener(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String permissions[] = new String[2];
            permissions[0] = Manifest.permission.ACCESS_FINE_LOCATION;
            permissions[1] = Manifest.permission.ACCESS_COARSE_LOCATION;
            ActivityCompat.requestPermissions(this, permissions, REQUEST_ACCESS_LOCATION_REQUEST_CODE);
        } else {
            checkGPS();
            setupButton();
            if (location == null) {
                location = new Location();
                translateToCurrentLocation();
            } else {
                translateToLocation(location, address);
            }
        }
        showCompassButton();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        this.location.setLat(latLng.latitude);
        this.location.setLng(latLng.longitude);
        showShopMarkerAt(latLng, null);
        isMarkerAtMyLocation = false;
    }

    private void checkGPS() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (manager == null) {
            return;
        }
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogBlackText);
        builder.setTitle(R.string.gps_title)
                .setMessage(R.string.gps_message)
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showCompassButton() {
        View compassButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("5"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) compassButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 0, 100);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.isEmpty()) {
            addressAdapter.clear();
            recyclerView.setVisibility(View.INVISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            if (activeLatLngQueryDisposable != null && !activeLatLngQueryDisposable.isDisposed()) {
                activeLatLngQueryDisposable.dispose();
            }
            activeLatLngQueryDisposable = getMapLatLonService.queryAddress(newText)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(success -> {
                        Log.i(TAG, "onQueryTextChange: " + success);
                        addressAdapter.refresh(success.getResults());
                    }, failure -> {
                        Log.i(TAG, "onQueryTextChange: " + failure);
                    });
            compositeDisposable.add(activeLatLngQueryDisposable);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ACCESS_LOCATION_REQUEST_CODE: {
                for (int grantResult : grantResults) {
                    if (grantResult == PackageManager.PERMISSION_DENIED) {
                        return;
                    }
                }
                setupButton();
                if (location == null) {
                    location = new Location();
                    translateToCurrentLocation();
                } else {
                    translateToLocation(location, address);
                }
            }
            break;
        }
    }

    private void fetchCurrentLocation(SingleShotLocationProvider.LocationCallback locationCallback) {
        SingleShotLocationProvider.requestSingleUpdate(this, locationCallback);
    }

    @SuppressLint("MissingPermission")
    private void translateToCurrentLocation() {
        fetchCurrentLocation(location -> {
            if (location == null) {
                Toast.makeText(this, R.string.auto_locate_failed, Toast.LENGTH_SHORT).show();
                return;
            }

            LatLng latLng = new LatLng(location.latitude, location.longitude);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            if (!isMarkerAtMyLocation) {
                isMarkerAtMyLocation = true;
                showShopMarkerAt(new LatLng(location.latitude, location.longitude), null);
            }
        });
    }

    private void translateToLocation(Location location, String address) {
        LatLng latLng = new LatLng(location.getLat(), location.getLng());
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        if (!isMarkerAtMyLocation) {
            isMarkerAtMyLocation = true;
            showShopMarkerAt(new LatLng(location.getLat(), location.getLng()), address);
        }
    }

    @SuppressLint("MissingPermission")
    public void setupButton() {
        googleMap.setMyLocationEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        compositeDisposable.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_my_location: {
                translateToCurrentLocation();
            }
            break;

            default: {
                break;
            }
        }
    }

    @Override
    public void onItemClick(RecyclerView.Adapter adapter, View view, int viewType, int position) {
        searchView.clearFocus();
        Address address = addressAdapter.getItem(position, Address.class);
        isMarkerAtMyLocation = false;
        translateToLocation(address.getGeometry().getLocation(), address.getFormatted_address());
    }

    private class AddressAdapter extends RecyclerViewAdapter {

        public AddressAdapter(Context context) {
            super(context, false);
        }

        @Override
        protected RecyclerView.ViewHolder initNormalViewHolder(ViewGroup parent) {
            View item = getInflater().inflate(R.layout.item_address, parent, false);
            return new AddressViewHolder(item);
        }

        @Override
        protected void bindNormalViewHolder(NormalViewHolder holder, int position) {
            AddressViewHolder addressViewHolder = (AddressViewHolder) holder;
            Address address = getItem(position, Address.class);
            addressViewHolder.textName.setText(address.getFormatted_address());
        }
    }

    public class AddressViewHolder extends RecyclerViewAdapter.NormalViewHolder {
        @BindView(R.id.txt_name)
        TextView textName;

        public AddressViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
