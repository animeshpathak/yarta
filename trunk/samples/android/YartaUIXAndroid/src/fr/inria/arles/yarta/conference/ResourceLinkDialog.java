package fr.inria.arles.yarta.conference;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.inria.arles.yarta.conference.ResourceListAdapter.ItemCheck;
import fr.inria.arles.yarta.core.YartaWrapper;
import fr.inria.arles.yarta.resources.Resource;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class ResourceLinkDialog extends Dialog implements View.OnClickListener,
		ItemCheck {

	public ResourceLinkDialog(Context context) {
		super(context);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_linkresource);

		lstAdapter = new ResourceListAdapter(getContext());
		lstAdapter.setEditable(false, this);
		ListView list = (ListView) findViewById(R.id.resourceList);
		list.setAdapter(lstAdapter);

		List<Object> checkedResources = new ArrayList<Object>();
		List<Object> resources = new ArrayList<Object>();

		try {
			Method getAllMethod = YartaWrapper.class.getMethod("getAll"
					+ requiredClass.getSimpleName() + "s");
			resources.addAll((Collection<? extends Resource>) getAllMethod
					.invoke(YartaWrapper.getInstance()));

			Method getAllLinkMethod = resource.getClass().getMethod(
					"get" + linkType);
			checkedResources
					.addAll((Collection<? extends Resource>) getAllLinkMethod
							.invoke(resource));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		lstAdapter.setResources(resources);

		// set checks as well

		List<Boolean> checks = new ArrayList<Boolean>();
		for (Object resource : resources) {
			checks.add(checkedResources.contains(resource));
		}
		lstAdapter.setChecks(checks);

		findViewById(R.id.okButton).setOnClickListener(this);
		findViewById(R.id.cancelButton).setVisibility(View.GONE);

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
		case R.id.okButton: {
			dismiss();
			break;
		}
		}
	}

	@Override
	public void onItemCheck(Resource resource, boolean check) {
		if (check) {
			Method method = getAddMethod();
			try {
				method.invoke(this.resource, resource);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			Method method = getRemoveMethod();
			try {
				method.invoke(this.resource, resource);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private Method getAddMethod() {
		if (addMethod == null) {
			Method[] methods = resource.getClass().getMethods();

			for (Method m : methods) {
				if (m.getName().equals("add" + linkType)) {
					addMethod = m;
					break;
				}
			}
		}

		return addMethod;
	}

	private Method getRemoveMethod() {
		if (removeMethod == null) {
			Method[] methods = resource.getClass().getMethods();

			for (Method m : methods) {
				if (m.getName().equals("delete" + linkType)) {
					removeMethod = m;
					break;
				}
			}
		}

		return removeMethod;
	}

	private Method removeMethod;
	private Method addMethod;

	private Object resource;
	private String linkType;
	private Class<?> requiredClass;
	private ResourceListAdapter lstAdapter;
}
