package com.idthk.wristband.ui.preference;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class MyDialogPreference extends DialogPreference {
	private static final String NAMESPACE = "http://schemas.android.com/apk/res/android";

	public MyDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

	}

	@Override
	protected View onCreateDialogView() {
		
		return super.onCreateDialogView();

	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		Log.v("MyDialogPreference", dialog.toString());

		super.onDismiss(dialog);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {

		persistBoolean(positiveResult);
		Log.v("MyDialogPreference","positiveResult :"+positiveResult);
		notifyChanged();
		callChangeListener(positiveResult); 
		super.onDialogClosed(positiveResult);
	}

}
