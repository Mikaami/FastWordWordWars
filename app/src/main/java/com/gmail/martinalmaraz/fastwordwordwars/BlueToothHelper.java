package com.gmail.martinalmaraz.fastwordwordwars;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.InputStream;
import java.io.OutputStream;

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

            byte[] buffer = new byte[1024];

            int read = in.read(buffer);
            isRunning = true;
            while (read != -1)
            {
                publishProgress(read);
                read = in.read(buffer);
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
        textView.setText(progress.toString());
    }
}
