package com.idthk.wristband.ui.preference;

import java.util.Calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

public class TimePreference extends DialogPreference {
    private int lastHour=0;
    private int lastMinute=0;
    private TimePicker picker=null;

    public static int getHour(String time) {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[0]));
    }

    public static int getMinute(String time) {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[1]));
    }

    public static String getAmPm(String time) {
    	Calendar datetime = Calendar.getInstance();
        datetime.set(Calendar.HOUR_OF_DAY, getHour(time));
        datetime.set(Calendar.MINUTE, getMinute(time));
        String am_pm = "";

        if (datetime.get(Calendar.AM_PM) == Calendar.AM)
            am_pm = "AM";
        else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
            am_pm = "PM";
        return am_pm;
    }

    public TimePreference(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);

        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");
    }

    @Override
    protected View onCreateDialogView() {
        picker=new TimePicker(getContext());

        
        return(picker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);

        picker.setCurrentHour(lastHour);
        picker.setCurrentMinute(lastMinute);
       
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            lastHour=picker.getCurrentHour();
            lastMinute=picker.getCurrentMinute();
            String format = "%1$02d";
            String time=String.format(format,lastHour)+":"+String.format(format,lastMinute);
            
            setSummary(lastHour,lastMinute);
            if (callChangeListener(time)) {
                persistString(time);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return(a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time=null;

        if (restoreValue) {
            if (defaultValue==null) {
                time=getPersistedString("00:00");
            }
            else {
                time=getPersistedString(defaultValue.toString());
            }
        }
        else {
            time=defaultValue.toString();
        }

        lastHour=getHour(time);
        lastMinute=getMinute(time);
        if (callChangeListener(time)) {
            persistString(time);
        }
        setSummary(lastHour, lastMinute);
    }
    
    public void setSummary(int lastHour, int lastMinute)
    {
        
//        String format = "%1$02d";
//        String summaryTime=String.format(format,lastHour)+":"+String.format(format,lastMinute);
    	
    	Calendar datetime = Calendar.getInstance();
        datetime.set(Calendar.HOUR_OF_DAY, lastHour);
        datetime.set(Calendar.MINUTE, lastMinute);
        String am_pm = "";

        if (datetime.get(Calendar.AM_PM) == Calendar.AM)
            am_pm = "AM";
        else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
            am_pm = "PM";
      String format = "%1$02d";
//      String summaryTime=String.format(format,lastHour)+":"+String.format(format,lastMinute);
        String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ?"12":String.format(format, datetime.get(Calendar.HOUR) );
        

        setSummary(strHrsToShow+":"+String.format(format,datetime.get(Calendar.MINUTE))+" "+am_pm);
    	
//        setSummary(summaryTime);
        
    }
    @Override
    public void setSummary (CharSequence summary)
    {
    	
    	super.setSummary(summary);
    }
}