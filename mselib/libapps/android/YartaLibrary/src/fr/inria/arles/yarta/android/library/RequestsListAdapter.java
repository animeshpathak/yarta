package fr.inria.arles.yarta.android.library;

import java.util.ArrayList;
import java.util.List;

import fr.inria.arles.iris.R;
import fr.inria.arles.iris.web.ImageCache;
import fr.inria.arles.iris.web.RequestItem;
import fr.inria.arles.iris.web.UserItem;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class RequestsListAdapter extends BaseAdapter implements
		View.OnClickListener {

	public interface Callback {
		public void onAccept(RequestItem item);

		public void onIgnore(RequestItem item);

		public void onProfile(UserItem item);
	}

	private List<RequestItem> items = new ArrayList<RequestItem>();

	private class ViewHolder {
		ImageView icon;
		TextView title;
		TextView info;
		Button ignore;
		Button accept;
	}

	private LayoutInflater inflater;
	private String groupJoinFmt;

	private Callback callback;

	public RequestsListAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		groupJoinFmt = context
				.getString(R.string.notifications_wants_to_join_group);
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
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
					.inflate(R.layout.item_request, parent, false);

			holder = new ViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.title = (TextView) convertView.findViewById(R.id.author);
			holder.info = (TextView) convertView.findViewById(R.id.description);
			holder.ignore = (Button) convertView.findViewById(R.id.ignore);
			holder.accept = (Button) convertView.findViewById(R.id.accept);

			holder.ignore.setOnClickListener(this);
			holder.accept.setOnClickListener(this);
			holder.title.setOnClickListener(this);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		RequestItem item = items.get(position);

		UserItem user = item.getUser();

		holder.title.setText(Html.fromHtml(user.getName()));

		if (item.getGroup() == null) {
			holder.info.setText(R.string.notifications_wants_to_be_friend);
		} else {
			holder.info.setText(String.format(groupJoinFmt, item.getGroup()
					.getName()));
		}

		Drawable drawable = ImageCache.getDrawable(user.getAvatarURL());
		holder.icon.setImageDrawable(drawable);

		holder.accept.setTag(position);
		holder.ignore.setTag(position);
		holder.title.setTag(position);

		return convertView;
	}

	public void setItems(List<RequestItem> items) {
		this.items.clear();
		this.items.addAll(items);
		notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		if (callback != null) {
			Integer position = (Integer) v.getTag();
			RequestItem item = items.get(position);

			switch (v.getId()) {
			case R.id.accept:
				callback.onAccept(item);
				break;
			case R.id.ignore:
				callback.onIgnore(item);
				break;
			case R.id.author:
				callback.onProfile(item.getUser());
				break;
			}
		}
	}
}
