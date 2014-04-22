package fr.inria.arles.yarta.android.library;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import fr.inria.arles.yarta.android.library.util.Settings;
import fr.inria.arles.yarta.knowledgebase.UpdateHelper;
import fr.inria.arles.yarta.knowledgebase.interfaces.Node;
import fr.inria.arles.yarta.knowledgebase.interfaces.Triple;

public class AndroidUpdateHelper implements UpdateHelper {
	private String DATABASE_NAME = "triples";
	private static final String TABLE_NAME = "triples";
	private static final int DATABASE_VERSION = 1;
	private DBHelper helper;
	private SQLiteDatabase m_db;
	private Settings settings;
	private Context context;

	public AndroidUpdateHelper(Context context) {
		this.context = context;
	}

	@Override
	public void init() {
		settings = new Settings(context);
		try {
			helper = new DBHelper(context);
			m_db = helper.getWritableDatabase();
		} catch (Exception ex) {
			if (m_db != null) {
				m_db.close();
			}
			helper.close();
		}
	}

	@Override
	public void uninit() {
		if (m_db != null) {
			try {
				m_db.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			m_db = null;
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
	public boolean isDirty(Node node, long time) {
		long s = hash(node.getName());

		Cursor cursor = m_db.query(TABLE_NAME, new String[] { "time" }, "s="
				+ s + " AND (time > " + time + " OR time = 0)", null, null,
				null, null);

		boolean result = false;

		if (cursor.moveToNext()) {
			result = true;
		}

		cursor.close();
		return result;
	}

	@Override
	public long getTime(Triple triple) {
		long s = hash(triple.getSubject().getName());
		long p = hash(triple.getProperty().getName());
		long o = hash(triple.getObject().getName());

		Cursor cursor = m_db.query(TABLE_NAME, new String[] { "time" }, "s="
				+ s + " AND p=" + p + " AND o=" + o, null, null, null, null);

		while (cursor.moveToNext()) {
			long result = cursor.getLong(0);
			cursor.close();
			return result;
		}
		return 0;
	}

	@Override
	public void setTime(Triple triple, long time) {
		long s = hash(triple.getSubject().getName());
		long p = hash(triple.getProperty().getName());
		long o = hash(triple.getObject().getName());

		String sql = String.format(
				"delete from %s where s=%d AND p=%d AND o=%d", TABLE_NAME, s,
				p, o);
		m_db.execSQL(sql);

		sql = String.format(
				"insert into %s (s, p, o, time) VALUES(%d, %d, %d, %d)",
				TABLE_NAME, s, p, o, time);
		m_db.execSQL(sql);
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

	private class DBHelper extends SQLiteOpenHelper {
		DBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + TABLE_NAME
					+ "(s LONG, p LONG, o LONG, time LONG)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);
		}
	}
}
