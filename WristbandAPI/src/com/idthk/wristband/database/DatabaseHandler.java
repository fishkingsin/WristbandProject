package com.idthk.wristband.database;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//import com.idthk.wristband.api.Utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {
	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "recordManager";

	// Record table name
	String TABLE_RECORDS = "record";
	String TABLE_SLEEP = "sleep";
	String TABLE_SLEEPPATTERN = "sleeppattern";

	// Record Table Columns names
	private static final String KEY_ID = "id";
	// Record KEY
	private static final String KEY_TIMESTAMP = "timestamp";
	private static final String KEY_CALORIES = "calories";
	private static final String KEY_DISTANCE = "distance";
	private static final String KEY_STEPS = "step";
	private static final String KEY_MINUTES = "minutes";
	private static final String KEY_DATE = "key_date";

	// sleep key
	private static final String KEY_FALL_AS_SLEEP_DURATION = "key_fall_as_sleep_duration";
	private static final String KEY_TIME_WAKEN = "key_time_waken";
	private static final String KEY_IN_BED_TIME = "key_in_bed_time";
	private static final String KEY_ACTUAL_SLEEP_TIME = "key_actual_sleep_time";
	private static final String KEY_GO_TO_BED_TIME = "key_go_to_bed_time";
	private static final String KEY_ACTUAL_WAKE_TIME = "key_actual_wake_time";
	private static final String KEY_PRESET_WAKE_UP_TIME = "key_preset_wake_up_time";
	private static final String KEY_SLEEP_EFFICIENCY = "key_sleep_efficiency";

	// sleep pattern key
	private static final String KEY_PATTERN_AMPLITUDE = "sleep_pattern_amplitude";
	private static final String KEY_PATTERN_DURATION = "sleep_pattern_duration";
	private static final String KEY_PATTERN_TIME = "sleep_pattern_time";

	private static final String TAG = "DatabaseHandler";
	private static final String SQL_DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final SimpleDateFormat simpleFormat = new SimpleDateFormat(
			SQL_DATEFORMAT);
	String sql_num_format = "%1$02d";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public DatabaseHandler(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, null, version);

		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String CREATE_RECORDS_TABLE = "CREATE TABLE " + TABLE_RECORDS + "("
				+ KEY_TIMESTAMP + " LONG PRIMARY KEY, " + KEY_DATE
				+ " datetime, " + KEY_STEPS + " INTEGER, " + KEY_DISTANCE
				+ " FLOAT, " + KEY_CALORIES + " INTEGER, " + KEY_MINUTES
				+ " INTEGER " + ")";
//		Log.v(TAG, "Create query " + CREATE_RECORDS_TABLE);
		db.execSQL(CREATE_RECORDS_TABLE);

		String CREATE_SLEEP_TABLE = "CREATE TABLE " + TABLE_SLEEP + "("
				+ KEY_TIMESTAMP + " LONG PRIMARY KEY , " + KEY_DATE
				+ " datetime, " + KEY_FALL_AS_SLEEP_DURATION + " INTEGER , "
				+ KEY_TIME_WAKEN + " INTEGER , " + KEY_IN_BED_TIME
				+ " INTEGER , " + KEY_ACTUAL_SLEEP_TIME + " INTEGER , "
				+ KEY_GO_TO_BED_TIME + " datetime , " + KEY_ACTUAL_WAKE_TIME
				+ " datetime , " + KEY_PRESET_WAKE_UP_TIME + " datetime , "
				+ KEY_SLEEP_EFFICIENCY + " INTEGER " + ")";
		Log.v(TAG, "Create query " + CREATE_SLEEP_TABLE);
		db.execSQL(CREATE_SLEEP_TABLE);

		String CREATE_SLEEPPATTERN_TABLE = "CREATE TABLE " + TABLE_SLEEPPATTERN
				+ "(" 
				+ KEY_ID +" INTEGER  PRIMARY KEY , " 
				+ KEY_TIMESTAMP + " datetime ," 
				+ KEY_PATTERN_AMPLITUDE + " INTEGER , "
				+ KEY_PATTERN_DURATION + " INTEGER , " + KEY_PATTERN_TIME
				+ " INTEGER " + ")";
