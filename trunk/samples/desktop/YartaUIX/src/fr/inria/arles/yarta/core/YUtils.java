package fr.inria.arles.yarta.core;

import java.awt.Component;

import javax.swing.JOptionPane;

import fr.inria.arles.yarta.resources.Content;
import fr.inria.arles.yarta.resources.Event;
import fr.inria.arles.yarta.resources.Group;
import fr.inria.arles.yarta.resources.Person;
import fr.inria.arles.yarta.resources.Place;
import fr.inria.arles.yarta.resources.Topic;

public class YUtils {
	
	public static void showError(Component parent, String message) {
		JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	public static String resourceAsString(Object resource) {
		if (resource instanceof Person) {
			Person person = (Person) resource;
			return person.getEmail();
		} else if (resource instanceof Group) {
			Group group = (Group) resource;
			return group.getEmail();
		} else if (resource instanceof Content) {
			Content content = (Content) resource;
			if (content.getTitle() != null) {
				return content.getTitle();
			}
			return "Anonymous content";
		} else if (resource instanceof Topic) {
			Topic topic = (Topic) resource;
			if (topic.getTitle() != null) {
				return topic.getTitle();
			}
			
			return "Anonymous topic";
		} else if (resource instanceof Place) {
			Place place = (Place) resource;
			if (place.getName() != null) {
				return place.getName();
			}
			
			return "Anonumous place";
		} else if (resource instanceof Event) {
			Event event = (Event) resource;
			if (event.getTitle() != null) {
				return event.getTitle();
			}
			
			return "Anonymouse event";
		}
		return "#Edit AgentDialog.";
	}
}
