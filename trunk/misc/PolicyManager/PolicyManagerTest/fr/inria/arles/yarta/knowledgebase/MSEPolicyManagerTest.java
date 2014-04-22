/**
 * 
 */
package fr.inria.arles.yarta.knowledgebase;

import java.io.InputStream;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import fr.inria.arles.yarta.knowledgebase.MSEGraph;
import fr.inria.arles.yarta.knowledgebase.MSELiteral;
import fr.inria.arles.yarta.knowledgebase.MSEPolicyManager;
import fr.inria.arles.yarta.knowledgebase.MSEResource;
import fr.inria.arles.yarta.knowledgebase.MSETriple;
import fr.inria.arles.yarta.knowledgebase.MSEVariableNode;
import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.knowledgebase.interfaces.Triple;


import junit.framework.TestCase;

/**
 * @author User
 *
 */
public class MSEPolicyManagerTest extends TestCase {
	String newPolicy,newPolicy2, preDefinedResource;
	String policy;
	String policyWithType;
	MSEPolicyManager myPolicyManager;
	String ns,  inputDataOntology;
	Model m, dataModel;
	String PR, PR1, PR_all, PR_demo;
	Node owner, predicate , object, owner2;
	Node variableSubject, variableObject, variableObj, variablePred;
	Node email_predicate;
	String resultQuery;
	String policyInstance;
	String ConstructedPolicyInstance;
	InputStream in, inData ;
	Node emailValue,variableValue, ValueToRemove, nameValue, namePredicate;
	MSEGraph g;


