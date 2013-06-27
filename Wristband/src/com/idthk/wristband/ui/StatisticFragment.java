package com.idthk.wristband.ui;

//import java.util.ArrayList;
//import java.util.List;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.idthk.wristband.database.DatabaseHandler;
import com.idthk.wristband.database.Record;
import com.idthk.wristband.graphview.RoundBarGraphView;
import com.idthk.wristband.ui.R;
import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.LineGraphView;
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
					publishGraph(
							((ViewGroup) mRootView.findViewById(R.id.graph1)),
							message);
				}

			});
			prevEntryButton = (Button) mRootView
					.findViewById(R.id.btn_prev_entry);
			prevEntryButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					publishGraph(
							((ViewGroup) mRootView.findViewById(R.id.graph1)),
							message);
				}

			});
		} catch (Exception e) {
			Log.v(TAG, e.getMessage());
		}
		// TextView messageTextView = (TextView) mRootView
		// .findViewById(R.id.activity_indicator);
		publishGraph(((ViewGroup) mRootView.findViewById(R.id.graph1)), message);
		// test value
		return mRootView;
	}

	public void publishGraph(ViewGroup graph, String message) {

		graph.removeAllViews();
		String hStr[] = null;
		GraphViewSeriesStyle style = new GraphViewSeriesStyle();
		style.thickness = 5;
		style.color = 0xFF73CBfD;
//		DatabaseHandler db = null;
//		GraphViewData data[] = null;
//		GraphViewSeries series = null;

		if (message.equals(SleepStatisticTabFragment.TAB_WEEK)) {
		} else if (message.equals(SleepStatisticTabFragment.TAB_MONTH)) {
		} else if (message.equals(SleepStatisticTabFragment.TAB_YEAR)) {
			DatabaseHandler db = new DatabaseHandler(getActivity(), Main.TABLE_CONTENT_SLEEP,
					null, 1);
		} else if (message.equals(ActivityStatisticTabFragment.TAB_DAY)) {
			LineGraphView mGraphView = new LineGraphView(getActivity(), "");
			DatabaseHandler db = new DatabaseHandler(getActivity(),
					Main.TABLE_CONTENT_ACTIVITY, null, 1);
			Calendar _calendar = Calendar.getInstance();
			_calendar.set(2012, 1, 1);
			List<Record> records = db.getSumOfRecordsByDay(_calendar);
			GraphViewData [] data = new GraphViewData[records.size()];
			int j = 0;
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			
			hStr = new String[records.size()];
			for (Record cn : records) {
				//cn.getCalendar().get(Calendar.HOUR_OF_DAY)
				data[j] = new GraphViewData(j, cn.getMinutes());
				hStr[j] = sdf.format(cn.getCalendar().getTime());
				j++;
			}
			
			GraphViewSeries series = new GraphViewSeries("Day" , style , data);
			
			
			mGraphView.setManualYAxisBounds(60, 0);
			mGraphView.setHorizontalLabels(hStr);
			mGraphView.addSeries(series);

			graph.addView(mGraphView);

		} else if (message.equals(ActivityStatisticTabFragment.TAB_WEEK)) {
			RoundBarGraphView mGraphView = new RoundBarGraphView(getActivity(),
					"");
			DatabaseHandler db = new DatabaseHandler(getActivity(),
					Main.TABLE_CONTENT_ACTIVITY, null, 1);
			List<Record> records = db.getSumOfRecordsByWeek(1, 2012);

			GraphViewData [] data = new GraphViewData[records.size()];
			int j = 0;
			TextView tv = (TextView) getActivity().findViewById(
					R.id.graph_view_title_indicator);
			tv.setText(String.valueOf(records.get(0).getCalendar()
					.get(Calendar.WEEK_OF_MONTH))
					+ " "
					+ String.valueOf(records.get(0).getCalendar()
							.get(Calendar.WEEK_OF_MONTH)));
			hStr = new String[records.size()];
			for (Record cn : records) {

				data[j] = new GraphViewData(cn.getCalendar()
						.get(Calendar.MONTH), cn.getMinutes());

				SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
				Date d = cn.getCalendar().getTime();
				String dayOfTheWeek = sdf.format(d);

				hStr[j] = dayOfTheWeek;
				j++;
			}
			GraphViewSeries series = new GraphViewSeries("Week" , style , data);
			mGraphView.setManualYAxisBounds(1440, 0);
			mGraphView.setHorizontalLabels(hStr);
			mGraphView.addSeries(series);
			graph.addView(mGraphView);

		} else if (message.equals(ActivityStatisticTabFragment.TAB_MONTH)) {
			RoundBarGraphView mGraphView = new RoundBarGraphView(getActivity(),
					"");
			DatabaseHandler db = new DatabaseHandler(getActivity(),
					Main.TABLE_CONTENT_ACTIVITY, null, 1);
			List<Record> records = db.getSumOfRecordsByMonth(1, 2012);
			GraphViewData [] data = new GraphViewData[records.size()];
			int j = 0;
			// TextView tv = (TextView) getActivity().findViewById(
			// R.id.graph_view_title_indicator);
			// tv.setText(String.valueOf(records.get(0).getCalendar()
			// .get(Calendar.MONTH)));
			hStr = new String[records.size()];
			for (Record cn : records) {

				data[j] = new GraphViewData(cn.getCalendar()
						.get(Calendar.MONTH), cn.getMinutes());
				hStr[j] = String.valueOf(cn.getCalendar().get(
						Calendar.DAY_OF_MONTH));
				j++;
			}
			GraphViewSeries series = new GraphViewSeries("Month" , style ,data);

			mGraphView.setManualYAxisBounds(1440, 0);

			mGraphView.setHorizontalLabels(hStr);

			mGraphView.addSeries(series);
			graph.addView(mGraphView);

		} else if (message.equals(ActivityStatisticTabFragment.TAB_YEAR)) {
			RoundBarGraphView mGraphView = new RoundBarGraphView(getActivity(),
					"");

			DatabaseHandler db = new DatabaseHandler(getActivity(), "activity_table", null, 1);

			List<Record> records = db.getSumOfRecordsByYear(2012);
			GraphViewData []data = new GraphViewData[records.size()];
			int j = 0;
			TextView tv = (TextView) getActivity().findViewById(
					R.id.graph_view_title_indicator);
			tv.setText(String.valueOf(records.get(0).getCalendar()
					.get(Calendar.YEAR)));
			hStr = new String[records.size()];
			for (Record cn : records) {

				data[j] = new GraphViewData(cn.getCalendar()
						.get(Calendar.MONTH), cn.getMinutes());
				hStr[j] = cn.getCalendar().getDisplayName(Calendar.MONTH,
						Calendar.SHORT, Locale.US);
				j++;
			}
			GraphViewSeries series = new GraphViewSeries("Year" , style ,data);

			mGraphView.setManualYAxisBounds(44640, 0);

			mGraphView.setHorizontalLabels(hStr);

			mGraphView.addSeries(series);
			graph.addView(mGraphView);
		}

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
