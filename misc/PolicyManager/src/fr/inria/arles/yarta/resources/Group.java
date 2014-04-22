/**
 * 
 */
package fr.inria.arles.yarta.resources;

/**
 * <p>A group is a set of people. Both groups and 
 * single persons have the same first-class properties
 * linking them to other first-class entities: therefore, 
 * they are modeled as subclass of the same Agent class.
 * A person is linked to a group by the rdfs:member property. 
 * We reuse this existing property, whether another more 
 * specific property is needed has to be seen.</p>
 * @author pathak
 *
 */
public class Group extends Agent {
	/** The unique "type" URI*/
	public static final String typeURI = 
		"http://yarta.gforge.inria.fr/ontologies/mse.rdf#Group"; 
	

}
