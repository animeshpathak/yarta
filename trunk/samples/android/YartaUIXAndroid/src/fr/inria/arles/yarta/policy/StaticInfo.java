package fr.inria.arles.yarta.policy;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.AssetManager;

public class StaticInfo {
	public final static String ANY = "*";

	public static StaticInfo getInstance() {
		return s_instance;
	}

	private static StaticInfo s_instance = new StaticInfo();

	private StaticInfo() {
	}
	
	public void loadRelationships(Context context) {
		String content = "";
		try {
			InputStream fin = context.getAssets().open("relationships.js",
					AssetManager.ACCESS_RANDOM);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(fin));

			String line;

			while ((line = reader.readLine()) != null) {
				content += line;
			}
			
			reader.close();
			
			relationshipsJSON = new JSONObject(content);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private JSONObject relationshipsJSON;

	public List<String> getAllClasses() {
		List<String> allClasses = new ArrayList<String>();
		allClasses.add(StaticInfo.ANY);

		Iterator<?> it = relationshipsJSON.keys();
		while (it.hasNext()) {
			String currentClass = (String) it.next();
			allClasses.add(currentClass);
		}

		Collections.sort(allClasses);
		return allClasses;
	}

	public List<String> getPredicates(String subjectClass, String objectClass) {
		
		if (subjectClass == null) {
			subjectClass = StaticInfo.ANY;
		}
		
		if (objectClass == null) {
			objectClass = StaticInfo.ANY;
		}
		
		List<String> subjectPredicates = getPredicates(subjectClass);
		List<String> objectPredicates = getInversePredicates(objectClass);

		for (int i = subjectPredicates.size() - 1; i > -1; --i) {
			String str = subjectPredicates.get(i);
			if (!objectPredicates.remove(str)) {
				subjectPredicates.remove(str);
			}
		}

		Collections.sort(subjectPredicates);

		return subjectPredicates;
	}

	public List<String> getAllObjects(String subjectClass, String predicate) {
		Set<String> objects = new HashSet<String>();
		
		if (subjectClass == null) {
			subjectClass = StaticInfo.ANY;
		}
		
		if (predicate == null) {
			predicate = StaticInfo.ANY;
		}

		if (subjectClass.equals(StaticInfo.ANY)) {
			Iterator<?> it = relationshipsJSON.keys();
			while (it.hasNext()) {
				String currentClass = (String) it.next();
				objects.addAll(getAllObjects(currentClass, predicate));
			}
			ArrayList<String> result = new ArrayList<String>(objects);
			Collections.sort(result);
			return result;
		}

		try {
			JSONObject requestedClass = relationshipsJSON
					.getJSONObject(subjectClass);

			JSONObject objectProperties = requestedClass.getJSONObject(
					"isSubjectOf").getJSONObject("objectProperties");

			Iterator<?> it = objectProperties.keys();
			while (it.hasNext()) {
				String objectProperty = (String) it.next();

				if (objectProperty.equals(predicate)
						|| predicate.equals(StaticInfo.ANY)) {
					JSONArray array = objectProperties
							.getJSONArray(objectProperty);

					for (int i = 0; i < array.length(); i++) {
						objects.add(array.getString(i));
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		List<String> result = new ArrayList<String>(objects);
		result.add(StaticInfo.ANY);
		Collections.sort(result);

		return result;
	}

	public List<String> getAllSubjects(String predicate, String objectClass) {
		
		if (objectClass == null) {
			objectClass = StaticInfo.ANY;
		}
		
		if (predicate == null) {
			predicate = StaticInfo.ANY;
		}
		
		Set<String> result = new HashSet<String>();
		
		result.add(StaticInfo.ANY);

		Iterator<?> it = relationshipsJSON.keys();
		while (it.hasNext()) {
			String currentClass = (String) it.next();
			try {
				JSONObject requestedClass = relationshipsJSON
						.getJSONObject(currentClass);

				JSONObject objectProperties = requestedClass.getJSONObject(
						"isSubjectOf").getJSONObject("objectProperties");

				Iterator<?> itObj = objectProperties.keys();
				while (itObj.hasNext()) {
					String objectProperty = (String) itObj.next();

					if (predicate.equals(objectProperty)
							|| predicate.equals(StaticInfo.ANY)) {
						JSONArray array = objectProperties
								.getJSONArray(objectProperty);

						for (int i = 0; i < array.length(); i++) {
							String object = array.getString(i);
							if (objectClass.equals(object)
									|| objectClass.equals(StaticInfo.ANY)) {
								result.add(currentClass);
							}
						}
					}
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		List<String> list = new ArrayList<String>(result);
		Collections.sort(list);
		return list;
	}

	private List<String> getPredicates(String selectedClass) {
		List<String> predicates = new ArrayList<String>();

		if (selectedClass.equals(StaticInfo.ANY)) {
			Set<String> result = new HashSet<String>();

			Iterator<?> it = relationshipsJSON.keys();
			while (it.hasNext()) {
				String currentClass = (String) it.next();
				result.addAll(getPredicates(currentClass));
			}

			predicates.addAll(result);
			return predicates;
		}

		predicates.add(StaticInfo.ANY);

		try {
			JSONObject requestedClass = relationshipsJSON
					.getJSONObject(selectedClass);

			JSONObject objectProperties = requestedClass.getJSONObject(
					"isSubjectOf").getJSONObject("objectProperties");
			Iterator<?> it = objectProperties.keys();
			while (it.hasNext()) {
				String objectProperty = (String) it.next();
				predicates.add(objectProperty);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return predicates;
	}

	private List<String> getInversePredicates(String selectedClass) {
		List<String> predicates = new ArrayList<String>();

		if (selectedClass.equals(StaticInfo.ANY)) {
			Set<String> result = new HashSet<String>();
			Iterator<?> it = relationshipsJSON.keys();
			while (it.hasNext()) {
				String currentClass = (String) it.next();
				result.addAll(getInversePredicates(currentClass));
			}
			predicates.addAll(result);
			return predicates;
		}

		predicates.add(StaticInfo.ANY);

		try {
			JSONObject requestedClass = relationshipsJSON
					.getJSONObject(selectedClass);

			JSONObject objectProperties = requestedClass
					.getJSONObject("isObjectOf");
			Iterator<?> it = objectProperties.keys();
			while (it.hasNext()) {
				String objectProperty = (String) it.next();
				predicates.add(objectProperty);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return predicates;
	}
}
