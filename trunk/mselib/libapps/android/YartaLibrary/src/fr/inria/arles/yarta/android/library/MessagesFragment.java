package fr.inria.arles.yarta.android.library;

import java.io.Serializable;
import java.util.List;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.web.ImageCache;
import fr.inria.arles.yarta.android.library.web.MessageItem;
import fr.inria.arles.yarta.android.library.web.UserItem;
import fr.inria.arles.yarta.android.library.web.WebClient;
import fr.inria.arles.yarta.android.library.util.BaseFragment;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;
import fr.inria.arles.yarta.android.library.util.PullToRefreshListView;

public class MessagesFragment extends BaseFragment implements
		MessageListAdapter.Callback, PullToRefreshListView.OnRefreshListener {

	private static final int MENU_COMPOSE = 1;

	private MessageListAdapter adapter;
	private PullToRefreshListView list;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_messages, container,
				false);

		adapter = new MessageListAdapter(getSherlockActivity());
		adapter.setCallback(this);

		list = (PullToRefreshListView) root.findViewById(R.id.listInbox);
		list.setAdapter(adapter);
		list.setOnRefreshListener(this);
		list.setEmptyView(root.findViewById(R.id.listEmpty));
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getSherlockActivity(),
						MessageActivity.class);
				intent.putExtra(MessageActivity.Message,
						(Serializable) adapter.getItem(position));
				startActivity(intent);
			}
		});

		setHasOptionsMenu(true);
		return root;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		MenuItem item = menu.add(0, MENU_COMPOSE, 0,
				R.string.main_message_compose);
		item.setIcon(R.drawable.icon_compose);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_COMPOSE:
			onCompose();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		super.onResume();

		refreshUI();
	}

	@Override
	public void onRefresh() {
		refreshUI();
	}

	private List<MessageItem> items;

	@Override
	public void refreshUI() {
		runner.runBackground(new Job() {

			@Override
			public void doWork() {
				items = client.getMessages();
			}

			@Override
			public void doUIAfter() {
				adapter.setItems(items);
				list.onRefreshComplete();
				new Thread(lazyImageLoader).start();
			}
		});
	}

	private Runnable lazyImageLoader = new Runnable() {

		@Override
		public void run() {
			for (MessageItem item : items) {
				String url = item.getFrom().getAvatarURL();
				if (ImageCache.getDrawable(url) == null) {
					try {
						ImageCache.setDrawable(url,
								ImageCache.drawableFromUrl(url));
						handler.post(refreshListAdapter);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	};

	private Handler handler = new Handler();
	private Runnable refreshListAdapter = new Runnable() {

		@Override
		public void run() {
			adapter.notifyDataSetChanged();
		}
	};
	
	@Override
	public void onProfileClick(UserItem item) {
		Intent intent = new Intent(getSherlockActivity(), ProfileActivity.class);
		intent.putExtra(ProfileActivity.UserName, item.getUsername());
		startActivity(intent);
	}

	@Override
	public void onRemove(final MessageItem item) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				getSherlockActivity());
		builder.setMessage(R.string.message_remove_message)
				.setPositiveButton(R.string.message_remove_yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								removeMessage(item);
								dialog.cancel();
							}
						})
				.setNegativeButton(R.string.message_remove_cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alert = builder.create();
		alert.setTitle(R.string.message_remove_title);
		alert.show();
	}

	private void removeMessage(final MessageItem item) {
		runner.runBackground(new Job() {

			int result = -1;

			@Override
			public void doWork() {
				result = client.removeMessage(item);
			}

			@Override
			public void doUIAfter() {
				if (result == WebClient.RESULT_OK) {
					refreshUI();
				} else {
					Toast.makeText(getSherlockActivity(),
							client.getLastError(), Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void onCompose() {
		Intent intent = new Intent(getSherlockActivity(), MessageActivity.class);
		startActivity(intent);
	}
}
