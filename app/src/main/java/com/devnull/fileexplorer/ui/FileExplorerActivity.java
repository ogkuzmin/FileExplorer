package com.devnull.fileexplorer.ui;

import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.devnull.fileexplorer.R;
import com.devnull.fileexplorer.ui.ExplorerFragment;

import java.io.File;

public class FileExplorerActivity extends AppCompatActivity implements ExplorerFragment.FileEventListener {

    ExplorerFragment    explorerFragment;
    LinearLayout        container;
    Toolbar             toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

            getSupportFragmentManager().beginTransaction().add(R.id.container, explorerFragment).addToBackStack(null).commit();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();

        updateToolbar();
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

        if (file == null)
            updateToolbar();
        else {
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
}
