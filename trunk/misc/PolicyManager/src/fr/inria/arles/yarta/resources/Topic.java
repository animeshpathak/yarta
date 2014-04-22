/**
 * 
 */
package fr.inria.arles.yarta.resources;

/**
 * This class represents any topic that might be, for example, 
 * subject of interest for a person or object of description in 
 * a document (content) or many others. It was inspired by users' 
 * tagging activities. As such, it might be mapped onto keywords, 
 * folksonomies or more structured taxonomies.
 * @author pathak
 *
 */
public class Topic extends Resource{
	/** The unique "type" URI*/
	public static final String typeURI = 
		"http://yarta.gforge.inria.fr/ontologies/mse.rdf#Topic"; 

}
