package fr.inria.arles.yarta.resources;

import java.util.Set;

/**
 * The message interface.
 */
public interface Message extends Content {

	/** The unique "type" URI */
	public static final String typeURI = baseMSEURI + "#Message";
	
	/**
	 * The inverse property contains.
	 * @return
	 */
	public Set<Conversation> getContains_inverse();
}
