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
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.idthk.wristband.database.DatabaseHandler;
import com.idthk.wristband.database.Record;
import com.idthk.wristband.database.SleepPattern;
import com.idthk.wristband.database.SleepRecord;
import com.idthk.wristband.graphview.GaplessBarGraphView;
import com.idthk.wristband.graphview.StrokelessBarGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.LineGraphView;

public class Utilities {
	public static AlertDialog alertDialog;
	public static final String DEFAULT_FONT = "handel.ttf";
	public static final boolean DEBUG_MODE = true;
	private static String sTag = "Utilities";
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");
	private static SimpleDateFormat dateFormatMonthAndDay = new SimpleDateFormat(
			"MM-dd");

	private static SimpleDateFormat dateFormatYear = new SimpleDateFormat(
			"yyyy-MM-dd");

	private static SimpleDateFormat simpleTimeFormat = new SimpleDateFormat(
			"HH:mm");

	public static boolean showBalloon = true;

	public static final String VERSION_NUMBER = "2.0";

	private static Calendar _lastDate = Calendar.getInstance();

	/**
	 * @return the simpleDateForamt
	 */
	public static SimpleDateFormat getSimpleDateForamt() {
		return simpleDateFormat;
	}

	/**
	 * @param simpleDateForamt
	 *            the simpleDateForamt to set
	 */
	public static void setSimpleDateForamt(SimpleDateFormat simpleDateForamt) {
		Utilities.simpleDateFormat = simpleDateForamt;
	}

	/**
	 * @return the simpleTimeFormat
	 */
	public static SimpleDateFormat getSimpleTimeFormat() {
		return simpleTimeFormat;
	}

	/**
	 * @param simpleTimeFormat the simpleTimeFormat to set
	 */
	public static void setSimpleTimeFormat(SimpleDateFormat simpleTimeFormat) {
		Utilities.simpleTimeFormat = simpleTimeFormat;
	}

	/**
	 * @return the dateFormatYear
	 */
	public static SimpleDateFormat getDateFormatYear() {
		return dateFormatYear;
	}

	/**
	 * @param dateFormatYear
	 *            the dateFormatYear to set
	 */
	public static void setDateFormatYear(SimpleDateFormat dateFormatYear) {
		Utilities.dateFormatYear = dateFormatYear;
	}

	public static Calendar lastDate() {
		_lastDate.set(Calendar.HOUR_OF_DAY, 0);
		_lastDate.set(Calendar.MINUTE, 0);
		_lastDate.set(Calendar.SECOND, 0);
		_lastDate.set(Calendar.MILLISECOND, 0);
		return _lastDate;
	}

//	public static void setLastdate(Calendar cal) {
//		_lastDate.setTimeInMillis(cal.getTimeInMillis());
//		_lastDate.set(Calendar.HOUR_OF_DAY, 0);
//		_lastDate.set(Calendar.MINUTE, 0);
//		_lastDate.set(Calendar.SECOND, 0);
//		_lastDate.set(Calendar.MILLISECOND, 0);
//	}

	private static Calendar _firstDate = Calendar.getInstance();

	public static Calendar firstDate() {
		return _firstDate;
	}

	public static void setFirstdate(Calendar cal) {
		_firstDate.setTimeInMillis(cal.getTimeInMillis());
		_firstDate.set(Calendar.HOUR_OF_DAY, 0);
		_firstDate.set(Calendar.MINUTE, 0);
		_firstDate.set(Calendar.SECOND, 0);
		_firstDate.set(Calendar.MILLISECOND, 0);
	}

	private static Calendar _targetDate = Calendar.getInstance();

	public static Calendar targetDate() {
		_targetDate.set(Calendar.HOUR_OF_DAY, 0);
		_targetDate.set(Calendar.MINUTE, 0);
		_targetDate.set(Calendar.SECOND, 0);
		_targetDate.set(Calendar.MILLISECOND, 0);
		return _targetDate;
	}

