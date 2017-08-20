package com.devnull.fileexplorer.analyzer

import com.devnull.fileexplorer.interfaces.IFileTypeCacheRepo
import io.realm.Realm
import java.io.File

class RealmFileTypeCacheRepo: IFileTypeCacheRepo {

    companion object {
        val NULL_FILE_EXCEPTION_STRING = "File argument must be initialized!"
        val DIR_FILE_ARG_EXCEPTION_STRING = "File argument can't be directory type"

        fun validateFileArgument(file: File?) {
            if (file == null)
                throw IllegalArgumentException(NULL_FILE_EXCEPTION_STRING)
            if (file.isDirectory)
                throw IllegalArgumentException(DIR_FILE_ARG_EXCEPTION_STRING)
        }
    }

    override fun saveFileInfo(file: File?) {
       validateFileArgument(file)

    }

    override fun updateFileInfo(file: File?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeFileInfo(file: File?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun doesRepoContainsFile(file: File?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCommonFileType(file: File?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}