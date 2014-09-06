package fr.inria.arles.yarta.android.library;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import fr.inria.arles.iris.web.PostItem;
import fr.inria.arles.iris.web.UserItem;

public class TopicListAdapter extends BaseAdapter implements
		View.OnClickListener {

	public interface Callback {
		public void onClickProfile(UserItem user);

		public void onClickPost(PostItem post);
	}

	private class ViewHolder {
		public TextView author;
		public TextView content;
		public TextView time;
		public ImageView image;
		public View container;
		public View bottomSeparator;
	}

	private List<PostItem> items = new ArrayList<PostItem>();
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM, HH:mm",
			Locale.getDefault());

	private LayoutInflater inflater;
	private Callback callback;

	public TopicListAdapter(Context context) {
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
		PostItem item = items.get(position);

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_post, parent, false);

			holder = new ViewHolder();
			holder.author = (TextView) convertView.findViewById(R.id.author);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.container = convertView.findViewById(R.id.container);
			holder.image = (ImageView) convertView.findViewById(R.id.icon);
			holder.bottomSeparator = convertView
					.findViewById(R.id.bottom_separator);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.author.setText(Html.fromHtml(item.getOwner().getName()));
		holder.author.setTag(position);
		holder.author.setOnClickListener(this);

		holder.container.setTag(position);
		holder.container.setOnClickListener(this);

		holder.content.setText(Html.fromHtml(item.getTitle()));
		holder.time.setText(sdf.format(new Date(item.getTime())));

		Drawable drawable = ImageCache.getDrawable(item.getOwner()
				.getAvatarURL());
		holder.image.setImageDrawable(drawable);

		boolean isLast = position == getCount() - 1;
		holder.bottomSeparator.setVisibility(isLast ? View.VISIBLE : View.GONE);

		return convertView;
	}

	public void setItems(List<PostItem> items) {
		this.items.clear();
		this.items.addAll(items);
		notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag();
		PostItem item = items.get(position);

		switch (v.getId()) {
		case R.id.container:
			callback.onClickPost(item);
			break;
		case R.id.author:
			callback.onClickProfile(item.getOwner());
			break;
		}
	}
}
