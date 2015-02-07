package fr.inria.arles.yarta.android.library.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.ContentClientPictures;
import fr.inria.arles.yarta.android.library.msemanagement.StorageAccessManagerEx;
import fr.inria.arles.yarta.android.library.resources.Person;
import fr.inria.arles.yarta.android.library.resources.Picture;
import fr.inria.arles.yarta.resources.Agent;
import fr.inria.arles.yarta.resources.Conversation;
import fr.inria.arles.yarta.resources.Message;
import android.content.Context;
import android.graphics.Bitmap;
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

	private List<Conversation> messages = new ArrayList<Conversation>();
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM, HH:mm",
			Locale.getDefault());
	private LayoutInflater inflater;
	private ContentClientPictures content;
	private StorageAccessManagerEx sam;
	private Person owner;

	public ThreadsListAdapter(Context context, StorageAccessManagerEx sam) {
		inflater = LayoutInflater.from(context);
		content = new ContentClientPictures(context);
		try {
			this.sam = sam;
			this.owner = (Person) sam.getMe();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
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
			convertView = inflater.inflate(R.layout.item_thread, parent, false);

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

		Conversation thread = messages.get(position);

		Person person = null;
		for (Agent agent : thread.getParticipatesTo_inverse()) {
			if (!agent.equals(owner)) {
				person = (Person) sam.getResourceByURI(agent.getUniqueId());
			}
		}

		Set<Message> messages = thread.getContains();

		if (messages.size() > 0) {
			// TODO: get the last message
			Message message = null;
			for (Message m : messages) {
				message = m;
			}

			String name = person.getName();
			if (name == null) {
				name = person.getUserId();
			}
			holder.author.setText(Html.fromHtml(name));
			holder.subject.setText(Html.fromHtml(message.getTitle()));

			try {
				holder.time.setText(sdf.format(new Date(message.getTime())));
			} catch (Exception ex) {
				holder.time.setText("return Long.valueOf ex!");
			}

			// TODO: see if message is read
			holder.container.setVisibility(View.GONE);

			Bitmap bitmap = null;
			for (Picture picture : person.getPicture()) {
				bitmap = content.getBitmap(picture);
			}

			if (bitmap != null) {
				holder.image.setImageResource(0);
				holder.image.setImageBitmap(bitmap);
			} else {
				holder.image.setImageBitmap(null);
				holder.image.setImageResource(R.drawable.user_default);
			}
		}
		return convertView;
	}

	public void setItems(Set<Conversation> messages) {
		this.messages.clear();
		this.messages.addAll(messages);
		notifyDataSetChanged();
	}
}
