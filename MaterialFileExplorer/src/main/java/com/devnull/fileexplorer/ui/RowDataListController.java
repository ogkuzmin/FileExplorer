package com.devnull.fileexplorer.ui;


import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

import com.devnull.fileexplorer.CommonUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;

/**
 * Created by devnull on 25.03.2016.
 */
public class RowDataListController extends Observable implements ItemRow.OnItemRowClickListener {

    private static final String TAG = RowDataListController.class.getSimpleName();

    private File            currentDir;
    private List<RowData>   rowDataList;
    private Context         context;
    private File[]          dirArray;
    private File[]          fileArray;
    private Activity        hostActivity;


    public RowDataListController(Context context){

        this.context = context;
        rowDataList = new ArrayList<RowData>();
    }
    public void setCurrentDir(@Nullable File file){
        this.currentDir = file;
        fillData();
    }
    private void fillData(){

        dirArray = null;
        fileArray = null;

        if (currentDir != null) {
            //standard case. Fills data for selected currentDir.
            fillDirAndFileList();
        } else {
            //case for first app screen with root dir, sdcard
            fillRootDirRowList();
        }
    }
    public Context getContext() {
        return context;
    }
    public List<RowData> getRowDataList() {
        return rowDataList;
    }
    public void setHostActivity(Activity hostActivity){
        this.hostActivity = hostActivity;
    }
    private void fillDirAndFileList(){

        if (currentDir != null) {

            dirArray = currentDir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isDirectory();
                }
            });

            fileArray = currentDir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return !file.isDirectory();
                }
            });

            Arrays.sort(dirArray);
            Arrays.sort(fileArray);

            fillItemRowList();
        }
    }
    private void fillRootDirRowList(){

        if (currentDir == null) {

            dirArray = new File[2];
            dirArray[0] = new File("/");

            try {
                if (CommonUtils.isExtStorageReadable())
                    dirArray[1] = new File(Environment.getExternalStorageDirectory().getAbsolutePath());

            } catch (Exception e) {

                Log.e(TAG, "Exception in fillRootDirRowList() is " + e.getMessage());
            }
            fillItemRowList();
        }
    }

    private void fillItemRowList(){

        Log.i(TAG, "starting fillItemRowList()");

        rowDataList.clear();

        if (currentDir != null){
            RowData parentRowData = createRowData(currentDir, true, this);
            rowDataList.add(parentRowData);
        }
        if (dirArray != null) {
            for (File dir : dirArray) {
                if (dir != null) {
                    RowData tmp = createRowData(dir, false, this);
                    rowDataList.add(tmp);
                }
            }
        }
        if (fileArray != null) {
            for (File file : fileArray) {
                if (file != null) {
                    RowData tmp = createRowData(file, false, this);

                    rowDataList.add(tmp);
                }
            }
        }
    }
    /**
     * Method for creating new RowData.
     *
     * @param file set file which would represent this row.
     * @param isParent show is this row represents parent row of directory of files.
     *                 Uses for returning to parent directory.
     * @param headListener object that used for callback OnItemRowClickListener
     * @return new RowData.
     */
    private RowData createRowData(File file, boolean isParent, ItemRow.OnItemRowClickListener headListener){
        RowData rowData = new RowData();
        rowData.setItemFile(file);
        rowData.setParentBoolean(isParent);
        rowData.registerHeadListener(headListener);
        rowData.setHostActivity(hostActivity);
        return rowData;
    }
    @Override
    public void onItemRowClick(File file) {
        if (file.isDirectory()) {
            setCurrentDir(file);
        }
        setChanged();
        notifyObservers(file);
    }
}
