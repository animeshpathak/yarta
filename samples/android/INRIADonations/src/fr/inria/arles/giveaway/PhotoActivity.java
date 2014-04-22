package fr.inria.arles.giveaway;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class PhotoActivity extends BaseActivity implements View.OnClickListener {

	public static final String BitmapName = "BitmapName";

	private ContentClientPictures client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);

		String name = getIntent().getStringExtra(BitmapName);

		Bitmap bitmap = null;

		if (name.length() > 2) {
			client = new ContentClientPictures(this);
			bitmap = client.getBitmap(name);
		} else {
			bitmap = ItemActivity.getBitmap(name);
		}

		if (bitmap != null) {
			ImageView image = (ImageView) findViewById(R.id.fullimage);
			image.setImageBitmap(bitmap);
		}

		findViewById(R.id.layout_root).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		finish();
	}
}
