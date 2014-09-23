package fr.inria.arles.yarta.android.library.util;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import fr.inria.arles.iris.web.ObjectItem;
import fr.inria.arles.iris.web.UserItem;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;

/**
 * This is an in-memory image cache used to fast access pictures.
 */
public class ImageCache {

	public static Bitmap getBitmap(UserItem user) {
		Bitmap result = get(user.getAvatarURL());
		if (result == null && !isUIThread()) {
			result = set(user.getAvatarURL(),
					bitmapFromUrl(user.getAvatarURL()));
		}
		return result;
	}

	public static Bitmap getBitmap(ObjectItem item) {
		Bitmap result = get(item.getDescription());
		if (result == null && !isUIThread()) {
			result = set(item.getDescription(),
					bitmapFromUrl(item.getDescription()));
		}
		return result;
	}

	private static Bitmap get(String url) {
		synchronized (cache) {
			return cache.get(url);
		}
	}

	private static Bitmap set(String url, Bitmap bitmap) {
		synchronized (cache) {
			cache.put(url, bitmap);
		}
		return bitmap;
	}

	private static boolean isUIThread() {
		return Looper.myLooper() == Looper.getMainLooper();
	}

	private static Bitmap bitmapFromUrl(String url) {
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url)
					.openConnection();
			connection.connect();
			InputStream input = connection.getInputStream();

			return BitmapFactory.decodeStream(input);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private static Map<String, Bitmap> cache = new HashMap<String, Bitmap>();
}
