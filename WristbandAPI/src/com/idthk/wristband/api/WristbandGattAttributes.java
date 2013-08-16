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

import java.util.HashMap;
import java.util.UUID;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

/**
 * This class includes a small subset of standard GATT attributes for
 * demonstration purposes.
 */
public class WristbandGattAttributes {

	public static BluetoothGattService pe128Service;
	public static BluetoothGattService pe128NotiService;
	public static BluetoothGattCharacteristic pe128Receiver;
	public static BluetoothGattCharacteristic pe128Streaming;
	public static BluetoothGattCharacteristic pe128Transfer;
	public static BluetoothGattService indicateService;

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
	final static byte[] SET_TARGET_RETURN_PREFIX = { (byte) 0xAA, (byte) 0x42,
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

	// AA-50-F1-00-04-02-01-06- 01-55
	final static byte[] HISTORY_PREFIX = { (byte) 0xAA, (byte) 0x50,
			(byte) 0xF1, (byte) 0x00, (byte) 0x04, (byte) 0x02, (byte) 0x01,
			(byte) 0x06, (byte) 0x01, (byte) 0x55 };
	final static byte[] ACTIVITY_HISTORY_RETURN_HEADER = { (byte) 0x6D,
			(byte) 0x6D, (byte) 0x6D, (byte) 0x6D, (byte) 0x6D, (byte) 0x6D };
	final static byte[] CURRENT_ACTIVITY_HISTORY_RETURN_HEADER = { (byte) 0x7E,
			(byte) 0x7E, (byte) 0x7E, (byte) 0x7E, (byte) 0x7E, (byte) 0x7E,
			(byte) 0x6D, (byte) 0x6D, (byte) 0x6D, (byte) 0x6D, (byte) 0x6D,
			(byte) 0x6D };
	final static byte[] SLEEP_HISTORY_RETURN_HEADER = { (byte) 0x6E,
			(byte) 0x6E, (byte) 0x6E, (byte) 0x6E, (byte) 0x6E, (byte) 0x6E };
	final static byte[] HISTORY_RETURN_FOOTER = { (byte) 0x7E, (byte) 0x7E,
			(byte) 0x7E, (byte) 0x7E, (byte) 0x7E, (byte) 0x7E };

	private static HashMap<UUID, String> attributes = new HashMap();

	public static UUID PE128_SERVICE = UUID
			.fromString("c231ff01-8d74-4fa9-a7dd-13abdfe5cbff");

	public static UUID PE128_NOTI_SERVICE = UUID
			.fromString("00001810-0000-1000-8000-00805f9b34fb");

	public static UUID PE128_CHAR_STREAMING = UUID
			.fromString("00002a35-0000-1000-8000-00805f9b34fb");
	public static final UUID PE128_CHAR_RCVD = UUID
			.fromString("c231ff08-8d74-4fa9-a7dd-13abdfe5cbff");

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
	static {

		// Services
		attributes.put(PE128_SERVICE, "PE128 Servie");
		attributes.put(PE128_NOTI_SERVICE, "PE128 Notification Servie");
		attributes.put(INDICATOR_SERVICE_UUID, "PE128 Indicator Service");

		// Characteristics

		attributes.put(PE128_CHAR_STREAMING, "Stream Char");
		attributes.put(PE128_CHAR_RCVD, "Receiver Char");
		attributes.put(PE128_CHAR_XFER, "Transfer Char");
		attributes.put(DEVICE_INFORMATION, "Device Infomation Cahr");
		attributes.put(SERIAL_NUMBER_STRING, "Wristband Indicator Service");
		attributes.put(FIRMWARE_REVISON_UUID, "Wristband Indicator Service");
		attributes.put(DIS_UUID, "Wristband Indicator Service");
		attributes.put(INDICATOR_UUID, "Wristband Indicator Service");
		attributes.put(INDICATOR_UUID36, "Wristband Indicator Service");
		attributes.put(INDICATOR_UUID37, "Wristband Indicator Service");
		attributes.put(INDICATOR_UUID38, "Wristband Indicator Service");
		attributes.put(INDICATOR_UUID49, "Wristband Indicator Service");
	}

	public static String lookup(String uuid, String defaultName) {
		String name = attributes.get(uuid);
		return name == null ? defaultName : name;
	}
	
	
}
