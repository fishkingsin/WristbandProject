/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.idthk.wristband.api;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import com.samsung.android.sdk.bt.gatt.*;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class WristbandBLEService extends Service {
	// set true to print debug message;
	static final boolean bDebug = false;

	static final String TAG = "WristbandBLEService";

	BluetoothGattService pe128Service;
	public static UUID PE128_SERVICE = UUID
			.fromString("c231ff01-8d74-4fa9-a7dd-13abdfe5cbff");

	BluetoothGattService pe128NotiService;
	public static UUID PE128_NOTI_SERVICE = UUID
			.fromString("00001810-0000-1000-8000-00805f9b34fb");

	BluetoothGattCharacteristic pe128Streaming;
	public static UUID PE128_CHAR_STREAMING = UUID
			.fromString("00002a35-0000-1000-8000-00805f9b34fb");
	BluetoothGattCharacteristic pe128Receiver;
	public static final UUID PE128_CHAR_RCVD = UUID
			.fromString("c231ff08-8d74-4fa9-a7dd-13abdfe5cbff");
	BluetoothGattCharacteristic pe128Transfer;
	public static final UUID PE128_CHAR_XFER = UUID
			.fromString("c231ff02-8d74-4fa9-a7dd-13abdfe5cbff");
	public static final UUID DEVICE_INFORMATION = UUID
			.fromString("0000180A-0000-1000-8000-00805f9b34fb");
	public static final UUID SERIAL_NUMBER_STRING = UUID
			.fromString("00002A25-0000-1000-8000-00805f9b34fb");

	public static final UUID CCC = UUID
			.fromString("00002902-0000-1000-8000-00805f9b34fb");
	public static final UUID FIRMWARE_REVISON_UUID = UUID
			.fromString("00002a26-0000-1000-8000-00805f9b34fb");
	public static final UUID DIS_UUID = UUID
			.fromString("0000180a-0000-1000-8000-00805f9b34fb");
	// public static final String SERIAL_STRING =
	// "com.fishkingsin.ble.serialstring";
	BluetoothGattService indicateService;
	public static final UUID INDICATOR_SERVICE_UUID = UUID
			.fromString("00001810-0000-1000-8000-00805f9b34fb");
	public static final UUID INDICATOR_UUID = UUID
			.fromString("00002a35-0000-1000-8000-00805f9b34fb");
	public static final UUID INDICATOR_UUID49 = UUID
			.fromString("00002a49-0000-1000-8000-00805f9b34fb");
	public static final UUID INDICATOR_UUID36 = UUID
			.fromString("00002a36-0000-1000-8000-00805f9b34fb");
	public static final UUID INDICATOR_UUID37 = UUID

	.fromString("00002a37-0000-1000-8000-00805f9b34fb");
	public static final UUID INDICATOR_UUID38 = UUID
			.fromString("00002a38-0000-1000-8000-00805f9b34fb");

	// protocol
//	final static byte[] SET_DEMAND_PREFIX = { (byte) 0xAA, (byte) 0x50,
//			(byte) 0xF1, (byte) 0x00, (byte) 0x04, (byte) 0x02, (byte) 0x01,
//			(byte) 0x06, (byte) 0x01, (byte) 0x55 };

	final static byte[] STREAM_MODE_START = { (byte) 0xAA, (byte) 0x50,
			(byte) 0xF1, (byte) 0x00, (byte) 0x04, (byte) 0x02, (byte) 0x01,
			(byte) 0x05, (byte) 0x01, (byte) 0x55 };
	final static byte[] STREAM_MODE_STOP = { (byte) 0xAA, (byte) 0x50,
			(byte) 0xF1, (byte) 0x00, (byte) 0x04, (byte) 0x02, (byte) 0x00,
			(byte) 0x05, (byte) 0xFF, (byte) 0x55 };

	// AA-50-F1-00-03-01-07-01-55
	final static byte[] TEST_VERSION_PREFIX = { (byte) 0xAA, (byte) 0x50,
			(byte) 0xF1, (byte) 0x00, (byte) 0x03, (byte) 0x01, (byte) 0x07,
			(byte) 0x01, (byte) 0x55 };
	// AA-42-10-00-0E-01-07-50-45-31-32-31-2D-36-2D-38-3A-XX-YY-55

	final static byte[] TEST_VERSION_RETURN_PREFIX = { (byte) 0xAA,
			(byte) 0x42, (byte) 0x10, (byte) 0x00, (byte) 0x0E, (byte) 0x01,
			(byte) 0x07, (byte) 0x50, (byte) 0x45, (byte) 0x31, (byte) 0x32,
			(byte) 0x31, (byte) 0x2D, (byte) 0x36, (byte) 0x2D, (byte) 0x38,
			(byte) 0x3A, (byte) 0x00, (byte) 0x00, (byte) 0x55 };

	final static byte[] SET_TIME_PREFIX = { (byte) 0xAA, (byte) 0x50,
			(byte) 0xF0, (byte) 0x00, (byte) 0x0B, (byte) 0x02, (byte) 0x01,
			(byte) 0x01, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x55 };
	// AA-42-10-00-0B-02-00-01-01-UU-VV-WW-XX-YY-ZZ-SS-55
	final static byte[] SET_TIME_RETURN_PREFIX = { (byte) 0xAA, (byte) 0x42,
			(byte) 0x10, (byte) 0x00, (byte) 0x0B, (byte) 0x02, (byte) 0x00,
			(byte) 0x01, (byte) 0x01 };

	// AA-50-F0-00-09-02-01-02-02-QQ-RR-SS-TT-UU -55
	final static byte[] SET_PROFILE_PREFIX = { (byte) 0xAA, (byte) 0x50,
			(byte) 0xF0, (byte) 0x00, (byte) 0x09, (byte) 0x02, (byte) 0x01,
			(byte) 0x02, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x55 };
	// AA-42-10-00-09-02-00-02-02-QQ-RR-SS-TT-UU-55
	final static byte[] SET_PROFILE_RETURN_PREFIX = { (byte) 0xAA, (byte) 0x42,
			(byte) 0x10, (byte) 0x00, (byte) 0x09, (byte) 0x02, (byte) 0x00,
			(byte) 0x02, (byte) 0x02 };

	// AA-50-F0-00-09-02-01-03-01-XX-YY- RR-SS-ZZ-55
	final static byte[] SET_SLEEP_PREFIX = { (byte) 0xAA, (byte) 0x50,
			(byte) 0xF0, (byte) 0x00, (byte) 0x09, (byte) 0x02, (byte) 0x01,
			(byte) 0x03, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x55 };
	final static byte[] SET_SLEEP_RETURN_PREFIX = { (byte) 0xAA, (byte) 0x42,
			(byte) 0x10, (byte) 0x00, (byte) 0x09, (byte) 0x02, (byte) 0x00,
			(byte) 0x03, (byte) 0x01 };

	final static byte[] SET_TARGET_PREFIX = { (byte) 0xAA, (byte) 0x50,
			(byte) 0xF0, (byte) 0x00, (byte) 0x0D, (byte) 0x02, (byte) 0x01,
			(byte) 0x04, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x55 };
	final byte[] SET_TARGET_RETURN_PREFIX = { (byte) 0xAA, (byte) 0x42,
			(byte) 0x10, (byte) 0x00, (byte) 0x0D, (byte) 0x02, (byte) 0x00,
			(byte) 0x04, (byte) 0x01 };
	// AA-50-F0-FE-FE-FE-FE-FE-55
	final static byte[] SET_DISCONNECT_PREFIX = { (byte) 0xAA, (byte) 0x50,
			(byte) 0xF0, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE,
			(byte) 0xFE, (byte) 0x55 };

	final static byte[] TEST_SERIAL_PREFIX = { (byte) 0xAA, (byte) 0x50,
			(byte) 0xF1, (byte) 0x00, (byte) 0x03, (byte) 0x01, (byte) 0x05,
			(byte) 0x01, (byte) 0x55 };

	final static byte[] TEST_SERIAL_RETURN_PREFIX = { (byte) 0xAA, (byte) 0x42,
			(byte) 0x10, (byte) 0x00, (byte) 0x0D, (byte) 0x01, (byte) 0x05,
			(byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x55 };
	
	//AA-50-F1-00-04-02-01-06- 01-55
	final static byte[] HISTORY_PREFIX = { (byte) 0xAA, (byte) 0x50,
		(byte) 0xF1, (byte) 0x00, (byte) 0x04, (byte) 0x02, (byte) 0x01,
		(byte) 0x06, (byte) 0x01, (byte) 0x55 };
	final static byte[] HISTORY_RETURN_HEADER = { (byte)0x6D,(byte)0x6D,(byte)0x6D,(byte)0x6D,(byte)0x6D,(byte)0x6D };
	final static byte[] HISTORY_RETURN_FOOTER = { (byte)0x7E,(byte)0x7E,(byte)0x7E,(byte)0x7E,(byte)0x7E,(byte)0x7E };

	public static final int BLE_STREAM_MSG = 20;
	public static final int BLE_CONNECT_MSG = 21;
	public static final int BLE_DISCONNECT_MSG = 22;
	public static final int BLE_READY_MSG = 23;
	public static final int BLE_VALUE_MSG = 24;
	public static final int BLE_ERROR_MSG = 0xF0;

	public static final int BLE_CONNECTTION_ERROR = 0xF1;

	// James Kong 20130506
	// ------------------------------------------------------
	public static final int BLE_SERVICE_DISCOVER_MSG = 29;
	// ------------------------------------------------------
	public static final int GATT_DEVICE_FOUND_MSG = 25;
	public static final int GATT_CHARACTERISTIC_RSSI_MSG = 26;
	public static final int BLE_WRITE_REQUEST_MSG = 27;
	public static final int BLE_CHARACTERISTIC_WROTE = 28;

	// James Kong 20130523
	public static final int DEVICE_RETURN_TARGET = 30;
	public static final int DEVICE_RETURN_TIME = 31;
	public static final int DEVICE_RETURN_SLEEP = 32;
	public static final int DEVICE_RETURN_PROFILE = 33;
	public static final int DEVICE_RETURN_STARTSTREAM = 34;
	public static final int DEVICE_RETURN_STOPSTREAM = 35;
	public static final int DEVICE_RETURN_VERSION = 36;
	public static final int DEVICE_RETURN_SERIAL = 37;
	public static final int DEVICE_RETURN_HISTORY = 38;
	/**
	 * Source of device entries in the device list
	 */
	public static final int DEVICE_SOURCE_SCAN = 10;
	public static final int DEVICE_SOURCE_BONDED = 11;
	public static final int DEVICE_SOURCE_CONNECTED = 12;

	/**
	 * Intent extras
	 */
	public static final String EXTRA_DEVICE = "DEVICE";
	public static final String EXTRA_RSSI = "RSSI";
	public static final String EXTRA_SOURCE = "SOURCE";
	public static final String EXTRA_ADDR = "ADDRESS";
	public static final String EXTRA_CONNECTED = "CONNECTED";
	public static final String EXTRA_STATUS = "STATUS";
	public static final String EXTRA_UUID = "UUID";
	public static final String EXTRA_VALUE = "VALUE";

	public static final byte NO_ALERT = 0;
	public static final byte LOW_ALERT = 1;
	public static final byte HIGH_ALERT = 2;

	

	private BluetoothAdapter mBtAdapter = null;
	public BluetoothGatt mBluetoothGatt = null;
	public BluetoothDevice mDevice = null;
	private Handler mActivityHandler = null;
	private Handler mDeviceListHandler = null;
	public boolean isNoti = false;

	/**
	 * Profile service connection listener
	 */
	public class LocalBinder extends Binder {
		WristbandBLEService getService() {
			return WristbandBLEService.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}

	private final IBinder binder = new LocalBinder();

	@Override
	public void onCreate() {
		if (bDebug) {
			Log.d(TAG, "onCreate()");
			Log.d(TAG, "Only Handle BLE characteristic filtering");
		}
		if (mBtAdapter == null) {
			mBtAdapter = BluetoothAdapter.getDefaultAdapter();
			if (mBtAdapter == null)
				return;
		}
		BluetoothGattAdapter.getProfileProxy(this, mProfileServiceListener,
				BluetoothGattAdapter.GATT);

	}

	public void setActivityHandler(Handler mHandler) {
		if (bDebug)
			Log.d(TAG, "Activity Handler set");
		mActivityHandler = mHandler;
	}

	public void setDeviceListHandler(Handler mHandler) {
		if (bDebug)
			Log.d(TAG, "Device List Handler set");
		mDeviceListHandler = mHandler;
	}

	@Override
	public void onDestroy() {
		if (bDebug)
			Log.d(TAG, "onDestroy()");
		if (mBtAdapter != null && mBluetoothGatt != null) {
			BluetoothGattAdapter.closeProfileProxy(BluetoothGattAdapter.GATT,
					mBluetoothGatt);
		}
		super.onDestroy();
	}

	private BluetoothProfile.ServiceListener mProfileServiceListener = new BluetoothProfile.ServiceListener() {
		@SuppressLint("NewApi")
		@Override
		public void onServiceConnected(int profile, BluetoothProfile proxy) {
			if (bDebug)
				Log.d(TAG, "onServiceConnected() - client. profile is"
						+ profile);

			if (profile == BluetoothGattAdapter.GATT) {
				if (bDebug)
					Log.d(TAG,
							" Inside GATT onServiceConnected() - client. profile is"
									+ profile);
				mBluetoothGatt = (BluetoothGatt) proxy;
				mBluetoothGatt.registerApp(mGattCallbacks);

			}
		}

		@Override
		public void onServiceDisconnected(int profile) {
			if (profile == BluetoothGattAdapter.GATT) {

				if (mBluetoothGatt != null) {
					mBluetoothGatt.unregisterApp();
					mBluetoothGatt = null;
				}
			}

		}
	};

	/**
	 * GATT client callbacks
	 */
	private BluetoothGattCallback mGattCallbacks = new BluetoothGattCallback() {
		public void onAppRegistered(int status) {
			Log.v(TAG, "onAppRegistered ()");
			checkGattStatus(status);
		}

		@Override
		public void onScanResult(BluetoothDevice device, int rssi,
				byte[] scanRecord) {
			 if(bDebug)Log.d(TAG, "onScanResult() - device=" + device +
			 ", rssi=" + rssi);
			if (!checkIfBroadcastMode(scanRecord)) {
				mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(
						device.getAddress());

				if (device.getName().charAt(0) == 'A') {
					connect( false);
					Bundle mBundle = new Bundle();
					Message msg = Message.obtain(mDeviceListHandler,
							GATT_DEVICE_FOUND_MSG);
					mBundle.putParcelable(BluetoothDevice.EXTRA_DEVICE, device);
					// mBundle.putInt(EXTRA_RSSI, rssi);
					// mBundle.putInt(EXTRA_SOURCE, DEVICE_SOURCE_SCAN);
					msg.setData(mBundle);
					msg.sendToTarget();
				}
				
			} else
				Log.i(TAG, "device =" + device
						+ " is in Brodacast mode, hence not displaying");
		}

		@Override
		public void onConnectionStateChange(BluetoothDevice device, int status,
				int newState) {
			if (bDebug)
				Log.d(TAG,
						" Client onConnectionStateChange ("
								+ device.getAddress() + ")");
			// Device has been connected - start service discovery
			if (newState == BluetoothProfile.STATE_CONNECTED
					&& mBluetoothGatt != null) {
				Log.v(TAG, "newState STATE_CONNECTED");
				Bundle mBundle = new Bundle();
				Message msg = Message.obtain(mActivityHandler, BLE_CONNECT_MSG);
				mBundle.putString(BluetoothDevice.EXTRA_DEVICE,
						device.getAddress());
				msg.setData(mBundle);
				msg.sendToTarget();
				// ParcelUuid uuids[] = device.getUuids();
				mDevice = device;
				mBluetoothGatt.discoverServices(mDevice);

			} else if (newState == BluetoothProfile.STATE_DISCONNECTED
					&& mBluetoothGatt != null) {
				Bundle mBundle = new Bundle();
				Message msg = Message.obtain(mActivityHandler,
						BLE_DISCONNECT_MSG);
				mBundle.putString(BluetoothDevice.EXTRA_DEVICE,
						device.getAddress());
				msg.setData(mBundle);
				msg.sendToTarget();
				Log.v(TAG, "newState STATE_DISCONNECTED");
			} else {
				Log.v(TAG, "newState " + newState);
			}
		}

		@Override
		public void onCharacteristicChanged(
				BluetoothGattCharacteristic characteristic) {
			if (bDebug) {
				Log.d(TAG, "onCharacteristicChanged()");
			}
			String s = "";
			try {

				s = new String(characteristic.getValue(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (bDebug) {
				Log.v(TAG, "characteristic.getValue() = " + s);
			}
			if (characteristic.getUuid().equals(INDICATOR_UUID)) {
				Bundle mBundle = new Bundle();
				Message msg = Message.obtain(mActivityHandler, BLE_STREAM_MSG);
				mBundle.putByteArray(EXTRA_VALUE, characteristic.getValue());
				msg.setData(mBundle);
				msg.sendToTarget();
			} else {
				Bundle mBundle = new Bundle();
				Message msg = Message.obtain(mActivityHandler, BLE_VALUE_MSG);
				mBundle.putByteArray(EXTRA_VALUE, characteristic.getValue());
				msg.setData(mBundle);
				msg.sendToTarget();

			}
		}

		@Override
		public void onServicesDiscovered(BluetoothDevice device, int status) {
			List<BluetoothGattService> services = mBluetoothGatt
					.getServices(device);
			/*
			 * scan through the list to check the uuid if match the notification
			 * uuid
			 */
			for (BluetoothGattService service : services) {
				if (bDebug)
					Log.v(TAG, "Services : " + service.getUuid());
				if (service.getUuid().equals(PE128_SERVICE)) {
					Log.v(TAG, "Set pe128Service");
					pe128Service = service;
				}
				if (service.getUuid().equals(PE128_NOTI_SERVICE)) {
					Log.v(TAG, "Set pe128NotiService");
					pe128NotiService = service;
				}

				else if (service.getUuid().equals(INDICATOR_SERVICE_UUID)) {
					Log.v(TAG, "Set indicateService");
					indicateService = service;
				}

				List<BluetoothGattCharacteristic> characteristics = service
						.getCharacteristics();
				for (BluetoothGattCharacteristic characteristic : characteristics) {
					if (bDebug)
						Log.v(TAG,
								"Characteristic : " + characteristic.getUuid());
					if (characteristic.getUuid().equals(PE128_CHAR_STREAMING)) {
						pe128Streaming = characteristic;
					} else if (characteristic.getUuid().equals(PE128_CHAR_RCVD)) {
						pe128Receiver = characteristic;
					} else if (characteristic.getUuid().equals(PE128_CHAR_XFER)) {
						pe128Transfer = characteristic;
					}
					checkPropertieStyle(characteristic.getProperties());

				}
			}

			if (bDebug)
				Log.d(TAG, "onServicesDcovered()");
			Message msg = Message.obtain(mActivityHandler,
					BLE_SERVICE_DISCOVER_MSG);
			checkGattStatus(status);
			Bundle mBundle = new Bundle();

			mBundle.putParcelable(BluetoothDevice.EXTRA_DEVICE, device);
			msg.setData(mBundle);
			msg.sendToTarget();
			DummyReadForSecLevelCheck(device);
		}

		@Override
		public void onCharacteristicWrite(
				BluetoothGattCharacteristic characteristic, int status) {
			if (bDebug)
				Log.d(TAG, "onCharacteristicWrite()");
			checkGattStatus(status);
		}

		@Override
		public void onCharacteristicRead(
				BluetoothGattCharacteristic characteristic, int status) {
			UUID charUuid = characteristic.getUuid();
			if (bDebug)
				Log.d(TAG, "onCharacteristicRead()");
			checkGattStatus(status);
			Bundle mBundle = new Bundle();
			Message msg = Message.obtain(mActivityHandler, BLE_VALUE_MSG);

			// if (charUuid.equals(SERIAL_NUMBER_STRING)) {
			// mBundle.putString(SERIAL_STRING,
			// characteristic.getStringValue(0));
			// }
			mBundle.putByteArray(characteristic.getUuid().toString(),
					characteristic.getValue());
			mBundle.putByteArray(EXTRA_VALUE, characteristic.getValue());
			String s = "";
			try {
				s = new String(characteristic.getValue(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.v(TAG, "characteristic.getValue() = " + s);
			msg.setData(mBundle);
			msg.sendToTarget();

		}

		public void onDescriptorWrite(BluetoothGattDescriptor descriptor,
				int status) {
			Log.i(TAG, "onDescriptorWrite status: " + status);
			Bundle mBundle = new Bundle();
			Message msg = Message.obtain(mActivityHandler, BLE_VALUE_MSG);
			String s = "";
			s = String.format("%02X", descriptor.getValue());

			Log.v(TAG, "descriptor.getValue() = " + s);
			mBundle.putByteArray(EXTRA_VALUE, descriptor.getValue());

			msg.setData(mBundle);
			msg.sendToTarget();
			checkGattStatus(status);
		}

		public void onDescriptorRead(BluetoothGattDescriptor descriptor,
				int status) {
			Log.i(TAG, "onDescriptorRead : " + status);
			checkGattStatus(status);
			BluetoothGattCharacteristic mCCC = descriptor.getCharacteristic();
			Log.i(TAG, "Registering for notification UUID : " + mCCC.getUuid());

			String s = "";
			try {
				s = new String(descriptor.getValue(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.v(TAG, "descriptor.getValue() = " + s);

			boolean isenabled = enableNotification(true, mCCC);
			Log.i(TAG, "Notification status =" + isenabled);
			// should fire enable success
		}

		public void onReadRemoteRssi(BluetoothDevice device, int rssi,
				int status) {
			Log.i(TAG, "onRssiRead rssi value is " + rssi);
			Bundle mBundle = new Bundle();
			Message msg = Message.obtain(mActivityHandler,
					GATT_CHARACTERISTIC_RSSI_MSG);
			mBundle.putParcelable(EXTRA_DEVICE, device);
			mBundle.putInt(EXTRA_RSSI, rssi);
			mBundle.putInt(EXTRA_STATUS, status);
			msg.setData(mBundle);
			msg.sendToTarget();
		}

	};

	/*
	 * Broadcast mode checker API
	 */
	public boolean checkIfBroadcastMode(byte[] scanRecord) {
		int offset = 0;
		while (offset < (scanRecord.length - 2)) {
			int len = scanRecord[offset++];
			if (len == 0)
				break; // Length == 0 , we ignore rest of the packet
			// TODO: Check the rest of the packet if get len = 0

			int type = scanRecord[offset++];
			switch (type) {
			case 0x01:

				if (len >= 2) {
					// The usual scenario(2) and More that 2 octets scenario.
					// Since this data will be in Little endian format, we
					// are interested in first 2 bits of first byte
					byte flag = scanRecord[offset++];
					/*
					 * 00000011(0x03) - LE Limited Discoverable Mode and LE
					 * General Discoverable Mode
					 */
					if ((flag & 0x03) > 0)
						return false;
					else
						return true;
				} else if (len == 1) {
					continue;// ignore that packet and continue with the rest
				}
			default:
				offset += (len - 1);
				break;
			}
		}
		return false;
	}

	// it is going to read serial

	public void read_serial_number() {
		Log.i(TAG, "read 0x2A25 uuid charachteristic");
		if (mDevice != null) {
			BluetoothGattService mDI = mBluetoothGatt.getService(mDevice,
					DEVICE_INFORMATION);
			if (mDI == null) {
				Log.e(TAG, "Device Information Service Not Found!!!");
				return;
			}
			BluetoothGattCharacteristic mSNS = mDI
					.getCharacteristic(SERIAL_NUMBER_STRING);
			if (mSNS == null) {
				Log.e(TAG, "Serial Number String Characteristic Not Found!!!");
				return;
			}
			mBluetoothGatt.readCharacteristic(mSNS);
		}
	}

	public void ReadDevice(UUID serviceUUID, UUID charUUID) {
		if (mDevice != null) {
			Log.i(TAG, "ReadDevice charachteristic");
			BluetoothGattService mDI = mBluetoothGatt.getService(mDevice,
					serviceUUID);
			if (mDI == null) {
				Log.e(TAG, "ReadDevice Device Information Service Not Found!!!");
				return;
			}
			BluetoothGattCharacteristic mSNS = mDI.getCharacteristic(charUUID);
			if (mSNS == null) {
				Log.e(TAG, "ReadDevice Characteristic Not Found!!!");
				return;
			}
			if (!mBluetoothGatt.readCharacteristic(mSNS)) {
				Log.e(TAG, "ReadDevice Characteristic Fail to Read!!! UUID: "
						+ mSNS.getUuid());
				return;
			}
		}
	}

	public boolean enableNotification(boolean enable,
			BluetoothGattCharacteristic characteristic) {

		if (mBluetoothGatt == null)
			return false;
		if (!mBluetoothGatt.setCharacteristicNotification(characteristic,
				enable))
			return false;
		List<BluetoothGattDescriptor> descriptors = characteristic
				.getDescriptors();
		for (BluetoothGattDescriptor descriptor : descriptors) {
			Log.i(TAG, "descriptors " + descriptor.getUuid());
		}
		BluetoothGattDescriptor clientConfig = characteristic
				.getDescriptor(CCC);
		if (clientConfig == null)
			return false;

		if (enable) {

			if (characteristic.getProperties() == BluetoothGattCharacteristic.PROPERTY_INDICATE) {
				Log.i(TAG, "enable notification ENABLE_INDICATION_VALUE");
				clientConfig
						.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
			} else {
				Log.i(TAG, "enable notification ENABLE_NOTIFICATION_VALUE");
				clientConfig
						.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			}
		} else {
			Log.i(TAG, "disable notification");
			clientConfig
					.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
		}
		return mBluetoothGatt.writeDescriptor(clientConfig);

	}

	private void showMessage(String msg) {
		Log.e(TAG, msg);
	}

	public void connect( boolean autoconnect) {
		if (mBluetoothGatt != null && mDevice!=null) {
			if (!mBluetoothGatt.connect(mDevice, autoconnect)) {
				Bundle mBundle = new Bundle();
				Message msg = Message.obtain(mActivityHandler, BLE_ERROR_MSG);
				mBundle.putInt(EXTRA_VALUE, BLE_CONNECTTION_ERROR);
				msg.setData(mBundle);
				msg.sendToTarget();
			}
		}
	}

	// public void disconnect(BluetoothDevice device) {
	// if (mBluetoothGatt != null) {
	// mBluetoothGatt.cancelConnection(device);
	// }
	// }
	public void disconnect() {
		if (mBluetoothGatt != null && mDevice != null) {
			mBluetoothGatt.cancelConnection(mDevice);
		}
	}

	// public void removeBond(BluetoothDevice device) {
	// if (mBluetoothGatt != null) {
	// mBluetoothGatt.removeBond(device);
	// }
	//
	// }
	public void removeBond() {
		if (mBluetoothGatt != null && mDevice != null) {
			mBluetoothGatt.removeBond(mDevice);
		}

	}

	public void scan(boolean start) {
		if (mBluetoothGatt == null)
			return;

		if (start) {
			mBluetoothGatt.startScan();
			Bundle mBundle = new Bundle();
			Message msg = Message.obtain(mActivityHandler, BLE_READY_MSG);
			msg.setData(mBundle);
			msg.sendToTarget();
		} else {
			mBluetoothGatt.stopScan();
		}
	}

	public BluetoothGattService getService(UUID ServiceUUID) {
		if (mDevice != null) {
			if (mBluetoothGatt != null) {
				if (bDebug)
					Log.d(TAG, "getService() - ServiceUUID=" + ServiceUUID);
				return mBluetoothGatt.getService(mDevice, ServiceUUID);
			}
		}
		return null;
	}

	public BluetoothGattCharacteristic getCharacteristic(
			BluetoothGattService iService, UUID CharUUID) {
		if (iService != null) {
			if (bDebug)
				Log.d(TAG, "getService() - CharUUID=" + CharUUID);
			return iService.getCharacteristic(CharUUID);
		}
		return null;
	}

	public boolean readCharacteristic(BluetoothGattCharacteristic Char) {
		boolean result = false;
		if (mBluetoothGatt != null) {
			result = mBluetoothGatt.readCharacteristic(Char);
			if (bDebug)
				Log.d(TAG, "readCharacteristic() - Char=" + Char);
			return result;
		}
		return false;
	}

	public void DummyReadForSecLevelCheck(BluetoothDevice device) {
		boolean result = false;
		if (mBluetoothGatt != null && device != null) {
			BluetoothGattService disService = mBluetoothGatt.getService(device,
					DIS_UUID);
			Log.v(TAG, "disService : " + disService.getUuid());

			if (disService == null) {
				showMessage("Dis service not found!");
				return;
			}

			BluetoothGattCharacteristic firmwareIdcharc = disService
					.getCharacteristic(FIRMWARE_REVISON_UUID);
			if (firmwareIdcharc == null) {
				showMessage("firmware revision charateristic not found!");
				return;
			}
			result = mBluetoothGatt.readCharacteristic(firmwareIdcharc);

			if (result == false) {
				showMessage("firmware revision reading is failed!");
			} else {
				Log.v(TAG, "firmwareIdcharc : " + firmwareIdcharc);
			}
		}

	}

	public void WriteDevice(UUID serviceUUID, UUID charUUID, byte[] data) {

		BluetoothGattService service = findService(serviceUUID);

		if (service == null) {

			// for (BluetoothGattService service : services) {
			// Log.v(TAG,"service "+service.getUuid().toString());
			// List<BluetoothGattCharacteristic> characteristics = service
			// .getCharacteristics();
			// for (BluetoothGattCharacteristic characteristic :
			// characteristics) {
			// Log.v(TAG,"characteristic "+characteristic.getUuid().toString());
			// }
			// }

			showMessage("Device service not found! " + serviceUUID.toString());
			return;
		}
		BluetoothGattCharacteristic characteristic = findCharacteristic(
				charUUID, service);

		if (characteristic == null) {
			showMessage("Device charateristic not found!");
			return;
		}
		boolean status = false;
		int storedLevel = characteristic.getWriteType();
		if (bDebug) {
			Log.d(TAG, "WriteDevice storedLevel=" + storedLevel);
			Log.d(TAG, "WriteDevice data=" + data.toString());
		}
		characteristic.setValue(data);

		characteristic
				.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
		status = mBluetoothGatt.writeCharacteristic(characteristic);
		if (bDebug)
			Log.d(TAG, "WriteDevice - status = " + status);

	}

	public void EnableDeviceNoti(UUID serviceUUID, UUID charUUID) {
		if (mDevice != null) {
			boolean result = false;

			BluetoothGattService service = findService(serviceUUID);
			if (service == null) {
				showMessage("service not found! " + serviceUUID.toString());
				return;
			}

			BluetoothGattCharacteristic characteristic = findCharacteristic(
					charUUID, service);

			if (characteristic == null) {
				showMessage("charateristic not found!");
				return;
			}
			List<BluetoothGattDescriptor> descriptors = characteristic
					.getDescriptors();
			for (BluetoothGattDescriptor descriptor : descriptors) {
				Log.i(TAG, "descriptors " + descriptor.getUuid());
			}
			BluetoothGattDescriptor descriptor = (BluetoothGattDescriptor) characteristic
					.getDescriptor(CCC);
			if (descriptor == null) {
				Log.e(TAG, "CCC for charateristic not found!");
				return;
			}

			result = mBluetoothGatt.readDescriptor(descriptor);

			if (result == false) {
				Log.e(TAG, "readDescriptor() is failed");
				return;
			} else {
				if (bDebug)
					Log.d(TAG, "EnableDeviceNoti()");
			}
		}
	}

	public boolean readRssi() {
		if (mDevice != null) {
			mBluetoothGatt.readRemoteRssi(mDevice);
			return true;
		}

		return false;
	}

	public boolean isBLEDevice(BluetoothDevice iDevice) {
		boolean result = false;
		
			result = mBluetoothGatt.isBLEDevice(iDevice);
		
		if (bDebug)
			Log.d(TAG, "isBLEDevice after" + result);
		return result;
	}

	void checkPropertieStyle(int properties) {
		switch (properties) {

		case BluetoothGattCharacteristic.PROPERTY_BROADCAST:
			Log.v(TAG, "BluetoothGattCharacteristic.PROPERTY_BROADCAST");
			break;
		case BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS:
			Log.v(TAG, "BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS");
			break;
		case BluetoothGattCharacteristic.PROPERTY_INDICATE:
			Log.v(TAG, "BluetoothGattCharacteristic.PROPERTY_INDICATE");
			break;
		case BluetoothGattCharacteristic.PROPERTY_NOTIFY:
			Log.v(TAG, "BluetoothGattCharacteristic.PROPERTY_NOTIFY");
			break;
		case BluetoothGattCharacteristic.PROPERTY_READ:
			Log.v(TAG, "BluetoothGattCharacteristic.PROPERTY_NOTIFY");
			break;
		case BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE:
			Log.v(TAG, "BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE");
			break;
		case BluetoothGattCharacteristic.PROPERTY_WRITE:
			Log.v(TAG, "BluetoothGattCharacteristic.PROPERTY_WRITE");
			break;
		case BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE:
			Log.v(TAG, "BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE");
			break;
		}
	}

	void checkGattStatus(int status) {
		switch (status) {
		case BluetoothGatt.GATT_ALREADY_OPEN:
			Log.v(TAG, "BluetoothGatt.GATT_ALREADY_OPEN");
			break;
		case BluetoothGatt.GATT_ERROR:
			Log.v(TAG, "BluetoothGatt.GATT_ERROR");
			break;
		case BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION:
			Log.v(TAG, "BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION");
			break;
		case BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION:
			Log.v(TAG, "BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION");
			break;
		case BluetoothGatt.GATT_INTERNAL_ERROR:
			Log.v(TAG, "BluetoothGatt.GATT_INTERNAL_ERROR");
			break;
		case BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH:
			Log.v(TAG, "BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH");
			break;
		case BluetoothGatt.GATT_INVALID_OFFSET:
			Log.v(TAG, "BluetoothGatt.GATT_INVALID_OFFSET");
			break;
		case BluetoothGatt.GATT_NO_RESOURCES:
			Log.v(TAG, "BluetoothGatt.GATT_NO_RESOURCES");
			break;
		case BluetoothGatt.GATT_READ_NOT_PERMITTED:
			Log.v(TAG, "BluetoothGatt.GATT_READ_NOT_PERMITTED");
			break;
		case BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED:
			Log.v(TAG, "BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED");
			break;
		case BluetoothGatt.GATT_SUCCESS:
			Log.v(TAG, "BluetoothGatt.GATT_SUCCESS");
			break;
		case BluetoothGatt.GATT_WRITE_NOT_PERMITTED:
			Log.v(TAG, "BluetoothGatt.GATT_WRITE_NOT_PERMITTED");
			break;
		default:
			Log.v(TAG, "BluetoothGatt status unkonwn :" + status);
			break;

		}
	}

	public void enableWristbandNotification() {
		Log.v(TAG, "Enable Notification PE128_CHAR_RCVD");
		this.EnableDeviceNoti(PE128_SERVICE, PE128_CHAR_RCVD);
		// try {
		// Thread.sleep(1000);
		Log.v(TAG, "Enable Notification PE128_CHAR_STREAMING Service : "
				+ PE128_NOTI_SERVICE.toString() + " Char : "
				+ PE128_CHAR_STREAMING.toString());
		// this.EnableDeviceNoti(iDevice, PE128_NOTI_SERVICE,
		// PE128_CHAR_STREAMING);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

	// TO-DO
	// miss RETURN PREFIX
	public int checkPrefix(byte[] data) {
		// if(data.length<9)
		// return -1;
		boolean ret = false;
		for (int i = 0; i < 9; i++) {
			if (data[i] == SET_TARGET_RETURN_PREFIX[i]) {
				ret = true;
			} else {
				ret = false;
				break;
			}
		}
		// if(data[data.length-1]==0x55 && ret) ret = true;
		if (ret)
			return DEVICE_RETURN_TARGET;
		ret = false;
		for (int i = 0; i < 9; i++) {

			if (data[i] == SET_SLEEP_RETURN_PREFIX[i]) {
				ret = true;
			} else {
				ret = false;
				break;
			}
		}
		// if(data[data.length-1]==0x55) ret = true;
		if (ret)
			return DEVICE_RETURN_SLEEP;
		ret = false;
		for (int i = 0; i < 9; i++) {
			if (data[i] == SET_PROFILE_RETURN_PREFIX[i]) {
				ret = true;
			} else {
				ret = false;
				break;
			}
		}
		// if(data[data.length-1]==0x55) ret = true;
		if (ret)
			return DEVICE_RETURN_PROFILE;

		ret = false;
		for (int i = 0; i < 9; i++) {
			if (data[i] == SET_TIME_RETURN_PREFIX[i]) {
				ret = true;
			} else {
				ret = false;
				break;
			}
		}
		// if(data[data.length-1]==0x55) ret = true;
		if (ret)
			return DEVICE_RETURN_TIME;

		ret = false;
		// if (data.length > TEST_VERSION_RETURN_PREFIX.length) {
		for (int i = 0; i < 16; i++) {
			if (data[i] == TEST_VERSION_RETURN_PREFIX[i]) {
				ret = true;
			} else {
				ret = false;
				break;
			}
		}
		// if(data[data.length-1]==0x55) ret = true;
		if (ret)
			return DEVICE_RETURN_VERSION;
		// }

		ret = false;
		for (int i = 0; i < 8; i++) {
			if (data[i] == TEST_SERIAL_RETURN_PREFIX[i]) {
				ret = true;
			} else {
				ret = false;
				break;
			}
		}
		if (ret)
			return DEVICE_RETURN_SERIAL;

		return 0;
	}

	public void printPrefix(int ret) {
		switch (ret) {
		case DEVICE_RETURN_TIME:
			Log.d(TAG, "DEVICE_RETURN_TIME");
			break;
		case DEVICE_RETURN_TARGET:
			Log.d(TAG, "DEVICE_RETURN_TARGET");
			break;
		case DEVICE_RETURN_SLEEP:
			Log.d(TAG, "DEVICE_RETURN_SLEEP");
			break;
		case DEVICE_RETURN_PROFILE:
			Log.d(TAG, "DEVICE_RETURN_PROFILE");
			break;
		case DEVICE_RETURN_VERSION:
			Log.d(TAG, "DEVICE_RETURN_VERSION");
			break;
		case DEVICE_RETURN_SERIAL:
			Log.d(TAG, "DEVICE_RETURN_SERIAL");
			break;
		default:
			Log.d(TAG, "Unknown");
			break;
		}
	}

	private BluetoothGattService findService(UUID serviceUUID) {
		if (pe128Service != null) {
			if (pe128Service.getUuid().equals(serviceUUID)) {
				return pe128Service;
			}

		}
		if (pe128NotiService != null) {
			if (pe128NotiService.getUuid().equals(serviceUUID)) {
				return pe128NotiService;
			}

		}

		else if (indicateService != null) {
			if (indicateService.getUuid().equals(serviceUUID)) {
				return indicateService;
			}
		} else if (mBluetoothGatt != null & mDevice!=null) {
			return mBluetoothGatt.getService(mDevice,serviceUUID);
		}
		return null;
	}

	private BluetoothGattCharacteristic findCharacteristic(UUID charUUID,
			BluetoothGattService service) {
		if (charUUID.equals(PE128_CHAR_STREAMING)) {
			return pe128Streaming;
		} else if (charUUID.equals(PE128_CHAR_RCVD)) {
			return pe128Receiver;
		} else if (charUUID.equals(PE128_CHAR_XFER)) {
			return pe128Transfer;
		} else {
			return service.getCharacteristic(charUUID);

		}
	}

}