	public static void setTargetdate(Calendar cal) {
		_targetDate.setTimeInMillis(cal.getTimeInMillis());
		_targetDate.set(Calendar.HOUR_OF_DAY, 0);
		_targetDate.set(Calendar.MINUTE, 0);
		_targetDate.set(Calendar.SECOND, 0);
		_targetDate.set(Calendar.MILLISECOND, 0);
	}
	public static void setTargetDate(Date date) {
		_targetDate.setTime(date);
		_targetDate.set(Calendar.HOUR_OF_DAY, 0);
		_targetDate.set(Calendar.MINUTE, 0);
		_targetDate.set(Calendar.SECOND, 0);
		_targetDate.set(Calendar.MILLISECOND, 0);
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
		getLog(sTag, " publishGraph " + message);
		DatabaseHandler db = new DatabaseHandler(context,
				Main.TABLE_CONTENT, null, 1);
		graph.removeAllViews();
		String hStr[] = null;
		GraphViewSeriesStyle style = new GraphViewSeriesStyle();
		style.thickness = 5;
		style.color = 0xFF73CBfD;
		int PER_HOUR = 24;
		int PER_WEEK = 7;
		int PER_MONTH = 31;
		int PER_YEAR = 12;
		
		if (message.equals(TabFragmentActivityStatistic.TAB_DAY)) {
			LineGraphView mGraphView = new LineGraphView(context, "");
			

			List<Record> records = db.getSumOfRecordsByDay(targetDate());
			GraphViewData[] data = new GraphViewData[PER_HOUR];
			hStr = new String[PER_HOUR];
			String format = "%1$02d";

			int r = 0;
			for (int i = 0; i < PER_HOUR; i++) {
				if (i == 6 || i == 12 || i == 18) {
					hStr[i] = String.format(format, i) + ":00";
				} else {
					hStr[i] = "|";
				}
				int ret = 0;
				if (r < records.size()) {
					Record record = records.get(r);
					int hour = record.getCalendar().get(Calendar.HOUR_OF_DAY);

					if (hour == i) {
						// Utilities.getLog("ActivityStatisticTabFragment.TAB_DAY",
						// " hour int: "
						// + hour);
						ret = record.getActivityTime();
						r++;

					}

				}
				data[i] = new GraphViewData(i, ret);
			}
			String dayString = String.valueOf(targetDate().get(
					Calendar.DAY_OF_MONTH))
					+ " "
					+ targetDate().getDisplayName(Calendar.MONTH,
							Calendar.LONG, Locale.getDefault())
					+ " "
					+ String.valueOf(targetDate().get(Calendar.YEAR));
			((TextView) rootView.findViewById(R.id.graph_view_title_indicator))
					.setText(dayString);
			GraphViewSeries series = new GraphViewSeries("Day", style, data);

			mGraphView.setManualYAxisBounds(60, 0);
			mGraphView.setHorizontalLabels(hStr);
			mGraphView.setVerticalLabels(new String[] { "60", "55", "50", "45",
					"40", "35", "30", "25", "20", "15", "10", "5", "0" });
			mGraphView.addSeries(series);

			graph.addView(mGraphView);

		} else if (message.equals(TabFragmentActivityStatistic.TAB_WEEK)) {

			GaplessBarGraphView mGraphView = new GaplessBarGraphView(context, "");


			Calendar sunday = Calendar.getInstance();
			sunday.setTime(targetDate().getTime());
			int dayOfTheWeek = targetDate().get(Calendar.DAY_OF_WEEK);
			sunday.add(Calendar.DAY_OF_WEEK, Calendar.SUNDAY - dayOfTheWeek);

			Calendar saturday = Calendar.getInstance();
			saturday.setTime(targetDate().getTime());
			dayOfTheWeek = targetDate().get(Calendar.DAY_OF_WEEK);
			saturday.add(Calendar.DAY_OF_WEEK, Calendar.SATURDAY - dayOfTheWeek);

			List<Record> records = db.getSumOfRecordsByRange(sunday, saturday);
			// getLog(sTag, targetDate().toString());
			// getLog(sTag, sunday.toString());

			GraphViewData[] data = new GraphViewData[PER_WEEK];

			hStr = new String[PER_WEEK];
			String rangeOfWeek = "";
			int r = 0;

			for (int i = 0; i < PER_WEEK; i++) {

				int ret = 0;
				if (r < records.size()) {
					// sunday .. saturday
					int weekday = records.get(r).getCalendar()
							.get(Calendar.DAY_OF_WEEK);

					if (weekday == Calendar.SUNDAY + i) {
						// Utilities.getLog("ActivityStatisticTabFragment.TAB_WEEK",
						// " weekday int: " + weekday);
						Record record = records.get(r);
						ret = record.getActivityTime();

						r++;
					}
				}
				hStr[i] = getWeekDay(i + Calendar.SUNDAY);
				data[i] = new GraphViewData(i, ret);
			}
			rangeOfWeek = getWeekRangeSring(sunday, saturday);
			((TextView) rootView.findViewById(R.id.graph_view_title_indicator))
					.setText(rangeOfWeek);

			GraphViewSeries series = new GraphViewSeries("Week", style, data);
			mGraphView.setManualYAxisBounds(1440, 0);
			mGraphView.setVerticalLabels(new String[] { "High", "Mid", "Low" });
			mGraphView.setHorizontalLabels(hStr);
			mGraphView.addSeries(series);
			graph.addView(mGraphView);

		} else if (message.equals(TabFragmentActivityStatistic.TAB_MONTH)) {
			GaplessBarGraphView mGraphView = new GaplessBarGraphView(context, "");

			List<Record> records = db.getSumOfRecordsByMonth(targetDate());
			PER_MONTH = targetDate().getActualMaximum(Calendar.DAY_OF_MONTH);
			GraphViewData[] data = new GraphViewData[PER_MONTH];

			hStr = new String[PER_MONTH];

			int r = 0;

			for (int i = 0; i < PER_MONTH; i++) {

				int ret = 0;
				if (r < records.size()) {
					Record record = records.get(r);
					int day = record.getCalendar().get(Calendar.DAY_OF_MONTH);

					if (day == i) {
						// Utilities.getLog("ActivityStatisticTabFragment.TAB_MONH",
						// " da int: "
						// + day);
						ret = record.getActivityTime();
						r++;
					}
					if (r == 0) {
						TextView tv = (TextView) rootView
								.findViewById(R.id.graph_view_title_indicator);

						tv.setText(record.getCalendar().getDisplayName(
								Calendar.MONTH, Calendar.LONG,
								Locale.getDefault()));
					}
				}
				if (i == 4 || i == 15 || i == 25) {
					hStr[i] = String.valueOf(i + 1);
				} else {
					hStr[i] = "|";
				}
				data[i] = new GraphViewData(i, ret);
			}
			((TextView) rootView.findViewById(R.id.graph_view_title_indicator))
					.setText(targetDate().getDisplayName(Calendar.MONTH,
							Calendar.LONG, Locale.getDefault())
							+ " "
							+ String.valueOf(targetDate().get(Calendar.YEAR)));
			GraphViewSeries series = new GraphViewSeries("Month", style, data);

			mGraphView.setManualYAxisBounds(1440, 0);
			mGraphView.setVerticalLabels(new String[] { "High", "Mid", "Low" });
			mGraphView.setHorizontalLabels(hStr);

			mGraphView.addSeries(series);
			graph.addView(mGraphView);

		} else if (message.equals(TabFragmentActivityStatistic.TAB_YEAR)) {
			GaplessBarGraphView mGraphView = new GaplessBarGraphView(context, "");

			List<Record> records = db.getSumOfRecordsByYear(targetDate().get(
					Calendar.YEAR));
			GraphViewData[] data = new GraphViewData[PER_YEAR];

			hStr = new String[PER_YEAR];

			int r = 0;

			for (int i = 0; i < PER_YEAR; i++) {

				int ret = 0;
				if (r < records.size()) {

					int month = records.get(r).getCalendar()
							.get(Calendar.MONTH);

					if (month == i) {
						// Utilities.getLog("ActivityStatisticTabFragment.TAB_YEAR",
						// " month int: " + month);
						Record record = records.get(r);
						ret = record.getActivityTime();
						r++;
					}
				}
				if (i == 2 || i == 5 || i == 8) {
					hStr[i] = getMonth(i);
				} else {
					hStr[i] = "|";
				}
				data[i] = new GraphViewData(i, ret);
			}
			TextView tv = (TextView) rootView
					.findViewById(R.id.graph_view_title_indicator);

			tv.setText(String.valueOf(targetDate().get(Calendar.YEAR)));

			GraphViewSeries series = new GraphViewSeries("Year", style, data);

			mGraphView.setManualYAxisBounds(44640, 0);
			mGraphView.setVerticalLabels(new String[] { "High", "Mid", "Low" });
			mGraphView.setHorizontalLabels(hStr);

			mGraphView.addSeries(series);
			graph.addView(mGraphView);
		} else if (message.equals(TabFragmentSleepStatistic.TAB_WEEK)) {
			GaplessBarGraphView mGraphView = new GaplessBarGraphView(context, "");

			Calendar sunday = Calendar.getInstance();
			sunday.setTime(targetDate().getTime());
			int dayOfTheWeek = targetDate().get(Calendar.DAY_OF_WEEK);
			sunday.add(Calendar.DAY_OF_WEEK, Calendar.SUNDAY - dayOfTheWeek);

			Calendar saturday = Calendar.getInstance();
			saturday.setTime(targetDate().getTime());
			dayOfTheWeek = targetDate().get(Calendar.DAY_OF_WEEK);
			saturday.add(Calendar.DAY_OF_WEEK, Calendar.SATURDAY - dayOfTheWeek);

			List<SleepRecord> records = db.getSumOfSleepTimeByRange(sunday,
					saturday);
			GraphViewData[] data = new GraphViewData[PER_WEEK];

			hStr = new String[PER_WEEK];
			String rangeOfWeek = "";
			int r = 0;

			for (int i = 0; i < PER_WEEK; i++) {

				int ret = 0;
				if (r < records.size()) {
					// sunday ... saturday
					SleepRecord sleepRecord = records.get(r);
					int weekday = sleepRecord.getGoToBedTime().get(
							Calendar.DAY_OF_WEEK);

					if (weekday == Calendar.SUNDAY + i) {
						// Utilities.getLog("SleepStatisticTabFragment.TAB_WEEK",
						// " weekday int: " + weekday);
						ret = sleepRecord.getActualSleepTime();

						r++;
					}

				}
				switch (i + Calendar.SUNDAY) {
				case Calendar.SUNDAY:
					hStr[i] = "SUNDAY";
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
				}
				data[i] = new GraphViewData(i, ret);
			}

			TextView tv = (TextView) rootView
					.findViewById(R.id.graph_view_title_indicator);
			rangeOfWeek = getWeekRangeSring(sunday, saturday);

			((TextView) rootView.findViewById(R.id.graph_view_title_indicator))
					.setText(rangeOfWeek);
			tv.setText(rangeOfWeek);

			GraphViewSeries series = new GraphViewSeries("Week", style, data);
			mGraphView.setManualYAxisBounds(1440, 0);
			mGraphView.setHorizontalLabels(hStr);
			mGraphView.setVerticalLabels(new String[] { "High", "Mid", "Low" });
			mGraphView.addSeries(series);
			graph.addView(mGraphView);
		} else if (message.equals(TabFragmentSleepStatistic.TAB_MONTH)) {
			GaplessBarGraphView mGraphView = new GaplessBarGraphView(context, "");

			List<SleepRecord> sleepRecords = db
					.getSumOfSleepTimeByMonth(targetDate().getTime());
			PER_MONTH = targetDate().getActualMaximum(Calendar.DAY_OF_MONTH);
			GraphViewData[] data = new GraphViewData[PER_MONTH];

			hStr = new String[PER_MONTH];

			int r = 0;
			int numEntry = sleepRecords.size();
			for (int i = 0; i < PER_MONTH; i++) {

				int ret = 0;
				if (r < numEntry) {
					SleepRecord sleepRecord = sleepRecords.get(r);
					int day = sleepRecord.getGoToBedTime().get(
							Calendar.DAY_OF_MONTH);
					if (day == i) {

						ret = sleepRecord.getActualSleepTime();
						r++;
					}

				}

				if (i == 4 || i == 15 || i == 25) {
					hStr[i] = String.valueOf(i + 1);
				} else {
					hStr[i] = "|";
				}
				data[i] = new GraphViewData(i, ret);
			}

			TextView tv = (TextView) rootView
					.findViewById(R.id.graph_view_title_indicator);

			tv.setText(targetDate().getDisplayName(Calendar.MONTH,
					Calendar.LONG, Locale.getDefault())
					+ " " + String.valueOf(targetDate().get(Calendar.YEAR)));
			GraphViewSeries series = new GraphViewSeries("Month", style, data);

			mGraphView.setManualYAxisBounds(1440, 0);

			mGraphView.setHorizontalLabels(hStr);
			mGraphView.setVerticalLabels(new String[] { "High", "Mid", "Low" });
			mGraphView.addSeries(series);
			graph.addView(mGraphView);
		} else if (message.equals(TabFragmentSleepStatistic.TAB_YEAR)) {
			GaplessBarGraphView mGraphView = new GaplessBarGraphView(context, "");

			List<SleepRecord> sleepRecords = db
					.getSumOfSleepTimeByYear(targetDate().get(Calendar.YEAR));

			GraphViewData[] data = new GraphViewData[PER_YEAR];

			hStr = new String[PER_YEAR];

			int r = 0;
			int numEntry = sleepRecords.size();
			for (int i = 0; i < PER_YEAR; i++) {

				int ret = 0;
				if (r < numEntry) {
					SleepRecord sleepRecord = sleepRecords.get(r);
					int day = sleepRecord.getGoToBedTime().get(Calendar.MONTH);
					if (day == i) {

						ret = sleepRecord.getActualSleepTime();
						r++;
					}
				}
				if (i == 2 || i == 5 || i == 8) {
					hStr[i] = getMonth(i);
				} else {
					hStr[i] = "|";
				}

				data[i] = new GraphViewData(i, ret);
			}

			TextView tv = (TextView) rootView
					.findViewById(R.id.graph_view_title_indicator);

			tv.setText(String.valueOf(targetDate().get(Calendar.YEAR)));

			GraphViewSeries series = new GraphViewSeries("Year", style, data);

			mGraphView.setManualYAxisBounds(44640, 0);

			mGraphView.setHorizontalLabels(hStr);
			mGraphView.setVerticalLabels(new String[] { "High", "Mid", "Low" });
			mGraphView.addSeries(series);
			graph.addView(mGraphView);
		}

	}

