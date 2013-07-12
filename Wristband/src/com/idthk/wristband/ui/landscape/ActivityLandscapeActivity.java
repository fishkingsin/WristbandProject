package com.idthk.wristband.ui.landscape;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.idthk.wristband.database.DatabaseHandler;
import com.idthk.wristband.database.Record;
import com.idthk.wristband.ui.ActivityStatisticTabFragment;
import com.idthk.wristband.ui.Main;
import com.idthk.wristband.ui.R;
import com.idthk.wristband.ui.Utilities;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;


public class ActivityLandscapeActivity extends LandscapeActivity {
	static final String TAG = "ActivityLandscapeActivity";
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.landsape_main_activity);
		super.onCreate(savedInstanceState);
		

		displayType = ActivityStatisticTabFragment.TAB_DAY;
		Utilities.publishGraph((Context)this , getWindow().getDecorView().getRootView(),
				((ViewGroup) findViewById(R.id.graph1)),
				displayType);

	}


}
