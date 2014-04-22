package fr.inria.arles.foosball;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import android.view.MotionEvent;
import android.widget.CompoundButton;
import android.widget.RadioButton;

public class CustomRadio extends RadioButton implements
		CompoundButton.OnCheckedChangeListener {

	private Bitmap background;
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG
			| Paint.FILTER_BITMAP_FLAG);

	public CustomRadio(Context context, AttributeSet attrs) {
		super(context, attrs);

		if (getBackground() instanceof BitmapDrawable) {
			background = ((BitmapDrawable) getBackground()).getBitmap();
		}

		setOnCheckedChangeListener(this);
	}

	// needed for nice positioning the bitmap
	Rect src, dest;

	RectF fullRect;

	@Override
	public void draw(Canvas canvas) {
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);

		if (src == null && dest == null && fullRect == null) {
			fullRect = new RectF(0, 0, getWidth(), getHeight());
			if (background != null) {
				src = new Rect(0, 0, background.getWidth(),
						background.getHeight());
			} else {
				src = new Rect(0, 0, getWidth(), getHeight());
			}
			dest = new Rect(10, 10, getWidth() - 10, getHeight() - 10);
		}

		if (isChecked()) {
			paint.setColor(Color.rgb(0xc7, 0xc7, 0xc7));
			canvas.drawRoundRect(fullRect, 10, 10, paint);
		}

		if (background != null) {
			canvas.drawBitmap(background, src, dest, paint);
		} else {
			Drawable drawable = getBackground();
			drawable.setBounds(padding, padding, getWidth() - padding,
					getHeight() - padding);
			drawable.draw(canvas);
		}
	}

	private final int padding = 30;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		invalidate();
	}

}