	private static String getWeekRangeSring(Calendar sunday, Calendar saturday) {
		int year1 = sunday.get(Calendar.YEAR);
		int year2 = saturday.get(Calendar.YEAR);

		// TODO Auto-generated method stub
		return String.valueOf(sunday.get(Calendar.DAY_OF_MONTH))
				+ " "
				+ sunday.getDisplayName(Calendar.MONTH, Calendar.SHORT,
						Locale.getDefault())

				+ " - "
				+ String.valueOf(saturday.get(Calendar.DAY_OF_MONTH))
				+ " "
				+ saturday.getDisplayName(Calendar.MONTH, Calendar.SHORT,
						Locale.getDefault())
				+ " "
				+ ((year1 == year2) ? String.valueOf(year1) : String
						.valueOf(year1) + "-" + String.valueOf(year2));
	}

	private static String getWeekDay(int i) {
		// TODO Auto-generated method stub
		switch (i) {
		case Calendar.SUNDAY:
			return "SUNDAY";

		case Calendar.MONDAY:
			return "MONDAY";

		case Calendar.TUESDAY:
			return "TUESDAY";

		case Calendar.WEDNESDAY:
			return "WEDNESDAY";

		case Calendar.THURSDAY:
			return "THURSDAY";

		case Calendar.FRIDAY:
			return "FRIDAY";

		case Calendar.SATURDAY:
			return "SATURDAY";
		default:
			return "N/A";
		}

	}

