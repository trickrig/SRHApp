/**
 * QUellen für das Vorschlagssystem
 * http://stackoverflow.com/questions/17857334/android-autocompletetextview-only-works-when-backspacing
 * http://stackoverflow.com/questions/19858843/how-to-dynamically-add-suggestions-to-autocompletetextview-with-preserving-chara
 * LiveHttpHeader
 * https://www.dvb.de/apps/pointfinder/index?query=dor
 */
package de.srh.srha.tabs;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.srh.srha.R;
import de.srh.srha.communication.AsyncTaskPool;
import de.srh.srha.communication.DownloadFileFromUrl;
import de.srh.srha.model.Profile;
import de.srh.srha.model.ProfileManager;
import de.srh.srha.model.Settings;
import de.srh.srha.model.dvb;
import android.widget.Filter;

public class SettingsTab extends Fragment implements AdapterView.OnItemSelectedListener {
    private boolean inUseDownloader=false;
    private Settings settings;
    private Profile profile;
    private ProfileManager manager;
    private Filter filterDeparture, filterArrival;
    private Switch wifiSwitch,bluetoothSwitch, gpsSwitch, mobileSwitch, vibrationSwitch;
    private SeekBar volumeSeekBar;
    private TextView volumeTextView, profilNameTextView;
    private EditText  profilName;
    private AutoCompleteTextView preferredDeparture, preferredArrival;
    private Button createProfilButton;
    private Spinner wifiSpinner;
    private String selectedSpinnerItem;
    private ArrayAdapter<String> adapterDeparture, adapterArrival;
    private int maxVolume;

    //TODO volume in percent
    private float volumeValue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("LOG", "OnCreate SettingsTab");
        final View v = inflater.inflate(R.layout.settingstab_layout, container, false);

        manager = new ProfileManager(getActivity().getApplicationContext());
        profile = manager.getCurrentProfile();
        settings = profile.settingsManager.getSettings();

        wifiSwitch = (Switch) v.findViewById(R.id.wifiSwitch);
        bluetoothSwitch = (Switch) v.findViewById(R.id.bluetoothSwitch);
        gpsSwitch = (Switch) v.findViewById(R.id.gpsSwitch);
        mobileSwitch = (Switch) v.findViewById(R.id.mobileSwitch);
        vibrationSwitch = (Switch) v.findViewById(R.id.vibrationSwitch);

        preferredArrival = (AutoCompleteTextView) v.findViewById(R.id.zielhaltEditText);
        preferredDeparture = (AutoCompleteTextView) v.findViewById(R.id.starthaltEditText);
        preferredArrival.setThreshold(3);
        preferredDeparture.setThreshold(3);
        profilName = (EditText) v.findViewById(R.id.profilNameEditText);

        volumeSeekBar = (SeekBar) v.findViewById(R.id.volumeSeekBar);
        maxVolume = profile.settingsManager.getMaxVolume(getActivity().getApplicationContext());
        volumeSeekBar.setMax(maxVolume);

        volumeTextView = (TextView) v.findViewById(R.id.volumeTextView);
        profilNameTextView = (TextView) v.findViewById(R.id.profilNameTextView);

        createProfilButton = (Button) v.findViewById(R.id.createProfilButton);

        wifiSpinner = (Spinner) v.findViewById(R.id.wifiSpinner);
        wifiSpinner.setOnItemSelectedListener(this);

