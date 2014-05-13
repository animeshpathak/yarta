package fr.inria.arles.giveaway;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import fr.inria.arles.giveaway.resources.Announcement;
import fr.inria.arles.giveaway.resources.Donation;
import fr.inria.arles.giveaway.resources.DonationImpl;
import fr.inria.arles.giveaway.resources.Person;
import fr.inria.arles.giveaway.resources.PersonImpl;
import fr.inria.arles.giveaway.resources.Picture;
import fr.inria.arles.giveaway.resources.Request;
import fr.inria.arles.giveaway.resources.RequestImpl;
import fr.inria.arles.giveaway.resources.Sale;
import fr.inria.arles.giveaway.resources.SaleImpl;
import fr.inria.arles.giveaway.util.FastCache;
import fr.inria.arles.giveaway.util.JobRunner.Job;
import fr.inria.arles.yarta.knowledgebase.MSEResource;
import fr.inria.arles.yarta.resources.Agent;
import fr.inria.arles.yarta.resources.Content;
import fr.inria.arles.yarta.resources.Resource;
import fr.inria.arles.yarta.resources.YartaResource;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class ItemActivity extends BaseActivity implements View.OnClickListener,
		FeedbackDialog.Handler {

	private static final int MENU_ACCEPT = 1;
	private static final int MENU_FEEDBACK = 2;

	public static final String DonationId = "DonationId";
	public static final String RequestId = "RequestId";
	public static final String SaleId = "SaleId";

	// pass type
	public static final String Type = "Type";

	private Donation currentDonation;
	private Request currentRequest;
	private Sale currentSale;

	private boolean editable = true;
	private ContentClientPictures client;

	private FastCache cache;
	private Person creator;

	private static List<Bitmap> tempBitmapList = new ArrayList<Bitmap>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_item);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.activity_item);

		client = new ContentClientPictures(this);
		cache = new FastCache(this);

		tempBitmapList.clear();

		initSpinners();
	}

	@Override
	protected void refreshUI() {
		if (getIntent().hasExtra(DonationId)) {
			String donationId = getIntent().getStringExtra(DonationId);
			currentDonation = new DonationImpl(getSAM(), new MSEResource(
					donationId, Donation.typeURI));
			loadDonation();
		} else if (getIntent().hasExtra(RequestId)) {
			String requestId = getIntent().getStringExtra(RequestId);
			currentRequest = new RequestImpl(getSAM(), new MSEResource(
					requestId, Request.typeURI));
			loadRequest();
		} else if (getIntent().hasExtra(SaleId)) {
			String saleId = getIntent().getStringExtra(SaleId);
			currentSale = new SaleImpl(getSAM(), new MSEResource(saleId,
					Sale.typeURI));
			loadSale();
		}

		findViewById(R.id.addPhoto).setOnClickListener(this);
		findViewById(R.id.contact_info).setOnClickListener(this);

		initializeSpecificView();
	}

	/**
	 * Sets the correct title depending on the resource being edited. In case of
	 * requests hides the pictures panel.
	 */
	private void initializeSpecificView() {
		int titleResId = R.string.additem_title;

		if (getIntent().hasExtra(DonationId)) {
			titleResId = R.string.additem_donation_title;
			findViewById(R.id.priceContainer).setVisibility(View.GONE);
		} else if (getIntent().hasExtra(RequestId)) {
			findViewById(R.id.photosContainer).setVisibility(View.GONE);
			titleResId = R.string.additem_request_title;
		} else if (getIntent().hasExtra(SaleId)) {
			titleResId = R.string.additem_sale_title;
		} else if (getIntent().hasExtra(Type)) {
			int type = Integer.valueOf(getIntent().getStringExtra(Type));
			switch (type) {
			case 0:
				titleResId = R.string.additem_donation_title;
				findViewById(R.id.priceContainer).setVisibility(View.GONE);
				break;
			case 1:
				findViewById(R.id.photosContainer).setVisibility(View.GONE);
				titleResId = R.string.additem_request_title;
				break;
			case 2:
				titleResId = R.string.additem_sale_title;
				break;
			}
		}

		setTitle(titleResId);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (isEditable()) {
			MenuItem item = menu
					.add(0, MENU_ACCEPT, 0, R.string.additem_accept);

			item.setIcon(R.drawable.icon_accept);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ACCEPT:
			if (onSaveItem() != null) {
				DonationApp app = (DonationApp) getApplication();
				app.sendNotify(Common.InriaID);
				finish();
			}
			break;
		case MENU_FEEDBACK:
			onSendFeedback();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void onSendFeedback() {
		FeedbackDialog dlg = new FeedbackDialog(this);
		dlg.setHandler(this);
		dlg.show();
	}

	@Override
	public void onSendFeedback(final String content) {
		execute(new Job() {
			boolean success;

			@Override
			public void doWork() {
				String userId = "";

				try {
					userId = getSAM().getMe().getUserId();
				} catch (Exception ex) {
					userId = "err";
				}

				success = FeedbackDialog.sendFeedback(
						"fr.inria.arles.giveaway", userId, content);
			}

			@Override
			public void doUIAfter() {
				Toast.makeText(
						getApplicationContext(),
						success ? R.string.main_feedback_sent_ok
								: R.string.main_feedback_sent_error,
						Toast.LENGTH_LONG).show();
			}
		});
	}

	public boolean isEditable() {
		return editable;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.addPhoto:
			onAddPhoto();
			break;

		case R.id.contact_info:
			onContactInfo();
			break;
		}
	}

	private void onContactInfo() {
		if (creator != null) {
			String uniqueId = creator.getUniqueId();
			Intent intent = new Intent(
					"fr.inria.arles.yarta.android.library.PersonActivity");
			intent.putExtra("PersonGUID", uniqueId);
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(intent);
		}
	}

	private void onAddPhoto() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent,
				getString(R.string.additem_pick_photo)), 0);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0 && resultCode == SherlockActivity.RESULT_OK) {
			String picturePath = getPath(data.getData());
			Bitmap bitmap = Common.decodeSampledBitmap(picturePath, 300, 300);

			if (currentDonation != null) {
				Picture picture = getSAM().createPicture();
				addBitmap(getShortName(picture), bitmap);
				client.setBitmap(getShortName(picture), bitmap);
				currentDonation.addPicture(picture);
			} else if (currentSale != null) {
				Picture picture = getSAM().createPicture();
				addBitmap(getShortName(picture), bitmap);
				client.setBitmap(getShortName(picture), bitmap);
				currentSale.addPicture(picture);
			} else {
				addBitmap("" + tempBitmapList.size(), bitmap);
				tempBitmapList.add(bitmap);
			}
		}
	}

	private void addBitmap(String shortName, Bitmap bitmap) {
		LinearLayout list = (LinearLayout) findViewById(R.id.myList);

		View vParent = getLayoutInflater().inflate(R.layout.item_photo, list,
				false);

		ImageView imageView = (ImageView) vParent.findViewById(R.id.image);
		imageView.setImageBitmap(bitmap);

		vParent.setClickable(true);
		vParent.setTag(shortName);
		vParent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String name = (String) v.getTag();
				onClickBitmap(name);
			}
		});
		list.addView(vParent);
	}

	private void onClickBitmap(String name) {
		Intent intent = new Intent(this, PhotoActivity.class);
		intent.putExtra(PhotoActivity.BitmapName, name);
		startActivity(intent);
	}

	public static Bitmap getBitmap(String name) {
		int position = Integer.valueOf(name);
		return tempBitmapList.get(position);
	}

	private String getShortName(Resource resource) {
		String uniqueId = resource.getUniqueId();
		return uniqueId.substring(uniqueId.indexOf('#') + 1);
	}

	private String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(uri, projection, null, null,
				null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	private void initSpinners() {
		String[] items = new String[] {
				getString(R.string.additem_type_donation),
				getString(R.string.additem_type_request),
				getString(R.string.additem_type_sale) };

		Spinner sp = (Spinner) findViewById(R.id.item_type);
		sp.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, items));

		sp.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (!editable) {
					return;
				}
				findViewById(R.id.photosContainer).setVisibility(
						position != 1 ? View.VISIBLE : View.GONE);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		if (getIntent().hasExtra(ItemActivity.Type)) {
			int position = Integer.parseInt(getIntent().getStringExtra(
					ItemActivity.Type));
			sp.setSelection(position);
		}

		sp = (Spinner) findViewById(R.id.item_category);
		sp.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, Common
						.getCategories(this)));
	}

	private void loadSale() {
		Spinner sp = (Spinner) findViewById(R.id.item_type);
		sp.setSelection(2);

		int price = 0;
		try {
			float fPrice = currentDonation.getPrice();
			price = (int) fPrice;
		} catch (Exception ex) {
		}
		setCtrlText(R.id.item_title, currentSale.getTitle());
		setCtrlText(R.id.item_content, currentSale.getDescription());

		setCtrlText(R.id.item_prix, "" + price);

		sp = (Spinner) findViewById(R.id.item_category);

		String cat = Common.getLocalizedCategory(this, currentSale);

		int position = 0;
		for (String category : Common.getCategories(this)) {
			if (cat.equals(category)) {
				sp.setSelection(position);
			}
			position++;
		}

		displayTools(currentSale);

		if (!editable && currentSale.getPicture().size() == 0) {
			findViewById(R.id.photosContainer).setVisibility(View.GONE);
		} else {
			loadPictures(currentSale);
			if (!editable) {
				findViewById(R.id.addPhoto).setVisibility(View.GONE);
			}
		}
	}

	private void loadDonation() {
		Spinner sp = (Spinner) findViewById(R.id.item_type);
		sp.setSelection(0);

		setCtrlText(R.id.item_title, currentDonation.getTitle());
		setCtrlText(R.id.item_content, currentDonation.getDescription());

		sp = (Spinner) findViewById(R.id.item_category);

		String cat = Common.getLocalizedCategory(this, currentDonation);

		int position = 0;
		for (String category : Common.getCategories(this)) {
			if (category.equals(cat)) {
				sp.setSelection(position);
			}
			position++;
		}

		displayTools(currentDonation);

		if (!editable && currentDonation.getPicture().size() == 0) {
			findViewById(R.id.photosContainer).setVisibility(View.GONE);
		} else {
			loadPictures(currentDonation);
			if (!editable) {
				findViewById(R.id.addPhoto).setVisibility(View.GONE);
			}
		}
	}

	private void loadPictures(Announcement announcement) {
		LinearLayout list = (LinearLayout) findViewById(R.id.myList);
		list.removeAllViews();
		for (Picture p : announcement.getPicture()) {
			Bitmap bitmap = cache.getBitmap(getShortName(p));
			if (bitmap == null) {
				bitmap = client.getSmallBitmap(getShortName(p));
				cache.setBitmap(getShortName(p), bitmap);
			}
			addBitmap(getShortName(p), bitmap);
		}
	}

	private void loadRequest() {
		Spinner sp = (Spinner) findViewById(R.id.item_type);
		sp.setSelection(1);

		int price = 0;
		try {
			float fPrice = currentRequest.getPrice();
			price = (int) fPrice;
		} catch (Exception ex) {
		}

		setCtrlText(R.id.item_title, currentRequest.getTitle());
		setCtrlText(R.id.item_content, currentRequest.getDescription());
		setCtrlText(R.id.item_prix, "" + price);

		sp = (Spinner) findViewById(R.id.item_category);

		String cat = Common.getLocalizedCategory(this, currentRequest);

		int position = 0;
		for (String category : Common.getCategories(this)) {
			if (cat.equals(category)) {
				sp.setSelection(position);
			}
			position++;
		}

		displayTools(currentRequest);

		// requests can not have photos
		findViewById(R.id.photosContainer).setVisibility(View.GONE);
	}

	/**
	 * Shows contact info in case item is not mine, else shows save button
	 * 
	 * @param topic
	 */
	private void displayTools(Content topic) {
		for (Agent a : topic.getCreator_inverse()) {
			creator = new PersonImpl(getSAM(), ((YartaResource) a).getNode());
			break;
		}

		String currentUserId = null;

		try {
			currentUserId = getSAM().getMe().getUserId();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// no creator?!
		if (creator == null) {
			editable = false;
			findViewById(R.id.item_category).setEnabled(false);
			findViewById(R.id.item_title).setFocusable(false);
			findViewById(R.id.item_content).setFocusable(false);
			findViewById(R.id.item_type).setEnabled(false);
			return;
		}

		if (!creator.getUserId().contains(currentUserId)) {
			editable = false;

			findViewById(R.id.item_category).setEnabled(false);
			findViewById(R.id.item_title).setFocusable(false);
			findViewById(R.id.item_content).setFocusable(false);
			findViewById(R.id.item_prix).setFocusable(false);
		}

		// this can not be edited later;
		findViewById(R.id.item_type).setEnabled(false);

		findViewById(R.id.profileContainer).setVisibility(View.VISIBLE);
		setCtrlText(R.id.contact_info,
				"#" + creator.getUserId().replace("@inria.fr", ""));
	}

	public Resource onSaveItem() {
		Spinner sp = (Spinner) findViewById(R.id.item_type);
		int itemType = sp.getSelectedItemPosition(); // 0 - donation 1 - request

		if (itemType == -1) {
			Toast.makeText(this, R.string.additem_choose_item_type,
					Toast.LENGTH_LONG).show();
			return null;
		}

		sp = (Spinner) findViewById(R.id.item_category);
		if (sp.getSelectedItemPosition() == -1) {
			Toast.makeText(this, R.string.additem_choose_item_category,
					Toast.LENGTH_LONG).show();
			return null;
		}
		String category = (String) sp.getSelectedItem();

		String title = getCtrlText(R.id.item_title);
		String description = getCtrlText(R.id.item_content);
		String price = getCtrlText(R.id.item_prix);

		if (title.length() == 0 || description.length() == 0
				|| price.length() == 0) {
			Toast.makeText(this, R.string.additem_input_all_details,
					Toast.LENGTH_LONG).show();
			return null;
		}

		long now = System.currentTimeMillis();

		Float fPrice = Float.valueOf(price);
		Person me = null;

		try {
			me = new PersonImpl(getSAM(),
					((YartaResource) getSAM().getMe()).getNode());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (itemType == 0) {
			Donation donation = currentDonation;
			if (donation == null) {
				donation = getSAM().createDonation();
			}
			donation.setTime(now);
			donation.setTitle(title);
			donation.setDescription(description);
			donation.addCategory(Common.getLocalizedCategory(this, getSAM(),
					category));
			donation.setPrice(fPrice);

			me.deleteCreator(donation);
			me.addCreator(donation);

			if (tempBitmapList.size() > 0) {
				for (Bitmap b : tempBitmapList) {
					Picture p = getSAM().createPicture();
					client.setBitmap(getShortName(p), b);
					donation.addPicture(p);
				}
			}

			return donation;
		} else if (itemType == 1) {
			Request request = currentRequest;
			if (request == null) {
				request = getSAM().createRequest();
			}
			request.setTime(now);
			request.setTitle(title);
			request.setDescription(description);
			request.addCategory(Common.getLocalizedCategory(this, getSAM(),
					category));
			request.setPrice(fPrice);

			me.deleteCreator(request);
			me.addCreator(request);

			return request;
		} else if (itemType == 2) {
			Sale sale = currentSale;
			if (sale == null) {
				sale = getSAM().createSale();
			}
			sale.setTime(now);
			sale.setTitle(title);
			sale.setDescription(description);
			sale.addCategory(Common.getLocalizedCategory(this, getSAM(),
					category));
			sale.setPrice(fPrice);

			me.deleteCreator(sale);
			me.addCreator(sale);

			if (tempBitmapList.size() > 0) {
				for (Bitmap b : tempBitmapList) {
					Picture p = getSAM().createPicture();
					client.setBitmap(getShortName(p), b);
					sale.addPicture(p);
				}
			}

			return sale;
		}

		return null;
	}
}
