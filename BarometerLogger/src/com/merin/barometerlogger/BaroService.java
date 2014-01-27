package com.merin.barometerlogger;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;  
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class BaroService extends Service implements SensorEventListener{

	private SensorManager mSensorManager;
	private Sensor mSensor;
	private float p;
	private final int DELAY_VALUE = 1000000;
	private NumberFormat nformat = NumberFormat.getNumberInstance();
	private final static String TAG = "merin-tag";
	private MyDBHelper dbHelper;
	private ContentValues cv;
	private Calendar cal;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	@Override
	public void onCreate() {
		nformat.setMaximumFractionDigits(1);
		nformat.setMinimumFractionDigits(1);
		
		//get an instance of system sensor manager for getting the baromter reading
		mSensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
		
		//create a instance of dbhelper class for our db related operation
		dbHelper = new MyDBHelper(getApplicationContext());
		
		
		//get a writable DB for logging the barometer 
		dbHelper.openWrite();
		
		//Check if sensor is present and then get an instance
		if(mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null){
			mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
			//register our barometer listener for callback when a reading is taken
			mSensorManager.registerListener(this, mSensor, DELAY_VALUE);
		}else{
    		Toast.makeText(this, "SENSOR NOT PRESENT", Toast.LENGTH_LONG).show();
		}

		super.onCreate();
	}



	@Override
	public void onDestroy() {
		// unregister sensor when the service is destroyed
		mSensorManager.unregisterListener(this);
		super.onDestroy();
	}



	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//Toast.makeText(BaroService.this, "onStartCommand : ", Toast.LENGTH_SHORT).show();
		return super.onStartCommand(intent, flags, startId);
	}



	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		//when a new reading is taking , log it outside this method with asynchronous task
	    new SensorEventLoggerTask().execute(event);
		mSensorManager.unregisterListener(this);
		stopSelf();
	
	}
	
	
	private class SensorEventLoggerTask extends AsyncTask<SensorEvent, Void, Void>{

		@Override
		protected Void doInBackground(SensorEvent... events) {
			SensorEvent event = events[0];
		    p =  event.values[0];
		    //get calendar instance for logging the current date while logging
		    cal = Calendar.getInstance();
		    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
		    String fdate = sdf.format(cal.getTime());
		    //content values for storing value and date, then inserting to DB
		    cv = new ContentValues();
		    cv.put(MyDBHelper.COL_MBARS, p);
		    cv.put(MyDBHelper.COL_TSTAMP, fdate);
		    Log.e(TAG, "mbars : " + cv.get(MyDBHelper.COL_MBARS));
		    Log.e(TAG, "date : " + cv.get(MyDBHelper.COL_TSTAMP));
		    
		    //call the insert method for logging
		    dbHelper.insertrow(MyDBHelper.TABLE_PRS, cv);
			return null;
		}
		
	}

}
