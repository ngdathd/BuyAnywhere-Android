package com.uides.buyanywhere.yen;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uides.buyanywhere.R;
import com.uides.buyanywhere.recyclerview_adapter.EndlessLoadingRecyclerViewAdapter;
import com.uides.buyanywhere.recyclerview_adapter.RecyclerViewAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yen on 26/11/2017.
 */

public class CommentActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, EndlessLoadingRecyclerViewAdapter.OnLoadingMoreListener {
    private static final String TAG = "CommentActivity";
    @BindView(R.id.comment)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_comment)
    SwipeRefreshLayout swipeRefreshLayout;

    private CommentAdapter commentAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_comment);
        ButterKnife.bind(this);
        init();
//        swipeRefreshLayout.setRefreshing(true);
    }

    private void init() {
        commentAdapter = new CommentAdapter(this);
        ArrayList a = new ArrayList();
        a.add(1);
        a.add(1);
        a.add(1);
        a.add(1);
        a.add(1);
        a.add(1);
        a.add(1);
        a.add(1);
        a.add(1);
        a.add(1);
        commentAdapter.addModels(a, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(commentAdapter);
    }

    @Override
    public void onRefresh() {
        // get from api
    }

    @Override
    public void onLoadMore() {
        // get from api
    }

    private class CommentAdapter extends EndlessLoadingRecyclerViewAdapter {
        CommentAdapter(Context context) {
            super(context, false);
        }

        @Override
        protected RecyclerView.ViewHolder initLoadingViewHolder(ViewGroup parent) {
            View loadingView = getInflater().inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(loadingView);
        }

        @Override
        protected void bindLoadingViewHolder(LoadingViewHolder loadingViewHolder, int position) {

        }

        @Override
        protected RecyclerView.ViewHolder initNormalViewHolder(ViewGroup parent) {
            View comment = getInflater().inflate(R.layout.item_comment, parent, false);
            return new CommentViewHolder(comment);
        }

        @Override
        protected void bindNormalViewHolder(NormalViewHolder holder, int position) {

        }
    }

    class CommentViewHolder extends RecyclerViewAdapter.NormalViewHolder {
        @BindView(R.id.commented_ava)
        ImageView ava;
        @BindView(R.id.commented_name)
        TextView name;
        @BindView(R.id.comment_content)
        TextView content;

        CommentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
