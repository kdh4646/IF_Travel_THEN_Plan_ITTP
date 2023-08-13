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

public class TravelActivity extends AppCompatActivity {
    //Value for "requestCode"
    public final int ACTIVITY_RESULT = 1;

    //Set variables
    TextView travel_list_txt;
    TextView default_txt;
    ListView travel_list;

    //Set extra variables for control
    LinearLayout travel_layout;

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
        setContentView(R.layout.activity_travel);

        //Initialize Variables
        travel_list_txt = (TextView) findViewById(R.id.travel_list_text);
        default_txt = (TextView) findViewById(R.id.default_travellist_text);
        travel_list = (ListView) findViewById(R.id.travel_list);

        //Set Database
        dbHelper = new DatabaseOpenHelper(this);
        db = dbHelper.getWritableDatabase();

        //Set cursor to Database Query and read TravelList
        cursor = readTravelList();

        //Set SimpleCursorAdapter connects with ListView
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.travellistformat, cursor,
                need_column, new int[]{R.id.travel_name_part, R.id.start_date_part, R.id.end_date_part},0);
        travel_list.setAdapter(cursorAdapter);

        //Set Language setting
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String language = sharedPreferences.getString("language_preference", "en");

        //If Language preference is changed from SettingActivity
        if(!language.equalsIgnoreCase(""))
        {
            //Language: English
            if(language.equals("en"))
            {
                travel_list_txt.setText("\u003c Travel List \u003e");
            }

            //Language: Korean
            else
            {
                travel_list_txt.setText("\u003c 여행 목록 \u003e");
            }
        }

        //If List is Empty then set Default Text and List is GONE
        if(cursor.getCount() == 0)
        {
            travel_list.setVisibility(View.GONE);

            //If Language preference is changed from SettingActivity
            if(!language.equalsIgnoreCase(""))
            {
                //Language: English
                if(language.equals("en"))
                {
                    default_txt.setText("No Travel !");
                }

                //Language: Korean
                else
                {
                    default_txt.setText("여행 목록이 없습니다");
                }
            }
        }

        //else List is NOT Empty
        else
        {
            default_txt.setVisibility(View.GONE);

            //Set onItemClick for ListView
            travel_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    cursor.moveToPosition(position);

                    //New Intent for Loading Activity
                    Intent loading_intent = new Intent(getApplicationContext(), TravelLoadingActivity.class);

                    //Column 1: Travel Name
                    //Column 6: Start Date
                    //Column 7: End Date
                    loading_intent.putExtra(TravelLoadingActivity.DATA_LOADING_TITLE, cursor.getString(1));
                    loading_intent.putExtra(TravelLoadingActivity.DATA_LOADING_START_DATE, cursor.getString(6));
                    loading_intent.putExtra(TravelLoadingActivity.DATA_LOADING_END_DATE, cursor.getString(7));

                    startActivityForResult(loading_intent, ACTIVITY_RESULT);
                }
            });

            //Set onItemLongClick for ListView
            travel_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    //Get cursor to clicked item position
                    cursor.moveToPosition(position);

                    //Set Language setting
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String language = sharedPreferences.getString("language_preference", "en");

                    //Set Alert Dialog with "Delete" and "Cancel" buttons or "삭제" and "취소" buttons
                    AlertDialog alertDialog = new AlertDialog.Builder(TravelActivity.this).create();

                    //If Language preference is changed from SettingActivity
                    if(!language.equalsIgnoreCase(""))
                    {
                        //Language: English
                        if(language.equals("en"))
                        {
                            alertDialog.setTitle("Are you sure you want to delete your Travel ?");
                            alertDialog.setMessage("> Clicked Travel: " + cursor.getString(1));

                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    Toast.makeText(getApplicationContext(), "Travel Deleted: " + cursor.getString(1), Toast.LENGTH_SHORT).show();

                                    //Column 1: Travel Name
                                    //Column 6: Start Date
                                    //Column 7: End Date
                                    //Delete Long Clicked Travel from the List and the Database
                                    db.delete(dbHelper.DBTRAVEL, dbHelper.TRAVEL + "=?", new String[] {cursor.getString(1)});

                                    //Set cursor to Database Query and read TravelList
                                    cursor = readTravelList();

                                    //If List is Empty then set Default Text and List is GONE
                                    if(cursor.getCount() == 0)
                                    {
                                        travel_list.setVisibility(View.GONE);
                                        default_txt.setVisibility(View.VISIBLE);
                                        default_txt.setText("No Travel !");
                                    }

                                    //Set SimpleCursorAdapter connects with ListView
                                    cursorAdapter = new SimpleCursorAdapter(TravelActivity.this, R.layout.travellistformat, cursor,
                                            need_column, new int[]{R.id.travel_name_part, R.id.start_date_part, R.id.end_date_part},0);
                                    travel_list.setAdapter(cursorAdapter);
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
                            alertDialog.setTitle("여행을 목록에서 삭제하시겠습니까 ?");
                            alertDialog.setMessage("> 선택된 여행: " + cursor.getString(1));

                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "삭제", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    Toast.makeText(getApplicationContext(), "삭제된 여행: " + cursor.getString(1), Toast.LENGTH_SHORT).show();

                                    //Column 1: Travel Name
                                    //Column 6: Start Date
                                    //Column 7: End Date
                                    //Delete Long Clicked Travel from the List and the Database
                                    db.delete(dbHelper.DBTRAVEL, dbHelper.TRAVEL + "=?", new String[] {cursor.getString(1)});

                                    //Set cursor to Database Query and read TravelList
                                    cursor = readTravelList();

                                    //If List is Empty then set Default Text and List is GONE
                                    if(cursor.getCount() == 0)
                                    {
                                        travel_list.setVisibility(View.GONE);
                                        default_txt.setVisibility(View.VISIBLE);
                                        default_txt.setText("여행 목록이 없습니다");
                                    }

                                    //Set SimpleCursorAdapter connects with ListView
                                    cursorAdapter = new SimpleCursorAdapter(TravelActivity.this, R.layout.travellistformat, cursor,
                                            need_column, new int[]{R.id.travel_name_part, R.id.start_date_part, R.id.end_date_part},0);
                                    travel_list.setAdapter(cursorAdapter);
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
                travel_layout = (LinearLayout) findViewById(R.id.activity_travel);
                travel_layout.setBackgroundResource(R.drawable.main_activity_background_default);
            }

            //Background: RED
            else if(background.equals("color_red"))
            {
                travel_layout = (LinearLayout) findViewById(R.id.activity_travel);
                travel_layout.setBackgroundResource(R.drawable.main_activity_background_red);
            }

            //Background: GREEN
            else if(background.equals("color_green"))
            {
                travel_layout = (LinearLayout) findViewById(R.id.activity_travel);
                travel_layout.setBackgroundResource(R.drawable.main_activity_background_green);
            }

            //Background: BLUE
            else
            {
                travel_layout = (LinearLayout) findViewById(R.id.activity_travel);
                travel_layout.setBackgroundResource(R.drawable.main_activity_background_blue);
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
                travel_layout = (LinearLayout) findViewById(R.id.activity_travel);
                travel_layout.setBackgroundResource(R.drawable.main_activity_background_default);
            }

            //Background: RED
            else if(background.equals("color_red"))
            {
                travel_layout = (LinearLayout) findViewById(R.id.activity_travel);
                travel_layout.setBackgroundResource(R.drawable.main_activity_background_red);
            }

            //Background: GREEN
            else if(background.equals("color_green"))
            {
                travel_layout = (LinearLayout) findViewById(R.id.activity_travel);
                travel_layout.setBackgroundResource(R.drawable.main_activity_background_green);
            }

            //Background: BLUE
            else
            {
                travel_layout = (LinearLayout) findViewById(R.id.activity_travel);
                travel_layout.setBackgroundResource(R.drawable.main_activity_background_blue);
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

    //Cursor readTravelList method
    private Cursor readTravelList()
    {
        return db.query(dbHelper.DBTRAVEL, columns, null, new String[] {}, dbHelper.TRAVEL, null, null);
    }

    //Getting Result from the Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ACTIVITY_RESULT)
        {
            //Update Travel List After TravelActivity
            //Set cursor to Database Query and read TravelList
            cursor = readTravelList();

            //If List is Empty then set Default Text and List is GONE
            if(cursor.getCount() == 0)
            {
                default_txt.setVisibility(View.VISIBLE);
                travel_list.setVisibility(View.GONE);

                //Set Language setting
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                String language = sharedPreferences.getString("language_preference", "en");

                //If Language preference is changed from SettingActivity
                if(!language.equalsIgnoreCase(""))
                {
                    //Language: English
                    if(language.equals("en"))
                    {
                        default_txt.setText("No Travel !");
                    }

                    //Language: Korean
                    else
                    {
                        default_txt.setText("여행 목록이 없습니다");
                    }
                }
            }

            //else List is NOT Empty
            else
            {
                default_txt.setVisibility(View.GONE);

                //Set SimpleCursorAdapter connects with ListView
                cursorAdapter = new SimpleCursorAdapter(TravelActivity.this, R.layout.travellistformat, cursor,
                        need_column, new int[]{R.id.travel_name_part, R.id.start_date_part, R.id.end_date_part},0);
                travel_list.setAdapter(cursorAdapter);
            }
        }
    }
}