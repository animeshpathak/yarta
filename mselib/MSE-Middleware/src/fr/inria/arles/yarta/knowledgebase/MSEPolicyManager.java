package fr.inria.arles.yarta.knowledgebase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import fr.inria.arles.yarta.knowledgebase.interfaces.Graph;
import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.knowledgebase.interfaces.PolicyManager;
import fr.inria.arles.yarta.knowledgebase.interfaces.ThinKnowledgeBase;
import fr.inria.arles.yarta.knowledgebase.interfaces.Triple;
import fr.inria.arles.yarta.logging.YLogger;
import fr.inria.arles.yarta.logging.YLoggerFactory;

/**
 * Policy manager working with the Jena implementation of the Knowledge Base. We
 * should out this class in the knowledgebase.policy package, but for now we
 * prefer to keep it here to have the implementation hidden. This class cannot
 * be public.
 */
public class MSEPolicyManager implements PolicyManager {

	private static final String POLICY_TEMP_USERNAME = "TemporaryUserUriSHOULD_NOT_BE_IN_KB_AFTER_POLICY_CHECK!!";
	private static final String rdf_namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	private static final String policy_namespace = "http://yarta.gforge.inria.fr/ontologies/mse.rdf#";

	private static final String SUBJECT = "%subject%";
	private static final String PREDICATE = "%predicate%";
	private static final String OBJECT = "%object%";
	private static final String OWNER = "%owner%";

	private Node dataOwner;
	private String user_namespace;
	private YLogger logger = YLoggerFactory.getLogger();

	public Set<String> policies = new HashSet<String>();

	/**
	 * temporary model used in queries;
	 */
	private Model tempResults;

	private String requestorKBName;
	private boolean requestorIsTemporary;

	public MSEPolicyManager() {
	}

	/**
	 * TODO The function parses all policies into an arrayList that will be used
	 * when policies are to be evaluated
	 * 
	 * @param filePR
	 *            - a String defining the file where policies are stored
	 * @param userNamespace
	 *            - a String defining the namespace of the user
	 * @param owner
	 *            - a Node defining the owner of the rdf data graph
	 */
	public void initialize(String filePR, String userNamespace, Node owner) {
		user_namespace = userNamespace;
		dataOwner = owner;

		policies.addAll(getPolicies(filePR));

		if (userNamespace == null) {
			logger.e("MSEPolicyManager.initialize", "namespace is null");
		}
	}

	public boolean loadPolicies(String path) {
		policies.addAll(getPolicies(path));
		return true;
	}

	private List<String> getPolicies(String path) {
		File queryFile = new File(path);
		ArrayList<String> policiesArrayList = new ArrayList<String>();
		try {
			BufferedReader input = new BufferedReader(new FileReader(queryFile));
			try {
				String line = null;
				while ((line = input.readLine()) != null) {
					policiesArrayList.add(line.replace(OWNER,
							"<" + dataOwner.getName() + ">"));
				}
				input.close();
			} catch (IOException e) {
				logger.e("MSEPolicyManager.initialize",
						"input/output operation failed", e);
			}
		} catch (FileNotFoundException e) {
			logger.e("MSEPolicyManager.initialize", "Policies file not found",
					e);
		}

		return policiesArrayList;
	}

	public void uninitialize(String filePR) {
		try {
			PrintWriter writer = new PrintWriter(filePR);
			for (String rule : policies) {
				writer.println(rule);
			}
			writer.close();
		} catch (FileNotFoundException e) {
			logger.e("MSEPolicyManager.uninitialize",
					"input/output operation failed", e);
		}
	}

