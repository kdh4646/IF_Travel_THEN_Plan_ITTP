package gmu.edu.cs477.ittp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class HistoryActivity extends AppCompatActivity {
    //Set variables
    TextView history_text;
    TextView default_text;
    ListView history_travel_list;

    //Set extra variables for control
    LinearLayout history_layout;

    //Set variables for Database
    private SQLiteDatabase db = null;
    private DatabaseOpenHelper dbHelper = null;
    final static String[] columns = {DatabaseOpenHelper.ID, DatabaseOpenHelper.TRAVEL,
            DatabaseOpenHelper.DATE, DatabaseOpenHelper.SCHEDULE, DatabaseOpenHelper.ADDRESS, DatabaseOpenHelper.NOTE, DatabaseOpenHelper.STARTDATE, DatabaseOpenHelper.ENDDATE, DatabaseOpenHelper.DONE};
    final static String[] need_column = {DatabaseOpenHelper.TRAVEL, DatabaseOpenHelper.STARTDATE, DatabaseOpenHelper.ENDDATE};
    SimpleCursorAdapter cursorAdapter;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        //Initialize Variables
        history_text = (TextView) findViewById(R.id.history_text);
        default_text = (TextView) findViewById(R.id.default_history_text);
        history_travel_list = (ListView) findViewById(R.id.history_travel_list);

        //Set Database
        dbHelper = new DatabaseOpenHelper(this);
        db = dbHelper.getWritableDatabase();

        //Set cursor to Database Query and read History Travel List
        cursor = readHistory();

        //Set Language setting
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String language = sharedPreferences.getString("language_preference", "en");

        //If Language preference is changed from SettingActivity
        if(!language.equalsIgnoreCase(""))
        {
            //Language: English
            if(language.equals("en"))
            {
                history_text.setText("\u003c History \u003e");
                default_text.setText("No History !");
            }

            //Language: Korean
            else
            {
                history_text.setText("\u003c 기록 \u003e");
                default_text.setText("기록이 없습니다");
            }
        }

        //Set SimpleCursorAdapter connects with ListView
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.travellistformat, cursor,
                need_column, new int[]{R.id.travel_name_part, R.id.start_date_part, R.id.end_date_part},0);
        history_travel_list.setAdapter(cursorAdapter);

        //If List is Empty then set Default Text and List is GONE
        if(cursor.getCount() == 0)
        {
            history_travel_list.setVisibility(View.GONE);
        }

        //else List is NOT Empty
        else
        {
            default_text.setVisibility(View.GONE);

            //Set onItemClick for ListView
            history_travel_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    cursor.moveToPosition(position);

                    //New Intent for View Activity
                    Intent view_intent = new Intent(getApplicationContext(), HistoryViewActivity.class);

                    //Column 1: Travel Name
                    //Column 6: Start Date
                    //Column 7: End Date
                    view_intent.putExtra(HistoryViewActivity.DATA_VIEW_TITLE, cursor.getString(1));
                    view_intent.putExtra(HistoryViewActivity.DATA_VIEW_START_DATE, cursor.getString(6));
                    view_intent.putExtra(HistoryViewActivity.DATA_VIEW_END_DATE, cursor.getString(7));

                    startActivity(view_intent);
                }
            });

            //Set onItemLongClick for ListView
            history_travel_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    //Get cursor to clicked item position
                    cursor.moveToPosition(position);

                    //Set Alert Dialog with "Delete" and "Cancel" buttons
                    AlertDialog alertDialog = new AlertDialog.Builder(HistoryActivity.this).create();

                    //Set Language setting
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String language = sharedPreferences.getString("language_preference", "en");

                    //If Language preference is changed from SettingActivity
                    if(!language.equalsIgnoreCase(""))
                    {
                        //Language: English
                        if(language.equals("en"))
                        {
                            alertDialog.setTitle("Are you sure you want to delete your History ?");
                            alertDialog.setMessage("> Clicked Travel History: " + cursor.getString(1));

                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    Toast.makeText(getApplicationContext(), "Travel History Deleted: " + cursor.getString(1), Toast.LENGTH_SHORT).show();

                                    //Column 1: Travel Name
                                    //Column 6: Start Date
                                    //Column 7: End Date
                                    //Delete Long Clicked Travel from the List and the Database
                                    db.delete(dbHelper.DBHISTORY, dbHelper.TRAVEL + "=?", new String[] {cursor.getString(1)});

                                    //Set cursor to Database Query and read TravelList
                                    cursor = readHistory();

                                    //If List is Empty then set Default Text and List is GONE
                                    if(cursor.getCount() == 0)
                                    {
                                        default_text.setVisibility(View.VISIBLE);
                                        history_travel_list.setVisibility(View.GONE);
                                    }

                                    //else List is NOT Empty
                                    else
                                    {
                                        default_text.setVisibility(View.GONE);

                                        //Set SimpleCursorAdapter connects with ListView
                                        cursorAdapter = new SimpleCursorAdapter(HistoryActivity.this, R.layout.travellistformat, cursor,
                                                need_column, new int[]{R.id.travel_name_part, R.id.start_date_part, R.id.end_date_part},0);
                                        history_travel_list.setAdapter(cursorAdapter);
                                    }
                                }
                            });

                            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                        }

                        //Language: Korean
                        else
                        {
                            alertDialog.setTitle("여행기록을 목록에서 삭제하시겠습니까 ?");
                            alertDialog.setMessage("> 선택된 여행 기록: " + cursor.getString(1));

                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "삭제", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    Toast.makeText(getApplicationContext(), "삭제된 기록: " + cursor.getString(1), Toast.LENGTH_SHORT).show();

                                    //Column 1: Travel Name
                                    //Column 6: Start Date
                                    //Column 7: End Date
                                    //Delete Long Clicked Travel from the List and the Database
                                    db.delete(dbHelper.DBHISTORY, dbHelper.TRAVEL + "=?", new String[] {cursor.getString(1)});

                                    //Set cursor to Database Query and read TravelList
                                    cursor = readHistory();

                                    //If List is Empty then set Default Text and List is GONE
                                    if(cursor.getCount() == 0)
                                    {
                                        default_text.setVisibility(View.VISIBLE);
                                        history_travel_list.setVisibility(View.GONE);
                                    }

                                    //else List is NOT Empty
                                    else
                                    {
                                        default_text.setVisibility(View.GONE);

                                        //Set SimpleCursorAdapter connects with ListView
                                        cursorAdapter = new SimpleCursorAdapter(HistoryActivity.this, R.layout.travellistformat, cursor,
                                                need_column, new int[]{R.id.travel_name_part, R.id.start_date_part, R.id.end_date_part},0);
                                        history_travel_list.setAdapter(cursorAdapter);
                                    }
                                }
                            });

                            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                        }
                    }

                    alertDialog.show();

                    //Repositioning Alert Dialog Buttons
                    Button btnDelete = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    Button btnCancel = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnDelete.getLayoutParams();
                    layoutParams.weight = 10;
                    btnDelete.setLayoutParams(layoutParams);
                    btnCancel.setLayoutParams(layoutParams);

                    return true;
                }
            });
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
                history_layout = (LinearLayout) findViewById(R.id.activity_history);
                history_layout.setBackgroundResource(R.drawable.main_activity_background_default);
            }

            //Background: RED
            else if(background.equals("color_red"))
            {
                history_layout = (LinearLayout) findViewById(R.id.activity_history);
                history_layout.setBackgroundResource(R.drawable.main_activity_background_red);
            }

            //Background: GREEN
            else if(background.equals("color_green"))
            {
                history_layout = (LinearLayout) findViewById(R.id.activity_history);
                history_layout.setBackgroundResource(R.drawable.main_activity_background_green);
            }

            //Background: BLUE
            else
            {
                history_layout = (LinearLayout) findViewById(R.id.activity_history);
                history_layout.setBackgroundResource(R.drawable.main_activity_background_blue);
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
                history_layout = (LinearLayout) findViewById(R.id.activity_history);
                history_layout.setBackgroundResource(R.drawable.main_activity_background_default);
            }

            //Background: RED
            else if(background.equals("color_red"))
            {
                history_layout = (LinearLayout) findViewById(R.id.activity_history);
                history_layout.setBackgroundResource(R.drawable.main_activity_background_red);
            }

            //Background: GREEN
            else if(background.equals("color_green"))
            {
                history_layout = (LinearLayout) findViewById(R.id.activity_history);
                history_layout.setBackgroundResource(R.drawable.main_activity_background_green);
            }

            //Background: BLUE
            else
            {
                history_layout = (LinearLayout) findViewById(R.id.activity_history);
                history_layout.setBackgroundResource(R.drawable.main_activity_background_blue);
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

    //Cursor readHistory method
    private Cursor readHistory()
    {
        return db.query(dbHelper.DBHISTORY, columns, null, new String[] {}, dbHelper.TRAVEL, null, null);
    }
}