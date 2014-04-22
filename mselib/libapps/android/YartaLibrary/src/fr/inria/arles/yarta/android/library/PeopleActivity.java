package fr.inria.arles.yarta.android.library;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import fr.inria.arles.yarta.R;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;
import fr.inria.arles.yarta.resources.Agent;
import fr.inria.arles.yarta.resources.Person;
import fr.inria.arles.yarta.resources.PersonImpl;
import fr.inria.arles.yarta.resources.YartaResource;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class PeopleActivity extends BaseActivity implements
		AdapterView.OnItemClickListener, PersonAddDialog.Handler {

	private static final int MENU_QR = 1;
	private static final int MENU_ADD = 2;
	private static final int ID_FROM_BARCODE_INTENT_REQUEST = 1;

	private List<Person> persons = new ArrayList<Person>();
	private PeopleListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_people);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		adapter = new PeopleListAdapter(this);

		ListView list = (ListView) findViewById(R.id.listPeople);
		list.setAdapter(adapter);
		list.setEmptyView(findViewById(R.id.listPeopleEmpty));
		list.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Person p = persons.get(position);
		Intent intent = new Intent(this, PersonActivity.class);
		intent.putExtra(PersonActivity.PersonGUID, p.getUniqueId());
		startActivity(intent);
	}

	@Override
	protected void refreshUI() {
		super.refreshUI();

		execute(new Job() {
			@Override
			public void doWork() {
				try {
					Set<Agent> friends = getSAM().getMe().getKnows();

					persons.clear();

					Person me = getSAM().getMe();
					persons.add(me);
					for (Agent agent : friends) {
						if (agent.equals(me)) {
							continue;
						} else {
							Person p = new PersonImpl(getSAM(),
									((YartaResource) agent).getNode());
							persons.add(p);
						}
					}
				} catch (Exception ex) {
					logError("refreshUI error: %s", ex.getMessage());
				}
			}

			@Override
			public void doUIAfter() {
				adapter.setPeople(persons);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item = menu.add(0, MENU_QR, 0, R.string.people_qr);
		item.setIcon(R.drawable.icon_qr);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		item = menu.add(0, MENU_ADD, 0, R.string.people_add);
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
		PersonAddDialog dialog = new PersonAddDialog(this);
		dialog.setHandler(this);
		dialog.setPersons(getSAM().getAllPersons());
		dialog.show();
	}

	@Override
	public void onScanQR() {
		try {
			Intent getIdFromBarcodeintent = new Intent(
					"com.google.zxing.client.android.SCAN");
			getIdFromBarcodeintent.putExtra("SCAN_MODE", "QR_CODE_MODE");
			startActivityForResult(getIdFromBarcodeintent,
					ID_FROM_BARCODE_INTENT_REQUEST);
		} catch (Exception ex) {
			Toast.makeText(this, R.string.people_no_zing, Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	public void onAdd(String name) {
		addPerson(name);
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
			Toast.makeText(this, R.string.people_no_zing, Toast.LENGTH_LONG)
					.show();
		}
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
					Toast.makeText(getApplicationContext(),
							R.string.people_hello_error, Toast.LENGTH_LONG)
							.show();
				} else {
					Toast.makeText(getApplicationContext(),
							R.string.people_hello_ok, Toast.LENGTH_LONG).show();
				}

				refreshUI();
			}
		});
	}
}
