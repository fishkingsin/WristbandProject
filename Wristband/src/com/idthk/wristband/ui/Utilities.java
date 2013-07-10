package com.idthk.wristband.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Locale;

import com.idthk.wristband.database.DatabaseHandler;
import com.idthk.wristband.database.Record;
import com.idthk.wristband.database.SleepPattern;
import com.idthk.wristband.database.SleepRecord;
import com.idthk.wristband.graphview.RoundBarGraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;

public class Utilities {
	public static AlertDialog alertDialog;
	public static final String DEFAULT_FONT = "handel.ttf";
	public static final boolean DEBUG_MODE = true;
	private static String sTag = "Utilities";
	private static SimpleDateFormat simpleDateForamt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static SimpleDateFormat simpleTimeForamt = new SimpleDateFormat("HH:mm");
	
	public static boolean showBalloon = true;

	public static final String VERSION_NUMBER = "2.0";

	private static Calendar _lastDate = Calendar.getInstance();

	/**
	 * @return the simpleDateForamt
	 */
	public static SimpleDateFormat getSimpleDateForamt() {
		return simpleDateForamt;
	}

	/**
	 * @param simpleDateForamt the simpleDateForamt to set
	 */
	public static void setSimpleDateForamt(SimpleDateFormat simpleDateForamt) {
		Utilities.simpleDateForamt = simpleDateForamt;
	}

	public static Calendar lastDate() {
		return _lastDate;
	}

	public static void setLastdate(Calendar cal) {
		_lastDate.setTimeInMillis(cal.getTimeInMillis());
	}

	private static Calendar _firstDate = Calendar.getInstance();

	public static Calendar firstDate() {
		return _firstDate;
	}

	public static void setFirstdate(Calendar cal) {
		_firstDate.setTimeInMillis(cal.getTimeInMillis());
	}

	private static Calendar _targetDate = Calendar.getInstance();

	public static Calendar targetDate() {
		return _targetDate;
	}

	public static void setTargetdate(Calendar cal) {
		_targetDate.setTimeInMillis(cal.getTimeInMillis());
	}

