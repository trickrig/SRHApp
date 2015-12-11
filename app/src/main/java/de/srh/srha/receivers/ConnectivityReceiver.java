package de.srh.srha.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import de.srh.srha.R;

public class ConnectivityReceiver extends BroadcastReceiver {

    private int NOTIFICATION = R.string.app_name;

    @Override
    public void onReceive(Context context, Intent intent) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        NetworkInfo networkInfo = intent.getParcelableExtra(wifiManager.EXTRA_NETWORK_INFO);
        if (networkInfo.isConnected()) {
            String message = "SRHA: " + networkInfo.getState();
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            message += " [" + wifiInfo.getSSID() + "]";
            if (!wifiInfo.getSSID().equals("<unknown ssid>")) {
                Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
