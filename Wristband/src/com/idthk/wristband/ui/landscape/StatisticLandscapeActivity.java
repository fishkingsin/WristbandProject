package com.idthk.wristband.ui.landscape;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.bostonandroid.datepreference.DatePreference;

import com.idthk.wristband.ui.Utilities;
import com.idthk.wristband.database.DatabaseHandler;
import com.idthk.wristband.database.Record;
import com.idthk.wristband.graphview.RoundBarGraphView;
import com.idthk.wristband.ui.ActivityStatisticTabFragment;
import com.idthk.wristband.ui.Main;
import com.idthk.wristband.ui.R;
import com.idthk.wristband.ui.SleepStatisticTabFragment;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.LineGraphView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
//import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
//import android.widget.Toast;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class StatisticLandscapeActivity extends LandscapeActivity {
	static final String TAG = "SleepLandscapeActivity";
	public static final String TYPE = "type";
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.landsape_main);

		nextEntryButton = (View) findViewById(R.id.btn_next_entry);
		prevEntryButton = (View) findViewById(R.id.btn_prev_entry);
		nextEntryButton.setOnClickListener(this);
		prevEntryButton.setOnClickListener(this);
		Bundle bundle = getIntent().getBundleExtra(
				LandscapeActivity.LANDSCAPE_BUNDLE);
		displayType = bundle.getString(StatisticLandscapeActivity.TYPE);
		Log.v(TAG, displayType);

		Utilities.publishGraph((Context)this , getWindow().getDecorView().getRootView(),
				((ViewGroup) findViewById(R.id.graph1)),
				displayType);
		checkButtonVisible();
	}


	

}
