package fr.inria.arles.yarta.desktop.library;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JProgressBar;
import javax.swing.JLabel;

import fr.inria.arles.yarta.desktop.library.util.Strings;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloaderDialog extends JDialog implements Runnable {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	private JProgressBar progressBar;

	/**
	 * Create the dialog.
	 */
	public DownloaderDialog() {
		setResizable(false);
		setTitle(Strings.DownloaderTitle);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		setBounds(100, 100, 416, 141);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			progressBar = new JProgressBar();
			progressBar.setBounds(10, 45, 380, 14);
			contentPanel.add(progressBar);
		}

		JLabel lblNewLabel = new JLabel(Strings.DownloaderText);
		lblNewLabel.setBounds(10, 20, 380, 14);
		contentPanel.add(lblNewLabel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
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

	private boolean interrupted;
	private String result;
	private Thread downloadThread;

	public String doModal() {
		downloadThread = new Thread(this);
		downloadThread.start();
		setVisible(true);
		return result;
	}

	private void onCancelClicked() {
		interrupted = true;
		result = null;
		dispose();
	}

	private void onDownloadProgress(long progress) {
		progressBar.setValue((int) progress);
	}

	@Override
	public void run() {
		try {
			URL url = new URL(Strings.DownloaderYartaLink);
			URLConnection conn = url.openConnection();

			File folder = File.createTempFile("temp", null);
			folder.delete();
			folder.mkdir();
			result = folder.getAbsolutePath() + "/yarta.jar";

			long percent = 0;

			long lSize = conn.getContentLength();
			long lCurrent = 0;

			InputStream in = conn.getInputStream();
			OutputStream out = new FileOutputStream(result);
			byte[] buffer = new byte[1024];

			int length;
			while ((length = in.read(buffer)) > 0 && !interrupted) {
				out.write(buffer, 0, length);
				lCurrent += length;

				if (lCurrent * 100 / lSize > percent) {
					percent = lCurrent * 100 / lSize;
					onDownloadProgress(percent);
				}
			}
			in.close();
			out.close();
			dispose();
		} catch (Exception ex) {
			// error occurred!
			ex.printStackTrace();
			result = null;
			dispose();
		}
	}
}
