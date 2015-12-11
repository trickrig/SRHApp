package de.srh.srha.tabs;

/**
 * Created by hendrik on 11.12.15.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import de.srh.srha.R;

public class StartTab extends Fragment {

    private Switch wifiSwitch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.starttab_layout, container, false);
        return v;
    }

}