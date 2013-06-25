package com.idthk.wristband.ui;

//import java.util.ArrayList;
//import java.util.List;

import java.util.Random;

import com.idthk.wristband.graphview.RoundBarGraphView;
import com.idthk.wristband.ui.R;
import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphView.GraphViewData;
//import com.idthk.wristband.ui.ScrollPagerMain.ScrollPagerMainCallback;

//import android.annotation.SuppressLint;
import android.app.Activity;
//import android.content.Context;
import android.os.Bundle;
//import android.support.v4.app.ListFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
//import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
//import android.widget.ArrayAdapter;
//import android.widget.LinearLayout;
import android.widget.TextView;

public class StatisticFragment extends Fragment implements
		LoaderCallbacks<Void> {
	public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
	private static final String TAG = "StatisticFragment";
	private Button nextEntryButton;
	private Button prevEntryButton;
	View mRootView = null;
	String message = null;
//	GraphView mGraphView = null;
//	GraphViewSeries series = null;

	public static final StatisticFragment newInstance(String message) {
		StatisticFragment f = new StatisticFragment();
		Bundle bdl = new Bundle(1);
		bdl.putString(EXTRA_MESSAGE, message);
		f.setArguments(bdl);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		message = getArguments().getString(EXTRA_MESSAGE);
		mRootView = inflater.inflate(R.layout.statistic_fragment, container,
				false);
		try {
			nextEntryButton = (Button) mRootView
					.findViewById(R.id.btn_next_entry);
			nextEntryButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					publishGraph(((ViewGroup) mRootView.findViewById(R.id.graph1)),message);
				}

			});
			prevEntryButton = (Button) mRootView
					.findViewById(R.id.btn_prev_entry);
			prevEntryButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					publishGraph(((ViewGroup) mRootView.findViewById(R.id.graph1)),message);
				}

			});
		} catch (Exception e) {
			Log.v(TAG, e.getMessage());
		}
		// TextView messageTextView = (TextView) mRootView
		// .findViewById(R.id.activity_indicator);
		publishGraph(((ViewGroup) mRootView.findViewById(R.id.graph1)),message);
		// test value
		return mRootView;
	}

	public void publishGraph(ViewGroup graph, String message) {
		//TO-DO
		//retrive db data 
		//port to graph
		
		graph.removeAllViews();
		// TODO Auto-generated method stub
		Random random = new Random();
		int numBars = random.nextInt(50) + 50;
		Log.v(TAG, "message : " + message);
		String hStr[] = null; 
		if (message.equals(SleepStatisticTabFragment.TAB_WEEK)) {
			numBars = 7;
			
		} else if (message.equals(SleepStatisticTabFragment.TAB_MONTH)) {
			numBars = 31;
		} else if (message.equals(SleepStatisticTabFragment.TAB_YEAR)) {
			numBars = 12;
		} else if (message.equals(ActivityStatisticTabFragment.TAB_DAY)) {
			numBars = 24;
		} else if (message.equals(ActivityStatisticTabFragment.TAB_WEEK)) {
			numBars = 7;
		} else if (message.equals(ActivityStatisticTabFragment.TAB_MONTH)) {
			numBars = 31;
		} else if (message.equals(ActivityStatisticTabFragment.TAB_YEAR)) {
			numBars = 12;
		}

		// messageTextView.setText(message);

		// test value

		// graph with dynamically genereated horizontal and vertical labels

		RoundBarGraphView mGraphView = new RoundBarGraphView(getActivity(), "");
		hStr = new String[numBars];
		
		for(int i = 0; i < numBars ; i++)
		{
			hStr[i] = String.valueOf(i+1);
		}
		
		mGraphView.setHorizontalLabels(hStr);
		mGraphView.setVerticalLabels(new String[] { "120" , "90" , "60", "30" , "0"});

		GraphViewData data[] = new GraphViewData[numBars];
		for (int i = 0; i < numBars; i++) {
			data[i] = new GraphViewData(i, random.nextInt(9) + 1);
		}
		GraphViewSeries series = new GraphViewSeries(data);

		mGraphView.addSeries(series); // data

		graph.addView(mGraphView);
		

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
	}

	@Override
	public Loader<Void> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Void> arg0, Void arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoaderReset(Loader<Void> arg0) {
		// TODO Auto-generated method stub

	}
}
