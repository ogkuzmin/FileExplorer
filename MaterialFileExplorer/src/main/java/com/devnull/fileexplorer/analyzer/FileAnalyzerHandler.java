package com.devnull.fileexplorer.analyzer;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import java.io.File;

import javax.inject.Inject;


/**
 * Created by devnull on 05.11.16.
 */
class FileAnalyzerHandler extends Handler {

    public static final int ANALYZE_FILE = 1;

    public static final String FILE_PATH_KEY = "FILE_PATH";


    private static FileAnalyzerHandler sInstance;

    private static final String TAG = FileAnalyzerHandler.class.getSimpleName();
    private static Looper looper;

    static {
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        looper = handlerThread.getLooper();
    }

    public static FileAnalyzerHandler getInstance() {
        if (sInstance == null) {
            sInstance = new FileAnalyzerHandler();
        }
        return sInstance;
    }

    private FileAnalyzerHandler() {
        super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case ANALYZE_FILE:
                analyzeFile((String) msg.obj);
        }
    }

    public void analyzeFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists())
            throw new IllegalArgumentException("No such file! " + filePath);

    }
}
