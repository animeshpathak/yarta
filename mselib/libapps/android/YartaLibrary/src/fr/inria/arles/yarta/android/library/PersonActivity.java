package fr.inria.arles.yarta.android.library;

import java.util.Set;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import fr.inria.arles.yarta.R;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;
import fr.inria.arles.yarta.knowledgebase.MSEResource;
import fr.inria.arles.yarta.resources.Agent;
import fr.inria.arles.yarta.resources.Conversation;
import fr.inria.arles.yarta.resources.Person;
import fr.inria.arles.yarta.resources.PersonImpl;
import fr.inria.arles.yarta.resources.Place;
import fr.inria.arles.yarta.resources.YartaResource;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PersonActivity extends BaseActivity {

	public static final String PersonGUID = "PersonGUID";

	private static final int MENU_ACCEPT = 1;
	private static final int MENU_UPDATE = 2;

	private String[] items;
	private Place[] itemPlaces;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_person);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void refreshUI() {
		Set<Place> places = getSAM().getAllPlaces();
		items = new String[places.size()];
		itemPlaces = new Place[places.size()];

		int c = 0;
		for (Place place : places) {
			items[c] = place.getName();
			itemPlaces[c] = place;
			c++;
		}

		Spinner sp = (Spinner) findViewById(R.id.location);
		sp.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, items));

		loadInformation();
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

	public void onClickAddFriend(View view) {
		addPerson(currentPerson.getUserId());
	}

	public void onClickSendMessage(View view) {
		try {
			Person me = getSAM().getMe();

			Conversation conversation = null;

			Set<Conversation> conversations = me.getParticipatesTo();

			for (Conversation c : conversations) {
				Set<Agent> participants = c.getParticipatesTo_inverse();
				if (participants.size() == 2
						&& participants.contains(currentPerson)) {
					conversation = c;
					break;
				}
			}

			if (conversation == null) {
				// create the conversation
				conversation = getSAM().createConversation();
				currentPerson.addParticipatesTo(conversation);
				me.addParticipatesTo(conversation);
			}

			Intent intent = new Intent(this, ConversationActivity.class);
			intent.putExtra(ConversationActivity.ConversationId,
					conversation.getUniqueId());

			startActivity(intent);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private boolean readOnly = false;

	private void enableItem(int id, boolean enable) {
		View view = findViewById(id);
		if (view instanceof TextView) {
			view.setFocusable(enable);
		} else {
			view.setEnabled(enable);
		}
	}

	private Person currentPerson;

	private void loadInformation() {
		currentPerson = null;
		Intent intent = getIntent();

		if (intent.hasExtra(PersonActivity.PersonGUID)) {
			String userGUID = intent.getStringExtra(PersonActivity.PersonGUID);
			currentPerson = new PersonImpl(getSAM(), new MSEResource(userGUID,
					Person.typeURI));

			try {
				readOnly = !currentPerson.equals(getSAM().getMe());
			} catch (Exception ex) {
				readOnly = true;
			}

			// disable anything
			enableItem(R.id.email, !readOnly);
			enableItem(R.id.location, !readOnly);
		} else {
			try {
				currentPerson = getSAM().getMe();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		try {
			String userId = currentPerson.getUserId();
			userId = userId.substring(0, userId.indexOf('@'));
			setCtrlText(R.id.profileid, String.format(
					getString(R.string.person_profile_id), userId));
			setCtrlText(R.id.email, currentPerson.getEmail());

			Set<Place> places = currentPerson.getIsLocated();

			for (Place place : places) {
				String placeName = place.getName();

				for (int c = 0; c < items.length; c++) {
					if (items[c].endsWith(placeName)) {
						Spinner sp = (Spinner) findViewById(R.id.location);
						sp.setSelection(c);
						break;
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private boolean saveInformation() {
		if (readOnly) {
			return false;
		}

		try {
			// TODO: only if changed
			Person p = new PersonImpl(getSAM(), ((YartaResource) getSAM()
					.getMe()).getNode());

			p.setEmail(getCtrlText(R.id.email));

			Spinner sp = (Spinner) findViewById(R.id.location);

			if (sp.getSelectedItemPosition() != -1) {
				Place place = itemPlaces[sp.getSelectedItemPosition()];
				p.addIsLocated(place);
			}

			// TODO: send notify to inria serv.
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!readOnly) {
			MenuItem item = menu.add(0, MENU_ACCEPT, 0, R.string.person_save);

			item.setIcon(R.drawable.icon_accept);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		} else {
			MenuItem item = menu.add(0, MENU_UPDATE, 0, R.string.person_update);

			item.setIcon(R.drawable.icon_sync);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ACCEPT:
			if (saveInformation()) {
				finish();
			}
			break;

		case MENU_UPDATE:
			execute(new Job() {

				boolean success;

				@Override
				public void doWork() {
					try {
						success = getCOMM().sendUpdateRequest(
								currentPerson.getUserId()) != -1;
					} catch (Exception ex) {
						success = false;
					}
				}

				@Override
				public void doUIAfter() {
					Toast.makeText(
							getApplicationContext(),
							success ? R.string.person_update_success
									: R.string.person_update_error,
							Toast.LENGTH_SHORT).show();
				}
			});
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
