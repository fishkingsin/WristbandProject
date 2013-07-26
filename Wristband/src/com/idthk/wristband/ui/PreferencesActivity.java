package com.idthk.wristband.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.idthk.wristband.ui.preference.NumberPickerPreference;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
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
						PrefsFragment.create(targetPreferenceResource))
				.commit();

		mContext = this;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	public static class PrefsFragment extends PreferenceFragment implements
			SharedPreferences.OnSharedPreferenceChangeListener {
		static final String ARG_XML = "xml";
		private int targetPreferenceFile;

		public static PrefsFragment create(int targetPreferenceFile) {
			PrefsFragment fragment = new PrefsFragment();
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

			
			EditTextPreference editTextPref = (EditTextPreference) findPreference(getString(R.string.pref_targetSteps));
			editTextPref.setSummary(sharedPreferences.getString(
					getString(R.string.pref_targetSteps), "0"));

			editTextPref = (EditTextPreference) findPreference(getString(R.string.pref_targetCalories));
			editTextPref.setSummary(sharedPreferences.getString(
					getString(R.string.pref_targetCalories), "0"));

			editTextPref = (EditTextPreference) findPreference(getString(R.string.pref_targetDistances_display));
			editTextPref.setSummary(sharedPreferences.getString(
					getString(R.string.pref_targetDistances_display), "0"));
			

			pref = findPreference(getString(R.string.pref_user_manual));
			pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
//					Intent intents = new Intent(getActivity(),MyWebView.class);
//					getActivity().startActivity(intents);
					
					/*Intent intent = new Intent(Intent.ACTION_VIEW,
					        Uri.parse("file:///android_assets/manual.pdf"));
					intent.setType("application/pdf");
					PackageManager pm = getActivity().getPackageManager();
					List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
					if (activities.size() > 0) {
					    startActivity(intent);
					} else {
					    // Do something else here. Maybe pop up a Dialog or Toast
					}*/
					Uri path = Uri.parse("file:///android:asset/manual.pdf");
					Log.v(TAG,path.toString());
					   Intent intent  = new Intent(Intent.ACTION_VIEW);
					   intent.setDataAndType(path, "application/pdf");
					   intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					   startActivity(intent);
					
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
			covertUnit(sharedPreferences);
			
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
						
			else if (key.equals(getString(R.string.pref_unpair))) {
				Utilities.getLog(TAG,
						key
								+ " "
								+ String.valueOf(sharedPreferences
										.getBoolean(
												getString(R.string.pref_unpair),
												false)));
			} else if (key.equals(getString(R.string.pref_week_up_weekday))
					|| key.equals(getString(R.string.pref_week_up_weekend))) {
				boolean isWeekday = sharedPreferences.getBoolean(
						getString(R.string.pref_week_up_weekday), false);
				boolean isWeekend = sharedPreferences.getBoolean(
						getString(R.string.pref_week_up_weekend), false);

			} else if (key
					.equals(getString(R.string.pref_targetDistances_display))) {

				pref = findPreference(key);
				String value = sharedPreferences.getString(key, "0");
				pref.setSummary(value);
				//set distance and convert to km unit
				SharedPreferences.Editor editor = sharedPreferences.edit();
				String unitString = sharedPreferences.getString(getString(R.string.prefUnit), "Metric");
				boolean isMetric = (unitString.equals("Metric")) ? true : false;
				if (isMetric) {
					editor.putFloat(getString(R.string.pref_targetDistances),
							Float.valueOf(value));
				} else {
					editor.putFloat(getString(R.string.pref_targetDistances),
							Utilities.MI2KM(Float.valueOf(value)));
				}
				// Commit the edits!
				editor.commit();

			} else if (key.equals(getString(R.string.pref_targetCalories))) {

				pref = findPreference(key);
				String value = sharedPreferences.getString(key, "0");
				pref.setSummary(value);
			} else if (key.equals(getString(R.string.pref_targetSteps))) {

				pref = findPreference(key);
				String value = sharedPreferences.getString(key, "0");
				pref.setSummary(value);
			} else if (key.equals("prefUnpair")) {
				boolean unpair = sharedPreferences.getBoolean(key, false);

				pref = findPreference(key);
				if (unpair) {
					pref.setSummary(getString(R.string.serial) + "#:");
				}
			} else if (key.equals(getString(R.string.prefUnit))) {

				covertUnit(sharedPreferences);
			}

		}
		private void covertUnit(SharedPreferences sharedPreferences)
		{
			 
			String unitString = sharedPreferences.getString(getString(R.string.prefUnit),
					"Metric");
			boolean isMetric = (unitString.equals("Metric")) ? true : false;
			
			Preference pref = findPreference(getString(R.string.pref_targetDistances_display));
			float fV = sharedPreferences.getFloat(getString(R.string.pref_targetDistances), 7);
			String fformat = "%.1f";
			EditTextPreference editPref = (EditTextPreference)pref;
			Resources res = getResources();
			String distance_unit[] = res.getStringArray(R.array.distance_unit);
			if (isMetric) {
				String st = String.format(fformat , fV);
				editPref.setTitle(distance_unit[0]);
				editPref.setSummary(st);
				editPref.setText(st);
			} else {
				String st = String.format(fformat , Utilities.KM2MI(fV));
				editPref.setTitle(distance_unit[1]);
				editPref.setSummary(st);
				editPref.setText(st);
			}
			
			
			
		}
		
	}
}
