package cx.mccormick.pddroidparty;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.text.DecimalFormat;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.StaticLayout;
import android.view.MotionEvent;
import android.util.Log;

public class Numberbox extends Widget {
	private static final String TAG = "Floatatom";
	
	float min, max;
	int numwidth;
	
	StaticLayout numLayout = null;
	DecimalFormat fmt = null;
	Rect tRect = new Rect();
	
	boolean down = false;
	
	public Numberbox(PdDroidPatchView app, String[] atomline) {
		super(app);
		
		float x = Float.parseFloat(atomline[2]) / parent.patchwidth * screenwidth;
		float y = Float.parseFloat(atomline[3]) / parent.patchheight * screenheight;
		
		// calculate screen bounds for the numbers that can fit
		numwidth = Integer.parseInt(atomline[4]);
		StringBuffer calclen = new StringBuffer();
		for (int s=0; s<numwidth; s++) {
			if (s == 1) {
				calclen.append(".");
			} else {
				calclen.append("#");
			}
		}
		fmt = new DecimalFormat(calclen.toString());
		paint.getTextBounds(calclen.toString(), 0, calclen.length(), tRect);
		dRect.set(tRect);
		dRect.sort();
		dRect.offset((int)x, (int)y + fontsize);
		dRect.top -= 3;
		dRect.bottom += 3;
		dRect.left -= 3;
		dRect.right += 3;
		
		min = Float.parseFloat(atomline[5]);
		max = Float.parseFloat(atomline[6]);
		sendname = app.app.replaceDollarZero(atomline[10]);
		receivename = atomline[9];
		label = setLabel(atomline[8]);
		labelpos[0] = x;
		labelpos[1] = y;
		
		setval(0, 0);
		
		// listen out for floats from Pd
		setupreceive();
	}
	
	public Numberbox(PdDroidPatchView app) {
		super(app);
	}
	
	public void draw(Canvas canvas) {
		canvas.drawLine(dRect.left + 1, dRect.top, dRect.right - 5, dRect.top, paint);
		canvas.drawLine(dRect.left + 1, dRect.bottom, dRect.right, dRect.bottom, paint);
		canvas.drawLine(dRect.left, dRect.top + 1, dRect.left, dRect.bottom, paint);
		canvas.drawLine(dRect.right, dRect.top + 5, dRect.right, dRect.bottom, paint);
		canvas.drawLine(dRect.right - 5, dRect.top, dRect.right, dRect.top + 5, paint);
	}
	
	public void touch(MotionEvent event) {
		float ex = event.getX();
		float ey = event.getY();
		
		if (event.getAction() == event.ACTION_DOWN && dRect.contains(ex, ey)) {
			down = true;
		}
		
		if (event.getAction() == event.ACTION_UP) {
			if (dRect.contains(ex, ey)) {
				parent.app.launchDialog(this, PdDroidParty.DIALOG_NUMBERBOX);
			}
			down = false;
		}
		
		// TODO: allow dragging to set the number
		/*if (down) {
			//Log.e(TAG, "touch:" + val);
			if (event.getAction() == event.ACTION_DOWN || event.getAction() == event.ACTION_MOVE) {
				// calculate the new valimport com.csipsimple.R;ue based on touch
				if (orientation_horizontal) {
					val = (((ex - x) / w) * (max - min) + min);
				} else {
					val = (((h - (ey - y)) / h) * (max - min) + min);
				}
				// clamp the value
				val = Math.min(max, Math.max(min, val));
				// send the result to Pd
				send("" + val);
			} else if (event.getAction() == event.ACTION_UP) {
				down = false;
			}
		}*/
	}
	
	public void receiveList(Object... args) {
		if (args.length > 0 && args[0].getClass().equals(Float.class)) {
			receiveFloat((Float)args[0]);
		}
	}
	
	public void receiveFloat(float v) {
		if (min != 0 || max != 0) {
			val = Math.min(max, Math.max(v, min));
		} else {
			val = v;
		}
		sendFloat(val);
	}
}

