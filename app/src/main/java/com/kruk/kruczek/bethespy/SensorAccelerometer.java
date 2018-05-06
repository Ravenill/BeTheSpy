package com.kruk.kruczek.bethespy;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.List;

public class SensorAccelerometer
{
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private AudioRecordHandler audioRecorder;

    public SensorAccelerometer(SensorManager sensorManager, AudioRecordHandler audioRecorder)
    {
        this.sensorManager = sensorManager;
        this.audioRecorder = audioRecorder;
        this.accelerometerSensor = null;
    }

    public void registerAccelerometer()
    {
        if (accelerometerSensor != null)
            return;

        if (hasAccelerometerSensor())
        {
            sensorManager.registerListener(accelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private boolean hasAccelerometerSensor()
    {
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensorList.size() > 0)
        {
            accelerometerSensor = sensorList.get(0);
            return true;
        }
        return false;
    }

    public void releaseAccelerometer()
    {
        if (accelerometerSensor == null)
            return;

        sensorManager.unregisterListener(accelerometerListener);
        accelerometerSensor = null;
    }

    private SensorEventListener accelerometerListener = new SensorEventListener()
    {

        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1)
        {

        }

        @Override
        public void onSensorChanged(SensorEvent arg0)
        {
            float z_value = arg0.values[2];
            if (z_value >= 0)
            {
                audioRecorder.recording(false);
            }
            else
            {
                audioRecorder.recording(true);
            }
        }
    };
}
