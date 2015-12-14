package de.srh.srha.tabs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.LinkedList;
import java.util.List;

import de.srh.srha.R;
import de.srh.srha.model.Profile;
import de.srh.srha.model.ProfileManager;
import de.srh.srha.model.Settings;
import de.srh.srha.model.dvb;


public class SettingsTab extends Fragment {

    private FragmentTabHost mTabHost;

    private Settings settings;
    private Profile profile;
    private ProfileManager manager;

    private Switch wifiSwitch,bluetoothSwitch, gpsSwitch, mobileSwitch, vibrationSwitch;
    private SeekBar volumeSeekBar;
    private TextView volumeTextView;
    private EditText preferredDeparture, preferredArrival, profilName;
    private Button createProfilButton;

    private ArrayAdapter<String> adapter;
    private List<String> list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.settingstab_layout, container, false);

        manager = new ProfileManager(getActivity());
        profile = manager.getCurrentProfile();
        settings = profile.settingsManager.getSettings();

        //detect all switches
        wifiSwitch = (Switch) v.findViewById(R.id.wifiSwitch);
        bluetoothSwitch = (Switch) v.findViewById(R.id.bluetoothSwitch);
        gpsSwitch = (Switch) v.findViewById(R.id.gpsSwitch);
        mobileSwitch = (Switch) v.findViewById(R.id.mobileSwitch);
        vibrationSwitch = (Switch) v.findViewById(R.id.mobileSwitch);

        //detect edit Text
        preferredArrival = (EditText) v.findViewById(R.id.zielhaltEditText);
        preferredDeparture = (EditText) v.findViewById(R.id.starthaltEditText);
        profilName = (EditText) v.findViewById(R.id.profilNameEditText);

        //detect seekbar & text
        volumeSeekBar = (SeekBar) v.findViewById(R.id.volumeSeekBar);
        volumeTextView = (TextView) v.findViewById(R.id.volumeTextView);

        //detect button
        createProfilButton = (Button) v.findViewById(R.id.createProfilButton);

        setUi();

        //give everythung a changelistener and send information to settings
        wifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setWifiOn(isChecked);
            }
        });

        bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setBluetoothOn(isChecked);
            }
        });

        gpsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setGpsOn(isChecked);
            }
        });

        mobileSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setMobileDataOn(isChecked);
            }
        });

        vibrationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setVibrateOn(isChecked);
                //TODO: if checked, volume seekbar invisible
            }
        });

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                volumeTextView = (TextView) v.findViewById(R.id.volumeTextView);
                volumeTextView.setText("Volume: " + progress);
                //possible int: 0 - 100;
                settings.setRingVolume(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        createProfilButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: switches haben funktion setTextOn setTextOff, automatisieren
                //TODO: text der edit felder automatisch löschen
                //where do i get id from, null has to be getId() Function
                dvb test = new dvb();
                //String IdArrival = test.getIdFromName(preferredArrival.getText().toString());
                //String IdDep = test.getIdFromName(preferredDeparture.getText().toString());
                profile.setPreferredArrival(test.getIdFromName(preferredArrival.getText().toString()),
                      preferredArrival.getText().toString());
                profile.setPreferredDeparture(test.getIdFromName(preferredDeparture.getText().toString()),
                      preferredDeparture.getText().toString());
//              preferredArrival.setText(IdArrival);
//              preferredDeparture.setText(IdDep);
                profile.setProfileName(profilName.getText().toString());
                manager.updateProfile(profile, settings);
//              RoutePlan rout = test.getRoute(IdArrival, IdDep);
//              preferredDeparture.setText(rout.getDestinationTime());
            }
        });

        return v;
    }

    public void createProfile(View v){

        if(v.getId()==R.id.createProfilButton){
            //TODO: switches haben funktion setTextOn setTextOff, automatisieren
            //TODO: text der edit felder automatisch löschen
            //where do i get id from, null has to be getId() Function
            dvb test = new dvb();
            //String IdArrival = test.getIdFromName(preferredArrival.getText().toString());
            //String IdDep = test.getIdFromName(preferredDeparture.getText().toString());
            profile.setPreferredArrival(test.getIdFromName(preferredArrival.getText().toString()),
                    preferredArrival.getText().toString());
            profile.setPreferredDeparture(test.getIdFromName(preferredDeparture.getText().toString()),
                    preferredDeparture.getText().toString());
//            preferredArrival.setText(IdArrival);
//            preferredDeparture.setText(IdDep);
            profile.setProfileName(profilName.getText().toString());
            manager.updateProfile(profile, settings);
//            RoutePlan rout = test.getRoute(IdArrival, IdDep);
//            preferredDeparture.setText(rout.getDestinationTime());
        }
    }

    public void setUi(){
        wifiSwitch.setChecked(settings.isWifiOn());
        bluetoothSwitch.setChecked(settings.isBluetoothOn());
        gpsSwitch.setChecked(settings.isGpsOn());
        vibrationSwitch.setChecked(settings.isVibrateOn());
        mobileSwitch.setChecked(settings.isVibrateOn());
    }

}