	private static String getMonth(int i) {
		// TODO Auto-generated method stub
		switch (i) {
		case 0:
			return "January";
		case 1:
			return "February";
		case 2:
			return "March";
		case 3:
			return "April";
		case 4:
			return "May";
		case 5:
			return "June";
		case 6:
			return "July";
		case 7:
			return "August";
		case 8:
			return "September";
		case 9:
			return "October";
		case 10:
			return "November";
		case 11:
			return "December";
		default:
			return "N/A";
		}

		/*
		 * January February March April May June July August September October
		 * November December
		 */
	}

	public static void populateSleepPatternGraph(Context context,
			View rootView, ViewGroup graph) {
		graph.removeAllViews();
		GraphViewSeriesStyle style = new GraphViewSeriesStyle();
		style.thickness = 5;
		style.color = 0xFF73CBfD;

		StrokelessBarGraphView mGraphView = new StrokelessBarGraphView(context, "");
		DatabaseHandler db = new DatabaseHandler(context, Main.TABLE_CONTENT,
				null, 1);

		SimpleDateFormat df = new SimpleDateFormat("HH:mm");

		SleepRecord sleeprecord = db.getLastSleepRecord();
		String bedTime = "00:00";
		int actualSleepTime = 0;
		String inBedTime = "0";
		if (sleeprecord != null) {
			bedTime = df.format(sleeprecord.getGoToBedTime().getTime());
			actualSleepTime = sleeprecord.getActualSleepTime();
			inBedTime = String.valueOf(sleeprecord.getInBedTime());
		}
		TextView acutalSleepTimeHourTV = ((TextView) rootView
				.findViewById(R.id.sleep_time_hour_textview));
		if (acutalSleepTimeHourTV != null)
			acutalSleepTimeHourTV.setText(String.valueOf(actualSleepTime / 60));

		TextView acutalSleepTimeMinutesTV = ((TextView) rootView
				.findViewById(R.id.sleep_time_mins_textview));
		if (acutalSleepTimeMinutesTV != null)
			acutalSleepTimeMinutesTV.setText(String
					.valueOf(actualSleepTime % 60));

		TextView sleepEndTV = ((TextView) rootView
				.findViewById(R.id.sleep_end_textfield));
		if (sleepEndTV != null)
			sleepEndTV.setText(bedTime);

		TextView sleepDurationTV = ((TextView) rootView
				.findViewById(R.id.sleep_duration_textfield));
		if (sleepDurationTV != null)
			sleepDurationTV.setText(inBedTime);
		try{
			String dayString = String.valueOf(targetDate().get(
					Calendar.DAY_OF_MONTH))
					+ " "
					+ targetDate().getDisplayName(Calendar.MONTH,
							Calendar.LONG, Locale.getDefault())
					+ " "
					+ String.valueOf(targetDate().get(Calendar.YEAR));
			((TextView) rootView.findViewById(R.id.graph_view_title_indicator)).setText(dayString);
		}
		catch(Exception e)
		{
			getLog(sTag,e.toString());
		}
		
		List<GraphViewData> data = new ArrayList<GraphViewData>();
		List<String> hStr = new ArrayList<String>();
		if (sleeprecord != null) {
			int fallingAsleepDuration = sleeprecord.getFallingAsleepDuration();
			int startSleepInMins = sleeprecord.getGoToBedTime().get(Calendar.HOUR_OF_DAY)*60+sleeprecord.getGoToBedTime().get(Calendar.MINUTE);
			
			int endSleepInMins = sleeprecord.getActualWakeupTime().get(Calendar.HOUR_OF_DAY)*60+sleeprecord.getActualWakeupTime().get(Calendar.MINUTE);
			
			int duration = (endSleepInMins+(60*24))-endSleepInMins;
			for(int j = 0 ; j < fallingAsleepDuration ;j++)
			{
				data.add(new GraphViewData(j, 3));
			}
			
			for(int j = fallingAsleepDuration ; j < duration ;j++)
			{
				data.add(new GraphViewData(j, 0));
			}
			List<SleepPattern> patterns = sleeprecord.getPatterns();

			int j = 0;
			int xValue = 0;
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			for (SleepPattern pattern : patterns) {
				for (int i = 0; i < pattern.getDuration(); i++) {
					
//					int a = 0;
//					switch (pattern.getAmplitude()) {
//					case 22:
//						a = 1;
//						break;
//					case 44:
//						a = 2;
//						break;
//					case 64:
//
//					case 66:
//						a = 3;
//						break;
//					}
//					data.add(new GraphViewData(xValue, a));
//					hStr.add("|");
//					xValue++;
				}

			}
		} else {
			// dummy data
			for (int i = 0; i < 8; i++) {
				data.add(new GraphViewData(i, 0));
				hStr.add("|");
			}
		}

		GraphViewData[] a = data.toArray(new GraphViewData[data.size()]);
		GraphViewSeries series = new GraphViewSeries("Hour", style, a);
		mGraphView.setHorizontalLabels(hStr.toArray(new String[hStr.size()]));
		mGraphView.setManualYAxisBounds(3, 0);
		mGraphView.addSeries(series);
		// stuff that updates ui
		graph.addView(mGraphView);

	}

