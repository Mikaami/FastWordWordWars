package com.gmail.martinalmaraz.fastwordwordwars;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    public BluetoothAdapter myBlueTooth;
    int REQUEST_ENABLE_BLUETOOTH = 1;
    String NAME = "fastwordwordwars";
    UUID MY_UUID = UUID.fromString("d364b420-8d71-11e3-baa8-0800200c9a66");

    //UUID MY_UUID = UUID.randomUUID();

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

    public void joinGame(View v)
    {
        Set<BluetoothDevice> devices = myBlueTooth.getBondedDevices();
        final BluetoothDevice other = devices.iterator().next();
        /*
        if(devices.size() > 0)
        {
            for(BluetoothDevice b : devices)
            {
                other = b;
                Log.d("test", b.getAddress());
            }
        }
        */

        new Thread(new Runnable()
        {
            public void run()
            {
                ConnectThread join = new ConnectThread(other);
                join.run();
            }
        }).run();

    }

    public void hostGame(View v)
    {
        new Thread(new Runnable()
        {
            public void run()
            {
                AcceptThread host = new AcceptThread();
                host.run();
            }
        }).run();

        /*
        new Thread(new Runnable()
        {
            public void run()
            {
                AcceptThread host = new AcceptThread();
                host.run();
            }
        });
        */

    }


    public class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) { }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                Log.d("run", "going");
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.d("run", "failed accept");
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
                    // Do work to manage the connection (in a separate thread)

                    if(socket.isConnected())
                        Log.d("test", "connected! Accept Thread");
                    startGame(socket);
                    try
                    {
                        //manageConnection manageconnectino = new manageConnection(socket);
                        //manageconnectino.run();
                        mmServerSocket.close();
                    }
                    catch (Exception e)
                    {
                        Log.d("test", "failed to close bluetooth socket");
                    }

                    break;
                }
            }
        }

        /** Will cancel the listening socket, and cause the thread to finish */
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) { }
        }
    }


    public class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private BluetoothAdapter mBluetoothAdapter= BluetoothAdapter.getDefaultAdapter();

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                if(tmp != null)
                    Log.d("test", "here1");
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();
            Log.d("run", "going");
            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();

                if(mmSocket.isConnected())
                    Log.d("test", "connected! Connect Thread");
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    Log.d("test", "closing socket");
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            Log.d("test", "starting game connect thread");
            startGame(mmSocket);

        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    public void startGame(final BluetoothSocket mmSocket)
    {
        Intent intent = new Intent(this, Game.class);
        new Thread(new Runnable()
        {
            public void run()
            {
                manageConnection manageconnection = new manageConnection(mmSocket);
                Log.d("b", "7");
                manageconnection.run();
                Log.d("b", "8");
                manageconnection.write("testing".getBytes());
                Log.d("b", "9");
            }

        }).run();
        startActivity(intent);
    }


    public void startGame(View view)
    {
        Intent intent = new Intent(this, Game.class);
        startActivity(intent);
    }

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
                    Log.d("conn", Integer.toString(bytes));
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
                Log.d("b", "1");
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
}


