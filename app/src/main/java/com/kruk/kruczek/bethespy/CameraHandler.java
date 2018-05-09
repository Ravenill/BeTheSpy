package com.kruk.kruczek.bethespy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.*;
import android.media.ImageReader;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.WindowManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.hardware.camera2.CameraMetadata.*;

public class CameraHandler extends Component
{
    private Context context;

    private String cameraId;
    private CameraDevice cameraDevice;
    private CameraManager cameraManager;
    private WindowManager windowManager;

    private Size[] imageDimension;
    private int width = 640;
    private int height = 480;

    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static
    {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    public CameraHandler(Context context, Object cameraService, WindowManager windowManager)
    {
        this.context = context;
        this.windowManager = windowManager;

        cameraDevice = null;
        cameraManager = (CameraManager)cameraService;
        mBackgroundHandler = null;
        mBackgroundThread = null;
    }

    private final CameraDevice.StateCallback cameraStateCallback = new CameraDevice.StateCallback()
    {
        @Override
        public void onOpened(CameraDevice camera)
        {
            cameraDevice = camera;
        }

        @Override
        public void onDisconnected(CameraDevice camera)
        {
            cameraDevice.close();
            cameraDevice = null;
        }

        @Override
        public void onError(CameraDevice camera, int error)
        {
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    public void openCamera()
    {
        if (cameraDevice != null)
            return;

        startBackgroundThread();

        try
        {
            cameraId = cameraManager.getCameraIdList()[0];
            if (ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            {
                setSizeOfPictures();
                cameraManager.openCamera(cameraId, cameraStateCallback, null);
            }
        }
        catch (CameraAccessException error)
        {
            error.printStackTrace();
        }
    }

    private void setSizeOfPictures() throws CameraAccessException
    {
        CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);

        if (characteristics != null)
        {
            imageDimension = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
        }

        if (imageDimension != null && 0 < imageDimension.length)
        {
            width = imageDimension[0].getWidth();
            height = imageDimension[0].getHeight();
        }
    }

    public void releaseCamera()
    {
        stopBackgroundThread();

        if (cameraDevice != null)
        {
            cameraDevice.close();
            cameraDevice = null;
        }
    }

    public void makeShot()
    {
        if (cameraDevice == null)
            return;

        try
        {
            int rotation = windowManager.getDefaultDisplay().getRotation();
            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);

            List<Surface> surfaces = new ArrayList<>();
            Surface captureSurface = reader.getSurface();
            surfaces.add(captureSurface);

            CaptureRequest.Builder captureRequestBuilder = createCaptureRequestBuilder(rotation, captureSurface);
            final CaptureRequest captureRequest = captureRequestBuilder.build();

            File file = createFileToSave();

            reader.setOnImageAvailableListener(new ImageReaderListener(file), mBackgroundHandler);
            cameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback()
            {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession)
                {
                    try
                    {
                        cameraCaptureSession.capture(captureRequest, new ImageCaptureListener(), mBackgroundHandler);
                    }
                    catch (CameraAccessException e)
                    {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session)
                {

                }
            }, mBackgroundHandler);

        }
        catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
    }

    private CaptureRequest.Builder createCaptureRequestBuilder(int rotation, Surface captureSurface) throws CameraAccessException
    {
        CaptureRequest.Builder captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
        captureRequestBuilder.addTarget(captureSurface);
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CONTROL_MODE_AUTO);
        captureRequestBuilder.set(CaptureRequest.FLASH_MODE, FLASH_MODE_OFF);
        captureRequestBuilder.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE, STATISTICS_FACE_DETECT_MODE_SIMPLE);
        captureRequestBuilder.set(CaptureRequest.CONTROL_AE_ANTIBANDING_MODE, CONTROL_AE_ANTIBANDING_MODE_AUTO);
        captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CONTROL_AF_MODE_AUTO);
        captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

        return captureRequestBuilder;
    }

    private File createFileToSave()
    {
        String path = getFilePath();
        File file = new File(path);

        return file;
    }

    private String getFilePath()
    {
        String fileDir = getFileDir();
        String fileName = getFileName();
        return fileDir + fileName;
    }

    private String getFileDir()
    {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "BeTheSpy");

        if (!mediaStorageDir.exists())
        {
            if (!mediaStorageDir.mkdirs())
            {
                Log.d("BeTheSpy", "failed to create directory");
                return "";
            }
        }
        return mediaStorageDir.getPath() + File.separator;
    }

    private String getFileName()
    {
        @SuppressLint("SimpleDateFormat") String timeStamp;
        timeStamp = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss").format(new Date());
        String fileName = "IMG_" + timeStamp + ".jpg";
        return fileName;
    }

    private void startBackgroundThread()
    {
        if (mBackgroundThread != null)
            return;

        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread()
    {
        if (mBackgroundThread == null)
            return;

        mBackgroundThread.quitSafely();
        try
        {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
