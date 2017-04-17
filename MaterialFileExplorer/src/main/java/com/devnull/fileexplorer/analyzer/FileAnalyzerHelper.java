package com.devnull.fileexplorer.analyzer;

import android.os.Handler;

import com.devnull.fileexplorer.interfaces.FileAnalyzerController;

/**
 * Created by oleg on 17.04.17.
 */

public class FileAnalyzerHelper implements FileAnalyzerController {

    private Handler mAnalyzeHandler;

    public FileAnalyzerHelper() {
        mAnalyzeHandler = FileAnalyzerHandler.getInstance();
    }

    @Override
    public void startAsyncQueryToAnalyzeFile(String filePath) {
        mAnalyzeHandler.obtainMessage(FileAnalyzerHandler.ANALYZE_FILE, filePath);
    }
}
