package com.uides.buyanywhere.recyclerview_adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Admin on 5/14/2017.
 */

public abstract class EndlessLoadingRecyclerViewAdapter extends RecyclerViewAdapter {
    protected boolean isLoading = false;
    public static final int VIEW_TYPE_LOADING = 2;
    private OnLoadingMoreListener loadingMoreListener;

    public EndlessLoadingRecyclerViewAdapter(Context context, boolean enableSelectedMode) {
        super(context, enableSelectedMode);
    }

    public void setLoadingMoreListener(OnLoadingMoreListener loadingMoreListener) {
        this.loadingMoreListener = loadingMoreListener;
    }

    @Override
    protected void onScrollStopped(RecyclerView recyclerView) {
        int scrollOrient = getScrollOrient();
        if (scrollOrient == SCROLL_DOWN) {
            if (isLoading) {
                return;
            }
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
            int lastVisibleItemPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
            if (firstVisibleItemPosition > 0 && lastVisibleItemPosition == getItemCount() - 1) {
                isLoading = true;
                if(loadingMoreListener != null) {
                    loadingMoreListener.onLoadMore();
                }
            }
        } else if (scrollOrient == SCROLL_UP) {
            super.onScrollStopped(recyclerView);
        }
    }

    public void showLoadingItem(boolean isScroll) {
        addModel(null, VIEW_TYPE_LOADING, isScroll);
    }
	
	public void hideLoadingItem(OnItemRemovedCompleteListener onLoadingItemRemoveCompleteListener) {
        if (isLoading) {
            removeModel(getItemCount() - 1);
            isLoading = false;
        }else if(onLoadingItemRemoveCompleteListener != null) {
            onLoadingItemRemoveCompleteListener.onItemRemovedComplete();
        }
    }

    @Override
    protected RecyclerView.ViewHolder solvedOnCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder result;
        switch (viewType) {
            case VIEW_TYPE_REFRESHING: {
                result = initRefreshingViewHolder(parent);
            }
            break;

            case VIEW_TYPE_LOADING: {
                result = initLoadingViewHolder(parent);
            }
            break;

            default: {
                result = initNormalViewHolder(parent);
            }
            break;
        }
        return result;
    }

    @Override
    protected void solvedOnBindViewHolder(RecyclerView.ViewHolder viewHolder, int viewType, int position) {
        switch (viewType) {
            case VIEW_TYPE_REFRESHING: {
                bindRefreshingViewHolder((RefreshingViewHolder) viewHolder, position);
            }
            break;

            case VIEW_TYPE_LOADING: {
                bindLoadingViewHolder((LoadingViewHolder) viewHolder, position);
            }
            break;

            default: {
                bindNormalViewHolder((NormalViewHolder) viewHolder, position);
            }
            break;
        }
    }

    public interface OnLoadingMoreListener {
        void onLoadMore();
    }

    protected abstract RecyclerView.ViewHolder initLoadingViewHolder(ViewGroup parent);

    protected abstract void bindLoadingViewHolder(LoadingViewHolder loadingViewHolder, int position);

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }
	
	protected class EndlessLoadingRecyclerViewItemAnimatorObservable extends RecyclerViewItemAnimatorObservable{
        @Override
        public void onRemoveFinished(RecyclerView.ViewHolder item) {
            if(itemRemovedCompleteListener != null &&
                    (item instanceof RefreshingViewHolder || item instanceof LoadingViewHolder)){
                observeMove = true;
            }
        }
    }
}
