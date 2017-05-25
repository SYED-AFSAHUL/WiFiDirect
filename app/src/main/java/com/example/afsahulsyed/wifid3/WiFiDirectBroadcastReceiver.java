package com.example.afsahulsyed.wifid3;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;

/**
 * Created by afsahulsyed on 23/5/17.
 */

/**
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 */

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
    private WifiP2pManager.ConnectionInfoListener mInfoListener;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private Activity mActivity;
    WifiP2pManager.PeerListListener mPeerListListener;
    private static final String TAG = "sMess";

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       WifiP2pManager.PeerListListener peerListListener,
                                       Activity activity,
                                       WifiP2pManager.ConnectionInfoListener infoListener) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mPeerListListener = peerListListener;
        this.mActivity = activity;
        this.mInfoListener = infoListener;
        Log.d(TAG, "WiFiDirectBroadcastReceiver");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG,"onReceive");
        switch (action) {
            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
                Log.d(TAG,"WIFI_P2P_STATE_CHANGED_ACTION");
                // Check to see if Wi-Fi is enabled and notify appropriate activity
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    // Wifi P2P is enabled
                    Log.d(TAG,"Wifi P2P is enabled");

                } else {
                    // Wi-Fi P2P is not enabled
                    Log.d(TAG,"Wifi P2P is not enabled");
                    try {
                        Method method1 = mManager.getClass().getMethod("enableP2p", WifiP2pManager.Channel.class);
                        method1.invoke(mManager, mChannel);
                        Toast.makeText(context.getApplicationContext(), "method found",
                               Toast.LENGTH_SHORT).show();
                        Log.d(TAG,"Method found");
                    } catch (Exception e) {
                        Toast.makeText(context.getApplicationContext(), "method did not found",
                                Toast.LENGTH_SHORT).show();
                        Log.d(TAG,"method not found");
                        e.printStackTrace();
                    }
                }

                break;
            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                Log.d(TAG,"");
                // Call WifiP2pManager.requestPeers() to get a list of current peers
                mManager.requestPeers(mChannel, mPeerListListener);
                break;
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                Log.d(TAG,"WIFI_P2P_CONNECTION_CHANGED_ACTION");
                // Respond to new connection or disconnections
                if (mManager == null) {
                    Log.d(TAG,"mManager = null");
                    return;
                }

                NetworkInfo networkInfo = intent.getParcelableExtra(
                                                WifiP2pManager.EXTRA_NETWORK_INFO);

                if (networkInfo.isConnected()) {
                    Log.d(TAG, "Connected");
                    mManager.requestConnectionInfo(mChannel, mInfoListener);
                } else {
                    Log.d(TAG, "DisConnected");
                    return;
                }
                break;
            case WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION:
                Log.d(TAG,"WIFI_P2P_DISCOVERY_CHANGED_ACTION");
                break;
            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                Log.d(TAG,"WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
                // Respond to this device's wifi state changing
                break;
            default :
                Log.d(TAG,"in default in switch, action -: "+ action);

        }
    }
    }
