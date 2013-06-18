package com.idthk.wristband.api;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import com.samsung.android.sdk.bt.gatt.BluetoothGattCharacteristic;
import com.samsung.android.sdk.bt.gatt.BluetoothGattService;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

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

public class BLEBaseFragmentActivity extends FragmentActivity {
	static final String TAG = "BLEBaseFragmentActivity";
	protected static final int REQUEST_SELECT_DEVICE = 1;
	protected static final int REQUEST_ENABLE_BT = 2;
	protected static final int STATE_READY = 10;
	protected static final int BLE_PROFILE_CONNECTED = 20;
	protected static final int BLE_PROFILE_DISCONNECTED = 21;
	protected static final int BLE_SERVICE_DISCOVERED = 22;
	protected static final int STATE_OFF = 10;
	protected int mState = BLE_PROFILE_DISCONNECTED;

	protected WristbandBLEService mService = null;
	protected BluetoothDevice mDevice = null;
	protected BluetoothAdapter mBtAdapter = null;
	protected ServiceConnection onService = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mBtAdapter = BluetoothAdapter.getDefaultAdapter();

		if (mBtAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		Intent bindIntent = new Intent(this, WristbandBLEService.class);
		startService(bindIntent);
		bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		this.registerReceiver(bleStatusChangeReceiver, filter);

	}

	@Override
	public void onStart() {
		super.onStart();
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		this.registerReceiver(mReceiver, filter);

	}

	@Override
	public void onDestroy() {
		Log.v(TAG, "onDestory");
		if (mService != null) {
			mService.scan(false);
			mService.disconnect(mDevice);
		}

		try {
			unregisterReceiver(bleStatusChangeReceiver);
		} catch (Exception ignore) {
			Log.e(TAG, ignore.toString());
		}
		unbindService(mServiceConnection);
		unregisterReceiver(mReceiver);
		stopService(new Intent(this, WristbandBLEService.class));
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		Log.v(TAG, "onStop");

		super.onStop();
	}

	@Override
	protected void onPause() {
		Log.v(TAG, "onPause");
		super.onPause();
	}

	@Override
	protected void onRestart() {
		super.onRestart();

	}

	@Override
	public void onResume() {
		Log.v(TAG, "onResume");
		super.onResume();

		if (!mBtAdapter.isEnabled()) {

			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}

	}

