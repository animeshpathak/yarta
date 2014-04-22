package fr.inria.arles.yarta.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JPasswordField;

import fr.inria.arles.yarta.core.YUtils;
import fr.inria.arles.yarta.core.YartaWrapper;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class LoginDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private final JLabel lblUsername = new JLabel("Username:");
	private String title = "Log-In";
	private JTextField txtUserField;
	private JPasswordField txtPasswordField;

	/**
	 * Create the dialog.
	 */
	public LoginDialog(YartaWrapper wrapper) {
		super();

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		setBounds(100, 100, 264, 160);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			txtUserField = new JTextField();
			txtUserField.setText("conference.bot@inria.fr");
			txtUserField.setBounds(86, 16, 152, 20);
			contentPanel.add(txtUserField);
			txtUserField.setColumns(10);
		}
		lblUsername.setBounds(10, 11, 66, 31);
		contentPanel.add(lblUsername);

		JLabel label = new JLabel("Password:");
		label.setBounds(10, 47, 66, 31);
		contentPanel.add(label);

		txtPasswordField = new JPasswordField();
		txtPasswordField.setBounds(86, 52, 152, 20);
		contentPanel.add(txtPasswordField);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						onOkClicked();
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
						onCancelClicked();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}

		setLocationRelativeTo(null);
		setTitle(title);
	}

	private void onOkClicked() {
		userId = txtUserField.getText();

		if (!userId.isEmpty()) {
			dispose();
		} else {
			YUtils.showError(getContentPane(), "Empty user name!");
		}
	}
	
	public String getUserId() {
		return userId;
	}

	private void onCancelClicked() {
		dispose();
	}
	
	private String userId;
}
