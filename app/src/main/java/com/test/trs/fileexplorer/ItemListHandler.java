package com.test.trs.fileexplorer;


import android.content.Context;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by kuzmin_og on 25.03.2016.
 */
public class ItemListHandler extends Observable implements ItemRow.OnItemRowClickListener {

    private static final String TAG = "ItemListHandler";

    private File            currentDir;
    private List<ItemRow>   rowList;
    private Context         context;
    private List<String>    fileTypesList;
    private List<File>      dirList;
    private List<File>      fileList;




    public ItemListHandler(Context context){

        this.context = context;
        rowList = new ArrayList<ItemRow>();
        fileTypesList = new ArrayList<String>();
        dirList = new ArrayList<File>();
        fileList = new ArrayList<File>();

    }

    public void setCurrentDir(@Nullable File file){
        this.currentDir = file;
    }

    public void registerExtension(String ext){

        boolean alreadyRegistered = false;

        for (String regExt: fileTypesList){
            alreadyRegistered |= regExt.equalsIgnoreCase(ext);
        }

        if (!alreadyRegistered)
            fileTypesList.add(ext);
    }

    public void registerObserver(Observer observer){
        addObserver(observer);
    }



    public void fillData(){

        dirList.clear();
        fileList.clear();

        if (currentDir != null)
            fillDirAndFileList();
        else
            fillRootDirRowList();
    }

    private void fillDirAndFileList(){

        if (currentDir != null) {

            File files[] = currentDir.listFiles();

            dirList = new ArrayList<File>();
            fileList = new ArrayList<File>();

            for (File file : files) {

                if (file.isDirectory())
                    dirList.add(file);
                else {
                    if (isNeededFile(file))
                        fileList.add(file);

                }
            }

            fillItemRowList();
        }
    }


    private void fillRootDirRowList(){

        if (currentDir == null) {

            dirList.add(new File("/"));
            dirList.add(context.getFilesDir());

            try {
                String extStorageState = Environment.getExternalStorageState();

                if (extStorageState.equalsIgnoreCase(Environment.MEDIA_MOUNTED) || extStorageState.equalsIgnoreCase(Environment.MEDIA_MOUNTED_READ_ONLY))
                    dirList.add(new File(Environment.getExternalStorageDirectory().getAbsolutePath()));

            } catch (Exception e) {

                Log.e(TAG, "Exception in fillRootDirRowList() is " + e.getMessage());
            }

            fillItemRowList();
        }
    }

    private void fillItemRowList(){

        Log.i(TAG, "starting fillItemRowList()");

        rowList.clear();

        if (currentDir != null){
            ItemRow parentRow = createAndSetUpItemRow(context, currentDir, true, this);
            rowList.add(parentRow);
        }
        for (File dir: dirList) {
            ItemRow tmp = createAndSetUpItemRow(context, dir, false, this);
            rowList.add(tmp);
        }
        for (File file: fileList){
            ItemRow tmp = createAndSetUpItemRow(context, file, false, this);

            rowList.add(tmp);
        }
    }

    private ItemRow createAndSetUpItemRow(Context context, File file, boolean isParent, ItemRow.OnItemRowClickListener headListener){

        ItemRow row = new ItemRow(context);
        row.setItemFile(file);

        if (isParent)
        row.setParentBoolean();

        row.initRow();
        row.registerHeadListener(headListener);

        return row;
    }

    public void addViewsToContainer(ViewGroup container){

        container.removeAllViews();

        for (ItemRow row: rowList)
            container.addView(row);
    }


    private boolean isNeededFile(File file){

        boolean result = false;

        for (String registerExt: fileTypesList){
            String ext = ExplorerFragment.getFileExtension(file);

            result = (ext.equalsIgnoreCase(registerExt));
        }

        return result;
    }

    @Override
    public void onItemRowClick(File file) {

        Log.i(TAG, "Starting onItemRowClick() with file.getName() is " + file.getName());

        updateData(file);
        setChanged();
        notifyObservers(file);
    }

    public void updateData(@Nullable File file){

        currentDir = file;

        fillData();
    }
}
