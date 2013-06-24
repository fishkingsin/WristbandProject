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

public class SleepPatternDatabaseHandler extends SQLiteOpenHelper {
	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "recordManager";

	// Record table name
	String TABLE_RECORDS = "sleep_pattern";

	// Record Table Columns names

	private static final String KEY_TIMESTAMP = "timestamp";
	private static final String KEY_PATTERN = "pattern";

	private static final String TAG = "DatabaseHandler";

	public SleepPatternDatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public SleepPatternDatabaseHandler(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, null, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_RECORDS + "("
				+ KEY_TIMESTAMP + " LONG PRIMARY KEY," + KEY_PATTERN
				+ " INTEGER," + ")";

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
	public void addRecord(SleepPattern record) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TIMESTAMP, record.getTimeStamp());
		values.put(KEY_PATTERN, record.getPattern());

		// Inserting Row
		db.insert(TABLE_RECORDS, null, values);
		db.close(); // Closing database connection
	}

	// Getting single contact
	public SleepPattern getSleepPattern(long millis) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db
				.query(TABLE_RECORDS, new String[] { KEY_TIMESTAMP,
						KEY_TIMESTAMP, KEY_PATTERN }, KEY_TIMESTAMP + "=?",
						new String[] { String.valueOf(millis) }, null, null,
						null, null);
		if (cursor != null)
			cursor.moveToFirst();

		SleepPattern record = new SleepPattern(Long.parseLong(cursor
				.getString(0)), Integer.parseInt(cursor.getString(1)));
		// return contact
		return record;
	}

	public List<SleepPattern> getAllSleepPatterns() {
		List<SleepPattern> recordList = new ArrayList<SleepPattern>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_RECORDS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				SleepPattern contact = new SleepPattern();
				contact.setTimeStamp(Long.parseLong(cursor.getString(0)));
				contact.setPattern(Integer.parseInt(cursor.getString(1)));

				// Adding contact to list
				recordList.add(contact);
			} while (cursor.moveToNext());
		}

		// return contact list
		return recordList;
	}

	public List<SleepPattern> getSleepPatternsByMonth(int startMonth,
			int endMonth) {
		List<SleepPattern> recordList = new ArrayList<SleepPattern>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_RECORDS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		Log.v(TAG, cursor.toString());
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				SleepPattern contact = new SleepPattern();
				contact.setTimeStamp(Long.parseLong(cursor.getString(0)));
				contact.setPattern(Integer.parseInt(cursor.getString(1)));

				// Adding contact to list
				recordList.add(contact);
			} while (cursor.moveToNext());
		}

		// return contact list
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
	public int updateContact(SleepPattern record) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TIMESTAMP, record.getTimeStamp());
		values.put(KEY_PATTERN, record.getPattern());

		// updating row
		return db.update(TABLE_RECORDS, values, KEY_TIMESTAMP + " = ?",
				new String[] { String.valueOf(record.getTimeStamp()) });
	}

	// Deleting single contact
	public void deleteContact(SleepPattern record) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_RECORDS, KEY_TIMESTAMP + " = ?",
				new String[] { String.valueOf(record.getTimeStamp()) });
		db.close();
	}

}
