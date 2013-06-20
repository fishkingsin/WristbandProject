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
import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.LinearLayout;
import android.widget.TextView;

public class StatisticFragment extends Fragment implements
		LoaderCallbacks<Void> {
	public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
	private static final String TAG = "StatisticFragment";

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
		String message = getArguments().getString(EXTRA_MESSAGE);
		View mRootView = inflater.inflate(R.layout.activity_statistic_fragment,
				container, false);
		TextView messageTextView = (TextView) mRootView
				.findViewById(R.id.activity_indicator);
		Random random = new Random();
		int numBars = random.nextInt(50)+50;
		Log.v(TAG,"message : "+message);
		
		if(message.equals(SleepStatisticTabFragment.TAB_WEEK))
		{
			numBars = 7;
		}
		else if(message.equals(SleepStatisticTabFragment.TAB_MONTH))
		{
			numBars =31;
		}
		else if(message.equals(SleepStatisticTabFragment.TAB_YEAR))
		{
			numBars = 12;
		}
		else if(message.equals(ActivityStatisticTabFragment.TAB_DAY))
		{
			numBars = 24;
		}
		else if(message.equals(ActivityStatisticTabFragment.TAB_WEEK))
		{
			numBars = 7;
		}
		else if(message.equals(ActivityStatisticTabFragment.TAB_MONTH))
		{
			numBars =31;
		}
		else if(message.equals(ActivityStatisticTabFragment.TAB_YEAR))
		{
			numBars = 12;
		}
		
		
		messageTextView.setText(message);

		//test value
		
		GraphViewData data[] = new GraphViewData[numBars];

		for (int i = 0; i < numBars; i++) {
			data[i] = new GraphViewData(i, random.nextInt(9)+1);
		}

		GraphViewSeries exampleSeries = new GraphViewSeries(data);

		// graph with dynamically genereated horizontal and vertical labels

		GraphView mGraphView = new RoundBarGraphView(getActivity(), "");

		mGraphView.setHorizontalLabels(new String[] {
				getString(R.string.start), getString(R.string.end) });
		mGraphView.setVerticalLabels(new String[] { getString(R.string.high),
				getString(R.string.middle), getString(R.string.low) });
		mGraphView.addSeries(exampleSeries); // data

//		mGraphView.setViewPort(10, 5);
		((ViewGroup) mRootView.findViewById(R.id.graph1)).addView(mGraphView);
		//test value
		return mRootView;
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
