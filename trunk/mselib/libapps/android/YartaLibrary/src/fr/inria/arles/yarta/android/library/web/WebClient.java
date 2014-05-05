package fr.inria.arles.yarta.android.library.web;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import fr.inria.arles.yarta.android.library.util.Base64;
import fr.inria.arles.yarta.logging.YLoggerFactory;

/**
 * Elgg Android Client
 * 
 * @author grosca
 */
public class WebClient {

	// work;
	// private static final String ElggBase =
	// "http://128.93.51.31/elgg/services/api/rest/xml/?";
	// private static final String PublicKey =
	// "5b68ca6a34bfeb566d16630eb8a59b8d60570463";
	// private static final String PrivateKey =
	// "7b3c034ff8216b4ae475668fc83596040c10ab4e";

	// public static final String ElggCAS =
	// "http://128.93.51.31/elgg/cas_auth_ws?";

	private static final String ElggBase = "https://reseau-iris.inria.fr/services/api/rest/xml/?";
	private static final String PublicKey = "8db4eb33c014379e40c6d7d28b4b6b290e6359e7";
	private static final String PrivateKey = "acc41359feefd470d87410bdfbd80828e852993f";

	public static final String ElggCAS = "https://reseau-iris.inria.fr/cas_auth_ws?";

	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

	private static WebClient instance = new WebClient();

	private String userToken;
	private String userName;
	private String userGuid;

	private String lastError;

	public static final int ACCESS_PUBLIC = 2;
	public static final int ACCESS_LOGGED_USERS = 1;

	public static final int RESULT_OK = 0;
	public static final int RESULT_AUTH_FAILED = -20;

	private void log(String format, Object... args) {
		YLoggerFactory.getLogger().d("IrisClient", String.format(format, args));
	}

	/**
	 * Interface to be called when we must login
	 * 
	 * @author grosca
	 */
	public interface WebErrorCallback {
		public void onAuthenticationFailed();

		public void onNetworkFailed();
	}

	private static WebErrorCallback callback;

	public void setErrorCallback(WebErrorCallback callback) {
		WebClient.callback = callback;
	}

	private void notifyLoginObservers() {
		if (callback != null) {
			callback.onAuthenticationFailed();
		}
	}

	private WebClient() {
	}

	public static WebClient getInstance() {
		return instance;
	}

	public String getUsername() {
		return userName;
	}

	public String getUserToken() {
		return userToken;
	}

	public String getUserGuid() {
		return userGuid;
	}