	/**
	 * The function adds temporary triples representing the requestor's ID and
	 * the requested action to a JENA model over which it executes the queries
	 * parsed into an arraylist. It returns the triples accessible to the user
	 * in a new model
	 * 
	 * @param action
	 *            - a String defining the action to perform by the requestor
	 * @param requestorUserId
	 *            - a String defining the userID of the requestor
	 * @param model
	 *            - a CLONED JENA model that contains all the ontology data
	 * @param triple
	 *            - the requested triple
	 * @return true if access is granted, false otherwise
	 */
	public boolean isAllowed(String action, String requestorUserId,
			Model model, Triple triple) {

		if (!ThinKnowledgeBase.PERFORM_CHECKS) {
			logger.e("MSEPolicyManager.isAllowed",
					"The PERFORM_CHECKS is false, then why am I being called?!");
		}

		Node subject = triple.getSubject();
		Node predicate = triple.getProperty();
		Node object = triple.getObject();

		if (subject == null || object == null || predicate == null
				|| subject.toString().equals("")
				|| object.toString().equals("")
				|| predicate.toString().equals("")) {
			logger.e("MSEPolicyManager.isAllowed", "requested data is null");
			return false;
		}

		// DONE replaced numbers with static final ints
		if ((action.equals("add"))
				&& (subject.whichNode() == Node.VARIABLE_NODE
						|| object.whichNode() == Node.VARIABLE_NODE || predicate
						.whichNode() == Node.VARIABLE_NODE)) {
			return false;
		} else {
			boolean accessOK = false;

			addRequestorInfo(model, requestorUserId, action);

			Iterator<String> i = this.policies.iterator();
			while (i.hasNext()) {
				Model temp = null;
				String policy = i.next();
				if (!policy.contains(action))
					continue;

				temp = executeQuery(policy, model, subject, predicate, object);

				if (temp != null) {
					if (!temp.isEmpty()) {
						accessOK = true;
						break;
					}
				}
			}// end while

			removeRequestorInfo(model, requestorUserId, action);

			return accessOK;
		}// end if

	}

	/**
	 * 
	 * @param action
	 *            - requested action
	 * @param requestorUserId
	 *            - requestor's id
	 * @param model
	 *            - the underlying Jena model containgin all data
	 * @param g
	 *            - the graph for which accessibility is checked
	 * @return true if access is allowed, false if not
	 */
	public boolean isAllowed(String action, String requestorUserId,
			Model model, Graph g) {

		if (!ThinKnowledgeBase.PERFORM_CHECKS) {
			logger.e("MSEPolicyManager.isAllowed",
					"The PERFORM_CHECKS is false, then why am I being called?!");
		}

		if (g == null) {
			logger.e("MSEPolicyManager.isAllowed", "graph is null");
			return false;
		}
		if (g.isEmpty()) {
			logger.e("MSEPolicyManager.isAllowed", "graph is empty");
			return false;
		}

		ArrayList<Triple> requestTriples;
		MSETriple triple;
		requestTriples = g.getTriples();

		Iterator<Triple> i = requestTriples.iterator();
		while (i.hasNext()) {
			triple = (MSETriple) i.next();
			if (!isAllowed(action, requestorUserId, model, triple)) {
				return false;
			}
		}
		// loop has ended, I found no disallowed triples. All is well.
		return true;
	}

	/**
	 * If some objects in the returned triples are resources whose type is not
	 * known (i.e., there is no triple in the returned set stating that object o
	 * if of rdf:type t), then we should look for and add those triples
	 * 
	 * @param action
	 *            - requested action
	 * @param requestorUserId
	 *            - requestor's id
	 * @param model
	 *            - the underlying CLONED Jena model containing all data
	 * @param triple
	 *            - the triple for which accessibility is checked
	 * @return - a graph containing all accessible triples (maybe just one)
	 */

