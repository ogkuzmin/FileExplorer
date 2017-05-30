package com.devnull.fileexplorer.interfaces;

import com.devnull.fileexplorer.ui.FileRowModel;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;

/**
 * Created by oleg on 23.05.17.
 */

public interface IFileExplorerPresenter extends MvpPresenter<IFileExplorerView> {

    void loadData();

    void onRowEvent(FileRowModel row);

    void onBackPressed();

    boolean isViewAttached();
}
