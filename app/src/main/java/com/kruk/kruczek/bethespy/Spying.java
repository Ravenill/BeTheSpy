package com.kruk.kruczek.bethespy;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.Settings;


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

        setDarkness();
    }

    private int getActualBrightnessLevel()
    {
        int brightnessValue = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);
        return brightnessValue;
    }

    private int getActualBrightnessMode()
    {
        int brightnessMode = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
        return brightnessMode;
    }

    private void setDarkness()
    {
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, DARKNESS);
    }

    @Override
    public void finish()
    {
        setPreviousBrightness();
        super.finish();
    }

    private void setPreviousBrightness()
    {
        Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, previousBrightnessLevel);
    }

}
