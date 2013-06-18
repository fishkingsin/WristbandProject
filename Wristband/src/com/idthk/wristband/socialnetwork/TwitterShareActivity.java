package com.idthk.wristband.socialnetwork;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.idthk.wristband.ui.*;
public class TwitterShareActivity extends Activity {

	private Button tweetButton;
	private Button tweetPicButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.twitter_share_activity);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		Intent intent = getIntent();
        String title = intent.getStringExtra(Main.TITLE);
        String contentString = intent.getStringExtra(MainSlideFragment.TWITTER);
        ((TextView)findViewById(R.id.titlebar_textview)).setText(title);
        EditText editText = (EditText) findViewById(R.id.twitter_share_textfield);
        editText.append(contentString);
		
		((Button) findViewById(R.id.btn_settings_done)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				finish();
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
		tweetButton = (Button) findViewById(R.id.TweetButton);
		tweetButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent shareIntent = findTwitterClient(false); 
				if (shareIntent == null){
					Log.d("Fail", "No Twitter App");
					new AlertDialog.Builder(TwitterShareActivity.this)
                    .setTitle("Fail")
                    .setMessage("No Twitter App")
                    .setPositiveButton("Ok", null)
                    .show();
				}
				else {
					//change the text accordingly
					String message = "test";
			        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
			        startActivity(Intent.createChooser(shareIntent, "Share"));
				}
			}
		});
		
		tweetPicButton = (Button) findViewById(R.id.TweetPicButton);
		tweetPicButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent shareIntent = findTwitterClient(true); 
				if (shareIntent == null){
					Log.d("Fail", "No Twitter App");
					new AlertDialog.Builder(TwitterShareActivity.this)
                    .setTitle("Fail")
                    .setMessage("No Twitter App")
                    .setPositiveButton("Ok", null)
                    .show();
				}
				else {
					//Get the bitmap you wish to share with, the example below is taking a screenshot of the tweetbutton view.
					Bitmap bitmap;
					View v1 = tweetButton;
					v1.setDrawingCacheEnabled(true);
					bitmap = Bitmap.createBitmap(v1.getDrawingCache());
					v1.setDrawingCacheEnabled(false);
					
					//The bitmap must be saved in local directory before I can be used to share out.
					String savepath = getSavePath() + "/temp.jpg";
					
					saveToFilePath(savepath, bitmap);
					
					File file = new File(savepath);
					Log.d("savepath", savepath);
					
					//Get the Uri of the file path and add it to EXTRA_STREAM
			        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
			        //Change the message accordingly
					String message = "test";
			        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
				}
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
		return false;
	}
	
	
	public Intent findTwitterClient(boolean withPic) {
	    final String[] twitterApps = {
	            // package // name - nb installs (thousands)
	            "com.twitter.android", // official - 10 000
	            "com.twidroid", // twidroid - 5 000
	            "com.handmark.tweetcaster", // Tweecaster - 5 000
	            "com.thedeck.android" }; // TweetDeck - 5 000 };
	    Intent tweetIntent = new Intent(Intent.ACTION_SEND);
	    if (withPic){
	    	Log.d("Set", "as image");
	    	tweetIntent.setType("image/jpeg");
	    }
	    else {
		    tweetIntent.setType("text/plain");
	    }
	    final PackageManager packageManager = getPackageManager();
	    List<ResolveInfo> list = packageManager.queryIntentActivities(
	            tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);

	    for (int i = 0; i < twitterApps.length; i++) {
	        for (ResolveInfo resolveInfo : list) {
	            String p = resolveInfo.activityInfo.packageName;
	            if (p != null && p.startsWith(twitterApps[i])) {
	                tweetIntent.setPackage(p);
	                return tweetIntent;
	            }
	        }
	    }
	    return null;

	}


	public void saveToFilePath(String filename,Bitmap bmp) {
	      try {
	          FileOutputStream out = new FileOutputStream(filename);
	          bmp.compress(CompressFormat.PNG, 100, out);
	          out.flush();
	          out.close();
	      } catch(Exception e) {}
	  }
	
	public static File getSavePath() {
	      File path;
	      if (hasSDCard()) { // SD card
	          path = new File(getSDCardPath() + "/Temp/");
	          path.mkdir();
	      } else { 
	          path = Environment.getDataDirectory();
	      }
	      Log.d("path", path.getAbsolutePath());
	      return path;
	  }
	
	public static boolean hasSDCard() { 
	      String status = Environment.getExternalStorageState();
	      return status.equals(Environment.MEDIA_MOUNTED);
	}
	
	public static String getSDCardPath() {
	      File path = Environment.getExternalStorageDirectory();
	      return path.getAbsolutePath();
	}
}
