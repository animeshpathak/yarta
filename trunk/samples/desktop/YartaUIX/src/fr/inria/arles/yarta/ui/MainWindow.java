package fr.inria.arles.yarta.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import fr.inria.arles.yarta.conference.resources.Building;
import fr.inria.arles.yarta.conference.resources.Company;
import fr.inria.arles.yarta.conference.resources.Conference;
import fr.inria.arles.yarta.conference.resources.Event;
import fr.inria.arles.yarta.conference.resources.MegaSpeaker;
import fr.inria.arles.yarta.conference.resources.Paper;
import fr.inria.arles.yarta.conference.resources.Person;
import fr.inria.arles.yarta.conference.resources.Presentation;
import fr.inria.arles.yarta.conference.resources.Speaker;
import fr.inria.arles.yarta.core.YartaWrapper;
import fr.inria.arles.yarta.middleware.communication.Message;
import fr.inria.arles.yarta.middleware.communication.Receiver;
import fr.inria.arles.yarta.middleware.msemanagement.MSEApplication;
import fr.inria.arles.yarta.resources.Content;
import fr.inria.arles.yarta.resources.Group;
import fr.inria.arles.yarta.resources.Place;
import fr.inria.arles.yarta.resources.Topic;
import fr.inria.arles.yarta.ui.ctrl.GraphUI;

