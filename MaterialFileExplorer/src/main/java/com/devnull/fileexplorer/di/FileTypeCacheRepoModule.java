package com.devnull.fileexplorer.di;

import com.devnull.fileexplorer.analyzer.RealmFileTypeCacheRepo;
import com.devnull.fileexplorer.interfaces.IFileTypeCacheRepo;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class FileTypeCacheRepoModule {
    @Provides
    @Singleton
    IFileTypeCacheRepo provideFileTypeCacheRepo() {return new RealmFileTypeCacheRepo();}
}
