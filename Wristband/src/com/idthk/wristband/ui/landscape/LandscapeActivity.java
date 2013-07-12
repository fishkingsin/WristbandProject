package com.idthk.wristband.ui.landscape;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import com.idthk.wristband.ui.Main;
import com.idthk.wristband.ui.R;
import com.idthk.wristband.ui.Utilities;


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
		
		nextEntryButton = (View) findViewById(R.id.btn_next_entry);
		prevEntryButton = (View) findViewById(R.id.btn_prev_entry);
		nextEntryButton.setOnClickListener(this);
		prevEntryButton.setOnClickListener(this);
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
		
		
		checkButtonVisible() ;

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
		
		if(arg0.getId() == R.id.btn_next_entry)
		{
			loadNextEntry();
			
		}
		else if(arg0.getId() == R.id.btn_prev_entry)
		{
			loadPrevEntry();	
		}
	}

	private void loadPrevEntry() {
		// TODO Auto-generated method stub
		int ret = Utilities.prevEntryDate(displayType);
		
		if(ret==-1)
		{
			if(prevEntryButton!=null)prevEntryButton.setVisibility(View.INVISIBLE);
		}
		else
		{
			Utilities.publishGraph(this,  findViewById(android.R.id.content) ,
					((ViewGroup) findViewById(R.id.graph1)), displayType);
			if(prevEntryButton!=null)prevEntryButton.setVisibility(View.VISIBLE);
			if(nextEntryButton!=null)nextEntryButton.setVisibility(View.VISIBLE);
		}
		
	}
	private void loadNextEntry() {
		int ret = Utilities.nextEntryDate(displayType);
			

		if(ret==1)
		{
			if(nextEntryButton!=null)nextEntryButton.setVisibility(View.INVISIBLE);
		}
		else
		{
			Utilities.publishGraph(this,  findViewById(android.R.id.content) ,
					((ViewGroup) findViewById(R.id.graph1)), displayType);
			if(nextEntryButton!=null)nextEntryButton.setVisibility(View.VISIBLE);
			if(prevEntryButton!=null)prevEntryButton.setVisibility(View.VISIBLE);
		}
	}
	
	protected void checkButtonVisible() {

		if (Utilities.targetDate().compareTo(Utilities.lastDate()) == 0 ) 
		{
			nextEntryButton.setVisibility(View.INVISIBLE);
			prevEntryButton.setVisibility(View.VISIBLE);
		}
		else
		{
			prevEntryButton.setVisibility(View.VISIBLE);
			nextEntryButton.setVisibility(View.VISIBLE);
		}
		if (Utilities.targetDate().compareTo(Utilities.firstDate()) == 0 ) {
			prevEntryButton.setVisibility(View.INVISIBLE);
			nextEntryButton.setVisibility(View.VISIBLE);
		} else {
			prevEntryButton.setVisibility(View.VISIBLE);
			nextEntryButton.setVisibility(View.VISIBLE);
		}
	}

	
		
	
}
