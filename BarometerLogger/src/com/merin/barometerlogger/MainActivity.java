package com.merin.barometerlogger;

import java.util.List;

import org.xml.sax.Parser;

import android.app.Activity;
import android.app.Service;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener{

	private SensorManager mSensorManager;
	private Sensor mSensor;
	private int mdelay;
	private TextView tv2;
	private final int DELAY_VALUE = 50000;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tv2 = (TextView) findViewById(R.id.textview2);
		tv2.setText("Loading");
		mSensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
		//List<Sensor> deviceSensor = mSensorManager.getSensorList(Sensor.TYPE_ALL);
		if(mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null){
			mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
		}
		mdelay = mSensor.getMinDelay();
		mSensorManager.registerListener(this, mSensor, DELAY_VALUE);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float p =  event.values[0];
		
		long dt = event.timestamp;

		CharSequence mText;
		mText = " " + p + "   " + dt;
		
		tv2.setText(mText);
	}

	@Override
	protected void onPause() {
		mSensorManager.unregisterListener(this);
		super.onPause();

	}

	@Override
	protected void onResume() {
		mSensorManager.registerListener(this, mSensor, DELAY_VALUE);
		super.onResume();
		//mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

	}

}
