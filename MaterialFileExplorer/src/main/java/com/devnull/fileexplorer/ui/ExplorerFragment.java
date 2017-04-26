package com.devnull.fileexplorer.ui;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.devnull.fileexplorer.analyzer.FileAnalyzerHelper;
import com.devnull.fileexplorer.interfaces.FileAnalyzerController;
import com.devnull.fileexplorer.interfaces.FileEventListener;
import com.devnull.fileexplorer.interfaces.OnBackPressedListener;
import com.devnull.fileexplorer.R;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by devnull on 25.03.2016.
 */
public class ExplorerFragment extends Fragment implements Observer, OnBackPressedListener {

    private static final String TAG = ExplorerFragment.class.getSimpleName();

    private RelativeLayout          rootView;
    private RecyclerView            mRecyclerView;
    private RecyclerViewAdapter     mAdapter;

    private FileEventListener       mActivityListener;
    private RowDataListController   mRowDataListController;
    private FileAnalyzerController  mFileAnalyzerController;

    private File                    currentDir;

    public ExplorerFragment()
    {
        super();
    }

    public void updateCurrentDir(File file){
        currentDir = file;
        mRowDataListController.setCurrentDir(currentDir);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = (RelativeLayout) inflater.inflate(R.layout.explorer_fragment_layout, container, false);

        initAndSetupRowDataListController();

        if (mFileAnalyzerController == null) {
            mFileAnalyzerController = new FileAnalyzerHelper();
        }

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.container_for_content);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mAdapter = new RecyclerViewAdapter(mRowDataListController);
        mRecyclerView.setLayoutManager(llm);

        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    private void initAndSetupRowDataListController() {
        if (mRowDataListController == null) {
            mRowDataListController = new RowDataListController(getActivity());
            mRowDataListController.setOnBackPressedListener(this);
            mRowDataListController.addObserver(this);
        }
        mRowDataListController.setCurrentDir(currentDir);
    }

    public void updateData(@Nullable File file) {

        if (file == null || file.isDirectory()) {
            updateCurrentDir(file);
            mAdapter.notifyDataSetChanged();
        }
        mActivityListener.onFileEvent(file);
    }

    @Override
    public void update(Observable observable, Object data) {
        updateData((File) data);
    }

    public String getToolbarTitle() {
        if (currentDir == null)
            return getResources().getString(R.string.choose_dir);
        else if (currentDir.getAbsolutePath().equalsIgnoreCase(
                Environment.getExternalStorageDirectory().getAbsolutePath()))
            return getResources().getString(RowData.EXTERNAL_STORAGE_STRING_CODE);
        else if (currentDir.getAbsolutePath().equalsIgnoreCase("/"))
            return getResources().getString(RowData.ROOT_DIRECTORY_STRING_CODE);
        else
            return currentDir.getName();
    }

    public void registerFileExplorerListener(FileEventListener activityListener) {
        this.mActivityListener = activityListener;
    }

    public void onBackPressed() {
        if (currentDir != null) {
            File newCurrentDir = null;
            String curPath = currentDir.getAbsolutePath();

            if (!(curPath.equalsIgnoreCase("/")
                    || curPath.equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath())
                    || curPath.equalsIgnoreCase(getActivity().getFilesDir().getAbsolutePath()))) {
                String parentPath = currentDir.getParent();
                if (parentPath != null)
                    newCurrentDir = new File(parentPath);
            }
            updateData(newCurrentDir);
        }
    }
}
