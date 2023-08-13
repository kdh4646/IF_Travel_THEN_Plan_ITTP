package gmu.edu.cs477.ittp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
    //Set variables for Database
    final static String DBTRAVEL = "travel_db";
    final static String DBPLAN= "plan_db";
    final static String DBHISTORY = "history_db";
    final static String ID = "_id";
    final static String TRAVEL = "travel";
    final static String DATE = "date";
    final static String SCHEDULE = "schedule";
    final static String ADDRESS = "address";
    final static String NOTE = "note";
    final static String STARTDATE = "start_date";
    final static String ENDDATE = "end_date";
    final static String DONE = "schedule_done";
    final private static Integer VERSION = 1;
    final private Context context;

    //Set Table for SAVE DATA and KEEP
    final private static String TRAVEL_TABLE =
            "CREATE TABLE " + DBTRAVEL + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TRAVEL + " TEXT NOT NULL," +
                    DATE + " TEXT NOT NULL," +
                    SCHEDULE + " TEXT NOT NULL," +
                    ADDRESS + " TEXT NOT NULL," +
                    NOTE + " TEXT NOT NULL," +
                    STARTDATE + " TEXT NOT NULL," +
                    ENDDATE + " TEXT NOT NULL," +
                    DONE + " TEXT NOT NULL)";

    //Set Table for TEMP DATA and RESET
    final private static String PLAN_TABLE =
            "CREATE TABLE " + DBPLAN + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TRAVEL + " TEXT NOT NULL," +
                    DATE + " TEXT NOT NULL," +
                    SCHEDULE + " TEXT NOT NULL," +
                    ADDRESS + " TEXT NOT NULL," +
                    NOTE + " TEXT NOT NULL," +
                    STARTDATE + " TEXT NOT NULL," +
                    ENDDATE + " TEXT NOT NULL," +
                    DONE + " TEXT NOT NULL)";

    //Set Table for HISTORY DATA and KEEP
    final private static String HISTORY_TABLE =
            "CREATE TABLE " + DBHISTORY + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TRAVEL + " TEXT NOT NULL," +
                    DATE + " TEXT NOT NULL," +
                    SCHEDULE + " TEXT NOT NULL," +
                    ADDRESS + " TEXT NOT NULL," +
                    NOTE + " TEXT NOT NULL," +
                    STARTDATE + " TEXT NOT NULL," +
                    ENDDATE + " TEXT NOT NULL," +
                    DONE + " TEXT NOT NULL)";

    //Set Constructor
    public DatabaseOpenHelper(Context context)
    {
        super(context, "travel_plan_db", null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        //Create the Database table
        db.execSQL(TRAVEL_TABLE);
        db.execSQL(PLAN_TABLE);
        db.execSQL(HISTORY_TABLE);

        //Initialize Database
        //if values needed (NO need)
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //TODO Auto-generated method stub
    }

    void deleteDatabase() { context.deleteDatabase(DBTRAVEL); context.deleteDatabase(DBPLAN); }
}