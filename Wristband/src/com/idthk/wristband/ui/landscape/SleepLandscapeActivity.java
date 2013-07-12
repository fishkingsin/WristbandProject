package com.idthk.wristband.ui.landscape;

import java.util.Random;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.idthk.wristband.graphview.RoundBarGraphView;
import com.idthk.wristband.ui.R;
import com.idthk.wristband.ui.SleepStatisticTabFragment;
import com.idthk.wristband.ui.Utilities;

public class SleepLandscapeActivity extends LandscapeActivity {
	static final String TAG = "SleepLandscapeActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.landsape_main_sleep);
		super.onCreate(savedInstanceState);

		
		Random random = new Random();
		int numBars = 100;
		RoundBarGraphView mGraphView = new RoundBarGraphView(this, "");
		mGraphView.setHorizontalLabels(new String[] {
				getString(R.string.start), getString(R.string.end) });
		mGraphView.setVerticalLabels(new String[] { getString(R.string.high),
				getString(R.string.middle), getString(R.string.low) });

		
		displayType = SleepStatisticTabFragment.TAB_DAY;

		Utilities.populateSleepPatternGraph((Context)this , getWindow().getDecorView().getRootView(),
				((ViewGroup) findViewById(R.id.graph1)));

	}
	
}
