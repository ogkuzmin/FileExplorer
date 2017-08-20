package com.devnull.fileexplorer.di;

import com.devnull.fileexplorer.interfaces.IFileAnalyzerController;
import com.devnull.fileexplorer.interfaces.IFileTypeCacheRepo;
import com.devnull.fileexplorer.ui.PresentationModelTransformer;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {FileTypeCacheRepoModule.class})
public interface FileTypeCacheRepoComponent {

    IFileTypeCacheRepo provideFileTypeCacheRepo();

    void inject(IFileAnalyzerController controller);

    void inject(PresentationModelTransformer transformer);
}
