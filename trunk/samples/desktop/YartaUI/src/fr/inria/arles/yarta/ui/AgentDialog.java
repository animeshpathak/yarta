package fr.inria.arles.yarta.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;

import fr.inria.arles.yarta.core.YUtils;
import fr.inria.arles.yarta.core.YartaWrapper;
import fr.inria.arles.yarta.resources.Agent;
import fr.inria.arles.yarta.resources.Content;
import fr.inria.arles.yarta.resources.Event;
import fr.inria.arles.yarta.resources.Group;
import fr.inria.arles.yarta.resources.Person;
import fr.inria.arles.yarta.resources.Place;
import fr.inria.arles.yarta.resources.Resource;
import fr.inria.arles.yarta.resources.Topic;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Set;

public class AgentDialog extends JDialog {

	public final static int PERSON = 0;
	public final static int GROUP = 1;

	private static final long serialVersionUID = 1L;
	private Agent agent = null;
	private int agentType = 0;

	private final JPanel contentPanel = new JPanel();
	private JTextField txtEmail;
	private JTextField txtFirstName;
	private JTextField txtLastName;
	private JTextField txtHomePage;
	private JTextField txtKnows;

	private JButton btnEditKnows;
	private JButton btnEditMemberOf;
	private JButton btnHasInterest;
	private JButton btnIsCreator;
	private JTextField txtName;
	private JTextField txtMemberOf;
	private JTextField txtHasInterest;
	private JTextField txtIsCreator;
	private JLabel lblIstagged;
	private JTextField txtIsTagged;
	private JButton btnIsTagged;
	private JLabel lblIsLocated;
	private JTextField txtIsLocated;
	private JButton btnIsLocated;
	private JLabel lblParticipates;
	private JTextField txtParticipates;
	private JButton btnParticipates;

	/**
	 * Create the dialog.
	 */
	public AgentDialog() {
		setResizable(false);
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 413, 456);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		txtEmail = new JTextField();
		txtEmail.setBounds(85, 11, 198, 20);
		contentPanel.add(txtEmail);
		txtEmail.setColumns(10);

		JLabel lblEmail = new JLabel("E-mail (id):");
		lblEmail.setBounds(10, 14, 65, 14);
		contentPanel.add(lblEmail);

		JLabel lblFirstName = new JLabel("First name:");
		lblFirstName.setBounds(10, 46, 65, 14);
		contentPanel.add(lblFirstName);

		txtFirstName = new JTextField();
		txtFirstName.setColumns(10);
		txtFirstName.setBounds(85, 43, 198, 20);
		contentPanel.add(txtFirstName);

		JLabel lblLastName = new JLabel("Last name:");
		lblLastName.setBounds(10, 74, 65, 14);
		contentPanel.add(lblLastName);

		txtLastName = new JTextField();
		txtLastName.setColumns(10);
		txtLastName.setBounds(85, 71, 198, 20);
		contentPanel.add(txtLastName);

		JLabel lblHomePage = new JLabel("Home page:");
		lblHomePage.setBounds(10, 130, 65, 14);
		contentPanel.add(lblHomePage);

		txtHomePage = new JTextField();
		txtHomePage.setColumns(10);
		txtHomePage.setBounds(85, 127, 198, 20);
		contentPanel.add(txtHomePage);

		JLabel lblKnows = new JLabel("knows:");
		lblKnows.setBounds(10, 159, 46, 14);
		contentPanel.add(lblKnows);

		txtKnows = new JTextField();
		txtKnows.setEditable(false);
		txtKnows.setBounds(85, 156, 198, 20);
		contentPanel.add(txtKnows);
		txtKnows.setColumns(500);

