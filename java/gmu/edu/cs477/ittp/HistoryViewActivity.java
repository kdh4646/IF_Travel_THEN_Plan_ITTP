package gmu.edu.cs477.ittp;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class HistoryViewActivity extends AppCompatActivity {
    //Set updated DATA save place for Travel Title, Start Date and End Date
    public final static String DATA_VIEW_TITLE = "gmu.edu.cs477.ittp.DATA_VIEW_TITLE";
    public final static String DATA_VIEW_START_DATE = "gmu.edu.cs477.ittp.DATA_VIEW_START_DATE";
    public final static String DATA_VIEW_END_DATE = "gmu.edu.cs477.ittp.DATA_VIEW_END_DATE";

    //Set variables
    TextView history_travel_name;
    TextView history_from_to;
    TextView history_days_nights;
    TextView history_default_text;
    ListView history_listview;

    //Set extra variables for control
    LinearLayout historyview_layout;

    //Set variables for Database
    private SQLiteDatabase db = null;
    private DatabaseOpenHelper dbHelper = null;
    final static String[] columns = {DatabaseOpenHelper.ID, DatabaseOpenHelper.TRAVEL,
            DatabaseOpenHelper.DATE, DatabaseOpenHelper.SCHEDULE, DatabaseOpenHelper.ADDRESS, DatabaseOpenHelper.NOTE, DatabaseOpenHelper.STARTDATE, DatabaseOpenHelper.ENDDATE, DatabaseOpenHelper.DONE};
    final static String[] need_column = {DatabaseOpenHelper.DATE, DatabaseOpenHelper.SCHEDULE, DatabaseOpenHelper.ADDRESS, DatabaseOpenHelper.NOTE};
    SimpleCursorAdapter cursorAdapter;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historyview);

        //Initialize variables
        history_travel_name = (TextView) findViewById(R.id.history_travel_name);
        history_from_to = (TextView) findViewById(R.id.history_from_to);
        history_days_nights = (TextView) findViewById(R.id.history_days_nights);
        history_default_text = (TextView) findViewById(R.id.history_default_listview_text);
        history_listview = (ListView) findViewById(R.id.history_all_information_listview);

        //Set Name and Date
        history_travel_name.setText("\u003c " + super.getIntent().getExtras().getString(DATA_VIEW_TITLE) + " \u003e");
        history_from_to.setText(super.getIntent().getExtras().getString(DATA_VIEW_START_DATE) + " - " + super.getIntent().getExtras().getString(DATA_VIEW_END_DATE));

        //Set Language setting
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String language = sharedPreferences.getString("language_preference", "en");

        //Calculate Days and Nights
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy");
            long difference_two_dates = dateFormat.parse(super.getIntent().getExtras().getString(DATA_VIEW_END_DATE)).getTime() - dateFormat.parse(super.getIntent().getExtras().getString(DATA_VIEW_START_DATE)).getTime();

            //If Language preference is changed from SettingActivity
            if(!language.equalsIgnoreCase(""))
            {
                //Language: English
                if(language.equals("en"))
                {
                    history_days_nights.setText(TimeUnit.DAYS.convert(difference_two_dates, TimeUnit.MILLISECONDS) + 1 + " days" + "(" + TimeUnit.DAYS.convert(difference_two_dates, TimeUnit.MILLISECONDS) + " nights)");
                    history_default_text.setText("No Plans !");
                }

                //Language: Korean
                else
                {
                    history_days_nights.setText(TimeUnit.DAYS.convert(difference_two_dates, TimeUnit.MILLISECONDS) + " 박 " + (TimeUnit.DAYS.convert(difference_two_dates, TimeUnit.MILLISECONDS) + 1) + " 일");
                    history_default_text.setText("현재 계획이 없습니다");
                }
            }

        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        //Set Database
        dbHelper = new DatabaseOpenHelper(this);
        db = dbHelper.getWritableDatabase();

        //Set cursor to Database Query and read HistoryViewData
        cursor = readHistoryViewData();

        //check if Table is Empty
        if (cursor.getCount() == 0) {
            //NO ListView needed for now
            history_listview.setVisibility(View.GONE);
        }

        //else Table is NOT Empty
        else
        {
            //Default Message NO need
            history_default_text.setVisibility(View.GONE);

            //If Language preference is changed from SettingActivity
            if(!language.equalsIgnoreCase(""))
            {
                //Language: English
                if(language.equals("en"))
                {
                    //Set SimpleCursorAdapter connects with ListView
                    cursorAdapter = new SimpleCursorAdapter(this, R.layout.historyviewformat_en, cursor,
                            need_column, new int[]{R.id.history_view_date_part, R.id.history_view_schedule_part, R.id.history_view_location_part, R.id.history_view_note_part},0);
                    history_listview.setAdapter(cursorAdapter);
                }

                //Language: Korean
                else
                {
                    //Set SimpleCursorAdapter connects with ListView
                    cursorAdapter = new SimpleCursorAdapter(this, R.layout.historyviewformat_ko, cursor,
                            need_column, new int[]{R.id.history_view_date_part, R.id.history_view_schedule_part, R.id.history_view_location_part, R.id.history_view_note_part},0);
                    history_listview.setAdapter(cursorAdapter);
                }
            }
        }
    }

    //Change BackGround Image
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
                historyview_layout = (LinearLayout) findViewById(R.id.activity_historyview);
                historyview_layout.setBackgroundResource(R.drawable.main_activity_background_default);
            }

            //Background: RED
            else if(background.equals("color_red"))
            {
                historyview_layout = (LinearLayout) findViewById(R.id.activity_historyview);
                historyview_layout.setBackgroundResource(R.drawable.main_activity_background_red);
            }

            //Background: GREEN
            else if(background.equals("color_green"))
            {
                historyview_layout = (LinearLayout) findViewById(R.id.activity_historyview);
                historyview_layout.setBackgroundResource(R.drawable.main_activity_background_green);
            }

            //Background: BLUE
            else
            {
                historyview_layout = (LinearLayout) findViewById(R.id.activity_historyview);
                historyview_layout.setBackgroundResource(R.drawable.main_activity_background_blue);
            }
        }
    }

    //Change Background Image
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
                historyview_layout = (LinearLayout) findViewById(R.id.activity_historyview);
                historyview_layout.setBackgroundResource(R.drawable.main_activity_background_default);
            }

            //Background: RED
            else if(background.equals("color_red"))
            {
                historyview_layout = (LinearLayout) findViewById(R.id.activity_historyview);
                historyview_layout.setBackgroundResource(R.drawable.main_activity_background_red);
            }

            //Background: GREEN
            else if(background.equals("color_green"))
            {
                historyview_layout = (LinearLayout) findViewById(R.id.activity_historyview);
                historyview_layout.setBackgroundResource(R.drawable.main_activity_background_green);
            }

            //Background: BLUE
            else
            {
                historyview_layout = (LinearLayout) findViewById(R.id.activity_historyview);
                historyview_layout.setBackgroundResource(R.drawable.main_activity_background_blue);
            }
        }
    }

    //on Destroy method
    @Override
    protected void onDestroy() {
        db.close();
        dbHelper.deleteDatabase();
        super.onDestroy();
    }

    //Cursor readHistoryViewData method
    private Cursor readHistoryViewData()
    {
        return db.query(dbHelper.DBHISTORY, columns, dbHelper.TRAVEL + "=?", new String[]{super.getIntent().getExtras().getString(DATA_VIEW_TITLE)}, null, null, null);
    }
}