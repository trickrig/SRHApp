package de.srh.srha;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.srh.srha.model.Profile;
import de.srh.srha.model.ProfileManager;
import de.srh.srha.model.RoutePlan;
import de.srh.srha.model.Settings;
import de.srh.srha.model.dvb;
import de.srh.srha.tabs.ServiceTab;
import de.srh.srha.tabs.SettingsTab;
import de.srh.srha.tabs.StartTab;

public class MainActivity extends FragmentActivity {

    /*
    Tabs müssen gemacht werden
    alles ordentlich und profilname hoch

    alle aktuellen profile müssten geladen werden mit knopf neues profil hinzufügen mit eigenschaften des currentProfils
     */

    private FragmentTabHost mTabHost;

    //Quelle TabSystem: https://maxalley.wordpress.com/2013/05/18/android-creating-a-tab-layout-with-fragmenttabhost-and-fragments/
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