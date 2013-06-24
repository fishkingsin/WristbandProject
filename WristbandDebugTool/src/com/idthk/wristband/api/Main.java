package com.idthk.wristband.api;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

//Only UI handling
public class Main extends BLEBaseActivity {
	static final String TAG = "Main";
	private String currentText = null;

	TextView myTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		myTextView = (TextView) findViewById(R.id.textView);
		myTextView.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				final ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view);

				scrollView.scrollTo(
						0,
						(int) (myTextView.getHeight() + myTextView.getHeight() * 0.2));
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}
		});
		((Button) findViewById(R.id.btn_connect))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showMessage("Trying to connect BLE");
						showMessage("Turn on Device now");
//						connect();
					}
				});

		((Button) findViewById(R.id.btn_disconnect))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {

//						disconnect();

					}
				});
		((Button) findViewById(R.id.btn_ondemand))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						setDemand();
					}
				});

		((Button) findViewById(R.id.btn_startstream))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (mService != null) {
							startStream();
						}
					}
				});
		((Button) findViewById(R.id.btn_stopstream))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (mService != null) {
							stopStream();
						}
					}
				});
		((Button) findViewById(R.id.btn_version))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						getVersion();
					}
				});
		((Button) findViewById(R.id.writetarget))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						int step = 94184;
						setTarget(60, 1, step, 6, 1869);

					}
				});
		((Button) findViewById(R.id.btn_writeportfile))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						setProfile(0, 1985, 8, 75, 178);
					}
				});
		((Button) findViewById(R.id.btn_writesleep))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						setSleep(12, 0, 23, 0, 3);
					}
				});
		((Button) findViewById(R.id.btn_writetime))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Calendar c = Calendar.getInstance();
						// ////showMessage("Set Time to wristband "+c.toString());
						setTime(c.get(Calendar.YEAR) - 2000,
								c.get(Calendar.MONTH) + 1,
								c.get(Calendar.DAY_OF_MONTH),
								c.get(Calendar.HOUR_OF_DAY),
								c.get(Calendar.MINUTE), c.get(Calendar.SECOND),
								c.get(Calendar.WEDNESDAY));
					}
				});
		((Button) findViewById(R.id.btn_clearlog))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						clearLog();
					}
				});
		((Button) findViewById(R.id.btn_enable_notify))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						
							mService.enableWristbandNotification();
					}
				});

	}

	private void showMessage(String msg) {
		this.currentText += "\n" + msg;

		myTextView.setText(currentText);

	}

	private void clearLog() {

		this.currentText = "Log:\n";
		final TextView myTextView = (TextView) findViewById(R.id.textView);
		final ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view);

		myTextView.setText(currentText);
		scrollView.scrollTo(0, myTextView.getHeight());
	}

	private void setUiState() {

		switch (mState) {
		case BLE_PROFILE_CONNECTED:
			showMessage("STATE_CONNECTED::device name" + mService.mDevice.getName());

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
		showMessage("Connected");
		setUiState();
	}

	@Override
	public void onDisconnected() {
		showMessage("Disconnected");
		setUiState();

	}

	@Override
	public void onReady() {
		setUiState();
		showMessage("BLE Ready");
	}

	@Override
	public void onServiceDiscovered() {
		setUiState();
		showMessage("Service Discovered");
	}
	@Override
	public void onStreamMessage(int steps, int calories,
			float	 distance, int activityTime,
			int batteryLevel) {
		// TODO Auto-generated method stub
		String s = "";
		s += "Wristband Stream :\n";
		s += "steps : " + steps + "\n";
		s += "calories : " + calories + "\n";
		s += "distance : " + distance + "\n";
		s += "activityTime : " + activityTime + "\n";
		s += "batteryLevel : " + batteryLevel + "\n";
		
		showMessage(s);
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
			_msg +=  Integer.toHexString( value[i]) + " , ";
		}
		showMessage("Unknown Message");
		showMessage(s);
		showMessage(_msg);

	}

	@Override
	public void onReadVersion(int xx, int yy) {
		String s = "ReadVersion " + Integer.valueOf(xx-30) + "-" + Integer.valueOf(yy-30);
		showMessage(s);
	}
	@Override
    public void onBackPressed() {
		super.onBackPressed();
	
	}
}
