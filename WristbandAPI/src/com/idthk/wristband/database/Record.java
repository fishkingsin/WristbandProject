package com.idthk.wristband.database;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.util.Log;

public class Record {
	// private variables
//	int _id;

	private float distance;
	private int steps;
	private int calories;
	private int activityTime;
	private long timestamp;

	// Empty constructor
	public Record() {

	}

	// constructor
	public Record( long _timestamp, int _activityTime, int _steps,
			int _calories, float _distance) {
		
		this.timestamp = _timestamp;
		this.activityTime = _activityTime;
		this.steps = _steps;
		this.calories = _calories;
		this.distance = _distance;
//		Log.v("Record" , "steps : "+steps +
//		"  calories : "+ calories + " distance : "+distance);
	}

	// constructor
	public Record(long _timestamp, int _activityTime) {

		this.timestamp = _timestamp;

		this.activityTime = _activityTime;
		this.steps = 0;
		this.calories = 0;
		this.distance = 0;
	}

	public void setActivityTime(int _activityTime) {
		this.activityTime = _activityTime;
	}

	public int getActivityTime() {
		return this.activityTime;
	}

	public void setDate(long _timestamp) {

		this.timestamp = _timestamp;
	}
	public void setTimestamp(long _timestamp) {

		this.timestamp = _timestamp;
	}
	public long getTimeStamp() {
		return timestamp;
	}
	public Calendar getCalendar() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp);
		return calendar;
	}

	public void setDate(Calendar _clendar) {
		timestamp = _clendar.getTimeInMillis();
	}

	

	public int getYear() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp);
		return calendar.get(Calendar.YEAR);
	}

	public int getWeekofYear() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp);
		return calendar.get(Calendar.WEEK_OF_YEAR);
	}

	public int getMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp);
		return calendar.get(Calendar.MONTH);
	}

	public int getDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	public int getHour() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp);
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	public void setSteps(int _step) {
		steps = _step;
	}

	public void setCalories(int _calories) {
		calories = _calories;
	}

	public void setDistance(float _distance) {
		distance = _distance;
	}

	public int getSteps() {
		return steps;
	}

	public int getCalories() {
		return calories;
	}

	public float getDistance() {
		return distance;
	}
	@Override
	public String toString()
	{
		String SQL_DATE_ONLY_FORMAT = "yyyy-MM-dd HH:mm";
		SimpleDateFormat dateOnlyFormat = new SimpleDateFormat(
				SQL_DATE_ONLY_FORMAT);
		return dateOnlyFormat.format(getCalendar().getTime()) + " - steps : " + getSteps()
				+ " calories : " + getCalories()
				+ " distance : " + getDistance()
				+ " activity time : " + getActivityTime();
		
	}
}
