package fr.inria.arles.yarta.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import fr.inria.arles.yarta.conference.msemanagement.StorageAccessManagerEx;
import fr.inria.arles.yarta.core.YUtils;
import fr.inria.arles.yarta.core.YartaWrapper;
import fr.inria.arles.yarta.resources.Resource;

public class ResourceDialog extends JDialog {

	public interface Validator {
		public boolean validate(Map<String, String> values);
	}

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	private Validator validator;
	private Object resource;
	private Class<?> resourceClass;

	/**
	 * Create the dialog.
	 */
	public ResourceDialog() {
		setTitle("Yarta Resource");
		setModal(true);
		setBounds(100, 100, 430, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						onClickOK();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						onClickCancel();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	private void onClickCancel() {
		dispose();
	}

	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	private void onClickOK() {
		Map<String, String> staticProperties = new HashMap<String, String>();

		if (!collectStaticProperties(staticProperties)) {
			YUtils.showError(this, "Invalid parameters !");
			return;
		}

		// must be created?
		if (resource == null) {
			String className = resourceClass.getSimpleName()
					.replace("Impl", "");
			StorageAccessManagerEx accessManager = YartaWrapper.getInstance()
					.getAccessManager();

			if (className.contains("Person") || className.contains("Speaker")) {
				try {
					Method method = StorageAccessManagerEx.class.getMethod(
							"create" + className, String.class);
					resource = method.invoke(accessManager,
							staticProperties.get("UserId"));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else {
				try {
					Method method = StorageAccessManagerEx.class
							.getMethod("create" + className);
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
		dispose();
	}

	private boolean collectStaticProperties(Map<String, String> properties) {
		Iterator<Map.Entry<String, JTextField>> it = dataProperties.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry<String, JTextField> pair = it.next();

			String propertyName = pair.getKey();
			String propertyValue = pair.getValue().getText();

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

	public void setResource(Object resource) {
		this.resource = resource;
		setClass(resource.getClass());
		runtimeFillUI();
	}

	public void setClass(Class<?> resourceClass) {
		this.resourceClass = resourceClass;
		runtimeGenerateUI();
	}

	int nextLine = 11;

	private void runtimeGenerateUI() {
		Method[] methods = resourceClass.getMethods();

		for (Method method : methods) {
			String methodName = method.getName();
			if (methodName.startsWith("set")) {
				String propertyName = methodName.replace("set", "");

				JTextField txtProperty = new JTextField();
				txtProperty.setBounds(100, nextLine, 198, 20);
				contentPanel.add(txtProperty);
				txtProperty.setColumns(10);

				JLabel lblProperty = new JLabel(propertyName);
				lblProperty.setBounds(10, nextLine, 80, 14);
				contentPanel.add(lblProperty);

				dataProperties.put(propertyName, txtProperty);

				nextLine += 30;
			} else if (methodName.startsWith("add")) {
				final String propertyName = methodName.replace("add", "");

				JButton button = new JButton("Edit");
				button.setEnabled(resource != null);
				button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						onEditObjectPropertyClicked(propertyName);
					}
				});
				button.setBounds(300, nextLine, 57, 23);
				contentPanel.add(button);

				JTextField txtProperty = new JTextField();
				txtProperty.setBounds(100, nextLine, 198, 20);
				txtProperty.setEditable(false);
				contentPanel.add(txtProperty);
				txtProperty.setColumns(10);

				JLabel lblProperty = new JLabel(propertyName);
				lblProperty.setBounds(10, nextLine, 80, 14);
				contentPanel.add(lblProperty);

				objectProperties.put(propertyName, txtProperty);

				nextLine += 30;
			}
		}

		prefferedDimension.height = nextLine + 70;
		pack();
		setLocationRelativeTo(null);
	}

	Dimension prefferedDimension = new Dimension(430, 300);

	@Override
	public Dimension getPreferredSize() {
		return prefferedDimension;
	}

	private void runtimeFillUI() {
		fillDataProperties();
		fillObjectProperties();
	}

	private void fillObjectProperties() {
		Iterator<Map.Entry<String, JTextField>> it = objectProperties
				.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<String, JTextField> pair = it.next();
			String propertyName = pair.getKey();

			try {
				Method method = resource.getClass().getMethod(
						"get" + propertyName);

				String strText = "";

				@SuppressWarnings("unchecked")
				Set<? extends Resource> result = (Set<? extends Resource>) method
						.invoke(resource);

				for (Resource resource : result) {
					strText += (YUtils.resourceAsString(resource) + "; ");
				}

				JTextField text = pair.getValue();
				text.setText(strText);

			} catch (Exception ex) {
				YUtils.showError(getContentPane(),
						"Runtime Fill UI: " + ex.getMessage());
				ex.printStackTrace();
			}
		}
	}

	private void fillDataProperties() {
		Iterator<Map.Entry<String, JTextField>> it = dataProperties.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry<String, JTextField> pair = it.next();

			String propertyName = pair.getKey();
			try {
				Method method = resource.getClass().getMethod(
						"get" + propertyName);

				Object value = method.invoke(resource);
				String property = "";

				if (value != null) {
					property += value;
				}

				JTextField text = pair.getValue();
				text.setText(property);
			} catch (Exception ex) {
				YUtils.showError(getContentPane(),
						"Runtime Fill UI: " + ex.getMessage());
				ex.printStackTrace();
			}
		}
	}

	private void reloadProperty(final String propertyName) {
		try {
			Method method = resource.getClass().getMethod(
					"get" + propertyName);

			String strText = "";

			@SuppressWarnings("unchecked")
			Set<? extends Resource> result = (Set<? extends Resource>) method
					.invoke(resource);

			for (Resource resource : result) {
				strText += (YUtils.resourceAsString(resource) + "; ");
			}

			JTextField text = objectProperties.get(propertyName);
			text.setText(strText);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void onEditObjectPropertyClicked(final String propertyName) {
		Method[] methods = resourceClass.getMethods();

		for (Method method : methods) {
			if (method.getName().equals("add" + propertyName)) {
				Class<?> requiredClass = method.getParameterTypes()[0];

				ResourceLinkDialog dialog = new ResourceLinkDialog(resource,
						propertyName, requiredClass);
				dialog.setVisible(true);

				reloadProperty(propertyName);
			}
		}
	}

	private Map<String, JTextField> dataProperties = new HashMap<String, JTextField>();
	private Map<String, JTextField> objectProperties = new HashMap<String, JTextField>();
}
