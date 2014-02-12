package com.merin.barometerlogger;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.util.PlotStatistics;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;

import java.text.NumberFormat;
import java.util.Calendar;

public class MainActivity extends Activity implements SensorEventListener{

	private SensorManager mSensorManager;
	private Sensor mSensor;
	private TextView mTextView2;
	private TextView mMaxView;
	private TextView mMinView;
	private TextView mSensorText;
	private String[] mDate_key;
	private float mPressure;
	private int mIndex = 0;
	private Intent mBaroIntent;
	private Intent mSettingIntent;
	private PendingIntent mSchedulerIntent;
	private Button mStartServiceButton;
	private Button mStartButton;
	private Button mStopButton;
	private Spinner mDateSpinner;
	private AlarmManager mScheduler;
	private Calendar mCalendar;
	private MyDBHelper mDBHelper;
	private float[] mInputCvalue = new float[30];;
    private NumberFormat mNumformat = NumberFormat.getNumberInstance();
    private final int DELAY_VALUE = 1000000;
    private float mMax = 0;
    private float mMin = 0;
    private Boolean mFirstRun = true;
    private DatePickerDialog.OnDateSetListener mDateLis;
	// objects used for androidplot implementation
	private static final Number MINUS_Y_AXIS = 960;
	private static final Number PLUS_Y_AXIS = 1024;
	private static final Number MINUS_X_AXIS = 0;
	private static final Number PLUS_X_AXIS = 24;
	private static final int HISTORY_SIZE = (Integer) PLUS_X_AXIS; // number of points to plot in history
	private XYPlot mARPHistoryPlot = null;
    private CheckBox mHWAcceleratedCb;
    private CheckBox mShowFPSCb;
    private SimpleXYSeries mPressureHistorySeries = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setupPlotter();           //setup the androidplot
		setupView();              //setup the UI
	}

	private void setupView() {
		mCalendar = Calendar.getInstance();
		mNumformat.setMaximumFractionDigits(1);
		mNumformat.setMinimumFractionDigits(1);
		new Intent(MainActivity.this,BaroService.class);
		//intent for settings view
		mSettingIntent = new Intent(MainActivity.this,SettingsActivity.class);
		//Getting instance of UI components
		mStartServiceButton = (Button) findViewById(R.id.loggerButton);
		mStartButton = (Button) findViewById(R.id.sButton);
		mStopButton = (Button) findViewById(R.id.stButton);
		mDateSpinner = (Spinner) findViewById(R.id.spinner1);
		mTextView2 = (TextView) findViewById(R.id.textview2);
		mTextView2.setText("Loading");
		//getting scheduler instance for taking pressure readings periodically
		mScheduler = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		//intent for baro service, which is scheduled with mScheduler
		mBaroIntent = new Intent(getApplicationContext(),BaroService.class);
		mSchedulerIntent = PendingIntent.getService(getApplicationContext(), 0, mBaroIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mMaxView = (TextView) findViewById(R.id.textMax);
		mMinView = (TextView) findViewById(R.id.textMin);
		mSensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);

		mDateLis = new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				String s = (monthOfYear + 1) + "-" + (dayOfMonth) + "-" + year;
				String a = "";
				mDate_key = new String[] {s,a};
			}
		};

        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        mSensorText = (TextView) findViewById(R.id.sensorText);

		if(mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null){
		    //sensor is present in the device
			mSensorText.setText("PRESENT");
			mSensorText.setTextColor(Color.GREEN);
		}else{
            //sensor is NOT present in the device
	          mSensorText.setText("NOT PRESENT");
	          mSensorText.setTextColor(Color.RED);
            Toast.makeText(this,"Your device dont have Barometer SENSOR, this app wont work",Toast.LENGTH_SHORT).show();
		}
		mSensorManager.registerListener(this, mSensor, DELAY_VALUE);


		mStartServiceButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				unloadDB();
				updatePlotter();
			}
		});

		mStartButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mScheduler.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, mSchedulerIntent);
			}
		});

		mStopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mScheduler.cancel(mSchedulerIntent);

			}
		});

		mDateSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(final AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				if (arg2 == 0){
					mDate_key = null;
				}else if (arg2 == 1)
				{
					new DatePickerDialog(MainActivity.this, mDateLis, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();
				}
			};

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
	}

	protected void unloadDB() {
		mInputCvalue = mDBHelper.getdata(MyDBHelper.TABLE_PRS, MyDBHelper.COL_TSTAMP, mDate_key);
	}

	private void setupPlotter() {
		mDBHelper = new MyDBHelper(getApplicationContext());
		mDBHelper.openRead();
        mARPHistoryPlot = (XYPlot) findViewById(R.id.aprHistoryPlot);
        mPressureHistorySeries = new SimpleXYSeries("Atmospheric Pressure");
        mPressureHistorySeries.useImplicitXVals();
        mARPHistoryPlot.setRangeBoundaries(MINUS_Y_AXIS, PLUS_Y_AXIS, BoundaryMode.FIXED);
        mARPHistoryPlot.setDomainBoundaries(MINUS_X_AXIS, PLUS_X_AXIS, BoundaryMode.FIXED);
        mARPHistoryPlot.addSeries(mPressureHistorySeries, new LineAndPointFormatter(Color.rgb(100, 100, 200), Color.BLACK, null,null));
        mARPHistoryPlot.setDomainStepValue(5);
        mARPHistoryPlot.setTicksPerRangeLabel(3);
        mARPHistoryPlot.setDomainLabel("time (hr)");
        mARPHistoryPlot.getDomainLabelWidget().pack();
        mARPHistoryPlot.setRangeLabel("millibars (mb)");
        mARPHistoryPlot.getRangeLabelWidget().pack();

        // setup checkboxes:
        mHWAcceleratedCb = (CheckBox) findViewById(R.id.hwAccelerationCb);
        final PlotStatistics levelStats = new PlotStatistics(1000, false);
        final PlotStatistics histStats = new PlotStatistics(1000, false);
        mARPHistoryPlot.addListener(histStats);
        mHWAcceleratedCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {

                    mARPHistoryPlot.setLayerType(View.LAYER_TYPE_NONE, null);
                } else {

                    mARPHistoryPlot.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                }
            }
        });

        mShowFPSCb = (CheckBox) findViewById(R.id.showFpsCb);
        mShowFPSCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                levelStats.setAnnotatePlotEnabled(b);
                histStats.setAnnotatePlotEnabled(b);
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    super.onOptionsItemSelected(item);
	    startActivity(mSettingIntent);
	    return true;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		mPressure =  event.values[0];
		if (mFirstRun){
			mFirstRun = false;
			mMax = mPressure;
			mMin = mPressure;
			setView();
		}
		checkMinMax(mPressure);
		CharSequence mText;
		mText = mNumformat.format(mPressure) + "";
		mTextView2.setText(mText);
	}

	private void updatePlotter() {
		for(mIndex = 0; mIndex < mInputCvalue.length; mIndex++){
	        // get rid the oldest sample in history:
	        if (mPressureHistorySeries.size() > HISTORY_SIZE) {
	            mPressureHistorySeries.removeFirst();
	        }
	        // add the latest history sample:
	        mPressureHistorySeries.addLast(null, mInputCvalue[mIndex]);
		}
        // redraw the Plots:
        mARPHistoryPlot.redraw();
	}

	//this method find the minimum and maximum values for a session
	private void checkMinMax(float p) {
		if(p > mMax){
			mMax = p;
			setView();
		}else if(p < mMin){
			mMin = p;
			setView();
		}
	}

	//set the min and max to views
	private void setView(){
		mMaxView.setText(mNumformat.format(mMax));
		mMinView.setText(mNumformat.format(mMin));
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
	}
}
