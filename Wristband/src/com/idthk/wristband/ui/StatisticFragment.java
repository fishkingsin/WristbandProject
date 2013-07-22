package com.idthk.wristband.ui;

//import java.util.ArrayList;
//import java.util.List;

import java.util.Calendar;

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
		LoaderCallbacks<Void> ,
		OnClickListener{
	public static final String EXTRA_DISPLAY_TYPE = "EXTRA_DISPLAY_TYPE";
	private static final String TAG = "StatisticFragment";
	private Button nextEntryButton;
	private Button prevEntryButton;
	View mRootView = null;
	String displayType = null;

	public interface OnRefreshListener {
		public void onRefresh();
	}

	public static final StatisticFragment newInstance(String displayType) {
		StatisticFragment f = new StatisticFragment();
		Bundle bdl = new Bundle(1);
		bdl.putString(EXTRA_DISPLAY_TYPE, displayType);
		f.setArguments(bdl);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		displayType = getArguments().getString(EXTRA_DISPLAY_TYPE);
		mRootView = inflater.inflate(R.layout.statistic_fragment, container,
				false);
		try {
			nextEntryButton = (Button) mRootView
					.findViewById(R.id.btn_next_entry);
			nextEntryButton.setOnClickListener(this);

			prevEntryButton = (Button) mRootView
					.findViewById(R.id.btn_prev_entry);
			prevEntryButton.setOnClickListener(this);
			Utilities.setTargetDate( Calendar.getInstance().getTime());
			
			this.checkButtonVisible();

		} catch (Exception e) {
			Utilities.getLog(TAG, e.getMessage());
		}
		Utilities.publishGraph(getActivity(), mRootView,
				((ViewGroup) mRootView.findViewById(R.id.graph1)), displayType);
		return mRootView;
	}
	
	@Override
	public void onClick(View arg0) {
		
		Utilities.getLog(TAG,arg0.toString());
		
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
		
		int ret = Utilities.prevEntryDate(displayType);
		
		if(ret==-1 )
		{
			if(prevEntryButton!=null)prevEntryButton.setVisibility(View.INVISIBLE);
			if(nextEntryButton!=null)nextEntryButton.setVisibility(View.VISIBLE);
		}
		else
		{
			Utilities.publishGraph(getActivity(), mRootView,
					((ViewGroup) mRootView
							.findViewById(R.id.graph1)), displayType);
			if(nextEntryButton!=null)nextEntryButton.setVisibility(View.VISIBLE);
			if(ret==0)
			{
				if(prevEntryButton!=null)prevEntryButton.setVisibility(View.INVISIBLE);
			}
			else
			{
				if(prevEntryButton!=null)prevEntryButton.setVisibility(View.VISIBLE);
			}
		}
		
		
	}
	private void loadNextEntry() {
		int ret = Utilities.nextEntryDate(displayType);
			

		if(ret==1)
		{
			if(nextEntryButton!=null)nextEntryButton.setVisibility(View.INVISIBLE);
			if(prevEntryButton!=null)prevEntryButton.setVisibility(View.VISIBLE);
		}
		else
		{
			Utilities.publishGraph(getActivity(), mRootView,
					((ViewGroup) mRootView
							.findViewById(R.id.graph1)), displayType);
			if(prevEntryButton!=null)prevEntryButton.setVisibility(View.VISIBLE);
			if(ret==0)
			{
				if(nextEntryButton!=null)nextEntryButton.setVisibility(View.INVISIBLE);
			}
			else
			{
				if(nextEntryButton!=null)nextEntryButton.setVisibility(View.VISIBLE);
			}
		}
	}
	
	protected void checkButtonVisible() {

		if (Utilities.targetDate().compareTo(Utilities.lastDate()) == 0 ) 
		{
			nextEntryButton.setVisibility(View.INVISIBLE);
			//prevEntryButton.setVisibility(View.VISIBLE);
		}
		if (Utilities.targetDate().compareTo(Utilities.firstDate()) == 0 ) {
			prevEntryButton.setVisibility(View.INVISIBLE);
			//nextEntryButton.setVisibility(View.VISIBLE);
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
		Utilities.getLog(TAG, "on publish graph");
		super.onResume();
		Utilities.publishGraph(getActivity(), mRootView,
				((ViewGroup) mRootView.findViewById(R.id.graph1)), displayType);
	}

	@Override
	public Loader<Void> onCreateLoader(int arg0, Bundle arg1) {
		
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Void> arg0, Void arg1) {
		

	}

	@Override
	public void onLoaderReset(Loader<Void> arg0) {
		

	}

	public void onRefresh() {
		Utilities.publishGraph(getActivity(), mRootView,
				((ViewGroup) mRootView.findViewById(R.id.graph1)), displayType);
	}
}
