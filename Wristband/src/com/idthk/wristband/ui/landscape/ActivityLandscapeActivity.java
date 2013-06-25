package com.idthk.wristband.ui.landscape;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.bostonandroid.datepreference.DatePreference;

import com.idthk.wristband.database.DatabaseHandler;
import com.idthk.wristband.database.Record;
import com.idthk.wristband.graphview.RoundBarGraphView;
import com.idthk.wristband.ui.R;
import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.LineGraphView;

import android.app.Activity;
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
import android.widget.Button;

public class ActivityLandscapeActivity extends LandscapeActivity {
	static final String TAG = "ActivityLandscapeActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.landsape_main_activity);
		nextEntryButton = (View) findViewById(R.id.btn_next_entry);
		prevEntryButton = (View) findViewById(R.id.btn_prev_entry);
		nextEntryButton.setOnClickListener(this);
		prevEntryButton.setOnClickListener(this);
		Random random = new Random();
		int numBars = 100;
		LineGraphView mGraphView = new LineGraphView(this, "");
		mGraphView.setHorizontalLabels(new String[] {
				getString(R.string.start), getString(R.string.end) });
		mGraphView.setVerticalLabels(new String[] { getString(R.string.high),
				getString(R.string.middle), getString(R.string.low) });

		GraphViewData data[] = new GraphViewData[numBars];
		for (int i = 0; i < numBars; i++) {
			data[i] = new GraphViewData(i, random.nextInt(9) + 1);
		}
		GraphViewSeries series = new GraphViewSeries(data);

		mGraphView.addSeries(series); // data
		ViewGroup graph = ((ViewGroup) findViewById(R.id.graph1));
		graph.addView(mGraphView);

	}
	@Override
	public void loadPrevEntry() {
		// TODO Auto-generated method stub
		Log.v(TAG, "loadPrevEntry" );
	}
	@Override
	public void loadNextEntry() {
		// TODO Auto-generated method stub
		Log.v(TAG, "loadNextEntry" );
	}

}
