package fr.inria.arles.yarta.conference;

import java.util.ArrayList;
import java.util.List;

import fr.inria.arles.yarta.core.YartaWrapper;
import fr.inria.arles.yarta.resources.Resource;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ResourcePickDialog extends Dialog implements View.OnClickListener {

	public interface Handler {
		public void onResourcesSelected(List<String> resourceIds);
	}

	public ResourcePickDialog(Context context) {
		super(context);
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_linkresource);
		setTitle(R.string.pick_title);

		lstAdapter = new ResourceListAdapter(getContext());
		lstAdapter.setEditable(false, null);

		ListView list = (ListView) findViewById(R.id.resourceList);
		list.setAdapter(lstAdapter);

		Button button = (Button) findViewById(R.id.okButton);
		button.setText(R.string.pick_ok);
		button.setOnClickListener(this);

		button = (Button) findViewById(R.id.cancelButton);
		button.setText(R.string.pick_cancel);
		button.setOnClickListener(this);

		List<Object> resources = new ArrayList<Object>();

		try {
			resources.addAll(YartaWrapper.getInstance()
					.getAllPushableResources());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		lstAdapter.setResources(resources);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.okButton:
			onButtonOKClicked();
			break;
		case R.id.cancelButton:
			onButtonCancelClicked();
			break;
		}
	}

	private void onButtonOKClicked() {
		List<String> selectedResIds = new ArrayList<String>();
		for (int i = 0; i < lstAdapter.getCount(); i++) {
			if (!lstAdapter.getChecked(i))
				continue;

			selectedResIds
					.add(((Resource) lstAdapter.getItem(i)).getUniqueId());
		}

		if (selectedResIds.size() == 0) {
			Toast.makeText(getContext(), R.string.pick_none_selected,
					Toast.LENGTH_SHORT).show();
			return;
		}

		dismiss();
		if (handler != null) {
			handler.onResourcesSelected(selectedResIds);
		}
	}

	private void onButtonCancelClicked() {
		dismiss();
	}

	private Handler handler;
	private ResourceListAdapter lstAdapter;
}
