package fr.inria.arles.yarta.desktop.library;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import fr.inria.arles.yarta.desktop.library.util.Installer;
import fr.inria.arles.yarta.desktop.library.util.Settings;
import fr.inria.arles.yarta.knowledgebase.UpdateHelper;
import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.knowledgebase.interfaces.Triple;

public class DesktopUpdateHelper implements UpdateHelper {

	private Settings settings = new Settings();
	private Connection connection = null;
	private Statement statement = null;

	public DesktopUpdateHelper() {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void init() {
		if (connection == null) {
			try {
				connection = DriverManager.getConnection("jdbc:sqlite:"
						+ Installer.FilesPath + "triples.db");
				statement = connection.createStatement();
				statement.setQueryTimeout(30); // set timeout to 30 sec.
				statement
						.executeUpdate("create table if not exists triples (s long, p long, o long, time long)");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void uninit() {
		if (connection != null) {
			try {
				connection.close();
				connection = null;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public long getTime(String peerId) {
		return settings.getLong(peerId + ".lastUpdate");
	}

	@Override
	public void setTime(String peerId, long time) {
		settings.setLong(peerId + ".lastUpdate", time);
	}

	@Override
	public long getTime(Triple triple) {
		long s = hash(triple.getSubject().getName());
		long p = hash(triple.getProperty().getName());
		long o = hash(triple.getObject().getName());

		long result = 0;

		String sql = String.format(
				"select time from triples where s=%d AND p=%d AND o=%d", s, p,
				o);

		synchronized (statement) {
			try {
				ResultSet rs = statement.executeQuery(sql);
				if (rs.next()) {
					result = rs.getLong(1);
				}
				rs.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public boolean isDirty(Node node, long time) {
		long s = hash(node.getName());

		String sql = String
				.format("select time from triples where s=%d AND (time = 0 OR time > %d)",
						s, time);

		boolean result = false;

		synchronized (statement) {
			try {
				ResultSet rs = statement.executeQuery(sql);

				if (rs.next()) {
					result = true;
				}
				rs.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public void setTime(Triple triple, long time) {
		long s = hash(triple.getSubject().getName());
		long p = hash(triple.getProperty().getName());
		long o = hash(triple.getObject().getName());

		try {
			synchronized (statement) {
				String sql = String.format(
						"delete from triples where s=%d AND p=%d AND o=%d", s,
						p, o);

				statement.executeUpdate(sql);

				sql = String
						.format("insert into triples (s, p, o, time) values(%d, %d, %d, %d)",
								s, p, o, time);

				statement.executeUpdate(sql);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Fast hash function.
	 * 
	 * @param string
	 * @return
	 */
	private long hash(String string) {
		long hash = 5381;

		for (int i = 0; i < string.length(); i++) {
			hash = ((hash << 5) + hash) + string.charAt(i); /* hash * 33 + c */
		}

		return hash;
	}
}
