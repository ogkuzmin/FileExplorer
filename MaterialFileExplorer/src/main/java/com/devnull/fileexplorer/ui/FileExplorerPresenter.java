package com.devnull.fileexplorer.ui;


import android.content.Context;
import android.os.Environment;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.devnull.fileexplorer.CommonUtils;
import com.devnull.fileexplorer.R;
import com.devnull.fileexplorer.interfaces.IFileExplorerView;
import com.devnull.fileexplorer.interfaces.IFileExplorerPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpFragment;

import java.io.File;
import java.io.FileFilter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by devnull on 25.03.2016.
 */
public class FileExplorerPresenter implements IFileExplorerPresenter {

    private static final String LOG_TAG = FileExplorerPresenter.class.getSimpleName();
    private static final int CHOOSE_DIR_STRING_CODE = R.string.choose_dir;
    private static final int EXTERNAL_STORAGE_STRING_CODE = FileRowModel.EXTERNAL_STORAGE_STRING_CODE;
    private static final int ROOT_DIRECTORY_STRING_CODE = FileRowModel.ROOT_DIRECTORY_STRING_CODE;

    private WeakReference<IFileExplorerView> mViewReference;
    private final HostFileModel mFileModel;
    private final PresentationModelTransformer mModelTransformer;

    private final FileFilter mDirFilter;
    private final FileFilter mFileFilter;
    private File[]           dirArray;
    private File[]           fileArray;

    public FileExplorerPresenter(){
        mFileModel = HostFileModel.getInstance();
        mModelTransformer = new PresentationModelTransformer();
        mDirFilter = (file) -> file.isDirectory();
        mFileFilter = (file) -> !file.isDirectory();
    }
    @UiThread
    @Override
    public void loadData() {
        Log.d(LOG_TAG, "::loadData()");

        if (isViewAttached()) {
            mViewReference.get().showLoading();
            getTitle().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSuccess(string -> mViewReference.get().showTitle(string))
                    .subscribe();

            loadFilesByModelAndConvertThem().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(rowList -> postDataByView(rowList));
        }
    }
    @UiThread
    @Override
    public void onRowEvent(FileRowModel row) {
        Log.d(LOG_TAG, "::onRowEvent() with row " + row.getItemFile().getAbsolutePath());

            if (row.isParentDir()) {
                onBackPressed();
                return;
            }

            switch (row.getItemCode()) {
                case FileRowModel.ROOT_DIRECTORY_CODE:
                    updateFileModelAndLoadData(new File("/"));
                    break;
                case FileRowModel.EXTERNAL_STORAGE_CODE:
                    if (CommonUtils.isExtStorageReadable())
                        updateFileModelAndLoadData(Environment.getExternalStorageDirectory());
                    break;
                case FileRowModel.DIRECTORY_CODE:
                    if (row.isReadable())
                        updateFileModelAndLoadData(row.getItemFile());
                    break;
                case FileRowModel.FILE_CODE:
                    updateFileModelAndLoadData(row.getItemFile());
                    break;
            }
    }

    @UiThread
    @Override
    public void attachView(IFileExplorerView view) {
        if (mViewReference != null) {
            mViewReference.clear();
        }
        mViewReference = new WeakReference<IFileExplorerView>(view);
    }
    @UiThread
    @Override
    public void detachView(boolean retainInstance) {
        if (mViewReference != null) {
            mViewReference.clear();
        }
        mViewReference = null;
    }
    @UiThread
    @Override
    public boolean isViewAttached() {
        return mViewReference != null && mViewReference.get() != null;
    }
    @WorkerThread
    private Single<List<FileRowModel>> loadFilesByModelAndConvertThem() {
        final File hostFile = mFileModel.getHostFile();
        final List<File> filesList;
        boolean isFirstScreen = false;

        if (hostFile == null) {
            filesList = getFilesForFirstScreen();
        } else {
            isFirstScreen = true;
            filesList = getChildDirsAndFilesByParent(hostFile);
        }

        List<FileRowModel> filesRowModelList = mModelTransformer
                .transformFileList(filesList, isFirstScreen);

        return Single.just(filesRowModelList);

    }
    @UiThread
    private void postDataByView(List<FileRowModel> rows) {
        Log.d(LOG_TAG, "postDataByView(): post list by size " + rows.size());
        if (isViewAttached()) {
            mViewReference.get().showFileRowsList(rows);
        }
    }
    private List<File> getFilesForFirstScreen() {
        List<File> files = new ArrayList<File>();
        files.add(new File("/"));

        if (CommonUtils.isExtStorageReadable()) {
            files.add(new File(Environment.getExternalStorageDirectory().getAbsolutePath()));
        }

        return files;
    }
    private List<File> getChildDirsAndFilesByParent(File parent) {
        List<File> files = new ArrayList<File>();
        files.add(parent);

        File[] childDirs = parent.listFiles(mDirFilter);
        File[] childFiles = parent.listFiles(mFileFilter);

        if (childDirs != null) {
            Arrays.sort(childDirs);
            files.addAll(Arrays.asList(childDirs));
            childDirs = null;
        }
        if (childFiles != null) {
            Arrays.sort(childFiles);
            files.addAll(Arrays.asList(childFiles));
            childFiles = null;
        }

        return files;
    }
    private Single<String> getTitle() {
        final File hostFile = mFileModel.getHostFile();
        final String title;
        final Context context = ((Fragment) mViewReference.get()).getContext();

        if (hostFile == null) {
            title = context.getResources().getString(CHOOSE_DIR_STRING_CODE);
        } else if (hostFile.getAbsolutePath().equalsIgnoreCase(
                Environment.getExternalStorageDirectory().getAbsolutePath())) {
            title = context.getResources().getString(EXTERNAL_STORAGE_STRING_CODE);
        } else if (hostFile.getAbsolutePath().equalsIgnoreCase("/")) {
            title = context.getResources().getString(ROOT_DIRECTORY_STRING_CODE);
        } else {
            title = hostFile.getName();
        }

        Log.d(LOG_TAG, "getTitle(): return title " + title);

        return Single.just(title);
    }
    public void onBackPressed() {
        Log.d(LOG_TAG, "onBackPressed()");

        File hostFile = mFileModel.getHostFile();
        if (hostFile != null) {
            File newHostFile = null;
            String curPath = hostFile.getAbsolutePath();

            if (!(curPath.equalsIgnoreCase("/")
                    || curPath.equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath())
                    /*|| curPath.equalsIgnoreCase(getActivity().getFilesDir().getAbsolutePath())*/)) {
                String parentPath = hostFile.getParent();
                if (parentPath != null)
                    newHostFile = new File(parentPath);
            }
            updateFileModelAndLoadData(newHostFile);
        } else {
            mViewReference.get().closeApp();
        }
    }
    private void updateFileModelAndLoadData(File newHostFile) {
        mFileModel.setHostFile(newHostFile);
        loadData();
    }
}