	public Model getAllowedModel(String action, String requestorUserId,
			Model model, Triple triple) {
		Model result = null;
		Node subject = triple.getSubject();
		Node predicate = triple.getProperty();
		Node object = triple.getObject();

		if ((action.equals("add"))
				&& ((subject.whichNode() == Node.VARIABLE_NODE)
						|| (object.whichNode() == Node.VARIABLE_NODE) || (predicate
						.whichNode() == Node.VARIABLE_NODE))) {
			logger.e("MSEPolicyManager.isAllowedModel",
					"action not allowed. Cannot add triple which has one of more variables");
			return null;
		} else {
			result = ModelFactory.createDefaultModel();

			addRequestorInfo(model, requestorUserId, action);

			for (String policy : this.policies) {
				Model temp = null;
				if (!policy.contains(action))
					continue;

				temp = executeQuery(policy, model, subject, predicate, object);
				if ((temp != null) && (!temp.isEmpty())) {
					result.add(temp);
				}
			}

			removeRequestorInfo(model, requestorUserId, action);
			removeRequestorInfo(result, requestorUserId, action);
		}

		return result;
	}

	/**
	 * 
	 * @param action
	 *            - requested action
	 * @param requestor
	 *            - requestor id
	 * @param model
	 *            - a JENA model that contains all the ontology data
	 * @param g
	 *            - a graph containing all KB knowledge
	 * @return - a Jena model corresponding to the accessible portion of the KB.
	 *         Note that this model could be emtpy
	 */
	public Model filteredModel(String action, String requestor, Model model,
			Graph g) {
		if (g == null) {
			logger.e("MSEPolicyManager.filteredModel", "graph is null");
			return null;
		}

		// This does not need to be OntologyModel, only the temp returned by
		// isAllowedModel
		// finalResults=ModelFactory.createDefaultModel();
		if (g.isEmpty()) {
			logger.e("MSEPolicyManager.filteredModel", "graph is empty");
			return null;
		}

		if (tempResults == null) {
			tempResults = ModelFactory.createDefaultModel();
		} else {
			tempResults.removeAll();
		}

		ArrayList<Triple> requestTriples = new ArrayList<Triple>();
		MSETriple triple = null;
		requestTriples = g.getTriples();

		// boolean answer = false;
		Iterator<Triple> i = requestTriples.iterator();

		while (i.hasNext()) {
			triple = (MSETriple) i.next();
			// logger.d("MSEPolicyManager.filteredModel",
			// "Showing request triples\n" + triple);

			Model temp = getAllowedModel(action, requestor, model, triple);
			// logger.d("MSEPolicyManager.filteredModel", "Allowed triple: " +
			// temp.toString());
			if (!temp.isEmpty() && temp != null)
				tempResults.add(temp);
			// logger.d("MSEPolicyManager.filteredModel",
			// "Current filtered model: " + results.toString());

		}
		// if (results.isEmpty() || results == null)
		// return null;
		if (tempResults == null) {
			return null;
		} else {
			return tempResults;
		}

	}

	/**
	 * The function executes a SPARQL query over a JENA model , constructs an
	 * RDF graph and returns the answer in a new model
	 * 
	 * @param queryString
	 *            - a String that presents a SPARQL query
	 * @param model
	 *            - a JENA model over which SPARQL queries will be executed
	 * @param subject
	 *            - a MSE node that presents the subject of the requested triple
	 * @param predicate
	 *            - a MSE node that presents the predicate of the requested
	 *            triple
	 * @param object
	 *            - a MSE node that presents the object of the requested triple
	 * @return a JENA model that contains the result of the query execution
	 */
	private Model executeQuery(String queryString, Model model, Node subject,
			Node predicate, Node object) {
		Model result = null;
		try {
			if (compareToRequest(queryString, subject, predicate, object)) {
				queryString = constructQuery(queryString, subject, predicate,
						object);
				queryString = constructTriple(queryString, subject, predicate,
						object);
				Query query = QueryFactory.create(queryString);
				try {
					QueryExecution qe = QueryExecutionFactory.create(query,
							model);
					result = qe.execConstruct();
				} catch (NullPointerException e) {
					logger.e("MSEPolicyManager.executeQuery",
							"error executing policy", e);
					return null;
				}
			}
		} catch (QueryParseException e) {
			logger.e("MSEPolicyManager.executeQuery",
					"error creating the query", e);
			return null;
		}
		return result;
	}

