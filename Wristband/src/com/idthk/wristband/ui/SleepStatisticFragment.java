package com.idthk.wristband.ui;

//import java.util.ArrayList;
//import java.util.List;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.idthk.wristband.graphview.RoundBarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;

public class SleepStatisticFragment extends Fragment implements
		LoaderCallbacks<Void> {
	public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

	public static final SleepStatisticFragment newInstance(String message) {
		SleepStatisticFragment f = new SleepStatisticFragment();
		Bundle bdl = new Bundle(1);
		bdl.putString(EXTRA_MESSAGE, message);
		f.setArguments(bdl);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		String message = getArguments().getString(EXTRA_MESSAGE);
		View mRootView = inflater.inflate(R.layout.sleep_statistic_fragment,
				container, false);
		TextView messageTextView = (TextView) mRootView
				.findViewById(R.id.sleep_indicator);
		messageTextView.setText(message);

		//test value
		Random random = new Random();
		int numBars = random.nextInt(50);
		GraphViewData data[] = new GraphViewData[numBars];

		for (int i = 0; i < numBars; i++) {
			data[i] = new GraphViewData(i, random.nextInt(10));
		}

		GraphViewSeries exampleSeries = new GraphViewSeries(data);

		// graph with dynamically genereated horizontal and vertical labels

		GraphView mGraphView = new RoundBarGraphView(getActivity(), "");

		mGraphView.setHorizontalLabels(new String[] {
				getString(R.string.start), getString(R.string.end) });
		mGraphView.setVerticalLabels(new String[] { getString(R.string.high),
				getString(R.string.middle), getString(R.string.low) });
		mGraphView.addSeries(exampleSeries); // data

//		mGraphView.setViewPort(5, 5);
//		mGraphView.setScalable(true);
//		mGraphView.setScrollable(true);
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
