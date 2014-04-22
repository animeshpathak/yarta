package fr.inria.arles.yarta.android.library.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpProtocolParams;

public class CASUtil {

	public CASUtil() {
	}

	public boolean performLogin(String username, String password) {
		try {
			if (username.equals("anonymous") && password.equals("anonymous")) {
				return true;
			}
			return internalLogin(username, password);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	private boolean internalLogin(String username, String password)
			throws Exception {

		DefaultHttpClient client = new DefaultHttpClient();

		HttpProtocolParams
				.setUserAgent(
						client.getParams(),
						"Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

		HttpResponse response = client.execute(new HttpGet(
				"https://cas.inria.fr/cas/login"));

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				response.getEntity().getContent()));

		String content = "";
		String line;
		while ((line = reader.readLine()) != null) {
			content += line;
		}

		String search = "name=\"lt\" value=\"";
		int idx = content.indexOf(search);

		String lt = content.substring(idx + search.length(),
				content.indexOf('"', idx + search.length()));

		search = "name=\"_eventId\" value=\"";
		idx = content.indexOf(search);
		String eventId = content.substring(idx + search.length(),
				content.indexOf('"', idx + search.length()));

		HttpPost post = new HttpPost("https://cas.inria.fr/cas/login");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("username", username));
		nameValuePairs.add(new BasicNameValuePair("password", password));
		nameValuePairs.add(new BasicNameValuePair("lt", lt));
		nameValuePairs.add(new BasicNameValuePair("_eventId", eventId));

		post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		response = client.execute(post);

		reader = new BufferedReader(new InputStreamReader(response.getEntity()
				.getContent()));

		content = "";

		boolean success = false;
		while ((line = reader.readLine()) != null) {
			content += line;
			if (line.contains("Log In Successful")) {
				success = true;
				break;
			}
		}

		reader.close();

		return success;
	}
}
