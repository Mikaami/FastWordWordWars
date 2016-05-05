package com.gmail.martinalmaraz.fastwordwordwars;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


public class MainActivity extends AppCompatActivity {

    BluetoothAdapter myBlueTooth;
    int REQUEST_ENABLE_BLUETOOTH = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupBlue();
    }

    @Override
    protected void  onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUEST_ENABLE_BLUETOOTH && resultCode == RESULT_OK)
        {
            Log.d("test", "did it");
        }
        else
            Log.d("test", "didnt do it");
    }

    public void setupBlue()
    {
        myBlueTooth = BluetoothAdapter.getDefaultAdapter();
        if(myBlueTooth == null)
        {
            Log.d("fail", "doesnt support bluetooth");
        }

        if (!myBlueTooth.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
        }
    }

    public void startServer()
    {
        BluetoothSocket socket;

        while(true)
        {
            try
            {
                socket =
            }
        }
    }



}
