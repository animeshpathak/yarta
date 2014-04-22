package fr.inria.arles.yarta.desktop.library.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JOptionPane;

import fr.inria.arles.yarta.desktop.library.DownloaderDialog;
import fr.inria.arles.yarta.desktop.library.RMIUtil;
import fr.inria.arles.yarta.desktop.library.Service;

/**
 * Helper class which permits (un)installing & updating the application.
 */
public class Installer {

	public static final String InstallPath = System.getProperty("user.home")
			+ "/.yarta/";
	public static final String FilesPath = InstallPath + "res/";

	private static final String[] files = { "ibicoop.properties",
			"ibicoopcacert.crt", "mse-1.2.rdf", "policies" };

	private String currentJarPath;
	private String installedJarPath;

	private Exception error;

	public Installer() {
		String jarFile = System.getProperty("java.class.path");

		if (!jarFile.endsWith("jar")) {
			jarFile = "yarta.jar";
		}
		currentJarPath = new File(jarFile).getAbsolutePath();
		installedJarPath = InstallPath + "yarta.jar";
	}

	/**
	 * Checks whether Yarta is installed on the current machine.
	 * 
	 * @return
	 */
	public boolean isInstalled() {
		return checkFilesConsistency();
	}

	/**
	 * Checks if Yarta Service is running.
	 * 
	 * @return
	 */
	public boolean isRunning() {
		Service service = RMIUtil.getObject(Service.Name);
		boolean running = service != null;
		service = null;
		return running;
	}

	/**
	 * Runs a jar file with the specified arguments;
	 * 
	 * @param jarPath
	 * @param args
	 * @return
	 */
	private Process runJar(String jarPath, String... args) {
		String command = "java -jar " + jarPath;
		if (isWindows()) {
			command = "javaw -jar " + jarPath;
		}

		for (String arg : args) {
			command += " " + arg;
		}
		try {
			return Runtime.getRuntime().exec(command);
		} catch (Exception ex) {
		}
		return null;
	}

	/**
	 * Launches the application.
	 * 
	 * @return
	 */
	public boolean launchApp() {
		return runJar(installedJarPath) != null;
	}

	/**
	 * Returns the timestamp of yarta.jar from Internet.
	 * 
	 * @return
	 */
	private long getLastModifiedRemote() {
		long lastModified = 0;
		try {
			URL url = new URL(Strings.DownloaderYartaLink);
			URLConnection conn = url.openConnection();
			lastModified = conn.getLastModified();
		} catch (Exception ex) {
		}
		return lastModified;
	}

