package fr.inria.arles.yarta.desktop.library;

import fr.inria.arles.yarta.desktop.library.util.Installer;

public class DependencyCheck {

	public static boolean ensureYartaIsInstalledAndRunning() {
		Installer installer = new Installer();

		// check if installed
		if (!installer.isInstalled()) {
			installer.downloadAndInstall();
		}

		// check again
		if (installer.isInstalled()) {

			if (!installer.isRunning()) {

				installer.launchService();

				int timeout = 20;
				while (timeout > 0 && !installer.isRunning()) {
					try {
						Thread.sleep(500);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					timeout--;
				}
				return timeout > 0;
			} else {
				return true;
			}
		}
		return false;
	}
}