//		Log.v(TAG, "Create query " + CREATE_SLEEPPATTERN_TABLE);
		db.execSQL(CREATE_SLEEPPATTERN_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDS);

		// Create tables again
		onCreate(db);
	}

	// Adding new Record
	public void addRecord(Record record) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TIMESTAMP, record.getTimeStamp());

		values.put(KEY_MINUTES, record.getActivityTime());
		values.put(KEY_DATE,
				simpleFormat.format(record.getCalendar().getTime()));
		values.put(KEY_STEPS, record.getSteps());
		values.put(KEY_DISTANCE, record.getDistance());
		values.put(KEY_CALORIES, record.getCalories());

		// Inserting Row
		db.insert(TABLE_RECORDS, null, values);
		db.close(); // Closing database connection
	}

	// Adding new Record
	public void addSleepRecord(SleepRecord sleepRecord) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TIMESTAMP, sleepRecord.getTimeStamp());
		values.put(KEY_DATE, simpleFormat.format(sleepRecord.getTimeStamp()));
		values.put(KEY_FALL_AS_SLEEP_DURATION,
				sleepRecord.getFallingAsleepDuration());
		values.put(KEY_TIME_WAKEN, sleepRecord.getNumberOfTimesWaken());
		values.put(KEY_IN_BED_TIME, sleepRecord.getInBedTime());
		values.put(KEY_ACTUAL_SLEEP_TIME, sleepRecord.getActualSleepTime());
		values.put(KEY_GO_TO_BED_TIME,
				simpleFormat.format(sleepRecord.getGoToBedTime().getTime()));
		values.put(KEY_ACTUAL_WAKE_TIME, simpleFormat.format(sleepRecord
				.getActualWakeupTime().getTime()));
		values.put(KEY_PRESET_WAKE_UP_TIME, simpleFormat.format(sleepRecord
				.getPresetWakeupTime().getTime()));
		values.put(KEY_SLEEP_EFFICIENCY, sleepRecord.getSleepEfficiency());

		db.insert(TABLE_SLEEP, null, values);
		db.close(); // Closing database connection
		
		List<SleepPattern> sleeppatterns = sleepRecord.getPatterns();
