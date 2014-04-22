package fr.inria.arles.yarta;

import java.awt.EventQueue;

import javax.swing.JDialog;

import fr.inria.arles.yarta.core.YartaWrapper;
import fr.inria.arles.yarta.ui.LoginDialog;
import fr.inria.arles.yarta.ui.MainWindow;

public class Main {
	public static void main(String args[]) {
		final YartaWrapper wrapper = YartaWrapper.getInstance();

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginDialog dialog = new LoginDialog(YartaWrapper
							.getInstance());
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);

					if (dialog.getUserId() != null) {
						MainWindow window = new MainWindow();
						wrapper.init(dialog.getUserId(), window);
						if (wrapper.isInitialized()) {
							window.initialize();
							window.setVisible(true);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