	/**
	 * The function compares the subject, predicate and object in the construct
	 * clause of the policy to the subject, predicate and object sent by the
	 * requester. It returns true if they match and false if they don't.
	 * 
	 * @param query
	 *            - a String that presents a SPARQL query
	 * @param subject
	 *            - a MSE node that presents the subject of the requested triple
	 * @param predicate
	 *            - a MSE node that presents the predicate of the requested
	 *            triple
	 * @param object
	 *            - a MSE node that presents the object of the requested triple
	 * @return - true if the triple to construct matches the requested triple
	 */
	private boolean compareToRequest(String query, Node subject,
			Node predicate, Node object) {
		if (!query.contains(SUBJECT)) {
			if (query.contains("CONSTRUCT {" + "<" + subject.getName() + ">")
					|| (subject.whichNode() == Node.VARIABLE_NODE)) {
				if (!query.contains(PREDICATE))
					if ((query.contains("CONSTRUCT {" + "<" + subject.getName()
							+ "> <" + predicate.getName() + ">"))
							|| ((query.contains("CONSTRUCT {" + "<"
									+ subject.getName() + ">")) && (predicate
									.whichNode() == 2))
							|| (subject.whichNode() == 2 && predicate
									.whichNode() == 2)
							|| (subject.whichNode() == 2 && query
									.substring(
											query.indexOf("{")
													+ 4
													+ subject.getName()
															.length(),
											query.indexOf("{")
													+ 4
													+ subject.getName()
															.length()
													+ predicate.getName()
															.length() + 2)
									.toString()
									.contentEquals(
											"<" + predicate.getName() + ">")))
						if (!query.contains(OBJECT)) {
							if (query.contains("CONSTRUCT {<"
									+ subject.getName() + "> <"
									+ predicate.getName() + "> <"
									+ object.getName() + ">")
									|| ((query.contains("CONSTRUCT {" + "<"
											+ subject.getName() + "> <"
											+ predicate.getName() + ">")) && (object
											.whichNode() == 2))
									|| ((query.contains("CONSTRUCT {" + "<"
											+ subject.getName() + ">"))
											&& (predicate.whichNode() == 2) && object
											.whichNode() == 2)
									|| (subject.whichNode() == 2
											&& predicate.whichNode() == 2 && object
											.whichNode() == 2)
									|| (subject.whichNode() == 2 && query
											.substring(
													query.indexOf("{")
															+ 4
															+ subject.getName()
																	.length(),
													query.indexOf("{")
															+ 4
															+ subject.getName()
																	.length()
															+ predicate
																	.getName()
																	.length()
															+ 2
															+ object.getName()
																	.length()
															+ 3)
											.toString()
											.contentEquals(
													"<" + predicate.getName()
															+ "> <"
															+ object.getName()
															+ ">"))
									|| (subject.whichNode() == 2
											&& query.substring(
													query.indexOf("{")
															+ 4
															+ subject.getName()
																	.length(),
													query.indexOf("{")
															+ 4
															+ subject.getName()
																	.length()
															+ predicate
																	.getName()
																	.length()
															+ 2)
													.toString()
													.contentEquals(
															"<"
																	+ predicate
																			.getName()
																	+ ">") && object
											.whichNode() == 2))
								return true;
							else
								return false;
						} else {
							System.out
									.println(query.substring(query.indexOf("{")
											+ 4 + subject.getName().length(),
											query.indexOf("{")
													+ 4
													+ subject.getName()
															.length()
													+ predicate.getName()
															.length() + 2));
							return true;
						}

					else
						return false;
				else if (query.contains(PREDICATE) && !query.contains(OBJECT))
					if (query.contains("CONSTRUCT {<" + subject.getName() + ">"
							+ " " + PREDICATE + " " + "<" + object.getName()
							+ ">") == true
							|| ((query.contains("CONSTRUCT {<"
									+ subject.getName() + ">" + " " + PREDICATE
									+ " ") == true) && (object.whichNode() == 2))
							|| (subject.whichNode() == 2 && object.whichNode() == 2)
							|| subject.whichNode() == 2
							&& query.substring(
									query.indexOf("{") + 4
											+ subject.getName().length(),
									query.indexOf("{") + 4
											+ subject.getName().length() + 12
											+ object.getName().length() + 3)
									.toString()
									.contentEquals("<" + object.getName() + ">"))
						return true;
					else
						return false;
				else
					return true;
			} else
				return false;
		} else if (query.contains(SUBJECT) && !query.contains(PREDICATE))
			if ((query.contains("CONSTRUCT {" + SUBJECT + " <"
					+ predicate.getName() + ">"))
					|| (predicate.whichNode() == 2))
				if (!query.contains(OBJECT))
					if ((query.contains("CONSTRUCT {" + SUBJECT + " <"
							+ predicate.getName() + "> <" + object.getName()
							+ ">"))
							|| ((object.whichNode() == 2) && (predicate
									.whichNode() == 2))
							|| (query.contains("CONSTRUCT {" + SUBJECT + " <"
									+ predicate.getName() + ">") == true && object
									.whichNode() == 2))
						return true;
					else
						return false;
				else
					return true;

			else
				return false;
		else if (query.contains(SUBJECT) && query.contains(PREDICATE))
			if (!query.contains(OBJECT))
				if ((query.contains("CONSTRUCT {" + SUBJECT + " " + PREDICATE
						+ " <" + object.getName() + ">"))
						|| (object.whichNode() == 2))
					return true;
				else
					return false;
			else
				return true;

		return false;
	}