	public static int prevEntryDate(String displaytype) {
		int ret = 0;
		int type = 0;
		if (displaytype.equals(TabFragmentSleepStatistic.TAB_WEEK)) {
			targetDate().add(Calendar.WEEK_OF_YEAR, -1);
			type = Calendar.WEEK_OF_YEAR;
		} else if (displaytype.equals(TabFragmentSleepStatistic.TAB_MONTH)) {
			targetDate().add(Calendar.MONTH, -1);
			type = Calendar.MONTH;
		} else if (displaytype.equals(TabFragmentSleepStatistic.TAB_YEAR)) {
			targetDate().add(Calendar.YEAR, -1);
			type = Calendar.YEAR;
		} else if (displaytype.equals(TabFragmentActivityStatistic.TAB_DAY)) {
			targetDate().add(Calendar.DAY_OF_YEAR, -1);
			type = Calendar.DAY_OF_YEAR;
		} else if (displaytype.equals(TabFragmentActivityStatistic.TAB_WEEK)) {
			targetDate().add(Calendar.WEEK_OF_YEAR, -1);
			type = Calendar.WEEK_OF_YEAR;
		} else if (displaytype.equals(TabFragmentActivityStatistic.TAB_MONTH)) {
			targetDate().add(Calendar.MONTH, -1);
			type = Calendar.MONTH;
		} else if (displaytype.equals(TabFragmentActivityStatistic.TAB_YEAR)) {
			targetDate().add(Calendar.YEAR, -1);
			type = Calendar.YEAR;
		}
		
		String SQL_DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
		final SimpleDateFormat sqlDateFormat = new SimpleDateFormat(
				SQL_DATEFORMAT);
		
		getLog(sTag,"Target Date : "+sqlDateFormat.format(targetDate().getTime()));
		getLog(sTag,"First Date : "+sqlDateFormat.format(firstDate().getTime()));
		ret = Utilities.targetDate().compareTo(Utilities.firstDate());
		if (targetDate().get(type) == firstDate().get(type)
				|| Utilities.targetDate().compareTo(Utilities.firstDate()) == -1) {
			Utilities.setTargetDate(firstDate().getTime());
		}
		return ret;
	}

