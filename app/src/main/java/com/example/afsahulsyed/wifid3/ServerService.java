package com.example.afsahulsyed.wifid3;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by afsahulsyed on 24/5/17.
 */

public class ServerService extends AsyncTask<Void, Void, String> {

    private Context context;
    private TextView statusText;
    private static final String TAG = "sMess";

    public ServerService(Context context, View statusText) {
        this.context = context;
        this.statusText = (TextView) statusText;
        Log.d(TAG,"ServerService");
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            Log.d(TAG,"doInBackground");
            /**
             * Create a server socket and wait for client connections. This
             * call blocks until a connection is accepted from a client
             */
            ServerSocket serverSocket = new ServerSocket(8888);
            Socket client = serverSocket.accept();

            /**
             * If this code is reached, a client has connected and transferred data
             * Save the input stream from the client as a JPEG file
             */
           /* final File f = new File(Environment.getExternalStorageDirectory() + "/"
                    + context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
                    + ".jpg");

            File dirs = new File(f.getParent());
            if (!dirs.exists())
                dirs.mkdirs();
            f.createNewFile();*/
            InputStream inputstream = client.getInputStream();
            //copyFile(inputstream, new FileOutputStream(f));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int i;
            while ((i = inputstream.read()) != -1) {
                baos.write(i);
            }

            String str = baos.toString();
            serverSocket.close();
            //return f.getAbsolutePath();
            return str;
        } catch (IOException e) {
           // Log.e(WiFiDirectActivity.TAG, e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Start activity that can handle the JPEG image
     */
    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(context, "result"+result, Toast.LENGTH_SHORT).show();

        if (result != null) {
            statusText.setText("Data-String is " + result);
        }
    }
}
