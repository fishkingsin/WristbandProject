package com.idthk.wristband.database;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SleepPattern {
	int id ;
	private Calendar timestamp = Calendar.getInstance();
	private int time;
	private int duration;
	private int amplitude;
	SimpleDateFormat dateOnlyFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");
	public SleepPattern()
	{
		setTime(0);
		setDuration(0);
		setAmplitude(0);
	}
	public SleepPattern(int _id , Date _timestamp , int _time, int _duration, int _amplitude)
	{
		id = _id;
		setTimestamp(_timestamp);
		setTime(_time);
		setDuration(_duration);
		setAmplitude(_amplitude);
	}
	public SleepPattern( Date _timestamp , int _time, int _duration, int _amplitude)
	{
		
		setTimestamp(_timestamp);
		setTime(_time);
		setDuration(_duration);
		setAmplitude(_amplitude);
	}
	
	public void setId(int _id) {

		this.id = _id;
	}

	public int getId() {
		return id;
	}
	
	public void setTimestamp(Date _timestamp) {

		this.timestamp.setTime(_timestamp);
	}

	public Calendar getTimeStamp() {
		return timestamp;
	}
	/**
	 * @return the time
	 */
	public int getTime() {
		return time;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(int time) {
		this.time = time;
	}
	/**
	 * @return the duration
	 */
	public int getDuration() {
		return duration;
	}
	/**
	 * @param duration the duration to set
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}
	/**
	 * @return the amplitude
	 */
	public int getAmplitude() {
		return amplitude;
	}
	/**
	 * @param amplitude the amplitude to set
	 */
	public void setAmplitude(int amplitude) {
		this.amplitude = amplitude;
	}
	
	@Override
	public String toString()
	{
		return "ID : "+getId() +" " + dateOnlyFormat.format(this.getTimeStamp().getTime()) +" - " + "Time : "+String.valueOf(getTime()) + " Duration : "+String.valueOf(getDuration()) + " Amplitude : " +String.valueOf(getAmplitude());
	}
	
}
