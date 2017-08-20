package com.devnull.fileexplorer.di;

import com.devnull.fileexplorer.analyzer.FileAnalyzerHelper;
import com.devnull.fileexplorer.interfaces.IFileAnalyzerController;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class FileAnalyzerModule {
    @Provides
    @Singleton
    IFileAnalyzerController provideFileAnalyzerController() {
        return new FileAnalyzerHelper();
    }
}
