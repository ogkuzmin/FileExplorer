package com.devnull.fileexplorer.ui;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.devnull.fileexplorer.analyzer.FileAnalyzerHelper;
import com.devnull.fileexplorer.interfaces.FileAnalyzerController;
import com.devnull.fileexplorer.interfaces.FileEventListener;
import com.devnull.fileexplorer.interfaces.OnBackPressedListener;
import com.devnull.fileexplorer.workers.ItemListController;
import com.devnull.fileexplorer.R;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by devnull on 25.03.2016.
 */
public class ExplorerFragment extends Fragment implements Observer, OnBackPressedListener {

    private static final String TAG = ExplorerFragment.class.getSimpleName();

    private LinearLayout            rootView;
    private File                    currentDir;
    private FileEventListener       activityListener;
    private ItemListController      itemListController;
    private FileAnalyzerController  fileAnalyzerController;

    public ExplorerFragment()
    {
        super();
    }

    public void setCurrentDir(File file){
        currentDir = file;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = (LinearLayout) inflater.inflate(R.layout.explorer_fragment_layout, container, false);

        if (itemListController == null) {
            itemListController = new ItemListController(getActivity());
        }

        if (fileAnalyzerController == null) {
            fileAnalyzerController = new FileAnalyzerHelper();
        }

        fillDataAndAddViews(rootView);

        return rootView;
    }

    public void fillDataAndAddViews(ViewGroup container) {

        itemListController.setCurrentDir(currentDir);
        itemListController.addObserver(this);
        itemListController.fillData();
        itemListController.setOnBackPressedListener(this);
        itemListController.addViewsToContainer(container);
    }

    public void updateData(@Nullable File file) {

        if (file == null) {
            currentDir = null;
            fillDataAndAddViews(rootView);
        } else if (file.isDirectory()) {
            currentDir = file;
            fillDataAndAddViews(rootView);
            fileAnalyzerController.startAsyncQueryToAnalyzeFile(currentDir.getAbsolutePath());
        }
        activityListener.onFileEvent(file);
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
            return getResources().getString(ItemRow.EXTERNAL_STORAGE_STRING_CODE);
        else if (currentDir.getAbsolutePath().equalsIgnoreCase("/"))
            return getResources().getString(ItemRow.ROOT_DIRECTORY_STRING_CODE);
        else
            return currentDir.getName();
    }

    public void registerFileExplorerListener(FileEventListener activityListener) {
        this.activityListener = activityListener;
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
