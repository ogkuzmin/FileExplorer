package com.devnull.fileexplorer.ui;

import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.devnull.fileexplorer.R;

public class FileExplorerActivity extends AppCompatActivity {

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
            getSupportFragmentManager().beginTransaction().add(R.id.container_for_fragment,
                    explorerFragment).addToBackStack(null).commit();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkFileReadingPermission();
        }
    }
    public void setToolbarTitle(String title){
        toolbar.setTitleTextColor(getResources().getColor(R.color.background));
        getSupportActionBar().setTitle(title);

        if (!title.equalsIgnoreCase(getResources().getString(R.string.choose_dir)))
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        else
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
    /*@Override
    public void onFileEvent(@Nullable File file) {
        if (file == null) {
            setToolbarTitle();
        } else {
            if (file.isDirectory())
                setToolbarTitle();
            else
                showConfirmDialog(file);
        }
    }*/

    @Override
    public void onBackPressed() {
        explorerFragment.onBackPressed();
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
