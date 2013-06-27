package com.idthk.wristband.database;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

	// Record Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_TIMESTAMP = "timestamp";
//	private static final String KEY_YEAR = "year";
//	private static final String KEY_MONTH = "month";
//	private static final String KEY_WEEK_OF_YEAR = "wek_of_year";
//	private static final String KEY_DAY = "day";
//	private static final String KEY_HOUR = "hour";
	private static final String KEY_MINUTES = "minutes";
	private static final String KEY_DATE = "key_date";
	private static final String TAG = "DatabaseHandler";
	private static final String SQL_DATEFORMAT = "yyyy-MM-dd hh:mm:ss";
	SimpleDateFormat simpleFormat = new SimpleDateFormat(SQL_DATEFORMAT);
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
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_RECORDS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_TIMESTAMP + " LONG,"
//				+ KEY_YEAR + " INTEGER," + KEY_MONTH + " INTEGER,"
//				+ KEY_WEEK_OF_YEAR + " INTEGER," + KEY_DAY + " INTEGER,"
//				+ KEY_HOUR 
				+ " INTEGER," + KEY_MINUTES 
				+ " INTEGER," + KEY_DATE
				+ " datetime" + ")";
		Log.v(TAG, CREATE_CONTACTS_TABLE);
		db.execSQL(CREATE_CONTACTS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDS);

		// Create tables again
		onCreate(db);
	}

	// Adding new contact
	public void addRecord(Record record) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TIMESTAMP, record.getTimeStamp());
//		values.put(KEY_YEAR, record.getYear());
//		values.put(KEY_MONTH, record.getMonth());
//		values.put(KEY_WEEK_OF_YEAR, record.getWeekofYear());
//
//		values.put(KEY_DAY, record.getDay());
//		values.put(KEY_HOUR, record.getHour());
		values.put(KEY_MINUTES, record.getMinutes());
		values.put(KEY_DATE,
				simpleFormat.format(record.getCalendar().getTime()));

		// Inserting Row
		db.insert(TABLE_RECORDS, null, values);
		db.close(); // Closing database connection
	}

	// Getting single contact
	public Record getRecord(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_RECORDS, new String[] { KEY_ID,
				KEY_TIMESTAMP, KEY_MINUTES }, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		Log.v(TAG, cursor.toString());
		if (cursor != null)
			cursor.moveToFirst();

		Record record = new Record(Integer.parseInt(cursor.getString(0)),
				Long.parseLong(cursor.getString(1)), Integer.parseInt(cursor
						.getString(6)));
		// return contact
		return record;
	}

	public List<Record> getAllRecords() {
		String selectQuery = "SELECT  * FROM " + TABLE_RECORDS;
		return getRecordsByRawQuery(selectQuery);
	}

//	public List<Record> getRecordsByDay(int day, int month, int year) {
//
//		String selectQuery = "SELECT  * FROM " + TABLE_RECORDS + " WHERE "
//				+ KEY_YEAR + "=" + String.valueOf(year) + " AND " + KEY_MONTH
//				+ "=" + String.valueOf(month) + " AND " + KEY_DAY + "="
//				+ String.valueOf(day);
//
//		return getRecordsByRawQuery(selectQuery);
//
//	}

