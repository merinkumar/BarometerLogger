package com.merin.barometerlogger;

public class Logger {

    private float mPressure;
    private String mTimeStamp;

    public Logger(){

    }

    public Logger(float press, String tstamp){
        this.mPressure = press;
        this.mTimeStamp = tstamp;
    }

    /**
     * @return the mPressure
     */
    public float getmPressure() {
        return mPressure;
    }

    /**
     * @param mPressure the mPressure to set
     */
    public void setmPressure(float mPressure) {
        this.mPressure = mPressure;
    }

    /**
     * @return the mTimeStamp
     */
    public String getmTimeStamp() {
        return mTimeStamp;
    }

    /**
     * @param mTimeStamp the mTimeStamp to set
     */
    public void setmTimeStamp(String mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }



}
