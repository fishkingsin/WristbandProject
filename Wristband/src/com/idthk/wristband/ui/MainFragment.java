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

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import com.idthk.wristband.ui.preference.TimePreference;


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
		SharedPreferences.OnSharedPreferenceChangeListener {
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
	static private int targetDistances = 1;

	private Integer currentActivityTime = 0;
	private float currentDistanceProgress = 0;
	private Integer currentCalories = 0;
	private Integer currentSteps = 0;
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
		Log.v(TAG, "onResume");
		super.onResume();
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

		// Log.v(TAG,"Tag : "+getTag());
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

			publishSettings(sharedPreferences);

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

			final ScrollView scrollView = (ScrollView) mRootView
					.findViewById(R.id.main_activity_scroll_view);

			scrollView.post(new Runnable() {
				public void run() {
					scrollView.scrollTo(0, 0);
				}
			});

		} else {
			mRootView = (ViewGroup) inflater.inflate(
					R.layout.main_fragment_sleep, container, false);

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

			publishSettings(sharedPreferences);
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

			targetSteps = prefs.getInt(getString(R.string.pref_targetSteps),
					Integer.valueOf(getString(R.string.defalut_target_steps)));
			targetCalories = prefs
					.getInt(getString(R.string.pref_targetCalories),
							Integer.valueOf(getString(R.string.defalut_target_calories)));
			targetDistances = prefs
					.getInt(getString(R.string.pref_targetDistances),
							Integer.valueOf(getString(R.string.defalut_target_distances)));

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
			goalStepsTv.setText("" + targetSteps);
			goalCaloriesTv = ((TextView) mRootView
					.findViewById(R.id.goal_calories_indicate_textview));
			goalCaloriesTv.setText("" + targetCalories);
			goalDistancesTv = ((TextView) mRootView
					.findViewById(R.id.goal_distances_indicat_textview));
			goalDistancesTv.setText("" + targetDistances);

		} else {
			Calendar datetime = Calendar.getInstance();

			int weekday = datetime.get(Calendar.WEEK_OF_MONTH);

			String startSleep, endSleep;
			int inbedTime = prefs.getInt(getString(R.string.pref_in_bed_time),
					8);

			String format = "%1$02d";

			if (weekday == 5 || weekday == 6) {
				startSleep = prefs.getString(getString(R.string.pref_weekend),
						"00:00");
				startSleep = String.format(format,
						TimePreference.getHour(startSleep))
						+ ":"
						+ String.format(format,
								TimePreference.getMinute(startSleep))
						+ TimePreference.getAmPm(startSleep);
				endSleep = prefs.getString(
						getString(R.string.pref_weekend_wake), "08:00AM");

			} else {
				startSleep = prefs.getString(getString(R.string.pref_weekday),
						"00:00");
				startSleep = String.format(format,
						TimePreference.getHour(startSleep))
						+ ":"
						+ String.format(format,
								TimePreference.getMinute(startSleep))
						+ TimePreference.getAmPm(startSleep);
				endSleep = prefs.getString(
						getString(R.string.pref_weekend_wake), "08:00AM");
			
			if (mRootView.findViewById(R.id.sleep_start_textfield) != null)
				((TextView) mRootView.findViewById(R.id.sleep_start_textfield))
						.setText(startSleep);
			if (mRootView.findViewById(R.id.sleep_end_textfield) != null)
				((TextView) mRootView.findViewById(R.id.sleep_end_textfield))
						.setText(endSleep);
			if (mRootView.findViewById(R.id.sleep_duration_textfield) != null)
				((TextView) mRootView
						.findViewById(R.id.sleep_duration_textfield))
						.setText(String.valueOf(inbedTime));
			}
		}

	}

	private void populateGraph(View mRootView) {
		if (mPageNumber == 0) {
			Calendar now = Calendar.getInstance();
			Utilities.targetDate().setTime(now.getTime());
			Utilities.publishGraph(getActivity(), mRootView,
					((ViewGroup) mRootView.findViewById(R.id.graph1)),
					SleepStatisticTabFragment.TAB_DAY);
		} else {
			// TODO implement sleep patter graph view
			Utilities.populateSleepPatternGraph(getActivity(), mRootView,((ViewGroup) mRootView
					.findViewById(R.id.graph1)));
		}

	}

	/*private void populateSleepPatternGraph(ViewGroup graph) {
		graph.removeAllViews();
		GraphViewSeriesStyle style = new GraphViewSeriesStyle();
		style.thickness = 5;
		style.color = 0xFF73CBfD;

		Context context = getActivity();
		
		
		LineGraphView mGraphView = new LineGraphView(context, "");
		DatabaseHandler db = new DatabaseHandler(context, Main.TABLE_CONTENT,
				null, 1);
		
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		
		SleepRecord sleeprecord = db.getLastSleepRecord();
		
		((TextView)mRootView.findViewById(R.id.sleep_time_hour_textview)).setText(df.format(sleeprecord.getGoToBedTime().getTime()));
	
		((TextView)mRootView.findViewById(R.id.sleep_end_textfield)).setText(df.format(sleeprecord.getActualWakeupTime().getTime()));
		((TextView)mRootView.findViewById(R.id.sleep_duration_textfield)).setText(String.valueOf(sleeprecord.getInBedTime()));
		if (sleeprecord != null) {
			List<SleepPattern> patterns = sleeprecord.getPatterns();
			List <GraphViewData> data = new ArrayList<GraphViewData>();

			int j = 0;
			int xValue = 0;
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			for (SleepPattern pattern : patterns) {
				for(int i = 0 ; i < pattern.getDuration();i++)
				{
					int a = 0;
					switch(pattern.getAmplitude())
					{
					case 22:
							a = 1;
						break;
					case 44:
							a = 2;
						break;
					case 64:

					case 66:
						a = 3;
						break;
					}
					data.add( new GraphViewData(xValue, a));
					xValue++;
				}
				
			}
			
			GraphViewData[] a = data.toArray(new GraphViewData[data.size()]);
			GraphViewSeries series = new GraphViewSeries("Hour", style,
					a);

			mGraphView.setManualYAxisBounds(3, 0);
			mGraphView.addSeries(series);
			// stuff that updates ui
			graph.addView(mGraphView);
		}
	}
*/
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
				targetSteps = sharedPreferences.getInt(
						getString(R.string.pref_targetSteps), 0);
				goalStepsTv.setText(Integer.toString(targetSteps));
			} else if (key.equals(getString(R.string.pref_targetCalories))) {
				targetCalories = sharedPreferences.getInt(
						getString(R.string.pref_targetCalories), 0);
				goalCaloriesTv.setText(Integer.toString(targetCalories));
			} else if (key.equals(getString(R.string.pref_targetDistances))) {
				targetDistances = sharedPreferences.getInt(
						getString(R.string.pref_targetDistances), 0);
				goalDistancesTv.setText(Integer.toString(targetDistances));
			} else if (key.equals(getString(R.string.pref_targetActivity))) {
				int targetActivity = Integer.valueOf(sharedPreferences
						.getString(getString(R.string.pref_targetActivity),
								"30"));
				try {
					// Log.v(TAG, "targetActivity " + targetActivity);
					m_activityTimeProgressBar.setTarget(targetActivity);
				} catch (NullPointerException errr) {
					Log.e(TAG, "On error " + errr.getMessage());
				}
			} else if (key.equals(getString(R.string.pref_user_name))) {

			} else {

			}
		} else if (mPageNumber == 1) {

			if (key.equals(getString(R.string.keySleepStart))) {
				String wakeup_start = sharedPreferences.getString(key, "10:00 pm");
				((TextView)mRootView.findViewById(R.id.sleep_start_textfield)).setText(wakeup_start);
			}
			else if (key.equals(getString(R.string.pref_weekend))) {
				String wakeup_end = sharedPreferences.getString(key, "8:00 am");
				((TextView)mRootView.findViewById(R.id.sleep_end_textfield)).setText(wakeup_end);
				
			}
			else if (key.equals(getString(R.string.pref_weekday))) {
				String wakeup_end = sharedPreferences.getString(key, "07:00 am");
				((TextView)mRootView.findViewById(R.id.sleep_end_textfield)).setText(wakeup_end);
				
			}
			else if(key.equals(getString(R.string.keyActualSleepTime)))
			{
				int value = sharedPreferences.getInt(key, 0);
				
				 ((TextView)mRootView.findViewById(R.id.sleep_time_hour_textview)).setText(String.valueOf(value/60));
				 
				 ((TextView)mRootView.findViewById(R.id.sleep_time_mins_textview)).setText(String.valueOf(value%60));
			}
			else if(key.equals(getString(R.string.keyTimeFallAsSleep)))
			{
				((TextView)mRootView.findViewById(R.id.fall_asleep_time_mins_textview)).setText(String.valueOf(sharedPreferences.getInt(key, 0)));
			}
				

		}
		if (key.equals(getString(R.string.pref_last_sync_time))) {
			// moved to Main.java
		}
		

	}

	public void onStreamMessage(int steps, int calories, float distance,
			int activityTime, int batteryLevel) {
		// TODO Auto-generated method stub
//		String s = "";
//		s += "Wristband Stream :\n";
//		s += "steps : " + steps + "\n";
//		s += "calories : " + calories + "\n";
//		s += "distance : " + distance + "\n";
//		s += "activityTime : " + activityTime + "\n";
//		s += "batteryLevel : " + batteryLevel + "\n";
//		Log.v(TAG,s);
		currentSteps = steps;
		currentCalories = calories;
		currentActivityTime = activityTime;
		currentDistanceProgress = distance;
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
			if (mTargetDistancesIndicatedTV != null)
				mTargetDistancesIndicatedTV.setText(String
						.valueOf(currentDistanceProgress));

			if (mTargetDistancesIndicatedTV != null)
				mStepIndicatedTV.setText(String.valueOf(currentSteps));
			if (mCaloriesIndicatedTV != null)
				mCaloriesIndicatedTV.setText(String.valueOf(currentCalories));
			if (mDistancesIndicatedTV != null)
				mDistancesIndicatedTV.setText(String
						.valueOf(currentDistanceProgress));
		}

	}

}
