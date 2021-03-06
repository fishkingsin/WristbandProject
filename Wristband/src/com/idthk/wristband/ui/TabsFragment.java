package com.idthk.wristband.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;


public class TabsFragment extends Fragment implements OnTabChangeListener {

	private static final String TAG = "FragmentTabs";
	public static final String TAB_WORDS = "words";
	public static final String TAB_NUMBERS = "numbers";
	public static final String TAB_SETTINGS = "Settings";
	public static final String TAB_PAGER = "pager";
	public static final String TAB_FRAGMENT_TAB = "tabfragmenttab";
	public static final String TAB_MAIN = "main";
	private View mRoot;
	private TabHost mTabHost;
	private int mCurrentTab;
//	private int mPreviousTab;
	OnFragmentTabbedListener mCallback;
	private Context mContext;
    public interface OnFragmentTabbedListener {
        public void onTabbed(String s);
        public void dispatchTabhost(TabHost tabHost);
    }
	@Override
	public void onAttach(Activity activity) {
		try {
            mCallback = (OnFragmentTabbedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
		mContext = activity;
		super.onAttach(activity);
		 
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRoot = inflater.inflate(R.layout.tabs_fragment, null);
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
		updateTabMain(TAB_MAIN, R.id.tab_1);
	}

	private void setupTabs() {
		mTabHost.setup(); // important!

		mTabHost.addTab(newTab(TAB_MAIN, R.string.tab_scroll, R.id.tab_1,
				R.drawable.tab_selector_home));
		mTabHost.addTab(newTab(TAB_FRAGMENT_TAB, R.string.tab_tabfragementtab,
				R.id.tab_2, R.drawable.tab_selector_graph));
		mTabHost.addTab(newTab(TAB_SETTINGS, R.string.tab_settings, R.id.tab_3,
				R.drawable.tab_selector_settings));
		mCallback.dispatchTabhost(mTabHost);
	}

	private TabSpec newTab(String tag, int labelId, int tabContentId,
			Integer imageId) {

		View tab = LayoutInflater.from(mContext).inflate(R.layout.tab, null);
		ImageView image = (ImageView) tab.findViewById(R.id.icon);

		if (imageId != null) {
			image.setImageResource(imageId);
		}

		TabSpec tabSpec = mTabHost.newTabSpec(tag).setIndicator(tab)
				.setContent(tabContentId);
		return tabSpec;
	}

	@Override
	public void onTabChanged(String tabId) {
		
//		Log.d(TAG, "onTabChanged(): tabId=" + tabId);

		if (TAB_MAIN.equals(tabId)) {

			updateTabMain(tabId, R.id.tab_1);
			mCurrentTab = 0;
			return;
		}
		if (TAB_FRAGMENT_TAB.equals(tabId)) {

			updateTabFragTab(tabId, R.id.tab_2);
			mCurrentTab = 1;
			return;
		}
		if (TAB_SETTINGS.equals(tabId)) {
			mCallback.onTabbed(tabId);
			updateTabSetting(tabId, R.id.tab_3);
			mCurrentTab = 2;
			return;
		}
		
		
	}

	private void updateTabSetting(String tabId, int placeholder) {
		
		
//		FragmentManager fm = getFragmentManager();
//		if (fm.findFragmentByTag(tabId) == null) {
//			// gonna to manage actvitiy here
//			fm.beginTransaction()
//					.replace(placeholder, SettingsFragment.newInstance("Settings"),
//							tabId).commit();
//
//		}
	}


	private void updateTabFragTab(String tabId, int placeholder) {
		FragmentManager fm = getFragmentManager();
		if (fm.findFragmentByTag(tabId) == null) {
			// gonna to manage actvitiy here

			fm.beginTransaction()
					.replace(placeholder, new StatisticFragmentPager(), tabId).commit();
			mCallback.onTabbed("Activity Level");
		}
		else
		{
			StatisticFragmentPager fragment = (StatisticFragmentPager)fm.findFragmentByTag(tabId);
			mCallback.onTabbed((fragment.getCurrentPage()==0)?"Activity Level":"Sleep Level");
		}
	}

	private void updateTabMain(String tabId, int placeholder) {
		FragmentManager fm = getFragmentManager();
		
		if (fm.findFragmentByTag(tabId) == null) {
			// gonna to manage actvitiy here
			fm.beginTransaction()
			.replace(placeholder, new MainFragmentPager(), tabId)
			.addToBackStack(tabId)
			.commit();
//			fm.beginTransaction()
//					.replace(placeholder, new ScrollPagerMain(), tabId)
//					.commit();
			mCallback.onTabbed("Activity");
		}
		else
		{
			MainFragmentPager fragment = (MainFragmentPager)fm.findFragmentByTag(tabId);
			mCallback.onTabbed((fragment.getCurrentPage()==0)?"Activity":"Sleep");
		}
	}

}
