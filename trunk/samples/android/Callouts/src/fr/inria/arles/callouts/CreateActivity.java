package fr.inria.arles.callouts;

import java.util.Calendar;

import fr.inria.arles.yarta.knowledgebase.MSEResource;
import fr.inria.arles.yarta.resources.Content;
import fr.inria.arles.yarta.resources.Group;
import fr.inria.arles.yarta.resources.GroupImpl;
import fr.inria.arles.yarta.resources.Person;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class CreateActivity extends BaseActivity {

	private static final int MENU_ACCEPT = 1;

	private Group group;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		EditText text = (EditText) findViewById(R.id.date);
		text.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus)
					return;
				onSetDate();
			}
		});

		text = (EditText) findViewById(R.id.time);
		text.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus)
					return;
				onSetTime();
			}
		});
	}

	private void onSetTime() {
		// Use the current time as the default values for the picker
		final Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);

		// Create a new instance of TimePickerDialog and return it
		TimePickerDialog dialog = new TimePickerDialog(this,
				new OnTimeSetListener() {

					@Override
					public void onTimeSet(TimePicker view, int hourOfDay,
							int minute) {
						setText(R.id.time,
								String.format("%02d:%02d", hourOfDay, minute));
					}
				}, hour, minute, DateFormat.is24HourFormat(this));
		dialog.show();
	}

	private void onSetDate() {
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		DatePickerDialog dialog = new DatePickerDialog(this,
				new OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						setText(R.id.date, String.format("%02d/%02d/%04d",
								dayOfMonth, monthOfYear, year));
					}
				}, year, month, day);
		dialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item = menu.add(0, MENU_ACCEPT, 0, R.string.callout_save);
		item.setIcon(R.drawable.icon_accept);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ACCEPT:
			group = new GroupImpl(getSAM(), new MSEResource(
					Constants.getGroupId(), Group.typeURI));

			String title = getCtrlText(R.id.title);
			String date = getCtrlText(R.id.date);
			String time = getCtrlText(R.id.time);
			String description = getCtrlText(R.id.description);

			if (title.length() == 0 || date.length() == 0 || time.length() == 0
					|| description.length() == 0) {
				Toast.makeText(this, R.string.app_all_fields, Toast.LENGTH_LONG)
						.show();
			} else {
				try {
					Person me = getSAM().getMe();
					String format = getString(R.string.app_callout_format);
					String fullcontent = String.format(format, date, time,
							description);

					Content content = getSAM().createContent();
					content.setTitle(title);
					content.setContent(fullcontent);
					content.setTime(System.currentTimeMillis());

					me.addCreator(content);
					group.addHasContent(content);

					finish();
				} catch (Exception ex) {
					ex.printStackTrace();
					Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG)
							.show();
				}
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