	/**
	 * Checks for updates, and if there are any, asks users and update. Returns
	 * false otherwise.
	 * 
	 * @return true/false
	 */
	public boolean checkAndUpdate() {
		long lastModifiedLocal = new File(installedJarPath).lastModified();
		long lastModifiedRemote = getLastModifiedRemote();

		if (lastModifiedRemote > lastModifiedLocal) {

			int option = 0;
			try {
				option = JOptionPane.showConfirmDialog(null,
						Strings.InstallerUpdatePrompt,
						Strings.InstallerUpdateTitle,
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception ex) {
				// system does not have UI
				option = JOptionPane.OK_OPTION;
			}
			if (option == JOptionPane.OK_OPTION) {
				String downloadedJarFile = performDownload();
				if (downloadedJarFile != null) {
					return performInstallerLaunch(downloadedJarFile);
				}
			}
		}

		return false;
	}

	public boolean launchService() {
		return runJar(installedJarPath, "/start") != null;
	}

	public boolean stopService() {
		if (!new File(installedJarPath).exists()) {
			return true;
		}
		try {
			Process process = runJar(installedJarPath, "/stop");
			process.waitFor();
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public boolean install() {
		boolean hasUI = true;
		try {
			int selection = JOptionPane.showConfirmDialog(null,
					Strings.InstallerPrompt, Strings.InstallerPromptTitle,
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.INFORMATION_MESSAGE);

			if (selection == JOptionPane.OK_OPTION) {
				return performInstallation();
			}
		} catch (Exception ex) {
			hasUI = false;
		}

		if (!hasUI) {
			return performInstallation();
		}
		return false;
	}

	/**
	 * This should download and install Yarta.
	 * 
	 * When the function returns true Yarta will be installed.
	 * 
	 * @return true/false
	 */
	public boolean downloadAndInstall() {
		int selection = JOptionPane.showConfirmDialog(null,
				Strings.InstallerDownloadPrompt,
				Strings.InstallerDownloadTitle, JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.INFORMATION_MESSAGE);

		if (selection == JOptionPane.OK_OPTION) {
			String downloadedJarFile = performDownload();
			if (downloadedJarFile != null) {
				return performInstaller(downloadedJarFile);
			}
		}
		return false;
	}

	private String performDownload() {
		DownloaderDialog dlg = new DownloaderDialog();
		return dlg.doModal();
	}

	private boolean performInstaller(String downloadedJarFile) {
		boolean result = false;
		try {
			Process process = runJar(downloadedJarFile, "/install");
			process.waitFor();
			result = true;
		} catch (Exception ex) {
		}

		return result;
	}

	private boolean performInstallerLaunch(String downloadedJarFile) {
		return runJar(downloadedJarFile, "/install") != null;
	}

	public boolean uninstall() {
		return remove(new File(InstallPath))
				& removeFromStartup(installedJarPath);
	}

	private boolean remove(File f) {
		boolean result = true;
		if (f.isDirectory()) {
			for (File c : f.listFiles()) {
				result &= remove(c);
			}
		}

		return result & f.delete();
	}

	private boolean checkFilesConsistency() {
		for (String file : files) {
			if (!new File(FilesPath + file).exists()) {
				return false;
			}
		}
		return true;
	}

	private boolean performInstallation() {
		new File(FilesPath).mkdirs();

		if (!copyFile(currentJarPath, installedJarPath)) {
			printError(error);
			return false;
		}

		if (!putAtStartup(installedJarPath)) {
			printError(error);
			return false;
		}

		for (String file : files) {
			if (!dropFile(file, FilesPath)) {
				printError(error);
				return false;
			}
		}
		return true;
	}

	private boolean dropFile(String fileName, String folder) {
		try {
			InputStream in = getClass().getResourceAsStream(
					"/fr/inria/arles/yarta/desktop/library/files/" + fileName);

			OutputStream out = new FileOutputStream(folder + fileName);
			byte[] buffer = new byte[1024];

			int length;
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
			in.close();
			out.close();
			return true;
		} catch (Exception ex) {
			error = ex;
			return false;
		}
	}

	private void printError(Exception ex) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		sw.toString();

		JOptionPane.showMessageDialog(null, sw.toString());
	}

	private boolean removeFromStartup(String path) {
		if (isWindows()) {
			try {
				WinRegistry.deleteValue(WinRegistry.HKEY_CURRENT_USER,
						"SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run",
						"Yarta Middleware");
			} catch (Exception ex) {
				error = ex;
				return false;
			}
		} else {
			return removeLine(System.getProperty("user.home") + "/.profile",
					path);
		}

		return true;
	}

	private boolean removeLine(String file, String line) {
		File inputFile = new File(file);
		File tempFile = new File(file + ".tmp");

		try {
			BufferedReader reader = new BufferedReader(
					new FileReader(inputFile));

			PrintWriter writer = new PrintWriter(tempFile);

			String currentLine;

			while ((currentLine = reader.readLine()) != null) {
				// trim newline when comparing with lineToRemove
				String trimmedLine = currentLine.trim();
				if (trimmedLine.contains(line))
					continue;
				writer.println(currentLine);
			}

			reader.close();
			writer.close();

			boolean successful = tempFile.renameTo(inputFile);
			return successful;
		} catch (Exception ex) {
			error = ex;
			return false;
		}
	}

	private boolean putAtStartup(String path) {
		if (isWindows()) {
			try {
				WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER,
						"SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run",
						"Yarta Middleware", "javaw -jar " + path + " /startup");
			} catch (Exception ex) {
				error = ex;
				return false;
			}
		} else {
			// treat as Linux

			removeFromStartup(path);
			try {
				PrintWriter writer = new PrintWriter(new FileOutputStream(
						System.getProperty("user.home") + "/.profile", true));
				writer.println("java -jar " + path + " /startup &");
				writer.close();
			} catch (Exception ex) {
				error = ex;
				return false;
			}
		}

		return true;
	}

	/**
	 * Copies a file from one point to another.
	 * 
	 * @param source
	 * @param destination
	 * @return true/false
	 */
	private boolean copyFile(String source, String destination) {
		try {
			InputStream inStream = null;
			OutputStream outStream = null;
			try {

				File file1 = new File(source);
				File file2 = new File(destination);

				inStream = new FileInputStream(file1);
				outStream = new FileOutputStream(file2);

				byte[] buffer = new byte[1024];

				int length;
				while ((length = inStream.read(buffer)) > 0) {
					outStream.write(buffer, 0, length);
				}

				if (inStream != null)
					inStream.close();
				if (outStream != null)
					outStream.close();
			} catch (IOException e) {
				error = e;
				return false;
			}
		} catch (Exception ex) {
			error = ex;
			return false;
		}
		return true;
	}

	/**
	 * Checks if the current OS is Windows.
	 * 
	 * @return true/false
	 */
	private boolean isWindows() {
		return System.getProperty("os.name").contains("Windows");
	}
}
