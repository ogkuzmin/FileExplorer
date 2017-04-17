package com.devnull.fileexplorer.workers;


import android.content.Context;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ViewGroup;

import com.devnull.fileexplorer.CommonUtils;
import com.devnull.fileexplorer.interfaces.OnBackPressedListener;
import com.devnull.fileexplorer.interfaces.OnItemRowClickListener;
import com.devnull.fileexplorer.ui.ItemRow;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;

/**
 * Created by devnull on 25.03.2016.
 */
public class ItemListController extends Observable implements OnItemRowClickListener {

    private static final String TAG = ItemListController.class.getSimpleName();

    private final List<ItemRow> rowList = new ArrayList<ItemRow>();

    private File            currentDir;
    private Context         context;
    private File[]          dirArray;
    private File[]          fileArray;


    public ItemListController(Context context){
        this.context = context;
    }

    public void setCurrentDir(@Nullable File file){
        this.currentDir = file;
    }

    public void fillData(){

        dirArray = null;
        fileArray = null;

        if (currentDir != null)
            fillDirAndFileList();
        else
            fillRootDirRowList();
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

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        for (ItemRow row: rowList)
            row.setOnBackPressedListener(onBackPressedListener);
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
        rowList.clear();

        if (currentDir != null) {
            ItemRow parentRow = createAndSetUpItemRow(context, currentDir, true, this);
            rowList.add(parentRow);
        }
        if (dirArray != null) {
            for (File dir : dirArray) {
                if (dir != null) {
                    ItemRow tmp = createAndSetUpItemRow(context, dir, false, this);
                    rowList.add(tmp);
                }
            }
        }
        if (fileArray != null) {
            for (File file : fileArray) {
                if (file != null) {
                    ItemRow tmp = createAndSetUpItemRow(context, file, false, this);
                    rowList.add(tmp);
                }
            }
        }
    }

    private ItemRow createAndSetUpItemRow(Context context, File file, boolean isParent,
                                          OnItemRowClickListener headListener) {
        ItemRow row = new ItemRow(context);
        row.setItemFile(file);

        if (isParent) {
            row.setParentBoolean();
        }

        row.initRow();
        row.registerHeadListener(headListener);

        return row;
    }

    public void addViewsToContainer(ViewGroup container){

        container.removeAllViews();

        for (ItemRow row: rowList)
            container.addView(row);
    }

    @Override
    public void onItemRowClick(File file) {

        if (file.isDirectory()) {
            updateData(file);
        }

        setChanged();
        notifyObservers(file);
    }

    public void updateData(@Nullable File file){
        currentDir = file;
        fillData();
    }
}
