package gmu.edu.cs477.ittp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;

import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    //Set variables
    Button plan;
    Button travel;
    Button history;
    Button setting;

    //Set extra variables for control
    ConstraintLayout main_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize variables
        plan = (Button) findViewById(R.id.plan_button);
        travel = (Button) findViewById(R.id.travel_button);
        history = (Button) findViewById(R.id.history_button);
        setting = (Button) findViewById(R.id.setting_button);
    }

    //Change BackGround Image, Language
    @Override
    public void onResume() {
        super.onResume();

        //Get SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //Get "background_color_preference"
        String background = sharedPreferences.getString("background_color_preference", "color_ivory");

        //If Background preference is changed from SettingActivity
        if(!background.equalsIgnoreCase(""))
        {
            //Background: IVORY
            if(background.equals("color_ivory"))
            {
                main_layout = (ConstraintLayout)findViewById(R.id.activity_main);
                main_layout.setBackgroundResource(R.drawable.main_activity_background_default);
            }

            //Background: RED
            else if(background.equals("color_red"))
            {
                main_layout = (ConstraintLayout)findViewById(R.id.activity_main);
                main_layout.setBackgroundResource(R.drawable.main_activity_background_red);
            }

            //Background: GREEN
            else if(background.equals("color_green"))
            {
                main_layout = (ConstraintLayout)findViewById(R.id.activity_main);
                main_layout.setBackgroundResource(R.drawable.main_activity_background_green);
            }

            //Background: BLUE
            else
            {
                main_layout = (ConstraintLayout)findViewById(R.id.activity_main);
                main_layout.setBackgroundResource(R.drawable.main_activity_background_blue);
            }

            //Get "language_preference"
            String language = sharedPreferences.getString("language_preference", "en");

            //If Language preference is changed from SettingActivity
            if(!language.equalsIgnoreCase(""))
            {
                //Language: English
                if(language.equals("en"))
                {
                    plan.setText("PLAN");
                    travel.setText("TRAVEL");
                    history.setText("HISTORY");
                    setting.setText("SETTING");
                }

                //Language: Korean
                else
                {
                    plan.setText("일정");
                    travel.setText("여행");
                    history.setText("기록");
                    setting.setText("설정");
                }
            }
        }
    }

    //Change Background Image, Language
    @Override
    public void onPause() {
        super.onPause();

        //Get SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //Get "background_color_preference"
        String background = sharedPreferences.getString("background_color_preference", "color_ivory");

        //If Background preference is changed from SettingActivity
        if(!background.equalsIgnoreCase(""))
        {
            //Background: IVORY
            if(background.equals("color_ivory"))
            {
                main_layout = (ConstraintLayout)findViewById(R.id.activity_main);
                main_layout.setBackgroundResource(R.drawable.main_activity_background_default);
            }

            //Background: RED
            else if(background.equals("color_red"))
            {
                main_layout = (ConstraintLayout)findViewById(R.id.activity_main);
                main_layout.setBackgroundResource(R.drawable.main_activity_background_red);
            }

            //Background: GREEN
            else if(background.equals("color_green"))
            {
                main_layout = (ConstraintLayout)findViewById(R.id.activity_main);
                main_layout.setBackgroundResource(R.drawable.main_activity_background_green);
            }

            //Background: BLUE
            else
            {
                main_layout = (ConstraintLayout)findViewById(R.id.activity_main);
                main_layout.setBackgroundResource(R.drawable.main_activity_background_blue);
            }

            //Get "language_preference"
            String language = sharedPreferences.getString("language_preference", "en");

            //If Language preference is changed from SettingActivity
            if(!language.equalsIgnoreCase(""))
            {
                //Language: English
                if(language.equals("en"))
                {
                    plan.setText("PLAN");
                    travel.setText("TRAVEL");
                    history.setText("HISTORY");
                    setting.setText("SETTING");
                }

                //Language: Korean
                else
                {
                    plan.setText("일정");
                    travel.setText("여행");
                    history.setText("기록");
                    setting.setText("설정");
                }
            }
        }
    }

    //When Click "PLAN" button
    public void gotoPlan(View v)
    {
        Intent plan_intent = new Intent(this, PlanTitleActivity.class);
        startActivity(plan_intent);
    }

    //When Click "TRAVEL" button
    public void gotoTravel(View v)
    {
        Intent travel_intent = new Intent(this, TravelActivity.class);
        startActivity(travel_intent);
    }

    //When Click "HISTORY" button
    public void gotoHistory(View v)
    {
        Intent history_intent = new Intent(this, HistoryActivity.class);
        startActivity(history_intent);
    }

    //When Click "SETTING" button
    public void gotoSetting(View v)
    {
        Intent setting_intent = new Intent(this, SettingActivity.class);
        startActivity(setting_intent);
    }

    //When back button is pressed DONE
    public void onBackPressed()
    {
        //Exit App
        Intent exit_intent = new Intent(Intent.ACTION_MAIN);
        exit_intent.addCategory(Intent.CATEGORY_HOME);
        exit_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(exit_intent);
    }
}