//package com.idthk.wristband.ui;
//
//import android.support.v4.app.Fragment;
//
//public class StatisticFragmentPager extends Fragment {
//
//}
//

package com.idthk.wristband.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

public class StatisticFragmentPager extends Fragment {
	private static final int NUMBER_OF_PAGES = 2;
	public static final int ACTIVITY_LEVEL = 3;
	public static final int SLEEP_LEVEL = 4;
	protected static final String TAG = "StatisticFragmentPager";
	private int mCurrentPage = 0;
	private ViewPager mViewPager;
	private MyStatisticFragmentPagerAdapter mMyStatisticFragmentPagerAdapter;

	StatisticPagerChangedCallback mCallback;
	PageIndicator mIndicator;

	// Container Activity must implement this interface
	public interface StatisticPagerChangedCallback {
		public void onStatisticPagerChangedCallback(int page);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (StatisticPagerChangedCallback) activity;

			// mCallback.onStatisticPagerChangedCallback(mCurrentPage);

		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnHeadlineSelectedListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View mRootView = inflater.inflate(R.layout.statistic_fragmentpager,
				container, false);
		mViewPager = (ViewPager) mRootView.findViewById(R.id.pager2);
		mMyStatisticFragmentPagerAdapter = new MyStatisticFragmentPagerAdapter(
				getFragmentManager());
		mViewPager.setAdapter(mMyStatisticFragmentPagerAdapter);

		Log.v(TAG, "onCreateView ");


		mIndicator = (CirclePageIndicator) mRootView
				.findViewById(R.id.indicator);
		mIndicator.setViewPager(mViewPager);
		mIndicator.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				Log.v(TAG, "onPageSelected " + position);
				mCurrentPage = position;
				mCallback.onStatisticPagerChangedCallback(position);

				// When changing pages, reset the action bar actions
				// since they are dependent
				// on which page is currently active. An alternative
				// approach is to have each
				// fragment expose actions itself (rather than the
				// activity exposing actions),
				// but for simplicity, the activity provides the actions
				// in this sample.

			}
		});
		return mRootView;
	}

	@Override
	public void onResume() {
		// if(mCallback!=null)
		// {
		// mCallback.onStatisticPagerChangedCallback(mCurrentPage);
		// }
		super.onResume();

	}

	private static class MyStatisticFragmentPagerAdapter extends
			FragmentPagerAdapter {

		public MyStatisticFragmentPagerAdapter(FragmentManager fm) {

			super(fm);

		}

		@Override
		public Fragment getItem(int index) {
			if (index == 0) {
				return new TabFragmentActivityStatistic();
			} else {
				return new TabFragmentSleepStatistic();
			}

		}

		@Override
		public int getCount() {

			return NUMBER_OF_PAGES;
		}
	}

	public int getCurrentPage() {
		// TODO Auto-generated method stub
		return mCurrentPage;
	}
}
