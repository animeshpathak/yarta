package fr.inria.arles.yarta.android.library;

import java.util.ArrayList;
import java.util.List;

import fr.inria.arles.yarta.R;
import fr.inria.arles.yarta.resources.Agent;
import fr.inria.arles.yarta.resources.Message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MessagesListAdapter extends BaseAdapter {

	private Context context;

	public MessagesListAdapter(Context context) {
		this.context = context;
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
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_message, parent, false);
		}

		Message message = messages.get(position);

		String content = message.getTitle();
		String creator = null;

		for (Agent a : message.getCreator_inverse()) {
			creator = a.getUniqueId().substring(a.getUniqueId().indexOf('_') + 1);
		}

		TextView txtCreator = (TextView) convertView.findViewById(R.id.creator);
		TextView txtContent = (TextView) convertView.findViewById(R.id.content);

		txtCreator.setText(creator);
		txtContent.setText(content);
		return convertView;
	}

	public void setMessages(List<Message> messages) {
		this.messages.clear();
		this.messages.addAll(messages);
		notifyDataSetChanged();
	}

	private List<Message> messages = new ArrayList<Message>();
}
