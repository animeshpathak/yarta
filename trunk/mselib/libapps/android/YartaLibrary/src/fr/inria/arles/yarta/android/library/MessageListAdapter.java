package fr.inria.arles.yarta.android.library;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.web.ImageCache;
import fr.inria.arles.yarta.android.library.web.MessageItem;
import fr.inria.arles.yarta.android.library.web.UserItem;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageListAdapter extends BaseAdapter implements
		View.OnClickListener {

	public interface Callback {
		public void onProfileClick(UserItem item);

		public void onRemove(MessageItem item);
	}

	private class ViewHolder {
		public TextView author;
		public TextView subject;
		public TextView time;
		public View remove;
		public View container;
		public ImageView image;
	}

	private List<MessageItem> messages = new ArrayList<MessageItem>();
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM, HH:mm",
			Locale.getDefault());
	private LayoutInflater inflater;
	private Callback callback;

	public MessageListAdapter(Context context) {
		inflater = LayoutInflater.from(context);
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
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
					.inflate(R.layout.item_message, parent, false);

			holder = new ViewHolder();
			holder.author = (TextView) convertView.findViewById(R.id.author);
			holder.subject = (TextView) convertView.findViewById(R.id.subject);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.remove = convertView.findViewById(R.id.remove);
			holder.container = convertView.findViewById(R.id.container);
			holder.image = (ImageView) convertView.findViewById(R.id.icon);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		MessageItem item = messages.get(position);

		holder.author.setText(Html.fromHtml(item.getFrom().getName()));
		holder.subject.setText(Html.fromHtml(item.getSubject()));
		holder.time.setText(sdf.format(new Date(item.getTimestamp() * 1000)));

		holder.container
				.setVisibility(item.isRead() ? View.GONE : View.VISIBLE);

		holder.remove.setTag(position);
		holder.remove.setOnClickListener(this);

		holder.author.setTag(position);
		holder.author.setOnClickListener(this);

		Drawable drawable = ImageCache.getDrawable(item.getFrom()
				.getAvatarURL());
		holder.image.setImageDrawable(drawable);
		return convertView;
	}

	public void setItems(List<MessageItem> messages) {
		this.messages.clear();
		this.messages.addAll(messages);
		notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag();
		MessageItem item = messages.get(position);
		switch (v.getId()) {
		case R.id.remove:
			if (callback != null) {
				callback.onRemove(item);
			}
			break;
		case R.id.author:
			if (callback != null) {
				callback.onProfileClick(item.getFrom());
			}
			break;
		}
	}
}
