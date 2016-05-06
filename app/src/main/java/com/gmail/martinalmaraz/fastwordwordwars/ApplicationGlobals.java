package com.gmail.martinalmaraz.fastwordwordwars;

import android.app.Application;
import android.bluetooth.BluetoothSocket;

/**
 * Created by pico on 5/5/16.
 */
public class ApplicationGlobals extends Application {

    public static BluetoothSocket mmSocket;

    public static BluetoothSocket getMmSocket()
    {
        return mmSocket;
    }
    public  void setMmSocket(BluetoothSocket socket)
    {
        this.mmSocket = socket;
    }

}
