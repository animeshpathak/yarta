package fr.inria.arles.yarta;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import fr.inria.arles.yarta.basic.R;
import fr.inria.arles.yarta.core.YartaWrapper;
import fr.inria.arles.yarta.middleware.msemanagement.StorageAccessManager;
import fr.inria.arles.yarta.resources.Resource;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ResourceActivity extends Activity implements View.OnClickListener {

	public interface Validator {
		public boolean validate(Map<String, String> values);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resource);

		runner = new AsyncRunner(this);
		resource = YartaWrapper.getInstance().currentResource;
		resourceClass = YartaWrapper.getInstance().currentResourceClass;
		validator = YartaWrapper.getInstance().validator;

		updateDialogTitle();

		findViewById(R.id.cancelButton).setOnClickListener(this);
		findViewById(R.id.setButton).setOnClickListener(this);

		runtimeGenerateUI(findViewById(R.id.propertiesContainer));

		runner.run(new AsyncRunner.Job() {

			@Override
			public void doWork() {
				runtimeFillUI(true);
			}

			@Override
			public void doUI() {
				runtimeFillUI(false);
			}
		});
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.setButton: {
			onClickOK();
			break;
		}
		case R.id.cancelButton: {
			finish();
			break;
		}
		}
	}

	private void onClickOK() {
		final Map<String, String> staticProperties = new HashMap<String, String>();

		if (!collectStaticProperties(staticProperties)) {
			System.err.println("Invalid parameters !");
			return;
		}

		runner.run(new AsyncRunner.Job() {

			@Override
			public void doWork() {
				internalClickOK(staticProperties);
			}

			@Override
			public void doUI() {
				finish();
			}
		});
	}

	private void internalClickOK(Map<String, String> staticProperties) {
		// must be created?
		if (resource == null) {
			String className = resourceClass.getSimpleName()
					.replace("Impl", "");
			StorageAccessManager accessManager = YartaWrapper.getInstance()
					.getAccessManager();

			if (className.contains("Person") || className.contains("Speaker")) {
				try {
					Method method = accessManager.getClass().getMethod(
							"create" + className, String.class);
					resource = method.invoke(accessManager,
							staticProperties.get("UserId"));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else {
				try {
					Method method = accessManager.getClass().getMethod(
							"create" + className);
					resource = method.invoke(accessManager);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}

		Iterator<Map.Entry<String, String>> it = staticProperties.entrySet()
				.iterator();

		while (it.hasNext()) {
			Map.Entry<String, String> pair = it.next();

			boolean useFloat = pair.getKey().equals("Longitude")
					|| pair.getKey().equals("Latitude")
					|| pair.getKey().equals("Number");

			try {
				Method method = resourceClass.getMethod("set" + pair.getKey(),
						useFloat ? Float.class : String.class);
				method.invoke(
						resource,
						useFloat ? Float.parseFloat(pair.getValue()) : pair
								.getValue());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private boolean collectStaticProperties(Map<String, String> properties) {
		Iterator<Map.Entry<String, EditText>> it = dataProperties.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry<String, EditText> pair = it.next();

			String propertyName = pair.getKey();
			String propertyValue = pair.getValue().getText().toString();

			if (propertyValue != null && propertyValue.length() == 0) {
				propertyValue = null;
			}

			properties.put(propertyName, propertyValue);
		}
		return validateProperties(properties);
	}

	private boolean validateProperties(Map<String, String> properties) {
		if (properties.containsKey("Longitude")
				&& properties.containsKey("Latitude")) {
			try {
				Float.parseFloat(properties.get("Longitude"));
				Float.parseFloat(properties.get("Latitude"));
			} catch (Exception ex) {
				return false;
			}
		}

		if (validator != null) {
			return validator.validate(properties);
		}
		return true;
	}

	private void runtimeGenerateUI(View view) {
		Method[] methods = resourceClass.getMethods();

		for (Method method : methods) {
			String methodName = method.getName();
			if (!methodName.startsWith("set"))
				continue;

			String propertyName = methodName.replace("set", "");

			if (dataProperties.containsKey(propertyName)) {
				continue;
			}

			LinearLayout container = new LinearLayout(this);
			container.setOrientation(LinearLayout.HORIZONTAL);

			TextView label = new TextView(this);
			label.setWidth(150);
			label.setText(propertyName);
			label.setTextColor(Color.WHITE);

			EditText edit = new EditText(this);
			edit.setWidth(200);
			edit.setSingleLine(true);
			edit.setBackgroundColor(Color.TRANSPARENT);
			edit.setTextColor(Color.WHITE);
			edit.setHint(R.string.resources_hint);

			container.addView(label);
			container.addView(edit);

			((LinearLayout) view).addView(container);

			dataProperties.put(propertyName, edit);
		}

		for (Method method : methods) {
			String methodName = method.getName();

			if (!methodName.startsWith("add"))
				continue;

			final String propertyName = methodName.replace("add", "");

			if (objectProperties.containsKey(propertyName)) {
				continue;
			}

			LinearLayout container = new LinearLayout(this);
			container.setOrientation(LinearLayout.HORIZONTAL);

			TextView label = new TextView(this);
			label.setWidth(150);
			label.setText(propertyName);
			label.setTextColor(Color.WHITE);

			EditText edit = new EditText(this);
			edit.setWidth(200);
			edit.setSingleLine(false);
			edit.setBackgroundColor(Color.TRANSPARENT);
			edit.setTextColor(Color.WHITE);
			edit.setHint(R.string.resources_hint);
			edit.setFocusable(false);

			Button button = new Button(this);
			button.setText("...");
			button.setBackgroundResource(R.drawable.buttonstates);
			button.setTextColor(Color.WHITE);

			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					onEditObjectPropertyClicked(propertyName);
				}
			});

			if (resource == null) {
				button.setEnabled(false);
			}

			container.addView(label);
			container.addView(edit);
			container.addView(button);

			((LinearLayout) view).addView(container);

			objectProperties.put(propertyName, edit);
		}
	}

	private void runtimeFillUI(boolean foreground) {
		if (resource == null) {
			return;
		}

		fillDataProperties(foreground);
		fillObjectProperties(foreground);
	}

	private void fillDataProperties(boolean foreground) {
		Iterator<Map.Entry<String, EditText>> it = dataProperties.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry<String, EditText> pair = it.next();

			String propertyName = pair.getKey();
			try {
				if (foreground) {
					Method method = resource.getClass().getMethod(
							"get" + propertyName);

					Object value = method.invoke(resource);
					String property = "";

					if (value != null) {
						property += value;
					}
					EditText text = pair.getValue();
					reqestedInfo.put(text, property);
				} else {
					EditText text = pair.getValue();
					text.setText(reqestedInfo.get(text));
				}
			} catch (Exception ex) {
				System.err.println("Runtime Fill UI: " + ex.getMessage());
			}
		}
	}

	private void fillObjectProperties(boolean foreground) {
		Iterator<Map.Entry<String, EditText>> it = objectProperties.entrySet()
				.iterator();

		while (it.hasNext()) {
			Map.Entry<String, EditText> pair = it.next();
			String propertyName = pair.getKey();

			try {
				if (foreground) {
					Method method = resource.getClass().getMethod(
							"get" + propertyName);

					String strText = "";

					@SuppressWarnings("unchecked")
					Set<? extends Resource> result = (Set<? extends Resource>) method
							.invoke(resource);

					for (Resource resource : result) {
						strText += (YartaWrapper.getInstance().getResInfo(
								resource) + "; ");
					}
					EditText text = pair.getValue();
					reqestedInfo.put(text, strText);
				} else {
					EditText text = pair.getValue();
					text.setText(reqestedInfo.get(text));
				}
			} catch (Exception ex) {
				System.err.println("Runtime Fill UI: " + ex.getMessage());
			}
		}
	}

	private void updateDialogTitle() {
		if (resource != null) {
			setTitle("Update "
					+ resourceClass.getSimpleName().replace("Impl", ""));
		} else {
			setTitle("Create " + resourceClass.getSimpleName());
		}
	}

	private void onEditObjectPropertyClicked(final String propertyName) {
		Method[] methods = resourceClass.getMethods();

		for (Method method : methods) {
			if (method.getName().equals("add" + propertyName)) {
				Class<?> requiredClass = method.getParameterTypes()[0];

				ResourceLinkDialog dialog = new ResourceLinkDialog(this);
				dialog.setParameters(resource, propertyName, requiredClass);
				dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

					@Override
					public void onDismiss(DialogInterface dialog) {
						reloadProperty(propertyName);
					}
				});
				dialog.show();
			}
		}
	}

	private void reloadProperty(final String propertyName) {
		try {
			Method method = resource.getClass().getMethod("get" + propertyName);

			String strText = "";

			@SuppressWarnings("unchecked")
			Set<? extends Resource> result = (Set<? extends Resource>) method
					.invoke(resource);

			for (Resource resource : result) {
				strText += (YartaWrapper.getInstance().getResInfo(resource) + "; ");
			}

			TextView text = objectProperties.get(propertyName);
			text.setText(strText);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private AsyncRunner runner;
	private Object resource;
	private Class<?> resourceClass;
	private Validator validator;

	private Map<EditText, String> reqestedInfo = new HashMap<EditText, String>();
	private Map<String, EditText> dataProperties = new HashMap<String, EditText>();
	private Map<String, EditText> objectProperties = new HashMap<String, EditText>();
}
