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
//		publishGraph(((ViewGroup) findViewById(R.id.graph1)), displayType);
		Utilities.publishGraph((Context)this , getWindow().getDecorView().getRootView(),
				((ViewGroup) findViewById(R.id.graph1)),
				displayType);
		checkButtonVisible();
	}

//	public void publishGraph(ViewGroup graph, String message) {
//		// TO-DO
//		// retrive db data
//		// port to graph
//
//		graph.removeAllViews();
//		// TODO Auto-generated method stub
//		Random random = new Random();
//		GraphViewData data[] = null;
//		GraphViewSeries series = null;
//
//		RoundBarGraphView mGraphView = new RoundBarGraphView(this, "");
//
//		String hStr[] = null;
//
//		if (message.equals(SleepStatisticTabFragment.TAB_WEEK)) {
//
//		} else if (message.equals(SleepStatisticTabFragment.TAB_MONTH)) {
//
//		} else if (message.equals(SleepStatisticTabFragment.TAB_YEAR)) {
//
//		} else if (message.equals(ActivityStatisticTabFragment.TAB_DAY)) {
//
//		} else if (message.equals(ActivityStatisticTabFragment.TAB_WEEK)) {
//			DatabaseHandler db = new DatabaseHandler(this, Main.TABLE_CONTENT_ACTIVITY,
//					null, 1);
//
//			List<Record> records = db.getSumOfRecordsByWeek(1,2012);
//			data = new GraphViewData[records.size()];
//			int j = 0;
//			TextView tv = (TextView) findViewById(R.id.graph_view_title_indicator);
//			tv.setText(String.valueOf(records.get(0).getCalendar()
//					.get(Calendar.WEEK_OF_MONTH)) + " "+ String.valueOf(records.get(0).getCalendar()
//							.get(Calendar.MONTH)));
//			hStr = new String[records.size()];
//			for (Record cn : records) {
//
//				data[j] = new GraphViewData(cn.getCalendar()
//						.get(Calendar.MONTH), cn.getMinutes());
//				
//				SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
//				Date d = cn.getCalendar().getTime();
//				String dayOfTheWeek = sdf.format(d);
//				
//				hStr[j] = dayOfTheWeek;
//				j++;
//			}
//			series = new GraphViewSeries(data);
////			mGraphView.setManualYAxisBounds(44640, 0);
//			mGraphView.setHorizontalLabels(hStr);
//			mGraphView.addSeries(series);
//			
//		} else if (message.equals(ActivityStatisticTabFragment.TAB_MONTH)) {
//
//		} else if (message.equals(ActivityStatisticTabFragment.TAB_YEAR)) {
//
//			DatabaseHandler db = new DatabaseHandler(this, Main.TABLE_CONTENT_ACTIVITY,
//					null, 1);
//
//			List<Record> records = db.getSumOfRecordsByYear(2012);
//			data = new GraphViewData[records.size()];
//			int j = 0;
//			TextView tv = (TextView) findViewById(R.id.graph_view_title_indicator);
//			tv.setText(String.valueOf(records.get(0).getCalendar()
//					.get(Calendar.YEAR)));
//			hStr = new String[records.size()];
//			for (Record cn : records) {
//
//				data[j] = new GraphViewData(cn.getCalendar()
//						.get(Calendar.MONTH), cn.getMinutes());
//				hStr[j] = String.valueOf(cn.getCalendar().get(Calendar.MONTH));
//				j++;
//			}
//			series = new GraphViewSeries(data);
//			mGraphView.setManualYAxisBounds(44640, 0);
//			mGraphView.setHorizontalLabels(hStr);
//			mGraphView.addSeries(series); // data
//
//		}
//		if(mGraphView!=null)graph.addView(mGraphView);
//	}

	

}
