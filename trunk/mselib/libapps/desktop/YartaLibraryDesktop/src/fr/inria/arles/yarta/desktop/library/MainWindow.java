package fr.inria.arles.yarta.desktop.library;

import java.awt.Desktop;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Color;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;

import fr.inria.arles.yarta.desktop.library.plugins.OauthPlugin;
import fr.inria.arles.yarta.desktop.library.plugins.PluginManager;
import fr.inria.arles.yarta.desktop.library.util.Settings;
import fr.inria.arles.yarta.desktop.library.util.Strings;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainWindow extends JFrame implements ItemListener, Runnable {

	public static final int SYNC_ON_DEMAND = 0;
	public static final int SYNC_HOURLY = 1;
	public static final int SYNC_DAILY = 2;

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	private JRadioButton radioOnDemand, radioHourly, radioDaily;

	public static void main(String[] args) {
		MainWindow main = new MainWindow();
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main.setVisible(true);
	}

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		setResizable(false);
		setTitle(Strings.AppTitle);
		setBounds(0, 0, 525, 386);

		contentPane = new JPanel();
		contentPane.setBackground(colorBackground);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel(Strings.SyncTitle);
		lblNewLabel.setBounds(10, 11, 774, 14);
		lblNewLabel.setForeground(colorText);
		contentPane.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel(Strings.SyncText);
		lblNewLabel_1.setBounds(10, 28, 388, 45);
		lblNewLabel_1.setForeground(colorSubText);
		contentPane.add(lblNewLabel_1);

		JButton btnSyncNow = new JButton(Strings.SyncNow);
		btnSyncNow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onSyncNow();
			}
		});
		btnSyncNow.setBounds(408, 24, 100, 23);
		contentPane.add(btnSyncNow);

		JLabel lblSocialSyncInterval = new JLabel(Strings.SyncInterval);
		lblSocialSyncInterval.setForeground(colorText);
		lblSocialSyncInterval.setBounds(10, 80, 388, 14);
		contentPane.add(lblSocialSyncInterval);

		JLabel lblLetUsKnow = new JLabel(Strings.SyncIntervalText);
		lblLetUsKnow.setForeground(colorSubText);
		lblLetUsKnow.setBounds(10, 97, 774, 14);
		contentPane.add(lblLetUsKnow);

		radioOnDemand = new JRadioButton(Strings.SyncOnDemand);
		radioOnDemand.setBackground(colorBackground);
		radioOnDemand.setForeground(colorText);
		radioOnDemand.setBounds(10, 118, 109, 23);
		radioOnDemand.addItemListener(this);
		contentPane.add(radioOnDemand);

		radioHourly = new JRadioButton(Strings.SyncHourly);
		radioHourly.setBackground(colorBackground);
		radioHourly.setBounds(127, 118, 109, 23);
		radioHourly.setForeground(colorText);
		radioHourly.addItemListener(this);
		contentPane.add(radioHourly);

		radioDaily = new JRadioButton(Strings.SyncDaily);
		radioDaily.setBackground(colorBackground);
		radioDaily.setBounds(244, 118, 109, 23);
		radioDaily.setForeground(colorText);
		radioDaily.addItemListener(this);
		contentPane.add(radioDaily);

		ButtonGroup group = new ButtonGroup();
		group.add(radioOnDemand);
		group.add(radioHourly);
		group.add(radioDaily);

		loadPluginsUI(contentPane);

		setLocationRelativeTo(null);

		refreshPlugins();

		loadSettings();
	}

	private Map<String, JLabel> mLabels = new HashMap<String, JLabel>();
	private Map<String, JButton> mButtons = new HashMap<String, JButton>();

	private void loadPluginsUI(JPanel contentPane) {
		final int pluginUIHeight = 60;
		final int pluginUIStart = 150;

		List<OauthPlugin> plugins = plgMgr.getPlugins();

		for (int i = 0; i < plugins.size(); i++) {
			OauthPlugin plugin = plugins.get(i);
			final String pluginName = plugin.getName();

			JLabel lblPlugin = new JLabel(pluginName);
			lblPlugin.setForeground(colorText);
			lblPlugin
					.setBounds(10, pluginUIStart + i * pluginUIHeight, 388, 14);
			contentPane.add(lblPlugin);

			JLabel lblStatus = new JLabel(Strings.PluginNotConnected);
			lblStatus.setForeground(colorSubText);
			lblStatus.setBounds(10, pluginUIStart + i * pluginUIHeight + 17,
					388, 28);
			contentPane.add(lblStatus);

			JButton button = new JButton(Strings.PluginConnect);
			button.setBounds(408, pluginUIStart + i * pluginUIHeight + 20, 100,
					23);
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					onClickConnect(pluginName);
				}
			});
			contentPane.add(button);

			mLabels.put(pluginName, lblStatus);
			mButtons.put(pluginName, button);
		}
	}

	private void refreshPlugins() {
		for (OauthPlugin plugin : plgMgr.getPlugins()) {
			JLabel label = mLabels.get(plugin.getName());
			JButton button = mButtons.get(plugin.getName());

			if (plugin.isLoggedIn()) {
				String userName = plugin.getUserName();
				long lastSync = plugin.getLastSync();

				String statusFmt = Strings.PluginConnected;
				String status;

				if (lastSync == 0) {
					status = String.format(statusFmt, userName,
							Strings.PluginNever);
				} else {
					status = String.format(statusFmt, userName,
							sdf.format(new Date(lastSync)));
				}

				label.setText(status);
				button.setText(Strings.PluginDisconnect);
			} else {
				label.setText(Strings.PluginNotConnected);
				button.setText(Strings.PluginConnect);
			}
		}
	}

	private void loadSettings() {
		long syncType = settings.getLong(Settings.SyncType);
		switch ((int) syncType) {
		case SYNC_ON_DEMAND:
			radioOnDemand.setSelected(true);
			break;
		case SYNC_HOURLY:
			radioHourly.setSelected(true);
			break;
		case SYNC_DAILY:
			radioDaily.setSelected(true);
			break;
		}
	}

	private void saveSettings() {
		long syncType = SYNC_ON_DEMAND;
		if (radioHourly.isSelected()) {
			syncType = SYNC_HOURLY;
		} else if (radioDaily.isSelected()) {
			syncType = SYNC_DAILY;
		}

		settings.setLong(Settings.SyncType, syncType);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		saveSettings();
		PluginManager.getInstance().schedulePluginsSync();
	}

	private void onSyncNow() {
		setTitle(Strings.AppTitleSyncing);
		PluginManager.getInstance().sync(this);
	}

	@Override
	public void run() {
		setTitle(Strings.AppTitle);
		refreshPlugins();
	}

	/**
	 * Copies the specfied strign to clip board.
	 * 
	 * @param string
	 * 
	 * @return true/false.
	 */
	private boolean copyToClipboard(String string) {
		boolean result = false;

		try {
			StringSelection stringSelection = new StringSelection(string);
			Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
			clpbrd.setContents(stringSelection, null);
			result = true;
		} catch (Exception ex) {
		}

		return result;
	}

	/**
	 * Opens a web page using the default browser.
	 * 
	 * @param urlString
	 * 
	 * @return true/false
	 */
	private boolean openWebpage(String urlString) {
		try {
			Desktop.getDesktop().browse(new URL(urlString).toURI());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			Runtime.getRuntime().exec("xdg-open " + urlString);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	private void onClickConnect(String pluginName) {
		OauthPlugin plugin = plgMgr.getPlugin(pluginName);
		if (plugin.isLoggedIn()) {
			performLogOff(plugin);
		} else {
			performLogIn(plugin);
		}
		refreshPlugins();
	}

	private void performLogOff(OauthPlugin plugin) {
		plugin.clearData();
	}

	private void performLogIn(OauthPlugin plugin) {
		Object[] options = { Strings.PluginLoginOpen, Strings.PluginLoginCopy,
				Strings.PluginLoginCancel };
		int optionSelected = JOptionPane.showOptionDialog(null,
				Strings.PluginLoginMessage, Strings.PluginLoginTitle,
				JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
				options, options[0]);

		switch (optionSelected) {
		case 0:
			if (openWebpage(plugin.getAuthorizationURL())) {
				startMonitor(plugin.getName(), plugin.getSessionId());
			} else {
				showError(Strings.PluginLoginError);
			}
			break;
		case 1:
			if (copyToClipboard(plugin.getAuthorizationURL())) {
				startMonitor(plugin.getName(), plugin.getSessionId());
			} else {
				showError(Strings.PluginLoginError);
			}
			break;
		}
	}

	/**
	 * Starts a monitoring thread which waits for the log-in for the current
	 * session id;
	 * 
	 * @param pluginName
	 * @param sessionId
	 */
	private void startMonitor(String pluginName, String sessionId) {
		new Thread(new MonitorTask(pluginName, sessionId)).start();
	}

	/**
	 * The monitoring runnable task.
	 */
	private class MonitorTask implements Runnable {
		private String sessionId;
		private String pluginName;

		public MonitorTask(String pluginName, String sessionId) {
			this.sessionId = sessionId;
			this.pluginName = pluginName;
		}

		@Override
		public void run() {
			int timeout = 120; // seconds
			int leap = 5; // seconds

			String code = "";

			while (timeout > 0) {
				code = readURL(OauthPlugin.DEFAULT_REDIRECT_URL + "?sid="
						+ sessionId + "&read");

				if (code.length() > 0) {
					break;
				}

				try {
					Thread.sleep(leap * 1000);
					timeout -= leap;
				} catch (Exception ex) {
				}
			}

			if (code.length() > 0) {
				readURL(OauthPlugin.DEFAULT_REDIRECT_URL + "?sid=" + sessionId
						+ "&remove");

				plgMgr.getPlugin(pluginName).authorize(code);
				refreshPlugins();
			}
		}
	}

	private String readURL(String url) {
		try {
			URL website = new URL(url);
			URLConnection connection = website.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

			StringBuilder response = new StringBuilder();
			String inputLine;

			while ((inputLine = in.readLine()) != null)
				response.append(inputLine);

			in.close();

			return response.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

	private void showError(String message) {
		JOptionPane.showMessageDialog(this, message, Strings.PluginError,
				JOptionPane.ERROR_MESSAGE);
	}

	private Settings settings = new Settings();
	private SimpleDateFormat sdf = new SimpleDateFormat("hh:mm, dd-mm-yy");
	private final PluginManager plgMgr = PluginManager.getInstance();
	private final Color colorText = new Color(0x00, 0x00, 0x00);
	private final Color colorSubText = new Color(0x34, 0x49, 0x5e);
	private final Color colorBackground = new Color(0xfe, 0xfe, 0xfe);
}
