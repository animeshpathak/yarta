package fr.inria.arles.yarta.android.library;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.inria.arles.iris.R;
import fr.inria.arles.iris.web.ImageCache;
import fr.inria.arles.iris.web.MessageItem;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ThreadsListAdapter extends BaseAdapter {

	private class ViewHolder {
		public TextView author;
		public TextView subject;
		public TextView time;
		public View container;
		public ImageView image;
	}

	private List<List<MessageItem>> messages = new ArrayList<List<MessageItem>>();
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM, HH:mm",
			Locale.getDefault());
	private LayoutInflater inflater;

	public ThreadsListAdapter(Context context) {
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return messages.size();
	}

	@Override
	public Object getItem(int position) {
		return messages.get(position);
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
					.inflate(R.layout.item_thread, parent, false);

			holder = new ViewHolder();
			holder.author = (TextView) convertView.findViewById(R.id.author);
			holder.subject = (TextView) convertView.findViewById(R.id.subject);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.container = convertView.findViewById(R.id.container);
			holder.image = (ImageView) convertView.findViewById(R.id.icon);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		List<MessageItem> thread = messages.get(position);
		MessageItem item = thread.get(thread.size() - 1);

		holder.author.setText(Html.fromHtml(item.getFrom().getName()));
		holder.subject.setText(Html.fromHtml(item.getSubject()));
		holder.time.setText(sdf.format(new Date(item.getTimestamp() * 1000)));

		holder.container
				.setVisibility(item.isRead() ? View.GONE : View.VISIBLE);

		Drawable drawable = ImageCache.getDrawable(item.getFrom()
				.getAvatarURL());
		holder.image.setImageDrawable(drawable);
		return convertView;
	}

	public void setItems(List<List<MessageItem>> messages) {
		this.messages.clear();
		this.messages.addAll(messages);
		notifyDataSetChanged();
	}
}
