package com.test.trs.fileexplorer;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by kuzmin_og on 25.03.2016.
 */
public class ExplorerFragment extends Fragment implements Observer {

    private static final String TAG = "ExplorerFragment";

    private LinearLayout            rootView;
    private File                    currentDir;
    private FileExplorerListener    activityListener;
    private ItemListHandler         itemListHandler;

    public interface FileExplorerListener{

        public void onEventInFileExplorer(File file);
    }

    public ExplorerFragment()
    {
        super();
    }

    public void setCurrentDir(File file){

        currentDir = file;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        rootView = (LinearLayout) inflater.inflate(R.layout.explorer_fragment_layout, container, false);

        if (itemListHandler == null)
            itemListHandler = new ItemListHandler(getContext());

        fillDataAndAddViews(rootView);

        return rootView;
    }

    public void fillDataAndAddViews(ViewGroup container){

        itemListHandler.registerExtension(ItemRow.MAP_EXT);
        itemListHandler.setCurrentDir(currentDir);
        //itemListHandler.registerObserver(ExplorerFragment.this);
        itemListHandler.addObserver(this);
        itemListHandler.fillData();
        itemListHandler.addViewsToContainer(container);


    }

    public void updateData(@Nullable File file){

        if (file == null){

            currentDir = null;
            fillDataAndAddViews(rootView);
        }
        else if (file.isDirectory()) {
            currentDir = file;
            fillDataAndAddViews(rootView);
        }

        activityListener.onEventInFileExplorer(file);
    }

    @Override
    public void update(Observable observable, Object data) {

        Log.i(TAG, "Observer get update message");

        updateData((File) data);
    }

    public String getToolbarTitle(){

        if (currentDir != null)
            return currentDir.getName();
        else
            return "Выберите каталог";
    }

    public void registerFileExplorerListener(FileExplorerListener activityListener){

        this.activityListener = activityListener;
    }
    public static String getFileExtension(File file){

        String ext = "";

        if (file.isFile()) {
            String fileName = file.getName();

            if (fileName.lastIndexOf(".") > 0)
                ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        }

        return ext;
    }

    public void onBackPressed() {

        if (currentDir != null) {

            File newCurrentDir = null;

            String curPath = currentDir.getAbsolutePath();

            if (!(curPath.equalsIgnoreCase("/")
                    || curPath.equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath())
                    || curPath.equalsIgnoreCase(getContext().getFilesDir().getAbsolutePath()))) {


                String parentPath = currentDir.getParent();

                if (parentPath != null)
                    newCurrentDir = new File(parentPath);
            }

            updateData(newCurrentDir);
        }

    }

}
