package com.idthk.wristband.ui.preference;

import java.io.File;

import com.idthk.wristband.ui.R;
import com.idthk.wristband.ui.Utilities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

public class PhotoPickerPreference extends DialogPreference implements
		SharedPreferences.OnSharedPreferenceChangeListener, OnClickListener {
	private static final String NAMESPACE = "http://schemas.android.com/apk/res/android";
	private static final String TAG = "PhotoPickerPreference";
	public static final String TAG_CAPTURE_PHOTO = "capture_photo";
	public static final String TAG_SELECT_PHOTO = "select_photo";
	private Context mCtx;
	private Button capturePhotoButton;
	private Button selectPhotoButton;

	public PhotoPickerPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		mCtx = context;
		// TODO Auto-generated constructor stub
		

	}


	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(this.getKey())) {
			Utilities.getLog(TAG, key + " " + "onSharedPreferenceChanged");
		}
	}

	@Override
	protected View onCreateDialogView() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getContext());
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
		LinearLayout dialogLayout = new LinearLayout(mCtx);
		capturePhotoButton = new Button(mCtx);

		capturePhotoButton.setText((String) mCtx.getResources().getText(
				R.string.take_picture));
		selectPhotoButton = new Button(mCtx);
		selectPhotoButton.setText((String) mCtx.getResources().getText(
				R.string.choose_from_library));

		capturePhotoButton.setOnClickListener(this);
		selectPhotoButton.setOnClickListener(this);

		// Set View attributes
		dialogLayout.setOrientation(LinearLayout.VERTICAL);

		dialogLayout.addView(capturePhotoButton);
		dialogLayout.addView(selectPhotoButton);

		return dialogLayout;

	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		Log.v("MyDialogPreference", dialog.toString());
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getContext());
		sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
		super.onDismiss(dialog);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {

		// if(positiveResult) {
		// persistBoolean(!getPersistedBoolean(true));
		// }
		// Log.v("MyDialogPreference","positiveResult :"+positiveResult);
		// notifyChanged();
		// callChangeListener(positiveResult);

	}
	public void setSummary(String imagePath) {
		
		Bitmap myBitmap = Utilities.decodeFile(new File(imagePath),
				mCtx );
		myBitmap =  Utilities.getRoundedCornerBitmap(myBitmap);
		Drawable icon = new BitmapDrawable(
				mCtx.getResources(), myBitmap);
		this.setIcon(icon);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.equals(capturePhotoButton)) {

			callChangeListener(TAG_CAPTURE_PHOTO);

			if (shouldPersist()) {
				persistString((String) TAG_CAPTURE_PHOTO);
				notifyChanged();
			}

		} else if (v.equals(selectPhotoButton)) {
			callChangeListener(TAG_SELECT_PHOTO);

			if (shouldPersist()) {
				persistString((String) TAG_SELECT_PHOTO);
				notifyChanged();
			}
		}

	}

}
