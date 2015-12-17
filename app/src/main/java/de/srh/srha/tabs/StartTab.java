package de.srh.srha.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.HashSet;
import java.util.Set;

import de.srh.srha.R;
import de.srh.srha.communication.DownloadFileFromUrl;
import de.srh.srha.model.Profile;
import de.srh.srha.model.ProfileManager;
import de.srh.srha.model.Settings;

public class StartTab extends Fragment {
    static DisplayMetrics display;
    static GridLayout dvb;
    static Downloader download;
    private ProfileManager manager;
    private TextView currentProfileTextView, currentWifiTextView;
    private Profile profile;
    private Settings settings;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.starttab_layout, container, false);

        manager = new ProfileManager(getActivity().getApplicationContext());
        profile = manager.getCurrentProfile();
        settings = profile.settingsManager.getSettings();

        currentProfileTextView = (TextView) v.findViewById(R.id.currentProfileTextView);

        currentWifiTextView = (TextView) v.findViewById(R.id.connectedWifiTextView);

        setUi(profile, settings);
        display = this.getResources().getDisplayMetrics();
        dvb = (GridLayout)v.findViewById(R.id.abfahrtsGrid);

        download = new Downloader();
        ProfileManager manager = new ProfileManager(getActivity().getApplicationContext());
        Profile profile = manager.getCurrentProfile();
        refreshStations(profile.getPreferredDepartureName());
        TextView tv = (TextView) v.findViewById(R.id.textHaltestelle);
        tv.setText(getString(R.string.Abfahrtsmonitor) + ": " + profile.getPreferredDepartureName());
        return v;
    }

    public void setUi(Profile profile, Settings settings){

        if (profile.getProfileName().equals("<default>"))
            Toast.makeText(getActivity(), "There is no profile set!", Toast.LENGTH_SHORT).show();

        currentProfileTextView.setText("Current Profile: " + profile.getProfileName());
        currentWifiTextView.setText("Connected to: " + profile.getAssociatedWifi());
    }



    private void addText(String hst, String linie, String wartezeit){
        TextView txtHst = new TextView(getContext());
        TextView txtLine = new TextView(getContext());
        TextView txtZeit = new TextView(getContext());
        TextView txtBuffer = new TextView(getContext());
        txtHst.setGravity(Gravity.LEFT);
        txtHst.setWidth(display.widthPixels / 3);
        txtHst.setText(hst);

        txtLine.setGravity(Gravity.LEFT);
        txtLine.setWidth(display.widthPixels / 5);
        txtLine.setText(linie);


        txtZeit.setGravity(Gravity.RIGHT);
        txtZeit.setWidth(display.widthPixels / 4);
        txtZeit.setText(wartezeit);

        txtBuffer.setWidth(display.widthPixels / 15);


        dvb.addView(txtZeit, 0);
        dvb.addView(txtHst, 0);
        dvb.addView(txtLine, 0);
        dvb.addView(txtBuffer, 0);


    }

    public void refreshStations(String station){
        download.execute("http://widgets.vvo-online.de/abfahrtsmonitor/Abfahrten.do?ort=Dresden&lim=9&hst=" + station);
    }

    /**************************************************************************************
     * Help Class for download
     **************************************************************************************/
    class Downloader extends DownloadFileFromUrl {

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            dvb.removeAllViews();

            try {
                Set<String> used = new HashSet<String>();
                JSONArray arrayJson = new JSONArray(Source);

                for (int i = arrayJson.length()-1; i >= 0 ; --i) {

                    JSONArray obj = arrayJson.getJSONArray(i);
                    if (!used.contains(obj.get(1))) {
                        //addText(obj.getString(0), obj.getString(2), obj.getString(1) );
                        addText(obj.getString(1), obj.getString(0), obj.getString(2) );
                        used.add(obj.getString(1));
                    }

                }

                addText(getString(R.string.Direction), getString(R.string.Linie), getString(R.string.Abfahrt));

            } catch (Exception e){
                Log.wtf("LOG", e.getMessage());
            }



        }

    }

}