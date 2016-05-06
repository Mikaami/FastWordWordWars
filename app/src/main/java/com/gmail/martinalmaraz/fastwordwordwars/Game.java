package com.gmail.martinalmaraz.fastwordwordwars;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
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
    Encoder encoder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("encoder", "wtf");
        setContentView(R.layout.game);
        //Sets up encoder. Handles it in a seperate thread
        Log.d("encoder", "before");
        new Thread(new Runnable() {
            @Override
            public void run() {
                AssetManager assetManager = getAssets();
                try {
                    Log.d("encoder", "start");
                    encoder = new Encoder(assetManager.open("words"));
                    Log.d("encoder", "end");
                }catch (IOException e)
                {
                    Log.d("file", "unable to open file", e);
                }
                if(encoder != null)
                {
                  Log.d("encoder", "success");
                }
                else
                    Log.d("encoder", "is null");
            }
        }).start();
        Log.d("encoder", "after");

    }

    public void test(View view)
    {

        String test = "testing";
        boolean[] temp = encoder.encode(test);
        Log.d("verify", "before-> " + test);
        Log.d("verify", "after-> " + encoder.decode(temp));


        /*
        String test = "a";
        Log.d("final", "before-> " + test);
        boolean[] bool = encoder.encode(test);
        byte[] toSend = encoder.toBytes(bool);
        boolean[] newBool = encoder.toBoolean(toSend);

        for(boolean b : newBool)
            Log.d("verify", "newBool-> " + String.valueOf(b));
        for(boolean b : bool)
            Log.d("verify", "bool-> " + String.valueOf(b));

        String output = encoder.decode(newBool);
        Log.d("final", "after-> " + output);
        */

        //String word = "angel";
        //bits = encoder.encode(word);
        //Log.d("encoder", encoder.decode(bits));
        //word = "martin";
        //bits = encoder.encode(word);
        //Log.d("encoder", encoder.decode(bits));
    }

    public void sendData(View v)
    {
        try
        {
            Log.d("string1", "test");
            out = socket.getOutputStream();
            Log.d("string1", "test");
            EditText textView = (EditText) findViewById(R.id.sample);
            Log.d("string1", "test");
            String sending = textView.getText().toString();
            Log.d("string1", sending.getBytes("UTF-8").toString());
            Log.d("string1", sending);
            out.write(sending.getBytes("UTF-8"));
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
