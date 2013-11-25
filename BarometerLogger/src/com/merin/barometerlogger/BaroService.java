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
		
		mSensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
		
		dbHelper = new MyDBHelper(getApplicationContext());
		
		dbHelper.openWrite();
		
		//Check if sensor is present and then get an instance
		if(mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null){
			mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
		}else{
    		Toast.makeText(this, "SENSOR NOT PRESENT", Toast.LENGTH_LONG).show();
		}
		mSensorManager.registerListener(this, mSensor, DELAY_VALUE);
		super.onCreate();
	}



	@Override
	public void onDestroy() {
		// unregister sensor when no longer needed
		mSensorManager.unregisterListener(this);
		super.onDestroy();
	}



	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(BaroService.this, "onStartCommand : ", Toast.LENGTH_SHORT).show();
		return super.onStartCommand(intent, flags, startId);
	}



	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
	    new SensorEventLoggerTask().execute(event);
		mSensorManager.unregisterListener(this);
		stopSelf();
		

		//CharSequence mText;
		//mText = nformat.format(p);
		
	}
	
	
	private class SensorEventLoggerTask extends AsyncTask<SensorEvent, Void, Void>{

		@Override
		protected Void doInBackground(SensorEvent... events) {
			SensorEvent event = events[0];
		    p =  event.values[0];
		    Log.e(TAG, "value : " + p);
		    
		    cal = Calendar.getInstance();
		    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
		    String fdate = sdf.format(cal.getTime());
		    cv = new ContentValues();
		    cv.put(MyDBHelper.COL_MBARS, p);
		    cv.put(MyDBHelper.COL_TSTAMP, fdate);
		    Log.e(TAG, "mbars : " + cv.get(MyDBHelper.COL_MBARS));
		    Log.e(TAG, "date : " + cv.get(MyDBHelper.COL_TSTAMP));
		    
		    
		    dbHelper.insertrow(MyDBHelper.TABLE_PRS, cv);
			//Toast.makeText(BaroService.this, " " +  p, Toast.LENGTH_SHORT).show();
			return null;
		}
		
	}

}
