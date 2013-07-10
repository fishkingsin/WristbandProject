package com.idthk.wristband.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.util.Log;

public class SleepRecord {
	// private variables
	
	private long timestamp = 0;

	private int fallingAsleepDuration = 0;

	private int numberOfTimesWaken = 0;

	private int inBedTime = 0;

	private int actualSleepTime = 0;

	private Calendar goToBedTime = Calendar.getInstance();
	private Calendar actualWakeupTime = Calendar.getInstance();

	private Calendar presetWakeupTime = Calendar.getInstance();
	private int sleepEfficiency = 0;
	private List<SleepPattern> patterns = null;//new ArrayList<SleepPattern>();
	private static final String SQL_DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
	SimpleDateFormat mFullDateFormat = new SimpleDateFormat(
			SQL_DATEFORMAT);
	SimpleDateFormat mTimeFormat = new SimpleDateFormat(
			"HH:mm");
	// Empty constructor
	public SleepRecord() {
		

	}

	public SleepRecord( int _fallingAsleepDuration,
			int _numberOfTimesWaken, int _inBedTime, int _actualSleepTime,
			Date _goToBedTime, Date _actualWakeupTime,
			Date _presetWakeupTime, int _sleepEfficiency) {
//		timestamp = _timestamp;

		setPatterns(new ArrayList<SleepPattern>());

		setFallingAsleepDuration(_fallingAsleepDuration);

		setNumberOfTimesWaken(_numberOfTimesWaken);

		setInBedTime(_inBedTime);

		setActualSleepTime(_actualSleepTime);

		getGoToBedTime().setTime(_goToBedTime);
		
		getActualWakeupTime().setTime(_actualWakeupTime);
		getPresetWakeupTime().setTime(_presetWakeupTime);
		
		setSleepEfficiency(_sleepEfficiency);
	}

//	public void setTimestamp(long _timestamp) {
//
//		this.timestamp = _timestamp;
//	}

	public long getTimeStamp() {
		return getGoToBedTime().getTimeInMillis() ;
	}


	/**
	 * @return the goToBedTime
	 */
	public Calendar getGoToBedTime() {
		return goToBedTime;
	}

	/**
	 * @param ts the goToBedTime to set
	 */
	public void setGoToBedTime(Date ts) {
		this.goToBedTime.setTime( ts );
	}
	
	public void setGoToBedTime(Calendar instance) {
		// TODO Auto-generated method stub
		this.goToBedTime.setTime(instance.getTime());
	}

	/**
	 * @return the fallingAsleepDuration
	 */
	public int getFallingAsleepDuration() {
		return fallingAsleepDuration;
	}

	/**
	 * @param fallingAsleepDuration the fallingAsleepDuration to set
	 */
	public void setFallingAsleepDuration(int fallingAsleepDuration) {
		this.fallingAsleepDuration = fallingAsleepDuration;
	}

	/**
	 * @return the numberOfTimesWaken
	 */
	public int getNumberOfTimesWaken() {
		return numberOfTimesWaken;
	}

	/**
	 * @param numberOfTimesWaken the numberOfTimesWaken to set
	 */
	public void setNumberOfTimesWaken(int numberOfTimesWaken) {
		this.numberOfTimesWaken = numberOfTimesWaken;
	}

	/**
	 * @return the inBedTime
	 */
	public int getInBedTime() {
		return inBedTime;
	}

	/**
	 * @param inBedTime the inBedTime to set
	 */
	public void setInBedTime(int inBedTime) {
		this.inBedTime = inBedTime;
	}

	/**
	 * @return the actualSleepTime
	 */
	public int getActualSleepTime() {
		return actualSleepTime;
	}

	/**
	 * @param actualSleepTime the actualSleepTime to set
	 */
	public void setActualSleepTime(int actualSleepTime) {
		this.actualSleepTime = actualSleepTime;
	}

	/**
	 * @return the actualWakeupTime
	 */
	public Calendar getActualWakeupTime() {
		return actualWakeupTime;
	}

	/**
	 * @param actualWakeupTime the actualWakeupTime to set
	 */
	public void setActualWakeupTime(Calendar actualWakeupTime) {
		this.actualWakeupTime = actualWakeupTime;
	}

	/**
	 * @return the presetWakeupTime
	 */
	public Calendar getPresetWakeupTime() {
		return presetWakeupTime;
	}

	/**
	 * @param presetWakeupTime the presetWakeupTime to set
	 */
	public void setPresetWakeupTime(Calendar presetWakeupTime) {
		this.presetWakeupTime = presetWakeupTime;
	}

	/**
	 * @return the sleepEfficiency
	 */
	public int getSleepEfficiency() {
		return sleepEfficiency;
	}

	/**
	 * @param sleepEfficiency the sleepEfficiency to set
	 */
	public void setSleepEfficiency(int sleepEfficiency) {
		this.sleepEfficiency = sleepEfficiency;
	}

	/**
	 * @return the patterns
	 */
	public List<SleepPattern> getPatterns() {
		if(patterns==null)
		{
			patterns = new ArrayList<SleepPattern>();
		}
		return patterns;
	}

	/**
	 * @param patterns the patterns to set
	 */
	public void setPatterns(List<SleepPattern> patterns) {
		this.patterns = new ArrayList<SleepPattern>(patterns);
	}



	@Override
	public String toString() {
		
		String _msg = mFullDateFormat.format(this.getTimeStamp()) +" - ";
		_msg += "fallingAsleepDuration "
				+ Integer.toString(getFallingAsleepDuration()) + " , "
				+ " numberOfTimesWaken " + Integer.toString(getNumberOfTimesWaken())
				+ " , " + "inBedTime " + Integer.toString(getInBedTime()) + " , "
				+ " actualSleepTime " + Integer.toString(getActualSleepTime())
				+ " , " + "goToBedTime "
				+ mFullDateFormat.format(getGoToBedTime().getTime()) + " , "
				+ " actualWakeupTime "
				+ mTimeFormat.format(getActualWakeupTime().getTime()) + " , "
				+ " presetWakeupTime "
				+ mTimeFormat.format(getPresetWakeupTime().getTime()) + " , "
				+ " sleepEfficiency " + Integer.toString(getSleepEfficiency());

		for (SleepPattern pattern : getPatterns()) {
			_msg += "\nPattern : " + pattern.toString();
		}
		return _msg;
	}

	

}
