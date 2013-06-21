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

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
//import java.lang.reflect.Array;
//import java.util.ArrayList;
//import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

//import org.xmlpull.v1.XmlPullParser;

import com.idthk.wristband.graphview.RoundBarGraphView;
import com.idthk.wristband.ui.R;
import com.idthk.wristband.ui.preference.TimePreference;
//import com.idthk.wristband.ui.ScrollPagerMain.ScrollPagerMainCallback;
//import com.tomoki.iwai.ScrollPagerHorizontal;
//import com.tomoki.iwai.ScrollPagerVertical;

import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.graphics.Rect;
//import android.graphics.drawable.ClipDrawable;
//import android.graphics.drawable.Drawable;
//import android.graphics.drawable.ShapeDrawable;
//import android.graphics.drawable.shapes.RoundRectShape;
//import android.os.AsyncTask;
//import android.app.Fragment;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.DialogPreference;
//import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
//import android.util.AttributeSet;
import android.util.Log;
//import android.util.Xml;
//import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
//import android.widget.FrameLayout;
//import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TimePicker;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;

//import com.jjoe64.graphview.LineGraphView;

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
	public static final String TAG = "MainSlideFragment";
	private ViewGroup mRootView;
	private OnShareButtonClickedListener mCallback;
	private CustomProgressBar m_activityTimeProgressBar;

	private ProgressBar m_stepsProgressBar = null;
	private ProgressBar m_caloriesProgressBar = null;
	private ProgressBar m_distancesProgressBar = null;

	private View targetView = null;
	private View nonTargetView = null;
	private TextView goalStepsTv = null;
	private TextView goalCaloriesTv = null;
	private TextView goalDistancesTv = null;
	private TextView userNameTv = null;
	private GraphView mGraphView = null;

	private TextView target_steps_indicated_textview = null;
	private TextView target_calories_indicated_textview = null;
	private TextView target_distances_indicated_textview = null;

	private TextView steps_indicated_textview = null;
	private TextView calories_indicated_textview = null;
	private TextView distances_indicated_textview = null;
	private ImageView battery_indicated_imageview = null;

	public interface OnShareButtonClickedListener {
		public void onShareButtonClicked(String s);

		public void dispatchSelf(MainFragment mainSlideFragment);
	}

	/**
	 * The fragment's page number, which is set to the argument value for
	 * {@link #ARG_PAGE}.
	 */
	private int mPageNumber;
	private TextView lastSyncTimeTv;
	static private int targetSteps = 1;
	static private int targetCalories = 1;
	static private int targetDistances = 1;

	private Integer currentActivityTime = 0;
	private float currentDistanceProgress = 0;
	private Integer currentCalories = 0;
	private Integer currentSteps = 0;
	private Integer currentBatteryLevel = 0;

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
		Main mainActivity = (Main) activity;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPageNumber = getArguments().getInt(ARG_PAGE);
		// Log.v(TAG, "ScreenSlidePageFragment : ID " + this.getId());
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
					R.layout.main_scrollview_activity, container, false);
			userNameTv = ((TextView) mRootView.findViewById(R.id.userNameTv));
			lastSyncTimeTv = ((TextView) mRootView
					.findViewById(R.id.last_sync_time_textview));

			m_activityTimeProgressBar = (CustomProgressBar) mRootView
					.findViewById(R.id.target_progress_bar_large);

			m_activityTimeProgressBar.setTarget(0);
			m_activityTimeProgressBar.setProgressInMins(0);
			if (mRootView.findViewById(R.id.steps_progressbar) != null) {
				m_stepsProgressBar = (ProgressBar) mRootView
						.findViewById(R.id.steps_progressbar);
				m_stepsProgressBar.setProgress(0);
			}
			if (mRootView.findViewById(R.id.calories_progressbar) != null) {
				m_caloriesProgressBar = (ProgressBar) mRootView
						.findViewById(R.id.calories_progressbar);
				m_caloriesProgressBar.setProgress(0);
			}
			if (mRootView.findViewById(R.id.distances_progressbar) != null) {
				m_distancesProgressBar = (ProgressBar) mRootView
						.findViewById(R.id.distances_progressbar);
				m_distancesProgressBar.setProgress(0);
			}

			target_steps_indicated_textview = ((TextView) mRootView
					.findViewById(R.id.target_steps_indicated_textview));
			target_calories_indicated_textview = ((TextView) mRootView
					.findViewById(R.id.target_calories_indicated_textview));
			target_distances_indicated_textview = ((TextView) mRootView
					.findViewById(R.id.target_distances_indicated_textview));

			steps_indicated_textview = ((TextView) mRootView
					.findViewById(R.id.steps_indicated_textview));
			calories_indicated_textview = ((TextView) mRootView
					.findViewById(R.id.calories_indicated_textview));
			distances_indicated_textview = ((TextView) mRootView
					.findViewById(R.id.distances_indicated_textview));

			publishSettings(sharedPreferences);

			String path = sharedPreferences.getString(
					getString(R.string.pref_profile_pic), "");
			// Log.v(TAG, "profile path : " + path);
			if (path != "") {
				Bitmap myBitmap = Utilities.decodeFile(new File(path),
						this.getActivity());
				((ImageView) mRootView.findViewById(R.id.profile_pic))
						.setImageBitmap(myBitmap);
			}
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
			if (mRootView.findViewById(R.id.battery_image) != null) {
				battery_indicated_imageview = ((ImageView) mRootView
						.findViewById(R.id.battery_image));
			}
			final ScrollView scrollView = (ScrollView) mRootView
					.findViewById(R.id.main_activity_scroll_view);

			scrollView.post(new Runnable() {
				public void run() {
					scrollView.scrollTo(0, 0);
				}
			});

			String s = "2012-05-08";
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			((TextView) mRootView.findViewById(R.id.today_text_view))
					.setText(sdf.format(cal.getTime()));

			// new UpdateBarTask().execute();//currentActivityTime,
			// currentDistanceProgress, currentCalories,
			// currentSteps,currentBatteryLevel);

		} else {
			mRootView = (ViewGroup) inflater.inflate(
					R.layout.main_scrollview_sleep, container, false);
			userNameTv = ((TextView) mRootView.findViewById(R.id.userNameTv));
			lastSyncTimeTv = ((TextView) mRootView
					.findViewById(R.id.last_sync_time_textview));
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
			if (mRootView.findViewById(R.id.battery_image) != null) {
				battery_indicated_imageview = ((ImageView) mRootView
						.findViewById(R.id.battery_image));
			}
			String path = sharedPreferences.getString(
					getString(R.string.pref_profile_pic), "");
			// Log.v(TAG, "profile path : " + path);
			if (path != "") {
				Bitmap myBitmap = Utilities.decodeFile(new File(path),
						this.getActivity());
				((ImageView) mRootView.findViewById(R.id.profile_pic))
						.setImageBitmap(myBitmap);
			}

			publishSettings(sharedPreferences);
			populateGraph(mRootView);
			// mCallback.dispatchSelf(this);
			// new UpdateBarTask().execute();
		}

		return mRootView;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);

		// Log.v(TAG,"setUserVisibleHint " + isVisibleToUser + " " + mPageNumber
		// );
		if (isVisibleToUser) {
			mCallback.dispatchSelf(this);
		}
	}

	private void publishSettings(SharedPreferences prefs) {
		// TODO Auto-generated method stub
		if (mPageNumber == 0) {
			boolean target = prefs.getBoolean(
					getString(R.string.pref_toggle_target), true);
			targetView = ((View) mRootView.findViewById(R.id.target_layout_on));
			nonTargetView = ((View) mRootView
					.findViewById(R.id.target_layout_off));

			targetView.setVisibility((target) ? View.VISIBLE : View.GONE);

			nonTargetView.setVisibility((target) ? View.GONE : View.VISIBLE);

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
			if (m_stepsProgressBar != null)
				m_stepsProgressBar.setMax(targetSteps);
			if (m_caloriesProgressBar != null)
				m_caloriesProgressBar.setMax(targetCalories);
			if (m_distancesProgressBar != null)
				m_distancesProgressBar.setMax(100);

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
			}
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

		if(lastSyncTimeTv!=null)lastSyncTimeTv.setText(prefs.getString(
				getString(R.string.pref_last_sync_time),
				getString(R.string.default_last_sync_time)));
		if(userNameTv!=null)userNameTv.setText(prefs.getString(getString(R.string.pref_user_name),
				getString(R.string.default_user_name)));
	}

	private void populateGraph(View mRootView) {
		// TODO Auto-generated method stub
		// init example series data
		Random random = new Random();
		int numBars = 20;
		GraphViewData data[] = new GraphViewData[numBars];

		for (int i = 0; i < numBars; i++) {
			data[i] = new GraphViewData(i, random.nextInt(10));
		}
		// {
		// new GraphViewData(1, 2.0d),
		// new GraphViewData(2, 1.5d),
		// new GraphViewData(2.5, 3.0d),
		// new GraphViewData(3, 2.5d),
		// new GraphViewData(4, 1.0d),
		// new GraphViewData(5, random.nextInt(10)),
		// new GraphViewData(6, random.nextInt(10)),
		// new GraphViewData(7, random.nextInt(10)),
		// new GraphViewData(8, random.nextInt(10)),
		// new GraphViewData(9, random.nextInt(10))};

		GraphViewSeries exampleSeries = new GraphViewSeries(data);

		// graph with dynamically genereated horizontal and vertical labels
		if (mGraphView != null) {
			mGraphView = null;
		}

		mGraphView = new RoundBarGraphView(getActivity(), "");

		mGraphView.setHorizontalLabels(new String[] {
				getString(R.string.start), getString(R.string.end) });
		mGraphView.setVerticalLabels(new String[] { getString(R.string.high),
				getString(R.string.middle), getString(R.string.low) });
		mGraphView.addSeries(exampleSeries); // data

		mGraphView.setViewPort(10, 5);
		// mGraphView.setScrollable(true);
		// optional - activate scaling / zooming
		// mGraphView.setScalable(true);

		((ViewGroup) mRootView.findViewById(R.id.graph1)).addView(mGraphView);

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
				targetView.setVisibility((target) ? View.VISIBLE : View.GONE);

				nonTargetView
						.setVisibility((target) ? View.GONE : View.VISIBLE);
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
				userNameTv.setText(sharedPreferences.getString(
						getString(R.string.pref_user_name),
						getString(R.string.default_user_name)));
			} else {
				// Log.v(TAG, "key :" + key);
			}
		} else if (mPageNumber == 1) {

			if (key.equals(getString(R.string.pref_weekday))) {

			} else if (key.equals(getString(R.string.pref_weekend))) {

			}

		}
		if (key.equals(getString(R.string.pref_last_sync_time))) {
			lastSyncTimeTv.setText(sharedPreferences.getString(
					getString(R.string.pref_last_sync_time),
					getString(R.string.default_last_sync_time)));
		}
	}

	public void onStreamMessage(int steps, int calories, float distance,
			int activityTime, int batteryLevel) {
		// TODO Auto-generated method stub
		String s = "";
		s += "Wristband Stream :\n";
		s += "steps : " + steps + "\n";
		s += "calories : " + calories + "\n";
		s += "distance : " + distance + "\n";
		s += "activityTime : " + activityTime + "\n";
		s += "batteryLevel : " + batteryLevel + "\n";
		Log.v(TAG, s);
		currentSteps = steps;
		currentCalories = calories;
		currentActivityTime = activityTime;
		currentDistanceProgress = distance;
		currentBatteryLevel = batteryLevel;

		// new UpdateBarTask().execute();
		// class UpdateBarTask extends AsyncTask<Void, Integer, Void> {
		//
		//
		//
		// @Override
		// protected Void doInBackground(Void... params) {
		// //***sometime wont do
		// Log.v("UpdateBarTask", "doInBackground");
		// publishProgress();
		//
		//
		// return null;
		// }
		//
		// @Override
		// protected void onProgressUpdate(Integer... values) {
		if (mPageNumber == 0) {
			if (m_stepsProgressBar != null)
				m_stepsProgressBar.setProgress(currentSteps);
			if (m_caloriesProgressBar != null)
				m_caloriesProgressBar.setProgress(currentCalories);
			if (m_distancesProgressBar != null)
				m_distancesProgressBar
						.setProgress((int) ((currentDistanceProgress / targetDistances) * 100));
			if (m_activityTimeProgressBar != null)
				m_activityTimeProgressBar
						.setProgressInMins(currentActivityTime);

			if (target_steps_indicated_textview != null)
				target_steps_indicated_textview.setText(String
						.valueOf(currentSteps));
			if (target_calories_indicated_textview != null)
				target_calories_indicated_textview.setText(String
						.valueOf(currentCalories));
			if (target_distances_indicated_textview != null)
				target_distances_indicated_textview.setText(String
						.valueOf(currentDistanceProgress));

			if (target_distances_indicated_textview != null)
				steps_indicated_textview.setText(String.valueOf(currentSteps));
			if (calories_indicated_textview != null)
				calories_indicated_textview.setText(String
						.valueOf(currentCalories));
			if (distances_indicated_textview != null)
				distances_indicated_textview.setText(String
						.valueOf(currentDistanceProgress));
		}
		if (battery_indicated_imageview != null) {
			if (currentBatteryLevel < 33) {
				battery_indicated_imageview
						.setImageResource(R.drawable.battery_0);
			} else if (currentBatteryLevel > 33 && currentBatteryLevel < 66) {
				battery_indicated_imageview
						.setImageResource(R.drawable.battery_1);
			} else if (currentBatteryLevel > 66) {
				battery_indicated_imageview
						.setImageResource(R.drawable.battery_2);
			}
		}
	}
	// }
	// UpdateBarTask myTask = new UpdateBarTask();
	// myTask.execute();
	// }

}
