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
        //this.encoder = ((ApplicationGlobals) activity.getApplication()).getEncoder();

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
            read = in.read();
            isRunning = true;
            while (read != -1)
            {
                publishProgress(read);
                read = in.read();
                Log.d("inputstream", String.valueOf((char)read));
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
            Log.d("closing", "closed");
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
            if(String.valueOf(Integer.toString(progress[0])).equals("49"))
                out += "1";
            else
                out += "0";
            Log.d("update", String.valueOf(Integer.toString(progress[0])));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        Log.d("string", out );

        boolean[] temp = new boolean[out.length()];

        int i = 0;
        for(char c : out.toCharArray())
        {
            if(c == '1')
                temp[i++] = true;
            else
                temp[i++] = false;
        }
        out = textView.getText() + out;
        textView.setText(out);
        Log.d("codec", "out-> " + out);

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
