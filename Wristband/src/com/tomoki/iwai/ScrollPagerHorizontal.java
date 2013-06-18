/**
 * Copyright (c) 2011-2012 Tomoki Iwai
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.tomoki.iwai;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.animation.OvershootInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.Scroller;

public class ScrollPagerHorizontal implements OnTouchListener
{
	// The class encapsulates scrolling.(Overshoot)
	private Scroller scroller;
	// The task make scroll view scrolled.
	private Runnable task;
	
	private HorizontalScrollView mScrollView;
	private ViewGroup mContentView;
	
	public ScrollPagerHorizontal(HorizontalScrollView aScrollView, ViewGroup aContentView)
	{
		mScrollView = aScrollView;
		mContentView = aContentView;
		scroller = new Scroller(mScrollView.getContext(), new OvershootInterpolator());
		task = new Runnable()
		{
			@Override
			public void run()
			{
				scroller.computeScrollOffset();
				mScrollView.scrollTo(scroller.getCurrX(), 0);
				
				if (!scroller.isFinished())
				{
					mScrollView.post(this);
				}
			}
		};
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		// Stop scrolling calculation.
		scroller.forceFinished(true);
		// Stop scrolling animation.
		mScrollView.removeCallbacks(task);
		
		if (event.getAction() == MotionEvent.ACTION_UP)
		{
			// The height of scroll view, in pixels
			int displayWidth = mScrollView.getWidth();
			// The top of content view, in pixels.
			int contentLeft = mContentView.getPaddingTop();
			// The top of content view, in pixels.
			int contentRight= mContentView.getWidth() - mContentView.getPaddingRight();
			// The top of last page, in pixels.
			int lastPageLeft = contentRight - displayWidth;
			
			// The scrolled top position of scroll view, in pixels.
			int currScrollX = mScrollView.getScrollX();
			// The scrolled middle position of scroll view, in pixels.
			int currScrollMiddleY = currScrollX + displayWidth / 2 - contentLeft;
			
			// Current page num.
			int currPage = currScrollMiddleY / displayWidth;
			
			// Next page num.
			int nextPage = currPage;
			
			// The top of next page, in pixels.
			int nextPageLeft = contentLeft + nextPage * displayWidth; 
			
			// Start scrolling calculation.
			scroller.startScroll(currScrollX, 0, Math.max(Math.min(lastPageLeft, nextPageLeft), contentLeft) - currScrollX, 0, 500);
			
			// Start animation.
			mScrollView.post(task);
			
			// consume(to stop fling)
			return true;
		}
		
		return false;
	}
}
