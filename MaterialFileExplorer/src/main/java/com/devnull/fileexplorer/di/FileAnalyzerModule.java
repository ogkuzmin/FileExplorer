package com.devnull.fileexplorer.di;

import com.devnull.fileexplorer.analyzer.FileAnalyzerHelper;
import com.devnull.fileexplorer.interfaces.FileAnalyzerController;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class FileAnalyzerModule {

    @Provides
    @Singleton
    FileAnalyzerController provideFileAnalyzerController() {
        return new FileAnalyzerHelper();
    }
}