	public static void showAlert(String msg, Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.message)
				.setMessage(msg)
				.setCancelable(false)
				.setPositiveButton(
						context.getResources().getString(R.string.ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		if (alertDialog != null && alertDialog.isShowing()) {
			try {
				alertDialog.cancel();
			} catch (Exception e) {
				Utilities.getLog(sTag, e.toString());
			}
		}

		try {
			alertDialog = builder.create();
			alertDialog.show();
		} catch (Exception e) {
			Log.e("error", e.toString());
		}
	}

	public static void quitApp(Context context) {
		Intent intent = new Intent();
		intent.setAction(IntentMessager.ACTION_FILTER);
		intent.putExtra(IntentMessager.ACTION_ID,
				IntentMessager.ACTION_CLOSE_APP);
		context.sendBroadcast(intent);
	}

	public static void finishLoading(Context context) {
		Intent intent = new Intent();
		intent.setAction(IntentMessager.ACTION_FILTER);
		intent.putExtra(IntentMessager.ACTION_ID,
				IntentMessager.ACTION_FINISH_LOADING);
		context.sendBroadcast(intent);
	}

	public static void setDefaultFontStyle(Object object, int paddingLeft,
			int paddingTop, int paddingRight, int paddingBottom, Context context) {
		setFontStyle(object, DEFAULT_FONT, paddingLeft, paddingTop,
				paddingRight, paddingBottom, context);
	}

	public static void setFontStyle(Object object, String font,
			int paddingLeft, int paddingTop, int paddingRight,
			int paddingBottom, Context context) {
		Typeface typeface = Typeface.createFromAsset(context.getAssets(),
				"fonts/" + font);

		if (object.getClass().equals(TextView.class)) {
			((TextView) object).setTypeface(typeface);
			((TextView) object).setPadding(paddingLeft, paddingTop,
					paddingRight, paddingBottom);
		} else if (object.getClass().equals(EditText.class)) {
			((EditText) object).setTypeface(typeface);
			((EditText) object).setPadding(paddingLeft, paddingTop,
					paddingRight, paddingBottom);
		}
	}

	public static boolean macAddressFormatIsValid(String inputStr) {
		String patternStr = "[0-9a-f]{12}";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(inputStr);
		return matcher.matches();
	}

	public static boolean emailAddressIsValid(String inputStr) {
		String patternStr = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(inputStr);
		return matcher.matches();
	}

	public static void getLog(String sTag, String message) {
		if (DEBUG_MODE) {
			Log.d(sTag, message);
		}
	}

	// decodes image and scales it to reduce memory consumption
	public static Bitmap decodeFile(File f, Context mContext) {
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// The new size we want to scale to
			final int REQUIRED_SIZE = 300;

			// Find the correct scale value. It should be the power of 2.
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_SIZE
					&& o.outHeight / scale / 2 >= REQUIRED_SIZE)
				scale *= 2;

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;

			// ContentResolver cr = mContext.getContentResolver();
			float degree = 0;
			Bitmap bitmap = null;
			try {
				ExifInterface exif = new ExifInterface(f.getPath());
				String exifOrientation = exif
						.getAttribute(ExifInterface.TAG_ORIENTATION);
				bitmap = BitmapFactory.decodeStream(new FileInputStream(f),
						null, o2);

				if (bitmap != null) {
					degree = getDegree(exifOrientation);
					Log.d("degree:", String.valueOf(degree));
					// if (degree != 0)
					// bitmap = createRotatedBitmap(bitmap, degree);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return bitmap;
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	public static float getDegree(String exifOrientation) {
		float degree = 0;
		if (exifOrientation.equals("6"))
			degree = 90;
		else if (exifOrientation.equals("3"))
			degree = 180;
		else if (exifOrientation.equals("8"))
			degree = 270;
		return degree;
	}

	public static Bitmap createRotatedBitmap(Bitmap bm, float degree) {
		Bitmap bitmap = null;
		if (degree != 0) {
			Matrix matrix = new Matrix();
			matrix.preRotate(degree);
			bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
					bm.getHeight(), matrix, true);
		}

		return bitmap;
	}

	public static String replaceSpecialCharacter(String text) {
		String editedText = "";

		if (text != null) {
			editedText = text.replace("\"", "");
		}

		return editedText;
	}

	public static String getCurrentDate() {
		Calendar rightNow = Calendar.getInstance();

		int hour = rightNow.get(Calendar.HOUR_OF_DAY);
		int minute = rightNow.get(Calendar.MINUTE);

		String format = "%1$02d";
		String time = String.format(format, hour) + ":"
				+ String.format(format, minute);
		return time;

	}

	public static void publishGraph(Context context, View rootView,
			ViewGroup graph, String message) {

		graph.removeAllViews();
		String hStr[] = null;
		GraphViewSeriesStyle style = new GraphViewSeriesStyle();
		style.thickness = 5;
		style.color = 0xFF73CBfD;
		int PER_HOUR = 24;
		int PER_WEEK = 7;
		int PER_MONTH = 31;
		int PER_YEAR = 12;

		if (message.equals(ActivityStatisticTabFragment.TAB_DAY)) {
			LineGraphView mGraphView = new LineGraphView(context, "");
			DatabaseHandler db = new DatabaseHandler(context,
					Main.TABLE_CONTENT, null, 1);

			List<Record> records = db.getSumOfRecordsByDay(targetDate());
			GraphViewData[] data = new GraphViewData[PER_HOUR];
			hStr = new String[PER_HOUR];

			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

			String format = "%1$02d";
			if (records.size() > 0) {
				TextView tv = (TextView) rootView
						.findViewById(R.id.graph_view_title_indicator);
				SimpleDateFormat _format = new SimpleDateFormat("yyyy-MM-dd");
				tv.setText(_format.format(records.get(0).getCalendar()
						.getTime()));
			}
			int r = 0;
			for (int i = 0; i < PER_HOUR; i++) {
				hStr[i] = String.format(format, i) + ":00";
				int ret = 0;
				if (r < records.size()) {
					Record record = records.get(r);
					int hour = record.getCalendar().get(Calendar.HOUR_OF_DAY) ;
					Log.v("ActivityStatisticTabFragment.TAB_DAY"," hour int: "+hour);
					if (hour == i) {
						ret = record.getActivityTime();
						r++;
					}
				}
				data[i] = new GraphViewData(i, ret);
			}

			GraphViewSeries series = new GraphViewSeries("Day", style, data);

			mGraphView.setManualYAxisBounds(60, 0);
			mGraphView.setHorizontalLabels(hStr);
			mGraphView.setVerticalLabels(new String[] { "High", "Mid", "Low" });
			mGraphView.addSeries(series);

			graph.addView(mGraphView);

		} else if (message.equals(ActivityStatisticTabFragment.TAB_WEEK)) 
		{

			RoundBarGraphView mGraphView = new RoundBarGraphView(context, "");
			DatabaseHandler db = new DatabaseHandler(context,
					Main.TABLE_CONTENT, null, 1);

			Calendar sunday = Calendar.getInstance();
			int dayOfTheWeek = targetDate().get( Calendar.DAY_OF_WEEK );
			sunday.add( Calendar.DAY_OF_WEEK, Calendar.SUNDAY - dayOfTheWeek );
			
			Calendar saturday = Calendar.getInstance();
			dayOfTheWeek = targetDate().get( Calendar.DAY_OF_WEEK );
			saturday.add( Calendar.DAY_OF_WEEK, Calendar.SATURDAY - dayOfTheWeek );
			
			List<Record> records = db.getSumOfRecordsByRange(sunday,
					saturday);
			getLog(sTag, targetDate().toString());
			getLog(sTag, sunday.toString());

			GraphViewData[] data = new GraphViewData[PER_WEEK];

			hStr = new String[PER_MONTH];
			String rangeOfWeek = "";
			int r = 0;

			for (int i = 0; i < PER_WEEK; i++) {

				int ret = 0;
				if (r < records.size()) {
					// sunday .. saturday
					int weekday = records.get(r).getCalendar()
							.get(Calendar.DAY_OF_WEEK);
					Log.v("ActivityStatisticTabFragment.TAB_WEEK"," weekday int: "+weekday);
					if (weekday == Calendar.SUNDAY + i) {
						Record record = records.get(r);
						ret = record.getActivityTime();

						switch (weekday) {
						case Calendar.SUNDAY:
							hStr[i] = "SUNDAY";
							rangeOfWeek += record.getCalendar().getDisplayName(
									Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
									+ " "
									+ String.valueOf(record.getCalendar().get(
											Calendar.DAY_OF_MONTH)) + "-";
							break;
						case Calendar.MONDAY:
							hStr[i] = "MONDAY";
							break;
						case Calendar.TUESDAY:
							hStr[i] = "TUESDAY";
							break;
						case Calendar.WEDNESDAY:
							hStr[i] = "WEDNESDAY";
							break;
						case Calendar.THURSDAY:
							hStr[i] = "THURSDAY";
							break;
						case Calendar.FRIDAY:
							hStr[i] = "FRIDAY";
							break;
						case Calendar.SATURDAY:
							hStr[i] = "SATURDAY";
							rangeOfWeek += record.getCalendar().getDisplayName(
									Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
									+ " "
									+ String.valueOf(record.getCalendar().get(
											Calendar.DAY_OF_MONTH))
									+ " - "
									+ String.valueOf(record.getCalendar().get(
											Calendar.YEAR));
							break;

						}
						r++;
					}
				}
				data[i] = new GraphViewData(i, ret);
			}

			((TextView) rootView.findViewById(R.id.graph_view_title_indicator))
					.setText(rangeOfWeek);

			GraphViewSeries series = new GraphViewSeries("Week", style, data);
			mGraphView.setManualYAxisBounds(1440, 0);
			mGraphView.setVerticalLabels(new String[] { "High", "Mid", "Low" });
			mGraphView.setHorizontalLabels(hStr);
			mGraphView.addSeries(series);
			graph.addView(mGraphView);

		} else if (message.equals(ActivityStatisticTabFragment.TAB_MONTH)) {
			RoundBarGraphView mGraphView = new RoundBarGraphView(context, "");
			DatabaseHandler db = new DatabaseHandler(context,
					Main.TABLE_CONTENT, null, 1);
			List<Record> records = db.getSumOfRecordsByMonth(targetDate());
			PER_MONTH = targetDate().getActualMaximum(Calendar.DAY_OF_MONTH);
			GraphViewData[] data = new GraphViewData[PER_MONTH];

			hStr = new String[PER_MONTH];
			String rangeOfWeek = "";
			int r = 0;

			for (int i = 0; i < PER_MONTH; i++) {

				int ret = 0;
				if (r < records.size()) {

					int day = records.get(r).getCalendar()
							.get(Calendar.DAY_OF_MONTH);
					Log.v("ActivityStatisticTabFragment.TAB_MONH"," da int: "+day);
					if (day ==  i) {
						Record record = records.get(r);
						ret = record.getActivityTime();
						r++;
					}
				}
				hStr[i] = String.valueOf(i);
				data[i] = new GraphViewData(i, ret);
			}
			
			TextView tv = (TextView) rootView
					.findViewById(R.id.graph_view_title_indicator);

			tv.setText(records.get(0).getCalendar().getDisplayName(Calendar.MONTH,
					Calendar.LONG, Locale.getDefault()));

			GraphViewSeries series = new GraphViewSeries("Month", style, data);

			mGraphView.setManualYAxisBounds(1440, 0);
			mGraphView.setVerticalLabels(new String[] { "High", "Mid", "Low" });
			mGraphView.setHorizontalLabels(hStr);

			mGraphView.addSeries(series);
			graph.addView(mGraphView);

		} else if (message.equals(ActivityStatisticTabFragment.TAB_YEAR)) {
			RoundBarGraphView mGraphView = new RoundBarGraphView(context, "");

			DatabaseHandler db = new DatabaseHandler(context,
					Main.TABLE_CONTENT, null, 1);

			List<Record> records = db.getSumOfRecordsByYear(targetDate().get(
					Calendar.YEAR));
			GraphViewData[] data = new GraphViewData[PER_YEAR];

			hStr = new String[PER_YEAR];
			String rangeOfWeek = "";
			int r = 0;

			for (int i = 0; i < PER_YEAR; i++) {

				int ret = 0;
				if (r < records.size()) {

					int month = records.get(r).getCalendar()
							.get(Calendar.MONTH);
					Log.v("ActivityStatisticTabFragment.TAB_YEAR"," month int: "+month);
					if (month ==  i) {
						Record record = records.get(r);
						ret = record.getActivityTime();
						r++;
					}
				}
				hStr[i] = String.valueOf(i);
				data[i] = new GraphViewData(i, ret);
			}
//			GraphViewData[] data = new GraphViewData[records.size()];
//			int j = 0;
//			hStr = new String[records.size()];
//			for (Record cn : records) {
//				if (j == 0) {
//					TextView tv = (TextView) rootView
//							.findViewById(R.id.graph_view_title_indicator);
//
//					tv.setText(String.valueOf(cn.getCalendar().get(
//							Calendar.YEAR)));
//				}
//				data[j] = new GraphViewData(cn.getCalendar()
//						.get(Calendar.MONTH), cn.getActivityTime());
//				hStr[j] = cn.getCalendar().getDisplayName(Calendar.MONTH,
//						Calendar.SHORT, Locale.getDefault());
//				j++;
//			}
			GraphViewSeries series = new GraphViewSeries("Year", style, data);

			mGraphView.setManualYAxisBounds(44640, 0);
			mGraphView.setVerticalLabels(new String[] { "High", "Mid", "Low" });
			mGraphView.setHorizontalLabels(hStr);

			mGraphView.addSeries(series);
			graph.addView(mGraphView);
		} else if (message.equals(SleepStatisticTabFragment.TAB_WEEK)) {
			RoundBarGraphView mGraphView = new RoundBarGraphView(context, "");
			DatabaseHandler db = new DatabaseHandler(context,
					Main.TABLE_CONTENT, null, 1);
			
			Calendar sunday = Calendar.getInstance();
			int dayOfTheWeek = targetDate().get( Calendar.DAY_OF_WEEK );
			sunday.add( Calendar.DAY_OF_WEEK, Calendar.SUNDAY - dayOfTheWeek );
			
			Calendar saturday = Calendar.getInstance();
			dayOfTheWeek = targetDate().get( Calendar.DAY_OF_WEEK );
			saturday.add( Calendar.DAY_OF_WEEK, Calendar.SATURDAY - dayOfTheWeek );
			
		
			List<SleepRecord> records = db.getSumOfSleepTimeByRange(sunday,saturday);
			GraphViewData[] data = new GraphViewData[PER_WEEK];

			hStr = new String[PER_MONTH];
			String rangeOfWeek = "";
			int r = 0;

			for (int i = 0; i < PER_WEEK; i++) {

				int ret = 0;
				if (r < records.size()) {
					// sunday ... saturday
					SleepRecord sleepRecord = records.get(r);
					int weekday = sleepRecord.getGoToBedTime()
							.get(Calendar.DAY_OF_WEEK);
					Log.v("SleepStatisticTabFragment.TAB_WEEK"," weekday int: "+weekday);
					if (weekday == Calendar.SUNDAY + i) {
						
						ret = sleepRecord.getActualSleepTime();

						switch (weekday) {
						case Calendar.SUNDAY:
							hStr[i] = "SUNDAY";
							rangeOfWeek += sleepRecord.getGoToBedTime().getDisplayName(
									Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
									+ " "
									+ String.valueOf(sleepRecord.getGoToBedTime().get(
											Calendar.DAY_OF_MONTH)) + "-";
							break;
						case Calendar.MONDAY:
							hStr[i] = "MONDAY";
							break;
						case Calendar.TUESDAY:
							hStr[i] = "TUESDAY";
							break;
						case Calendar.WEDNESDAY:
							hStr[i] = "WEDNESDAY";
							break;
						case Calendar.THURSDAY:
							hStr[i] = "THURSDAY";
							break;
						case Calendar.FRIDAY:
							hStr[i] = "FRIDAY";
							break;
						case Calendar.SATURDAY:
							hStr[i] = "SATURDAY";
							rangeOfWeek += sleepRecord.getGoToBedTime().getDisplayName(
									Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
									+ " "
									+ String.valueOf(sleepRecord.getGoToBedTime().get(
											Calendar.DAY_OF_MONTH))
									+ " - "
									+ String.valueOf(sleepRecord.getGoToBedTime().get(
											Calendar.YEAR));
							break;

						}
						r++;
					}
				}
				data[i] = new GraphViewData(i, ret);
			}

			((TextView) rootView.findViewById(R.id.graph_view_title_indicator))
					.setText(rangeOfWeek);

			GraphViewSeries series = new GraphViewSeries("Week", style, data);
			mGraphView.setManualYAxisBounds(1440, 0);
			mGraphView.setHorizontalLabels(hStr);
			mGraphView.setVerticalLabels(new String[] { "High", "Mid", "Low" });
			mGraphView.addSeries(series);
			graph.addView(mGraphView);
		} else if (message.equals(SleepStatisticTabFragment.TAB_MONTH)) {
			RoundBarGraphView mGraphView = new RoundBarGraphView(context, "");
			DatabaseHandler db = new DatabaseHandler(context,
					Main.TABLE_CONTENT, null, 1);
			List<SleepRecord> sleepRecords = db.getSumOfSleepTimeByMonth(targetDate().getTime());
			
			GraphViewData[] data = new GraphViewData[PER_MONTH];

			hStr = new String[PER_MONTH];
			
			int r = 0;
			int numEntry = sleepRecords.size();
			for (int i = 0; i < PER_MONTH; i++) {

				int ret = 0;
				if (r < numEntry) {
					SleepRecord sleepRecord = sleepRecords.get(r);
					int day = sleepRecord.getGoToBedTime()
							.get(Calendar.DAY_OF_MONTH);
					if (day ==  i) {
						
						ret = sleepRecord.getActualSleepTime();
						r++;
					}
				}
				hStr[i] = String.valueOf(i);
				data[i] = new GraphViewData(i, ret);
			}
			
//			GraphViewData[] data = new GraphViewData[records.size()];
//			int j = 0;
//			hStr = new String[records.size()];
//			for (Record cn : records) {
//				if (j == 0) {
//					TextView tv = (TextView) rootView
//							.findViewById(R.id.graph_view_title_indicator);
//
//					tv.setText(cn.getCalendar().getDisplayName(Calendar.MONTH,
//							Calendar.LONG, Locale.getDefault()));
//				}
//				data[j] = new GraphViewData(cn.getCalendar()
//						.get(Calendar.MONTH), cn.getActivityTime());
//				hStr[j] = String.valueOf(cn.getCalendar().get(
//						Calendar.DAY_OF_MONTH));
//				j++;
//			}
			GraphViewSeries series = new GraphViewSeries("Month", style, data);

			mGraphView.setManualYAxisBounds(1440, 0);

			mGraphView.setHorizontalLabels(hStr);
			mGraphView.setVerticalLabels(new String[] { "High", "Mid", "Low" });
			mGraphView.addSeries(series);
			graph.addView(mGraphView);
		} else if (message.equals(SleepStatisticTabFragment.TAB_YEAR)) {
			RoundBarGraphView mGraphView = new RoundBarGraphView(context, "");

			DatabaseHandler db = new DatabaseHandler(context,
					Main.TABLE_CONTENT, null, 1);

			List<SleepRecord> sleepRecords = db.getSumOfSleepTimeByYear(targetDate().get(
					Calendar.YEAR));
			
			GraphViewData[] data = new GraphViewData[PER_YEAR];

			hStr = new String[PER_YEAR];
			String rangeOfWeek = "";
			int r = 0;
			int numEntry = sleepRecords.size();
			for (int i = 0; i < PER_YEAR; i++) {

				int ret = 0;
				if (r < numEntry) {
					SleepRecord sleepRecord = sleepRecords.get(r);
					int day = sleepRecord.getGoToBedTime()
							.get(Calendar.MONTH);
					if (day ==  i) {
						
						ret = sleepRecord.getActualSleepTime();
						r++;
					}
				}
				hStr[i] = String.valueOf(i);
				data[i] = new GraphViewData(i, ret);
			}
			
//			GraphViewData[] data = new GraphViewData[records.size()];
//			int j = 0;
//			hStr = new String[records.size()];
//			for (Record cn : records) {
//				if (j == 0) {
//					TextView tv = (TextView) rootView
//							.findViewById(R.id.graph_view_title_indicator);
//
//					tv.setText(String.valueOf(cn.getCalendar().get(
//							Calendar.YEAR)));
//				}
//				data[j] = new GraphViewData(cn.getCalendar()
//						.get(Calendar.MONTH), cn.getActivityTime());
//				hStr[j] = cn.getCalendar().getDisplayName(Calendar.MONTH,
//						Calendar.SHORT, Locale.getDefault());
//				j++;
//			}
			GraphViewSeries series = new GraphViewSeries("Year", style, data);

			mGraphView.setManualYAxisBounds(44640, 0);

			mGraphView.setHorizontalLabels(hStr);
			mGraphView.setVerticalLabels(new String[] { "High", "Mid", "Low" });
			mGraphView.addSeries(series);
			graph.addView(mGraphView);
		}

	}

	public static void populateSleepPatternGraph(Context context,
			View rootView, ViewGroup graph) {
		graph.removeAllViews();
		GraphViewSeriesStyle style = new GraphViewSeriesStyle();
		style.thickness = 5;
		style.color = 0xFF73CBfD;

		LineGraphView mGraphView = new LineGraphView(context, "");
		DatabaseHandler db = new DatabaseHandler(context, Main.TABLE_CONTENT,
				null, 1);

		SimpleDateFormat df = new SimpleDateFormat("HH:mm");

		SleepRecord sleeprecord = db.getLastSleepRecord();
		TextView sleepTimeHourTV = ((TextView) rootView
				.findViewById(R.id.sleep_time_hour_textview));
		if (sleepTimeHourTV != null)
			sleepTimeHourTV.setText(df.format(sleeprecord.getGoToBedTime()
					.getTime()));

		TextView sleepEndTV = ((TextView) rootView
				.findViewById(R.id.sleep_end_textfield));
		if (sleepEndTV != null)
			sleepEndTV.setText(df.format(sleeprecord.getActualWakeupTime()
					.getTime()));

		TextView sleepDurationTV = ((TextView) rootView
				.findViewById(R.id.sleep_duration_textfield));
		if (sleepDurationTV != null)
			sleepDurationTV.setText(String.valueOf(sleeprecord.getInBedTime()));

		if (sleeprecord != null) {
			List<SleepPattern> patterns = sleeprecord.getPatterns();
			List<GraphViewData> data = new ArrayList<GraphViewData>();

			int j = 0;
			int xValue = 0;
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			for (SleepPattern pattern : patterns) {
				for (int i = 0; i < pattern.getDuration(); i++) {
					int a = 0;
					switch (pattern.getAmplitude()) {
					case 22:
						a = 1;
						break;
					case 44:
						a = 2;
						break;
					case 64:

					case 66:
						a = 3;
						break;
					}
					data.add(new GraphViewData(xValue, a));
					xValue++;
				}

			}

			GraphViewData[] a = data.toArray(new GraphViewData[data.size()]);
			GraphViewSeries series = new GraphViewSeries("Hour", style, a);

			mGraphView.setManualYAxisBounds(3, 0);
			mGraphView.addSeries(series);
			// stuff that updates ui
			graph.addView(mGraphView);
		}
	}

	public static int prevEntryDate(String displaytype) {
		int ret = 0;
		int type = 0;
		if (displaytype.equals(SleepStatisticTabFragment.TAB_WEEK)) {
			targetDate().add(Calendar.WEEK_OF_YEAR, -1);
			type = Calendar.WEEK_OF_YEAR;
		} else if (displaytype.equals(SleepStatisticTabFragment.TAB_MONTH)) {
			targetDate().add(Calendar.MONTH, -1);
			type = Calendar.MONTH;
		} else if (displaytype.equals(SleepStatisticTabFragment.TAB_YEAR)) {
			targetDate().add(Calendar.YEAR, -1);
			type = Calendar.YEAR;
		} else if (displaytype.equals(ActivityStatisticTabFragment.TAB_DAY)) {
			targetDate().add(Calendar.DAY_OF_YEAR, -1);
			type = Calendar.DAY_OF_YEAR;
		} else if (displaytype.equals(ActivityStatisticTabFragment.TAB_WEEK)) {
			targetDate().add(Calendar.WEEK_OF_YEAR, -1);
			type = Calendar.WEEK_OF_YEAR;
		} else if (displaytype.equals(ActivityStatisticTabFragment.TAB_MONTH)) {
			targetDate().add(Calendar.MONTH, -1);
			type = Calendar.MONTH;
		} else if (displaytype.equals(ActivityStatisticTabFragment.TAB_YEAR)) {
			targetDate().add(Calendar.YEAR, -1);
			type = Calendar.YEAR;
		}
		if (targetDate().get(type) == firstDate().get(type)
				|| Utilities.targetDate().compareTo(Utilities.firstDate()) == -1) {
			Utilities.targetDate().setTime(firstDate().getTime());

		}
		ret = Utilities.targetDate().compareTo(Utilities.firstDate());

		return ret;
	}

	public static int nextEntryDate(String displaytype) {
		int ret = 0;
		int type = 0;
		if (displaytype.equals(SleepStatisticTabFragment.TAB_WEEK)) {
			targetDate().add(Calendar.WEEK_OF_YEAR, 1);
			type = Calendar.WEEK_OF_YEAR;
		} else if (displaytype.equals(SleepStatisticTabFragment.TAB_MONTH)) {
			targetDate().add(Calendar.MONTH, 1);
			type = Calendar.MONTH;
		} else if (displaytype.equals(SleepStatisticTabFragment.TAB_YEAR)) {
			targetDate().add(Calendar.YEAR, 1);
			type = Calendar.YEAR;

		} else if (displaytype.equals(ActivityStatisticTabFragment.TAB_DAY)) {
			targetDate().add(Calendar.DAY_OF_YEAR, 1);
			type = Calendar.DAY_OF_YEAR;
		} else if (displaytype.equals(ActivityStatisticTabFragment.TAB_WEEK)) {
			targetDate().add(Calendar.WEEK_OF_YEAR, 1);
			type = Calendar.WEEK_OF_YEAR;
		} else if (displaytype.equals(ActivityStatisticTabFragment.TAB_MONTH)) {
			targetDate().add(Calendar.MONTH, 1);
			type = Calendar.MONTH;
		} else if (displaytype.equals(ActivityStatisticTabFragment.TAB_YEAR)) {
			targetDate().add(Calendar.YEAR, 1);
			type = Calendar.YEAR;

		}
		if (targetDate().get(type) == lastDate().get(type)
				|| Utilities.targetDate().compareTo(Utilities.lastDate()) == 1) {
			Utilities.targetDate().setTime(lastDate().getTime());

		}
		ret = Utilities.targetDate().compareTo(Utilities.lastDate());

		return ret;
	}

}
