package com.kruk.kruczek.bethespy;

import android.content.Context;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

public class DisplayHandler
{
    private Context context;
    private View view;
    private ActionBar supportActionBar;

    private int previousBrightnessLevel;
    private int previousBrightnessMode;
    private int previousDisplayMode;
    private static final int DARKNESS = 0;

    public DisplayHandler(Context context, View view, ActionBar supportActionBar)
    {
        this.context = context;
        this.view = view;
        this.supportActionBar = supportActionBar;

        previousBrightnessLevel = getActualBrightnessLevel();
        previousBrightnessMode = getActualBrightnessMode();
        previousDisplayMode = view.getSystemUiVisibility();
    }

    public void setDarkness()
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

    public void setDisplay()
    {
        view.setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE_STICKY | SYSTEM_UI_FLAG_HIDE_NAVIGATION | SYSTEM_UI_FLAG_FULLSCREEN);
        view.setBackgroundColor(Color.BLACK);
        supportActionBar.hide();
    }

    public void setPreviousBrightness()
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

    public void setPreviousDisplayMode()
    {
        view.setSystemUiVisibility(previousDisplayMode);
    }

    private int getActualBrightnessLevel()
    {
        int brightnessValue = 100;

        try
        {
            brightnessValue = android.provider.Settings.System.getInt(context.getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, 0);
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
            brightnessMode = android.provider.Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
        }
        catch (Exception error)
        {
            Log.e("Error", "Cannot access system brightness");
            error.printStackTrace();
        }

        return brightnessMode;
    }
}
