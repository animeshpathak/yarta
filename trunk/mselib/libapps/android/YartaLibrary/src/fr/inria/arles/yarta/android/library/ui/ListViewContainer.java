package fr.inria.arles.yarta.android.library.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * This mimics the ListView functionality without the scrolling part.
 */
public class ListViewContainer {

	private ViewGroup itemsContainer;
	private View emptyView;
	private BaseAdapter adapter;

	public ListViewContainer(BaseAdapter adapter, ViewGroup itemsContainer,
			View emptyView) {
		this.adapter = adapter;
		this.itemsContainer = itemsContainer;
		this.emptyView = emptyView;
	}

	public void update() {
		int count = adapter.getCount();

		boolean noItems = count == 0;

		if (noItems) {
			itemsContainer.setVisibility(View.GONE);

			if (emptyView != null) {
				emptyView.setVisibility(View.VISIBLE);
			}
		} else {
			itemsContainer.setVisibility(View.VISIBLE);

			if (emptyView != null) {
				emptyView.setVisibility(View.GONE);
			}

			itemsContainer.removeAllViews();

			for (int i = 0; i < count; i++) {
				itemsContainer
						.addView(adapter.getView(i, null, itemsContainer));
			}
		}
	}

}
