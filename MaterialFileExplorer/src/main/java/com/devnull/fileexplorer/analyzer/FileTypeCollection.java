package com.devnull.fileexplorer.analyzer;

import android.support.annotation.NonNull;

import com.j256.simplemagic.ContentType;

/**
 * Class represents used types of files for UI and some utils for it.
 */
public class FileTypeCollection {

    /**
     * These types are used to show icon of file.
     */
    public static class CommonType {
        public static final int AUDIO = -1;
        public static final int IMAGE = -2;
        public static final int VIDEO = -3;
        public static final int PDF = -4;
        public static final int OFFICE_DOC = -5;
        public static final int OFFICE_SPREADSHEETS = -6;
        public static final int OFFICE_PRESENTATION = -7;
        public static final int ARCHIVE = -8;
        public static final int APK = -9;
        public static final int TEXT = -10;
        public static final int BINARY = -11;
        public static final int OTHER = -12;
    }

    /**
     * Maps the mime type of file to {@link CommonType}.
     *
     * @param mimeType MimeType of file.
     * @return int that should be equal one of constants from CommonType.
     */
    public static int getCommonTypeFromMimeType(String mimeType) {
        int result;

        if (mimeType.contains("image/")) {
            result = CommonType.IMAGE;
        } else if (mimeType.contains("audio/")) {
            result = CommonType.AUDIO;
        } else if (mimeType.contains("video/")) {
            result = CommonType.VIDEO;
        } else if (mimeType.contains("application/pdf")) {
            result = CommonType.PDF;
        } else if (isOfficeDoc(mimeType)) {
            result = CommonType.OFFICE_DOC;
        } else if (isSpreadSheetOfficeDoc(mimeType)) {
            result = CommonType.OFFICE_SPREADSHEETS;
        } else if (isOfficePresentation(mimeType)) {
            result = CommonType.OFFICE_PRESENTATION;
        } else if (isArchive(mimeType)) {
            result = CommonType.ARCHIVE;
        } else if (isApkFile(mimeType)) {
            result = CommonType.APK;
        } else if (isText(mimeType)) {
            result = CommonType.TEXT;
        } else if (isBinary(mimeType)) {
            result = CommonType.BINARY;
        } else {
            result = CommonType.OTHER;
        }

        return result;
    }
    /**
     * Matches a {@link ContentType} instance to an instance of {@link CommonType}.
     *
     * @param type that should be matched.
     * @return one field from class {@link CommonType}.
     */
    public static int getCommonTypeFromContentType(@NonNull ContentType type) {
        return getCommonTypeFromMimeType(type.getMimeType());
    }
    private static boolean isOfficeDoc(String mimeType) {
        return (mimeType.contains("application/") && (mimeType.contains("msword") ||
                mimeType.contains("openxmlformats-officedocument.wordprocessingml.document") ||
                mimeType.contains("opendocument.text")));
    }
    private static boolean isSpreadSheetOfficeDoc(String mimeType) {
        return (mimeType.contains("application/") && (mimeType.contains("ms-excel") ||
                mimeType.contains("openxmlformats-officedocument.spreadsheetml.sheet") ||
                mimeType.contains("opendocument.spreadsheet")));
    }
    private static boolean isOfficePresentation(String mimeType) {
        return (mimeType.contains("application/") && (mimeType.contains("ms-powerpoint") ||
                mimeType.contains("openxmlformats-officedocument.presentationml.presentation") ||
                mimeType.contains("opendocument.presentation")));
    }
    private static boolean isArchive(String mimeType) {
        return (mimeType.contains("application/") && (mimeType.contains("x-arc") ||
                mimeType.contains("x-cpio") ||  mimeType.contains("x-lha") || mimeType.contains("x-rar") ||
                mimeType.contains("x-stuffit") || mimeType.contains("x-tar") || mimeType.contains("zip") ||
                mimeType.contains("x-zoo") || mimeType.contains("java-archive") || mimeType.contains("joda-archive") ||
                mimeType.contains("tao.intent-module-archive")));
    }
    private static boolean isApkFile(String mimeType) {
        return (mimeType.contains("application/") && mimeType.contains("android.package-archive"));
    }
    private static boolean isText(String mimeType) {
        return (!mimeType.contains("application/") && mimeType.contains("text"));
    }
    private static boolean isBinary(String mimeType) {
        return mimeType.contains("application/");
    }
}
