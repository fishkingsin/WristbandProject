package com.idthk.wristband.ui;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.bostonandroid.datepreference.DatePreference;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.idthk.wristband.api.BLEBaseFragmentActivity;
import com.idthk.wristband.ui.Utilities;
import com.idthk.wristband.database.DatabaseHandler;
import com.idthk.wristband.database.Record;
import com.idthk.wristband.database.SleepPattern;
import com.idthk.wristband.database.SleepRecord;
import com.idthk.wristband.socialnetwork.FacebookShareActivity;
import com.idthk.wristband.socialnetwork.TwitterShareActivity;
import com.idthk.wristband.ui.MainFragment.OnShareButtonClickedListener;
import com.idthk.wristband.ui.landscape.ActivityLandscapeActivity;
import com.idthk.wristband.ui.landscape.LandscapeActivity;
import com.idthk.wristband.ui.landscape.SleepLandscapeActivity;
import com.idthk.wristband.ui.landscape.StatisticLandscapeActivity;
import com.idthk.wristband.ui.preference.TimePreference;
//import android.content.res.Configuration;
//import android.hardware.SensorManager;
//import android.widget.Toast;

public class Main extends BLEBaseFragmentActivity implements
		MainFragmentPager.PagerChangedCallback,
		StatisticFragmentPager.StatisticPagerChangedCallback,
		MainFragment.OnShareButtonClickedListener,
		TabsFragment.OnFragmentTabbedListener,
		ActivityStatisticTabFragment.OnFragmentTabbedListener,
		SleepStatisticTabFragment.OnFragmentTabbedListener,
		SharedPreferences.OnSharedPreferenceChangeListener {
	class WristbandStartupConstant {
		static final int DISCONNECT = 0x210;
		static final int CONNECT = 0x211;
		static final int SYNC_USER_PROFILE = 0x212;
		static final int SYNC_DAILY_TARGET = 0x213;
		static final int SYNC_WAKE_UP_TIME = 0x214;
		static final int SYNC_TIME = 0x215;
		static final int GET_SOFTWARE_VERSION = 0x216;
		static final int GET_HISTORY_DATA = 0x217;
		static final int START_STREAM = 0x218;

	}

	static final int TO_INSTRUCTION_REQUEST = 0x10;
	static final int TO_USER_PROFILE_REQUEST = 0x20;
	static final int LANSCAPE_REQUEST = 0x30;
	static final int FACEBOOK_REQUEST = 0x40;
	static final int TWITTER_REQUEST = 0x50;
	static final int IMAGE_GALLERY_REQUEST = 0x60;
	static final int TAKE_PHOTO_CODE = 0x70;
	static final int SELECT_IMAGE_CODE = 0x90;
	public static final int USER_PREFERENCES_REQUEST = 0x100;
	static final int THRESHOLD = 5;
	static final String TAG = "Main";

	static final String FIRST_TIME = "firsttime";

	public static final String TITLE = "title";
	public static final String TARGET_ORIENTTION = "target_orientation";

	

	int mStartUpState = WristbandStartupConstant.DISCONNECT;

	MainFragment mFrag = null;
	TabHost mTabHost = null;
	OrientationEventListener orientationListener;
	OnShareButtonClickedListener mShareButtonClickedListener;

	String mCurrentView = "Activity";
	String mStatisticType = "";

	Context mContext;
	Integer connectivity_images[] = { R.drawable.wireless_connection_icon_0,
			R.drawable.wireless_connection_icon_1,
			R.drawable.wireless_connection_icon_2,
			R.drawable.wireless_connection_icon_3,
			R.drawable.wireless_connection_icon_4 };

	private CountDownTimer mCountDownTimer;

	private ProgressDialog pd;

	private boolean firstTime;
	private boolean isInLandscapeActivity;
	private boolean inBackground;
	private boolean isInPreferenceActivity;
	private boolean canRotateView;

	
	private SleepRecord sleepRecord = null;

	final static private long ONE_SECOND = 1000;
	final static private long TWENTY_SECONDS = ONE_SECOND * 20;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


		mContext = this;

		pd = new ProgressDialog(mContext);
		pd.setTitle("Processing...");
		pd.setMessage("Please wait.");
		pd.setCancelable(false);
		pd.setIndeterminate(true);

		setContentView(R.layout.main);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);
		firstTime = prefs.getBoolean(FIRST_TIME, true);
		canRotateView = prefs.getBoolean(
				getString(R.string.pref_enable_rotation_view), false);
		((ImageView) findViewById(R.id.connectivity))
				.setImageResource(R.drawable.wireless_connection_icon_0);
		if (firstTime) {

			class CreateDBTask extends AsyncTask<Void, Integer, Void> {

				@Override
				protected void onPreExecute() {

					pd.setTitle("Create Sample DB...");
					pd.setMessage("Please wait.");
					pd.show();
				}

				@Override
				protected Void doInBackground(Void... params) {
					
					createDBTable(TABLE_CONTENT);

					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					pd.dismiss();
				}

			}
			;
			new CreateDBTask().execute();
			Intent intent = new Intent(this, InstructionActivity.class);
			intent.putExtra(ScreenSlidePageFragment.ARG_FIRSTTIME, firstTime);
			startActivityForResult(intent, TO_INSTRUCTION_REQUEST);

			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean(FIRST_TIME, false);

			// Commit the edits!
			editor.commit();
		} else {
			
			orientationListener = new OrientationEventListener(this,
					SensorManager.SENSOR_DELAY_UI) {

				@Override
				public void onOrientationChanged(int orientation) {
					// TODO Auto-generated method stub
					if (canShow(orientation) && canRotateView) {
						startLandscapeActivity(orientation);

					}

				}

			};
		}
		DatabaseHandler db = new DatabaseHandler(this,
				Main.TABLE_CONTENT, null, 1);

		List<String> cal = db.getRecordRange();
		if (cal.size() > 0) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			try {
				Utilities.getLog(TAG,
						"First Date "+sdf.parse(cal.get(0))
						+"\nLastDate"+sdf.parse(cal.get(1)));
				Calendar _cal = Calendar.getInstance(); 
				_cal.setTime(sdf.parse(cal.get(0)));
				Utilities.setFirstdate(_cal);
				_cal.setTime(sdf.parse(cal.get(1)));
				Utilities.setLastdate(_cal);
				Utilities.setTargetdate(Utilities.lastDate());
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	

	private void createDBTable(String string) {
		DatabaseHandler db = new DatabaseHandler(this, string, null, 1);
		// create sample data
		/**
		 * CRUD Operations
		 * */
		// Inserting Contacts
		Log.d("Insert: ", "Inserting ..");
		Calendar cal = Calendar.getInstance();
		Random random = new Random();
		
		
		int startYear = 2011;
		int endYear = 2012;
		int _startMonth = 7;
		int _endMonth = 6;
		Utilities.firstDate().set(startYear,_startMonth,1);
		cal.setTime(Utilities.firstDate().getTime());
		
		SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		//SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		for (int year = startYear; year <= endYear; year++) {
			int startMonth = (year==2011)?10:1;
			int endMonth = (year==2011)?12:3;
			cal.set(Calendar.YEAR, year);
			for (int month = startMonth; month <= endMonth; month++) {
				int targetDay = 31;
				switch (targetDay) {
				case 2:
					targetDay = (leapyear(year)!=-1)?29:28;
					break;
				case 4:
				case 6:
				case 9:
				case 11:
					targetDay = 30;
					break;
				}
				cal.set(Calendar.MONTH, month);
				for (int day = 0; day < targetDay; day++) {
					int startHour = random.nextInt(4) + 8;
					int endHour = 20 - random.nextInt(4);
					if (string.equals("sleep_table")) {
						startHour = endHour;
						endHour = startHour + 24;
					}
					cal.set(Calendar.DAY_OF_MONTH, day);
					
					for (int hour = startHour; hour < endHour; hour++) {
						int _hour = hour%24 ;
						
						
						cal.set(Calendar.HOUR_OF_DAY, _hour);
						db.addRecord(new Record(cal.getTimeInMillis(), random
								.nextInt(60)));
						
					}
				}

			}

		}
		Utilities.lastDate().setTime(cal.getTime());
		Utilities.targetDate().setTime(Utilities.lastDate().getTime());

	}
	int leapyear (int yr)
	{
		int leap = 0;
		int notLeap = -1;

		
		
			if ((yr % 4 == 0) && !(yr % 100 == 0)|| (yr % 400 == 0))
				leap = yr;
			else
				leap = notLeap;

		
		return leap;

	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void prepareToConnect() {
		Log.v(TAG, "prepareToConnect");
		if (!firstTime) {

			connect();

		}

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.v(TAG, "requestCode " + requestCode + " resultCode " + resultCode);
		if (requestCode == USER_PREFERENCES_REQUEST) {
			isInPreferenceActivity = false;
			if (mTabHost != null) {
				mTabHost.setCurrentTab(0);
			}
		} else if (requestCode == TO_INSTRUCTION_REQUEST) {
			connect();


		} else if (requestCode == LANSCAPE_REQUEST) {
			isInLandscapeActivity = false;
			if (data != null) {
				Bundle conData = data.getExtras();
				if (conData.get(LandscapeActivity.LANDSCAPE_ACTIVITY_TAG)
						.equals(LandscapeActivity.FINISH_APP)) {
					finish();

				}
			}

		}
	}

	private void startLandscapeActivity(int orientation) {
		isInLandscapeActivity = true;

		Intent intent = null;
		Bundle bundle = new Bundle();
		if (mCurrentView.equals("Activity")) {
			intent = new Intent(this, ActivityLandscapeActivity.class);

		} else if (mCurrentView.equals("Sleep")) {
			intent = new Intent(this, SleepLandscapeActivity.class);

		} else if (mCurrentView.equals("Activity Level")
				|| mCurrentView.equals("Sleep Level")) {
			
			intent = new Intent(this, StatisticLandscapeActivity.class);
			if (!mStatisticType.equals("")) {
				bundle.putString(StatisticLandscapeActivity.TYPE, mStatisticType);
			}

		}
		if (intent != null) {
			if (isLandscapeLeft(orientation)) {
				intent.putExtra(Main.TARGET_ORIENTTION,
						ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			} else {
				intent.putExtra(Main.TARGET_ORIENTTION,
						ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
			}

			intent.putExtra(LandscapeActivity.LANDSCAPE_BUNDLE, bundle);
			startActivityForResult(intent, LANSCAPE_REQUEST);
		}
	}

	private boolean isLandscapeRight(int orientation) {
		return orientation >= (90 - THRESHOLD)
				&& orientation <= (90 + THRESHOLD);
	}

	private boolean isLandscapeLeft(int orientation) {
		return orientation >= (270 - THRESHOLD)
				&& orientation <= (270 + THRESHOLD);
	}

	private boolean isPortrait(int orientation) {
		return (orientation >= (360 - THRESHOLD) && orientation <= 360)
				|| (orientation >= 0 && orientation <= THRESHOLD);
	}

	public boolean canShow(int orientation) {
		return isLandscapeLeft(orientation) || isLandscapeRight(orientation);
	}

	public void initUI() {
		Log.v(TAG, "initUI");
	}

	@Override
	public void onShareButtonClicked(String s) {
		// TODO Auto-generated method stub
		if (s.equals(MainFragment.FACEBOOK)) {
			Intent intent = new Intent(this, FacebookShareActivity.class);
			intent.putExtra(MainFragment.FACEBOOK,
					"I'm going for my daily goal");
			intent.putExtra(TITLE, MainFragment.FACEBOOK);
			startActivityForResult(intent, FACEBOOK_REQUEST);
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);

		} else if (s.equals(MainFragment.TWITTER)) {
			Intent intent = new Intent(this, TwitterShareActivity.class);

			intent.putExtra(MainFragment.TWITTER, "I'm going for my daily goal");
			intent.putExtra(TITLE, MainFragment.TWITTER);
			startActivityForResult(intent, TWITTER_REQUEST);
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
		}

	}

	@Override
	public void onTabbed(String s) {
		// TODO Auto-generated method stub
		((TextView) findViewById(R.id.titlebar_textview)).setText(s);
		if (TabsFragment.TAB_SETTINGS.equals(s)) {
			showSetting();

		} else {
			mCurrentView = s;
		}

	}

	@Override
	public void dispatchTabhost(TabHost tabHost) {
		mTabHost = tabHost;
	}

	private void showSetting() {
		// TODO Auto-generated method stub

		Intent intent = new Intent(this, PreferencesActivity.class);
		intent.putExtra(MainFragment.FACEBOOK, "I'm going for my daily goal");
		isInPreferenceActivity = true;
		startActivityForResult(intent, Main.USER_PREFERENCES_REQUEST);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (orientationListener != null)
			orientationListener.enable();
		if (inBackground) {
			// You just came from the background
			inBackground = false;

			if (mCountDownTimer != null)
				mCountDownTimer.cancel();
			Log.v(TAG, "I think i am coming back from background");
		} else {
			// You just returned from another activity within your own app
			Log.v(TAG, "returned from another activity within your own app");
		}
	}

	@Override
	public void onDestroy() {

		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
		super.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (orientationListener != null)
			orientationListener.disable();

		if (inBackground) {

			mCountDownTimer = new CountDownTimer(1000 * 60 * 5, 1000) {

				public void onFinish() {
					Log.v(TAG, "5 mins pass finish app");
					disconnect();
				}

				@Override
				public void onTick(long millisUntilFinished) {
					// TODO Auto-generated method stub

				}
			}.start();
			Log.v(TAG, "I think i am going into background");
		} else {

		}
	}

	private class UpdateConnectivityTask extends AsyncTask<Void, Integer, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(Void... params) {

			Log.v("UpdateBarTask", "doInBackground");

			while (getState() == STATE_READY) {
				for (int i = 1; i < 3; i++) {

					try {
						publishProgress(i);
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			if (getState() == BLE_PROFILE_CONNECTED) {
				publishProgress(connectivity_images.length - 2);
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			((ImageView) findViewById(R.id.connectivity))
					.setImageResource(connectivity_images[values[0]]);
		}
	}

	@Override
	public void onDeviceFound() {

	}

	@Override
	public void onStatisticPagerChangedCallback(int page) {
		Log.v(TAG, "onStatisticPagerChangedCallback " + page);
		if (findViewById(R.id.titlebar_textview) != null) {
			// TODO Auto-generated method stub
			if (page == 0) {
				((TextView) findViewById(R.id.titlebar_textview))
						.setText("Activity Level");
				((Button) findViewById(R.id.btn_settings_done))
						.setVisibility(View.GONE);
				mCurrentView = "Activity Level";
			}

			else if (page == 1) {
				((TextView) findViewById(R.id.titlebar_textview))
						.setText("Sleep Level");
				((Button) findViewById(R.id.btn_settings_done))
						.setVisibility(View.GONE);
				mCurrentView = "Sleep Level";
			}
		}
	}

	private void showMessage(String msg) {

		Log.v(TAG, msg);

	}

	private void setUiState() {
		switch (getState()) {
		case BLE_PROFILE_CONNECTED:
			showMessage("STATE_CONNECTED::device name"
					+ mService.mDevice.getName());

			break;
		case BLE_PROFILE_DISCONNECTED:
			showMessage("disconnected");
			break;
		case STATE_READY:
			showMessage("ready");
			break;
		default:
			showMessage("wrong mState");
			break;
		}
	}

	@Override
	public void onConnected() {
		super.onConnected();
		showMessage("Connected");
		setUiState();
		((ImageView) findViewById(R.id.connectivity))
				.setImageResource(connectivity_images[1]);
	}

	@Override
	public void onError(int errorType) {
		pd.dismiss();
	}

	@Override
	public void onDisconnected() {
		super.onDisconnected();
		if (inBackground) {
			finish();
			return;
		}
		
		showMessage("Disconnected");
		setUiState();
		((ImageView) findViewById(R.id.connectivity))
				.setImageResource(connectivity_images[0]);
		
		if (!isInLandscapeActivity && !isInPreferenceActivity) {
			connect();
		}
		
	}

	@Override
	public void connect() {
		super.connect();
	}

	@Override
	public void onReady() {
		super.onReady();
		setUiState();
		showMessage("BLE Ready");
		
	}

	@Override
	public void onServiceDiscovered() {

		super.onServiceDiscovered();
		setUiState();
		showMessage("Service Discovered");

		((ImageView) findViewById(R.id.connectivity))
				.setImageResource(connectivity_images[connectivity_images.length - 1]);

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		Log.v(TAG, "Set " + getString(R.string.pref_last_sync_time) + " "
				+ Utilities.getCurrentDate());
		editor.putString(getString(R.string.pref_last_sync_time),
				Utilities.getCurrentDate());
		editor.commit();
		mStartUpState = WristbandStartupConstant.CONNECT;

		
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getSerial();

	}

	/*
	 * private class WristbandTask extends AsyncTask<Void, Integer, Void> {
	 * 
	 * // private ProgressDialog pd;
	 * 
	 * @Override protected void onPreExecute() { // pd = new
	 * ProgressDialog(mContext); // pd.setTitle("Processing..."); //
	 * pd.setMessage("Please wait."); // pd.setCancelable(false); //
	 * pd.setIndeterminate(true); // pd.show(); }
	 * 
	 * @Override protected Void doInBackground(Void... params) {
	 * 
	 * try {
	 * 
	 * Thread.sleep(2000); getSerial(); Thread.sleep(2000);
	 * checkState(mStartUpState); } catch (Exception e) { Log.v(TAG,
	 * e.getMessage()); }
	 * 
	 * return null; }
	 * 
	 * @Override protected void onProgressUpdate(Integer... values) { // if
	 * (isCancelled()) { // pd.dismiss(); // } }
	 * 
	 * @Override protected void onPostExecute(Void result) { // pd.dismiss(); }
	 * }
	 */

	@Override
	public void onStreamMessage(int steps, int calories, float distance,
			int activityTime, int batteryLevel) {
		// TODO Auto-generated method stub
		String s = "";
		s += "Wristband Stream :\n";
		s += "steps : " + steps + "\n";
		s += "calories : " + calories + "\n";
		s += "distance : " + distance + "\n";
		s += "activityTime : " + activityTime + "\n";
		s += "batteryLevel : " + batteryLevel + "\n";
		
		try {

			mFrag.onStreamMessage(steps, calories, distance, activityTime,
					batteryLevel);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "mFrag is null");
			// e.printStackTrace();
		}
		super.onStreamMessage(steps, calories, distance, activityTime,
				batteryLevel);
	}

	@Override
	public void onReadTime(int year, int month, int day, int hour, int minute,
			int second, int weekday) {
		String s = "";

		s += "Wristband Set Time :\n";
		s += "YEAR : " + year + "\n";
		s += "MONTH : " + month + "\n";
		s += "DAY : " + day + "\n";
		s += "HOUR : " + hour + "\n";
		s += "MINUTE : " + minute + "\n";
		s += "SECOND : " + second + "\n";
		s += "WEEKDAY : " + weekday + "\n";
		showMessage(s);
		mStartUpState = WristbandStartupConstant.SYNC_TIME;
		checkState(mStartUpState);
		super.onReadTime(year, month, day, hour, minute, second, weekday);
	}

	@Override
	public void onReadProfile(int gender, int year, byte month, int weight,
			int height) {
		super.onReadProfile(gender, year, month, weight, height);
		// TODO Auto-generated method stub
		String s = "";
		s += "Wristband Set Profile :\n";
		s += "GENDER : " + ((gender == 0) ? "Male" : "Female") + "\n";
		s += "YEAR : " + (year) + "\n";
		s += "MONTH : " + (month) + "\n";
		s += "WEIGHT : " + (weight) + "\n";
		s += "HEIGHT : " + (height);
		showMessage(s);
		mStartUpState = WristbandStartupConstant.SYNC_USER_PROFILE;
		checkState(mStartUpState);

	}

	@Override
	public void onReadSleep(int weekday_hour, int weekday_minute,
			int weekend_hour, int weekend_minute, int toggle) {
		// TODO Auto-generated method stub
		String s = "";
		s += "Wristband Set Sleep :\n";

		s += "WEEKDAY HOUR : " + weekday_hour + "\n";
		s += "WEEKDAY MINUTE : " + weekday_minute + "\n";
		s += "WEEKEND HOUR : " + weekend_hour + "\n";
		s += "WEEKEND MINUTE : " + weekend_minute + "\n";
		switch (toggle) {
		case 0:
			s += "TOGGLE : ALL OFF\n";
			break;
		case 1:
			s += "TOGGLE : WEEKDAY ON\n";
			break;
		case 2:
			s += "TOGGLE : WEEKEND ON\n";
			break;
		case 3:
			s += "TOGGLE : ALL ON\n";
			break;
		default:
			s += "TOGGLE : " + toggle + "\n";
			break;
		}
		showMessage(s);
		mStartUpState = WristbandStartupConstant.SYNC_WAKE_UP_TIME;
		checkState(mStartUpState);
		super.onReadSleep(weekday_hour, weekday_minute, weekend_hour,
				weekend_minute, toggle);
	}

	@Override
	public void onReadTarget(int duration, int toggle, long step, int distance,
			int calories) {
		// TODO Auto-generated method stub
		String s = "";
		s += "Wristband Set Target :\n";

		s += "Activity Value " + duration + "\n";
		s += "Toggle Gauge " + toggle + "\n";
		s += "Step " + step + "\n";
		s += "Distance " + distance + "km \n";
		s += "Calories " + calories + "kcal \n";
		showMessage(s);
		mStartUpState = WristbandStartupConstant.SYNC_DAILY_TARGET;
		checkState(mStartUpState);
		super.onReadTarget(duration, toggle, step, distance, calories);
	}

	@Override
	public void onReadUnknownProtocol(byte[] value) {
		// TODO Auto-generated method stub
		String s = "";
		try {
			s = new String(value, "UTF-8");

		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String _msg = "";
		for (int i = 0; i < value.length; i++) {
			_msg += value[i] + " , ";
		}
		showMessage("Unknown Message");
		showMessage(s);
		showMessage(_msg);
		

	}

	@Override
	public void onReadVersion(int xx, int yy) {
		String s = xx + "." + yy;
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(getString(R.string.Wristband_Version), s);

		// Commit the edits!
		editor.commit();
		showMessage("ReadVersion " + s);
		mStartUpState = WristbandStartupConstant.GET_SOFTWARE_VERSION;
		checkState(mStartUpState);
		super.onReadVersion(xx, yy);
	}

	@Override
	public void onReadSerial(byte serial[]) {
		Charset charset = Charset.forName("UTF-8");
		CharSequence seq2 = new String(serial, charset);
		Log.v(TAG, "Serial : " + seq2);
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(getString(R.string.pref_serial), String.valueOf(seq2));
		// Commit the edits!
		editor.commit();

		super.onReadSerial(serial);

		mStartUpState = WristbandStartupConstant.CONNECT;
		checkState(mStartUpState);
	}

	@Override
	public void onReadActivityHistoryData(List<Record> pedometerData) {

//		mStartUpState = WristbandStartupConstant.GET_HISTORY_DATA;
//		checkState(mStartUpState);
		super.onReadActivityHistoryData(pedometerData);
		DatabaseHandler db = new DatabaseHandler(this,
				Main.TABLE_CONTENT, null, 1);
		for (Record record : pedometerData) {
			if(db.updateRecord(record)==0)
			{
				db.addRecord(record);
			}
			
		}
	}
	@Override
	public void onReadSleepHistoryData(SleepRecord data) {
		super.onReadSleepHistoryData(data);
		
//		boolean newRecordFound = false;
		DatabaseHandler db = new DatabaseHandler(this, TABLE_CONTENT, null, 1);
		List<SleepRecord>record =  db.getSleepRecord(data.getGoToBedTime().getTime());
		if(record.size()>=1)
		{
			 // Update
			SleepRecord sleepData = record.get(0);
			if (data.getActualSleepTime() > sleepData.getActualSleepTime()) {
	            sleepData.setActualSleepTime(data.getActualSleepTime());
	            sleepData.setActualWakeupTime(data.getActualWakeupTime());
	            sleepData.setFallingAsleepDuration(data.getFallingAsleepDuration());
	            sleepData.setGoToBedTime(data.getGoToBedTime());
	            sleepData.setInBedTime(data.getInBedTime());
	            sleepData.setNumberOfTimesWaken(data.getNumberOfTimesWaken());
	            sleepData.setPresetWakeupTime(data.getPresetWakeupTime());
	            sleepData.setSleepEfficiency(data.getSleepEfficiency());
	            sleepData.setPatterns(data.getPatterns());
//	            newRecordFound = true;
	            db.updateSleepRecord(sleepData);
	        }
		}else
		{
			db.addSleepRecord(data);
		}
		
		List <SleepRecord> sleepRrecords = db.getLastSleepRecord();
		if(sleepRrecords.size()==1)
		{
			SharedPreferences sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putBoolean(FIRST_TIME, false);
			
			editor.putInt(getString(R.string.keyActualSleepTime),sleepRrecords.get(0).getActualSleepTime());
			editor.putInt(getString(R.string.keyTimeFallAsSleep),sleepRrecords.get(0).getFallingAsleepDuration());
			editor.commit();
		}
		mStartUpState = WristbandStartupConstant.GET_HISTORY_DATA;
		checkState(mStartUpState);

	}

	public static boolean isApplicationBroughtToBackground(
			final Activity activity) {
		ActivityManager activityManager = (ActivityManager) activity
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> tasks = activityManager
				.getRunningTasks(1);

		// Check the top Activity against the list of Activities contained in
		// the Application's package.
		if (!tasks.isEmpty()) {
			ComponentName topActivity = tasks.get(0).topActivity;
			try {
				PackageInfo pi = activity.getPackageManager().getPackageInfo(
						activity.getPackageName(),
						PackageManager.GET_ACTIVITIES);
				for (ActivityInfo activityInfo : pi.activities) {
					if (topActivity.getClassName().equals(activityInfo.name)) {
						return false;
					}
				}
			} catch (PackageManager.NameNotFoundException e) {
				return false; // Never happens.
			}
		}
		return true;
	}

	@Override
	public void onConnectionTimeout() {
		if (!this.isDestroyed()) {
			new AlertDialog.Builder(this)
					.setTitle(R.string.connection_time_out)
					.setMessage(R.string.do_you_want_to_reconnect)
					.setPositiveButton(R.string.popup_retry,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// continue with delete
									connect();
								}
							})
					.setNegativeButton(R.string.popup_quite,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// do nothing
									finish();
								}
							})
					.setNeutralButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {

								}
							}).show();
		}
		super.onConnectionTimeout();
	}

	@Override
	public void dispatchSelf(MainFragment mainSlideFragment) {
		// TODO Auto-generated method stub
		mFrag = mainSlideFragment;
	}

	private void checkState(int state) {
		switch (state) {
		case WristbandStartupConstant.DISCONNECT:
			break;
		case WristbandStartupConstant.CONNECT: {
			// try {
			Log.v(TAG, "checkState set Profile");
			try {
				SharedPreferences sharedPreferences = PreferenceManager
						.getDefaultSharedPreferences(this);

				Calendar c = DatePreference.getDateFor(sharedPreferences,
						"prefDateOfBirth");

				String gender = sharedPreferences.getString("prefUserGender",
						"Female");

				int height = sharedPreferences.getInt("prefHeight", 170);

				int weight = sharedPreferences.getInt("prefWeight", 50);

				Log.v(TAG, "set Profile");

				setProfile((gender.equals("Male")) ? 0 : 1,
						c.get(Calendar.YEAR), c.get(Calendar.MONTH), weight,
						height);
			} catch (Exception e) {
				new AlertDialog.Builder(this)
						.setTitle("Shit happen")
						.setMessage("Set Profile")
						.setNeutralButton(R.string.cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).show();
			}

		}
			break;
		case WristbandStartupConstant.SYNC_USER_PROFILE:

		{
			try {
				SharedPreferences sharedPreferences = PreferenceManager
						.getDefaultSharedPreferences(this);

				setTarget(
						Integer.valueOf(sharedPreferences.getString(
								getString(R.string.pref_targetActivity), "30")),
						(sharedPreferences.getBoolean(
								getString(R.string.pref_toggle_target), false)) ? 0
								: 1, sharedPreferences.getInt(
								getString(R.string.pref_targetSteps), 10000),
						sharedPreferences.getInt(
								getString(R.string.pref_targetDistances), 7),
						sharedPreferences.getInt(
								getString(R.string.pref_targetCalories), 1000));
			} catch (Exception e) {
				new AlertDialog.Builder(this)
						.setTitle("Shit happen")
						.setMessage("Set Target")
						.setNeutralButton(R.string.cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).show();
			}
		}
			break;
		case WristbandStartupConstant.SYNC_DAILY_TARGET:
			//daily target set going to set wake up time
			{
			try {
				SharedPreferences sharedPreferences = PreferenceManager
						.getDefaultSharedPreferences(this);

				String weekday = sharedPreferences.getString(
						getString(R.string.pref_weekday), "7:00");
				String weekend = sharedPreferences.getString(
						getString(R.string.pref_weekend), "8:00");
				Boolean do_wakeup_weekday = sharedPreferences.getBoolean(
						getString(R.string.pref_week_up_weekday), false);
				Boolean do_wakeup_weekend = sharedPreferences.getBoolean(
						getString(R.string.pref_week_up_weekend), false);
				setSleep(
						TimePreference.getHour(weekday),
						TimePreference.getMinute(weekday),
						TimePreference.getHour(weekend),
						TimePreference.getMinute(weekend),
						(do_wakeup_weekday && do_wakeup_weekend) ? 3
								: (!do_wakeup_weekday && do_wakeup_weekend) ? 2
										: (do_wakeup_weekday && !do_wakeup_weekend) ? 1
												: 0);
			} catch (Exception e) {
				new AlertDialog.Builder(this)
						.setTitle("Shit Sleep")
						.setMessage("Set Profile")
						.setNeutralButton(R.string.cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).show();
			}
		}
			break;
		case WristbandStartupConstant.SYNC_WAKE_UP_TIME: {
			//SYNC_WAKE_UP_TIME target was set going to set wake up systemtime
			try {
				Calendar c = Calendar.getInstance();
				setTime(c.get(Calendar.YEAR) - 2000, c.get(Calendar.MONTH) + 1,
						c.get(Calendar.DAY_OF_MONTH),
						c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
						c.get(Calendar.SECOND), c.get(Calendar.WEDNESDAY));
			} catch (Exception e) {
				new AlertDialog.Builder(this)
						.setTitle("Shit happen")
						.setMessage("Set Time")
						.setNeutralButton(R.string.cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).show();
			}
		}
			break;
		case WristbandStartupConstant.SYNC_TIME:

			getVersion();
			break;
		case WristbandStartupConstant.GET_SOFTWARE_VERSION:
			getHistory();
			break;
		case WristbandStartupConstant.GET_HISTORY_DATA:
			try {
				startStream();
				((ImageView) findViewById(R.id.connectivity))
						.setImageResource(connectivity_images[connectivity_images.length - 1]);
				mStartUpState = WristbandStartupConstant.START_STREAM;
				checkState(mStartUpState);
			} catch (Exception e) {
				new AlertDialog.Builder(this)
						.setTitle("Shit happen")
						.setMessage("Start Stream")
						.setNeutralButton(R.string.cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).show();
			}
			break;
		case WristbandStartupConstant.START_STREAM:
			Log.v(TAG, "Woo hooo start Streaming now");
			pd.dismiss();
			break;
		}
	}

	@Override
	public void onBackPressed() {

		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(R.string.popup_title)
				.setMessage(R.string.popup_message)
				.setPositiveButton(R.string.popup_yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								disconnect();
								finish();
							}
						}).setNegativeButton(R.string.popup_no, null).show();

	}

	@Override
	public void onUserLeaveHint() {
		if (!isInLandscapeActivity && !isInPreferenceActivity) {
			inBackground = true;
		} else {
			inBackground = false;
		}
		super.onUserLeaveHint();
	}

	@Override
	public void onPagerChangedCallback(int position) {
		Log.v(TAG, "onPagerChangedCallback " + position);
		if (findViewById(R.id.titlebar_textview) != null) {
			// TODO Auto-generated method stub
			if (position == MainFragmentPager.ACTIVITY) {
				((TextView) findViewById(R.id.titlebar_textview))
						.setText("Activity");
				((Button) findViewById(R.id.btn_settings_done))
						.setVisibility(View.GONE);
				mCurrentView = "Activity";
			} else if (position == MainFragmentPager.SLEEP) {
				((TextView) findViewById(R.id.titlebar_textview))
						.setText("Sleep");
				((Button) findViewById(R.id.btn_settings_done))
						.setVisibility(View.GONE);
				mCurrentView = "Sleep";
			}
		}

	}

	@Override
	public void onActivityStatisticTabbed(String s) {
		// TODO Auto-generated method stub
		Log.v(TAG, "onActivityStatisticTabbed " + s);
		mStatisticType = s;
	}

	@Override
	public void onSleepStatisticTabbed(String s) {
		// TODO Auto-generated method stub
		Log.v(TAG, "onSleepStatisticTabbed " + s);
		mStatisticType = s;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		Log.v(TAG, "onSharedPreferenceChanged " + key);
		// TODO Auto-generated method stub
		if (key.equals(getString(R.string.pref_enable_rotation_view))) {
			canRotateView = sharedPreferences.getBoolean(key, false);
		}

	}
}