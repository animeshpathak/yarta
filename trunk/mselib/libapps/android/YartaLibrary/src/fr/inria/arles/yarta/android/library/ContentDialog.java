package fr.inria.arles.yarta.android.library;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.msemanagement.StorageAccessManagerEx;
import fr.inria.arles.yarta.android.library.resources.Group;
import fr.inria.arles.yarta.android.library.resources.Person;
import fr.inria.arles.yarta.android.library.util.BaseDialog;
import fr.inria.arles.yarta.resources.Content;

public class ContentDialog extends BaseDialog implements View.OnClickListener {

	public interface Callback {
		public void onContentAdded();
	}

	private Group group;
	private StorageAccessManagerEx sam;
	private Callback callback;

	public ContentDialog(Context context, Group group) {
		super(context);
		this.group = group;
	}

	public void setSAM(StorageAccessManagerEx sam) {
		this.sam = sam;
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle(R.string.content_title);
		setContentView(R.layout.dialog_add_content);

		findViewById(R.id.cancel).setOnClickListener(this);
		findViewById(R.id.set).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancel:
			onCancel();
			break;
		case R.id.set:
			onPost();
			break;
		}
	}

	private void onCancel() {
		dismiss();
	}

	private void onPost() {
		final String text = getCtrlText(R.id.content);
		final String title = getCtrlText(R.id.title);

		try {
			Person me = sam.getMe();
			Content content = sam.createContent();

			me.addCreator(content);
			content.setTime(System.currentTimeMillis());
			content.setTitle(title);
			content.setContent(text);
			
			group.addHasContent(content);

			callback.onContentAdded();
			dismiss();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}