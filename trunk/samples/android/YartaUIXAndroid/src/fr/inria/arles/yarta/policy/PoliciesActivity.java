package fr.inria.arles.yarta.policy;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import fr.inria.arles.yarta.conference.R;
import fr.inria.arles.yarta.core.YartaWrapper;
import fr.inria.arles.yarta.knowledgebase.interfaces.PolicyManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

public class PoliciesActivity extends FragmentActivity implements
		PoliciesFragment.OnRuleSelectedListener {

	public final static int MENU_NEW = 1;
	public final static int MENU_SEND = 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_policies);

		policyManager = YartaWrapper.getInstance().getPolicyManager();

		StaticInfo.getInstance().loadRelationships(this);

		listAdapter = new PoliciesListAdapter(this);
		listAdapter.setRules(getRulesFromPM());

		// Check whether the activity is using the layout version with
		// the fragment_container FrameLayout. If so, we must add the first
		// fragment
		if (findViewById(R.id.fragment_container) != null) {

			if (savedInstanceState != null) {
				return;
			}

			PoliciesFragment firstFragment = new PoliciesFragment();

			// In case this activity was started with special instructions from
			// an Intent,
			// pass the Intent's extras to the fragment as arguments
			firstFragment.setArguments(getIntent().getExtras());

			// Add the fragment to the 'fragment_container' FrameLayout
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragment_container, firstFragment).commit();
		}
	}

	@Override
	protected void onDestroy() {
		setRulesToPM(listAdapter.getRules());
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.activity_main, menu);

		menu.add(0, MENU_NEW, 0, R.string.rule_new);
		menu.add(0, MENU_SEND, 0, R.string.rule_send);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case MENU_NEW:
			onMenuNew();
			break;
		case MENU_SEND:
			onMenuSend();
			break;
		}
		return true;
	}

	private void onMenuNew() {
		listAdapter.addRule(new AccessRule());
		onRuleSelected(listAdapter.getCount() - 1);
	}

	private void onMenuSend() {
		setRulesToPM(listAdapter.getRules());
		try {
			PrintWriter writer = new PrintWriter(
					Environment.getExternalStorageDirectory() + "/policies");

			for (AccessRule rule : listAdapter.getRules()) {
				writer.println(rule.toString());
			}

			writer.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		Uri uri = Uri.fromFile(new File(Environment
				.getExternalStorageDirectory(), "policies"));
		Intent i = new Intent(Intent.ACTION_SEND);
		i.putExtra(Intent.EXTRA_SUBJECT, "Rules");
		i.putExtra(Intent.EXTRA_TEXT, "Rules attached.");
		i.putExtra(Intent.EXTRA_STREAM, uri);
		i.putExtra(Intent.EXTRA_EMAIL, new String[] { "george.rosca@inria.fr" });

		i.setType("text/plain");
		startActivity(Intent.createChooser(i, "Send mail"));
	}

	public void onRuleSelected(int position) {
		// The user selected the headline of an article from the
		// HeadlinesFragment

		// Capture the article fragment from the activity layout
		PolicyFragment articleFrag = (PolicyFragment) getSupportFragmentManager()
				.findFragmentById(R.id.article_fragment);

		if (articleFrag != null) {
			// If article frag is available, we're in two-pane layout...

			// Call a method in the ArticleFragment to update its content
			articleFrag.loadRuleUI(position);

		} else {
			// If the frag is not available, we're in the one-pane layout and
			// must swap frags...

			// Create fragment and give it an argument for the selected article
			PolicyFragment newFragment = new PolicyFragment();
			Bundle args = new Bundle();
			args.putInt(PolicyFragment.ARG_POSITION, position);
			newFragment.setArguments(args);
			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();

			// Replace whatever is in the fragment_container view with this
			// fragment,
			// and add the transaction to the back stack so the user can
			// navigate back
			transaction.replace(R.id.fragment_container, newFragment);
			transaction.addToBackStack(null);

			// Commit the transaction
			transaction.commit();
		}
	}

	private List<AccessRule> getRulesFromPM() {
		List<AccessRule> rules = new ArrayList<AccessRule>();

		if (policyManager != null) {
			for (int i = 0; i < policyManager.getRulesCount(); i++) {
				rules.add(new AccessRule(policyManager.getRule(i)));
			}
		}

		return rules;
	}

	private void setRulesToPM(List<AccessRule> rules) {
		if (policyManager == null) {
			return;
		}
		for (int i = policyManager.getRulesCount() - 1; i >= 0; i--) {
			policyManager.removeRule(i);
		}

		for (AccessRule rule : rules) {
			policyManager.addRule(rule.toString());
		}
	}

	private PolicyManager policyManager;
	private PoliciesListAdapter listAdapter;
}