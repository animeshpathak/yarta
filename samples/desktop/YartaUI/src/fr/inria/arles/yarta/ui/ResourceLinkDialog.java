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
import fr.inria.arles.yarta.resources.Agent;
import fr.inria.arles.yarta.resources.Content;
import fr.inria.arles.yarta.resources.Event;
import fr.inria.arles.yarta.resources.Group;
import fr.inria.arles.yarta.resources.Place;
import fr.inria.arles.yarta.resources.Resource;
import fr.inria.arles.yarta.resources.Topic;
import fr.inria.arles.yarta.ui.ctrl.ResourceCellRenderer;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class ResourceLinkDialog extends JDialog {

	public final static int TYPE_KNOWS = 0;
	public final static int TYPE_MEMBEROF = 1;
	public final static int TYPE_HASINTEREST = 2;
	public final static int TYPE_ISCREATOR = 3;
	public final static int TYPE_ISTAGGED = 4;
	public final static int TYPE_ISLOCATED = 5;
	public final static int TYPE_PARTICIPATES = 6;

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	private Resource resource;
	private int linkType;

	private DefaultListModel modelResources = new DefaultListModel();
	private JList lstResources = new JList(modelResources);

	/**
	 * Create the dialog.
	 */
	public ResourceLinkDialog(Resource resource, int linkType) {
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

		if (linkType == TYPE_KNOWS) {
			resources.addAll(YartaWrapper.getInstance().getAllAgents());
		} else if (linkType == TYPE_MEMBEROF) {
			resources.addAll(YartaWrapper.getInstance().getAllGroups());
		} else if (linkType == TYPE_HASINTEREST) {
			resources.addAll(YartaWrapper.getInstance().getAllResources());
		} else if (linkType == TYPE_ISCREATOR) {
			resources.addAll(YartaWrapper.getInstance().getAllContents());
		} else if (linkType == TYPE_ISTAGGED) {
			resources.addAll(YartaWrapper.getInstance().getAllTopics());
		} else if (linkType == TYPE_ISLOCATED) {
			resources.addAll(YartaWrapper.getInstance().getAllPlaces());
		} else if (linkType == TYPE_PARTICIPATES) {
			resources.addAll(YartaWrapper.getInstance().getAllEvents());
		} else {
			throw new RuntimeException("Implement please!");
		}

		for (Resource res : resources) {
			modelResources.addElement(res);
		}

		setLocationRelativeTo(null);
		setTitle(YUtils.resourceAsString(resource));
	}

	private void onBtnRemove() {
		int[] selectedIndices = lstResources.getSelectedIndices();
		for (int i = 0; i < selectedIndices.length; i++) {

			Resource res = (Resource) modelResources
					.getElementAt(selectedIndices[i]);

			if (res == null) {
				YUtils.showError(getContentPane(), "Resource is null!");
			} else {
				if (linkType == TYPE_KNOWS) {
					((Agent) resource).deleteKnows((Agent) res);
				} else if (linkType == TYPE_MEMBEROF) {
					((Agent) resource).deleteIsMemberOf((Group) res);
				} else if (linkType == TYPE_HASINTEREST) {
					((Agent) resource).deleteHasInterest(res);
				} else if (linkType == TYPE_ISCREATOR) {
					((Agent) resource).deleteCreator((Content) res);
				} else if (linkType == TYPE_ISTAGGED) {
					resource.deleteIsTagged((Topic) res);
				} else if (linkType == TYPE_ISLOCATED) {
					if (resource instanceof Agent) {
						((Agent) resource).deleteIsLocated((Place) res);
					} else if (resource instanceof Event) {
						((Event) resource).deleteIsLocated((Place) res);
					} else if (resource instanceof Place) {
						((Place) resource).deleteIsLocated((Place) res);
					}
				} else if (linkType == TYPE_PARTICIPATES) {
					((Agent) resource).deleteIsAttending((Event) res);
				}
			}
		}
	}

	private void onBtnAdd() {
		int[] selectedIndices = lstResources.getSelectedIndices();
		for (int i = 0; i < selectedIndices.length; i++) {
			Resource res = (Resource) modelResources
					.getElementAt(selectedIndices[i]);

			if (res == null) {
				YUtils.showError(getContentPane(), "Resource is null!");
			} else {
				if (linkType == TYPE_KNOWS) {
					((Agent) resource).addKnows((Agent) res);
				} else if (linkType == TYPE_MEMBEROF) {
					((Agent) resource).addIsMemberOf((Group) res);
				} else if (linkType == TYPE_HASINTEREST) {
					((Agent) resource).addHasInterest(res);
				} else if (linkType == TYPE_ISCREATOR) {
					((Agent) resource).addCreator((Content) res);
				} else if (linkType == TYPE_ISTAGGED) {
					resource.addIsTagged((Topic) res);
				} else if (linkType == TYPE_ISLOCATED) {
					if (resource instanceof Agent) {
						((Agent) resource).addIsLocated((Place) res);
					} else if (resource instanceof Event) {
						((Event) resource).addIsLocated((Place) res);
					} else if (resource instanceof Place) {
						((Place) resource).addIsLocated((Place) res);
					}
				} else if (linkType == TYPE_PARTICIPATES) {
					((Agent) resource).addIsAttending((Event) res);
				}
			}
		}
	}
}
