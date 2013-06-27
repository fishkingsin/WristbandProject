package com.idthk.wristband.database;

import java.util.Calendar;

public class Record {
	//private variables
    int _id;
	int minutes;

	int year;
	int month;
	int day;
	int weekofyear;
	int hour;
	long millis;
	// Empty constructor
    public Record(){
    	
    }
    // constructor
    public Record(int id, long _millis, int _minutes){
        this._id = id;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(_millis);
        this.year =  calendar.get(Calendar.YEAR);
        this.month =  calendar.get(Calendar.MONTH);
        this.day =  calendar.get(Calendar.DAY_OF_MONTH);
        this.weekofyear =  calendar.get(Calendar.WEEK_OF_YEAR);
        this.hour =  calendar.get(Calendar.HOUR_OF_DAY);
        
        this.millis = _millis;
        
        this.minutes = _minutes;
    }
     
    // constructor
    public Record(long _millis, int _minutes){
    	Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(_millis);
        this.year =  calendar.get(Calendar.YEAR);
        this.month =  calendar.get(Calendar.MONTH);
        this.day =  calendar.get(Calendar.DAY_OF_MONTH);
        this.weekofyear =  calendar.get(Calendar.WEEK_OF_YEAR);
        this.hour =  calendar.get(Calendar.HOUR_OF_DAY);
        
        this.millis = _millis;
        
    	this.minutes = _minutes;
    	
    }
    // getting ID
    public int getID(){
        return this._id;
    }
    public void setID(int id){
        this._id = id;
    }
    public void setMinutes(int _minutes){
        this.minutes = _minutes;
    }

    public int getMinutes(){
        return this.minutes;
    }
    public void setDate(long _millis){
    	Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(_millis);
        this.year =  calendar.get(Calendar.YEAR);
        this.month =  calendar.get(Calendar.MONTH);
        this.day =  calendar.get(Calendar.DAY_OF_MONTH);
        this.weekofyear =  calendar.get(Calendar.WEEK_OF_YEAR);
        this.hour =  calendar.get(Calendar.HOUR_OF_DAY);
        
        this.millis = _millis;
    }
    public Calendar getCalendar(){
    	Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar;
    }
    public void setDate(Calendar _clendar){
//    	Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(millis);
        millis = _clendar.getTimeInMillis();
        this.year =  _clendar.get(Calendar.YEAR);
        this.month =  _clendar.get(Calendar.MONTH);
        this.day =  _clendar.get(Calendar.DAY_OF_MONTH);
        this.weekofyear =  _clendar.get(Calendar.WEEK_OF_YEAR);
        this.hour =  _clendar.get(Calendar.HOUR_OF_DAY);
    }
    public long getTimeStamp(){
        return millis;
    }
    public int getYear(){
        return year;
    }
    public int getWeekofYear(){
        return weekofyear;
    }
    public int getMonth(){
        return month;
    }
    public int getDay(){
        return day;
    }
    public long getHour(){
        return hour;
    }
}

