package com.devnull.fileexplorer.interfaces;

import android.support.annotation.Nullable;

import java.io.File;

/**
 * Created by oleg on 09.03.17.
 */
/**
 * Interface describes listener for click event on item rows.
 */
public interface OnItemRowClickListener {
    /**
     * The callback for event of clicked row.
     *
     * @param file the file which was represented by a clicked item row.
     */
    public void onItemRowClick(@Nullable File file);
}
