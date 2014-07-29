package fr.inria.arles.yarta.android.library;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.inria.arles.iris.R;
import fr.inria.arles.iris.web.ImageCache;
import fr.inria.arles.iris.web.UserItem;

public class FriendsListAdapter extends BaseAdapter {

	private List<UserItem> items = new ArrayList<UserItem>();

	private class ViewHolder {
		ImageView icon;
		TextView title;
		TextView info;
	}

	private LayoutInflater inflater;

	public FriendsListAdapter(Context context) {
		inflater = LayoutInflater.from(context);
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

		UserItem item = items.get(position);

		holder.title.setText(Html.fromHtml(item.getName()));
		holder.info.setText(item.getLocation());

		Drawable drawable = ImageCache.getDrawable(item.getAvatarURL());
		holder.icon.setImageDrawable(drawable);

		return convertView;
	}

	public void setItems(List<UserItem> items) {
		this.items.clear();
		this.items.addAll(items);
		notifyDataSetChanged();
	}
}
