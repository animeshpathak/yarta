package fr.inria.arles.foosball;

import java.util.Set;

import fr.inria.arles.yarta.resources.Person;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

public class AddBuddyDialog extends Dialog implements View.OnClickListener,
		TextWatcher {
	public interface Handler {
		public void onScanQR();

		public void onAdd(String name);
	}

	private Handler handler;

	public AddBuddyDialog(Context context) {
		super(context, R.style.AppDialog);
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setCancelable(true);
		setCanceledOnTouchOutside(true);

		setContentView(R.layout.dialog_add_player);
		setTitle(R.string.buddies_add);

		findViewById(R.id.cancel).setOnClickListener(this);
		findViewById(R.id.set).setOnClickListener(this);

		stopsAdapter = new ArrayAdapter<String>(getContext(),
				android.R.layout.simple_dropdown_item_1line);

		AutoCompleteTextView txt = (AutoCompleteTextView) findViewById(R.id.nickName);
		txt.addTextChangedListener(this);
		txt.setAdapter(stopsAdapter);
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		Set<Person> persons = getPersons();
		stopsAdapter.clear();

		for (Person person : persons) {
			String userId = person.getUserId();
			if (userId.contains(s.toString())) {
				stopsAdapter.add(userId);
			}
		}
	}

	private Set<Person> getPersons() {
		return persons;
	}

	public void setPersons(Set<Person> persons) {
		this.persons = persons;
	}

	Set<Person> persons;

	private ArrayAdapter<String> stopsAdapter;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.set:
			onSet();
			break;
		case R.id.cancel:
			onCancel();
			break;
		}
	}

	private void onSet() {
		TextView txt = (TextView) findViewById(R.id.nickName);

		String name = txt.getText().toString();
		if (name.length() > 0) {
			if (!name.contains("@inria.fr")) {
				name += "@inria.fr";
			}
			handler.onAdd(name);
			dismiss();
		}
	}

	private void onCancel() {
		handler.onScanQR();
		dismiss();
	}

}
