package com.gmail.martinalmaraz.fastwordwordwars;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.OutputStream;

/**
 * Created by kami on 5/4/2016.
 */
public class Game extends Activity
{
    private final int SENDING = 1;
    private final int RECIEVING = 2;
    BlueToothHelper helper = new BlueToothHelper(this);
    BluetoothSocket socket = ((ApplicationGlobals)this.getApplication()).getMmSocket();
    OutputStream out;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
    }

    public void sendData(View v)
    {
        try
        {
            out = socket.getOutputStream();
            out.write("testing 1 2 3".getBytes());
            out.close();
        }
        catch (Exception e)
        {
            Log.d("game", "failed to get write socket", e);
        }
    }

    public void receiveData(View v)
    {
        if(!helper.status())
        {
            helper.execute();
        }
        else
        {
            helper.cancel(true);
            helper.execute();
        }
    }
}