	/**
	 * The function constructs a new request specific instance of the SPARQL
	 * query by substituting the parameters in the queries with the requested
	 * nodes. It returns a String representing the new instance.
	 * 
	 * @param queryString
	 *            - a String that presents a SPARQL query
	 * @param subject
	 *            - a MSE node that presents the subject of the requested triple
	 * @param predicate
	 *            - a MSE node that presents the predicate of the requested
	 *            triple
	 * @param object
	 *            - a MSE node that presents the object of the requested triple
	 * @return String representing the new instance of the SPARQL query
	 */

	private String constructQuery(String queryString, Node subject,
			Node predicate, Node object) {
		queryString = queryString.replace(OWNER, "<" + dataOwner.getName()
				+ ">");
		queryString = queryString.replace("%namespace%", user_namespace);

		if (subject.whichNode() == Node.RDF_RESOURCE)
			queryString = queryString.replace(SUBJECT, "<" + subject.getName()
					+ ">");
		else if (subject.whichNode() == Node.RDF_LITERAL)
			logger.e("MSEPolicyManager.executeQuery",
					"The subject can't be a literal");
		else if (subject.whichNode() == Node.VARIABLE_NODE)
			queryString = queryString.replace(SUBJECT, subject.getName());

		if (predicate.whichNode() == Node.RDF_RESOURCE)
			queryString = queryString.replace(PREDICATE,
					"<" + predicate.getName() + ">");
		else if (predicate.whichNode() == Node.RDF_LITERAL)
			logger.e("MSEPolicyManager.executeQuery",
					"The predicate can't be a literal");
		else if (predicate.whichNode() == 2)
			queryString = queryString.replace(PREDICATE, predicate.getName());

		if (object.whichNode() == Node.RDF_RESOURCE)
			queryString = queryString.replace(OBJECT, "<" + object.getName()
					+ ">");
		else if (object.whichNode() == Node.RDF_LITERAL)
			queryString = queryString.replace(OBJECT, "'"
					+ escapeLiteral(object.getName()) + "'");
		else if (object.whichNode() == Node.VARIABLE_NODE)
			queryString = queryString.replace(OBJECT, object.getName());

		return queryString;
	}

	/**
	 * Escapes a literal so it can be used in a SPARQL query.
	 * 
	 * @param literal
	 *            the literal's value
	 * @return escaped string
	 */
	private String escapeLiteral(String literal) {
		// TODO: escape all: \n, \t, \r, \b, \b, \f, \", \' and \\
		return literal.replace("'", "\\'").replace("\n", "\\n");
	}

