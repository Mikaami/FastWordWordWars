package com.gmail.martinalmaraz.fastwordwordwars;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    FastDictionary dic;
    private char mostRecentChar;
    String END_TURN = "52";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("encoder", "wtf");
        setContentView(R.layout.game);

        if((int)getIntent().getExtras().get("turn") == SENDING)
        {
            isTurn = true;
            //enableButtons();
        }

        else if((int)getIntent().getExtras().get("turn") == RECIEVING)
        {
            isTurn = false;
            //disableButtons();
        }

        //Sets up encoder. Handles it in a seperate thread
        Log.d("encoder", "before");
        health = 100;
        enemyHealth = 100;
        disableButtons();
        try {
            Log.d("encoder", "start fastDic");
            dic = new FastDictionary(getAssets().open("words"));
            enableButtons();

            Log.d("encoder", "end fastDic");
        }catch (IOException e)
        {
            Log.d("file", "unable to open file", e);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                AssetManager assetManager = getAssets();
                try {
                    Log.d("encoder", "start");
                    Encoder.setFile(assetManager.open("words"));
                    encoder = Encoder.getInstance();
                    enableButtons();

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

    public void disableButtons()
    {
        ((Button)findViewById(R.id.send)).setEnabled(false);
        ((Button)findViewById(R.id.fix)).setEnabled(false);
        ((EditText)findViewById(R.id.sample)).setEnabled(false);
    }

    public void enableButtons()
    {
        ((Button)findViewById(R.id.send)).setEnabled(true);
        ((Button)findViewById(R.id.fix)).setEnabled(true);
        ((EditText)findViewById(R.id.sample)).setEnabled(true);
    }

    public void clearText()
    {
        ((TextView)findViewById(R.id.sample)).setText("");
    }

    public void sendData(String code)
    {
        try
        {
            out = socket.getOutputStream();
            out.write(code.getBytes());

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
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

            // check if that is a legal word //
            if(!dic.isWord(sending))
            {
                ((EditText) findViewById(R.id.sample)).setHighlightColor(Color.RED);
                return;
            }
            else
            {
                // can deal damage
                dealDamage(sending);
            }


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
            sendData(END_TURN);
            disableButtons();
            //out.write("-1".getBytes()); // send -1 to stop
        }
        catch (Exception e)
        {
            Log.d("game", "failed to get write socket", e);
        }

    }

    public void reInit(View view)
    {
        enableButtons();
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
        TextView enemy = (TextView) findViewById(R.id.textView);
        enemyHealth -= word.length();
        enemy.setText("Enemy Health: " + enemyHealth);

    }

    public void takeDamage(String word)
    {
        TextView player = (TextView) findViewById(R.id.textView2);
        health -= word.length();
        player.setText("Health: " + health);

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
                timeView.setText("TURN OVER");
                clearText();
                Log.d("50", "here");
                sendData(END_TURN);
                disableButtons();
                Log.d("50", "after");
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
        takeDamage(encoder.decode(temp));
    }
}
