package fr.inria.arles.yarta.ui.ctrl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import fr.inria.arles.yarta.core.YartaWrapper;

public class GraphUI extends BaseCanvasUI {

	public interface EventHandler {
		public void onResourceDoubleClick(Object resource);
	}

	public GraphUI() {
		super();
	}

	public void init(EventHandler eventHandler) {
		addMouseListener(this);
		addMouseMotionListener(this);
		this.eventHandler = eventHandler;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int clicks = e.getClickCount();
		int x = e.getX();
		int y = e.getY();
		e.consume();

		if (clicks > 1) {
			for (NodeUI node : lstNodes) {
				if (node.contains(x, y)) {
					onNodeDoubleClick(node);
					return;
				}
			}
		}
	}

	private void onNodeDoubleClick(NodeUI node) {
		eventHandler.onResourceDoubleClick(node.getObject());
	}

	@Override
	public synchronized void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		e.consume();

		for (int i = 0; i < lstNodes.size(); i++) {
			NodeUI node = lstNodes.get(i);
			if (node.contains(x, y)) {
				currentNode = i;

				deltaMouse.setLocation(x - node.getX(), y - node.getY());
			}
		}
	}

	@Override
	public synchronized void mouseReleased(MouseEvent e) {
		e.consume();
		currentNode = -1;
		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		e.consume();

		if (currentNode != -1) {
			NodeUI node = lstNodes.get(currentNode);
			node.setPosition(x - deltaMouse.x, y - deltaMouse.y);
			repaint();
		}
	}

	@Override
	public void update(Graphics graphics) {
		paint(graphics);
	}

	@Override
	public void paint(Graphics graphics) {
		Image offscreen = createImage(getWidth(), getHeight());
		Graphics bufferGraphics = offscreen.getGraphics();

		// set smooth anti-aliasing!
		Graphics2D bufferGraphics2D = (Graphics2D) bufferGraphics;
		bufferGraphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		bufferGraphics.setColor(Colors.background);
		bufferGraphics.fillRect(0, 0, getWidth(), getHeight());

		drawNodes(bufferGraphics);
		drawLinks(bufferGraphics);

		graphics.drawImage(offscreen, 0, 0, this);
		bufferGraphics.dispose();
	}

	private void drawNodes(Graphics graphics) {
		for (NodeUI node : lstNodes) {
			node.draw(graphics);
		}
	}

	private void drawLinks(Graphics graphics) {
		for (NodeUI node : lstNodes) {
			node.reset();
		}
		
		for (NodeUI nodeI : lstNodes) {
			for (NodeUI nodeJ : lstNodes) {
				if (!nodeI.equals(nodeJ)) {
					if (YartaWrapper.getInstance().knowsLink(nodeI.getObject(),
							nodeJ.getObject())) {
						drawArrow((Graphics2D) graphics, nodeI, nodeJ,
								Color.red);
					}
					if (YartaWrapper.getInstance().memberOfLink(
							nodeI.getObject(), nodeJ.getObject())) {
						drawArrow((Graphics2D) graphics, nodeI, nodeJ,
								Color.blue);
					}
					if (YartaWrapper.getInstance().creatorLink(
							nodeI.getObject(), nodeJ.getObject())) {
						drawArrow((Graphics2D) graphics, nodeI, nodeJ,
								Color.pink);
					}
					if (YartaWrapper.getInstance().hasInterestLink(
							nodeI.getObject(), nodeJ.getObject())) {
						drawArrow((Graphics2D) graphics, nodeI, nodeJ,
								Color.cyan);
					}
					if (YartaWrapper.getInstance().isTaggedLink(
							nodeI.getObject(), nodeJ.getObject())) {
						drawArrow((Graphics2D) graphics, nodeI, nodeJ,
								Color.green);
					}

					if (YartaWrapper.getInstance().isLocatedLink(
							nodeI.getObject(), nodeJ.getObject())) {
						drawArrow((Graphics2D) graphics, nodeI, nodeJ,
								Color.magenta);
					}
					
					if (YartaWrapper.getInstance().participatesLink(
							nodeI.getObject(), nodeJ.getObject())) {
						drawArrow((Graphics2D) graphics, nodeI, nodeJ,
								Color.black);
					}
				}
			}
		}
	}

	private void drawArrow(Graphics2D graphics, NodeUI i, NodeUI j,
			Color color) {
		graphics.setColor(color);
		
		int x1 = i.getNextLinkX();
		int y1 = i.getNextLinkY();
		
		int x2 = j.getNextLinkX();
		int y2 = j.getNextLinkY();
		
		graphics.drawLine(x1, y1, x2, y2);

		graphics.fillOval(x2 - 2, y2 - 2, 4, 4);
	}

	public void setObjects(List<Object> objects) {
		
		YartaWrapper.getInstance().resetLinks();
		
		lstNodes.clear();

		for (Object object : objects) {
			NodeUI node = new NodeUI(0, 0);
			node.setObject(object);
			lstNodes.add(node);
		}

		generateObjectsPositions();

		repaint();
	}

	private void generateObjectsPositions() {
		int centerX = getWidth() / 2;
		int centerY = getHeight() / 2;
		int radius = Math.min(getWidth(), getHeight()) / 3;
		float f = 0;
		float df = (float) (2 * 3.14) / lstNodes.size();

		for (NodeUI node : lstNodes) {
			node.setPosition(
					centerX + (int) (Math.cos(f) * radius) - node.getWidth()
							/ 2,
					centerY + (int) (Math.sin(f) * radius) - node.getHeight()
							/ 2);
			f += df;
		}
	}

	private EventHandler eventHandler;
	private int currentNode = -1;
	private Point deltaMouse = new Point();
	private List<NodeUI> lstNodes = new ArrayList<NodeUI>();
	private static final long serialVersionUID = 1L;
}