	/**
	 * the function adds the type of the SPARQL variable nodes to the the SPARQL
	 * query.
	 * 
	 * @param queryString
	 *            - a String that presents a SPARQL query
	 * @param subject
	 *            - a MSE node that presents the subject of the requested triple
	 * @param predicate
	 *            - a MSE node that presents the predicate of the requested
	 *            triple
	 * @param object
	 *            - a MSE node that presents the object of the requested triple
	 * @return String representing the SPARQL query with the newly added
	 *         triples.
	 */
	private String constructTriple(String queryString, Node subject,
			Node predicate, Node object) {

		String constructedTriple = " ";
		if (subject.whichNode() == Node.VARIABLE_NODE
				&& subject.getType() != null)
			constructedTriple = constructedTriple + " " + subject.getName()
					+ " <" + rdf_namespace + "type" + "> <" + subject.getType()
					+ ">.";

		if (object.whichNode() == Node.VARIABLE_NODE
				&& object.getType() != null)
			constructedTriple = constructedTriple + " " + object.getName()
					+ " <" + rdf_namespace + "type" + "> <" + object.getType()
					+ ">.";

		if (constructedTriple != null)
			queryString = queryString.replace("%type%", constructedTriple);
		return queryString;
	}

	/**
	 * Adds requestor info before the SPARQL query.
	 * 
	 * @param model
	 * @param requestorUserId
	 * @param action
	 */
	private void addRequestorInfo(Model model, String requestorUserId,
			String action) {
		requestorIsTemporary = false;

		// Sparql for checking if there is a person node with this userId
		// property
		String queryString = "PREFIX mse:<http://yarta.gforge.inria.fr/ontologies/mse.rdf#> "
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ " SELECT ?req where { ?req rdf:type mse:Person . ?req mse:userId '"
				+ requestorUserId + "' }";

		Query query = QueryFactory.create(queryString);
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		ResultSet execSelect = qe.execSelect();
		Iterator<QuerySolution> tempQresult = execSelect;
		if (tempQresult.hasNext() == false) {
			logger.d("MSEPolicyManager.isAllowed",
					"No person found with userId " + requestorUserId
							+ ", using temporary username");
			// person is not in KB, add temp person with this userId
			requestorKBName = POLICY_TEMP_USERNAME;

			// add triple "randomUID is a Person", assign to node
			// requestorNode
			createInstanceOfClass(model, policy_namespace, user_namespace,
					MSEKnowledgeBase.TYPE_PERSON_NAME, requestorKBName);

			// give this person the userID
			addObjectProperty(model, policy_namespace, user_namespace,
					requestorKBName, MSEKnowledgeBase.PROPERTY_USERID_NAME,
					requestorUserId);

			requestorIsTemporary = true;
		} else {
			QuerySolution soln = tempQresult.next();
			String resultString = soln.toString();
			// not needed? if (resultString.contains("#"))
			// convert
			// <http://yarta.gforge.inria.fr/ontologies/mse.rdf#personUri2345>
			// to personUri2345
			requestorKBName = resultString.substring(
					resultString.indexOf("#") + 1, resultString.indexOf(">"));
		}
		qe.close();

		createInstanceOfClass(model, policy_namespace, user_namespace,
				"requestor", requestorKBName);

		addObjectProperty(model, policy_namespace, user_namespace,
				requestorKBName, "performs", action);
	}

