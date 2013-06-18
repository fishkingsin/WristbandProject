package com.idthk.wristband.ui;

import com.idthk.wristband.ui.R;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.LinearLayout;
public class ActivityStatisticTabFragment extends Fragment implements OnTabChangeListener {

	private static final String TAG = "TabFragmentTab";
	public static final String TAB_DAY = "tab_day";
	public static final String TAB_WEEK = "tab_week";
	public static final String TAB_MONTH = "tab_month";
	public static final String TAB_YEAR = "tab_year";

	private View mRoot;
	private TabHost mTabHost;
	private int mCurrentTab;
	public static final String ARG_PAGE = "page";

//	public static ActivityStatisticTabFragment create(int pageNumber) {
//		ActivityStatisticTabFragment fragment = new ActivityStatisticTabFragment();
//        Bundle args = new Bundle();
//        args.putInt(ARG_PAGE, pageNumber);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//	@Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        int mPageNumber = getArguments().getInt(ARG_PAGE);
//        Log.v(TAG,"mPageNumber "+mPageNumber);
//    }
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRoot = inflater.inflate(R.layout.tabs_fragment_tabs_activity, null);
		mTabHost = (TabHost) mRoot.findViewById(android.R.id.tabhost);
		
		setupTabs();
		return mRoot;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);

		mTabHost.setOnTabChangedListener(this);
		mTabHost.setCurrentTab(mCurrentTab);
		// manually start loading stuff in the first tab
		updateTab(TAB_DAY, R.id.tab_day1);
	}

	private void setupTabs() {
		mTabHost.setup(); // important!
		mTabHost.addTab(newTab(TAB_DAY, R.string.tab_day, R.id.tab_day1));
		mTabHost.addTab(newTab(TAB_WEEK, R.string.tab_week, R.id.tab_week2));
		mTabHost.addTab(newTab(TAB_MONTH, R.string.tab_month, R.id.tab_month3));
		mTabHost.addTab(newTab(TAB_YEAR, R.string.tab_year, R.id.tab_year4));
		
	}

	private TabSpec newTab(String tag, int labelId, int tabContentId) {
		Log.d(TAG, "buildTab(): tag=" + tag);

		View indicator = LayoutInflater.from(getActivity()).inflate(
				R.layout.tab_tab,
				(ViewGroup) mRoot.findViewById(android.R.id.tabs), false);
		((TextView) indicator.findViewById(R.id.text)).setText(labelId);
		LinearLayout ll = ((LinearLayout) indicator.findViewById(R.id.tab_layout));
		if(tabContentId == R.id.tab_day1)
		{
			ll.setBackgroundResource(R.drawable.tab_selector_day);
		}
		else if(tabContentId == R.id.tab_week2)
		{
			ll.setBackgroundResource(R.drawable.tab_selector_week);
		}
		else if(tabContentId == R.id.tab_month3)
		{
			ll.setBackgroundResource(R.drawable.tab_selector_month);
		}
		else
		{
			ll.setBackgroundResource(R.drawable.tab_selector_year);
		}
		
		TabSpec tabSpec = mTabHost.newTabSpec(tag);
		tabSpec.setIndicator(indicator);
		tabSpec.setContent(tabContentId);
		return tabSpec;
	}

	@Override
	public void onTabChanged(String tabId) {
		Log.d(TAG, "onTabChanged(): tabId=" + tabId);

		if (TAB_DAY.equals(tabId)) {
			updateTab(tabId, R.id.tab_day1);
			mCurrentTab = 0;
			return;
		}
		if (TAB_WEEK.equals(tabId)) {
			updateTab(tabId, R.id.tab_week2);
			mCurrentTab = 1;
			return;
		}
		if (TAB_MONTH.equals(tabId)) {
			updateTab(tabId, R.id.tab_month3);
			mCurrentTab = 2;
			return;
		}
		if (TAB_YEAR.equals(tabId)) {
			updateTab(tabId, R.id.tab_year4);
			mCurrentTab = 3;
			return;
		}

	}

	private void updateTab(String tabId, int placeholder) {
		FragmentManager fm = getFragmentManager();
		Log.v(TAG,"tabId : " + tabId);
		if (fm.findFragmentByTag(tabId) == null) {
			//gonna to manage actvitiy here
		
			fm.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.replace(placeholder, StatisticFragment.newInstance(tabId), tabId)
					.commit();
		}
		else
		{
			Log.e(TAG,"Fragent not fount");
		}
	}

}