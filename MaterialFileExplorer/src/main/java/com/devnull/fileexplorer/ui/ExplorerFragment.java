package com.devnull.fileexplorer.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.devnull.fileexplorer.analyzer.FileAnalyzerHelper;
import com.devnull.fileexplorer.interfaces.IFileAnalyzerController;
import com.devnull.fileexplorer.R;
import com.devnull.fileexplorer.interfaces.IFileExplorerPresenter;
import com.devnull.fileexplorer.interfaces.IFileExplorerView;
import com.hannesdorfmann.mosby3.mvp.MvpFragment;

import java.lang.ref.WeakReference;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by devnull on 25.03.2016.
 */

public class ExplorerFragment extends MvpFragment<IFileExplorerView, IFileExplorerPresenter> implements IFileExplorerView {

    private static final String LOG_TAG = ExplorerFragment.class.getSimpleName();

    private WeakReference<FileExplorerActivity> mFileExplorerActivityReference;
    private RelativeLayout          rootView;
    private RecyclerView            mRecyclerView;
    private RecyclerViewAdapter     mAdapter;
    private ProgressBar             mLoadingProgressBar;

    private RowItemClickListener mItemClickListener = new RowItemClickListener();

    public ExplorerFragment()
    {
        super();
        Log.d(LOG_TAG, "create instance");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mFileExplorerActivityReference = new WeakReference<FileExplorerActivity>((FileExplorerActivity) getActivity());
        Log.d(LOG_TAG, "::onAttach()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFileExplorerActivityReference.clear();
        mFileExplorerActivityReference = null;
        Log.d(LOG_TAG, "::onDetach()");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = (RelativeLayout) inflater.inflate(R.layout.explorer_fragment_layout, container, false);

        Log.d(LOG_TAG, "::onCreateView()");

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLoadingProgressBar = (ProgressBar) rootView.findViewById(R.id.loading_progress_bar);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.container_for_content);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mAdapter = new RecyclerViewAdapter();
        mAdapter.setOnClickListener(mItemClickListener);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(mAdapter);

        presenter.loadData();

        Log.d(LOG_TAG, "::onViewCreated()");
    }
    public IFileExplorerPresenter createPresenter() {
        return new FileExplorerPresenter();
    }
    @Override
    public void showFileRowsList(List<FileRowModel> rows) {
        Log.d(LOG_TAG, "::showFileRowsList() with list size " + rows.size());
        mLoadingProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mAdapter.setDataList(rows);
        mAdapter.notifyDataSetChanged();
    }
    @Override
    public void showTitle(String title) {
        Log.d(LOG_TAG, "::showTitle() with title " + title);
        if (mFileExplorerActivityReference != null && mFileExplorerActivityReference.get() != null) {
            mFileExplorerActivityReference.get().setToolbarTitle(title);
        }
    }
    @Override
    public void showLoading() {
        Log.d(LOG_TAG, "::showLoading()");
        mLoadingProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }
    @Override
    public void processRowEvent(FileRowModel row) {
        Log.d(LOG_TAG, "::processRowEvent()");
        getPresenter().onRowEvent(row);
    }
    public void onBackPressed() {
        getPresenter().onBackPressed();
    }

    @Override
    public void closeApp() {
        final AlertDialog.Builder closeDialog = new AlertDialog.Builder(getContext());
        closeDialog.setCancelable(true);
        closeDialog.setMessage(R.string.exit_app_question);
        closeDialog.setNegativeButton(R.string.negative_button_for_close_dialog, (di, which) -> di.dismiss());
        closeDialog.setPositiveButton(R.string.positive_button_for_close_dialog, (di, which) -> getActivity().finish());
        closeDialog.show();
    }


    private class RowItemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.d(RowItemClickListener.class.getSimpleName(), "::onClick()");
            FileRowModel fileRowModel = ((ItemRow) v).getItemData();
            ExplorerFragment.this.processRowEvent(fileRowModel);
        }
    }
}
