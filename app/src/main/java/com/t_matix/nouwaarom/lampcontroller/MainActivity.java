package com.t_matix.nouwaarom.lampcontroller;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;


public class MainActivity extends Activity
{
    //this should probaply be in a seperate file
    private static final int REQUEST_ENABLE_BT = 42;

    private ConnectedThread connected;
    protected BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check if the device has bluetooth
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
            Toast.makeText(this, "no haz support", Toast.LENGTH_LONG).show();
            //quit app as there is nothing to do anymore
            this.finish();
            System.exit(0);
        }
        else
        {
            //check if the bluetooth is enabled
            if(!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            else {
                listDevices();
            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void launchConnectedActivity(View view, String deviceName)
    {
        Intent intent = new Intent(this, ConnectedActivity.class);
        intent.putExtra("DEVICE", deviceName);
        startActivity(intent);
    }

    //callback for bluetooth enable request
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //check if the request matches our bluetooth enable request
        if(requestCode == REQUEST_ENABLE_BT){
            if(resultCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth enabled", Toast.LENGTH_LONG).show();

                listDevices();
            }
            else
            {
                Toast.makeText(this, "Bah ...", Toast.LENGTH_LONG).show();
                //we can now quit our app
                this.finish();
                System.exit(0);
            }
        }
    }

    private void listDevices()
    {
        final ListView deviceListView = (ListView) findViewById(R.id.device_list_view);
        Set<BluetoothDevice> pairedDevices;

        //callback for the device list click
        deviceListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //get the selected device
                String item = (String) parent.getItemAtPosition(position);

                //actual connection with the bluetooth device will be done in the connected activity
                launchConnectedActivity(deviceListView, item);
            }
        });

        //everything is fine and we can start checking for devices
        pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0)
        {
            //create an array adapter to store the results
            ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                mArrayAdapter.add(device.getName());
            }

            //display the results
            deviceListView.setAdapter(mArrayAdapter);
            //now we need to wait for the user to select a device from the list
        }
    }
}
