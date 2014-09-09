package fr.inria.arles.callouts;

import fr.inria.arles.yarta.knowledgebase.MSEResource;
import fr.inria.arles.yarta.resources.Agent;
import fr.inria.arles.yarta.resources.Content;
import fr.inria.arles.yarta.resources.ContentImpl;
import fr.inria.arles.yarta.resources.Person;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class CalloutActivity extends BaseActivity {

	public static final String CalloutId = "CalloutId";
	private static final int MENU_JOIN = 1;

	private CalloutsListAdapter adapter;
	private Content content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_callout);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		content = new ContentImpl(getSAM(), new MSEResource(getIntent()
				.getStringExtra(CalloutId), Content.typeURI));

		adapter = new CalloutsListAdapter(this);

		ListView list = (ListView) findViewById(R.id.listComments);
		list.setAdapter(adapter);
		list.setEmptyView(findViewById(R.id.none));
		refreshUI();
	}

	@Override
	protected void refreshUI() {
		setTitle(content.getTitle());
		setText(R.id.description, content.getContent());

		for (Agent agent : content.getCreator_inverse()) {
			setText(R.id.author, agent.getName());
			break;
		}
		adapter.setItems(content.getHasReply());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item = menu.add(0, MENU_JOIN, 0, R.string.callout_join);
		item.setIcon(R.drawable.icon_join);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_JOIN:
			onClickJoin();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void onClickJoin() {
		try {
			Person me = getSAM().getMe();

			Content c = getSAM().createContent();
			c.setTime(System.currentTimeMillis());
			c.setTitle(getString(R.string.callout_join_text));
			me.addCreator(c);

			content.addHasReply(c);
		} catch (Exception ex) {
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		}
	}

	public void onClickAuthor(View view) {
		for (Agent agent : content.getCreator_inverse()) {
			String authorId = agent.getUniqueId();
			authorId = authorId.substring(authorId.indexOf('_') + 1);

			Intent intent = new Intent("Yarta.Profile");
			intent.putExtra("UserName", authorId);
			startActivity(intent);
			break;
		}
	}
}
