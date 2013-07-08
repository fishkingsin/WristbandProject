package com.idthk.wristband.ui.landscape;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.bostonandroid.datepreference.DatePreference;

import com.idthk.wristband.ui.Utilities;
import com.idthk.wristband.database.DatabaseHandler;
import com.idthk.wristband.database.Record;
import com.idthk.wristband.graphview.RoundBarGraphView;
import com.idthk.wristband.ui.Main;
import com.idthk.wristband.ui.R;
import com.jjoe64.graphview.BarGraphView;
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
import android.view.View.OnClickListener;
//import android.widget.Toast;
import android.view.ViewGroup;
import android.widget.Button;

public class LandscapeActivity extends Activity implements OnClickListener {
	static final String TAG = "LandscapeActivity";
	public static final String LANDSCAPE_BUNDLE = "landscape_bundle";
	OrientationEventListener orientationListener;
	static final int THRESHOLD = 5;
	public static final String LANDSCAPE_ACTIVITY_TAG = "landscape_activity";
	public static final String FINISH_APP = "finish_app";
	protected View nextEntryButton = null;
	protected View prevEntryButton = null;
	protected String displayType;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		
		
		
        int ori = intent.getIntExtra(Main.TARGET_ORIENTTION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
		setRequestedOrientation(ori);
		

		orientationListener = new OrientationEventListener(this,SensorManager.SENSOR_DELAY_UI)
		{

			@Override
			public void onOrientationChanged(int orientation) {
				// TODO Auto-generated method stub
				if(isPortrait(orientation)){
					finish();
					
		         } 
				else if(isLandscapeLeft(orientation))
				{
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				}
				else if (isLandscapeRight(orientation))
				{
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
				}
				
			}
			
		};
		
		
		

	}
	private boolean isLandscapeRight(int orientation){
        return orientation >= (90 - THRESHOLD) && orientation <= (90 + THRESHOLD);
    }
	private boolean isLandscapeLeft(int orientation){
        return orientation >= (270 - THRESHOLD) && orientation <= (270 + THRESHOLD);
    }

	private boolean isPortrait(int orientation){
		return (orientation >= (360 - THRESHOLD) && orientation <= 360) || (orientation >= 0 && orientation <= THRESHOLD);
	}
	@Override
	public void onResume(){
	    super.onResume();
	    orientationListener.enable();
	}

	@Override
	public void onPause(){
	    super.onPause();
	    orientationListener.disable();
	}
	@Override
    public void onBackPressed() {
		//override and disable back button
		Bundle conData = new Bundle();
		   conData.putString(LANDSCAPE_ACTIVITY_TAG, FINISH_APP);
		   Intent intent = new Intent();
		   intent.putExtras(conData);
		   setResult(RESULT_OK, intent);
		   finish();
		
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Log.v(TAG,arg0.toString());
		
//		if(arg0.getId() == R.id.btn_next_entry)
//		{
//			loadNextEntry();
//			
//		}
//		else if(arg0.getId() == R.id.btn_prev_entry)
//		{
//			loadPrevEntry();	
//		}
	}

//	public void loadPrevEntry() {
//		if (Utilities.prevEntryDate(displayType)) {
//			Utilities.publishGraph((Context)this , getWindow().getDecorView().getRootView(),
//					((ViewGroup) findViewById(R.id.graph1)),
//					displayType);
//		}
//		
//		checkButtonVisible();
//	}
//
//
//	public void loadNextEntry() {
//		
//		if (Utilities.nextEntryDate(displayType)) {
//			Utilities.publishGraph((Context)this , getWindow().getDecorView().getRootView(),
//					((ViewGroup) findViewById(R.id.graph1)),
//					displayType);
//		}
//		checkButtonVisible();
//
//	}
	protected void createDBTable(String string) {
		DatabaseHandler db = new DatabaseHandler(this,string,null,1);

		/**
		 * Read Operations
		 * */

		// Reading all contacts
		Log.d("Reading: ", "Reading all Record from "+string+" ...");
		List<Record> records = db.getAllRecords();

		for (Record cn : records) {
			String log = "Timestamp: " +cn.getTimeStamp()
					+ " ,Date: " + DatePreference.summaryFormatter().format(cn.getCalendar().getTime())
					+ " ,Year: " + cn.getYear()
					+ " ,Month: " + cn.getMonth()
					+ " ,Week of Year: " + cn.getWeekofYear()
					+ " ,Day: " + cn.getDay()
					+ " ,Hour: " + cn.getHour()
					
					+ " ,Minutes: " + cn.getActivityTime();
			// Writing Contacts to log
			Log.d("Name: ", log);
		}
		
	}
	
	protected void checkButtonVisible() {
		// TODO Auto-generated method stub
		if (Utilities.targetDate().compareTo(Utilities.lastDate()) == 0 || Utilities.targetDate().compareTo(Utilities.lastDate()) == 1) {
			nextEntryButton.setVisibility(View.INVISIBLE);
			prevEntryButton.setVisibility(View.VISIBLE);
		} else {
			prevEntryButton.setVisibility(View.VISIBLE);
			nextEntryButton.setVisibility(View.VISIBLE);
		}
		if (Utilities.targetDate().compareTo(Utilities.firstDate()) == 0 || Utilities.targetDate().compareTo(Utilities.firstDate()) == -1) {
			prevEntryButton.setVisibility(View.INVISIBLE);
			nextEntryButton.setVisibility(View.VISIBLE);
		} else {
			prevEntryButton.setVisibility(View.VISIBLE);
			nextEntryButton.setVisibility(View.VISIBLE);
		}
		
	}
}
