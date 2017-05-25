package com.example.afsahulsyed.wifid3;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by afsahulsyed on 24/5/17.
 */

public class ClientService extends IntentService {

    private static final int SOCKET_TIMEOUT = 5000;
    public static final String ACTION_SEND_FILE = "com.example.android.wifidirect.SEND_DATA";
    public static final String EXTRAS_GROUP_OWNER_ADDRESS = "sd_go_host";
    public static final String EXTRAS_GROUP_OWNER_PORT = "sd_go_port";

    private static final String TAG = "sMess";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ClientService(String name) {
        super(name);
        Log.d(TAG,"ClientService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG,"onHandleIntent");
       // Context context = this.getApplicationContext();
        String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
        int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);
       // int len;
        Socket socket = new Socket();

      //  byte buf[]  = new byte[1024];

        try {
            /**
             * Create a client socket with the host,
             * port, and timeout information.
             */
            socket.bind(null);
            socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);

            /**
             * Create a byte stream from a JPEG file and pipe it to the output stream
             * of the socket. This data will be retrieved by the server device.
             */
            OutputStream outputStream = socket.getOutputStream();
           // ContentResolver cr = context.getContentResolver();
           // InputStream inputStream = null;
           // inputStream = cr.openInputStream(Uri.parse("path/to/picture.jpg"));
           // while ((len = inputStream.read(buf)) != -1) {
                outputStream.write("qwertyuiop".getBytes());
           // }
            outputStream.close();
           // inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //catch logic
        } catch (IOException e) {
            e.printStackTrace();
            //catch logic
        } catch (Exception e){
            e.printStackTrace();
        }

/**
 * Clean up any open sockets when done
 * transferring or if an exception occurred.
 */
        finally {
            if (socket != null) {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        //catch logic
                    }
                }
            }
        }
    }
}
