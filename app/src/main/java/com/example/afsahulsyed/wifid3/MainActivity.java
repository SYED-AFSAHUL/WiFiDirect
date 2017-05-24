package com.example.afsahulsyed.wifid3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "sMess";
    private WifiP2pInfo info;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    WifiP2pDevice mDevice;
    private ServerService mDataTask;

    Button discoverBT;
    Button connectBT;
    Button sendDataBT;
    Button receiveDataBT;
    NumberPicker deviceNoNP;

    private List peers = new ArrayList();
    //private List<HashMap<String, String>> peersshow = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initIntentFilter();
        initReceiver();
        initEvents();
    }

    private void initView() {
        discoverBT = (Button) findViewById(R.id.button);
        connectBT = (Button)findViewById(R.id.button2);
        sendDataBT = (Button)findViewById(R.id.buttonC);
        receiveDataBT = (Button)findViewById(R.id.buttonS);
        deviceNoNP = (NumberPicker)findViewById(R.id.numberPicker);
        deviceNoNP.setMinValue(1);
        deviceNoNP.setWrapSelectorWheel(true);
    }

    private void initIntentFilter() {
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    private void initReceiver() {
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        WifiP2pManager.PeerListListener mPeerListListerner = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peersList) {
                peers.clear();
                // peersshow.clear();
                Collection<WifiP2pDevice> aList = peersList.getDeviceList();
                peers.addAll(aList);

                deviceNoNP.setMaxValue(aList.size());

                for (int i = 0; i < aList.size(); i++) {
                    WifiP2pDevice a = (WifiP2pDevice) peers.get(i);
                    // HashMap<String, String> map = new HashMap<String, String>();
                    // map.put("name", a.deviceName);
                    //  map.put("address", a.deviceAddress);
                    //    peersshow.add(map);
                    Log.d(TAG,"device name -; " + a.deviceName + "  address -: " + a.deviceAddress);
                }
            }
        };

        WifiP2pManager.ConnectionInfoListener mInfoListener = new WifiP2pManager.ConnectionInfoListener() {

            @Override
            public void onConnectionInfoAvailable(final WifiP2pInfo minfo) {

                Log.i("xyz", "InfoAvailable is on");
                info = minfo;
                TextView view = (TextView) findViewById(R.id.textView);
                if (info.groupFormed && info.isGroupOwner) {
                    Log.i("xyz", "owmer start");

                   // mServerTask = new FileServerAsyncTask(MainActivity.this, view);
                   // mServerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    mDataTask = new ServerService(getApplicationContext(), view);
                    mDataTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                } else if (info.groupFormed) {
                    SetButtonVisible();
                }
            }
        };

        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel,mPeerListListerner,
                                                        this, mInfoListener);
    }

    private void SetButtonVisible() {
        //sendpicture.setVisibility(View.VISIBLE);
        sendDataBT.setVisibility(View.VISIBLE);
    }


    private void initEvents() {

        discoverBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DiscoverPeers();
            }
        });

        connectBT.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int no = deviceNoNP.getValue();
                mDevice = (WifiP2pDevice) peers.get(no);
                CreateConnect(mDevice);
            }
        });

        //Client
        sendDataBT.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(MainActivity.this,
                        ClientService.class);

                serviceIntent.setAction(ClientService.ACTION_SEND_FILE);

                serviceIntent.putExtra(ClientService.EXTRAS_GROUP_OWNER_ADDRESS,
                        info.groupOwnerAddress.getHostAddress());
                Log.i("address", "owenerip is " + info.groupOwnerAddress.getHostAddress());
                serviceIntent.putExtra(ClientService.EXTRAS_GROUP_OWNER_PORT,
                        8888);
                MainActivity.this.startService(serviceIntent);
            }
        });
    }

    private void DiscoverPeers() {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            }
            @Override
            public void onFailure(int reasonCode) {
            }
        });
    }

    private void CreateConnect(WifiP2pDevice device) {
        WifiP2pConfig config = new WifiP2pConfig();

        config.deviceAddress = device.deviceAddress;

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int reason) {
            }
        });
    }



    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }
}
