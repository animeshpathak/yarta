package fr.inria.arles.yarta.android.library.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.resources.Person;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;
import fr.inria.arles.yarta.knowledgebase.MSEResource;
import fr.inria.arles.yarta.resources.Agent;
import fr.inria.arles.yarta.resources.Content;
import fr.inria.arles.yarta.resources.Conversation;
import fr.inria.arles.yarta.resources.ConversationImpl;
import fr.inria.arles.yarta.resources.Message;

public class MessagesActivity extends BaseActivity implements
		AdapterView.OnItemClickListener {

	public static final String ThreadId = "ThreadId";
	private static final int MENU_REPLY = 2;

	private MessagesListAdapter adapter;
	private Conversation conversation;

	public static void sort(List<? extends Content> list,
			final boolean ascending) {
		Collections.sort(list, new Comparator<Content>() {

			@Override
			public int compare(Content lhs, Content rhs) {
				long l = lhs.getTime();
				long r = rhs.getTime();
				if (l < r) {
					return ascending ? -1 : 1;
				} else if (l > r) {
					return ascending ? 1 : -1;
				}
				return 0;
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messages);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		String threadId = getIntent().getStringExtra(ThreadId);
		conversation = new ConversationImpl(getSAM(), new MSEResource(threadId,
				Conversation.typeURI));

		adapter = new MessagesListAdapter(this, getSAM());

		final ListView list = (ListView) findViewById(R.id.listMessages);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);

		execute(new Job() {
			Person me;
			String title;
			List<Message> messages;

			@Override
			public void doWork() {
				try {
					me = getSAM().getMe();
					messages = new ArrayList<Message>();
					messages.addAll(conversation.getContains());

					sort(messages, true);

					for (Agent agent : conversation.getParticipatesTo_inverse()) {
						if (!agent.equals(me))
							title = agent.getName();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			@Override
			public void doUIAfter() {
				setTitle(title);
				adapter.setItems(messages);
				list.setSelection(messages.size() - 1);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item = menu.add(0, MENU_REPLY, 0, R.string.message_reply);
		item.setIcon(R.drawable.icon_reply);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_REPLY:
			onReply();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void onReply() {
		Intent intent = new Intent(this, MessageActivity.class);

		Message message = (Message) adapter.getItem(0);
		intent.putExtra(MessageActivity.ReplyId, message.getUniqueId());
		startActivity(intent);
		finish();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Message message = (Message) adapter.getItem(position);

		Intent intent = new Intent(this, MessageActivity.class);
		intent.putExtra(MessageActivity.MessageId, message.getUniqueId());
		startActivity(intent);
	}
}
