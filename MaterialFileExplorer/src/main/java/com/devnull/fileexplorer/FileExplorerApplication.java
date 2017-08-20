package com.devnull.fileexplorer;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class FileExplorerApplication extends Application {

    private static FileExplorerApplication sApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    public FileExplorerApplication getInstance() {
        return sApplication;
    }


}
