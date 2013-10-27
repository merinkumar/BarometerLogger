package com.merin.barometerlogger;

import java.text.NumberFormat;
import java.util.List;

import org.xml.sax.Parser;

import android.app.Activity;
import android.app.Service;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener{

	private SensorManager mSensorManager;
	private Sensor mSensor;
	private int mdelay;
	private TextView tv2;
	private TextView mMaxView;
	private TextView mMinView;
	
	private NumberFormat nformat = NumberFormat.getNumberInstance();
	private final int DELAY_VALUE = 50000;
	private float mMax = 0;
	private float mMin = 0;
	private Boolean mFirstRun = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		nformat.setMaximumFractionDigits(1);
		nformat.setMinimumFractionDigits(1);
		
		tv2 = (TextView) findViewById(R.id.textview2);
		tv2.setText("Loading");
		
		mMaxView = (TextView) findViewById(R.id.textMax);
		
		mMinView = (TextView) findViewById(R.id.textMin);
		
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
		
		//long dt = event.timestamp;
		if (mFirstRun){
			mFirstRun = false;
			mMax = p;
			mMin = p;
			setView();
		}
		checkMinMax(p);
		
		CharSequence mText;
		mText = nformat.format(p) + "   ";
		
		tv2.setText(mText);
	}

	private void checkMinMax(float p) {
		if(p > mMax){
			mMax = p;
			setView();
		}else if(p < mMin){
			mMin = p;
			setView();
		}
		
	}
	
	private void setView(){
		mMaxView.setText(nformat.format(mMax));
		mMinView.setText(nformat.format(mMin));
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