	public void setUsername(String userName) {
		if (userName != null && userName.contains("@")) {
			this.userName = userName.substring(0, userName.indexOf('@'));
		} else {
			this.userName = userName;
		}
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

	public void setUserGuid(String guid) {
		this.userGuid = guid;
	}

	public String getLastError() {
		return lastError;
	}

	private boolean noInternet() {
		if (lastError != null) {
			return lastError.contains("UnknownHostException")
					|| lastError.contains("Hostname");
		}
		return false;
	}

	public String getUserGuid(String username) {
		String urlParams = "method=user.getguid&username=" + username;

		try {
			String xml = doAuthenticatedPost(ElggBase, urlParams);
			Document doc = loadXMLFromString(xml);
			Node node = doc.getElementsByTagName("result").item(0);
			if (node != null) {
				return node.getTextContent();
			}

			Node n = doc.getElementsByTagName("message").item(0);
			if (n != null) {
				lastError = n.getTextContent();
			}
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "getUserGuid", 0, lastError);
		return null;
	}

	public int addFriend(String username) {
		String urlParams = "method=user.friend.add&friend=" + username;

		int result = -1;

		try {
			String xml = doAuthenticatedPost(ElggBase, urlParams);
			Document doc = loadXMLFromString(xml);
			String rs = doc.getElementsByTagName("status").item(0)
					.getTextContent();

			result = Integer.parseInt(rs);
			Node n = doc.getElementsByTagName("message").item(0);
			if (n != null) {
				lastError = n.getTextContent();
			}
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "addFriend", result, lastError);

		if (result == RESULT_AUTH_FAILED) {
			notifyLoginObservers();
		}

		return result;
	}

	public List<UserItem> getFriends(String username) {
		lastError = null;
		String urlParams = "method=user.friend.get_friends&username="
				+ username;

		List<UserItem> users = new ArrayList<UserItem>();

		int result = -1;

		try {
			String xml = doAuthenticatedGet(ElggBase, urlParams);
			Document doc = loadXMLFromString(xml);

			String rs = doc.getElementsByTagName("status").item(0)
					.getTextContent();

			result = Integer.parseInt(rs);

			NodeList nl = doc.getElementsByTagName("array_item");

			for (int i = 0; i < nl.getLength(); i++) {
				Element el = (Element) nl.item(i);

				String name = el.getElementsByTagName("name").item(0)
						.getTextContent();

				String uname = el.getElementsByTagName("username").item(0)
						.getTextContent();

				String avatar_url = el.getElementsByTagName("avatar_url")
						.item(0).getTextContent();

				users.add(new UserItem(name, uname, avatar_url));
			}
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "getFriends", result, lastError);

		if (result == RESULT_AUTH_FAILED) {
			notifyLoginObservers();
		}

		if (noInternet()) {
			callback.onNetworkFailed();
		}

		return users;
	}

	public int setUser(String username, UserItem user) {
		lastError = null;
		String urlParams = "method=user.save_profile&username=" + username;
		String array = "&profile[website]=" + encode(user.getWebsite());
		array += "&profile[name]=" + encode(user.getName());
		urlParams += array;

		int result = -1;

		try {
			String xml = doAuthenticatedPost(ElggBase, urlParams);
			Document doc = loadXMLFromString(xml);

			String rs = doc.getElementsByTagName("status").item(0)
					.getTextContent();

			result = Integer.parseInt(rs);

			Node n = doc.getElementsByTagName("message").item(0);
			if (n != null) {
				lastError = n.getTextContent();
			}

			n = doc.getElementsByTagName("result").item(0);
			if (n != null) {
				lastError = n.getTextContent();
			}
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "setUser", result, lastError);

		if (result == RESULT_AUTH_FAILED) {
			notifyLoginObservers();
		}

		if (noInternet()) {
			callback.onNetworkFailed();
		}

		return result;
	}

	public List<ObjectItem> search(String query) {
		lastError = null;
		String urlParams = "method=site.search&query=" + encode(query);
		List<ObjectItem> items = new ArrayList<ObjectItem>();

		int result = -1;

		try {
			String xml = doAuthenticatedGet(ElggBase, urlParams);
			Document doc = loadXMLFromString(xml);

			String rs = doc.getElementsByTagName("status").item(0)
					.getTextContent();

			result = Integer.parseInt(rs);

			Node n = doc.getElementsByTagName("message").item(0);
			if (n != null) {
				lastError = n.getTextContent();
			}

			NodeList nl = doc.getElementsByTagName("array_item");

			for (int i = 0; i < nl.getLength(); i++) {
				Element el = (Element) nl.item(i);

				String guid = el.getElementsByTagName("guid").item(0)
						.getTextContent();
				String title = el.getElementsByTagName("title").item(0)
						.getTextContent();
				String type = el.getElementsByTagName("type").item(0)
						.getTextContent();
				if (type.equals("object")) {
					type = el.getElementsByTagName("subtype").item(0)
							.getTextContent();
				}

				if (type.equals("user") || type.equals("group")
						|| type.equals("blog")) {
					String avatarURL = el.getElementsByTagName("avatar_url")
							.item(0).getTextContent();
					items.add(new ObjectItem(guid, title, avatarURL, type));
				}
			}
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "search", result, lastError);

		if (result == RESULT_AUTH_FAILED) {
			notifyLoginObservers();
		}

		if (noInternet()) {
			callback.onNetworkFailed();
		}

		return items;
	}

	public List<PostItem> getPostComments(String postGuid) {
		lastError = null;
		String urlParams = "method=blog.get_comments&guid=" + postGuid;
		List<PostItem> comments = new ArrayList<PostItem>();

		int result = -1;
		try {
			String xml = doAuthenticatedGet(ElggBase, urlParams);
			Document doc = loadXMLFromString(xml);

			String rs = doc.getElementsByTagName("status").item(0)
					.getTextContent();

			result = Integer.parseInt(rs);

			Node n = doc.getElementsByTagName("message").item(0);
			if (n != null) {
				lastError = n.getTextContent();
			}

			NodeList nl = doc.getElementsByTagName("array_item");

			for (int i = 0; i < nl.getLength(); i++) {
				Element el = (Element) nl.item(i);

				String guid = el.getElementsByTagName("guid").item(0)
						.getTextContent();
				String title = el.getElementsByTagName("description").item(0)
						.getTextContent();
				long time = Integer.parseInt(el
						.getElementsByTagName("time_created").item(0)
						.getTextContent()) * 1000;

				Element elOwner = (Element) el.getElementsByTagName("owner")
						.item(0);
				String name = elOwner.getElementsByTagName("name").item(0)
						.getTextContent();
				String username = elOwner.getElementsByTagName("username")
						.item(0).getTextContent();
				String avatarURL = elOwner.getElementsByTagName("avatar_url")
						.item(0).getTextContent();

				UserItem owner = new UserItem(name, username, avatarURL);

				comments.add(new PostItem(guid, title, time, owner));
			}

		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "getPostComments", result,
				lastError);

		if (result == RESULT_AUTH_FAILED) {
			notifyLoginObservers();
		}

		if (noInternet()) {
			callback.onNetworkFailed();
		}

		return comments;
	}

	public List<PostItem> getGroupPosts(String groupGuid) {
		lastError = null;
		String urlParams = "method=blog.get_posts&context=group&group_guid="
				+ groupGuid;

		int result = -1;

		List<PostItem> posts = new ArrayList<PostItem>();

		try {
			String xml = doAuthenticatedGet(ElggBase, urlParams);
			Document doc = loadXMLFromString(xml);

			String rs = doc.getElementsByTagName("status").item(0)
					.getTextContent();

			result = Integer.parseInt(rs);

			Node n = doc.getElementsByTagName("message").item(0);
			if (n != null) {
				lastError = n.getTextContent();
			}

			NodeList nl = doc.getElementsByTagName("array_item");

			for (int i = 0; i < nl.getLength(); i++) {
				Element el = (Element) nl.item(i);

				String guid = el.getElementsByTagName("guid").item(0)
						.getTextContent();
				String title = el.getElementsByTagName("title").item(0)
						.getTextContent();
				long time = Integer.parseInt(el
						.getElementsByTagName("time_created").item(0)
						.getTextContent()) * 1000;

				Element elOwner = (Element) el.getElementsByTagName("owner")
						.item(0);
				String name = elOwner.getElementsByTagName("name").item(0)
						.getTextContent();
				String username = elOwner.getElementsByTagName("username")
						.item(0).getTextContent();
				String avatarURL = elOwner.getElementsByTagName("avatar_url")
						.item(0).getTextContent();

				UserItem owner = new UserItem(name, username, avatarURL);

				posts.add(new PostItem(guid, title, time, owner));
			}
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "getGroupPosts", result, lastError);

		if (result == RESULT_AUTH_FAILED) {
			notifyLoginObservers();
		}

		if (noInternet()) {
			callback.onNetworkFailed();
		}

		return posts;
	}

	public PostItem getPost(String guid) {
		lastError = null;
		String urlParams = "method=blog.get_post&guid=" + guid + "&username="
				+ userName;

		int result = -1;

		String title = null;
		String description = null;
		long time = 0;
		UserItem owner = null;

		try {
			String xml = doAuthenticatedGet(ElggBase, urlParams);
			Document doc = loadXMLFromString(xml);

			String rs = doc.getElementsByTagName("status").item(0)
					.getTextContent();

			result = Integer.parseInt(rs);

			Node n = doc.getElementsByTagName("message").item(0);
			if (n != null) {
				lastError = n.getTextContent();
			}

			title = doc.getElementsByTagName("title").item(0).getTextContent();
			description = doc.getElementsByTagName("content").item(0)
					.getTextContent();

			String owner_guid = doc.getElementsByTagName("owner_guid").item(0)
					.getTextContent();
			String owner_username = getUsername(owner_guid);

			owner = getUser(owner_username);
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		if (noInternet()) {
			callback.onNetworkFailed();
		}

		log("%s: result<%d>, lastError<%s>", "getPost", result, lastError);

		return new PostItem(guid, title, description, time, owner);
	}

	public List<GroupItem> getGroups(String username) {
		lastError = null;
		String urlParams = "method=group.get_groups&context=user&username="
				+ username;

		int result = -1;
		List<GroupItem> items = new ArrayList<GroupItem>();

		try {
			String xml = doAuthenticatedGet(ElggBase, urlParams);
			Document doc = loadXMLFromString(xml);

			String rs = doc.getElementsByTagName("status").item(0)
					.getTextContent();

			result = Integer.parseInt(rs);

			Node n = doc.getElementsByTagName("message").item(0);
			if (n != null) {
				lastError = n.getTextContent();
			}

			NodeList nl = doc.getElementsByTagName("array_item");

			for (int i = 0; i < nl.getLength(); i++) {
				Element el = (Element) nl.item(i);
				String name = el.getElementsByTagName("name").item(0)
						.getTextContent();
				int members = Integer.parseInt(el
						.getElementsByTagName("members").item(0)
						.getTextContent());
				String avatarURL = el.getElementsByTagName("avatar_url")
						.item(0).getTextContent();
				String guid = el.getElementsByTagName("guid").item(0)
						.getTextContent();
				items.add(new GroupItem(guid, name, avatarURL, members));
			}
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "getGroups", result, lastError);

		if (result == RESULT_AUTH_FAILED) {
			notifyLoginObservers();
		}

		if (noInternet()) {
			callback.onNetworkFailed();
		}

		return items;
	}

	public GroupItem getGroup(String guid) {
		lastError = null;
		String urlParams = "method=group.get&guid=" + guid;

		int result = -1;

		String name = null;
		String ownerName = null;
		String avatarURL = null;
		int members = 0;

		try {
			String xml = doAuthenticatedGet(ElggBase, urlParams);
			Document doc = loadXMLFromString(xml);

			String rs = doc.getElementsByTagName("status").item(0)
					.getTextContent();

			result = Integer.parseInt(rs);

			Node n = doc.getElementsByTagName("message").item(0);
			if (n != null) {
				lastError = n.getTextContent();
			}

			name = doc.getElementsByTagName("name").item(0).getTextContent();
			ownerName = doc.getElementsByTagName("owner_name").item(0)
					.getTextContent();
			members = Integer.parseInt(doc
					.getElementsByTagName("members_count").item(0)
					.getTextContent());
			avatarURL = doc.getElementsByTagName("avatar_url").item(0)
					.getTextContent();
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "getGroup", result, lastError);

		if (result == RESULT_AUTH_FAILED) {
			notifyLoginObservers();
		}

		if (noInternet()) {
			callback.onNetworkFailed();
		}

		return new GroupItem(name, avatarURL, members, ownerName);
	}

	public UserItem getUser(String username) {
		lastError = null;
		String urlParams = "method=user.get_profile&username=" + username;

		String name = null;
		String website = null;
		String avatar_url = null;
		String inriaRoom = null;
		String inriaPhone = null;
		String inriaLocation = null;

		int result = -1;

		try {
			String xml = doAuthenticatedGet(ElggBase, urlParams);
			Document doc = loadXMLFromString(xml);

			String rs = doc.getElementsByTagName("status").item(0)
					.getTextContent();

			result = Integer.parseInt(rs);

			Node n = doc.getElementsByTagName("message").item(0);
			if (n != null) {
				lastError = n.getTextContent();
			}

			n = doc.getElementsByTagName("website").item(0);
			if (n != null) {
				website = ((Element) n).getElementsByTagName("value").item(0)
						.getTextContent();
			}

			n = doc.getElementsByTagName("avatar_url").item(0);
			if (n != null) {
				avatar_url = n.getTextContent();
			}

			n = doc.getElementsByTagName("name").item(0);
			if (n != null) {
				name = n.getTextContent();
			}

			n = doc.getElementsByTagName("inria_location").item(0);
			if (n == null)
				n = doc.getElementsByTagName("location").item(0);
			if (n != null) {
				inriaLocation = ((Element) n).getElementsByTagName("value")
						.item(0).getTextContent();
			}

			n = doc.getElementsByTagName("inria_room").item(0);

			if (n == null)
				n = doc.getElementsByTagName("room").item(0);

			if (n != null) {
				inriaRoom = ((Element) n).getElementsByTagName("value").item(0)
						.getTextContent();
			}

			n = doc.getElementsByTagName("inria_phone").item(0);
			if (n == null) {
				n = doc.getElementsByTagName("phone").item(0);
			}

			if (n != null) {
				inriaPhone = ((Element) n).getElementsByTagName("value")
						.item(0).getTextContent();
			}
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "getUser", result, lastError);
		if (result == RESULT_AUTH_FAILED) {
			notifyLoginObservers();
		}

		if (noInternet()) {
			callback.onNetworkFailed();
		}

		return new UserItem(name, website, avatar_url, inriaLocation,
				inriaRoom, inriaPhone);
	}

	public int addComment(String postGuid, String content) {
		String urlParams = "method=blog.post_comment&guid=" + postGuid
				+ "&text=" + encode(content);

		int result = -1;

		try {
			String xml = doAuthenticatedPost(ElggBase, urlParams);
			Document doc = loadXMLFromString(xml);

			String rs = doc.getElementsByTagName("status").item(0)
					.getTextContent();

			result = Integer.parseInt(rs);

			Node n = doc.getElementsByTagName("message").item(0);
			if (n != null) {
				lastError = n.getTextContent();
			}
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "addComment", result, lastError);

		if (result == RESULT_AUTH_FAILED) {
			notifyLoginObservers();
		}
		return result;
	}

	public int addWire(String content, int access) {
		String urlParams = "method=wire.save_post&username=" + userName
				+ "&text=" + encode(content) + "&access=" + access;

		int result = -1;
		try {
			String xml = doAuthenticatedPost(ElggBase, urlParams);
			Document doc = loadXMLFromString(xml);

			String rs = doc.getElementsByTagName("status").item(0)
					.getTextContent();

			result = Integer.parseInt(rs);

			Node n = doc.getElementsByTagName("message").item(0);
			if (n != null) {
				lastError = n.getTextContent();
			}
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "addWire", result, lastError);

		if (result == RESULT_AUTH_FAILED) {
			notifyLoginObservers();
		}
		return result;
	}

	public int removeWire(String guid) {
		String urlParams = "method=wire.delete_posts&username=" + userName
				+ "&wireid=" + guid;
		int result = -1;

		try {
			String xml = doAuthenticatedPost(ElggBase, urlParams);
			Document doc = loadXMLFromString(xml);

			String rs = doc.getElementsByTagName("status").item(0)
					.getTextContent();

			result = Integer.parseInt(rs);

			Node n = doc.getElementsByTagName("message").item(0);
			if (n != null) {
				lastError = n.getTextContent();
			}
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "removeWire", result, lastError);

		if (result == RESULT_AUTH_FAILED) {
			notifyLoginObservers();
		}

		return result;
	}

	public int readMessage(MessageItem item) {
		String urlParams = "method=messages.read&guid=" + item.getGuid();
		int result = -1;

		try {
			String xml = doAuthenticatedGet(ElggBase, urlParams);
			Document doc = loadXMLFromString(xml);

			String rs = doc.getElementsByTagName("status").item(0)
					.getTextContent();

			result = Integer.parseInt(rs);

			Node n = doc.getElementsByTagName("message").item(0);
			if (n != null) {
				lastError = n.getTextContent();
			}
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "readMessage", result, lastError);

		if (result == RESULT_AUTH_FAILED) {
			notifyLoginObservers();
		}

		return result;
	}

	public int removeMessage(MessageItem message) {
		String urlParams = "method=messages.remove&username=" + userName
				+ "&messageid=" + message.getGuid();

		int result = -1;

		try {
			String xml = doAuthenticatedPost(ElggBase, urlParams);
			Document doc = loadXMLFromString(xml);

			String rs = doc.getElementsByTagName("status").item(0)
					.getTextContent();

			result = Integer.parseInt(rs);

			Node n = doc.getElementsByTagName("message").item(0);
			if (n != null) {
				lastError = n.getTextContent();
			}
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "removeMessage", result, lastError);

		if (result == RESULT_AUTH_FAILED) {
			notifyLoginObservers();
		}

		return result;
	}

	public int sendMessage(String username, String subject, String body,
			String reply) {
		String urlParams = "method=message.send&send_to=" + username
				+ "&subject=" + encode(subject) + "&body=" + encode(body)
				+ "&reply=" + reply;

		int result = -1;

		try {
			String xml = doAuthenticatedPost(ElggBase, urlParams);
			Document doc = loadXMLFromString(xml);

			String rs = doc.getElementsByTagName("status").item(0)
					.getTextContent();

			result = Integer.parseInt(rs);

			Node n = doc.getElementsByTagName("message").item(0);
			if (n != null) {
				lastError = n.getTextContent();
			}
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "sendMessage", result, lastError);

		if (result == RESULT_AUTH_FAILED) {
			notifyLoginObservers();
		}

		return result;
	}

	public List<MessageItem> getMessages() {
		lastError = null;
		List<MessageItem> items = new ArrayList<MessageItem>();

		String urlParams = "method=messages.inbox";

		int result = -1;
		try {
			String xml = doAuthenticatedGet(ElggBase, urlParams);
			Document doc = loadXMLFromString(xml);

			String rs = doc.getElementsByTagName("status").item(0)
					.getTextContent();

			result = Integer.parseInt(rs);

			Node n = doc.getElementsByTagName("message").item(0);
			if (n != null) {
				lastError = n.getTextContent();
			}

			NodeList nl = doc.getElementsByTagName("array_item");

			for (int i = 0; i < nl.getLength(); i++) {
				Element el = (Element) nl.item(i);

				String guid = el.getElementsByTagName("guid").item(0)
						.getTextContent();

				String subject = el.getElementsByTagName("subject").item(0)
						.getTextContent();
				long timestamp = Long.parseLong(el
						.getElementsByTagName("timestamp").item(0)
						.getTextContent());

				String description = el.getElementsByTagName("description")
						.item(0).getTextContent();

				boolean read = el.getElementsByTagName("read").item(0)
						.getTextContent().equals("yes");

				String name = el.getElementsByTagName("name").item(0)
						.getTextContent();
				String username = el.getElementsByTagName("username").item(0)
						.getTextContent();
				String avatar_url = el.getElementsByTagName("avatar_url")
						.item(0).getTextContent();

				UserItem from = new UserItem(name, username, avatar_url);
				MessageItem item = new MessageItem(guid, subject, description,
						timestamp, read, from);

				items.add(item);
			}
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "getMessages", result, lastError);

		if (result == RESULT_AUTH_FAILED) {
			notifyLoginObservers();
		}

		if (noInternet()) {
			callback.onNetworkFailed();
		}

		return items;
	}

	public String getUsername(String guid) {
		String urlParams = "method=user.getusername&guid=" + guid;

		String result = null;

		int r = -1;

		try {
			String xml = doAuthenticatedPost(ElggBase, urlParams);
			Document doc = loadXMLFromString(xml);

			String rs = doc.getElementsByTagName("status").item(0)
					.getTextContent();

			r = Integer.parseInt(rs);

			Node n = doc.getElementsByTagName("message").item(0);
			if (n != null) {
				lastError = n.getTextContent();
			}

			n = doc.getElementsByTagName("result").item(0);

			if (n != null) {
				result = n.getTextContent();
			}
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "getUsername", r, lastError);

		if (r == RESULT_AUTH_FAILED) {
			notifyLoginObservers();
		}

		return result;
	}

	public List<RiverItem> getGroupActivity(String groupGuid) {
		lastError = null;
		List<RiverItem> items = new ArrayList<RiverItem>();

		String urlParams = "method=group.activity&limit=20&offset=0&guid="
				+ groupGuid;

		int result = -1;

		try {
			String json = doAuthenticatedGet(ElggBase.replace("xml", "json"),
					urlParams);

			JSONObject all = new JSONObject(json);

			result = all.getInt("status");

			JSONArray array = all.getJSONArray("result");

			for (int i = 0; i < array.length(); i++) {
				JSONObject item = array.getJSONObject(i);

				UserItem subject = null;
				if (item.has("subject_metadata")) {
					JSONObject subjectMeta = item
							.getJSONObject("subject_metadata");
					subject = new UserItem(subjectMeta.getString("name"),
							subjectMeta.getString("username"),
							subjectMeta.getString("avatar_url"));
				} else {
					subject = getUser(getUsername(""
							+ item.getLong("subject_guid")));
				}

				String predicate = item.getString("string");
				long time = item.getLong("posted") * 1000;

				String objectType = item.getString("type");
				if (objectType.length() == 0 || objectType.equals("object")) {
					objectType = item.getString("subtype");
				}

				String objectGuid = "" + item.getLong("object_guid");

				JSONObject objectMeta = item.getJSONObject("object_metadata");

				String objectName = objectMeta.getString("name");
				String objectDescription = objectMeta.getString("description");

				ObjectItem object = new ObjectItem(objectGuid, objectName,
						objectDescription, objectType);

				items.add(new RiverItem(subject, predicate, object, time));
			}
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "getGroupActivity", result,
				lastError);

		if (result == RESULT_AUTH_FAILED) {
			notifyLoginObservers();
		}

		if (noInternet()) {
			callback.onNetworkFailed();
		}

		return items;
	}

	public List<RiverItem> getRiverItems(String username) {
		lastError = null;
		List<RiverItem> items = new ArrayList<RiverItem>();

		String urlParams = "method=user.activity&limit=20&offset=0&username="
				+ username;

		int result = -1;

		try {
			String json = doAuthenticatedGet(ElggBase.replace("xml", "json"),
					urlParams);

			JSONObject all = new JSONObject(json);

			result = all.getInt("status");

			JSONArray array = all.getJSONArray("result");

			for (int i = 0; i < array.length(); i++) {
				JSONObject item = array.getJSONObject(i);

				UserItem subject = null;
				if (item.has("subject_metadata")) {
					JSONObject subjectMeta = item
							.getJSONObject("subject_metadata");
					subject = new UserItem(subjectMeta.getString("name"),
							subjectMeta.getString("username"),
							subjectMeta.getString("avatar_url"));
				} else {
					subject = getUser(getUsername(""
							+ item.getLong("subject_guid")));
				}

				String predicate = item.getString("string");
				long time = item.getLong("posted") * 1000;

				String objectType = item.getString("type");
				if (objectType.length() == 0 || objectType.equals("object")) {
					objectType = item.getString("subtype");
				}

				String objectGuid = "" + item.getLong("object_guid");

				JSONObject objectMeta = item.getJSONObject("object_metadata");

				String objectName = objectMeta.getString("name");
				String objectDescription = objectMeta.getString("description");

				ObjectItem object = new ObjectItem(objectGuid, objectName,
						objectDescription, objectType);

				items.add(new RiverItem(subject, predicate, object, time));
			}
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "getRiverItems_u", result,
				lastError);

		if (result == RESULT_AUTH_FAILED) {
			notifyLoginObservers();
		}

		if (noInternet()) {
			callback.onNetworkFailed();
		}

		return items;
	}

	public List<RiverItem> getRiverItems(int offset) {
		lastError = null;
		List<RiverItem> items = new ArrayList<RiverItem>();

		String urlParams = "method=site.river_feed&limit=20&offset=" + offset;

		int result = -1;

		try {
			String json = doAuthenticatedGet(ElggBase.replace("xml", "json"),
					urlParams);

			JSONObject all = new JSONObject(json);

			result = all.getInt("status");

			JSONArray array = all.getJSONArray("result");

			for (int i = 0; i < array.length(); i++) {
				JSONObject item = array.getJSONObject(i);

				UserItem subject = null;
				if (item.has("subject_metadata")) {
					JSONObject subjectMeta = item
							.getJSONObject("subject_metadata");
					subject = new UserItem(subjectMeta.getString("name"),
							subjectMeta.getString("username"),
							subjectMeta.getString("avatar_url"));
				} else {
					subject = getUser(getUsername(""
							+ item.getLong("subject_guid")));
				}

				String predicate = item.getString("string");
				long time = item.getLong("posted") * 1000;

				String objectType = item.getString("type");
				if (objectType.length() == 0 || objectType.equals("object")) {
					objectType = item.getString("subtype");
				}

				String objectGuid = "" + item.getLong("object_guid");

				JSONObject objectMeta = item.getJSONObject("object_metadata");

				String objectName = objectMeta.getString("name");
				String objectDescription = objectMeta.getString("description");

				ObjectItem object = new ObjectItem(objectGuid, objectName,
						objectDescription, objectType);

				items.add(new RiverItem(subject, predicate, object, time));
			}
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "getRiverItems", result, lastError);

		if (result == RESULT_AUTH_FAILED) {
			notifyLoginObservers();
		}

		if (noInternet() && callback != null) {
			callback.onNetworkFailed();
		}

		return items;
	}

	public List<WireItem> getWirePosts(int offset) {
		lastError = null;
		List<WireItem> items = new ArrayList<WireItem>();

		String urlParams = "method=wire.get_posts&offset=" + offset
				+ "&limit=15";

		int result = -1;

		try {
			String xml = doAuthenticatedGet(ElggBase, urlParams);
			Document doc = loadXMLFromString(xml);

			String rs = doc.getElementsByTagName("status").item(0)
					.getTextContent();

			result = Integer.parseInt(rs);

			Node n = doc.getElementsByTagName("message").item(0);
			if (n != null) {
				lastError = n.getTextContent();
			}

			NodeList nl = doc.getElementsByTagName("array_item");

			for (int i = 0; i < nl.getLength(); i++) {
				Element el = (Element) nl.item(i);

				String guid = el.getElementsByTagName("guid").item(0)
						.getTextContent();
				long time = Long.parseLong(el
						.getElementsByTagName("time_created").item(0)
						.getTextContent());
				String content = el.getElementsByTagName("description").item(0)
						.getTextContent();

				String avatar_url = el.getElementsByTagName("avatar_url")
						.item(0).getTextContent();
				String name = el.getElementsByTagName("name").item(0)
						.getTextContent();

				String userguid = ((Element) el.getElementsByTagName("owner")
						.item(0)).getElementsByTagName("guid").item(0)
						.getTextContent();

				UserItem author = new UserItem(name, userguid, avatar_url);

				items.add(new WireItem(guid, content, time, author));
			}
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "getWirePosts", result, lastError);

		if (result == RESULT_AUTH_FAILED) {
			notifyLoginObservers();
		}

		if (noInternet()) {
			callback.onNetworkFailed();
		}

		return items;
	}

	public int authenticate(String username, String password) {
		String urlParams = "method=auth.gettoken&username=" + username
				+ "&password=" + password + "";
		int result = -1;

		try {
			String xml = doPost(ElggBase, urlParams);
			Document doc = loadXMLFromString(xml);
			String rs = doc.getElementsByTagName("status").item(0)
					.getTextContent();

			result = Integer.parseInt(rs);

			Node n = doc.getElementsByTagName("message").item(0);
			if (n != null) {
				lastError = n.getTextContent();
			}

			n = doc.getElementsByTagName("result").item(0);
			if (n != null) {
				userToken = n.getTextContent();
				WebClient.this.userName = username;
			}

			WebClient.this.userGuid = getUserGuid(username);
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "authenticate", result, lastError);
		return result;
	}

	private String encode(String text) {
		String encodedText = text;
		try {
			encodedText = URLEncoder.encode(text, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
		}
		return encodedText;
	}

	/**
	 * Loads a document from a String
	 * 
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public Document loadXMLFromString(String xml) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		return builder.parse(is);
	}

	/**
	 * Does a No-Authentication post. User only when logging in.
	 * 
	 * @param url
	 * @param urlParameters
	 * @return
	 */
	public String doPost(String url, String urlParameters) throws Exception {
		HttpURLConnection connection = (HttpURLConnection) getURL(url)
				.openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setInstanceFollowRedirects(false);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		connection.setRequestProperty("charset", "utf-8");
		connection.setRequestProperty("Content-Length",
				"" + Integer.toString(urlParameters.getBytes().length));
		connection.setUseCaches(false);

		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		BufferedReader rd = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));

		String response = "";
		String line = null;
		while ((line = rd.readLine()) != null) {
			response += line;
		}
		rd.close();
		connection.disconnect();
		return response;
	}

	/**
	 * Returns an URL with trailing question mark '?' if needed.
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	private URL getURL(String url) throws Exception {
		URL u = new URL(url);
		if (url.endsWith("?")) {
			Field f = u.getClass().getDeclaredField("file");
			f.setAccessible(true);

			String file = (String) f.get(u);

			if (!file.endsWith("?")) {
				f.set(u, file + "?");
			}
		}
		return u;
	}

	/**
	 * Calculates the HMAC value for this request.
	 * 
	 * @param microtime
	 * @param nonce
	 * @param publicKey
	 * @param privateKey
	 * @param params
	 * @param postHash
	 * @return
	 */
	private String calcHMAC(String microtime, String nonce, String publicKey,
			String privateKey, String params, String postHash) {
		try {
			SecretKeySpec signingKey = new SecretKeySpec(privateKey.getBytes(),
					HMAC_SHA1_ALGORITHM);

			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			mac.init(signingKey);
			mac.update(microtime.getBytes());
			mac.update(nonce.getBytes());
			mac.update(publicKey.getBytes());
			mac.update(params.getBytes());
			if (postHash.length() > 0) {
				mac.update(postHash.getBytes());
			}
			byte[] b = mac.doFinal();
			String result = URLEncoder.encode(Base64.encode(b));
			return result;
		} catch (Exception ex) {
			lastError = ex.toString();
		}
		return null;
	}

	/**
	 * Calculates the MD5
	 * 
	 * @param message
	 * @return
	 */
	private String calcMD5(String message) {
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.reset();
			m.update(message.getBytes());
			byte[] digest = m.digest();
			BigInteger bigInt = new BigInteger(1, digest);
			String hashtext = bigInt.toString(16);
			// Now we need to zero pad it if you actually want the full 32
			// chars.
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
			return hashtext;
		} catch (Exception ex) {
			lastError = ex.toString();
		}
		return null;
	}

