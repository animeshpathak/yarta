package fr.inria.arles.giveaway.util;

import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class FastCache {

	private String filesDir;

	public FastCache(Context context) {
		filesDir = context.getCacheDir().getAbsolutePath() + "/fastcache/";
		new File(filesDir).mkdirs();
	}

	public Bitmap getBitmap(String id) {
		String filename = filesDir + id;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		return BitmapFactory.decodeFile(filename, options);
	}

	public void setBitmap(String id, Bitmap bitmap) {
		try {
			String filename = filesDir + id;
			FileOutputStream out = new FileOutputStream(filename);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
