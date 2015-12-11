package de.srh.srha.tabs;

/**
 * Created by hendrik on 11.12.15.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.srh.srha.R;

public class SettingsTab extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settingstab_layout, container, false);
        TextView tv = (TextView) v.findViewById(R.id.settingsstring);
        tv.setText("Settings");
        return v;
    }

}