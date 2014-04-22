package fr.inria.arles.yarta.desktop.library;

import fr.inria.arles.yarta.desktop.library.util.Installer;
import fr.inria.arles.yarta.desktop.library.util.Strings;

/**
 * Yarta Library implementation for PC.
 */
public class Main {

	public static void main(String[] args) {
		// set mac dock title
		System.setProperty("com.apple.mrj.application.apple.menu.about.name",
				Strings.AppTitle);

		Installer installer = new Installer();
		if (!installer.isInstalled()) {
			if (installer.install()) {
				installer.launchApp();
			}
		} else {
			if (args.length == 0) {
				installer.launchService();

				int timeout = 10;
				while (timeout > 0 && !onAppStart()) {
					try {
						Thread.sleep(500);
					} catch (Exception ex) {
					}
					timeout--;
				}
			} else {
				String option = args[0];

				if (option.contains("startup")) {
					log = false;
				}
				if (option.contains("uninstall")) {
					installer.uninstall();
				} else if (option.contains("install")) {
					installer.stopService();
					installer.install();
					installer.launchApp();
				} else if (option.contains("start")) {
					if (!installer.checkAndUpdate()) {
						onServiceStart();
					}
				} else if (option.contains("stop")) {
					onServiceStop();
				}
			}
		}
	}

	private static boolean onAppStart() {
		Service service = RMIUtil.getObject(Service.Name);
		try {
			service.showUI();
		} catch (Exception ex) {
			return false;
		}
		service = null;
		return true;
	}

	private static void onServiceStart() {
		RMIUtil.startRegistry();

		if (RMIUtil.getObject(Service.Name) != null) {
			print("Service was already started.");
		} else {
			try {
				LibraryService service = new LibraryService();
				RMIUtil.setObject(Service.Name, service);
				service.start();

				Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

					@Override
					public void run() {
						Service service = RMIUtil.getObject(Service.Name);
						if (service != null) {
							try {
								service.stop();
								service = null;
							} catch (Exception ex) {
							}
						}
					}
				}));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private static void onServiceStop() {
		Service service = RMIUtil.getObject(Service.Name);
		if (service != null) {
			try {
				service.stop();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			print("Service was not started.");
		}
	}

	private static void print(String format, Object... args) {
		if (log) {
			System.out.println(String.format(format, args));
		}
	}

	private static boolean log = true;
}
