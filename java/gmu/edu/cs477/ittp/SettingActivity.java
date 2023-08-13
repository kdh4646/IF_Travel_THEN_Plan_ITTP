package gmu.edu.cs477.ittp;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.setting_preferences, rootKey);

            //Get PreferenceCategory Titles
            PreferenceCategory general = (PreferenceCategory)findPreference("general_title");
            PreferenceCategory notification = (PreferenceCategory)findPreference("notification_title");
            PreferenceCategory about = (PreferenceCategory)findPreference("about_title");

            //Get ListPreference Titles
            ListPreference background_color = (ListPreference)findPreference("background_color_preference");
            ListPreference language_list = (ListPreference)findPreference("language_preference");

            //Get Preference About for changing Summary
            Preference about_summary = (Preference)findPreference("about_preference");

            //Get SharedPreferences
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

            //Get "language_preference"
            String language = sharedPreferences.getString("language_preference", "en");

            //If Language preference is changed from SettingActivity
            if(!language.equalsIgnoreCase(""))
            {
                //Language: English
                if(language.equals("en"))
                {
                    getActivity().setTitle("Setting");
                    general.setTitle("General");
                    about.setTitle("About");

                    background_color.setTitle("Background Color");
                    background_color.setDialogTitle("Background Color");

                    language_list.setTitle("Language");
                    language_list.setDialogTitle("Language");

                    about_summary.setSummary("Application for helping Travelers \n \n dkweon2@masonlive.gmu.edu");
                }

                //Language: Korean
                else
                {
                    getActivity().setTitle("설정");
                    general.setTitle("일반");
                    about.setTitle("소개");

                    background_color.setTitle("배경 색");
                    background_color.setDialogTitle("배경 색");

                    language_list.setTitle("언어");
                    language_list.setDialogTitle("언어");

                    about_summary.setSummary("여행하는 사람들을 위한 앱입니다. \n \n dkweon2@masonlive.gmu.edu");
                }
            }
        }
    }
}