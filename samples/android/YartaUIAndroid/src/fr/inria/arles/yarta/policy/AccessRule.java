package fr.inria.arles.yarta.policy;

import java.util.HashMap;
import java.util.Map;

public class AccessRule {

	public static class Resource {
		public String className;
		public String guid;

		public Resource() {
			this.className = "*";
			this.guid = "*";
		}

		public Resource(String className, String guid) {
			this.className = className;
			this.guid = guid;
		}
	}

	public final static int READ = 0;
	public final static int ADD = 1;
	public final static int REMOVE = 2;

	public final static int SUBJECT = 1;
	public final static int OBJECT = 2;
	public final static int SPECIFIC = 3;

	public final static int LINK_NONE = 0;
	public final static int LINK_AS_OBJECT = 1;
	public final static int LINK_AS_SUBJECT = 2;

	public AccessRule() {
	}

	public AccessRule(String rule) {
		int firstOpenBrace = rule.indexOf("{");
		int firstCloseBrace = rule.indexOf("}");

		int secondOpenBrace = rule.indexOf("{", firstOpenBrace + 1);
		int secondCloseBrace = rule.indexOf("}", firstCloseBrace + 1);

		String conditions = rule.substring(secondOpenBrace + 1,
				secondCloseBrace);

		String array[] = conditions.split("\\s\\.\\s");

		for (int i = 0; i < array.length; i++) {
			String condition = array[i].trim();

			String triple[] = condition.split("\\s");

			if (triple[0].equals("?req") && triple[1].equals("rdf:type")) {
				continue;
			}

			// the right
			if (triple[2].equals("mse:add")) {
				right = ADD;
				continue;
			} else if (triple[2].equals("mse:read")) {
				right = READ;
				continue;
			} else if (triple[2].equals("mse:remove")) {
				right = REMOVE;
				continue;
			}

			if (condition.contains("?req") && condition.contains("?info")) {
				requestorPredicate = condition.replace("?req", "")
						.replace("?info", "").replace("mse:", "").trim();
			} else if (condition.startsWith("?info")) {
				link = LINK_AS_SUBJECT;
			} else if (condition.endsWith("?info")) {
				link = LINK_AS_OBJECT;
			}

			if (condition.contains("%subject% rdf:type")) {
				infoSubject.className = condition.replace("%subject%", "")
						.replace("rdf:type", "").replace("mse:", "").trim();

				if (link == LINK_AS_SUBJECT) {
					if (requestor == SUBJECT) {
						requestorObject.className = infoSubject.className;
					} else {
						requestorSubject.className = infoSubject.className;
					}
				}
			}

			if (condition.contains("%object% rdf:type")) {
				infoObject.className = triple[2].replace("mse:", "").trim();

				if (link == LINK_AS_OBJECT) {
					if (requestor == SUBJECT) {
						requestorObject.className = infoObject.className;
					} else {
						requestorSubject.className = infoObject.className;
					}
				}
			}

			if (triple[1].equals("rdf:type") && !triple[0].equals("%subject%")
					&& !triple[0].equals("%object%")) {
				addType(triple[0], triple[2]);
			}

			if (condition.contains("%subject% %predicate%")
					&& !condition.contains("%object%")
					&& !condition.contains("?info")) {
				infoObject.guid = condition.replace("%subject%", "")
						.replace("%predicate%", "").replace("<", "")
						.replace(">", "").trim();
			}

			if (condition.contains("%predicate% %object%")
					&& !condition.contains("%subject%")
					&& !condition.contains("?info")) {
				infoSubject.guid = condition.replace("%predicate%", "")
						.replace("%object%", "").replace("<", "")
						.replace(">", "").trim();
			}

			if (triple[0].equals("%subject%")
					&& !triple[1].equals("%predicate%")
					&& triple[2].equals("%object%")) {
				infoPredicate = triple[1].replace("mse:", "").trim();
			}

			if (condition.startsWith("?req") && !condition.contains("%object%")
					&& !condition.contains("%subject%")
					&& !condition.contains("%predicate%")) {
				requestor = SUBJECT;
				requestorSubject.className = "Person";
				requestorSubject.guid = "?req";

				requestorPredicate = triple[1].replace("mse:", "");

				requestorObject.className = getType(triple[2]);
				requestorObject.guid = triple[2].replace("<", "").replace(">",
						"");
			} else if (condition.endsWith("?req")
					&& !condition.contains("%object%")
					&& !condition.contains("%subject%")
					&& !condition.contains("%predicate%")) {
				requestor = OBJECT;
				requestorObject.className = "Person";
				requestorObject.guid = "?req";

				requestorPredicate = triple[1].replace("mse:", "");

				requestorSubject.className = getType(triple[0]);
				requestorSubject.guid = triple[0].replace("<", "").replace(">",
						"");
			} else if (triple[1].equals("rdf:type")
					&& triple[2].equals("mse:requestor")) {
				requestor = SPECIFIC;
				requestorSubject.className = "Person";
				requestorSubject.guid = triple[0].replace("<", "").replace(">",
						"");
			}
		}
	}

	private void addType(String guid, String className) {
		propertiesMap.put(guid, className.replace("mse:", ""));
	}

	private String getType(String guid) {
		return propertiesMap.get(guid);
	}

