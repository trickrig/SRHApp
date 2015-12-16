package de.srh.srha.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import de.srh.srha.R;
import de.srh.srha.model.ProfileManager;

public class ConnectivityReceiver extends BroadcastReceiver {

    private int NOTIFICATION = R.string.app_name;

    @Override
    public void onReceive(Context context, Intent intent) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        NetworkInfo networkInfo = intent.getParcelableExtra(wifiManager.EXTRA_NETWORK_INFO);
        ProfileManager profileManager = new ProfileManager(context.getApplicationContext());
        if (networkInfo.isConnected()) {
            String message = "SRHA: " + networkInfo.getState();
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (!wifiInfo.getSSID().equals("<unknown ssid>")) {
                message += " [" + wifiInfo.getSSID() + "]";
                Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                profileManager.onConnectivityChange(wifiInfo.getSSID());
            }
        } else {
            profileManager.onDisconnectivityChange();
        }
    }

}
