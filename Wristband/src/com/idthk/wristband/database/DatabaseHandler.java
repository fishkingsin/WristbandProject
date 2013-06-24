package com.idthk.wristband.database;

import java.util.ArrayList;
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
	private static final String KEY_YEAR = "year";
	private static final String KEY_MONTH = "month";
	private static final String KEY_WEEK_OF_YEAR = "wek_of_year";
	private static final String KEY_DAY = "day";
	private static final String KEY_HOUR = "hour";
	private static final String KEY_MINUTES = "minutes";

	private static final String TAG = "DatabaseHandler";
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
				+ KEY_ID + " INTEGER PRIMARY KEY," 
				+ KEY_TIMESTAMP + " LONG,"
				+ KEY_YEAR + " INTEGER,"
				+ KEY_MONTH + " INTEGER,"
				+ KEY_WEEK_OF_YEAR + " INTEGER,"
				
				+ KEY_DAY + " INTEGER,"
				
				+ KEY_HOUR + " INTEGER,"
				+ KEY_MINUTES + " INTEGER" + ")";
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
		values.put(KEY_YEAR, record.getYear());
		values.put(KEY_MONTH, record.getMonth());
		values.put(KEY_WEEK_OF_YEAR, record.getWeekofYear() );
		
		values.put(KEY_DAY, record.getDay());
		values.put(KEY_HOUR, record.getHour());
		values.put(KEY_MINUTES, record.getMinutes());

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
		if (cursor != null)
			cursor.moveToFirst();

		Record record = new Record(Integer.parseInt(cursor.getString(0)),
				Long.parseLong(cursor.getString(1)), Integer.parseInt(cursor
						.getString(6)));
		// return contact
		return record;
	}

	public List<Record> getAllRecords() {
		List<Record> recordList = new ArrayList<Record>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_RECORDS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Record contact = new Record();
				contact.setID(Integer.parseInt(cursor.getString(0)));
				contact.setDate(Long.parseLong(cursor.getString(1)));
				contact.setMinutes(Integer.parseInt(cursor.getString(6)));

				// Adding contact to list
				recordList.add(contact);
			} while (cursor.moveToNext());
		}

		// return contact list
		return recordList;
	}
	
	public List<Record> getRecordsByMonth(int startMonth , int endMonth) {
		List<Record> recordList = new ArrayList<Record>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_RECORDS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		Log.v(TAG,cursor.toString());
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Record contact = new Record();
				contact.setID(Integer.parseInt(cursor.getString(0)));
				contact.setDate(Long.parseLong(cursor.getString(1)));
				contact.setMinutes(Integer.parseInt(cursor.getString(6)));

				// Adding contact to list
				recordList.add(contact);
			} while (cursor.moveToNext());
		}

		// return contact list
		return recordList;
	}
	public List<Record> getRecordsByDay(int day) {
		List<Record> recordList = new ArrayList<Record>();
		return recordList;
	}
	public List<Record> getRecordsByWeek(int weekofyear) {
		List<Record> recordList = new ArrayList<Record>();
		return recordList;
	}
	public List<Record> getRecordsByYear(int year) {
		List<Record> recordList = new ArrayList<Record>();
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
		values.put(KEY_YEAR, record.getYear());
		values.put(KEY_MONTH, record.getMonth());
		values.put(KEY_WEEK_OF_YEAR, record.getWeekofYear() );
		values.put(KEY_DAY, record.getDay());
		values.put(KEY_HOUR, record.getHour());
		values.put(KEY_MINUTES, record.getMinutes());
		
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
