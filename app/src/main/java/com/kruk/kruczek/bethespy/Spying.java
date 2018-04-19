package com.kruk.kruczek.bethespy;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;


public class Spying extends AppCompatActivity
{
    private Context context;

    private int previousBrightnessLevel;
    private int previousBrightnessMode;
    private static final int DARKNESS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spying);

        context = getApplicationContext();
        previousBrightnessLevel = getActualBrightnessLevel();
        previousBrightnessMode = getActualBrightnessMode();

        checkCorrectnessOfPermission();
        setDarkness();
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

    @Override
    protected void onPause()
    {
        setPreviousBrightness();
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        setPreviousBrightness();
        super.onStop();
    }

    @Override
    protected void onResume()
    {
        setDarkness();
        super.onResume();
    }

    @Override
    protected void onStart()
    {
        setDarkness();
        super.onStart();
    }


}
