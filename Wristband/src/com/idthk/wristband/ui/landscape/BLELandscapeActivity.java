package com.idthk.wristband.ui.landscape;

import java.util.List;

import org.bostonandroid.datepreference.DatePreference;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnClickListener;

import com.idthk.wristband.database.DatabaseHandler;
import com.idthk.wristband.database.Record;
import com.idthk.wristband.ui.Main;
import com.idthk.wristband.ui.R;
//import android.content.Intent;
//import android.widget.Toast;

public class BLELandscapeActivity extends LandscapeActivity implements OnClickListener {
	static final String TAG = "LandscapeActivity";
	public static final String LANDSCAPE_BUNDLE = "landscape_bundle";
	OrientationEventListener orientationListener;
	static final int THRESHOLD = 5;
	protected View nextEntryButton = null;
	protected View prevEntryButton = null;
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
		
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Log.v(TAG,arg0.toString());
		
		if(arg0.getId() == R.id.btn_next_entry)
		{
			loadNextEntry();
			
		}
		else if(arg0.getId() == R.id.btn_prev_entry)
		{
			loadPrevEntry();	
		}
	}
	public void loadPrevEntry() {
		// TODO Auto-generated method stub
		
	}
	public void loadNextEntry() {
		// TODO Auto-generated method stub
		
	}
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
	
	public void onStreamMessage(int steps, int calories, float distance,
			int activityTime, int batteryLevel) {
		// TODO Auto-generated method stub

	}
	
}
