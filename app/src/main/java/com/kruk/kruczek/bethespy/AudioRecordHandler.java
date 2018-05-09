package com.kruk.kruczek.bethespy;

import android.annotation.SuppressLint;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AudioRecordHandler extends Component
{
    private MediaRecorder recorder;
    private boolean duringRecording;

    public AudioRecordHandler()
    {
        recorder = null;
        registerRecorder();
        duringRecording = false;
    }

    public void registerRecorder()
    {
        if (recorder != null)
            return;

        if (duringRecording == true)
            return;

        recorder = new MediaRecorder();
    }

    public void recording(boolean record)
    {
        if (record)
            startRecording();
        else
            stopRecording();
    }

    private void startRecording()
    {
        if (duringRecording == true)
            return;

        registerRecorder();

        if (recorder == null)
            return;

        String path = getFilePath();

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setOutputFile(path);

        try
        {
            recorder.prepare();
        }
        catch (IOException e)
        {
            Log.e("Recorder: ", "prepare() failed");
        }

        duringRecording = true;
        recorder.start();
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
        String fileName = "REC_" + timeStamp + ".mp4";
        return fileName;
    }

    private void stopRecording()
    {
        if (duringRecording == false)
            return;

        if (recorder == null)
            return;

        duringRecording = false;
        releaseRecorder();
    }
    public void releaseRecorder()
    {
        if (recorder == null)
            return;

        recorder.release();
        recorder = null;
    }
}
