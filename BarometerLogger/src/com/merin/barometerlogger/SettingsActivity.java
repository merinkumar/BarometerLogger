package com.merin.barometerlogger;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class SettingsActivity extends Activity {

	Button mDeleteButton;
 	MyDBHelper mDBHelper = new MyDBHelper(this);
	int mDeletedRows;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		mDeleteButton = (Button) findViewById(R.id.button1);
		mDeleteButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mDBHelper.openWrite();
				mDeletedRows = mDBHelper.resetDb(MyDBHelper.TABLE_PRS, "1", null);
				
				//if (deletedRows == )
	    		Toast.makeText(getApplicationContext(), "Deleted " + mDeletedRows + " rows", Toast.LENGTH_LONG).show();
				
			}
		});

	}



}
