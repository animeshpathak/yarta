package fr.inria.arles.yarta.android.library;

import fr.inria.arles.iris.R;
import fr.inria.arles.iris.web.MessageItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class MessagesListAdapter extends BaseAdapter {

	private class ViewHolder {
		LinearLayout container;
		TextView title;
		TextView content;
		TextView time;
		ImageView pictureL;
		ImageView pictureR;
	}

	private List<MessageItem> items = new ArrayList<MessageItem>();
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM, HH:mm",
			Locale.getDefault());
	private LayoutInflater inflater;
	private int margins;
	private Drawable me;
	private Drawable other;

	public MessagesListAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		Resources resources = context.getResources();
		margins = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				10, resources.getDisplayMetrics());
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
					.inflate(R.layout.item_message, parent, false);

			holder = new ViewHolder();

			holder.container = (LinearLayout) convertView
					.findViewById(R.id.container);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.pictureL = (ImageView) convertView
					.findViewById(R.id.pictureL);
			holder.pictureR = (ImageView) convertView
					.findViewById(R.id.pictureR);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		MessageItem message = (MessageItem) getItem(position);

		LayoutParams lp = (LayoutParams) holder.container.getLayoutParams();

		if (message.isSent()) {
			holder.container
					.setBackgroundResource(R.drawable.speech_bubble_green);

			lp.setMargins(margins, 0, -margins, 0);

			holder.pictureL.setVisibility(View.GONE);
			holder.pictureR.setVisibility(View.VISIBLE);

			if (me != null) {
				holder.pictureR.setImageDrawable(me);
			}
		} else {
			holder.container
					.setBackgroundResource(R.drawable.speech_bubble_orange);

			lp.setMargins(-margins, 0, margins, 0);

			holder.pictureR.setVisibility(View.GONE);
			holder.pictureL.setVisibility(View.VISIBLE);

			if (other != null) {
				holder.pictureL.setImageDrawable(other);
			}
		}
		holder.container.setLayoutParams(lp);

		holder.title.setText(trim(Html.fromHtml(message.getSubject())));
		holder.content.setText(trim(Html.fromHtml(message.getDescription())));
		holder.time.setText(sdf.format(new Date(message.getTimestamp())));

		return convertView;
	}

	private CharSequence trim(CharSequence s) {
		int start = 0, end = s.length();
		while (start < end && Character.isWhitespace(s.charAt(start))) {
			start++;
		}

		while (end > start && Character.isWhitespace(s.charAt(end - 1))) {
			end--;
		}

		return s.subSequence(start, end);
	}

	public void setItems(List<MessageItem> items) {
		this.items.clear();
		this.items.addAll(items);
		notifyDataSetChanged();
	}

	public void setThumbnails(Drawable me, Drawable other) {
		this.me = me;
		this.other = other;
		notifyDataSetChanged();
	}
}
