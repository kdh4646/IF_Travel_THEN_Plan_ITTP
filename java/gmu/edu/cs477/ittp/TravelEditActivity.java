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

public class TravelEditActivity extends AppCompatActivity {
    //Set updated DATA save place for TRAVEL NAME, DATE, START DATE, END DATE, EDIT STATUS and SELECTED ITEM POSITION
    public final static String DATA_GET_TRAVEL_NAME = "gmu.edu.cs477.ittp.DATA_GET_TRAVEL_NAME";
    public final static String DATA_GET_DATE = "gmu.edu.cs477.ittp.DATA_GET_DATE";
    public final static String DATA_GET_START_DATE = "gmu.edu.cs477.ittp.DATA_GET_START_DATE";
    public final static String DATA_GET_END_DATE = "gmu.edu.cs477.ittp.DATA_GET_END_DATE";
    public final static String DATA_GET_EDIT_STATUS = "gmu.edu.cs477.ittp.DATA_GET_EDIT_STAUTS";
    public final static String DATA_GET_ITEM_POSITION = "gmu.edu.cs477.ittp.DATA_GET_ITEM_POSITION";

    //Set variables
    TextView schedule_name_text;
    TextView address_info_text;
    TextView notes_info_text;
    EditText edit_schedule_name;
    EditText edit_address_info;
    EditText edit_notes_info;
    Button delete_plan;
    Button edit_plan;

    //Set extra variables for control
    String pos;
    int position;

