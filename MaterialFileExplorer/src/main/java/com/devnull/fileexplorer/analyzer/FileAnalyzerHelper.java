package com.devnull.fileexplorer.analyzer;

import android.os.Handler;

import com.devnull.fileexplorer.interfaces.FileAnalyzerController;

import javax.inject.Inject;

/**
 * Created by oleg on 17.04.17.
 */

public class FileAnalyzerHelper implements FileAnalyzerController {

    private Handler mAnalyzeHandler;

    @Inject
    public FileAnalyzerHelper() {
        mAnalyzeHandler = FileAnalyzerHandler.getInstance();
    }

    @Override
    public void startAsyncQueryToAnalyzeFile(String filePath) {
        mAnalyzeHandler.obtainMessage(FileAnalyzerHandler.ANALYZE_FILE, filePath);
    }
}
