package com.idthk.wristband.ui;

//import java.util.ArrayList;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;


public class InstructionActivity extends FragmentActivity implements ScreenSlidePageFragment.OnSkipClickedListener{
	
	static final String TAG = "InstructionActivity";
	static int numberOfPages = 3;
	ViewPager myViewPager;
	MyFragmentPagerAdapter myFragmentPagerAdapter;
	    String text = "test";
	static boolean bFirstTime = false;
	
	PageIndicator mIndicator;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	    setContentView(R.layout.instruction_fragment);
	    bFirstTime = getIntent().getBooleanExtra(ScreenSlidePageFragment.ARG_FIRSTTIME, false);
	    myViewPager = (ViewPager) findViewById(R.id.pager);
	    myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
	    myViewPager.setAdapter(myFragmentPagerAdapter);
//	    ScreenSlidePageFragment page1 = myFragmentPagerAdapter.getItem(0);
	    
	    mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(myViewPager);
	    
	    if(myViewPager.findViewById(R.id.slide3_skip_textview)!=null)
	    {
	    	Log.v(TAG,"slide3_skip_textview found");
	    }
	    else
	    {
	    	Log.e(TAG,"slide3_skip_textview cam not be found");	
	    }
	}
	 public void onSelected(int arg) {
//		 finish();
		 if(arg == ScreenSlidePageFragment.USERPROFILE_REQUEST)
		 {
			 Intent intent = new Intent(this, UserPreferencesActivity.class);
			 startActivityForResult(intent,Main.TO_USER_PROFILE_REQUEST);
		 }
		 else if(arg == ScreenSlidePageFragment.CLOSE_REQUEST)
		 {
			 finish();
		 }
		 
		 
	        // The user selected the headline of an article from the HeadlinesFragment
	        // Do something here to display that article
	 }
	 protected void onActivityResult(int requestCode, int resultCode,
	            Intent data) {
	        
	        Log.v(TAG,"requestCode " + requestCode +" resultCode "+ resultCode);
	        if(requestCode==Main.TO_USER_PROFILE_REQUEST)
	        {
	        	finish();
	        }
	    }
	// Adapters
	private static class MyFragmentPagerAdapter extends FragmentPagerAdapter {
	    public MyFragmentPagerAdapter(FragmentManager fm)   {
	        super(fm);
	    }

	    @Override
	    public ScreenSlidePageFragment getItem(int index) {

	        return ScreenSlidePageFragment.create(index,bFirstTime);
	    }

	    @Override
	    public int getCount() {
	        return numberOfPages;
	    }
	}
	@Override
	public void onBackPressed() {
	}

}