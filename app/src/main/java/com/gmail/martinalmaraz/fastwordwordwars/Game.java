package com.gmail.martinalmaraz.fastwordwordwars;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by kami on 5/4/2016.
 */
public class Game extends Activity
{
    private final int SENDING = 1;
    private final int RECIEVING = 2;
    private boolean isTurn = false;
    BlueToothHelper helper = new BlueToothHelper(this);
    public int health;
    public int enemyHealth;
    BluetoothSocket socket = ((ApplicationGlobals)this.getApplication()).getMmSocket();
    OutputStream out;
    Encoder encoder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("encoder", "wtf");
        setContentView(R.layout.game);

        if((int)getIntent().getExtras().get("turn") == SENDING)
        {
            isTurn = true;
        }

        else if((int)getIntent().getExtras().get("turn") == RECIEVING)
        {
            isTurn = false;
        }

        //Sets up encoder. Handles it in a seperate thread
        Log.d("encoder", "before");
        health = 100;
        enemyHealth = 100;
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
        startTimer();
        receiveData();
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
            boolean[] temp = encoder.encode(sending);

            String outSend = "";
            for(boolean b: temp)
            {
                if(b)
                    outSend += "1";
                else
                    outSend += "0";
            }
            Log.d("string1", "booleanString-> " + outSend);
            out.write(outSend.getBytes());
        }
        catch (Exception e)
        {
            Log.d("game", "failed to get write socket", e);
        }
    }

    public void receiveData()
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
    public void dealDamage(String word)
    {
        boolean timeBonus = true;
        if(timeBonus)
        {
            health += word.length() - 3;
            enemyHealth -= word.length();
        }
        else
        {
            enemyHealth -= word.length();
        }
    }

    public void startTimer()
    {
        new CountDownTimer(15000, 1000)
        {
            public void onTick(long millisUntilFinished)
            {
                //settext field to seconds remainding
                TextView timeView = (TextView) findViewById(R.id.time);
                timeView.setText("Time: " + millisUntilFinished/1000);
            }
            public void onFinish()
            {
                // setText to done
                TextView timeView = (TextView) findViewById(R.id.time);
                timeView.setText("Done.");
            }
        }.start();
    }

    public void fixString(View v)
    {
        TextView textView = (TextView)findViewById(R.id.testing);
        Log.d("fix1", textView.getText().toString());
        boolean[] temp = new boolean[textView.getText().length()];
        int i = 0;
        for(char c: textView.getText().toString().toCharArray())
        {
            if(c == '1')
            {
                temp[i++] = true;
            }
            else
                temp[i++] = false;
        }

        Log.d("fix", encoder.decode(temp));
        textView.setText("");
        textView = (TextView)findViewById(R.id.sample);
        textView.setHint(encoder.decode(temp));
    }
}
