package fr.inria.arles.foosball;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class VerticalProgress extends ProgressBar {
	public VerticalProgress(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

		paint.setColor(Color.rgb(0xBD, 0xC3, 0xC7));
		canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

		paint.setColor(getResources().getColor(R.color.AppBlueColor));
		canvas.drawRect(0, (100 - progress) * getHeight() / 100, getWidth(),
				getHeight(), paint);

		paint.setColor(Color.WHITE);
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(20);
		canvas.drawText(progress + "%", getWidth() / 2, getHeight() / 2, paint);
	}

	private int progress;

	@Override
	public synchronized void setProgress(int progress) {
		super.setProgress(progress);
		this.progress = progress;
		invalidate();
	}
}
