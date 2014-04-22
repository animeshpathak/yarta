package fr.inria.arles.yarta.ui.ctrl;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import fr.inria.arles.yarta.resources.Content;
import fr.inria.arles.yarta.resources.Event;
import fr.inria.arles.yarta.resources.Group;
import fr.inria.arles.yarta.resources.Person;
import fr.inria.arles.yarta.resources.Place;
import fr.inria.arles.yarta.resources.Topic;

public class ResourceCellRenderer implements
		ListCellRenderer {

	protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
	
	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
	    JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,
	            isSelected, cellHasFocus);
	    
	    String text = "#Edit ResourceCellRenderer";
	    if (value instanceof Person) {
	    	text = "Person " + ((Person) value).getEmail();
	    } else if (value instanceof Group) {
	    	text = "Group " + ((Group) value).getEmail();
	    } else if (value instanceof Content) {
	    	text = "Content " + ((Content) value).getTitle();
	    } else if (value instanceof Topic) {
	    	text = "Topic " + ((Topic) value).getTitle();
	    } else if (value instanceof Place) {
	    	text = "Place " + ((Place) value).getName();
	    } else if (value instanceof Event) {
	    	text = "Event " + ((Event) value).getTitle();
	    }
	    renderer.setText(text);
		return renderer;
	}
}
