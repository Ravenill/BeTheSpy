package com.kruk.kruczek.bethespy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Switch;

public class Settings extends AppCompatActivity
{
    Switch camSwitch;
    Switch recSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        CreateButtons();
        SetButtons();
    }

    private void CreateButtons()
    {
        camSwitch = findViewById(R.id.cam_switch);
        recSwitch = findViewById(R.id.rec_switch);
    }

    private void SetButtons()
    {
        boolean valueOfCam = getIntent().getExtras().getBoolean("CamOpt");
        boolean valueOfRec = getIntent().getExtras().getBoolean("RecOpt");

        camSwitch.setChecked(valueOfCam);
        recSwitch.setChecked(valueOfRec);
    }

    @Override
    public void finish()
    {
        Intent data = new Intent();
        data.putExtra("CamOptReturn", camSwitch.isChecked());
        data.putExtra("RecOptReturn", recSwitch.isChecked());
        setResult(RESULT_OK, data);

        super.finish();
    }
}
