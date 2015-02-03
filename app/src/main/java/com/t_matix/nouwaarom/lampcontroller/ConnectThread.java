package com.t_matix.nouwaarom.lampcontroller;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;


public class ConnectThread extends Thread
{
    private static final String TAG = "ConnectDevice";

    private BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;

    public ConnectThread(BluetoothDevice device)
    {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mmDevice = device;

        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try
        {
            tmp = device.createRfcommSocketToServiceRecord(uuid);
        }
        catch (NullPointerException e) {}
        catch (IOException e) {}

        mmSocket = tmp;
    }

    public BluetoothSocket connect()
    {
        try
        {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
        }
        catch (IOException connectException)
        {
            // Unable to connect; close the socket and get out
            try
            {
                mmSocket.close();
            }
            catch (IOException closeException) { }
            return(null);
        }

        // Do work to manage the connection (in a separate thread)
        return(mmSocket);
    }
}
