package com.devnull.fileexplorer.interfaces;

import java.io.File;

/**
 * Created by oleg on 10.03.17.
 */

public interface FileEventListener {

    public void onFileEvent(File file);
    public void onLongFileEvent(File file, int eventCode);

}
