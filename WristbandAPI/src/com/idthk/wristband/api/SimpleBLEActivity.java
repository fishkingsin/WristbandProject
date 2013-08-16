package com.idthk.wristband.api;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import android.app.Activity;
import android.app.AlertDialog;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class SimpleBLEActivity extends Activity {
	private final static String TAG = SimpleBLEActivity.class.getSimpleName();

	protected WristbandBLEService mService = null;

	protected ServiceConnection onService = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		onService = new ServiceConnection() {
			public void onServiceConnected(ComponentName className,
					IBinder rawBinder) {
				mService = ((WristbandBLEService.LocalBinder) rawBinder)
						.getService();
				if (mService != null) {
					mService.setActivityHandler(mHandler);
				}

			}

			public void onServiceDisconnected(ComponentName classname) {
				mService = null;
			}
		};
		startService(new Intent(this, WristbandBLEService.class));
		Intent bindIntent = new Intent(this, WristbandBLEService.class);
		bindService(bindIntent, onService, Context.BIND_AUTO_CREATE);

	}


	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			final Bundle data = msg.getData();
			final int rssi = data.getInt(WristbandBLEService.EXTRA_RSSI);
			switch (msg.what) {

			
			case WristbandBLEService.BLE_VALUE_MSG:

				final byte[] value = data
						.getByteArray(WristbandBLEService.EXTRA_VALUE);

				runOnUiThread(new Runnable() {
					public void run() {
						try {

							int ret = mService.checkPrefix(value);
							final int dataStart = 9;
							mService.printPrefix(ret);
							if (ret == WristbandBLEService.DEVICE_RETURN_TIME) {

								onReadTime((2000 + value[dataStart]),
										value[dataStart + 1],
										value[dataStart + 2],
										value[dataStart + 3],
										value[dataStart + 4],
										value[dataStart + 5],
										value[dataStart + 6]);
							} else if (ret == WristbandBLEService.DEVICE_RETURN_PROFILE) {
								onReadProfile(value[dataStart],
										(value[dataStart + 1] + 1914),
										(value[dataStart + 2]),
										(value[dataStart + 3] + 20),
										(value[dataStart + 4] + 69));
							} else if (ret == WristbandBLEService.DEVICE_RETURN_TARGET) {
								int steps = (value[dataStart + 2] * 10000)
										+ (value[dataStart + 3] * 100)
										+ value[dataStart + 4];
								int calories = (value[dataStart + 6] * 10000)
										+ (value[dataStart + 7] * 100)
										+ value[dataStart + 8];

								onReadTarget(value[dataStart],
										value[dataStart + 1], steps,
										value[dataStart + 5], calories);
							} else if (ret == WristbandBLEService.DEVICE_RETURN_SLEEP) {
								onReadSleep(value[dataStart],
										value[dataStart + 1],
										value[dataStart + 2],
										value[dataStart + 3],
										value[dataStart + 4]);

							} else if (ret == WristbandBLEService.DEVICE_RETURN_VERSION) {
								int xx = value[17];
								int yy = value[18];
								onReadVersion(xx, yy);

							}

							else {
								onReadUnknownProtocol(value);

							}
						} catch (Exception e) {

						}
					}

				});
				break;
			
			default:
				super.handleMessage(msg);
			}
		}
	};
	

	public void onConnected() {

	}

	public void onDisconnected() {

	}

	public void onReady() {

	}

	public void onServiceDiscovered() {

	}

	public void onDeviceFound() {

	}

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
		
		Log.v(TAG, s);

	}

	public void onReadVersion(int xx, int yy) {
		String s = xx + "-" + yy;
		Log.v(TAG, s);
	}

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
		Log.v(TAG, s);
	}

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
		Log.v(TAG, s);
	}

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
		}
		Log.v(TAG, s);
	}

	public void onReadTarget(int duration, int toggle, long step, int distance,
			int calories) {
		// TODO Auto-generated method stub
		String s = "";
		s += "Wristband Set Target :\n";

		s += "Activity duration " + duration + "minutes\n";
		s += "Toggle Gauge" + toggle + "\n";
		s += "Step " + step + "\n";
		s += "Distance " + distance + "km \n";
		s += "Calories " + calories + "kcal \n";
		Log.v(TAG, s);
	}

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
		Log.v(TAG, s);
		Log.v(TAG, _msg);

	}

}
