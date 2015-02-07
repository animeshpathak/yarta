package fr.inria.arles.yarta.android.library.ui;

import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.ContentClientPictures;
import fr.inria.arles.yarta.android.library.msemanagement.StorageAccessManagerEx;
import fr.inria.arles.yarta.android.library.resources.Person;
import fr.inria.arles.yarta.android.library.resources.Picture;
import fr.inria.arles.yarta.resources.Agent;
import fr.inria.arles.yarta.resources.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
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

	private List<Message> items = new ArrayList<Message>();
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM, HH:mm",
			Locale.getDefault());
	private LayoutInflater inflater;
	private int margins;
	private ContentClientPictures content;

	private StorageAccessManagerEx sam;
	private Person me;

	public MessagesListAdapter(Context context, StorageAccessManagerEx sam) {
		content = new ContentClientPictures(context);
		inflater = LayoutInflater.from(context);
		Resources resources = context.getResources();
		margins = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				10, resources.getDisplayMetrics());

		try {
			this.sam = sam;
			me = (Person) sam.getMe();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
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

		Message message = (Message) getItem(position);

		LayoutParams lp = (LayoutParams) holder.container.getLayoutParams();

		Bitmap bitmap = null;
		boolean sent = false;
		for (Agent agent : message.getCreator_inverse()) {
			if (agent.equals(me)) {
				sent = true;
			}
			Person person = (Person) sam.getResourceByURI(agent.getUniqueId());
			for (Picture picture : person.getPicture()) {
				bitmap = content.getBitmap(picture);
			}
		}

		if (sent) {
			holder.container
					.setBackgroundResource(R.drawable.speech_bubble_green);

			lp.setMargins(margins, 0, -margins, 0);

			holder.pictureL.setVisibility(View.GONE);
			holder.pictureR.setVisibility(View.VISIBLE);

			if (bitmap != null) {
				holder.pictureR.setImageResource(0);
				holder.pictureR.setImageBitmap(bitmap);
			} else {
				holder.pictureR.setImageBitmap(null);
				holder.pictureR.setImageResource(R.drawable.user_default);
			}
		} else {
			holder.container
					.setBackgroundResource(R.drawable.speech_bubble_orange);

			lp.setMargins(-margins, 0, margins, 0);

			holder.pictureR.setVisibility(View.GONE);
			holder.pictureL.setVisibility(View.VISIBLE);

			if (bitmap != null) {
				holder.pictureL.setImageResource(0);
				holder.pictureL.setImageBitmap(bitmap);
			} else {
				holder.pictureL.setImageBitmap(null);
				holder.pictureL.setImageResource(R.drawable.user_default);
			}
		}
		holder.container.setLayoutParams(lp);

		holder.title.setText(trim(Html.fromHtml(message.getTitle())));
		holder.content.setText(trim(Html.fromHtml(message.getContent())));
		holder.time.setText(sdf.format(new Date(message.getTime())));

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

	public void setItems(List<Message> items) {
		this.items.clear();
		this.items.addAll(items);
		notifyDataSetChanged();
	}
}