	protected void setUp() throws Exception {
		PR = "./PolicyManagerTest/testPolicies";
		PR1 = "./PolicyManagerTest/policyWithType";
		PR_demo = "./PolicyManagerTest/policies-demo";
		myPolicyManager = new MSEPolicyManager(m);
		String subjectName = "http://yarta.gforge.inria.fr/ontologies/conference/demo-conference.rdf#valerie@yarta.inria.fr";
		String subName = "http://yarta.gforge.inria.fr/ontologies/conference/demo-conference.rdf#larrypage@google.com";
		String predicateType = "http://www.w3.org/2002/07/owl#DatatypeProperty";
		String predicateName = "http://yarta.gforge.inria.fr/ontologies/mse.rdf#knows";
		String email_predicateName = "http://yarta.gforge.inria.fr/ontologies/mse.rdf#email";
		String objectName = "http://yarta.gforge.inria.fr/ontologies/conference/demo-conference.rdf#alessandra@yarta.inria.fr";
		String objectType = "http://yarta.gforge.inria.fr/ontologies/mse.rdf#Person";
		String literalType = "http://www.w3.org/2001/XMLSchema#string"; 
		String namePredicateName = "http://yarta.gforge.inria.fr/ontologies/mse.rdf#lastName";
		owner = new MSEResource (subjectName, objectType);
		owner2 = new MSEResource (subName, objectType);
	
		predicate =  new MSEResource (predicateName, predicateType);
		object = new MSEResource (objectName,objectType );
		email_predicate = new MSEResource (email_predicateName, predicateType);
		emailValue = new MSELiteral("valerie.issarny@inria.fr",literalType);
		nameValue = new MSELiteral("Toninelli",literalType);
		namePredicate = new MSEResource (namePredicateName, predicateType);
		
		policy = "PREFIX mse:<http://yarta.gforge.inria.fr/ontologies/mse.rdf#> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> CONSTRUCT {%subject% %predicate% %object% .} where {%owner% rdf:type mse:requestor. %owner% mse:performs mse:read. %subject% %predicate% %object% . }";
		ns= "http://yarta.gforge.inria.fr/ontologies/conference/demo-conference.rdf#";
		
		String variableName = "sub";
		String variableObjectName = "obj";
		String variablePredicateName = "pre";
		variableSubject = new MSEVariableNode(variableName, objectType);
		variableObject = new MSEVariableNode (variableObjectName, objectType);
		variableObj = new MSEVariableNode (variableObjectName, null);
		variablePred = new MSEVariableNode (variablePredicateName, null);
		variableValue = new MSEVariableNode (variableObjectName, literalType);
		policyWithType = "PREFIX ns:<%namespace%> PREFIX mse:<http://yarta.gforge.inria.fr/ontologies/mse.rdf#> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> CONSTRUCT {%subject% %predicate% %object%. %type%} where {%owner% rdf:type ns:requestor. %owner% ns:performs ns:read. %subject% %predicate% %object%. %type%}";
		policyInstance = "PREFIX ns:<%namespace%> PREFIX mse:<http://yarta.gforge.inria.fr/ontologies/mse.rdf#> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> CONSTRUCT {%subject% %predicate% %object%.   ?sub <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://yarta.gforge.inria.fr/ontologies/mse.rdf#Person>. ?obj <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://yarta.gforge.inria.fr/ontologies/mse.rdf#Person>.} where {%owner% rdf:type ns:requestor. %owner% ns:performs ns:read. %subject% %predicate% %object%.   ?sub <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://yarta.gforge.inria.fr/ontologies/mse.rdf#Person>. ?obj <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://yarta.gforge.inria.fr/ontologies/mse.rdf#Person>.}";
		ConstructedPolicyInstance = "PREFIX ns:<" + ns +  "> PREFIX mse:<http://yarta.gforge.inria.fr/ontologies/mse.rdf#> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> CONSTRUCT {?sub <" + predicateName + "> <" + objectName + ">.   ?sub <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <" + objectType + ">.} where {<" + subjectName + "> rdf:type ns:requestor. <" + subjectName + "> ns:performs ns:read. ?sub <" + predicateName + "> <" + objectName + ">.   ?sub <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <" + objectType + ">.}";
		preDefinedResource = "PREFIX mse:<http://yarta.gforge.inria.fr/ontologies/mse.rdf#> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> CONSTRUCT {%subject% <http://yarta.gforge.inria.fr/ontologies/mse.rdf#email> %object%} where {?req rdf:type <%namespace%requestor>.  ?req <%namespace%performs> <%namespace%add>.  ?req <http://yarta.gforge.inria.fr/ontologies/conference/demo-conference.rdf#affiliation> ?group. %subject% <http://yarta.gforge.inria.fr/ontologies/conference/demo-conference.rdf#affiliation> ?group.%subject% mse:knows ?req.}";
		
		inputDataOntology = "PolicyManagerTest/mse-1.1-nc.rdf";
		String dataModelPath = "/Users/hachemsara/Documents/workspace/test/data/demo-conference-vi.rdf";
		m = ModelFactory.createDefaultModel();
		in = FileManager.get().open(inputDataOntology);
		m.read(in,null,null);
		dataModel =  ModelFactory.createDefaultModel();
		inData = FileManager.get().open(dataModelPath);
		dataModel.read(inData,null,null);
		in.close();

	}


public void testInitializePR (){
	myPolicyManager.initializePR(PR, ns, owner);
	Object [] a = myPolicyManager.policies.toArray();
	
	if (myPolicyManager.policies.isEmpty())
    fail();
	
	if (a[0].equals(policy))
		{assertTrue("extract policies succeeded", true);
		}
	else
		fail();
}


public void testconstructTriple (){
	myPolicyManager.initializePR(PR1, ns, owner);
	resultQuery = myPolicyManager.constructTriple(policyWithType, variableSubject, predicate, variableObject);
	if (resultQuery.equals(policyInstance))
		assertTrue("type successfully created", true);
	else
		fail();
	
	newPolicy2 = myPolicyManager.constructQuery(policyWithType, variableSubject, predicate, object);
	resultQuery = myPolicyManager.constructTriple(newPolicy2, variableSubject, predicate, object);
	
	if (resultQuery.contentEquals(ConstructedPolicyInstance)) {
		assertTrue ("query succesfully constructed", true);
		}
	else
		fail();
		}

public void testCompareToRequest() {
	boolean answer;
	myPolicyManager.initializePR(PR, ns, owner);
	answer = myPolicyManager.compareToRequest (preDefinedResource, variableSubject,email_predicate, variableObj);
	if (answer == true)
		assertTrue ("query constructed successfully",true);
	else
		fail();
}


public void testisAllowed (){
	boolean answer;
	myPolicyManager.initializePR(PR, ns, owner);
	String action = "read";
	String requestor= "valerie@yarta.inria.fr";
	Triple requestTriple = new MSETriple (variableSubject, email_predicate, emailValue);
	answer = myPolicyManager.isAllowed(action, requestor,dataModel, requestTriple );
	
	if (answer == true)
		assertTrue ("policy evaluation successfull",true);
	else
		fail();
		}

public void testisAllowed1 (){
	boolean answer;
	myPolicyManager.initializePR(PR1, ns, owner);
	String action = "read";
	String requestor1= "valerie@yarta.inria.fr";
	Triple requestTriple1 = new MSETriple (owner, predicate, variableObject);
	
	answer = myPolicyManager.isAllowed(action, requestor1,dataModel, requestTriple1 );
	if (answer == true)
	assertTrue ("policy evaluation successfull",true);
	else
		fail();
		}

public void testisAllowed2(){
	boolean answer;
	myPolicyManager.initializePR(PR, ns, owner);
	String action = "read";
	String requestor1= "alessandra@yarta.inria.fr";
	Triple requestTriple1 = new MSETriple (owner, namePredicate, variableValue);
	
	answer = myPolicyManager.isAllowed(action, requestor1,dataModel, requestTriple1 );
	if (answer == true)
	assertTrue ("policy evaluation successfull",true);
	else
		fail();
		}

public void testisAllowedAdd (){
	boolean answer;
	myPolicyManager.initializePR(PR1, ns, owner);
	String action = "add";
	String requestor1= "valerie@yarta.inria.fr";
	Triple requestTriple1 = new MSETriple (owner, email_predicate, emailValue);
	
	answer = myPolicyManager.isAllowed(action, requestor1,dataModel, requestTriple1 );
	if (answer == true)
	assertTrue ("policy evaluation successfull",true);
	else
		fail();
		}

public void testisAllowedAdd1 (){
	boolean answer;
	myPolicyManager.initializePR(PR1, ns, owner);
	String action = "add";
	String requestor1= "valerie@yarta.inria.fr";
	Triple requestTriple1 = new MSETriple (owner, email_predicate, variableObject);
	
	answer = myPolicyManager.isAllowed(action, requestor1,dataModel, requestTriple1 );
	if (answer == false)
		assertFalse ("policy evaluation successfull",false);
	else
		fail();
		}

public void testisAllowedRemove(){
	boolean answer;
	myPolicyManager.initializePR(PR1, ns, owner);
	String action = "remove";
	String requestor1= "valerie@yarta.inria.fr";
	Triple requestTriple1 = new MSETriple (owner, email_predicate, emailValue);
	answer = myPolicyManager.isAllowed(action, requestor1,dataModel, requestTriple1 );
	if (answer == true)
	assertTrue ("policy evaluation successfull",true);
	else
	{ System.out.println(newPolicy);
	fail();}
	}
		

public void testisAllowedRemove1(){
	boolean answer;
	myPolicyManager.initializePR(PR1, ns, owner);
	String action = "remove";
	String requestor1= "alessandra@yarta.inria.fr";
	Triple requestTriple1 = new MSETriple (owner, predicate, object);
	
	answer = myPolicyManager.isAllowed(action, requestor1,dataModel, requestTriple1 );
	if (answer == true)
		assertTrue ("policy evaluation successfull",true);
	else
		fail();
		}

public void testfilterModel() {
	myPolicyManager.initializePR(PR_demo, ns, owner);
	String action = "read";
	String requestor1= "valerie@yarta.inria.fr";
	g = new MSEGraph(dataModel);
Model filtered = myPolicyManager.filteredModel(action, requestor1, dataModel, g);
if (g.equals(dataModel))
	assertTrue("policy evaluation successfull",true);
//System.out.println(filtered);
}

public void testfilterModel1() {
	myPolicyManager.initializePR(PR1, ns, owner);
	String action = "read";
	String requestor1= "valerie@yarta.inria.fr";
	g = new MSEGraph(dataModel);
Model filtered = myPolicyManager.filteredModel(action, requestor1, dataModel, g);
System.out.println(filtered);
}

public void testfilterModel2() {
	myPolicyManager.initializePR(PR1, ns, owner);
	String action = "read";
	String requestor1= "alessandra@yarta.inria.fr"; 
	g = new MSEGraph(dataModel);
Model filtered = myPolicyManager.filteredModel(action, requestor1, dataModel, g);
System.out.println(filtered);
}

 public void testisAllowedModel() {
	 myPolicyManager.initializePR(PR_demo, ns, owner2);
	 String action = "read";
		String requestor1= "alessandra@yarta.inria.fr";
		Triple affiliationTriple = new MSETriple (variableSubject, variableObj, variablePred);
		Model filtered = myPolicyManager.isAllowedModel(action, requestor1, dataModel, affiliationTriple);
		System.out.println("filtered model is" + filtered);
		String action1 = "add";
		g = new MSEGraph (filtered);
		Model filtered_add = myPolicyManager.filteredModel(action1, requestor1, dataModel, g);
		System.out.println(filtered_add);
		
 }

}