	private Map<String, String> propertiesMap = new HashMap<String, String>();

	public void setRight(int right) {
		this.right = right;
	}

	public int getRight() {
		return right;
	}

	public void setRequestor(int requestor) {
		this.requestor = requestor;
	}

	public int getRequestor() {
		return requestor;
	}

	public void setRequestorPredicate(String predicate) {
		this.requestorPredicate = predicate;
	}

	public String getRequestorPredicate() {
		return requestorPredicate;
	}

	public void setRequestorObject(Resource resource) {
		this.requestorObject = resource;
	}

	public Resource getRequestorObject() {
		return this.requestorObject;
	}

	public void setRequestorSubject(Resource resource) {
		this.requestorSubject = resource;
	}

	public Resource getRequestorSubject() {
		return this.requestorSubject;
	}

	public void setLink(int link) {
		this.link = link;
	}

	public int getLink() {
		return link;
	}

	public void setInfoSubject(Resource resource) {
		this.infoSubject = resource;
	}

	public Resource getInfoSubject() {
		return infoSubject;
	}

	public void setInfoPredicate(String predicate) {
		this.infoPredicate = predicate;
	}

	public String getInfoPredicate() {
		return infoPredicate;
	}

	public void setInfoObject(Resource resource) {
		this.infoObject = resource;
	}

	public Resource getInfoObject() {
		return infoObject;
	}

	public String toString() {
		String result = "";

		result += "PREFIX mse:<http://yarta.gforge.inria.fr/ontologies/mse.rdf#> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>";
		result += "CONSTRUCT { %subject% %predicate% %object% } where { ";

		if (requestor == SUBJECT || requestor == OBJECT) {
			result += "?req rdf:type mse:requestor . ";
			result += "?req mse:performs mse:"
					+ (right == ADD ? "add" : right == READ ? "read" : "remove")
					+ " . ";
		} else {
			result += "<" + requestorSubject.guid
					+ "> rdf:type mse:requestor . ";
			result += "<"
					+ requestorSubject.guid
					+ "> mse:performs mse:"
					+ (right == ADD ? "add" : right == READ ? "read" : "remove")
					+ " . ";
		}

		if (requestor == SUBJECT) {
			if (!requestorPredicate.equals("*")) {
				if (link != LINK_NONE) {
					if (!requestorObject.className.equals("*")) {
						result += String.format("?info rdf:type mse:%s . ",
								requestorObject.className);
					}
					result += "?req mse:" + requestorPredicate + " ?info"
							+ " . ";
				} else if (!requestorObject.guid.equals("*")) {
					result += String.format("<%s> rdf:type mse:%s . ",
							requestorObject.guid, requestorObject.className);
					result += String.format("?req mse:%s <%s> . ",
							requestorPredicate, requestorObject.guid);
				}
			}
		} else if (requestor == OBJECT) {
			if (!requestorPredicate.equals("*")) {
				if (link != LINK_NONE) {
					if (!requestorSubject.className.equals("*")) {
						result += String.format("?info rdf:type mse:%s . ",
								requestorSubject.className);
					}
					result += "?info mse:" + requestorPredicate + " ?req . ";
				} else if (!requestorSubject.guid.equals("*")) {
					result += String.format("<%s> rdf:type mse:%s . ",
							requestorSubject.guid, requestorSubject.className);
					result += String.format("<%s> mse:%s ?req . ",
							requestorSubject.guid, requestorPredicate);
				}
			}
		}

		if (link == LINK_AS_OBJECT) {
			result += "%subject% %predicate% ?info . ";
		} else if (link == LINK_AS_SUBJECT) {
			result += "?info %predicate% %object% . ";
		} else if (link == LINK_NONE) {
			if (!infoSubject.guid.equals("*")) {
				if (infoSubject.guid.equals("?req")) {
					result += infoSubject.guid + " %predicate% %object% . ";
				} else {
					result += "<" + infoSubject.guid
							+ "> %predicate% %object% . ";
				}
			}

			if (!infoObject.guid.equals("*")) {
				if (infoObject.guid.equals("?req")) {
					result += "%subject% %predicate% " + infoObject.guid
							+ " . ";
				} else {
					result += "%subject% %predicate% <" + infoObject.guid
							+ "> . ";
				}
			}
		}

		if (!infoPredicate.equals("*")) {
			result += "%subject% mse:" + infoPredicate + " %object% . ";
		}

		if (!infoSubject.className.equals("*")) {
			result += "%subject% rdf:type mse:" + infoSubject.className + " . ";
		}

		if (!infoObject.className.equals("*")) {
			result += "%object% rdf:type mse:" + infoObject.className + " . ";
		}

		if (right == REMOVE || right == READ) {
			result += "%subject% %predicate% %object% . ";
		}
		result += "}";
		return result;
	}

	public String toShortString() {
		return "short-version-of-the-rule";
	}

	private int right = READ;

	private int requestor = SUBJECT;
	private String requestorPredicate = "*";
	private Resource requestorObject = new Resource();
	private Resource requestorSubject = new Resource("Person", "?req");

	private int link = LINK_NONE;

	private Resource infoSubject = new Resource();
	private String infoPredicate = "*";
	private Resource infoObject = new Resource();
}
