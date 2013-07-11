/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.idthk.wristband.ui;
import com.idthk.wristband.ui.R;


import android.support.v4.app.Fragment;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ScrollView;

import android.widget.Button;

/**
 * A fragment representing a single step in a wizard. The fragment shows a dummy title indicating
 * the page number, along with some dummy text.
 *
 * <p>This class is used by the {@link CardFlipActivity} and {@link
 * ScreenSlideActivity} samples.</p>
 */
public class StatisticSlidePageFragment extends Fragment{
	public static final String FACEBOOK = "Facebook";
	public static final String TWITTER = "Twitter";
    /**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";
    public static final String TAG = "ScreenSlidePageFragment";
    OnShareButtonClickedListener mCallback;
    public interface OnShareButtonClickedListener {
        public void onShareButtonClicked(String s);
    }
    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber;

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnShareButtonClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
    
    public static StatisticSlidePageFragment create(int pageNumber) {
    	StatisticSlidePageFragment fragment = new StatisticSlidePageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public StatisticSlidePageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
        Log.v(TAG,"ScreenSlidePageFragment : ID "+this.getId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	final ViewGroup rootView;
    	if(mPageNumber==0)
    	{
    		rootView = (ViewGroup) inflater
                  .inflate(R.layout.main_fragment_activity, container, false);
    		((Button)rootView.findViewById(R.id.button_facebook_share)).setOnClickListener( new OnClickListener() {
                public void onClick(View m) {
                	
                	mCallback.onShareButtonClicked(FACEBOOK);
                 }
             });
    		((Button)rootView.findViewById(R.id.button_twitter_share)).setOnClickListener( new OnClickListener() {
                public void onClick(View m) {
                	
                	mCallback.onShareButtonClicked(TWITTER);
                 }
             });
    		
    		final ScrollView scrollView = (ScrollView) rootView.findViewById(R.id.main_activity_scroll_view);
//    		scrollView.setOnTouchListener(new ScrollPagerVertical(scrollView, rootView));
    		scrollView.post(new Runnable()
	        {
	                public void run()
	                {
	                	scrollView.scrollTo(0, 0);
	                }
	        });
    		
//    		SaundProgressBar m_regularProgressBar = (SaundProgressBar) rootView.findViewById(R.id.progress_bar_large);
//    	    
//    	    Drawable indicator = getResources().getDrawable(R.drawable.progress_indicator);
//    	    Rect bounds = new Rect(0, 0, indicator.getIntrinsicWidth() + 5, indicator.getIntrinsicHeight());
//    	    
//    	    indicator.setBounds(bounds);
//    	    
//    	    m_regularProgressBar.setProgressIndicator(indicator);
//    	    m_regularProgressBar.setProgress(50);
    	}
    	else
    	{
    		rootView = (ViewGroup) inflater
                    .inflate(R.layout.main_fragment_sleep, container, false);
    		((Button)rootView.findViewById(R.id.button_facebook_share)).setOnClickListener( new OnClickListener() {
                public void onClick(View m) {
                	
                	mCallback.onShareButtonClicked(FACEBOOK);
                 }
             });
    		((Button)rootView.findViewById(R.id.button_twitter_share)).setOnClickListener( new OnClickListener() {
                public void onClick(View m) {
                	
                	mCallback.onShareButtonClicked(TWITTER);
                 }
             });
    	}
        // Inflate the layout containing a title and body text.
//    	ViewGroup rootView = (ViewGroup) inflater
//                .inflate(R.layout.fragment_screen_slide_page, container, false);
//    	// Set the title view to show the page number.
//        ((TextView) rootView.findViewById(android.R.id.text1)).setText(
//                getString(R.string.title_template_step, mPageNumber + 1));
        return rootView;
    }
    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }
}
