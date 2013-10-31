package com.merin.barometerlogger;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;

import org.xml.sax.Parser;

import com.androidplot.util.PlotStatistics;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;

import android.app.Activity;
import android.app.Service;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener{

	private SensorManager mSensorManager;
	private Sensor mSensor;
	private int mdelay;
	private TextView tv2;
	private TextView mMaxView;
	private TextView mMinView;
	private float p;
	
	// objects used for androidplot implementation
            // number of points to plot in history
	private static final Number MINUS_Y_AXIS = 900;
	private static final Number PLUS_Y_AXIS = 1000;
	private static final Number MINUS_X_AXIS = 0;
	private static final Number PLUS_X_AXIS = 60;
	private static final int HISTORY_SIZE = (Integer) PLUS_X_AXIS;
	
	private XYPlot aprHistoryPlot = null;
    private CheckBox hwAcceleratedCb;
    private CheckBox showFpsCb;
    private SimpleXYSeries pressureHistorySeries = null;
    
	private NumberFormat nformat = NumberFormat.getNumberInstance();
	private final int DELAY_VALUE = 1000000;
	private float mMax = 0;
	private float mMin = 0;
	private Boolean mFirstRun = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		setupPlotter();
		
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

	private void setupPlotter() {
		// setup the APR History plot:
        aprHistoryPlot = (XYPlot) findViewById(R.id.aprHistoryPlot);
 
        pressureHistorySeries = new SimpleXYSeries("Atmospheric Pressure");
        pressureHistorySeries.useImplicitXVals();
 
        aprHistoryPlot.setRangeBoundaries(MINUS_Y_AXIS, PLUS_Y_AXIS, BoundaryMode.FIXED);
        aprHistoryPlot.setDomainBoundaries(MINUS_X_AXIS, PLUS_X_AXIS, BoundaryMode.FIXED);  
        //aprHistoryPlot.addSeries(azimuthHistorySeries, new LineAndPointFormatter(Color.rgb(100, 100, 200), Color.BLACK, null));
        //LineAndPointFormatter f1 = new LineAndPointFormatter(Color.rgb(0, 0, 200), null, Color.rgb(0, 0, 80));
        //f1.getFillPaint().setAlpha(220);
        aprHistoryPlot.addSeries(pressureHistorySeries, new LineAndPointFormatter(Color.rgb(100, 100, 200), Color.BLACK, null,null));
        aprHistoryPlot.setDomainStepValue(5);
        aprHistoryPlot.setTicksPerRangeLabel(3);
        aprHistoryPlot.setDomainLabel("Seconds");
        aprHistoryPlot.getDomainLabelWidget().pack();
        aprHistoryPlot.setRangeLabel("millibars (mb)");
        aprHistoryPlot.getRangeLabelWidget().pack();
 
        // setup checkboxes:
        hwAcceleratedCb = (CheckBox) findViewById(R.id.hwAccelerationCb);
        final PlotStatistics levelStats = new PlotStatistics(1000, false);
        final PlotStatistics histStats = new PlotStatistics(1000, false);
 

        aprHistoryPlot.addListener(histStats);
        hwAcceleratedCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {

                    aprHistoryPlot.setLayerType(View.LAYER_TYPE_NONE, null);
                } else {

                    aprHistoryPlot.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                }
            }
        });
	
        showFpsCb = (CheckBox) findViewById(R.id.showFpsCb);
        showFpsCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                levelStats.setAnnotatePlotEnabled(b);
                histStats.setAnnotatePlotEnabled(b);
            }
        });
        
        
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
		p =  event.values[0];
		
		updatePlotter();
		
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

	private void updatePlotter() {
		Number[] series1Numbers = {p};
        //aprLevelsSeries.setModel(Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
 
        // get rid the oldest sample in history:
        if (pressureHistorySeries.size() > HISTORY_SIZE) {
            pressureHistorySeries.removeFirst();
        }
        
     // add the latest history sample:
        pressureHistorySeries.addLast(null, p);
 
        // redraw the Plots:
        aprHistoryPlot.redraw();
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
