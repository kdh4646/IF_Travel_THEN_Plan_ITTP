package gmu.edu.cs477.ittp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class TravelLoadingActivity extends AppCompatActivity {
    //Set updated DATA save place for Travel Title, Start Date, End Date, Count Layouts, Check if Next and Previous Date
    public final static String DATA_LOADING_TITLE = "gmu.edu.cs477.ittp.DATA_LOADING_TITLE";
    public final static String DATA_LOADING_START_DATE = "gmu.edu.cs477.ittp.DATA_LOADING_START_DATE";
    public final static String DATA_LOADING_END_DATE = "gmu.edu.cs477.ittp.DATA_LOADING_END_DATE";
    public final static String DATA_LOADING_COUNT_LAYOUTS = "gmu.edu.cs477.ittp.DATA_LOADING_COUNT_LAYOUTS";
    public final static String DATA_LOADING_IS_NEXT = "gmu.edu.cs477.ittp.DATA_LOADING_IS_NEXT";
    public final static String DATA_LOADING_PREV_DATE = "gmu.edu.cs477.ittp.DATA_LOADING_PREV_DATE";

    //Value for "requestCode"
    public final int ACTIVITY_RESULT = 1;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    //Set variables
    TextView travel_name;
    TextView specific_date;
    TextView default_text;
    ListView travel_plan_list;
    private LinearLayout Dots_layout;
    private ImageView[] dots;
    FloatingActionButton fab;
    Button prev_button;
    Button next_button;

    //Set extra variables for control
    String send_loading_title;
    String send_loading_start_date;
    String send_loading_end_date;
    String next_date;
    String prev_date;
    int num_layouts;
    LinearLayout travelloading_layout;

    //Set variables for Database
    private SQLiteDatabase db = null;
    private DatabaseOpenHelper dbHelper = null;
    final static String[] columns = {DatabaseOpenHelper.ID, DatabaseOpenHelper.TRAVEL,
            DatabaseOpenHelper.DATE, DatabaseOpenHelper.SCHEDULE, DatabaseOpenHelper.ADDRESS, DatabaseOpenHelper.NOTE, DatabaseOpenHelper.STARTDATE, DatabaseOpenHelper.ENDDATE, DatabaseOpenHelper.DONE};
    final static String[] need_column = {DatabaseOpenHelper.SCHEDULE, DatabaseOpenHelper.NOTE};
    SimpleCursorAdapter cursorAdapter;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travelloading);

        //Initialize Variables
        travel_name = (TextView) findViewById(R.id.loading_travel_title);
        specific_date = (TextView) findViewById(R.id.loading_date);
        default_text = (TextView) findViewById(R.id.loading_default_listview_text);
        travel_plan_list = (ListView) findViewById(R.id.loading_plan_listview);
        Dots_layout = (LinearLayout) findViewById(R.id.loading_dots);
        fab = (FloatingActionButton) findViewById(R.id.loading_fab);
        prev_button = (Button) findViewById(R.id.loading_previous_slide_button);
        next_button = (Button) findViewById(R.id.loading_next_slide_button);

        //Get DATA from other Activities
        String title_text = super.getIntent().getExtras().getString(DATA_LOADING_TITLE);
        String start_text = super.getIntent().getExtras().getString(DATA_LOADING_START_DATE);
        String end_text = super.getIntent().getExtras().getString(DATA_LOADING_END_DATE);

        //Set DATA for Send Information to new Activity
        send_loading_title = title_text;
        send_loading_start_date = start_text;
        send_loading_end_date = end_text;

        //Set DATA Title
        travel_name.setText(title_text);

        //Set Calculate Number of Days
        try {
            //Calculate number of dot layouts
            SimpleDateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy");
            long difference_two_dates = dateFormat.parse(super.getIntent().getExtras().getString(DATA_LOADING_END_DATE)).getTime() - dateFormat.parse(super.getIntent().getExtras().getString(DATA_LOADING_START_DATE)).getTime();
            num_layouts = (int) TimeUnit.DAYS.convert(difference_two_dates, TimeUnit.MILLISECONDS) + 1;

            //Calendar variable for calculate DATE
            Calendar date_calc = Calendar.getInstance();
            Calendar date_prev = Calendar.getInstance();

            //Get incremented date for planning when "NEXT" is clicked
            date_calc.setTime(dateFormat.parse(super.getIntent().getExtras().getString(DATA_LOADING_START_DATE)));
            date_calc.add(Calendar.DATE, super.getIntent().getExtras().getInt(DATA_LOADING_COUNT_LAYOUTS));
            next_date = dateFormat.format(date_calc.getTime());

            //Get decreased date for planning when "PREVIOUS" is clicked
            date_prev.setTime(dateFormat.parse(super.getIntent().getExtras().getString(DATA_LOADING_START_DATE)));
            date_prev.add(Calendar.DATE, super.getIntent().getExtras().getInt(DATA_LOADING_COUNT_LAYOUTS) - 1);
            prev_date = dateFormat.format(date_prev.getTime());
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        //If TravelLoadingActivity is FIRST time created or "NEXT" button is clicked
        if (TextUtils.isEmpty(super.getIntent().getExtras().getString(DATA_LOADING_IS_NEXT)) || super.getIntent().getExtras().getString(DATA_LOADING_IS_NEXT).equals("true")) {
            //Set Specific Date for planning by increasing date
            specific_date.setText("\u003c " + next_date + " \u003e");
        }

        //else if "PREVIOUS" button is clicked
        else if (super.getIntent().getExtras().getString(DATA_LOADING_IS_NEXT).equals("false")) {
            //Set Specific Date for planning by decreasing date
            specific_date.setText("\u003c " + super.getIntent().getExtras().getString(DATA_LOADING_PREV_DATE) + " \u003e");
        }

        //Set Database
        dbHelper = new DatabaseOpenHelper(this);
        db = dbHelper.getWritableDatabase();

        //Set cursor to Database Query and read TravelPlanData
        cursor = readTravelPlanData();

        //Set Language setting
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String language = sharedPreferences.getString("language_preference", "en");

        //If Language preference is changed from SettingActivity
        if(!language.equalsIgnoreCase(""))
        {
            //Language: English
            if(language.equals("en"))
            {
                prev_button.setText("PREVIOUS");
                next_button.setText("NEXT");
            }

            //Language: Korean
            else
            {
                prev_button.setText("이전");
                next_button.setText("다음");
            }
        }

        //check if Table is Empty for Specific Date
        if (cursor.getCount() == 0) {
            //NO ListView needed for now
            travel_plan_list.setVisibility(View.GONE);

            //If Language preference is changed from SettingActivity
            if(!language.equalsIgnoreCase(""))
            {
                //Language: English
                if(language.equals("en"))
                {
                    default_text.setText("No Plans !");
                }

                //Language: Korean
                else
                {
                    default_text.setText("현재 계획이 없습니다");
                }
            }
        }

        //else Table is NOT Empty for Specific Date
        else {
            //Default Message NO need
            default_text.setVisibility(View.GONE);

            //Set SimpleCursorAdapter connects with ListView
            cursorAdapter = new SimpleCursorAdapter(this, R.layout.travelplanlistformat, cursor,
                    need_column, new int[]{R.id.travelplan_name_part, R.id.travelplan_note_part}, 0);
            travel_plan_list.setAdapter(cursorAdapter);

            //Set onItemClick for ListView
            travel_plan_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Get cursor to clicked item position
                    cursor.moveToPosition(position);

                    //If DONE status is "false"
                    if(cursor.getString(8).equals("false"))
                    {
                        //Set Alert Dialog with "Done" and "Start" buttons
                        AlertDialog alertDialog = new AlertDialog.Builder(TravelLoadingActivity.this).create();

                        //Set Language setting
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        String language = sharedPreferences.getString("language_preference", "en");

                        //If Language preference is changed from SettingActivity
                        if(!language.equalsIgnoreCase(""))
                        {
                            //Language: English
                            if(language.equals("en"))
                            {
                                alertDialog.setTitle("Start Travel or Set Travel as Done");

                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "START", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        //If Location is NOT empty
                                        if(cursor.getString(4).trim().length() != 0)
                                        {
                                            //Google Maps or Maps GO intent with current location to destination
                                            Intent maps_intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + cursor.getString(4)));

                                            //If Google Maps is installed, use this as Primary
                                            //If Google Maps and Maps GO both are installed, use Google Maps as Primary
                                            if(isGoogleMapsInstalled())
                                            {
                                                //Make sure it only uses Google Maps
                                                maps_intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                                startActivity(maps_intent);
                                            }

                                            else if(isMapsGoInstalled())
                                            {
                                                //Maps Go is installed
                                                startActivity(maps_intent);
                                            }

                                            //NONE of two maps are installed
                                            else
                                            {
                                                Toast.makeText(getApplicationContext(), "You need to install Google Maps or Maps GO to use this function", Toast.LENGTH_LONG).show();
                                            }
                                        }

                                        //else Location is empty
                                        else
                                        {
                                            Toast.makeText(getApplicationContext(), "ERROR: Location Address is EMPTY", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONE", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        ContentValues cv = new ContentValues();
                                        cv.put(dbHelper.SCHEDULE, "\uD83C\uDD53" + "\uD83C\uDD5E" + "\uD83C\uDD5D" + "\uD83C\uDD54 " + cursor.getString(3));
                                        cv.put(dbHelper.DONE, "true");

                                        //Update Done Status to "true"
                                        db.update(dbHelper.DBTRAVEL, cv, dbHelper.TRAVEL + "=?" + " AND " + dbHelper.DATE + "=?" + " AND " + dbHelper.SCHEDULE + "=?", new String[] {
                                                cursor.getString(1), cursor.getString(2), cursor.getString(3)});

                                        //Set cursor to Database Query and read TravelPlanData
                                        cursor = readTravelPlanData();

                                        //Set SimpleCursorAdapter connects with ListView
                                        cursorAdapter = new SimpleCursorAdapter(TravelLoadingActivity.this, R.layout.travelplanlistformat, cursor,
                                                need_column, new int[]{R.id.travelplan_name_part, R.id.travelplan_note_part}, 0);
                                        travel_plan_list.setAdapter(cursorAdapter);
                                    }
                                });
                            }

                            //Language: Korean
                            else
                            {
                                alertDialog.setTitle("여행을 시작하거나 또는 여행완료를 설정하십시오");

                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "시작", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        //If Location is NOT empty
                                        if(cursor.getString(4).trim().length() != 0)
                                        {
                                            //Google Maps or Maps GO intent with current location to destination
                                            Intent maps_intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + cursor.getString(4)));

                                            //If Google Maps is installed, use this as Primary
                                            //If Google Maps and Maps GO both are installed, use Google Maps as Primary
                                            if(isGoogleMapsInstalled())
                                            {
                                                //Make sure it only uses Google Maps
                                                maps_intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                                startActivity(maps_intent);
                                            }

                                            else if(isMapsGoInstalled())
                                            {
                                                //Maps Go is installed
                                                startActivity(maps_intent);
                                            }

                                            //NONE of two maps are installed
                                            else
                                            {
                                                Toast.makeText(getApplicationContext(), "기능을 사용하기 위해서는 구글 맵 혹은 맵스 고를 설치하셔야 합니다", Toast.LENGTH_LONG).show();
                                            }
                                        }

                                        //else Location is empty
                                        else
                                        {
                                            Toast.makeText(getApplicationContext(), "에러: 주소 정보가 없습니다", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "완료", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        ContentValues cv = new ContentValues();
                                        cv.put(dbHelper.SCHEDULE, "\uD83C\uDD53" + "\uD83C\uDD5E" + "\uD83C\uDD5D" + "\uD83C\uDD54 " + cursor.getString(3));
                                        cv.put(dbHelper.DONE, "true");

                                        //Update Done Status to "true"
                                        db.update(dbHelper.DBTRAVEL, cv, dbHelper.TRAVEL + "=?" + " AND " + dbHelper.DATE + "=?" + " AND " + dbHelper.SCHEDULE + "=?", new String[] {
                                                cursor.getString(1), cursor.getString(2), cursor.getString(3)});

                                        //Set cursor to Database Query and read TravelPlanData
                                        cursor = readTravelPlanData();

                                        //Set SimpleCursorAdapter connects with ListView
                                        cursorAdapter = new SimpleCursorAdapter(TravelLoadingActivity.this, R.layout.travelplanlistformat, cursor,
                                                need_column, new int[]{R.id.travelplan_name_part, R.id.travelplan_note_part}, 0);
                                        travel_plan_list.setAdapter(cursorAdapter);
                                    }
                                });
                            }
                        }

                        alertDialog.show();

                        //Repositioning Alert Dialog Buttons
                        Button btnStart = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        Button btnDone = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnStart.getLayoutParams();
                        layoutParams.weight = 10;
                        btnStart.setLayoutParams(layoutParams);
                        btnDone.setLayoutParams(layoutParams);
                    }

                    //else DONE status is "true"
                    else
                    {
                        //Set Alert Dialog with "Done" and "Start" buttons
                        AlertDialog alertDialog = new AlertDialog.Builder(TravelLoadingActivity.this).create();

                        //Set Language setting
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        String language = sharedPreferences.getString("language_preference", "en");

                        //If Language preference is changed from SettingActivity
                        if(!language.equalsIgnoreCase(""))
                        {
                            //Language: English
                            if(language.equals("en"))
                            {
                                alertDialog.setTitle("Are you sure this Plan is not DONE ?");
                                alertDialog.setMessage("This will remove the Done sign from the Plan");

                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "NOT DONE", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        String replace = cursor.getString(3).replace("\uD83C\uDD53" + "\uD83C\uDD5E" + "\uD83C\uDD5D" + "\uD83C\uDD54 ", "");

                                        ContentValues cv = new ContentValues();
                                        cv.put(dbHelper.SCHEDULE, replace);
                                        cv.put(dbHelper.DONE, "false");

                                        //Update Done Status to "true"
                                        db.update(dbHelper.DBTRAVEL, cv, dbHelper.TRAVEL + "=?" + " AND " + dbHelper.DATE + "=?" + " AND " + dbHelper.SCHEDULE + "=?", new String[] {
                                                cursor.getString(1), cursor.getString(2), cursor.getString(3)});

                                        //Set cursor to Database Query and read TravelPlanData
                                        cursor = readTravelPlanData();

                                        //Set SimpleCursorAdapter connects with ListView
                                        cursorAdapter = new SimpleCursorAdapter(TravelLoadingActivity.this, R.layout.travelplanlistformat, cursor,
                                                need_column, new int[]{R.id.travelplan_name_part, R.id.travelplan_note_part}, 0);
                                        travel_plan_list.setAdapter(cursorAdapter);
                                    }
                                });
                            }

                            //Language: Korean
                            else
                            {
                                alertDialog.setTitle("해당 여행을 완료하지 않으셨습니까 ?");
                                alertDialog.setMessage("여행이 완료되지 않음으로 설정될 것입니다");

                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "미완료", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        String replace = cursor.getString(3).replace("\uD83C\uDD53" + "\uD83C\uDD5E" + "\uD83C\uDD5D" + "\uD83C\uDD54 ", "");

                                        ContentValues cv = new ContentValues();
                                        cv.put(dbHelper.SCHEDULE, replace);
                                        cv.put(dbHelper.DONE, "false");

                                        //Update Done Status to "true"
                                        db.update(dbHelper.DBTRAVEL, cv, dbHelper.TRAVEL + "=?" + " AND " + dbHelper.DATE + "=?" + " AND " + dbHelper.SCHEDULE + "=?", new String[] {
                                                cursor.getString(1), cursor.getString(2), cursor.getString(3)});

                                        //Set cursor to Database Query and read TravelPlanData
                                        cursor = readTravelPlanData();

                                        //Set SimpleCursorAdapter connects with ListView
                                        cursorAdapter = new SimpleCursorAdapter(TravelLoadingActivity.this, R.layout.travelplanlistformat, cursor,
                                                need_column, new int[]{R.id.travelplan_name_part, R.id.travelplan_note_part}, 0);
                                        travel_plan_list.setAdapter(cursorAdapter);
                                    }
                                });
                            }
                        }

                        alertDialog.show();
                    }
                }
            });

            //Set onItemLongClick for ListView
            travel_plan_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    //Get cursor to clicked item position
                    cursor.moveToPosition(position);

                    //Only enters if travel DONE status is "false"
                    if(cursor.getString(8).equals("false"))
                    {
                        //New Intent for Edit Travel Activity
                        Intent edit_travel_intent = new Intent(getApplicationContext(), TravelEditActivity.class);

                        //Send DATA to TravelEditActivity for edit or delete Database context
                        //Column 1: Travel Name
                        //Column 2: Specific Date
                        edit_travel_intent.putExtra(TravelEditActivity.DATA_GET_TRAVEL_NAME, cursor.getString(1));
                        edit_travel_intent.putExtra(TravelEditActivity.DATA_GET_DATE, cursor.getString(2));
                        edit_travel_intent.putExtra(TravelEditActivity.DATA_GET_ITEM_POSITION, Integer.toString(position));
                        edit_travel_intent.putExtra(TravelEditActivity.DATA_GET_EDIT_STATUS, "true");

                        startActivityForResult(edit_travel_intent, ACTIVITY_RESULT);
                    }

                    return true;
                }
            });
        }

        //Create Tracking Dots for Layouts
        createDots(super.getIntent().getExtras().getInt(DATA_LOADING_COUNT_LAYOUTS));

        //If start to travel, dates are set, NO previous button
        if (super.getIntent().getExtras().getInt(DATA_LOADING_COUNT_LAYOUTS) == 0) {
            prev_button.setVisibility(View.INVISIBLE);
        }

        //If End Date and NO more Layouts remain
        //set next slide button to "DONE" or "완료"
        if (num_layouts - 1 == super.getIntent().getExtras().getInt(DATA_LOADING_COUNT_LAYOUTS)) {

            //If Language preference is changed from SettingActivity
            if(!language.equalsIgnoreCase(""))
            {
                //Language: English
                if(language.equals("en"))
                {
                    next_button.setText("DONE");
                }

                //Language: Korean
                else
                {
                    next_button.setText("완료");
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

        //Get "language_preference"
        String language = sharedPreferences.getString("language_preference", "en");

        //If Google Play Service is NOT installed
        if(!checkPlayServices())
        {
            //If Language preference is changed from SettingActivity
            if(!language.equalsIgnoreCase(""))
            {
                //Language: English
                if(language.equals("en"))
                {
                    Toast.makeText(this, "You need to install Google Play Services to use the App properly", Toast.LENGTH_LONG).show();
                }

                //Language: Korean
                else
                {
                    Toast.makeText(this, "앱을 사용하시려면 구글 플레이 서비스를 설치하셔야 합니다", Toast.LENGTH_LONG).show();
                }
            }
        }

        //else Google Play is installed
        else
        {
            //If NONE of two maps are installed
            if(!(isGoogleMapsInstalled() || isMapsGoInstalled()))
            {
                //If Language preference is changed from SettingActivity
                if(!language.equalsIgnoreCase(""))
                {
                    //Language: English
                    if (language.equals("en"))
                    {
                        Toast.makeText(this, "You need to install Google Maps or Maps GO to use this function", Toast.LENGTH_LONG).show();
                    }

                    //Language: Korean
                    else
                    {
                        Toast.makeText(this, "기능을 사용하기 위해서는 구글 맵 혹은 맵스 고를 설치하셔야 합니다", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

        //Get "background_color_preference"
        String background = sharedPreferences.getString("background_color_preference", "color_ivory");

        //If Background preference is changed from SettingActivity
        if(!background.equalsIgnoreCase(""))
        {
            //Background: IVORY
            if(background.equals("color_ivory"))
            {
                travelloading_layout = (LinearLayout) findViewById(R.id.activity_travelloading);
                travelloading_layout.setBackgroundResource(R.drawable.main_activity_background_default);
            }

            //Background: RED
            else if(background.equals("color_red"))
            {
                travelloading_layout = (LinearLayout) findViewById(R.id.activity_travelloading);
                travelloading_layout.setBackgroundResource(R.drawable.main_activity_background_red);
            }

            //Background: GREEN
            else if(background.equals("color_green"))
            {
                travelloading_layout = (LinearLayout) findViewById(R.id.activity_travelloading);
                travelloading_layout.setBackgroundResource(R.drawable.main_activity_background_green);
            }

            //Background: BLUE
            else
            {
                travelloading_layout = (LinearLayout) findViewById(R.id.activity_travelloading);
                travelloading_layout.setBackgroundResource(R.drawable.main_activity_background_blue);
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
                travelloading_layout = (LinearLayout) findViewById(R.id.activity_travelloading);
                travelloading_layout.setBackgroundResource(R.drawable.main_activity_background_default);
            }

            //Background: RED
            else if(background.equals("color_red"))
            {
                travelloading_layout = (LinearLayout) findViewById(R.id.activity_travelloading);
                travelloading_layout.setBackgroundResource(R.drawable.main_activity_background_red);
            }

            //Background: GREEN
            else if(background.equals("color_green"))
            {
                travelloading_layout = (LinearLayout) findViewById(R.id.activity_travelloading);
                travelloading_layout.setBackgroundResource(R.drawable.main_activity_background_green);
            }

            //Background: BLUE
            else
            {
                travelloading_layout = (LinearLayout) findViewById(R.id.activity_travelloading);
                travelloading_layout.setBackgroundResource(R.drawable.main_activity_background_blue);
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

    //Cursor readTravelPlanData method
    private Cursor readTravelPlanData() {
        //If TravelLoadingActivity is FIRST time created or "NEXT" or "다음" button is clicked
        if (TextUtils.isEmpty(super.getIntent().getExtras().getString(DATA_LOADING_IS_NEXT)) || super.getIntent().getExtras().getString(DATA_LOADING_IS_NEXT).equals("true")) {
            return db.query(dbHelper.DBTRAVEL, columns, dbHelper.TRAVEL + "=?" + " AND " + dbHelper.DATE + "=?", new String[]{super.getIntent().getExtras().getString(DATA_LOADING_TITLE), next_date}, null, null, null);
        }

        //else "PREVIOUS" or "이전" button is clicked
        else {
            return db.query(dbHelper.DBTRAVEL, columns, dbHelper.TRAVEL + "=?" + " AND " + dbHelper.DATE + "=?", new String[]{super.getIntent().getExtras().getString(DATA_LOADING_TITLE), super.getIntent().getExtras().getString(DATA_LOADING_PREV_DATE)}, null, null, null);
        }
    }

    //Create Dot
    private void createDots(int current_position) {
        if (Dots_layout != null) {
            Dots_layout.removeAllViews();
        }

        dots = new ImageView[num_layouts];

        for (int i = 0; i < num_layouts; i++) {
            dots[i] = new ImageView(this);

            //If selected slide then set BLACK dots
            if (i == current_position) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.selected_dots));
            }

            //else NOT selected slide then set GRAY dots
            else {
                dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.not_selected_dots));
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            params.setMargins(4, 4, 4, 4);

            Dots_layout.addView(dots[i], params);
        }
    }

    //When Floating Action Button is clicked for add plan
    public void gotoAdd(View v)
    {
        Intent add_travel_intent = new Intent(this, TravelEditActivity.class);

        add_travel_intent.putExtra(TravelEditActivity.DATA_GET_TRAVEL_NAME, send_loading_title);

        //If TravelLoadingActivity is FIRST time created or "NEXT" or "다음" button was clicked
        if(TextUtils.isEmpty(super.getIntent().getExtras().getString(DATA_LOADING_IS_NEXT)) || super.getIntent().getExtras().getString(DATA_LOADING_IS_NEXT).equals("true"))
        {
            //Send date to get saved data equals date
            add_travel_intent.putExtra(TravelEditActivity.DATA_GET_DATE, next_date);
        }

        //else "PREVIOUS" or "이전" button was clicked
        else
        {
            //Send date to get saved data equals date
            add_travel_intent.putExtra(TravelEditActivity.DATA_GET_DATE, super.getIntent().getExtras().getString(DATA_LOADING_PREV_DATE));
        }

        //Send Start and End Date
        add_travel_intent.putExtra(TravelEditActivity.DATA_GET_START_DATE, send_loading_start_date);
        add_travel_intent.putExtra(TravelEditActivity.DATA_GET_END_DATE, send_loading_end_date);

        add_travel_intent.putExtra(TravelEditActivity.DATA_GET_EDIT_STATUS, "false");

        startActivityForResult(add_travel_intent, ACTIVITY_RESULT);
    }

    //When Click "PREVIOUS" or "이전" button
    public void gotoPreviousSlide(View v) {
        //Get number of layouts
        int count = super.getIntent().getExtras().getInt(DATA_LOADING_COUNT_LAYOUTS);

        //When clicked decrease count
        count--;

        Intent loading_prev_intent = new Intent(this, TravelLoadingActivity.class);

        loading_prev_intent.putExtra(DATA_LOADING_TITLE, send_loading_title);
        loading_prev_intent.putExtra(DATA_LOADING_START_DATE, send_loading_start_date);
        loading_prev_intent.putExtra(DATA_LOADING_END_DATE, send_loading_end_date);
        loading_prev_intent.putExtra(DATA_LOADING_COUNT_LAYOUTS, count);
        loading_prev_intent.putExtra(DATA_LOADING_IS_NEXT, "false");
        loading_prev_intent.putExtra(DATA_LOADING_PREV_DATE, prev_date);

        startActivity(loading_prev_intent);

        finish();
    }

    //When Click "NEXT" or "DONE" or "다음" or "완료" button
    public void gotoNextSlide(View v) {
        //Count number of layouts
        int count = super.getIntent().getExtras().getInt(DATA_LOADING_COUNT_LAYOUTS, 0);

        //When Clicked Increase count
        count++;

        //If Layouts remain and "NEXT" or "다음" button
        if (((count <= num_layouts - 1) && next_button.getText().toString().equals("NEXT"))
            || ((count <= num_layouts - 1) && next_button.getText().toString().equals("다음"))) {
            //Start next day TravelLoadingActivity
            Intent loading_next_intent = new Intent(this, TravelLoadingActivity.class);

            loading_next_intent.putExtra(DATA_LOADING_TITLE, send_loading_title);
            loading_next_intent.putExtra(DATA_LOADING_START_DATE, send_loading_start_date);
            loading_next_intent.putExtra(DATA_LOADING_END_DATE, send_loading_end_date);
            loading_next_intent.putExtra(DATA_LOADING_COUNT_LAYOUTS, count);
            loading_next_intent.putExtra(DATA_LOADING_IS_NEXT, "true");

            startActivity(loading_next_intent);

            finish();
        }

        //If NO more Layouts remain and "DONE" or "완료" button
        if (next_button.getText().toString().equals("DONE") || next_button.getText().toString().equals("완료")) {
            //Take Done Travel Data from Travel table to History table
            db.execSQL("INSERT INTO " + dbHelper.DBHISTORY + " SELECT * FROM " + dbHelper.DBTRAVEL
                            + " WHERE " + dbHelper.TRAVEL + " = '" + super.getIntent().getExtras().getString(DATA_LOADING_TITLE) + "'");

            //Delete the Done Travel Data from Travel Table
            db.execSQL("DELETE FROM " + dbHelper.DBTRAVEL
                            + " WHERE " + dbHelper.TRAVEL + " = '" + super.getIntent().getExtras().getString(DATA_LOADING_TITLE) + "'");

            if(next_button.getText().toString().equals("DONE"))
            {
                Toast.makeText(this, "\tYou finished YOUR TRAVEL ! \n It has been moved to HISTORY", Toast.LENGTH_LONG).show();
            }

            else
            {
                Toast.makeText(this, "\t여행이 완료되었습니다 ! \n 해당여행은 기록보관소로 이동되었습니다", Toast.LENGTH_LONG).show();
            }

            finish();
        }
    }

    //Check Google play service is installed
    private boolean checkPlayServices()
    {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();

        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if(resultCode != ConnectionResult.SUCCESS)
        {
            if(apiAvailability.isUserResolvableError(resultCode))
            {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            }

            else
            {
                finish();
            }

            return false;
        }

        return true;
    }

    //Check Google Map is Installed
    public boolean isGoogleMapsInstalled()
    {
        try
        {
            getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0 );
            return true;
        }
        catch(PackageManager.NameNotFoundException e)
        {
            return false;
        }
    }

    //Check Maps Go is Installed
    public boolean isMapsGoInstalled()
    {
        try
        {
            getPackageManager().getApplicationInfo("com.google.android.apps.mapslite", 0 );
            return true;
        }
        catch(PackageManager.NameNotFoundException e)
        {
            return false;
        }
    }

    //When back button is pressed go back to TravelActivity screen
    public void onBackPressed() {
        finish();
    }

    //Getting Result from the Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ACTIVITY_RESULT)
        {
            //read updated Database and set cursor to update the ListView
            cursor = readTravelPlanData();

            //If Nothing exist
            if(cursor.getCount() == 0)
            {
                default_text.setVisibility(View.VISIBLE);
                travel_plan_list.setVisibility(View.GONE);
            }

            //else still some plans exist
            else
            {
                default_text.setVisibility(View.GONE);
                travel_plan_list.setVisibility(View.VISIBLE);

                //Set SimpleCursorAdapter connects with ListView
                cursorAdapter = new SimpleCursorAdapter(this, R.layout.travelplanlistformat, cursor,
                        need_column, new int[]{R.id.travelplan_name_part, R.id.travelplan_note_part}, 0);
                travel_plan_list.setAdapter(cursorAdapter);
            }

            //Set onItemClick for ListView
            travel_plan_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Get cursor to clicked item position
                    cursor.moveToPosition(position);

                    //If DONE status is "false"
                    if(cursor.getString(8).equals("false"))
                    {
                        //Set Alert Dialog with "Done" and "Start" buttons
                        AlertDialog alertDialog = new AlertDialog.Builder(TravelLoadingActivity.this).create();

                        //Set Language setting
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        String language = sharedPreferences.getString("language_preference", "en");

                        //If Language preference is changed from SettingActivity
                        if(!language.equalsIgnoreCase(""))
                        {
                            //Language: English
                            if(language.equals("en"))
                            {
                                alertDialog.setTitle("Start Travel or Set Travel as Done");

                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "START", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        //If Location is NOT empty
                                        if(cursor.getString(4).trim().length() != 0)
                                        {
                                            //Google Maps or Maps GO intent with current location to destination
                                            Intent maps_intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + cursor.getString(4)));

                                            //If Google Maps is installed, use this as Primary
                                            //If Google Maps and Maps GO both are installed, use Google Maps as Primary
                                            if(isGoogleMapsInstalled())
                                            {
                                                //Make sure it only uses Google Maps
                                                maps_intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                                startActivity(maps_intent);
                                            }

                                            else if(isMapsGoInstalled())
                                            {
                                                //Maps Go is installed
                                                startActivity(maps_intent);
                                            }

                                            //NONE of two maps are installed
                                            else
                                            {
                                                Toast.makeText(getApplicationContext(), "You need to install Google Maps or Maps GO to use this function", Toast.LENGTH_LONG).show();
                                            }
                                        }

                                        //else Location is empty
                                        else
                                        {
                                            Toast.makeText(getApplicationContext(), "ERROR: Location Address is EMPTY", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONE", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        ContentValues cv = new ContentValues();
                                        cv.put(dbHelper.SCHEDULE, "\uD83C\uDD53" + "\uD83C\uDD5E" + "\uD83C\uDD5D" + "\uD83C\uDD54 " + cursor.getString(3));
                                        cv.put(dbHelper.DONE, "true");

                                        //Update Done Status to "true"
                                        db.update(dbHelper.DBTRAVEL, cv, dbHelper.TRAVEL + "=?" + " AND " + dbHelper.DATE + "=?" + " AND " + dbHelper.SCHEDULE + "=?", new String[] {
                                                cursor.getString(1), cursor.getString(2), cursor.getString(3)});

                                        //Set cursor to Database Query and read TravelPlanData
                                        cursor = readTravelPlanData();

                                        //Set SimpleCursorAdapter connects with ListView
                                        cursorAdapter = new SimpleCursorAdapter(TravelLoadingActivity.this, R.layout.travelplanlistformat, cursor,
                                                need_column, new int[]{R.id.travelplan_name_part, R.id.travelplan_note_part}, 0);
                                        travel_plan_list.setAdapter(cursorAdapter);
                                    }
                                });
                            }

                            //Language: Korean
                            else
                            {
                                alertDialog.setTitle("여행을 시작하거나 또는 여행완료를 설정하십시오");

                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "시작", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        //If Location is NOT empty
                                        if(cursor.getString(4).trim().length() != 0)
                                        {
                                            //Google Maps or Maps GO intent with current location to destination
                                            Intent maps_intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + cursor.getString(4)));

                                            //If Google Maps is installed, use this as Primary
                                            //If Google Maps and Maps GO both are installed, use Google Maps as Primary
                                            if(isGoogleMapsInstalled())
                                            {
                                                //Make sure it only uses Google Maps
                                                maps_intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                                startActivity(maps_intent);
                                            }

                                            else if(isMapsGoInstalled())
                                            {
                                                //Maps Go is installed
                                                startActivity(maps_intent);
                                            }

                                            //NONE of two maps are installed
                                            else
                                            {
                                                Toast.makeText(getApplicationContext(), "기능을 사용하기 위해서는 구글 맵 혹은 맵스 고를 설치하셔야 합니다", Toast.LENGTH_LONG).show();
                                            }
                                        }

                                        //else Location is empty
                                        else
                                        {
                                            Toast.makeText(getApplicationContext(), "에러: 주소 정보가 없습니다", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "완료", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        ContentValues cv = new ContentValues();
                                        cv.put(dbHelper.SCHEDULE, "\uD83C\uDD53" + "\uD83C\uDD5E" + "\uD83C\uDD5D" + "\uD83C\uDD54 " + cursor.getString(3));
                                        cv.put(dbHelper.DONE, "true");

                                        //Update Done Status to "true"
                                        db.update(dbHelper.DBTRAVEL, cv, dbHelper.TRAVEL + "=?" + " AND " + dbHelper.DATE + "=?" + " AND " + dbHelper.SCHEDULE + "=?", new String[] {
                                                cursor.getString(1), cursor.getString(2), cursor.getString(3)});

                                        //Set cursor to Database Query and read TravelPlanData
                                        cursor = readTravelPlanData();

                                        //Set SimpleCursorAdapter connects with ListView
                                        cursorAdapter = new SimpleCursorAdapter(TravelLoadingActivity.this, R.layout.travelplanlistformat, cursor,
                                                need_column, new int[]{R.id.travelplan_name_part, R.id.travelplan_note_part}, 0);
                                        travel_plan_list.setAdapter(cursorAdapter);
                                    }
                                });
                            }
                        }

                        alertDialog.show();

                        //Repositioning Alert Dialog Buttons
                        Button btnStart = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        Button btnDone = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnStart.getLayoutParams();
                        layoutParams.weight = 10;
                        btnStart.setLayoutParams(layoutParams);
                        btnDone.setLayoutParams(layoutParams);
                    }

                    //else DONE status is "true"
                    else
                    {
                        //Set Alert Dialog with "Done" and "Start" buttons
                        AlertDialog alertDialog = new AlertDialog.Builder(TravelLoadingActivity.this).create();

                        //Set Language setting
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        String language = sharedPreferences.getString("language_preference", "en");

                        //If Language preference is changed from SettingActivity
                        if(!language.equalsIgnoreCase(""))
                        {
                            //Language: English
                            if(language.equals("en"))
                            {
                                alertDialog.setTitle("Are you sure this Plan is not DONE ?");
                                alertDialog.setMessage("This will remove the Check sign from the Plan");

                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "NOT DONE", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        String replace = cursor.getString(3).replace("\uD83C\uDD53" + "\uD83C\uDD5E" + "\uD83C\uDD5D" + "\uD83C\uDD54 ", "");

                                        ContentValues cv = new ContentValues();
                                        cv.put(dbHelper.SCHEDULE, replace);
                                        cv.put(dbHelper.DONE, "false");

                                        //Update Done Status to "true"
                                        db.update(dbHelper.DBTRAVEL, cv, dbHelper.TRAVEL + "=?" + " AND " + dbHelper.DATE + "=?" + " AND " + dbHelper.SCHEDULE + "=?", new String[] {
                                                cursor.getString(1), cursor.getString(2), cursor.getString(3)});

                                        //Set cursor to Database Query and read TravelPlanData
                                        cursor = readTravelPlanData();

                                        //Set SimpleCursorAdapter connects with ListView
                                        cursorAdapter = new SimpleCursorAdapter(TravelLoadingActivity.this, R.layout.travelplanlistformat, cursor,
                                                need_column, new int[]{R.id.travelplan_name_part, R.id.travelplan_note_part}, 0);
                                        travel_plan_list.setAdapter(cursorAdapter);
                                    }
                                });
                            }

                            //Language: Korean
                            else
                            {
                                alertDialog.setTitle("해당 여행을 완료하지 않으셨습니까 ?");
                                alertDialog.setMessage("여행이 완료되지 않음으로 설정될 것입니다");

                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "미완료", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        String replace = cursor.getString(3).replace("\uD83C\uDD53" + "\uD83C\uDD5E" + "\uD83C\uDD5D" + "\uD83C\uDD54 ", "");

                                        ContentValues cv = new ContentValues();
                                        cv.put(dbHelper.SCHEDULE, replace);
                                        cv.put(dbHelper.DONE, "false");

                                        //Update Done Status to "true"
                                        db.update(dbHelper.DBTRAVEL, cv, dbHelper.TRAVEL + "=?" + " AND " + dbHelper.DATE + "=?" + " AND " + dbHelper.SCHEDULE + "=?", new String[] {
                                                cursor.getString(1), cursor.getString(2), cursor.getString(3)});

                                        //Set cursor to Database Query and read TravelPlanData
                                        cursor = readTravelPlanData();

                                        //Set SimpleCursorAdapter connects with ListView
                                        cursorAdapter = new SimpleCursorAdapter(TravelLoadingActivity.this, R.layout.travelplanlistformat, cursor,
                                                need_column, new int[]{R.id.travelplan_name_part, R.id.travelplan_note_part}, 0);
                                        travel_plan_list.setAdapter(cursorAdapter);
                                    }
                                });
                            }
                        }

                        alertDialog.show();
                    }
                }
            });

            //Set onItemLongClick for ListView
            travel_plan_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    //Get cursor to clicked item position
                    cursor.moveToPosition(position);

                    //Only enters if travel DONE status is "false"
                    if(cursor.getString(8).equals("false"))
                    {
                        //New Intent for Edit Travel Activity
                        Intent edit_travel_intent = new Intent(getApplicationContext(), TravelEditActivity.class);

                        //Send DATA to TravelEditActivity for edit or delete Database context
                        //Column 1: Travel Name
                        //Column 2: Specific Date
                        edit_travel_intent.putExtra(TravelEditActivity.DATA_GET_TRAVEL_NAME, cursor.getString(1));
                        edit_travel_intent.putExtra(TravelEditActivity.DATA_GET_DATE, cursor.getString(2));
                        edit_travel_intent.putExtra(TravelEditActivity.DATA_GET_ITEM_POSITION, Integer.toString(position));
                        edit_travel_intent.putExtra(TravelEditActivity.DATA_GET_EDIT_STATUS, "true");

                        startActivityForResult(edit_travel_intent, ACTIVITY_RESULT);
                    }

                    return true;
                }
            });
        }
    }
}