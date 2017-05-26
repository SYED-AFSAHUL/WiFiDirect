package com.example.afsahulsyed.wifid3;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by afsahulsyed on 25/5/17.
 */

public class ServerFiles extends AsyncTask<Void, Void, String> {

    private static final String TAG = "sMess";
    private Context context;
    private TextView statusText;

    public ServerFiles(){
        Log.d(TAG,"ServerFiles()");
    }

    public ServerFiles(Context context, View statusText) {
        Log.d(TAG,"ServerFiles(Context context, View statusText)");
        this.context = context;
        this.statusText = (TextView) statusText;
    }

    @Override
    protected String doInBackground(Void... params) {
        Log.d(TAG,"doInBackground- serverfiles");
        try {

            Log.d(TAG,"waiting to accept connection.....");
            /**
             * Create a server socket and wait for client connections. This
             * call blocks until a connection is accepted from a client
             */
            ServerSocket serverSocket = new ServerSocket(8988);
            Socket client = serverSocket.accept();
            Log.d(TAG,"Connection Accepted");
            /**
             * If this code is reached, a client has connected and transferred data
             * Save the input stream from the client as a JPEG file
             */
            /*final File f = new File(context.getFilesDir(),
                    "wifip2pshared-" + System.currentTimeMillis()
                            + ".jpg");*/
            /*final File f = new File("/storage/emulated/0/wifid3/",
                    "wifip2pshared-" + System.currentTimeMillis() + ".jpg");*/

           /* final File f = new File(
                    Environment.getExternalStorageDirectory() + "/"
                            + "com.example" + "/wifip2pshared-"
                            + System.currentTimeMillis() + ".jpg");*/
            //final File folder = new File(Environment.getExternalStoragePublicDirectory(Environme‌​nt.DIRECTORY_PICTURE‌​S) + "/Whatever/");

            //File folder = new File(this.getActivity().getExternalFilesDir(null) + IMAGE_DIRECTORY + "whatever you want for your directory name");

            File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            File f = new File(path, "wifip2pshared-" + System.currentTimeMillis()+ ".jpg");


            File dirs = new File(f.getParent());
            try {
                if (!dirs.exists()) {
                    dirs.mkdirs();
                    Log.d(TAG, "!dirs.exists()");
                }
                f.createNewFile();
            }catch (Exception e){
                Log.d(TAG,e.getMessage());
            }
            if (f.exists()) {
                Log.d(TAG, "dir exist");
            }else{
                Log.d(TAG, "dir still doesn't exist");
            }
            InputStream inputstream = client.getInputStream();
            if(copyFile(inputstream, new FileOutputStream(f))){
                Log.d(TAG,"successfully copied");
            } else {
                Log.d(TAG,"error in coping");
            }
            serverSocket.close();
            Log.d(TAG,"exiting doInBackGround");
            return f.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
            return null;
        }
    }

    /**
     * Start activity that can handle the JPEG image
     */
    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG,"onPostExecute");
        try {
            if (result != null) {
                statusText.setText("File copied - " + result);
                Log.d(TAG,"location --- "+ result);
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + result), "image/*");
                context.startActivity(intent);
            }
        }catch (Exception e){
            Log.d(TAG,e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean copyFile(InputStream inputStream, OutputStream out) {
        Log.d(TAG,"copyFile");
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG,e.getMessage());
            return false;
        }
        return true;
    }
}
