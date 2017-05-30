package com.devnull.fileexplorer.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.devnull.fileexplorer.analyzer.FileAnalyzerHelper;
import com.devnull.fileexplorer.interfaces.FileAnalyzerController;
import com.devnull.fileexplorer.R;
import com.devnull.fileexplorer.interfaces.IFileExplorerPresenter;
import com.devnull.fileexplorer.interfaces.IFileExplorerView;
import com.hannesdorfmann.mosby3.mvp.MvpFragment;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by devnull on 25.03.2016.
 */

public class ExplorerFragment extends MvpFragment<IFileExplorerView, IFileExplorerPresenter> {

    private static final String LOG_TAG = ExplorerFragment.class.getSimpleName();

    private WeakReference<FileExplorerActivity> mFileExplorerActivityReference;
    private RelativeLayout          rootView;
    private RecyclerView            mRecyclerView;
    private RecyclerViewAdapter     mAdapter;
    private ProgressBar             mLoadingProgressBar;

    private FileAnalyzerController  mFileAnalyzerController;
    private RowItemClickListener mItemClickListener = new RowItemClickListener();


    public ExplorerFragment()
    {
        super();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mFileExplorerActivityReference = new WeakReference<FileExplorerActivity>((FileExplorerActivity) getActivity());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFileExplorerActivityReference.clear();
        mFileExplorerActivityReference = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = (RelativeLayout) inflater.inflate(R.layout.explorer_fragment_layout, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mFileAnalyzerController == null) {
            mFileAnalyzerController = new FileAnalyzerHelper();
        }

        mLoadingProgressBar = (ProgressBar) rootView.findViewById(R.id.loading_progress_bar);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.container_for_content);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mAdapter = new RecyclerViewAdapter();
        mAdapter.setOnClickListener(mItemClickListener);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(mAdapter);
    }
    public IFileExplorerPresenter createPresenter() {
        return new FileExplorerPresenter();
    }
    public void showFileRowsList(List<FileRowModel> rows) {
        mLoadingProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mAdapter.setDataList(rows);
        mAdapter.notifyDataSetChanged();
    }
    public void showTitle(String title) {
        if (mFileExplorerActivityReference != null && mFileExplorerActivityReference.get() != null) {
            mFileExplorerActivityReference.get().setToolbarTitle(title);
        }
    }
    public void showLoading() {
        mLoadingProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }
    public void processRowEvent(FileRowModel row) {
        getPresenter().onRowEvent(row);
    };
    public View.OnClickListener getClickController() {
        return mItemClickListener;
    }
    public void onBackPressed() {
        getPresenter().onBackPressed();
    }

    private class RowItemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            FileRowModel fileRowModel = ((ItemRow) v).getItemData();
            ExplorerFragment.this.processRowEvent(fileRowModel);
        }
    }
}
