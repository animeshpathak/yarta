package fr.inria.arles.yarta.android.library;

import fr.inria.arles.yarta.R;
import android.os.Bundle;

public class GroupsActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groups);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
}
