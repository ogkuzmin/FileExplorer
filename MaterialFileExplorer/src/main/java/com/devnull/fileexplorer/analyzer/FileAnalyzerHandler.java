package com.devnull.fileexplorer.analyzer;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import java.io.File;

import com.devnull.fileexplorer.analyzer.FileTypeCollection.CommonType;

/**
 * Created by devnull on 05.11.16.
 */
public class FileAnalyzerHandler extends Handler{

    public static final int ANALYZE_FILE = 1;


    public static final String FILE_PATH_KEY = "FILE_PATH";

    private static final String TAG = FileAnalyzerHandler.class.getSimpleName();
    private static FileAnalyzerHandler sInstance;

    public static FileAnalyzerHandler getInstance() {
        if (sInstance == null) {
            sInstance = new FileAnalyzerHandler();
        }
        return sInstance;
    }

    private FileAnalyzerHandler() {
        super(new HandlerThread(TAG).getLooper());
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
