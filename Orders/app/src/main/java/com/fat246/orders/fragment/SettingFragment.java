package com.fat246.orders.fragment;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.fat246.orders.MyApplication;
import com.fat246.orders.R;

/**
 * Created by ken on 16-7-17.
 */
public class SettingFragment extends PreferenceFragment {

    private EditTextPreference serverIp;
    private EditTextPreference serverPort;

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        serverIp = (EditTextPreference) findPreference("setting_server_ip");
        serverPort = (EditTextPreference) findPreference("setting_server_port");

        serverIp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {

                MyApplication.setServerIp(o.toString().trim());

                return true;
            }
        });

        serverPort.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {

                MyApplication.setServerPort(o.toString().trim());

                return true;
            }
        });
    }
}