        fillConfiguredNetworksSpinner();

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
// FIlter
        filterDeparture = new Filter() {
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                Log.i("LOG",
                        "Filter Departure:" + constraint + " thread: " + Thread.currentThread());
                if (constraint != null) {
                    Log.i("LOG", "doing a search FIlter Departure..");
                    AsyncTaskPool.execute(new AdapterUpdaterTaskDeparture());
                }
                return null;
            }
        };

        filterArrival = new Filter() {
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                Log.i("LOG",
                        "Filter Arival:" + constraint + " thread: " + Thread.currentThread());
                if (constraint != null) {
                    Log.i("LOG", "doing a search Filter Arrival");
                    AsyncTaskPool.execute(new AdapterUpdaterTaskArrival());
                }
                return null;
            }
        };

        adapterDeparture = new ArrayAdapter<String>(preferredDeparture.getContext(),
                android.R.layout.simple_dropdown_item_1line) {
            public android.widget.Filter getFilter() {
                Log.i("LOG", "Return Filter Departure");
                return filterDeparture;
            }
        };

        adapterArrival = new ArrayAdapter<String>(preferredArrival.getContext(),
                android.R.layout.simple_dropdown_item_1line) {
            public android.widget.Filter getFilter() {
                Log.i("LOG", "Return Filter Arrival");
                return filterArrival;
            }
        };

        preferredArrival.setAdapter(adapterArrival);
        preferredDeparture.setAdapter(adapterDeparture);
        adapterArrival.setNotifyOnChange(false);
        adapterDeparture.setNotifyOnChange(false);

        preferredDeparture.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                preferredDeparture.showDropDown();
                return false;
            }
        });

        preferredArrival.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                preferredArrival.showDropDown();
                return false;
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
                int temp = 0;
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
                //possible int: 0 - max from function;
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
                Log.i("DVB", "Load ID for " + preferredArrival.getText().toString());
                profile.setPreferredArrival(test.getIdFromName(preferredArrival.getText().toString()),
                        preferredArrival.getText().toString());
                Log.i("DVB", "Load ID for " + preferredArrival.getText().toString());
                profile.setPreferredDeparture(test.getIdFromName(preferredDeparture.getText().toString()),
                        preferredDeparture.getText().toString());

                profile.setProfileName(profilName.getText().toString());
                profile.addWifi(selectedSpinnerItem);
                manager.updateProfile(profile, settings);

                preferredArrival.setText("");
                preferredDeparture.setText("");
                profilName.setText("");
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

        profilName.setText(profile.getProfileName(), TextView.BufferType.EDITABLE);
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
        Profile p = manager.getProfileBySsid(selectedSpinnerItem);
        if (p == null){
            Toast.makeText(getActivity(), "No Profile for " + selectedSpinnerItem, Toast.LENGTH_SHORT).show();
            profile = manager.getCurrentProfile();
            createProfilButton.setText("Create Profile");

        } else {
            Toast.makeText(getActivity(), "Loaded Profile: " + profile.getProfileName(), Toast.LENGTH_SHORT).show();
            profile = p;
            createProfilButton.setText("Update Profile");
        }
        settings = profile.settingsManager.getSettings();

        setUi(profile, settings);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        selectedSpinnerItem = null;
    }

    /*************************************************************************************
     * Helper Class for download
     **************************************************************************************/
    class Downloader extends DownloadFileFromUrl{

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            inUseDownloader=false;
        }
    }

    /**************************************************************************************
     * Helper Class for Update
     **************************************************************************************/


    public class AdapterUpdaterTaskDeparture extends AsyncTask<String, String, String> {
        private Downloader downloader = new Downloader();
        private String hst;
        @Override
        protected void onPreExecute() {
            Log.i("LOG", "Pre Execute Apapter Departure");
            super.onPreExecute();
            String buffer = preferredDeparture.getText().toString();
            if(buffer.length() >= 3) {
                if(downloader.isFinished() || downloader.getStatus() == AsyncTask.Status.PENDING){

                        downloader.execute("https://www.dvb.de/apps/pointfinder/index?query=".concat(buffer));
                } else {
                    downloader.cancel(true);
                    downloader = new Downloader();
                    downloader.execute("https://www.dvb.de/apps/pointfinder/index?query=".concat(buffer));

                }
            }else {
                cancel(true);
            }
        }

        @Override
        protected String doInBackground(String... hst) {
            Log.i("LOG", "AdapterUpdaterTaskDeparture DoInBackGround");
            while(!downloader.isFinished() && !downloader.isCancelled()){

            }
            return "";
        }

        @Override
        protected void onPostExecute(String aVoid) {
            Log.i("LOG", "onPostExecute");

            String Source = downloader.getSource();
            if(Source.length() < 5){
                Source = "Unbekannt";
            } else {
                Source = Source.substring(Source.indexOf("|") + 1);
                Source = Source.substring(Source.indexOf("|") + 1);
                Source = Source.substring(Source.indexOf("|") + 1);
                Source = Source.substring(0, Source.indexOf("|"));
            }


            adapterDeparture.clear();
            adapterDeparture.add(Source);

            adapterDeparture.notifyDataSetChanged();
            preferredDeparture.showDropDown();

            super.onPostExecute(aVoid);
        }
    }


    public class AdapterUpdaterTaskArrival extends AsyncTask<String, String, String> {
        private Downloader downloader = new Downloader();
        @Override
        protected void onPreExecute() {
            Log.i("LOG", "Pre Execute Apapter Arrival");
            super.onPreExecute();
            String buffer = preferredArrival.getText().toString();
            if(buffer.length() >= 3) {
                if(downloader.isFinished() || downloader.getStatus() == AsyncTask.Status.PENDING){
                    downloader.execute("https://www.dvb.de/apps/pointfinder/index?query=".concat(buffer));
                } else {
                    downloader.cancel(true);
                    downloader = new Downloader();
                    downloader.execute("https://www.dvb.de/apps/pointfinder/index?query=".concat(buffer));
                }

            }else {
                cancel(true);
            }
        }

        @Override
        protected String doInBackground(String... hst) {
            Log.i("LOG", "In Background Adapter Arrival");
            while(!downloader.isFinished() && !downloader.isCancelled()){

            }
            Log.i("LOG", "Finished Download");
            return "";
        }

        @Override
        protected void onPostExecute(String aVoid) {
            Log.i("LOG", "Arrival Post execute");

            String Source = downloader.getSource();
            if(Source.length() < 5){
                Source = "Unbekannt";
            }else {
                Source = Source.substring(Source.indexOf("|") + 1);
                Source = Source.substring(Source.indexOf("|") + 1);
                Source = Source.substring(Source.indexOf("|") + 1);
                Source = Source.substring(0, Source.indexOf("|"));
            }


            adapterArrival.clear();
            adapterArrival.add(Source);

            adapterArrival.notifyDataSetChanged();
            preferredArrival.showDropDown();

            super.onPostExecute(aVoid);
        }
    }
}