package gmu.edu.cs477.ittp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PlanCalendarActivity extends AppCompatActivity {
    //Set updated DATA save place for Plan Title
    public final static String DATA_PASSING_TITLE = "gmu.edu.cs477.ittp.DATA_PASSING_TITLE";

    //Set variables
    TextView choose_text;
    CalendarView travel_calendar;
    Button previous_button;
    Button next_button;
    String set_start_date;
    String start_date_highlight;
    String start_date_compare;
    String set_end_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plancalendar);

        //Initialize variables
        choose_text = (TextView) findViewById(R.id.choose_date);
        travel_calendar = (CalendarView) findViewById(R.id.calendarView);
        previous_button = (Button) findViewById(R.id.previous_button);
        next_button = (Button) findViewById(R.id.next_button);

        //Set Language setting
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String language = sharedPreferences.getString("language_preference", "en");

        //If Language preference is changed from SettingActivity
        if(!language.equalsIgnoreCase(""))
        {
            //Language: English
            if(language.equals("en"))
            {
                //Set Calendar view to English
                Locale locale = new Locale("en");
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getApplicationContext().getResources().updateConfiguration(config, null);

                //Set initial Title to "From"
                setTitle("From");

                //Set TextView to "Choose Start Date" and choose available date
                choose_text.setText("Choose Start Date");

                //Set Buttons in English
                previous_button.setText("PREVIOUS");
                next_button.setText("NEXT");

                //When Click the Date on Calendar
                travel_calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(CalendarView calendarView, int year, int month, int day) {
                        //If Title is "From"
                        if(getTitle().equals("From"))
                        {
                            set_start_date = "Start Date: " + (month + 1) + "/" + day + "/" + year;
                            start_date_highlight = (month + 1) + "/" + day + "/" + year;
                            start_date_compare = year + "/" + (month + 1) + "/" + day;
                            choose_text.setText(set_start_date);
                        }

                        //else Title is "To"
                        else
                        {
                            //Check Whether End Date is after Start Date or not
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/mm/dd");
                            String end_date_check = year + "/" + (month + 1) + "/" + day;

                            try
                            {
                                //If End Date is NOT after Start Date and NOT same as Start Date
                                if(!dateFormat.parse(start_date_compare).before(dateFormat.parse(end_date_check))
                                        && !dateFormat.parse(start_date_compare).equals(dateFormat.parse(end_date_check)))
                                {
                                    //Set TextView to "Choose End Date" and choose available date
                                    choose_text.setText("Choose End Date");

                                    //Set Previously saved Start Date Highlighted for showing Start Date
                                    String divide[] = start_date_highlight.split("/");

                                    int month_show = Integer.parseInt(divide[0]);
                                    int day_show = Integer.parseInt(divide[1]);
                                    int year_show = Integer.parseInt(divide[2]);

                                    Calendar calendar = Calendar.getInstance();

                                    calendar.set(Calendar.MONTH, month_show - 1);
                                    calendar.set(Calendar.DAY_OF_MONTH, day_show);
                                    calendar.set(Calendar.YEAR, year_show);

                                    long milliTime = calendar.getTimeInMillis();

                                    travel_calendar.setDate(milliTime, true, true);

                                    Toast.makeText(getApplicationContext(), "End Date Not Available \n ** Before Start Date **", Toast.LENGTH_SHORT).show();
                                }

                                //else End Date is available
                                else
                                {
                                    set_end_date = "End Date: " + (month + 1) + "/" + day + "/" + year;
                                    choose_text.setText(set_end_date);
                                }
                            }

                            catch(java.text.ParseException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }

            //Language: Korean
            else
            {
                //Set Calendar view to Korean
                Locale locale = new Locale("ko");
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getApplicationContext().getResources().updateConfiguration(config, null);

                //Set initial Title to "첫날"
                setTitle("첫날");

                //Set TextView to "여행 첫날을 고르세요" and choose available date
                choose_text.setText("여행 첫날을 고르세요");

                //Set Buttons in Korean
                previous_button.setText("이전");
                next_button.setText("다음");

                //When Click the Date on Calendar
                travel_calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(CalendarView calendarView, int year, int month, int day) {
                        //If Title is "첫날"
                        if(getTitle().equals("첫날"))
                        {
                            set_start_date = "첫날: " + (month + 1) + "/" + day + "/" + year;
                            start_date_highlight = (month + 1) + "/" + day + "/" + year;
                            start_date_compare = year + "/" + (month + 1) + "/" + day;
                            choose_text.setText(set_start_date);
                        }

                        //else Title is "마지막날"
                        else
                        {
                            //Check Whether 마지막날 is after 첫날 or not
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/mm/dd");
                            String end_date_check = year + "/" + (month + 1) + "/" + day;

                            try
                            {
                                //If 마지막날 is NOT after 첫날 and NOT same as 첫날
                                if(!dateFormat.parse(start_date_compare).before(dateFormat.parse(end_date_check))
                                        && !dateFormat.parse(start_date_compare).equals(dateFormat.parse(end_date_check)))
                                {
                                    //Set TextView to "여행 마지막날을 고르세요" and choose available date
                                    choose_text.setText("여행 마지막날을 고르세요");

                                    //Set Previously saved Start Date Highlighted for showing Start Date
                                    String divide[] = start_date_highlight.split("/");

                                    int month_show = Integer.parseInt(divide[0]);
                                    int day_show = Integer.parseInt(divide[1]);
                                    int year_show = Integer.parseInt(divide[2]);

                                    Calendar calendar = Calendar.getInstance();

                                    calendar.set(Calendar.MONTH, month_show - 1);
                                    calendar.set(Calendar.DAY_OF_MONTH, day_show);
                                    calendar.set(Calendar.YEAR, year_show);

                                    long milliTime = calendar.getTimeInMillis();

                                    travel_calendar.setDate(milliTime, true, true);

                                    Toast.makeText(getApplicationContext(), "마지막날이 잘 못 선택되었습니다 \n ** 첫날보다 전에 있습니다 **", Toast.LENGTH_SHORT).show();
                                }

                                //else 마지막날 is available
                                else
                                {
                                    set_end_date = "마지막날: " + (month + 1) + "/" + day + "/" + year;
                                    choose_text.setText(set_end_date);
                                }
                            }

                            catch(java.text.ParseException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }

        //Update and keep the data when Orientation is changed
        if(savedInstanceState != null)
        {
            //Get saved information
            String temp_saved_title_text = savedInstanceState.getString("saved_title_text");
            String temp_saved_choose_text = savedInstanceState.getString("saved_choose_text");
            String temp_saved_start_date = savedInstanceState.getString("saved_start_date");
            String temp_saved_start_date_highlight = savedInstanceState.getString("saved_start_date_highlight");
            String temp_saved_start_date_compare = savedInstanceState.getString("saved_start_date_compare");
            String temp_saved_end_date = savedInstanceState.getString("saved_end_date");

            //Set information for Title and Choose text
            setTitle(temp_saved_title_text);
            choose_text.setText(temp_saved_choose_text);

            //When Title is "From" and Choose text is "Start Date: " OR Title is "To" and Choose text is "Choose End Date: "
            // or Title is "첫날" and Choose text is "첫날: " OR Title is "마지막날" and Choose text is "마지막날: "
            if((getTitle().equals("From") && choose_text.getText().toString().contains("Start Date: "))
                || (getTitle().equals("To") && choose_text.getText().toString().equals("Choose End Date"))
                || (getTitle().equals("첫날") && choose_text.getText().toString().contains("첫날: "))
                || (getTitle().equals("마지막날") && choose_text.getText().toString().equals("여행 마지막날을 고르세요")))
            {
                //Set saved start date for highlight and compare
                set_start_date = temp_saved_start_date;
                start_date_highlight = temp_saved_start_date_highlight;
                start_date_compare = temp_saved_start_date_compare;

                //Set saved start date highlighted on the Calendar
                String divide[] = start_date_highlight.split("/");

                int month_saved = Integer.parseInt(divide[0]);
                int day_saved = Integer.parseInt(divide[1]);
                int year_saved = Integer.parseInt(divide[2]);

                Calendar calendar = Calendar.getInstance();

                calendar.set(Calendar.MONTH, month_saved - 1);
                calendar.set(Calendar.DAY_OF_MONTH, day_saved);
                calendar.set(Calendar.YEAR, year_saved);

                long milliTime = calendar.getTimeInMillis();

                travel_calendar.setDate(milliTime, true, true);
            }

            //When Title is "To" and Choose text is "End Date: " or Title is "마지막날" and Choose text is "마지막날: "
            else if((getTitle().equals("To") && choose_text.getText().toString().contains("End Date: "))
                || (getTitle().equals("마지막날") && choose_text.getText().toString().contains("마지막날: ")))
            {
                //Set saved start date for highlight and compare
                set_start_date = temp_saved_start_date;
                start_date_highlight = temp_saved_start_date_highlight;
                start_date_compare = temp_saved_start_date_compare;

                //Set saved end date
                set_end_date = temp_saved_end_date;

                //Set clicked end date highlighted on the Calendar
                String for_replace = temp_saved_choose_text;

                //Set extra variable to get information
                String saved_end_date = "";

                //If Language preference is changed from SettingActivity
                if(!language.equalsIgnoreCase(""))
                {
                    //Language: English
                    if(language.equals("en"))
                    {
                        saved_end_date = for_replace.replace("End Date: ", "");

                    }

                    //Language: Korean
                    else
                    {
                        saved_end_date = for_replace.replace("마지막날: ", "");
                    }
                }

                String divide[] = saved_end_date.split("/");

                int month_show = Integer.parseInt(divide[0]);
                int day_show = Integer.parseInt(divide[1]);
                int year_show = Integer.parseInt(divide[2]);

                Calendar calendar = Calendar.getInstance();

                calendar.set(Calendar.MONTH, month_show - 1);
                calendar.set(Calendar.DAY_OF_MONTH, day_show);
                calendar.set(Calendar.YEAR, year_show);

                long milliTime = calendar.getTimeInMillis();

                travel_calendar.setDate(milliTime, true, true);
            }
        }
    }

    //When "PREVIOUS" or "이전" button is clicked
    public void gotoPrevious(View v)
    {
        //If Title is "From" or "첫날"
        if(getTitle().equals("From") || getTitle().equals("첫날"))
        {
            //Finish Calendar Activity
            //User can change the Title name
            finish();
        }

        //else Title is "To" or "마지막날"
        else
        {
            //Set Language setting
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String language = sharedPreferences.getString("language_preference", "en");

            //If Language preference is changed from SettingActivity
            if(!language.equalsIgnoreCase(""))
            {
                //Language: English
                if(language.equals("en"))
                {
                    //Set Title to "From"
                    setTitle("From");
                }

                //Language: Korean
                else
                {
                    //Set Title to "첫날"
                    setTitle("첫날");
                }
            }

            //Get Previously saved Start Date for modify
            choose_text.setText(set_start_date);

            //Set Previously saved Start Date Highlighted
            String divide[] = start_date_highlight.split("/");

            int month = Integer.parseInt(divide[0]);
            int day = Integer.parseInt(divide[1]);
            int year = Integer.parseInt(divide[2]);

            Calendar calendar = Calendar.getInstance();

            calendar.set(Calendar.MONTH, month - 1);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.YEAR, year);

            long milliTime = calendar.getTimeInMillis();

            travel_calendar.setDate(milliTime, true, true);
        }
    }

    //When "NEXT" or "다음" button is clicked
    public void gotoNext(View v)
    {
        //Set Language setting
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String language = sharedPreferences.getString("language_preference", "en");

        //If Title is "From" or "첫날"
        if(getTitle().equals("From") || getTitle().equals("첫날"))
        {
            //If User does not choose Start Date
            if(choose_text.getText().toString().equals("Choose Start Date") || choose_text.getText().toString().equals("여행 첫날을 고르세요"))
            {
                if(choose_text.getText().toString().equals("Choose Start Date"))
                {
                    Toast.makeText(getApplicationContext(), "Please Choose Start Date", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    Toast.makeText(getApplicationContext(), "여행 첫날을 골라주세요", Toast.LENGTH_SHORT).show();
                }
            }

            //else User chooses Start Date
            else
            {
                //If Language preference is changed from SettingActivity
                if(!language.equalsIgnoreCase(""))
                {
                    //Language: English
                    if(language.equals("en"))
                    {
                        setTitle("To");
                        choose_text.setText("Choose End Date");
                    }

                    //Language: Korean
                    else
                    {
                        setTitle("마지막날");
                        choose_text.setText("여행 마지막날을 고르세요");
                    }
                }
            }
        }

        //else Title is "To" or "마지막날"
        else
        {
            //If User does not choose End Date
            if(choose_text.getText().toString().equals("Choose End Date") || choose_text.getText().toString().equals("여행 마지막날을 고르세요"))
            {
                if(choose_text.getText().toString().equals("Choose End Date"))
                {
                    Toast.makeText(getApplicationContext(), "Please Choose End Date", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    Toast.makeText(getApplicationContext(), "여행 마지막날을 골라주세요", Toast.LENGTH_SHORT).show();
                }
            }

            //else User chooses End Date
            else
            {
                //If Language preference is changed from SettingActivity
                if(!language.equalsIgnoreCase(""))
                {
                    //Language: English
                    if(language.equals("en"))
                    {
                        Toast.makeText(getApplicationContext(), "Start Planning", Toast.LENGTH_SHORT).show();
                    }

                    //Language: Korean
                    else
                    {
                        Toast.makeText(getApplicationContext(), "여행계획을 시작합니다", Toast.LENGTH_SHORT).show();
                    }
                }

                Intent make_intent = new Intent(PlanCalendarActivity.this, PlanMakeActivity.class);

                //Send the Title, Start Date and End Date to PlanMakeActivity
                make_intent.putExtra(PlanMakeActivity.DATA_TITLE, super.getIntent().getExtras().getString(DATA_PASSING_TITLE));
                make_intent.putExtra(PlanMakeActivity.DATA_START_DATE, set_start_date);
                make_intent.putExtra(PlanMakeActivity.DATA_END_DATE, set_end_date);

                startActivity(make_intent);
            }
        }
    }

    //When orientation changes, keep the current Title text
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        //When Title is "From" and Choose text is "Choose Start Date" or Title is "첫날" and Choose text is "여행 첫날을 고르세요"
        if((getTitle().equals("From") && choose_text.getText().toString().equals("Choose Start Date"))
            || (getTitle().equals("첫날") && choose_text.getText().toString().equals("여행 첫날을 고르세요")))
        {
            //Get current Title, Choose TextView
            outState.putString("saved_title_text", getTitle().toString());
            outState.putString("saved_choose_text", choose_text.getText().toString());
        }

        //When Title is "From" and Choose text is "Start Date: " OR Title is "To" and Choose text is "Choose End Date: "
        // or Title is "첫날" and Choose text is "첫날: " OR Title is "마지막날" and Choose text is "마지막날: "
        else if((getTitle().equals("From") && choose_text.getText().toString().contains("Start Date: "))
            || (getTitle().equals("To") && choose_text.getText().toString().equals("Choose End Date"))
            || (getTitle().equals("첫날") && choose_text.getText().toString().contains("첫날: "))
            || (getTitle().equals("마지막날") && choose_text.getText().toString().equals("여행 마지막날을 고르세요")))
        {
            //Get current Title, Choose TextView
            outState.putString("saved_title_text", getTitle().toString());
            outState.putString("saved_choose_text", choose_text.getText().toString());

            //Get current start date for highlight and compare
            outState.putString("saved_start_date", set_start_date);
            outState.putString("saved_start_date_highlight", start_date_highlight);
            outState.putString("saved_start_date_compare", start_date_compare);
        }

        //When Title is "To" and Choose text is "End Date: " or Title is "마지막날" and Choose text is "마지막날: "
        else
        {
            //Get current Title, Choose TextView
            outState.putString("saved_title_text", getTitle().toString());
            outState.putString("saved_choose_text", choose_text.getText().toString());

            ///Get current start date for highlight and compare
            outState.putString("saved_start_date", set_start_date);
            outState.putString("saved_start_date_highlight", start_date_highlight);
            outState.putString("saved_start_date_compare", start_date_compare);

            //Get current end date
            outState.putString("saved_end_date", set_end_date);
        }
    }

    //When orientation is changed restore the current state in onCreate
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }

    //When back button is pressed go back to previous MainActivity screen
    public void onBackPressed()
    {
        //Do something in response to Back button
        Intent intent = new Intent(this, MainActivity.class);

        //Finish this Activity and return to MainActivity
        startActivity(intent);
        finish();
    }
}