//		Log.v(TAG,"sleeppattern timetamp : "+String.valueOf(sleepRecord.getGoToBedTime().getTimeInMillis()));
		
		for (SleepPattern pattern : sleeppatterns) {
			pattern.setTimestamp(sleepRecord.getGoToBedTime().getTime());
			addSleepPattern(pattern);
		}
	}

	// Adding new pattern
	private void addSleepPattern(SleepPattern sleepPattern) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		
		values.put(KEY_TIMESTAMP, simpleFormat.format(sleepPattern.getTimeStamp().getTime()));
		values.put(KEY_PATTERN_AMPLITUDE, sleepPattern.getAmplitude());
		values.put(KEY_PATTERN_DURATION, sleepPattern.getDuration());
		values.put(KEY_PATTERN_TIME, sleepPattern.getTime());

		// Inserting Row
		db.insert(TABLE_SLEEPPATTERN, null, values);
		db.close(); // Closing database connection
	}

	public Record getRecord(long timestamp) {

		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_RECORDS, new String[] { KEY_TIMESTAMP,
				KEY_MINUTES }, KEY_TIMESTAMP + "=?",
				new String[] { String.valueOf(timestamp) }, null, null, null,
				null);
		//Log.v(TAG, cursor.toString());
		if (cursor != null)
			cursor.moveToFirst();
		else
			return null;
		
		Record record = new Record(Long.parseLong(cursor.getString(cursor
				.getColumnIndex(KEY_TIMESTAMP))), Integer.parseInt(cursor
				.getString(cursor.getColumnIndex(KEY_MINUTES))),
				Integer.parseInt(cursor.getString(cursor
						.getColumnIndex(KEY_STEPS))), Integer.parseInt(cursor
						.getString(cursor.getColumnIndex(KEY_CALORIES))),
				Float.parseFloat(cursor.getString(cursor
						.getColumnIndex(KEY_DISTANCE))));
		// return Record
		return record;
	}
	public List<SleepRecord> getSleepRecord(Date gotobadtime) {

		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_SLEEP
				+" WHERE "+KEY_GO_TO_BED_TIME +"='"+ simpleFormat.format(gotobadtime)+"'";
		
		return getSleepRecordsByRawQuery(selectQuery);
	}
	public List<SleepRecord> getLastSleepRecord() {

		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_SLEEP 
				+ " WHERE  " + KEY_GO_TO_BED_TIME +" = (SELECT LAST( "+KEY_GO_TO_BED_TIME+ " )  FROM "+TABLE_SLEEP+")";
		
		return getSleepRecordsByRawQuery(selectQuery);
	}

	public List<Record> getAllRecords() {
		//Log.v(TAG, record.toString());
		String selectQuery = "SELECT  * FROM " + TABLE_RECORDS;
		return getRecordsByRawQuery(selectQuery);
	}

	public List<SleepRecord> getAllSleepRecords() {
		//Log.v(TAG, record.toString());
		String selectQuery = "SELECT  * FROM " + TABLE_SLEEP;
		return getSleepRecordsByRawQuery(selectQuery);
	}

	public List<SleepPattern> getAllSleepPatterns() {
		//Log.v(TAG, record.toString());
		String selectQuery = "SELECT  * FROM " + TABLE_SLEEPPATTERN;
		return getSleepPatternsByRawQuery(selectQuery);
	}
	
	public List<SleepPattern> getSleepPatterns(Date date) {
		//Log.v(TAG, " getSleepPatterns "+String.valueOf(date));
		String selectQuery = "SELECT  * FROM " + TABLE_SLEEPPATTERN
				+" WHERE " + KEY_TIMESTAMP + "='" +  simpleFormat.format(date) +"'";

		return getSleepPatternsByRawQuery(selectQuery);
		
		
	}

	// return 12 month sum
	public List<Record> getSumOfRecordsByYear(int whichyear) {
		List<Record> recordList = new ArrayList<Record>();
		String selectQuery = "SELECT SUM(" + KEY_MINUTES + ") " + " , "
				+ KEY_DATE + " , strftime('%Y', " + KEY_DATE + ") as year"
				+ " FROM " + TABLE_RECORDS + " WHERE year ='"
				+ String.format(sql_num_format, whichyear) + "'"
				+ " GROUP BY strftime('%m', " + KEY_DATE + ")" + " ORDER BY "
				+ KEY_DATE;

		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				Record record = new Record();

				Calendar _calendar = Calendar.getInstance();
				SimpleDateFormat dateFormat = new SimpleDateFormat(
						SQL_DATEFORMAT);
				try {
					_calendar.setTime(dateFormat.parse(cursor.getString(1)));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				record.setDate(_calendar);

				record.setActivityTime(cursor.getInt(0));
				// Log.v("getSumOfRecordsByYear", " Minutes " + cursor.getInt(0)
				// + "\n date " + cursor.getString(1) + "\n month "
				// + cursor.getInt(2));
				recordList.add(record);
			} while (cursor.moveToNext());
		}

		return recordList;
	}

	// return a day sum
	public List<Record> getSumOfRecordsByWeek(int whichweek, int whichyear) {
		List<Record> recordList = new ArrayList<Record>();

		String selectQuery = "SELECT SUM(" + KEY_MINUTES + ")" + ", "
				+ KEY_DATE + ",strftime('%Y'," + KEY_DATE + ") as year"
				+ ",strftime('%W'," + KEY_DATE + ") as weekofyear" + " FROM "
				+ TABLE_RECORDS + " WHERE weekofyear='"
				+ String.format(sql_num_format, whichweek) + "'"
				+ " AND year='" + String.format(sql_num_format, whichyear)
				+ "'" + " GROUP BY strftime('%j', " + KEY_DATE + ")"
				+ " ORDER BY " + KEY_DATE;

		Log.v("getSumOfRecordsByWeek", selectQuery);
		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				Record record = new Record();
				Calendar _calendar = Calendar.getInstance();
				SimpleDateFormat dateFormat = new SimpleDateFormat(
						SQL_DATEFORMAT);
				try {
					_calendar.setTime(dateFormat.parse(cursor.getString(1)));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				record.setDate(_calendar);
				record.setActivityTime(cursor.getInt(0));

				recordList.add(record);
			} while (cursor.moveToNext());
		}

		return recordList;
	}

	// return 28-31 day sum
	public List<Record> getSumOfRecordsByMonth(Calendar whichMonth) {
		List<Record> recordList = new ArrayList<Record>();
		String SQL_MONTH_FORMAT = "yyyy-MM";
		SimpleDateFormat dateOnlyFormat = new SimpleDateFormat(SQL_MONTH_FORMAT);

		String selectQuery = "SELECT SUM(" + KEY_MINUTES + ")" + " , "
				+ KEY_DATE + " FROM " + TABLE_RECORDS
				+ " WHERE strftime('%Y-%m'," + KEY_DATE + ")='"
				+ dateOnlyFormat.format(whichMonth.getTime()) + "'"
				+ " GROUP BY strftime('%j', " + KEY_DATE + ")" + " ORDER BY "
				+ KEY_DATE;

		Log.v("getSumOfRecordsByYear", selectQuery);
		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				Record record = new Record();
				Calendar _calendar = Calendar.getInstance();
				SimpleDateFormat dateFormat = new SimpleDateFormat(
						SQL_DATEFORMAT);
				try {
					_calendar.setTime(dateFormat.parse(cursor.getString(1)));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				record.setDate(_calendar);
				record.setActivityTime(cursor.getInt(0));

				recordList.add(record);
			} while (cursor.moveToNext());
		}

		return recordList;
	}

	public List<Record> getSumOfRecordsByRange(Calendar start, Calendar end) {
		List<Record> recordList = new ArrayList<Record>();
		String selectQuery = "SELECT SUM(" + KEY_MINUTES + ")" + " , "
				+ KEY_DATE + " , strftime('%H'," + KEY_DATE + ") as hour"
				+ " , strftime('HH:mm'," + KEY_DATE + ") as time" + " FROM "
				+ TABLE_RECORDS + " WHERE strftime('%Y-%m-%d', " + KEY_DATE
				+ ") >='" + simpleFormat.format(start.getTime()) + "'"
				+ " AND strftime('%Y-%m-%d', " + KEY_DATE + ") <='"
				+ simpleFormat.format(end.getTime()) + "'"
				+ " GROUP BY strftime('%d', " + KEY_DATE + ")" + " ORDER BY "
				+ KEY_DATE;
		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				Record record = new Record();
				Calendar _calendar = Calendar.getInstance();

				try {
					_calendar.setTime(simpleFormat.parse(cursor.getString(1)));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				record.setDate(_calendar);
				record.setActivityTime(cursor.getInt(0));
				// Log.v(TAG, "getSumOfRecordsByMonth sum minute"
				// + cursor.getInt(0)
				// + "\n getSumOfRecordsByMonth  date" + cursor.getString(1)
				// + "\n getSumOfRecordsByMonth  hour" + cursor.getInt(2));

				recordList.add(record);
			} while (cursor.moveToNext());
		}
		return recordList;
	}

	public List<Record> getSumOfRecordsByHour(Calendar whichdate) {
		// List<Record> recordList = new ArrayList<Record>();
		String SQL_DATE_ONLY_FORMAT = "yyyy-MM-dd";
		SimpleDateFormat dateOnlyFormat = new SimpleDateFormat(
				SQL_DATE_ONLY_FORMAT);
		String selectQuery = "SELECT * " + " FROM " + TABLE_RECORDS
				+ " WHERE strftime('%Y-%m-%d', " + KEY_DATE + ") ='"
				+ dateOnlyFormat.format(whichdate.getTime()) + "' "
				+ " ORDER BY " + KEY_DATE;
		//Log.v(TAG, selectQuery);
		return getRecordsByRawQuery(selectQuery);
	}

	public List<Record> getSumOfRecordsByDay(Calendar whichdate) {
		List<Record> recordList = new ArrayList<Record>();
		String SQL_DATE_ONLY_FORMAT = "yyyy-MM-dd";
		SimpleDateFormat dateOnlyFormat = new SimpleDateFormat(
				SQL_DATE_ONLY_FORMAT);

		String selectQuery = "SELECT SUM(" + KEY_MINUTES + ")" + " , "
				+ KEY_DATE + " , strftime('%H'," + KEY_DATE + ") as hour"
				+ " FROM " + TABLE_RECORDS + " WHERE strftime('%Y-%m-%d', "
				+ KEY_DATE + ") ='"
				+ dateOnlyFormat.format(whichdate.getTime()) + "'"
				+ " GROUP BY strftime('%H', " + KEY_DATE + ")" + " ORDER BY "
				+ KEY_DATE;

//		Log.v(TAG, selectQuery);
		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				Record record = new Record();
				Calendar _calendar = Calendar.getInstance();

				try {
					_calendar.setTime(simpleFormat.parse(cursor.getString(1)));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				record.setDate(_calendar);
				record.setActivityTime(cursor.getInt(0));
				// Log.v(TAG, "getSumOfRecordsByMonth sum minute"
				// + cursor.getInt(0) + "\n getSumOfRecordsByMonth  date"
				// + cursor.getString(1)
				// + "\n getSumOfRecordsByMonth  hour" + cursor.getInt(2));

				recordList.add(record);
			} while (cursor.moveToNext());
		}

		return recordList;
	}

	public List<Record> getRecordsByRawQuery(String selectQuery) {
		List<Record> recordList = new ArrayList<Record>();

		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {

				Record Record = new Record(cursor.getLong(cursor
						.getColumnIndex(KEY_TIMESTAMP)), cursor.getInt(cursor
						.getColumnIndex(KEY_MINUTES)), cursor.getInt(cursor
						.getColumnIndex(KEY_STEPS)), cursor.getInt(cursor
						.getColumnIndex(KEY_CALORIES)), cursor.getFloat(cursor
						.getColumnIndex(KEY_DISTANCE)));
				recordList.add(Record);
			} while (cursor.moveToNext());
		}
		return recordList;
	}

	public List<SleepRecord> getSleepRecordsByRawQuery(String selectQuery) {
		List<SleepRecord> recordList = new ArrayList<SleepRecord>();

		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.rawQuery(selectQuery, null);
//		Calendar date1 = Calendar.getInstance();
//		Calendar date2 = Calendar.getInstance();
//		Calendar date3 = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat(SQL_DATEFORMAT);

		// date.setTime(dateFormat.parse(cursor.getString(1)));
		if (cursor.moveToFirst()) {
			do {

				try {

					SleepRecord sleepRecord = new SleepRecord(
							//cursor.getLong(cursor.getColumnIndex(KEY_TIMESTAMP)),
							cursor.getInt(cursor
									.getColumnIndex(KEY_FALL_AS_SLEEP_DURATION)),
							cursor.getInt(cursor.getColumnIndex(KEY_TIME_WAKEN)),
							cursor.getInt(cursor
									.getColumnIndex(KEY_IN_BED_TIME)),
							cursor.getInt(cursor
									.getColumnIndex(KEY_ACTUAL_SLEEP_TIME)),
							dateFormat.parse(cursor.getString(cursor
									.getColumnIndex(KEY_GO_TO_BED_TIME))),
							dateFormat.parse(cursor.getString(cursor
									.getColumnIndex(KEY_ACTUAL_WAKE_TIME))),
							dateFormat.parse(cursor.getString(cursor
									.getColumnIndex(KEY_PRESET_WAKE_UP_TIME))),
							cursor.getInt(cursor
									.getColumnIndex(KEY_SLEEP_EFFICIENCY)));
					sleepRecord.setPatterns(getSleepPatterns(sleepRecord.getGoToBedTime().getTime()));
					recordList.add(sleepRecord);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} while (cursor.moveToNext());
		}
		return recordList;
	}

	public List<SleepPattern> getSleepPatternsByRawQuery(String selectQuery) {
		List<SleepPattern> recordList = new ArrayList<SleepPattern>();
//		Log.v(TAG,"getSleepPatternsByRawQuery : " + selectQuery);
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		// date.setTime(dateFormat.parse(cursor.getString(1)));
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				SQL_DATEFORMAT);
		if (cursor.moveToFirst()) {
			do {
				SleepPattern pattern;
				try {
					pattern = new SleepPattern(
							cursor.getInt(cursor.getColumnIndex(KEY_ID)),
							dateFormat.parse(cursor.getString(cursor.getColumnIndex(KEY_TIMESTAMP))),
							cursor.getInt(cursor.getColumnIndex(KEY_PATTERN_TIME)),
							cursor.getInt(cursor.getColumnIndex(KEY_PATTERN_DURATION)),
							cursor.getInt(cursor.getColumnIndex(KEY_PATTERN_AMPLITUDE))
							
							);
					recordList.add(pattern);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} while (cursor.moveToNext());
		}
		return recordList;
	}

	// Getting Records Count
	public int getRecordsCount() {
		String countQuery = "SELECT  * FROM " + TABLE_RECORDS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();

		// return count
		return cursor.getCount();
	}

	public List<String> getRecordRange() {
		String countQuery = "SELECT MIN (" + KEY_DATE
				+ ") AS OldestOrder ,MAX(" + KEY_DATE
				+ ") AS LastestOrder FROM " + TABLE_RECORDS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		List<String> dateRange = new ArrayList<String>();
		Calendar date = Calendar.getInstance();
		if (cursor.getColumnCount() > 0) {
			if (cursor.moveToFirst()) {
				do {
					try {
						if (cursor.getString(0) != null)
							dateRange.add(cursor.getString(0));

						if (cursor.getString(1) != null)
							dateRange.add(cursor.getString(1));

//						Log.v(TAG, cursor.getString(0));
//						Log.v(TAG, cursor.getString(1));
//						Log.v(TAG, "first date " + cursor.getString(0)
//								+ "\nlast date" + cursor.getString(1));
					} catch (Exception e) {
						e.printStackTrace();
					}
				} while (cursor.moveToNext());
			}
		}
		cursor.close();

		return dateRange;

	}

	// Updating single Record
	public int updateRecord(Record record) {
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_TIMESTAMP, record.getTimeStamp());
		values.put(KEY_MINUTES, record.getActivityTime());
		values.put(KEY_DATE,
				simpleFormat.format(record.getCalendar().getTime()));

		// updating row
		return db.update(TABLE_RECORDS, values, KEY_TIMESTAMP + " = ?",
				new String[] { String.valueOf(record.getTimeStamp()) });
	}
	// Adding new Record
	public int updateSleepRecord(SleepRecord sleepRecord) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TIMESTAMP, sleepRecord.getTimeStamp());
		values.put(KEY_DATE, simpleFormat.format(sleepRecord.getTimeStamp()));
		values.put(KEY_FALL_AS_SLEEP_DURATION,
				sleepRecord.getFallingAsleepDuration());
		values.put(KEY_TIME_WAKEN, sleepRecord.getNumberOfTimesWaken());
		values.put(KEY_IN_BED_TIME, sleepRecord.getInBedTime());
		values.put(KEY_ACTUAL_SLEEP_TIME, sleepRecord.getActualSleepTime());
		values.put(KEY_GO_TO_BED_TIME,
				simpleFormat.format(sleepRecord.getGoToBedTime().getTime()));
		values.put(KEY_ACTUAL_WAKE_TIME, simpleFormat.format(sleepRecord
				.getActualWakeupTime().getTime()));
		values.put(KEY_PRESET_WAKE_UP_TIME, simpleFormat.format(sleepRecord
				.getPresetWakeupTime().getTime()));
		values.put(KEY_SLEEP_EFFICIENCY, sleepRecord.getSleepEfficiency());


		// updating row
		int ret =  db.update(TABLE_SLEEP, values, KEY_TIMESTAMP + " = ?",
				new String[] { String.valueOf(sleepRecord.getTimeStamp()) });
		if(ret>0)
		{
			List<SleepPattern> sleeppatterns = sleepRecord.getPatterns();
			
			for (SleepPattern pattern : sleeppatterns) {

				updateSleepPattern(pattern);
			}
		}
//		Log.v(TAG,"Update Sleep Record : " + simpleFormat.format(sleepRecord.getTimeStamp()));
		return ret;
	}
	private int updateSleepPattern(SleepPattern sleepPattern) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		
		values.put(KEY_TIMESTAMP, simpleFormat.format(sleepPattern.getTimeStamp().getTime()));
		values.put(KEY_PATTERN_AMPLITUDE, sleepPattern.getAmplitude());
		values.put(KEY_PATTERN_DURATION, sleepPattern.getDuration());
		values.put(KEY_PATTERN_TIME, sleepPattern.getTime());

		int ret =  db.update(TABLE_SLEEPPATTERN, values, KEY_ID + " = ?",
				new String[] { String.valueOf(sleepPattern.getId() ) });
		
//		Log.v(TAG,"Update SleepPattern ID : " + sleepPattern.getId());
		return ret;
	}
	// Deleting single Record
	public void deleteRecord(Record record) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_RECORDS, KEY_TIMESTAMP + " = ?",
				new String[] { String.valueOf(record.getTimeStamp()) });
		db.close();
	}

}
