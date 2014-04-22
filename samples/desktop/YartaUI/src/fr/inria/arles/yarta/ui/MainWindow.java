package fr.inria.arles.yarta.ui;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import fr.inria.arles.yarta.core.YUtils;
import fr.inria.arles.yarta.core.YartaWrapper;
import fr.inria.arles.yarta.middleware.communication.Message;
import fr.inria.arles.yarta.middleware.communication.Receiver;
import fr.inria.arles.yarta.middleware.msemanagement.MSEApplication;
import fr.inria.arles.yarta.resources.Agent;
import fr.inria.arles.yarta.resources.Content;
import fr.inria.arles.yarta.resources.Event;
import fr.inria.arles.yarta.resources.Place;
import fr.inria.arles.yarta.resources.Topic;
import fr.inria.arles.yarta.ui.ctrl.GraphUI;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainWindow implements GraphUI.EventHandler, MSEApplication,
		Receiver {

	public JFrame frame;
	private GraphUI canvas;
	private String title = "YartaUI";

	class WindowEventHandler extends WindowAdapter {
		public void windowClosing(WindowEvent evt) {
			YartaWrapper.getInstance().uninit();
		}
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
	}

	/**
	 * Initialize the contents of the frame.
	 */
	public void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle(title);
		frame.setLocationRelativeTo(null);
		frame.addWindowListener(new WindowEventHandler());

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnCreate = new JMenu("Create");
		menuBar.add(mnCreate);

		JMenuItem mntmGroup = new JMenuItem("Group");
		mntmGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onCreateNewGroup();
			}
		});
		mnCreate.add(mntmGroup);

		JMenuItem mntmPerson = new JMenuItem("Person");
		mntmPerson.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onCreateNewPerson();
			}
		});
		mnCreate.add(mntmPerson);

		mnCreate.addSeparator();

		JMenuItem mntmContent = new JMenuItem("Content");
		mntmContent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCreateNewContent();
			}
		});
		mnCreate.add(mntmContent);

		JMenuItem mntmEvent = new JMenuItem("Event");
		mntmEvent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCreateNewEvent();
			}
		});
		mnCreate.add(mntmEvent);

		JMenuItem mntmPlace = new JMenuItem("Place");
		mntmPlace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCreateNewPlace();
			}
		});
		mnCreate.add(mntmPlace);

		JMenuItem mntmTopic = new JMenuItem("Topic");
		mntmTopic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCreateNewTopic();
			}
		});
		mnCreate.add(mntmTopic);

		JMenu mnKb = new JMenu("KB");
		menuBar.add(mnKb);

		JMenuItem mntmLoad = new JMenuItem("Load");
		mntmLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onLoadClicked();
			}
		});
		mnKb.add(mntmLoad);

		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onSaveClicked();
			}
		});
		mnKb.add(mntmSave);

		JMenu mnCom = new JMenu("Comm");
		menuBar.add(mnCom);

		JMenuItem mntmSendMessage = new JMenuItem("Message");
		mntmSendMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onSendMessageClicked();
			}
		});
		mnCom.add(mntmSendMessage);

		mnCom.addSeparator();

		JMenuItem mntmSendHello = new JMenuItem("Hello");
		mntmSendHello.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onSendHelloClicked();
			}
		});
		mnCom.add(mntmSendHello);

		JMenuItem mntmSendUpdate = new JMenuItem("Update");
		mntmSendUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onSendUpdateClicked();
			}
		});
		mnCom.add(mntmSendUpdate);

		frame.getContentPane().setLayout(null);

		canvas = new GraphUI();
		canvas.setBounds(0, 0, frame.getWidth(), frame.getHeight());
		canvas.init(this);
		frame.getContentPane().add(canvas);

		frame.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				canvas.setBounds(0, 0, frame.getWidth(), frame.getHeight());
			}
		});

		YartaWrapper.getInstance().setReceiver(this);
		refreshCanvas();
	}

	private synchronized void refreshCanvas() {
		List<Object> objects = new ArrayList<Object>();
		objects.addAll(YartaWrapper.getInstance().getAllResources());
		canvas.setObjects(objects);
	}

	private void onCreateNewPerson() {
		AgentDialog dialog = new AgentDialog();
		dialog.setAgent(AgentDialog.PERSON);
		dialog.setVisible(true);
		refreshCanvas();
	}

	private void onCreateNewGroup() {
		AgentDialog dialog = new AgentDialog();
		dialog.setAgent(AgentDialog.GROUP);
		dialog.setVisible(true);
		refreshCanvas();
	}

	private void onCreateNewContent() {
		ContentDialog dialog = new ContentDialog();
		dialog.setVisible(true);
		refreshCanvas();
	}

	private void onCreateNewTopic() {
		TopicDialog dialog = new TopicDialog();
		dialog.setVisible(true);
		refreshCanvas();
	}

	private void onCreateNewPlace() {
		PlaceDialog dialog = new PlaceDialog();
		dialog.setVisible(true);
		refreshCanvas();
	}

	private void onCreateNewEvent() {
		EventDialog dialog = new EventDialog();
		dialog.setVisible(true);
		refreshCanvas();
	}

	@Override
	public void onResourceDoubleClick(Object resource) {
		if (resource instanceof Agent) {
			Agent agent = (Agent) resource;
			AgentDialog dialog = new AgentDialog();
			dialog.setAgent(agent);
			dialog.setVisible(true);
		} else if (resource instanceof Content) {
			ContentDialog dialog = new ContentDialog();
			dialog.setContent((Content) resource);
			dialog.setVisible(true);
		} else if (resource instanceof Topic) {
			TopicDialog dialog = new TopicDialog();
			dialog.setTopic((Topic) resource);
			dialog.setVisible(true);
		} else if (resource instanceof Place) {
			PlaceDialog dialog = new PlaceDialog();
			dialog.setPlace((Place) resource);
			dialog.setVisible(true);
		} else if (resource instanceof Event) {
			EventDialog dialog = new EventDialog();
			dialog.setEvent((Event) resource);
			dialog.setVisible(true);
		} else {
			YUtils.showError(frame.getContentPane(),
					"Can not edit resource. Update onResourceDoubleClick function.");
		}

		refreshCanvas();
	}

	private void onLoadClicked() {
		JFileChooser chooser = new JFileChooser();
		chooser.showOpenDialog(frame);
		if (chooser.getSelectedFile() != null) {
			if (chooser.getSelectedFile().getAbsolutePath() != null) {
				YartaWrapper.getInstance().loadKB(
						chooser.getSelectedFile().getAbsolutePath());
				refreshCanvas();
			}
		}
	}

	private void onSaveClicked() {
		JFileChooser chooser = new JFileChooser();
		chooser.showSaveDialog(frame);
		if (chooser.getSelectedFile() != null) {
			if (chooser.getSelectedFile().getAbsolutePath() != null) {
				YartaWrapper.getInstance().dumpKB(
						chooser.getSelectedFile().getAbsolutePath());
				refreshCanvas();
			}
		}
	}

	private void onSendHelloClicked() {
		String result = JOptionPane.showInputDialog(frame,
				"Enter parter user id: ");

		if (result != null) {
			YartaWrapper.getInstance().sendHello(result);
		}
	}

	private void onSendUpdateClicked() {
		String result = JOptionPane.showInputDialog(frame,
				"Enter parter user id: ");

		if (result != null) {
			YartaWrapper.getInstance().sendUpdate(result);
		}
	}

	private void onSendMessageClicked() {
		String partnerId = JOptionPane.showInputDialog(frame,
				"Enter parter user id: ");

		if (partnerId != null) {
			String message = JOptionPane.showInputDialog(frame,
					"Enter your message: ");

			if (message != null) {
				YartaWrapper.getInstance().sendMessage(partnerId, message);
			}
		}
	}

	@Override
	public void handleNotification(String notification) {
		System.err.println(notification);
		refreshCanvas();
	}

	@Override
	public boolean handleQuery(String query) {
		refreshCanvas();
		int dialogResult = JOptionPane.showConfirmDialog(frame, query,
				"Warning", JOptionPane.YES_NO_OPTION);
		return dialogResult == JOptionPane.YES_OPTION;
	}

	@Override
	public void handleKBReady(String userId) {
		YartaWrapper.getInstance().setOwnerId(userId);
		refreshCanvas();
	}

	@Override
	public boolean handleMessage(String id, Message message) {
		if (message.getType() == Message.TYPE_USER_MESSAGE) {
			String format = "From: %s\r\nMessage: %s";

			JOptionPane.showMessageDialog(frame,
					String.format(format, id, new String(message.getData())));

			return true;
		}
		return false;
	}

	@Override
	public Message handleRequest(String id, Message message) {
		System.err.println("handleRequest: not expected!");
		return null;
	}

	@Override
	public String getAppId() {
		return "fr.inria.arles.yarta.YartaUI";
	}
}
