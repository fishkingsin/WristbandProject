package com.idthk.wristband.ui;


import com.idthk.wristband.ui.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.TextView;

public class MainFragmentPager extends Fragment {
	private static final int NUMBER_OF_PAGES = 2;
	public static final int ACTIVITY = 0;
	public static final int SLEEP = 1;
	private int mCurrentPage = 0;
	private ViewPager mViewPager;
	private MyFragmentPagerAdapter mMyFragmentPagerAdapter;
	
	
	PagerChangedCallback mCallback;

    // Container Activity must implement this interface
    public interface PagerChangedCallback {
        public void onPagerChangedCallback(int page);
    }
	
	@Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container,
	   Bundle savedInstanceState) {
	   
	   View v = inflater.inflate(R.layout.main_fragmentpager, container, false);
	   mViewPager = (ViewPager) v.findViewById(R.id.pager);
	   mMyFragmentPagerAdapter = new MyFragmentPagerAdapter(getFragmentManager());
	   mViewPager.setAdapter(mMyFragmentPagerAdapter);
	   mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
           @Override
           public void onPageSelected(int position) {
        	   mCurrentPage = position;
        	   mCallback.onPagerChangedCallback(position);
        	   
               // When changing pages, reset the action bar actions since they are dependent
               // on which page is currently active. An alternative approach is to have each
               // fragment expose actions itself (rather than the activity exposing actions),
               // but for simplicity, the activity provides the actions in this sample.
            
           }
       });
//	   mIndicator = (CirclePageIndicator)v.findViewById(R.id.indicator);
//       mIndicator.setViewPager(mViewPager);
	   return v;
	 }
	@Override 
	public void onResume()
	{
		if(mCallback!=null)
		{
			mCallback.onPagerChangedCallback(mCurrentPage);
		}
		super.onResume();
		
	}
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (PagerChangedCallback) activity;
            mCallback.onPagerChangedCallback(ACTIVITY);
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
	private static class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
             super(fm);
        }  

        @Override
        public Fragment getItem(int index) { 

             return MainSlideFragment.create( index);
        }  

        @Override 
        public int getCount() {

             return NUMBER_OF_PAGES;
        }
   }

	public int getCurrentPage() {
		// TODO Auto-generated method stub
		return mCurrentPage;
	}
}
