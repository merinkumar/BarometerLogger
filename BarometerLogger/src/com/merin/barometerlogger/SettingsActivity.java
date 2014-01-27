package com.merin.barometerlogger;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class SettingsActivity extends Activity {

	Button deleteButton;
 	MyDBHelper dbHelper = new MyDBHelper(this);
	int deletedRows;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		deleteButton = (Button) findViewById(R.id.button1);
		deleteButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dbHelper.openWrite();
				deletedRows = dbHelper.resetDb(MyDBHelper.TABLE_PRS, "1", null);
				
				//if (deletedRows == )
	    		Toast.makeText(getApplicationContext(), "Deleted " + deletedRows + " rows", Toast.LENGTH_LONG).show();
				
			}
		});

	}



}
