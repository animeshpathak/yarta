package fr.inria.arles.yarta.resources;

/**
 * <p>
 * An event consisting of two or more single events, which may take place in
 * parallel or in sequence.
 * </p>
 * <p>
 * Composite events might be handled, for example, by means of RDF Containers
 * (Bag, Alt and Seq). It has to be verified if they are suitable for data
 * processing purposes. The property used to state that one SingleEvent belongs
 * to a CompositeEvent is the rdfs:member property.
 * </p>
 */
public interface CompositeEvent extends Event {

	/** The unique "type" URI */
	public static final String typeURI = baseMSEURI + "#CompositeEvent";
}
