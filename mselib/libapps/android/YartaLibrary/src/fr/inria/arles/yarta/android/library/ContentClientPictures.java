package fr.inria.arles.yarta.android.library;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import fr.inria.arles.yarta.android.library.ContentClientAndroid;
import fr.inria.arles.yarta.android.library.resources.Picture;
import fr.inria.arles.yarta.resources.Resource;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * User defined content client;
 */
public class ContentClientPictures extends ContentClientAndroid {

	public ContentClientPictures(Object context) {
		super(context);
	}

	private String getShortName(Resource resource) {
		String uniqueId = resource.getUniqueId();
		return uniqueId.substring(uniqueId.indexOf('#') + 1);
	}
	
	public Bitmap getBitmap(Picture picture) {
		byte[] data = getData(getShortName(picture));
		if (data != null) {
			return decodeSampledBitmap(data);
		} else {
			return null;
		}
	}

	public void setBitmap(String id, Bitmap bitmap) {
		byte[] data = null;
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			data = stream.toByteArray();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		setData(id, data);
	}

	public void setBitmap(String id, String url) {
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url)
					.openConnection();
			connection.connect();
			InputStream input = connection.getInputStream();

			Bitmap bitmap = BitmapFactory.decodeStream(input);
			setBitmap(id, bitmap);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static Bitmap decodeSampledBitmap(byte data[]) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		BitmapFactory.decodeStream(bis, null, options);

		try {
			bis.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		Bitmap bitmap = null;
		bis = new ByteArrayInputStream(data);
		bitmap = BitmapFactory.decodeStream(bis, null, options);
		try {
			bis.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return bitmap;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}
	// @Override
	// public byte[] getData(String id) {
	// try {
	// ParcelFileDescriptor pfd = contentResolver.openFileDescriptor(
	// Uri.parse(URI + id), "r");
	// InputStream is = new FileInputStream(pfd.getFileDescriptor());
	//
	// ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	// int nRead;
	// byte[] data = new byte[16384];
	//
	// while ((nRead = is.read(data, 0, data.length)) != -1) {
	// buffer.write(data, 0, nRead);
	// }
	//
	// is.close();
	//
	// buffer.flush();
	//
	// return buffer.toByteArray();
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// }
	// return null;
	// }
}
