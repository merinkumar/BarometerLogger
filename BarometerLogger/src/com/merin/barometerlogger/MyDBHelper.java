package com.merin.barometerlogger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

public class MyDBHelper {
private static final String DB_NAME = "logger.db";
private static int    DB_VERSION = 1;
public static String TABLE_PRS = "PRESSURE";
public static String COL_MBARS = "MBARS";
public static String COL_TSTAMP = "TSTAMP";
private float mbarArray[] = new float[1];
//create table query for Pressure table
private final static String CREATE_PRESSURE_TABLE = "create table if not exists "+TABLE_PRS+" ("+COL_MBARS+" REAL PRIMARY KEY, "+COL_TSTAMP+" TEXT NOT NULL)";
private final static String TAG = "merin-tag";
DatabaseHelper sqlHelper;
private final Context mCtx;
private SQLiteDatabase db;		
	

    public MyDBHelper(Context mctx) {
    	this.mCtx = mctx;
	
}

    
	private static class DatabaseHelper extends SQLiteOpenHelper {
	public DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
			// TODO Auto-generated constructor stub
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
	
    public MyDBHelper openWrite() throws SQLException{
    	sqlHelper = new DatabaseHelper(mCtx);
    	db = sqlHelper.getWritableDatabase();
		return this;
   }
      
    public MyDBHelper openRead() throws SQLException{
    	sqlHelper = new DatabaseHelper(mCtx);
    	db = sqlHelper.getReadableDatabase();
		return this;
   }
       
    public long insertrow(String table, ContentValues cvs){
    	return db.insert(table, null, cvs);
}
    //return the DB values based on the timestamp
    public float[] getdata(String table, String key, String date[]){

    	SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
    	queryBuilder.setTables(table);
    	if (date != null){
    		queryBuilder.appendWhere(MyDBHelper.COL_TSTAMP + " = '" + date[0] + "'");
    	}

    	Cursor c = null;

		try {
			c = queryBuilder.query(db, null, null, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(c != null){
			mbarArray = new float[c.getCount()];
			c.moveToFirst();
			int i = 0;
			while(!c.isAfterLast()){
			mbarArray[i] = 	c.getFloat(0);
			i++;	
			c.moveToNext();
		}
			c.close();
			return mbarArray;
		}
		return mbarArray;
    }



public int resetDb(String table,String field,String args[]) {
	int i = db.delete(table, field, args);
	db.close();
	return i;
	
}



}