package fr.inria.arles.yarta.android.library.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;
import fr.inria.arles.iris.R;
import fr.inria.arles.iris.web.FileItem;
import fr.inria.arles.iris.web.ImageCache;
import fr.inria.arles.yarta.android.library.util.Base64;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;

public class FileActivity extends BaseActivity {

	public static final String FileGuid = "FileGuid";

	private String fileGuid;
	private FileItem item;
	private String fileName;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM, HH:mm",
			Locale.getDefault());

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		fileGuid = getIntent().getStringExtra(FileGuid);

		execute(new Job() {

			@Override
			public void doWork() {
				item = client.getFileInfo(fileGuid);

				if (item != null) {
					if (item.getOwner() != null) {
						String avatarURL = item.getOwner().getAvatarURL();
						if (ImageCache.getDrawable(avatarURL) == null) {
							try {
								ImageCache.setDrawable(avatarURL,
										ImageCache.drawableFromUrl(avatarURL));
							} catch (Exception ex) {
							}
						}
					}
				}
			}

			@Override
			public void doUIAfter() {
				if (item == null) {
					return;
				} else if (item.getOwner() == null) {
					return;
				}
				setCtrlText(R.id.name, Html.fromHtml(item.getOwner().getName()));
				setCtrlText(R.id.title, Html.fromHtml(item.getTitle()));
				setCtrlText(R.id.content, Html.fromHtml(item.getDescription()));
				ImageView image = (ImageView) findViewById(R.id.icon);
				Drawable drawable = ImageCache.getDrawable(item.getOwner()
						.getAvatarURL());
				if (drawable != null) {
					image.setImageDrawable(drawable);
				} else {
					image.setVisibility(View.GONE);
				}

				// name, time, size
				String fname = getString(R.string.file_name) + item.getName();
				setCtrlText(R.id.fname, fname);

				String time = getString(R.string.file_time)
						+ sdf.format(new Date(item.getTime()));
				setCtrlText(R.id.time, time);
				setCtrlText(R.id.size, String.format(
						getString(R.string.file_size_fmt),
						(float) item.getSize() / 1048576.0));

				refreshActionButton();
			}
		});
	}

	private String getFilename(FileItem item) {
		File downloads = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		try {
			downloads.mkdirs();
		} catch (Exception ex) {
			// just to make sure folder exists
		}

		fileName = downloads.getAbsolutePath() + "/" + item.getName();
		return fileName;
	}

	private void refreshActionButton() {
		// if file exists download = open
		fileName = getFilename(item);

		if (fileName != null && new File(fileName).exists()) {
			setCtrlText(R.id.download, getString(R.string.file_open));
		}
	}

	private String fileExt(String url) {
		if (url.indexOf("?") > -1) {
			url = url.substring(0, url.indexOf("?"));
		}
		if (url.lastIndexOf(".") == -1) {
			return null;
		} else {
			String ext = url.substring(url.lastIndexOf("."));
			if (ext.indexOf("%") > -1) {
				ext = ext.substring(0, ext.indexOf("%"));
			}
			if (ext.indexOf("/") > -1) {
				ext = ext.substring(0, ext.indexOf("/"));
			}
			return ext.toLowerCase();
		}
	}

	private void openFile(String fileName) {
		MimeTypeMap myMime = MimeTypeMap.getSingleton();

		Intent newIntent = new Intent(android.content.Intent.ACTION_VIEW);

		// Intent newIntent = new Intent(Intent.ACTION_VIEW);
		String mimeType = myMime.getMimeTypeFromExtension(fileExt(fileName)
				.substring(1));
		newIntent.setDataAndType(Uri.fromFile(new File(fileName)), mimeType);
		newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			startActivity(newIntent);
		} catch (android.content.ActivityNotFoundException e) {
			Toast.makeText(this, R.string.file_cant_open, Toast.LENGTH_LONG)
					.show();
		}
	}

	public void onClickDownload(View view) {
		// in case it was downloaded, just open it
		if (fileName != null && new File(fileName).exists()) {
			openFile(fileName);
			return;
		}
		executeWithMessage(R.string.file_downloading, new Job() {

			String fileName;

			@Override
			public void doWork() {
				String content = client.getFileContent(fileGuid);
				byte[] binaryContent = Base64.decode(content);
				File downloads = Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
				fileName = downloads.getAbsolutePath() + "/" + item.getName();

				try {
					downloads.mkdirs();
					FileOutputStream out = new FileOutputStream(fileName);
					out.write(binaryContent);
					out.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			@Override
			public void doUIAfter() {
				Toast.makeText(
						FileActivity.this,
						getString(R.string.file_downloaded)
								+ "\r\n"
								+ Environment
										.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
						Toast.LENGTH_LONG).show();
				refreshActionButton();
			}
		});
	}
}