		btnEditKnows = new JButton("Edit");
		btnEditKnows.setEnabled(false);
		btnEditKnows.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onEditKnowsClicked();
			}
		});
		btnEditKnows.setBounds(293, 155, 57, 23);
		contentPanel.add(btnEditKnows);

		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(10, 102, 65, 14);
		contentPanel.add(lblName);

		txtName = new JTextField();
		txtName.setColumns(10);
		txtName.setBounds(85, 99, 198, 20);
		contentPanel.add(txtName);

		JLabel lblMemberof = new JLabel("memberOf:");
		lblMemberof.setBounds(10, 190, 65, 14);
		contentPanel.add(lblMemberof);

		txtMemberOf = new JTextField();
		txtMemberOf.setEditable(false);
		txtMemberOf.setColumns(500);
		txtMemberOf.setBounds(85, 187, 198, 20);
		contentPanel.add(txtMemberOf);

		btnEditMemberOf = new JButton("Edit");
		btnEditMemberOf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onEditMemberOfClicked();
			}
		});
		btnEditMemberOf.setEnabled(false);
		btnEditMemberOf.setBounds(293, 186, 57, 23);
		contentPanel.add(btnEditMemberOf);

		JLabel lblHasinterest = new JLabel("hasInterest:");
		lblHasinterest.setBounds(10, 219, 65, 14);
		contentPanel.add(lblHasinterest);

		txtHasInterest = new JTextField();
		txtHasInterest.setEditable(false);
		txtHasInterest.setColumns(500);
		txtHasInterest.setBounds(85, 216, 198, 20);
		contentPanel.add(txtHasInterest);

		btnHasInterest = new JButton("Edit");
		btnHasInterest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onEditHasInterestClicked();
			}
		});
		btnHasInterest.setEnabled(false);
		btnHasInterest.setBounds(293, 215, 57, 23);
		contentPanel.add(btnHasInterest);

		JLabel lblIscreator = new JLabel("isCreator:");
		lblIscreator.setBounds(10, 248, 65, 14);
		contentPanel.add(lblIscreator);

		txtIsCreator = new JTextField();
		txtIsCreator.setEditable(false);
		txtIsCreator.setColumns(500);
		txtIsCreator.setBounds(85, 245, 198, 20);
		contentPanel.add(txtIsCreator);

		btnIsCreator = new JButton("Edit");
		btnIsCreator.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onEditIsCreatorClicked();
			}
		});
		btnIsCreator.setEnabled(false);
		btnIsCreator.setBounds(293, 244, 57, 23);
		contentPanel.add(btnIsCreator);

		lblIstagged = new JLabel("isTagged:");
		lblIstagged.setBounds(10, 277, 65, 14);
		contentPanel.add(lblIstagged);

		txtIsTagged = new JTextField();
		txtIsTagged.setEditable(false);
		txtIsTagged.setColumns(500);
		txtIsTagged.setBounds(85, 274, 198, 20);
		contentPanel.add(txtIsTagged);

		btnIsTagged = new JButton("Edit");
		btnIsTagged.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onEditIsTaggedClick();
			}
		});
		btnIsTagged.setEnabled(false);
		btnIsTagged.setBounds(293, 273, 57, 23);
		contentPanel.add(btnIsTagged);

		lblIsLocated = new JLabel("isLocated:");
		lblIsLocated.setBounds(10, 306, 65, 14);
		contentPanel.add(lblIsLocated);

		txtIsLocated = new JTextField();
		txtIsLocated.setEditable(false);
		txtIsLocated.setColumns(500);
		txtIsLocated.setBounds(85, 303, 198, 20);
		contentPanel.add(txtIsLocated);

		btnIsLocated = new JButton("Edit");
		btnIsLocated.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onEditIsLocatedClick();
			}
		});
		btnIsLocated.setEnabled(false);
		btnIsLocated.setBounds(293, 302, 57, 23);
		contentPanel.add(btnIsLocated);

		lblParticipates = new JLabel("participates:");
		lblParticipates.setBounds(10, 335, 65, 14);
		contentPanel.add(lblParticipates);

		txtParticipates = new JTextField();
		txtParticipates.setEditable(false);
		txtParticipates.setColumns(500);
		txtParticipates.setBounds(85, 332, 198, 20);
		contentPanel.add(txtParticipates);

		btnParticipates = new JButton("Edit");
		btnParticipates.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onEditParticipatesClick();
			}
		});
		btnParticipates.setEnabled(false);
		btnParticipates.setBounds(293, 331, 57, 23);
		contentPanel.add(btnParticipates);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Update");
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

		setTitle("Create agent");
		setLocationRelativeTo(null);
	}

	public void setAgent(Agent agent) {
		this.agent = agent;

		setTitle("Update agent " + agent.getEmail());
		btnEditMemberOf.setEnabled(true);
		btnEditKnows.setEnabled(true);
		btnHasInterest.setEnabled(true);
		btnIsCreator.setEnabled(true);
		btnIsTagged.setEnabled(true);
		btnIsLocated.setEnabled(true);
		btnParticipates.setEnabled(true);

		if (isPerson()) {
			txtFirstName.setText(((Person) agent).getFirstName());
			txtLastName.setText(((Person) agent).getLastName());
		} else {
			txtFirstName.setEnabled(false);
			txtLastName.setEnabled(false);
		}

		txtEmail.setText(agent.getEmail());
		txtName.setText(agent.getName());
		txtHomePage.setText(agent.getHomepage());

		updateKnows();
		updateMemberOf();
		updateHasInterest();
		updateIsCreator();
		updateIsTagged();
		updateIsLocated();
		updateParticipates();
	}

	public void setAgent(int agentType) {
		this.agentType = agentType;

		if (!isPerson()) {
			txtFirstName.setEnabled(false);
			txtLastName.setEnabled(false);
		}
	}

	private void updateKnows() {
		String strKnown = "";

		Set<Agent> knownAgents = agent.getKnows();
		for (Agent agent : knownAgents) {
			strKnown += (YUtils.resourceAsString(agent) + "; ");
		}

		txtKnows.setText(strKnown);
	}

	private void updateMemberOf() {
		String strMemberOfGroups = "";

		Set<Group> memberOfGroups = agent.getIsMemberOf();
		for (Group group : memberOfGroups) {
			strMemberOfGroups += (YUtils.resourceAsString(group) + "; ");
		}

		txtMemberOf.setText(strMemberOfGroups);
	}

	private void updateHasInterest() {
		String strHasInterest = "";

		Set<Resource> hasInterestResources = agent.getHasInterest();
		for (Resource resource : hasInterestResources) {
			strHasInterest += (YUtils.resourceAsString(resource) + "; ");
		}

		txtHasInterest.setText(strHasInterest);
	}

	private void updateIsCreator() {
		String strIsCreator = "";

		Set<Content> isCreatorContents = agent.getCreator();
		for (Content content : isCreatorContents) {
			strIsCreator += (YUtils.resourceAsString(content) + "; ");
		}

		txtIsCreator.setText(strIsCreator);
	}

	private void updateIsTagged() {
		String strIsTagged = "";

		Set<Topic> isTaggedTopics = agent.getIsTagged();
		for (Topic topic : isTaggedTopics) {
			strIsTagged += (YUtils.resourceAsString(topic) + "; ");
		}

		txtIsTagged.setText(strIsTagged);
	}

	private void updateIsLocated() {
		String strIsLocated = "";

		Set<Place> isLocatedPlaces = agent.getIsLocated();
		for (Place place : isLocatedPlaces) {
			strIsLocated += (YUtils.resourceAsString(place) + "; ");
		}

		txtIsLocated.setText(strIsLocated);
	}

	public void updateParticipates() {
		String strParticipates = "";

		Set<Event> participatesEvents = agent.getIsAttending();
		for (Event event : participatesEvents) {
			strParticipates += (YUtils.resourceAsString(event) + "; ");
		}

		txtParticipates.setText(strParticipates);
	}

	private void onClickCancel() {
		dispose();
	}

	private void onClickOK() {
		String email = txtEmail.getText();
		String firstName = txtFirstName.getText();
		String lastName = txtLastName.getText();
		String homePage = txtHomePage.getText();
		String name = txtName.getText();

		if (agent == null) {
			onClickOKCreateAgent(email, firstName, lastName, homePage, name);
			return;
		}

		if (isPerson()) {
			YartaWrapper.getInstance().updatePerson(
					((Person) agent).getUserId(), email, name, firstName,
					lastName, homePage);
		} else if (agent instanceof Group) {
			YartaWrapper.getInstance().updateGroup(agent.getUniqueId(), email,
					name, homePage);
		}

		dispose();
	}

	private void onClickOKCreateAgent(String email, String firstName,
			String lastName, String homePage, String name) {

		if (email.isEmpty()) {
			YUtils.showError(getContentPane(), "No e-mail specified.");
			return;
		}

		if (isPerson() && YartaWrapper.getInstance().readPerson(email) != null) {
			YUtils.showError(getContentPane(), "This e-mail already exists.");
			return;
		}

		if (isPerson()) {
			YartaWrapper.getInstance().createPerson(email, firstName, lastName,
					homePage);
		} else {
			YartaWrapper.getInstance().createGroup(email, homePage, name);
		}

		dispose();
	}

	private boolean isPerson() {
		if (agent == null)
			return agentType == PERSON;
		return agent instanceof Person;
	}

	private void onEditKnowsClicked() {
		ResourceLinkDialog dialog = new ResourceLinkDialog(agent,
				ResourceLinkDialog.TYPE_KNOWS);
		dialog.setVisible(true);
		updateKnows();
	}

	private void onEditMemberOfClicked() {
		ResourceLinkDialog dialog = new ResourceLinkDialog(agent,
				ResourceLinkDialog.TYPE_MEMBEROF);
		dialog.setVisible(true);
		updateMemberOf();
	}

	private void onEditHasInterestClicked() {
		ResourceLinkDialog dialog = new ResourceLinkDialog(agent,
				ResourceLinkDialog.TYPE_HASINTEREST);
		dialog.setVisible(true);
		updateHasInterest();
	}

	private void onEditIsCreatorClicked() {
		ResourceLinkDialog dialog = new ResourceLinkDialog(agent,
				ResourceLinkDialog.TYPE_ISCREATOR);
		dialog.setVisible(true);
		updateIsCreator();
	}

	public void onEditIsTaggedClick() {
		ResourceLinkDialog dialog = new ResourceLinkDialog(agent,
				ResourceLinkDialog.TYPE_ISTAGGED);
		dialog.setVisible(true);
		updateIsTagged();
	}

	public void onEditIsLocatedClick() {
		ResourceLinkDialog dialog = new ResourceLinkDialog(agent,
				ResourceLinkDialog.TYPE_ISLOCATED);
		dialog.setVisible(true);
		updateIsLocated();
	}

	public void onEditParticipatesClick() {
		ResourceLinkDialog dialog = new ResourceLinkDialog(agent,
				ResourceLinkDialog.TYPE_PARTICIPATES);
		dialog.setVisible(true);
		updateParticipates();
	}
}
