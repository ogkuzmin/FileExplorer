package com.devnull.fileexplorer.analyzer

import android.util.Log

import java.io.File
import java.io.IOException

import com.j256.simplemagic.ContentInfo
import com.j256.simplemagic.ContentInfoUtil

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

/**
 * Created by oleg on 26.04.17.
 */

internal class AnalyzedFileCacheEntity: RealmObject() {

    companion object {
        private val LOG_TAG = AnalyzedFileCacheEntity.javaClass.simpleName
        val FILE_PATH_FIELD_NAME = "filePath"
        val PARENT_DIR_FIELD_NAME = "parentDir"
        val IS_FILE_FIELD_NAME = "isFile"
        val FILE_SIZE_FIELD_NAME = "fileSize"
        val MIME_TYPE_FIELD_NAME = "mimeType"
        val LAST_MODIFIED_FIELD_NAME = "lastModified"
    }

    @Required
    @PrimaryKey
    var filePath: String? = null
    var parentDir: String? = null
    @Required
    var isFile: Boolean = false
    var fileSize: Long = 0
    var mimeType: String? = null
    var lastModified: Long = 0


    /**
     * Determines is this instance actual or not.
     * @param file File that should be compared with instance.
     * *
     * @return true if this entity is actual.
     */
    public fun isActualEntityComparedByFile(file: File): Boolean {
        if (!file.exists()) {
            throw IllegalArgumentException("File argument should exist!!!")
        }

        var result = true
        result = result and (file.absolutePath == filePath)
        if (file.parentFile == null) {
            result = result and (parentDir == null)
        } else {
            result = result and (file.parentFile.absolutePath == parentDir)
        }
        result = result and (isFile == file.isFile)
        result = result and (fileSize == file.length())
        result = result and (lastModified == file.lastModified())

        val util = ContentInfoUtil()
        var contentInfo: ContentInfo? = null

        try {
            contentInfo = util.findMatch(file)
        } catch (exception: IOException) {
            Log.d(LOG_TAG, exception.message)
        }

        if (contentInfo == null) {
            result = result and (mimeType == null)
        } else {
            result = result and (mimeType == contentInfo.mimeType)
        }
        return result
    }
}


