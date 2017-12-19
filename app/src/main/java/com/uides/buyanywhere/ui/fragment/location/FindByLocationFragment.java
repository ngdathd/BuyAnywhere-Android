package com.uides.buyanywhere.ui.fragment.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.adapter.ProductAdapter;
import com.uides.buyanywhere.auth.UserAuth;
import com.uides.buyanywhere.custom_view.dialog.LoadingDialog;
import com.uides.buyanywhere.model.Feedback;
import com.uides.buyanywhere.model.PageResult;
import com.uides.buyanywhere.model.Product;
import com.uides.buyanywhere.model.ProductPreview;
import com.uides.buyanywhere.model.ShopLocationResult;
import com.uides.buyanywhere.network.Network;
import com.uides.buyanywhere.network.SingleShotLocationProvider;
import com.uides.buyanywhere.recyclerview_adapter.RecyclerViewAdapter;
import com.uides.buyanywhere.service.product.GetProductService;
import com.uides.buyanywhere.service.rating.GetFeedbackService;
import com.uides.buyanywhere.service.user.CheckUserCartService;
import com.uides.buyanywhere.service.user.GetProductByLocationService;
import com.uides.buyanywhere.ui.activity.ProductDetailActivity;
import com.uides.buyanywhere.ui.activity.ProductDetailLoadingActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;

import static com.uides.buyanywhere.ui.fragment.product.AllProductsFragment.LATEST_FEEDBACK_COUNT;

/**
 * Created by TranThanhTung on 19/11/2017.
 */

