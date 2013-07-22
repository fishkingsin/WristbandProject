package com.idthk.wristband.ui.landscape;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import com.idthk.wristband.ui.ActivityStatisticTabFragment;
import com.idthk.wristband.ui.Main;
import com.idthk.wristband.ui.R;
import com.idthk.wristband.ui.Utilities;

public class ActivityLandscapeActivity extends LandscapeActivity {
	static final String TAG = "ActivityLandscapeActivity";
	TextView mStepIndicatedTV;
	TextView mCaloriesIndicatedTV;
	TextView mDistancesIndicatedTV;
	TextView mActivityIndicatedTV;
	private long timeDiff  = 0;
	IntentFilter intentFilter = new IntentFilter(
            "android.intent.action.MAIN");

	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //extract our message from intent
            step = intent.getIntExtra(Main.TAG_STEPS,0);
            calories = intent.getIntExtra(Main.TAG_CALROIES,0);
            distance = intent.getFloatExtra(Main.TAG_DISTANCE,0);
            activitytime = intent.getIntExtra(Main.TAG_ACTIVITYTIME,0);
            //log our message value
            
            runOnUiThread(new Runnable() {
				public void run() {
					onStreamMessage(step , calories , distance , activitytime);
				}
            });
        }

		
    };

    
    @Override
	public void onResume(){
	    super.onResume();
	    this.registerReceiver(mReceiver, intentFilter);
	}

	@Override
	public void onPause(){
	    super.onPause();
	    this.unregisterReceiver(mReceiver);
	}
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.landsape_main_activity);
		super.onCreate(savedInstanceState);

		Calendar now = Calendar.getInstance();

		Utilities.targetDate().setTime(now.getTime());
		displayType = ActivityStatisticTabFragment.TAB_DAY;
		Utilities.publishGraph((Context) this, getWindow().getDecorView()
				.getRootView(), ((ViewGroup) findViewById(R.id.graph1)),
				displayType);

		mActivityIndicatedTV = ((TextView) findViewById(R.id.activity_indicated_textview));
		mStepIndicatedTV = ((TextView) findViewById(R.id.steps_indicated_textview));
		mCaloriesIndicatedTV = ((TextView) findViewById(R.id.calories_indicated_textview));
		mDistancesIndicatedTV = ((TextView) findViewById(R.id.distances_indicated_textview));
		timeDiff = System.currentTimeMillis();
		

	}

	@Override
	public void onStreamMessage(int steps, int calories, float distance,
			int activityTime) {
		mActivityIndicatedTV.setText(String.valueOf(activityTime));
		mStepIndicatedTV.setText(String.valueOf(steps));
		mCaloriesIndicatedTV.setText(String.valueOf(calories));
		mDistancesIndicatedTV.setText(String.valueOf(distance));
		long curr =  System.currentTimeMillis() - timeDiff; 
		if(curr>7000)
		{
			Calendar now = Calendar.getInstance();
			Utilities.targetDate().setTime(now.getTime());
			displayType = ActivityStatisticTabFragment.TAB_DAY;
			Utilities.publishGraph((Context) this, getWindow().getDecorView()
					.getRootView(), ((ViewGroup) findViewById(R.id.graph1)),
					displayType);
			timeDiff = System.currentTimeMillis();
		}
	}

}
