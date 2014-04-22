package fr.inria.arles.yarta.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;

import fr.inria.arles.yarta.core.YUtils;
import fr.inria.arles.yarta.core.YartaWrapper;
import fr.inria.arles.yarta.resources.Place;
import fr.inria.arles.yarta.resources.Topic;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Set;

public class PlaceDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField txtName;
	private JTextField txtLatitude;
	private JTextField txtLongitude;
	private JTextField txtIsTagged;
	private JTextField txtIsLocated;
	
	private JButton btnEditIsTagged;
	private JButton btnEditIsLocated;

	private Place place;
	/**
	 * Create the dialog.
	 */
	public PlaceDialog() {
		setTitle("Place");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		setBounds(100, 100, 413, 358);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblName = new JLabel("Name:");
			lblName.setBounds(10, 14, 65, 14);
			contentPanel.add(lblName);
		}
		{
			txtName = new JTextField();
			txtName.setColumns(10);
			txtName.setBounds(85, 11, 198, 20);
			contentPanel.add(txtName);
		}
		{
			JLabel lblLatitude = new JLabel("Latitude:");
			lblLatitude.setBounds(10, 42, 65, 14);
			contentPanel.add(lblLatitude);
		}
		{
			txtLatitude = new JTextField();
			txtLatitude.setColumns(10);
			txtLatitude.setBounds(85, 39, 198, 20);
			contentPanel.add(txtLatitude);
		}
		{
			JLabel lblLongitude = new JLabel("Longitude:");
			lblLongitude.setBounds(10, 70, 65, 14);
			contentPanel.add(lblLongitude);
		}
		{
			txtLongitude = new JTextField();
			txtLongitude.setColumns(10);
			txtLongitude.setBounds(85, 67, 198, 20);
			contentPanel.add(txtLongitude);
		}
		{
			JLabel lblIsTagged = new JLabel("isTagged:");
			lblIsTagged.setBounds(10, 104, 65, 14);
			contentPanel.add(lblIsTagged);
		}
		{
			txtIsTagged = new JTextField();
			txtIsTagged.setEditable(false);
			txtIsTagged.setColumns(500);
			txtIsTagged.setBounds(85, 101, 198, 20);
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
			btnEditIsTagged.setBounds(293, 100, 57, 23);
			contentPanel.add(btnEditIsTagged);
		}
		{
			JLabel lblIsLocated = new JLabel("isLocated:");
			lblIsLocated.setBounds(10, 133, 65, 14);
			contentPanel.add(lblIsLocated);
		}
		{
			txtIsLocated = new JTextField();
			txtIsLocated.setEditable(false);
			txtIsLocated.setColumns(500);
			txtIsLocated.setBounds(85, 130, 198, 20);
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
			btnEditIsLocated.setBounds(293, 129, 57, 23);
			contentPanel.add(btnEditIsLocated);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Update");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						onClickUpdate();
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
		setLocationRelativeTo(null);
	}
	
	public void setPlace(Place place) {
		this.place = place;
		
		txtName.setText(place.getName());
		txtLatitude.setText("" + place.getLatitude());
		txtLongitude.setText("" + place.getLongitude());
		
		btnEditIsLocated.setEnabled(true);
		btnEditIsTagged.setEnabled(true);
		
		updateIsTagged();
		updateIsLocated();
	}
	
	private void onClickUpdate() {
		String name = txtName.getText();
		float latitude = 0;
		float longitude = 0;
		
		try {
			latitude = Float.parseFloat(txtLatitude.getText());
			longitude = Float.parseFloat(txtLongitude.getText());
		}
		catch (Exception ex) {
			YUtils.showError(getContentPane(), ex.getMessage());
			return;
		}
		
		if (place == null) {
			YartaWrapper.getInstance().createPlace(name, latitude, longitude);
		} else {
			YartaWrapper.getInstance().updatePlace(place.getUniqueId(), name, latitude, longitude);
		}
		dispose();
	}
	
	private void onClickCancel() {
		dispose();
	}
	
	private void onEditIsTaggedClicked() {
		ResourceLinkDialog dialog = new ResourceLinkDialog(place, ResourceLinkDialog.TYPE_ISTAGGED);
		dialog.setVisible(true);
		updateIsTagged();
	}
	
	private void onEditIsLocatedClicked() {
		ResourceLinkDialog dialog = new ResourceLinkDialog(place, ResourceLinkDialog.TYPE_ISLOCATED);
		dialog.setVisible(true);
		updateIsLocated();
	}
	
	private void updateIsTagged() {
		String strIsTagged = "";
		
		Set<Topic> isTaggedTopics = place.getIsTagged();
		for (Topic topic : isTaggedTopics) {
			strIsTagged += (YUtils.resourceAsString(topic) + "; ");
		}
		
		txtIsTagged.setText(strIsTagged);
	}
	
	private void updateIsLocated() {
		String strIsLocated = "";
		
		Set<Place> isLocatedPlaces = place.getIsLocated();
		for (Place plc : isLocatedPlaces) {
			strIsLocated += (YUtils.resourceAsString(plc) + "; ");
		}
		
		txtIsLocated.setText(strIsLocated);
	}
}