	/**
	 * Removes the previously added requester info.
	 * 
	 * @param model
	 * @param requestorUserId
	 * @param action
	 */
	private void removeRequestorInfo(Model model, String requestorUserId,
			String action) {
		// clean up now
		// we need to remove Triples, since the copy used here is NOT cloned
		// from the initial_model
		//
		removeTriple(model, policy_namespace, user_namespace, requestorKBName,
				rdf_namespace + "type", "requestor");
		removeTriple(model, policy_namespace, user_namespace, requestorKBName,
				policy_namespace + "performs", action);

		if (requestorIsTemporary) {
			// remove his username
			removeTriple(model, policy_namespace, user_namespace,
					requestorKBName, user_namespace
							+ MSEKnowledgeBase.PROPERTY_USERID_NAME,
					requestorUserId);

			// remove his existence
			removeTriple(model, policy_namespace, user_namespace,
					requestorKBName, rdf_namespace + "type",
					MSEKnowledgeBase.TYPE_PERSON_NAME);
		}// end while
	}

	/**
	 * doesn't already exist
	 * 
	 * @param model
	 *            - a JENA model where the new triple will be added
	 * @param namespace
	 *            - a String defining the KB's namespace
	 * @param className
	 *            - a String defining the class to add
	 * @param instanceName
	 *            - a String defining the instance of the class to add
	 * @param userNamespace
	 *            - a String defining the user's namespace
	 * @return boolean - true if the add was successful and false if the add
	 *         fails
	 */
	private boolean createInstanceOfClass(Model model, String namespace,
			String userNamespace, String className, String instanceName) {

		Resource rs = model.getResource(userNamespace + instanceName);
		if ((instanceName == null) || (instanceName.equals(""))) {
			logger.e("MSEPolicyManager.createInstanceOfClass",
					"instance of class" + instanceName + " to create is null");
		}
		if (rs == null)
			rs = model.createResource(userNamespace + instanceName);
		Property p = model.getProperty(rdf_namespace + "type");
		Resource rs2 = model.getResource(namespace + className);
		if ((rs != null) && (rs2 != null) && (p != null)) {
			rs.addProperty(p, rs2);
			return true;
		}
		return false;
	}

	/**
	 * The function creates a triple consisting of a subject, a property, and an
	 * object. It creates a new triple even if the object property doesn't
	 * already exist.
	 * 
	 * @param model
	 *            - a JENA model where the subject , property , object triples
	 *            will be added
	 * @param namespace
	 *            - a String defining the user's namespace
	 * @param subject
	 *            - a String defining the subject of the triple
	 * @param property
	 *            - a String defining the property of the triple
	 * @param object
	 *            - a String defining the object of the triple
	 * @return boolean - true if the add was successful and false if the add
	 *         fails
	 */
	private boolean addObjectProperty(Model model, String namespace,
			String userNamespace, String subject, String propertyName,
			String object) {
		if ((object == null) || (object.equals(""))) {
			logger.e("MSEPolicyManager.createInstanceOfClass",
					"object of Property " + propertyName + " to create is null");
		}
		Resource rs1 = model.getResource(userNamespace + subject);
		Resource rs2 = model.getResource(namespace + object);
		Property p = model.getProperty(namespace + propertyName);
		if ((rs1 != null) && (rs2 != null) && (p != null)) {
			rs1.addProperty(p, rs2);
			return true;
		}
		return false;
	}

	/**
	 * The function removes a triple from a JENA model
	 * 
	 * @param model
	 *            - a JENA model representing the model from which the triple
	 *            will be deleted
	 * @param namespace
	 *            - a String defining the user's namespace
	 * @param subject
	 *            - a String defining the subject of the triple
	 * @param propertyName
	 *            - a String defining the property of the triple
	 * @param object
	 *            - a String defining the object of the triple
	 * @return boolean - true if the remove was successful and false if the
	 *         remove fails
	 */
	private boolean removeTriple(Model model, String namespace,
			String userNamespace, String subject, String propertyName,
			String object) {
		Resource rs = model.getResource(userNamespace + subject);
		Property p = model.getProperty(propertyName);
		Resource rs2 = model.getResource(namespace + object);
		if ((rs != null) && (rs2 != null) && (p != null)) {
			if (model.contains(rs, p, rs2)) {
				model.removeAll(rs, p, rs2);
				return true;
			} else {
				return false;
			}
		}

		return false;
	}
}
