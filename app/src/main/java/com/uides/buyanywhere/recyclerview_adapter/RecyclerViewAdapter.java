package com.uides.buyanywhere.recyclerview_adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 5/12/2017.
 */

public abstract class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static String TAG = "RecyclerViewAdapter";
    public static final int VIEW_TYPE_NORMAL = 0;

    private List<ModelWrapper> listWrapperModels;
    private List<ModelWrapper> listBackupWrapperModels;
    private List<Boolean> listSelectedItems;
    private LayoutInflater mInflater;
    private OnItemClickListener mOnItemClickListener;
    private boolean selectedMode;
    private RecyclerView mRecyclerView;

    public RecyclerViewAdapter(Context context, boolean enableSelectedMode) {
        this.mInflater = LayoutInflater.from(context);
        this.listWrapperModels = new ArrayList<>();
        this.listBackupWrapperModels = new ArrayList<>();
        this.selectedMode = enableSelectedMode;

        if (enableSelectedMode) {
            this.listSelectedItems = new ArrayList<>();
        } else {
            this.listSelectedItems = new ArrayList<>(0);
        }
    }

    public void retrieveList(){
        listWrapperModels = listBackupWrapperModels;
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        this.mRecyclerView = null;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public int getPositionOf(Object object) {
        for (int i = 0; i < listWrapperModels.size(); i++) {
            if(listWrapperModels.get(i).model.equals(object)){
                return i;
            }
        }
        return -1;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void clear() {
        listWrapperModels.clear();
        listBackupWrapperModels.clear();
        notifyDataSetChanged();
    }

    public <T> void addModels(List<T> listModels, boolean isScroll) {
        addModels(listModels, VIEW_TYPE_NORMAL, isScroll);
    }

    public <T> void addModels(List<T> listModels, int viewType, boolean isScroll) {
        addModels(listModels, 0, listModels.size() - 1, viewType, isScroll);
    }

    public <T> void addModels(List<T> listModels, int fromIndex, int toIndex, int viewType, boolean isScroll) {
        if (fromIndex < 0 || fromIndex >= listModels.size() ||
                toIndex < 0 || toIndex >= listModels.size() ||
                fromIndex > toIndex) {
            return;
        }
        for (int i = fromIndex; i <= toIndex; i++) {
            addModel(listModels.get(i), viewType, false, false);
        }
        notifyDataSetChanged();
        if (isScroll) {
            getRecyclerView().scrollToPosition(listWrapperModels.size() - 1);
        }
    }

    public void addModel(Object model, boolean isScroll) {
        addModel(listWrapperModels.size(), model, isScroll);
    }

    public void addModel(Object model, boolean isScroll, boolean isUpdate) {
        addModel(listWrapperModels.size(), model, isScroll, isUpdate);
    }

    public void addModel(Object model, int viewType, boolean isScroll) {
        addModel(listWrapperModels.size(), model, viewType, isScroll);
    }

    public void addModel(Object model, int viewType, boolean isScroll, boolean isUpdate) {
        addModel(listWrapperModels.size(), model, viewType, isScroll, isUpdate);
    }

    public void addModel(int index, Object model, boolean isScroll) {
        addModel(index, model, VIEW_TYPE_NORMAL, isScroll, true);
    }

    public void addModel(int index, Object model, boolean isScroll, boolean isUpdate) {
        addModel(index, model, VIEW_TYPE_NORMAL, isScroll, isUpdate);
    }

    public void addModel(int index, Object model, int viewType, boolean isScroll) {
        addModel(index, model, viewType, isScroll, true);
    }

    public void addModel(int index, Object model, int viewType, boolean isScroll, boolean isUpdate) {
        ModelWrapper modelWrapper = new ModelWrapper(model, viewType);
        this.listWrapperModels.add(index, modelWrapper);
        this.listBackupWrapperModels.add(index, modelWrapper);
        if (selectedMode) {
            listSelectedItems.add(index, false);
        }
        if (isUpdate) {
            notifyItemInserted(index);
        }
        if (isScroll) {
            getRecyclerView().scrollToPosition(index);
        }
    }

    public void removeModel(int index) {
        this.listWrapperModels.remove(index);
        this.listBackupWrapperModels.remove(index);
        notifyItemRemoved(index);
    }

    public void setSelectedItem(int position, boolean isSelected) {
        if (selectedMode && position >= 0 && position < listSelectedItems.size()) {
            listSelectedItems.set(position, isSelected);
            setSelectedItemBackground(getViewHolder(position).itemView,
                    listWrapperModels.get(position).viewType,
                    isSelected);
        }
    }

    private RecyclerView.ViewHolder getViewHolder(int position) {
        return mRecyclerView.findViewHolderForAdapterPosition(position);
    }

    public boolean isItemSelected(int position) {
        if (selectedMode && position >= 0 && position < listSelectedItems.size()) {
            return listSelectedItems.get(position);
        }
        return false;
    }

    public LayoutInflater getInflater() {
        return mInflater;
    }

    public <T> T getItem(int position, Class<T> classType) {
        return classType.cast(listWrapperModels.get(position).model);
    }

    @Override
    public int getItemViewType(int position) {
        return listWrapperModels.get(position).viewType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        final RecyclerView.ViewHolder viewHolder = solvedOnCreateViewHolder(parent, viewType);
        setClickStateBackground(viewHolder.itemView, viewType, false);
        viewHolder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        setClickStateBackground(view, viewType, true);
                    }
                    break;

                    case MotionEvent.ACTION_CANCEL: {
                        setClickStateBackground(view, viewType, false);
                    }
                    break;

                    case MotionEvent.ACTION_UP: {
                        int itemPosition = getItemPosition(view);
                        setClickStateBackground(view, viewType, false);
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(RecyclerViewAdapter.this, view, viewType, itemPosition);
                        }
                    }
                    break;

                    default: {
                        break;
                    }
                }
                return false;
            }
        });
        return viewHolder;
    }

    private int getItemPosition(View view) {
        return mRecyclerView.getChildLayoutPosition(view);
    }

    protected RecyclerView.ViewHolder solvedOnCreateViewHolder(ViewGroup parent, int viewType) {
        return initNormalViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = listWrapperModels.get(position).viewType;
        if (selectedMode) {
            setSelectedItemBackground(holder.itemView,
                    viewType,
                    isItemSelected(position));
        }
        solvedOnBindViewHolder(holder, viewType, position);
    }

    protected void solvedOnBindViewHolder(RecyclerView.ViewHolder viewHolder, int viewType, int position) {
        bindNormalViewHolder((NormalViewHolder) viewHolder, position);
    }

    protected abstract RecyclerView.ViewHolder initNormalViewHolder(ViewGroup parent);

    protected abstract void bindNormalViewHolder(NormalViewHolder holder, int position);

    protected void setSelectedItemBackground(View view, int viewType, boolean isSelected) {

    }

    protected void setClickStateBackground(View view, int viewType, boolean isPress) {

    }

    @Override
    public int getItemCount() {
        return listWrapperModels.size();
    }

    public interface OnItemClickListener {
        void onItemClick(RecyclerView.Adapter adapter, View view, int viewType, int position);
    }

    public static class NormalViewHolder extends RecyclerView.ViewHolder {
        public NormalViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class ModelWrapper {
        Object model;
        int viewType;

        ModelWrapper(Object model, int viewType) {
            this.model = model;
            this.viewType = viewType;
        }
    }
}