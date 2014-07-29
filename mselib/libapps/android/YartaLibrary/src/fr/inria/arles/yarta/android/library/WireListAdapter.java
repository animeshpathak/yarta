package fr.inria.arles.yarta.android.library;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.inria.arles.iris.R;
import fr.inria.arles.iris.web.ElggClient;
import fr.inria.arles.iris.web.ImageCache;
import fr.inria.arles.iris.web.UserItem;
import fr.inria.arles.iris.web.WireItem;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WireListAdapter extends BaseAdapter implements
		View.OnClickListener {

	public interface Callback {
		public void onProfileClick(UserItem user);

		public void onItemRemove(String guid);
	}

	private class ViewHolder {
		public TextView author;
		public TextView content;
		public TextView time;
		public ImageView image;
		public View remove;
		public View bottomSeparator;
	}

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM, HH:mm",
			Locale.getDefault());
	private List<WireItem> items = new ArrayList<WireItem>();
	private LayoutInflater inflater;
	private Callback callback;

	public WireListAdapter(Context context) {
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
		WireItem item = items.get(position);

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_wire, parent, false);

			holder = new ViewHolder();
			holder.author = (TextView) convertView.findViewById(R.id.author);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.remove = convertView.findViewById(R.id.remove);
			holder.image = (ImageView) convertView.findViewById(R.id.icon);
			holder.bottomSeparator = convertView
					.findViewById(R.id.bottom_separator);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.author.setText(Html.fromHtml(item.getAuthor().getName()));
		holder.author.setTag(position);
		holder.author.setOnClickListener(this);

		holder.remove.setVisibility(item.getAuthor().getUsername()
				.equals(ElggClient.getInstance().getUserGuid()) ? View.VISIBLE
				: View.GONE);
		holder.remove.setTag(position);
		holder.remove.setOnClickListener(this);

		holder.content.setText(Html.fromHtml(item.getContent()));
		holder.time.setText(sdf.format(new Date(item.getTime() * 1000)));

		Drawable drawable = ImageCache.getDrawable(item.getAuthor()
				.getAvatarURL());
		holder.image.setImageDrawable(drawable);

		boolean isLast = position == getCount() - 1;
		holder.bottomSeparator.setVisibility(isLast ? View.VISIBLE : View.GONE);

		return convertView;
	}

	public void setItems(List<WireItem> items) {
		this.items.clear();
		this.items.addAll(items);
		sortItems();
		notifyDataSetChanged();
	}

	public void appendItems(List<WireItem> items) {
		this.items.addAll(items);
		sortItems();
		notifyDataSetChanged();
	}

	private void sortItems() {
		Collections.sort(items, comparator);
	}

	private Comparator<WireItem> comparator = new Comparator<WireItem>() {

		@Override
		public int compare(WireItem lhs, WireItem rhs) {
			long ll = lhs.getTime();
			long lr = rhs.getTime();

			if (ll > lr) {
				return -1;
			} else if (ll < lr) {
				return 1;
			}
			return 0;
		}
	};

	@Override
	public void onClick(View v) {
		Integer position = (Integer) v.getTag();
		WireItem item = items.get(position);

		switch (v.getId()) {
		case R.id.author:
			callback.onProfileClick(item.getAuthor());
			break;
		case R.id.remove:
			callback.onItemRemove(item.getGuid());
			break;
		}
	}
}
