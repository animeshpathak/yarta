package fr.inria.arles.yarta.android.library;

import java.text.SimpleDateFormat;
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
import fr.inria.arles.yarta.android.library.web.MessageItem;
import fr.inria.arles.yarta.android.library.web.UserItem;
import fr.inria.arles.yarta.android.library.web.WebClient;

import fr.inria.arles.yarta.android.library.util.JobRunner.Job;

public class MessageActivity extends BaseActivity {

	public static final String Message = "Message";
	public static final String Reply = "Reply";
	public static final String User = "User";

	private static final int MENU_SEND = 1;
	private static final int MENU_REPLY = 2;

	private MessageItem message;
	private MessageItem reply;

	private List<UserItem> friends;
	
	private WebClient client = WebClient.getInstance();

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

	@Override
	public void refreshUI() {
		if (getIntent().hasExtra(Message)) {
			message = (MessageItem) getIntent().getSerializableExtra(Message);
			loadMessage();
		} else {
			// load friends in to spinner;
			runner.runBackground(new Job() {

				private UserItem item;

				@Override
				public void doWork() {
					friends = client.getFriends(client.getUsername());

					if (getIntent().hasExtra(Reply)) {
						reply = (MessageItem) getIntent().getSerializableExtra(
								Reply);
						friends.add(reply.getFrom());
					} else if (getIntent().hasExtra(User)) {
						item = (UserItem) getIntent()
								.getSerializableExtra(User);
						friends.add(item);
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
						setCtrlText(R.id.subject, "RE: " + reply.getSubject());
						setSelectedUser(reply.getFrom());
					} else if (item != null) {
						setSelectedUser(item);
					}
				}
			});
		}
	}

	private void setSelectedUser(UserItem user) {
		Spinner spinner = (Spinner) findViewById(R.id.peer);
		for (int i = 0; i < friends.size(); i++) {
			if (friends.get(i).getUsername().equals(user.getUsername())) {
				spinner.setSelection(i);
			}
		}
	}

	private void loadMessage() {
		findViewById(R.id.read).setVisibility(View.VISIBLE);
		findViewById(R.id.compose).setVisibility(View.GONE);

		setCtrlText(R.id.subject, message.getSubject());
		setCtrlText(R.id.content, Html.fromHtml(message.getDescription()));

		setFocusable(R.id.subject, false);
		setFocusable(R.id.content, false);

		setCtrlText(R.id.author, message.getFrom().getName());

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM, HH:mm",
				Locale.getDefault());
		setCtrlText(R.id.time,
				sdf.format(new Date(message.getTimestamp() * 1000)));

		runner.runBackground(new Job() {
			@Override
			public void doWork() {
				client.readMessage(message);
			}
		});
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
		final UserItem item = friends.get(spinner.getSelectedItemPosition());
		final String subject = getCtrlText(R.id.subject);
		final String body = getCtrlText(R.id.content);
		final String replyTo = reply == null ? "0" : reply.getGuid();

		if (body.length() == 0 || replyTo.length() == 0) {
			Toast.makeText(this, R.string.message_empty_content_not_allowed,
					Toast.LENGTH_SHORT).show();
			return;
		}

		runner.runBackground(new Job() {

			int result = -1;

			@Override
			public void doWork() {
				result = client.sendMessage(item.getUsername(), subject, body,
						replyTo);
			}

			@Override
			public void doUIAfter() {
				if (result != WebClient.RESULT_OK) {
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
		intent.putExtra(MessageActivity.Reply, message);
		startActivity(intent);
		finish();
	}
}
