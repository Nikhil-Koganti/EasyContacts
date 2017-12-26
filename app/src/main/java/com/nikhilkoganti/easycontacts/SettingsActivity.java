package com.nikhilkoganti.easycontacts;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.nikhilkoganti.easycontacts.R;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    CheckBoxPreference pref = (CheckBoxPreference) findPreference("pref_DarkTheme");

    public static final String KEY_DARK_THEME = "pref_DarkTheme";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_DARK_THEME)) {
            Preference darkThemePref = findPreference(key);
            // Set summary to be the user-description for the selected value
//            connectionPref.setSummary(sharedPreferences.getString(key, ""));
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String theme =  pref.isChecked() ? "Dark" : "Light";
            Log.i("Theme changed to ", theme);
            editor.putString(KEY_DARK_THEME, theme);
            editor.commit();

        }

    }
}
