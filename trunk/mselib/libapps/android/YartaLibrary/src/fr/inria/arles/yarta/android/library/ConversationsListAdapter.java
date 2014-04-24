package fr.inria.arles.yarta.android.library;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.resources.Agent;
import fr.inria.arles.yarta.resources.Conversation;
import fr.inria.arles.yarta.resources.Message;
import fr.inria.arles.yarta.resources.Person;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ConversationsListAdapter extends BaseAdapter {

	private Context context;

	public ConversationsListAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return conversations.size();
	}

	@Override
	public Object getItem(int position) {
		return conversations.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_conversation, parent, false);
		}

		Conversation conversation = conversations.get(position);
		Set<Agent> agents = conversation.getParticipatesTo_inverse();

		String participants = "";

		for (Agent a : agents) {
			if (a.equals(me))
				continue;
			String id = a.getUniqueId();
			id = "#" + id.substring(id.indexOf('_') + 1, id.indexOf('@')) + " ";
			participants += id;
		}

		List<Message> set = conversation.getMessages();

		if (set.size() > 0) {
			Message msg = set.get(set.size() - 1);

			TextView txtContent = (TextView) convertView
					.findViewById(R.id.content);
			txtContent.setText(msg.getTitle());
		}

		TextView txtInfo = (TextView) convertView.findViewById(R.id.info);
		txtInfo.setText(participants);
		return convertView;
	}

	public void setConversations(List<Conversation> conversations, Person me) {
		this.me = me;
		this.conversations.clear();
		this.conversations.addAll(conversations);
		this.notifyDataSetChanged();
	}

	private Person me;
	private List<Conversation> conversations = new ArrayList<Conversation>();
}
