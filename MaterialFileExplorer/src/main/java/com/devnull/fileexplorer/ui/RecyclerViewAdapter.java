package com.devnull.fileexplorer.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devnull.fileexplorer.R;
import com.devnull.fileexplorer.di.DaggerFileAnalyzerComponent;
import com.devnull.fileexplorer.di.FileAnalyzerComponent;
import com.devnull.fileexplorer.interfaces.FileAnalyzerController;

import java.util.List;

import javax.inject.Inject;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    @Inject
    FileAnalyzerController mFileAnalyzerController;

    private FileAnalyzerComponent mFileAnalyzerComponent;

    private List<FileRowModel> mFileRowModelList;
    private View.OnClickListener mOnClickListener;

    public RecyclerViewAdapter() {
        mFileAnalyzerComponent = DaggerFileAnalyzerComponent.builder().build();
        mFileAnalyzerComponent.inject(this);
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mOnClickListener = listener;
    }

    public void setDataList(List<FileRowModel> fileRowModels) {
        mFileRowModelList = fileRowModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemRow = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.item_row_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemRow);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        FileRowModel previousData = holder.itemRow.getItemData();
        FileRowModel actualData = mFileRowModelList.get(position);

        if (holder.itemRow.getItemData() == null ||
                !(previousData.getItemFile().equals(actualData.getItemFile()))) {
            FileRowModel fileRowModel = mFileRowModelList.get(position);
            holder.itemRow.setRowDataAndInitUi(fileRowModel);
            holder.itemRow.setOnClickListener(mOnClickListener);
            processFileAnalyze(fileRowModel);
        }
    }
    @Override
    public int getItemCount() {
        if (mFileRowModelList != null) {
            return mFileRowModelList.size();
        } else {
            return 0;
        }
    }
    private void processFileAnalyze(FileRowModel rowModel) {
        mFileAnalyzerController.startAsyncQueryToAnalyzeFile(rowModel);
    }
    static class ViewHolder extends RecyclerView.ViewHolder {

        public ItemRow itemRow;

        public ViewHolder(View itemRow) {
            super(itemRow);
            this.itemRow = (ItemRow) itemRow;
        }
    }
}
