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

    private static final String DESC = "Hello there!\n" +
                                        "Tap on screen - shot from camera,\n" +
                                        "Rotate phone - start/stop recording,\n" +
                                        "3x press home button - exit from app";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createButtons();
        setDesc();
    }

    private void createButtons()
    {
        txtDesc = findViewById(R.id.desc);
        btnStart = findViewById(R.id.startButton);
        btnOptions = findViewById(R.id.optionsButton);

        setListeners();
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
        startActivity(intent);
    }

    public void onClickOptions()
    {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    private void setDesc()
    {
        txtDesc.setText(DESC);
    }
}
