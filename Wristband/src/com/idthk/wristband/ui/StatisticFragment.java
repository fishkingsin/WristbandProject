package com.idthk.wristband.ui;

//import java.util.ArrayList;
//import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class StatisticFragment extends Fragment implements
		LoaderCallbacks<Void> {
	public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
	private static final String TAG = "StatisticFragment";
	private Button nextEntryButton;
	private Button prevEntryButton;
	View mRootView = null;
	String message = null;

	public interface OnRefreshListener {
		public void onRefresh();
	}

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
					int ret = Utilities.nextEntryDate(message);
						
						if(ret==1)
						{
							if(nextEntryButton!=null)nextEntryButton.setVisibility(View.INVISIBLE);
							if(prevEntryButton!=null)prevEntryButton.setVisibility(View.VISIBLE);
						}
						else
							
						{
							Utilities.publishGraph(getActivity(), mRootView,
									((ViewGroup) mRootView
											.findViewById(R.id.graph1)), message);
							if(nextEntryButton!=null)nextEntryButton.setVisibility(View.VISIBLE);
							if(prevEntryButton!=null)prevEntryButton.setVisibility(View.VISIBLE);
						}
				}
			});

			prevEntryButton = (Button) mRootView
					.findViewById(R.id.btn_prev_entry);
			prevEntryButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					int ret = Utilities.prevEntryDate(message);

						
						if(ret==-1)
						{
							if(prevEntryButton!=null)prevEntryButton.setVisibility(View.INVISIBLE);
							if(nextEntryButton!=null)nextEntryButton.setVisibility(View.VISIBLE);
						}
						else
						{
							Utilities.publishGraph(getActivity(), mRootView,
									((ViewGroup) mRootView
											.findViewById(R.id.graph1)), message);
							if(prevEntryButton!=null)prevEntryButton.setVisibility(View.VISIBLE);
							if(nextEntryButton!=null)nextEntryButton.setVisibility(View.VISIBLE);
							
						}
				}

			});
			checkButtonVisible();

		} catch (Exception e) {
			Log.v(TAG, e.getMessage());
		}
		Utilities.publishGraph(getActivity(), mRootView,
				((ViewGroup) mRootView.findViewById(R.id.graph1)), message);
		return mRootView;
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
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
	}

	@Override
	public void onResume() {
		Log.v(TAG, "on publish graph");
		super.onResume();
		Utilities.publishGraph(getActivity(), mRootView,
				((ViewGroup) mRootView.findViewById(R.id.graph1)), message);
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

	public void onRefresh() {
		Utilities.publishGraph(getActivity(), mRootView,
				((ViewGroup) mRootView.findViewById(R.id.graph1)), message);
	}
}
