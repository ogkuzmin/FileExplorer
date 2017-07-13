package com.devnull.fileexplorer.di;

import com.devnull.fileexplorer.interfaces.FileAnalyzerController;
import com.devnull.fileexplorer.ui.RecyclerViewAdapter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {FileAnalyzerModule.class})
public interface FileAnalyzerComponent {

    FileAnalyzerController provideFileAnalyzerController();

    void inject(RecyclerViewAdapter adapter);
}
