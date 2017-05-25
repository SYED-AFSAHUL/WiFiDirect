package com.example.afsahulsyed.wifid3;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by afsahulsyed on 25/5/17.
 */

public class ClientFiles extends IntentService {

    private static final String TAG = "sMess";
    private static final int SOCKET_TIMEOUT = 5000;
    public static final String ACTION_SEND_FILE = "com.example.android.wifidirect.SEND_FILE";
    public static final String EXTRAS_FILE_PATH = "sf_file_url";
    public static final String EXTRAS_GROUP_OWNER_ADDRESS = "sf_go_host";
    public static final String EXTRAS_GROUP_OWNER_PORT = "sf_go_port";
    byte buf[]  = new byte[1024];
    int len;
    //String filePath = "/storage/emulated/0/WhatsApp/Media/WhatsApp Images/IMG-wifid.jpg";
    String filePath = "/sdcard/DCIM/m-7.jpg";

    public ClientFiles(String name) {
        super(name);
        Log.d(TAG,"ClientFiles(String name)");
    }

    public ClientFiles() {
        super("ClientFiles");
        Log.d(TAG,"ClientFiles()");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG,"onHandleIntent- clientFiles");
        Context context = getApplicationContext();

        if (intent.getAction().equals(ACTION_SEND_FILE)) {
            Log.d(TAG,"ACTION_SEND_FILE");

            //String fileUri = intent.getExtras().getString(EXTRAS_FILE_PATH);
            String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);

            Socket socket = new Socket();

            int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);

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
                ContentResolver cr = context.getContentResolver();
                InputStream inputStream = null;
                inputStream = cr.openInputStream(Uri.parse(filePath));//("path/to/picture.jpg"));
                while ((len = inputStream.read(buf)) != -1) {
                    outputStream.write(buf, 0, len);
                }
                outputStream.close();
                inputStream.close();
            } catch (FileNotFoundException e) {
                //catch logic
                e.printStackTrace();
            } catch (IOException e) {
                //catch logic
                e.printStackTrace();
            }

            /**
            * Clean up any open sockets when done
            * transferring or if an exception occurred.
            */
            finally {
                Log.d(TAG,"in finally");
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
}
