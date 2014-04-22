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
import fr.inria.arles.yarta.resources.Topic;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Set;

public class TopicDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField txtTitle;

	private Topic topic;
	private JTextField txtIsTagged;
	private JButton btnEditIsTagged;

	/**
	 * Create the dialog.
	 */
	public TopicDialog() {
		setTitle("Topic");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		setBounds(100, 100, 413, 358);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblNewLabel = new JLabel("Title:");
			lblNewLabel.setBounds(10, 11, 46, 14);
			contentPanel.add(lblNewLabel);
		}
		{
			txtTitle = new JTextField();
			txtTitle.setBounds(85, 11, 198, 20);
			contentPanel.add(txtTitle);
			txtTitle.setColumns(10);
		}
		{
			JLabel lblIsTagged = new JLabel("isTagged:");
			lblIsTagged.setBounds(10, 49, 65, 14);
			contentPanel.add(lblIsTagged);
		}
		{
			txtIsTagged = new JTextField();
			txtIsTagged.setEditable(false);
			txtIsTagged.setColumns(500);
			txtIsTagged.setBounds(85, 46, 198, 20);
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
			btnEditIsTagged.setBounds(293, 45, 57, 23);
			contentPanel.add(btnEditIsTagged);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
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
	
	public void setTopic(Topic topic) {
		this.topic = topic;
		
		txtTitle.setText(topic.getTitle());
		
		btnEditIsTagged.setEnabled(true);
		updateIsTagged();
	}
	
	private void onClickUpdate() {
		String title = txtTitle.getText();
		
		if (topic == null) {
			YartaWrapper.getInstance().createTopic(title);
		} else {
			YartaWrapper.getInstance().updateTopic(topic.getUniqueId(), title);
		}
		dispose();
	}
	
	private void onClickCancel() {
		dispose();
	}
	
	private void onEditIsTaggedClicked() {
		ResourceLinkDialog dialog = new ResourceLinkDialog(topic, ResourceLinkDialog.TYPE_ISTAGGED);
		dialog.setVisible(true);
		updateIsTagged();
	}
	
	private void updateIsTagged() {
		String strIsTagged = "";
		
		Set<Topic> isTaggedTopics = topic.getIsTagged();
		for (Topic tpc : isTaggedTopics) {
			strIsTagged += (YUtils.resourceAsString(tpc) + "; ");
		}
		
		txtIsTagged.setText(strIsTagged);
	}
}
