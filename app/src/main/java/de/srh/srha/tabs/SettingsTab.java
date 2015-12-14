package de.srh.srha.tabs;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

import de.srh.srha.R;
import de.srh.srha.model.Profile;
import de.srh.srha.model.ProfileManager;
import de.srh.srha.model.Settings;
import de.srh.srha.model.dvb;


public class SettingsTab extends Fragment implements AdapterView.OnItemSelectedListener {

    private Settings settings;
    private Profile profile;
    private ProfileManager manager;

    private Switch wifiSwitch,bluetoothSwitch, gpsSwitch, mobileSwitch, vibrationSwitch;
    private SeekBar volumeSeekBar;
    private TextView volumeTextView;
    private EditText preferredDeparture, preferredArrival, profilName;
    private Button createProfilButton, newProfileButton;
    private Spinner wifiSpinner;
    private String selectedSpinnerItem;

    //TODO volume in percent
    private float volumeValue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.settingstab_layout, container, false);

        manager = new ProfileManager(getActivity().getApplicationContext());
        profile = manager.getCurrentProfile();
        settings = profile.settingsManager.getSettings();

        wifiSwitch = (Switch) v.findViewById(R.id.wifiSwitch);
        bluetoothSwitch = (Switch) v.findViewById(R.id.bluetoothSwitch);
        gpsSwitch = (Switch) v.findViewById(R.id.gpsSwitch);
        mobileSwitch = (Switch) v.findViewById(R.id.mobileSwitch);
        vibrationSwitch = (Switch) v.findViewById(R.id.vibrationSwitch);

        preferredArrival = (EditText) v.findViewById(R.id.zielhaltEditText);
        preferredDeparture = (EditText) v.findViewById(R.id.starthaltEditText);
        profilName = (EditText) v.findViewById(R.id.profilNameEditText);

        volumeSeekBar = (SeekBar) v.findViewById(R.id.volumeSeekBar);
        //TODO How to convert max volume to progress on seekbar
        //volumeValue =
        //profile.settingsManager.getMaxVolume(getActivity().getApplicationContext())/100;
        volumeTextView = (TextView) v.findViewById(R.id.volumeTextView);

        createProfilButton = (Button) v.findViewById(R.id.createProfilButton);
        newProfileButton = (Button) v.findViewById(R.id.newProfileButton);

        wifiSpinner = (Spinner) v.findViewById(R.id.wifiSpinner);

        setUi(profile, settings);

        wifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setWifiOn(isChecked);
                if (settings.isWifiOn())
                    wifiSwitch.setText("WiFi On");
                else
                    wifiSwitch.setText("WiFi Off");
            }
        });

        bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setBluetoothOn(isChecked);
                if(settings.isBluetoothOn())
                    bluetoothSwitch.setText("Bluetooth On");
                else
                    bluetoothSwitch.setText("Bluetooth Off");
            }
        });

        gpsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setGpsOn(isChecked);
                if (settings.isGpsOn())
                    gpsSwitch.setText("GPS On");
                else
                    gpsSwitch.setText("GPS Off");
            }
        });

        mobileSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setMobileDataOn(isChecked);
                if (settings.isMobileDataOn())
                    mobileSwitch.setText("Mobile Data On");
                else
                    mobileSwitch.setText("Mobile Data Off");
            }
        });

        vibrationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setVibrateOn(isChecked);
                //TODO: if checked, volume seekbar invisible
                if (settings.isVibrateOn())
                    vibrationSwitch.setText("Vibration On");
                else
                    vibrationSwitch.setText("Vibration Off");
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
                preferredArrival.setText("");
                preferredDeparture.setText("");
                profilName.setText("");
            }
        });

        newProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO können wir schon mehrere Profile mit einer SSID haben?
                Toast.makeText(getActivity(), "Not implemented yet", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    public void setUi(Profile profile, Settings settings){
        //init for ui
        wifiSwitch.setChecked(settings.isWifiOn());
        bluetoothSwitch.setChecked(settings.isBluetoothOn());
        gpsSwitch.setChecked(settings.isGpsOn());
        vibrationSwitch.setChecked(settings.isVibrateOn());
        mobileSwitch.setChecked(settings.isMobileDataOn());

        if (settings.isWifiOn())
            wifiSwitch.setText("WiFi On");
        else
            wifiSwitch.setText("WiFi Off");

        if(settings.isBluetoothOn())
            bluetoothSwitch.setText("Bluetooth On");
        else
            bluetoothSwitch.setText("Bluetooth Off");

        if (settings.isGpsOn())
            gpsSwitch.setText("GPS On");
        else
            gpsSwitch.setText("GPS Off");

        if (settings.isVibrateOn())
            vibrationSwitch.setText("Vibration On");
        else
            vibrationSwitch.setText("Vibration Off");

        if (settings.isMobileDataOn())
            mobileSwitch.setText("Mobile Data On");
        else
            mobileSwitch.setText("Mobile Data Off");

        volumeSeekBar.setProgress(settings.getRingVolume());

        fillConfiguredNetworksSpinner();

        //TODO change the textViews on the starttab_layout.xml
    }

    public void fillConfiguredNetworksSpinner() {
        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> wifis = wifiManager.getConfiguredNetworks();
        List<String> wifiNames = new LinkedList<String>();
        if (wifis != null) {
            for (WifiConfiguration conf : wifis) {
                wifiNames.add(conf.SSID);
            }
        }
        else {
            Toast.makeText(getActivity(), "Wifi must be enabled in order to read configured networks", Toast.LENGTH_SHORT).show();
        }
        if (this.profile.getAssociatedWifi() != null &&
            !wifiNames.contains(this.profile.getAssociatedWifi())) {
            wifiNames.add(this.profile.getAssociatedWifi());
        }
        int positionActive = wifiNames.indexOf(this.profile.getAssociatedWifi());

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, wifiNames);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wifiSpinner.setAdapter(arrayAdapter);
        wifiSpinner.setSelection(positionActive);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        selectedSpinnerItem = parent.getItemAtPosition(pos).toString();
        Toast.makeText(getActivity(), "loaded" + selectedSpinnerItem,
                Toast.LENGTH_SHORT).show();
        //TODO would be nice to have a function like select profile by ssid
        //profile = manager.getProfile(selectedSpinnerItem);
        //settings = profile.settingsManager.getSettings();
        //setUi(profile, settings);
        //change the textViews on starttab_layout.xml
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        selectedSpinnerItem = null;
    }

}