//	public List<Record> getRecordsByWeek(int weekofyear, int year) {
//
//		String selectQuery = "SELECT  * FROM " + TABLE_RECORDS + " WHERE "
//				+ KEY_YEAR + "=" + String.valueOf(year) + " AND "
//				+ KEY_WEEK_OF_YEAR + "=" + String.valueOf(weekofyear);
//		return getRecordsByRawQuery(selectQuery);
//	}
//
//	public List<Record> getRecordsByMonth(int month, int year) {
//		String selectQuery = "SELECT  * FROM " + TABLE_RECORDS + " WHERE "
//				+ KEY_YEAR + "=" + String.valueOf(year) + " AND " + KEY_MONTH
//				+ "=" + String.valueOf(month);
//		return getRecordsByRawQuery(selectQuery);
//	}
//
//	public List<Record> getRecordsByYear(int year) {
//		String selectQuery = "SELECT  * FROM " + TABLE_RECORDS + " WHERE "
//				+ KEY_YEAR + "=" + String.valueOf(year);
//		return getRecordsByRawQuery(selectQuery);
//	}

	// return 12 month sum
	public List<Record> getSumOfRecordsByYear(int whichyear) {
		List<Record> recordList = new ArrayList<Record>();
		String selectQuery = "SELECT SUM(" + KEY_MINUTES + ") " + " , "
				+ KEY_DATE + " , strftime('%m', " + KEY_DATE + ") as month"
				+ " FROM " + TABLE_RECORDS + " GROUP BY strftime('%m', "
				+ KEY_DATE + ")";

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

				record.setMinutes(cursor.getInt(0));
				Log.v(TAG, "getSumOfRecordsByMonth Minutes " + cursor.getInt(0));
				Log.v(TAG, "getSumOfRecordsByMonth date " + cursor.getString(1));
				Log.v(TAG, "getSumOfRecordsByMonth month " + cursor.getInt(2));
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
				+ String.format(sql_num_format, whichweek) + "'" + " AND year='"
				+ String.format(sql_num_format, whichyear) + "'"
				+ " GROUP BY strftime('%j', " + KEY_DATE + ")";

		Log.v(TAG, selectQuery);
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
				record.setMinutes(cursor.getInt(0));

				recordList.add(record);
			} while (cursor.moveToNext());
		}

		return recordList;
	}

	// return 28-31 day sum
	public List<Record> getSumOfRecordsByMonth(int whichmonth, int whichyear) {
		List<Record> recordList = new ArrayList<Record>();
		
		String selectQuery = "SELECT SUM(" + KEY_MINUTES + ")" + ", "
				+ KEY_DATE + ",strftime('%Y'," + KEY_DATE + ") as year"
				+ ",strftime('%m'," + KEY_DATE + ") as month"
				+ " FROM " + TABLE_RECORDS 
				+ " WHERE year='" + String.format(sql_num_format, whichyear) + "'" 
				+ " AND month='" + String.format(sql_num_format, whichmonth) + "'"
				+ " GROUP BY strftime('%j', " + KEY_DATE + ")";

		Log.v(TAG, selectQuery);
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
				record.setMinutes(cursor.getInt(0));
				Log.v(TAG,
						"getSumOfRecordsByMonth sum minute" + cursor.getInt(0));
				Log.v(TAG, "getSumOfRecordsByMonth  year" + cursor.getString(1));
				Log.v(TAG, "getSumOfRecordsByMonth  month" + cursor.getInt(2));
//				Log.v(TAG, "getSumOfRecordsByMonth  day" + cursor.getInt(4));

				recordList.add(record);
			} while (cursor.moveToNext());
		}

		return recordList;
	}

	public List<Record> getSumOfRecordsByDay(Calendar whichdate) {
		List<Record> recordList = new ArrayList<Record>();
		String SQL_DATE_ONLY_FORMAT = "yyyy-MM-dd";
		SimpleDateFormat dateOnlyFormat = new SimpleDateFormat(SQL_DATE_ONLY_FORMAT);
		
		String selectQuery = "SELECT SUM(" + KEY_MINUTES + ")" 
				+ " , "+ KEY_DATE 
				+ " , strftime('%H'," + KEY_DATE + ") as hour"
				+ " FROM " + TABLE_RECORDS 
				+ " WHERE strftime('%Y-%m-%d', " +KEY_DATE+ ") ='" + dateOnlyFormat.format(whichdate.getTime()) + "'"
				+ " GROUP BY strftime('%H', " + KEY_DATE + ")";

		Log.v(TAG, selectQuery);
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
				record.setMinutes(cursor.getInt(0));
				Log.v(TAG,
						"getSumOfRecordsByMonth sum minute" + cursor.getInt(0));
				Log.v(TAG, "getSumOfRecordsByMonth  date" + cursor.getString(1));
				Log.v(TAG, "getSumOfRecordsByMonth  hour" + cursor.getInt(2));

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

				Record contact = new Record();
				contact.setID(Integer.parseInt(cursor.getString(0)));
				contact.setDate(Long.parseLong(cursor.getString(1)));
				contact.setMinutes(Integer.parseInt(cursor.getString(6)));
				recordList.add(contact);
			} while (cursor.moveToNext());
		}
		return recordList;
	}

	// Getting contacts Count
	public int getRecordsCount() {
		String countQuery = "SELECT  * FROM " + TABLE_RECORDS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();

		// return count
		return cursor.getCount();
	}

	// Updating single contact
	public int updateContact(Record record) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TIMESTAMP, record.getTimeStamp());
//		values.put(KEY_YEAR, record.getYear());
//		values.put(KEY_MONTH, record.getMonth());
//		values.put(KEY_WEEK_OF_YEAR, record.getWeekofYear());
//		values.put(KEY_DAY, record.getDay());
//		values.put(KEY_HOUR, record.getHour());
		values.put(KEY_MINUTES, record.getMinutes());
		values.put(KEY_DATE, simpleFormat.format(record.getCalendar().getTime()));

		// updating row
		return db.update(TABLE_RECORDS, values, KEY_ID + " = ?",
				new String[] { String.valueOf(record.getID()) });
	}

	// Deleting single contact
	public void deleteContact(Record record) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_RECORDS, KEY_ID + " = ?",
				new String[] { String.valueOf(record.getID()) });
		db.close();
	}

}
