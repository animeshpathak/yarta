package fr.inria.arles.yarta.android.library;

import java.util.List;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import fr.inria.arles.iris.R;
import fr.inria.arles.iris.web.ImageCache;
import fr.inria.arles.iris.web.MessageItem;
import fr.inria.arles.iris.web.UserItem;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;

public class MessagesActivity extends BaseActivity implements
		AdapterView.OnItemClickListener {

	public static final String Thread = "Thread";
	private static final int MENU_REPLY = 2;

	private MessagesListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messages);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		final List<MessageItem> messages = (List<MessageItem>) getIntent()
				.getSerializableExtra(Thread);
		if (messages.size() > 0) {
			setTitle(messages.get(0).getFrom().getName());
		}

		adapter = new MessagesListAdapter(this);
		adapter.setItems(messages);

		ListView list = (ListView) findViewById(R.id.listMessages);
		list.setAdapter(adapter);
		list.setSelection(messages.size() - 1);
		list.setOnItemClickListener(this);

		execute(new Job() {

			private Drawable me;
			private Drawable other;

			@Override
			public void doWork() {
				me = getDrawable(client.getUser(client.getUsername()));
				other = getDrawable(messages.get(0).getFrom());
			}

			private Drawable getDrawable(UserItem user) {
				String url = user.getAvatarURL();
				if (ImageCache.getDrawable(url) == null) {
					try {
						ImageCache.setDrawable(url,
								ImageCache.drawableFromUrl(url));
					} catch (Exception ex) {
					}
				}
				return ImageCache.getDrawable(url);
			}

			@Override
			public void doUIAfter() {
				adapter.setThumbnails(me, other);
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
		intent.putExtra(MessageActivity.Reply, (MessageItem) adapter.getItem(0));
		startActivity(intent);
		finish();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(this, MessageActivity.class);
		intent.putExtra(MessageActivity.Message,
				(MessageItem) adapter.getItem(position));
		startActivity(intent);
	}
}
