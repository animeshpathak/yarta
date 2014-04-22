package fr.inria.arles.yarta.policy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.inria.arles.yarta.conference.R;
import fr.inria.arles.yarta.core.YartaWrapper;
import fr.inria.arles.yarta.resources.Resource;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class PolicyFragment extends Fragment implements
		AdapterView.OnItemSelectedListener, RadioGroup.OnCheckedChangeListener,
		View.OnClickListener {
	public final static String ARG_POSITION = "position";
	private int mCurrentPosition = -1;
	private StaticInfo staticInfo = StaticInfo.getInstance();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// If activity recreated (such as from screen rotate), restore
		// the previous article selection set by onSaveInstanceState().
		// This is primarily necessary when in the two-pane layout.
		if (savedInstanceState != null) {
			mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
		}

		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.rule_view, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();

		initUISpinners();

		RadioGroup group = (RadioGroup) getActivity().findViewById(
				R.id.ruleRequestor);
		group.setOnCheckedChangeListener(this);

		group = (RadioGroup) getActivity().findViewById(R.id.ruleLinkage);
		group.setOnCheckedChangeListener(this);

		View view = getActivity().findViewById(R.id.rule_save);
		view.setOnClickListener(this);

		// During startup, check if there are arguments passed to the fragment.
		// onStart is a good place to do this because the layout has already
		// been
		// applied to the fragment at this point so we can safely call the
		// method
		// below that sets the article text.
		Bundle args = getArguments();
		if (args != null) {
			// Set article based on argument passed in
			loadRuleUI(args.getInt(ARG_POSITION));
		} else if (mCurrentPosition != -1) {
			// Set article based on saved instance state defined during
			// onCreateView
			loadRuleUI(mCurrentPosition);
		}
	}

	public void onStop() {
		super.onStop();
	}

	private void initUISpinners() {
		setSpinnerListener(R.id.requestor_spinner_subject_class, this);
		setSpinnerListener(R.id.requestor_spinner_predicate, this);
		setSpinnerListener(R.id.requestor_spinner_object_class, this);

		setSpinnerListener(R.id.requested_spinner_subject_class, this);
		setSpinnerListener(R.id.requested_spinner_predicate, this);
		setSpinnerListener(R.id.requested_spinner_object_class, this);

		setSpinnerItems(R.id.requestor_spinner_subject_class,
				staticInfo.getAllClasses());
		setSpinnerItems(R.id.requestor_spinner_object_class,
				staticInfo.getAllClasses());

		setSpinnerItems(R.id.requested_spinner_subject_class,
				staticInfo.getAllClasses());
		setSpinnerItems(R.id.requested_spinner_object_class,
				staticInfo.getAllClasses());
	}

	private void setSpinnerListener(int spinnerId,
			AdapterView.OnItemSelectedListener listener) {
		Spinner spinner = (Spinner) getActivity().findViewById(spinnerId);
		spinner.setOnItemSelectedListener(listener);
	}

	private SparseArray<ArrayAdapter<String>> adapters = new SparseArray<ArrayAdapter<String>>();

	private ArrayAdapter<String> getAdapter(Spinner spinner) {
		ArrayAdapter<String> adapter = adapters.get(spinner.getId());
		if (adapter == null) {
			adapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_spinner_item);

			spinner.setAdapter(adapter);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			adapters.put(spinner.getId(), adapter);
		}
		return adapter;
	}

	// read settings
	public void loadRuleUI(int position) {
		mCurrentPosition = position;

		AccessRule rule = (AccessRule) PoliciesListAdapter.getInstance()
				.getItem(mCurrentPosition);
		setUIRight(rule.getRight());

		setUIRequestor(rule.getRequestor());

		setSpinnerSelectedItem(R.id.requestor_spinner_subject_class,
				rule.getRequestorSubject().className);
		setSpinnerSelectedItem(R.id.requestor_spinner_subject,
				rule.getRequestorSubject().guid);

		setSpinnerSelectedItem(R.id.requestor_spinner_predicate,
				rule.getRequestorPredicate());

		setSpinnerSelectedItem(R.id.requestor_spinner_object_class,
				rule.getRequestorObject().className);
		setSpinnerSelectedItem(R.id.requestor_spinner_object,
				rule.getRequestorObject().guid);

		setUILink(rule.getLink());

		setSpinnerSelectedItem(R.id.requested_spinner_subject_class,
				rule.getInfoSubject().className);
		setSpinnerSelectedItem(R.id.requested_spinner_subject,
				rule.getInfoSubject().guid);

		setSpinnerSelectedItem(R.id.requested_spinner_predicate,
				rule.getInfoPredicate());

		setSpinnerSelectedItem(R.id.requested_spinner_object_class,
				rule.getInfoObject().className);
		setSpinnerSelectedItem(R.id.requested_spinner_object,
				rule.getInfoObject().guid);
	}

	// save settings
	public void saveRuleUI() {
		AccessRule rule = (AccessRule) PoliciesListAdapter.getInstance()
				.getItem(mCurrentPosition);

		rule.setRight(getUIRight());

		rule.setRequestor(getUIRequestor());
		rule.setRequestorSubject(new AccessRule.Resource(
				getSpinnerSelectedItem(R.id.requestor_spinner_subject_class),
				getSpinnerSelectedItem(R.id.requestor_spinner_subject)));
		rule.setRequestorPredicate(getSpinnerSelectedItem(R.id.requestor_spinner_predicate));
		rule.setRequestorObject(new AccessRule.Resource(
				getSpinnerSelectedItem(R.id.requestor_spinner_object_class),
				getSpinnerSelectedItem(R.id.requestor_spinner_object)));

		rule.setLink(getUILink());

		rule.setInfoSubject(new AccessRule.Resource(
				getSpinnerSelectedItem(R.id.requested_spinner_subject_class),
				getSpinnerSelectedItem(R.id.requested_spinner_subject)));
		rule.setInfoPredicate(getSpinnerSelectedItem(R.id.requested_spinner_predicate));
		rule.setInfoObject(new AccessRule.Resource(
				getSpinnerSelectedItem(R.id.requested_spinner_object_class),
				getSpinnerSelectedItem(R.id.requested_spinner_object)));
	}

	public int getUILink() {
		RadioGroup group = (RadioGroup) getActivity().findViewById(
				R.id.ruleLinkage);
		int selectedId = group.getCheckedRadioButtonId();

		if (selectedId == R.id.do_not_link) {
			return AccessRule.LINK_NONE;
		} else if (selectedId == R.id.link_as_subject) {
			return AccessRule.LINK_AS_SUBJECT;
		} else if (selectedId == R.id.link_as_object) {
			return AccessRule.LINK_AS_OBJECT;
		}

		return -1;
	}

	public void setUILink(int link) {
		RadioGroup group = (RadioGroup) getActivity().findViewById(
				R.id.ruleLinkage);

		if (link == AccessRule.LINK_NONE) {
			group.check(R.id.do_not_link);
		} else if (link == AccessRule.LINK_AS_OBJECT) {
			group.check(R.id.link_as_object);
		} else if (link == AccessRule.LINK_AS_SUBJECT) {
			group.check(R.id.link_as_subject);
		}
	}

	// gets the requestor type from the UI
	public int getUIRequestor() {
		RadioGroup group = (RadioGroup) getActivity().findViewById(
				R.id.ruleRequestor);
		int selectedId = group.getCheckedRadioButtonId();

		if (selectedId == R.id.requestor_is_subject) {
			return AccessRule.SUBJECT;
		} else if (selectedId == R.id.requestor_is_object) {
			return AccessRule.OBJECT;
		} else if (selectedId == R.id.requestor_is_specific) {
			return AccessRule.SPECIFIC;
		}
		return -1;
	}

	// sets the requestor type in the UI
	public void setUIRequestor(int requestor) {
		RadioGroup group = (RadioGroup) getActivity().findViewById(
				R.id.ruleRequestor);

		if (requestor == AccessRule.SUBJECT) {
			group.check(R.id.requestor_is_subject);
		} else if (requestor == AccessRule.OBJECT) {
			group.check(R.id.requestor_is_object);
		} else if (requestor == AccessRule.SPECIFIC) {
			group.check(R.id.requestor_is_specific);
		}
	}

	// returns AccessRule.READ, etc.
	public int getUIRight() {
		RadioGroup group = (RadioGroup) getActivity().findViewById(
				R.id.ruleRight);
		int selectedId = group.getCheckedRadioButtonId();

		if (selectedId == R.id.right_read) {
			return AccessRule.READ;
		} else if (selectedId == R.id.right_add) {
			return AccessRule.ADD;
		} else if (selectedId == R.id.right_remove) {
			return AccessRule.REMOVE;
		}

		return -1;
	}

	// set the AccessRule.READ, etc.
	public void setUIRight(int right) {
		RadioGroup group = (RadioGroup) getActivity().findViewById(
				R.id.ruleRight);

		if (right == AccessRule.READ) {
			group.check(R.id.right_read);
		} else if (right == AccessRule.ADD) {
			group.check(R.id.right_add);
		} else if (right == AccessRule.REMOVE) {
			group.check(R.id.right_remove);
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rule_save:
			saveRuleUI();
			String message = String.format(
					getActivity().getString(R.string.rule_saved),
					mCurrentPosition);
			Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

			getActivity().onBackPressed();
			break;
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(ARG_POSITION, mCurrentPosition);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		String selected = (String) parent.getItemAtPosition(position);
		handleItemSelected(parent.getId(), selected);
	}

	private void handleItemSelected(int spinnerId, String selected) {
		switch (spinnerId) {
		case R.id.requestor_spinner_subject_class:
			updatePredicates(R.id.requestor_spinner_predicate, selected,
					getSpinnerSelectedItem(R.id.requestor_spinner_object_class));

			updateObjects(R.id.requestor_spinner_object_class, selected,
					getSpinnerSelectedItem(R.id.requestor_spinner_predicate));

			populateSpinner(R.id.requestor_spinner_subject, selected);
			break;
		case R.id.requestor_spinner_predicate:
			updateSubjects(R.id.requestor_spinner_subject_class, selected,
					getSpinnerSelectedItem(R.id.requestor_spinner_object_class));
			updateObjects(
					R.id.requestor_spinner_object_class,
					getSpinnerSelectedItem(R.id.requestor_spinner_subject_class),
					selected);
			break;

		case R.id.requestor_spinner_object_class:
			updateSubjects(R.id.requestor_spinner_subject_class,
					getSpinnerSelectedItem(R.id.requestor_spinner_predicate),
					selected);

			updatePredicates(
					R.id.requestor_spinner_predicate,
					getSpinnerSelectedItem(R.id.requestor_spinner_subject_class),
					selected);
			populateSpinner(R.id.requestor_spinner_object, selected);
			break;

		case R.id.requested_spinner_subject_class:
			updatePredicates(R.id.requested_spinner_predicate, selected,
					getSpinnerSelectedItem(R.id.requested_spinner_object_class));
			updateObjects(R.id.requested_spinner_object_class, selected,
					getSpinnerSelectedItem(R.id.requested_spinner_predicate));

			populateSpinner(R.id.requested_spinner_subject, selected);
			break;

		case R.id.requested_spinner_predicate:
			updateObjects(
					R.id.requested_spinner_object_class,
					getSpinnerSelectedItem(R.id.requested_spinner_subject_class),
					selected);
			updateSubjects(R.id.requested_spinner_subject_class, selected,
					getSpinnerSelectedItem(R.id.requested_spinner_object_class));
			break;
		case R.id.requested_spinner_object_class:
			updatePredicates(
					R.id.requested_spinner_predicate,
					getSpinnerSelectedItem(R.id.requested_spinner_subject_class),
					selected);

			updateSubjects(R.id.requested_spinner_subject_class,
					getSpinnerSelectedItem(R.id.requested_spinner_predicate),
					selected);

			populateSpinner(R.id.requested_spinner_object, selected);
			break;
		}
	}

	private void updatePredicates(int predicateSpinner, String subjectClass,
			String objectClass) {
		setSpinnerItems(predicateSpinner,
				staticInfo.getPredicates(subjectClass, objectClass));
	}

	private void updateObjects(int objectSpinner, String subjectClass,
			String predicate) {
		setSpinnerItems(objectSpinner,
				staticInfo.getAllObjects(subjectClass, predicate));
	}

	private void updateSubjects(int subjectSpinner, String predicate,
			String objectClass) {
		setSpinnerItems(subjectSpinner,
				staticInfo.getAllSubjects(predicate, objectClass));
	}

	private void setSpinnerItems(int spinnerId, List<String> items) {

		Spinner spinner = (Spinner) getActivity().findViewById(spinnerId);
		ArrayAdapter<String> adapter = getAdapter(spinner);

		if (adapter.getCount() == items.size()) {
			boolean same = true;
			for (int i = 0; i < items.size(); i++) {
				if (!adapter.getItem(i).equals(items.get(i))) {
					same = false;
					break;
				}
			}

			if (same) {
				return;
			}
		}

		// save the previous value
		String previousValue = (String) spinner.getSelectedItem();

		adapter.clear();
		for (String item : items) {

			if (previousValue != null) {
				if (item.contains(previousValue)) {
					previousValue = item;
				}
			}
			adapter.add(item);
		}
		adapter.notifyDataSetChanged();

		if (previousValue != null) {
			spinner.setSelection(adapter.getPosition(previousValue), false);
		} else {
			spinner.setSelection(0, false);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	@Override
	public void onCheckedChanged(RadioGroup rGroup, int checkedId) {
		int rButtonId = rGroup.getCheckedRadioButtonId();

		switch (rGroup.getId()) {
		case R.id.ruleLinkage:
			onRuleLinkageChanged(rGroup, rButtonId);
			break;
		case R.id.ruleRequestor:
			onRuleRequestorChanged(rGroup, rButtonId);
			break;
		}
	}

	@SuppressWarnings("unchecked")
	private void populateSpinner(final int spinnerId,
			final String requestedClass) {

		List<String> displayedItems = new ArrayList<String>();
		displayedItems.add(StaticInfo.ANY);
		if (requestedClass.equals("Agent") || requestedClass.equals("Person")
				|| requestedClass.equals(StaticInfo.ANY)) {
			displayedItems.add("Requestor");
		}

		try {

			List<Object> resources = new ArrayList<Object>();
			Method method = YartaWrapper.class.getMethod("getAll"
					+ requestedClass + "s");
			resources.addAll((Collection<? extends Resource>) method
					.invoke(YartaWrapper.getInstance()));

			for (Object object : resources) {
				String name = YartaWrapper.getInstance().getResName(object);
				displayedItems.add(name);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		setSpinnerItems(spinnerId, displayedItems);
	}

	public void setSpinnerSelectedItem(int spinnerId, int position) {
		Spinner s = (Spinner) getActivity().findViewById(spinnerId);
		s.setSelection(position, true);
	}

	public boolean setSpinnerSelectedItem(int spinnerId, String item) {
		if (item.equals("?req")) {
			item = "Requestor";
		}
		if (item.equals("?info")) {
			item = "*";
		}
		
		Spinner s = (Spinner) getActivity().findViewById(spinnerId);
		ArrayAdapter<String> adapter = getAdapter(s);

		boolean found = false;
		for (int i = 0; i < adapter.getCount(); i ++) {
			if (adapter.getItem(i).contains(item)) {
				found = true;
				s.setSelection(i, false);
				break;
			}
		}
		if (!found) {
			// hack: add the item, and select it
			adapter.add(item);
			adapter.notifyDataSetChanged();
			s.setSelection(adapter.getPosition(item));
		}

		return true;
	}

	public String getSpinnerSelectedItem(int spinnerId) {
		Spinner s = (Spinner) getActivity().findViewById(spinnerId);

		String selected = (String) s.getSelectedItem();

		if (selected != null) {
			int firstBrace = selected.indexOf("(");
			int secondBrace = selected.indexOf(")");

			if (firstBrace >= 0 && secondBrace >= 0) {
				return selected.substring(firstBrace + 1, secondBrace);
			}

			if (selected.equals("Requestor")) {
				return "?req";
			}
		}
		return selected;
	}

	public void setEnabled(int viewId, boolean enabled) {
		View v = getActivity().findViewById(viewId);
		v.setEnabled(enabled);
	}

	private void onRuleRequestorChanged(RadioGroup rGroup, int rButtonId) {
		if (rButtonId == R.id.requestor_is_specific) {
			setSpinnerSelectedItem(R.id.requestor_spinner_subject_class, "Person");
			
			setSpinnerSelectedItem(R.id.requestor_spinner_predicate, "*");
			
			setSpinnerSelectedItem(R.id.requestor_spinner_object, "*");
			setSpinnerSelectedItem(R.id.requestor_spinner_object_class, "*");
			
			setEnabled(R.id.requestor_spinner_subject, true);
			setEnabled(R.id.requestor_spinner_subject_class, false);
			
			setEnabled(R.id.requestor_spinner_predicate, false);
			
			setEnabled(R.id.requestor_spinner_object, false);
			setEnabled(R.id.requestor_spinner_object_class, false);
			
		} else if (rButtonId == R.id.requestor_is_object) {

			// enable subject
			setEnabled(R.id.requestor_spinner_subject_class, true);
			setEnabled(R.id.requestor_spinner_subject, true);
			setEnabled(R.id.requestor_spinner_predicate, true);

			// reset subject + predicate
			setSpinnerSelectedItem(R.id.requestor_spinner_predicate, 0);
			setSpinnerSelectedItem(R.id.requestor_spinner_subject, 0);
			setSpinnerSelectedItem(R.id.requestor_spinner_subject_class, 0);

			// set Person + ?req
			setSpinnerSelectedItem(R.id.requestor_spinner_object_class,
					"Person");
			setSpinnerSelectedItem(R.id.requestor_spinner_object, "?req");

			// disable object
			setEnabled(R.id.requestor_spinner_object_class, false);
			setEnabled(R.id.requestor_spinner_object, false);
		} else {
			// enable object
			setEnabled(R.id.requestor_spinner_object_class, true);
			setEnabled(R.id.requestor_spinner_object, true);
			setEnabled(R.id.requestor_spinner_predicate, true);

			// reset object + predicate
			setSpinnerSelectedItem(R.id.requestor_spinner_predicate, 0);
			setSpinnerSelectedItem(R.id.requestor_spinner_object, 0);
			setSpinnerSelectedItem(R.id.requestor_spinner_object_class, 0);

			// set Person + ?req
			setSpinnerSelectedItem(R.id.requestor_spinner_subject_class,
					"Person");
			setSpinnerSelectedItem(R.id.requestor_spinner_subject, "?req");

			// disable subject
			setEnabled(R.id.requestor_spinner_subject_class, false);
			setEnabled(R.id.requestor_spinner_subject, false);
		}
	}

	private void onRuleLinkageChanged(RadioGroup rGroup, int rButtonId) {

		// reset everything
		setSpinnerSelectedItem(R.id.requested_spinner_object_class,
				StaticInfo.ANY);
		setSpinnerSelectedItem(R.id.requested_spinner_subject_class,
				StaticInfo.ANY);
		setSpinnerSelectedItem(R.id.requested_spinner_predicate, StaticInfo.ANY);

		String cloneClass;
		String cloneObject;

		int reqType = getUIRequestor();

		if (reqType == AccessRule.OBJECT) {
			cloneClass = getSpinnerSelectedItem(R.id.requestor_spinner_subject_class);
			cloneObject = getSpinnerSelectedItem(R.id.requestor_spinner_subject);

			if (rButtonId == R.id.do_not_link) {
				setEnabled(R.id.requestor_spinner_subject, true);
				setEnabled(R.id.requestor_spinner_subject_class, true);
			} else {
				setEnabled(R.id.requestor_spinner_subject, false);
				setEnabled(R.id.requestor_spinner_subject_class, false);
			}
		} else {
			cloneClass = getSpinnerSelectedItem(R.id.requestor_spinner_object_class);
			cloneObject = getSpinnerSelectedItem(R.id.requestor_spinner_object);

			if (rButtonId == R.id.do_not_link) {
				setEnabled(R.id.requestor_spinner_object, true);
				setEnabled(R.id.requestor_spinner_object_class, true);
			} else {
				setEnabled(R.id.requestor_spinner_object, false);
				setEnabled(R.id.requestor_spinner_object_class, false);
			}
		}

		if (rButtonId == R.id.link_as_object) {
			if (setSpinnerSelectedItem(R.id.requested_spinner_object_class,
					cloneClass)
					&& setSpinnerSelectedItem(R.id.requested_spinner_object,
							cloneObject)) {
				setEnabled(R.id.requested_spinner_object_class, false);
				setEnabled(R.id.requested_spinner_object, false);
				setEnabled(R.id.requested_spinner_subject_class, true);
				setEnabled(R.id.requested_spinner_subject, true);
			} else {
				Toast.makeText(getActivity(), R.string.rule_error_linking,
						Toast.LENGTH_SHORT).show();
				rGroup.check(R.id.do_not_link);
			}
		} else if (rButtonId == R.id.link_as_subject) {
			if (setSpinnerSelectedItem(R.id.requested_spinner_subject_class,
					cloneClass)
					&& setSpinnerSelectedItem(R.id.requested_spinner_subject,
							cloneObject)) {
				setEnabled(R.id.requested_spinner_subject_class, false);
				setEnabled(R.id.requested_spinner_subject, false);
				setEnabled(R.id.requested_spinner_object_class, true);
				setEnabled(R.id.requested_spinner_object, true);
			} else {
				Toast.makeText(getActivity(), R.string.rule_error_linking,
						Toast.LENGTH_SHORT).show();
				rGroup.check(R.id.do_not_link);
			}
		} else if (rButtonId == R.id.do_not_link) {
			setEnabled(R.id.requested_spinner_object_class, true);
			setEnabled(R.id.requested_spinner_object, true);
			setEnabled(R.id.requested_spinner_subject_class, true);
			setEnabled(R.id.requested_spinner_subject, true);
		}
	}
}