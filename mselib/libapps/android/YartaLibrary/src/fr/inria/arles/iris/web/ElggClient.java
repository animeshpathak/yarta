package fr.inria.arles.iris.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import fr.inria.arles.yarta.android.library.util.Base64;
import fr.inria.arles.yarta.logging.YLoggerFactory;

/**
 * Android Web Client for ELGG social networking platform.
 */
public class ElggClient {
	private static final int GET = 0;
	private static final int POST = 1;
	private static final int POST_NOAUTH = 2;

	private static ElggClient instance = new ElggClient();

	private String username;
	private String userGuid;

	private HttpClient client;
	private String lastError;
	private int lastErrorCode;

	private List<WebCallback> callbacks = new ArrayList<WebCallback>();

	private ElggClient() {
		client = new HttpClient();
	}

	private void log(String format, Object... args) {
		YLoggerFactory.getLogger().d("ElggClient", String.format(format, args));
	}

	/**
	 * Checks whether the last error is caused by lack of Internet.
	 * 
	 * @return
	 */
	private boolean noInternet() {
		if (lastError != null) {
			return lastError.contains("UnknownHostException")
					|| lastError.contains("Hostname")
					|| lastError.contains("ConnectException");
		}
		return false;
	}

	/**
	 * HTML received does not contain br's so we will insert them automatically
	 * when punctuation signs are not followed by space.
	 * 
	 * @param text
	 * @return
	 */
	private String correctText(String text) {
		char[] ch = text.toCharArray();
		String result = "";

		for (int i = 0; i < ch.length - 1; i++) {
			result += ch[i];
			boolean punctuation = ch[i] == '.' || ch[i] == '!' || ch[i] == '?'
					|| ch[i] == ':' || ch[i] == ',';
			if (punctuation && Character.isUpperCase(ch[i + 1])) {
				result += "<br/>";
			}
		}

		if (ch.length > 0) {
			result += ch[ch.length - 1];
		}
		result = result.replace("\r\n", "<br />");
		return result;
	}

