package com.idthk.wristband.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

public class MainFragmentPager extends Fragment implements
		SharedPreferences.OnSharedPreferenceChangeListener {
	private static final int NUMBER_OF_PAGES = 2;
	public static final int ACTIVITY = 0;
	public static final int SLEEP = 1;
	static final String TAG = "MainFragmentPager";
	private int mCurrentPage = 0;
	private ViewPager mViewPager;
	private MyFragmentPagerAdapter mMyFragmentPagerAdapter;

	PagerChangedCallback mCallback;
	private ImageView battery_indicated_imageview;
	private Integer battery_images[] = { R.drawable.battery_0,

	R.drawable.battery_1, R.drawable.battery_2 };
	private TextView userNameTv;
	private TextView lastSyncTimeTv;
	PageIndicator mIndicator;
	private ImageView profilePic;

	// Container Activity must implement this interface
	public interface PagerChangedCallback {
		public void onPagerChangedCallback(int page);

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (PagerChangedCallback) activity;
			// mCallback.onPagerChangedCallback(mCurrentPage);
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnHeadlineSelectedListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater
				.inflate(R.layout.main_fragmentpager, container, false);

		// load user profile

		publishUserProfile(v);

		mViewPager = (ViewPager) v.findViewById(R.id.pager);

		mMyFragmentPagerAdapter = new MyFragmentPagerAdapter(
				getFragmentManager());
		mViewPager.setAdapter(mMyFragmentPagerAdapter);
		mCallback.onPagerChangedCallback(ACTIVITY);

		mIndicator = (CirclePageIndicator) v.findViewById(R.id.indicator);
		mIndicator.setViewPager(mViewPager);
		mIndicator
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						mCurrentPage = position;

						mCallback.onPagerChangedCallback(mCurrentPage);

					}
				});
		return v;
	}

	private void publishUserProfile(View v) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this.getActivity());
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
		// TODO Auto-generated method stub
		
		// Log.v(TAG, "profile path : " + path);

		profilePic = ((ImageView) v.findViewById(R.id.profile_pic));

		profilePic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						UserPreferencesActivity.class);
				startActivityForResult(intent, Main.TO_USER_PROFILE_REQUEST);
			}

		});

		String path = sharedPreferences.getString(
				getString(R.string.pref_profile_pic), "");
		if (path != "") {
			Bitmap myBitmap = Utilities.decodeFile(new File(path),
					this.getActivity());
			profilePic.setImageBitmap(myBitmap);
		}
		if (v.findViewById(R.id.battery_image) != null) {
			battery_indicated_imageview = ((ImageView) v
					.findViewById(R.id.battery_image));
		}

		userNameTv = ((TextView) v.findViewById(R.id.userNameTv));
		lastSyncTimeTv = ((TextView) v
				.findViewById(R.id.last_sync_time_textview));
		lastSyncTimeTv.setText(sharedPreferences.getString(
				getString(R.string.pref_last_sync_time),
				getString(R.string.default_last_sync_time)));

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		((TextView) v.findViewById(R.id.today_text_view)).setText(sdf
				.format(cal.getTime()));
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(getString(R.string.pref_profile_pic))) {
			
			String path = sharedPreferences.getString(
					getString(R.string.pref_profile_pic), "");
			if (path != "") {
				Bitmap myBitmap = Utilities.decodeFile(new File(path),
						this.getActivity());
				profilePic.setImageBitmap(myBitmap);
			}
		}
		if (key.equals(getString(R.string.pref_last_sync_time))) {
			lastSyncTimeTv.setText(sharedPreferences.getString(
					getString(R.string.pref_last_sync_time),
					getString(R.string.default_last_sync_time)));
		}
	}

	@Override
	public void onResume() {
		// mCallback.onPagerChangedCallback(mCurrentPage);
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this.getActivity());
		String path = sharedPreferences.getString(
				getString(R.string.pref_profile_pic), "");
		if (path != "") {
			Bitmap myBitmap = Utilities.decodeFile(new File(path),
					this.getActivity());
			profilePic.setImageBitmap(myBitmap);
		}
		super.onResume();

	}

	@Override
	public void onDestroyView() {

		PreferenceManager.getDefaultSharedPreferences(this.getActivity())
				.unregisterOnSharedPreferenceChangeListener(this);

		super.onDestroyView();
	}

	private static class MyFragmentPagerAdapter extends FragmentPagerAdapter {
		// SparseArray<Fragment> registeredFragments = new
		// SparseArray<Fragment>();

		public MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int index) {

			return MainFragment.create(index);
		}

		@Override
		public int getCount() {

			return NUMBER_OF_PAGES;
		}

		// @Override
		// public Object instantiateItem(ViewGroup container, int position) {
		// Fragment fragment = (Fragment) super.instantiateItem(container,
		// position);
		// registeredFragments.put(position, fragment);
		// return fragment;
		// }
		//
		// @Override
		// public void destroyItem(ViewGroup container, int position, Object
		// object) {
		// registeredFragments.remove(position);
		// super.destroyItem(container, position, object);
		// }
		//
		// public Fragment getRegisteredFragment(int position) {
		// return registeredFragments.get(position);
		// }
	}

	public int getCurrentPage() {
		// TODO Auto-generated method stub
		return mCurrentPage;
	}

	public void updateBatteryLevet(int batteryLevel) {
		// TODO Auto-generated method stub
		if (battery_indicated_imageview != null) {
			if (batteryLevel <= 33) {
				battery_indicated_imageview.setImageResource(battery_images[0]);
			}
			else if (batteryLevel > 33 && batteryLevel<=66) {
				battery_indicated_imageview.setImageResource(battery_images[1]);
			}
			else if (batteryLevel >66 ) {
				battery_indicated_imageview.setImageResource(battery_images[2]);
			}
		}
	}

}
