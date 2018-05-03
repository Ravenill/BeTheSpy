package com.kruk.kruczek.bethespy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import static android.view.View.*;


public class Spying extends AppCompatActivity
{
    private Context context;

    private int previousBrightnessLevel;
    private int previousBrightnessMode;
    private int previousDisplayMode;
    private static final int DARKNESS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spying);

        context = getApplicationContext();
        previousBrightnessLevel = getActualBrightnessLevel();
        previousBrightnessMode = getActualBrightnessMode();
        previousDisplayMode = getWindow().getDecorView().getSystemUiVisibility();

        checkCorrectnessOfPermission();
        setDarkness();
        setDisplay();
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
        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE_STICKY | SYSTEM_UI_FLAG_HIDE_NAVIGATION | SYSTEM_UI_FLAG_FULLSCREEN);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        getSupportActionBar().hide();
    }

    @Override
    public void finish()
    {
        setPreviousBrightness();
        super.finish();
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
        getWindow().getDecorView().setSystemUiVisibility(previousDisplayMode);
    }

    @Override
    protected void onPause()
    {
        setPreviousBrightness();
        setPreviousDisplayMode();
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        setPreviousBrightness();
        setPreviousDisplayMode();
        super.onStop();
    }

    @Override
    protected void onResume()
    {
        setDarkness();
        setDisplay();
        super.onResume();
    }

    @Override
    protected void onStart()
    {
        setDarkness();
        setDisplay();
        super.onStart();
    }


}
