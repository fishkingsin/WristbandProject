package com.idthk.wristband.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.Window;

public class PreferencesActivity extends Activity {
	static final String TAG = "PreferencesActivity";
	private static final String PDF_MIME_TYPE = "application/pdf";
	private static Context mContext;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title_bar);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		int targetPreferenceResource = R.xml.preferences;

		getFragmentManager()
				.beginTransaction()
				.replace(android.R.id.content,
						UserPrefsFragment.create(targetPreferenceResource))
				.commit();

		mContext = this;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	public static class UserPrefsFragment extends PreferenceFragment implements
			SharedPreferences.OnSharedPreferenceChangeListener {
		static final String ARG_XML = "xml";
		private int targetPreferenceFile;

		public static UserPrefsFragment create(int targetPreferenceFile) {
			UserPrefsFragment fragment = new UserPrefsFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_XML, targetPreferenceFile);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			targetPreferenceFile = getArguments().getInt(ARG_XML);
			// Load the preferences from an XML resource
			addPreferencesFromResource(targetPreferenceFile);
			SharedPreferences sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(this.getActivity());
			sharedPreferences.registerOnSharedPreferenceChangeListener(this);

			Preference pref = findPreference(getString(R.string.pref_targetActivity));
			pref.setSummary(sharedPreferences.getString(
					getString(R.string.pref_targetActivity), "0"));

			
			//set target distance
			String key = getString(R.string.pref_targetDistances);
			 pref = findPreference(getString(R.string.pref_targetDistances_display));
			 float value = sharedPreferences.getFloat(
					 key, 0);
			 String format = "%.1f";
			pref.setSummary(String.format(format,value));
			
			pref = findPreference(getString(R.string.pref_user_manual));
			pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
					return false;
				}
			});

			pref = findPreference("prefUserProfile");
			pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
					getActivity().overridePendingTransition(
							R.anim.slide_in_right, R.anim.slide_out_left);
					return false;

				}
				
			});
			 String unitString = sharedPreferences.getString("prefUnit", "Metric");
			 boolean isMetric = (unitString.equals("Metric"))?true:false;
			if(isMetric)
			{
				convertMetricUnit();
			}
			else
			{
				convertImperialUnit();
			}
			pref = findPreference(getString(R.string.pref_unpair));
			pref.setSummary(getString(R.string.serial)
					+ "#:"
					+ sharedPreferences.getString(
							getString(R.string.pref_serial),
							getString(R.string.default_serial_summary)));

			try {
				PackageInfo pInfo;
				pInfo = mContext.getPackageManager().getPackageInfo(
						mContext.getPackageName(), 0);
				PreferenceScreen prefScreen = (PreferenceScreen) findPreference(getString(R.string.App_Version));

				prefScreen.setSummary(pInfo.versionName);

				prefScreen = (PreferenceScreen) findPreference(getString(R.string.Wristband_Version));

				prefScreen
						.setSummary(String.valueOf(sharedPreferences.getString(
								getString(R.string.Wristband_Version), "0.0")));
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		@Override
		public void onDestroyView() {

			SharedPreferences sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(this.getActivity());
			sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
			super.onDestroyView();
		}

		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			// handle the preference change here'
			Preference pref = findPreference(key);
			if (key.equals(getString(R.string.pref_targetActivity))) {

				pref.setSummary(sharedPreferences.getString(
						getString(R.string.pref_targetActivity), "0"));

			}
			if (key.equals(getString(R.string.pref_unpair))) {
				Log.v(TAG,
						key
								+ " "
								+ String.valueOf(sharedPreferences
										.getBoolean(
												getString(R.string.pref_week_up_weekend),
												false)));
			} else if (key.equals(getString(R.string.pref_week_up_weekday))
					|| key.equals(getString(R.string.pref_week_up_weekend))) {
				boolean isWeekday = sharedPreferences.getBoolean(
						getString(R.string.pref_week_up_weekday), false);
				boolean isWeekend = sharedPreferences.getBoolean(
						getString(R.string.pref_week_up_weekend), false);

			}else if(key.equals(getString(R.string.pref_targetDistances_display)))
			{
				
				 pref = findPreference(key);
				 String value = sharedPreferences.getString(
						 key, "0");
				//pref.setSummary(value);
				
				
				SharedPreferences.Editor editor = sharedPreferences.edit();
				String unitString = sharedPreferences.getString(key, "Metric");
				 boolean isMetric = (unitString.equals("Metric"))?true:false;
				if(isMetric)
				{
					editor.putFloat( getString(R.string.pref_targetDistances), Float.valueOf(value));
				}else
				{
					editor.putFloat( getString(R.string.pref_targetDistances), Utilities.MI2KM(Float.valueOf(value)));
				}
				// Commit the edits!
				editor.commit();
				

				
			}
			else if(key.equals(getString(R.string.pref_targetCalories)))
			{
				
				 pref = findPreference(key);
				 String value = sharedPreferences.getString(
						 key, "0");
				pref.setSummary(value);
			}
			else if(key.equals(getString(R.string.pref_targetSteps)))
			{
				
				 pref = findPreference(key);
				 String value = sharedPreferences.getString(
						 key, "0");
				pref.setSummary(value);
			}
			else if(key.equals("prefUnpair"))
			{
				boolean unpair = sharedPreferences.getBoolean(key, false);
				
				 pref = findPreference(key);
				 if(unpair)
				 {
					 pref.setSummary(getString(R.string.serial)+"#:");
				 }
			}
			else if(key.equals("prefUnit"))
			{
				
				 pref = findPreference(key);
				 String unitString = sharedPreferences.getString(key, "Metric");
				 boolean isMetric = (unitString.equals("Metric"))?true:false;
				if(isMetric)
				{
					convertMetricUnit();
				}
				else
				{
					convertImperialUnit();
				}
			}

		}

		private void convertImperialUnit() {
			SharedPreferences sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(this.getActivity());
			// TODO Auto-generated method stub
			//set distance unit
			Preference targetDistanceDisplayPref = findPreference(getString(R.string.pref_targetDistances_display));
			Resources res = getResources();
			String[] planets = res.getStringArray(R.array.distance_unit);
			
			float distance = sharedPreferences.getFloat(getString(R.string.pref_targetDistances),Float.valueOf(getString(R.string.defalut_target_distances)));
			String format = "%.1f";
			
			targetDistanceDisplayPref.setTitle(getString(R.string.distances)+"("+planets[1]+")");
			String value = String.format(format,Utilities.KM2MI(distance));
			targetDistanceDisplayPref.setSummary(value);
			
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString( value, getString(R.string.defalut_target_distances));
			// Commit the edits!
			editor.commit();
			
		}

		private void convertMetricUnit() {
			SharedPreferences sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(this.getActivity());
			// TODO Auto-generated method stub
			Preference targetDistanceDisplayPref = findPreference(getString(R.string.pref_targetDistances_display));
			
			Resources res = getResources();
			String[] planets = res.getStringArray(R.array.distance_unit);
			float distance = sharedPreferences.getFloat(getString(R.string.pref_targetDistances),Float.valueOf(getString(R.string.defalut_target_distances)));
			String format = ".1f%";
			
			targetDistanceDisplayPref.setTitle(getString(R.string.distances)+"("+planets[0]+")");
			
			String value = String.valueOf(distance);
			targetDistanceDisplayPref.setSummary(value);
			
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString( value, getString(R.string.defalut_target_distances));
			// Commit the edits!
			editor.commit();

			
		}
	}
}
