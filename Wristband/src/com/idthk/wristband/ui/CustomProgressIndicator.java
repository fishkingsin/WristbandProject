package com.idthk.wristband.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomProgressIndicator extends TextView {

  public CustomProgressIndicator(Context context) {
    this(context, null);
  }
  
  public CustomProgressIndicator(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }
  
  public CustomProgressIndicator(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }
}
