package com.devnull.fileexplorer.interfaces;

import java.io.File;

public interface IFileTypeCacheRepo {
    void saveFileInfo(File file);
    void updateFileInfo(File file);
    void removeFileInfo(File file);
    boolean doesRepoContainsFile(File file);
    int getCommonFileType(File file);
}
