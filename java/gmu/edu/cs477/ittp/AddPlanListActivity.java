package gmu.edu.cs477.ittp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddPlanListActivity extends AppCompatActivity {
    //Set updated DATA save place for TRAVEL NAME, DATE, EDIT BUTTON STATUS, DATA POSITION, START DATE and END DATE
    public final static String DATA_TRAVEL_NAME = "gmu.edu.cs477.ittp.DATA_TRAVEL_NAME";
    public final static String DATA_DATE = "gmu.edu.cs477.ittp.DATA_DATE";
    public final static String DATA_EDIT_STATUS = "gmu.edu.cs477.ittp.DATA_EDIT_STATUS";
    public final static String DATA_POSITION = "gmu.edu.cs477.ittp.DATA_POSITION";
    public final static String DATA_START_DATE_PLAN = "gmu.edu.cs477.ittp.DATA_START_DATE_PLAN";
    public final static String DATA_END_DATE_PLAN = "gmu.edu.cs477.ittp.DATA_END_DATE_PLAN";

    //Set variables
    TextView schedule_text;
    TextView address_text;
    TextView note_text;
    EditText schedule_name;
    EditText address_info;
    EditText notes_info;
    Button cancel_plan;
    Button add_plan;

    //Set extra variables for control
    String converted_start_date;
    String converted_end_date;

    //Set variables for Database
    private SQLiteDatabase db = null;
    private DatabaseOpenHelper dbHelper = null;
    final static String[] columns = {DatabaseOpenHelper.ID, DatabaseOpenHelper.TRAVEL,
            DatabaseOpenHelper.DATE, DatabaseOpenHelper.SCHEDULE, DatabaseOpenHelper.ADDRESS, DatabaseOpenHelper.NOTE, DatabaseOpenHelper.STARTDATE, DatabaseOpenHelper.ENDDATE, DatabaseOpenHelper.DONE};
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addplanlist);

        //Initialize variables
        schedule_text = (TextView) findViewById(R.id.schedule_text);
        address_text = (TextView) findViewById(R.id.address_text);
        note_text = (TextView) findViewById(R.id.note_text);
        schedule_name = (EditText) findViewById(R.id.schedule_info);
        address_info = (EditText) findViewById(R.id.address_info);
        notes_info = (EditText) findViewById(R.id.extra_note);
        cancel_plan = (Button) findViewById(R.id.cancel_button_addplan);
        add_plan = (Button) findViewById(R.id.add_button_addplan);

        //Set Database
        dbHelper = new DatabaseOpenHelper(this);
        db = dbHelper.getWritableDatabase();

        //Set Language setting
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String language = sharedPreferences.getString("language_preference", "en");

        //If Language preference is changed from SettingActivity
        if(!language.equalsIgnoreCase(""))
        {
            //Language: English
            if(language.equals("en"))
            {
                schedule_text.setText("\u2713 Schedule Name: ");
                address_text.setText("\u2713 Location Address: ");
                note_text.setText("\u2713 Notes: ");

                schedule_name.setHint("enter schedule");
                address_info.setHint("enter address");
                notes_info.setHint("enter notes");

                cancel_plan.setText("CANCEL");
            }

            //Language: Korean
            else
            {
                schedule_text.setText("\u2713 계획: ");
                address_text.setText("\u2713 주소: ");
                note_text.setText("\u2713 추가사항: ");

                schedule_name.setHint("스케쥴을 입력하세요");
                address_info.setHint("주소를 입력하세요");
                notes_info.setHint("추가사항을 입력하세요");

                cancel_plan.setText("취소");
            }
        }

        //check "EDIT" item status
        if(super.getIntent().getExtras().getString(DATA_EDIT_STATUS).equals("true"))
        {
            //If Language preference is changed from SettingActivity
            if(!language.equalsIgnoreCase(""))
            {
                //Language: English
                if(language.equals("en"))
                {
                    setTitle("Edit Plan");
                }

                //Language: Korean
                else
                {
                    setTitle("계획 수정");
                }
            }
        }

        //else "ADD" item status
        else
        {
            //If Language preference is changed from SettingActivity
            if(!language.equalsIgnoreCase(""))
            {
                //Language: English
                if(language.equals("en"))
                {
                    setTitle("Add Plan");
                }

                //Language: Korean
                else
                {
                    setTitle("계획 추가");
                }
            }
        }

        //If Registered plan is clicked for edit
        if(getTitle().toString().equals("Edit Plan") || getTitle().toString().equals("계획 수정"))
        {
            //If Language preference is changed from SettingActivity
            if(!language.equalsIgnoreCase(""))
            {
                //Language: English
                if(language.equals("en"))
                {
                    //Set "EDIT" button
                    add_plan.setText("EDIT");
                }

                //Language: Korean
                else
                {
                    //Set "수정" button
                    add_plan.setText("수정");
                }
            }

            //Get Data from Database
            cursor = getnEditPlanData();

            //Get Clicked Item position
            String pos = super.getIntent().getExtras().getString(DATA_POSITION);
            int position = Integer.parseInt(pos);

            //Move cursor to the data position
            cursor.moveToPosition(position);

            //Column 3: Schedule Name
            //Column 4: Address info
            //Column 5: Notes info
            schedule_name.setText(cursor.getString(3));
            address_info.setText(cursor.getString(4));
            notes_info.setText(cursor.getString(5));
        }

        //else title is "Add Plan" or "계획 추가"
        else
        {
            //If Language preference is changed from SettingActivity
            if(!language.equalsIgnoreCase(""))
            {
                //Language: English
                if(language.equals("en"))
                {
                    //Set "ADD" button
                    add_plan.setText("ADD");
                }

                //Language: Korean
                else
                {
                    //Set "추가" button
                    add_plan.setText("추가");
                }
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

    //Cursor get and Edit Plan Data method
    private Cursor getnEditPlanData()
    {
        return db.query(dbHelper.DBPLAN, columns, dbHelper.DATE + "=?", new String[] {super.getIntent().getExtras().getString(DATA_DATE)}, null, null, null);
    }

    //When "CANCEL" button is clicked
    public void gotoCancelPlan(View v)
    {
        finish();
    }

    //When "ADD" or "추가" or "EDIT" or "수정" button is clicked
    public void gotoAddPlan(View v)
    {
        Intent plan_intent = new Intent();

        //if button is "ADD" or "추가"
        if(add_plan.getText().toString().equals("ADD") || add_plan.getText().toString().equals("추가"))
        {
            //Add plans in Database
            ContentValues cv = new ContentValues();
            String add_schedule_name = schedule_name.getText().toString();
            String add_address_info = address_info.getText().toString();
            String add_notes_info = notes_info.getText().toString();

            //If Schedule Name is not written
            if(add_schedule_name.trim().length() == 0)
            {
                if(add_plan.getText().toString().equals("ADD"))
                {
                    Toast.makeText(getApplicationContext(), "ERROR: No Schedule Name Provided", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    Toast.makeText(getApplicationContext(), "에러: 계획 제목이 비어있습니다", Toast.LENGTH_SHORT).show();
                }
            }

            //else Schedule Name is provided
            else
            {
                cv.put(dbHelper.TRAVEL, super.getIntent().getExtras().getString(DATA_TRAVEL_NAME));
                cv.put(dbHelper.DATE, super.getIntent().getExtras().getString(DATA_DATE));
                cv.put(dbHelper.SCHEDULE, add_schedule_name);
                cv.put(dbHelper.ADDRESS, add_address_info);
                cv.put(dbHelper.NOTE, add_notes_info);

                //Change String to Date Format
                try
                {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm/dd/yyyy");
                    Calendar date_convert = Calendar.getInstance();

                    date_convert.setTime(simpleDateFormat.parse(super.getIntent().getExtras().getString(DATA_START_DATE_PLAN)));
                    converted_start_date = simpleDateFormat.format(date_convert.getTime());

                    date_convert.setTime(simpleDateFormat.parse(super.getIntent().getExtras().getString(DATA_END_DATE_PLAN)));
                    converted_end_date = simpleDateFormat.format(date_convert.getTime());
                }

                catch(java.text.ParseException e)
                {
                    e.printStackTrace();
                }

                cv.put(dbHelper.STARTDATE, converted_start_date);
                cv.put(dbHelper.ENDDATE, converted_end_date);
                cv.put(dbHelper.DONE, "false");

                db.insert(dbHelper.DBPLAN, null, cv);

                //Set result and finish this Activity
                setResult(Activity.RESULT_OK, plan_intent);
                finish();
            }
        }

        //else button is "EDIT" or "수정"
        else
        {
            //Get Clicked Item position
            String pos = super.getIntent().getExtras().getString(DATA_POSITION);
            int position = Integer.parseInt(pos);

            //Move cursor to the data position
            cursor.moveToPosition(position);

            //Edit plans in Database
            ContentValues cv = new ContentValues();
            String add_schedule_name = schedule_name.getText().toString();
            String add_address_info = address_info.getText().toString();
            String add_notes_info = notes_info.getText().toString();

            //If Schedule Name is not written
            if(add_schedule_name.trim().length() == 0)
            {
                if(add_plan.getText().toString().equals("EDIT"))
                {
                    Toast.makeText(getApplicationContext(), "ERROR: No Schedule Name Provided", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    Toast.makeText(getApplicationContext(), "에러: 계획 제목이 비어있습니다", Toast.LENGTH_SHORT).show();
                }
            }

            //else Schedule Name is provided
            else
            {
                cv.put(dbHelper.SCHEDULE, add_schedule_name);
                cv.put(dbHelper.ADDRESS, add_address_info);
                cv.put(dbHelper.NOTE, add_notes_info);

                db.update(dbHelper.DBPLAN, cv, dbHelper.SCHEDULE + "=?", new String[] {cursor.getString(3)});

                //Set result and finish this Activity
                setResult(Activity.RESULT_OK, plan_intent);
                finish();
            }
        }
    }
}