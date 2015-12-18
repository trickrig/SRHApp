package de.srh.srha;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;

import de.srh.srha.tabs.ServiceTab;
import de.srh.srha.tabs.SettingsTab;
import de.srh.srha.tabs.StartTab;

public class MainActivity extends FragmentActivity {

    private FragmentTabHost mTabHost;

    //Quelle TabSystem: https://maxalley.wordpress.com/2013/05/18/android-creating-a-tab-layout-with-fragmenttabhost-and-fragments/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(
                mTabHost.newTabSpec("start").setIndicator("Start", null),
                StartTab.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("service").setIndicator("Service", null),
                ServiceTab.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("setting").setIndicator("Settings", null),
                SettingsTab.class, null);

        boolean showSettings = getIntent().getBooleanExtra("de.srh.srha.showSettings", false);
        if (showSettings) { mTabHost.setCurrentTab(2); }
    }
}