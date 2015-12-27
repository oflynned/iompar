package com.glassbyte.iompar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

/**
 * Created by ed on 18/12/15.
 */
public class Settings extends PreferenceActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    PreferenceScreen preferenceScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferenceFragment() {
            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setTheme(R.style.AppTheme);

                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                preferenceScreen = getPreferenceManager().createPreferenceScreen(getActivity());

                //categories
                PreferenceCategory moreCategory = new PreferenceCategory(getActivity());
                moreCategory.setTitle(R.string.more_from_glassbyte);
                moreCategory.setKey(getString(R.string.pref_key_contact));

                PreferenceCategory appSettings = new PreferenceCategory(getActivity());
                appSettings.setTitle(R.string.personal);
                appSettings.setKey(getString(R.string.pref_key_app_settings));

                PreferenceCategory languageSettings = new PreferenceCategory(getActivity());
                languageSettings.setTitle(R.string.language_options);
                languageSettings.setKey(getString(R.string.pref_key_language));

                //individual settings for more from glassbyte
                Preference moreFromGlassByte = new Preference(getActivity());
                Preference librariesUsed = new Preference(getActivity());
                Preference apacheLicence = new Preference(getActivity());

                moreFromGlassByte.setTitle(R.string.more_apps);
                moreFromGlassByte.setSummary(R.string.click_here_for_more_apps);
                moreFromGlassByte.setIntent(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/developer?id=GlassByte")));

                librariesUsed.setTitle(R.string.libraries_used);
                librariesUsed.setSummary(R.string.list_libraries_used);
                librariesUsed.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle(getString(R.string.libraries_used))
                                .setMessage(getString(R.string.list_libraries_used))
                                .setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();
                        return true;
                    }
                });

                apacheLicence.setTitle(getResources().getString(R.string.apache_title));
                apacheLicence.setSummary(getString(R.string.apache_summary));
                apacheLicence.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle(getResources().getString(R.string.apache_title))
                                .setMessage(getResources().getString(R.string.apache_body))
                                .setPositiveButton(getResources().getString(R.string.OK),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                .show();
                        return true;
                    }
                });

                String name = sharedPreferences.getString(getString(R.string.pref_key_name), "");

                //app settings for payment choice
                EditTextPreference namePreference = new EditTextPreference(getActivity());
                namePreference.setTitle(R.string.modify_name);
                namePreference.setSummary(name);
                namePreference.setKey(getString(R.string.pref_key_name));
                namePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        preference.setSummary(String.valueOf(newValue));

                        editor = sharedPreferences.edit();
                        editor.putString(preference.getKey(), String.valueOf(newValue));
                        editor.apply();

                        preference.setSummary(String.valueOf(newValue));

                        return true;
                    }
                });

                Preference farePreference = new ListPreference(getActivity());
                farePreference.setTitle(R.string.modify_fare);
                farePreference.setSummary(sharedPreferences.getString(getString(R.string.pref_key_fare), ""));
                farePreference.setKey(getString(R.string.pref_key_fare));
                ((ListPreference) farePreference).setEntries(R.array.fare_types);
                ((ListPreference) farePreference).setEntryValues(R.array.fare_types);
                farePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        preference.setSummary(String.valueOf(newValue));
                        return true;
                    }
                });

                //language settings
                final SwitchPreference irishLanguage = new SwitchPreference(getActivity());
                irishLanguage.setTitle(R.string.irish_language);
                irishLanguage.setSummary(R.string.irish_language_summary);
                irishLanguage.setKey(getString(R.string.pref_key_irish));
                irishLanguage.setDefaultValue(false);

                //parent categories
                preferenceScreen.addPreference(moreCategory);
                preferenceScreen.addPreference(appSettings);
                preferenceScreen.addPreference(languageSettings);

                //child preferences
                moreCategory.addPreference(moreFromGlassByte);
                moreCategory.addPreference(librariesUsed);
                moreCategory.addPreference(apacheLicence);

                appSettings.addPreference(namePreference);
                appSettings.addPreference(farePreference);

                languageSettings.addPreference(irishLanguage);

                setPreferenceScreen(preferenceScreen);
            }

            @Override
            public void onActivityCreated(Bundle savedInstanceState) {
                super.onActivityCreated(savedInstanceState);
            }
        }).commit();
    }
}
