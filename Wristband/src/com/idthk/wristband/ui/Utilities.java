package com.idthk.wristband.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

public class Utilities {
	public static AlertDialog alertDialog;
	public static final String DEFAULT_FONT = "handel.ttf";
	public static final boolean DEBUG_MODE = false; 
	private static String sTag = "Utilities";
	public static boolean showBalloon = true;
	
	public static final String VERSION_NUMBER = "2.0";
	
	public static void showAlert(String msg, Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
    	builder.setTitle(R.string.message)
    		   .setMessage(msg)
    	       .setCancelable(false)
    	       .setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	        	   dialog.cancel();
    	           }
    	       });
    	if (alertDialog != null && alertDialog.isShowing()) {
    		try{
    			alertDialog.cancel();
    		}catch(Exception e){
    			Utilities.getLog(sTag, e.toString());
    		}
    	}
    	
    	try{
	    	alertDialog = builder.create();
	    	alertDialog.show();
    	}catch(Exception e){
    		Log.e("error", e.toString());
    	}
    }
	
	public static void quitApp(Context context) {
		Intent intent = new Intent();
		intent.setAction(IntentMessager.ACTION_FILTER);
		intent.putExtra(IntentMessager.ACTION_ID, IntentMessager.ACTION_CLOSE_APP);
		context.sendBroadcast(intent);
	}
	
	public static void finishLoading(Context context){
		Intent intent = new Intent();
		intent.setAction(IntentMessager.ACTION_FILTER);
		intent.putExtra(IntentMessager.ACTION_ID, IntentMessager.ACTION_FINISH_LOADING);
		context.sendBroadcast(intent);
	}
	
	public static void setDefaultFontStyle(Object object, int paddingLeft, int paddingTop, int paddingRight, int paddingBottom, Context context) {
		setFontStyle(object, DEFAULT_FONT, paddingLeft, paddingTop, paddingRight, paddingBottom, context);
	}
	
	public static void setFontStyle(Object object, String font, int paddingLeft, int paddingTop, int paddingRight, int paddingBottom, Context context) {
		Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + font);
		
		if (object.getClass().equals(TextView.class)) {
			((TextView) object).setTypeface(typeface);
			((TextView) object).setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
		} else if (object.getClass().equals(EditText.class)) {
			((EditText) object).setTypeface(typeface);
			((EditText) object).setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
		}
	}
	
	public static boolean macAddressFormatIsValid(String inputStr){
	    String patternStr = "[0-9a-f]{12}";
	    Pattern pattern = Pattern.compile(patternStr);
	    Matcher matcher = pattern.matcher(inputStr);
	    return matcher.matches();
	}
	
	public static boolean emailAddressIsValid(String inputStr){
	    String patternStr = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	    Pattern pattern = Pattern.compile(patternStr);
	    Matcher matcher = pattern.matcher(inputStr);
	    return matcher.matches();
	}
	
	public static void getLog(String sTag, String message){
		if (DEBUG_MODE){
			Log.d(sTag, message);
		}
	}
	
	//decodes image and scales it to reduce memory consumption
	public static Bitmap decodeFile(File f, Context mContext){
	    try {
	        //Decode image size
	        BitmapFactory.Options o = new BitmapFactory.Options();
	        o.inJustDecodeBounds = true;
	        BitmapFactory.decodeStream(new FileInputStream(f),null,o);

	        //The new size we want to scale to
	        final int REQUIRED_SIZE=300;

	        //Find the correct scale value. It should be the power of 2.
	        int scale=1;
	        while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
	            scale*=2;

	        //Decode with inSampleSize
	        BitmapFactory.Options o2 = new BitmapFactory.Options();
	        o2.inSampleSize=scale;
	        
//	        ContentResolver cr = mContext.getContentResolver();
	        float degree = 0;
	        Bitmap bitmap = null;
	        try {
	            ExifInterface exif = new ExifInterface(f.getPath());
	            String exifOrientation = exif
	                    .getAttribute(ExifInterface.TAG_ORIENTATION);
	            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
	            
	            if (bitmap != null) {
	                degree = getDegree(exifOrientation);
	                Log.d("degree:", String.valueOf(degree));
	                //if (degree != 0)
	                    //bitmap = createRotatedBitmap(bitmap, degree);
	            }
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        
	        return bitmap;
	    } catch (FileNotFoundException e) {}
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
	
	public static String replaceSpecialCharacter(String text){
		String editedText = "";
		
		if (text != null){
			editedText = text.replace("\"", "");
		}

		return editedText;
	}
	
}
