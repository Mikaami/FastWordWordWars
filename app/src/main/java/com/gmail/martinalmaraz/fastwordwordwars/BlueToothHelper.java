package com.gmail.martinalmaraz.fastwordwordwars;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * Created by pico on 5/5/16.
 */
public class BlueToothHelper extends AsyncTask<Void, Integer, Long> {
                        // doingbackArg, progressUpdateArg, doInbackReturn

    private BluetoothSocket mmSocket;
    private manageConnection btManager;
    private Activity parent;
    private InputStream in;
    private OutputStream out;
    private boolean isRunning;

    public BlueToothHelper(Activity activity)
    {
        mmSocket = ((ApplicationGlobals)activity.getApplication()).getMmSocket();
        //btManager = new manageConnection(mmSocket);
        parent = activity;
    }

    public boolean status()
    {
        return isRunning;
    }


    protected Long doInBackground(Void... params)
    {
        try
        {
            in = mmSocket.getInputStream();
            out = mmSocket.getOutputStream();

            int BUFFER_SIZE = 1024;
            byte[] buffer = new byte[BUFFER_SIZE];

            int read = 0;
            read = in.read(buffer, read, BUFFER_SIZE - read);
            isRunning = true;
            while (read != -1)
            {
                publishProgress(read);
                read = in.read(buffer, read, BUFFER_SIZE - read);
            }
        }
        catch (Exception e)
        {
            Log.d("helper", "failed", e);
        }



        return null;
    }

    protected void onPostExecute(Long Result)
    {
        try
        {
            mmSocket.close();
            isRunning = false;
        }
        catch (Exception e)
        {
            Log.d("helper", "failed close", e);
        }
    }

    protected void onProgressUpdate(Integer... progress)
    {
        TextView textView = (TextView) parent.findViewById(R.id.testing);
        String out = "";
        try{
            out = new String(toByteArray(progress), "UTF-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        Log.d("string", out );
        textView.setText(out);
    }

    public byte[] toByteArray(Integer[] data)
    {
        Log.d("string2", data.toString());
        int[] newdata = new int[data.length];
        int i = 0;
        for(Integer j : data)
            newdata[i++] = j;
        Log.d("string3", newdata.toString());
        ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * 4);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(newdata);
        return byteBuffer.array();
    }
}
