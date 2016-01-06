package com.glassbyte.iompar;

import android.content.Context;
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
                Preference facebook = new Preference(getActivity());
                Preference twitter = new Preference(getActivity());
                Preference rateListing = new Preference(getActivity());

                moreFromGlassByte.setTitle(R.string.more_apps);
                moreFromGlassByte.setSummary(R.string.click_here_for_more_apps);
                moreFromGlassByte.setIntent(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/developer?id=GlassByte")));

                facebook.setTitle(R.string.follow_on_fb);
                facebook.setIntent(getOpenAppIntent(getApplicationContext(), "com.facebook.katana",
                        "fb://facewebmodal/f?href=http://www.facebook.com/GlassByte", "http://www.facebook.com/GlassByte"));

                twitter.setTitle(R.string.follow_on_twitter);
                twitter.setIntent(getOpenAppIntent(getApplicationContext(), "com.twitter.android",
                        "twitter://user?user_id=id_num", "http://www.twitter.com/GlassByte"));

                rateListing.setTitle(R.string.rate_app);
                rateListing.setIntent(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=com.glassbyte.iompar")));

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

                final SwitchPreference syncOnStart = new SwitchPreference(getActivity());
                syncOnStart.setTitle(R.string.sync_on_app_start);
                syncOnStart.setSummary(R.string.sync_on_app_start_desc);
                syncOnStart.setKey(getString(R.string.pref_key_leap_sync));
                syncOnStart.setDefaultValue(false);

                //language settings
                final SwitchPreference irishLanguage = new SwitchPreference(getActivity());
                irishLanguage.setTitle(R.string.irish_language);
                irishLanguage.setSummary(R.string.irish_language_summary);
                irishLanguage.setKey(getString(R.string.pref_key_irish));
                irishLanguage.setDefaultValue(false);

                //parent categories
                preferenceScreen.addPreference(moreCategory);
                preferenceScreen.addPreference(languageSettings);
                preferenceScreen.addPreference(appSettings);

                //child preferences
                moreCategory.addPreference(moreFromGlassByte);
                moreCategory.addPreference(facebook);
                moreCategory.addPreference(twitter);
                moreCategory.addPreference(rateListing);

                languageSettings.addPreference(irishLanguage);

                appSettings.addPreference(namePreference);
                appSettings.addPreference(farePreference);
                appSettings.addPreference(syncOnStart);

                setPreferenceScreen(preferenceScreen);
            }

            @Override
            public void onActivityCreated(Bundle savedInstanceState) {
                super.onActivityCreated(savedInstanceState);
            }
        }).commit();
    }

    public Intent getOpenAppIntent(Context context, String app, String linkApp, String linkWeb) {
        try {
            context.getPackageManager().getPackageInfo(app, 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse(linkApp));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse(linkWeb));
        }
    }
}
