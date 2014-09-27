package fr.inria.arles.yarta.android.library.ui;

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
import android.view.ViewGroup;
import android.widget.ImageView;
import fr.inria.arles.iris.R;
import fr.inria.arles.yarta.android.library.resources.Person;
import fr.inria.arles.yarta.android.library.resources.PersonImpl;
import fr.inria.arles.yarta.android.library.resources.Picture;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;
import fr.inria.arles.yarta.knowledgebase.MSEResource;
import fr.inria.arles.yarta.resources.Agent;
import fr.inria.arles.yarta.resources.Content;
import fr.inria.arles.yarta.resources.ContentImpl;

public class ContentActivity extends BaseActivity implements
		ContentListAdapter.Callback, ContentCommentDialog.Callback {

	public static final String PostGuid = "PostGuid";

	private static final int MENU_ADD = 1;

	private Content post;

	private ContentListAdapter adapter;
	private ListViewContainer list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		String postId = getIntent().getStringExtra(PostGuid);

		if (!postId.contains(Content.baseMSEURI)) {
			// it's comming from river
			postId = Content.typeURI + "_" + postId;
		}

		post = new ContentImpl(getSAM(), new MSEResource(postId,
				Content.typeURI));

		adapter = new ContentListAdapter(this);
		adapter.setCallback(this);

		list = new ListViewContainer(adapter,
				(ViewGroup) findViewById(R.id.itemsContainer), findViewById(R.id.listEmpty));

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
				list.update();
			}
		});
	}

	public void refreshUI() {
		if (post == null) {
			return;
		}
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
