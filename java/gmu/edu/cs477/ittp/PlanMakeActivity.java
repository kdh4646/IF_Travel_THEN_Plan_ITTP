package gmu.edu.cs477.ittp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class PlanMakeActivity extends AppCompatActivity {
    //Set updated DATA save place for Plan Title, Start Date, End Date, Count Layouts, Check if Next and Previous Date
    public final static String DATA_TITLE = "gmu.edu.cs477.ittp.DATA_TITLE";
    public final static String DATA_START_DATE = "gmu.edu.cs477.ittp.DATA_START_DATE";
    public final static String DATA_END_DATE = "gmu.edu.cs477.ittp.DATA_END_DATE";
    public final static String DATA_COUNT_LAYOUTS = "gmu.edu.cs477.ittp.DATA_COUNT_LAYOUTS";
    public final static String DATA_IS_NEXT = "gmu.edu.cs477.ittp.DATA_IS_NEXT";
    public final static String DATA_PREV_DATE = "gmu.edu.cs477.ittp.DATA_PREV_DATE";

    //Value for "requestCode"
    public final int ACTIVITY_RESULT = 1;

    //Set variables
    TextView travel_title;
    TextView from_to_text;
    TextView days_nights_text;
    TextView specific_date;
    TextView default_text;
    ListView specific_list;
    private LinearLayout Dots_layout;
    private ImageView[] dots;
    FloatingActionButton fab;
    Button previous_slide;
    Button next_slide;

    //Set extra variables for control
    String send_title;
    String send_start_date;
    String send_end_date;
    String send_db_start_date;
    String send_db_end_date;
    String prev_date;
    String update_date;
    int num_layouts;
    LinearLayout planmake_layout;

    //Set variables for Database
    private SQLiteDatabase db = null;
    private DatabaseOpenHelper dbHelper = null;
    final static String[] columns = {DatabaseOpenHelper.ID, DatabaseOpenHelper.TRAVEL,
            DatabaseOpenHelper.DATE, DatabaseOpenHelper.SCHEDULE, DatabaseOpenHelper.ADDRESS, DatabaseOpenHelper.NOTE, DatabaseOpenHelper.STARTDATE, DatabaseOpenHelper.ENDDATE, DatabaseOpenHelper.DONE};
    final static String[] need_column = {DatabaseOpenHelper.SCHEDULE};
    SimpleCursorAdapter cursorAdapter;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planmake);

        //Initialize variables
        travel_title = (TextView) findViewById(R.id.travel_title);
        from_to_text = (TextView) findViewById(R.id.from_to);
        days_nights_text = (TextView) findViewById(R.id.days_nights);
        specific_date = (TextView) findViewById(R.id.specific_date_text);
        default_text = (TextView) findViewById(R.id.default_listview_text);
        specific_list = (ListView) findViewById(R.id.specific_date_listview);
        Dots_layout = (LinearLayout) findViewById(R.id.dots);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        previous_slide = (Button) findViewById(R.id.previous_slide_button);
        next_slide = (Button) findViewById(R.id.next_slide_button);

        //Get DATA from other Activities
        String title_text = super.getIntent().getExtras().getString(DATA_TITLE);
        String start_text = super.getIntent().getExtras().getString(DATA_START_DATE);
        String end_text = super.getIntent().getExtras().getString(DATA_END_DATE);

        //Set DATA for Send Information to new Activity
        send_title = title_text;
        send_start_date = start_text;
        send_end_date = end_text;

        //Set DATA Title
        travel_title.setText(title_text);

        //Set Language setting
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String language = sharedPreferences.getString("language_preference", "en");

        //Set start and end date for calculation (replace Text and keep Numbers)
        String start_text_replace = "";
        String end_text_replace = "";

        //If Language preference is changed from SettingActivity
        if(!language.equalsIgnoreCase(""))
        {
            //Language: English
            if(language.equals("en"))
            {
                //Set DATA Start Date and End Date
                start_text_replace = start_text.replace("Start Date: ", "");
                end_text_replace = end_text.replace("End Date: ", "");
                from_to_text.setText(start_text_replace + " - " + end_text_replace);

                previous_slide.setText("PREVIOUS");
                next_slide.setText("NEXT");
            }

            //Language: Korean
            else
            {
                //Set DATA Start Date and End Date
                start_text_replace = start_text.replace("첫날: ", "");
                end_text_replace = end_text.replace("마지막날: ", "");
                from_to_text.setText(start_text_replace + " - " + end_text_replace);

                previous_slide.setText("이전");
                next_slide.setText("다음");
            }
        }

        //Set Data for Database Update in AddPlanListActivity
        send_db_start_date = start_text_replace;
        send_db_end_date = end_text_replace;

        //Set DATA Days and Nights
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy");
            long difference_two_dates = dateFormat.parse(end_text_replace).getTime() - dateFormat.parse(start_text_replace).getTime();
            num_layouts = (int) TimeUnit.DAYS.convert(difference_two_dates, TimeUnit.MILLISECONDS) + 1;

            //If Language preference is changed from SettingActivity
            if(!language.equalsIgnoreCase(""))
            {
                //Language: English
                if(language.equals("en"))
                {
                    days_nights_text.setText(TimeUnit.DAYS.convert(difference_two_dates, TimeUnit.MILLISECONDS) + 1 + " days" + "(" + TimeUnit.DAYS.convert(difference_two_dates, TimeUnit.MILLISECONDS) + " nights)");
                }

                //Language: Korean
                else
                {
                    days_nights_text.setText(TimeUnit.DAYS.convert(difference_two_dates, TimeUnit.MILLISECONDS) + " 박 " + (TimeUnit.DAYS.convert(difference_two_dates, TimeUnit.MILLISECONDS) + 1) + " 일");
                }
            }

            //Calendar variable for calculate DATE
            Calendar date_calc = Calendar.getInstance();
            Calendar date_prev = Calendar.getInstance();

            //Get incremented date for planning when "NEXT" or "다음" is clicked
            date_calc.setTime(dateFormat.parse(start_text_replace));
            date_calc.add(Calendar.DATE, super.getIntent().getExtras().getInt(DATA_COUNT_LAYOUTS));
            update_date = dateFormat.format(date_calc.getTime());

            //Get decreased date for planning when "PREVIOUS" or "이전" is clicked
            date_prev.setTime(dateFormat.parse(start_text_replace));
            date_prev.add(Calendar.DATE, super.getIntent().getExtras().getInt(DATA_COUNT_LAYOUTS) - 1);
            prev_date = dateFormat.format(date_prev.getTime());
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        //If PlanMakeActivity is FIRST time created or "NEXT" or "다음" button is clicked
        if (TextUtils.isEmpty(super.getIntent().getExtras().getString(DATA_IS_NEXT)) || super.getIntent().getExtras().getString(DATA_IS_NEXT).equals("true")) {
            //Set Specific Date for planning by increasing date
            specific_date.setText("\u275d " + update_date + " \u275e");
        }

        //else if "PREVIOUS" or "이전" button is clicked
        else if (super.getIntent().getExtras().getString(DATA_IS_NEXT).equals("false")) {
            //Set Specific Date for planning by decreasing date
            specific_date.setText("\u275d " + super.getIntent().getExtras().getString(DATA_PREV_DATE) + " \u275e");
        }

        //Set Database
        dbHelper = new DatabaseOpenHelper(this);
        db = dbHelper.getWritableDatabase();

        //Set cursor to Database Query and read PlanData
        cursor = readPlanData();

        //check if Table is Empty for Specific Date
        if (cursor.getCount() == 0) {
            //NO ListView needed for now
            specific_list.setVisibility(View.GONE);

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
            cursorAdapter = new SimpleCursorAdapter(this, R.layout.planlistformat, cursor,
                    need_column, new int[]{R.id.schedule_name_part}, 0);
            specific_list.setAdapter(cursorAdapter);

            //Set onItemClick for ListView
            specific_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //New Intent for Edit Activity
                    Intent add_intent = new Intent(getApplicationContext(), AddPlanListActivity.class);

                    //Send clicked item's position to new intent
                    String pos = Integer.toString(position);
                    add_intent.putExtra(AddPlanListActivity.DATA_POSITION, pos);

                    //If PlanMakeActivity is FIRST time created or "NEXT" or "다음" button was clicked
                    if (TextUtils.isEmpty(getIntent().getExtras().getString(DATA_IS_NEXT)) || getIntent().getExtras().getString(DATA_IS_NEXT).equals("true")) {
                        //Send date to get saved data equals date
                        add_intent.putExtra(AddPlanListActivity.DATA_DATE, update_date);
                    }

                    //else "PREVIOUS" or "이전" button was clicked
                    else {
                        //Send date to get saved data equals date
                        add_intent.putExtra(AddPlanListActivity.DATA_DATE, getIntent().getExtras().getString(DATA_PREV_DATE));
                    }

                    //Send Start and End Date
                    add_intent.putExtra(AddPlanListActivity.DATA_START_DATE_PLAN, send_db_start_date);
                    add_intent.putExtra(AddPlanListActivity.DATA_END_DATE_PLAN, send_db_end_date);

                    //Send this is for "EDIT" or "수정"
                    add_intent.putExtra(AddPlanListActivity.DATA_EDIT_STATUS, "true");

                    startActivityForResult(add_intent, ACTIVITY_RESULT);
                }
            });

            //Set onItemLongClick for ListView
            specific_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    //Get cursor to clicked item position
                    cursor.moveToPosition(position);

                    //Set Language setting
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String language = sharedPreferences.getString("language_preference", "en");

                    //Language: English
                    if(language.equals("en"))
                    {
                        //Column 3: Schedule Name
                        Toast.makeText(getApplicationContext(), "Schedule Deleted: " + cursor.getString(3), Toast.LENGTH_SHORT).show();
                    }

                    //Language: Korean
                    else
                    {
                        //Column 3: Schedule Name
                        Toast.makeText(getApplicationContext(), "계획이 삭제되었습니다: " + cursor.getString(3), Toast.LENGTH_SHORT).show();
                    }

                    //Delete Long Clicked Schedule from the List and the Database
                    db.delete(dbHelper.DBPLAN, dbHelper.SCHEDULE + "=?", new String[]{cursor.getString(3)});

                    //Read Plan Data and update cursor and cursorAdapter with ListView
                    cursor = readPlanData();

                    //If Nothing exist
                    if(cursor.getCount() == 0)
                    {
                        default_text.setVisibility(View.VISIBLE);
                        specific_list.setVisibility(View.GONE);

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

                    //else still some plans exist
                    else
                    {
                        cursorAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.planlistformat, cursor,
                                need_column, new int[]{R.id.schedule_name_part}, 1);
                        specific_list.setAdapter(cursorAdapter);
                    }

                    return true;
                }
            });
        }

        //Create Tracking Dots for Layouts
        createDots(super.getIntent().getExtras().getInt(DATA_COUNT_LAYOUTS));

        //If start to plan, dates are set, NO previous or 이전 button
        if (super.getIntent().getExtras().getInt(DATA_COUNT_LAYOUTS) == 0) {
            previous_slide.setVisibility(View.INVISIBLE);
        }

        //If Start Date is Same as End Date and NO more Layouts remain
        //set next slide button to "DONE" or "완료"
        if (num_layouts - 1 == super.getIntent().getExtras().getInt(DATA_COUNT_LAYOUTS)) {

            //If Language preference is changed from SettingActivity
            if(!language.equalsIgnoreCase(""))
            {
                //Language: English
                if(language.equals("en"))
                {
                    next_slide.setText("DONE");
                }

                //Language: Korean
                else
                {
                    next_slide.setText("완료");
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
                planmake_layout = (LinearLayout) findViewById(R.id.activity_planmake);
                planmake_layout.setBackgroundResource(R.drawable.main_activity_background_default);
            }

            //Background: RED
            else if(background.equals("color_red"))
            {
                planmake_layout = (LinearLayout) findViewById(R.id.activity_planmake);
                planmake_layout.setBackgroundResource(R.drawable.main_activity_background_red);
            }

            //Background: GREEN
            else if(background.equals("color_green"))
            {
                planmake_layout = (LinearLayout) findViewById(R.id.activity_planmake);
                planmake_layout.setBackgroundResource(R.drawable.main_activity_background_green);
            }

            //Background: BLUE
            else
            {
                planmake_layout = (LinearLayout) findViewById(R.id.activity_planmake);
                planmake_layout.setBackgroundResource(R.drawable.main_activity_background_blue);
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
                planmake_layout = (LinearLayout) findViewById(R.id.activity_planmake);
                planmake_layout.setBackgroundResource(R.drawable.main_activity_background_default);
            }

            //Background: RED
            else if(background.equals("color_red"))
            {
                planmake_layout = (LinearLayout) findViewById(R.id.activity_planmake);
                planmake_layout.setBackgroundResource(R.drawable.main_activity_background_red);
            }

            //Background: GREEN
            else if(background.equals("color_green"))
            {
                planmake_layout = (LinearLayout) findViewById(R.id.activity_planmake);
                planmake_layout.setBackgroundResource(R.drawable.main_activity_background_green);
            }

            //Background: BLUE
            else
            {
                planmake_layout = (LinearLayout) findViewById(R.id.activity_planmake);
                planmake_layout.setBackgroundResource(R.drawable.main_activity_background_blue);
            }
        }
    }

    //on Destroy method
    @Override
    protected void onDestroy()
    {
        db.close();
        dbHelper.deleteDatabase();
        super.onDestroy();
    }

    //Cursor readPlanData method
    private Cursor readPlanData()
    {
        //If PlanMakeActivity is FIRST time created or "NEXT" button is clicked
        if(TextUtils.isEmpty(super.getIntent().getExtras().getString(DATA_IS_NEXT)) || super.getIntent().getExtras().getString(DATA_IS_NEXT).equals("true"))
        {
            return db.query(dbHelper.DBPLAN, columns, dbHelper.DATE + "=?", new String[] {update_date}, null, null, null);
        }

        //else "PREVIOUS" button is clicked
        else
        {
            return db.query(dbHelper.DBPLAN, columns, dbHelper.DATE + "=?", new String[] {super.getIntent().getExtras().getString(DATA_PREV_DATE)}, null, null, null);
        }
    }

    //Cursor readAllData method
    private Cursor readAllData()
    {
        return db.query(dbHelper.DBPLAN, columns, null, new String[] {}, null, null, null);
    }

    //Create Dot
    private void createDots(int current_position)
    {
        if(Dots_layout != null)
        {
            Dots_layout.removeAllViews();
        }

        dots = new ImageView[num_layouts];

        for(int i = 0; i < num_layouts; i++)
        {
           dots[i] = new ImageView(this);

           //If selected slide then set BLACK dots
           if(i == current_position)
           {
               dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.selected_dots));
           }

           //else NOT selected slide then set GRAY dots
           else
           {
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
        Intent add_intent = new Intent(this, AddPlanListActivity.class);
        add_intent.putExtra(AddPlanListActivity.DATA_TRAVEL_NAME, send_title);

        //If PlanMakeActivity is FIRST time created or "NEXT" or "다음" button was clicked
        if(TextUtils.isEmpty(super.getIntent().getExtras().getString(DATA_IS_NEXT)) || super.getIntent().getExtras().getString(DATA_IS_NEXT).equals("true"))
        {
            //Send date to get saved data equals date
            add_intent.putExtra(AddPlanListActivity.DATA_DATE, update_date);
        }

        //else "PREVIOUS" or "이전" button was clicked
        else
        {
            //Send date to get saved data equals date
            add_intent.putExtra(AddPlanListActivity.DATA_DATE, super.getIntent().getExtras().getString(DATA_PREV_DATE));
        }

        //Send Start and End Date
        add_intent.putExtra(AddPlanListActivity.DATA_START_DATE_PLAN, send_db_start_date);
        add_intent.putExtra(AddPlanListActivity.DATA_END_DATE_PLAN, send_db_end_date);

        add_intent.putExtra(AddPlanListActivity.DATA_EDIT_STATUS, "false");
        startActivityForResult(add_intent, ACTIVITY_RESULT);
    }

    //When Click "PREVIOUS" or "이전" button
    public void gotoPreviousSlide(View v)
    {
        //Get number of layouts
        int count = super.getIntent().getExtras().getInt(DATA_COUNT_LAYOUTS);

        //When clicked decrease count
        count--;

        Intent prev_slide_intent = new Intent(this, PlanMakeActivity.class);

        prev_slide_intent.putExtra(DATA_TITLE, send_title);
        prev_slide_intent.putExtra(DATA_START_DATE, send_start_date);
        prev_slide_intent.putExtra(DATA_END_DATE, send_end_date);
        prev_slide_intent.putExtra(DATA_COUNT_LAYOUTS, count);
        prev_slide_intent.putExtra(DATA_IS_NEXT, "false");
        prev_slide_intent.putExtra(DATA_PREV_DATE, prev_date);

        startActivity(prev_slide_intent);

        finish();
    }

    //When Click "NEXT" or "DONE" button or "다음" or "완료" button
    public void gotoNextSlide(View v)
    {
        //Count number of layouts
        int count = super.getIntent().getExtras().getInt(DATA_COUNT_LAYOUTS, 0);

        //When Clicked Increase count
        count++;

        //If Layouts remain and "NEXT" or "다음" button
        if(((count <= num_layouts - 1) && next_slide.getText().toString().equals("NEXT"))
            || ((count <= num_layouts - 1) && next_slide.getText().toString().equals("다음")))
        {
            //Start next day PlanMakeActivity
            Intent next_slide_intent = new Intent(this, PlanMakeActivity.class);

            next_slide_intent.putExtra(DATA_TITLE, send_title);
            next_slide_intent.putExtra(DATA_START_DATE, send_start_date);
            next_slide_intent.putExtra(DATA_END_DATE, send_end_date);
            next_slide_intent.putExtra(DATA_COUNT_LAYOUTS, count);
            next_slide_intent.putExtra(DATA_IS_NEXT, "true");

            startActivity(next_slide_intent);

            finish();
        }

        //If NO more Layouts remain and "DONE" or "완료" button
        if(next_slide.getText().toString().equals("DONE") || next_slide.getText().toString().equals("완료"))
        {
            //Read All Data from Plan Table
            cursor = readAllData();

            //Nothing Added for Entire Travel
            if(cursor.getCount() == 0)
            {
                if(next_slide.getText().toString().equals("DONE"))
                {
                    Toast.makeText(getApplicationContext(), "\t\t\tTravel has NO Plan \n ** Auto: Travel Deleted **", Toast.LENGTH_LONG).show();
                }

                else if(next_slide.getText().toString().equals("완료"))
                {
                    Toast.makeText(getApplicationContext(), "\t\t\t계획이 비어있습니다 \n ** 자동: 여행계획이 삭제되었습니다 **", Toast.LENGTH_LONG).show();
                }
            }

            //Some plans are added
            else
            {
                //Take All Plan Data from Plan table to finalized Travel table
                db.execSQL("INSERT INTO " + dbHelper.DBTRAVEL + " SELECT * FROM " + dbHelper.DBPLAN);

                //Reset the Plan table for making another plan
                db.execSQL("DELETE FROM " + dbHelper.DBPLAN);

                //Set Toast Message for Adding New Travel
                cursor.moveToFirst();

                if(next_slide.getText().toString().equals("DONE"))
                {
                    Toast.makeText(getApplicationContext(), "New Travel: ** " + cursor.getString(1) + " ** Added" , Toast.LENGTH_LONG).show();
                }

                else if(next_slide.getText().toString().equals("완료"))
                {
                    Toast.makeText(getApplicationContext(), "새로운 여행: ** " + cursor.getString(1) + " ** 추가되었습니다" , Toast.LENGTH_LONG).show();
                }
            }

            //Go to MainActivity
            Intent main_intent = new Intent(this, MainActivity.class);
            startActivity(main_intent);
            finish();
        }
    }

    //When back button is pressed go back to MainActivity screen
    public void onBackPressed()
    {
        //Reset the Plan Table
        db.execSQL("DELETE FROM " + dbHelper.DBPLAN);

        //Finish this Activity and return to MainActivity
        Intent main_intent = new Intent(this, MainActivity.class);
        startActivity(main_intent);
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
            cursor = readPlanData();

            //Some plans are added
            if(cursor.getCount() != 0)
            {
                default_text.setVisibility(View.GONE);
                specific_list.setVisibility(View.VISIBLE);
            }

            //Set SimpleCursorAdapter connects with ListView
            cursorAdapter = new SimpleCursorAdapter(this, R.layout.planlistformat, cursor,
                    need_column, new int[]{R.id.schedule_name_part},0);
            specific_list.setAdapter(cursorAdapter);

            //Set onItemClick for ListView
            specific_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //New Intent for Edit Activity
                    Intent add_intent = new Intent(getApplicationContext(), AddPlanListActivity.class);

                    //Send clicked item's position to new intent
                    String pos = Integer.toString(position);
                    add_intent.putExtra(AddPlanListActivity.DATA_POSITION, pos);

                    //If PlanMakeActivity is FIRST time created or "NEXT" or "다음" button was clicked
                    if(TextUtils.isEmpty(getIntent().getExtras().getString(DATA_IS_NEXT)) || getIntent().getExtras().getString(DATA_IS_NEXT).equals("true"))
                    {
                        //Send date to get saved data equals date
                        add_intent.putExtra(AddPlanListActivity.DATA_DATE, update_date);
                    }

                    //else "PREVIOUS" or "이전" button was clicked
                    else
                    {
                        //Send date to get saved data equals date
                        add_intent.putExtra(AddPlanListActivity.DATA_DATE, getIntent().getExtras().getString(DATA_PREV_DATE));
                    }

                    //Send this is for "EDIT" or "수정"
                    add_intent.putExtra(AddPlanListActivity.DATA_EDIT_STATUS, "true");

                    startActivityForResult(add_intent, ACTIVITY_RESULT);
                }
            });

            //Set onItemLongClick for ListView
            specific_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    //Get cursor to clicked item position
                    cursor.moveToPosition(position);

                    //Set Language setting
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String language = sharedPreferences.getString("language_preference", "en");

                    //Language: English
                    if(language.equals("en"))
                    {
                        //Column 3: Schedule Name
                        Toast.makeText(getApplicationContext(), "Schedule Deleted: " + cursor.getString(3), Toast.LENGTH_SHORT).show();
                    }

                    //Language: Korean
                    else
                    {
                        //Column 3: Schedule Name
                        Toast.makeText(getApplicationContext(), "계획이 삭제되었습니다: " + cursor.getString(3), Toast.LENGTH_SHORT).show();
                    }

                    //Delete Long Clicked Schedule from the List and the Database
                    db.delete(dbHelper.DBPLAN, dbHelper.SCHEDULE + "=?", new String[] {cursor.getString(3)});

                    //Read Plan Data and update cursor and cursorAdapter with ListView
                    cursor = readPlanData();

                    //If Nothing exist
                    if(cursor.getCount() == 0)
                    {
                        default_text.setVisibility(View.VISIBLE);
                        specific_list.setVisibility(View.GONE);
                    }

                    //else still some plans exist
                    else
                    {
                        cursorAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.planlistformat, cursor,
                                need_column, new int[]{R.id.schedule_name_part}, 1);
                        specific_list.setAdapter(cursorAdapter);
                    }

                    return true;
                }
            });
        }
    }
}