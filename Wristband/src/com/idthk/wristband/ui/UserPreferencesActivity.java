package com.idthk.wristband.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.bostonandroid.datepreference.DatePreference;

import com.idthk.wristband.ui.preference.NumberPickerPreference;
import com.idthk.wristband.ui.preference.PhotoPickerPreference;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

public class UserPreferencesActivity extends Activity {
	static final String TAG = "UserProfileActivity";
	private Activity mContext;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title_bar);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mContext = this;
		int targetPreferenceResource = R.xml.userprofile_preferences;
		UserPrefsFragment fragment = UserPrefsFragment
				.create(targetPreferenceResource);
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, fragment).commit();

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
		private Context mContext;
		private View mRootView;
		final int PIC_CROP = 2;
		private Uri picUri;
		private static List<CharSequence> pref_user_height_metric_entries;
		private static List<CharSequence> pref_user_height_metric_entryvalues;
		private static List<CharSequence> pref_user_height_imperial_entries;
		private static List<CharSequence> pref_user_height_imperial_entryvalues;

		public static UserPrefsFragment create(int targetPreferenceFile) {
			UserPrefsFragment fragment = new UserPrefsFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_XML, targetPreferenceFile);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public void onAttach(Activity activity) {
			mContext = activity;
			super.onAttach(activity);
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			int start = 69;

			int range = 231 - 69;
			int step = start;

			pref_user_height_metric_entries = new ArrayList<CharSequence>();
			pref_user_height_metric_entryvalues = new ArrayList<CharSequence>();
			pref_user_height_imperial_entries = new ArrayList<CharSequence>();
			pref_user_height_imperial_entryvalues = new ArrayList<CharSequence>();
			for (int i = 0; i <= range; i++) {
				pref_user_height_metric_entries.add(String.valueOf(step));
				pref_user_height_metric_entryvalues.add(String.valueOf(step));
				step++;
			}

			for (int foot = 3; foot <= 7; foot++) {
				for (int inch = 1; inch <= 11; inch++) {
					if (foot != 3 && foot != 2) {
						String msg = String.valueOf(foot) + "'"
								+ String.valueOf(inch) + "\"";
						float v = (float) ((foot * 12 + inch) * 2.54);
						// Utilities.getLog(TAG, msg);
						pref_user_height_imperial_entries.add(msg);
						pref_user_height_imperial_entryvalues.add(String
								.valueOf((int) v));
					}

				}
			}

			super.onCreate(savedInstanceState);
			targetPreferenceFile = getArguments().getInt(ARG_XML);
			// Load the preferences from an XML resource
			addPreferencesFromResource(targetPreferenceFile);

			SharedPreferences sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(mContext);
			sharedPreferences.registerOnSharedPreferenceChangeListener(this);

			Preference pref = findPreference(getString(R.string.pref_user_name));
			pref.setSummary(sharedPreferences.getString(
					getString(R.string.pref_user_name),
					getString(R.string.default_user_name)));

			pref = findPreference(getString(R.string.prefUserGender));
			String gender = sharedPreferences.getString(
					getString(R.string.prefUserGender),
					getString(R.string.default_user_gender));
			pref.setSummary(gender);

			pref = findPreference(getString(R.string.prefUnit));
			pref.setSummary(sharedPreferences.getString(
					getString(R.string.prefUnit),
					getString(R.string.default_unit)));

			covertUnit(sharedPreferences);

			pref = findPreference(getString(R.string.prefDateOfBirth));
			DatePreference datePRef = ((DatePreference) pref);
			datePRef.setSummary();
			
		}

		@Override
		public void onDestroyView() {

			SharedPreferences sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(mContext);
			sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
			super.onDestroyView();
		}

		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			// handle the preference change here'

			Preference pref = findPreference(key);
			if (key.equals(getString(R.string.prefUserGender))) {

				String gender = sharedPreferences.getString(
						getString(R.string.prefUserGender),
						getString(R.string.default_user_gender));
				pref.setSummary(gender);

			} else if (key.equals(getString(R.string.prefUnit))) {
				pref.setSummary(sharedPreferences.getString(key, "Metric"));

				covertUnit(sharedPreferences);

			} else if (key.equals(getString(R.string.pref_user_name))) {
				pref.setSummary(sharedPreferences.getString(key,
						getString(R.string.default_user_name)));
			} else if (key.equals(getString(R.string.prefWeightDisplay))) {

				pref = findPreference(key);
				int value = sharedPreferences.getInt(key, Integer
						.valueOf(getString(R.string.default_user_weight)));
				pref.setSummary(value);
				// set distance and convert to km unit
				SharedPreferences.Editor editor = sharedPreferences.edit();
				String unitString = sharedPreferences.getString(
						getString(R.string.prefUnit), "Metric");
				boolean isMetric = (unitString.equals("Metric")) ? true : false;
				if (isMetric) {
					editor.putInt(getString(R.string.prefWeight),
							Integer.valueOf(value));
				} else {
					editor.putInt(getString(R.string.prefWeight),
							(int) Utilities.LBS2KG(Float.valueOf(value)));
				}
				// Commit the edits!
				editor.commit();

			}
			//
			else if (key.equals(getString(R.string.prefHeightDisplay))) {
				//

				String value = sharedPreferences.getString(key,
						getString(R.string.default_user_height));
				ListPreference ListPref = (ListPreference) findPreference(key);
				if (ListPref.getEntry() != null)
					ListPref.setSummary(ListPref.getEntry());
				else {
					ListPref.setSummary(getString(R.string.default_user_height));
					ListPref.setValueIndex(0);
				}
				// // set distance and convert to km unit
				SharedPreferences.Editor editor = sharedPreferences.edit();
				String unitString = sharedPreferences.getString(
						getString(R.string.prefUnit), "Metric");

				editor.putInt(getString(R.string.prefHeight),
						Integer.valueOf(value));
				// // Commit the edits!
				editor.commit();
				//
			} else if (key.equals(getString(R.string.pref_profile_pic))) {

				String s = sharedPreferences.getString(key,
						PhotoPickerPreference.TAG_CAPTURE_PHOTO);
				if (s.equals(PhotoPickerPreference.TAG_CAPTURE_PHOTO)) {
					Intent captureIntent = new Intent(
							MediaStore.ACTION_IMAGE_CAPTURE);
					// we will handle the returned data in onActivityResult
					startActivityForResult(captureIntent, Main.TAKE_PHOTO_CODE);
				} else if (s.equals(PhotoPickerPreference.TAG_SELECT_PHOTO)) {
					Intent intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
					startActivityForResult(intent, Main.SELECT_IMAGE_CODE);
				}
			} 

		}

		private void covertUnit(SharedPreferences sharedPreferences) {
			Resources res = getResources();
			String unitString = sharedPreferences.getString(
					getString(R.string.prefUnit), "Metric");
			boolean isMetric = (unitString.equals("Metric")) ? true : false;
			int height = sharedPreferences.getInt(
					getString(R.string.prefHeight), 200);

			// set user weight unit
			String fformat = "%.1f";
			Preference pref = findPreference(getString(R.string.prefWeightDisplay));
			NumberPickerPreference numPickPref = (NumberPickerPreference) pref;
			int v = sharedPreferences.getInt(getString(R.string.prefWeight),
					Integer.valueOf(getString(R.string.default_user_weight)));

			String waray[] = res.getStringArray(R.array.weight_unit);
			if (isMetric) {
				numPickPref.setSummary(v, false);
				numPickPref.setTitle(waray[0]);
				numPickPref.setDialogTitle(waray[0]);

			} else {
				numPickPref.setSummary((int) Utilities.KG2LBS(v), false);
				numPickPref.setTitle(waray[1]);
				numPickPref.setDialogTitle(waray[1]);
			}

			// set user height unit
			String key = getString(R.string.prefHeightDisplay);
			String defaultValue = getString(R.string.default_user_height);
			ListPreference ListPref = (ListPreference) findPreference(key);
			if (ListPref != null) {
				// int index = 0;

				// CharSequence[] entries = ListPref.getEntries();
				// CharSequence targetEntry = ListPref.getEntry();
				// if (targetEntry != null) {
				// for (CharSequence entry : entries) {
				// if (targetEntry.equals(entry)) {
				// break;
				// }
				// index++;
				// }
				// }
				String haray[] = res.getStringArray(R.array.height_unit);
				if (isMetric) {

					String value = sharedPreferences.getString(key,
							defaultValue);

					ListPref.setEntries(pref_user_height_metric_entries
							.toArray(new CharSequence[pref_user_height_metric_entries
									.size()]));

					ListPref.setEntryValues(pref_user_height_metric_entryvalues
							.toArray(new CharSequence[pref_user_height_metric_entryvalues
									.size()]));

					int index = 0;
					Utilities.getLog(TAG, "Height : " + height);
					for (CharSequence c : pref_user_height_metric_entryvalues) {
						Utilities.getLog(TAG,
								"pref_user_height_metric_entryvalues : " + c);
						if (height == Integer.valueOf((String) c)) {

							break;
						}
						index++;
					}
					ListPref.setValueIndex(index);
					if (index < pref_user_height_metric_entryvalues.size())
						ListPref.setSummary(pref_user_height_metric_entries
								.get(index));

					ListPref.setTitle(haray[0]);
					ListPref.setDialogTitle(haray[0]);

				} else {
					String oldEntry = ListPref.getValue();
					String value = sharedPreferences.getString(key,
							defaultValue);

					ListPref.setEntries(pref_user_height_imperial_entries
							.toArray(new CharSequence[pref_user_height_imperial_entries
									.size()]));

					ListPref.setEntryValues(pref_user_height_imperial_entryvalues
							.toArray(new CharSequence[pref_user_height_imperial_entryvalues
									.size()]));

					int index = 0;
					int closest = 0;
					int targetIndex = 0;
					Utilities.getLog(TAG, "Height : " + height);
					for (CharSequence c : pref_user_height_imperial_entryvalues) {
						if (Math.abs(closest - height) > Math.abs(Integer
								.valueOf((String) c) - height)) {
							closest = Integer.valueOf((String) c);
							targetIndex = index;
						}
						index++;
					}
					ListPref.setValueIndex(targetIndex);
					ListPref.setSummary(pref_user_height_imperial_entries
							.get(targetIndex));
					ListPref.setTitle(haray[1]);
					ListPref.setDialogTitle(haray[1]);

				}
			}
		}

		public static int closest1(int find, int... values) {
			int closest = values[0];
			for (int i : values)
				if (Math.abs(closest - find) > Math.abs(i - find))
					closest = i;
			return closest;
		}

		private void showDialog() {
			final String[] list = {
					(String) getResources().getText(R.string.take_picture),
					(String) getResources().getText(
							R.string.choose_from_library) };// ,

			AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

			DialogInterface.OnClickListener listItemOnClick = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int position) {
					Intent intent;
					switch (position) {
					case 0:
						Intent captureIntent = new Intent(
								MediaStore.ACTION_IMAGE_CAPTURE);
						// we will handle the returned data in onActivityResult
						startActivityForResult(captureIntent,
								Main.TAKE_PHOTO_CODE);
						break;
					case 1:
						intent = new Intent(
								Intent.ACTION_PICK,
								android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
						startActivityForResult(intent, Main.SELECT_IMAGE_CODE);
						break;

					}

				}

			};

			DialogInterface.OnClickListener cancelBtnOnClick = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int position) {

				}
			};

			alertDialog.setItems(list, listItemOnClick);
			alertDialog.setNeutralButton("Cancel", cancelBtnOnClick);
			alertDialog.show();
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode,
				Intent data) {
			// Utilities.getLog("resultCode", resultCode + "");
			Utilities.getLog(TAG, "resultCode " + resultCode);

			if (resultCode == RESULT_OK) {

				switch (requestCode) {
				case Main.TAKE_PHOTO_CODE:
					picUri = data.getData();
					// carry out the crop operation
					performCrop(picUri);
					break;
				case PIC_CROP:
					try {
						// // get the returned data
						Bundle extras = data.getExtras();
						// // get the cropped bitmap
						final Bitmap bmp = extras.getParcelable("data");

						OutputStream fOut = null;
						final File file = getTempFile(mContext);

						Utilities.getLog(TAG, file.toString());
						fOut = new FileOutputStream(file);
						bmp.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
						fOut.flush();
						fOut.close();

						MediaStore.Images.Media.insertImage(
								mContext.getContentResolver(),
								file.getAbsolutePath(), file.getName(),
								file.getName());

						this.getActivity().runOnUiThread(new Runnable() {
							public void run() {

								try {
									PhotoPickerPreference fric = (PhotoPickerPreference) findPreference(getString(R.string.pref_profile_pic));
									fric.setSummary(file.getPath());
									// ViewGroup v = (ViewGroup) fric.getView(
									// null, null);
									// ImageView profilePicImageView =
									// (ImageView) v
									// .findViewById(R.id.profile_picture_image_view);
									// //
									// profilePicImageView.setImageBitmap(bmp);
									// Drawable icon = new BitmapDrawable(
									// getResources(), bmp);
									// profilePicImageView.setImageDrawable(icon);
									//
								} catch (final Exception ex) {
									Utilities.getLog(TAG, ex.toString());
								}
							}
						});

						SharedPreferences prefs = PreferenceManager
								.getDefaultSharedPreferences(mContext);

						SharedPreferences.Editor editor = prefs.edit();
						editor.putString(getString(R.string.pref_profile_pic),
								file.getPath());
						// Commit the edits!
						editor.commit();

					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}

					break;

				case Main.SELECT_IMAGE_CODE:
					picUri = data.getData();

					Utilities.getLog(TAG, picUri.toString());

					performCrop(picUri);

					break;
				}
			}
			super.onActivityResult(requestCode, resultCode, data);
		}

		private void performCrop(Uri uri) {
			// TODO Auto-generated method stub
			try {
				// call the standard crop action intent (the user device may not
				// support it)
				Intent cropIntent = new Intent("com.android.camera.action.CROP");
				// indicate image type and Uri
				cropIntent.setDataAndType(uri, "image/*");
				// set crop properties
				cropIntent.putExtra("crop", "true");
				// indicate aspect of desired crop
				cropIntent.putExtra("aspectX", 1);
				cropIntent.putExtra("aspectY", 1);
				// indicate output X and Y
				cropIntent.putExtra("outputX", 256);
				cropIntent.putExtra("outputY", 256);
				// retrieve data on return
				cropIntent.putExtra("return-data", true);
				// start the activity - we handle returning in onActivityResult
				startActivityForResult(cropIntent, PIC_CROP);
			} catch (ActivityNotFoundException anfe) {
				// display an error message
				String errorMessage = "Whoops - your device doesn't support the crop action!";
				Utilities.getLog(TAG, errorMessage);
				// Toast toast = Toast.makeText(this, errorMessage,
				// Toast.LENGTH_SHORT);
				// toast.show();
			}
		}

		static public File getTempFile(Context mContext) {
			// TODO Auto-generated method stub
			final File path = new File(
					Environment.getExternalStorageDirectory(),
					mContext.getPackageName());
			if (!path.exists()) {
				path.mkdir();
			}
			Calendar c = Calendar.getInstance();
			int seconds = c.get(Calendar.SECOND);

			return new File(path, "image.png");
		}

		static public Bitmap getResizedBitmap(Bitmap bm, int newHeight,
				int newWidth) {
			int width = bm.getWidth();
			int height = bm.getHeight();
			float scaleWidth = ((float) newWidth) / width;
			float scaleHeight = ((float) newHeight) / height;
			// CREATE A MATRIX FOR THE MANIPULATION
			Matrix matrix = new Matrix();
			// RESIZE THE BIT MAP
			matrix.postScale(scaleWidth, scaleHeight);

			// RECREATE THE NEW BITMAP
			Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
					matrix, false);
			return resizedBitmap;
		}
	}

}
