/**
 * 
 */
package fr.inria.arles.yarta.knowledgebase.interfaces;


import java.io.OutputStream;
import java.util.ArrayList;



/**
 * @author alessandra
 *
 */
public interface Graph {
	
	/**
	 * Write the graph on a file
	 * @param output - the file name where to write
	 * @param notation - writing language
	 */
	public void writeGraph(OutputStream output, String notation);
	
	
	/**
	 * Read from a file into a graph
	 * @param input - the input file
	 * @param notation - reading language
	 */
	public void readGraph(String input, String notation);

	
	/**
	 * Check if the graph is empty
	 * @return true if the graph is empty
	 */
	public boolean isEmpty();
	
	/**
	 * Return an array list containing triples that describe the model (note that the list can be empty)
	 * @return an ArrayList contaning the triples in the graph
	 */
	public ArrayList<Triple> getTriples();

}
