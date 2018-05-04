package com.kruk.kruczek.bethespy;

import android.media.Image;
import android.media.ImageReader;

import java.io.*;
import java.nio.ByteBuffer;

public class ImageReaderListener implements ImageReader.OnImageAvailableListener
{
    private File file;

    public ImageReaderListener(File _file)
    {
        file = _file;
    }

    @Override
    public void onImageAvailable(ImageReader reader)
    {
        Image image = null;
        try

        {
            image = reader.acquireLatestImage();
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.capacity()];
            buffer.get(bytes);
            save(bytes);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (image != null)
            {
                image.close();
            }
        }
    }

    private void save(byte[] bytes) throws IOException
    {
        OutputStream output = null;
        try
        {
            output = new FileOutputStream(file);
            output.write(bytes);
        }
        finally
        {
            if (null != output)
            {
                output.close();
            }
        }
    }
}
