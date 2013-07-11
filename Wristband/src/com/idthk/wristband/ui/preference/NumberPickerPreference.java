package com.idthk.wristband.ui.preference;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

public class NumberPickerPreference extends DialogPreference implements NumberPicker.OnValueChangeListener
    {
        private static final String NAMESPACE="http://schemas.android.com/apk/res/android";

        private NumberPicker mNumberPicker;
        private TextView mTvDialogMessage;
        private Context mCtx;

        private String mDialogMessage;
        private int mDefault;
        private int mMax;
        private int mValue = 0;

		private String TAG = "NumberPickerPreference";

        public NumberPickerPreference(Context ctx, AttributeSet attr) { 
            super(ctx, attr); 
            mCtx = ctx;

            //Get XML attributes
            mDialogMessage = attr.getAttributeValue(NAMESPACE,"dialogMessage");
            mDefault = attr.getAttributeIntValue(NAMESPACE,"defaultValue", 10000);
            mMax = attr.getAttributeIntValue(NAMESPACE,"max", 200);
            mValue=mDefault;
            
            if (shouldPersist())
            {
                persistInt(mValue);
                notifyChanged();
            }
            setSummary(mValue);
        }

        @Override 
        protected View onCreateDialogView() {
            //Create Views
            LinearLayout dialogLayout = new LinearLayout(mCtx);
            mTvDialogMessage = new TextView(mCtx);
            mNumberPicker = new NumberPicker(mCtx);

            //Set View attributes
            dialogLayout.setOrientation(LinearLayout.VERTICAL);
            if (mDialogMessage!=null)
                mTvDialogMessage.setText(mDialogMessage);
            dialogLayout.addView(mTvDialogMessage);
            mNumberPicker.setOnValueChangedListener(this);
            dialogLayout.addView(mNumberPicker, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            if (shouldPersist())
                mValue = getPersistedInt(mDefault);
            mNumberPicker.setMaxValue(mMax);
            mNumberPicker.setMinValue(1);
            mNumberPicker.setValue(mValue);
            setSummary(mValue);
            return dialogLayout;
        }
        @Override
		public void onDismiss(DialogInterface dialog)
        {
        	Log.v("mNumberPicker.getValue()","value "+ mNumberPicker.getValue());
        	
        	super.onDismiss(dialog);
        }
        @Override 
        protected void onBindDialogView(View v) {
            super.onBindDialogView(v);
            mNumberPicker.setMaxValue(mMax);
            mNumberPicker.setMinValue(1);
            mNumberPicker.setValue(mValue);     
            setSummary(mValue);
        }

        @Override
        protected void onSetInitialValue(boolean restore, Object defaultValue)  
        {
        	Log.v(TAG ,"onSetInitialValue ");
            super.onSetInitialValue(restore, defaultValue);
            if (restore) 
                mValue = shouldPersist() ? getPersistedInt(mDefault) : 2;
            else 
                mValue = (Integer)defaultValue;
            
            if (mNumberPicker!=null)
                mNumberPicker.setValue(mValue);
            
            
            if (shouldPersist())
            {
                persistInt(mValue);
                notifyChanged();
            }
            setSummary(mValue);


        }

        public void setSummary(int value) {
        	setSummary(Integer.toString(value));

        }

        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            setSummary(newVal);
            if (shouldPersist())
            {
                persistInt(newVal);
                notifyChanged();
            }
            callChangeListener(Integer.valueOf(newVal));    
        }
    }