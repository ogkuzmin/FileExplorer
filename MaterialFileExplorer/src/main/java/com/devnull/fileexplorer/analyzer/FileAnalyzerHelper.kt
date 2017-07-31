package com.devnull.fileexplorer.analyzer

import android.os.Handler

import com.devnull.fileexplorer.interfaces.FileAnalyzerController
import com.devnull.fileexplorer.ui.FileRowModel
import io.realm.Realm

import javax.inject.Inject


/**
 * Created by oleg on 17.04.17.
 */

class FileAnalyzerHelper : FileAnalyzerController {

    override fun startAsyncQueryToAnalyzeFile(fileRowModel: FileRowModel) {
        //Don't analyze root dir, external storage and if it represents parent dir
        if (fileRowModel.itemCode == FileRowModel.ROOT_DIRECTORY_CODE ||
                fileRowModel.itemCode == FileRowModel.EXTERNAL_STORAGE_CODE ||
                fileRowModel.isParentDir) {
            return
        }

        val realm = Realm.getDefaultInstance()
        val cacheEntity = realm.where(AnalyzedFileCacheEntity::class.java).
                equalTo(AnalyzedFileCacheEntity.FILE_PATH_FIELD_NAME, fileRowModel.itemFile.absolutePath).
                findFirst()

        if (cacheEntity == null || cacheEntity.isActualEntityComparedByFile(fileRowModel.itemFile))
            return
    }
}
