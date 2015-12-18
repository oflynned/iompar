package com.syzible.iompar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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
                moreCategory.setTitle("More from GlassByte");
                moreCategory.setKey(getString(R.string.pref_key_contact));

                PreferenceCategory appSettings = new PreferenceCategory(getActivity());
                appSettings.setTitle("Personal");
                appSettings.setKey(getString(R.string.pref_key_app_settings));

                PreferenceCategory languageSettings = new PreferenceCategory(getActivity());
                languageSettings.setTitle("Language Options");
                languageSettings.setKey(getString(R.string.pref_key_language));

                //individual settings for more from glassbyte
                Preference moreFromGlassByte = new Preference(getActivity());
                Preference librariesUsed = new Preference(getActivity());
                Preference apacheLicence = new Preference(getActivity());

                moreFromGlassByte.setTitle("More Stuff");
                moreFromGlassByte.setSummary("Click here to see more of our apps on Google Play.");
                moreFromGlassByte.setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/developer?id=GlassByte")));

                librariesUsed.setTitle("Libraries Used");
                librariesUsed.setSummary("Here's a list of the libraries used by the developers.");
                librariesUsed.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Title")
                                .setMessage("Message")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();
                        return true;
                    }
                });

                apacheLicence.setTitle("Apache Licence");
                apacheLicence.setSummary("Here's some information about software distribution.");
                apacheLicence.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Title")
                                .setMessage("Message")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(getActivity(), "lol you didn't read this", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .show();
                        return true;
                    }
                });

                //app settings for payment choice
                SwitchPreference preferLeap = new SwitchPreference(getActivity());
                preferLeap.setTitle("Prefer Leap");
                preferLeap.setSummary("Select this if you pay with a Leap card frequently.");
                preferLeap.setKey(getString(R.string.pref_key_prefer_leap));
                preferLeap.setDefaultValue(false);

                //language settings
                SwitchPreference irishLanguage = new SwitchPreference(getActivity());
                irishLanguage.setTitle("Irish Language");
                irishLanguage.setSummary("Select this to use this app in Irish Gaelic.");
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

                appSettings.addPreference(preferLeap);

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
