package com.kruk.kruczek.bethespy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import static android.view.View.*;


public class Spying extends AppCompatActivity
{
    private Camera camera;
    private PhotoHandler photoHandler;

    private Context context;
    private View view;

    private int previousBrightnessLevel;
    private int previousBrightnessMode;
    private int previousDisplayMode;
    private static final int DARKNESS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spying);

        setCamera();
        photoHandler = new PhotoHandler();

        context = getApplicationContext();
        view = getWindow().getDecorView();

        previousBrightnessLevel = getActualBrightnessLevel();
        previousBrightnessMode = getActualBrightnessMode();
        previousDisplayMode = getWindow().getDecorView().getSystemUiVisibility();

        checkCorrectnessOfPermission();
        setDarkness();
        setDisplay();
    }

    @Override
    protected void onStart()
    {
        setDarkness();
        setDisplay();
        setCamera();
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        setDarkness();
        setDisplay();
        setCamera();
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        setPreviousBrightness();
        setPreviousDisplayMode();
        releaseCamera();
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        setPreviousBrightness();
        setPreviousDisplayMode();
        releaseCamera();
        super.onStop();
    }

    @Override
    public void finish()
    {
        setPreviousBrightness();
        setPreviousDisplayMode();
        releaseCamera();
        super.finish();
    }

    private int getActualBrightnessLevel()
    {
        int brightnessValue = 100;

        try
        {
            brightnessValue = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);
        }
        catch (Exception error)
        {
            Log.e("Error", "Cannot access system brightness");
            error.printStackTrace();
        }

        return brightnessValue;
    }

    private int getActualBrightnessMode()
    {
        int brightnessMode = 0;

        try
        {
            brightnessMode = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
        }
        catch (Exception error)
        {
            Log.e("Error", "Cannot access system brightness");
            error.printStackTrace();
        }

        return brightnessMode;
    }

    private void checkCorrectnessOfPermission()
    {
        if (!hasWriteSettingsPermission())
            changeWriteSettingsPermission();
    }

    private boolean hasWriteSettingsPermission()
    {
        boolean permission = true;
        permission = Settings.System.canWrite(context);
        return permission;
    }

    private void changeWriteSettingsPermission()
    {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        context.startActivity(intent);
    }

    private void setDarkness()
    {
        try
        {
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, DARKNESS);
        }
        catch (Exception error)
        {
            Log.e("Error", "Cannot access system brightness");
            error.printStackTrace();
        }
    }

    private void setDisplay()
    {
        view.setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE_STICKY | SYSTEM_UI_FLAG_HIDE_NAVIGATION | SYSTEM_UI_FLAG_FULLSCREEN);
        view.setBackgroundColor(Color.BLACK);
        getSupportActionBar().hide();
    }

    private void setPreviousBrightness()
    {
        try
        {
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, previousBrightnessLevel);
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, previousBrightnessMode);
        }
        catch (Exception error)
        {
            Log.e("Error", "Cannot access system brightness");
            error.printStackTrace();
        }
    }

    private void setPreviousDisplayMode()
    {
        view.setSystemUiVisibility(previousDisplayMode);
    }

    private void setCamera()
    {
        if (camera == null)
            camera = getCameraInstance();
    }

    public static Camera getCameraInstance()
    {
        Camera temp_camera = null;
        try
        {
            temp_camera = Camera.open();
        }
        catch (Exception error)
        {
            Log.e("Error", "Cannot access camera");
        }
        return temp_camera;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int action = event.getAction();

        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_MOVE:
                break;

            case MotionEvent.ACTION_UP:
                makeShot();
                break;
        }
        return false;
    }

    private void makeShot()
    {
        camera.takePicture(null, null, photoHandler);
    }

    private void releaseCamera()
    {
        if (camera != null)
        {
            camera.release();
            camera = null;
        }
    }

}
