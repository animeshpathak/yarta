package fr.inria.arles.yarta.android.library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;
import fr.inria.arles.yarta.knowledgebase.MSEResource;
import fr.inria.arles.yarta.resources.Agent;
import fr.inria.arles.yarta.resources.Conversation;
import fr.inria.arles.yarta.resources.ConversationImpl;
import fr.inria.arles.yarta.resources.Message;
import fr.inria.arles.yarta.resources.Person;
import fr.inria.arles.yarta.resources.PersonImpl;
import fr.inria.arles.yarta.resources.YartaResource;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class ConversationActivity extends BaseActivity {

	private static final int MENU_ADD = 1;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item = menu.add(0, MENU_ADD, 0, R.string.app_name);
		item.setIcon(R.drawable.icon_add);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ADD:
			onMenuAdd();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public static final String ConversationId = "ConversationId";

	private Conversation currentConversation;
	private Person me;

	private MessagesListAdapter adapter;

	private ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_conversation);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		initMSE();

		adapter = new MessagesListAdapter(this);

		list = (ListView) findViewById(R.id.listMessages);
		list.setAdapter(adapter);
	}

	@Override
	protected void refreshUI() {
		if (getIntent().hasExtra(ConversationId) && currentConversation == null) {
			currentConversation = new ConversationImpl(getSAM(),
					new MSEResource(getIntent().getStringExtra(ConversationId),
							Conversation.typeURI));
		}

		try {
			me = getSAM().getMe();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		execute(new Job() {

			@Override
			public void doWork() {
				if (currentConversation != null) {
					synchronized (messages) {
						messages.clear();
						messages.addAll(currentConversation.getMessages());

						// sort by time desc

						Map<Message, Long> msgTimes = new HashMap<Message, Long>();

						for (Message m : messages) {
							msgTimes.put(m, m.getTime());
						}

						for (int i = 0; i < messages.size() - 1; i++) {
							for (int j = i + 1; j < messages.size(); j++) {
								Message mi = messages.get(i);
								Message mj = messages.get(j);

								if (msgTimes.get(mi) > msgTimes.get(mj)) {
									messages.set(i, mj);
									messages.set(j, mi);
								}
							}
						}
					}
				}
			}

			@Override
			public void doUIAfter() {
				synchronized (messages) {
					adapter.setMessages(messages);
					list.post(new Runnable() {

						@Override
						public void run() {
							list.setSelection(messages.size() - 1);
						}
					});
				}
			}
		});
	}

	public void onMessageSend(View view) {
		String msg = getCtrlText(R.id.content);
		if (msg.length() == 0) {
			Toast.makeText(this,
					R.string.conversation_empty_content_not_allowed,
					Toast.LENGTH_LONG).show();
			return;
		}
		setCtrlText(R.id.content, "");

		if (currentConversation == null) {
			currentConversation = getSAM().createConversation();
			me.addParticipatesTo(currentConversation);
		}

		Message message = getSAM().createMessage();
		me.addCreator(message);
		message.setTime(System.currentTimeMillis());
		message.setTitle(msg);

		currentConversation.addMessage(message);
		refreshUI();

		// notify peers
		for (Agent agent : currentConversation.getParticipatesTo_inverse()) {
			if (!agent.equals(me)) {
				Person p = new PersonImpl(getSAM(),
						((YartaResource) agent).getNode());
				String userId = p.getUserId();

				sendNotify(userId);
			}
		}
	}

	Set<Agent> agents;

	protected Set<Agent> getKnownAgents() {
		if (agents == null) {
			try {
				agents = getSAM().getMe().getKnows();
				agents.add(getSAM().getMe());
			} catch (Exception ex) {
				agents = null;
			}
		}

		return agents;
	}

	protected List<Person> getRemaniningBuddies() {
		List<Person> result = new ArrayList<Person>();
		Set<Agent> agents = getKnownAgents();

		for (Agent agent : agents) {
			Person p = (Person) agent;

			result.add(new PersonImpl(getSAM(), new MSEResource(
					p.getUniqueId(), Person.typeURI)));

		}

		if (currentConversation != null) {
			for (Agent a : currentConversation.getParticipatesTo_inverse()) {
				result.remove(a);
			}
		}

		return result;
	}

	protected void onMenuAdd() {
		execute(new Job() {

			String[] items;
			List<Person> people;

			@Override
			public void doWork() {
				people = getRemaniningBuddies();
				items = new String[people.size()];

				for (int i = 0; i < items.length; i++) {
					items[i] = people.get(i).getUserId();
				}
			}

			@Override
			public void doUIAfter() {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ConversationActivity.this);
				builder.setTitle(R.string.conversation_add_participant);
				builder.setSingleChoiceItems(items, -1,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();

								Person person = people.get(which);
								if (currentConversation == null) {
									currentConversation = getSAM()
											.createConversation();
									me.addParticipatesTo(currentConversation);
								}

								person.addParticipatesTo(currentConversation);
							}
						});

				AlertDialog dialog = builder.create();
				dialog.show();
			}
		});
	}

	private List<Message> messages = new ArrayList<Message>();
}
