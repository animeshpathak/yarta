package fr.inria.arles.giveaway;

import java.util.Set;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import fr.inria.arles.giveaway.msemanagement.StorageAccessManagerEx;
import fr.inria.arles.giveaway.resources.Announcement;
import fr.inria.arles.giveaway.resources.Category;
import fr.inria.arles.giveaway.resources.CategoryImpl;
import fr.inria.arles.yarta.knowledgebase.MSEResource;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

public class Common {

	// public static final String InriaID = "inria@inria.fr";

	private static String[] categories;

	public static String[] getCategories(SherlockFragmentActivity context) {
		if (categories == null) {
			categories = new String[7];

			categories[0] = context.getString(R.string.category_kitchen);
			categories[1] = context.getString(R.string.category_games);
			categories[2] = context.getString(R.string.category_furniture);
			categories[3] = context.getString(R.string.category_laisure);
			categories[4] = context.getString(R.string.category_animals);
			categories[5] = context.getString(R.string.category_bricolaje);
			categories[6] = context.getString(R.string.category_misc);
		}

		return categories;
	}

	public static Bitmap decodeSampledBitmap(String imagePath, int reqWidth,
			int reqHeight) {

		int orientation = 0;

		try {
			ExifInterface exif = new ExifInterface(imagePath);
			orientation = exif
					.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
		} catch (Exception ex) {
		}

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagePath, options);

		// Calculate inSampleSize
		options.inSampleSize = ContentClientPictures.calculateInSampleSize(
				options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

		int rotation = 90;

		System.out.println(orientation);

		if (orientation == 1) {
			return bitmap;
		} else if (orientation == 8) {
			rotation = -90;
		} else if (orientation == 3) {
			rotation = 180;
		}

		Matrix matrix = new Matrix();
		matrix.postRotate(rotation);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, false);
	}

	/**
	 * Returns the Category instance associated with this localized category
	 * string.
	 * 
	 * @param sam
	 * @param category
	 * @return
	 */
	public static Category getLocalizedCategory(Context context,
			StorageAccessManagerEx sam, String category) {
		final String mse = "http://yarta.gforge.inria.fr/ontologies/mse.rdf";
		String categoryId = null;

		if (category.equals(context.getString(R.string.category_kitchen))) {
			categoryId = mse + "#Kitchen";
		} else if (category.equals(context
				.getString(R.string.category_furniture))) {
			categoryId = mse + "#Furniture";
		} else if (category.equals(context.getString(R.string.category_games))) {
			categoryId = mse + "#Games";
		} else if (category
				.equals(context.getString(R.string.category_laisure))) {
			categoryId = mse + "#Leisure";
		} else if (category
				.equals(context.getString(R.string.category_animals))) {
			categoryId = mse + "#Animals";
		} else if (category.equals(context
				.getString(R.string.category_bricolaje))) {
			categoryId = mse + "#Gardening";
		} else if (category.equals(context.getString(R.string.category_misc))) {
			categoryId = mse + "#Misc";
		}

		if (categoryId != null) {
			return new CategoryImpl(sam, new MSEResource(categoryId,
					Category.typeURI));
		}
		return null;
	}

	/**
	 * Returns the string associated with the Category#uniqueId of this
	 * Announcement.
	 * 
	 * @param context
	 * @param announcement
	 * @return String
	 */
	public static String getLocalizedCategory(Context context,
			Announcement announcement) {
		Set<Category> categories = announcement.getCategory();

		for (Category category : categories) {
			String uniqueId = category.getUniqueId();

			if (uniqueId.contains("#Kitchen")) {
				return context.getString(R.string.category_kitchen);
			}
			if (uniqueId.contains("#Games")) {
				return context.getString(R.string.category_games);
			}
			if (uniqueId.contains("#Furniture")) {
				return context.getString(R.string.category_furniture);
			}
			if (uniqueId.contains("#Leisure")) {
				return context.getString(R.string.category_laisure);
			}
			if (uniqueId.contains("#Animals")) {
				return context.getString(R.string.category_animals);
			}
			if (uniqueId.contains("#Gardening")) {
				return context.getString(R.string.category_bricolaje);
			}
			if (uniqueId.contains("#Misc")) {
				return context.getString(R.string.category_misc);
			}
		}
		return null;
	}
}
