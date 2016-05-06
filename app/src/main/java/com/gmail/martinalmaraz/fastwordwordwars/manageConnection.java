package com.gmail.martinalmaraz.fastwordwordwars;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by pico on 5/5/16.
 */
public class manageConnection extends Thread
{
    private final BluetoothSocket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public manageConnection(BluetoothSocket s)
    {
        socket = s;
        InputStream tmpI = null;
        OutputStream tmpO = null;

        try
        {
            tmpI = s.getInputStream();
            tmpO = s.getOutputStream();
            Log.d("b", "5");
        }
        catch (Exception e)
        {
            Log.d("conn", "failed to open socket");
        }

        inputStream = tmpI;
        outputStream = tmpO;
    }

    public void run()
    {
        byte[] buff = new byte[1024];
        int bytes;

        while(true)
        {
            Log.d("b", "hereee");
            try
            {
                Log.d("b", "here?");
                bytes = inputStream.read(buff);
                Log.d("b", "2");
                Log.d("conn b", Integer.toString(bytes));
                Log.d("b", "3");
            }
            catch (Exception e)
            {
                Log.d("b", "4");
                break;
            }
        }
    }

    public void write(byte[] bytes)
    {
        try
        {
            Log.d("conn b", "1");
            outputStream.write(bytes);
        }
        catch (Exception e)
        {
            Log.d("conn", "failed write");
        }
    }

    public void cancel()
    {
        try
        {
            socket.close();
        }
        catch (Exception e)
        {
            Log.d("conn", "failed to close");
        }
    }



}