	private ServiceConnection mServiceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className,
				IBinder rawBinder) {
			mService = ((WristbandBLEService.LocalBinder) rawBinder)
					.getService();

			// set call back method
			mService.setActivityHandler(mHandler);
			// set call back method
			mService.setDeviceListHandler(mHandler);
			prepareToConnect();
		}

		public void onServiceDisconnected(ComponentName classname) {
			mService.disconnect(mDevice);
			mService = null;
		}
	};
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			final Bundle data = msg.getData();
			final int rssi = data.getInt(WristbandBLEService.EXTRA_RSSI);
			switch (msg.what) {

			// Gatt device found message.
			case WristbandBLEService.BLE_CONNECT_MSG:

				runOnUiThread(new Runnable() {
					public void run() {
						if (mDevice != null
								&& mDevice
										.getAddress()
										.equals(data
												.getString(BluetoothDevice.EXTRA_DEVICE))) {

							mState = BLE_PROFILE_CONNECTED;

							onConnected();
						}
					}
				});
				break;
			case WristbandBLEService.GATT_DEVICE_FOUND_MSG:

				final BluetoothDevice device = data
						.getParcelable(BluetoothDevice.EXTRA_DEVICE);

				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						mDevice = BluetoothAdapter.getDefaultAdapter()
								.getRemoteDevice(device.getAddress());

						Log.v(TAG, "Device Name : "
								+ device.getName().charAt(0));
						if (device.getName().charAt(0) == 'A') {
							mService.connect(mDevice, false);

							Log.v(TAG, "Device Name : " + device.getName());
						}
						onDeviceFound();

					}
				});
				break;

			case WristbandBLEService.BLE_DISCONNECT_MSG:
				runOnUiThread(new Runnable() {
					public void run() {
						if (mDevice != null
								&& mDevice
										.getAddress()
										.equals(data
												.getString(BluetoothDevice.EXTRA_DEVICE))) {
							mState = BLE_PROFILE_DISCONNECTED;
							mService.disconnect(mDevice);
							onDisconnected();
						}
					}
				});
				break;

			case WristbandBLEService.BLE_READY_MSG:

				runOnUiThread(new Runnable() {
					public void run() {
						mState = STATE_READY;
						onReady();
					}
				});
				break;
			case WristbandBLEService.BLE_CHARACTERISTIC_WROTE:

				break;
			case WristbandBLEService.BLE_STREAM_MSG:
				final byte[] value_ = data
						.getByteArray(WristbandBLEService.EXTRA_VALUE);

				runOnUiThread(new Runnable() {
					public void run() {
						try {
							if (value_.length == 19) {
								int steps = (value_[1] * 10000)
										+ (value_[2] * 100) + value_[3];
								int calories = (value_[7] * 10000)
										+ (value_[8] * 100) + value_[9];
								float distance = (float) ((value_[10] * 100)
										+ value_[11] + (value_[12] / 100.0));

								int activityTime = (value_[13] * 60)
										+ value_[14];
								// int activityLevel = value_[15];

								int batteryLevel = value_[16];

								onStreamMessage(steps, calories, distance,
										activityTime, batteryLevel);
							}
						} catch (Exception e) {
							Log.e(TAG, e.toString());
						}
					}

				});
				break;
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
			case WristbandBLEService.GATT_CHARACTERISTIC_RSSI_MSG:

				final BluetoothDevice RemoteRssidevice = data
						.getParcelable(WristbandBLEService.EXTRA_DEVICE);
				runOnUiThread(new Runnable() {
					public void run() {
						if (mDevice != null && RemoteRssidevice != null
								&& mDevice.equals(RemoteRssidevice)) {
							if (rssi != 0) {

							}
						}
					}
				});
				break;
			case WristbandBLEService.BLE_WRITE_REQUEST_MSG: {
				final byte[] value1 = data
						.getByteArray(WristbandBLEService.EXTRA_VALUE);

				String __msg = "";
				for (int i = 0; i < value1.length; i++) {
					__msg += "BLE_WRITE_REQUEST_MSG " + i + " : " + value1[i]
							+ "\n";
				}

				runOnUiThread(new Runnable() {
					@Override
					public void run() {

					}
				});
			}
				break;
			case WristbandBLEService.BLE_SERVICE_DISCOVER_MSG: {

				Bundle _data = msg.getData();
				final BluetoothDevice _device = _data
						.getParcelable(BluetoothDevice.EXTRA_DEVICE);

				List<BluetoothGattService> services = mService.mBluetoothGatt
						.getServices(_device);
				for (BluetoothGattService service : services) {
					Log.v(TAG, "service " + service.getUuid().toString());
					List<BluetoothGattCharacteristic> characteristics = service
							.getCharacteristics();
					for (BluetoothGattCharacteristic characteristic : characteristics) {
						Log.v(TAG, "characteristic "
								+ characteristic.getUuid().toString());
					}
				}
				mService.scan(false);
				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
//						try {
//							Thread.sleep(1000);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}

						mService.EnableDeviceNoti(mDevice,
								WristbandBLEService.PE128_SERVICE,
								WristbandBLEService.PE128_CHAR_RCVD);
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						mService.EnableDeviceNoti(mDevice,
								WristbandBLEService.PE128_NOTI_SERVICE,
								WristbandBLEService.PE128_CHAR_STREAMING);
					}
				});

				onServiceDiscovered();

			}
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

				setProgressBarIndeterminateVisibility(false);

			}
			if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {

				if (!mBtAdapter.isEnabled()) {

				}

			}
		}
	};
	private final BroadcastReceiver bleStatusChangeReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			final Intent mIntent = intent;
			if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
				BluetoothDevice device = mIntent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// showMessage( "BluetoothDevice.ACTION_BOND_STATE_CHANGED");
				if (device.equals(mDevice)) {
					runOnUiThread(new Runnable() {
						public void run() {

						}
					});
				}
			}
			if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
				final int state = mIntent.getIntExtra(
						BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
				// showMessage( "BluetoothAdapter.ACTION_STATE_CHANGED" +
				// "state is"+ state);
				runOnUiThread(new Runnable() {
					public void run() {
						if (state == STATE_OFF) {

						}
					}
				});
			}
		}
	};

	@Override
	public void onBackPressed() {
		if (mState == STATE_READY) {
			Intent startMain = new Intent(Intent.ACTION_MAIN);
			startMain.addCategory(Intent.CATEGORY_HOME);
			startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(startMain);
		} else {
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
							}).setNegativeButton(R.string.popup_no, null)
					.show();
		}
	}

	public void prepareToConnect() {
		// TODO Auto-generated method stub
		if (mState == BLE_PROFILE_DISCONNECTED) {

			connect();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public static String bytesToHex(byte[] bytes) {
		final char[] hexArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
				'9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] hexChars = new char[bytes.length * 3];
		int v;
		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j * 3] = hexArray[v >>> 4];
			hexChars[j * 3 + 1] = hexArray[v & 0x0F];
			hexChars[j * 3 + 2] = '-';
		}
		return new String(hexChars);
	}

	// public set function
	// wristband specify function

	public void startStream() {
		mService.WriteDevice(mDevice, WristbandBLEService.PE128_SERVICE,
				WristbandBLEService.PE128_CHAR_XFER,
				WristbandBLEService.STREAM_MODE_START);
	}

	public void stopStream() {
		mService.WriteDevice(mDevice, WristbandBLEService.PE128_SERVICE,
				WristbandBLEService.PE128_CHAR_XFER,
				WristbandBLEService.STREAM_MODE_STOP);
	}

	public void setTime(int year, int month, int day, int hour, int minute,
			int second, int week) {
		byte data[] = WristbandBLEService.SET_TIME_PREFIX;
		int dataStart = 9;
		data[dataStart] = (byte) year;
		data[dataStart + 1] = (byte) month;
		data[dataStart + 2] = (byte) day;
		data[dataStart + 3] = (byte) hour;
		data[dataStart + 4] = (byte) minute;
		data[dataStart + 5] = (byte) second;
		data[dataStart + 6] = (byte) week;
		mService.WriteDevice(mDevice, WristbandBLEService.PE128_SERVICE,
				WristbandBLEService.PE128_CHAR_XFER, data);
	}

	public void setProfile(int gender, int birthyear, int birthmonth,
			int weight, int height) {
		byte data[] = WristbandBLEService.SET_PROFILE_PREFIX;
		int dataStart = 9;
		data[dataStart] = (byte) gender;
		data[dataStart + 1] = (byte) (birthyear - 1914);
		data[dataStart + 2] = (byte) birthmonth;
		data[dataStart + 3] = (byte) (weight - 20);
		data[dataStart + 4] = (byte) (height - 69);
		mService.WriteDevice(mDevice, WristbandBLEService.PE128_SERVICE,
				WristbandBLEService.PE128_CHAR_XFER, data);
	}

	public void setSleep(int weekday_weak_hour, int weekday_weak_minute,
			int weekend_weak_hour, int weekend_weak_minute, int toggle) {
		byte data[] = WristbandBLEService.SET_SLEEP_PREFIX;
		int dataStart = 9;
		data[dataStart] = (byte) weekday_weak_hour;
		data[dataStart + 1] = (byte) weekday_weak_minute;
		data[dataStart + 2] = (byte) weekend_weak_hour;
		data[dataStart + 3] = (byte) weekend_weak_minute;
		data[dataStart + 4] = (byte) toggle;
		mService.WriteDevice(mDevice, WristbandBLEService.PE128_SERVICE,
				WristbandBLEService.PE128_CHAR_XFER, data);
	}

	public void setTarget(int duration, int togggle, int step, int distance,
			int calories) {
		byte data[] = WristbandBLEService.SET_TARGET_PREFIX;
		int dataStart = 9;
		data[dataStart] = (byte) duration;
		data[dataStart + 1] = (byte) togggle;
		Log.v(TAG, "Step " + step);

		int steps_high = step / 10000;
		int steps_medium = (step - (steps_high) * 10000) / 100;
		int steps_low = step % 100;

		int calories_high = calories / 10000;
		int calories_medium = (calories - (calories_high) * 10000) / 100;
		int calories_low = calories % 100;

		data[dataStart + 2] = (byte) steps_high;
		data[dataStart + 3] = (byte) steps_medium;
		data[dataStart + 4] = (byte) steps_low;
		data[dataStart + 5] = (byte) distance;
		data[dataStart + 6] = (byte) calories_high;
		data[dataStart + 7] = (byte) calories_medium;
		data[dataStart + 8] = (byte) calories_low;

		mService.WriteDevice(mDevice, WristbandBLEService.PE128_SERVICE,
				WristbandBLEService.PE128_CHAR_XFER, data);
	}

	public void getVersion() {

		mService.WriteDevice(mDevice, WristbandBLEService.PE128_SERVICE,
				WristbandBLEService.PE128_CHAR_XFER,
				WristbandBLEService.TEST_VERSION_PREFIX);
	}

	public void setDemand() {

		mService.WriteDevice(mDevice, WristbandBLEService.PE128_SERVICE,
				WristbandBLEService.PE128_CHAR_XFER,
				WristbandBLEService.SET_DEMAND_PREFIX);
	}

	// public callback function
	public void connect() {
		Log.v(TAG, "Connecting to BLE Device");
		Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
		for (BluetoothDevice pairedDevice : pairedDevices) {
			boolean result = false;
			result = mService.isBLEDevice(pairedDevice);
			if (result == true) {

				if (pairedDevice.getName().charAt(0) == 'A') {
					mDevice = pairedDevice;
					mService.connect(mDevice, false);

					Log.v(TAG, "Device Name : " + mDevice.getName());
				}
				onDeviceFound();
			}
		}
		if (mDevice == null) {
			if (mService != null)
				mService.scan(true);
		}
	}

	public void disconnect() {
		if (mService != null) {
			mService.scan(false);
			mService.disconnect(mDevice);
		}
	}

	public void discover(boolean scan) {

	}

	// public void onDiscover()
	// {
	//
	// }
	public void onConnected() {
		mState = BLE_PROFILE_CONNECTED;
	}

	public void onDisconnected() {
		mState = BLE_PROFILE_DISCONNECTED;
	}

	public void onReady() {
		mState = this.STATE_READY;
	}

	public void onServiceDiscovered() {
		mState = BLE_SERVICE_DISCOVERED;
	}

	public void onDeviceFound() {

	}

	public void onStreamMessage(int steps, int calories, float distance,
			int activityTime, int activityLevel) {
		// TODO Auto-generated method stub

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
