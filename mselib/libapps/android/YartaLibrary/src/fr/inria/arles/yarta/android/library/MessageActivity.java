package fr.inria.arles.yarta.android.library;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import fr.inria.arles.iris.R;
import fr.inria.arles.iris.web.ElggClient;
import fr.inria.arles.yarta.android.library.resources.Person;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;
import fr.inria.arles.yarta.knowledgebase.MSEResource;
import fr.inria.arles.yarta.resources.Agent;
import fr.inria.arles.yarta.resources.Conversation;
import fr.inria.arles.yarta.resources.Message;
import fr.inria.arles.yarta.resources.MessageImpl;

public class MessageActivity extends BaseActivity {

	public static final String MessageId = "MessageId";
	public static final String ReplyId = "ReplyId";
	public static final String UserId = "UserId";

	private static final int MENU_SEND = 1;
	private static final int MENU_REPLY = 2;

	private Message message;
	private Message reply;

	private List<Agent> friends;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onResume() {
		super.onResume();

		refreshUI();
	}

	/**
	 * Returns the peer for which to send the message
	 * 
	 * @param message
	 * @return
	 */
	private Agent getPeer(Message message, Agent me) {
		for (Conversation conversation : message.getContains_inverse()) {
			for (Agent agent : conversation.getParticipatesTo_inverse()) {
				if (!agent.equals(me)) {
					return agent;
				}
			}
		}
		return null;
	}

	@Override
	public void refreshUI() {
		if (getIntent().hasExtra(MessageId)) {
			String messageId = getIntent().getStringExtra(MessageId);
			message = new MessageImpl(getSAM(), new MSEResource(messageId,
					Message.typeURI));
			loadMessage();
		} else {
			runner.runBackground(new Job() {

				Agent peer;

				@Override
				public void doWork() {
					Person me = null;
					try {
						friends = new ArrayList<Agent>();
						me = getSAM().getMe();
						friends.addAll(me.getKnows_inverse());
					} catch (Exception ex) {
						ex.printStackTrace();
					}

					if (getIntent().hasExtra(ReplyId)) {
						String replyId = getIntent().getStringExtra(ReplyId);
						reply = new MessageImpl(getSAM(), new MSEResource(
								replyId, Message.typeURI));

						peer = getPeer(reply, me);
						friends.add(peer);
					} else if (getIntent().hasExtra(UserId)) {
						String userId = getIntent().getStringExtra(UserId);

						try {
							Person person = getSAM().getPersonByUserId(userId);
							friends.add(person);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}

				@Override
				public void doUIAfter() {
					Spinner spinner = (Spinner) findViewById(R.id.peer);

					String fa[] = new String[friends.size()];
					for (int i = 0; i < fa.length; i++) {
						fa[i] = friends.get(i).getName();
					}
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							getApplicationContext(), R.layout.item_spinner, fa);
					spinner.setAdapter(adapter);

					if (reply != null) {
						setCtrlText(R.id.subject,
								Html.fromHtml(reply.getTitle()));
						setSelectedUser(peer);
					}
				}
			});
		}
	}

	private void setSelectedUser(Agent user) {
		Spinner spinner = (Spinner) findViewById(R.id.peer);
		for (int i = 0; i < friends.size(); i++) {
			if (friends.get(i).equals(user)) {
				spinner.setSelection(i);
			}
		}
	}

	private void loadMessage() {
		findViewById(R.id.read).setVisibility(View.VISIBLE);
		findViewById(R.id.compose).setVisibility(View.GONE);

		setCtrlText(R.id.subject, Html.fromHtml(message.getTitle()));
		setCtrlText(R.id.content, Html.fromHtml(message.getContent()));

		setFocusable(R.id.subject, false);
		setFocusable(R.id.content, false);

		for (Agent agent : message.getCreator_inverse()) {
			setCtrlText(R.id.author, agent.getName());
		}

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM, HH:mm",
				Locale.getDefault());
		setCtrlText(R.id.time, sdf.format(new Date(message.getTime())));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (message == null) {
			MenuItem item = menu.add(0, MENU_SEND, 0, R.string.message_send);
			item.setIcon(R.drawable.icon_send);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		} else {
			MenuItem item = menu.add(0, MENU_REPLY, 0, R.string.message_reply);
			item.setIcon(R.drawable.icon_reply);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_SEND:
			onSend();
			break;
		case MENU_REPLY:
			onReply();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void onSend() {
		Spinner spinner = (Spinner) findViewById(R.id.peer);

		if (spinner.getSelectedItemPosition() == -1) {
			return;
		}
		final Agent item = friends.get(spinner.getSelectedItemPosition());
		final String subject = Html.toHtml(getCtrlHtml(R.id.subject));
		final String body = Html.toHtml(getCtrlHtml(R.id.content));

		String guid = reply.getUniqueId();
		guid = guid.substring(guid.indexOf('_') + 1);

		final String replyTo = reply == null ? "0" : guid;

		if (body.length() == 0 || replyTo.length() == 0) {
			Toast.makeText(this, R.string.message_empty_content_not_allowed,
					Toast.LENGTH_SHORT).show();
			return;
		}

		runner.runBackground(new Job() {

			int result = -1;

			@Override
			public void doWork() {
				String username = item.getUniqueId();
				username = username.substring(username.indexOf('_') + 1);
				result = client.sendMessage(username, subject, body, replyTo);
			}

			@Override
			public void doUIAfter() {
				if (result != ElggClient.RESULT_OK) {
					Toast.makeText(getApplicationContext(),
							client.getLastError(), Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(),
							R.string.message_sent_ok, Toast.LENGTH_SHORT)
							.show();
					finish();
				}
			}
		});
	}

	private void onReply() {
		Intent intent = new Intent(this, MessageActivity.class);
		intent.putExtra(MessageActivity.ReplyId, message.getUniqueId());
		startActivity(intent);
		finish();
	}
}