	public static int nextEntryDate(String displaytype) {
		int ret = 0;
		int type = 0;
		if (displaytype.equals(TabFragmentSleepStatistic.TAB_WEEK)) {
			targetDate().add(Calendar.WEEK_OF_YEAR, 1);
			type = Calendar.WEEK_OF_YEAR;
		} else if (displaytype.equals(TabFragmentSleepStatistic.TAB_MONTH)) {
			targetDate().add(Calendar.MONTH, 1);
			type = Calendar.MONTH;
		} else if (displaytype.equals(TabFragmentSleepStatistic.TAB_YEAR)) {
			targetDate().add(Calendar.YEAR, 1);
			type = Calendar.YEAR;

		} else if (displaytype.equals(TabFragmentActivityStatistic.TAB_DAY)) {
			targetDate().add(Calendar.DAY_OF_YEAR, 1);
			type = Calendar.DAY_OF_YEAR;
		} else if (displaytype.equals(TabFragmentActivityStatistic.TAB_WEEK)) {
			targetDate().add(Calendar.WEEK_OF_YEAR, 1);
			type = Calendar.WEEK_OF_YEAR;
		} else if (displaytype.equals(TabFragmentActivityStatistic.TAB_MONTH)) {
			targetDate().add(Calendar.MONTH, 1);
			type = Calendar.MONTH;
		} else if (displaytype.equals(TabFragmentActivityStatistic.TAB_YEAR)) {
			targetDate().add(Calendar.YEAR, 1);
			type = Calendar.YEAR;

		}

		ret = Utilities.targetDate().compareTo(Utilities.lastDate());
		
		String SQL_DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
		final SimpleDateFormat sqlDateFormat = new SimpleDateFormat(
				SQL_DATEFORMAT);
		getLog(sTag,"Target Date : "+sqlDateFormat.format(targetDate().getTime()));
		getLog(sTag,"lastDate Date : "+sqlDateFormat.format(lastDate().getTime()));
		if (targetDate().get(type) == lastDate().get(type)
				|| Utilities.targetDate().compareTo(Utilities.lastDate()) == 1) {
			Utilities.setTargetDate( lastDate().getTime());

		}
		return ret;
	}

	public static float KM2MI(float distance) {
		// TODO Auto-generated method stub
		return (float) (distance * 0.621371);
	}

	public static float MI2KM(float distance) {
		// TODO Auto-generated method stub
		return (float) (distance * 1.60934);
	}

	public static float KG2LBS(int v) {
		// TODO Auto-generated method stub
		return (float) (v * 2.20462);
	}

	public static float KG2LBS(Float v) {
		// TODO Auto-generated method stub
		return (float) (v * 2.20462);
	}

	public static float CM2INCH(int v) {
		// TODO Auto-generated method stub
		return (float) (v * 0.393701);
	}

	public static float CM2INCH(Float valueOf) {
		// TODO Auto-generated method stub
		return (float) (valueOf * 0.393701);
	}

	public static int LBS2KG(Float valueOf) {
		// TODO Auto-generated method stub

		return (int) (valueOf * 0.453592);
	}

	public static int INCH2CM(Float valueOf) {
		// TODO Auto-generated method stub
		return (int) (valueOf * 2.54);
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 12;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}
}
