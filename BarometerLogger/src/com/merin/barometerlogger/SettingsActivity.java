package com.merin.barometerlogger;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class SettingsActivity extends Activity {

	private Button mDeleteButton;
	private Button mExportButton;
 	private MyDBHelper mDBHelper = new MyDBHelper(this);
	private int mDeletedRows;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		mDeleteButton = (Button) findViewById(R.id.button1);
		mExportButton = (Button) findViewById(R.id.exportButton);
        mDeleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDBHelper.openWrite();
				mDeletedRows = mDBHelper.resetDb(MyDBHelper.TABLE_PRS, "1", null);
				//if (deletedRows == )
	    		Toast.makeText(getApplicationContext(), "Deleted " + mDeletedRows + " rows", Toast.LENGTH_LONG).show();

			}
		});

		mExportButton.setOnClickListener(new OnClickListener() {

            private String String;

            @Override
            public void onClick(View v) {
                ExportDatabaseCSVTask mExpTask = new ExportDatabaseCSVTask(SettingsActivity.this);
                mExpTask.execute(String );

            }
        });
	}
}




