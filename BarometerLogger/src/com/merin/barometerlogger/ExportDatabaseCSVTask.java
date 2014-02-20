package com.merin.barometerlogger;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import au.com.bytecode.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ExportDatabaseCSVTask extends AsyncTask<String, Void, Boolean>{

private ProgressDialog dialog;
private Context context;
private File file=null;
private ArrayList<Logger> mListLogger = null;
private Logger mLogger = null;
private MyDBHelper mMyDBHelper;



    ExportDatabaseCSVTask(Context ctx){
           context = ctx;
           dialog = new ProgressDialog(context);
           mMyDBHelper = new MyDBHelper(context);
    }

    /* (non-Javadoc)
     * @see android.os.AsyncTask#onPreExecute()
     */
    @Override
    protected void onPreExecute() {
            //  ProgressDialog.show(context, "Export DB ", "Exporting Database ... ");
        this.dialog.setTitle("test");
        this.dialog.setMessage("Message");
        this.dialog.show();
        super.onPreExecute();
    }


    @Override
    protected Boolean doInBackground(String... params) {

        mMyDBHelper.openRead();
        //File mDBFile = getDatabasePath("logger.db");
        File mExportDir = new File(Environment.getExternalStorageDirectory(),"");
        if(!mExportDir.exists()){
            mExportDir.mkdir();
        }

        file = new File(mExportDir,"LoggerCSV.csv");
        try {
            file.createNewFile();
            CSVWriter mCSVWrite = new CSVWriter(new FileWriter(file));
            mListLogger = mMyDBHelper.getalldata(MyDBHelper.TABLE_PRS);
            // this is the Column of the table and same for Header of CSV file
            String mArrStr1[] ={"MBARS", "TSTAMP"};
            mCSVWrite.writeNext(mArrStr1);

            if(mListLogger.size() > 1)
            {
             for(int mIndex=0; mIndex < mListLogger.size(); mIndex++)
             {
               mLogger=mListLogger.get(mIndex);
               String mArrStr[] ={mLogger.getmPressure()+"", mLogger.getmTimeStamp()};
               mCSVWrite.writeNext(mArrStr);
             }
            }

            mCSVWrite.close();
            return true;

        } catch (IOException e) {
            Log.e("ExportDatabaseCSVTask", e.getMessage(), e);
            e.printStackTrace();
            return false;
        }
    }

    /* (non-Javadoc)
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(Boolean success) {
        if(this.dialog.isShowing()){
            this.dialog.dismiss();
        }
        if(success){
            Toast.makeText(context, "Export successful!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Export failed!", Toast.LENGTH_SHORT).show();
        }
    }
}
