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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;
import java.util.TimeZone;

/**
 * Created by kuzmin_og on 25.03.2016.
 */
public class ExplorerFragment extends Fragment implements Observer, ItemRow.OnBackPressedListener {

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
        itemListHandler.registerExtension("apk");
        itemListHandler.registerExtension("txt");
        itemListHandler.setCurrentDir(currentDir);
        itemListHandler.addObserver(this);
        itemListHandler.fillData();
        itemListHandler.setOnBackPressedListener(ExplorerFragment.this);
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

        if (currentDir == null)
            return "Выберите каталог";
        else if (currentDir.getAbsolutePath().equalsIgnoreCase(getContext().getFilesDir().getAbsolutePath()))
            return ItemRow.INTERNAL_STORAGE_STRING;
        else if (currentDir.getAbsolutePath().equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath()))
            return ItemRow.EXTERNAL_STORAGE_STRING;
        else
            return currentDir.getName();

    }

    public void registerFileExplorerListener(FileExplorerListener activityListener){

        this.activityListener = activityListener;
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

    public static String getFileExtension(File file){

        String ext = "";

        if (file.isFile()) {
            String fileName = file.getName();

            if (fileName.lastIndexOf(".") > 0)
                ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        }

        return ext;
    }

    public static boolean isExtStorageReadable(){

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static final String getStringTimeFromLong(final long timeInMillis){

        final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy HH:mm");
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeInMillis);
        c.setTimeZone(TimeZone.getDefault());
        return format.format(c.getTime());
    }

    public static final String getStringSizeFileFromLong(final long fileSize){

        double sizeInMB = (double)fileSize/(1024*1024);
        double sizeInKB = (double)fileSize/1024;

        if (sizeInMB >= 1)
            return roundDoubleToDecimal(sizeInMB) + " MB";
        else if(sizeInKB >= 1)
            return roundDoubleToDecimal(sizeInKB)  + " KB";
        else
            return fileSize + " B";
    }

    private static double roundDoubleToDecimal(double d){

        d = d * 10;
        int i = (int)Math.round(d);
        d = (double)i/10;

        return d;
    }


}
