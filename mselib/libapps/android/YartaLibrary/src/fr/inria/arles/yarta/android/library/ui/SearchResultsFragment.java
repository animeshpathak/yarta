package fr.inria.arles.yarta.android.library.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import fr.inria.arles.iris.R;
import fr.inria.arles.iris.web.ObjectItem;
import fr.inria.arles.yarta.android.library.resources.Group;
import fr.inria.arles.yarta.android.library.util.BaseFragment;
import fr.inria.arles.yarta.android.library.util.ImageCache;

public class SearchResultsFragment extends BaseFragment implements
		AdapterView.OnItemClickListener {

	private SearchListAdapter adapter;
	private String type;
	private List<ObjectItem> items = new ArrayList<ObjectItem>();

	private Handler handler = new Handler();
	private Runnable refreshListAdapter = new Runnable() {

		@Override
		public void run() {
			adapter.notifyDataSetChanged();
		}
	};

	private Runnable lazyImageLoader = new Runnable() {

		@Override
		public void run() {
			try {
				for (ObjectItem item : items) {
					if (item.getType().equals(ObjectItem.Object))
						continue;
					ImageCache.getBitmap(item);
					handler.post(refreshListAdapter);
				}
			} catch (Exception ex) {
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_search_results,
				container, false);

		adapter = new SearchListAdapter(getSherlockActivity());

		ListView list = (ListView) root.findViewById(R.id.listResults);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		list.setEmptyView(root.findViewById(R.id.listEmpty));

		fillAdapter();

		return root;
	}

	@Override
	public void refreshUI(String notification) {
	}

	// sets the type of resources displayed
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Safe loads a string. If not attached or error return empty string.
	 * 
	 * @param stringId
	 * @return
	 */
	private String get(int stringId) {
		try {
			return getActivity().getString(stringId);
		} catch (Exception ex) {
			// not attached to an activity;
		}
		return "";
	}

	private String getTypeName(String type) {
		if (type.equals(ObjectItem.User)) {
			return get(R.string.search_users);
		} else if (type.equals(ObjectItem.Group)) {
			return get(R.string.search_groups);
		} else if (type.equals(ObjectItem.File)) {
			return get(R.string.search_files);
		} else if (type.equals(ObjectItem.Blog)) {
			return get(R.string.search_blogs);
		} else if (type.equals(ObjectItem.Topic)) {
			return get(R.string.search_discussions);
		}
		return null;
	}

	// sets the raw data which will be filtered
	public void setData(List<ObjectItem> items) {
		// this is the all result fragment
		if (type.equals(ObjectItem.Object)) {
			synchronized (this.items) {
				this.items.clear();
				String currentType = null;

				for (ObjectItem item : items) {
					if (!item.getType().equals(currentType)) {
						this.items.add(new ObjectItem(null, getTypeName(item
								.getType()), null, ObjectItem.Object));
						currentType = item.getType();
					}
					this.items.add(item);
				}
			}
			fillAdapter();
		} else {
			if (items != null) {
				synchronized (this.items) {
					this.items.clear();
					for (ObjectItem item : items) {
						if (item.getType().equals(type)) {
							this.items.add(item);
						}
					}
				}

				fillAdapter();
			}
		}
	}

	public void fillAdapter() {
		if (adapter != null) {
			synchronized (items) {
				adapter.setItems(items);
			}

			new Thread(lazyImageLoader).start();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		ObjectItem item = (ObjectItem) adapter.getItem(position);
		Intent intent = null;
		String type = item.getType();
		if (type.equals(ObjectItem.User)) {
			intent = new Intent(getSherlockActivity(), ProfileActivity.class);
			intent.putExtra(ProfileActivity.UserGuid, item.getGuid());
		} else if (type.equals(ObjectItem.Group)) {
			intent = new Intent(getSherlockActivity(), GroupActivity.class);
			String groupId = Group.typeURI + "_" + item.getGuid();
			intent.putExtra(GroupActivity.GroupGuid, groupId);
		} else if (type.equals(ObjectItem.Blog)) {
			intent = new Intent(getSherlockActivity(), BlogActivity.class);
			intent.putExtra(BlogActivity.BlogGuid, item.getGuid());
		} else if (type.equals(ObjectItem.Topic)) {
			intent = new Intent(getSherlockActivity(), ContentActivity.class);
			intent.putExtra(ContentActivity.PostGuid, item.getGuid());
		} else if (type.equals(ObjectItem.File)) {
			intent = new Intent(getSherlockActivity(), FileActivity.class);
			intent.putExtra(FileActivity.FileGuid, item.getGuid());
		}

		if (intent != null) {
			startActivity(intent);
		}
	}
}
