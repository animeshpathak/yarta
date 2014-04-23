package fr.inria.arles.foosball;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import fr.inria.arles.foosball.R;
import fr.inria.arles.foosball.PlayersApp.Observer;
import fr.inria.arles.foosball.resources.Player;
import fr.inria.arles.foosball.resources.PlayerImpl;
import fr.inria.arles.foosball.util.JobRunner.Job;
import fr.inria.arles.yarta.knowledgebase.MSEResource;
import fr.inria.arles.yarta.resources.Agent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class BuddiesActivity extends BaseActivity implements
		AdapterView.OnItemClickListener, Observer, AddBuddyDialog.Handler {

	private static final int MENU_QR = 1;
	private static final int MENU_ADD = 2;
	private static final int ID_FROM_BARCODE_INTENT_REQUEST = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_buddies);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		players = new ArrayList<Player>();

		adapter = new PlayerListAdapter(this);

		ListView list = (ListView) findViewById(R.id.listBuddies);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);

		addObserver(this);
	}

	@Override
	protected void refreshUI() {
		refreshPlayersList();
	}

	@Override
	protected void onDestroy() {
		removeObserver(this);
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();

		refreshPlayersList();
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();

		ListView listPatients = (ListView) findViewById(R.id.listBuddies);
		listPatients.setEmptyView(findViewById(R.id.listBuddiesEmpty));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item = menu.add(0, MENU_QR, 0, R.string.buddies_qr);
		item.setIcon(R.drawable.icon_qr);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		item = menu.add(0, MENU_ADD, 0, R.string.buddies_add);
		item.setIcon(R.drawable.icon_add);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_QR:
			onQR();
			break;
		case MENU_ADD:
			onAdd();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void addPerson(final String partnerID) {
		execute(new Job() {
			boolean error = false;

			@Override
			public void doWork() {
				try {
					if (getCOMM().sendHello(partnerID) < 0) {
						throw new Exception();
					}
				} catch (Exception ex) {
					error = true;
				}
			}

			@Override
			public void doUIAfter() {
				if (error) {
					Toast.makeText(BuddiesActivity.this,
							R.string.buddies_hello_error, Toast.LENGTH_LONG)
							.show();
				} else {
					Toast.makeText(BuddiesActivity.this,
							R.string.buddies_hello_ok, Toast.LENGTH_LONG)
							.show();
				}

				refreshPlayersList();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ID_FROM_BARCODE_INTENT_REQUEST) {
			if (resultCode == RESULT_OK) {
				final String partnerID = data.getStringExtra("SCAN_RESULT");
				addPerson(partnerID);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void onAdd() {
		AddBuddyDialog dlg = new AddBuddyDialog(this);
		dlg.setPersons(getSAM().getAllPersons());
		dlg.setHandler(this);
		dlg.show();
	}

	private void onQR() {
		try {
			Intent showIdInBarcodeIntent = new Intent(
					"com.google.zxing.client.android.ENCODE");
			showIdInBarcodeIntent.putExtra("ENCODE_TYPE", "TEXT_TYPE");
			showIdInBarcodeIntent.putExtra("ENCODE_DATA", getSAM().getMe()
					.getUserId());
			startActivity(showIdInBarcodeIntent);
		} catch (Exception ex) {
			Toast.makeText(this, R.string.buddies_no_zing, Toast.LENGTH_LONG)
					.show();
		}
	}

	private void refreshPlayersList() {
		try {
			players.clear();

			Set<Agent> agents = getSAM().getMe().getKnows();

			for (Agent agent : agents) {
				players.add(new PlayerImpl(getSAM(), new MSEResource(agent
						.getUniqueId(), Player.typeURI)));

			}
		} catch (Exception ex) {
		}

		adapter.setItems(players);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Player selected = players.get(position);
		Intent intent = new Intent(this, PlayerActivity.class);
		intent.putExtra(PlayerActivity.PlayerGUID, selected.getUniqueId());
		startActivity(intent);
	}

	private List<Player> players;
	private PlayerListAdapter adapter;

	@Override
	public void onScanQR() {
		try {
			Intent getIdFromBarcodeintent = new Intent(
					"com.google.zxing.client.android.SCAN");
			getIdFromBarcodeintent.putExtra("SCAN_MODE", "QR_CODE_MODE");
			startActivityForResult(getIdFromBarcodeintent,
					ID_FROM_BARCODE_INTENT_REQUEST);
		} catch (Exception ex) {
			Toast.makeText(this, R.string.buddies_hello_error,
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onAdd(String name) {
		addPerson(name);
	}
}
