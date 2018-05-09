package com.kruk.kruczek.bethespy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    private TextView txtDesc;
    private Button btnStart;
    private Button btnOptions;

    private boolean optCam;
    private boolean optRec;

    private static final int START_INTENT_CODE = 101;
    private static final int OPTION_INTENT_CODE = 102;

    private static final String DESC = "Hello there!\n" +
            "Tap on screen - shot from camera,\n" +
            "Rotate phone - start/stop recording";

    private static final String DESC2 = "Hello there!\n" +
            "Tap on screen - shot from camera";

    private static final String DESC3 = "Hello there!\n" +
            "Tap on screen - start/stop recording";

    private static final String DESC4 = "Hello there!\n" +
            "ALL OPTIONS ARE OFF";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createButtons();
        setOptions();
        setDesc();
    }

    @Override
    protected void onStart()
    {
        setDesc();
        super.onStart();
    }

    private void createButtons()
    {
        txtDesc = findViewById(R.id.desc);
        btnStart = findViewById(R.id.startButton);
        btnOptions = findViewById(R.id.optionsButton);

        setListeners();
    }

    private void setOptions()
    {
        optCam = true;
        optRec = true;
    }

    private void setListeners()
    {
        btnStart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onClickStart();
            }
        });
        btnOptions.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onClickOptions();
            }
        });
    }

    public void onClickStart()
    {
        Intent intent = new Intent(this, Spying.class);
        intent.putExtra("CamOpt", optCam);
        intent.putExtra("RecOpt", optRec);
        startActivityForResult(intent, START_INTENT_CODE);
    }

    public void onClickOptions()
    {
        Intent intent = new Intent(this, Settings.class);
        intent.putExtra("CamOpt", optCam);
        intent.putExtra("RecOpt", optRec);
        startActivityForResult(intent, OPTION_INTENT_CODE);
    }

    private void setDesc()
    {
        if (optCam == true && optRec == true)
            txtDesc.setText(DESC);
        else if (optCam == true)
            txtDesc.setText(DESC2);
        else if (optRec == true)
            txtDesc.setText(DESC3);
        else
            txtDesc.setText(DESC4);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            if (requestCode == START_INTENT_CODE)
            {
                onActivityResultStart(data);
            }

            if (requestCode == OPTION_INTENT_CODE)
            {
                onActivityResultOptions(data);
            }
        }
    }

    private void onActivityResultStart(Intent data)
    {
        //NOTHING TODO
    }

    private void onActivityResultOptions(Intent data)
    {
        if (data.hasExtra("CamOptReturn"))
        {
            optCam = data.getExtras().getBoolean("CamOptReturn");
        }

        if (data.hasExtra("RecOptReturn"))
        {
            optRec = data.getExtras().getBoolean("RecOptReturn");
        }
    }
}
