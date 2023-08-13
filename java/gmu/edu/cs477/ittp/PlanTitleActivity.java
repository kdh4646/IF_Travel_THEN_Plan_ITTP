package gmu.edu.cs477.ittp;

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

public class PlanTitleActivity extends AppCompatActivity {
    //Set variable
    TextView title_text;
    EditText epic_plan;
    Button cancel_button;
    Button ok_button;
    String check_txt;
    boolean check_status;

    //Set variables for Database
    private SQLiteDatabase db = null;
    private DatabaseOpenHelper dbHelper = null;
    final static String[] columns = {DatabaseOpenHelper.ID, DatabaseOpenHelper.TRAVEL,
            DatabaseOpenHelper.DATE, DatabaseOpenHelper.SCHEDULE, DatabaseOpenHelper.ADDRESS, DatabaseOpenHelper.NOTE, DatabaseOpenHelper.STARTDATE, DatabaseOpenHelper.ENDDATE, DatabaseOpenHelper.DONE};
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plantitle);

        //Initialize variable
        title_text = (TextView) findViewById(R.id.title_textview);
        epic_plan = (EditText) findViewById(R.id.plan_title);
        cancel_button = (Button) findViewById(R.id.cancel_button);
        ok_button = (Button) findViewById(R.id.ok_button);

        //Set Database
        dbHelper = new DatabaseOpenHelper(this);
        db = dbHelper.getReadableDatabase();

        //Set cursor to Database Query and read TravelList
        cursor = readTravelList();

        //Set Language setting
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String language = sharedPreferences.getString("language_preference", "en");

        //If Language preference is changed from SettingActivity
        if(!language.equalsIgnoreCase(""))
        {
            //Language: English
            if(language.equals("en"))
            {
                setTitle("Title");
                title_text.setText("Enter Title");
                epic_plan.setHint("Your Epic Plan Name");
                cancel_button.setText("CANCEL");
                ok_button.setText("OK");
            }

            //Language: Korean
            else
            {
                setTitle("제목");
                title_text.setText("제목을 입력하세요");
                epic_plan.setHint("최고의 여행 이름");
                cancel_button.setText("취소");
                ok_button.setText("확인");
            }
        }

        //Update and keep the Title text when Orientation is changed
        if(savedInstanceState != null)
        {
            //Get current name of text
            String temp_saved_title_text = savedInstanceState.getString("saved_title_text");
            title_text.setText(temp_saved_title_text);
        }
    }

    //Cursor readTravelList method
    private Cursor readTravelList()
    {
        return db.query(dbHelper.DBTRAVEL, columns, null, new String[] {}, dbHelper.TRAVEL, null, null);
    }

    //When click "CANCEL" or "취소" button
    public void gotoCancel(View v)
    {
        //Finish this Activity and return to MainActivity
        finish();
    }

    //When click "OK" or "확인" button
    public void gotoOk(View v)
    {
        //Set cursor to Database Query and read TravelList
        cursor = readTravelList();

        //Set Language setting
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String language = sharedPreferences.getString("language_preference", "en");

        //Check whether Travel Name is Already Used
        while(cursor.moveToNext())
        {
            check_txt = cursor.getString(1);

            //If typed plan name is used
            if(check_txt.equals(epic_plan.getText().toString()))
            {
                check_status = true;
            }

            //else typed plan name is NOT used
            else
            {
                check_status = false;
            }
        }

        if(check_status == false)
        {
            //If Title is NOT SET
            if(title_text.getText().toString().equals("Enter Title") || (title_text.getText().toString().equals("제목을 입력하세요")))
            {
                //If Title is NOT EMPTY
                if(epic_plan.getText().toString().trim().length() != 0)
                {
                    //Start PlanCalendarActivity
                    //and send EditText to the Activity
                    Intent calendar_intent = new Intent(this, PlanCalendarActivity.class);
                    calendar_intent.putExtra(PlanCalendarActivity.DATA_PASSING_TITLE, epic_plan.getText().toString());

                    //If Language preference is changed from SettingActivity
                    if(!language.equalsIgnoreCase(""))
                    {
                        //Language: English
                        if(language.equals("en"))
                        {
                            Toast.makeText(getApplicationContext(), "Title: " + epic_plan.getText().toString(), Toast.LENGTH_SHORT).show();

                            //Set "Enter Title" text to typed title name for user notification
                            //when previous button pressed from the calendar
                            title_text.setText("Title Saved: " + epic_plan.getText().toString());

                            epic_plan.setText("");

                            startActivity(calendar_intent);
                        }

                        //Language: Korean
                        else
                        {
                            Toast.makeText(getApplicationContext(), "제목: " + epic_plan.getText().toString(), Toast.LENGTH_SHORT).show();

                            //Set "Enter Title" text to typed title name for user notification
                            //when previous button pressed from the calendar
                            title_text.setText("저장된 제목: " + epic_plan.getText().toString());

                            epic_plan.setText("");

                            startActivity(calendar_intent);
                        }
                    }
                }

                //else Title is EMPTY
                else
                {
                    //If Language preference is changed from SettingActivity
                    if(!language.equalsIgnoreCase(""))
                    {
                        //Language: English
                        if(language.equals("en"))
                        {
                            Toast.makeText(getApplicationContext(), "Title is EMPTY", Toast.LENGTH_SHORT).show();
                        }

                        //Language: Korean
                        else
                        {
                            Toast.makeText(getApplicationContext(), "제목이 없습니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            //else Title is already SET
            else
            {
                //If Title is NOT EMPTY = want to modify Title
                if(epic_plan.getText().toString().trim().length() != 0)
                {
                    //Start PlanCalendarActivity
                    //and send EditText to the Activity
                    Intent calendar_intent = new Intent(this, PlanCalendarActivity.class);
                    calendar_intent.putExtra(PlanCalendarActivity.DATA_PASSING_TITLE, epic_plan.getText().toString());

                    //If Language preference is changed from SettingActivity
                    if(!language.equalsIgnoreCase(""))
                    {
                        //Language: English
                        if(language.equals("en"))
                        {
                            Toast.makeText(getApplicationContext(), "Title: " + epic_plan.getText().toString(), Toast.LENGTH_SHORT).show();

                            //Set "Enter Title" text to typed title name for user notification
                            //when previous button pressed from the calendar
                            title_text.setText("Title Saved: " + epic_plan.getText().toString());

                            epic_plan.setText("");

                            startActivity(calendar_intent);
                        }

                        //Language: Korean
                        else
                        {
                            Toast.makeText(getApplicationContext(), "제목: " + epic_plan.getText().toString(), Toast.LENGTH_SHORT).show();

                            //Set "Enter Title" text to typed title name for user notification
                            //when previous button pressed from the calendar
                            title_text.setText("저장된 제목: " + epic_plan.getText().toString());

                            epic_plan.setText("");

                            startActivity(calendar_intent);
                        }
                    }
                }

                //else Title is EMPTY = keep the previous Title
                else
                {
                    Intent calendar_intent = new Intent(this, PlanCalendarActivity.class);

                    //If Language preference is changed from SettingActivity
                    if(!language.equalsIgnoreCase(""))
                    {
                        //Language: English
                        if(language.equals("en"))
                        {
                            String saved_title = title_text.getText().toString().replace("Title Saved: ", "");
                            calendar_intent.putExtra(PlanCalendarActivity.DATA_PASSING_TITLE, saved_title);
                            startActivity(calendar_intent);
                        }

                        //Language: Korean
                        else
                        {
                            String saved_title = title_text.getText().toString().replace("저장된 제목: ", "");
                            calendar_intent.putExtra(PlanCalendarActivity.DATA_PASSING_TITLE, saved_title);
                            startActivity(calendar_intent);
                        }
                    }
                }
            }
        }

        //else check_status is TRUE
        else
        {
            //If Language preference is changed from SettingActivity
            if(!language.equalsIgnoreCase(""))
            {
                //Language: English
                if(language.equals("en"))
                {
                    Toast.makeText(getApplicationContext(), "** PLAN NAME ALREADY EXIST **", Toast.LENGTH_SHORT).show();
                }

                //Language: Korean
                else
                {
                    Toast.makeText(getApplicationContext(), "** 같은 제목이 존재합니다 **", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //When orientation changes, keep the current Title text
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putString("saved_title_text", title_text.getText().toString());
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
        //Finish this Activity and return to MainActivity
        finish();
    }
}