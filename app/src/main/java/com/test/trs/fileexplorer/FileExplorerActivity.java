package com.test.trs.fileexplorer;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.File;

public class FileExplorerActivity extends AppCompatActivity implements ExplorerFragment.FileExplorerListener {

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
            //explorerFragment.setCurrentDir(new File("/"));
            explorerFragment.registerFileExplorerListener(this);

            getSupportFragmentManager().beginTransaction().add(R.id.container, explorerFragment).addToBackStack(null).commit();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(explorerFragment.getToolbarTitle());
    }



    @Override
    public void onEventInFileExplorer(@Nullable File file) {

        if (file == null)
            toolbar.setTitle(explorerFragment.getToolbarTitle());
        else {
            if (file.isDirectory())
                toolbar.setTitle(explorerFragment.getToolbarTitle());
            else
                showConfirmDialog(file);
        }

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
