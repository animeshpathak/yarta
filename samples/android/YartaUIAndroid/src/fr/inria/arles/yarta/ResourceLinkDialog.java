package fr.inria.arles.yarta;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.inria.arles.yarta.basic.R;
import fr.inria.arles.yarta.core.YartaWrapper;
import fr.inria.arles.yarta.resources.Resource;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class ResourceLinkDialog extends Dialog implements View.OnClickListener {

	public ResourceLinkDialog(Context context) {
		super(context);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_linkresource);

		lstAdapter = new ResourceListAdapter(getContext());
		lstAdapter.setEditable(false);
		ListView list = (ListView) findViewById(R.id.resourceList);
		list.setAdapter(lstAdapter);

		List<Object> resources = new ArrayList<Object>();

		try {
			Method method = YartaWrapper.class.getMethod("getAll"
					+ requiredClass.getSimpleName() + "s");
			resources.addAll((Collection<? extends Resource>) method
					.invoke(YartaWrapper.getInstance()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		lstAdapter.setResources(resources);

		findViewById(R.id.addButton).setOnClickListener(this);
		findViewById(R.id.removeButton).setOnClickListener(this);

		setTitle(linkType);
	}

	public void setParameters(Object resource, String propertyName,
			Class<?> requiredClass) {
		this.resource = resource;
		this.linkType = propertyName;
		this.requiredClass = requiredClass;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.addButton: {
			onBtnAdd();
			break;
		}

		case R.id.removeButton: {
			onBtnRemove();
			break;
		}
		}
	}

	private void onBtnRemove() {
		Method method = null;
		Method[] methods = resource.getClass().getMethods();

		for (Method m : methods) {
			if (m.getName().equals("delete" + linkType)) {
				method = m;
			}
		}
		for (int i = 0; i < lstAdapter.getCount(); i++) {

			if (!lstAdapter.getChecked(i))
				continue;

			Resource res = (Resource) lstAdapter.getItem(i);

			if (res == null) {
				System.err.println("Resource is null!");
			} else {
				try {
					method.invoke(resource, res);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}

		dismiss();
	}

	private void onBtnAdd() {
		Method method = null;
		Method[] methods = resource.getClass().getMethods();

		for (Method m : methods) {
			if (m.getName().equals("add" + linkType)) {
				method = m;
			}
		}
		for (int i = 0; i < lstAdapter.getCount(); i++) {

			if (!lstAdapter.getChecked(i))
				continue;

			Resource res = (Resource) lstAdapter.getItem(i);

			if (res == null) {
				System.err.println("Resource is null!");
			} else {
				try {
					method.invoke(resource, res);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}

		dismiss();
	}

	private Object resource;
	private String linkType;
	private Class<?> requiredClass;
	private ResourceListAdapter lstAdapter;
}
