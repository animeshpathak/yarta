package fr.inria.arles.yarta.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JList;

import fr.inria.arles.yarta.core.YUtils;
import fr.inria.arles.yarta.core.YartaWrapper;
import fr.inria.arles.yarta.resources.Resource;
import fr.inria.arles.yarta.ui.ctrl.ResourceCellRenderer;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ResourceLinkDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	private Object resource;
	private String linkType;

	private DefaultListModel modelResources = new DefaultListModel();
	private JList lstResources = new JList(modelResources);

	/**
	 * Create the dialog.
	 */
	@SuppressWarnings("unchecked")
	public ResourceLinkDialog(Object resource, String linkType,
			Class<?> requiredClass) {
		this.resource = resource;
		this.linkType = linkType;

		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 359, 330);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		lstResources.setBounds(10, 11, 323, 199);
		lstResources.setCellRenderer(new ResourceCellRenderer());
		contentPanel.add(lstResources);
		{
			JButton btnAdd = new JButton("Add");
			btnAdd.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onBtnAdd();
				}
			});
			btnAdd.setActionCommand("Cancel");
			btnAdd.setBounds(180, 221, 67, 23);
			contentPanel.add(btnAdd);
		}
		{
			JButton btnRemove = new JButton("Remove");
			btnRemove.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onBtnRemove();
				}
			});
			btnRemove.setActionCommand("Cancel");
			btnRemove.setBounds(257, 221, 76, 23);
			contentPanel.add(btnRemove);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton dismissButton = new JButton("Dismiss");
				dismissButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				dismissButton.setActionCommand("Cancel");
				buttonPane.add(dismissButton);
			}
		}

		List<Resource> resources = new ArrayList<Resource>();

		try {
			Method method = YartaWrapper.class.getMethod("getAll"
					+ requiredClass.getSimpleName() + "s");
			resources.addAll((Collection<? extends Resource>) method.invoke(YartaWrapper
					.getInstance()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		for (Resource res : resources) {
			modelResources.addElement(res);
		}

		setLocationRelativeTo(null);
		setTitle(YUtils.resourceAsString(resource));
	}

	private void onBtnRemove() {
		Method method = null;
		Method[] methods = resource.getClass().getMethods();

		for (Method m : methods) {
			if (m.getName().equals("delete" + linkType)) {
				method = m;
			}
		}
		int[] selectedIndices = lstResources.getSelectedIndices();
		for (int i = 0; i < selectedIndices.length; i++) {

			Resource res = (Resource) modelResources.getElementAt(selectedIndices[i]);

			if (res == null) {
				YUtils.showError(getContentPane(), "Resource is null!");
			} else {
				try {
					method.invoke(resource, res);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private void onBtnAdd() {
		Method method = null;
		Method[] methods = resource.getClass().getMethods();

		for (Method m : methods) {
			if (m.getName().equals("add" + linkType)) {
				method = m;
			}
		}
		int[] selectedIndices = lstResources.getSelectedIndices();
		for (int i = 0; i < selectedIndices.length; i++) {
			Resource res = (Resource) modelResources.getElementAt(selectedIndices[i]);

			if (res == null) {
				YUtils.showError(getContentPane(), "Resource is null!");
			} else {
				try {
					method.invoke(resource, res);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
}
