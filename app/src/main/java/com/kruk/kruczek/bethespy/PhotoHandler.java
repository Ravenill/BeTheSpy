package com.kruk.kruczek.bethespy;

import android.hardware.Camera;
import android.hardware.Camera.*;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class PhotoHandler implements PictureCallback
{
    public void onPictureTaken(byte[] data, Camera camera)
    {

        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);

        if (pictureFile == null)
        {
            Log.d(TAG, "Error creating media file, check storage permissions");
            return;
        }

        try
        {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
        }
        catch (FileNotFoundException error)
        {
            Log.d(TAG, "File not found: " + error.getMessage());
        }
        catch (IOException error)
        {
            Log.d(TAG, "Error accessing file: " + error.getMessage());
        }
    }

    private static File getOutputMediaFile(int type)
    {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "BeTheSpy");

        if (!mediaStorageDir.exists())
        {
            if (!mediaStorageDir.mkdirs())
            {
                Log.d("BeTheSpy", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE)
        {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        }
        else
        {
            mediaFile = null;
        }

        return mediaFile;
    }
}