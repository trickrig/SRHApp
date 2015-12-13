package de.srh.srha;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
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
import de.srh.srha.model.Settings;
import de.srh.srha.tabs.ServiceTab;
import de.srh.srha.tabs.SettingsTab;
import de.srh.srha.tabs.StartTab;

public class MainActivity extends FragmentActivity {

    private FragmentTabHost mTabHost;

    private Settings settings = new Settings(false,false,false, false, false, 0);
    private Profile profile = new Profile(null, null, null, null, null,null);

    private Switch wifiSwitch,bluetoothSwitch, gpsSwitch, mobileSwitch, vibrationSwitch;
    private SeekBar volumeSeekBar;
    private TextView volumeTextView;
    private EditText preferredDeparture, preferredArrival, profilName;
    private Spinner spinner;

    private ArrayAdapter<String> adapter;
    private List<String> list;

    private ProfileManager manager;

    //Quelle TabSystem: https://maxalley.wordpress.com/2013/05/18/android-creating-a-tab-layout-with-fragmenttabhost-and-fragments/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO:can i split this into SettingsTab.java and ServiceTab.java?
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = new ProfileManager(getApplicationContext());

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

        //set view on settingstab_layout. else he will search switches on activity_main!
        setContentView(R.layout.settingstab_layout);

        //detect all switches
        wifiSwitch = (Switch) findViewById(R.id.wifiSwitch);
        bluetoothSwitch = (Switch) findViewById(R.id.bluetoothSwitch);
        gpsSwitch = (Switch) findViewById(R.id.gpsSwitch);
        mobileSwitch = (Switch) findViewById(R.id.mobileSwitch);
        vibrationSwitch = (Switch) findViewById(R.id.mobileSwitch);

        //give every switch a changelistener and send information to settings
        wifiSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setWifiOn(isChecked);
            }
        });

        bluetoothSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setBluetoothOn(isChecked);
            }
        });

        gpsSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setGpsOn(isChecked);
            }
        });

        mobileSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setMobileDataOn(isChecked);
            }
        });

        vibrationSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setVibrateOn(isChecked);
                //TODO: if checked, volume seekbar invisible
            }
        });

        //detect seekbar
        volumeSeekBar = (SeekBar) findViewById(R.id.volumeSeekBar);

        //give seekbar a changelistener and send information to settings & on ui
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                volumeTextView = (TextView) findViewById(R.id.volumeTextView);
                volumeTextView.setText("Volume: "+progress);
                //possible int: 0 - 100;
                settings.setRingVolume(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //detect edit Text
        preferredArrival = (EditText) findViewById(R.id.zielhaltEditText);
        preferredDeparture = (EditText) findViewById(R.id.starthaltEditText);
        profilName = (EditText) findViewById(R.id.profilNameEditText);

        //detect spinner, actually not safe
        spinner = (Spinner) findViewById(R.id.spinner);
        list = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, R.layout.settingstab_layout, list);
        spinner.setAdapter(adapter);
    }


    public void createProfile(View v){

        LinkedList list;

        if(v.getId()==R.id.createProfilButton){
            //TODO: switches haben funktion setTextOn setTextOff, automatisieren
            //TODO: text der edit felder automatisch löschen
            //where do i get id from, null has to be getId() Function
            profile.setPreferredArrival(null, preferredArrival.getText().toString());
            profile.setPreferredDeparture(null, preferredDeparture.getText().toString());
            profile.setProfileName(profilName.getText().toString());
            manager.updateProfile(profile, settings);
        }
    }
}