	/**
	 * Does a POST request with authentication
	 * 
	 * @param url
	 * @param urlParameters
	 * @return
	 */
	private String doAuthenticatedPost(String url, String urlParameters)
			throws Exception {

		// add user authentication
		urlParameters += "&auth_token=" + userToken;
		HttpURLConnection connection = (HttpURLConnection) getURL(url)
				.openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setInstanceFollowRedirects(false);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		connection.setRequestProperty("Charset", "UTF-8");
		connection.setRequestProperty("Content-Length",
				"" + Integer.toString(urlParameters.getBytes().length));
		connection.setUseCaches(false);

		// API Authentication
		double ftime = System.currentTimeMillis();
		ftime /= 1000.0;
		String microtime = String.format(Locale.getDefault(), "%.4f", ftime);

		String postHash = calcMD5(urlParameters);

		String nonce = "randomstring";

		connection.setRequestProperty("X-Elgg-apikey", PublicKey);
		connection.setRequestProperty("X-Elgg-time", microtime);
		connection.setRequestProperty("X-Elgg-nonce", nonce);
		connection.setRequestProperty("X-Elgg-hmac-algo", "sha1");
		connection
				.setRequestProperty(
						"X-Elgg-hmac",
						calcHMAC(microtime, nonce, PublicKey, PrivateKey, "",
								postHash));

		connection.setRequestProperty("X-Elgg-posthash", postHash);
		connection.setRequestProperty("X-Elgg-posthash-algo", "md5");
		// API Authentication

		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		BufferedReader rd = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));

		String response = "";
		String line = null;
		while ((line = rd.readLine()) != null) {
			response += line;
		}
		rd.close();
		connection.disconnect();
		return response;

	}

	/**
	 * Does an Authenticated GET
	 * 
	 * @param url
	 * @param urlParameters
	 * @return
	 */
	private String doAuthenticatedGet(String url, String urlParameters)
			throws Exception {

		// add user authentication
		urlParameters += "&auth_token=" + userToken;
		HttpURLConnection connection = (HttpURLConnection) getURL(
				url + urlParameters).openConnection();
		connection.setDoOutput(false);
		connection.setDoInput(true);
		connection.setInstanceFollowRedirects(false);
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		connection.setRequestProperty("charset", "utf-8");
		connection.setRequestProperty("Content-Length", "0");
		connection.setUseCaches(false);

		// API Authentication
		double ftime = System.currentTimeMillis();
		ftime /= 1000.0;
		String microtime = String.format(Locale.getDefault(), "%.4f", ftime);

		String nonce = "randomstring";
		String hmac = calcHMAC(microtime, nonce, PublicKey, PrivateKey,
				urlParameters, "");

		connection.setRequestProperty("X-Elgg-apikey", PublicKey);
		connection.setRequestProperty("X-Elgg-time", microtime);
		connection.setRequestProperty("X-Elgg-nonce", nonce);
		connection.setRequestProperty("X-Elgg-hmac-algo", "sha1");
		connection.setRequestProperty("X-Elgg-hmac", hmac);

		// API Authentication

		BufferedReader rd = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));

		String response = "";
		String line = null;
		while ((line = rd.readLine()) != null) {
			response += line;
		}
		rd.close();
		connection.disconnect();
		return response;
	}
}
