package com.devnull.fileexplorer.ui;

import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.devnull.fileexplorer.R;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

public class FileExplorerActivity extends AppCompatActivity implements Observer{

    private static final String TAG = FileExplorerActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private Toolbar mToolbar;

    private File currentDir;
    private RowDataListController   rowDataListController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_explorer_activity_layout);

        if (mToolbar == null){
            mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
            setSupportActionBar(mToolbar);
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        initAndSetupRowDataListController();
        mRecyclerView = (RecyclerView) findViewById(R.id.container_for_content);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        mAdapter = new RecyclerViewAdapter(rowDataListController);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(mAdapter);

    }
    @Override
    protected void onStart() {
        super.onStart();
        updateToolbar();
    }
    private void initAndSetupRowDataListController() {
        if (rowDataListController == null) {
            rowDataListController = new RowDataListController(this);
            rowDataListController.setHostActivity(this);
            rowDataListController.addObserver(this);
        }
        rowDataListController.setCurrentDir(currentDir);
    }
    private void updateToolbar(){
        mToolbar.setTitleTextColor(getResources().getColor(R.color.background));
        String toolbarTitle = getToolbarTitle();
        getSupportActionBar().setTitle(toolbarTitle);

        if (!toolbarTitle.equalsIgnoreCase(getResources().getString(R.string.choose_dir)))
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        else
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
    private String getToolbarTitle() {
        if (currentDir == null)
            return getResources().getString(R.string.choose_dir);
        else if (currentDir.getAbsolutePath().equalsIgnoreCase(
                Environment.getExternalStorageDirectory().getAbsolutePath()))
            return getResources().getString(RowData.EXTERNAL_STORAGE_STRING_CODE);
        else if (currentDir.getAbsolutePath().equalsIgnoreCase("/"))
            return getResources().getString(RowData.ROOT_DIRECTORY_STRING_CODE);
        else
            return currentDir.getName();
    }
    public void onFileEvent(@Nullable File file) {
        if (file == null || file.isDirectory()) {
            setCurrentDir(file);
            rowDataListController.setCurrentDir(file);
            mAdapter.notifyDataSetChanged();
            updateToolbar();
        } else
            showConfirmDialog(file);
    }
    public void onLongFileEvent(File file, int eventCode) {

    }
    public void showConfirmDialog(File file){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

    }
    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed()");


        if (currentDir == null)
            Log.d(TAG, "onBackPressed(): currentDir == null");
        else
            Log.d(TAG, "onBackPressed(): with currentDir path " + currentDir.getAbsolutePath());

        if (currentDir != null) {
            File newCurrentDir = null;
            String curPath = currentDir.getAbsolutePath();

            if (!(curPath.equalsIgnoreCase("/")
                    || curPath.equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath())
                    || curPath.equalsIgnoreCase(getFilesDir().getAbsolutePath()))) {
                String parentPath = currentDir.getParent();
                if (parentPath != null)
                    newCurrentDir = new File(parentPath);
            }
            onFileEvent(newCurrentDir);
        }
    }
    @Override
    public void update(Observable observable, Object data) {
        Log.i(TAG, "Observer get update message");

        onFileEvent((File) data);
    }
    private void setCurrentDir(File file) {
        currentDir = file;
    }
}
