package io.programminglife.androidvideostreamserver.util;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by andreivisan on 11/7/15.
 */
public class FileUtil {

    public byte[] getImageFile(String name) {
        File sdCardPicture = new File("/sdcard/DCIM/Camera/IMG_20151021_080749.jpg");
        return readFileContetnIntoByteArray(sdCardPicture);
    }

    private byte[] readFileContetnIntoByteArray(File file) {
        FileInputStream fileInputStream = null;
        byte[] bFile = new byte[(int) file.length()];
        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bFile;
    }

}
