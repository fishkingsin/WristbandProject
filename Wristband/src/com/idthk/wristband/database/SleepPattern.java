package com.idthk.wristband.database;

import java.util.Calendar;

public class SleepPattern {
	//private variables
	int pattern;
	long millis;
	// Empty constructor
    public SleepPattern(){
    	
    }
    // constructor
    public SleepPattern(long _millis, int _pattern){

        this.millis = _millis;
        this.pattern = _pattern;
    }
     
    public void setTimeStamp(long _millis){
        this.millis = _millis;
    }
    
    public long getTimeStamp(){
        return millis;
    }
    public void setPattern(int _pattern){
        this.pattern = _pattern;
    }
    
    public int getPattern(){
        return pattern;
    }
    
}

