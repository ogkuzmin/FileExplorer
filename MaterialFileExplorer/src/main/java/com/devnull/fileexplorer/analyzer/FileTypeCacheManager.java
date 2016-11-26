package com.devnull.fileexplorer.analyzer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by devnull on 19.11.16.
 */
public class FileTypeCacheManager {


    private FileTypeCacheDBHelper dbHelper;


        public static abstract class FileTypeCacheEntry implements BaseColumns {

            public static final String TABLE_NAME = "file_type_cache";
            public static final String COLUMN_FILE_PATH = "file_path";
            public static final String COLUMN_PARENT_DIR = "parent_dir";
            public static final String COLUMN_IS_FILE = "is_file";
            public static final String COLUMN_MIME_TYPE = "mime_type";
            public static final String COLUMN_LAST_MODIFIED = "last_modified";
        }



    private class FileTypeCacheDBHelper extends SQLiteOpenHelper{

        private static final String DATABASE_NAME = "filetypecache.db";
        private static final int DATABASE_VERSION = 1;
        private static final String COMMA_SEP = ", ";
        private static final String TEXT_TYPE = " TEXT";
        private static final String INT_TYPE = " INTEGER";


        public FileTypeCacheDBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(getCommandToCreateFileTypeCacheTable());
        }
        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL(getCommandToDeleteFileTypeCacheTable());
            onCreate(sqLiteDatabase);
        }
        private String getCommandToCreateFileTypeCacheTable() {

            String createTable;

            createTable = "CREATE TABLE " + FileTypeCacheEntry.TABLE_NAME + " (" +
                    FileTypeCacheEntry.COLUMN_FILE_PATH     + TEXT_TYPE     + COMMA_SEP +
                    FileTypeCacheEntry.COLUMN_PARENT_DIR    + TEXT_TYPE     + COMMA_SEP +
                    FileTypeCacheEntry.COLUMN_IS_FILE       + INT_TYPE      + COMMA_SEP +
                    FileTypeCacheEntry.COLUMN_MIME_TYPE     + TEXT_TYPE     + COMMA_SEP +
                    FileTypeCacheEntry.COLUMN_LAST_MODIFIED + TEXT_TYPE     + ")";

            return createTable;
        }
        private String getCommandToDeleteFileTypeCacheTable() {
            return "DROP TABLE IF EXISTS " + FileTypeCacheEntry.TABLE_NAME;
        }



    }
}
