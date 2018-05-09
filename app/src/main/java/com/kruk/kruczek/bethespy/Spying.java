package com.kruk.kruczek.bethespy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
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
    private SensorManager sensorManager;

    private CameraHandler camera;
    private DisplayHandler display;
    private AudioRecordHandler audioRecorder;
    private SensorAccelerometer sensorAccelerometer;

    private boolean optCam;
    private boolean optRec;

    private boolean actualrec;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spying);

        getOptions();

        context = getApplicationContext();
        view = getWindow().getDecorView();
        supportActionBar = getSupportActionBar();

        cameraService = getSystemService(Context.CAMERA_SERVICE);
        windowManager = getWindowManager();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        checkCorrectnessOfPermission();
        createComponents();

        camera.openCamera();
        audioRecorder.registerRecorder();

        //temporary fix, coz lack of time
        if (optRec == true && optCam == true)
            sensorAccelerometer.registerAccelerometer();

        display.setDarkness();
        display.setDisplay();

        actualrec = false;
    }

    @Override
    protected void onStart()
    {
        display.setDarkness();
        display.setDisplay();
        camera.openCamera();
        audioRecorder.registerRecorder();

        //temporary fix, coz lack of time
        if (optRec == true && optCam == true)
            sensorAccelerometer.registerAccelerometer();

        super.onStart();
    }

    @Override
    protected void onResume()
    {
        display.setDarkness();
        display.setDisplay();
        camera.openCamera();
        audioRecorder.registerRecorder();

        //temporary fix, coz lack of time
        if (optRec == true && optCam == true)
            sensorAccelerometer.registerAccelerometer();

        super.onResume();
    }

    @Override
    protected void onPause()
    {
        display.setPreviousBrightness();
        display.setPreviousDisplayMode();
        camera.releaseCamera();

        //temporary fix, coz lack of time
        if (optRec == true && optCam == true)
            sensorAccelerometer.releaseAccelerometer();
        audioRecorder.releaseRecorder();

        super.onPause();
    }

    @Override
    protected void onStop()
    {
        display.setPreviousBrightness();
        display.setPreviousDisplayMode();
        camera.releaseCamera();

        //temporary fix, coz lack of time
        if (optRec == true && optCam == true)
            sensorAccelerometer.releaseAccelerometer();

        audioRecorder.releaseRecorder();
        super.onStop();
    }

    @Override
    public void finish()
    {
        display.setPreviousBrightness();
        display.setPreviousDisplayMode();
        camera.releaseCamera();

        //temporary fix, coz lack of time
        if (optRec == true && optCam == true)
            sensorAccelerometer.releaseAccelerometer();

        audioRecorder.releaseRecorder();
        super.finish();
    }

    private void checkCorrectnessOfPermission()
    {
        if (!hasWriteSettingsPermission())
            requestWriteSettingsPermission();

        if (!hasCameraPermission())
            requestCameraPermission();

        if (!hasExternalStoragePermission())
            requestExternalStoragePermission();

        if (!hasAudioRecordPermission())
            requestAudioRecordPermission();
    }

    private boolean hasWriteSettingsPermission()
    {
        return Settings.System.canWrite(context);
    }

    private void requestWriteSettingsPermission()
    {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        context.startActivity(intent);
    }

    private boolean hasCameraPermission()
    {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 200);
    }

    private boolean hasExternalStoragePermission()
    {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestExternalStoragePermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 201);
    }

    private boolean hasAudioRecordPermission()
    {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestAudioRecordPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 202);
    }

    private void getOptions()
    {
        optCam = getIntent().getExtras().getBoolean("CamOpt");
        optRec = getIntent().getExtras().getBoolean("RecOpt");
    }

    private void createComponents()
    {
        camera = new CameraHandler(context, cameraService, windowManager);
        audioRecorder = new AudioRecordHandler();
        display = new DisplayHandler(context, view, supportActionBar);

        //temporary fix, coz lack of time
        if (optRec == true && optCam == true)
            sensorAccelerometer = new SensorAccelerometer(sensorManager, audioRecorder);
        else
            sensorAccelerometer = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int action = event.getAction();

        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                //temporary fix, coz lack of time
                if (optCam == true)
                    camera.makeShot();
                else if (optRec == true)
                    audioRecorder.recording(!actualrec);
                    actualrec = !actualrec;
                break;

            case MotionEvent.ACTION_MOVE:
                break;

            case MotionEvent.ACTION_UP:
                break;
        }
        return false;
    }
}


