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
    private Encoder encoder = Encoder.getInstance();


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
                if(read == -1)
                {
                    read = in.read();
                }
                read = in.read();
                Log.d("inputstream", String.valueOf((char)read));
            }

            Log.d("stream", "read ->" + String.valueOf(read));
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
            //mmSocket.close();
            //isRunning = false;
            //((Game)parent).enableButtons();

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
        boolean cont = true;
        try{
            if(String.valueOf(Integer.toString(progress[0])).equals("49"))
                out += "1";
            else if(String.valueOf(Integer.toString(progress[0])).equals("48"))
                out += "0";
            else if(String.valueOf(Integer.toString(progress[0])).equals("50"))
            {
                // other stuff
                Log.d("info", "was 50, stopped");
                parent.findViewById(R.id.send).setEnabled(true);
                parent.findViewById(R.id.TextField).setEnabled(true);
                cont = false;
                String temp = textView.getText().toString();
                if(temp.length() > 0)
                {
                    boolean[] t = new boolean[temp.length()];
                    int j = 0;
                    for(char c : temp.toCharArray())
                    {
                        if(c == '1')
                            t[j++] = true;
                        else
                            t[j++] = false;
                    }
                    if(encoder == null)
                    {
                        Log.d("ERR", "WHY NULL??");
                        encoder = Encoder.getInstance();
                    }
                    textView = (TextView)parent.findViewById(R.id.Recent);
                    textView.setText(encoder.decode(t));
                    textView = (TextView) parent.findViewById(R.id.testing);
                    textView.setText(""); // set binary textview to empty
                    //textView = (TextView) parent.findViewById(R.id.TextField);
                }

                //return;
            }
            Log.d("update", String.valueOf(Integer.toString(progress[0])));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        Log.d("string", out );

        if(cont)
        {
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
