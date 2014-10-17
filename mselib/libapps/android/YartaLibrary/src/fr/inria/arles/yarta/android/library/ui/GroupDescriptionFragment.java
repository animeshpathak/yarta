package fr.inria.arles.yarta.android.library.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.resources.Group;
import fr.inria.arles.yarta.android.library.resources.GroupImpl;
import fr.inria.arles.yarta.android.library.resources.Picture;
import fr.inria.arles.yarta.android.library.util.BaseFragment;
import fr.inria.arles.yarta.knowledgebase.MSEResource;

public class GroupDescriptionFragment extends BaseFragment {

	private Group group;

	public void setGroupGuid(String groupGuid) {
		group = new GroupImpl(sam, new MSEResource(groupGuid, Group.typeURI));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_group_description,
				container, false);

		return root;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (sam != null) {
			refreshUI(null);
		}
	}

	@Override
	public void refreshUI(String notification) {
		if (group.getName() != null) {
			setCtrlText(R.id.name, Html.fromHtml(group.getName()));
		}

		try {
			String members = String.format(getString(R.string.group_members),
					group.getMembers());
			setCtrlText(R.id.members, members);
		} catch (NumberFormatException ex) {
			// TODO: we should not throw!
		}

		if (group.getDescription() != null) {
			setVisible(R.id.description, true);
			setCtrlText(R.id.description, Html.fromHtml(group.getDescription()));
		} else {
			setVisible(R.id.description, false);
		}

		Bitmap bitmap = null;
		for (Picture picture : group.getPicture()) {
			bitmap = contentClient.getBitmap(picture);
		}
		ImageView image = (ImageView) getView().findViewById(R.id.icon);
		if (bitmap != null) {
			image.setImageBitmap(bitmap);
		} else {
			image.setImageResource(R.drawable.group_default);
		}
	}
}