public class FindByLocationFragment extends Fragment implements OnMapReadyCallback, SearchView.OnQueryTextListener, AdapterView.OnItemSelectedListener, View.OnClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, RecyclerViewAdapter.OnItemClickListener {
    private static final String TAG = "FindByLocationFragment";
    private static final int REQUEST_ACCESS_LOCATION_REQUEST_CODE = 0;
    @BindView(R.id.search_view)
    SearchView searchView;
    @BindView(R.id.spinner_distance)
    AppCompatSpinner spinnerDistance;
    @BindView(R.id.map_view)
    MapView mapView;
    @BindView(R.id.fab_my_location)
    FloatingActionButton fabMyLocation;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.txt_searching)
    TextView textSearching;
    @BindView(R.id.txt_shop_name)
    TextView textShopName;
    @BindView(R.id.txt_product_found)
    TextView textProductFound;
    @BindView(R.id.txt_distance)
    TextView textDistance;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    GoogleMap googleMap;

    private Circle circle;
    private Marker myLocationMarker;
    private List<Marker> shopMarkers;

    private CompositeDisposable compositeDisposable;
    private GetProductByLocationService getProductByLocationService;

    private Disposable activeDisposable;
    private LatLng selectedLocation;
    private View rootView;
    private BottomSheetBehavior<RelativeLayout> bottomSheetBehavior;

    private ProductAdapter productAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_find_by_location, container, false);
        ButterKnife.bind(this, rootView);

        shopMarkers = new ArrayList<>();
        productAdapter = new ProductAdapter(getActivity());
        productAdapter.setOnItemClickListener(this);
        productAdapter.disableLoadingMore(true);

        initServices();
        initViews();
        initMap(savedInstanceState);
        return rootView;
    }

    private void initServices() {
        compositeDisposable = new CompositeDisposable();
        Network network = Network.getInstance();
        getProductByLocationService = network.createService(GetProductByLocationService.class);
    }

    @Override
    public void onStop() {
        super.onStop();
        this.compositeDisposable.clear();
        selectedLocation = null;
        this.circle = null;
        this.myLocationMarker = null;
    }

    private void initMap(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        MapsInitializer.initialize(getActivity());
        mapView.getMapAsync(this);
    }

    private void initViews() {

        spinnerDistance.setAdapter(new DistanceAdapter(getActivity()));
        spinnerDistance.setSelection(0);
        spinnerDistance.setOnItemSelectedListener(this);

        searchView.setOnQueryTextListener(this);
        searchView.setIconified(false);
        searchView.clearFocus();
        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(android.R.color.black));
        searchView.setOnCloseListener(() -> true);

        fabMyLocation.setOnClickListener(this);

        RelativeLayout bottomSheetLayout = (RelativeLayout) rootView.findViewById(R.id.rl_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(productAdapter);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        Activity activity = getActivity();

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String permissions[] = new String[2];
            permissions[0] = Manifest.permission.ACCESS_FINE_LOCATION;
            permissions[1] = Manifest.permission.ACCESS_COARSE_LOCATION;
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_ACCESS_LOCATION_REQUEST_CODE);
        } else {
            checkGPS();
            setupButton();
            translateToMyLocation();
        }
        showCompassButton();
    }

    @SuppressLint("MissingPermission")
    private void translateToMyLocation() {
        fetchCurrentLocation(location -> {
            if (location != null) {
                LatLng loc = new LatLng(location.latitude, location.longitude);

                if (loc.equals(selectedLocation)) {
                    return;
                }

                showSelectedLocationMarkerAt(loc);

                CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(13).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });
    }

    private void showSelectedLocationMarkerAt(LatLng location) {
        this.selectedLocation = location;

        if (circle == null) {
            Resources resources = getActivity().getResources();
            circle = googleMap.addCircle(new CircleOptions()
                    .center(location)
                    .radius(((int) spinnerDistance.getSelectedItem()) * 1000)
                    .strokeColor(resources.getColor(R.color.map_circle_stroke_color))
                    .fillColor(resources.getColor(R.color.map_circle_background_color)));
        } else {
            circle.setCenter(location);
        }

        if (myLocationMarker == null) {
            myLocationMarker = googleMap.addMarker(new MarkerOptions()
                    .position(location)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin)));
        } else {
            myLocationMarker.setPosition(location);
        }
    }

    private void fetchCurrentLocation(SingleShotLocationProvider.LocationCallback locationCallback) {
        SingleShotLocationProvider.requestSingleUpdate(getActivity(), locationCallback);
    }

    @SuppressLint("MissingPermission")
    public void setupButton() {
        googleMap.setMyLocationEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMarkerClickListener(this);
    }

    private void checkGPS() {
        LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (manager == null) {
            return;
        }
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogBlackText);
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
                translateToMyLocation();
            }
            break;
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        fetchProductByLocation(query, selectedLocation, (Integer) spinnerDistance.getSelectedItem());
        return false;
    }

    public void showSearchingProgress(boolean isShow) {
        if (isShow) {
            progressBar.setVisibility(View.VISIBLE);
            textSearching.setVisibility(View.VISIBLE);
            searchView.setVisibility(View.INVISIBLE);
            spinnerDistance.setVisibility(View.INVISIBLE);
        } else {
            searchView.setVisibility(View.VISIBLE);
            spinnerDistance.setVisibility(View.VISIBLE);
            textSearching.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void fetchProductByLocation(String productName, LatLng location, double distance) {
        if (activeDisposable != null && !activeDisposable.isDisposed()) {
            activeDisposable.dispose();
        }

        showSearchingProgress(true);

        activeDisposable = getProductByLocationService
                .getProducts(productName.isEmpty() ? null : productName, location.latitude, location.longitude, distance)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onFetchProductSuccess, this::onFetchProductFailed);
        compositeDisposable.add(activeDisposable);
    }

    private void clearAllShopMarker() {
        int count = shopMarkers.size();
        for (int i = count - 1; i >= 0; i--) {
            shopMarkers.remove(i).remove();
        }
    }

    private void addShopMarker(ShopLocationResult shopLocationResult) {
        List<ProductPreview> productPreviews = shopLocationResult.getProducts();
        if (productPreviews == null || productPreviews.isEmpty()) {
            return;
        }
        Marker shopMarker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(shopLocationResult.getLat(), shopLocationResult.getLon()))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker))
                .title(shopLocationResult.getShopName()));
        shopMarker.setTag(shopLocationResult);
        shopMarkers.add(shopMarker);
    }

    private void onFetchProductSuccess(List<ShopLocationResult> shopLocationResults) {
        clearAllShopMarker();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        showSearchingProgress(false);

        for (ShopLocationResult shopLocationResult : shopLocationResults) {
            addShopMarker(shopLocationResult);
        }
    }

    private void onFetchProductFailed(Throwable e) {
        showSearchingProgress(false);
        Log.i(TAG, "onFetchProductFailed: " + e);
        Toast.makeText(getActivity(), R.string.unexpected_error_message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (circle != null) {
            int value = (int) parent.getAdapter().getItem(position);
            circle.setRadius(value * 1000);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_my_location: {
                checkGPS();
                translateToMyLocation();
            }
            break;

            default: {
                break;
            }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        showSelectedLocationMarkerAt(latLng);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        ShopLocationResult shopLocationResult = (ShopLocationResult) marker.getTag();
        if (shopLocationResult != null) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            textShopName.setText(shopLocationResult.getShopName());
            List<ProductPreview> productPreviews = shopLocationResult.getProducts();
            textProductFound.setText("" + productPreviews.size());
            double distance = Math.round(shopLocationResult.getDistance() * 10.0) / 10.0;
            StringBuilder distanceValue = new StringBuilder();
            if(distance < 1.0) {
                distanceValue.append(distance * 1000).append(" m");
            } else {
                distanceValue.append(distance).append(" Km");
            }
            textDistance.setText(distanceValue.toString());
            productAdapter.refresh(productPreviews);
        }
        return false;
    }

    @Override
    public void onItemClick(RecyclerView.Adapter adapter, View view, int viewType, int position) {
        ProductPreview productPreview = productAdapter.getItem(position, ProductPreview.class);

        Intent intent = new Intent(getActivity(), ProductDetailLoadingActivity.class);
        intent.putExtra(Constant.PRODUCT_ID, productPreview.getId());
        startActivity(intent);
    }

    private class DistanceAdapter extends BaseAdapter {
        private List<Integer> distances;
        private LayoutInflater inflater;

        public DistanceAdapter(Context context) {
            inflater = LayoutInflater.from(context);
            distances = new ArrayList<>();
            distances.add(1);
            distances.add(3);
            distances.add(5);
            distances.add(10);
            distances.add(15);
            distances.add(20);
        }

        @Override
        public int getCount() {
            return distances.size();
        }

        @Override
        public Integer getItem(int position) {
            return distances.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DistanceViewHolder distanceViewHolder;
            if (convertView == null) {
                distanceViewHolder = new DistanceViewHolder();
                convertView = inflater.inflate(R.layout.distance_spinner_item, parent, false);
                distanceViewHolder.textContent = (TextView) convertView.findViewById(R.id.txt_content);
                convertView.setTag(distanceViewHolder);
            } else {
                distanceViewHolder = (DistanceViewHolder) convertView.getTag();
            }
            distanceViewHolder.textContent.setText(getItem(position) + " km");
            return convertView;
        }
    }

    private class DistanceViewHolder {
        private TextView textContent;
    }
}
