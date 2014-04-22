package fr.inria.arles.yarta.android.library;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import fr.inria.arles.yarta.middleware.msemanagement.ContentClient;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

public class ContentClientAndroid implements ContentClient {

	private static final String URI = "content://fr.inria.arles.yarta.android.library.YartaContentProvider/";
	private ContentResolver contentResolver;

	public ContentClientAndroid(Object context) {
		Context ctx = (Context) context;
		this.contentResolver = ctx.getContentResolver();
	}

	@Override
	public void setData(String id, byte[] data) {
		ContentValues cv = new ContentValues();
		cv.put("id", id);
		cv.put("data", data);
		contentResolver.insert(Uri.parse(URI), cv);
	}

	@Override
	public byte[] getData(String id) {
		try {
			ParcelFileDescriptor pfd = contentResolver.openFileDescriptor(
					Uri.parse(URI + id), "r");
			InputStream is = new FileInputStream(pfd.getFileDescriptor());

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int nRead;
			byte[] data = new byte[16384];

			while ((nRead = is.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}

			is.close();

			buffer.flush();

			return buffer.toByteArray();
		} catch (Exception ex) {
			System.err.println("getData(" + id + ") exception");
		}
		return null;
	}
}