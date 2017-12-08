package com.uides.buyanywhere.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.model.Feedback;
import com.uides.buyanywhere.recyclerview_adapter.RecyclerViewAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by TranThanhTung on 21/11/2017.
 */

public abstract class RecyclerViewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.img_empty)
    ImageView imageEmpty;

    private CompositeDisposable compositeDisposable;
    private RecyclerViewAdapter recyclerViewAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        compositeDisposable = new CompositeDisposable();
        recyclerViewAdapter = initAdapter();
    }

    public ImageView getImageEmpty() {
        return imageEmpty;
    }

    public void showEmptyImage(boolean isShow) {
        imageEmpty.setVisibility(isShow? View.VISIBLE : View.INVISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayout(), container, false);
        ButterKnife.bind(this, rootView);
        initViews(rootView);
        return rootView;
    }

    protected int getLayout() {
        return R.layout.fragment_recycler_view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (recyclerViewAdapter.getItemCount() == 0) {
            showEmptyImage(true);
            swipeRefreshLayout.setRefreshing(true);
            onRefresh();
        }
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    @Override
    public void onStop() {
        super.onStop();
        compositeDisposable.clear();
    }

    protected abstract RecyclerViewAdapter initAdapter();

    public RecyclerViewAdapter getAdapter() {
        return recyclerViewAdapter;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void addDisposable(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    private void initViews(View rootView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerViewAdapter);

        int firstColor = getResources().getColor(R.color.blue);
        int secondColor = getResources().getColor(R.color.red);
        int thirdColor = getResources().getColor(R.color.yellow);
        int fourthColor = getResources().getColor(R.color.green);
        swipeRefreshLayout.setColorSchemeColors(firstColor, secondColor, thirdColor, fourthColor);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {

    }
}
