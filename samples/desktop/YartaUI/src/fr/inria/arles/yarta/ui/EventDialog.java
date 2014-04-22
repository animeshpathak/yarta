package fr.inria.arles.yarta.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import fr.inria.arles.yarta.core.YUtils;
import fr.inria.arles.yarta.core.YartaWrapper;
import fr.inria.arles.yarta.resources.Event;
import fr.inria.arles.yarta.resources.Place;
import fr.inria.arles.yarta.resources.Topic;

import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Set;

public class EventDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	private Event event;
	private JTextField txtTitle;
	private JTextField txtIsTagged;
	private JTextField txtIsLocated;
	private JTextField txtDescription;
	
	private JButton btnEditIsTagged;
	private JButton btnEditIsLocated;
	
	/**
	 * Create the dialog.
	 */
	public EventDialog() {
		setTitle("Event");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		setBounds(100, 100, 413, 358);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Title:");
		lblNewLabel.setBounds(10, 11, 46, 14);
		contentPanel.add(lblNewLabel);
		
		txtTitle = new JTextField();
		txtTitle.setBounds(85, 11, 198, 20);
		contentPanel.add(txtTitle);
		txtTitle.setColumns(10);
		{
			JLabel label = new JLabel("isTagged:");
			label.setBounds(10, 84, 65, 14);
			contentPanel.add(label);
		}
		{
			txtIsTagged = new JTextField();
			txtIsTagged.setEditable(false);
			txtIsTagged.setColumns(500);
			txtIsTagged.setBounds(85, 81, 198, 20);
			contentPanel.add(txtIsTagged);
		}
		{
			btnEditIsTagged = new JButton("Edit");
			btnEditIsTagged.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onEditIsTaggedClicked();
				}
			});
			btnEditIsTagged.setEnabled(false);
			btnEditIsTagged.setBounds(293, 80, 57, 23);
			contentPanel.add(btnEditIsTagged);
		}
		{
			JLabel lblIslocated = new JLabel("isLocated:");
			lblIslocated.setBounds(10, 113, 65, 14);
			contentPanel.add(lblIslocated);
		}
		{
			txtIsLocated = new JTextField();
			txtIsLocated.setEditable(false);
			txtIsLocated.setColumns(500);
			txtIsLocated.setBounds(85, 110, 198, 20);
			contentPanel.add(txtIsLocated);
		}
		{
			btnEditIsLocated = new JButton("Edit");
			btnEditIsLocated.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onEditIsLocatedClicked();
				}
			});
			btnEditIsLocated.setEnabled(false);
			btnEditIsLocated.setBounds(293, 109, 57, 23);
			contentPanel.add(btnEditIsLocated);
		}
		{
			JLabel lblDescription = new JLabel("Description:");
			lblDescription.setBounds(10, 45, 65, 14);
			contentPanel.add(lblDescription);
		}
		{
			txtDescription = new JTextField();
			txtDescription.setColumns(10);
			txtDescription.setBounds(85, 45, 198, 20);
			contentPanel.add(txtDescription);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Update");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						onUpdateClicked();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						onCancelClicked();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		setLocationRelativeTo(null);
	}
	
	public void setEvent(Event event) {
		this.event = event;
		
		txtTitle.setText(event.getTitle());
		txtDescription.setText(event.getDescription());
		
		btnEditIsLocated.setEnabled(true);
		btnEditIsTagged.setEnabled(true);
		
		updateIsTagged();
		updateIsLocated();
	}
	
	private void updateIsTagged() {
		String strIsTagged = "";
		
		Set<Topic> isTaggedTopics = event.getIsTagged();
		for (Topic tpc : isTaggedTopics) {
			strIsTagged += (YUtils.resourceAsString(tpc) + "; ");
		}
		
		txtIsTagged.setText(strIsTagged);
	}
	
	private void updateIsLocated() {
		String strIsLocated = "";
		
		Set<Place> isLocatedPlaces = event.getIsLocated();
		for (Place plc : isLocatedPlaces) {
			strIsLocated += (YUtils.resourceAsString(plc) + ";");
		}
		
		txtIsLocated.setText(strIsLocated);
	}
	
	private void onEditIsTaggedClicked() {
		ResourceLinkDialog dialog = new ResourceLinkDialog(event, ResourceLinkDialog.TYPE_ISTAGGED);
		dialog.setVisible(true);
		updateIsTagged();
	}
	
	private void onEditIsLocatedClicked() {
		ResourceLinkDialog dialog = new ResourceLinkDialog(event, ResourceLinkDialog.TYPE_ISLOCATED);
		dialog.setVisible(true);
		updateIsLocated();
	}
	
	private void onUpdateClicked() {
		String title = txtTitle.getText();
		String description = txtDescription.getText();
		if (event == null) {
			YartaWrapper.getInstance().createEvent(title, description);
		} else {
			YartaWrapper.getInstance().updateEvent(event.getUniqueId(), title, description);
		}
		dispose();
	}
	
	private void onCancelClicked() {
		dispose();
	}
}
