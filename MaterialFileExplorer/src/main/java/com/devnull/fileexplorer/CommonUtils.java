package com.devnull.fileexplorer;

import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by ogkuzmin on 27.08.16.
 */
public class CommonUtils {

    private CommonUtils(){}

    public static String getFileExtension(File file){
        String ext = "";

        if (file.isFile()) {
            String fileName = file.getName();

            if (fileName.lastIndexOf(".") > 0)
                ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        }

        return ext;
    }
    public static boolean isExtStorageReadable(){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
    public static final String getStringTimeFromLong(final long timeInMillis){

        final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy HH:mm");
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeInMillis);
        c.setTimeZone(TimeZone.getDefault());
        return format.format(c.getTime());
    }
    public static final String getStringSizeFileFromLong(final long fileSize){

        double sizeInMB = (double)fileSize/(1024*1024);
        double sizeInKB = (double)fileSize/1024;

        if (sizeInMB >= 1)
            return roundDoubleToDecimal(sizeInMB) + " MB";
        else if(sizeInKB >= 1)
            return roundDoubleToDecimal(sizeInKB)  + " kB";
        else
            return fileSize + " B";
    }
    private static double roundDoubleToDecimal(double d){

        d = d * 10;
        int i = (int)Math.round(d);
        d = (double)i/10;

        return d;
    }
    public static long getRealLastModified(File file) {

        long lastModified = file.lastModified();
        return lastModified > 0? lastModified: 0;
    }
}
