package fr.inria.arles.foosball;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ProgressBar;

public class CircleProgress extends ProgressBar {

	public CircleProgress(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG
			| Paint.FILTER_BITMAP_FLAG);

	private RectF mainArea;
	private RectF topArea;
	private int stroke = 0;

	@Override
	public void draw(Canvas canvas) {
		if (mainArea == null || topArea == null) {
			mainArea = new RectF(0, 0, getWidth(), getHeight());
			topArea = new RectF(stroke, stroke, getWidth() - stroke,
					getHeight() - stroke);
		}

		if (stroke == 0) {
			stroke = getPixels(10);
		}

		paint.setColor(Color.rgb(0xBD, 0xC3, 0xC7));
		canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2,
				paint);

		int arc = 360 * progress / getMax();
		paint.setColor(getBackgroundColor(this));
		canvas.drawArc(mainArea, -90, -arc, true, paint);
		canvas.drawArc(topArea, -90, -arc, true, paint);

		paint.setColor(getResources().getColor(R.color.AppContentColor));
		canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2
				- stroke, paint);
	}

	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Rect mBounds;

	public void initIfNeeded() {
		if (mBitmap == null) {
			mBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
			mCanvas = new Canvas(mBitmap);
			mBounds = new Rect();
		}
	}

	public int getBackgroundColor(View view) {
		// The actual color, not the id.
		int color = Color.BLACK;

		if (view.getBackground() instanceof ColorDrawable) {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
				initIfNeeded();

				// If the ColorDrawable makes use of its bounds in the draw
				// method,
				// we may not be able to get the color we want. This is not the
				// usual
				// case before Ice Cream Sandwich (4.0.1 r1).
				// Yet, we change the bounds temporarily, just to be sure that
				// we are
				// successful.
				ColorDrawable colorDrawable = (ColorDrawable) view
						.getBackground();

				mBounds.set(colorDrawable.getBounds()); // Save the original
														// bounds.
				colorDrawable.setBounds(0, 0, 1, 1); // Change the bounds.

				colorDrawable.draw(mCanvas);
				color = mBitmap.getPixel(0, 0);

				colorDrawable.setBounds(mBounds); // Restore the original
													// bounds.
			} else {
				color = ((ColorDrawable) view.getBackground()).getColor();
			}
		}

		return color;
	}

	@Override
	public synchronized void setProgress(int progress) {
		super.setProgress(progress);
		this.progress = progress;
		invalidate();
	}

	public int getPixels(int dp) {
		Resources r = getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				r.getDisplayMetrics());
		return (int) px;
	}

	private int progress;
}
