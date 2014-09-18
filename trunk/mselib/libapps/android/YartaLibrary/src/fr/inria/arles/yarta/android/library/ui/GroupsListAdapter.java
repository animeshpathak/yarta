package fr.inria.arles.yarta.android.library.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.ContentClientPictures;
import fr.inria.arles.yarta.android.library.resources.Group;
import fr.inria.arles.yarta.android.library.resources.Picture;

public class GroupsListAdapter extends BaseAdapter {

	private List<Group> items = new ArrayList<Group>();
	private String membersFormat;

	private class ViewHolder {
		ImageView icon;
		TextView title;
		TextView info;
	}

	private LayoutInflater inflater;
	private ContentClientPictures content;

	public GroupsListAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		membersFormat = context.getString(R.string.groups_members);
		content = new ContentClientPictures(context);
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		if (convertView == null) {
			// TODO: define item group
			convertView = inflater
					.inflate(R.layout.item_profile, parent, false);

			holder = new ViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.title = (TextView) convertView.findViewById(R.id.author);
			holder.info = (TextView) convertView.findViewById(R.id.description);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Group item = items.get(position);

		holder.title.setText(Html.fromHtml(item.getName()));
		holder.info.setText(String.format(membersFormat, item.getMembers()));

		Bitmap bitmap = null;
		for (Picture picture : item.getPicture()) {
			bitmap = content.getBitmap(picture);
		}
		if (bitmap != null) {
			holder.icon.setImageBitmap(bitmap);
		} else {
			holder.icon.setImageResource(R.drawable.group_default);
		}

		return convertView;
	}

	public void setItems(List<Group> items) {
		this.items.clear();
		this.items.addAll(items);
		notifyDataSetChanged();
	}
}
