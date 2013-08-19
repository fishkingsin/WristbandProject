/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.idthk.wristband.ui;

import java.util.Calendar;

import com.idthk.wristband.database.SleepRecord;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * A fragment representing a single step in a wizard. The fragment shows a dummy
 * title indicating the page number, along with some dummy text.
 * 
 * <p>
 * This class is used by the {@link CardFlipActivity} and
 * {@link ScreenSlideActivity} samples.
 * </p>
 */
public class MainFragment extends Fragment implements
		SharedPreferences.OnSharedPreferenceChangeListener
// , OnGestureListener
{
	public static final String FACEBOOK = "Facebook";
	public static final String TWITTER = "Twitter";
	/**
	 * The argument key for the page number this fragment represents.
	 */
	public static final String ARG_PAGE = "page";
	public static final String TAG = "MainFragment";
	private ViewGroup mRootView;
	private OnShareButtonClickedListener mCallback;
	private CustomProgressBar m_activityTimeProgressBar;

	private ProgressBar mStepsProgressBar = null;
	private ProgressBar mCaloriesProgressBar = null;
	private ProgressBar mDistancesProgressBar = null;

	private View mTargetView = null;
	private View mNonTargetView = null;
	private TextView goalStepsTv = null;
	private TextView goalCaloriesTv = null;
	private TextView goalDistancesTv = null;

	private TextView mTargetStepsIndicatedTV = null;
	private TextView mTargetCaloriesIndicatedTV = null;
	private TextView mTargetDistancesIndicatedTV = null;

	private TextView mStepIndicatedTV = null;
	private TextView mCaloriesIndicatedTV = null;
	private TextView mDistancesIndicatedTV = null;
	private ScrollView scrollView = null;

	public interface OnShareButtonClickedListener {
		public void onShareButtonClicked(String s);

		public void dispatchSelf(MainFragment mainSlideFragment);
	}

	/**
	 * The fragment's page number, which is set to the argument value for
	 * {@link #ARG_PAGE}.
	 */
	private int mPageNumber;
	// private TextView lastSyncTimeTv;
	static private int targetSteps = 1;
	static private int targetCalories = 1;
	static private float targetDistances = 1.0f;

	private Integer currentActivityTime = 0;
	private float currentDistanceProgress = 0;
	private Integer currentCalories = 0;
	private Integer currentSteps = 0;
	private boolean isMetric = true;

	// GestureDetector gestureDetector;

	/**
	 * Factory method for this fragment class. Constructs a new fragment for the
	 * given page number.
	 */

	public static MainFragment create(int pageNumber) {
		MainFragment fragment = new MainFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PAGE, pageNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public MainFragment() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (OnShareButtonClickedListener) activity;

		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnHeadlineSelectedListener");
		}

	}

	@Override
	public void onResume() {
		Utilities.getLog(TAG, "onResume");
		super.onResume();
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this.getActivity());
		publishSettings(sharedPreferences);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPageNumber = getArguments().getInt(ARG_PAGE);

	}

	@Override
	public void onDestroyView() {

		PreferenceManager.getDefaultSharedPreferences(this.getActivity())
				.unregisterOnSharedPreferenceChangeListener(this);

		super.onDestroyView();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// gestureDetector = new GestureDetector(getActivity(), this);

		// Utilities.getLog(TAG,"Tag : "+getTag());
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this.getActivity());
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
		if (mPageNumber == 0) {
			mRootView = (ViewGroup) inflater.inflate(
					R.layout.main_fragment_activity, container, false);

			m_activityTimeProgressBar = (CustomProgressBar) mRootView
					.findViewById(R.id.target_progress_bar_large);

			m_activityTimeProgressBar.setTarget(0);
			m_activityTimeProgressBar.setProgressInMins(0);
			if (mRootView.findViewById(R.id.steps_progressbar) != null) {
				mStepsProgressBar = (ProgressBar) mRootView
						.findViewById(R.id.steps_progressbar);
				mStepsProgressBar.setProgress(0);
			}
			if (mRootView.findViewById(R.id.calories_progressbar) != null) {
				mCaloriesProgressBar = (ProgressBar) mRootView
						.findViewById(R.id.calories_progressbar);
				mCaloriesProgressBar.setProgress(0);
			}
			if (mRootView.findViewById(R.id.distances_progressbar) != null) {
				mDistancesProgressBar = (ProgressBar) mRootView
						.findViewById(R.id.distances_progressbar);
				mDistancesProgressBar.setProgress(0);
			}

			mTargetStepsIndicatedTV = ((TextView) mRootView
					.findViewById(R.id.target_steps_indicated_textview));
			mTargetCaloriesIndicatedTV = ((TextView) mRootView
					.findViewById(R.id.target_calories_indicated_textview));
			mTargetDistancesIndicatedTV = ((TextView) mRootView
					.findViewById(R.id.target_distances_indicated_textview));

			mStepIndicatedTV = ((TextView) mRootView
					.findViewById(R.id.steps_indicated_textview));
			mCaloriesIndicatedTV = ((TextView) mRootView
					.findViewById(R.id.calories_indicated_textview));
			mDistancesIndicatedTV = ((TextView) mRootView
					.findViewById(R.id.distances_indicated_textview));

			// publishSettings(sharedPreferences);

			if (mRootView.findViewById(R.id.button_facebook_share) != null) {
				((Button) mRootView.findViewById(R.id.button_facebook_share))
						.setOnClickListener(new OnClickListener() {
							public void onClick(View m) {

								mCallback.onShareButtonClicked(FACEBOOK);
							}
						});
			}
			if (mRootView.findViewById(R.id.button_twitter_share) != null) {
				((Button) mRootView.findViewById(R.id.button_twitter_share))
						.setOnClickListener(new OnClickListener() {
							public void onClick(View m) {

								mCallback.onShareButtonClicked(TWITTER);
							}
						});
			}

			scrollView = (ScrollView) mRootView
					.findViewById(R.id.main_activity_scroll_view);

			// scrollView.setOnTouchListener(new View.OnTouchListener() {
			//
			// public boolean onTouch(View v, MotionEvent event) {
			// // TODO Auto-generated method stub
			// Log.v(TAG,"PARENT TOUCH");
			// scrollView.fullScroll(View.FOCUS_DOWN);
			//
			// return false;
			// }
			// });

		} else {
			mRootView = (ViewGroup) inflater.inflate(
					R.layout.main_fragment_sleep, container, false);

			scrollView = (ScrollView) mRootView
					.findViewById(R.id.main_sleep_scroll_view);
			// scrollView.setOnTouchListener(new View.OnTouchListener() {
			//
			// public boolean onTouch(View v, MotionEvent event) {
			// // TODO Auto-generated method stub
			// Log.v(TAG,"PARENT TOUCH");
			// scrollView.fullScroll(View.FOCUS_DOWN);
			// return false;
			// }
			// });

			((Button) mRootView.findViewById(R.id.button_facebook_share))
					.setOnClickListener(new OnClickListener() {
						public void onClick(View m) {

							mCallback.onShareButtonClicked(FACEBOOK);
						}
					});
			((Button) mRootView.findViewById(R.id.button_twitter_share))
					.setOnClickListener(new OnClickListener() {
						public void onClick(View m) {

							mCallback.onShareButtonClicked(TWITTER);
						}
					});

			// publishSettings(sharedPreferences);
			populateGraph(mRootView);
		}

		return mRootView;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);

		if (isVisibleToUser) {
			mCallback.dispatchSelf(this);
		}
	}

	private void publishSettings(SharedPreferences prefs) {
		// TODO Auto-generated method stub
		if (mPageNumber == 0) {
			boolean target = prefs.getBoolean(
					getString(R.string.pref_toggle_target), true);
			mTargetView = ((View) mRootView.findViewById(R.id.target_layout_on));
			mNonTargetView = ((View) mRootView
					.findViewById(R.id.target_layout_off));

			mTargetView.setVisibility((target) ? View.VISIBLE : View.GONE);

			mNonTargetView.setVisibility((target) ? View.GONE : View.VISIBLE);

			int targetActivity = Integer.valueOf(prefs.getString(
					getString(R.string.pref_targetActivity), "30"));

			targetSteps = Integer.valueOf(prefs.getString(
					getString(R.string.pref_targetSteps),
					getString(R.string.defalut_target_steps)));
			targetCalories = Integer.valueOf(prefs.getString(
					getString(R.string.pref_targetCalories),
					getString(R.string.defalut_target_calories)));

			String value = prefs.getString(
					getString(R.string.pref_targetDistances_display),
					getString(R.string.defalut_target_distances));

			targetDistances = (!value.equals("")) ? Float.valueOf(value) : 0;

			if (m_activityTimeProgressBar != null)
				m_activityTimeProgressBar.setTarget(targetActivity);
			if (mStepsProgressBar != null)
				mStepsProgressBar.setMax(targetSteps);
			if (mCaloriesProgressBar != null)
				mCaloriesProgressBar.setMax(targetCalories);
			if (mDistancesProgressBar != null)
				mDistancesProgressBar.setMax(100);

			goalStepsTv = ((TextView) mRootView
					.findViewById(R.id.goal_steps_indicat_textview));
			goalStepsTv.setText(String.valueOf(targetSteps));
			goalCaloriesTv = ((TextView) mRootView
					.findViewById(R.id.goal_calories_indicate_textview));
			goalCaloriesTv.setText(String.valueOf(targetCalories));
			goalDistancesTv = ((TextView) mRootView
					.findViewById(R.id.goal_distances_indicat_textview));
			String format = "%.1f";
			goalDistancesTv.setText(String.format(format, targetDistances));

			// new
			changeDistanceText(prefs);

		} else {

			int actualSleepTime = prefs.getInt(
					getString(R.string.keyActualSleepTime), 0);
			String inBedTime = prefs.getString(
					getString(R.string.pref_in_bed_time), "0");

			String sleepEnd = prefs.getString(
					getString(R.string.pref_sleep_end), "00:00");

			String sleepStart = prefs.getString(
					getString(R.string.pref_sleep_start), "00:00");

			int timeFallAsSleep = prefs.getInt(
					getString(R.string.keyTimeFallAsSleep), 0);

			((TextView) mRootView
					.findViewById(R.id.sleep_duration_textfield))
					.setText(inBedTime);
			
//			((TextView) mRootView.findViewById(R.id.sleep_start_textfield))
//					.setText(sleepStart);
//
//			((TextView) mRootView.findViewById(R.id.sleep_end_textfield))
//					.setText(sleepEnd);

			int value = actualSleepTime;
			((TextView) mRootView.findViewById(R.id.sleep_time_hour_textview))
					.setText(String.valueOf(value / 60));

			((TextView) mRootView.findViewById(R.id.sleep_time_mins_textview))
					.setText(String.valueOf(value % 60));
			((TextView) mRootView
					.findViewById(R.id.fall_asleep_time_mins_textview))
					.setText(String.valueOf(timeFallAsSleep));

		}

	}

	private void changeDistanceText(SharedPreferences prefs) {
		// TODO Auto-generated method stub
		String unitString = prefs.getString(getString(R.string.prefUnit),
				"Metric");
		isMetric = (unitString.equals("Metric")) ? true : false;

		Resources res = getResources();
		String distance_unit[] = res.getStringArray(R.array.distance_unit);

		TextView tv1 = ((TextView) mRootView
				.findViewById(R.id.distanceUnitTextView1));
		TextView tv2 = ((TextView) mRootView
				.findViewById(R.id.distanceUnitTextView2));
		if (isMetric) {
			tv2.setText(distance_unit[0]);
			tv1.setText(distance_unit[0]);
		} else {

			tv1.setText(distance_unit[1]);
			tv2.setText(distance_unit[1]);
		}
	}

	private void populateGraph(View mRootView) {
		if (mPageNumber == 0) {
			Calendar now = Calendar.getInstance();
			Utilities.targetDate().setTime(now.getTime());
			Utilities.publishGraph(getActivity(), mRootView,
					((ViewGroup) mRootView.findViewById(R.id.graph1)),
					TabFragmentSleepStatistic.TAB_DAY);
		} else {
			// TODO implement sleep patter graph view
			Utilities.populateSleepPatternGraph(getActivity(), mRootView,
					((ViewGroup) mRootView.findViewById(R.id.graph1)));
		}

	}

	/**
	 * Returns the page number represented by this fragment object.
	 */
	public int getPageNumber() {
		return mPageNumber;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {

		if (mPageNumber == 0) {
			if (key.equals(getString(R.string.pref_toggle_target))) {
				boolean target = sharedPreferences.getBoolean(key, false);
				mTargetView.setVisibility((target) ? View.VISIBLE : View.GONE);

				mNonTargetView.setVisibility((target) ? View.GONE
						: View.VISIBLE);
			} else if (key.equals(getString(R.string.pref_targetSteps))) {
				targetSteps = Integer.valueOf(sharedPreferences.getString(
						getString(R.string.pref_targetSteps), "0"));
				goalStepsTv.setText(Integer.toString(targetSteps));
				mStepsProgressBar.setMax(targetSteps);
			} else if (key.equals(getString(R.string.pref_targetCalories))) {
				targetCalories = Integer.valueOf(sharedPreferences.getString(
						getString(R.string.pref_targetCalories), "0"));
				goalCaloriesTv.setText(Integer.toString(targetCalories));
				mCaloriesProgressBar.setMax(targetCalories);
			} else if (key
					.equals(getString(R.string.pref_targetDistances_display))) {
				// float value = Float.valueOf(sharedPreferences.getFloat(
				// getString(R.string.pref_targetDistances), 7));

				// targetDistances = (isMetric)?value:Utilities.KM2MI(value);

				String value = sharedPreferences.getString(
						getString(R.string.pref_targetDistances_display),
						getString(R.string.defalut_target_distances));
				targetDistances = Float.valueOf(value);

				goalDistancesTv.setText(Float.toString(targetDistances));

			} else if (key.equals(getString(R.string.pref_targetActivity))) {
				int targetActivity = Integer.valueOf(sharedPreferences
						.getString(getString(R.string.pref_targetActivity),
								"30"));
				try {
					// Utilities.getLog(TAG, "targetActivity " +
					// targetActivity);
					m_activityTimeProgressBar.setTarget(targetActivity);
				} catch (NullPointerException errr) {
					Log.e(TAG, "On error " + errr.getMessage());
				}
			} else if (key.equals(getString(R.string.pref_user_name))) {

			} else if (key.equals(getString(R.string.prefUnit))) {
				changeDistanceText(sharedPreferences);

			} else {

			}
		} else if (mPageNumber == 1) {

			// if (key.equals(getString(R.string.keySleepStart))) {
			// String wakeup_start = sharedPreferences.getString(key,
			// "10:00 pm");
			// ((TextView) mRootView.findViewById(R.id.sleep_start_textfield))
			// .setText(wakeup_start);
			// } else if (key.equals(getString(R.string.pref_weekend))) {
			// String wakeup_end = sharedPreferences.getString(key, "8:00 am");
			// ((TextView) mRootView.findViewById(R.id.sleep_end_textfield))
			// .setText(wakeup_end);
			//
			// } else if (key.equals(getString(R.string.pref_weekday))) {
			// String wakeup_end = sharedPreferences
			// .getString(key, "07:00 am");
			// ((TextView) mRootView.findViewById(R.id.sleep_end_textfield))
			// .setText(wakeup_end);
			//
			// } else if (key.equals(getString(R.string.keyActualSleepTime))) {
			// int value = sharedPreferences.getInt(key, 0);
			//
			// ((TextView) mRootView
			// .findViewById(R.id.sleep_time_hour_textview))
			// .setText(String.valueOf(value / 60));
			//
			// ((TextView) mRootView
			// .findViewById(R.id.sleep_time_mins_textview))
			// .setText(String.valueOf(value % 60));
			// } else if (key.equals(getString(R.string.keyTimeFallAsSleep))) {
			// ((TextView) mRootView
			// .findViewById(R.id.fall_asleep_time_mins_textview))
			// .setText(String.valueOf(sharedPreferences
			// .getInt(key, 0)));
			// }

		}
		if (key.equals(getString(R.string.pref_last_sync_time))) {
			// moved to Main.java
		}

	}

	public void onStreamMessage(int steps, int calories, float distance,
			int activityTime, int batteryLevel) {
		// TODO Auto-generated method stub
		// String s = "";
		// s += "Wristband Stream :\n";
		// s += "steps : " + steps + "\n";
		// s += "calories : " + calories + "\n";
		// s += "distance : " + distance + "\n";
		// s += "activityTime : " + activityTime + "\n";
		// s += "batteryLevel : " + batteryLevel + "\n";
		// Utilities.getLog(TAG,s);
		currentSteps = steps;
		currentCalories = calories;
		currentActivityTime = activityTime;
		currentDistanceProgress = (float) ((isMetric) ? Utilities
				.KM2MI(distance) : distance);
		if (mPageNumber == 0) {

			if (mStepsProgressBar != null)
				mStepsProgressBar.setProgress(currentSteps);
			if (mCaloriesProgressBar != null)
				mCaloriesProgressBar.setProgress(currentCalories);
			if (mDistancesProgressBar != null)
				mDistancesProgressBar
						.setProgress((int) ((currentDistanceProgress / targetDistances) * 100));
			if (m_activityTimeProgressBar != null)
				m_activityTimeProgressBar
						.setProgressInMins(currentActivityTime);

			if (mTargetStepsIndicatedTV != null)
				mTargetStepsIndicatedTV.setText(String.valueOf(currentSteps));
			if (mTargetCaloriesIndicatedTV != null)
				mTargetCaloriesIndicatedTV.setText(String
						.valueOf(currentCalories));

			String format = "%.1f";
			if (mTargetDistancesIndicatedTV != null)
				mTargetDistancesIndicatedTV.setText(String.format(format,
						currentDistanceProgress));

			if (mTargetDistancesIndicatedTV != null)
				mStepIndicatedTV.setText(String.valueOf(currentSteps));
			if (mCaloriesIndicatedTV != null)
				mCaloriesIndicatedTV.setText(String.valueOf(currentCalories));
			if (mDistancesIndicatedTV != null)
				mDistancesIndicatedTV.setText(String.format(format,
						currentDistanceProgress));
		}

	}

	protected void prevPage() {
		// TODO Auto-generated method stub
		Log.v(TAG, "PrevPage");

	}

	protected void nextPage() {
		// TODO Auto-generated method stub
		Log.v(TAG, "NextPage");
	}

	public void updateLastSlpeeRecord(final SleepRecord sleepRecord) {
		// TODO Auto-generated method stub
		if (mPageNumber == 1) {
			getActivity().runOnUiThread(new Runnable() {
				public void run() {

					((TextView) mRootView
							.findViewById(R.id.sleep_duration_textfield))
							.setText(String.valueOf(sleepRecord.getInBedTime()));

//					((TextView) mRootView
//							.findViewById(R.id.sleep_start_textfield))
//							.setText(Utilities.getSimpleTimeFormat().format(
//									sleepRecord.getGoToBedTime().getTime()));
//
//					((TextView) mRootView
//							.findViewById(R.id.sleep_end_textfield))
//							.setText(Utilities.getSimpleTimeFormat()
//									.format(sleepRecord.getActualWakeupTime()
//											.getTime()));

					int value = sleepRecord.getActualSleepTime();
					((TextView) mRootView
							.findViewById(R.id.sleep_time_hour_textview))
							.setText(String.valueOf(value / 60));

					((TextView) mRootView
							.findViewById(R.id.sleep_time_mins_textview))
							.setText(String.valueOf(value % 60));
					((TextView) mRootView
							.findViewById(R.id.fall_asleep_time_mins_textview))
							.setText(String.valueOf(sleepRecord
									.getFallingAsleepDuration()));
				}
			});

		}
	}

}
