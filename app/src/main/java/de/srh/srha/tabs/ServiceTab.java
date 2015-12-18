package de.srh.srha.tabs;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;

import de.srh.srha.R;
import de.srh.srha.communication.AsyncTaskPool;
import de.srh.srha.communication.DownloadFileFromUrl;
import de.srh.srha.model.Profile;
import de.srh.srha.model.ProfileManager;
import de.srh.srha.model.RouteConnection;
import de.srh.srha.model.RoutePlan;
import de.srh.srha.model.RouteStation;
import de.srh.srha.model.Settings;
import de.srh.srha.model.dvb;

public class ServiceTab extends Fragment {
    private TextView startHaltestelle, zielHaltestelle;
    private ProfileManager manager;
    static DisplayMetrics display;
    private Profile profile;
    private Settings settings;
    static private LoadRoute loader;
    static private dvb routeLoader;
    private GridLayout grid;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.servicetab_layout, container, false);
        manager = new ProfileManager(getActivity().getApplicationContext());
        profile = manager.getCurrentProfile();
        settings = profile.settingsManager.getSettings();


        routeLoader = new dvb();
        AsyncTaskPool.execute(new LoadRoute());

        grid = (GridLayout) v.findViewById(R.id.gridVerb);
        display = this.getResources().getDisplayMetrics();
        Log.i("Profil", "ServiceTab: " + profile.toString());
        return v;

    }

    class LoadRoute extends AsyncTask<String, String, String>{

        private RoutePlan route;
        protected void onPreExecute() {

            Log.i("RouteLoader", "OnPreExecute");
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            Log.i("RouteLoader", "Start in Background");
            route = routeLoader.getRoute(profile.getPreferredDepartureId(), profile.getPreferredArrivalId());

            return "";
        }

        @Override
        protected void onPostExecute(String file_url) {
            Log.i("RouteLoader", "Route found");
            RouteConnection[] r = route.getConnections();
            Log.i("RouteLoader", "Lenth of Route" + Integer.toString(r.length));

            for(int i=r.length-1; i>= 0; --i){
                Log.i("RouteLoader", Integer.toString(i) + " : " + r[i].getStartStation() + " -> " +
                        r[i].getZielStation() + " Zeit: " + r[i].getZeit() + " Tram " + r[i].getLinie());
                addText(r[i].getStartStation(), r[i].getZielStation(), r[i].getZeit(), r[i].getLinie());

            }
            //*/


            addText(getString(R.string.Abfahrthaltestelle), getString(R.string.ZielhalteStelle), getString(R.string.startzeit), getString(R.string.Linie));
        }

        private void addText(String haltestelleStart, String haltestelleZiel, String Abfahrtszeit,
                             String AbfahrtLinie){
            TextView txtAbfahrtLinie = new TextView(getContext());
            TextView txtHaltestelleStart = new TextView(getContext());
            TextView txtHaltestelleZiel = new TextView(getContext());
            TextView txtAbfahrtZeit= new TextView(getContext());




            //txtAbfahrtLinie.setGravity(Gravity.LEFT);
            txtAbfahrtLinie.setWidth(display.widthPixels / 6);
            txtAbfahrtLinie.setText(AbfahrtLinie);
     //       txtAbfahrtLinie.setHighlightColor(0);

           // txtAbfahrtZeit.setGravity(Gravity.LEFT);
            txtAbfahrtZeit.setWidth(display.widthPixels / 4);
            txtAbfahrtZeit.setText(Abfahrtszeit);
      //      txtAbfahrtLinie.setHighlightColor(255);

           // txtHaltestelleZiel.setGravity(Gravity.LEFT);
            txtHaltestelleZiel.setWidth(display.widthPixels / 4);
            txtHaltestelleZiel.setText(haltestelleZiel);
     //       txtAbfahrtLinie.setHighlightColor(65000);

           // txtHaltestelleStart.setGravity(Gravity.LEFT);
            txtHaltestelleStart.setWidth(display.widthPixels / 3);
            txtHaltestelleStart.setText(haltestelleStart);
     //       txtAbfahrtLinie.setHighlightColor(12700000);

            grid.addView(txtHaltestelleZiel, 0);
            grid.addView(txtAbfahrtZeit, 0);
            grid.addView(txtAbfahrtLinie, 0);
            grid.addView(txtHaltestelleStart, 0);


        }

    }

}