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
import fr.inria.arles.iris.web.ImageCache;
import fr.inria.arles.iris.web.ObjectItem;
import fr.inria.arles.yarta.android.library.resources.Group;
import fr.inria.arles.yarta.android.library.util.BaseFragment;

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
			for (ObjectItem item : items) {
				String url = item.getDescription();
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
	public void refreshUI() {
	}

	// sets the type of resources displayed
	public void setType(String type) {
		this.type = type;
	}

	// sets the raw data which will be filtered
	public void setData(List<ObjectItem> items) {

		// filter items
		this.items.clear();
		for (ObjectItem item : items) {
			if (item.getType().equals(type)) {
				this.items.add(item);
			}
		}

		fillAdapter();
	}

	public void fillAdapter() {
		if (adapter != null) {
			adapter.setItems(items);

			new Thread(lazyImageLoader).start();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		ObjectItem item = (ObjectItem) adapter.getItem(position);
		Intent intent = null;
		if (type.equals(ObjectItem.User)) {
			intent = new Intent(getSherlockActivity(), ProfileActivity.class);
			intent.putExtra(ProfileActivity.UserGuid, item.getGuid());
		} else if (type.equals(ObjectItem.Group)) {
			intent = new Intent(getSherlockActivity(), GroupActivity.class);
			String groupId = Group.typeURI + "_" + item.getGuid();
			intent.putExtra(GroupActivity.GroupGuid, groupId);
		} else if (type.equals(ObjectItem.Blog)) {
			intent = new Intent(getSherlockActivity(), ContentActivity.class);
			intent.putExtra(ContentActivity.PostGuid, item.getGuid());
		}

		if (intent != null) {
			startActivity(intent);
		}
	}
}
