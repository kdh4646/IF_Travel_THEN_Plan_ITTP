package gmu.edu.cs477.ittp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
    //Timer for Welcome Screen
    private int SLEEP_TIMER = 3;

    //Set variables for Database
    private SQLiteDatabase db = null;
    private DatabaseOpenHelper dbHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set Database
        dbHelper = new DatabaseOpenHelper(this);
        db = dbHelper.getWritableDatabase();

        //Reset the Plan table for making another plan
        db.execSQL("DELETE FROM " + dbHelper.DBPLAN);

        //Set Welcome Screen for Full Screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_welcome);

        //Object LauncherClick for starting welcome screen
        LauncherClick launcherClick = new LauncherClick();
        launcherClick.start();
    }

    //LauncherClick class
    //When Launcher is clicked, sleep for few seconds based on user input,
    //then go to new intent, MainActivity
    private class LauncherClick extends Thread
    {
        public void run()
        {
            try
            {
                sleep(1000 * SLEEP_TIMER);
            }

            catch(InterruptedException e)
            {
                e.printStackTrace();
            }

            Intent main_intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(main_intent);

            WelcomeActivity.this.finish();
        }
    }
}