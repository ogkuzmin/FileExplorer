package com.devnull.fileexplorer.ui;

import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.devnull.fileexplorer.R;
import com.devnull.fileexplorer.analyzer.FileTypeCollection.CommonType;

import java.io.File;

/**
 * Created by devnull on 27.11.16.
 */

public class FileRowModel {

    private static final String TAG = FileRowModel.class.getSimpleName();

    public static final int EXTERNAL_STORAGE_CODE = 4;
    public static final int EXTERNAL_STORAGE_STRING_CODE = R.string.ext_storage;

    public static final int ROOT_DIRECTORY_CODE = 3;
    public static final int ROOT_DIRECTORY_STRING_CODE = R.string.root_directory;

    public static final int DIRECTORY_CODE = 2;
    public static final int DIRECTORY_STRING_CODE = R.string.directory;

    public static final int FILE_CODE = 1;
    public static final int FILE_STRING_CODE = R.string.file;

    public static final int NO_SUCH_ICON = -1;

    private File        itemFile;
    private CommonType  fileType;


    private int         itemCode;
    private boolean     isParent = false;
    private boolean     isDir;
    private boolean     isReadable;

    public FileRowModel() {}

    public void setItemFile(File file) {
        itemFile = file;
        isReadable = itemFile.canRead();
        determineItemCode();
    }

    public void setParentBoolean(boolean isParent){
        this.isParent = isParent;
    }

    public File getItemFile() {
        return itemFile;
    }

    public CommonType getFileType() {
        return fileType;
    }

    public int getItemCode() {
        return itemCode;
    }

    public boolean isParentDir() {
        return isParent;
    }

    public boolean isDir() {
        return isDir;
    }

    public boolean isReadable() {
        return isReadable;
    }
    private void determineItemCode() {

        isDir = itemFile.isDirectory();

        if(isDir)
            isReadable = itemFile.canRead();

        if (isDir) {
            if (itemFile.getAbsolutePath().equalsIgnoreCase("/"))
                itemCode = ROOT_DIRECTORY_CODE;
            else if (itemFile.getAbsolutePath().equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath()))
                itemCode = EXTERNAL_STORAGE_CODE;
            else
                itemCode = DIRECTORY_CODE;
        } else
            itemCode = FILE_CODE;

        Log.i(TAG, "determineItemCode(): itemCode = " + itemCode);
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof FileRowModel)
            return this.itemFile.getAbsolutePath().equals(
                    ((FileRowModel) o).itemFile.getAbsolutePath());
        else
            return false;
    }
}
