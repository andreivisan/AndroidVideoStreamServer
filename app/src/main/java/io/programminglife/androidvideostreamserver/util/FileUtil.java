package io.programminglife.androidvideostreamserver.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * Created by andreivisan on 11/7/15.
 */
public class FileUtil {

    private final static String tag = FileUtil.class.getSimpleName();

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

    public File getFile() {
        File sdCardPicture = new File("/sdcard/DCIM/Camera/IMG_20151021_080749.jpg");
        return sdCardPicture;
    }

    public String base64EncodedImage() {
        Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/DCIM/Camera/IMG_20151021_080749.jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(bytes, Base64.DEFAULT);
        return encodedImage;
    }

    public JSONArray getMediaFilesList() {
        JSONArray mediaFilesList = new JSONArray();
        File sdCardMediaFolder = new File("/sdcard/DCIM/Camera/");
        try {
            if(sdCardMediaFolder.isDirectory()) {
                File[] files = sdCardMediaFolder.listFiles();
                for(int i = 0; i < files.length; i++) {
                    JSONObject fileJson = new JSONObject();
                    fileJson.put("fileName", files[i].getName());
                    fileJson.put("extension", getFileExtension(files[i].getName()));
                    mediaFilesList.put(i, fileJson);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mediaFilesList;
    }

    private String getFileExtension(String fileName) {
        String extension = null;
        int lastIndexOfDot = fileName.lastIndexOf(".");
        extension = fileName.substring(lastIndexOfDot, fileName.length());
        Log.i(tag, "File " + fileName + " has extension " + extension);
        return extension;
    }

}