public class MainWindow extends JFrame implements GraphUI.EventHandler,
		MSEApplication, Receiver {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private GraphUI canvas;

	class WindowEventHandler extends WindowAdapter {
		public void windowClosing(WindowEvent evt) {
			YartaWrapper.getInstance().uninit();
		}
	}

	/**
	 * Create the frame.
	 */
	public MainWindow() {
	}

	public void initialize() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		canvas = new GraphUI();
		canvas.setBounds(0, 0, getWidth(), getHeight());
		canvas.init(YartaWrapper.getInstance().getUserId(), this);
		getContentPane().add(canvas);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnCreate = new JMenu("Create");
		menuBar.add(mnCreate);

		JMenuItem mntmGroup = new JMenuItem("Group");
		mntmGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onCreateNewResource(Group.class, false);
			}
		});
		mnCreate.add(mntmGroup);

		JMenuItem mntmPerson = new JMenuItem("Person");
		mntmPerson.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onCreateNewResource(Person.class, true);
			}
		});
		mnCreate.add(mntmPerson);

		mnCreate.addSeparator();

		JMenuItem mntmContent = new JMenuItem("Content");
		mntmContent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCreateNewResource(Content.class, false);
			}
		});
		mnCreate.add(mntmContent);

		JMenuItem mntmEvent = new JMenuItem("Event");
		mntmEvent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCreateNewResource(Event.class, false);
			}
		});
		mnCreate.add(mntmEvent);

		JMenuItem mntmPlace = new JMenuItem("Place");
		mntmPlace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCreateNewResource(Place.class, false);
			}
		});
		mnCreate.add(mntmPlace);

		JMenuItem mntmTopic = new JMenuItem("Topic");
		mntmTopic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCreateNewResource(Topic.class, false);
			}
		});
		mnCreate.add(mntmTopic);

		JMenu mnCreateex = new JMenu("CreateEx");
		menuBar.add(mnCreateex);

		JMenuItem mntmSpeaker = new JMenuItem("Speaker");
		mntmSpeaker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCreateNewResource(Speaker.class, true);
			}
		});
		mnCreateex.add(mntmSpeaker);

		JMenuItem mntmMegaSpeaker = new JMenuItem("Mega-Speaker");
		mntmMegaSpeaker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCreateNewResource(MegaSpeaker.class, true);
			}
		});
		mnCreateex.add(mntmMegaSpeaker);

		JMenuItem mntmCompany = new JMenuItem("Company");
		mntmCompany.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCreateNewResource(Company.class, false);
			}
		});
		mnCreateex.add(mntmCompany);

		mnCreateex.addSeparator();

		JMenuItem mntmConference = new JMenuItem("Conference");
		mntmConference.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCreateNewResource(Conference.class, false);
			}
		});
		mnCreateex.add(mntmConference);

		JMenuItem mntmPresentation = new JMenuItem("Presentation");
		mntmPresentation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCreateNewResource(Presentation.class, false);
			}
		});
		mnCreateex.add(mntmPresentation);

		JMenuItem mntmPaper = new JMenuItem("Paper");
		mntmPaper.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCreateNewResource(Paper.class, false);
			}
		});
		mnCreateex.add(mntmPaper);

		JMenuItem mntmBuilding = new JMenuItem("Building");
		mntmBuilding.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCreateNewResource(Building.class, false);
			}
		});
		mnCreateex.add(mntmBuilding);

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

		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				canvas.setBounds(0, 0, getWidth(), getHeight());
			}
		});
		setTitle("Yarta Conference Demo");
		setLocationRelativeTo(null);
		addWindowListener(new WindowEventHandler());

		YartaWrapper.getInstance().setReceiver(this);
		refreshCanvas();
	}

	private void refreshCanvas() {
		List<Object> objects = new ArrayList<Object>();
		objects.addAll(YartaWrapper.getInstance().getAllResources());
		canvas.setObjects(objects);
	}

	private void onCreateNewResource(Class<?> resourceType, boolean uniqueId) {
		ResourceDialog dialog = new ResourceDialog();
		dialog.setClass(resourceType);

		if (uniqueId) {
			dialog.setValidator(new ResourceDialog.Validator() {

				@Override
				public boolean validate(Map<String, String> values) {
					String userId = values.get("UserId");
					if (userId.isEmpty()) {
						return false;
					}
					return YartaWrapper.getInstance().readPerson(userId) == null;
				}
			});
		}
		dialog.setVisible(true);
		refreshCanvas();
	}

	@Override
	public void onResourceDoubleClick(Object resource) {
		ResourceDialog dialog = new ResourceDialog();
		dialog.setResource(resource);
		dialog.setVisible(true);

		refreshCanvas();
	}

	private void onLoadClicked() {
		JFileChooser chooser = new JFileChooser();
		chooser.showOpenDialog(this);
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
		chooser.showSaveDialog(this);
		if (chooser.getSelectedFile() != null) {
			if (chooser.getSelectedFile().getAbsolutePath() != null) {
				YartaWrapper.getInstance().dumpKB(
						chooser.getSelectedFile().getAbsolutePath());
				refreshCanvas();
			}
		}
	}

	private void onSendHelloClicked() {
		String result = JOptionPane.showInputDialog(this,
				"Enter parter user id: ");

		if (result != null) {
			YartaWrapper.getInstance().sendHello(result);
		}
	}

	private void onSendUpdateClicked() {
		String result = JOptionPane.showInputDialog(this,
				"Enter parter user id: ");

		if (result != null) {
			YartaWrapper.getInstance().sendUpdate(result);
		}
	}

	private void onSendMessageClicked() {
		String partnerId = JOptionPane.showInputDialog(this,
				"Enter parter user id: ");

		if (partnerId != null) {
			String message = JOptionPane.showInputDialog(this,
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
		int dialogResult = JOptionPane.showConfirmDialog(this, query,
				"Warning", JOptionPane.YES_NO_OPTION);
		return dialogResult == JOptionPane.YES_OPTION;
	}

	@Override
	public void handleKBReady(String userId) {
		YartaWrapper.getInstance().setOwnerId(userId);
	}

	@Override
	public boolean handleMessage(String id, Message message) {
		if (message.getType() == Message.TYPE_USER_MESSAGE) {
			String format = "From: %s\r\nMessage: %s";

			JOptionPane.showMessageDialog(this,
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
		return "fr.inria.arles.yarta.Conference";
	}
}
