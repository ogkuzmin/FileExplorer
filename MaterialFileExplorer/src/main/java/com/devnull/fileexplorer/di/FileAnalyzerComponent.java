package com.devnull.fileexplorer.di;

import com.devnull.fileexplorer.interfaces.IFileAnalyzerController;
import com.devnull.fileexplorer.ui.RecyclerViewAdapter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {FileAnalyzerModule.class})
public interface FileAnalyzerComponent {

    IFileAnalyzerController provideFileAnalyzerController();

    void inject(RecyclerViewAdapter adapter);
}
