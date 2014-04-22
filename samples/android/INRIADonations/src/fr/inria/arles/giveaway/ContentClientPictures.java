package fr.inria.arles.giveaway;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import fr.inria.arles.yarta.android.library.ContentClientAndroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * User defined content client;
 */
public class ContentClientPictures extends ContentClientAndroid {

	public ContentClientPictures(Object context) {
		super(context);
	}

	public Bitmap getBitmap(String id) {
		return decodeSampledBitmap(getData(id), 640, 480);
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

	public Bitmap getSmallBitmap(String id) {
		return decodeSampledBitmap(getData(id), 85, 85);
	}

	public static Bitmap decodeSampledBitmap(byte data[], int reqWidth,
			int reqHeight) {

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

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

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
