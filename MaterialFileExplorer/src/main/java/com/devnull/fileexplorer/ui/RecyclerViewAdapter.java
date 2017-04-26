package com.devnull.fileexplorer.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devnull.fileexplorer.R;

import java.io.File;

/**
 * Created by devnull on 26.11.16.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private RowDataListController mRowController;

    public RecyclerViewAdapter(RowDataListController controller) {
        mRowController = controller;
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

        RowData previousData = holder.itemRow.getItemData();
        RowData actualData = mRowController.getRowDataList().get(position);

        if (holder.itemRow.getItemData() == null ||
                !(previousData.getItemFile().equals(actualData.getItemFile()))) {
            RowData rowData = mRowController.getRowDataList().get(position);
            holder.itemRow.setRowDataAndInitUi(rowData);
            processFileAnalyze(rowData.getItemFile());
        }
    }
    @Override
    public int getItemCount() {
        return mRowController.getRowDataList().size();
    }
    private void processFileAnalyze(File file) {

    }
    static class ViewHolder extends RecyclerView.ViewHolder{

        public ItemRow itemRow;

        public ViewHolder(View itemRow) {
            super(itemRow);
            this.itemRow = (ItemRow) itemRow;
        }
    }
}
