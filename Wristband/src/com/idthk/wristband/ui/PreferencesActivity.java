package com.idthk.wristband.ui;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import com.idthk.wristband.ui.R;

import android.app.Activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

public class PreferencesActivity extends Activity {
	static final String TAG = "PreferencesActivity";
	private static final String PDF_MIME_TYPE = "application/pdf";
	private static Context mContext;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_profile);

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
					getString(R.string.pref_targetActivity),
					"0"));
			pref = findPreference(getString(R.string.pref_user_manual));
			pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {

				    
					return false;
					
				}
			});
			boolean isWeekday = sharedPreferences.getBoolean(getString(R.string.pref_week_up_weekday),false);
			boolean isWeekend = sharedPreferences.getBoolean(getString(R.string.pref_week_up_weekend),false);
			
//			PreferenceScreen prefScreen = (PreferenceScreen) findPreference(getString(R.string.pref_week_up_time));
//			String summary=((isWeekday==true)?"Weekday":" ")+((isWeekend == false)?"Weekend":" ");
//			prefScreen.setSummary(summary);
			
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
			if(key.equals(getString(R.string.pref_targetActivity)))
			{
				
				pref.setSummary(sharedPreferences.getString(
						getString(R.string.pref_targetActivity),
						"0"));
				
			}
			if(key.equals(getString(R.string.pref_week_up_weekday)) ||
					key.equals(getString(R.string.pref_week_up_weekend)))
			{
				boolean isWeekday = sharedPreferences.getBoolean(getString(R.string.pref_week_up_weekday),false);
				boolean isWeekend = sharedPreferences.getBoolean(getString(R.string.pref_week_up_weekend),false);
				
//				PreferenceScreen prefScreen = (PreferenceScreen) findPreference(getString(R.string.pref_week_up_time));
//				String summary=((isWeekday==true)?"Weekday":" ")+((isWeekend == false)?"Weekend":" ");
////				Log.v(TAG,"isWeekday "+isWeekday + " isWeekend "+isWeekday + "summary "+summary);
//				prefScreen.setSummary(summary);
			}
			
//			else if(key.equals(getString(R.string.target_step)))
//			{
//				
//				pref.setSummary(sharedPreferences.getInt(
//						getString(R.string.target_step),
//						10000));
//				
//			}

		}
	}
}
