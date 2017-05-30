package com.devnull.fileexplorer.analyzer;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.j256.simplemagic.ContentType;

import java.io.File;
import java.io.IOException;

import static com.devnull.fileexplorer.analyzer.FileTypeCacheDBHelper.FileTypeCacheEntry.*;

/**
 * Created by oleg on 26.04.17.
 */

class AnalyzedFileCacheEntry {

    private static final String LOG_TAG = AnalyzedFileCacheEntry.class.getSimpleName();

    private String filePath;
    private String parentDir;
    private boolean isFile;
    private long fileSize;
    private String mimeType;
    private long lastModified;

    private static final int FIELD_COUNT = 6;

    private AnalyzedFileCacheEntry() {

    }

    public static AnalyzedFileCacheEntry createFromCursor(@NonNull Cursor cursor) {
        if (cursor.getColumnCount() != FIELD_COUNT) {
            return null;
        } else {
            AnalyzedFileCacheEntry fileEntry = new AnalyzedFileCacheEntry();

            fileEntry.filePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FILE_PATH));

            String parentDirFromDb = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PARENT_DIR));
            fileEntry.parentDir = parentDirFromDb.equals(COLUMN_NULL_VALUE)? null: parentDirFromDb;

            fileEntry.isFile = (cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_FILE)) ==
                    COLUMN_IS_FILE_TRUE_VALUE);
            fileEntry.fileSize = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FILE_SIZE)));
            fileEntry.mimeType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MIME_TYPE));
            fileEntry.lastModified = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_LAST_MODIFIED));

            return fileEntry;
        }
    }

    /**
     * Determines is this instance actual or not.
     *
     * @param file File that should be compared with instance.
     * @return
     */
    public boolean isActualEntryComparedByFile(@NonNull File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException("File argument should exist!!!");
        }

        boolean result = true;
        result &= file.getAbsolutePath().equals(filePath);
        if (file.getParentFile() == null) {
            result &= parentDir == null;
        } else {
            result &= file.getParentFile().getAbsolutePath().equals(parentDir);
        }
        result &= (isFile == file.isFile());
        result &= (fileSize == file.length());
        result &= (lastModified == file.lastModified());

        ContentInfoUtil util = new ContentInfoUtil();
        ContentInfo contentInfo = null;
        try {
            contentInfo = util.findMatch(file);
        } catch (IOException exception) {
            Log.d(LOG_TAG, exception.getMessage());
        }

        if (contentInfo == null) {
            result &= mimeType.equals(COLUMN_NULL_VALUE);
        } else {
            result &= mimeType.equals(contentInfo.getMimeType());
        }
        return result;
    }
}
