package com.devnull.fileexplorer.interfaces;

import com.devnull.fileexplorer.ui.FileRowModel;

/**
 * Encapsulates UI controller to create query to analyze file.
 */

public interface IFileAnalyzerController {

    void startAsyncQueryToAnalyzeFile(FileRowModel fileRowModel);
}
