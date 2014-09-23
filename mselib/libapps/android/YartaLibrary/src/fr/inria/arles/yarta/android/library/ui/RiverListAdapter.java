package fr.inria.arles.yarta.android.library.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.inria.arles.iris.R;
import fr.inria.arles.iris.web.ObjectItem;
import fr.inria.arles.iris.web.RiverItem;
import fr.inria.arles.iris.web.UserItem;
import fr.inria.arles.yarta.android.library.util.ImageCache;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RiverListAdapter extends BaseAdapter implements
		View.OnClickListener {

	public interface Callback {
		public void onProfileClick(UserItem user);

		public void onObjectClick(ObjectItem object);
	}

	private class ViewHolder {
		ImageView icon;
		TextView subject;
		TextView predicate;
		TextView object;
		TextView time;
		TextView extra;
		View bottomSeparator;
	}

	private List<RiverItem> items = new ArrayList<RiverItem>();
	private LayoutInflater inflater;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM, HH:mm",
			Locale.getDefault());
	private Callback callback;

	public RiverListAdapter(Context context) {
		inflater = LayoutInflater.from(context);
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
			convertView = inflater.inflate(R.layout.item_river, parent, false);

			holder = new ViewHolder();

			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.subject = (TextView) convertView.findViewById(R.id.subject);
			holder.object = (TextView) convertView.findViewById(R.id.object);
			holder.predicate = (TextView) convertView
					.findViewById(R.id.predicate);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.extra = (TextView) convertView.findViewById(R.id.extra);
			holder.bottomSeparator = convertView
					.findViewById(R.id.bottom_separator);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		RiverItem item = items.get(position);

		if (item.getSubject() != null) {
			holder.subject.setText(Html.fromHtml(item.getSubject().getName()));
			holder.subject.setOnClickListener(this);
			holder.subject.setTag(position);

			Bitmap bitmap = ImageCache.getBitmap(item.getSubject());
			holder.icon.setImageBitmap(bitmap);
		}

		holder.predicate.setText(Html.fromHtml(item.getPredicate()));

		if (item.getObject() != null) {
			boolean hasExtra = item.getObject().getType().equals("thewire");

			if (item.getObject().getType().equals("thewire")) {
				holder.object.setText(R.string.main_river_wire);
			} else {

				if (item.getObject().getName() != null) {
					holder.object.setText(Html.fromHtml(item.getObject()
							.getName()));
				} else {
					holder.object.setText("");
				}
			}

			holder.object.setTag(position);
			holder.object.setOnClickListener(this);

			if (hasExtra) {
				holder.extra.setVisibility(View.VISIBLE);
				holder.extra.setText(Html.fromHtml(item.getObject()
						.getDescription()));
			} else {
				holder.extra.setVisibility(View.GONE);
			}
		}

		holder.time.setText(sdf.format(new Date(item.getTime())));

		boolean isLast = position == getCount() - 1;
		holder.bottomSeparator.setVisibility(isLast ? View.VISIBLE : View.GONE);
		return convertView;
	}

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag();
		RiverItem item = items.get(position);

		switch (v.getId()) {
		case R.id.subject:
			callback.onProfileClick(item.getSubject());
			break;
		case R.id.object:
			callback.onObjectClick(item.getObject());
			break;
		}
	}

	public void setItems(List<RiverItem> items) {
		this.items.clear();
		this.items.addAll(items);
		notifyDataSetChanged();
	}

	public void appendItems(List<RiverItem> items) {
		this.items.addAll(items);
		notifyDataSetChanged();
	}
}
