package com.gmail.martinalmaraz.fastwordwordwars;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by kami on 5/4/2016.
 */
public class Game extends Activity
{
    public Encoder encoder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        //Sets up encoder. Handles it in a seperate thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                AssetManager assetManager = getAssets();
                try {
                    encoder = new Encoder(assetManager.open("words"));
                }catch (IOException e)
                {
                    Log.d("file", "unable to open file", e);
                }
                if(encoder != null)
                {
                    Log.d("encoder", "success");
                }
            }
        }).start();

    }

}
