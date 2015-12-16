package de.srh.srha.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import de.srh.srha.R;
import de.srh.srha.model.Profile;
import de.srh.srha.model.ProfileManager;
import de.srh.srha.model.Settings;

public class StartTab extends Fragment {

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

        return v;
    }

    public void setUi(Profile profile, Settings settings){

        currentProfileTextView.setText(profile.getProfileName());
        currentWifiTextView.setText(profile.getAssociatedWifi());

    }


    //TODO change the textViews on the starttab_layout.xml

}