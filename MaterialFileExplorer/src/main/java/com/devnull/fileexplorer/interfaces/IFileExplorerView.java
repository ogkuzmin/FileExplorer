package com.devnull.fileexplorer.interfaces;

import android.view.View;

import com.devnull.fileexplorer.ui.FileRowModel;
import com.hannesdorfmann.mosby3.mvp.MvpView;

import java.util.List;

/**
 * Created by oleg on 22.05.17.
 */

public interface IFileExplorerView extends MvpView {

    void showFileRowsList(List<FileRowModel> rows);

    void showTitle(String title);

    void showLoading();

    void processRowEvent(FileRowModel row);

    View.OnClickListener getClickController();

}
