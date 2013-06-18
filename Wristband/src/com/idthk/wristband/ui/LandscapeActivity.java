package com.idthk.wristband.ui;

import java.util.Random;

import com.idthk.wristband.graphview.RoundBarGraphView;
import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphView.GraphViewData;

import android.app.Activity;
import android.content.Intent;
//import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.OrientationEventListener;
//import android.widget.Toast;
import android.view.ViewGroup;

public class LandscapeActivity extends Activity {
	static final String TAG = "LandscapeActivity";
	OrientationEventListener orientationListener;
	static final int THRESHOLD = 5;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.landsape_main);
		Intent intent = getIntent();
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
		
		
		Random random = new Random();
		int numBars = 100;
		GraphViewData data[] = new GraphViewData[numBars];

		for (int i = 0; i < numBars; i++) {
			data[i] = new GraphViewData(i, random.nextInt(10));
		}

		GraphViewSeries exampleSeries = new GraphViewSeries(data);

		// graph with dynamically genereated horizontal and vertical labels

		GraphView mGraphView = new RoundBarGraphView(this, "");

		mGraphView.setHorizontalLabels(new String[] {
				getString(R.string.start), getString(R.string.end) });
		mGraphView.setVerticalLabels(new String[] { getString(R.string.high),
				getString(R.string.middle), getString(R.string.low) });
		mGraphView.addSeries(exampleSeries); // data

		mGraphView.setViewPort(10, 10);
		mGraphView.setScalable(true);
		mGraphView.setScrollable(true);
		((ViewGroup) findViewById(R.id.graph1)).addView(mGraphView);

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
}
