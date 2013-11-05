package com.merin.barometerlogger;

import java.text.NumberFormat;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.TextView;
import android.widget.Toast;

public class BaroService extends Service implements SensorEventListener{

	private SensorManager mSensorManager;
	private Sensor mSensor;
	private float p;
	private final int DELAY_VALUE = 1000000;
	private NumberFormat nformat = NumberFormat.getNumberInstance();
	
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
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}



	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
	    new SensorEventLoggerTask().execute(event);
		stopSelf();
		
	    p =  event.values[0];
		
		CharSequence mText;
		mText = nformat.format(p);
		
	}
	
	
	private class SensorEventLoggerTask extends AsyncTask<SensorEvent, Void, Void>{

		@Override
		protected Void doInBackground(SensorEvent... events) {
			SensorEvent event = events[0];
			return null;
		}
		
	}

}
