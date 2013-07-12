package com.idthk.wristband.ui.landscape;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.idthk.wristband.ui.R;
import com.idthk.wristband.ui.Utilities;


public class StatisticLandscapeActivity extends LandscapeActivity {
	static final String TAG = "SleepLandscapeActivity";
	public static final String TYPE = "type";
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		setContentView(R.layout.landsape_main);
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getIntent().getBundleExtra(
				LandscapeActivity.LANDSCAPE_BUNDLE);
		displayType = bundle.getString(StatisticLandscapeActivity.TYPE);
		Log.v(TAG, displayType);

		Utilities.publishGraph((Context)this , getWindow().getDecorView().getRootView(),
				((ViewGroup) findViewById(R.id.graph1)),
				displayType);
		
	}


	

}
