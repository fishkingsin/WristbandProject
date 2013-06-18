package com.idthk.wristband.ui.preference;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;

public class MyDialogPreference extends DialogPreference {
	private static final String NAMESPACE="http://schemas.android.com/apk/res/android";
	 public MyDialogPreference(Context context, AttributeSet attrs) {
	        super(context, attrs);
	        // TODO Auto-generated constructor stub
	        
//	        String dislogMessage = attrs.getAttributeValue(NAMESPACE,"dialogMessage"); 
//	        String dialogTitleString = attrs.getAttributeValue(NAMESPACE,"dialogTitle"); 
//	        String negativetiveButtonString = attrs.getAttributeValue(NAMESPACE,"negativeButtonText"); 
//      		String positiveButtonString = attrs.getAttributeValue(NAMESPACE,"positiveButtonText"); 
	    }
	 @Override 
     protected View onCreateDialogView() {
		return null;
	 
	 }
	 @Override
	    protected void onDialogClosed(boolean positiveResult) {
	        super.onDialogClosed(positiveResult);
	        persistBoolean(positiveResult);
	    }

}