	/**
	 * Encodes the given text into UTF-8.
	 * 
	 * @param text
	 * @return
	 */
	private String encode(String text) {
		String encodedText = text;
		try {
			encodedText = URLEncoder.encode(text, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
		}
		return encodedText;
	}

	/**
	 * Calls an HTTP method.
	 * 
	 * @param method
	 *            the name of the method
	 * @param callType
	 *            GET/POST
	 * @param args
	 *            param1, value2, param2, value2
	 * @return
	 */
	private JSONObject callMethod(String method, int callType, Object... args) {
		lastError = null;
		JSONObject result = null;
		String urlParameters = "method=" + method;

		for (int i = 0; i < args.length / 2; i++) {
			urlParameters += "&" + args[2 * i] + "=" + args[2 * i + 1];
		}

		try {
			String json = null;

			switch (callType) {
			case GET:
				json = client.doAuthenticatedGet(Strings.BaseService,
						urlParameters);
				break;
			case POST:
				json = client.doAuthenticatedPost(Strings.BaseService,
						urlParameters);
				break;
			case POST_NOAUTH:
				json = client.doPost(Strings.BaseService, urlParameters);
				break;
			}

			result = new JSONObject(json);
		} catch (Exception ex) {
			lastError = ex.toString();
		}
		return result;
	}

	/**
	 * Checks for common errors (no net or auth fail) and notifies through
	 * callbacks;
	 * 
	 * @param json
	 * @return
	 */
	private int checkErrors(JSONObject json) {
		lastErrorCode = -1;
		try {
			if (json != null) {
				if (json.has("status")) {
					lastErrorCode = json.getInt("status");
				}

				if (json.has("message")) {
					lastError = json.getString("message");
				}
			}
		} catch (Exception ex) {
			lastError = ex.toString();
			ex.printStackTrace();
		}
		if (lastErrorCode == RESULT_AUTH_FAILED) {
			notifiyAuthenticatioFailure();
		}

		if (noInternet()) {
			lastErrorCode = RESULT_NO_NET;
			notifyNetworkFailure();
		}
		return lastErrorCode;
	}

	/**
	 * Gets an xpath value out of a json object.
	 * 
	 * @param object
	 * @param path
	 * @return
	 * @throws Exception
	 */
	private String getString(JSONObject object, String path) throws Exception {
		String[] keys = path.split("/");
		String key = keys[0];
		for (int i = 0; i < keys.length - 1; i++) {
			if (object.has(key) && !object.isNull(key)) {
				object = object.getJSONObject(key);
			}
			key = keys[i + 1];
		}
		if (object.has(key)) {
			return object.getString(key);
		}
		return null;
	}

	public static final int ACCESS_PUBLIC = 2;
	public static final int ACCESS_LOGGED_USERS = 1;

	public static final int RESULT_ERROR = -1;
	public static final int RESULT_OK = 0;
	public static final int RESULT_AUTH_FAILED = -20;
	public static final int RESULT_NO_NET = -50;

	/**
	 * Interface to be called when we must login
	 */
	public interface WebCallback {
		public void onAuthenticationFailed();

		public void onAuthentication();

		public void onNetworkFailed();
	}

	public static ElggClient getInstance() {
		return instance;
	}

	public String getUsername() {
		return username;
	}

	public String getUserGuid() {
		return userGuid;
	}

	public String getToken() {
		return client.getToken();
	}

	public String getLastError() {
		return lastError;
	}

	public int getLastErrorCode() {
		return lastErrorCode;
	}

	public void setToken(String token) {
		client.setToken(token);
		notifiyAuthentication();
	}

	public void addCallback(WebCallback callback) {
		synchronized (callbacks) {
			callbacks.add(callback);
		}
	}

	public void removeCallback(WebCallback callback) {
		synchronized (callbacks) {
			callbacks.remove(callback);
		}
	}

	public void notifiyAuthenticatioFailure() {
		synchronized (callbacks) {
			for (WebCallback callback : callbacks) {
				callback.onAuthenticationFailed();
			}
		}
	}

	public void notifiyAuthentication() {
		synchronized (callbacks) {
			for (WebCallback callback : callbacks) {
				callback.onAuthentication();
			}
		}
	}

	public void notifyNetworkFailure() {
		synchronized (callbacks) {
			for (WebCallback callback : callbacks) {
				callback.onNetworkFailed();
			}
		}
	}

	public void setUsername(String username) {
		if (username != null && username.contains("@")) {
			username = username.substring(0, username.indexOf('@'));
		}
		this.username = username;
	}

	public void setUserGuid(String guid) {
		this.userGuid = guid;
	}

	// user functions
	public UserItem getUser(String username) {
		JSONObject json = callMethod("user.get_profile", GET, "username",
				username);
		int result = checkErrors(json);

		UserItem user = null;
		try {
			json = json.getJSONObject("result");

			String name = getString(json, "core/name");
			String avatar_url = getString(json, "avatar_url");
			String website = getString(json, "profile_fields/website/value");

			String location = getString(json,
					"profile_fields/inria_location/value");
			String room = getString(json, "profile_fields/inria_room/value");
			String phone = getString(json, "profile_fields/inria_phone/value");

			user = new UserItem(name, website, avatar_url, location, room,
					phone);
			user.setUsername(username);
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "getUser", result, lastError);
		return user;
	}

	// wire functions
	public int addWire(String content, int access) {
		JSONObject json = callMethod("wire.save_post", POST, "username",
				username, "text", encode(content), "access", access);
		int result = checkErrors(json);

		log("%s: result<%d>, lastError<%s>", "addWire", result, lastError);
		return result;
	}

	public int joinGroup(String guid) {
		JSONObject json = callMethod("group.join", POST, "username", username,
				"groupid", guid);

		int result = checkErrors(json);
		log("%s: result<%d>, lastError<%s>", "joinGroup", result, lastError);
		return result;
	}

	public int leaveGroup(String guid) {
		JSONObject json = callMethod("group.leave", POST, "username", username,
				"groupid", guid);

		int result = checkErrors(json);
		log("%s: result<%d>, lastError<%s>", "leaveGroup", result, lastError);
		return result;
	}

	public int removeWire(String guid) {
		JSONObject json = callMethod("wire.delete_posts", POST, "username",
				username, "wireid", guid);
		int result = checkErrors(json);

		log("%s: result<%d>, lastError<%s>", "removeWire", result, lastError);
		return result;
	}

	public List<WireItem> getWirePosts(int offset) {
		List<WireItem> items = new ArrayList<WireItem>();

		JSONObject json = callMethod("wire.get_posts", GET, "limit", 15,
				"offset", offset);
		int result = checkErrors(json);

		try {
			JSONArray all = json.getJSONArray("result");

			for (int i = 0; i < all.length(); i++) {
				JSONObject object = all.getJSONObject(i);
				String guid = object.getString("guid");
				long time = object.getLong("time_created");
				String content = object.getString("description");

				JSONObject owner = object.getJSONObject("owner");
				String avatar_url = owner.getString("avatar_url");
				String name = owner.getString("name");
				String userguid = owner.getString("guid");

				UserItem author = new UserItem(name, userguid, avatar_url);
				items.add(new WireItem(guid, content, time, author));
			}
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "getWirePosts", result, lastError);
		return items;
	}

	// river functions
	public List<RiverItem> getGroupActivity(String groupGuid) {
		List<RiverItem> items = new ArrayList<RiverItem>();

		JSONObject json = callMethod("group.activity", GET, "limit", 20,
				"offset", 0, "guid", groupGuid);
		int result = checkErrors(json);

		try {
			JSONArray all = json.getJSONArray("result");

			for (int i = 0; i < all.length(); i++) {
				JSONObject item = all.getJSONObject(i);

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
		return items;
	}

	public List<RiverItem> getRiverItems(String username) {
		List<RiverItem> items = new ArrayList<RiverItem>();

		JSONObject json = callMethod("user.activity", GET, "limit", 20,
				"offset", 0, "username", username);
		int result = checkErrors(json);

		try {
			JSONArray all = json.getJSONArray("result");

			for (int i = 0; i < all.length(); i++) {
				JSONObject item = all.getJSONObject(i);

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

				if (objectName.equals(subject.getName())) {
					objectName = "";
				}

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
		return items;
	}

	public List<RiverItem> getRiverItems(int offset) {
		List<RiverItem> items = new ArrayList<RiverItem>();

		JSONObject json = callMethod("site.river_feed", GET, "limit", 20,
				"offset", offset);
		int result = checkErrors(json);
		try {
			JSONArray all = json.getJSONArray("result");

			for (int i = 0; i < all.length(); i++) {
				JSONObject item = all.getJSONObject(i);

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

				if (objectName.equals(subject.getName())) {
					objectName = "";
				}
				String objectDescription = objectMeta.getString("description");

				ObjectItem object = new ObjectItem(objectGuid, objectName,
						objectDescription, objectType);

				items.add(new RiverItem(subject, predicate, object, time));
			}
		} catch (Exception ex) {
			lastError = ex.toString();
		}
		log("%s: result<%d>, lastError<%s>", "getRiverItems", result, lastError);
		return items;
	}

	public String getUsername(String guid) {
		JSONObject json = callMethod("user.getusername", POST, "guid", guid);
		int result = checkErrors(json);

		String username = null;

		try {
			username = getString(json, "result");
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "getUsername", result, lastError);
		return username;
	}

	public String getUserGuid(String username) {
		String guid = null;

		JSONObject json = callMethod("user.getguid", POST, "username", username);
		int result = checkErrors(json);
		try {
			guid = getString(json, "result");
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "getUserGuid", result, lastError);
		return guid;
	}

	// friends functions
	public List<UserItem> getFriends(String username, int offset) {
		List<UserItem> users = new ArrayList<UserItem>();

		JSONObject json = callMethod("user.friend.get_friends", GET,
				"username", username, "offset", offset);
		int result = checkErrors(json);

		try {
			JSONArray all = json.getJSONArray("result");
			for (int i = 0; i < all.length(); i++) {
				JSONObject object = all.getJSONObject(i);

				String uname = getString(object, "username");
				String name = getString(object, "name");
				String avatarURL = getString(object, "avatar_url");

				users.add(new UserItem(name, uname, avatarURL));
			}
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "getFriends", result, lastError);
		return users;
	}

	public int addFriend(String username) {
		JSONObject json = callMethod("user.friend.add", POST, "friend",
				username);
		int result = checkErrors(json);
		log("%s: result<%d>, lastError<%s>", "addFriend", result, lastError);
		return result;
	}

	// requests functions
	public List<RequestItem> getUserRequests() {
		List<RequestItem> requests = new ArrayList<RequestItem>();

		JSONObject json = callMethod("user.get_user_requests", GET, "username",
				username);
		int result = checkErrors(json);

		try {
			JSONObject res = json.getJSONObject("result");

			if (res.has("friend")) {
				JSONArray all = res.getJSONArray("friend");

				for (int i = 0; i < all.length(); i++) {
					JSONObject item = all.getJSONObject(i);

					String name = item.getString("name");
					String username = item.getString("username");
					String avatarURL = item.getString("avatar_url");

					UserItem user = new UserItem(name, username, avatarURL);
					requests.add(new RequestItem(user));
				}
			}

			if (res.has("join")) {
				JSONArray all = res.getJSONArray("join");
				for (int i = 0; i < all.length(); i++) {
					JSONObject item = all.getJSONObject(i);

					String name = item.getString("name");
					String username = item.getString("username");
					String avatarURL = item.getString("avatar_url");

					String groupId = item.getString("group_guid");
					String groupName = item.getString("group_name");

					UserItem user = new UserItem(name, username, avatarURL);
					GroupItem group = new GroupItem(groupId, groupName, null, 0);
					requests.add(new RequestItem(user, group));
				}
			}
		} catch (Exception ex) {
			lastError = ex.toString();
		}
		log("%s: result<%d>, lastError<%s>", "getUserRequests", result,
				lastError);
		return requests;
	}

	public int acceptRequest(RequestItem request) {
		JSONObject json = request.getGroup() != null ? callMethod(
				"user.accept_join_request", GET, "username", request.getUser()
						.getUsername(), "group_guid", request.getGroup()
						.getGuid()) : callMethod("user.accept_friend_request",
				GET, "username", request.getUser().getUsername());
		int result = checkErrors(json);
		log("%s: result<%d>, lastError<%s>", "acceptRequest", result, lastError);
		return result;
	}

	public int ignoreRequest(RequestItem request) {
		JSONObject json = request.getGroup() != null ? callMethod(
				"user.remove_join_request", GET, "username", request.getUser()
						.getUsername(), "group_guid", request.getGroup()
						.getGuid()) : callMethod("user.remove_friend_request",
				GET, "username", request.getUser().getUsername());
		int result = checkErrors(json);
		log("%s: result<%d>, lastError<%s>", "ignoreRequest", result, lastError);
		return result;
	}

	// group functions
	public GroupItem getGroup(String guid) {
		JSONObject json = callMethod("group.get", GET, "guid", guid);
		int result = checkErrors(json);

		GroupItem group = null;
		try {
			JSONObject res = json.getJSONObject("result");

			String name = getString(res, "name");
			String avatarURL = getString(res, "avatar_url");
			String description = correctText(getString(res,
					"fields/description/value"));

			int members = res.getInt("members_count");

			String ownerName = getString(res, "owner_name");

			group = new GroupItem(name, avatarURL, members, ownerName);
			group.setGuid(guid);
			group.setDescription(description);
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "getGroup", result, lastError);
		return group;
	}

	public Bitmap getGroupIcon(String guid, String size) {
		JSONObject json = callMethod("group.get_icon", POST, "guid", guid,
				"size", size);
		int result = checkErrors(json);

		Bitmap bitmap = null;
		try {
			String content64 = json.getJSONObject("result")
					.getString("content");
			byte[] data = Base64.decode(content64);
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "getGroupIcon", result, lastError);
		return bitmap;
	}

	public List<GroupItem> getGroups(String username) {
		List<GroupItem> groups = new ArrayList<GroupItem>();

		JSONObject json = callMethod("group.get_groups", GET, "context",
				"user", "username", username, "offset", 0);
		int result = checkErrors(json);

		try {
			JSONArray all = json.getJSONArray("result");

			for (int i = 0; i < all.length(); i++) {
				JSONObject item = all.getJSONObject(i);

				String name = getString(item, "name");
				String avatarURL = getString(item, "avatar_url");
				String guid = getString(item, "guid");

				int members = item.getInt("members");

				groups.add(new GroupItem(guid, name, avatarURL, members));
			}
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "getGroups", result, lastError);
		return groups;
	}

	public List<PostItem> getGroupPosts(String groupId) {
		List<PostItem> posts = new ArrayList<PostItem>();

		JSONObject json = callMethod("group.forum.get_posts", GET, "offset", 0,
				"guid", groupId);
		int result = checkErrors(json);

		try {
			JSONArray all = json.getJSONArray("result");

			for (int i = 0; i < all.length(); i++) {
				JSONObject item = all.getJSONObject(i);

				String guid = getString(item, "guid");
				String title = getString(item, "title");
				long time = item.getLong("time_created") * 1000;

				String name = getString(item, "owner/name");
				String username = getString(item, "owner/username");
				String avatarURL = getString(item, "owner/avatar_url");

				UserItem owner = new UserItem(name, username, avatarURL);
				PostItem post = new PostItem(guid, title, time, owner);
				posts.add(post);
			}
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "getGroupPosts", result, lastError);
		return posts;
	}

	public int deleteGroupPost(String postId) {
		JSONObject json = callMethod("group.forum.delete_post", POST,
				"topicid", "postId", "username", username);
		int result = checkErrors(json);
		log("%s: result<%d>, lastError<%s>", "addGroupPost", result, lastError);
		return result;
	}

	public int addGroupPost(String groupId, String title, String text) {
		JSONObject json = callMethod("group.forum.save_post", POST, "title",
				encode(title), "desc", encode(text), "groupid", groupId,
				"access_id", ACCESS_LOGGED_USERS);
		int result = checkErrors(json);
		log("%s: result<%d>, lastError<%s>", "addGroupPost", result, lastError);
		return result;
	}

	// post functions
	public int addGroupPostComment(String postId, String content) {
		JSONObject json = callMethod("group.forum.save_reply", POST, "postid",
				postId, "text", encode(content));
		int result = checkErrors(json);
		log("addComment(%s): result<%d>, lastError<%s>", postId, result,
				lastError);
		return result;
	}

	public List<PostItem> getGroupPostComments(String postGuid) {
		List<PostItem> comments = new ArrayList<PostItem>();

		JSONObject json = callMethod("group.forum.get_replies", GET, "guid",
				postGuid);
		int result = checkErrors(json);

		try {
			JSONArray all = json.getJSONArray("result");

			for (int i = 0; i < all.length(); i++) {
				JSONObject item = all.getJSONObject(i);

				String guid = getString(item, "id");
				String title = getString(item, "value");
				long time = item.getLong("time_created") * 1000;

				String name = getString(item, "owner/name");
				String username = getString(item, "owner/username");
				String avatarURL = getString(item, "owner/avatar_url");

				UserItem owner = new UserItem(name, username, avatarURL);
				PostItem comment = new PostItem(guid, title, time, owner);
				comments.add(comment);
			}
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "getPostComments", result,
				lastError);

		return comments;
	}

	public PostItem getGroupPost(String guid) {
		JSONObject json = callMethod("group.forum.get_post", GET, "guid", guid,
				"username", username);
		int result = checkErrors(json);

		PostItem post = null;
		try {
			JSONObject res = json.getJSONObject("result");

			String title = getString(res, "title");
			String description = getString(res, "description");
			long time = res.getLong("time_created") * 1000;

			String name = getString(res, "owner/name");
			String username = getString(res, "owner/username");
			String avatarURL = getString(res, "owner/avatar_url");

			UserItem owner = new UserItem(name, username, avatarURL);
			post = new PostItem(guid, title, description, time, owner);
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "getGroupPost", result, lastError);
		return post;
	}

	// micro-blogging
	public int addBlogComment(String blogGuid, String content) {
		JSONObject json = callMethod("blog.post_comment", POST, "guid",
				blogGuid, "text", encode(content));
		int result = checkErrors(json);
		log("%s: result<%d>, lastError<%s>", "addBlogComment", result,
				lastError);
		return result;
	}

	public List<PostItem> getBlogComments(String blogGuid) {
		List<PostItem> replies = new ArrayList<PostItem>();

		JSONObject json = callMethod("blog.get_comments", GET, "guid",
				blogGuid, "offset", 0);
		int result = checkErrors(json);

		try {
			JSONArray all = json.getJSONArray("result");

			for (int i = 0; i < all.length(); i++) {
				JSONObject item = all.getJSONObject(i);

				String guid = getString(item, "guid");
				String title = getString(item, "description");
				long time = item.getLong("time_created") * 1000;

				String name = getString(item, "owner/name");
				String username = getString(item, "owner/username");
				String avatarURL = getString(item, "owner/avatar_url");

				UserItem owner = new UserItem(name, username, avatarURL);
				PostItem reply = new PostItem(guid, title, time, owner);
				replies.add(reply);
			}
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "getBlogComments", result,
				lastError);
		return replies;
	}

	// file functions
	public FileItem getFileInfo(String guid) {
		JSONObject json = callMethod("file.get_info", POST, "guid", guid,
				"username", username);
		int result = checkErrors(json);

		FileItem item = null;
		try {
			json = json.getJSONObject("result");
			String fileName = getString(json, "name");
			fileName = fileName.substring(fileName.indexOf('/') + 1);

			long size = json.getLong("size");
			long time = json.getLong("time_created") * 1000;
			String title = getString(json, "title");
			String description = getString(json, "description");

			String name = getString(json, "owner/name");
			String username = getString(json, "owner/username");
			String avatarURL = getString(json, "owner/avatar_url");

			UserItem owner = new UserItem(name, username, avatarURL);
			item = new FileItem(guid, title, description, fileName, size, time,
					owner);
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "getFileInfo", result, lastError);
		return item;
	}

	public String getFileContent(String guid) {
		JSONObject json = callMethod("file.get_file", POST, "guid", guid,
				"username", username);
		int result = checkErrors(json);

		String content = null;

		try {
			content = getString(json, "result/content");
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "getFileInfo", result, lastError);
		return content;
	}

	// search api
	public List<ObjectItem> search(String query) {
		List<ObjectItem> items = new ArrayList<ObjectItem>();

		Map<String, List<ObjectItem>> groupedItems = new HashMap<String, List<ObjectItem>>();

		JSONObject json = callMethod("site.search", GET, "query", encode(query));
		int result = checkErrors(json);

		try {
			json = json.getJSONObject("result");

			String[] types = new String[] { ObjectItem.Group, ObjectItem.User,
					ObjectItem.Object };

			for (String classType : types) {
				if (!json.has(classType))
					continue;
				JSONObject objects = json.getJSONObject(classType);

				Iterator<?> it = objects.keys();
				while (it.hasNext()) {
					JSONObject item = objects.getJSONObject((String) it.next());

					String guid = getString(item, "guid");
					String title = getString(item, "title");
					String type = getString(item, "type");
					if (type.equals("object")) {
						type = getString(item, "subtype");
					}

					if (type.equals(ObjectItem.Blog)
							|| type.equals(ObjectItem.Page)
							|| type.equals(ObjectItem.PageTop)) {
						type = ObjectItem.Blog;
					}
					String avatarURL = getString(item, "avatar_url");

					if (type.equals(ObjectItem.User)
							|| type.equals(ObjectItem.Group)
							|| type.equals(ObjectItem.Topic)
							|| type.equals(ObjectItem.Blog)
							|| type.equals(ObjectItem.File)) {
						ObjectItem object = new ObjectItem(guid, title,
								avatarURL, type);

						if (!groupedItems.containsKey(type)) {
							groupedItems.put(type, new ArrayList<ObjectItem>());
						}
						groupedItems.get(type).add(object);
					}
				}
			}

			String[] order = new String[] { ObjectItem.User, ObjectItem.Group,
					ObjectItem.Topic, ObjectItem.Blog, ObjectItem.File };
			for (String type : order) {
				if (groupedItems.containsKey(type)) {
					items.addAll(groupedItems.get(type));
				}
			}
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "search", result, lastError);
		return items;
	}

	// messages api
	public List<List<MessageItem>> getMessageThreads() {
		List<List<MessageItem>> threads = new ArrayList<List<MessageItem>>();

		Map<UserItem, List<MessageItem>> threadMap = new HashMap<UserItem, List<MessageItem>>();

		List<MessageItem> all = getMessagesInbox();

		if (lastError != null)
			return threads;

		all.addAll(getMessagesOutbox());

		for (MessageItem message : all) {
			UserItem user = message.getFrom();

			List<MessageItem> items = threadMap.get(user);
			if (items == null) {
				items = new ArrayList<MessageItem>();
				threadMap.put(user, items);
			}

			items.add(message);
		}

		for (UserItem user : threadMap.keySet()) {
			threads.add(threadMap.get(user));
		}

		// sort each thread
		for (List<MessageItem> thread : threads) {
			Collections.sort(thread, new Comparator<MessageItem>() {
				@Override
				public int compare(MessageItem lhs, MessageItem rhs) {
					Long lt = lhs.getTimestamp();
					Long rt = rhs.getTimestamp();
					return lt.compareTo(rt);
				}
			});
		}

		// sort all threads
		Collections.sort(threads, new Comparator<List<MessageItem>>() {
			@Override
			public int compare(List<MessageItem> lhs, List<MessageItem> rhs) {
				Long lt = lhs.get(lhs.size() - 1).getTimestamp();
				Long rt = rhs.get(rhs.size() - 1).getTimestamp();
				return rt.compareTo(lt);
			}
		});

		return threads;
	}

	public List<MessageItem> getMessagesInbox() {
		List<MessageItem> messages = new ArrayList<MessageItem>();

		JSONObject json = callMethod("messages.inbox", GET, "offset", 0);
		int result = checkErrors(json);

		try {
			JSONArray all = json.getJSONArray("result");

			for (int i = 0; i < all.length(); i++) {
				JSONObject item = all.getJSONObject(i);

				String guid = getString(item, "guid");
				String subject = getString(item, "subject");
				String description = correctText(getString(item, "description"));
				long timestamp = item.getLong("timestamp") * 1000;
				boolean read = getString(item, "read").equals("yes");

				String name = getString(item, "user/name");
				String username = getString(item, "user/username");
				String avatarURL = getString(item, "user/avatar_url");

				UserItem user = new UserItem(name, username, avatarURL);
				MessageItem message = new MessageItem(guid, subject,
						description, timestamp, read, user);
				messages.add(message);
			}
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "getMessagesInbox", result,
				lastError);
		return messages;
	}

	// TODO: merge with inbox since common code;
	public List<MessageItem> getMessagesOutbox() {
		List<MessageItem> messages = new ArrayList<MessageItem>();

		JSONObject json = callMethod("messages.sent", GET, "offset", 0);
		int result = checkErrors(json);

		try {
			JSONArray all = json.getJSONArray("result");

			for (int i = 0; i < all.length(); i++) {
				JSONObject item = all.getJSONObject(i);

				String guid = getString(item, "guid");
				String subject = getString(item, "subject");
				String description = correctText(getString(item, "description"));
				long timestamp = item.getLong("timestamp") * 1000;
				boolean read = true;

				String name = getString(item, "user/name");
				String username = getString(item, "user/username");
				String avatarURL = getString(item, "user/avatar_url");

				UserItem user = new UserItem(name, username, avatarURL);
				MessageItem message = new MessageItem(guid, subject,
						description, timestamp, read, user);
				message.setSent(true);
				messages.add(message);
			}
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "getMessagesOutbox", result,
				lastError);
		return messages;
	}

	public int sendMessage(String username, String subject, String body,
			String reply) {
		JSONObject json = callMethod("message.send", POST, "send_to", username,
				"subject", encode(subject), "body", encode(body), "reply",
				reply);
		int result = checkErrors(json);
		log("%s: result<%d>, lastError<%s>", "sendMessage", result, lastError);
		return result;
	}

	public int readMessage(MessageItem item) {
		JSONObject json = callMethod("messages.read", GET, "guid",
				item.getGuid());
		int result = checkErrors(json);

		log("%s: result<%d>, lastError<%s>", "readMessage", result, lastError);
		return result;
	}

	// authentication API
	public int authenticate(String username, String password) {
		JSONObject json = callMethod("auth.gettoken", POST_NOAUTH, "username",
				username, "password", password);
		int result = checkErrors(json);

		try {
			String token = getString(json, "result");
			if (token != null) {
				setToken(token);
				this.username = username;
				this.userGuid = getUserGuid(username);
			}
		} catch (Exception ex) {
			lastError = ex.toString();
		}

		log("%s: result<%d>, lastError<%s>", "authenticate", result, lastError);
		return result;
	}
}
