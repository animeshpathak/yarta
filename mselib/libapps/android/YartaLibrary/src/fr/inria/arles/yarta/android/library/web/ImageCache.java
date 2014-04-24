package fr.inria.arles.yarta.android.library.web;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class ImageCache {

	private static Map<String, Drawable> cache = new HashMap<String, Drawable>();

	public static Drawable drawableFromUrl(String url) throws IOException {
		Bitmap x;

		HttpURLConnection connection = (HttpURLConnection) new URL(url)
				.openConnection();
		connection.connect();
		InputStream input = connection.getInputStream();

		x = BitmapFactory.decodeStream(input);
		return new BitmapDrawable(x);
	}

	public static Drawable getDrawable(String url) {
		synchronized (cache) {
			return cache.get(url);
		}
	}

	public static void setDrawable(String url, Drawable drawable) {
		synchronized (cache) {
			cache.put(url, drawable);
		}
	}
}
