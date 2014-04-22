package fr.inria.arles.yarta.android.library;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class YartaContentProvider extends ContentProvider {

	/**
	 * Thread used to keep the pipe filled between the two processes;
	 */
	private static class TransferThread extends Thread {
		InputStream in;
		OutputStream out;

		TransferThread(InputStream in, OutputStream out) {
			this.in = in;
			this.out = out;
		}

		@Override
		public void run() {
			byte[] buf = new byte[8192];
			int len;

			try {
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}

				in.close();
				out.flush();
				out.close();
			} catch (IOException e) {
				Log.e(getClass().getSimpleName(),
						"Exception transferring file", e);
			}
		}
	}

	private String filesDir;

	@Override
	public boolean onCreate() {
		filesDir = getContext().getFilesDir().getAbsolutePath() + "/data/";
		new File(filesDir).mkdirs();
		return true;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		String id = values.getAsString("id");
		byte[] data = values.getAsByteArray("data");

		try {
			FileOutputStream out = new FileOutputStream(new File(filesDir + id));
			out.write(data);
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		return null;
	}

	public ParcelFileDescriptor openFile(Uri uri, String mode)
			throws FileNotFoundException {
		ParcelFileDescriptor[] pipe = null;

		try {
			pipe = ParcelFileDescriptor.createPipe();

			InputStream in = new FileInputStream(filesDir
					+ uri.getLastPathSegment());
			new TransferThread(in,
					new ParcelFileDescriptor.AutoCloseOutputStream(pipe[1]))
					.start();
		} catch (Exception e) {
			return null;
		}

		return (pipe[0]);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}
}
