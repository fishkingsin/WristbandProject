package com.idthk.wristband.ui;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import com.idthk.wristband.socialnetwork.FacebookShareActivity;
import com.idthk.wristband.socialnetwork.TwitterShareActivity;
import com.idthk.wristband.ui.R;
import com.idthk.wristband.ui.MainSlideFragment.OnShareButtonClickedListener;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.SensorManager;
import android.os.AsyncTask;
//import android.content.res.Configuration;
//import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

//import android.widget.Toast;
import com.idthk.wristband.api.*;

public class Main extends BLEBaseFragmentActivity implements 
		MainFragmentPager.PagerChangedCallback,
		StatisticFragmentPager.StatisticPagerChangedCallback,
		MainSlideFragment.OnShareButtonClickedListener,
		TabsFragment.OnFragmentTabbedListener {
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

	MainSlideFragment frag;
	OrientationEventListener orientationListener;
	OnShareButtonClickedListener myShareButtonClickedListener;
	private boolean firstTime;
	Integer connectivity_images[] = {R.drawable.wireless_connection_icon_0
			,R.drawable.wireless_connection_icon_1
			,R.drawable.wireless_connection_icon_2
			,R.drawable.wireless_connection_icon_3
			,R.drawable.wireless_connection_icon_4 };
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.main);
		// Intent intent = new Intent(this, BLEBaseActivity.class);
		// startActivityForResult(intent, TO_INSTRUCTION_REQUEST);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		firstTime = prefs.getBoolean(FIRST_TIME, true);
		((ImageView)findViewById(R.id.connectivity)).setImageResource(R.drawable.wireless_connection_icon_0);
		if (firstTime) {

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
					if (canShow(orientation)) {
						startLandscapeActivity(orientation);

					}

				}

			};
		}
		// Intent intent = new Intent(this, FragmentPreferences
		// .class);
		// startActivityForResult(intent,ACTIVITY_REQUEST);

		// Intent intent = new Intent(this, FacebookShareActivity.class);
		// intent.putExtra(SlidePageFragment.FACEBOOK,
		// "I'm going for my daily goal");
		// intent.putExtra(TITLE , SlidePageFragment.FACEBOOK);
		// this.startActivityForResult(intent,ACTIVITY_REQUEST);//,bundle);
		// this.startActivityForResult(new Intent(this,
		// TwitterShareActivity.class),ACTIVITY_REQUEST);
		// this.startActivity(new Intent(this, SimpleGraph.class));

	}

	@Override
	public void prepareToConnect() {
		Log.v(TAG, "prepareToConnect");
		if (!firstTime) {
			// TODO Auto-generated method stub
			if (mState == BLE_PROFILE_DISCONNECTED) {

				connect();
			}
		}
		

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.v(TAG, "requestCode " + requestCode + " resultCode " + resultCode);
		if (requestCode == USER_PREFERENCES_REQUEST) {

		} else if (requestCode == TO_INSTRUCTION_REQUEST) {
			if (mState == BLE_PROFILE_DISCONNECTED) {

				connect();
			}
		}
	}

	private void startLandscapeActivity(int orientation) {

		Intent intent = new Intent(this, LandscapeActivity.class);
		if (isLandscapeLeft(orientation)) {
			intent.putExtra(Main.TARGET_ORIENTTION,
					ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			intent.putExtra(Main.TARGET_ORIENTTION,
					ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
		}
		startActivityForResult(intent, LANSCAPE_REQUEST);
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

	// public void onConfigurationChanged(Configuration newConfig) {
	// super.onConfigurationChanged(newConfig);
	//
	// // Checks the orientation of the screen
	// if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	// Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
	// } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
	// Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
	// }
	// }

	public void initUI() {
		Log.v(TAG, "initUI");
	}

	// public void onScrollPagerMainCallback() {
	// Log.v(TAG, "onScrollPagerMainCallback");
	//
	// }

	@Override
	public void onPagerChangedCallback(int page) {
		// TODO Auto-generated method stub
		if (page == MainFragmentPager.ACTIVITY) {
			((TextView) findViewById(R.id.titlebar_textview))
					.setText("Activity");
			((Button) findViewById(R.id.btn_settings_done))
					.setVisibility(View.GONE);
		} else if (page == MainFragmentPager.SLEEP) {
			((TextView) findViewById(R.id.titlebar_textview)).setText("Sleep");
			((Button) findViewById(R.id.btn_settings_done))
					.setVisibility(View.GONE);
		}

	}

	@Override
	public void onShareButtonClicked(String s) {
		// TODO Auto-generated method stub
		if (s.equals(MainSlideFragment.FACEBOOK)) {
			Intent intent = new Intent(this, FacebookShareActivity.class);
			intent.putExtra(MainSlideFragment.FACEBOOK,
					"I'm going for my daily goal");
			intent.putExtra(TITLE, MainSlideFragment.FACEBOOK);
			startActivityForResult(intent, FACEBOOK_REQUEST);
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);

		} else if (s.equals(MainSlideFragment.TWITTER)) {
			Intent intent = new Intent(this, TwitterShareActivity.class);

			intent.putExtra(MainSlideFragment.TWITTER,
					"I'm going for my daily goal");
			intent.putExtra(TITLE, MainSlideFragment.TWITTER);
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
			Intent intent = new Intent(this, PreferencesActivity.class);
			intent.putExtra(MainSlideFragment.FACEBOOK,
					"I'm going for my daily goal");

			startActivityForResult(intent, Main.USER_PREFERENCES_REQUEST);
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		if (orientationListener != null)
			orientationListener.enable();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (orientationListener != null)
			orientationListener.disable();
		if (mState == BLE_PROFILE_CONNECTED) {

			disconnect();
		}
	}
	
	private class UpdateConnectivityTask extends AsyncTask<Void, Integer, Void> {

		
		@Override
		protected void onPreExecute()
		{
			index = 0;
		}
		@Override
		protected Void doInBackground(Void... params) {
			
				Log.v("UpdateBarTask", "doInBackground");
				while(mState==STATE_READY)
				{
					for(int i = 1 ; i < connectivity_images.length-2; i++)
					{
						
						try {
							publishProgress(i);
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			
			
			
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			((ImageView)findViewById(R.id.connectivity)).setImageResource(connectivity_images[values[0]]);
		}
	}
	private int index;
	@Override 
	public void onDeviceFound()
	{
		
	}
	
	
	@Override
	public void onStatisticPagerChangedCallback(int page) {
		// TODO Auto-generated method stub
		if (page == 0) {
			((TextView) findViewById(R.id.titlebar_textview))
					.setText("Activity Level");
			((Button) findViewById(R.id.btn_settings_done))
					.setVisibility(View.GONE);
		}

		else if (page == 1) {
			((TextView) findViewById(R.id.titlebar_textview))
					.setText("Sleep Level");
			((Button) findViewById(R.id.btn_settings_done))
					.setVisibility(View.GONE);
		}
	}

	private void showMessage(String msg) {

		Log.v(TAG, msg);

	}
	
	private void setUiState() {
		switch (mState) {
		case BLE_PROFILE_CONNECTED:
			showMessage("STATE_CONNECTED::device name" + mDevice.getName());

			break;
		case BLE_PROFILE_DISCONNECTED:
			showMessage("disconnected");
			break;
		case STATE_READY:
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
		((ImageView)findViewById(R.id.connectivity)).setImageResource(connectivity_images[1]);
	}

	@Override
	public void onDisconnected() {
		super.onDisconnected();
		showMessage("Disconnected");
		setUiState();
		((ImageView)findViewById(R.id.connectivity)).setImageResource(connectivity_images[0]);
	}

	@Override
	public void onReady() {
		super.onReady();
		setUiState();
		showMessage("BLE Ready");
		new UpdateConnectivityTask().execute();
	}

	@Override
	public void onServiceDiscovered() {
		super.onServiceDiscovered();
		setUiState();
		showMessage("Service Discovered");
		
		((ImageView)findViewById(R.id.connectivity)).setImageResource(connectivity_images[connectivity_images.length-1]);
		
		runOnUiThread(new Runnable() {
			public void run() {
				showMessage("startStream ");
				try {
					Thread.sleep(1000);
					startStream();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}
	private Handler mHandler = new Handler();
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

//		showMessage(s);
		try {
			
			frag.onStreamMessage(steps, calories, distance, activityTime,
					batteryLevel);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// MainSlideFragment frag = (MainSlideFragment)
		// getSupportFragmentManager().findFragmentByTag(TabsFragment.TAB_MAIN);
		//
		// frag.goalCaloriesTv.setText(String.valueOf(calories));
		// frag.goalDistancesTv.setText(String.valueOf(distance));
		// frag.goalStepsTv.setText(String.valueOf(steps));

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
	}

	@Override
	public void onReadProfile(int gender, int year, byte month, int weight,
			int height) {
		// TODO Auto-generated method stub
		String s = "";
		s += "Wristband Set Profile :\n";
		s += "GENDER : " + ((gender == 0) ? "Male" : "Female") + "\n";
		s += "YEAR : " + (year) + "\n";
		s += "MONTH : " + (month) + "\n";
		s += "WEIGHT : " + (weight) + "\n";
		s += "HEIGHT : " + (height);
		showMessage(s);
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
	}

	@Override
	public void onReadTarget(int activity, int toggle, long step, int distance,
			int calories) {
		// TODO Auto-generated method stub
		String s = "";
		s += "Wristband Set Target :\n";

		s += "Activity Value " + activity + "\n";
		s += "Toggle Gauge " + toggle + "\n";
		s += "Step " + step + "\n";
		s += "Distance " + distance + "km \n";
		s += "Calories " + calories + "kcal \n";
		showMessage(s);
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
		String s = "ReadVersion " + xx + "-" + yy;
		showMessage(s);
	}

	@Override
	public void dispatchSelf(MainSlideFragment mainSlideFragment) {
		// TODO Auto-generated method stub
		frag = mainSlideFragment;
		Log.v(TAG, "MainSlideFragment " + frag.toString());
	}

}