    //Set variables for Database
    private SQLiteDatabase db = null;
    private DatabaseOpenHelper dbHelper = null;
    final static String[] columns = {DatabaseOpenHelper.ID, DatabaseOpenHelper.TRAVEL,
            DatabaseOpenHelper.DATE, DatabaseOpenHelper.SCHEDULE, DatabaseOpenHelper.ADDRESS, DatabaseOpenHelper.NOTE, DatabaseOpenHelper.STARTDATE, DatabaseOpenHelper.ENDDATE, DatabaseOpenHelper.DONE};
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarveledit);

        //Initialize Variables
        schedule_name_text = (TextView) findViewById(R.id.schedule_name_text);
        address_info_text = (TextView) findViewById(R.id.address_info_text);
        notes_info_text = (TextView) findViewById(R.id.notes_info_text);
        edit_schedule_name = (EditText) findViewById(R.id.travel_schedule_info);
        edit_address_info = (EditText) findViewById(R.id.travel_address_info);
        edit_notes_info = (EditText) findViewById(R.id.travel_extra_note);
        delete_plan = (Button) findViewById(R.id.delete_travelplan_button);
        edit_plan = (Button) findViewById(R.id.edit_travelplan_button);

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
                schedule_name_text.setText("\u2713 Schedule Name: ");
                address_info_text.setText("\u2713 Location Address: ");
                notes_info_text.setText("\u2713 Notes: ");

                edit_schedule_name.setHint("enter schedule");
                edit_address_info.setHint("enter address");
                edit_notes_info.setHint("enter notes");

                delete_plan.setText("DELETE");
                edit_plan.setText("EDIT");
            }

            //Language: Korean
            else
            {
                schedule_name_text.setText("\u2713 계획: ");
                address_info_text.setText("\u2713 주소: ");
                notes_info_text.setText("\u2713 추가사항: ");

                edit_schedule_name.setHint("스케쥴을 입력하세요");
                edit_address_info.setHint("주소를 입력하세요");
                edit_notes_info.setHint("추가사항을 입력하세요");

                delete_plan.setText("삭제");
                edit_plan.setText("수정");
            }
        }

        //check "EDIT" item status
        //If "false" then it is for "ADD"
        if(super.getIntent().getExtras().getString(DATA_GET_EDIT_STATUS).equals("false"))
        {
            //If Language preference is changed from SettingActivity
            if(!language.equalsIgnoreCase(""))
            {
                //Language: English
                if(language.equals("en"))
                {

                    setTitle("Add Plan");
                    delete_plan.setText("CANCEL");
                    edit_plan.setText("ADD");
                }

                //Language: Korean
                else
                {
                    setTitle("계획 추가");
                    delete_plan.setText("취소");
                    edit_plan.setText("추가");
                }
            }
        }

        //else "true" then it is for "EDIT"
        else
        {
            //Set cursor to Database Query and read SelectedTravelPlanData
            cursor = readSelectedTravelPlanData();

            //Get the selected item position
            pos = super.getIntent().getExtras().getString(DATA_GET_ITEM_POSITION);
            position = Integer.parseInt(pos);

            //Move the cursor to the position to get information
            cursor.moveToPosition(position);

            //Column 3: Schedule Name
            //Column 4: Address info
            //Column 5: Notes info
            edit_schedule_name.setText(cursor.getString(3));
            edit_address_info.setText(cursor.getString(4));
            edit_notes_info.setText(cursor.getString(5));
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

    //Cursor readSelectedTravelPlanData method
    private Cursor readSelectedTravelPlanData()
    {
        return db.query(dbHelper.DBTRAVEL, columns, dbHelper.TRAVEL + "=?" + " AND " + dbHelper.DATE + "=?", new String[]{super.getIntent().getExtras().getString(DATA_GET_TRAVEL_NAME),
                            super.getIntent().getExtras().getString(DATA_GET_DATE)}, null, null, null);
    }

    //When "DELETE" or "CANCEL" or "삭제" or "취소" button is clicked
    public void gotoDeletePlan(View v)
    {
        //If button is "DELETE" or "삭제"
        if(delete_plan.getText().toString().equals("DELETE") || delete_plan.getText().toString().equals("삭제"))
        {
            Intent delete_intent = new Intent();

            //Set cursor to Database Query and read SelectedTravelPlanData
            cursor = readSelectedTravelPlanData();

            //Get the selected item position
            pos = super.getIntent().getExtras().getString(DATA_GET_ITEM_POSITION);
            position = Integer.parseInt(pos);

            //Move the cursor to the position to get information
            cursor.moveToPosition(position);

            if(delete_plan.getText().toString().equals("DELETE"))
            {
                //Column 3: Schedule Name
                Toast.makeText(getApplicationContext(), "Schedule Deleted: " + cursor.getString(3), Toast.LENGTH_SHORT).show();
            }

            else
            {
                //Column 3: Schedule Name
                Toast.makeText(getApplicationContext(), "계획이 삭제되었습니다: " + cursor.getString(3), Toast.LENGTH_SHORT).show();
            }

            //Delete Schedule from the List and the Database
            db.delete(dbHelper.DBTRAVEL, dbHelper.TRAVEL + "=?" + " AND " + dbHelper.DATE + "=?" + " AND " + dbHelper.SCHEDULE + "=?", new String[] {
                        cursor.getString(1), cursor.getString(2), cursor.getString(3)});

            //Set result and finish this Activity
            setResult(Activity.RESULT_OK, delete_intent);
            finish();
        }

        //else button is "CANCEL" or "취소"
        else
        {
            finish();
        }
    }

    //When "EDIT" or "ADD" or "수정" or "추가" button is clicked
    public void gotoEditPlan(View v)
    {
        Intent edit_add_intent = new Intent();

        //If button is "EDIT" or "수정"
        if(edit_plan.getText().toString().equals("EDIT") || edit_plan.getText().toString().equals("수정"))
        {
            //Set cursor to Database Query and read SelectedTravelPlanData
            cursor = readSelectedTravelPlanData();

            //Get the selected item position
            pos = super.getIntent().getExtras().getString(DATA_GET_ITEM_POSITION);
            position = Integer.parseInt(pos);

            //Move the cursor to the position to get information
            cursor.moveToPosition(position);

            //Edit plans in Database
            ContentValues cv = new ContentValues();
            String get_schedule_name = edit_schedule_name.getText().toString();
            String get_address_info = edit_address_info.getText().toString();
            String get_notes_info = edit_notes_info.getText().toString();

            //If Schedule Name is not written
            if(get_schedule_name.trim().length() == 0)
            {
                Toast.makeText(getApplicationContext(), "ERROR: No Schedule Name Provided", Toast.LENGTH_SHORT).show();
            }

            //else Schedule Name is provided
            else
            {
                cv.put(dbHelper.SCHEDULE, get_schedule_name);
                cv.put(dbHelper.ADDRESS, get_address_info);
                cv.put(dbHelper.NOTE, get_notes_info);

                db.update(dbHelper.DBTRAVEL, cv, dbHelper.TRAVEL + "=?" + " AND " + dbHelper.DATE + "=?" + " AND " + dbHelper.SCHEDULE + "=?", new String[] {
                            cursor.getString(1), cursor.getString(2), cursor.getString(3)});

                //Set result and finish this Activity
                setResult(Activity.RESULT_OK, edit_add_intent);
                finish();
            }
        }

        //else button is "ADD" or "추가"
        else
        {
            //Add plans in Database
            ContentValues cv = new ContentValues();
            String add_schedule_name = edit_schedule_name.getText().toString();
            String add_address_info = edit_address_info.getText().toString();
            String add_notes_info = edit_notes_info.getText().toString();

            //If Schedule Name is not written
            if(add_schedule_name.trim().length() == 0)
            {
                if(edit_plan.getText().toString().equals("ADD"))
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
                cv.put(dbHelper.TRAVEL, super.getIntent().getExtras().getString(DATA_GET_TRAVEL_NAME));
                cv.put(dbHelper.DATE, super.getIntent().getExtras().getString(DATA_GET_DATE));
                cv.put(dbHelper.SCHEDULE, add_schedule_name);
                cv.put(dbHelper.ADDRESS, add_address_info);
                cv.put(dbHelper.NOTE, add_notes_info);
                cv.put(dbHelper.STARTDATE, super.getIntent().getExtras().getString(DATA_GET_START_DATE));
                cv.put(dbHelper.ENDDATE, super.getIntent().getExtras().getString(DATA_GET_END_DATE));
                cv.put(dbHelper.DONE, "false");

                db.insert(dbHelper.DBTRAVEL, null, cv);

                //Set result and finish this Activity
                setResult(Activity.RESULT_OK, edit_add_intent);
                finish();
            }
        }
    }
}