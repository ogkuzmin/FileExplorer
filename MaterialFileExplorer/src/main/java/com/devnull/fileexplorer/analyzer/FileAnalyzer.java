package com.devnull.fileexplorer.analyzer;

import android.support.v4.util.Pair;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import com.devnull.fileexplorer.analyzer.FileTypeCollection.CommonType;

/**
 * Created by devnull on 05.11.16.
 */
public class FileAnalyzer {

    private static final String TAG = FileAnalyzer.class.getSimpleName();
    private static final int BYTES_COUNT_FOR_ANALYZE = 512;
    private static final int MAX_ASCII_CODE = 256;

    private static FileAnalyzer sInstance;

    public FileAnalyzer getInstance() {
        if (null == sInstance)
            sInstance = new FileAnalyzer();

        return sInstance;
    }

    private FileAnalyzer() { }

    /*public Pair<CommonType, String> analyzeFile(File file) {

    }*/

    public enum TextEncodings {
        ASCII,
        UTF8,
        UNKNOWN
    }

    public static TextEncodings getTextEncoding(File file) {
        TextEncodings result = TextEncodings.UNKNOWN;


        return result;

    }

    private static boolean isAsciiEncoding(byte[] bytesForAnalyze){

        short tmp;
        for (byte currentByte : bytesForAnalyze) {
            tmp = (short) currentByte;
            if (tmp >= 0 && tmp <= (MAX_ASCII_CODE))
                continue;
            else
                return false;
            }
        return true;
    }

    private static boolean isUtf8Encoding(byte[] bytesForAnalyze) {
        CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
        try{
            decoder.decode(ByteBuffer.wrap(bytesForAnalyze));
        } catch (Exception ex) {
            return false;
        }
        return true;
    }
}
