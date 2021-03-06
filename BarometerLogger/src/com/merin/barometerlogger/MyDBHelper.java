package com.merin.barometerlogger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import java.util.ArrayList;

public class MyDBHelper {
private static final String DB_NAME = "logger.db";
private static final int    DB_VERSION = 1;
public static final String TABLE_PRS = "PRESSURE";
public static final String COL_MBARS = "MBARS";
public static final String COL_TSTAMP = "TSTAMP";
private float mbarArray[] = new float[1];
//create table query for Pressure table
private final static String CREATE_PRESSURE_TABLE = "create table if not exists "+TABLE_PRS+" ("+COL_MBARS+" REAL PRIMARY KEY, "+COL_TSTAMP+" TEXT NOT NULL)";
DatabaseHelper mSQLHelper;
private final Context mCtx;
private SQLiteDatabase mDB;


    public MyDBHelper(Context mctx) {
    	this.mCtx = mctx;

}


	private static class DatabaseHelper extends SQLiteOpenHelper {
	public DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

	@Override
	public void onCreate(SQLiteDatabase db) {

		try {
			db.execSQL(CREATE_PRESSURE_TABLE);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MyDBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRS);
        onCreate(db);
	}
}

    //Other DB handling helper methods

	//gets a referance to writable DB.
    public MyDBHelper openWrite() throws SQLException{
    	mSQLHelper = new DatabaseHelper(mCtx);
    	mDB = mSQLHelper.getWritableDatabase();
		return this;
   }

    //gets a referance to readable DB.
    public MyDBHelper openRead() throws SQLException{
    	mSQLHelper = new DatabaseHelper(mCtx);
    	mDB = mSQLHelper.getReadableDatabase();
		return this;
   }

    public long insertrow(String table, ContentValues cvs){
    	return mDB.insert(table, null, cvs);
}
    //return the pressure values from DB, no time stamp returned
    public float[] getdata(String table, String key, String date[]){

    	SQLiteQueryBuilder mQueryBuilder = new SQLiteQueryBuilder();
    	mQueryBuilder.setTables(table);
    	if (date != null){
    		mQueryBuilder.appendWhere(MyDBHelper.COL_TSTAMP + " = '" + date[0] + "'");
    	}

    	Cursor mCursor = null;

		try {
			mCursor = mQueryBuilder.query(mDB, null, null, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(mCursor != null){
			mbarArray = new float[mCursor.getCount()];
			mCursor.moveToFirst();
			int i = 0;
			while(!mCursor.isAfterLast()){
			mbarArray[i] = 	mCursor.getFloat(0);
			i++;
			mCursor.moveToNext();
		}
			mCursor.close();
			return mbarArray;
		}
		return mbarArray;
    }

  //return the values from DB, both pressure and timestamp values.
    public ArrayList<Logger> getalldata(String table){

        SQLiteQueryBuilder mQueryBuilder = new SQLiteQueryBuilder();
        mQueryBuilder.setTables(table);

        Cursor mCursor = null;
        Logger mlogger = new Logger();
        ArrayList<Logger> mLoggerList= new ArrayList<Logger>();

        try {
            mCursor = mQueryBuilder.query(mDB, null, null, null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(mCursor != null){
            //mbarArray = new float[mCursor.getCount()];
            mCursor.moveToFirst();

            while(!mCursor.isAfterLast()){
            //mbarArray[i] =  mCursor.getFloat(0);
            //mlogger.setmPressure(mCursor.getFloat(0));
            //mlogger.setmTimeStamp(mCursor.getString(1));
            mLoggerList.add(new Logger(mCursor.getFloat(0), mCursor.getString(1)));

            mCursor.moveToNext();
        }
            mCursor.close();
            return mLoggerList;
        }
        return mLoggerList;
    }

//delete all the rows in the DB
public int resetDb(String table,String field,String args[]) {
	int i = mDB.delete(table, field, args);
	mDB.close();
	return i;

}

}