package fr.inria.arles.yarta.android.library;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;
import fr.inria.arles.iris.R;
import fr.inria.arles.iris.web.ObjectItem;
import fr.inria.arles.yarta.android.library.util.BaseFragment;
import fr.inria.arles.yarta.android.library.util.GenericPageAdapter;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;

public class SearchFragment extends BaseFragment implements
		View.OnClickListener, TextView.OnEditorActionListener {

	private GenericPageAdapter adapter;
	private TabHost tabHost;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater
				.inflate(R.layout.fragment_search, container, false);

		adapter = new GenericPageAdapter(getSherlockActivity()
				.getSupportFragmentManager(), getSherlockActivity());

		SearchResultsFragment fragmentResults = new SearchResultsFragment();
		fragmentResults.setRunner(runner);
		fragmentResults.setType(ObjectItem.User);
		adapter.addFragment(fragmentResults, R.string.search_users);

		fragmentResults = new SearchResultsFragment();
		fragmentResults.setRunner(runner);
		fragmentResults.setType(ObjectItem.Group);
		adapter.addFragment(fragmentResults, R.string.search_groups);

		fragmentResults = new SearchResultsFragment();
		fragmentResults.setRunner(runner);
		fragmentResults.setType(ObjectItem.Blog);
		adapter.addFragment(fragmentResults, R.string.search_blogs);

		tabHost = (TabHost) root.findViewById(android.R.id.tabhost);
		tabHost.setup();
		TabSpec tab1 = tabHost.newTabSpec("0");
		TabSpec tab2 = tabHost.newTabSpec("1");
		TabSpec tab3 = tabHost.newTabSpec("2");

		tab1.setIndicator(createTabView(R.string.search_users));
		tab1.setContent(R.id.container);

		tab2.setIndicator(createTabView(R.string.search_groups));
		tab2.setContent(R.id.container);

		tab3.setIndicator(createTabView(R.string.search_blogs));
		tab3.setContent(R.id.container);

		tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				tabHost.clearFocus();
				int position = tabHost.getCurrentTab();
				Fragment fragment = adapter.getItem(position);
				FragmentTransaction ft = getSherlockActivity()
						.getSupportFragmentManager().beginTransaction();
				if (!fragment.isAdded()) {
					ft.replace(R.id.container, fragment);
					ft.commit();
				}
			}
		});

		tabHost.addTab(tab1);
		tabHost.addTab(tab2);
		tabHost.addTab(tab3);

		// ViewPager pager = (ViewPager) root.findViewById(R.id.pager);
		// pager.setAdapter(adapter);

		root.findViewById(R.id.search).setOnClickListener(this);

		EditText edit = (EditText) root.findViewById(R.id.query);
		edit.setOnEditorActionListener(this);
		return root;
	}

	@Override
	public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_SEARCH) {
			onClickSearch();
		}
		return false;
	}

	private View createTabView(int textId) {
		LayoutInflater inflater = getSherlockActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.item_tab, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(textId);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.search:
			onClickSearch();
			break;
		}
	}

	@Override
	public void refreshUI() {
	}

	private void onClickSearch() {
		final String query = getCtrlText(R.id.query);

		if (query.length() == 0) {
			Toast.makeText(getSherlockActivity(),
					R.string.search_empty_content, Toast.LENGTH_LONG).show();
		} else {
			runner.runBackground(new Job() {

				List<ObjectItem> items;

				@Override
				public void doWork() {
					items = client.search(query);
				}

				@Override
				public void doUIAfter() {
					View query = getView().findViewById(R.id.query);

					InputMethodManager imm = (InputMethodManager) getSherlockActivity()
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(query.getWindowToken(), 0);

					for (int i = 0; i < adapter.getCount(); i++) {
						SearchResultsFragment srf = (SearchResultsFragment) adapter
								.getItem(i);
						srf.setData(items);
					}
				}
			});
		}
	}
}
