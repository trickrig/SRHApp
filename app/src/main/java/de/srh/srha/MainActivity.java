package de.srh.srha;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;

import de.srh.srha.tabs.ServiceTab;
import de.srh.srha.tabs.SettingsTab;
import de.srh.srha.tabs.StartTab;

public class MainActivity extends FragmentActivity {

    private FragmentTabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(
                mTabHost.newTabSpec("start").setIndicator("Startbildschirm", null),
                StartTab.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("service").setIndicator("Verbindungen", null),
                ServiceTab.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("setting").setIndicator("Settings", null),
                SettingsTab.class, null);
    }
}