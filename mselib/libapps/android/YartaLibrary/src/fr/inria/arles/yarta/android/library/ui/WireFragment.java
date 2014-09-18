package fr.inria.arles.yarta.android.library.ui;

import java.util.List;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import fr.inria.arles.iris.R;
import fr.inria.arles.iris.web.ImageCache;
import fr.inria.arles.iris.web.UserItem;
import fr.inria.arles.iris.web.WireItem;
import fr.inria.arles.yarta.android.library.util.AlertDialog;
import fr.inria.arles.yarta.android.library.util.BaseFragment;
import fr.inria.arles.yarta.android.library.util.PullToRefreshListView;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;

public class WireFragment extends BaseFragment implements
		WireListAdapter.Callback, PullToRefreshListView.OnRefreshListener,
		AbsListView.OnScrollListener {

	private static final int MENU_ADD_WIRE = 1;

	private WireListAdapter adapter;
	private PullToRefreshListView list;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_wire, container, false);

		adapter = new WireListAdapter(getSherlockActivity());
		adapter.setCallback(this);

		list = (PullToRefreshListView) root.findViewById(R.id.listWire);
		list.setAdapter(adapter);
		list.setOnRefreshListener(this);
		list.setOnScrollListener(this);

		setHasOptionsMenu(true);
		return root;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		MenuItem item = menu.add(0, MENU_ADD_WIRE, 0, R.string.main_wire_add);
		item.setIcon(R.drawable.icon_add);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ADD_WIRE:
			onAddWire();
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
		if (!loadingMore) {
			refreshUI();
		}
	}

	private List<WireItem> items;

	@Override
	public void refreshUI() {
		runner.runBackground(new Job() {

			@Override
			public void doWork() {
				items = client.getWirePosts(0);
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
			try {
				for (WireItem item : items) {
					if (item.getAuthor() == null) {
						continue;
					}
					String url = item.getAuthor().getAvatarURL();
					if (ImageCache.getDrawable(url) == null) {
						try {
							ImageCache.setDrawable(url,
									ImageCache.drawableFromUrl(url));
							handler.post(refreshListAdapter);
						} catch (Exception ex) {
						}
					}
				}
			} catch (Exception ex) {
				// concurrent over wire items;
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
	public void onProfileClick(UserItem user) {
		Intent intent = new Intent(getSherlockActivity(), ProfileActivity.class);
		intent.putExtra(ProfileActivity.UserGuid, user.getGuid());
		startActivity(intent);
	}

	@Override
	public void onItemRemove(final String guid) {
		AlertDialog.show(getSherlockActivity(),
				getString(R.string.wire_remove_message),
				getString(R.string.wire_remove_title),
				getString(R.string.wire_remove_yes),
				getString(R.string.wire_remove_cancel),
				new AlertDialog.Handler() {

					@Override
					public void onOK() {
						removeWire(guid);
					}
				});
	}

	private void removeWire(final String guid) {
		runner.runBackground(new Job() {
			@Override
			public void doWork() {
				client.removeWire(guid);
			}

			@Override
			public void doUIAfter() {
				refreshUI();
			}
		});
	}

	private void onAddWire() {
		WireAddDialog dlg = new WireAddDialog(getSherlockActivity());
		dlg.setRunner(runner);
		dlg.setCallback(new WireAddDialog.Callback() {

			@Override
			public void onWireAdded() {
				refreshUI();
			}
		});
		dlg.show();
	}

	private boolean loadingMore = false;

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, final int totalItemCount) {
		if (firstVisibleItem + visibleItemCount == totalItemCount
				&& totalItemCount > 1 && !loadingMore) {
			loadingMore = true;
			runner.runBackground(new Job() {

				List<WireItem> moreItems;

				@Override
				public void doWork() {
					moreItems = client.getWirePosts(adapter.getCount());
				}

				@Override
				public void doUIAfter() {
					if (moreItems != null) {
						items.addAll(moreItems);
						adapter.appendItems(moreItems);
						list.onRefreshComplete();
						new Thread(lazyImageLoader).start();
					}
					loadingMore = false;
				}
			});
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}
}
