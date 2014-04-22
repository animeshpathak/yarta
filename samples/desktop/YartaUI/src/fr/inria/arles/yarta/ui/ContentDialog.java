package fr.inria.arles.yarta.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JTextField;

import fr.inria.arles.yarta.core.YUtils;
import fr.inria.arles.yarta.core.YartaWrapper;
import fr.inria.arles.yarta.resources.Content;
import fr.inria.arles.yarta.resources.Topic;

public class ContentDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField txtTitle;
	private JTextField txtSource;
	private JTextField txtIdentifier;
	private JTextField txtFormat;
	
	private Content content;
	private JTextField txtIsTagged;
	private JButton btnEditIsTagged;

	/**
	 * Create the dialog.
	 */
	public ContentDialog() {
		setTitle("Content");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
		setModal(true);
		setBounds(100, 100, 413, 358);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JLabel lblTitle = new JLabel("Title:");
		lblTitle.setBounds(10, 14, 65, 14);
		contentPanel.add(lblTitle);

		txtTitle = new JTextField();
		txtTitle.setBounds(85, 11, 198, 20);
		contentPanel.add(txtTitle);
		txtTitle.setColumns(10);
		{
			JLabel lblSource = new JLabel("Source:");
			lblSource.setBounds(10, 43, 65, 14);
			contentPanel.add(lblSource);
		}
		{
			txtSource = new JTextField();
			txtSource.setColumns(10);
			txtSource.setBounds(85, 40, 198, 20);
			contentPanel.add(txtSource);
		}
		{
			JLabel lblIdentifier = new JLabel("Identifier:");
			lblIdentifier.setBounds(10, 74, 65, 14);
			contentPanel.add(lblIdentifier);
		}
		{
			txtIdentifier = new JTextField();
			txtIdentifier.setColumns(10);
			txtIdentifier.setBounds(85, 71, 198, 20);
			contentPanel.add(txtIdentifier);
		}
		{
			JLabel lblFormat = new JLabel("Format:");
			lblFormat.setBounds(10, 102, 65, 14);
			contentPanel.add(lblFormat);
		}
		{
			txtFormat = new JTextField();
			txtFormat.setColumns(10);
			txtFormat.setBounds(85, 99, 198, 20);
			contentPanel.add(txtFormat);
		}
		{
			JLabel lblIsTagged = new JLabel("isTagged:");
			lblIsTagged.setBounds(10, 131, 65, 14);
			contentPanel.add(lblIsTagged);
		}
		{
			txtIsTagged = new JTextField();
			txtIsTagged.setEditable(false);
			txtIsTagged.setColumns(500);
			txtIsTagged.setBounds(85, 128, 198, 20);
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
			btnEditIsTagged.setBounds(293, 127, 57, 23);
			contentPanel.add(btnEditIsTagged);
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
					public void actionPerformed(ActionEvent arg0) {
						onClickCancel();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		setLocationRelativeTo(null);
	}
	
	public void setContent(Content content) {
		this.content = content;
		
		txtTitle.setText(content.getTitle());
		txtSource.setText(content.getSource());
		txtIdentifier.setText(content.getIdentifier());
		txtFormat.setText(content.getFormat());
		
		btnEditIsTagged.setEnabled(true);
		
		updateIsTagged();
	}

	private void onClickUpdate() {
		String title = txtTitle.getText();
		String source = txtSource.getText();
		String identifier = txtIdentifier.getText();
		String format = txtFormat.getText();

		if (content == null) {
			YartaWrapper.getInstance().createContent(title, source, identifier,
					format);
		} else {
			YartaWrapper.getInstance().updateContent(content.getUniqueId(), title, source, identifier, format);
		}

		dispose();
	}

	private void onClickCancel() {
		dispose();
	}
	
	private void onEditIsTaggedClicked() {
		ResourceLinkDialog dialog = new ResourceLinkDialog(content, ResourceLinkDialog.TYPE_ISTAGGED);
		dialog.setVisible(true);
		updateIsTagged();
	}
	
	private void updateIsTagged() {
		String strIsTagged = "";
		
		Set<Topic> isTaggedTopics = content.getIsTagged();
		for (Topic topic : isTaggedTopics) {
			strIsTagged += (YUtils.resourceAsString(topic) + "; ");
		}
		
		txtIsTagged.setText(strIsTagged);
	}
}
