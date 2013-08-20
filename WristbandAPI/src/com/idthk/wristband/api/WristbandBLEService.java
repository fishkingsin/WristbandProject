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

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

public class WristbandBLEService extends Service {
	// set true to print debug message;
	static final boolean bDebug = false;

	private final static String TAG = WristbandBLEService.class.getSimpleName();

	// protocol
	// final static byte[] SET_DEMAND_PREFIX = { (byte) 0xAA, (byte) 0x50,
	// (byte) 0xF1, (byte) 0x00, (byte) 0x04, (byte) 0x02, (byte) 0x01,
	// (byte) 0x06, (byte) 0x01, (byte) 0x55 };

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

	private static final long SCAN_PERIOD = 1000;

	private BluetoothAdapter mBtAdapter = null;
	public BluetoothGatt mBluetoothGatt = null;
	// public BluetoothDevice mDevice = null;
	private Handler mActivityHandler = null;
	private Handler mDeviceListHandler = null;
	public boolean isNoti = false;

	private boolean mScanning;

	// private Handler mHandler;
	public String deviceName;
	private String mBluetoothDeviceAddress;
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
		// mHandler = new Handler();

		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBtAdapter = bluetoothManager.getAdapter();

		// BluetoothGattAdapter.getProfileProxy(this, mProfileServiceListener,
		// BluetoothGattAdapter.GATT);

	}

	public void setActivityHandler(Handler handler) {
		if (bDebug)
			Log.d(TAG, "Activity Handler set");
		mActivityHandler = handler;
	}

	public void setDeviceListHandler(Handler handler) {
		if (bDebug)
			Log.d(TAG, "Device List Handler set");
		mDeviceListHandler = handler;
	}

	@Override
	public void onDestroy() {
		if (bDebug)
			Log.d(TAG, "onDestroy()");
		// if (mBtAdapter != null && mBluetoothGatt != null) {
		// BluetoothGattAdapter.closeProfileProxy(BluetoothGattAdapter.GATT,
		// mBluetoothGatt);
		// }
		super.onDestroy();
	}

	/**
	 * GATT client callbacks
	 */
	private BluetoothGattCallback mGattCallbacks = new BluetoothGattCallback() {

		/*
		 * public void onAppRegistered(int status) { Log.v(TAG,
		 * "onAppRegistered ()"); checkGattStatus(status); }
		 * 
		 * @Override public void onScanResult(BluetoothDevice device, int rssi,
		 * byte[] scanRecord) { if(bDebug)Log.d(TAG, "onScanResult() - device="
		 * + device + ", rssi=" + rssi); if (!checkIfBroadcastMode(scanRecord))
		 * { mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(
		 * device.getAddress());
		 * 
		 * if (device.getName().charAt(0) == 'A') { connect( false); Bundle
		 * mBundle = new Bundle(); Message msg =
		 * Message.obtain(mDeviceListHandler, GATT_DEVICE_FOUND_MSG);
		 * mBundle.putParcelable(BluetoothDevice.EXTRA_DEVICE, device); //
		 * mBundle.putInt(EXTRA_RSSI, rssi); // mBundle.putInt(EXTRA_SOURCE,
		 * DEVICE_SOURCE_SCAN); msg.setData(mBundle); msg.sendToTarget(); }
		 * 
		 * } else Log.i(TAG, "device =" + device +
		 * " is in Brodacast mode, hence not displaying"); }
		 */
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			if (bDebug)
				Log.d(TAG, " Client onConnectionStateChange (" + status + ")");
			// Device has been connected - start service discovery
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				Log.v(TAG, "newState STATE_CONNECTED");
				Bundle mBundle = new Bundle();
				Message msg = Message.obtain(mActivityHandler, BLE_CONNECT_MSG);
				// mBundle.putString(BluetoothDevice.EXTRA_DEVICE,
				// device.getAddress());
				msg.setData(mBundle);
				msg.sendToTarget();
				// ParcelUuid uuids[] = device.getUuids();
				// mDevice = device;
				gatt.discoverServices();

			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				Bundle mBundle = new Bundle();
				Message msg = Message.obtain(mActivityHandler,
						BLE_DISCONNECT_MSG);
				// mBundle.putString(BluetoothDevice.EXTRA_DEVICE,
				// device.getAddress());
				msg.setData(mBundle);
				msg.sendToTarget();
				Log.v(TAG, "newState STATE_DISCONNECTED");
			} else {
				Log.v(TAG, "newState " + newState);
			}
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
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
			if (characteristic.getUuid().equals(
					WristbandGattAttributes.INDICATOR_UUID)) {
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
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				if (bDebug)
					Log.d(TAG, "onServicesDiscovered (" + status + ")");
			} else {
				Log.w(TAG, "onServicesDiscovered received: " + status);
			}
			List<BluetoothGattService> services = gatt.getServices();

			// scan through the list to check the uuid if match the notification
			// uuid

			for (BluetoothGattService service : services) {
				if (bDebug)
					Log.v(TAG, "Services : " + service.getUuid());
				if (service.getUuid().equals(
						WristbandGattAttributes.PE128_SERVICE)) {
					Log.v(TAG, "Set pe128Service");
					WristbandGattAttributes.pe128Service = service;
				}
				if (service.getUuid().equals(
						WristbandGattAttributes.PE128_NOTI_SERVICE)) {
					Log.v(TAG, "Set pe128NotiService");
					WristbandGattAttributes.pe128NotiService = service;
				}

				else if (service.getUuid().equals(
						WristbandGattAttributes.INDICATOR_SERVICE_UUID)) {
					Log.v(TAG, "Set indicateService");
					WristbandGattAttributes.indicateService = service;
				}

				List<BluetoothGattCharacteristic> characteristics = service
						.getCharacteristics();
				for (BluetoothGattCharacteristic characteristic : characteristics) {
					if (bDebug)
						Log.v(TAG,
								"Characteristic : " + characteristic.getUuid());
					if (characteristic.getUuid().equals(
							WristbandGattAttributes.PE128_CHAR_STREAMING)) {
						WristbandGattAttributes.pe128Streaming = characteristic;
					} else if (characteristic.getUuid().equals(
							WristbandGattAttributes.PE128_CHAR_RCVD)) {
						WristbandGattAttributes.pe128Receiver = characteristic;
					} else if (characteristic.getUuid().equals(
							WristbandGattAttributes.PE128_CHAR_XFER)) {
						WristbandGattAttributes.pe128Transfer = characteristic;
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

			// mBundle.putParcelable(BluetoothDevice.EXTRA_DEVICE, mDevice);
			msg.setData(mBundle);
			msg.sendToTarget();
			// DummyReadForSecLevelCheck(device);
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			if (bDebug)
				Log.d(TAG, "onCharacteristicWrite()");
			checkGattStatus(status);
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
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

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
			Log.i(TAG, "onDescriptorWrite status: " + status);
			Bundle mBundle = new Bundle();
			Message msg = Message.obtain(mActivityHandler, BLE_VALUE_MSG);
			// String s = "";
			// s = String.format("%02X", descriptor.getValue());

			// Log.v(TAG, "descriptor.getValue() = " + s);
			mBundle.putByteArray(EXTRA_VALUE, descriptor.getValue());

			msg.setData(mBundle);
			msg.sendToTarget();
			checkGattStatus(status);
		}

		@Override
		public void onDescriptorRead(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
			Log.i(TAG, "onDescriptorRead : " + status);

			checkGattStatus(status);
			BluetoothGattCharacteristic mCCC = descriptor.getCharacteristic();
			Log.i(TAG, "Registering for notification UUID : " + mCCC.getUuid());

			String s = "";

			try {
				s = new String(descriptor.getValue(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated
			} catch (Exception e) {
				e.printStackTrace();
			}

			Log.v(TAG, "descriptor.getValue() = " + s);
			enableNotification(mCCC, true);
			// boolean isenabled = enableNotification(true, mCCC);
			// Log.i(TAG, "Notification status =" + isenabled); // should fire
			// enable
			// success
		}

		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			Log.i(TAG, "onRssiRead rssi value is " + rssi);
			Bundle mBundle = new Bundle();
			Message msg = Message.obtain(mActivityHandler,
					GATT_CHARACTERISTIC_RSSI_MSG);
			// mBundle.putParcelable(EXTRA_DEVICE, device);
			mBundle.putInt(EXTRA_RSSI, rssi);
			mBundle.putInt(EXTRA_STATUS, status);
			msg.setData(mBundle);
			msg.sendToTarget();
		}

		@Override
		public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {

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
		if (mBluetoothGatt != null) {
			BluetoothGattService mDI = mBluetoothGatt
					.getService(WristbandGattAttributes.DEVICE_INFORMATION);
			if (mDI == null) {
				Log.e(TAG, "Device Information Service Not Found!!!");
				return;
			}
			BluetoothGattCharacteristic mSNS = mDI
					.getCharacteristic(WristbandGattAttributes.SERIAL_NUMBER_STRING);
			if (mSNS == null) {
				Log.e(TAG, "Serial Number String Characteristic Not Found!!!");
				return;
			}
			mBluetoothGatt.readCharacteristic(mSNS);
		}
	}

	public void ReadDevice(UUID serviceUUID, UUID charUUID) {
		if (mBluetoothGatt != null) {
			Log.i(TAG, "ReadDevice charachteristic");
			BluetoothGattService mDI = mBluetoothGatt.getService(serviceUUID);
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

	public void enableNotification(BluetoothGattCharacteristic characteristic,
			boolean enabled) {
		if (mBtAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		boolean success = mBluetoothGatt.setCharacteristicNotification(
				characteristic, enabled);
		if (!success) {
			Log.v(TAG, "setCharacteristicNotification failed ");
		}
		// This is specific to Heart Rate Measurement.
		// if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
		BluetoothGattDescriptor descriptor = characteristic
				.getDescriptor(WristbandGattAttributes.CCC);
		descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
		mBluetoothGatt.writeDescriptor(descriptor);
		// }
	}

	// public boolean enableNotification(boolean enable,
	// BluetoothGattCharacteristic characteristic) {
	//
	// if (mBluetoothGatt == null)
	// return false;
	// if (!mBluetoothGatt.setCharacteristicNotification(characteristic,
	// enable))
	// return false;
	// List<BluetoothGattDescriptor> descriptors = characteristic
	// .getDescriptors();
	// for (BluetoothGattDescriptor descriptor : descriptors) {
	// Log.i(TAG, "descriptors " + descriptor.getUuid());
	// }
	// BluetoothGattDescriptor clientConfig = characteristic
	// .getDescriptor(WristbandGattAttributes.CCC);
	// if (clientConfig == null)
	// return false;
	//
	// if (enable) {
	//
	// if (characteristic.getProperties() ==
	// BluetoothGattCharacteristic.PROPERTY_INDICATE) {
	// Log.i(TAG, "enable notification ENABLE_INDICATION_VALUE");
	// clientConfig
	// .setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
	// } else {
	// Log.i(TAG, "enable notification ENABLE_NOTIFICATION_VALUE");
	// clientConfig
	// .setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
	// }
	// } else {
	// Log.i(TAG, "disable notification");
	// clientConfig
	// .setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
	// }
	// return mBluetoothGatt.writeDescriptor(clientConfig);
	//
	// }

	private void showMessage(String msg) {
		Log.e(TAG, msg);
	}


	public boolean connect(boolean autoconnect, final String address) {
		if (mBtAdapter == null || address == null) {
			Log.w(TAG,
					"BluetoothAdapter not initialized or unspecified address.");
			return false;
		}

		// Previously connected device. Try to reconnect.
		if (mBluetoothDeviceAddress != null
				&& address.equals(mBluetoothDeviceAddress)
				&& mBluetoothGatt != null) {
			Log.d(TAG,
					"Trying to use an existing mBluetoothGatt for connection.");
			if (mBluetoothGatt.connect()) {
				Bundle mBundle = new Bundle();
				Message msg = Message.obtain(mActivityHandler, BLE_ERROR_MSG);
				mBundle.putInt(EXTRA_VALUE, BLE_CONNECT_MSG);
				msg.setData(mBundle);
				msg.sendToTarget();
				return true;
			} else {
				return false;
			}
		}

		final BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
		if (device == null) {
			Log.w(TAG, "Device not found.  Unable to connect.");
			return false;
		}
		// We want to directly connect to the device, so we are setting the
		// autoConnect
		// parameter to false.
		mBluetoothGatt = device.connectGatt(this, false, mGattCallbacks);

		Bundle mBundle = new Bundle();
		Message msg = Message.obtain(mActivityHandler, BLE_ERROR_MSG);
		mBundle.putInt(EXTRA_VALUE, BLE_CONNECT_MSG);
		msg.setData(mBundle);
		msg.sendToTarget();

		return true;
	}

	// public void disconnect(BluetoothDevice device) {
	// if (mBluetoothGatt != null) {
	// mBluetoothGatt.cancelConnection(device);
	// }
	// }
	public void disconnect() {
		// if (mBluetoothGatt != null && mDevice != null) {
		// // mBluetoothGatt.cancelConnection(mDevice);
		// }
		if (mBtAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.disconnect();
	}

	public void close() {
		if (mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.close();
		mBluetoothGatt = null;
	}

	public void scan(final boolean enable) {
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			// mHandler.postDelayed(new Runnable() {
			// @Override
			// public void run() {
			// mScanning = false;
			// mBtAdapter.stopLeScan(mLeScanCallback);
			// // invalidateOptionsMenu();
			// }
			// }, SCAN_PERIOD);

			mScanning = true;
			mBtAdapter.startLeScan(mLeScanCallback);
		} else {
			mScanning = false;
			mBtAdapter.stopLeScan(mLeScanCallback);
		}
		// invalidateOptionsMenu();
	}

	// Device scan callback.
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
				byte[] scanRecord) {
			Log.d(TAG, "onLeScan " + device.getName());
			if (device.getName().charAt(0) == 'A') {
				deviceName = device.getName();
				String address = device.getAddress();
				// mDevice = mBtAdapter.getRemoteDevice(device.getAddress());
				scan(false);
				connect(false, address);
				Bundle mBundle = new Bundle();
				Message msg = Message.obtain(mDeviceListHandler,
						GATT_DEVICE_FOUND_MSG);
				mBundle.putParcelable(BluetoothDevice.EXTRA_DEVICE, device);

				msg.setData(mBundle);
				msg.sendToTarget();
			}
		}
	};

	public BluetoothGattService getService(UUID ServiceUUID) {

		if (mBluetoothGatt != null) {
			if (bDebug)
				Log.d(TAG, "getService() - ServiceUUID=" + ServiceUUID);
			return mBluetoothGatt.getService(ServiceUUID);
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

	/*
	 * Request a read on a given {@code BluetoothGattCharacteristic}. The read
	 * result is reported asynchronously through the {@code
	 * BluetoothGattCallback
	 * #onCharacteristicRead(android.bluetooth.BluetoothGatt,
	 * android.bluetooth.BluetoothGattCharacteristic, int)} callback.
	 * 
	 * @param characteristic The characteristic to read from.
	 */
	public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBtAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.readCharacteristic(characteristic);
	}

/*	public void DummyReadForSecLevelCheck(BluetoothDevice device) {
		boolean result = false;
		if (mBluetoothGatt != null && device != null) {
			 BluetoothGattService disService =
			 mBluetoothGatt.getService(device,
			 WristbandGattAttributes.DIS_UUID);
			 Log.v(TAG, "disService : " + disService.getUuid());
			
			 if (disService == null) {
			 showMessage("Dis service not found!");
			 return;
			 }
			
			 BluetoothGattCharacteristic firmwareIdcharc = disService
			 .getCharacteristic(WristbandGattAttributes.FIRMWARE_REVISON_UUID);
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

	}*/

	public void WriteDevice(UUID serviceUUID, UUID charUUID, byte[] data) {
		if (mBluetoothGatt != null) {
			BluetoothGattService service = mBluetoothGatt
					.getService(serviceUUID);

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

				showMessage("Device service not found! "
						+ serviceUUID.toString());
				return;
			}
			BluetoothGattCharacteristic characteristic = service
					.getCharacteristic(charUUID);

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
	}

	/**
	 * Enables or disables notification on a give characteristic.
	 * 
	 * @param characteristic
	 *            Characteristic to act on.
	 * @param enabled
	 *            If true, enable notification. False otherwise.
	 */
	public void setCharacteristicNotification(
			BluetoothGattCharacteristic characteristic, boolean enabled) {
		if (mBtAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		boolean success = mBluetoothGatt.setCharacteristicNotification(
				characteristic, enabled);
		if (!success) {
			Log.v(TAG, "setCharacteristicNotification failed ");
		}

		// This is specific to Heart Rate Measurement.
		// if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
		BluetoothGattDescriptor descriptor = characteristic
				.getDescriptor(WristbandGattAttributes.CCC);
		descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
		mBluetoothGatt.writeDescriptor(descriptor);
		// }
	}

	public void EnableDeviceNoti(UUID serviceUUID, UUID charUUID) {
		if (mBluetoothGatt != null) {
			boolean result = false;

			BluetoothGattService service = mBluetoothGatt
					.getService(serviceUUID);
			if (service == null) {
				showMessage("service not found! " + serviceUUID.toString());
				return;
			}

			BluetoothGattCharacteristic characteristic = service
					.getCharacteristic(charUUID);
			// final int charaProp = characteristic.getProperties();
			if (characteristic == null) {
				showMessage("charateristic not found!");
				return;
			}
			// List<BluetoothGattDescriptor> descriptors = characteristic
			// .getDescriptors();
			// for (BluetoothGattDescriptor descriptor : descriptors) {
			// Log.i(TAG, "descriptors " + descriptor.getUuid());
			// }
			BluetoothGattDescriptor descriptor = (BluetoothGattDescriptor) characteristic
					.getDescriptor(WristbandGattAttributes.CCC);
			if (descriptor == null) {
				Log.e(TAG, "CCC for charateristic not found!");
				return;
			}

			setCharacteristicNotification(characteristic, true);
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
		if (mBluetoothGatt != null) {
			mBluetoothGatt.readRemoteRssi();
			return true;
		}

		return false;
	}

	public boolean isBLEDevice(BluetoothDevice iDevice) {
		boolean result = false;

		// result = mBluetoothGatt.isBLEDevice(iDevice);

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
		// case BluetoothGatt.GATT_ALREADY_OPEN:
		// Log.v(TAG, "BluetoothGatt.GATT_ALREADY_OPEN");
		// break;
		// case BluetoothGatt.GATT_ERROR:
		// Log.v(TAG, "BluetoothGatt.GATT_ERROR");
		// break;
		case BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION:
			Log.v(TAG, "BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION");
			break;
		case BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION:
			Log.v(TAG, "BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION");
			break;
		// case BluetoothGatt.GATT_INTERNAL_ERROR:
		// Log.v(TAG, "BluetoothGatt.GATT_INTERNAL_ERROR");
		// break;
		case BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH:
			Log.v(TAG, "BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH");
			break;
		case BluetoothGatt.GATT_INVALID_OFFSET:
			Log.v(TAG, "BluetoothGatt.GATT_INVALID_OFFSET");
			break;
		// case BluetoothGatt.GATT_NO_RESOURCES:
		// Log.v(TAG, "BluetoothGatt.GATT_NO_RESOURCES");
		// break;
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

	public List<BluetoothGattService> getSupportedGattServices() {
		if (mBluetoothGatt == null)
			return null;

		return mBluetoothGatt.getServices();
	}

	public void enableWristbandNotification() {
		Log.v(TAG, "Enable Notification PE128_CHAR_RCVD");

		this.EnableDeviceNoti(WristbandGattAttributes.PE128_SERVICE,
				WristbandGattAttributes.PE128_CHAR_RCVD);
		// try {
		// Thread.sleep(1000);
		Log.v(TAG,
				"Enable Notification PE128_CHAR_STREAMING Service : "
						+ WristbandGattAttributes.PE128_NOTI_SERVICE.toString()
						+ " Char : "
						+ WristbandGattAttributes.PE128_CHAR_STREAMING
								.toString());
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
			if (data[i] == WristbandGattAttributes.SET_TARGET_RETURN_PREFIX[i]) {
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

			if (data[i] == WristbandGattAttributes.SET_SLEEP_RETURN_PREFIX[i]) {
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
			if (data[i] == WristbandGattAttributes.SET_PROFILE_RETURN_PREFIX[i]) {
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
			if (data[i] == WristbandGattAttributes.SET_TIME_RETURN_PREFIX[i]) {
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
			if (data[i] == WristbandGattAttributes.TEST_VERSION_RETURN_PREFIX[i]) {
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
			if (data[i] == WristbandGattAttributes.TEST_SERIAL_RETURN_PREFIX[i]) {
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

}
