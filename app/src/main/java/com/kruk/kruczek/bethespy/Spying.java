package com.kruk.kruczek.bethespy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class Spying extends AppCompatActivity
{
    private Context context;
    private View view;
    private ActionBar supportActionBar;
    private Object cameraService;
    private WindowManager windowManager;

    private CameraHandler cameraHandler;
    private DisplayHandler displayHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spying);

        context = getApplicationContext();
        view = getWindow().getDecorView();
        supportActionBar = getSupportActionBar();

        cameraService = getSystemService(Context.CAMERA_SERVICE);
        windowManager = getWindowManager();

        checkCorrectnessOfPermission();

        cameraHandler = new CameraHandler(context, cameraService, windowManager);
        displayHandler = new DisplayHandler(context, view, supportActionBar);

        cameraHandler.openCamera();
        displayHandler.setDarkness();
        displayHandler.setDisplay();
    }

    @Override
    protected void onStart()
    {
        displayHandler.setDarkness();
        displayHandler.setDisplay();
        cameraHandler.startBackgroundThread();
        cameraHandler.openCamera();
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        displayHandler.setDarkness();
        displayHandler.setDisplay();
        cameraHandler.startBackgroundThread();
        cameraHandler.openCamera();
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        displayHandler.setPreviousBrightness();
        displayHandler.setPreviousDisplayMode();
        cameraHandler.stopBackgroundThread();
        cameraHandler.releaseCamera();
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        displayHandler.setPreviousBrightness();
        displayHandler.setPreviousDisplayMode();
        cameraHandler.stopBackgroundThread();
        cameraHandler.releaseCamera();
        super.onStop();
    }

    @Override
    public void finish()
    {
        displayHandler.setPreviousBrightness();
        displayHandler.setPreviousDisplayMode();
        cameraHandler.stopBackgroundThread();
        cameraHandler.releaseCamera();
        super.finish();
    }

    private void checkCorrectnessOfPermission()
    {
        if (!hasWriteSettingsPermission())
            changeWriteSettingsPermission();

        if (!hasCameraPermission())
            requestCameraPermission();
    }

    private boolean hasWriteSettingsPermission()
    {
        return Settings.System.canWrite(context);
    }

    private void changeWriteSettingsPermission()
    {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        context.startActivity(intent);
    }

    private boolean hasCameraPermission()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            return false;
        }
        return true;
    }

    private void requestCameraPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int action = event.getAction();

        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                cameraHandler.makeShot();
                break;

            case MotionEvent.ACTION_MOVE:
                break;

            case MotionEvent.ACTION_UP:
                break;
        }
        return false;
    }
}


