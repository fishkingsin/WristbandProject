package com.idthk.wristband.graphview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;

public class GaplessBarGraphView extends BarGraphView {

	public GaplessBarGraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public GaplessBarGraphView(Context context, String string) {
		super(context, string);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void drawSeries(Canvas canvas, GraphViewData[] values, float graphwidth, float graphheight,
			float border, double minX, double minY, double diffX, double diffY,
			float horstart, GraphViewSeriesStyle style) {
		
		float colwidth = (graphwidth ) / values.length;

		paint.setStrokeWidth(style.thickness);
		paint.setColor(style.color);

		// draw data
		for (int i = 0; i < values.length; i++) {
			float valY = (float) (values[i].valueY - minY);
			float ratY = (float) (valY / diffY);
			float y = graphheight * ratY;

			// hook for value dependent color
			if (style.getValueDependentColor() != null) {
				paint.setColor(style.getValueDependentColor().get(values[i]));
			}

			canvas.drawRect((i * colwidth) + horstart, (border - y) + graphheight, ((i * colwidth) + horstart) + (colwidth - 1), graphheight + border - 1, paint);
		}
	}

}
