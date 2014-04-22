package fr.inria.arles.yarta;

import java.util.ArrayList;
import java.util.List;

import fr.inria.arles.yarta.basic.R;
import fr.inria.arles.yarta.core.Settings;
import fr.inria.arles.yarta.core.YartaWrapper;
import fr.inria.arles.yarta.middleware.communication.Message;
import fr.inria.arles.yarta.middleware.communication.Receiver;
import fr.inria.arles.yarta.middleware.msemanagement.MSEApplication;
import fr.inria.arles.yarta.policy.PoliciesActivity;
import fr.inria.arles.yarta.resources.Content;
import fr.inria.arles.yarta.resources.Event;
import fr.inria.arles.yarta.resources.Group;
import fr.inria.arles.yarta.resources.Person;
import fr.inria.arles.yarta.resources.Place;
import fr.inria.arles.yarta.resources.Topic;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements MSEApplication, Receiver,
		DialogInterface.OnClickListener, View.OnClickListener {

	private final static int MENU_HELLO = 0;
	private final static int MENU_SHOW_QR = 1;
	private final static int MENU_GETME = 2;

	private final static int ID_FROM_BARCODE_INTENT_REQUEST = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		wrapper = YartaWrapper.getInstance();
		wrapper.runner = new AsyncRunner(this);

		showLoginDialog();

		lstAdapter = new ResourceListAdapter(this);
		ListView list = (ListView) findViewById(R.id.resourceList);
		list.setAdapter(lstAdapter);

		View vPolicy = findViewById(R.id.policy_editor);
		vPolicy.setVisibility(View.VISIBLE);
		vPolicy.setOnClickListener(this);

		findViewById(R.id.main_create).setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		wrapper.uninit();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (bKBReady) {
			refreshList();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.policy_editor:
			if (bKBReady) {
				startActivity(new Intent(this, PoliciesActivity.class));
			} else {
				Toast.makeText(getApplicationContext(),
						R.string.main_kb_not_ready, Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.main_create:
			onNewClicked();
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);

		menu.add(0, MENU_HELLO, 0, R.string.main_hello);
		menu.add(0, MENU_SHOW_QR, 0, R.string.main_showqr);
		menu.add(0, MENU_GETME, 0, R.string.main_getme);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int index = item.getItemId();
		int group = item.getGroupId();

		if (group == 0) {
			if (index == MENU_HELLO) {
				if (bKBReady) {
					onHelloClicked();
				}
			} else if (index == MENU_SHOW_QR) {
				if (bKBReady) {
					onShowQRClicked();
				}
			} else if (index == MENU_GETME) {
				if (bKBReady) {
					onGetMeClicked();
				}
			}
			return true;
		}

		return true;
	}

	private void onNewClicked() {
		String[] items = new String[classes.length];
		for (int i = 0; i < classes.length; i++) {
			items[i] = classes[i].getSimpleName();
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.main_select_resource);
		builder.setSingleChoiceItems(items, -1,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if (which >= 0 && which < classes.length) {
							wrapper.currentResource = null;
							wrapper.currentResourceClass = classes[which];
							startActivity(new Intent(MainActivity.this,
									ResourceActivity.class));
						}
					}
				});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ID_FROM_BARCODE_INTENT_REQUEST) {
			if (resultCode == RESULT_OK) {
				String partnerId = data.getStringExtra("SCAN_RESULT");
				showHelloDialog(partnerId);
			} else {
				Toast.makeText(this, R.string.main_qr_cancelled,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void onHelloClicked() {
		try {
			Intent getIdFromBarcodeintent = new Intent(
					"com.google.zxing.client.android.SCAN");
			getIdFromBarcodeintent.putExtra("SCAN_MODE", "QR_CODE_MODE");
			startActivityForResult(getIdFromBarcodeintent,
					ID_FROM_BARCODE_INTENT_REQUEST);
		} catch (Exception ex) {
			showHelloDialog("");
		}
	}

	private void onShowQRClicked() {
		try {
			Intent showIdInBarcodeIntent = new Intent(
					"com.google.zxing.client.android.ENCODE");
			showIdInBarcodeIntent.putExtra("ENCODE_TYPE", "TEXT_TYPE");
			showIdInBarcodeIntent.putExtra("ENCODE_DATA", wrapper.getUserId());
			startActivity(showIdInBarcodeIntent);
		} catch (Exception ex) {
			Toast.makeText(this, R.string.main_qr_notinstalled,
					Toast.LENGTH_SHORT).show();
		}
	}

	private void showHelloDialog(String partnerId) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle(R.string.main_hello);

		// Set an EditText view to get user input
		final EditText inputName = new EditText(this);
		alert.setView(inputName);
		inputName.setText(partnerId);
		alert.setPositiveButton(R.string.main_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						boolean success = wrapper.sendHello(inputName.getText()
								.toString());
						Toast.makeText(
								MainActivity.this,
								success ? R.string.main_hello_success
										: R.string.main_hello_failed,
								Toast.LENGTH_SHORT).show();
					}
				});

		alert.show();
	}

	private void showLoginDialog() {
		final Settings settings = new Settings(this);
		String previousUserID = settings.getString(Settings.USER_ID);

		if (previousUserID.length() == 0) {
			previousUserID = getString(R.string.app_user);
		}

		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle(R.string.main_user);

		// Set an EditText view to get user input
		final EditText inputName = new EditText(this);
		alert.setView(inputName);
		inputName.setText(previousUserID);
		alert.setPositiveButton(R.string.main_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String userId = inputName.getText().toString();
						if (userId.length() == 0) {
							return;
						}
						settings.setString(Settings.USER_ID, userId);
						doLogin(userId);
					}
				});

		alert.show();
	}

	private void doLogin(final String userId) {
		wrapper.runner.run(new AsyncRunner.Job() {

			@Override
			public void doWork() {
				wrapper.init(userId.toString(), MainActivity.this,
						MainActivity.this);
				wrapper.setReceiver(MainActivity.this);
			}

			@Override
			public void doUI() {
			}
		});
	}

	private void onGetMeClicked() {
		wrapper.currentResource = wrapper.getMe();
		wrapper.currentResourceClass = wrapper.currentResource.getClass();
		startActivity(new Intent(this, ResourceActivity.class));
	}

	private List<Object> lstResources = new ArrayList<Object>();

	public void refreshList() {
		wrapper.runner.run(new AsyncRunner.Job() {

			@Override
			public void doWork() {
				synchronized (lstResources) {
					lstResources.clear();
					lstResources.addAll(wrapper.getAllResources());
				}
			}

			@Override
			public void doUI() {
				lstAdapter.setResources(lstResources);
			}
		});
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		bQueryResult = which == AlertDialog.BUTTON_POSITIVE ? true : false;
		bQueryAnswered = true;
	}

	private boolean messageBox(final String message) {
		bQueryAnswered = false;
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this);
				builder.setCancelable(false);
				builder.setMessage(message);
				builder.setPositiveButton(R.string.main_yes, MainActivity.this);
				builder.setNegativeButton(R.string.main_no, MainActivity.this);

				builder.create().show();
			}
		});

		while (!bQueryAnswered) {
			try {
				Thread.sleep(100);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return bQueryResult;
	}

	private Class<?> classes[] = new Class<?>[] { Person.class, Group.class,
			Event.class, Place.class, Content.class, Topic.class };
	private YartaWrapper wrapper;
	private ResourceListAdapter lstAdapter;
	private boolean bKBReady = false;
	private boolean bQueryAnswered = false;
	private boolean bQueryResult = false;

	@Override
	public void handleNotification(final String message) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				refreshList();
				Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG)
						.show();
			}
		});
	}

	@Override
	public boolean handleQuery(String query) {
		return messageBox(query);
	}

	@Override
	public void handleKBReady(String userId) {
		YartaWrapper.getInstance().setOwnerId(userId);
		refreshList();
		findViewById(R.id.main_create).setEnabled(true);
		bKBReady = true;
	}

	@Override
	public boolean handleMessage(final String peerId, final Message message) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				MessageDialog dialog = new MessageDialog(MainActivity.this,
						peerId, new String(message.getData()));
				dialog.show();
			}
		});

		return false;
	}

	@Override
	public Message handleRequest(String peerId, Message message) {
		return null;
	}

	@Override
	public String getAppId() {
		return "fr.inria.arles.yarta.YartaUI";
	}
}
