package com.t_matix.nouwaarom.lampcontroller;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;
import android.graphics.Color;

import java.util.Set;


public class ConnectedActivity extends Activity implements ColorPicker.OnColorChangedListener
{
    private boolean lampOn = false;
    private int lastColor = 0;
    String DEVICE = "";

    ConnectedThread connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);

        Intent intent = getIntent();
        DEVICE = intent.getStringExtra("DEVICE");

        Toast.makeText(this, "Connecting with " + DEVICE, Toast.LENGTH_LONG).show();

        //TODO search for online devices instead of paired devices
        Set<BluetoothDevice> devices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        for (BluetoothDevice device : devices) {
            //string equality is so nice
            if (device.getName().equals(DEVICE)) {
                ConnectThread connector = new ConnectThread(device);
                connected = new ConnectedThread(connector.connect());

                break;
            }
        }

        ColorPicker picker = (ColorPicker) findViewById(R.id.picker);
        ValueBar valueBar = (ValueBar) findViewById(R.id.valuebar);
        picker.addValueBar(valueBar);
        SaturationBar saturationBar = (SaturationBar) findViewById(R.id.saturationbar);
        picker.addSaturationBar(saturationBar);

        picker.setOnColorChangedListener(this);
        picker.setShowOldCenterColor(false);


        final Button toggle = (Button) findViewById(R.id.toggle);
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //toggle the lamp
                if(!lampOn)
                {
                    lampOn = true;
                    onColorChanged(lastColor);
                    toggle.setText("Turn off");
                }
                else {
                    lampOn = false;
                    onColorChanged(0);
                    toggle.setText("Turn on");
                }
            }
        });

        final Button alarm = (Button) findViewById(R.id.clock_button);
        alarm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
               launchAlarmActivity(v);
            }
        });

    }

    public void launchAlarmActivity(View view)
    {
        Intent intent = new Intent(this, AlarmActivity.class);
        intent.putExtra("DEVICE", DEVICE);
        startActivity(intent);
    }


    @Override
    public void onColorChanged(int color)
    {
        lampOn = true;

        if(color != 0)
            lastColor = color;

        connected.write((Color.green(color) + "g" +
                        Color.blue(color) + "b" +
                        Color.red(color) + "r").getBytes());
    }
}
