package com.devnull.fileexplorer.ui;

import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.devnull.fileexplorer.R;
import com.devnull.fileexplorer.interfaces.FileEventListener;

import java.io.File;

public class FileExplorerActivity extends AppCompatActivity implements FileEventListener {

    private ExplorerFragment    explorerFragment;
    private Toolbar             toolbar;
    private static final String READ_WRITE_EXT_STORAGE_PERMISSION = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int    READ_WRITE_EXT_STORAGE_PERMISSION_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_explorer_activity_layout);

        if (toolbar == null){
            toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
            setSupportActionBar(toolbar);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (explorerFragment == null){
            explorerFragment = new ExplorerFragment();
            explorerFragment.registerFileExplorerListener(this);

            getSupportFragmentManager().beginTransaction().add(R.id.container_for_fragment,
                    explorerFragment).addToBackStack(null).commit();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        updateToolbar();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkFileReadingPermission();
        }
    }
    public void updateToolbar(){
        toolbar.setTitleTextColor(getResources().getColor(R.color.background));
        String toolbarTitle = explorerFragment.getToolbarTitle();
        getSupportActionBar().setTitle(explorerFragment.getToolbarTitle());

        if (!toolbarTitle.equalsIgnoreCase(getResources().getString(R.string.choose_dir)))
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        else
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
    @Override
    public void onFileEvent(@Nullable File file) {
        if (file == null) {
            updateToolbar();
        } else {
            if (file.isDirectory())
                updateToolbar();
            else
                showConfirmDialog(file);
        }
    }

    @Override
    public void onLongFileEvent(File file, int eventCode) {

    }

    public void showConfirmDialog(File file){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

    }

    @Override
    public void onBackPressed() {

        if (explorerFragment != null){

            explorerFragment.onBackPressed();
        }
    }
    private void checkFileReadingPermission() {
        if (ContextCompat.checkSelfPermission(this, READ_WRITE_EXT_STORAGE_PERMISSION)
                == PackageManager.PERMISSION_DENIED) {

            if (shouldShowRequestPermissionRationale(READ_WRITE_EXT_STORAGE_PERMISSION)) {

            } else {
                requestPermissions(new String[]{READ_WRITE_EXT_STORAGE_PERMISSION},
                        READ_WRITE_EXT_STORAGE_PERMISSION_REQUEST);
            }
        }


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == READ_WRITE_EXT_STORAGE_PERMISSION_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // do nothing
            } else {
                showNoPermissionGranted();
            }
        }
    }
    private void showNoPermissionGranted() {

    }
}
