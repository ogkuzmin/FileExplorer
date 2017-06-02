package com.devnull.fileexplorer.ui;

import android.support.annotation.WorkerThread;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oleg on 24.05.17.
 */

public class PresentationModelTransformer {

    private static final String LOG_TAG = PresentationModelTransformer.class.getSimpleName();

    private static final int PARENT_INDEX = 0;

    public PresentationModelTransformer() {

    }

    @WorkerThread
    public List<FileRowModel> transformFileList(List<File> files, boolean isFirstScreen) {
        List<FileRowModel> rowModels = new ArrayList<FileRowModel>();

        boolean isParent;

        for (int i = 0; i < files.size(); i++) {
            isParent = false;
            if (isFirstScreen && i == PARENT_INDEX) {
                isParent = true;
            }
            FileRowModel rowModel = createRowData(files.get(i), isParent);
            rowModels.add(rowModel);
        }

        Log.d(LOG_TAG, "::transformFileList() returns list of row models by size " + rowModels.size());

        return rowModels;
    }
    /**
     * Method for creating new FileRowModel.
     *
     * @param file set file which would represent this row.
     * @param isParent show does this row represent parent row of directory of files.
     *                 Uses for returning to parent directory.
     * @return new FileRowModel.
     */
    private FileRowModel createRowData(File file, boolean isParent){
        FileRowModel fileRowModel = new FileRowModel();
        fileRowModel.setItemFile(file);
        fileRowModel.setParentBoolean(isParent);
        return fileRowModel;
    }
}
