package fr.inria.arles.yarta.android.library.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.resources.Person;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;
import fr.inria.arles.yarta.resources.Agent;
import fr.inria.arles.yarta.resources.Content;
import fr.inria.arles.yarta.resources.Conversation;
import fr.inria.arles.yarta.resources.Message;

public class MessagesActivity extends BaseActivity implements
		AdapterView.OnItemClickListener {

	public static final String ThreadId = "ThreadId";

	private MessagesListAdapter adapter;
	private Conversation conversation;
	private String peerId;
	private String conversationTitle;
	private ListView list;

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
		conversation = (Conversation) getSAM().getResourceByURI(threadId);

		adapter = new MessagesListAdapter(this, getSAM());

		list = (ListView) findViewById(R.id.listMessages);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		
		refreshUI(null);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Message message = (Message) adapter.getItem(position);

		Intent intent = new Intent(this, MessageActivity.class);
		intent.putExtra(MessageActivity.MessageId, message.getUniqueId());
		startActivity(intent);
	}

	@Override
	protected void refreshUI(String notification) {
		execute(new Job() {
			Person me;
			String title;
			List<Message> messages;

			@Override
			public void doWork() {
				try {
					me = (Person) getSAM().getMe();
					messages = new ArrayList<Message>();
					messages.addAll(conversation.getContains());

					sort(messages, true);

					for (Message message : messages) {
						conversationTitle = message.getTitle();
						break;
					}

					for (Agent agent : conversation.getParticipatesTo_inverse()) {
						if (!agent.equals(me)) {
							title = agent.getName();

							peerId = agent.getUniqueId();
							peerId = peerId.substring(peerId.indexOf('_') + 1);

							if (title == null) {
								title = peerId;
							}
						}
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

	public void onClickSend(View view) {
		String reply = getCtrlText(R.id.reply);
		if (reply.length() > 0) {

			try {
				Person me = (Person) getSAM().getMe();
				Message message = getSAM().createMessage();

				message.setTime(System.currentTimeMillis());
				message.setTitle(conversationTitle);
				message.setContent(reply);

				me.addCreator(message);

				conversation.addContains(message);

				// send notification
				execute(new Job() {
					@Override
					public void doWork() {
						getCOMM().sendNotify(peerId);
					}
				});

				// ui enhancements;
				setCtrlText(R.id.reply, "");
				findViewById(R.id.reply).clearFocus();

				refreshUI(null);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			Toast.makeText(this, R.string.feedback_content_empty,
					Toast.LENGTH_SHORT).show();
		}
	}
}
