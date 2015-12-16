package de.srh.srha.receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import de.srh.srha.model.ProfileManager;

public class WifiExpirator extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ProfileManager profileManager = new ProfileManager(context.getApplicationContext());
        profileManager.wifiExpires();
    }

}
