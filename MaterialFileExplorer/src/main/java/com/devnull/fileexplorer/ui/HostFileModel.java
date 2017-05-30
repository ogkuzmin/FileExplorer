package com.devnull.fileexplorer.ui;

import android.support.annotation.Nullable;

import java.io.File;

/**
 * Class which represents current host file-directory.
 * Makes safe-thread update of file host;
 */

public class HostFileModel {
    //Lock object to synchronize work from any thread.
    private static final Object sLock = new Object();
    //Singleton instance.
    private static HostFileModel sInstance;
    //Represents current host file.
    private File mHostFile;

    private HostFileModel() {
        mHostFile = null;
    }

    public static HostFileModel getInstance() {
        if (sInstance == null) {
            sInstance = new HostFileModel();
        }
        return sInstance;
    }

    public void setHostFile(@Nullable File file) {
        synchronized (sLock) {
            ensureCorrectHost(file);
            mHostFile = file;
        }
    }
    @Nullable
    public File getHostFile() {
        synchronized (sLock) {
            return mHostFile;
        }
    }

    private void ensureCorrectHost(File file) {
        if (file == null) {
            //skip null case, because null host represents the start screen.
            return;
        }
        if (!file.exists() || !file.isDirectory()) {
            throw new IllegalArgumentException("Host file should be existed directory only!!!");
        }
    }
}
