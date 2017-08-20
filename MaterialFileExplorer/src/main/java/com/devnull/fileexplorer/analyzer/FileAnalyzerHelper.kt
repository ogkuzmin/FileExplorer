package com.devnull.fileexplorer.analyzer

import com.devnull.fileexplorer.interfaces.IFileAnalyzerController
import com.devnull.fileexplorer.interfaces.IFileTypeCacheRepo
import com.devnull.fileexplorer.ui.FileRowModel
import javax.inject.Inject


/**
 * Created by oleg on 17.04.17.
 */

class FileAnalyzerHelper : IFileAnalyzerController {
    /*init {
        DaggerFileTypeCacheRepoComponent.builder().build().inject(this)
    }

    @Inject
    private var repo: IFileTypeCacheRepo*/



    override fun startAsyncQueryToAnalyzeFile(fileRowModel: FileRowModel) {
        //Don't analyze root dir, external storage and if it represents parent dir
        if (fileRowModel.itemCode == FileRowModel.ROOT_DIRECTORY_CODE ||
                fileRowModel.itemCode == FileRowModel.EXTERNAL_STORAGE_CODE ||
                fileRowModel.isParentDir) {
            return
        }

    }
}
