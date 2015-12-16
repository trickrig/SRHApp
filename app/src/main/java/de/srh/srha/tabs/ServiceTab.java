package de.srh.srha.tabs;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;

import de.srh.srha.R;
import de.srh.srha.communication.DownloadFileFromUrl;
import de.srh.srha.model.Profile;
import de.srh.srha.model.ProfileManager;

public class ServiceTab extends Fragment {
    static DisplayMetrics display;
    static GridLayout dvb;
    static Downloader download;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.servicetab_layout, container, false);
        TextView tv = (TextView) v.findViewById(R.id.servicestring);

        display = this.getResources().getDisplayMetrics();
        dvb = (GridLayout)v.findViewById(R.id.abfahrtGrid);
        download = new Downloader();
        ProfileManager manager = new ProfileManager(getActivity().getApplicationContext());
        Profile profile = manager.getCurrentProfile();
        refreshStations(profile.getPreferredArrivalName());
        tv.setText("Abfahrsmonitor: " + profile.getPreferredArrivalName());
        return v;

    }


    private void addText(String text){
        TextView txt = new TextView(getContext());
        txt.setWidth(display.widthPixels / 4);
        txt.setText(text);
        dvb.addView(txt, 0);
    }

    public void refreshStations(String station){
        download.execute("http://widgets.vvo-online.de/abfahrtsmonitor/Abfahrten.do?ort=Dresden&lim=9&hst=" + station);
    }

    /**************************************************************************************
     * Help Class for download
     **************************************************************************************/
    class Downloader extends DownloadFileFromUrl{

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
                        addText(obj.getString(2));
                        addText(obj.getString(1));
                        addText(obj.getString(0));
                        used.add(obj.getString(1));
                    }

                }
                addText("Wartezeit");
                addText("Richtung");
                addText("Linie");

            } catch (Exception e){
                addText(e.getMessage());
            }


        }

    }
}