package com.merin.barometerlogger;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;
import android.widget.Toast;

public class MyDBHelper {
private static final String DB_NAME = "logger.db";
private static int    DB_VERSION = 1;
public static String TABLE_PRS = "PRESSURE";
public static String COL_MBARS = "MBARS";
public static String COL_TSTAMP = "TSTAMP";

private final static String CREATE_PRESSURE_TABLE = "create table if not exists "+TABLE_PRS+" ("+COL_MBARS+" CHAR PRIMARY KEY, "+COL_TSTAMP+" TEXT NOT NULL)";
	
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, e.toString());  
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
    

    
    //ADD THE OTHER DB HANDLER METHODS BELOW
    
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
    
    public Boolean getdata(String table, String key, String pass){
    	
    	ContentValues cv = new ContentValues();
    	SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
    	queryBuilder.setTables(table);
    	queryBuilder.appendWhere(MyDBHelper.COL_MBARS + " = '" + key + "'");
    	Cursor c = null;
    	//String sql = "select * from " + table + " where MyDBHelper.COL_USERID = " + key;
    	//String sql = String.format("select * from USERCRED where USERID = " + " '" + key + "' ");
    	//Cursor c = null;
		try {
			c = queryBuilder.query(db, null, null, null, null, null, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
/*		if(c != null){
			c.moveToFirst();
			while(!c.isAfterLast()){
			
			System.out.println("USERID : " + c.getString(0));
			System.out.println("PASS : " + c.getString(1));
			System.out.println("EMAIL : " + c.getString(2));
			c.moveToNext();
		}
			c.close();
		}*/
    	//Toast.makeText(mCtx, c.getString(0), Toast.LENGTH_LONG).show();
    	
		if(!(!(c.moveToFirst()) || c.getCount() ==0)){
		String a = key;
		c.moveToFirst();
		String b = c.getString(0); 
		String p = pass;
		String p1 = c.getString(1);
    	if(a.equals(b) && p.equals(p1)){
    		return true;
    	}else{
    		Toast.makeText(mCtx, "user ID/pass incorrect", Toast.LENGTH_LONG).show();
    		return false;
    	}
		}else{
			System.out.println("merin-cursor error");
    		return false;
		}

    	

    	
    }
    
    
}
