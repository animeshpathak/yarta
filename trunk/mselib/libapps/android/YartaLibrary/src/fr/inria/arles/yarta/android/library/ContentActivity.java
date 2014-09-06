package fr.inria.arles.yarta.android.library;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import fr.inria.arles.iris.R;
import fr.inria.arles.util.PullToRefreshListView;
import fr.inria.arles.yarta.android.library.resources.Person;
import fr.inria.arles.yarta.android.library.resources.PersonImpl;
import fr.inria.arles.yarta.android.library.resources.Picture;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;
import fr.inria.arles.yarta.knowledgebase.MSEResource;
import fr.inria.arles.yarta.resources.Agent;
import fr.inria.arles.yarta.resources.Content;
import fr.inria.arles.yarta.resources.ContentImpl;

public class ContentActivity extends BaseActivity implements
		PullToRefreshListView.OnRefreshListener, ContentListAdapter.Callback,
		ContentCommentDialog.Callback {

	public static final String PostGuid = "PostGuid";

	private static final int MENU_ADD = 1;

	private Content post;

	private ContentListAdapter adapter;
	private PullToRefreshListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		String postId = getIntent().getStringExtra(PostGuid);

		if (!postId.contains("_")) {
			postId = Content.typeURI + "_" + postId;
		}

		post = new ContentImpl(getSAM(), new MSEResource(postId,
				Content.typeURI));

		adapter = new ContentListAdapter(this, getSAM());
		adapter.setCallback(this);

		list = (PullToRefreshListView) findViewById(R.id.listComents);
		list.setAdapter(adapter);
		list.setOnRefreshListener(this);

		trackUI("PostView");
	}

	@Override
	protected void onResume() {
		super.onResume();

		refreshUI();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item = menu.add(0, MENU_ADD, 0, R.string.post_add_comment);
		item.setIcon(R.drawable.icon_add);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ADD:
			onAddComment();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void onAddComment() {
		ContentCommentDialog dlg = new ContentCommentDialog(this, post);
		dlg.setCallback(this);
		dlg.setSAM(getSAM());
		dlg.show();
	}

	private List<Content> items;

	private void refreshCommentsList() {
		runner.runBackground(new Job() {

			@Override
			public void doWork() {
				items = new ArrayList<Content>();
				items.addAll(post.getHasReply());

				MessagesActivity.sort(items, false);
			}

			@Override
			public void doUIAfter() {
				adapter.setItems(items);
				list.onRefreshComplete();
				setListViewHeightBasedOnChildren(list);

				boolean noItems = items.size() == 0;
				list.setVisibility(noItems ? View.GONE : View.VISIBLE);
				findViewById(R.id.listEmpty).setVisibility(
						noItems ? View.VISIBLE : View.GONE);
			}
		});
	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(),
				MeasureSpec.AT_MOST);
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
		listView.requestLayout();
		listView.postInvalidate();
	}

	public void refreshUI() {
		for (Agent agent : post.getCreator_inverse()) {
			setCtrlText(R.id.name, Html.fromHtml(agent.getName()));
			Person person = new PersonImpl(getSAM(), new MSEResource(
					agent.getUniqueId(), Person.typeURI));

			Bitmap bitmap = null;

			for (Picture picture : person.getPicture()) {
				bitmap = contentClient.getBitmap(picture);
			}

			ImageView image = (ImageView) findViewById(R.id.icon);

			if (bitmap != null) {
				image.setImageBitmap(bitmap);
			} else {
				image.setVisibility(View.GONE);
			}
		}

		if (post.getTitle() != null) {
			setCtrlText(R.id.title, Html.fromHtml(post.getTitle()));
		}

		if (post.getContent() != null) {
			setCtrlText(R.id.content, Html.fromHtml(post.getContent()));
		}

		try {
			setCtrlText(
					R.id.time,
					getString(R.string.topic_time)
							+ sdf.format(new Date(post.getTime())));
		} catch (Exception ex) {
		}

		refreshCommentsList();
	}

	@Override
	public void onRefresh() {
		refreshCommentsList();
	}

	@Override
	public void onClickProfile(Person item) {
		Intent intent = new Intent(this, ProfileActivity.class);
		intent.putExtra(ProfileActivity.UserName, item.getUserId());
		startActivity(intent);
	}

	@Override
	public void onClickPost(Content item) {
		// nothing happens, it's a comment
	}

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM, HH:mm",
			Locale.getDefault());

	@Override
	public void onCommentAdded() {
		refreshCommentsList();
	}
}
