package fr.inria.arles.yarta.ui.ctrl;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import fr.inria.arles.yarta.core.YartaWrapper;

public class NodeUI {

	public NodeUI(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public boolean contains(int x, int y) {
		return x >= this.x - width / 2 && y >= this.y - height / 2
				&& x <= this.x + width / 2 && y <= this.y + height / 2;
	}

	static int getTextWidth(char c, FontMetrics fm) {
		if (c == ' ' || Character.isSpaceChar(c)) {
			return fm.charWidth('n');
		} else {
			return fm.charWidth(c);
		}
	}

	static void drawCircleText(Graphics2D g, String st, Point center, double r,
			double a1, double af) {
		double curangle = a1;

		AffineTransform at = g.getTransform();

		Point2D c = new Point2D.Double(center.x, center.y);
		char ch[] = st.toCharArray();
		FontMetrics fm = g.getFontMetrics();
		AffineTransform xform1, cxform;
		xform1 = AffineTransform.getTranslateInstance(c.getX(), c.getY());
		for (int i = 0; i < ch.length; i++) {
			double cwid = (double) (getTextWidth(ch[i], fm));
			if (!(ch[i] == ' ' || Character.isSpaceChar(ch[i]))) {
				cwid = (double) (fm.charWidth(ch[i]));
				cxform = new AffineTransform(xform1);
				cxform.rotate(curangle, 0.0, 0.0);
				String chstr = new String(ch, i, 1);
				g.setTransform(cxform);
				g.drawString(chstr, (float) (-cwid / 2), (float) (-r));
			}

			// compute advance of angle assuming cwid<
			if (i < (ch.length - 1)) {
				double adv = cwid / 2.0 + fm.getLeading()
						+ getTextWidth(ch[i + 1], fm) / 2.0;
				curangle += Math.sin(adv / r);
			}
		}

		g.setTransform(at);
	}

	public void draw(Graphics graphics) {
		graphics.setColor(Colors.objectColor);
		graphics.fillOval(x - width / 2, y - height / 2, width, height);

		// draw the icon
		Image image = YartaWrapper.getInstance().getResourceImage(object);
		if (image != null) {
			graphics.drawImage(image, x - image.getWidth(null) / 2,
					y - image.getHeight(null) / 2, null);
		}

		graphics.setColor(Colors.textColor);
		String textName = YartaWrapper.getInstance().getResourceName(object);
		drawCircleText((Graphics2D) graphics, textName, new Point(x, y),
				width / 2 - 10, -Math.PI / 2, 1.0);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public Object getObject() {
		return object;
	}

	private int x;
	private int y;
	private Object object;
	private final int width = 100;
	private final int height = 100;

	public void reset() {
		f = 0;
	}

	public int getNextLinkX() {
		int radius = Math.min(getWidth(), getHeight()) / 2;
		float df = (float) (2 * 3.14) / 5;
		f += df;

		return x + (int) (Math.cos(f) * radius);
	}

	public int getNextLinkY() {
		int radius = Math.min(getWidth(), getHeight()) / 2;
		return y + (int) (Math.sin(f) * radius);
	}

	float f = 0;
}
